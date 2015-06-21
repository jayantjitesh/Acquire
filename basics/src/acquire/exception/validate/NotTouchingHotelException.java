package acquire.exception.validate;

import acquire.basic.Location;

public class NotTouchingHotelException extends ValidationException {

  public NotTouchingHotelException(Location location) {
    this(location, 1);
  }

  public NotTouchingHotelException(Location location, int count) {
    super("Location " + location + " does not touch at least " + count
        + " hotel(s).");
  }

}
