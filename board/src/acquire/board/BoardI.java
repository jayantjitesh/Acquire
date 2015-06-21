package acquire.board;

import java.util.Map;
import java.util.Set;
import acquire.basic.HotelChainI;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.board.inspect.BoardInspector;
import acquire.board.inspect.InspectResult;
import acquire.board.validate.BoardActionValidator;
import acquire.exception.InvalidSizeException;

public interface BoardI {
  public static final int ZERO = 0;

  /**
   * Check if a hotel has been founded on this board.
   * 
   * @param name Hotel to check for.
   * @return True if given hotel has not been founded.
   */
  boolean isHotelAvailable(HotelName name);

  /**
   * Retrieve set of unfounded hotels.
   * 
   * @return Names of hotels that are not founded on this board.
   */
  Set<HotelName> getAvailableHotelNames();

  /**
   * Retrieve description of every spot on this board at the time of the call.
   * 
   * @return Mapping from location to spot on the board.
   */
  Map<Location, Spot> getSpots();

  /**
   * Retrieve the description of the founded hotels.
   * 
   * @return Mapping of hotel names to description of hotel. Hotels that have
   *         not been founded are omitted.
   * @throws InvalidSizeException
   */
  Map<HotelName, HotelChainI> getHotelChains();

  /**
   * Check what will happen if a tile is placed on this board.
   * 
   * @param location Coordinates where the tile would be placed.
   * @return Description of what result would occur.
   */
  InspectResult inspect(Location location);

  /**
   * Return the size of HotelChain if it's founded else <code>ZERO</code>
   * 
   * @param name the HotelName
   * @return the size of the given HotelName
   */
  int getHotelChainSize(HotelName name);

  BoardInspector getInspector();

  BoardActionValidator getValidator();

}