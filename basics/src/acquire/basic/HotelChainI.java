package acquire.basic;

import java.awt.Color;
import java.util.Set;
import acquire.board.Spot.SpotType;
import acquire.exception.AcquireRuntimeException;

public interface HotelChainI extends Comparable<HotelChainI> {

  /**
   * Minimum number of spots a hotel chain can have.
   */
  public static final int MIN_SPOTS = 2;
  /**
   * Minimum number of spots a hotel chain can have and be safe.
   */
  public static final int SAFE_SPOTS = 12;

  public static final int MAX_CHAIN_SIZE = 40;

  /**
   * @return Name of this chain.
   */
  HotelName getName();

  /**
   * @return Number of spots owned by this chain.
   */
  int getSize();

  /**
   * @return Locations that this chain owns at the time of the call.
   */
  Set<Location> getLocations();

  /**
   * @return True if this chain owns at least <code>SAFE_SPOTS</code> locations.
   */
  boolean isSafe();

  /**
   * All the possible hotel chains
   */
  public static enum HotelName {
    AMERICAN(Color.RED, "American"), CONTINENTAL(Color.BLUE, "Continental"), FESTIVAL(
        Color.GREEN, "Festival"), IMPERIAL(Color.YELLOW, "Imperial"), SACKSON(
        Color.MAGENTA, "Sackson"), TOWER(new Color(165, 42, 42), "Tower"), WORLDWIDE(
        Color.ORANGE, "Worldwide");

    private final Color _color;
    private final String _name;

    private HotelName(Color color, String name) {
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
     * Converts the given String value to corresponding Hotel Name if possible.
     * 
     * @param value the name in string
     * @return the corresponding Hotel Name
     * @throws IllegalArgumentException if the value doesn't match to any Hotel
     *           Name
     */
    public static HotelName valueFrom(String value) {
      for (HotelName hotel : values()) {
        if (hotel._name.equals(value)) {
          return hotel;
        }
      }
      throw new IllegalArgumentException(value
          + " does not map to a HotelName value.");
    }

    /**
     * Converts the given Spot Type to corresponding Hotel Name if possible.
     * 
     * @param name the spot type
     * @return the corresponding Hotel Name
     * @throws AcquireRuntimeException <code>name</code> is NONE.
     */
    public static HotelName valueFrom(SpotType name) {
      switch (name) {
        case AMERICAN:
          return HotelName.AMERICAN;
        case CONTINENTAL:
          return HotelName.CONTINENTAL;
        case FESTIVAL:
          return HotelName.FESTIVAL;
        case IMPERIAL:
          return HotelName.IMPERIAL;
        case SACKSON:
          return HotelName.SACKSON;
        case TOWER:
          return HotelName.TOWER;
        case WORLDWIDE:
          return HotelName.WORLDWIDE;
        case NONE:
          AcquireRuntimeException.logAndThrow(HotelChain.LOGGER,
              "This should never be called with NONE.");
        default:
          throw new AcquireRuntimeException(
              "Compiler doesn't know every enum case is checked.");
      }
    }
  }
}