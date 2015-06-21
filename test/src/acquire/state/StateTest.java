package acquire.state;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import util.Matchers;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.basic.StockDescription;
import acquire.board.Board;
import acquire.board.inspect.FoundingResult;
import acquire.board.inspect.ImposibleResult;
import acquire.enforcer.tile.TileSelectionModel;
import acquire.exception.AcquireException;
import acquire.exception.InvalidMoneyException;
import acquire.exception.InvalidPlayerNameException;
import acquire.exception.InvalidSharePriceException;
import acquire.exception.InvalidStockCountException;
import acquire.exception.InvalidTileSetException;
import acquire.exception.NoAvailableTileException;
import acquire.exception.validate.ValidationException;
import acquire.state.money.Banker;
import acquire.state.player.PlayerState;
import acquire.state.player.PlayerStateI;

public class StateTest {
  private static final String JAYANT = "Jayant";
  private static final String JJ = "JJ";
  private static final String PATRICK = "Patrick";
  private static final Location A1 = new Location(Row.A, Column.ONE);
  private State _state;
  private Queue<String> _playerNames;
  private Map<String, PlayerState> _playerStates;

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() throws Exception {
    _playerNames = new ArrayDeque<String>();
    _playerNames.add(JAYANT);
    _playerNames.add(JJ);
    _playerNames.add(PATRICK);

    _playerStates = new HashMap<String, PlayerState>();

    PlayerState pState = mock(PlayerState.class);
    Set<Location> tiles = mock(Set.class);
    when(tiles.isEmpty()).thenReturn(true);
    when(tiles.size()).thenReturn(PlayerStateI.MAX_TILES - 1);
    when(pState.getTilesInHand()).thenReturn(tiles);
    _playerStates.put(JAYANT, pState);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = NoAvailableTileException.class)
  public void testNoAvailableTiles() throws NoAvailableTileException,
      InvalidPlayerNameException {
    TileSelectionModel selectionModel = mock(TileSelectionModel.class);
    when(
        selectionModel.selectTile((Set<Location>) argThat(Matchers
            .any(Collection.class)))).thenThrow(new NoAvailableTileException());
    _state = new State(mock(Board.class), _playerNames, _playerStates,
        selectionModel);
    _state.nextTurn();
  }

  @Test
  public void testNextTurn() throws InvalidMoneyException,
      InvalidTileSetException, InvalidPlayerNameException,
      NoAvailableTileException {

    PlayerState player1 = new PlayerState(JAYANT, 6000, new HashSet<Location>());
    PlayerState player2 = new PlayerState(JJ, 6000, new HashSet<Location>());
    PlayerState player3 = new PlayerState(PATRICK, 6000,
        new HashSet<Location>());

    Map<String, PlayerState> playersMap = new HashMap<String, PlayerState>();
    playersMap.put(JAYANT, player1);
    playersMap.put(JJ, player2);
    playersMap.put(PATRICK, player3);
    _state = new State(new Board(), _playerNames, playersMap,
        mock(TileSelectionModel.class));

    _state.nextTurn();
    PlayerState currentPlayer = _state.getCurrentPlayer();
    assertThat(currentPlayer, is(player2));
    assertThat(_state.getPlayers(),
        containsInAnyOrder(player1, player2, player3));
  }

  @Test
  public void testKickPlayer() throws AcquireException {
    StockDescription stock = new StockDescription(HotelName.AMERICAN, 1);

    PlayerState player1 = new PlayerState(JAYANT, 6000, new HashSet<Location>(
        Arrays.asList(A1)));
    player1.setStock(stock);
    PlayerState player2 = new PlayerState(JJ, 6000, new HashSet<Location>());
    PlayerState player3 = new PlayerState(PATRICK, 6000,
        new HashSet<Location>());

    Map<String, PlayerState> playersMap = new HashMap<String, PlayerState>();
    playersMap.put(JAYANT, player1);
    playersMap.put(JJ, player2);
    playersMap.put(PATRICK, player3);

    Banker banker = mock(Banker.class);
    _state = new State(new Board(), _playerNames, playersMap, banker,
        mock(TileSelectionModel.class));

    assertThat(_state.getAvailableTiles(), not(hasItem(A1)));

    _state.kickCurrentPlayer();

    List<PlayerState> players = _state.getPlayers();
    assertThat(_playerNames, hasSize(2));
    assertThat(_playerNames, not(contains(JAYANT)));
    assertThat(players, hasSize(2));
    assertThat(players, not(contains(player1)));
    // assertThat(_state.getAvailableTiles(), hasItem(A1));
    // verify(banker).returnStocksToBank(stock);
  }

  @Test
  public void testFoundNoSharesLeft() throws AcquireException {
    PlayerState player1 = new PlayerState(JAYANT, 6000, new HashSet<Location>(
        Arrays.asList(A1)));

    Map<String, PlayerState> playersMap = new HashMap<String, PlayerState>();
    playersMap.put(JAYANT, player1);

    Banker banker = mock(Banker.class);
    when(banker.getAvailableShareCount(HotelName.AMERICAN)).thenReturn(0);

    FoundingResult result = new FoundingResult(A1, HotelName.AMERICAN,
        new HashSet<Location>());

    Board board = mock(Board.class);
    when(board.inspect(A1)).thenReturn(result);

    _state = new State(board, _playerNames, playersMap, banker,
        mock(TileSelectionModel.class));

    _state.place(A1, HotelName.AMERICAN);

    PlayerState currentPlayer = _state.getCurrentPlayer();
    assertThat(currentPlayer.getStock(HotelName.AMERICAN).getCount(), is(0));
    verify(board).addHotel(HotelName.AMERICAN, A1);
  }

  @Test(expected = ValidationException.class)
  public void testImpossiblePlacement() throws AcquireException {
    PlayerState player1 = new PlayerState(JAYANT, 6000, new HashSet<Location>(
        Arrays.asList(A1)));

    Map<String, PlayerState> playersMap = new HashMap<String, PlayerState>();
    playersMap.put(JAYANT, player1);

    Banker banker = mock(Banker.class);
    when(banker.getAvailableShareCount(HotelName.AMERICAN)).thenReturn(0);

    ImposibleResult result = new ImposibleResult("testing");

    Board board = mock(Board.class);
    when(board.inspect(A1)).thenReturn(result);

    _state = new State(board, _playerNames, playersMap, banker,
        mock(TileSelectionModel.class));

    _state.place(A1, HotelName.AMERICAN);
  }

  @Test
  public void testSellShare() throws InvalidMoneyException,
      InvalidTileSetException, InvalidPlayerNameException,
      InvalidStockCountException, InvalidSharePriceException {

    PlayerState player1 = new PlayerState(JAYANT, 6000, new HashSet<Location>(
        Arrays.asList(A1)));
    StockDescription stockDescription = new StockDescription(
        HotelName.AMERICAN, 10);
    player1.setStock(stockDescription);

    Map<String, PlayerState> playersMap = new HashMap<String, PlayerState>();
    playersMap.put(JAYANT, player1);
    Board board = mock(Board.class);
    Banker banker = mock(Banker.class);
    when(banker.getSharePrice(HotelName.AMERICAN, 0)).thenReturn(200);
    _state = new State(board, _playerNames, playersMap, banker,
        mock(TileSelectionModel.class));

    _state.sellShare(JAYANT, Arrays.asList(HotelName.AMERICAN),
        Arrays.asList(true));
    PlayerState playerState = _state.getPlayers().get(0);
    assertThat(playerState.getStock(HotelName.AMERICAN).getCount(), is(0));
    assertThat(playerState.getMoney(), is(6000 + (200 * 10)));
  }
}
