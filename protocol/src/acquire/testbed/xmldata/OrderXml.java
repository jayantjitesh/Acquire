package acquire.testbed.xmldata;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import acquire.basic.HotelChainI.HotelName;
import acquire.protocol.request.PurchaseRequest;

public class OrderXml implements XMLData {

  private final PurchaseRequest _purchase;

  public OrderXml(PurchaseRequest purchase) {
    _purchase = purchase;
  }

  public PurchaseRequest getPurchase() {
    return _purchase;
  }

  @Override
  public String getTag() {
    return TagType.ORDER;
  }

  @Override
  public Element generateXML(Document doc) {
    Element orderElement = doc.createElement("order");
    for (HotelName name : _purchase.getNames()) {
      Element hotelElemnt = doc.createElement("hotel");
      hotelElemnt.setAttribute("label", name.toString());
      orderElement.appendChild(hotelElemnt);
    }
    return orderElement;
  }

}
