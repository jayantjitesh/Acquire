package acquire.enforcer.listener;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import acquire.basic.Action.ActionType;
import acquire.basic.Action.PlacementType;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.enforcer.PublicRuleEnforcer;
import acquire.enforcer.RuleEnforcerModelTree;
import acquire.player.Player;
import acquire.player.StubPlayer;
import acquire.protocol.description.ActionDescription;
import acquire.protocol.request.PlaceRequest;
import acquire.protocol.request.PurchaseRequest;
import acquire.state.State;
import acquire.state.StateI;
import acquire.state.player.PlayerState;

public class ActionOccurredInformerTest {
  private static final String NAME = "Patrick";
  private static final Location A1 = new Location(Row.A, Column.ONE);
  private RuleEnforcerModelTree _model;
  private ActionPLayer _player;
  private State _state;
  private PlayerState _playerState;
  private PlaceRequest _placeRequest;
  private PurchaseRequest _purchaseRequest;
  private PublicRuleEnforcer _enforcer;
  private Map<String, Player> _players;

  @Before
  public void setUp() throws Exception {
    _placeRequest = mock(PlaceRequest.class);
    when(_placeRequest.getTag()).thenReturn(PlacementType.SINGLETON);
    when(_placeRequest.getLocation()).thenReturn(A1);
    _purchaseRequest = mock(PurchaseRequest.class);
    when(_purchaseRequest.getNames()).thenReturn(new ArrayList<HotelName>());

    _playerState = mock(PlayerState.class);
    when(_playerState.getName()).thenReturn(NAME);

    _state = mock(State.class);
    when(_state.getPlayers()).thenReturn(Arrays.asList(_playerState));
    when(_state.getCurrentPlayer()).thenReturn(_playerState);

    _model = spy(new RuleEnforcerModelTree(_state,
        RuleEnforcerModelTree.NO_MAX_TURN));
    // doReturn(true).when(_model).canPlayerMakeMove(_playerState);

    _player = new ActionPLayer();
    _players = new HashMap<String, Player>();
    _players.put(NAME, _player);
    // _enforcer = new PublicRuleEnforcer(_model, _players);
  }

  // @Test
  // public void testNormalTurnEvents() throws BadPlayerExecption {
  // _enforcer.doPlayerTurn(_playerState);
  //
  // for (ActionType type : Arrays.asList(ActionType.SINGLETON,
  // ActionType.PURCHASE, ActionType.END_TURN)) {
  // ActionDescription description = _player._descriptions.get(type);
  // assertThat(type.toString(), description, not(nullValue()));
  // assertThat(type.toString(), description.getPlayer(),
  // is((PlayerStateI) _playerState));
  // }
  // }

  private class ActionPLayer extends StubPlayer {
    Map<ActionType, ActionDescription> _descriptions = new HashMap<ActionType, ActionDescription>();

    @Override
    public String getName() {
      return NAME;
    }

    @Override
    public void actionOccured(StateI state, ActionDescription action) {
      ActionType actionType = action.getType();
      if (_descriptions.containsKey(actionType)) {
        throw new RuntimeException("Multiple actions for type: " + actionType);
      }
      _descriptions.put(actionType, action);
    }
  }

}
