package acquire.game.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import acquire.basic.HotelChainI;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.basic.StockDescription;
import acquire.board.BoardI;
import acquire.board.inspect.InspectResult;
import acquire.board.inspect.MergeResult;
import acquire.board.inspect.PossibleResult;
import acquire.exception.AcquireRuntimeException;
import acquire.exception.BadPlayerExecption;
import acquire.exception.InvalidPurchaseRequestException;
import acquire.exception.InvalidSharePriceException;
import acquire.exception.InvalidStockCountException;
import acquire.exception.InvalidStockPurchaseException;
import acquire.exception.NoAvailableTileException;
import acquire.exception.validate.ValidationException;
import acquire.game.ShareCombination;
import acquire.game.tree.exception.FinalStateException;
import acquire.game.tree.exception.InvalidEdgeException;
import acquire.game.tree.exception.InvalidPlayerTurnException;
import acquire.game.tree.exception.NotFinalStateException;
import acquire.player.Player;
import acquire.protocol.request.FoundingRequest;
import acquire.protocol.request.GrowingRequest;
import acquire.protocol.request.HotelRequest;
import acquire.protocol.request.MergingRequest;
import acquire.protocol.request.PlaceRequest;
import acquire.protocol.request.PurchaseRequest;
import acquire.protocol.request.SingletonRequest;
import acquire.state.State;
import acquire.state.money.BankerI;
import acquire.state.perspective.EndPerspective.Cause;
import acquire.state.player.MyPlayerState;
import acquire.state.player.PlayerState;
import acquire.state.player.PlayerStateI;

/**
 * A Tree is a new Tree(State state,List<Edge> getEdges(),State next(Edge e)
 * boolean isFinalState, Cause _gameOverCause) Interp: state: defines the
 * current game state getEdges() : defines a list of all possible valid moves
 * from current state next(Edge e) : defines the next state of game based on the
 * value of Edge e isFinalState : true iff the current game state is final game
 * state gameOverCause : defines the reason for end of game
 */

public class Tree implements TreeI {
  private static final Logger LOGGER = Logger.getLogger(Tree.class);
  protected final State _state;
  private final List<PurchaseRequest> _combinations;
  private boolean _isFinalState = false;
  private Cause _gameOverCause = null;

  /**
   * 
   * @param previuosnode
   * @param previousEdge
   * @param combinations
   * @throws BadPlayerExecption
   * @throws NoAvailableTileException
   */
  public Tree(State previuosnode, Edge previousEdge,
      List<PurchaseRequest> combinations) {
    _state = generateNextNode(previuosnode, previousEdge);
    _combinations = combinations;
    _isFinalState = checkForFinalState();
  }

  /**
   * 
   * @param node represents the current State of the game
   */
  public Tree(State node) {
    _state = node;
    _combinations = ShareCombination.generateCombinations();
    _isFinalState = checkForFinalState();
  }

  /**
   * Returns the subtree after applying the given edge e
   * 
   * @param e the edge to apply
   * @return the subtree after applying the given edge e
   * @throws InvalidEdgeException if the current edge doesn't belong to the
   *           current player
   * @throws FinalStateException if the current state is final state
   */
  protected TreeI next(Edge e) throws InvalidEdgeException, FinalStateException {

    if (isFinalState()) {
      throw new FinalStateException("This is the final state");
    }
    if (!validateEdge(e))
      throw new InvalidEdgeException(e);
    Tree tree = new Tree(new State(_state), e, _combinations);

    return tree;
  }

  @Override
  public State getNodeValue() {
    return _state;
  }

  @Override
  public boolean isFinalState() {
    return _isFinalState;
  }

  /**
   * Returns all possible valid place options for the current player based the
   * current state of the game
   * 
   * @param playerName Name of the player caller of this method
   * @return all possible valid place options
   * @throws InvalidPlayerTurnException
   * @throws FinalStateException
   */
  protected List<PlaceRequest> getPlaceOptions(String playerName)
      throws InvalidPlayerTurnException, FinalStateException {
    if (isFinalState())
      throw new FinalStateException("This is the final state");
    if (!_state.getCurrentPlayerName().equals(playerName))
      throw new InvalidPlayerTurnException(playerName);

    return getPlaceOptions(_state.getCurrentPlayer());
  }

  @Override
  public boolean validateEdge(Edge e) throws FinalStateException {
    MyPlayerState playerState = _state.getCurrentPlayer();
    if (!getPlaceOptions(playerState).contains(e.getPlaceRequest()))
      return false;
    if (!getPurchaseOptions(e.getPlaceRequest()).contains(e.getBuyRequest()))
      return false;
    if (!getFutureTilesOptions().contains(e.getTileToDistribute()))
      return false;
    return true;
  }

  /**
   * Returns all the possible place options for the given Player State.
   * 
   * @param myState
   * @return
   * @throws FinalStateException
   */
  protected Set<Location> getFutureTilesOptions() throws FinalStateException {
    if (isFinalState())
      throw new FinalStateException("This is the final state");
    return _state.getAvailableTiles();
  }

  @Override
  public Cause getReasonForFinalState() throws NotFinalStateException {
    if (isFinalState())
      return _gameOverCause;
    throw new NotFinalStateException();
  }

  /**
   * Ask all the players if they want to keep the shares of the acquired hotels
   * and if they decide to sell then sells the share
   * 
   * @param acquired the list of acquired hotels
   * @param players all the players
   * @return the list of updated player states
   */
  protected List<PlayerState> askPlayerToKeepShare(List<HotelName> acquired,
      Map<String, Player> players) {
    for (Player player : players.values()) {
      List<Boolean> decisions = player.keep(acquired);
      _state.sellShare(player.getName(), acquired, decisions);
    }
    return new ArrayList<PlayerState>(_state.getPlayers());
  }

  /**
   * Check if the state of the hotel chains cause the end of the game.
   * 
   * @return True if all chains are safe or a chain has reached the maximum
   *         size. False if there are no hotel chains.
   */

  protected boolean doHotelsCauseGameOver() {
    Map<HotelName, HotelChainI> chains = _state.getBoard().getHotelChains();
    if (chains.isEmpty()) {
      return false;
    }
    boolean isSafe = true;
    for (HotelChainI chain : _state.getBoard().getHotelChains().values()) {
      if (chain.getSize() == HotelChainI.MAX_CHAIN_SIZE) {
        _gameOverCause = Cause.FINISHED;
        return true;
      }
      isSafe = isSafe && chain.isSafe();
    }

    if (isSafe)
      _gameOverCause = Cause.FINISHED;
    return isSafe;
  }

  /**
   * Identify whether the current state is a final state and the game has to end
   * 
   * @return true iff the current state is a final state
   */
  private boolean checkForFinalState() {
    return doesPlayerCountPreventGame() || doHotelsCauseGameOver()
        || checkForNoMove() || checkForNoMoreAvailableTiles();
  }

  /**
   * Check if a player can make a valid move.
   * 
   * @return True if the current player can't make a move
   */
  private boolean checkForNoMove() {
    boolean result = getPlaceOptions(_state.getCurrentPlayer()).isEmpty();
    if (result)
      _gameOverCause = Cause.PLAYER_CANT_PLAY_TILE;
    return result;
  }

  /**
   * Check if a player can make a valid move.
   * 
   * @return True if the current player can't make a move
   */
  private boolean checkForNoMoreAvailableTiles() {
    boolean result = _state.getAvailableTiles().isEmpty();
    if (result)
      _gameOverCause = Cause.NO_MORE_TILES;
    return result;
  }

  /**
   * Check if the number of players prevents the game from continuing.
   * 
   * @return True if there are one or fewer players.
   */
  protected boolean doesPlayerCountPreventGame() {
    boolean result = _state.getPlayers().size() <= 1;
    if (result)
      _gameOverCause = Cause.ONLY_PLAYER_LEFT;
    return _state.getPlayers().size() <= 1;
  }

  /**
   * Generates all the possible edges
   * 
   * @param node the Perspective
   * @return the list of all the possible Edges
   */
  protected List<Edge> getChildren(MyPlayerState myState) {
    List<Edge> edges = new ArrayList<Edge>();
    List<PlaceRequest> placeOptions = getPlaceOptions(myState);

    Set<Location> availableTiles = _state.getAvailableTiles();

    for (Location location : availableTiles) {
      for (PlaceRequest placeRequest : placeOptions) {
        List<PurchaseRequest> shareList = getPurchaseOptions(placeRequest);
        for (PurchaseRequest purchaseRequest : shareList) {
          edges.add(new Edge(location, purchaseRequest, placeRequest));
        }
        // edges.add(new Edge(location, new PurchaseRequest(), placeRequest));
      }

    }
    return edges;
  }

  /**
   * getAvailableShares Generates all the possible edges
   * 
   * @param node the Perspective
   * @return the list of all the possible Edges
   */
  protected List<PurchaseRequest> getPurchaseOptions(PlaceRequest placeRequest) {

    Map<HotelName, Integer> availableShares = getAvailableShares(_state
        .getBanker());

    List<PurchaseRequest> shareList = null;
    try {
      if (null == placeRequest) {
        shareList = filterAndCreateShareList(availableShares, _state);
      } else {
        shareList = filterAndCreateShareList(availableShares,
            handleMove(new State(_state), placeRequest));
      }

    } catch (ValidationException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER, ex.getMessage());
    }

    return shareList;
  }

  /**
   * Generates all the possible edges
   * 
   * @param node the Perspective
   * @return the list of all the possible Edges
   */
  protected List<PlaceRequest> getPlaceOptions(MyPlayerState myState) {

    BoardI board = _state.getBoard();

    Set<Location> validTiles = getValidTiles(myState, board);
    Set<HotelName> availableHotelNames = board.getAvailableHotelNames();
    List<PlaceRequest> placeRequests = new ArrayList<PlaceRequest>();

    for (Location tile : validTiles) {
      InspectResult result = board.inspect(tile);
      placeRequests.addAll(convertToPlaceRequest((PossibleResult) result,
          availableHotelNames));
    }

    return placeRequests;
  }

  /**
   * Creates all valid combinations of PlaceRequest based on the given
   * PossibleResult
   * 
   * @param result the output of the inspect
   * @param availableHotelNames
   * @return the valid list of PlaceRequest
   */
  protected List<PlaceRequest> convertToPlaceRequest(PossibleResult result,
      Set<HotelName> availableHotelNames) {
    List<PlaceRequest> requests = new ArrayList<PlaceRequest>();

    switch (result.getType()) {
      case SINGLETON:
        requests.add(new SingletonRequest(result.getLocation()));
        return requests;

      case FOUND:
        for (HotelName label : availableHotelNames) {
          requests.add(new FoundingRequest(result.getLocation(), label));
        }
        return requests;

      case GROW:
        requests.add(new GrowingRequest(result.getLocation()));
        return requests;

      case MERGE:
        MergeResult mergeResult = (MergeResult) result;
        for (HotelName label : mergeResult.getLargest()) {
          requests.add(new MergingRequest(result.getLocation(), label));
        }
        return requests;

      default:
        AcquireRuntimeException.logAndThrow(LOGGER,
            "Unexpected placement type: " + result.getType());

    }
    return null;
  }

  /**
   * Filters all the tiles which can't be placed
   * 
   * @param perspective The current state of the board
   * @return The set of valid tiles
   */
  protected Set<Location> getValidTiles(MyPlayerState mySelf, BoardI board) {
    Set<Location> validTiles = new HashSet<Location>();
    for (Location tile : mySelf.getTilesInHand()) {
      InspectResult result = board.inspect(tile);
      if (result.isValid()) {
        validTiles.add(tile);
      }
    }
    return validTiles;
  }

  /**
   * Finds all the available shares with respect to the current
   * PlayerPerspective
   * 
   * @param perspective the current state of the game
   * @return available shares as the mapping of hotel names with their
   *         respective number of available shares
   */
  protected Map<HotelName, Integer> getAvailableShares(BankerI banker) {
    Map<HotelName, Integer> availableShares = new HashMap<HotelName, Integer>();

    for (HotelName name : HotelName.values()) {
      int shares = banker.getAvailableShareCount(name);
      if (shares > 0) {
        availableShares.put(name, shares);
      }
    }

    return availableShares;
  }

  /**
   * Filters out all the impossible PurchaseRequests based on the current state
   * 
   * @param availableShares Map of available Shares
   * @param perspective The PlayerPerspective
   * @return the List of all the possible valid purchase requests
   */
  protected List<PurchaseRequest> filterAndCreateShareList(
      Map<HotelName, Integer> availableShares, State state) {
    List<PurchaseRequest> result = new ArrayList<PurchaseRequest>();
    for (PurchaseRequest purchaseRequest : _combinations) {
      Map<HotelName, Integer> availableShareMap = new HashMap<HotelName, Integer>(
          availableShares);
      if (purchaseRequest.getNames().isEmpty())
        continue;
      if (isValidPurchase(purchaseRequest, availableShareMap, state))
        result.add(purchaseRequest);

    }
    try {
      result.add(new PurchaseRequest());
    } catch (InvalidPurchaseRequestException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "We know this will not happen: ");
    }
    return result;
  }

  /**
   * Check if a given Purchase request is valid or not
   * 
   * @param purchaseRequest The given PurchaseRequest
   * @param perspective The current game state
   * @param availableShares the map of available share
   * @param board2
   * @return true if the given purchase request is valid
   */
  private boolean isValidPurchase(PurchaseRequest purchaseRequest,
      Map<HotelName, Integer> availableShares, State state) {
    BankerI banker = state.getBanker();
    PlayerStateI player = state.getCurrentPlayer();
    BoardI board = state.getBoard();
    int money = player.getMoney();

    for (HotelName label : purchaseRequest.getNames()) {

      // If no shares available for purchase
      if (null == availableShares.get(label) || availableShares.get(label) <= 0)
        return false;
      else {
        // Decrement the count each time
        availableShares.put(label, availableShares.get(label) - 1);
      }

      try {
        int sharePrice = banker.getSharePrice(label,
            board.getHotelChainSize(label));
        // Not sufficient money
        if (money < sharePrice)
          return false;
        // Subtract the money for each purchase
        money = money - sharePrice;
      } catch (InvalidSharePriceException ex) {
        return false;
      }

    }
    return true;
  }

  /**
   * 
   * @param node current state of the game
   * @param e move to execute
   * @return next state after executing the move
   * @throws NoAvailableTileException
   * @throws BadPlayerExecptiongetValidTiles
   */
  private State generateNextNode(State node, Edge e) {
    PlaceRequest placeRequest = e.getPlaceRequest();
    PurchaseRequest buyRequest = e.getBuyRequest();
    Location tileToDistribute = e.getTileToDistribute();
    try {

      node = handleMove(node, placeRequest);
    } catch (ValidationException ex) {
      AcquireRuntimeException.logAndReturn(LOGGER, "This is already validated",
          ex);
    }
    node = handlePurchase(buyRequest, node);
    try {
      node.nextTurn(tileToDistribute);
    } catch (NoAvailableTileException ex) {
      AcquireRuntimeException.logAndReturn(LOGGER, "This is already validated",
          ex);
    }
    return new State(node);
  }

  /**
   * Process the placement request from a player.
   * 
   * @param request Request to process.
   * @throws ValidationException Request is an invalid placement.
   */
  private State handleMove(State state, PlaceRequest request)
      throws ValidationException {
    switch (request.getTag()) {
      case SINGLETON:
      case GROW:
        state.place(request.getLocation(), null);
        break;
      case MERGE:
      case FOUND:
        HotelRequest hotelRequest = (HotelRequest) request;
        state.place(hotelRequest.getLocation(), hotelRequest.getName());
        break;
      default:
        AcquireRuntimeException.logAndThrow(LOGGER, "Unexpected request type: "
            + request.getTag());
        break;
    }
    return state;
  }

  /**
   * Process the purchase request from a player.
   * 
   * @param request Request to process.
   * @throws BadPlayerExecption Request is an invalid purchase.
   */
  private State handlePurchase(PurchaseRequest request, State state) {
    try {

      Map<HotelName, StockDescription> stocks = new HashMap<HotelName, StockDescription>();
      for (HotelName name : request.getNames()) {
        StockDescription description = stocks.get(name);
        if (description == null) {
          description = new StockDescription(name, 1);
        } else {
          description = description.changeCount(1);
        }
        stocks.put(name, description);
      }
      state.purchaseStock(stocks.values());
    } catch (InvalidStockCountException ex) {
      AcquireRuntimeException.logAndReturn(LOGGER,
          "count should never get bigger than PurchaseRequest.MAX_SHARES", ex);
    } catch (InvalidStockPurchaseException ex) {
      AcquireRuntimeException.logAndReturn(LOGGER,
          "count should never get bigger than PurchaseRequest.MAX_SHARES", ex);
      ;
    } catch (InvalidSharePriceException ex) {
      AcquireRuntimeException.logAndReturn(LOGGER,
          "count should never get bigger than PurchaseRequest.MAX_SHARES", ex);
      ;
    }
    return state;
  }
}
