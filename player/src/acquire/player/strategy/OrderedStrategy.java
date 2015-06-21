package acquire.player.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.board.inspect.PossibleResult;
import acquire.protocol.description.ActionDescription;
import acquire.protocol.request.PurchaseRequest;
import acquire.state.money.BankerI;
import acquire.state.perspective.Perspective;

public class OrderedStrategy extends AbstractStrategy {

  @Override
  public PossibleResult choosePlacement(
      Map<Location, PossibleResult> validLocations, Perspective perspective) {
    List<Location> tiles = new ArrayList<Location>(validLocations.keySet());
    Collections.sort(tiles);
    return validLocations.get(tiles.get(0));

  }

  @Override
  public PurchaseRequest choosePurchase(
      Map<HotelName, Integer> availableShares, BankerI banker, int cash,
      Map<HotelName, Integer> hotelChainSize) {

    return choosePurchaseHelper(availableShares,
        Collections.reverseOrder(Collections.reverseOrder()),
        PurchaseRequest.MAX_SHARES, banker, cash, hotelChainSize);

  }

  @Override
  public void actionOccured(Perspective perspective, ActionDescription action) {
  }
}
