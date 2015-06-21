package acquire.exception;

import acquire.basic.HotelChainI.HotelName;

public class InvalidHotelPurchaseException extends InvalidStockPurchaseException {

  public InvalidHotelPurchaseException(HotelName name, int curr, int request) {
    super(name + " has " + curr + " stocks, can't purchase " + request + ".");
  }

}
