package by.alex.port.reader;

import by.alex.port.config.ShipConfiguration;
import by.alex.port.entity.ShipOperation;
import by.alex.port.exception.PortException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ShipConfigurationReader extends AbstractXmlReader<List<ShipConfiguration>> {

  private static final Logger logger = LogManager.getLogger();

  private static final String DEFAULT_RESOURCE = "ships.xml";
  private static final String TAG_SHIP = "ship";
  private static final String ATTRIBUTE_ID = "id";
  private static final String ATTRIBUTE_CAPACITY = "capacity";
  private static final String ATTRIBUTE_CONTAINERS_ON_BOARD = "containersOnBoard";
  private static final String ATTRIBUTE_OPERATION = "operation";
  private static final String ATTRIBUTE_CONTAINERS_TO_UNLOAD = "containersToUnload";
  private static final String ATTRIBUTE_CONTAINERS_TO_LOAD = "containersToLoad";

  @Override
  public List<ShipConfiguration> read() throws PortException {
    Element root = readRootElement(DEFAULT_RESOURCE);
    NodeList shipNodes = root.getElementsByTagName(TAG_SHIP);
    int shipCount = shipNodes.getLength();
    List<ShipConfiguration> ships = new ArrayList<>(shipCount);
    for (int i = 0; i < shipCount; i++) {
      Node shipNode = shipNodes.item(i);
      ShipConfiguration shipConfiguration = parseShip((Element) shipNode);
      ships.add(shipConfiguration);
    }
    logger.info("Ship configuration is loaded from {}: {} ships are expected", DEFAULT_RESOURCE,
        shipCount);
    return List.copyOf(ships);
  }

  private ShipConfiguration parseShip(Element shipElement) {
    long shipId = Long.parseLong(shipElement.getAttribute(ATTRIBUTE_ID));
    int capacity = Integer.parseInt(shipElement.getAttribute(ATTRIBUTE_CAPACITY));
    int containersOnBoard = Integer.parseInt(
        shipElement.getAttribute(ATTRIBUTE_CONTAINERS_ON_BOARD));
    ShipOperation operation = ShipOperation.valueOf(shipElement.getAttribute(ATTRIBUTE_OPERATION));
    int containersToUnload = Integer.parseInt(
        shipElement.getAttribute(ATTRIBUTE_CONTAINERS_TO_UNLOAD));
    int containersToLoad = Integer.parseInt(shipElement.getAttribute(ATTRIBUTE_CONTAINERS_TO_LOAD));
    return new ShipConfiguration(shipId, capacity, containersOnBoard, operation,
        containersToUnload, containersToLoad);
  }
}
