package acquire.protocol.description;

import java.util.ArrayList;
import java.util.List;
import acquire.basic.Action.ActionType;
import acquire.basic.HotelChainI.HotelName;
import acquire.state.player.PlayerStateI;

public class PurchaseDescription extends ActionDescription {
  private final List<HotelName> _names;

  public PurchaseDescription(PlayerStateI player, List<HotelName> names) {
    super(ActionType.PURCHASE, player);
    _names = names;
  }

  public List<HotelName> getNames() {
    return new ArrayList<HotelName>(_names);
  }

}
