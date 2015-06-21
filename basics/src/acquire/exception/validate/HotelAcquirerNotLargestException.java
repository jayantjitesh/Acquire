package acquire.exception.validate;

import acquire.basic.HotelChainI.HotelName;

public class HotelAcquirerNotLargestException extends ValidationException {

  public HotelAcquirerNotLargestException(HotelName name) {
    super(name + " was not the largest hotel among neighbors.");
  }

}
