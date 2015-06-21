package acquire.enforcer;

import java.util.Map;
import acquire.enforcer.listener.StateChangeListener;
import acquire.exception.BadPlayerExecption;
import acquire.exception.NoAvailableTileException;
import acquire.player.Player;
import acquire.protocol.description.ActionDescription;
import acquire.state.State;
import acquire.state.perspective.EndPerspective;
import acquire.state.perspective.EndPlayerPerspective;
import acquire.state.player.PlayerState;

/**
 * Game manger.<br />
 * Controls the running of a game and ensures that each player follows the
 * rules.
 */
public class RuleEnforcer {
  public static final int MAX_TURN_TIME = 1000;
  private final RuleEnforcerModel _model;
  private final Map<String, Player> _players;

  public RuleEnforcer(RuleEnforcerModel model, Map<String, Player> players) {
    _model = model;
    _players = players;
    _model.addStateChangeListener(new ActionOccurrecInformer());
  }

  Map<String, Player> getPlayers() {
    return _players;
  }

  RuleEnforcerModel getModel() {
    return _model;
  }

  public void addStateChangeListener(StateChangeListener listener) {
    _model.addStateChangeListener(listener);
  }

  public void removeStateChangeListener(StateChangeListener listener) {
    _model.removeStateChangeListener(listener);
  }

  /**
   * Execute the game.<br />
   * Blocks until the game ends for some reason.
   * 
   * @return State of the game when it ended.
   */
  public EndPerspective runGame() {
    _model.startGame();
    while (!_model.shouldStopGame()) {
      PlayerState currentPlayer = _model.getCurrentPlayer();
      try {
        if (!doPlayerTurn(currentPlayer)) {
          break;
        }
      } catch (BadPlayerExecption ex) {
        _model.kickCurrentPlayer();
        _players.remove(currentPlayer.getName());
      }
    }

    return _model.signalEndgame();
  }

  /**
   * Execute single turn for a player.
   * 
   * @param currentPlayerState Player turn should be for.
   * @return True if the player could not move or there are no available tiles
   *         to give player.
   * @throws BadPlayerExecption Player makes an illegal request.
   */
  boolean doPlayerTurn(PlayerState currentPlayerState)
      throws BadPlayerExecption {
    if (!_model.canPlayerMakeMove(currentPlayerState)) {
      return false;
    }

    Player currentPlayer = _players.get(currentPlayerState.getName());
    _model.doPlayerMove(currentPlayerState, currentPlayer);

    _model.doPlayerPurchase(currentPlayerState, currentPlayer);

    try {
      _model.nextTurn();
    } catch (NoAvailableTileException ex) {
      return false;
    }
    return true;
  }

  public class ActionOccurrecInformer implements StateChangeListener {

    @Override
    public void stateChanged(State state, ActionDescription description,
        int turnsExecuted, int maxTurns) {
      for (PlayerState playerState : _model.getPlayers()) {
        Player player = _players.get(playerState.getName());
        player.actionOccured(state, description);
      }
    }

    @Override
    public void gameOver(EndPerspective perspective) {
      for (PlayerState playerState : _model.getPlayers()) {
        Player player = _players.get(playerState.getName());
        player.gameOver(new EndPlayerPerspective(perspective, playerState));
      }
    }
  }
}