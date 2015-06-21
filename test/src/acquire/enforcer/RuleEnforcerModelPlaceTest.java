package acquire.enforcer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import acquire.basic.Action.PlacementType;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.exception.validate.ValidationException;
import acquire.player.Player;
import acquire.player.StubPlayer;
import acquire.protocol.request.HotelRequest;
import acquire.protocol.request.PlaceRequest;
import acquire.state.State;
import acquire.state.player.PlayerState;

@RunWith(Parameterized.class)
public class RuleEnforcerModelPlaceTest {
  private static final Location A1 = new Location(Row.A, Column.ONE);
  private final Location _location;
  private final HotelName _name;
  private final PlacementType _tag;
  private Player _player;
  private PlayerState _playerState;
  private PlaceRequest _placeRequest;
  private State _state;
  private RuleEnforcerModel _model;

  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[] { A1, null, PlacementType.SINGLETON },
        new Object[] { A1, null, PlacementType.GROW }, new Object[] { A1,
            HotelName.AMERICAN, PlacementType.FOUND }, new Object[] { A1,
            HotelName.AMERICAN, PlacementType.MERGE });
  }

  public RuleEnforcerModelPlaceTest(Location location, HotelName name,
      PlacementType tag) {
    _location = location;
    _name = name;
    _tag = tag;
  }

  @Before
  public void setUp() throws Exception {
    _playerState = mock(PlayerState.class);
    _state = mock(State.class);

    _player = new StubPlayer();

    if (_name == null) {
      _placeRequest = mock(PlaceRequest.class);
      when(_placeRequest.getTag()).thenReturn(_tag);
      when(_placeRequest.getLocation()).thenReturn(_location);
    } else {
      HotelRequest request = mock(HotelRequest.class);
      when(request.getName()).thenReturn(_name);
      _placeRequest = new HotelRequest(_location, _name) {
        @Override
        public PlacementType getTag() {
          return _tag;
        }
      };
    }

    _model = new RuleEnforcerModel(_state, 0);
  }

  // @Test
  // public void testDoPlayerMove() throws AcquireException {
  // _model.doPlayerMove(_playerState, _player);
  //
  // verify(_state).place(_location, _name);
  // }

  @Test
  public void testHandleMove() throws ValidationException {
    _model.handleMove(_placeRequest);

    verify(_state).place(_location, _name);
  }

}
