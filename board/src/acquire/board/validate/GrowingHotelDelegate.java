package acquire.board.validate;

import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.board.Board;
import acquire.board.Spot;
import acquire.board.Spot.SpotType;
import acquire.exception.validate.DifferentHotelException;
import acquire.exception.validate.NotTouchingHotelException;
import acquire.exception.validate.TouchingUnassociatedRegionExceptions;

public class GrowingHotelDelegate implements ValidationDelegate {
  private final Location _location;
  private SpotType _foundName = null;

  /**
   * Constructor
   * 
   * @param location Location who's neighbors are being validated by this
   *          delegate.
   */
  public GrowingHotelDelegate(Location location) {
    _location = location;
  }

  @Override
  public void validateNeighbor(Board board, Location neighbor)
      throws DifferentHotelException, TouchingUnassociatedRegionExceptions {
    Spot spot = board.getSpot(neighbor);

    if (spot.isSingleton()) {
      throw new TouchingUnassociatedRegionExceptions(spot.getLocation());
    }
    if (_foundName == null && spot.hasHotel()) {
      _foundName = spot.getSpotType();
    } else if (spot.hasHotel() && _foundName != spot.getSpotType()) {
      throw new DifferentHotelException(_foundName, spot.getSpotType());
    }
  }

  /**
   * @return Name of hotel in at least one of the neighboring locations.
   * @throws NotTouchingHotelException None of the neighbors have a hotel.
   */
  public HotelName getHotelName() throws NotTouchingHotelException {
    if (_foundName == null) {
      throw new NotTouchingHotelException(_location);
    }
    return HotelName.valueFrom(_foundName);
  }
}
