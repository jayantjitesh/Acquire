package acquire.exception;

import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;

public class DuplicateSpotException extends AcquireException {

  public DuplicateSpotException(Location spot, HotelName name) {
    super("The spot (" + spot + ") is already in the hotel chain " + name);
  }
}
