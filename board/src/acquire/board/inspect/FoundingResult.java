package acquire.board.inspect;

import java.util.Set;
import acquire.basic.Action.PlacementType;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;

/**
 * If the placement is causing founding of new hotel
 */
public class FoundingResult extends PossibleResult {
  private final HotelName _name;
  private final Set<Location> _addedHotels;

  public FoundingResult(Location location, HotelName hotel,
      Set<Location> addedHotels) {
    super(location);
    _name = hotel;
    _addedHotels = addedHotels;
  }

  public HotelName getName() {
    return _name;
  }

  public Set<Location> getAddedHotels() {
    return _addedHotels;
  }

  @Override
  public PlacementType getType() {
    return PlacementType.FOUND;
  }
}
