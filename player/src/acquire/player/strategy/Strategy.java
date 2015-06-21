package acquire.player.strategy;

import java.util.List;
import java.util.Map;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.board.inspect.PossibleResult;
import acquire.protocol.description.ActionDescription;
import acquire.protocol.request.PurchaseRequest;
import acquire.state.money.BankerI;
import acquire.state.perspective.Perspective;

public interface Strategy {
  /**
   * Decide which tile to place.<br />
   * If there are no valid locations, this method will not be called.
   * 
   * @param validLocations Mapping of tiles to their valid result.
   * @param perspective State of the game as seen by this player.
   * @return Placement description.
   */
  PossibleResult choosePlacement(Map<Location, PossibleResult> validLocations,
      Perspective perspective);

  /**
   * Decide which shares to purchase.<br />
   * If there are no available shares, this method will not be called.
   * 
   * @param availableShares Mapping of hotel name to available shares. Hotels
   *          without available shares are omitted.
   * @param perspective State of the game as seen by this player.
   * @return Purchase description.
   */
  PurchaseRequest choosePurchase(Map<HotelName, Integer> availableShares,
      BankerI banker, int cash, Map<HotelName, Integer> hotelChainSize);

  /**
   * Indicate that a valid action has occurred.<br />
   * Called after the game state has changed in some way. This could be a player
   * making a valid action, the end of a players turn, or the end of the game.<br />
   * 
   * @param perspective State of the game as seen by this player after the
   *          action is completed.
   * @param action Description of the action that occurred.
   */
  void actionOccured(Perspective perspective, ActionDescription action);

  List<Boolean> keep(List<HotelName> labels);
}
