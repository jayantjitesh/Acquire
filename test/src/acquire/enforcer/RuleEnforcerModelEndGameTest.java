package acquire.enforcer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import acquire.board.BoardI;
import acquire.enforcer.listener.StateChangeListener;
import acquire.state.State;
import acquire.state.money.BankerI;
import acquire.state.perspective.EndPerspective;
import acquire.state.perspective.EndPerspective.Cause;
import acquire.state.player.PlayerState;

@RunWith(Parameterized.class)
public class RuleEnforcerModelEndGameTest {
  private final Cause _cause;
  private State _state;
  private BoardI _board;
  private RuleEnforcerModel _model;
  private List<PlayerState> _players;
  private BankerI _banker;

  @Parameters
  public static Collection<Object[]> data() {
    List<Object[]> data = new ArrayList<Object[]>();
    for (Cause cause : Cause.values()) {
      data.add(new Object[] { cause });
    }
    return data;
  }

  public RuleEnforcerModelEndGameTest(Cause cause) {
    _cause = cause;
  }

  @Before
  public void setUp() throws Exception {
    _players = Arrays.asList(mock(PlayerState.class), mock(PlayerState.class));

    _board = mock(BoardI.class);

    _state = mock(State.class);
    when(_state.getPlayers()).thenReturn(_players);
    when(_state.getBoard()).thenReturn(_board);
    when(_state.getBanker()).thenReturn(_banker);

    _model = spy(new RuleEnforcerModel(_state, RuleEnforcerModel.NO_MAX_TURN));
    switch (_cause) {
      case FINISHED:
        doReturn(true).when(_model).canPlayerMakeMove(any(PlayerState.class));
        doReturn(true).when(_model).doHotelsCauseGameOver();
        break;
      case MAX_TURNS:
        doReturn(true).when(_model).canPlayerMakeMove(any(PlayerState.class));
        doReturn(true).when(_model).isMaxTurnsReached();
        break;
      case NO_MORE_TILES:
        doReturn(true).when(_model).canPlayerMakeMove(any(PlayerState.class));
        when(_state.isAvailableTilesEmpty()).thenReturn(true);
        break;
      case ONLY_PLAYER_LEFT:
        doReturn(true).when(_model).canPlayerMakeMove(any(PlayerState.class));
        doReturn(true).when(_model).doesPlayerCountPreventGame();
        break;
      case PLAYER_CANT_PLAY_TILE:
        doReturn(false).when(_model).canPlayerMakeMove(any(PlayerState.class));
        break;
      default:
        throw new RuntimeException("Unsupported cause type: " + _cause);
    }
  }

  @Test
  public void testSignalEndPerspective() {
    StateChangeListener listener = mock(StateChangeListener.class);

    _model.addStateChangeListener(listener);
    EndPerspective perspective = _model.signalEndgame();

    assertThat(_cause.toString(), perspective.getCause(), is(_cause));
    verify(listener).gameOver(perspective);
  }

  @Test
  public void testPreferAllToMaxTurns() {
    doReturn(true).when(_model).isMaxTurnsReached();

    StateChangeListener listener = mock(StateChangeListener.class);

    _model.addStateChangeListener(listener);
    EndPerspective perspective = _model.signalEndgame();

    assertThat(_cause.toString(), perspective.getCause(), is(_cause));
    verify(listener).gameOver(perspective);
  }
}
