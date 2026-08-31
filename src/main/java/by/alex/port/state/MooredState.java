package by.alex.port.state;

import by.alex.port.config.PortConfiguration;
import by.alex.port.entity.Ship;
import by.alex.port.entity.ShipOperation;
import by.alex.port.exception.PortException;
import by.alex.port.resource.Berth;
import by.alex.port.resource.SeaPort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MooredState extends AbstractShipState {

  private static final Logger logger = LogManager.getLogger();

  @Override
  public void proceed(Ship ship) throws PortException {
    SeaPort port = ship.getPort();
    PortConfiguration configuration = port.getConfiguration();
    long mooringTimeMillis = configuration.mooringTimeMillis();
    pause(mooringTimeMillis);
    Berth berth = ship.getBerth();
    logger.info("Ship {} is moored at berth {} in {} ms",
        ship.getShipId(), berth.berthId(), mooringTimeMillis);
    ShipOperation operation = ship.getOperation();
    if (operation.requiresUnloading()) {
      ship.setState(new UnloadingState());
    } else {
      ship.setState(new LoadingState());
    }
  }
}
