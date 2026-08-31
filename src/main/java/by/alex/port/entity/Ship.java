package by.alex.port.entity;

import by.alex.port.config.ShipConfiguration;
import by.alex.port.exception.PortException;
import by.alex.port.resource.Berth;
import by.alex.port.resource.SeaPort;
import by.alex.port.result.TransferResult;
import by.alex.port.state.ShipState;
import by.alex.port.state.WaitingForBerthState;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Ship implements Callable<TransferResult> {

  private static final Logger logger = LogManager.getLogger();

  private final long shipId;
  private final int capacity;
  private final ShipOperation operation;
  private final int plannedUnload;
  private final int plannedLoad;

  private SeaPort port;
  private ShipState state;
  private Berth berth;
  private int containersOnBoard;
  private int unloadedContainers;
  private int loadedContainers;
  private long waitingForBerthMillis;

  public Ship(ShipConfiguration configuration) {
    shipId = configuration.shipId();
    capacity = configuration.capacity();
    containersOnBoard = configuration.containersOnBoard();
    operation = configuration.operation();
    plannedUnload = configuration.containersToUnload();
    plannedLoad = configuration.containersToLoad();
  }

  @Override
  public TransferResult call() throws PortException {
    port = SeaPort.getInstance();
    state = new WaitingForBerthState();
    logger.info(
        "Ship {} arrived at the port: operation {}, {}/{} containers on board, plan -{} +{}",
        shipId, operation, containersOnBoard, capacity, plannedUnload, plannedLoad);
    try {
      boolean serviceCompleted = false;
      while (!serviceCompleted) {
        ShipState currentState = state;
        logger.debug("Ship {} enters state {}", shipId, currentState.stateName());
        currentState.proceed(this);
        serviceCompleted = currentState.isTerminal();
      }
    } finally {
      releaseBerthOnFailure();
    }
    return new TransferResult(shipId, operation, plannedUnload, unloadedContainers,
        plannedLoad, loadedContainers, waitingForBerthMillis);
  }

  private void releaseBerthOnFailure() {
    if (berth != null) {
      logger.warn("Ship {} did not finish normally, berth {} is released by the safety net",
          shipId, berth.berthId());
      port.releaseBerth(berth);
      berth = null;
    }
  }

  public void unloadContainers(int count) {
    containersOnBoard -= count;
    unloadedContainers += count;
  }

  public void loadContainers(int count) {
    containersOnBoard += count;
    loadedContainers += count;
  }

  public int getFreeSpace() {
    return capacity - containersOnBoard;
  }

  public void registerBerthWaiting(long millis) {
    waitingForBerthMillis = millis;
  }

  public long getShipId() {
    return shipId;
  }

  public int getCapacity() {
    return capacity;
  }

  public ShipOperation getOperation() {
    return operation;
  }

  public int getPlannedUnload() {
    return plannedUnload;
  }

  public int getPlannedLoad() {
    return plannedLoad;
  }

  public int getContainersOnBoard() {
    return containersOnBoard;
  }

  public int getUnloadedContainers() {
    return unloadedContainers;
  }

  public int getLoadedContainers() {
    return loadedContainers;
  }

  public SeaPort getPort() {
    return port;
  }

  public ShipState getState() {
    return state;
  }

  public void setState(ShipState state) {
    this.state = state;
  }

  public Berth getBerth() {
    return berth;
  }

  public void setBerth(Berth berth) {
    this.berth = berth;
  }

  @Override
  public String toString() {
    return "Ship{id=" + shipId + ", capacity=" + capacity + ", onBoard=" + containersOnBoard + '}';
  }
}
