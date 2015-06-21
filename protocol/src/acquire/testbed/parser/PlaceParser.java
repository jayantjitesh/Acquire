package acquire.testbed.parser;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.exception.ArgumentException;
import acquire.exception.testbed.ParseException;
import acquire.testbed.xmldata.PlaceXml;
import acquire.testbed.xmldata.TagType;

public class PlaceParser extends XMLParser {

  public PlaceParser() {
    super(TagType.PLACE);
  }

  @Override
  public PlaceXml parse(Node node, String xml) throws ParseException,
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

}
