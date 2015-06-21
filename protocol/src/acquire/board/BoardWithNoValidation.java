package acquire.board;

import java.util.Map;
import acquire.basic.HotelChain;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;

public class BoardWithNoValidation extends Board {
  /**
   * Constructor for directly injecting the data
   * 
   * @param hotels the hotels founded on the board
   * @param spots on the board
   */
  public BoardWithNoValidation(Map<HotelName, HotelChain> hotels,
      Map<Location, Spot> spots) {
    super(hotels, spots);
  }
}
