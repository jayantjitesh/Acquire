package acquire.protocol.request;

import acquire.basic.Action.PlacementType;
import acquire.basic.Location;

/**
 * Request to place a tile on a board causing no effect on other tiles.
 */
public class SingletonRequest extends PlaceRequest {

  public SingletonRequest(Location location) {
    super(location);
  }

  @Override
  public PlacementType getTag() {
    return PlacementType.SINGLETON;
  }

}
