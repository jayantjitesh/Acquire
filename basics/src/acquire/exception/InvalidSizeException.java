package acquire.exception;

import acquire.basic.HotelChainI;

public class InvalidSizeException extends ArgumentException {

  public InvalidSizeException(int count) {
    super("A hotel chain requires " + HotelChainI.MIN_SPOTS
        + ". Was only given " + count + ".");
  }
}
