package acquire.board.inspect.attempt;

import java.util.Set;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.board.Board;
import acquire.board.inspect.FoundingResult;
import acquire.board.inspect.InspectResult;
import acquire.board.validate.BoardActionValidator;
import acquire.exception.validate.ValidationException;

public class AttemptFounding implements AttemptAction {

  @Override
  public InspectResult attempt(Board board, Location location,
      BoardActionValidator validator) throws ValidationException {
    for (HotelName name : HotelName.values()) {
      if (board.isHotelAvailable(name)) {
        // allow exception to be thrown
        // if an available chain can't be found, nothing can be founded.
        Set<Location> addedHotels = validator.validateAddHotel(board, location,
            name);
        return new FoundingResult(location, name, addedHotels);
      }
    }
    throw new ValidationException("No available hotels to found.");
  }

}
