package acquire.enforcer.tile;

import java.util.Set;
import acquire.basic.Location;
import acquire.util.RandomHelper;

/**
 * Selection model where tiles are selected randomly.
 */
public class RandomTileSelection extends TileSelectionModel {
  private final RandomHelper _random = new RandomHelper();

  @Override
  protected Location chooseTile(Set<Location> available) {
    return _random.randomElement(available);
  }

}
