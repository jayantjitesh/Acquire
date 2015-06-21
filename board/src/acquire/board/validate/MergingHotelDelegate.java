package acquire.board.validate;

import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.board.Board;
import acquire.board.Spot;
import acquire.exception.AcquireRuntimeException;
import acquire.exception.ChainNotFoundException;
import acquire.exception.validate.TouchingSafeHotelException;
import acquire.exception.validate.TouchingUnassociatedRegionExceptions;

public class MergingHotelDelegate implements ValidationDelegate {
  private static final Logger LOGGER = Logger
      .getLogger(MergingHotelDelegate.class);
  private final Set<HotelName> _foundHotels = new HashSet<HotelName>();

  @Override
  public void validateNeighbor(Board board, Location neighbor)
      throws TouchingSafeHotelException, TouchingUnassociatedRegionExceptions {
    Spot spot = board.getSpot(neighbor);
    if (spot.isEmpty()) {
      return;
    }

    if (spot.isSingleton()) {
      throw new TouchingUnassociatedRegionExceptions(spot.getLocation());
    }

    HotelName name = HotelName.valueFrom(spot.getSpotType());
    try {
      if (board.getHotelChain(name).isSafe()) {
        throw new TouchingSafeHotelException(name);
      }
    } catch (ChainNotFoundException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "This should not happen. Name is coming from neighbor spots.", ex);
    }
    _foundHotels.add(name);
  }

  public Set<HotelName> getFoundHotels() {
    return _foundHotels;
  }
}
