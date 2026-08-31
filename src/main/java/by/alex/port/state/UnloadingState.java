package by.alex.port.state;

import by.alex.port.config.PortConfiguration;
import by.alex.port.entity.Ship;
import by.alex.port.entity.ShipOperation;
import by.alex.port.exception.PortException;
import by.alex.port.resource.Berth;
import by.alex.port.resource.SeaPort;
import by.alex.port.resource.Warehouse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UnloadingState extends AbstractShipState {

  private static final Logger logger = LogManager.getLogger();

  private static final int SINGLE_CONTAINER = 1;

  @Override
  public void proceed(Ship ship) throws PortException {
    SeaPort port = ship.getPort();
    Warehouse warehouse = port.getWarehouse();
    PortConfiguration configuration = port.getConfiguration();
    long handlingTimeMillis = configuration.containerHandlingTimeMillis();
    int plannedAmount = Math.min(ship.getPlannedUnload(), ship.getContainersOnBoard());
    int movedAmount = 0;
    while (movedAmount < plannedAmount) {
      if (warehouse.store(SINGLE_CONTAINER) == 0) {
        logger.warn(
            "Ship {}: warehouse is full ({}/{}), unloading is stopped after {} of {} containers",
            ship.getShipId(), warehouse.getContainerCount(), warehouse.getCapacity(),
            movedAmount, plannedAmount);
        break;
      }
      ship.unloadContainers(SINGLE_CONTAINER);
      movedAmount++;
      pause(handlingTimeMillis);
    }
    Berth berth = ship.getBerth();
    logger.info("Ship {} unloaded {} of {} containers at berth {}, warehouse now {}/{}",
        ship.getShipId(), movedAmount, ship.getPlannedUnload(), berth.berthId(),
        warehouse.getContainerCount(), warehouse.getCapacity());
    ShipOperation operation = ship.getOperation();
    if (operation.requiresLoading()) {
      ship.setState(new LoadingState());
    } else {
      ship.setState(new DepartedState());
    }
  }
}
