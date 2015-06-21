package acquire.game.tree;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static util.Matchers.any;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import acquire.basic.Action.PlacementType;
import acquire.basic.HotelChainI;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.basic.StockDescription;
import acquire.board.Board;
import acquire.board.Spot;
import acquire.board.inspect.FoundingResult;
import acquire.enforcer.RuleEnforcerBuilder;
import acquire.enforcer.tile.TileSelectionModel;
import acquire.exception.BadPlayerExecption;
import acquire.exception.InvalidPurchaseRequestException;
import acquire.game.tree.exception.FinalStateException;
import acquire.game.tree.exception.InvalidEdgeException;
import acquire.game.tree.exception.InvalidPlayerTurnException;
import acquire.protocol.request.PlaceRequest;
import acquire.protocol.request.PurchaseRequest;
import acquire.state.State;
import acquire.state.money.Banker;
import acquire.state.player.PlayerState;
import acquire.state.player.PlayerStateI;

public class PlayerTreeTest {
  private PlayerTree _tree;
  private State _state;
  protected static final Location D10 = new Location(Row.D, Column.TEN);
  private static final String JJ = "JJ";
  private static final String NISHANT = "NISHANT";
  private static Location A1 = new Location(Row.A, Column.ONE);
  private static Location A2 = new Location(Row.A, Column.TWO);
  private static Location A3 = new Location(Row.A, Column.THREE);
  private static Location A4 = new Location(Row.A, Column.FOUR);
  private static Location A5 = new Location(Row.A, Column.FIVE);
  private static Location A6 = new Location(Row.A, Column.SIX);
  private TileSelectionModel _selectionModel;
  private List<PlayerState> _players;
  private PlayerState playerState1;
  private PlayerState playerState2;
  private Set<Location> availableTiles;
  private Board board;

  @Before
  public void setUp() throws Exception {
    _state = mock(State.class);
    _selectionModel = mock(TileSelectionModel.class);
    availableTiles = new HashSet<Location>();
    availableTiles.add(D10);
    _players = new ArrayList<PlayerState>();
    playerState1 = mock(PlayerState.class);
    playerState2 = mock(PlayerState.class);
    _players.add(playerState1);
    _players.add(playerState2);
    Set<Location> locations = new HashSet<Location>(Arrays.asList(A1, A2, A3,
        A4, A5, A6));
    when(_selectionModel.selectTile(locations)).thenReturn(A1, A2, A3, A4, A5,
        A6);
    PlayerState pState = RuleEnforcerBuilder.makeInitialPlayer("JJ", locations,
        _selectionModel);
    when(_state.getAvailableTiles()).thenReturn(availableTiles);

    when(_state.getCurrentPlayer()).thenReturn(pState);

    when(_state.getPlayers()).thenReturn(_players);

    board = mock(Board.class);
    when(_state.getBoard()).thenReturn(board);
    FoundingResult result = new FoundingResult(A1, HotelName.AMERICAN,
        new HashSet<Location>());
    when(board.inspect(A1)).thenReturn(result);
    when(board.inspect(A2)).thenReturn(result);
    when(board.inspect(A3)).thenReturn(result);
    when(board.inspect(A4)).thenReturn(result);
    when(board.inspect(A5)).thenReturn(result);
    when(board.inspect(A6)).thenReturn(result);

    when(board.getAvailableHotelNames()).thenReturn(
        new HashSet<HotelName>(Arrays.asList(HotelName.values())));

    _tree = TreeGenerator.generatePlayerTree(_state);
  }

  @Test
  public void testGetQueryResult() throws BadPlayerExecption,
      InvalidPlayerTurnException, InvalidEdgeException, FinalStateException,
      InvalidPurchaseRequestException {
    when(_state.getCurrentPlayerName()).thenReturn(JJ);
    ArrayList<PurchaseRequest> orders = new ArrayList<PurchaseRequest>();
    orders.add(new PurchaseRequest());
    Banker banker = mock(Banker.class);
    when(_state.getBanker()).thenReturn(banker);
    when(banker.getAvailableStocks()).thenReturn(
        new HashMap<HotelChainI.HotelName, StockDescription>());
    when(banker.getAvailableShareCount(argThat(any(HotelName.class))))
        .thenReturn(10);
    assertThat(_tree.getQueryResult(1, PlacementType.FOUND, orders),
        is(HotelName.values().length * PlayerStateI.MAX_TILES));
  }

  @Test
  public void testGetMyPurchaseOptions() {
    Banker banker = mock(Banker.class);
    when(_state.getBanker()).thenReturn(banker);
    when(banker.getAvailableStocks()).thenReturn(
        new HashMap<HotelChainI.HotelName, StockDescription>());
    when(banker.getAvailableShareCount(argThat(any(HotelName.class))))
        .thenReturn(10);

    List<PurchaseRequest> purchaseRequests = _tree.getPurchaseOptions(null);
    assertThat(purchaseRequests.size(), is(15));
  }

  @Test(expected = InvalidPlayerTurnException.class)
  public void testGetMyPlaceOptionsNotMyTurn()
      throws InvalidPlayerTurnException, FinalStateException {
    when(_state.getPlayersName()).thenReturn(new ArrayDeque<String>());
    when(board.getSpots()).thenReturn(new HashMap<Location, Spot>());
    when(_state.getCurrentPlayerName()).thenReturn(JJ);
    _tree.getMyPlaceOptions(NISHANT);
  }

  @Test
  public void testGetMyPlaceOptions() throws InvalidPlayerTurnException,
      FinalStateException {
    when(_state.getCurrentPlayerName()).thenReturn(JJ);
    List<PlaceRequest> placeOptions = _tree.getMyPlaceOptions(JJ);
    assertThat(placeOptions.size(), is(HotelName.values().length
        * PlayerStateI.MAX_TILES));
  }
}
