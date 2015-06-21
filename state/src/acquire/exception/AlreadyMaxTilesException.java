package acquire.exception;

import acquire.state.player.PlayerStateI;

public class AlreadyMaxTilesException extends AcquireException {

  public AlreadyMaxTilesException(String name) {
    super("Player " + name + " already has " + PlayerStateI.MAX_TILES
        + " tiles.");
  }

}
