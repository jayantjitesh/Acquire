package acquire.exception;

import java.util.List;
import acquire.basic.HotelChainI.HotelName;
import acquire.protocol.request.PurchaseRequest;

public class InvalidPurchaseListException extends AcquireException {

  public InvalidPurchaseListException(List<HotelName> names) {
    super("Purchase may have at most " + PurchaseRequest.MAX_SHARES + " has "
        + names.size() + ".");
  }

}
