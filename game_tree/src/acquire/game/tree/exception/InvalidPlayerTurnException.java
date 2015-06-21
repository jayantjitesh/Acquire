package acquire.game.tree.exception;

import acquire.exception.AcquireException;

public class InvalidPlayerTurnException extends AcquireException {

  public InvalidPlayerTurnException(String playerName) {
    super(playerName + " is not the current player");
  }

}
