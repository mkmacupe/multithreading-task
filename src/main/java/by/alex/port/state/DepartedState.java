package by.alex.port.state;

import by.alex.port.entity.Ship;
import by.alex.port.resource.Berth;
import by.alex.port.resource.SeaPort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DepartedState extends AbstractShipState {

  private static final Logger logger = LogManager.getLogger();

  @Override
  public boolean isTerminal() {
    return true;
  }

  @Override
  public void proceed(Ship ship) {
    Berth berth = ship.getBerth();
    SeaPort port = ship.getPort();
    port.releaseBerth(berth);
    ship.setBerth(null);
    logger.info(
        "Ship {} released berth {} and departed with {} containers on board (unloaded {}, loaded {})",
        ship.getShipId(), berth.berthId(), ship.getContainersOnBoard(),
        ship.getUnloadedContainers(), ship.getLoadedContainers());
  }
}
