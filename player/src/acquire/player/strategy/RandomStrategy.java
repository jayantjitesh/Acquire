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
import acquire.util.RandomHelper;

public class RandomStrategy extends AbstractStrategy {

  private final RandomHelper _random;

  public RandomStrategy() {
    this(new RandomHelper());
  }

  RandomStrategy(RandomHelper random) {
    _random = random;
  }

  @Override
  public PossibleResult choosePlacement(
      Map<Location, PossibleResult> validLocations, Perspective perspective) {
    Location location = _random.randomElement(validLocations.keySet());

    return validLocations.get(location);

  }

  @Override
  public PurchaseRequest choosePurchase(
      Map<HotelName, Integer> availableShares, BankerI banker, int cash,
      Map<HotelName, Integer> hotelChainSize) {
    int sharesToBuy = _random.randomInt(PurchaseRequest.MAX_SHARES + 1);

    return choosePurchaseHelper(availableShares,
        Collections.reverseOrder(Collections.reverseOrder()), sharesToBuy,
        banker, cash, hotelChainSize);

  }

  @Override
  public void actionOccured(Perspective perspective, ActionDescription action) {
    // TODO Auto-generated method stub

  }
}
