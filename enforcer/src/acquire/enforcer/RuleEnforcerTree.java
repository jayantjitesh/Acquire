package acquire.enforcer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.enforcer.listener.StateChangeListener;
import acquire.exception.BadPlayerExecption;
import acquire.exception.DuplicateCallException;
import acquire.exception.NotValidAcquirerException;
import acquire.exception.NotValidMergerException;
import acquire.player.Player;
import acquire.protocol.ProxyRuleEnforcer;
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
public class RuleEnforcerTree implements ProxyRuleEnforcer {
  public static final int MAX_TURN_TIME = 1000;
  private final RuleEnforcerModelTree _model;
  private final Map<String, Player> _players;

  public RuleEnforcerTree(RuleEnforcerModelTree model,
      Map<String, Player> players) {
    _model = model;
    _players = players;
    _model.addStateChangeListener(new ActionOccurrecInformer());
  }

  Map<String, Player> getPlayers() {
    return _players;
  }

  RuleEnforcerModelTree getModel() {
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
    _model.startGame(new ArrayList<Player>(_players.values()));
    while (!_model.shouldStopGame()) {
      PlayerState currentPlayerState = _model.getCurrentPlayer();
      Player currentPlayer = _players.get(currentPlayerState.getName());
      try {
        _model.doPlayerTurn(currentPlayerState, currentPlayer, this);
      } catch (BadPlayerExecption ex) {
        _model.kickCurrentPlayer();
        _players.remove(currentPlayer.getName());
      }
    }
    return _model.signalEndgame();
  }

  @Override
  public List<PlayerState> place(Location loc, HotelName label)
      throws NotValidMergerException, NotValidAcquirerException,
      DuplicateCallException {
    return _model.keep(loc, label, _players);
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