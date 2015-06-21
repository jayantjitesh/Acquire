package acquire.exception.validate;

import acquire.basic.HotelChainI.HotelName;

public class TouchingSafeHotelException extends ValidationException {

  public TouchingSafeHotelException(HotelName name) {
    super("Hotle " + name + " is safe and can't be merged.");
  }

}
