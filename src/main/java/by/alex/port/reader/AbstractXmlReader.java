package by.alex.port.reader;

import by.alex.port.exception.PortException;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

abstract class AbstractXmlReader<T> {

  private static final String DISALLOW_DOCTYPE_FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";

  public abstract T read() throws PortException;

  protected Element readRootElement(String resourceName) throws PortException {
    ClassLoader classLoader = getClass().getClassLoader();
    try (InputStream input = classLoader.getResourceAsStream(resourceName)) {
      if (input == null) {
        throw new PortException("Configuration resource is not found: " + resourceName);
      }
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
      factory.setFeature(DISALLOW_DOCTYPE_FEATURE, true);
      factory.setExpandEntityReferences(false);
      DocumentBuilder documentBuilder = factory.newDocumentBuilder();
      Document document = documentBuilder.parse(input);
      return document.getDocumentElement();
    } catch (IOException | ParserConfigurationException | SAXException e) {
      throw new PortException("Unable to parse XML resource " + resourceName, e);
    }
  }

  protected String elementText(Element root, String tagName) {
    NodeList elements = root.getElementsByTagName(tagName);
    Node element = elements.item(0);
    String text = element.getTextContent();
    return text.trim();
  }
}
