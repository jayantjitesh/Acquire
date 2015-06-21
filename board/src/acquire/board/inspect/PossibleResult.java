package acquire.board.inspect;

import acquire.basic.Action.PlacementType;
import acquire.basic.Location;

/**
 * If the placement is possible
 */
public abstract class PossibleResult implements InspectResult {
  private final Location _location;

  public PossibleResult(Location location) {
    _location = location;
  }

  @Override
  public boolean isValid() {
    return true;
  }

  public Location getLocation() {
    return _location;
  }

  public abstract PlacementType getType();
}
