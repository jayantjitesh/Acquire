package acquire.board.inspect.attempt;

import acquire.basic.Location;
import acquire.board.Board;
import acquire.board.inspect.InspectResult;
import acquire.board.validate.BoardActionValidator;
import acquire.exception.validate.ValidationException;

public interface AttemptAction {
  /**
   * Produce a description of the action.
   * 
   * @param board State of the board.
   * @param location Location to attempt action at.
   * @param validator Logic to validate action.
   * @return Description of action.
   * @throws ValidationException Action cannot be executed.
   */
  InspectResult attempt(Board board, Location location,
      BoardActionValidator validator) throws ValidationException;
}
