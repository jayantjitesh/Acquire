package acquire.board.validate;

import acquire.basic.Location;
import acquire.board.Board;
import acquire.exception.validate.ValidationException;

public interface ValidationDelegate {
  public void validateNeighbor(Board board, Location neighbor)
      throws ValidationException;
}
