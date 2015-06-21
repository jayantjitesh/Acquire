package acquire.board.inspect;

import acquire.basic.Action.PlacementType;
import acquire.basic.Location;

/**
 * If the placement is causing no effect
 */
public class SingletonResult extends PossibleResult {

  public SingletonResult(Location location) {
    super(location);
  }

  @Override
  public PlacementType getType() {
    return PlacementType.SINGLETON;
  }
}
