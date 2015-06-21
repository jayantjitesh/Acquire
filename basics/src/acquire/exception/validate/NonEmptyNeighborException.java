package acquire.exception.validate;

import acquire.basic.Location;

public class NonEmptyNeighborException extends NonEmptyLocationException {

  public NonEmptyNeighborException(Location neighbor) {
    super(neighbor);
  }

}
