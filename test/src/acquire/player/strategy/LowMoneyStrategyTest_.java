package acquire.player.strategy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static util.Matchers.any;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.board.Board;
import acquire.board.inspect.ImposibleResult;
import acquire.board.inspect.PossibleResult;
import acquire.state.money.Banker;
import acquire.state.perspective.PlayerPerspective;
import acquire.state.player.MyPlayerState;

/**
 * Test strategies when money is restricted.
 * 
 * @param <T> Strategy being tested.
 */
public abstract class LowMoneyStrategyTest_<T extends Strategy> {
  protected static final int DEFAULT_SHARE_PRICE = 1;
  protected static final Location A1 = new Location(Row.A, Column.ONE);
  protected static final Location D10 = new Location(Row.D, Column.TEN);
  protected static final Location C10 = new Location(Row.C, Column.TEN);
  protected PossibleResult _possible = mock(PossibleResult.class);
  protected ImposibleResult _impossible = mock(ImposibleResult.class);
  protected Board _board;
  protected Banker _banker;
  protected PlayerPerspective _perspective;
  protected MyPlayerState _myself;
  protected T _strategy;

  @Before
  public void setUp() throws Exception {
    _board = mock(Board.class);
    _banker = mock(Banker.class);
    _myself = mock(MyPlayerState.class);
    _perspective = mock(PlayerPerspective.class);

    when(_perspective.getBoard()).thenReturn(_board);
    when(_perspective.getBanker()).thenReturn(_banker);
    when(_perspective.getMyself()).thenReturn(_myself);
    when(_possible.isValid()).thenReturn(true);
    when(_banker.getSharePrice(argThat(any(HotelName.class)), anyInt()))
        .thenReturn(DEFAULT_SHARE_PRICE);

  }

  @Test
  public void testNoMoney() {
    Map<HotelName, Integer> availableShares = new HashMap<HotelName, Integer>();
    availableShares.put(HotelName.AMERICAN, 8);
    availableShares.put(HotelName.TOWER, 2);
    // when(_myself.getMoney()).thenReturn(0);

    List<HotelName> names = _strategy.choosePurchase(availableShares, _banker,
        0, StrategyHelperTest.makeChainsMap()).getNames();
    // List<HotelName> names = _strategy.choosePurchase(availableShares,
    // _perspective).getNames();
    assertThat(names, hasSize(0));
  }
}
