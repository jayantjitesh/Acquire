package acquire.exception;

import acquire.basic.HotelChainI.HotelName;

public class InvalidShareNameException extends AcquireException {

  public InvalidShareNameException(HotelName name) {
    super("There are not shares for " + name);
  }

}
