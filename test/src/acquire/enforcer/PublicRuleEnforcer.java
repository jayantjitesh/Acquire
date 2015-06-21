package acquire.enforcer;

import java.util.Map;
import acquire.exception.BadPlayerExecption;
import acquire.player.Player;
import acquire.state.player.PlayerState;

public class PublicRuleEnforcer extends RuleEnforcer {

  public PublicRuleEnforcer(RuleEnforcerModel model, Map<String, Player> players) {
    super(model, players);
    // TODO Auto-generated constructor stub
  }

  @Override
  public boolean doPlayerTurn(PlayerState currentPlayerState)
      throws BadPlayerExecption {
    return super.doPlayerTurn(currentPlayerState);
  }
}
