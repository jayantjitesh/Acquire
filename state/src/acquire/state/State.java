package acquire.state;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import org.apache.log4j.Logger;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.basic.StockDescription;
import acquire.board.Board;
import acquire.board.BoardI;
import acquire.board.Spot;
import acquire.board.inspect.FoundingResult;
import acquire.board.inspect.ImposibleResult;
import acquire.board.inspect.InspectResult;
import acquire.board.inspect.MergeResult;
import acquire.board.inspect.PossibleResult;
import acquire.enforcer.tile.TileSelectionModel;
import acquire.exception.AcquireRuntimeException;
import acquire.exception.AlreadyMaxTilesException;
import acquire.exception.ChainNotFoundException;
import acquire.exception.InvalidHotelPurchaseException;
import acquire.exception.InvalidMoneyException;
import acquire.exception.InvalidSharePriceException;
import acquire.exception.InvalidStockCountException;
import acquire.exception.InvalidStockPurchaseException;
import acquire.exception.InvalidStockReturnException;
import acquire.exception.NoAvailableTileException;
import acquire.exception.validate.ValidationException;
import acquire.state.money.Banker;
import acquire.state.money.BankerI;
import acquire.state.player.PlayerState;
import acquire.state.player.PlayerStateI;

/**
 * defines the current state of the Game in respect of Board, Banker and all
 * Player's
 * 
 * A State is a new State(Board _board,Queue<String> _players, Map<String,
 * PlayerState> _playersMap,Set<Location> _availableTiles, Banker
 * _banker,TileSelectionModel _selectionModel) Interp: _board : defines the
 * current state of the Board _players : defines the name of all the active
 * players in the game _playersMap : defines each active player and his current
 * state _availableTiles : defines list of Tile's that banker holds _banker :
 * defines the current state of Banker _selectionModel : defines how the
 * replacement Tile is selected
 * 
 */
public class State implements StateI {
  private static final Logger LOGGER = Logger.getLogger(State.class);
  private static final int MAJORTITY_BONUS = 10;
  private static final int MINORITY_BONUS = 5;
  private final Board _board;
  private final Queue<String> _players;
  private final Map<String, PlayerState> _playersMap;
  private final Set<Location> _availableTiles;
  private final Banker _banker;
  private final TileSelectionModel _selectionModel;

  /**
   * Construct a state with the default <code>Banker</code>
   * 
   * @param board Board to player on.
   * @param players Player names in turn order.
   * @param playersMap Mapping of player names to state descriptions.
   * @param selectionModel Implementation of tile selection strategy.
   */
  public State(Board board, Queue<String> players,
      Map<String, PlayerState> playersMap, TileSelectionModel selectionModel) {
    this(board, players, playersMap, new Banker(), selectionModel);
  }

  public State(State state) {
    HashMap<String, PlayerState> playersMap = new HashMap<String, PlayerState>();

    for (Entry<String, PlayerState> entry : state._playersMap.entrySet()) {
      playersMap.put(entry.getKey(), new PlayerState(entry.getValue()));
    }
    _board = new Board(state._board);
    _players = new ArrayDeque<String>(state.getPlayersName());
    _playersMap = playersMap;
    _availableTiles = getAvailableTiles(_board, _playersMap);
    _banker = new Banker(state._banker);
    _selectionModel = state._selectionModel;

  }

  State(Board board, Queue<String> players,
      Map<String, PlayerState> playersMap, Banker banker,
      TileSelectionModel selectionModel) {
    _board = board;
    _players = players;
    _playersMap = playersMap;
    _availableTiles = getAvailableTiles(_board, _playersMap);
    _banker = banker;
    _selectionModel = selectionModel;
  }

  /*
   * (non-Javadoc)
   * 
   * @see acquire.state.IState#getAvailableTiles()
   */
  @Override
  public Set<Location> getAvailableTiles() {
    return _availableTiles;
  }

  @Override
  public BoardI getBoard() {
    return _board;
  }

  @Override
  public Queue<String> getPlayersName() {
    return _players;
  }

  @Override
  public List<PlayerState> getPlayers() {
    return new ArrayList<PlayerState>(_playersMap.values());
  }

  public String getCurrentPlayerName() {
    return _players.peek();
  }

  public PlayerState getCurrentPlayer() {
    return _playersMap.get(getCurrentPlayerName());
  }

  /**
   * Remove the current player from the game.<br />
   * Any shares or tiles held by the player are returned to the available set.
   */
  public void kickCurrentPlayer() {
    String current = _players.remove();
    // PlayerState currentPlayer = _playersMap.get(current);
    _playersMap.remove(current);

    // for (StockDescription stock : currentPlayer.getStockOptions().values()) {
    // try {
    // _banker.returnStocksToBank(stock);
    // } catch (InvalidStockReturnException ex) {
    // AcquireRuntimeException.logAndReturn(LOGGER,
    // "There is a fixed number of stocks to start with.", ex);
    // }
    // }
    //
    // _availableTiles.addAll(currentPlayer.getTilesInHand());
  }

  /**
   * Move the current player to the end of the queue. Give the player a new
   * tile, if possible.
   * 
   * @throws NoAvailableTileException if no new tiles are left
   * 
   */
  public void nextTurn() throws NoAvailableTileException {
    nextTurn(getNextTile());
  }

  /**
   * Move the current player to the end of the queue. Give the player a new
   * tile, if possible.
   * 
   * @throws NoAvailableTileException if no new tiles are left
   * 
   */
  public Location getNextTile() throws NoAvailableTileException {
    return _selectionModel.selectTile(_availableTiles);

  }

  /**
   * Move the current player to the end of the queue. Give the player a new
   * tile, if possible.
   * 
   * @throws NoAvailableTileException if no new tiles are left
   * 
   */
  public void nextTurn(Location tile) throws NoAvailableTileException {
    String current = _players.remove();
    PlayerState currentPlayer = _playersMap.get(current);
    if (currentPlayer.getTilesInHand().size() == PlayerStateI.MAX_TILES) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "Player must have played a tile during their turn.");
    }
    try {

      currentPlayer.addTileToHand(tile);
      _availableTiles.remove(tile);

    } catch (AlreadyMaxTilesException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "We should not be adding a tile if player already has max.", ex);
    }
    _players.add(current);
  }

  /**
   * Process the list of purchasing request for the current player
   * 
   * @param stocksToBuy Collection of stocks to buy
   * @throws InvalidHotelPurchaseException If not enough stocks are available
   * @throws InvalidStockPurchaseException if the money is not sufficient
   * @throws InvalidSharePriceException There is no price for the requested
   *           share.
   */
  public void purchaseStock(Collection<StockDescription> stocksToBuy)
      throws InvalidSharePriceException, InvalidStockPurchaseException {
    for (StockDescription stockDescription : stocksToBuy) {
      int size;
      HotelName hotelName = stockDescription.getName();
      try {
        size = _board.getHotelChain(hotelName).getSize();
      } catch (ChainNotFoundException ex) {
        size = 0;
      }

      int purchasePrice = stockDescription.getCount()
          * _banker.getSharePrice(hotelName, size);
      PlayerState currentPlayer = getCurrentPlayer();
      if (currentPlayer.getMoney() < purchasePrice) {
        throw new InvalidStockPurchaseException("Player "
            + currentPlayer.getName()
            + " doesn't have enough money to buy stock of hotel chain "
            + stockDescription.getName());
      }
      _banker.purchaseStocks(stockDescription);
      try {
        StockDescription currStock = currentPlayer.getStock(stockDescription
            .getName());
        currentPlayer.setStock(currStock.changeCount(stockDescription
            .getCount()));
        currentPlayer.setMoney(currentPlayer.getMoney() - purchasePrice);
      } catch (InvalidMoneyException ex) {
        AcquireRuntimeException.logAndThrow(LOGGER,
            "This should never happen. We are checking the money before.", ex);
      } catch (InvalidStockCountException ex) {
        AcquireRuntimeException.logAndThrow(LOGGER,
            "The bank is requlating how many stocks exist, "
                + "a player should not get more than exist.", ex);
      }
    }
  }

  /**
   * Place the tile according to the possible placement. First checks what kind
   * of affect the placing of tile will have then apply that move on board.
   * 
   * @param location the tile
   * @param hotelName the name of hotel if the effect is founding.
   *          <code>null</code> if place request did not supply a hotel name.
   * @throws ValidationException not a valid move
   */
  public void place(Location location, HotelName hotelName)
      throws ValidationException {
    PlayerState currentPlayer = getCurrentPlayer();
    if (!currentPlayer.getTilesInHand().contains(location)) {
      throw new ValidationException("Tile " + location.toString()
          + " doesn't belong to current player " + getCurrentPlayerName()
          + " .");
    }
    InspectResult inspect = _board.inspect(location);
    if (!inspect.isValid()) {
      ImposibleResult result = (ImposibleResult) inspect;
      throw new ValidationException("Location is impossible: "
          + result.getMessage());
    }
    PossibleResult result = (PossibleResult) inspect;
    switch (result.getType()) {
      case SINGLETON:
        _board.addSingleton(location);
        break;
      case FOUND:
        handleFoundPlacement((FoundingResult) inspect, hotelName, location,
            currentPlayer);
        break;
      case GROW:
        _board.growHotel(location);
        break;
      case MERGE:
        handleMergePlacement((MergeResult) inspect, hotelName, location);
        break;
      default:
        AcquireRuntimeException.logAndThrow(LOGGER,
            "Unexpected placement type: " + result.getType());
        break;
    }
    currentPlayer.removeTileFromHand(location);
  }

  /**
   * Handles the merging cases
   * 
   * @param result the information of merge
   * @param hotelName the acquirer hotel name
   * @param location the tile to place
   * @param currentPlayer the state of current player
   * @throws ValidationException if the merging can not be done
   */
  private void handleMergePlacement(MergeResult result, HotelName hotelName,
      Location location) throws ValidationException {
    if (hotelName == null) {
      throw new ValidationException(
          "Acquirer hotel name not provided for the merging request");
    }
    for (HotelName accuiredHotel : result.getHotels()) {
      if (accuiredHotel != hotelName) {
        updateMergeBonus(accuiredHotel);
      }
    }
    try {
      _board.mergeHotel(location, hotelName);
    } catch (ValidationException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "Insepect said merge, should be valid.", ex);
    }
  }

  /**
   * Handles the founding cases
   * 
   * @param result the information of founding
   * @param hotelName the founding hotel name
   * @param location the tile to place
   * @param currentPlayer the state of current player
   * @throws ValidationException if the founding can not be done
   */
  private void handleFoundPlacement(FoundingResult result, HotelName hotelName,
      Location location, PlayerState currentPlayer) throws ValidationException {
    if (hotelName == null) {
      throw new ValidationException(
          "Hotel name not provided for the founding request");
    }
    _board.addHotel(hotelName, location);

    if (_banker.getAvailableShareCount(hotelName) != 0) {
      try {
        StockDescription freeStock = new StockDescription(hotelName, 1);
        _banker.purchaseStocks(freeStock);
        currentPlayer.setStock(freeStock);
      } catch (InvalidHotelPurchaseException ex) {
        AcquireRuntimeException.logAndThrow(LOGGER,
            "Since we have checked that a share exists.", ex);
      } catch (InvalidStockCountException ex) {
        AcquireRuntimeException.logAndThrow(LOGGER,
            "Since we have checked that a share exists.", ex);
      }
    }
  }

  /**
   * Calculates the merging bonus by the acquire rule and update the players
   * money accordingly
   * 
   * @param hotelName the acquired Hotel
   */
  public void updateMergeBonus(HotelName hotelName) {
    Map<String, Integer> stockHolders = new HashMap<String, Integer>();
    for (PlayerState player : _playersMap.values()) {
      stockHolders.put(player.getName(), player.getStock(hotelName).getCount());
    }
    applyBonus(stockHolders, hotelName);
  }

  /**
   * Find the majority and minority stock owners based on the given mapping.
   * 
   * @param stockHolders Mapping to calculate from.
   * @param majorityPlayers Set to add majority owners to.
   * @param minorityPlayers Set to add minority owners to.
   */
  private void getMajorMinorOwners(Map<String, Integer> stockHolders,
      Set<String> majorityPlayers, Set<String> minorityPlayers) {
    int maxCount = 0;
    int secondMaxCount = 0;
    for (String playerKey : stockHolders.keySet()) {
      int count = stockHolders.get(playerKey);
      if (maxCount == count) {
        maxCount = count;
        majorityPlayers.add(playerKey);
      } else if (maxCount < count) {
        maxCount = count;
        majorityPlayers.clear();
        majorityPlayers.add(playerKey);
      } else if (secondMaxCount == count) {
        secondMaxCount = count;
        minorityPlayers.add(playerKey);
      } else if (secondMaxCount < count) {
        secondMaxCount = count;
        minorityPlayers.clear();
        minorityPlayers.add(playerKey);
      }
    }
  }

  /**
   * Finds the majority and minority shareholder for the given hotel and then
   * applies the bonus accordingly
   * 
   * @param stockHolders The mapping of player names to their respective stock
   *          holding
   * @param hotelName the name of hotel for which the bonus is getting
   *          calculated
   */
  private void applyBonus(Map<String, Integer> stockHolders, HotelName hotelName) {
    Set<String> majorityPlayers = new HashSet<String>();
    Set<String> minorityPlayers = new HashSet<String>();
    getMajorMinorOwners(stockHolders, majorityPlayers, minorityPlayers);

    try {
      int sharePrice = _banker.getSharePrice(hotelName,
          _board.getHotelChain(hotelName).getSize());

      if (majorityPlayers.size() > 1) {
        updatePlayers(majorityPlayers, sharePrice, MAJORTITY_BONUS
            + MINORITY_BONUS);
        return;
      }
      if (!majorityPlayers.isEmpty()) {
        updatePlayers(majorityPlayers, sharePrice, MAJORTITY_BONUS);
      }
      if (!minorityPlayers.isEmpty()) {
        updatePlayers(minorityPlayers, sharePrice, MINORITY_BONUS);
      }
    } catch (ChainNotFoundException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "Chain name is validated during merging.", ex);
    } catch (InvalidSharePriceException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "Chain name is validated during merging.", ex);
    }
  }

  /**
   * Updates the given set of player's money by equally dividing the
   * amount(sharePrice*Multiplier) among them
   * 
   * @param players to update the amount for
   * @param sharePrice the price of one share
   * @param muliplier the bonus multiplier
   */
  private void updatePlayers(Set<String> players, int sharePrice, int muliplier) {
    int amount = sharePrice * muliplier / players.size();

    for (String key : players) {
      try {
        PlayerState player = _playersMap.get(key);
        player.setMoney(amount + player.getMoney());
      } catch (InvalidMoneyException ex) {
        AcquireRuntimeException.logAndThrow(LOGGER,
            "The amount will be always valid since we are calculating", ex);
      }
    }
  }

  /**
   * Returns the set of available tiles on the board
   * 
   * @param board the current <code>Board</code>
   * @param players queue of <code>Player</code>
   * @return the set of available tiles
   */
  private static Set<Location> getAvailableTiles(BoardI board,
      Map<String, PlayerState> players) {
    Set<Location> availableTiles = new HashSet<Location>();

    for (Spot spot : board.getSpots().values()) {
      if (spot.isEmpty()) {
        availableTiles.add(spot.getLocation());
      }
    }

    for (PlayerState player : players.values()) {
      availableTiles.removeAll(player.getTilesInHand());
    }

    return availableTiles;
  }

  /**
   * Sells the share of the hotels if the corresponding decision is true and
   * update the player's money
   * 
   * @param playerName the player selling the shares
   * @param hotels the list of share label
   * @param decisions the list of decision in the same order of label in hotels.
   *          It should be of same size as the hotels
   */
  public void sellShare(String playerName, List<HotelName> hotels,
      List<Boolean> decisions) {
    PlayerState player = _playersMap.get(playerName);
    for (int i = 0; i < hotels.size(); i++) {
      HotelName hotelName = hotels.get(i);
      Boolean decision = decisions.get(i);
      if (decision) {
        try {
          int count = player.getStock(hotelName).getCount();
          if (count > 0) {
            int amount = _banker.getSharePrice(hotelName,
                _board.getHotelChainSize(hotelName))
                * count;
            player.setMoney(amount + player.getMoney());
            player.removeStock(hotelName, count);
            _banker.returnStocksToBank(new StockDescription(hotelName, count));
          }
        } catch (InvalidMoneyException ex) {
          AcquireRuntimeException.logAndThrow(LOGGER,
              "The amount will be always valid since we are calculating", ex);
        } catch (InvalidSharePriceException ex) {
          AcquireRuntimeException.logAndThrow(LOGGER,
              "This will never happen since this is validated while purchase",
              ex);
        } catch (InvalidStockCountException ex) {
          AcquireRuntimeException.logAndThrow(LOGGER,
              "This will never happen since this is validated while purchase",
              ex);
        } catch (InvalidStockReturnException ex) {
          AcquireRuntimeException.logAndThrow(LOGGER,
              "This will never happen since this is validated while purchase",
              ex);
        }
      }
    }
  }

  /**
   * Calculates the final score of each of the player. By applying the merger
   * bonus and selling all the shares
   * 
   * @return the final score
   */
  public Map<String, Integer> calculateFinalScore() {
    Map<String, Integer> finalScore = new HashMap<String, Integer>();
    for (HotelName founded : _board.getHotelChains().keySet()) {
      updateMergeBonus(founded);
    }
    for (PlayerState player : _playersMap.values()) {
      List<Boolean> decision = new ArrayList<Boolean>();
      List<HotelName> hotelsToSell = new ArrayList<HotelName>();
      for (HotelName name : player.getStockOptions().keySet()) {
        hotelsToSell.add(name);
        decision.add(true);
      }
      sellShare(player.getName(), hotelsToSell, decision);
      finalScore.put(player.getName(), player.getMoney());
    }
    return finalScore;
  }

  @Override
  public BankerI getBanker() {
    return _banker;
  }

  public boolean isAvailableTilesEmpty() {
    return _availableTiles.isEmpty();
  }
}
