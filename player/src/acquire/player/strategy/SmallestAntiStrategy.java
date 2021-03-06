package acquire.player.strategy;

import java.util.Collections;
import java.util.Map;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.board.inspect.PossibleResult;
import acquire.protocol.description.ActionDescription;
import acquire.protocol.request.PurchaseRequest;
import acquire.state.money.BankerI;
import acquire.state.perspective.Perspective;

/**
 * implementation of Strategy interface where the smallest tile and the shares
 * in anti-alphabetical order is returned.
 * 
 */
public class SmallestAntiStrategy extends AbstractStrategy {

  /**
   * return the PlaceRequest object, that contains the Tile to place and Hotel
   * involved according to the Comparator.
   * 
   * @param validLocations : Map of valid locations and the effect of it being
   *          placed in the board
   * @param perspective : contains the Perspective of the game
   * @return : placerequest, i.e. tile and hotel label
   */
  @Override
  public PossibleResult choosePlacement(
      Map<Location, PossibleResult> validLocations, Perspective perspective) {

    return choosePlacementHelper(validLocations, perspective,
        Collections.reverseOrder(Collections.reverseOrder()));

  }

  /**
   * return the shares to buy in order of the comparable object
   * 
   * @param availableShares : available share labels and their numbers
   * @param perspective : perspective of the game
   * @return : list of hotel shares to buy
   */
  @Override
  public PurchaseRequest choosePurchase(
      Map<HotelName, Integer> availableShares, BankerI banker, int cash,
      Map<HotelName, Integer> hotelChainSize) {

    return choosePurchaseHelper(availableShares, Collections.reverseOrder(),
        PurchaseRequest.MAX_SHARES, banker, cash, hotelChainSize);

  }

  /**
   * not implemented yet.
   */
  @Override
  public void actionOccured(Perspective perspective, ActionDescription action) {
    // TODO Auto-generated method stub

  }

}
