package acquire.exception;

import acquire.basic.HotelChainI.HotelName;

public class InvalidStockReturnException extends AcquireException {

  public InvalidStockReturnException(HotelName name, int curr, int returnCount) {
    super(name + " has " + curr + " stocks, can't return " + returnCount + ".");
  }

}
