package acquire.basic;

import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import acquire.exception.DuplicateSpotException;
import acquire.exception.InvalidSizeException;

/**
 * Representation of a founded hotel on a <code>Board</code>.
 */
public class HotelChain implements HotelChainI {
	static final Logger LOGGER = Logger.getLogger(HotelChain.class);
	private final HotelChainI.HotelName _name;
	private final Set<Location> _locations;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            of the Hotel
	 * @param locations
	 *            belong to this Hotel
	 * @throws InvalidSizeException
	 *             if the size of locations less than <code>MIN_SPOTS</code>
	 */
	public HotelChain(HotelName name, Set<Location> locations)
			throws InvalidSizeException {
		checkHotelSize(locations);
		_name = name;
		_locations = new HashSet<Location>(locations);
	}

	/**
	 * check for invariant that size of hotel >= <code>MIN_SPOTS</code>
	 * 
	 * @param locations
	 *            belong to this Hotel
	 * @throws InvalidSizeException
	 */
	private void checkHotelSize(Set<Location> locations)
			throws InvalidSizeException {
		if (locations.size() < MIN_SPOTS) {
			throw new InvalidSizeException(locations.size());
		}
	}

	/**
	 * The copy Constructor
	 * 
	 * @param chain
	 *            the chain to copy from.
	 * @throws InvalidSizeException
	 */
	public HotelChain(HotelChainI chain) throws InvalidSizeException {
		checkHotelSize(chain.getLocations());
		_name = chain.getName();
		_locations = chain.getLocations();
	}

	/**
	 * Add a location on the board to this Hotel Chain
	 * 
	 * @param spot
	 *            location on the board
	 * @throws DuplicateSpotException
	 *             if the spot already exists in this Hotel Chain
	 * @throws InvalidSizeException
	 */
	public void addSpot(Location spot) throws DuplicateSpotException,
			InvalidSizeException {
		checkDuplicateTiles(spot);
		_locations.add(spot);
		checkHotelSize(_locations);
	}

	/**
	 * check for pre-condition Tile not already part of HotelChain
	 * 
	 * @param spot
	 *            new tile to be added
	 * @throws DuplicateSpotException
	 */
	public void checkDuplicateTiles(Location spot)
			throws DuplicateSpotException {
		if (_locations.contains(spot)) {
			throw new DuplicateSpotException(spot, _name);
		}
	}

	@Override
	public HotelName getName() {
		return _name;
	}

	@Override
	public int getSize() {
		return _locations.size();
	}

	@Override
	public Set<Location> getLocations() {
		return new HashSet<Location>(_locations);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_name == null) ? 0 : _name.hashCode());
		result = prime * result
				+ ((_locations == null) ? 0 : _locations.hashCode());
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
		HotelChain other = (HotelChain) obj;
		if (_name == null) {
			if (other._name != null)
				return false;
		} else if (!_name.equals(other._name))
			return false;
		if (_locations == null) {
			if (other._locations != null)
				return false;
		} else if (!_locations.equals(other._locations))
			return false;
		return true;
	}

	/**
	 * Checks if the given location touches the given Hotel Chain
	 * 
	 * @param chain
	 *            the hotel chain to check within
	 * @param location
	 *            the location to check
	 * @return true if the location touches the chain else false
	 */
	public static boolean touches(HotelChainI chain, Location location) {
		Set<Location> inChain = chain.getLocations();
		for (Location neighbor : location.getNeighbors()) {
			for (Location locInChain : inChain) {
				if (locInChain.equals(neighbor)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isSafe() {
		return _locations.size() >= SAFE_SPOTS;
	}

	@Override
	public int compareTo(HotelChainI other) {
		return _name.compareTo(other.getName());
	}

}
