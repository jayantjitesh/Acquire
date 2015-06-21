package acquire.state.perspective;

import java.util.List;
import java.util.Set;
import acquire.basic.Location;
import acquire.board.BoardI;
import acquire.state.State;
import acquire.state.money.BankerI;
import acquire.state.player.MyPlayerState;
import acquire.state.player.PlayerState;

/**
 * Game state description passed to a player.
 */
public class PlayerPerspective extends Perspective {
  private final MyPlayerState _myself;

  /**
   * Constructor
   * 
   * @param gameState Current state of the actual game.
   * @param myself Player who's perspective this is for.
   * @param turnsExecuted Non-negative number of turns that have occurred since
   *          the start of the game.
   */
  public PlayerPerspective(State gameState, MyPlayerState myself,
      int turnsExecuted) {
    super(gameState, turnsExecuted);
    _myself = myself;
  }

  /**
   * Constructor
   * 
   * @param players All of the game players. Order is based on turn order, with
   *          the current player as the first element.
   * @param myself Player who's perspective this is for.
   * @param board Description of the tiles and hotels in play.
   * @param banker Description of available stocks and share prices.
   * @param turnsExecuted Non-negative number of turns that have occurred since
   *          the start of the game.
   */
  PlayerPerspective(List<PlayerState> players, MyPlayerState myself,
      BoardI board, BankerI banker, int turnsExecuted,
      Set<Location> availableTiles) {
    super(players, board, banker, turnsExecuted, availableTiles);
    _myself = myself;
  }

  /**
   * @return Description of the player who is given this state instance.
   */
  public MyPlayerState getMyself() {
    return _myself;
  }
}
