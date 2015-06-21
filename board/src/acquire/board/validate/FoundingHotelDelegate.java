package acquire.board.validate;

import java.util.HashSet;
import java.util.Set;
import acquire.basic.Location;
import acquire.board.Board;
import acquire.board.Spot;
import acquire.exception.validate.CannotAddHotelException;

public class FoundingHotelDelegate implements ValidationDelegate {

  private final Set<Location> _locations = new HashSet<Location>();

  @Override
  public void validateNeighbor(Board board, Location neighbor)
      throws CannotAddHotelException {

    Spot spot = board.getSpot(neighbor);
    if (spot.hasHotel()) {
      throw new CannotAddHotelException("Neighboring spot "
          + spot.getLocation() + " already has a hotel " + spot.getSpotType()
          + ".");
    }
    if (!spot.isEmpty()) {
      _locations.add(spot.getLocation());
      for (Location loc : spot.getLocation().getNeighbors()) {
        validateNeighbor(board, loc);
      }
    }
  }

  public Set<Location> getLocations() {
    return _locations;
  }

}
