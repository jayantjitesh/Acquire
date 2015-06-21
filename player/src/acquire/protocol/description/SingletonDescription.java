package acquire.protocol.description;

import acquire.basic.Action.ActionType;
import acquire.basic.Location;
import acquire.state.player.PlayerStateI;

public class SingletonDescription extends PlaceDescription {

  public SingletonDescription(PlayerStateI player, Location location) {
    super(ActionType.SINGLETON, player, location);
  }

}
