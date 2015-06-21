package acquire.player;

import java.util.List;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.protocol.TurnI;
import acquire.protocol.description.ActionDescription;
import acquire.protocol.description.ErrorDescription;
import acquire.protocol.request.Response;
import acquire.state.StateI;
import acquire.state.perspective.EndPerspective;

/**
 * The interface a user must implement in order to compete in the game.<br />
 * <br />
 * 
 * Every implementation must have a constructor with the following signature.<br />
 * <code>public CLASS_NAME(String name, Strategy strategy)</code><br />
 * name - Name the player should use when contacted by the RuleEnforcer.
 * strategy - Implementation of decision logic about placing tiles and buying
 * stocks.<br />
 * <br />
 * 
 * All of methods that return a request may block for a period of time while the
 * player decides what type of action to take. The restriction on how much time
 * is allowed is specified by <code>RuleEnforcer.MAX_TURN_TIME</code>. All other
 * methods should not block.
 */
public interface Player {

  /**
   * Retrieve player name.<br />
   * Should not be longer than <code>PlayerStateI.MAX_NAME_LENGTH</code>. Name
   * should be unique for the game.<br />
   * Called before a game starts.
   * 
   * @return Identifier of player.
   */
  String getName();

  /**
   * Set player name.<br />
   * 
   * @param name Should not be longer than
   *          <code>PlayerStateI.MAX_NAME_LENGTH</code>. Name should be unique
   *          for the game.<br />
   *          Called before a game starts.
   * 
   */
  void setName(String name);

  /**
   * Notify the Player about the start of game with the initial state
   * 
   * @param state the read object of the current state of the board and banker
   *          containing the name of the players
   */
  void startGame(StateI state);

  /**
   * asks this player to play his turn which consist of placing a Tile, may be
   * selecting a HotelName and may be buying at max
   * <code>PurchaseRequest.MAX_SHARES<code>
   * 
   * 
   * @param turn a proxy object with read access to game state and other
   *          player's state
   * @return a TurnResponse is a PlaceRequest, PurchaseRequest
   */
  Response takeTurn(TurnI turn);

  /**
   * Ask the player if they want to keep the shares of the given hotel names
   * 
   * @param labels A list of HotelName which are about to get merged in
   *          alphabetical order
   * @return the list of boolean as decision where the decisions are having the
   *         same index as the hotel name
   */
  List<Boolean> keep(List<HotelName> labels);

  /**
   * Indicate that the game has completed.
   * 
   * @param perspective State of the game at the end.
   */
  void gameOver(EndPerspective perspective);

  /**
   * Assigns a new tile to the player if possible
   * 
   * @param tile the new tile
   */
  void newTile(Location tile);

  /**
   * Indicate that a valid action has occurred.<br />
   * Called on every player in the game state has changed in some way. This
   * could be a player making a valid action, the end of a players turn, or the
   * end of the game.<br />
   * The <code>Player</code> implementation should forward this call to the
   * <code>Strategy</code> implementation.
   * 
   * @param perspective State of the game as seen by this player after the
   *          action is completed.
   * @param action Description of the action that occurred.
   */
  void actionOccured(StateI state, ActionDescription action);

  /**
   * Indicate that this player has been kicked out of the game. This player will
   * not be contacted after this point.
   * 
   * @param description Explanation of why this player was kicked from the game.
   */
  void kickedOut(ErrorDescription description);
}
