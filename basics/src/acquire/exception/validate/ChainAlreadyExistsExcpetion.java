package acquire.exception.validate;

import acquire.basic.HotelChainI.HotelName;

public class ChainAlreadyExistsExcpetion extends ValidationException {

  public ChainAlreadyExistsExcpetion(HotelName name) {
    super(name + " already exists in the board.");
  }

}
