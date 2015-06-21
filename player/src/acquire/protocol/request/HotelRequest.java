package acquire.protocol.request;

import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;

public abstract class HotelRequest extends PlaceRequest {
  private final HotelName _name;

  public HotelRequest(Location location, HotelName name) {
    super(location);
    _name = name;
  }

  public HotelName getName() {
    return _name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((_name == null) ? 0 : _name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    HotelRequest other = (HotelRequest) obj;
    if (_name != other._name)
      return false;
    return true;
  }

}
