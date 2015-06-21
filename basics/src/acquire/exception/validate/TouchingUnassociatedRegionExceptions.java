package acquire.exception.validate;

import acquire.basic.Location;

public class TouchingUnassociatedRegionExceptions extends ValidationException {

  public TouchingUnassociatedRegionExceptions(Location location) {
    super("Location " + location + "  is Singleton");
  }

}
