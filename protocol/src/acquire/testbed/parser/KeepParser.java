package acquire.testbed.parser;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import acquire.basic.HotelChainI.HotelName;
import acquire.exception.ArgumentException;
import acquire.exception.testbed.ParseException;
import acquire.testbed.xmldata.KeepDecisionXml;
import acquire.testbed.xmldata.KeepXml;
import acquire.testbed.xmldata.TagType;
import acquire.testbed.xmldata.XMLData;

public class KeepParser extends XMLParser {

  public KeepParser() {
    super(TagType.KEEP);
  }

  @Override
  public XMLData parse(Node node, String xml) throws ParseException,
      ArgumentException {
    NodeList childNodes = node.getChildNodes();
    List<HotelName> labels = new ArrayList<HotelName>();
    List<Boolean> decision = new ArrayList<Boolean>();

    for (int i = 0; i < childNodes.getLength(); i++) {
      Node item = childNodes.item(i);
      if (item.getNodeName() == HOTEL) {
        String name = item.getAttributes().getNamedItem(LABEL).getNodeValue();
        HotelName hotelName = HotelName.valueFrom(name);
        labels.add(hotelName);
      } else if (item.getNodeName() == "true") {
        decision.add(true);
      } else if (item.getNodeName() == "false") {
        decision.add(false);
      }
    }
    if (labels.isEmpty())
      return new KeepDecisionXml(decision);
    else
      return new KeepXml(labels);
  }

}
