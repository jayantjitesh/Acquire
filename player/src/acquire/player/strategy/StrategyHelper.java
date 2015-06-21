package acquire.player.strategy;

import java.util.List;
import java.util.Map;
import acquire.basic.HotelChainI.HotelName;
import acquire.board.inspect.FoundingResult;
import acquire.board.inspect.GrowResult;
import acquire.board.inspect.SingletonResult;
import acquire.exception.AcquireRuntimeException;
import acquire.exception.InvalidPurchaseListException;
import acquire.exception.InvalidPurchaseRequestException;
import acquire.exception.InvalidShareNameException;
import acquire.protocol.request.FoundingRequest;
import acquire.protocol.request.GrowingRequest;
import acquire.protocol.request.PurchaseRequest;
import acquire.protocol.request.SingletonRequest;

public class StrategyHelper {

  /**
   * Create a singleton request from a singleton inspect result.
   * 
   * @param result Description of what would happen if a tile was placed on the
   *          board.
   * @return Request this player wants to make to the rule enforcer.
   */
  public static SingletonRequest convertToRequest(SingletonResult result) {
    return new SingletonRequest(result.getLocation());
  }

  /**
   * Create a founding request from a singleton inspect result.
   * 
   * @param result Description of what would happen if a tile was placed on the
   *          board.
   * @return Request this player wants to make to the rule enforcer.
   */
  public static FoundingRequest convertToRequest(FoundingResult result) {
    return new FoundingRequest(result.getLocation(), result.getName());
  }

  /**
   * Create a growing request from a singleton inspect result.
   * 
   * @param result Description of what would happen if a tile was placed on the
   *          board.
   * @return Request this player wants to make to the rule enforcer.
   */
  public static GrowingRequest convertToRequest(GrowResult result) {
    return new GrowingRequest(result.getLocation());
  }

  public static PurchaseRequest makePurchaseRequest(List<HotelName> names)
      throws InvalidPurchaseListException {
    if (names.size() > PurchaseRequest.MAX_SHARES) {
      throw new InvalidPurchaseListException(names);
    }
    try {
      switch (names.size()) {
        case 0:

          return new PurchaseRequest();

        case 1:
          return new PurchaseRequest(names.get(0));
        case 2:
          return new PurchaseRequest(names.get(0), names.get(1));
          // case 3:
          // return new PurchaseRequest(names.get(0), names.get(1),
          // names.get(2));
        default:
          throw new AcquireRuntimeException("make compiler happy.");
      }

    } catch (InvalidPurchaseRequestException ex) {
      throw new InvalidPurchaseListException(names);
    }
  }

  /**
   * Decrement the number of available shares for a hotel. <br />
   * If there are no more shares for the hotel after the decrement, remove the
   * hotel from the map.
   * 
   * @param availableShares Map to be mutated.
   * @param name Hotel to remove a share for.
   * @throws InvalidShareNameException No shares exist.
   */
  public static void removeShare(Map<HotelName, Integer> availableShares,
      HotelName name) throws InvalidShareNameException {
    Integer numShares = availableShares.get(name);
    if (numShares == null) {
      throw new InvalidShareNameException(name);
    }
    if (numShares > 1) {
      availableShares.put(name, numShares - 1);
    } else {
      availableShares.remove(name);
    }
  }
}
