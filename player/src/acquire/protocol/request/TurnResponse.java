package acquire.protocol.request;

/**
 * A TurnResponse is a new TurnResponse(PurchaseRequest, PlaceRequest) Interp:
 * PurchaseRequest : A List of Hotel Name PlaceRequest : A Location specify the
 * Tile that player choose to place
 * 
 */

public class TurnResponse implements Response {
  private final PurchaseRequest _purchaseRequest;
  private final PlaceRequest _placeRequest;

  public TurnResponse(PlaceRequest placeRequest, PurchaseRequest purchaseRequest) {
    _placeRequest = placeRequest;
    _purchaseRequest = purchaseRequest;
  }

  public PurchaseRequest getPurchaseRequest() {
    return _purchaseRequest;
  }

  public PlaceRequest getPlaceRequest() {
    return _placeRequest;
  }
}
