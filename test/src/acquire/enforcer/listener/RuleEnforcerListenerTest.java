package acquire.enforcer.listener;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import acquire.basic.Action.ActionType;
import acquire.basic.Action.PlacementType;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.enforcer.RuleEnforcerModel;
import acquire.exception.NoAvailableTileException;
import acquire.player.Player;
import acquire.player.StubPlayer;
import acquire.protocol.description.ActionDescription;
import acquire.protocol.request.PlaceRequest;
import acquire.protocol.request.PurchaseRequest;
import acquire.state.State;
import acquire.state.perspective.EndPerspective;
import acquire.state.player.PlayerState;

public class RuleEnforcerListenerTest {
  private static final Location A1 = new Location(Row.A, Column.ONE);
  private static final int TURNS = 0;
  private State _state;
  private StateChangeListener _listener;
  private RuleEnforcerModel _model;
  private PlayerState _playerState;
  private Player _player;
  private PlaceRequest _placeRequest;
  private PurchaseRequest _purchaseRequest;

  @Before
  public void setUp() throws Exception {
    _playerState = mock(PlayerState.class);
    _state = mock(State.class);
    when(_state.getCurrentPlayer()).thenReturn(_playerState);

    _listener = mock(StateChangeListener.class);

    _player = new StubPlayer();
    _placeRequest = mock(PlaceRequest.class);
    when(_placeRequest.getTag()).thenReturn(PlacementType.SINGLETON);
    when(_placeRequest.getLocation()).thenReturn(A1);

    _purchaseRequest = mock(PurchaseRequest.class);
    when(_purchaseRequest.getNames()).thenReturn(
        Arrays.asList(HotelName.AMERICAN));

    _model = new RuleEnforcerModel(_state, TURNS);
    _model.addStateChangeListener(_listener);
  }

  // @Test
  // public void testDoPlayerMove() throws AcquireException {
  // _model.doPlayerMove(_playerState, _player);
  // verify(_listener)
  // .stateChanged(_state,
  // new ActionDescription(ActionType.SINGLETON, _playerState), TURNS,
  // TURNS);
  // }

  // @Test
  // public void testDoPlayerPurchase() throws AcquireException {
  // _model.doPlayerPurchase(_playerState, _player);
  // verify(_listener).stateChanged(_state,
  // new ActionDescription(ActionType.PURCHASE, _playerState), TURNS, TURNS);
  // }

  @Test
  public void testNextTurn() throws NoAvailableTileException {
    _model.nextTurn();
    verify(_listener).stateChanged(_state,
        new ActionDescription(ActionType.END_TURN, _playerState), TURNS + 1,
        TURNS);
  }

  @Test
  public void testKickCurrentPlayer() throws NoAvailableTileException {
    _model.kickCurrentPlayer();
    verify(_listener).stateChanged(_state,
        new ActionDescription(ActionType.KICK, _playerState), TURNS, TURNS);
  }

  @Test
  public void testEndGame() throws NoAvailableTileException {
    EndPerspective endPerspective = _model.signalEndgame();

    verify(_listener).gameOver(endPerspective);
  }

}
