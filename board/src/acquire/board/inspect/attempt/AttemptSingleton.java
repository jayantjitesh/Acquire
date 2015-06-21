package acquire.board.inspect.attempt;

import acquire.basic.Location;
import acquire.board.Board;
import acquire.board.inspect.InspectResult;
import acquire.board.inspect.SingletonResult;
import acquire.board.validate.BoardActionValidator;
import acquire.exception.validate.ValidationException;

public class AttemptSingleton implements AttemptAction {

  @Override
  public InspectResult attempt(Board board, Location location,
      BoardActionValidator validator) throws ValidationException {
    validator.validateSingleton(board, location);
    return new SingletonResult(location);
  }

}
