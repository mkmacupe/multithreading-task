package by.alex.port.resource;

import by.alex.port.config.PortConfiguration;
import by.alex.port.exception.PortException;
import by.alex.port.reader.PortConfigurationReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class SeaPort {

  private static final Logger logger = LogManager.getLogger();

  private static SeaPort instance;
  private static final Lock instanceLock = new ReentrantLock();
  private static final AtomicBoolean isCreated = new AtomicBoolean(false);

  private final PortConfiguration configuration;
  private final Warehouse warehouse;
  private final Deque<Berth> freeBerths = new ArrayDeque<>();
  private final List<Berth> occupiedBerths = new ArrayList<>();
  private final Lock berthLock = new ReentrantLock(true);
  private final Condition berthReleased = berthLock.newCondition();

  private SeaPort() throws PortException {
    PortConfigurationReader portConfigurationReader = new PortConfigurationReader();
    configuration = portConfigurationReader.read();
    warehouse = new Warehouse(configuration.warehouseCapacity(), configuration.initialContainers());
    for (int berthId = 1; berthId <= configuration.berthCount(); berthId++) {
      freeBerths.add(new Berth(berthId));
    }
    logger.info("Sea port is initialized: {} berths, warehouse {}/{} containers",
        configuration.berthCount(), warehouse.getContainerCount(), warehouse.getCapacity());
  }

  public static SeaPort getInstance() throws PortException {
    if (!isCreated.get()) {
      instanceLock.lock();
      try {
        if (!isCreated.get()) {
          instance = new SeaPort();
          isCreated.set(true);
        }
      } finally {
        instanceLock.unlock();
      }
    }
    return instance;
  }

  public Berth occupyBerth() throws PortException {
    berthLock.lock();
    try {
      while (freeBerths.isEmpty()) {
        berthReleased.await();
      }
      Berth berth = freeBerths.poll();
      occupiedBerths.add(berth);
      return berth;
    } catch (InterruptedException e) {
      Thread.currentThread()
            .interrupt();
      throw new PortException("Waiting for a free berth was interrupted", e);
    } finally {
      berthLock.unlock();
    }
  }

  public void releaseBerth(Berth berth) {
    berthLock.lock();
    try {
      occupiedBerths.remove(berth);
      freeBerths.offer(berth);
      berthReleased.signalAll();
    } finally {
      berthLock.unlock();
    }
  }

  public int getFreeBerthCount() {
    berthLock.lock();
    try {
      return freeBerths.size();
    } finally {
      berthLock.unlock();
    }
  }

  public int getOccupiedBerthCount() {
    berthLock.lock();
    try {
      return occupiedBerths.size();
    } finally {
      berthLock.unlock();
    }
  }

  public Warehouse getWarehouse() {
    return warehouse;
  }

  public PortConfiguration getConfiguration() {
    return configuration;
  }
}
