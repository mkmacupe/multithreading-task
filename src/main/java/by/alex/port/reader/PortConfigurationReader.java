package by.alex.port.reader;

import by.alex.port.config.PortConfiguration;
import by.alex.port.exception.PortException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;

public class PortConfigurationReader extends AbstractXmlReader<PortConfiguration> {

  private static final Logger logger = LogManager.getLogger();

  private static final String DEFAULT_RESOURCE = "port-config.xml";
  private static final String TAG_BERTH_COUNT = "berthCount";
  private static final String TAG_WAREHOUSE_CAPACITY = "warehouseCapacity";
  private static final String TAG_INITIAL_CONTAINERS = "initialContainers";
  private static final String TAG_MOORING_TIME = "mooringTimeMillis";
  private static final String TAG_HANDLING_TIME = "containerHandlingTimeMillis";

  @Override
  public PortConfiguration read() throws PortException {
    Element root = readRootElement(DEFAULT_RESOURCE);
    PortConfiguration configuration = parseConfiguration(root);
    logger.info("Port configuration is loaded from {}", DEFAULT_RESOURCE);
    return configuration;
  }

  private PortConfiguration parseConfiguration(Element root) {
    int berthCount = Integer.parseInt(elementText(root, TAG_BERTH_COUNT));
    int warehouseCapacity = Integer.parseInt(elementText(root, TAG_WAREHOUSE_CAPACITY));
    int initialContainers = Integer.parseInt(elementText(root, TAG_INITIAL_CONTAINERS));
    long mooringTimeMillis = Long.parseLong(elementText(root, TAG_MOORING_TIME));
    long containerHandlingTimeMillis = Long.parseLong(elementText(root, TAG_HANDLING_TIME));
    return new PortConfiguration(berthCount, warehouseCapacity, initialContainers,
        mooringTimeMillis, containerHandlingTimeMillis);
  }
}
