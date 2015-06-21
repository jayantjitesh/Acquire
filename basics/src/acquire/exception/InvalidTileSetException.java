package acquire.exception;

import java.util.Arrays;
import java.util.Set;
import acquire.basic.Location;

public class InvalidTileSetException extends ArgumentException {

  public InvalidTileSetException(Set<Location> tilesInHand) {
    super("Too many tiles: " + Arrays.toString(tilesInHand.toArray()));
  }

}
