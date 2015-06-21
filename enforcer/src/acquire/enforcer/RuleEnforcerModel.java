package acquire.enforcer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import acquire.basic.Action.ActionType;
import acquire.basic.HotelChainI;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.basic.StockDescription;
import acquire.board.inspect.InspectResult;
import acquire.enforcer.listener.StageChangeEventSource;
import acquire.enforcer.listener.StateChangeListener;
import acquire.exception.AcquireRuntimeException;
import acquire.exception.BadPlayerExecption;
import acquire.exception.InvalidSharePriceException;
import acquire.exception.InvalidStockCountException;
import acquire.exception.InvalidStockPurchaseException;
import acquire.exception.NoAvailableTileException;
import acquire.exception.validate.ValidationException;
import acquire.player.Player;
import acquire.protocol.description.ActionDescription;
import acquire.protocol.request.HotelRequest;
import acquire.protocol.request.PlaceRequest;
import acquire.protocol.request.PurchaseRequest;
import acquire.state.State;
import acquire.state.StateI;
import acquire.state.perspective.EndPerspective;
import acquire.state.perspective.EndPerspective.Cause;
import acquire.state.player.PlayerState;

/**
 * Implementation of control logic of game.
 */
public class RuleEnforcerModel {
  public static final int NO_MAX_TURN = -1;
  public static final int MAX_CHAIN_SIZE = 41;
  private static final Logger LOGGER = Logger
      .getLogger(RuleEnforcerModel.class);
  private final StageChangeEventSource _source;
  private final State _state;
  private final int _maxTurns;
  private int _turnsExecuted;

  public RuleEnforcerModel(State state, int maxTurns) {
    _state = state;
    _maxTurns = maxTurns;
    _turnsExecuted = 0;
    _source = new StageChangeEventSource();
  }

  StateI getState() {
    return _state;
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
    return _state.getPlayers();
  }

  /**
   * Retrieve the current player.
   * 
   * @return Description of the state of the player who's turn is occurring.
   */
  public PlayerState getCurrentPlayer() {
    return _state.getCurrentPlayer();
  }

  /**
   * Check if a player can make a valid move.
   * 
   * @param playerState Player to validate for possible moves.
   * @return True if there is at least one valid move.
   */
  public boolean canPlayerMakeMove(PlayerState playerState) {
    for (Location tile : playerState.getTilesInHand()) {
      InspectResult result = _state.getBoard().inspect(tile);
      if (result.isValid()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Have a player place a tile on the board.
   * 
   * @param playerState Description of the player to place a tile.
   * @param player Player implementation to query for placement request.
   * @throws BadPlayerExecption Player requests to make an invalid placement.
   */
  public void doPlayerMove(PlayerState playerState, Player player)
      throws BadPlayerExecption {
    // PlaceRequest moveRequest = player.getMoveRequest(new PlayerPerspective(
    // _state, playerState, _turnsExecuted));
    // try {
    // handleMove(moveRequest);
    // } catch (ValidationException ex) {
    // throw new BadPlayerExecption("Invalid placement", ex);
    // }
    // fireStateChangeEvent(new
    // ActionDescription(ActionType.fromValue(moveRequest
    // .getTag()), playerState));
  }

  /**
   * Process the placement request from a player.
   * 
   * @param request Request to process.
   * @throws ValidationException Request is an invalid placement.
   */
  public void handleMove(PlaceRequest request) throws ValidationException {
    switch (request.getTag()) {
      case SINGLETON:
      case GROW:
        _state.place(request.getLocation(), null);
        break;
      case MERGE:
      case FOUND:
        HotelRequest hotelRequest = (HotelRequest) request;
        _state.place(hotelRequest.getLocation(), hotelRequest.getName());
        break;
      default:
        AcquireRuntimeException.logAndThrow(LOGGER, "Unexpected request type: "
            + request.getTag());
        break;
    }
  }

  /**
   * Have a player purchase stocks for hotels.
   * 
   * @param playerState Description of the player to place a tile.
   * @param player Player implementation to query for placement request.
   * @throws BadPlayerExecption Player requests to make an invalid purchase.
   */
  public void doPlayerPurchase(PlayerState playerState, Player player)
      throws BadPlayerExecption {
    // PurchaseRequest request = player.getPurchaseRequest(new
    // PlayerPerspective(
    // _state, playerState, _turnsExecuted));
    // handlePurchase(request);
    // fireStateChangeEvent(new ActionDescription(ActionType.PURCHASE,
    // playerState));
  }

  /**
   * Process the purchase request from a player.
   * 
   * @param request Request to process.
   * @throws BadPlayerExecption Request is an invalid purchase.
   */
  public void handlePurchase(PurchaseRequest request) throws BadPlayerExecption {
    try {
      Map<HotelName, StockDescription> stocks = new HashMap<HotelName, StockDescription>();
      for (HotelName name : request.getNames()) {
        StockDescription description = stocks.get(name);
        if (description == null) {
          description = new StockDescription(name, 1);
        } else {
          description = description.changeCount(1);
        }
        stocks.put(name, description);
      }
      _state.purchaseStock(stocks.values());
    } catch (InvalidStockCountException ex) {
      AcquireRuntimeException.logAndReturn(LOGGER,
          "count should never get bigger than PurchaseRequest.MAX_SHARES", ex);
    } catch (InvalidStockPurchaseException ex) {
      throw new BadPlayerExecption(ex.getMessage(), ex);
    } catch (InvalidSharePriceException ex) {
      throw new BadPlayerExecption(
          "Attempted to purchase a hotel that does not exist.", ex);
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
    return doesPlayerCountPreventGame() || doHotelsCauseGameOver();
  }

  /**
   * Check if the state of the hotel chains cause the end of the game.
   * 
   * @return True if all chains are safe or a chain has reached the maximum
   *         size. False if there are no hotel chains.
   */
  public boolean doHotelsCauseGameOver() {
    Map<HotelName, HotelChainI> chains = _state.getBoard().getHotelChains();
    if (chains.isEmpty()) {
      return false;
    }
    boolean isSafe = true;
    for (HotelChainI chain : _state.getBoard().getHotelChains().values()) {
      if (chain.getSize() == MAX_CHAIN_SIZE) {
        return true;
      }
      isSafe = isSafe && chain.isSafe();
    }
    return isSafe;
  }

  /**
   * Check if the number of players prevents the game from continuing.
   * 
   * @return True if there are one or fewer players.
   */
  public boolean doesPlayerCountPreventGame() {
    return _state.getPlayers().size() <= 1;
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
   * Transition to the next player's turn.
   * 
   * @throws NoAvailableTileException There are not tiles to give the player who
   *           just completed their turn.
   */
  public void nextTurn() throws NoAvailableTileException {
    PlayerState currentPlayer = _state.getCurrentPlayer();
    _state.nextTurn();
    _turnsExecuted++;
    fireStateChangeEvent(new ActionDescription(ActionType.END_TURN,
        currentPlayer));
  }

  /**
   * Remove the current player from the game.<br />
   * Any shares or tiles held by the player are returned to the available set.
   */
  public void kickCurrentPlayer() {
    PlayerState currentPlayer = _state.getCurrentPlayer();
    _state.kickCurrentPlayer();
    fireStateChangeEvent(new ActionDescription(ActionType.KICK, currentPlayer));
  }

  public void startGame() {
    fireStateChangeEvent(new ActionDescription(ActionType.INITIAL,
        _state.getCurrentPlayer()));
  }

  /**
   * Retrieve the number of turns executed.
   * 
   * @return Count of turns the have completed successfully.
   */
  public int getTurnsExecuted() {
    return _turnsExecuted;
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
    if (_state.isAvailableTilesEmpty()) {
      cause = Cause.NO_MORE_TILES;
    } else if (!canPlayerMakeMove(getCurrentPlayer())) {
      cause = Cause.PLAYER_CANT_PLAY_TILE;
    } else if (doesPlayerCountPreventGame()) {
      cause = Cause.ONLY_PLAYER_LEFT;
    } else if (doHotelsCauseGameOver()) {
      cause = Cause.FINISHED;
    } else if (isMaxTurnsReached()) {
      cause = Cause.MAX_TURNS;
    } else {
      AcquireRuntimeException.logAndReturn(LOGGER,
          "Can't make an end perspective when there is no cause.", null);
    }
    if (cause == null) {
      AcquireRuntimeException.logAndReturn(LOGGER, "Cause is not being set.",
          null);
    }
    EndPerspective perspective = new EndPerspective(cause, _state,
        _turnsExecuted, _state.calculateFinalScore());
    _source.fireGameOverEvent(perspective);
    return perspective;
  }

  public void fireStateChangeEvent(ActionDescription description) {
    _source
        .fireStateChangeEvent(_state, description, _turnsExecuted, _maxTurns);
  }
}
