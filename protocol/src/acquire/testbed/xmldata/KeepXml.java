package acquire.testbed.xmldata;

import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import acquire.basic.HotelChainI.HotelName;

public class KeepXml implements XMLData {
  private final List<HotelName> _labels;

  public KeepXml(List<HotelName> labels) {
    _labels = labels;
  }

  public List<HotelName> getLabels() {
    return _labels;
  }

  @Override
  public Element generateXML(Document doc) {
    Element element = doc.createElement(TagType.KEEP);

    for (HotelName name : _labels) {
      Element hotelElemnt = doc.createElement("hotel");
      hotelElemnt.setAttribute("label", name.toString());
      element.appendChild(hotelElemnt);
    }
    return element;
  }

  @Override
  public String getTag() {
    return TagType.KEEP;
  }

}
