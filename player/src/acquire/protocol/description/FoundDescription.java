package acquire.protocol.description;

import acquire.basic.Action.ActionType;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.state.player.PlayerStateI;

public class FoundDescription extends PlaceDescription {
  private final HotelName _name;

  public FoundDescription(PlayerStateI player, Location location, HotelName name) {
    super(ActionType.SINGLETON, player, location);
    _name = name;
  }

  public HotelName getFoundedHotel() {
    return _name;
  }
}
