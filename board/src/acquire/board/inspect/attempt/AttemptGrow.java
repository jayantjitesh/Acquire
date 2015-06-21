package acquire.board.inspect.attempt;

import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.board.Board;
import acquire.board.inspect.GrowResult;
import acquire.board.inspect.InspectResult;
import acquire.board.validate.BoardActionValidator;
import acquire.exception.validate.ValidationException;

public class AttemptGrow implements AttemptAction {

  @Override
  public InspectResult attempt(Board board, Location location,
      BoardActionValidator validator) throws ValidationException {
    HotelName name = validator.validateGrowHotel(board, location);
    return new GrowResult(location, name);
  }

}
