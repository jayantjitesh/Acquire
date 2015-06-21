package acquire.board.inspect;

import acquire.basic.Action.PlacementType;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;

/**
 * If the placement is causing growing of a existing hotel
 */
public class GrowResult extends PossibleResult {
  private final HotelName _name;

  public GrowResult(Location location, HotelName name) {
    super(location);
    _name = name;
  }

  public HotelName getName() {
    return _name;
  }

  @Override
  public PlacementType getType() {
    return PlacementType.GROW;
  }
}
