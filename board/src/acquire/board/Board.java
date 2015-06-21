package acquire.board;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import acquire.basic.HotelChain;
import acquire.basic.HotelChainI;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.board.inspect.BoardInspector;
import acquire.board.inspect.InspectResult;
import acquire.board.validate.BoardActionValidator;
import acquire.exception.AcquireRuntimeException;
import acquire.exception.ChainNotFoundException;
import acquire.exception.DuplicateSpotException;
import acquire.exception.InvalidSizeException;
import acquire.exception.validate.ValidationException;

/**
 * Representation of the Acquire game board.
 */
public class Board implements BoardI {
  public static final int BOARD_SIZE = Row.values().length
      * Column.values().length;
  private static final Logger LOGGER = Logger.getLogger(Board.class);
  private final Map<Location, Spot> _spots;
  private final Map<HotelName, HotelChain> _hotels;
  private final BoardActionValidator _validator;
  private final BoardInspector _inspector;

  public Board() {
    this(new BoardActionValidator(), new BoardInspector());
  }

  Board(BoardActionValidator validator, BoardInspector inspector) {
    _spots = generateBoardMap();
    _hotels = new EnumMap<HotelName, HotelChain>(HotelName.class);
    _validator = validator;
    _inspector = inspector;
  }

  /**
   * Copy constructor.
   * 
   * @param board Current board state that should be copied to the new instance.
   * @throws InvalidSizeException
   */
  public Board(BoardI board) {
    _spots = board.getSpots();
    _hotels = new HashMap<HotelName, HotelChain>();
    for (HotelChainI chain : board.getHotelChains().values()) {
      try {
        _hotels.put(chain.getName(), new HotelChain(chain));
      } catch (InvalidSizeException e) {
        AcquireRuntimeException.logAndThrow(LOGGER,
            "This should never happen. " + "Validator checks this case.", e);
      }
    }

    _validator = board.getValidator();
    _inspector = board.getInspector();
  }

  /**
   * Constructor for directly injecting the data
   * 
   * @param hotels the hotels founded on the board
   * @param spots on the board
   */
  Board(Map<HotelName, HotelChain> hotels, Map<Location, Spot> spots) {
    _spots = spots;
    _hotels = hotels;
    _validator = new BoardActionValidator();
    _inspector = new BoardInspector();
  }

  @Override
  public BoardInspector getInspector() {
    return _inspector;
  }

  @Override
  public BoardActionValidator getValidator() {
    return _validator;
  }

  private void updateSpot(Location location, HotelName name) {
    Spot spot = getSpot(location);
    _spots.put(location, spot.changeName(name));
  }

  /**
   * Place a tile on the given location causing singleton
   * 
   * @param location to place the tile
   * @throws ValidationException if the placement can't be done
   */
  public void addSingleton(Location location) throws ValidationException {
    _validator.validateSingleton(this, location);
    _spots.put(location, getSpot(location).changeToSingleton());
  }

  /**
   * Add a new hotel chain to the board
   * 
   * @param name the name of the Hotel Chain
   * @param additional the location of the tile going to placed
   * @throws ValidationException if the placement can't be done
   */
  public void addHotel(HotelName name, Location additional)
      throws ValidationException {

    Set<Location> locations = _validator.validateAddHotel(this, additional,
        name);
    // make chain
    locations.add(additional);
    try {
      _hotels.put(name, new HotelChain(name, locations));
    } catch (InvalidSizeException ex) {
      throw new ValidationException(ex.getMessage(), ex);
    }
    for (Location location : locations) {
      updateSpot(location, name);
    }
  }

  /**
   * Add a hotel chain to the board
   * 
   * @param hotelChain the given the Hotel Chain
   * @throws ValidationException if the placement can't be done
   * @throws InvalidSizeException if the minimum locations requirement to form a
   *           Hotel Chain is not met
   */
  public void addHotel(HotelChain hotelChain) throws ValidationException {
    HotelName name = hotelChain.getName();
    Set<Location> locations = hotelChain.getLocations();
    _validator.ValidateAddHotel(this, locations, name);

    _hotels.put(hotelChain.getName(), hotelChain);
    for (Location location : locations) {
      updateSpot(location, name);
    }
  }

  /**
   * Add a given location to already founded Hotel Chain
   * 
   * @param location the location to add
   * @throws ValidationException if the placement can't be done
   * @throws InvalidSizeException
   */
  public void growHotel(Location location) throws ValidationException {
    HotelName name = _validator.validateGrowHotel(this, location);

    HotelChain chain = _hotels.get(name);
    checkAddSpot(chain, location);
    updateSpot(location, name);
  }

  /**
   * checks the invariant and precondition before changing HotelChain size
   * 
   * @param chain The HotelChain to which Spot is to be added
   * @param location The Spot to be added
   */
  private void checkAddSpot(HotelChain chain, Location location) {
    try {
      chain.addSpot(location);
    } catch (DuplicateSpotException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER, "This should never happen. "
          + "Validator checks this case.", ex);
    } catch (InvalidSizeException e) {
      AcquireRuntimeException.logAndThrow(LOGGER, "This should never happen. "
          + "Validator checks this case.", e);
    }

  }

  /**
   * Merges two or more hotel chain to the given Hotel Name
   * 
   * @param location causing the merging
   * @param name The name of the acquirer hotel chain
   * @throws ValidationException If the merging is not possible
   * @throws InvalidSizeException
   */
  public void mergeHotel(Location location, HotelName name)
      throws ValidationException {
    Set<HotelName> hotels = _validator.validateMergingHotels(this, location,
        name);
    hotels.remove(name);

    HotelChain acquirer = _hotels.get(name);
    checkAddSpot(acquirer, location);
    updateSpot(location, name);
    for (HotelName aquiredName : hotels) {
      HotelChainI acquired = _hotels.get(aquiredName);
      for (Location acquiredLocation : acquired.getLocations()) {
        checkAddSpot(acquirer, acquiredLocation);
        updateSpot(acquiredLocation, name);
      }
      _hotels.remove(aquiredName);
    }
  }

  @Override
  public boolean isHotelAvailable(HotelName name) {
    return _hotels.get(name) == null;
  }

  public HotelChain getHotelChain(HotelName name) throws ChainNotFoundException {
    if (!isHotelAvailable(name)) {
      return _hotels.get(name);
    }
    throw new ChainNotFoundException(name);
  }

  @Override
  public int getHotelChainSize(HotelName name) {
    if (!isHotelAvailable(name)) {
      return _hotels.get(name).getSize();
    }
    return ZERO;
  }

  public Spot getSpot(Location location) {
    return _spots.get(location);
  }

  @Override
  public Map<Location, Spot> getSpots() {
    return new HashMap<Location, Spot>(_spots);
  }

  /**
   * Creates a empty board.
   * 
   * @return the map of all the location on the board to the corresponding Sopt
   */
  public static Map<Location, Spot> generateBoardMap() {
    Map<Location, Spot> board = new HashMap<Location, Spot>();
    for (Row row : Row.values()) {
      for (Column column : Column.values()) {
        Location location = new Location(row, column);
        board.put(location, new Spot(location));
      }
    }
    return board;
  }

  /**
   * 
   * @return the count of available Hotel chains
   */
  public int getAvailableHotelCount() {
    return HotelName.values().length - _hotels.size();
  }

  @Override
  public Set<HotelName> getAvailableHotelNames() {
    Set<HotelName> available = new HashSet<HotelName>();
    for (HotelName name : HotelName.values()) {
      if (isHotelAvailable(name)) {
        available.add(name);
      }
    }
    return available;
  }

  @Override
  public Map<HotelName, HotelChainI> getHotelChains() {
    Map<HotelName, HotelChainI> hotels = new HashMap<HotelName, HotelChainI>();
    for (HotelChain chain : _hotels.values()) {
      try {
        hotels.put(chain.getName(), new HotelChain(chain));
      } catch (InvalidSizeException e) {
        AcquireRuntimeException.logAndThrow(LOGGER,
            "This should never happen. " + "Validator checks this case.", e);
      }
    }
    return hotels;
  }

  @Override
  public InspectResult inspect(Location location) {
    return _inspector.inspect(this, _validator, location);
  }

}
