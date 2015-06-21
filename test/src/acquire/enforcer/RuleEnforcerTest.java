package acquire.enforcer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.Matchers.any;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.verification.VerificationMode;
import acquire.exception.AcquireException;
import acquire.exception.BadPlayerExecption;
import acquire.exception.NoAvailableTileException;
import acquire.player.Player;
import acquire.protocol.TurnI;
import acquire.protocol.request.PlaceRequest;
import acquire.protocol.request.PurchaseRequest;
import acquire.protocol.request.TurnResponse;
import acquire.state.player.PlayerState;

public class RuleEnforcerTest {
  private static final String NAME = "Patrick";
  private RuleEnforcerModel _model;
  private Player _player;
  private PlayerState _playerState;
  private PlaceRequest _placeRequest;
  private PurchaseRequest _purchaseRequest;
  private RuleEnforcer _enforcer;
  private Map<String, Player> _players;

  @Before
  public void setUp() throws Exception {
    _placeRequest = mock(PlaceRequest.class);
    _purchaseRequest = mock(PurchaseRequest.class);
    TurnResponse turn = new TurnResponse(_placeRequest, _purchaseRequest);
    _player = mock(Player.class);
    when(_player.getName()).thenReturn(NAME);
    when(_player.takeTurn(argThat(any(TurnI.class)))).thenReturn(turn);

    _playerState = mock(PlayerState.class);
    when(_playerState.getName()).thenReturn(NAME);

    _model = mock(RuleEnforcerModel.class);
    when(_model.getCurrentPlayer()).thenReturn(_playerState);

    _players = new HashMap<String, Player>();
    _players.put(NAME, _player);
    _enforcer = new RuleEnforcer(_model, _players);
  }

  @Test
  public void testRunGameStopNow() throws AcquireException {
    when(_model.shouldStopGame()).thenReturn(true);

    _enforcer.runGame();

    verify(_model).shouldStopGame();
    verifyPlayeTurn(false, false, false, false);
  }

  // @Test
  // public void testRunGameOneRun() throws AcquireException {
  // when(_model.shouldStopGame()).thenReturn(false, true);
  // when(_model.canPlayerMakeMove(_playerState)).thenReturn(true);
  //
  // _enforcer.runGame();
  //
  // verify(_model, times(2)).shouldStopGame();
  // verifyPlayeTurn();
  // }

  @Test
  public void testRunGameBadMove() throws AcquireException {
    when(_model.shouldStopGame()).thenReturn(false, true);
    when(_model.canPlayerMakeMove(_playerState)).thenReturn(true);
    doThrow(new BadPlayerExecption(NAME, null)).when(_model).doPlayerMove(
        _playerState, _player);

    _enforcer.runGame();

    verify(_model, times(2)).shouldStopGame();
    assertThat(_enforcer.getPlayers().values(), hasSize(0));
  }

  @Test
  public void testRunGameBadPurchase() throws AcquireException {
    when(_model.shouldStopGame()).thenReturn(false, true);
    when(_model.canPlayerMakeMove(_playerState)).thenReturn(true);
    doThrow(new BadPlayerExecption(NAME, null)).when(_model).doPlayerPurchase(
        _playerState, _player);

    _enforcer.runGame();

    verify(_model, times(2)).shouldStopGame();
    assertThat(_enforcer.getPlayers().values(), hasSize(0));
  }

  @Test
  public void testDoPlayerTurn() throws AcquireException {
    when(_model.canPlayerMakeMove(_playerState)).thenReturn(true);

    assertThat(_enforcer.doPlayerTurn(_playerState), is(true));

    verifyPlayeTurn();
  }

  @Test
  public void testDoPlayerTurnNoPlayerMoves() throws AcquireException {
    when(_model.canPlayerMakeMove(_playerState)).thenReturn(false);

    assertThat(_enforcer.doPlayerTurn(_playerState), is(false));

    verifyPlayeTurn(true, false, false, false);
  }

  @Test(expected = BadPlayerExecption.class)
  public void testDoPlayerTurnBadPlayerMoves() throws AcquireException {
    when(_model.canPlayerMakeMove(_playerState)).thenReturn(true);
    doThrow(new BadPlayerExecption(NAME, null)).when(_model).doPlayerMove(
        _playerState, _player);

    assertThat(_enforcer.doPlayerTurn(_playerState), is(false));
  }

  // @Test(expected = BadPlayerExecption.class)
  // public void testDoPlayerTurnBadPlayerPurchase() throws AcquireException {
  // when(_model.canPlayerMakeMove(_playerState)).thenReturn(true);
  // doThrow(new BadPlayerExecption(NAME, null)).when(_model).doPlayerPurchase(
  // _playerState, _player);
  //
  // assertThat(_enforcer.doPlayerTurn(_playerState), is(false));
  // }

  @Test
  public void testDoPlayerTurnNONextTurn() throws AcquireException {
    when(_model.canPlayerMakeMove(_playerState)).thenReturn(true);
    doThrow(new NoAvailableTileException()).when(_model).nextTurn();

    assertThat(_enforcer.doPlayerTurn(_playerState), is(false));

    verifyPlayeTurn();
  }

  private void verifyPlayeTurn() throws AcquireException {
    verifyPlayeTurn(true, true, true, true);
  }

  private void verifyPlayeTurn(boolean makeMove, boolean move,
      boolean purchase, boolean nextTurn) throws AcquireException {
    verify(_model, getVerificationCount(makeMove)).canPlayerMakeMove(
        _playerState);
    verify(_model, getVerificationCount(move)).doPlayerMove(_playerState,
        _player);
    verify(_model, getVerificationCount(purchase)).doPlayerPurchase(
        _playerState, _player);
    verify(_model, getVerificationCount(nextTurn)).nextTurn();
  }

  private VerificationMode getVerificationCount(boolean occured) {
    return occured ? times(1) : never();
  }
}
