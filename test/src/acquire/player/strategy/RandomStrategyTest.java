package acquire.player.strategy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.board.inspect.PossibleResult;
import acquire.board.inspect.SingletonResult;
import acquire.protocol.request.PurchaseRequest;
import acquire.util.RandomHelper;

public class RandomStrategyTest extends StrategyTest_<RandomStrategy> {
  private RandomHelper _random;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    _random = mock(RandomHelper.class);

    _strategy = new RandomStrategy(_random);
  }

  @Override
  public void testChoosePlacementOnlyOne() {
    // real random should always choose same on only one option
    _strategy = new RandomStrategy();
    super.testChoosePlacementOnlyOne();
  }

  @Test
  public void testChoosePlacement() {
    Map<Location, PossibleResult> validLocations = new HashMap<Location, PossibleResult>();
    validLocations.put(C10, new SingletonResult(C10));
    validLocations.put(D10, new SingletonResult(D10));
    when(_random.randomElement(validLocations.keySet())).thenReturn(C10);

    assertThat(_strategy.choosePlacement(validLocations, _perspective)
        .getLocation(), is(C10));
  }

  @Test
  public void testChoosePlacementMany() {
    Map<Location, PossibleResult> validLocations = new HashMap<Location, PossibleResult>();
    validLocations.put(C10, new SingletonResult(C10));
    validLocations.put(D10, new SingletonResult(D10));
    validLocations.put(A1, new SingletonResult(A1));
    when(_random.randomElement(validLocations.keySet())).thenReturn(C10);

    assertThat(_strategy.choosePlacement(validLocations, _perspective)
        .getLocation(), is(C10));
  }

  @Test
  public void testChoosePurchaseRandomNoShares() {
    Map<HotelName, Integer> availableShares = new HashMap<HotelName, Integer>();
    availableShares.put(HotelName.AMERICAN, 8);
    availableShares.put(HotelName.TOWER, 2);
    when(_random.randomInt(PurchaseRequest.MAX_SHARES + 1)).thenReturn(0);

    List<HotelName> names = _strategy.choosePurchase(availableShares, _banker,
        0, StrategyHelperTest.makeChainsMap()).getNames();
    assertThat(names, hasSize(0));
  }

  @Test
  public void testChoosePurchaseOnlyOneShare() {
    _strategy = new RandomStrategy(new RandomNameSequence(HotelName.AMERICAN));
    Map<HotelName, Integer> availableShares = new HashMap<HotelName, Integer>();
    availableShares.put(HotelName.AMERICAN, 1);

    List<HotelName> names = _strategy.choosePurchase(availableShares, _banker,
        Integer.MAX_VALUE, StrategyHelperTest.makeChainsMap()).getNames();

    assertThat(names, hasSize(1));
    assertThat(names, contains(HotelName.AMERICAN));
  }

  @Test
  public void testChoosePurchaseTwoSharesSameHotel() {
    _strategy = new RandomStrategy(new RandomNameSequence(HotelName.AMERICAN));
    Map<HotelName, Integer> availableShares = new HashMap<HotelName, Integer>();
    availableShares.put(HotelName.AMERICAN, 2);

    List<HotelName> names = _strategy.choosePurchase(availableShares, _banker,
        Integer.MAX_VALUE, StrategyHelperTest.makeChainsMap()).getNames();
    assertThat(names, hasSize(2));
    assertThat(names, contains(HotelName.AMERICAN, HotelName.AMERICAN));
  }

  // @Test
  // public void testChoosePurchaseTwoSharesDiffHotel() {
  // _strategy = new RandomStrategy(new RandomNameSequence(HotelName.AMERICAN,
  // HotelName.TOWER));
  // Map<HotelName, Integer> availableShares = new HashMap<HotelName,
  // Integer>();
  // availableShares.put(HotelName.AMERICAN, 1);
  // availableShares.put(HotelName.TOWER, 1);
  //
  // List<HotelName> names = _strategy.choosePurchase(availableShares,
  // _perspective).getNames();
  // assertThat(names, hasSize(2));
  // assertThat(names, containsInAnyOrder(HotelName.AMERICAN, HotelName.TOWER));
  // }

  // @Test
  // public void testChoosePurchaseThreeSharesDiffHotel() {
  // _strategy = new RandomStrategy(new RandomNameSequence(HotelName.AMERICAN,
  // HotelName.TOWER, HotelName.TOWER));
  // Map<HotelName, Integer> availableShares = new HashMap<HotelName,
  // Integer>();
  // availableShares.put(HotelName.AMERICAN, 8);
  // availableShares.put(HotelName.TOWER, 2);
  // List<HotelName> names = _strategy.choosePurchase(availableShares,
  // _perspective).getNames();
  // assertThat(names, hasSize(3));
  // assertThat(
  // names,
  // containsInAnyOrder(HotelName.AMERICAN, HotelName.TOWER, HotelName.TOWER));
  // }

  // @Test
  // public void testChoosePurchaseThreeSharesSameHotel() {
  // _strategy = new RandomStrategy(new RandomNameSequence(HotelName.AMERICAN));
  // Map<HotelName, Integer> availableShares = new HashMap<HotelName,
  // Integer>();
  // availableShares.put(HotelName.AMERICAN, 8);
  // availableShares.put(HotelName.TOWER, 2);
  //
  // List<HotelName> names = _strategy.choosePurchase(availableShares,
  // _perspective).getNames();
  // assertThat(names, hasSize(3));
  // assertThat(names, containsInAnyOrder(HotelName.AMERICAN,
  // HotelName.AMERICAN, HotelName.AMERICAN));
  // }
}
