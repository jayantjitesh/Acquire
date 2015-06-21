package acquire.enforcer.tile;

import java.util.Set;
import acquire.basic.Location;
import acquire.exception.NoAvailableTileException;

/**
 * Model defining how to decide what tiles to hand to a player.
 */
public abstract class TileSelectionModel {
  /**
   * Select a tile from the set of available tiles.<br />
   * The selected tile is removed from <code>available</code>.
   * 
   * @param available Non-empty set of available tiles.
   * @return A tile from the set.
   * @throws NoAvailableTileException <code>available</code> is empty.
   */
  public Location selectTile(Set<Location> available)
      throws NoAvailableTileException {
    if (available.isEmpty()) {
      throw new NoAvailableTileException();
    }
    Location tile = chooseTile(available);
    // available.remove(tile);
    return tile;
  }

  /**
   * Implementation of the selection logic.
   * 
   * @param available Non-empty set of tiles to choose from.
   * @return The chosen tile.
   */
  protected abstract Location chooseTile(Set<Location> available);
}
