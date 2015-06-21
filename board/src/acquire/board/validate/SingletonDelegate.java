package acquire.board.validate;

import acquire.basic.Location;
import acquire.board.Board;
import acquire.board.Spot;
import acquire.exception.validate.NonEmptyNeighborException;

public class SingletonDelegate implements ValidationDelegate {

  @Override
  public void validateNeighbor(Board board, Location neighbor)
      throws NonEmptyNeighborException {
    Spot spot = board.getSpot(neighbor);
    if (spot.hasHotel()
        || (spot.isSingleton() && board.getAvailableHotelCount() > 0)) {
      throw new NonEmptyNeighborException(neighbor);
    }
  }

}
