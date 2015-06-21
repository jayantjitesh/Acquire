package acquire.exception.validate;

import acquire.basic.Location;

public class NonEmptyLocationException extends ValidationException {

  public NonEmptyLocationException(Location neighbor) {
    super("Location " + neighbor + " is not empty.");
  }

}
