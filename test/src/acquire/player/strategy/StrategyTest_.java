package acquire.player.strategy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static util.Matchers.any;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import acquire.basic.HotelChainI;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.board.Board;
import acquire.board.inspect.ImposibleResult;
import acquire.board.inspect.PossibleResult;
import acquire.board.inspect.SingletonResult;
import acquire.state.money.Banker;
import acquire.state.perspective.PlayerPerspective;
import acquire.state.player.MyPlayerState;

/**
 * Test static method of BasicPlayer.
 * 
 * @param <T> Type being tested.
 */
public abstract class StrategyTest_<T extends Strategy> {
  protected static final int IGNORE_MONEY = Integer.MAX_VALUE;
  protected static final Location A1 = new Location(Row.A, Column.ONE);
  protected static final Location D10 = new Location(Row.D, Column.TEN);
  protected static final Location C10 = new Location(Row.C, Column.TEN);
  protected PossibleResult _possible = mock(PossibleResult.class);
  protected ImposibleResult _impossible = mock(ImposibleResult.class);
  protected Board _board;
  protected Banker _banker;
  protected MyPlayerState _myself;
  protected PlayerPerspective _perspective;
  protected T _strategy;
  protected Map<HotelName, HotelChainI> _chains;

  @Before
  public void setUp() throws Exception {
    _board = mock(Board.class);
    _myself = mock(MyPlayerState.class);
    _banker = mock(Banker.class);
    _perspective = mock(PlayerPerspective.class);

    when(_perspective.getBoard()).thenReturn(_board);
    when(_perspective.getMyself()).thenReturn(_myself);
    when(_perspective.getBanker()).thenReturn(_banker);
    when(_possible.isValid()).thenReturn(true);
    when(_myself.getMoney()).thenReturn(Integer.MAX_VALUE);
    when(_banker.getSharePrice(argThat(any(HotelName.class)), anyInt()))
        .thenReturn(1);

    _chains = StrategyHelperTest.makeChains();
  }

  @Test
  public void testChoosePlacementOnlyOne() {
    Map<Location, PossibleResult> validLocations = new HashMap<Location, PossibleResult>();
    validLocations.put(C10, new SingletonResult(C10));

    assertThat(_strategy.choosePlacement(validLocations, _perspective)
        .getLocation(), is(C10));
  }
}
