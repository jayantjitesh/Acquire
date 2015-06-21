package acquire.player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import acquire.basic.Action.PlacementType;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.basic.StockDescription;
import acquire.board.BoardI;
import acquire.board.inspect.FoundingResult;
import acquire.board.inspect.GrowResult;
import acquire.board.inspect.InspectResult;
import acquire.board.inspect.MergeResult;
import acquire.board.inspect.PossibleResult;
import acquire.board.inspect.SingletonResult;
import acquire.exception.AcquireRuntimeException;
import acquire.exception.DuplicateCallException;
import acquire.exception.InvalidPurchaseRequestException;
import acquire.exception.NotValidAcquirerException;
import acquire.exception.NotValidMergerException;
import acquire.player.strategy.MyStrategy;
import acquire.player.strategy.StrategyHelper;
import acquire.protocol.TurnI;
import acquire.protocol.description.ActionDescription;
import acquire.protocol.description.ErrorDescription;
import acquire.protocol.request.MergingRequest;
import acquire.protocol.request.OrderResponse;
import acquire.protocol.request.PlaceRequest;
import acquire.protocol.request.PurchaseRequest;
import acquire.protocol.request.Response;
import acquire.protocol.request.TurnResponse;
import acquire.state.StateI;
import acquire.state.perspective.EndPerspective;
import acquire.state.perspective.Perspective;
import acquire.state.perspective.PlayerPerspective;
import acquire.state.player.PlayerState;
import acquire.state.player.PlayerStateI;

public class MyPlayer implements Player {

  private static final Logger LOGGER = Logger.getLogger(Player.class);
  private String _name;
  private final MyStrategy _strategy = new MyStrategy();
  private StateI _currentState;
  private boolean _isMerge = false;

  public MyPlayer(String name) {
    _name = name;
    // _strategy = strategy;
  }

  @Override
  public String getName() {
    return _name;
  }

  @Override
  public void actionOccured(StateI state, ActionDescription action) {
    _currentState = state;
  }

  @Override
  public void startGame(StateI state) {
    _currentState = state;
  }

  @Override
  public Response takeTurn(TurnI turn) {
    _isMerge = false;
    PossibleResult moveRequest = getMoveRequest(turn.getPerspective());
    PlaceRequest place = getPossibleResult(moveRequest);

    PurchaseRequest purchaseRequest = getPurchaseRequest(turn, moveRequest);

    if (_isMerge)
      return new OrderResponse(purchaseRequest);
    else
      return new TurnResponse(place, purchaseRequest);
  }

  @Override
  public List<Boolean> keep(List<HotelName> labels) {
    return _strategy.keep(labels);
  }

  @Override
  public void kickedOut(ErrorDescription description) {
  }

  @Override
  public void gameOver(EndPerspective perspective) {
  }

  /**
   * Creates the place request according to the current strategy
   * 
   * @param perspective the current game state
   * @return the desired place request
   */
  private PossibleResult getMoveRequest(PlayerPerspective perspective) {
    Set<PossibleResult> validMoves = getValidMoves(perspective);
    if (validMoves.isEmpty()) {
      return null;
    }
    return _strategy.choosePlacement(validMoves, perspective);
  }

  /**
   * Creates the purchase request according to the current strategy
   * 
   * @param turn the TurnI object
   * @param moveRequest the current place request
   * @return the desired purchase request
   */
  private PurchaseRequest getPurchaseRequest(TurnI turn,
      PossibleResult moveRequest) {
    PlayerPerspective perspective = turn.getPerspective();
    Map<HotelName, Integer> availableShares = getAvailableShares(perspective);

    BoardI board = perspective.getBoard();
    Map<HotelName, Integer> hotelChainSize = new HashMap<HotelName, Integer>();
    for (HotelName hotelName : HotelName.values()) {
      hotelChainSize.put(hotelName, board.getHotelChainSize(hotelName));
    }

    updateSharesAndHotelSize(moveRequest, availableShares, hotelChainSize, turn);
    if (availableShares.isEmpty()) {
      try {
        return new PurchaseRequest();
      } catch (InvalidPurchaseRequestException ex) {
        AcquireRuntimeException.logAndThrow(LOGGER,
            "This should never happen. ", ex);
      }
    }
    return _strategy.choosePurchase(availableShares, perspective.getBanker(),
        perspective.getMyself().getMoney(), hotelChainSize);
  }

  /**
   * Update the available shares and the hotel size maps according to the place
   * request
   * 
   * @param moveRequest the current place request
   * @param availableShares the current map of available shares
   * @param hotelChainSize the map of hotel name with their respective size
   * @param turn the TurnI object
   */

  private void updateSharesAndHotelSize(PossibleResult moveRequest,
      Map<HotelName, Integer> availableShares,
      Map<HotelName, Integer> hotelChainSize, TurnI turn) {

    switch (moveRequest.getType()) {
      case GROW:
        HotelName growHotel = ((GrowResult) moveRequest).getName();
        hotelChainSize.put(growHotel, hotelChainSize.get(growHotel) + 1);
        break;
      case FOUND:
        handleFound((FoundingResult) moveRequest, availableShares,
            hotelChainSize);
        break;
      case MERGE:
        handleMerge((MergeResult) moveRequest, availableShares, hotelChainSize,
            turn);
      default:
        break;
    }

  }

  /**
   * Update the available shares and the hotel size maps
   * 
   * @param foundingResult the current founding request
   * @param availableShares the current map of available shares
   * @param hotelChainSize the map of hotel name with their respective size
   */
  private void handleFound(FoundingResult foundingResult,
      Map<HotelName, Integer> availableShares,
      Map<HotelName, Integer> hotelChainSize) {
    HotelName foundedShare = foundingResult.getName();
    if (availableShares.containsKey(foundedShare))
      availableShares.put(foundedShare, availableShares.get(foundedShare) - 1);

    hotelChainSize
        .put(foundedShare, foundingResult.getAddedHotels().size() + 1);
  }

  /**
   * Calls the rule enforcer to ask player to if they want to sell the acquired
   * hotel's share and update it's list of available shares accordingly
   * 
   * @param mergRequest the merging request
   * @param availableShares the current map of available shares
   * @param hotelChainSize the map of hotel name with their respective size
   * @param turn the TurnI object
   */
  private void handleMerge(MergeResult mergRequest,
      Map<HotelName, Integer> availableShares,
      Map<HotelName, Integer> hotelChainSize, TurnI turn) {
    try {
      Map<HotelName, Integer> currentAcquiredShareHolder = createCurrentAcquiredShareHolder(
          turn.getPerspective().getAllPlayers(), mergRequest.getHotels());
      HotelName acquirer = (HotelName) mergRequest.getLargest().toArray()[0];
      Set<HotelName> acquiredHotels = mergRequest.getHotels();
      List<PlayerState> updatedPlayers = turn.place(mergRequest.getLocation(),
          acquirer);

      Map<HotelName, Integer> updatedAcquiredShareHolder = createCurrentAcquiredShareHolder(
          updatedPlayers, acquiredHotels);

      for (HotelName hotel : acquiredHotels) {
        int change = currentAcquiredShareHolder.get(hotel)
            - updatedAcquiredShareHolder.get(hotel);
        if (change > 0)
          availableShares.put(hotel, availableShares.get(hotel) + change);
      }

      int acquirerSize = 0;
      // update all the acquired hotel size to 0 and update the size of acquirer
      for (HotelName hotel : mergRequest.getHotels()) {
        acquirerSize = acquirerSize + hotelChainSize.get(hotel);
        hotelChainSize.put(hotel, 0);
      }
      hotelChainSize.put(acquirer, acquirerSize + 1);
      _isMerge = true;

    } catch (NotValidMergerException ex) {
      AcquireRuntimeException.logAndThrow(
          LOGGER,
          "This can't happen since it's already validated"
              + ex.getLocalizedMessage());
    } catch (NotValidAcquirerException ex) {
      AcquireRuntimeException.logAndThrow(
          LOGGER,
          "This can't happen since it's already validated"
              + ex.getLocalizedMessage());
    } catch (DuplicateCallException ex) {
      AcquireRuntimeException.logAndThrow(
          LOGGER,
          "This can't happen since it's already validated"
              + ex.getLocalizedMessage());
    }
  }

  /**
   * Creates a map with acquired hotel name as key and the share currently held
   * by all the players as value
   * 
   * @param playerState the list of all the players
   * @param acquiredHotels the set of acquired hotels
   * @return the map with acquired hotel name as key and the share count as
   *         value
   */
  private Map<HotelName, Integer> createCurrentAcquiredShareHolder(
      List<PlayerState> playerState, Set<HotelName> acquiredHotels) {
    Map<HotelName, Integer> currentShareHolders = new HashMap<HotelName, Integer>();

    for (HotelName acquired : acquiredHotels) {
      currentShareHolders.put(acquired, 0);
    }

    for (PlayerStateI player : playerState) {
      Map<HotelName, StockDescription> stockOptions = player.getStockOptions();
      for (HotelName acquired : acquiredHotels) {
        if (stockOptions.containsKey(acquired))
          currentShareHolders.put(acquired, currentShareHolders.get(acquired)
              + stockOptions.get(acquired).getCount());
      }
    }
    return currentShareHolders;
  }

  /**
   * Finds all kind of possible moves this player can make with his set of tiles
   * in hand
   * 
   * @param perspective the current PlayerPerspective
   * @return the mapping of each tile that can be placed with their possible
   *         effects
   */
  protected static Set<PossibleResult> getValidMoves(
      PlayerPerspective perspective) {
    Set<PossibleResult> valid = new HashSet<PossibleResult>();
    for (Location tile : perspective.getMyself().getTilesInHand()) {
      InspectResult result = perspective.getBoard().inspect(tile);
      if (result.isValid()) {
        valid.add((PossibleResult) result);
        if (((PossibleResult) result).getType().equals(PlacementType.FOUND)) {
          for (HotelName hotel : perspective.getBoard()
              .getAvailableHotelNames()) {
            valid.add(new FoundingResult(tile, hotel, tile.getNeighbors()));
          }
        }
      }
    }
    return valid;
  }

  /**
   * Finds all the available shares with respect to the current
   * PlayerPerspective
   * 
   * @param perspective the current state of the game
   * @return available shares as the mapping of hotel names with their
   *         respective number of available shares
   */
  protected static Map<HotelName, Integer> getAvailableShares(
      Perspective perspective) {

    Map<HotelName, Integer> availableShares = new HashMap<HotelName, Integer>();

    // for (HotelName name : HotelName.values()) {
    //
    // int shares = perspective.getBanker().getAvailableShareCount(name);
    // if (shares > 0) {
    // availableShares.put(name, shares);
    // }
    // }

    for (HotelName name : HotelName.values()) {
      availableShares.put(name, StockDescription.MAX_COUNT);
    }

    for (PlayerState pState : perspective.getAllPlayers()) {
      Map<HotelName, StockDescription> stockOptions = pState.getStockOptions();
      for (HotelName label : stockOptions.keySet()) {
        StockDescription option = stockOptions.get(label);
        if (option != null)
          availableShares.put(label,
              availableShares.get(label) - option.getCount());
      }
    }

    return availableShares;
  }

  /**
   * get the PlaceRequest based on the possible request, it just changes the
   * data from one objet to next.
   * 
   * @param result
   * @return
   * @throws AcquireRuntimeException
   */
  private static PlaceRequest getPossibleResult(PossibleResult result)
      throws AcquireRuntimeException {
    switch (result.getType()) {
      case SINGLETON:
        return StrategyHelper.convertToRequest((SingletonResult) result);
      case FOUND:
        return StrategyHelper.convertToRequest((FoundingResult) result);
      case GROW:
        return StrategyHelper.convertToRequest((GrowResult) result);
      case MERGE:
        return getMergeRequest((MergeResult) result);
      default:
        throw new AcquireRuntimeException("getPossibleResult: "
            + "LargestAlphaStrategy: invalid possible result passed in");
    }

  }

  /**
   * Convert mergeResult to MergingRequest
   * 
   * @param mresult
   * @return
   */
  private static MergingRequest getMergeRequest(MergeResult mresult) {
    return new MergingRequest(mresult.getLocation(), (HotelName) mresult
        .getLargest().toArray()[0]);
  }

  @Override
  public void newTile(Location tile) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setName(String name) {
    _name = name;
  }

}
