package acquire.exception;

import acquire.basic.HotelChainI.HotelName;

public class ChainNotFoundException extends AcquireException {

  public ChainNotFoundException(HotelName name) {
    super("Hotel chain " + name + " is not founded yet.");
  }
}
