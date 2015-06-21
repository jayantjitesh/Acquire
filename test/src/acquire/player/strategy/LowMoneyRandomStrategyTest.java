package acquire.player.strategy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import acquire.basic.HotelChainI.HotelName;
import acquire.exception.InvalidSharePriceException;
import acquire.protocol.request.PurchaseRequest;
import acquire.util.RandomHelper;

public class LowMoneyRandomStrategyTest extends
    LowMoneyStrategyTest_<RandomStrategy> {
  private RandomHelper _random;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    _random = mock(RandomHelper.class);
    when(_random.randomInt(PurchaseRequest.MAX_SHARES + 1)).thenReturn(3);
  }

  @Override
  public void testNoMoney() {
    _strategy = new RandomStrategy(new RandomNameSequence(HotelName.AMERICAN));
    super.testNoMoney();
  }

  @Test
  public void testMoneyOneShare() {
    _strategy = new RandomStrategy(new RandomNameSequence(HotelName.AMERICAN));
    Map<HotelName, Integer> availableShares = new HashMap<HotelName, Integer>();
    availableShares.put(HotelName.AMERICAN, 8);
    availableShares.put(HotelName.TOWER, 2);
    when(_myself.getMoney()).thenReturn(DEFAULT_SHARE_PRICE);

    List<HotelName> names = _strategy.choosePurchase(availableShares, _banker,
        DEFAULT_SHARE_PRICE, StrategyHelperTest.makeChainsMap()).getNames();
    assertThat(names, hasSize(1));
    assertThat(names, contains(HotelName.AMERICAN));
  }

  @Test
  public void testMoneyTwoShare() {
    _strategy = new RandomStrategy(new RandomNameSequence(HotelName.AMERICAN));
    Map<HotelName, Integer> availableShares = new HashMap<HotelName, Integer>();
    availableShares.put(HotelName.AMERICAN, 8);
    availableShares.put(HotelName.TOWER, 2);
    when(_myself.getMoney()).thenReturn(DEFAULT_SHARE_PRICE * 2);

    List<HotelName> names = _strategy.choosePurchase(availableShares, _banker,
        DEFAULT_SHARE_PRICE * 2, StrategyHelperTest.makeChainsMap()).getNames();
    assertThat(names, hasSize(2));
    assertThat(names, contains(HotelName.AMERICAN, HotelName.AMERICAN));
  }

  @Test
  public void testFirstExpensive() throws InvalidSharePriceException {
    _strategy = new RandomStrategy(new RandomNameSequence(HotelName.AMERICAN,
        HotelName.TOWER));
    Map<HotelName, Integer> availableShares = new HashMap<HotelName, Integer>();
    availableShares.put(HotelName.AMERICAN, 8);
    availableShares.put(HotelName.TOWER, 2);

    when(_banker.getSharePrice(eq(HotelName.AMERICAN), anyInt())).thenReturn(
        Integer.MAX_VALUE);
    when(_myself.getMoney()).thenReturn(DEFAULT_SHARE_PRICE * 2);

    List<HotelName> names = _strategy.choosePurchase(availableShares, _banker,
        DEFAULT_SHARE_PRICE * 2, StrategyHelperTest.makeChainsMap()).getNames();
    assertThat(names, hasSize(2));
    assertThat(names, contains(HotelName.TOWER, HotelName.TOWER));
  }
}
