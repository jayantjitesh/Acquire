package acquire.exception;

import acquire.basic.Location;

public class NotValidMergerException extends AcquireException {

  public NotValidMergerException(Location loc) {
    super("There is no merging happening at " + loc);
  }

}
