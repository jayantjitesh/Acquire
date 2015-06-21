package acquire.enforcer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.Matchers.any;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import acquire.basic.HotelChainI;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.board.Board;
import acquire.board.inspect.InspectResult;
import acquire.exception.AcquireException;
import acquire.exception.NoAvailableTileException;
import acquire.player.Player;
import acquire.protocol.TurnI;
import acquire.protocol.request.PlaceRequest;
import acquire.protocol.request.PurchaseRequest;
import acquire.protocol.request.TurnResponse;
import acquire.state.State;
import acquire.state.player.PlayerState;

public class RuleEnforcerModelTest {
  private static final String NAME = "Patrick";
  private static final Location A1 = new Location(Row.A, Column.ONE);
  private static final Location A2 = new Location(Row.A, Column.TWO);
  private static final int TURNS = 4;
  private Set<Location> _locations;
  private Player _player;
  private PlayerState _playerState;
  private PlaceRequest _placeRequest;
  private PurchaseRequest _purchaseRequest;
  private State _state;
  private Board _board;
  private InspectResult _inspectResult;
  private RuleEnforcerModel _model;

  @Before
  public void setUp() throws Exception {
    _locations = new HashSet<Location>(Arrays.asList(A1, A2));

    _playerState = mock(PlayerState.class);
    _placeRequest = mock(PlaceRequest.class);
    _purchaseRequest = mock(PurchaseRequest.class);
    TurnResponse turn = new TurnResponse(_placeRequest, _purchaseRequest);
    _player = mock(Player.class);
    when(_player.getName()).thenReturn(NAME);
    when(_player.takeTurn(argThat(any(TurnI.class)))).thenReturn(turn);
    // when(_player.getPurchaseRequest(argThat(any(PlayerPerspective.class))))
    // .thenReturn(_purchaseRequest);

    _inspectResult = mock(InspectResult.class);
    _board = mock(Board.class);
    when(_board.inspect(argThat(any(Location.class)))).thenReturn(
        _inspectResult);

    _state = mock(State.class);
    when(_state.getCurrentPlayer()).thenReturn(_playerState);
    when(_state.getCurrentPlayerName()).thenReturn(NAME);
    when(_state.getBoard()).thenReturn(_board);

    _model = new RuleEnforcerModel(_state, 0);
  }

  @Test
  public void testGetCurrentPlayer() {
    assertThat(_model.getCurrentPlayer(), is(_playerState));
  }

  @Test
  public void testCanPlayerMakeMove() {
    when(_playerState.getTilesInHand()).thenReturn(_locations);
    when(_inspectResult.isValid()).thenReturn(true);

    assertThat(_model.canPlayerMakeMove(_playerState), is(true));
  }

  @Test
  public void testCanPlayerMakeMoveAllInvalid() {
    when(_playerState.getTilesInHand()).thenReturn(_locations);
    when(_inspectResult.isValid()).thenReturn(false);

    assertThat(_model.canPlayerMakeMove(_playerState), is(false));
  }

  @Test
  public void testCanPlayerMakeMoveOneInvalid() {
    when(_playerState.getTilesInHand()).thenReturn(_locations);
    when(_inspectResult.isValid()).thenReturn(false, true);

    assertThat(_model.canPlayerMakeMove(_playerState), is(true));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testHandlePurchase() throws AcquireException {
    _model.handlePurchase(_purchaseRequest);

    verify(_state).purchaseStock(argThat(any(Collection.class)));
  }

  @Test
  public void testShouldStopGameNoTurnsNoChains() {
    when(_state.getPlayers()).thenReturn(
        Arrays.asList(_playerState, _playerState));
    _model = new RuleEnforcerModel(_state, TURNS);
    assertThat(_model.shouldStopGame(), is(false));
  }

  @Test
  public void testShouldStopGameMaxTurnReached()
      throws NoAvailableTileException {
    _model = new RuleEnforcerModel(_state, TURNS);
    for (int i = 0; i < TURNS; i++) {
      _model.nextTurn();
    }
    assertThat(_model.shouldStopGame(), is(true));
  }

  @Test
  public void testIsGameOverNoPlayer() {
    when(_state.getPlayers()).thenReturn(new ArrayList<PlayerState>());
    Map<HotelName, HotelChainI> chains = new HashMap<HotelName, HotelChainI>();
    when(_board.getHotelChains()).thenReturn(chains);
    _model = new RuleEnforcerModel(_state, TURNS);
    assertThat(_model.isGameOver(), is(true));
  }

  @Test
  public void testIsGameOverOnePlayer() {
    when(_state.getPlayers()).thenReturn(Arrays.asList(_playerState));
    Map<HotelName, HotelChainI> chains = new HashMap<HotelName, HotelChainI>();
    when(_board.getHotelChains()).thenReturn(chains);
    _model = new RuleEnforcerModel(_state, TURNS);
    assertThat(_model.isGameOver(), is(true));
  }

  @Test
  public void testIsGameOverNoChains() {
    when(_state.getPlayers()).thenReturn(
        Arrays.asList(_playerState, _playerState));
    Map<HotelName, HotelChainI> chains = new HashMap<HotelName, HotelChainI>();
    when(_board.getHotelChains()).thenReturn(chains);
    _model = new RuleEnforcerModel(_state, TURNS);
    assertThat(_model.isGameOver(), is(false));
  }

  @Test
  public void testIsGameOverOnlySafeChains() {
    HotelChainI chain = mock(HotelChainI.class);
    when(chain.isSafe()).thenReturn(true);
    Map<HotelName, HotelChainI> chains = new HashMap<HotelName, HotelChainI>();
    chains.put(HotelName.AMERICAN, chain);

    when(_board.getHotelChains()).thenReturn(chains);
    _model = new RuleEnforcerModel(_state, TURNS);
    assertThat(_model.isGameOver(), is(true));
  }

  @Test
  public void testIsGameOverMultiOnlySafeChains() {
    HotelChainI chain = mock(HotelChainI.class);
    when(chain.isSafe()).thenReturn(true);
    Map<HotelName, HotelChainI> chains = new HashMap<HotelName, HotelChainI>();
    chains.put(HotelName.AMERICAN, chain);
    chains.put(HotelName.TOWER, chain);

    when(_board.getHotelChains()).thenReturn(chains);
    _model = new RuleEnforcerModel(_state, TURNS);
    assertThat(_model.isGameOver(), is(true));
  }

  @Test
  public void testIsGameOverOneSafeChain() {
    when(_state.getPlayers()).thenReturn(
        Arrays.asList(_playerState, _playerState));
    HotelChainI chain = mock(HotelChainI.class);
    when(chain.isSafe()).thenReturn(false);
    Map<HotelName, HotelChainI> chains = new HashMap<HotelName, HotelChainI>();
    chains.put(HotelName.AMERICAN, chain);
    chain = mock(HotelChainI.class);
    when(chain.isSafe()).thenReturn(true);
    chains.put(HotelName.TOWER, chain);

    when(_board.getHotelChains()).thenReturn(chains);
    _model = new RuleEnforcerModel(_state, TURNS);
    assertThat(_model.isGameOver(), is(false));
  }

  @Test
  public void testIsGameOverNonSafeChains() {
    when(_state.getPlayers()).thenReturn(
        Arrays.asList(_playerState, _playerState));
    HotelChainI chain = mock(HotelChainI.class);
    when(chain.isSafe()).thenReturn(false);
    Map<HotelName, HotelChainI> chains = new HashMap<HotelName, HotelChainI>();
    chains.put(HotelName.AMERICAN, chain);

    when(_board.getHotelChains()).thenReturn(chains);
    _model = new RuleEnforcerModel(_state, TURNS);
    assertThat(_model.isGameOver(), is(false));
  }

  @Test
  public void testIsGameOverMaxChainSize() {
    HotelChainI chain = mock(HotelChainI.class);
    when(chain.isSafe()).thenReturn(false);
    when(chain.getSize()).thenReturn(HotelChainI.MAX_CHAIN_SIZE);
    Map<HotelName, HotelChainI> chains = new HashMap<HotelName, HotelChainI>();
    chains.put(HotelName.AMERICAN, chain);

    when(_board.getHotelChains()).thenReturn(chains);
    _model = new RuleEnforcerModel(_state, TURNS);
    assertThat(_model.isGameOver(), is(true));
  }

  @Test
  public void testNextTurn() throws NoAvailableTileException {
    _model.nextTurn();
    verify(_state).nextTurn();
    assertThat(_model.getTurnsExecuted(), is(1));
  }

  @Test
  public void testNextThreeTurn() throws NoAvailableTileException {
    for (int i = 0; i < TURNS; i++) {
      _model.nextTurn();
    }
    assertThat(_model.getTurnsExecuted(), is(TURNS));
  }

  @Test
  public void testKickCurrentPlayer() throws NoAvailableTileException {
    _model.kickCurrentPlayer();

    verify(_state).kickCurrentPlayer();
  }
}
