package acquire.player.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.board.inspect.PossibleResult;
import acquire.exception.AcquireRuntimeException;
import acquire.exception.InvalidPurchaseListException;
import acquire.exception.InvalidSharePriceException;
import acquire.protocol.description.ActionDescription;
import acquire.protocol.request.PurchaseRequest;
import acquire.state.money.BankerI;
import acquire.state.perspective.Perspective;

/**
 * this class represents the abstract strategy classes that includes common
 * functionality for classes LargestAlphaStrategy and SmallestAntiStratgey.
 * 
 */
public abstract class AbstractStrategy implements Strategy {

  /**
   * return the PlaceRequest object, that contains the Tile to place and Hotel
   * involved according to the Comparator.
   * 
   * @param validLocations : Map of valid locations and the effect of it being
   *          placed in the board
   * @param perspective : contains the Perspective of the game
   * @param comparator : comparator for ordering the tiles, either ascending or
   *          descending
   * @return : placerequest, i.e. tile and hotel label
   */
  protected PossibleResult choosePlacementHelper(
      Map<Location, PossibleResult> validLocations, Perspective perspective,
      Comparator<Object> comparator) {

    List<Location> locations = new ArrayList<Location>(validLocations.keySet());

    Collections.sort(locations, comparator);

    for (Location l : locations) {
      PossibleResult result = validLocations.get(l);
      return result;
    }
    throw new AcquireRuntimeException("choosePlacement: "
        + "LargestAlphaStrategy: exhausted all locations");

  }

  @Override
  public void actionOccured(Perspective perspective, ActionDescription action) {
    // TODO Auto-generated method stub

  }

  @Override
  public List<Boolean> keep(List<HotelName> labels) {
    List<Boolean> result = new ArrayList<Boolean>();
    for (HotelName hotelName : labels) {
      result.add(true);
    }
    return result;
  }

  /**
   * return the shares to buy in order of the comparable object
   * 
   * @param availableShares : available share labels and their numbers
   * @param perspective : perspective of the game
   * @param c : comparator that orders the hotel shares
   * @return : list of hotel shares to buy
   */
  protected PurchaseRequest choosePurchaseHelper(
      Map<HotelName, Integer> availableShares, Comparator<Object> c,
      int maxShare, BankerI banker, int cash,
      Map<HotelName, Integer> hotelChainSize) {

    List<HotelName> hotels = new ArrayList<HotelName>(availableShares.keySet());
    List<HotelName> sharestoBuy = new ArrayList<HotelName>();
    if (c != null)
      Collections.sort(hotels, c);
    if (maxShare > 0) {
      label: for (HotelName h : hotels) {

        int size = hotelChainSize.get(h);
        int sharePrice = 0;
        try {

          sharePrice = banker.getSharePrice(h, size);
        } catch (InvalidSharePriceException ex) {
          continue;
        }
        while (availableShares.get(h) > 0 && cash >= sharePrice
            && sharePrice > 0) {

          cash = cash - sharePrice;
          sharestoBuy.add(h);
          if (sharestoBuy.size() >= maxShare) {
            break label;
          }
          availableShares.put(h, availableShares.get(h) - 1);

        }
        if (sharestoBuy.size() > 0)
          break;

      }
    }
    try {
      return StrategyHelper.makePurchaseRequest(sharestoBuy);
    } catch (InvalidPurchaseListException e) {
      throw new AcquireRuntimeException("make compiler happy.");
    }

  }

}
