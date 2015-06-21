package acquire.testbed;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import acquire.exception.ArgumentException;
import acquire.exception.testbed.ParseException;
import acquire.testbed.parser.RunParser;
import acquire.testbed.request.RunRequest;

public class Parser {
  private static final Logger LOGGER = Logger.getLogger(Parser.class);
  public static final String ENCODING = "UTF-8";
  private static final String ELEMENT_ADD_OPEN = "<b>";
  private static final String ELEMENT_ADD_END = "</b>";

  private final RunParser _parser;

  private final DocumentBuilder _docBuilder;

  public Parser(RunParser parser) throws ParserConfigurationException {
    if (parser == null) {
      throw new ParserConfigurationException(
          "At least one XMLParser is required.");
    }
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    _docBuilder = dbFactory.newDocumentBuilder();
    _parser = parser;
  }

  /**
   * Parse the given XML into a Run request
   * 
   * @param node Representation of XML.
   * @param xml Content to parse.
   * @return Description of given XML.
   * @throws ParseException Error parsing XML.
   */
  public RunRequest parse(Node node, String xml) throws ParseException {
    LOGGER.info("parse to request" + xml);
    String name = node.getNodeName();
    try {
      if (name == "run") {
        return _parser.parse(node, xml);
      } else {
        throw new ParseException("Invalid request, was (" + name + ").", xml);
      }
    } catch (ArgumentException ex) {
      throw new ParseException(ex.getMessage(), xml);
    }

  }

  /**
   * Parses the multiple requests and create a List of Node out of it
   * 
   * @param xml Content to parse.
   * @return the List of Node
   * @throws ParseException Error parsing XML.
   */
  public List<Node> parseToNodes(String xml) throws ParseException {
    Element element = parseToElement(ELEMENT_ADD_OPEN + xml + ELEMENT_ADD_END);
    NodeList childNodes = element.getChildNodes();
    List<Node> nodes = new ArrayList<Node>();
    for (int i = 0; i < childNodes.getLength(); i++) {
      nodes.add(childNodes.item(i));
    }
    return nodes;
  }

  /**
   * Parse the given XML into a document element.
   * 
   * @param xml Content to parse.
   * @return Document of given XML.
   * @throws ParseException Error parsing XML.
   */
  public Element parseToElement(String xml) throws ParseException {
    LOGGER.info("attempting to parse---|" + xml + "|---");
    Document doc = null;
    try {
      doc = _docBuilder.parse(new ByteArrayInputStream(xml.getBytes(ENCODING)));
    } catch (SAXException ex) {
      throw new ParseException("There was an error parsing the content.", xml,
          ex);
    } catch (IOException ex) {
      throw new ParseException("There was an error reading the content.", xml,
          ex);
    }

    Element document = doc.getDocumentElement();
    removeEmptyTextNodes(document);
    return document;
  }

  public static void removeEmptyTextNodes(Node node) {
    NodeList nodeList = node.getChildNodes();
    Node childNode;
    for (int x = nodeList.getLength() - 1; x >= 0; x--) {
      childNode = nodeList.item(x);
      if (childNode.getNodeType() == Node.TEXT_NODE) {
        if (childNode.getNodeValue().trim().equals("")) {
          node.removeChild(childNode);
        }
      } else if (childNode.getNodeType() == Node.ELEMENT_NODE) {
        removeEmptyTextNodes(childNode);
      }
    }
  }

  public static Parser makeDefaultParser() throws ParserConfigurationException {
    return new Parser(new RunParser());
  }
}
