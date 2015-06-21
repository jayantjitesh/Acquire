package acquire.protocol.description;

import acquire.basic.Action.ActionType;
import acquire.basic.Location;
import acquire.state.player.PlayerStateI;

public abstract class PlaceDescription extends ActionDescription {
  private final Location _location;

  public PlaceDescription(ActionType type, PlayerStateI player,
      Location location) {
    super(type, player);
    _location = location;
  }

  public Location getLocation() {
    return _location;
  }

}
