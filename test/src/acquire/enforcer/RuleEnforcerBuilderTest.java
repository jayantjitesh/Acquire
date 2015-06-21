package acquire.enforcer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isOneOf;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import util.Matchers;
import acquire.basic.Location;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.enforcer.tile.TileSelectionModel;
import acquire.exception.AcquireException;
import acquire.exception.InvalidPlayerNameException;
import acquire.exception.InvalidTurnCountException;
import acquire.exception.NoAvailableTileException;
import acquire.exception.TooFewTilesException;
import acquire.exception.TooManyPlayersException;
import acquire.player.Player;
import acquire.state.player.PlayerState;
import acquire.state.player.PlayerStateI;

public class RuleEnforcerBuilderTest {
  private static final String PATRICK = "Patrick";
  private static final String JAYAN = "JAYANT";
  private static final String JJ = "JJ";
  private static final String BAD_NAME = "alksdjfgh;aslfjg;alfkjg;ljg;ldkfjg";
  private static Location A1 = new Location(Row.A, Column.ONE);
  private static Location A2 = new Location(Row.A, Column.TWO);
  private static Location A3 = new Location(Row.A, Column.THREE);
  private static Location A4 = new Location(Row.A, Column.FOUR);
  private static Location A5 = new Location(Row.A, Column.FIVE);
  private static Location A6 = new Location(Row.A, Column.SIX);
  private TileSelectionModel _selectionModel;
  private Player _player;
  private RuleEnforcerBuilder _builder;

  @Before
  public void setUp() throws Exception {
    _selectionModel = mock(TileSelectionModel.class);
    _player = mock(Player.class);
    _builder = new RuleEnforcerBuilder();
  }

  @Test(expected = InvalidPlayerNameException.class)
  public void testBadPlayerName() throws AcquireException {
    // when(_player.getName()).thenReturn(BAD_NAME);
    _builder.addPlayer(_player, BAD_NAME);
  }

  @Test(expected = InvalidTurnCountException.class)
  public void testBadTurnCount() throws AcquireException {
    when(_player.getName()).thenReturn(BAD_NAME);
    _builder.setMaxTurns(Integer.MIN_VALUE);
  }

  @Test
  public void testSamePlayerName() throws AcquireException {
    String firstName = _builder.addPlayer(_player, JJ);
    String secondName = _builder.addPlayer(_player, JJ);
    assertThat(firstName, is(JJ));
    assertThat(secondName, is(JJ + "_" + 0));
  }

  @Test(expected = TooManyPlayersException.class)
  public void testTooManyAdd() throws AcquireException {
    for (int i = 0; i < RuleEnforcerBuilder.MAX_PLAYERS + 1; i++) {
      Player p = mock(Player.class);
      // when(p.getName()).thenReturn(PATRICK + i);
      _builder.addPlayer(p, PATRICK + i);
    }
  }

  @Test
  public void testInitPlayer() throws TooFewTilesException,
      InvalidPlayerNameException, NoAvailableTileException {
    Set<Location> locations = new HashSet<Location>(Arrays.asList(A1, A2, A3,
        A4, A5, A6));
    when(_selectionModel.selectTile(locations)).thenReturn(A1, A2, A3, A4, A5,
        A6);
    PlayerState pState = RuleEnforcerBuilder.makeInitialPlayer(PATRICK,
        locations, _selectionModel);

    assertThat(pState.getName(), is(PATRICK));
    assertThat(pState.getMoney(), is(RuleEnforcerBuilder.INITIAL_CASH));
    assertThat(pState.getTilesInHand(),
        containsInAnyOrder(A1, A2, A3, A4, A5, A6));
    assertThat(pState.getStockOptions().values(), hasSize(0));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testMakeEnforcer() throws AcquireException {
    when(
        _selectionModel.selectTile((Set<Location>) argThat(Matchers
            .any(Collection.class)))).thenReturn(A1, A2, A3, A4, A5, A6);

    for (String name : new String[] { PATRICK, JAYAN, JJ }) {
      Player p = mock(Player.class);
      when(p.getName()).thenReturn(name);
      _builder.addPlayer(p, name);
    }
    RuleEnforcerTree enfrorcer = _builder.constructInitialRuleEnforcer();
    RuleEnforcerModelTree model = enfrorcer.getModel();
    assertThat(model.getMaxTurns(), is(RuleEnforcerModel.NO_MAX_TURN));

    Map<String, Player> players = enfrorcer.getPlayers();
    assertThat(players.values(), hasSize(3));
    for (Entry<String, Player> entry : players.entrySet()) {
      assertThat(entry.getKey(), isOneOf(PATRICK, JAYAN, JJ));
      assertThat(entry.getValue().getName(), isOneOf(PATRICK, JAYAN, JJ));
      assertThat(entry.getKey(), is(entry.getValue().getName()));
    }

    // State state = model.getState();
    // assertThat(state.getPlayersName(), containsInAnyOrder(PATRICK, JAYAN,
    // JJ));
  }

  @Test(expected = InvalidPlayerNameException.class)
  public void testInitBadName() throws TooFewTilesException,
      InvalidPlayerNameException {
    RuleEnforcerBuilder.makeInitialPlayer(BAD_NAME, new HashSet<Location>(
        Arrays.asList(A1, A2, A3, A4, A5, A6)), _selectionModel);
  }

  @Test(expected = TooFewTilesException.class)
  public void testInitFewTitles() throws TooFewTilesException,
      InvalidPlayerNameException {
    RuleEnforcerBuilder.makeInitialPlayer(PATRICK, new HashSet<Location>(),
        _selectionModel);
  }

  @Test
  public void testSelectTiles() throws TooFewTilesException,
      NoAvailableTileException {
    Set<Location> locations = new HashSet<Location>(Arrays.asList(A1, A2, A3,
        A4, A5, A6));
    when(_selectionModel.selectTile(locations)).thenReturn(A1, A2, A3, A4, A5,
        A6);
    Set<Location> selectTiles = RuleEnforcerBuilder.selectTiles(locations,
        _selectionModel);

    assertThat(selectTiles, hasSize(PlayerStateI.MAX_TILES));
    assertThat(selectTiles, containsInAnyOrder(A1, A2, A3, A4, A5, A6));
  }

  @Test(expected = TooFewTilesException.class)
  public void testFewTiles() throws TooFewTilesException {
    RuleEnforcerBuilder.selectTiles(new HashSet<Location>(), _selectionModel);
  }
}
