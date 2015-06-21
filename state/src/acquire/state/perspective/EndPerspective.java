package acquire.state.perspective;

import java.util.List;
import java.util.Map;
import java.util.Set;
import acquire.basic.Location;
import acquire.board.BoardI;
import acquire.state.State;
import acquire.state.money.BankerI;
import acquire.state.player.PlayerState;

public class EndPerspective extends Perspective {
  private final Cause _cause;
  private final Map<String, Integer> _finalScore;

  public EndPerspective(Cause cause, State gameState, int turnsExecuted,
      Map<String, Integer> finalScore) {
    super(gameState, turnsExecuted);
    _cause = cause;
    _finalScore = finalScore;
  }

  public EndPerspective(Cause cause, List<PlayerState> players, BoardI board,
      BankerI banker, int turnsExecuted, Set<Location> availableTiles,
      Map<String, Integer> finalScore) {
    super(players, board, banker, turnsExecuted, availableTiles);
    _cause = cause;
    _finalScore = finalScore;
  }

  /**
   * Return the final score. The final score is represented as a map of the
   * player name and their respective score
   * 
   * @return the final score
   */
  public Map<String, Integer> getFinalScore() {
    return _finalScore;
  }

  /**
   * @return Indication of why game ended.
   */
  public Cause getCause() {
    return _cause;
  }

  /**
   * @return Full description of player states.
   */
  public List<PlayerState> getAllPlayerStates() {
    return _players;
  }

  /**
   * @return Textual description of state with board and each player's tiles
   */
  @Override
  public String getDisplayText() {
    return getDisplayText(true, true);
  }

  @Override
  public String getDisplayText(boolean showBoard, boolean showTiles) {
    StringBuilder builder = new StringBuilder();
    builder.append("The game ended because ");
    builder.append(_cause.getDescription());
    if (_cause == Cause.PLAYER_CANT_PLAY_TILE) {
      builder.append(" (");
      builder.append(getCurrentPlayer().getName());
      builder.append(")");
    }
    builder.append("\n");
    builder.append(super.getDisplayText(showBoard, showTiles));
    return builder.toString();
  }

  public enum Cause {
    /**
     * Limit on number of turns was reached.
     */
    MAX_TURNS("Limit on number of turns was reached."),
    /**
     * RuleEnforcer ran out or tiles.
     */
    NO_MORE_TILES("RuleEnforcer ran out or tiles."),
    /**
     * A player had no valid moves.
     */
    PLAYER_CANT_PLAY_TILE("A player had no valid moves."),
    /**
     * All hotels are safe or a hotel has reached the maximum size.
     */
    FINISHED("All hotels are safe or a hotel has reached the maximum size."),
    /**
     * All other players have made illegal requests and have been kicked from
     * the game.
     */
    ONLY_PLAYER_LEFT(
        "All other players have made illegal requests and have been kicked from the game.");

    private final String _description;

    private Cause(String description) {
      _description = description;
    }

    public String getDescription() {
      return _description;
    }
  }
}
