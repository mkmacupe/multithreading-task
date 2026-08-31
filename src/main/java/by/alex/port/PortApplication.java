package by.alex.port;

import by.alex.port.config.ShipConfiguration;
import by.alex.port.entity.Ship;
import by.alex.port.exception.PortException;
import by.alex.port.reader.ShipConfigurationReader;
import by.alex.port.resource.SeaPort;
import by.alex.port.resource.Warehouse;
import by.alex.port.result.PortReport;
import by.alex.port.result.TransferResult;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PortApplication {

  private static final Logger logger = LogManager.getLogger();

  public static void main(String[] args) {
    try {
      ShipConfigurationReader shipConfigurationReader = new ShipConfigurationReader();
      List<ShipConfiguration> shipConfigurations = shipConfigurationReader.read();
      List<Ship> ships = new ArrayList<>(shipConfigurations.size());
      for (ShipConfiguration shipConfiguration : shipConfigurations) {
        ships.add(new Ship(shipConfiguration));
      }

      ExecutorService executor = Executors.newFixedThreadPool(ships.size());
      List<Future<TransferResult>> futures = new ArrayList<>(ships.size());
      for (Ship ship : ships) {
        futures.add(executor.submit(ship));
      }
      executor.shutdown();

      List<TransferResult> results = collectResults(futures);
      PortReport report = PortReport.from(results);
      SeaPort port = SeaPort.getInstance();
      Warehouse warehouse = port.getWarehouse();

      logger.info("Berths after the simulation: {} free, {} occupied",
          port.getFreeBerthCount(), port.getOccupiedBerthCount());
      logger.info("Warehouse after the simulation: {}/{} containers",
          warehouse.getContainerCount(), warehouse.getCapacity());
      logger.info("Containers moved: {} to the warehouse, {} to the ships",
          report.totalUnloadedContainers(), report.totalLoadedContainers());
      logger.info("Longest waiting for a berth: {} ms", report.longestBerthWaitingMillis());
      logger.info("Ships served: {} of {}, fully served with the whole declared cargo: {}",
          report.servedShips(), ships.size(), report.fullyServedShips());
      if (!report.isEveryShipServed(ships.size())) {
        logger.error("The port model is broken: {} of {} ships stayed unserved",
            ships.size() - report.servedShips(), ships.size());
      }
    } catch (PortException e) {
      logger.error("Port simulation has failed", e);
    }
  }

  private static List<TransferResult> collectResults(List<Future<TransferResult>> futures) {
    List<TransferResult> results = new ArrayList<>(futures.size());
    for (Future<TransferResult> future : futures) {
      try {
        results.add(future.get());
      } catch (ExecutionException e) {
        logger.error("Ship service has failed", e.getCause());
      } catch (InterruptedException e) {
        logger.error("Result collecting was interrupted", e);
        Thread.currentThread()
              .interrupt();
      }
    }
    return results;
  }
}
