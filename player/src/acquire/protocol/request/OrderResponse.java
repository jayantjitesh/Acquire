package acquire.protocol.request;

public class OrderResponse implements Response {
  private final PurchaseRequest _purchaseRequest;

  public OrderResponse(PurchaseRequest purchaseRequest) {

    _purchaseRequest = purchaseRequest;
  }

  public PurchaseRequest getPurchaseRequest() {
    return _purchaseRequest;
  }

}
