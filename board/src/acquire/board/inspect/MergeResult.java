package acquire.board.inspect;

import java.util.Set;
import acquire.basic.Action.PlacementType;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;

/**
 * If the placement is causing merging of two or more hotels
 */
public class MergeResult extends PossibleResult {
  private final Set<HotelName> _hotels;
  private final Set<HotelName> _largest;

  public MergeResult(Location location, Set<HotelName> hotels,
      Set<HotelName> largest) {
    super(location);
    _hotels = hotels;
    _largest = largest;
  }

  /**
   * @return Hotels involved in merger.
   */
  public Set<HotelName> getHotels() {
    return _hotels;
  }

  /**
   * @return Hotel(s) with the largest size.
   */
  public Set<HotelName> getLargest() {
    return _largest;
  }

  @Override
  public PlacementType getType() {
    return PlacementType.MERGE;
  }
}
