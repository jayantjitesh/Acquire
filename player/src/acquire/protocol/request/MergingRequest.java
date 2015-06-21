package acquire.protocol.request;

import acquire.basic.Action.PlacementType;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;

/**
 * Request to place a tile on a board causing a hotel to acquire at least one
 * other hotel.
 */
public class MergingRequest extends HotelRequest {

  public MergingRequest(Location location, HotelName name) {
    super(location, name);
  }

  @Override
  public PlacementType getTag() {
    return PlacementType.MERGE;
  }

}
