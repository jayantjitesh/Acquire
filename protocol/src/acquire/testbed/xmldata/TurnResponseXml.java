package acquire.testbed.xmldata;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import acquire.basic.HotelChainI.HotelName;
import acquire.protocol.request.PurchaseRequest;

/**
 * 
 * Stores the information about the place request
 * 
 */
public class TurnResponseXml implements XMLData {
  public static final int MAX_SHARE_BUY = 3;
  private final PlaceXml _place;
  private final PurchaseRequest _purchase;

  public TurnResponseXml(PlaceXml placeResponse) {
    this(placeResponse, null);
  }

  public TurnResponseXml(PlaceXml placeResponse, PurchaseRequest purchaseRequest) {
    _purchase = purchaseRequest;
    _place = placeResponse;
  }

  public TurnResponseXml(PurchaseRequest purchaseRequest) {
    this(null, purchaseRequest);
  }

  public PurchaseRequest getPurchase() {
    return _purchase;
  }

  public PlaceXml getPlace() {
    return _place;
  }

  @Override
  public Element generateXML(Document doc) {
    Element element = doc.createElement(TagType.TURN_RESPONSE);
    if (_place != null) {
      element.appendChild(_place.generateXML(doc));
    }

    Element orderElement = doc.createElement("order");
    element.appendChild(orderElement);
    for (HotelName name : _purchase.getNames()) {
      Element hotelElemnt = doc.createElement("hotel");
      hotelElemnt.setAttribute("label", name.toString());
      orderElement.appendChild(hotelElemnt);
    }
    return element;
  }

  @Override
  public String getTag() {
    return TagType.TURN_RESPONSE;
  }
}
