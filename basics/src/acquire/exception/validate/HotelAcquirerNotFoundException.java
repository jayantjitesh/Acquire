package acquire.exception.validate;

import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;

public class HotelAcquirerNotFoundException extends ValidationException {

  public HotelAcquirerNotFoundException(Location location, HotelName name) {
    super(location + " does not touch " + name);
  }

}
