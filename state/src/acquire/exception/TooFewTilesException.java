package acquire.exception;

import acquire.state.player.PlayerStateI;

public class TooFewTilesException extends AcquireException {

  public TooFewTilesException(int size) {
    super(PlayerStateI.MAX_TILES + " tiles required, only recieved " + size);
  }

}
