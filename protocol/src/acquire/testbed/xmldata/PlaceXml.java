package acquire.testbed.xmldata;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.protocol.request.FoundingRequest;
import acquire.protocol.request.GrowingRequest;
import acquire.protocol.request.MergingRequest;
import acquire.protocol.request.PlaceRequest;

/**
 * 
 * Stores the information about the place request
 * 
 */
public class PlaceXml implements XMLData {
  private final Location _location;
  // can be null
  private final HotelName _name;

  public PlaceXml(Location location) {
    this(location, null);
  }

  public PlaceXml(Location location, HotelName name) {

    _location = location;
    _name = name;
  }

  public Location getLocation() {
    return _location;
  }

  public HotelName getName() {
    return _name;
  }

  @Override
  public Element generateXML(Document doc) {
    Element element = doc.createElement(TagType.PLACE);
    Element tileElement = doc.createElement("tile");
    element.appendChild(tileElement);
    tileElement.setAttribute("column",
        Integer.toString(_location.getCol().getValue()));
    tileElement.setAttribute("row", _location.getRow().toString());
    if (_name != null) {
      Element hotelElement = doc.createElement("hotel");
      element.appendChild(hotelElement);
      hotelElement.setAttribute("label", _name.toString());
    }
    return element;
  }

  @Override
  public String getTag() {
    return TagType.PLACE;
  }

  public static PlaceXml convert(PlaceRequest request) {
    switch (request.getTag()) {

      case FOUND:
        FoundingRequest found = (FoundingRequest) request;
        return new PlaceXml(found.getLocation(), found.getName());
      case GROW:
        GrowingRequest grow = (GrowingRequest) request;
        return new PlaceXml(grow.getLocation());
      case MERGE:
        MergingRequest merge = (MergingRequest) request;
        return new PlaceXml(merge.getLocation(), merge.getName());
      case SINGLETON:
        return new PlaceXml(request.getLocation());
      default:
        return null;
    }
  }
}
