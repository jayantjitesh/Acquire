package acquire.state.player;

import java.util.Set;
import acquire.basic.Location;

public interface MyPlayerState extends PlayerStateI {

  /**
   * @return Locations this player can place tiles at the time of the call.
   */
  Set<Location> getTilesInHand();
}
