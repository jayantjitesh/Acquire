package acquire.exception;

import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;

public class NotValidAcquirerException extends AcquireException {

  public NotValidAcquirerException(HotelName label, Location loc) {
    super("The hotel name: " + label
        + " is not a valid acquirer for merging at " + loc);
  }

}
