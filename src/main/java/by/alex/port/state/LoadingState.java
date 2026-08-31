package by.alex.port.state;

import by.alex.port.config.PortConfiguration;
import by.alex.port.entity.Ship;
import by.alex.port.exception.PortException;
import by.alex.port.resource.Berth;
import by.alex.port.resource.SeaPort;
import by.alex.port.resource.Warehouse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoadingState extends AbstractShipState {

  private static final Logger logger = LogManager.getLogger();

  private static final int SINGLE_CONTAINER = 1;

  @Override
  public void proceed(Ship ship) throws PortException {
    SeaPort port = ship.getPort();
    Warehouse warehouse = port.getWarehouse();
    PortConfiguration configuration = port.getConfiguration();
    long handlingTimeMillis = configuration.containerHandlingTimeMillis();
    int plannedAmount = Math.min(ship.getPlannedLoad(), ship.getFreeSpace());
    int movedAmount = 0;
    while (movedAmount < plannedAmount) {
      if (warehouse.retrieve(SINGLE_CONTAINER) == 0) {
        logger.warn("Ship {}: warehouse is empty, loading is stopped after {} of {} containers",
            ship.getShipId(), movedAmount, plannedAmount);
        break;
      }
      ship.loadContainers(SINGLE_CONTAINER);
      movedAmount++;
      pause(handlingTimeMillis);
    }
    Berth berth = ship.getBerth();
    logger.info("Ship {} loaded {} of {} containers at berth {}, warehouse now {}/{}",
        ship.getShipId(), movedAmount, ship.getPlannedLoad(), berth.berthId(),
        warehouse.getContainerCount(), warehouse.getCapacity());
    ship.setState(new DepartedState());
  }
}
