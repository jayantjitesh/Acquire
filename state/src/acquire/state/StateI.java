package acquire.state;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import acquire.basic.Location;
import acquire.board.BoardI;
import acquire.state.money.BankerI;
import acquire.state.player.PlayerState;

public interface StateI {

  /**
   * 
   * @return the set of available tiles
   */
  Set<Location> getAvailableTiles();

  /**
   * 
   * @return The read object of the Board
   */

  BoardI getBoard();

  /**
   * 
   * @return playerNames Players who are playing the game. The order is in turn
   *         order, with the first player listed first.
   */
  Queue<String> getPlayersName();

  /**
   * 
   * @return the read object of the Banker
   */
  BankerI getBanker();

  /**
   * 
   * @return the state of all the players
   */
  List<PlayerState> getPlayers();

}