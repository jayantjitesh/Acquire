package acquire.testbed.parser;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.exception.ArgumentException;
import acquire.exception.InvalidPurchaseListException;
import acquire.exception.InvalidPurchaseRequestException;
import acquire.exception.testbed.ParseException;
import acquire.protocol.request.PurchaseRequest;
import acquire.testbed.xmldata.PlaceXml;
import acquire.testbed.xmldata.TagType;
import acquire.testbed.xmldata.TurnResponseXml;

/**
 * 
 * Parses the place request in xml format to the <code>PlaceRequest</code>
 * 
 */
public class TurnParser extends XMLParser {
  public TurnParser() {
    super(TagType.TURN_RESPONSE);
  }

  protected static final String ROW = "row";
  protected static final String COLUMN = "column";
  protected static final String NAME = "name";
  protected static final String LABEL = "label";
  protected static final String TILE = "tile";
  protected static final String HOTEL = "hotel";
  private static final String PLACEMENT = "placement";
  private static final String ORDER = "order";

  @Override
  public TurnResponseXml parse(Node node, String xml) throws ParseException,
      ArgumentException {
    PlaceXml parsePlace = null;
    PurchaseRequest parseBuy = null;

    NodeList childNodes = node.getChildNodes();
    for (int i = 0; i < childNodes.getLength(); i++) {
      Node item = childNodes.item(i);
      if (item.getNodeName().equals(PLACEMENT)) {
        parsePlace = parsePlace(item, xml);
      } else if (item.getNodeName().equals(ORDER)) {
        parseBuy = parseBuy(item, xml);
      }

    }
    return new TurnResponseXml(parsePlace, parseBuy);
  }

  public PlaceXml parsePlace(Node node, String xml) throws ParseException,
      ArgumentException {

    NodeList childNodes = node.getChildNodes();
    Node nameNode = null;
    Location location = null;
    for (int i = 0; i < childNodes.getLength(); i++) {
      Node item = childNodes.item(i);
      String name = item.getNodeName();
      if (name.equals(TILE)) {
        location = parseTile(item, xml);
      } else if (name.equals(HOTEL)) {
        nameNode = item.getAttributes().getNamedItem(LABEL);
      } else {
        throw new ParseException("Invalid xml element: " + name, xml);
      }
    }
    if (nameNode == null) {
      return new PlaceXml(location);
    } else {
      return new PlaceXml(location,
          HotelName.valueFrom(nameNode.getNodeValue()));
    }
  }

  public static PurchaseRequest parseBuy(Node node, String xml)
      throws ParseException, ArgumentException {
    NodeList childNodes = node.getChildNodes();
    List<HotelName> labels = new ArrayList<HotelName>();

    for (int i = 0; i < childNodes.getLength(); i++) {
      Node item = childNodes.item(i);
      if (item.getNodeName() == HOTEL) {
        String name = item.getAttributes().getNamedItem(LABEL).getNodeValue();
        HotelName hotelName = HotelName.valueFrom(name);
        labels.add(hotelName);
      }
    }

    try {
      return PurchaseRequest.createPurchaseRequest(labels);
    } catch (InvalidPurchaseListException ex) {
      throw new ParseException("The purchase request is not valid", xml);
    } catch (InvalidPurchaseRequestException ex) {
      throw new ParseException("The purchase request is not valid", xml);
    }
  }

}
