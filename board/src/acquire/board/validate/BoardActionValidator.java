package acquire.board.validate;

import java.util.HashSet;
import java.util.Set;
import acquire.basic.HotelChainI;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.board.Board;
import acquire.exception.ChainNotFoundException;
import acquire.exception.validate.CannotAddHotelException;
import acquire.exception.validate.ChainAlreadyExistsExcpetion;
import acquire.exception.validate.HotelAcquirerNotFoundException;
import acquire.exception.validate.HotelAcquirerNotLargestException;
import acquire.exception.validate.NonEmptyLocationException;
import acquire.exception.validate.NotTouchingHotelException;
import acquire.exception.validate.ValidationException;

public class BoardActionValidator {
  public static final int MIN_HOTELS_MERGE = 2;

  /**
   * Check if a tile can be placed as a singleton.
   * 
   * @param board State of current board.
   * @param location Where to place tile.
   * @throws ValidationException Singleton is not allowed.
   */
  public void validateSingleton(Board board, Location location)
      throws ValidationException {
    checkSpotEmpty(board, location);
    validateNeighbors(board, location.getNeighbors(), new SingletonDelegate());
  }

  /**
   * Check if a tile can be placed to found a hotel.
   * 
   * @param board State of current board.
   * @param location Where to place tile.
   * @param name Hotel to found.
   * @return Singleton neighbors of <code>location</code>.
   * @throws ValidationException Founding hotel is not allowed.
   * @throws ChainAlreadyExistsExcpetion Hotel has already been founded.
   */
  public Set<Location> validateAddHotel(Board board, Location location,
      HotelName name) throws ValidationException, ChainAlreadyExistsExcpetion {
    checkSpotEmpty(board, location);

    if (!board.isHotelAvailable(name)) {
      throw new ChainAlreadyExistsExcpetion(name);
    }
    FoundingHotelDelegate delegate = new FoundingHotelDelegate();
    validateNeighbors(board, location.getNeighbors(), delegate);
    Set<Location> locations = delegate.getLocations();

    if (locations.isEmpty()) {
      throw new CannotAddHotelException(
          "A hotel requires at least one neighboring tile.");
    }

    return locations;
  }

  /**
   * Check if a tile can be placed to grow a existing hotel
   * 
   * @param board State of current board.
   * @param location Where to place tile.
   * @return the growing HotelName
   * @throws ValidationException Growing hotel is not allowed.
   */
  public HotelName validateGrowHotel(Board board, Location location)
      throws ValidationException {
    checkSpotEmpty(board, location);

    GrowingHotelDelegate delegate = new GrowingHotelDelegate(location);
    validateNeighbors(board, location.getNeighbors(), delegate);
    HotelName name = delegate.getHotelName();
    return name;
  }

  /**
   * Check if a tile can be placed to merge two or more hotels.
   * 
   * @param board State of current board.
   * @param location Where to place tile.
   * @return the set of HotelName involved in the merging
   * @throws ValidationException Merging hotel is not allowed.
   */

  public Set<HotelName> validateMergingHotels(Board board, Location location)
      throws ValidationException {
    checkSpotEmpty(board, location);

    MergingHotelDelegate delegate = new MergingHotelDelegate();
    validateNeighbors(board, location.getNeighbors(), delegate);
    Set<HotelName> foundHotels = delegate.getFoundHotels();
    if (foundHotels.size() < MIN_HOTELS_MERGE) {
      throw new NotTouchingHotelException(location, MIN_HOTELS_MERGE);
    }

    return foundHotels;
  }

  /**
   * Check if a tile can be placed to acquire hotels to the given Hotel Name
   * 
   * @param board State of current board.
   * @param location Where to place tile.
   * @param name of the acquirer
   * @return the set of HotelName involved in the merging
   * @throws ValidationException Merging hotel is not allowed
   */
  public Set<HotelName> validateMergingHotels(Board board, Location location,
      HotelName name) throws ValidationException {
    Set<HotelName> hotels = validateMergingHotels(board, location);
    if (!hotels.contains(name)) {
      throw new HotelAcquirerNotFoundException(location, name);
    }

    Set<HotelName> largest = getLargest(board, hotels);
    if (!largest.contains(name)) {
      throw new HotelAcquirerNotLargestException(name);
    }

    return hotels;
  }

  /**
   * Find the set of largest hotel chains from the given Hotel Names
   * 
   * @param board the current state of the board
   * @param names All the Hotel Names to check for
   * @return the set of name of largest Hotel Chains
   */
  public Set<HotelName> getLargest(Board board, Set<HotelName> names) {
    Set<HotelName> largestNames = new HashSet<HotelName>();
    int largest = 0;
    for (HotelName name : names) {
      HotelChainI chain;
      try {
        chain = board.getHotelChain(name);
      } catch (ChainNotFoundException ex) {
        // chain that doesn't exist can never be largest
        continue;
      }
      int size = chain.getSize();
      if (largest == size) {
        largestNames.add(name);
      } else if (largest < size) {
        largestNames = new HashSet<HotelName>();
        largestNames.add(name);
        largest = size;
      }
    }
    return largestNames;
  }

  /**
   * Call delegate method on each neighbor.
   * 
   * @param board State of current board.
   * @param neighbors Locations to check.
   * @param delegate method to call.
   * @throws ValidationException A neighbor prevents action from occuring.
   */
  public void validateNeighbors(Board board, Set<Location> neighbors,
      ValidationDelegate delegate) throws ValidationException {
    for (Location neighbor : neighbors) {
      delegate.validateNeighbor(board, neighbor);
    }
  }

  /**
   * Validates if the Hotel Chain is valid or not
   * 
   * @param board State of current board.
   * @param locations Locations to check.
   * @param name HotelName
   * @throws ValidationException If the hotel chain can't be added
   */
  public void ValidateAddHotel(Board board, Set<Location> locations,
      HotelName name) throws ValidationException {
    if (!board.isHotelAvailable(name)) {
      throw new ChainAlreadyExistsExcpetion(name);
    }
    for (Location location : locations) {
      checkSpotEmpty(board, location);

      boolean isConnected = false;
      for (Location other : location.getNeighbors()) {
        isConnected = isConnected || locations.contains(other);
      }
      if (!isConnected) {
        throw new ValidationException("The tile" + location
            + " is not connected to rest of location");
      }
    }
  }

  /**
   * Check that spot is empty.
   * 
   * @param board State of current board.
   * @param location Where to place tile.
   * @throws NonEmptyLocationException Location is not empty.
   */
  public void checkSpotEmpty(Board board, Location location)
      throws NonEmptyLocationException {
    if (!board.getSpot(location).isEmpty()) {
      throw new NonEmptyLocationException(location);
    }
  }
}
