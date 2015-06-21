package acquire.protocol.request;

import acquire.basic.Action.PlacementType;
import acquire.basic.Location;

/**
 * Request to place a tile on a board causing a hotel to grow.
 */
public class GrowingRequest extends PlaceRequest {

  public GrowingRequest(Location location) {
    super(location);
  }

  @Override
  public PlacementType getTag() {
    return PlacementType.GROW;
  }

}
