package acquire.board;

import java.awt.Color;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.exception.AcquireRuntimeException;

/**
 * Representation of a cell on a <code>Board</code>.
 */
public class Spot {
  public static final Color SINGELTON_COLOR = Color.GRAY;
  private final Location _location;
  private final boolean _empty;
  private final SpotType _type;

  /**
   * Constructs empty spot with type of NONE.
   * 
   * @param location Coordinates of spot.
   */
  public Spot(Location location) {
    this(location, true, SpotType.NONE);
  }

  /**
   * Constructs non-empty spot.
   * 
   * @param loction Coordinates of spot.
   * @param name Name of founded hotel owning spot.
   */
  private Spot(Location loction, SpotType name) {
    this(loction, false, name);
  }

  /**
   * Constructor
   * 
   * @param loction Coordinates of this spot.
   * @param empty True if this spot does not have a tile placed on it.
   * @param type Type of hotel owning spot.
   */
  private Spot(Location loction, boolean empty, SpotType type) {
    _location = loction;
    _empty = empty;
    _type = type;
  }

  /**
   * @return Location of this spot on the board.
   */
  public Location getLocation() {
    return _location;
  }

  /**
   * @return True if this spot does not have a tile on it.
   */
  public boolean isEmpty() {
    return _empty;
  }

  /**
   * @return True if this spot does have a tile on it and it is not part of a
   *         hotel.
   */
  public boolean isSingleton() {
    return !_empty && _type == SpotType.NONE;
  }

  /**
   * @return True if this spot has a tile on it that is part of a hotel.
   */
  public boolean hasHotel() {
    return _type != SpotType.NONE;
  }

  /**
   * @return Indication of the spot properties. <code>None</code> if there is no
   *         hotel on this spot.
   */
  public SpotType getSpotType() {
    return _type;
  }

  public Spot changeName(HotelName name) {
    return new Spot(_location, SpotType.valueFrom(name));
  }

  public Spot changeToSingleton() {
    return new Spot(_location, SpotType.NONE);
  }

  public String getLabel() {
    return _location.getLabel();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (_empty ? 1231 : 1237);
    result = prime * result + ((_location == null) ? 0 : _location.hashCode());
    result = prime * result + ((_type == null) ? 0 : _type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Spot other = (Spot) obj;
    if (_empty != other._empty) {
      return false;
    }
    if (_location == null) {
      if (other._location != null) {
        return false;
      }
    } else if (!_location.equals(other._location)) {
      return false;
    }
    if (_type == null) {
      if (other._type != null) {
        return false;
      }
    } else if (!_type.equals(other._type)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Spot [_location=" + _location + ", _empty=" + _empty + ", _type="
        + _type + "]";
  }

  /**
   * All the possible spot type
   */
  public static enum SpotType {
    NONE(Color.WHITE, "NONE"), AMERICAN(Color.RED, "American"), CONTINENTAL(
        Color.BLUE, "Continental"), FESTIVAL(Color.GREEN, "Festival"), IMPERIAL(
        Color.YELLOW, "Imperial"), SACKSON(Color.MAGENTA, "Sackson"), TOWER(
        new Color(165, 42, 42), "Tower"), WORLDWIDE(Color.ORANGE, "Worldwide");

    private final Color _color;
    private final String _name;

    private SpotType(Color color, String name) {
      _color = color;
      _name = name;
    }

    public Color getColor() {
      return _color;
    }

    @Override
    public String toString() {
      return _name;
    }

    /**
     * Converts the given String value to corresponding spot type if possible.
     * 
     * @param value the name in string
     * @return the corresponding Spot Type
     * @throws IllegalArgumentException if the value doesn't match to any Spot
     *           Type
     */
    public static SpotType valueFrom(String value) {
      for (SpotType type : values()) {
        if (type._name.equals(value)) {
          return type;
        }
      }
      throw new IllegalArgumentException(value
          + " does not map to a SpotType value.");
    }

    /**
     * Converts the given Hotel Name to corresponding Spot Type if possible.
     * 
     * @param name the given Hotel Name
     * @return the corresponding Spot Type
     */
    public static SpotType valueFrom(HotelName name) {
      switch (name) {
        case AMERICAN:
          return SpotType.AMERICAN;
        case CONTINENTAL:
          return SpotType.CONTINENTAL;
        case FESTIVAL:
          return SpotType.FESTIVAL;
        case IMPERIAL:
          return SpotType.IMPERIAL;
        case SACKSON:
          return SpotType.SACKSON;
        case TOWER:
          return SpotType.TOWER;
        case WORLDWIDE:
          return SpotType.WORLDWIDE;
        default:
          throw new AcquireRuntimeException(
              "Compiler doesn't know every enum case is checked.");
      }
    }
  }
}
