package acquire.exception;

import acquire.basic.HotelChainI.HotelName;

public class InvalidSharePriceException extends AcquireException {

  public InvalidSharePriceException(HotelName name, int size) {
    super("There is no price listed for share:" + name
        + " with the current size of the chain as: " + size);
  }

}
