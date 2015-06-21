package acquire.board.inspect.attempt;

import java.util.Set;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.board.Board;
import acquire.board.inspect.InspectResult;
import acquire.board.inspect.MergeResult;
import acquire.board.validate.BoardActionValidator;
import acquire.exception.validate.ValidationException;

public class AttemptMerge implements AttemptAction {

  @Override
  public InspectResult attempt(Board board, Location location,
      BoardActionValidator validator) throws ValidationException {
    Set<HotelName> hotels = validator.validateMergingHotels(board, location);
    Set<HotelName> largest = validator.getLargest(board, hotels);
    return new MergeResult(location, hotels, largest);
  }

}
