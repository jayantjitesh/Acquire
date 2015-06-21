package acquire.protocol.request;

import acquire.basic.Action.PlacementType;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;

/**
 * Request to place a tile on a board causing a hotel to be founded.
 */
public class FoundingRequest extends HotelRequest {

  public FoundingRequest(Location location, HotelName name) {
    super(location, name);
  }

  @Override
  public PlacementType getTag() {
    return PlacementType.FOUND;
  }

}
