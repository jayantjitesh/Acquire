package acquire.game.tree;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.board.Board;
import acquire.board.inspect.FoundingResult;
import acquire.enforcer.RuleEnforcerBuilder;
import acquire.enforcer.tile.TileSelectionModel;
import acquire.game.tree.exception.NotFinalStateException;
import acquire.state.State;
import acquire.state.perspective.EndPerspective.Cause;
import acquire.state.player.PlayerState;

public class TreeTestBaicMethods {

  private TreeI _tree;
  private State _state;
  protected static final Location D10 = new Location(Row.D, Column.TEN);
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

  @Before
  public void setUp() throws Exception {
    _state = mock(State.class);
    _selectionModel = mock(TileSelectionModel.class);
    HashSet<Location> tiles = new HashSet<Location>();
    tiles.add(D10);
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
    when(_state.getAvailableTiles()).thenReturn(tiles);

    when(_state.getCurrentPlayer()).thenReturn(pState);

  }

  @Test
  public void testTreeStateValue() {
    _tree = new Tree(_state);
    assertThat(_tree.getNodeValue(), is(_state));

  }

  @Test
  public void testTreeNotFinalState() {
    when(_state.getPlayers()).thenReturn(_players);
    Board board = mock(Board.class);
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
    _tree = new Tree(_state);
    assertThat(_tree.isFinalState(), is(false));
  }

  @Test
  public void testTreeFinalState() throws NotFinalStateException {
    State state = mock(State.class);
    _tree = new Tree(state);
    assertThat(_tree.isFinalState(), is(true));
    assertThat(_tree.getReasonForFinalState(), is(Cause.ONLY_PLAYER_LEFT));
  }

  public void testNext() {

  }

  //
  // public void testValidateEdge() {
  // fail("Not yet implemented");
  // }
  //
  // public void testGetReasonForFinalState() {
  // fail("Not yet implemented");
  // }

}
