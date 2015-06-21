package acquire.exception;

import acquire.enforcer.RuleEnforcerBuilder;

public class TooFewPlayersException extends AcquireException {
  public TooFewPlayersException() {
    super("A game must have at least " + RuleEnforcerBuilder.MIN_PLAYERS
        + " players in a game.");
  }
}
