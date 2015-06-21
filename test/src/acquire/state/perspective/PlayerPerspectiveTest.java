package acquire.state.perspective;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import acquire.basic.Location;
import acquire.board.BoardI;
import acquire.state.State;
import acquire.state.money.BankerI;
import acquire.state.player.MyPlayerState;
import acquire.state.player.PlayerState;
import acquire.state.player.PlayerStateI;

public class PlayerPerspectiveTest {
  private List<PlayerState> _players;
  private MyPlayerState _myself;
  private PlayerState _current;
  private BoardI _board;
  private BankerI _banker;
  private int _turnsExecuted;
  private Set<Location> _availableTiles;

  @Before
  public void setUp() throws Exception {
    _players = new ArrayList<PlayerState>();
    _myself = mock(PlayerState.class);
    _current = mock(PlayerState.class);
    _board = mock(BoardI.class);
    _banker = mock(BankerI.class);
    _availableTiles = new HashSet<Location>();
    _turnsExecuted = 1;

    _players.add(_current);
  }

  @Test
  public void testConstroctorPieces() {
    PlayerPerspective perspective = new PlayerPerspective(_players, _myself,
        _board, _banker, _turnsExecuted, _availableTiles);
    assertPerspective(perspective);
  }

  @Test
  public void testPlayerPerspectiveStateMyPlayer() {
    State state = mock(State.class);
    when(state.getPlayers()).thenReturn(_players);
    when(state.getBoard()).thenReturn(_board);
    when(state.getBanker()).thenReturn(_banker);

    PlayerPerspective perspective = new PlayerPerspective(state, _myself,
        _turnsExecuted);

    assertPerspective(perspective);
  }

  private void assertPerspective(PlayerPerspective perspective) {
    for (PlayerState player : _players) {
      assertThat(perspective.getAllPlayers(), hasItem(player));
    }
    assertThat(perspective.getCurrentPlayer(), is((PlayerStateI) _current));
    assertThat(perspective.getMyself(), is(_myself));
    assertThat(perspective.getBoard(), is(_board));
    assertThat(perspective.getBanker(), is(_banker));
    assertThat(perspective.getTurnsExecuted(), is(_turnsExecuted));
  }
}
