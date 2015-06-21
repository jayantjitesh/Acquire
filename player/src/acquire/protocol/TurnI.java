package acquire.protocol;

import java.util.List;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.exception.DuplicateCallException;
import acquire.exception.NotValidAcquirerException;
import acquire.exception.NotValidMergerException;
import acquire.state.perspective.PlayerPerspective;
import acquire.state.player.PlayerState;

/**
 * 
 * The information passed to player in each turn containing the information
 * about the current state of the game. It contains a method to call the proxy
 * object of rule enforcer for selling the acquired shares in the case of
 * merger.
 * 
 */

public interface TurnI {

  /**
   * Return the State of the game as seen by this player. including the states
   * of other player
   * 
   * @return State of the game as seen by this player.
   */
  PlayerPerspective getPerspective();

  /**
   * In the case of merger, this method will be used to call place method.
   * Return the current state of player keeping the shares of the acquired hotel
   * 
   * @param loc tile to place
   * @param label the acquirer hotel name
   * @return Return the current state of player keeping the shares of the
   *         acquired hotel
   * @throws NotValidAcquirerException
   * @throws NotValidMergerException
   * @throws DuplicateCallException
   */
  List<PlayerState> place(Location loc, HotelName label)
      throws NotValidMergerException, NotValidAcquirerException,
      DuplicateCallException;

}
