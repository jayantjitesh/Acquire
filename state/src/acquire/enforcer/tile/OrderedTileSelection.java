package acquire.enforcer.tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import acquire.basic.Location;

/**
 * Selection model where tiles are selected in order.
 */
public class OrderedTileSelection extends TileSelectionModel {

  @Override
  protected Location chooseTile(Set<Location> available) {
    List<Location> orderedTiles = new ArrayList<Location>(available);
    Collections.sort(orderedTiles);
    return orderedTiles.get(0);
  }

}
