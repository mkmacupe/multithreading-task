package by.alex.port.state;

import by.alex.port.entity.Ship;
import by.alex.port.exception.PortException;
import by.alex.port.resource.Berth;
import by.alex.port.resource.SeaPort;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WaitingForBerthState extends AbstractShipState {

  private static final Logger logger = LogManager.getLogger();

  @Override
  public void proceed(Ship ship) throws PortException {
    SeaPort port = ship.getPort();
    logger.debug("Ship {} is waiting for a berth, free berths at the moment: {}",
        ship.getShipId(), port.getFreeBerthCount());
    long startNanos = System.nanoTime();
    Berth berth = port.occupyBerth();
    long waitedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
    ship.setBerth(berth);
    ship.registerBerthWaiting(waitedMillis);
    logger.info("Ship {} took berth {} after waiting {} ms", ship.getShipId(), berth.berthId(),
        waitedMillis);
    ship.setState(new MooredState());
  }
}
