package acquire.game.tree.exception;

import acquire.exception.AcquireException;

public class NotFinalStateException extends AcquireException {

  public NotFinalStateException() {
    super("Not a final state");

  }

}
