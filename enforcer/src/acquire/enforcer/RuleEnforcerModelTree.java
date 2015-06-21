package acquire.enforcer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import acquire.basic.Action.ActionType;
import acquire.basic.Action.PlacementType;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.board.inspect.InspectResult;
import acquire.board.inspect.MergeResult;
import acquire.board.inspect.PossibleResult;
import acquire.enforcer.listener.StageChangeEventSource;
import acquire.enforcer.listener.StateChangeListener;
import acquire.exception.AcquireException;
import acquire.exception.AcquireRuntimeException;
import acquire.exception.BadPlayerExecption;
import acquire.exception.DuplicateCallException;
import acquire.exception.NoAvailableTileException;
import acquire.exception.NotValidAcquirerException;
import acquire.exception.NotValidMergerException;
import acquire.exception.validate.ValidationException;
import acquire.game.tree.AdminTree;
import acquire.game.tree.Edge;
import acquire.game.tree.TreeGenerator;
import acquire.game.tree.exception.FinalStateException;
import acquire.game.tree.exception.InvalidEdgeException;
import acquire.game.tree.exception.NotFinalStateException;
import acquire.player.Player;
import acquire.protocol.Turn;
import acquire.protocol.description.ActionDescription;
import acquire.protocol.request.HotelRequest;
import acquire.protocol.request.PlaceRequest;
import acquire.protocol.request.TurnResponse;
import acquire.state.State;
import acquire.state.perspective.EndPerspective;
import acquire.state.perspective.EndPerspective.Cause;
import acquire.state.perspective.PlayerPerspective;
import acquire.state.player.PlayerState;

/**
 * Implementation of control logic of game.
 */
public class RuleEnforcerModelTree {
  public static final int NO_MAX_TURN = -1;
  private static final Logger LOGGER = Logger
      .getLogger(RuleEnforcerModelTree.class);
  private final StageChangeEventSource _source;
  private final int _maxTurns;
  private int _turnsExecuted;
  private AdminTree _tree;
  private AdminTree _treeAfterSell = null;

  public RuleEnforcerModelTree(State state, int maxTurns) {
    _tree = TreeGenerator.generateAdminTree(state);
    _maxTurns = maxTurns;
    _turnsExecuted = 0;
    _source = new StageChangeEventSource();
  }

  int getMaxTurns() {
    return _maxTurns;
  }

  /**
   * Subscribe a listener for state change events.
   * 
   * @param listener Listener that will receive events after this call
   *          completes.
   */
  public void addStateChangeListener(StateChangeListener listener) {
    _source.addListener(listener);
  }

  /**
   * Un-subscribe a listener for state change events.
   * 
   * @param listener Listener that will no longer receive events after this call
   *          completes.
   */
  public void removeStateChangeListener(StateChangeListener listener) {
    _source.removeListener(listener);
  }

  /**
   * Retrieve state of all players in the game.
   * 
   * @return Non-empty list of player states used by the game.
   */
  public List<PlayerState> getPlayers() {
    return _tree.getNodeValue().getPlayers();
  }

  /**
   * Retrieve the current player.
   * 
   * @return Description of the state of the player who's turn is occurring.
   */
  public PlayerState getCurrentPlayer() {
    return _tree.getNodeValue().getCurrentPlayer();
  }

  /**
   * Remove the current player from the game.<br />
   * Any shares or tiles held by the player are returned to the available set.
   */
  public void kickCurrentPlayer() {
    PlayerState currentPlayer = _tree.getNodeValue().getCurrentPlayer();
    _tree.getNodeValue().kickCurrentPlayer();
    _tree = TreeGenerator.generateAdminTree(_tree.getNodeValue());
    fireStateChangeEvent(new ActionDescription(ActionType.KICK, currentPlayer));
  }

  /**
   * Process the placement request from a player.
   * 
   * @param request Request to process.
   * @throws ValidationException Request is an invalid placement.
   */
  public void handleMove(PlaceRequest request, State state)
      throws ValidationException {
    switch (request.getTag()) {
      case SINGLETON:
      case GROW:
        state.place(request.getLocation(), null);
        break;
      case MERGE:
      case FOUND:
        HotelRequest hotelRequest = (HotelRequest) request;
        state.place(hotelRequest.getLocation(), hotelRequest.getName());
        break;
      default:
        AcquireRuntimeException.logAndThrow(LOGGER, "Unexpected request type: "
            + request.getTag());
        break;
    }
  }

  /**
   * Check if the game should not proceed further.
   * 
   * @return True if the maximum number of turns has occurred or the game has
   *         ended.
   */
  public boolean shouldStopGame() {
    return isMaxTurnsReached() || isGameOver();
  }

  /**
   * Check if the maximum number of turns has occurred .
   * 
   * @return True if the maximum number of turns is set and has occurred.
   */
  public boolean isMaxTurnsReached() {
    return (isMaxTurnsSet() && _turnsExecuted >= _maxTurns);
  }

  /**
   * Check if the game has ended.
   * 
   * @return True if all chains are safe, a chain has reached the maximum size
   *         or there is only one player left.
   */
  public boolean isGameOver() {
    return _tree.isFinalState();
  }

  /**
   * Check if there is a limit on the number of turns.
   * 
   * @return True if there is a specified limit on the number of turns.
   */
  public boolean isMaxTurnsSet() {
    return _maxTurns != NO_MAX_TURN;
  }

  /**
   * Notifies all the Player's who signed up about the start of game by passing
   * the Initial state (IState) of the game.
   * 
   * @param players List of all Player's joining the Game
   */
  public void startGame(List<Player> players) {
    for (Player player : players) {
      player.startGame(_tree.getNodeValue());
    }
    // fireStateChangeEvent(new ActionDescription(ActionType.INITIAL, _tree
    // .getNodeValue().getCurrentPlayer()));
  }

  /**
   * Create a description of a game that has ended and sends it to any
   * listeners.<br />
   * This should not be called if the game has not ended yet.
   * 
   * @return The description of the end game.
   */
  public EndPerspective signalEndgame() {
    Cause cause = null;
    try {
      cause = _tree.getReasonForFinalState();

    } catch (NotFinalStateException ex) {
      if (isMaxTurnsReached()) {
        cause = Cause.MAX_TURNS;
      }
    }

    if (cause == null) {
      AcquireRuntimeException.logAndReturn(LOGGER, "Cause is not being set.",
          null);
    }
    EndPerspective perspective = new EndPerspective(cause,
        _tree.getNodeValue(), _turnsExecuted, _tree.getNodeValue()
            .calculateFinalScore());
    _source.fireGameOverEvent(perspective);
    return perspective;
  }

  /**
   * fire the state change to all the subscribers
   * 
   * @param description The description of the previous action resulting in the
   *          State change
   */
  public void fireStateChangeEvent(ActionDescription description) {
    _source.fireStateChangeEvent(_tree.getNodeValue(), description,
        _turnsExecuted, _maxTurns);
  }

  /**
   * Execute the turn of the current Player
   * 
   * @param playerState the state of the current player
   * @param player the current player
   * @throws BadPlayerExecption if the player makes a invalid move
   */
  public void doPlayerTurn(PlayerState playerState, Player player,
      RuleEnforcerTree ruleEnforcerTree) throws BadPlayerExecption {

    _treeAfterSell = null;
    TurnResponse response = null;
    try {
      response = player.takeTurn(new Turn(ruleEnforcerTree,
          new PlayerPerspective(_tree.getNodeValue(), playerState,
              _turnsExecuted)));
    } catch (Exception e) {
      throw new BadPlayerExecption("Invalid turn respose",
          new AcquireException(e.getLocalizedMessage()));
    }
    Location nextTile;
    try {
      // The new tile
      if (_treeAfterSell != null) {
        if (!response.getPlaceRequest().getTag().equals(PlacementType.MERGE))
          throw new BadPlayerExecption("It should be a merge request", null);
        else {
          _tree = _treeAfterSell;
        }

      }
      nextTile = _tree.getNodeValue().getNextTile();
      Edge e = new Edge(nextTile, response.getPurchaseRequest(),
          response.getPlaceRequest());
      _tree = _tree.next(e);
      _turnsExecuted++;
      endTurn(player, e.getTileToDistribute());

    } catch (NoAvailableTileException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "This can't happen since the game will be over if there is no tiles left "
              + ex.getLocalizedMessage());
    } catch (InvalidEdgeException ex) {
      throw new BadPlayerExecption("The turn was not valid", ex);
    } catch (FinalStateException ex) {
      AcquireRuntimeException.logAndThrow(
          LOGGER,
          "This can't happen since the final state is already checked "
              + ex.getLocalizedMessage());
    }

  }

  /**
   * Signal all the player with the new state at the end of each turn and assign
   * a new tile to the current player
   * 
   * @param players the list of player
   */
  private void endTurn(Player currentPlayer, Location tile) {
    currentPlayer.newTile(tile);
    fireStateChangeEvent(new ActionDescription(ActionType.END_TURN, _tree
        .getNodeValue().getCurrentPlayer()));
  }

  /**
   * Checks if the given location is causing merger and the acquirer name is
   * valid then ask all the players if they want to keep the shares of the
   * acquired hotels and if they decide to sell then sells the share
   * 
   * @param loc The location causing the merger
   * @param label the acquirer hotel
   * @param players the map of all the players with their name as key
   * @return list of updated player states
   * @throws NotValidMergerException If the given location is not causing merger
   * @throws NotValidAcquirerException if the hotel name given is not the valid
   *           acquirer
   * @throws DuplicateCallException if this call has been already mad during
   *           this turn
   */
  public List<PlayerState> keep(Location loc, HotelName label,
      Map<String, Player> players) throws NotValidMergerException,
      NotValidAcquirerException, DuplicateCallException {

    if (_treeAfterSell != null)
      throw new DuplicateCallException(
          "This method is already called by the player in this turn");

    InspectResult inspect = _tree.getNodeValue().getBoard().inspect(loc);

    if (!inspect.isValid())
      throw new NotValidMergerException(loc);

    PossibleResult result = (PossibleResult) inspect;
    if (!result.getType().equals(PlacementType.MERGE))
      throw new NotValidMergerException(loc);
    MergeResult mergeResult = (MergeResult) result;
    if (!mergeResult.getLargest().contains(label))
      throw new NotValidAcquirerException(label, loc);
    List<HotelName> acquired = new ArrayList<HotelName>(mergeResult.getHotels());
    acquired.remove(label);
    _treeAfterSell = new AdminTree(_tree);
    return _treeAfterSell.askPlayerToKeepShare(acquired, players);

  }
}
