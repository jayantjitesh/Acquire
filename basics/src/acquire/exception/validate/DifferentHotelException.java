package acquire.exception.validate;

import acquire.board.Spot.SpotType;

public class DifferentHotelException extends ValidationException {

  public DifferentHotelException(SpotType name1, SpotType name2) {
    super("Location touches two hotels (" + name1 + "," + name2 + ")");
  }
}
