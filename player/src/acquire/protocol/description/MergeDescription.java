package acquire.protocol.description;

import java.util.Set;
import acquire.basic.Action.ActionType;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.exception.InvalidActionDescriptionException;
import acquire.state.player.PlayerStateI;

public class MergeDescription extends PlaceDescription {
  private final HotelName _acquirer;
  private final Set<HotelName> _acquired;

  public MergeDescription(PlayerStateI player, Location location,
      HotelName acquirer, Set<HotelName> acquired)
      throws InvalidActionDescriptionException {
    super(ActionType.SINGLETON, player, location);
    if (acquired.isEmpty()) {
      throw new InvalidActionDescriptionException(
          "At least one hotel must be acquired.");
    }
    if (acquired.contains(acquirer)) {
      throw new InvalidActionDescriptionException(
          "Acquirer can not be listed as acquired.");
    }

    _acquirer = acquirer;
    _acquired = acquired;
  }

  public HotelName getAcquirer() {
    return _acquirer;
  }

  public Set<HotelName> getAcquired() {
    return _acquired;
  }
}
