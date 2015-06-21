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
 * A Turn is a new Turn(ProxyRuleEnforcer _ruleEnforcer, PlayerPerspective
 * _playerPerspective) Interp: _ruleEnforcer : A proxy object of the
 * RuleEnforcer _playerPerspective : A description of game state from
 * perspective of player
 * 
 */
public class Turn implements TurnI {
  private final ProxyRuleEnforcer _ruleEnforcer;
  private final PlayerPerspective _playerPerspective;

  public Turn(ProxyRuleEnforcer ruleEnforcer,
      PlayerPerspective playerPerspective) {
    _ruleEnforcer = ruleEnforcer;
    _playerPerspective = playerPerspective;
  }

  @Override
  public PlayerPerspective getPerspective() {

    return _playerPerspective;
  }

  @Override
  public List<PlayerState> place(Location loc, HotelName label)
      throws NotValidMergerException, NotValidAcquirerException,
      DuplicateCallException {
    return _ruleEnforcer.place(loc, label);
  }

}
