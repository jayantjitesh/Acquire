package acquire.exception.testbed;

import acquire.basic.Location;

public class RepeatedLocationException extends ParseException {

  public RepeatedLocationException(Location location, String xml) {
    super(location + " was listed more than once.", xml);
  }

}
