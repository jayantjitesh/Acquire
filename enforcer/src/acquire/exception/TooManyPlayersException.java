package acquire.exception;

import acquire.enforcer.RuleEnforcerBuilder;

public class TooManyPlayersException extends AcquireException {
  public TooManyPlayersException() {
    super("A game cannot have more than " + RuleEnforcerBuilder.MAX_PLAYERS
        + " players in a game.");
  }
}
