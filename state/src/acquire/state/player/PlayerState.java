package acquire.state.player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.basic.StockDescription;
import acquire.exception.AlreadyMaxTilesException;
import acquire.exception.InvalidMoneyException;
import acquire.exception.InvalidPlayerNameException;
import acquire.exception.InvalidStockCountException;
import acquire.exception.InvalidTileSetException;

public class PlayerState implements MyPlayerState {
  private final String _name;
  private final Map<HotelName, StockDescription> _stockOptions;
  private final Set<Location> _tilesInHand;
  private int _money;

  /**
   * Constructor
   * 
   * @param name Display name for a player.
   * @param money Amount of cash player has.
   * @param tilesInHand Locations player can play a tile at.
   * @throws InvalidMoneyException <code>money</code> is less than 0.
   * @throws InvalidTileSetException <code>tilesInHand</code> contains more than
   *           <code>MAX_TILES</code> locations.
   * @throws InvalidPlayerNameException <code>name</code> is greater than
   *           <code>MAX_NAME_LENGTH</code>
   */
  public PlayerState(String name, int money, Set<Location> tilesInHand)
      throws InvalidMoneyException, InvalidTileSetException,
      InvalidPlayerNameException {
    if (money < 0) {
      throw new InvalidMoneyException();
    }
    if (tilesInHand.size() > MAX_TILES) {
      throw new InvalidTileSetException(tilesInHand);
    }
    // ValidatePlayerName(name);
    _name = name;
    _money = money;
    _stockOptions = new HashMap<HotelName, StockDescription>();
    _tilesInHand = new HashSet<Location>(tilesInHand);
  }

  public PlayerState(PlayerState player) {
    _name = player.getName();
    _money = player.getMoney();
    _stockOptions = player.getStockOptions();
    _tilesInHand = player.getTilesInHand();
  }

  @Override
  public String getName() {
    return _name;
  }

  @Override
  public int getMoney() {
    return _money;
  }

  /**
   * Set that amount of money this player has.
   * 
   * @param money new amount of cash.
   * @throws InvalidMoneyException <code>money</code> is less than 0.
   */
  public void setMoney(int money) throws InvalidMoneyException {
    if (money < 0) {
      throw new InvalidMoneyException();
    }
    _money = money;
  }

  /**
   * Retrieve description of shares held by this player for a given hotel.
   * 
   * @param name Hotel to retrieve description for.
   * @return Description of shares.
   */
  public StockDescription getStock(HotelName name) {
    StockDescription stock = _stockOptions.get(name);
    return stock == null ? new StockDescription(name) : stock;
  }

  /**
   * Removes the given number of shares for the given hotel from the player.
   * 
   * @param name Hotel to remove share for.
   * @param count number of shares to remove
   * @throws InvalidStockCountException if the player have share less than count
   */
  public void removeStock(HotelName name, int count)
      throws InvalidStockCountException {
    StockDescription myStock = _stockOptions.get(name);
    if (null != myStock) {
      StockDescription stockDescription = myStock.changeCount(-count);
      if (stockDescription.getCount() == 0)
        _stockOptions.remove(name);
      else
        _stockOptions.put(name, stockDescription);
    }
  }

  /**
   * Set the number of stocks held by this player.
   * 
   * @param stock Description of stocks player should have.
   */
  public void setStock(StockDescription stock) {
    _stockOptions.put(stock.getName(), stock);
  }

  @Override
  public Set<Location> getTilesInHand() {
    return new HashSet<Location>(_tilesInHand);
  }

  /**
   * Add a new tile that this player can play. Re-adding a location has no
   * effect.
   * 
   * @param location Location of the tile the player can place.
   * @throws AlreadyMaxTilesException Player already has <code>MAX_TILES</code>
   *           locations.
   */
  public void addTileToHand(Location location) throws AlreadyMaxTilesException {
    if (_tilesInHand.size() == MAX_TILES) {
      throw new AlreadyMaxTilesException(_name);
    }
    _tilesInHand.add(location);
  }

  /**
   * Remove a tile from the set of locations where this player can place a tile.
   * Removing a tile a player does not have has no effect.
   * 
   * @param location Location of the tile to remove.
   */
  public void removeTileFromHand(Location location) {
    _tilesInHand.remove(location);
  }

  @Override
  public Map<HotelName, StockDescription> getStockOptions() {
    return new HashMap<HotelName, StockDescription>(_stockOptions);
  }

  public static void ValidatePlayerName(String name)
      throws InvalidPlayerNameException {
    if (name.length() > MAX_NAME_LENGTH) {
      throw new InvalidPlayerNameException(name);
    }
  }

  @Override
  public int compareTo(PlayerStateI other) {
    return _name.compareTo(other.getName());
  }
}
