package acquire.board.inspect;

import acquire.basic.Location;
import acquire.board.Board;
import acquire.board.inspect.attempt.AttemptAction;
import acquire.board.inspect.attempt.AttemptFounding;
import acquire.board.inspect.attempt.AttemptGrow;
import acquire.board.inspect.attempt.AttemptMerge;
import acquire.board.inspect.attempt.AttemptSingleton;
import acquire.board.validate.BoardActionValidator;
import acquire.exception.validate.NonEmptyLocationException;
import acquire.exception.validate.ValidationException;

public class BoardInspector {
  private static final AttemptAction[] ACTIONS = { new AttemptMerge(),
      new AttemptGrow(), new AttemptFounding(), new AttemptSingleton() };

  /**
   * Inspect the effect by placing a tile at the given location on the given
   * board
   * 
   * @param board to place the tile on
   * @param validator to check all kind of effects
   * @param location of the tile
   * @return the effect of the placement
   */
  public InspectResult inspect(Board board, BoardActionValidator validator,
      Location location) {
    for (AttemptAction action : ACTIONS) {
      try {
        return action.attempt(board, location, validator);
      } catch (NonEmptyLocationException ex) {
        return new ImposibleResult(ex.getMessage());
      } catch (ValidationException ex) {
        ex.equals(ex);
        // attempt next action
        continue;
      }
    }
    return new ImposibleResult("Can't do anything.");
  }
}
