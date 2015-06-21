package acquire.testbed.parser;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import acquire.basic.HotelChainI.HotelName;
import acquire.exception.ArgumentException;
import acquire.exception.InvalidPurchaseListException;
import acquire.exception.InvalidPurchaseRequestException;
import acquire.exception.testbed.ParseException;
import acquire.protocol.request.PurchaseRequest;
import acquire.testbed.xmldata.OrderXml;
import acquire.testbed.xmldata.TagType;
import acquire.testbed.xmldata.XMLData;

public class OrderParser extends XMLParser {

  public OrderParser() {
    super(TagType.ORDER);
  }

  @Override
  public XMLData parse(Node node, String xml) throws ParseException,
      ArgumentException {
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
      return new OrderXml(PurchaseRequest.createPurchaseRequest(labels));
    } catch (InvalidPurchaseListException ex) {
      throw new ParseException("The purchase request is not valid", xml);
    } catch (InvalidPurchaseRequestException ex) {
      throw new ParseException("The purchase request is not valid", xml);
    }
  }
}
