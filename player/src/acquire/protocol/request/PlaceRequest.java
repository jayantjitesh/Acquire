package acquire.protocol.request;

import acquire.basic.Action.PlacementType;
import acquire.basic.Location;

/**
 * Request to place a tile on a board.
 */
public abstract class PlaceRequest {
  private final Location _location;

  public PlaceRequest(Location location) {
    _location = location;
  }

  public Location getLocation() {
    return _location;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_location == null) ? 0 : _location.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PlaceRequest other = (PlaceRequest) obj;
    if (_location == null) {
      if (other._location != null)
        return false;
    } else if (!_location.equals(other._location))
      return false;
    return true;
  }

  public abstract PlacementType getTag();
}
