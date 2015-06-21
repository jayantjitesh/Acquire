package acquire.game.tree;

import acquire.basic.Location;
import acquire.protocol.request.PlaceRequest;
import acquire.protocol.request.PurchaseRequest;

/**
 * An Edge is a new Edge(Location location, PurchaseRequest purchaseRequest,
 * PlaceRequest placeRequest)
 * Interp:
 * location : represents the new Tile alloted to the player if any
 * purchaseRequest : represents the Shares that the player wish to buy, if any 
 * placeRequest : represents the Tile that player wish to place on Board
 */

public class Edge {
	private final PurchaseRequest _purchaseRequest;
	private final PlaceRequest _placeRequest;
	private final Location _tileToDistribute;

	public Edge(Location location, PurchaseRequest purchaseRequest,
			PlaceRequest placeRequest) {
		_purchaseRequest = purchaseRequest;
		_placeRequest = placeRequest;
		_tileToDistribute = location;
	}

	public Location getTileToDistribute() {
		return _tileToDistribute;
	}

	public PurchaseRequest getBuyRequest() {
		return _purchaseRequest;
	}

	public PlaceRequest getPlaceRequest() {
		return _placeRequest;
	}

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((_placeRequest == null) ? 0 : _placeRequest.hashCode());
    result = prime * result
        + ((_purchaseRequest == null) ? 0 : _purchaseRequest.hashCode());
    result = prime * result
        + ((_tileToDistribute == null) ? 0 : _tileToDistribute.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Edge other = (Edge) obj;
    if (_placeRequest == null) {
      if (other._placeRequest != null)
        return false;
    } else if (!_placeRequest.equals(other._placeRequest))
      return false;
    if (_purchaseRequest == null) {
      if (other._purchaseRequest != null)
        return false;
    } else if (!_purchaseRequest.equals(other._purchaseRequest))
      return false;
    if (_tileToDistribute == null) {
      if (other._tileToDistribute != null)
        return false;
    } else if (!_tileToDistribute.equals(other._tileToDistribute))
      return false;
    return true;
  }

}
