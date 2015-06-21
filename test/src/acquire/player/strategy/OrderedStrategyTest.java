package acquire.player.strategy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.board.inspect.PossibleResult;
import acquire.board.inspect.SingletonResult;

public class OrderedStrategyTest extends StrategyTest_<OrderedStrategy> {

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();

    _strategy = new OrderedStrategy();
  }

  @Test
  public void testChoosePlacement() {
    Map<Location, PossibleResult> validLocations = new HashMap<Location, PossibleResult>();
    validLocations.put(C10, new SingletonResult(C10));
    validLocations.put(D10, new SingletonResult(D10));

    assertThat(_strategy.choosePlacement(validLocations, _perspective)
        .getLocation(), is(C10));
  }

  @Test
  public void testChoosePlacementMany() {
    Map<Location, PossibleResult> validLocations = new HashMap<Location, PossibleResult>();
    validLocations.put(C10, new SingletonResult(C10));
    validLocations.put(D10, new SingletonResult(D10));
    validLocations.put(A1, new SingletonResult(A1));

    assertThat(_strategy.choosePlacement(validLocations, _perspective)
        .getLocation(), is(A1));
  }

  @Test
  public void testChoosePurchaseOnlyOneShare() {
    Map<HotelName, Integer> availableShares = new HashMap<HotelName, Integer>();
    availableShares.put(HotelName.AMERICAN, 1);

    List<HotelName> names = _strategy.choosePurchase(availableShares, _banker,
        Integer.MAX_VALUE, StrategyHelperTest.makeChainsMap()).getNames();
    assertThat(names, hasSize(1));
    assertThat(names, contains(HotelName.AMERICAN));
  }

  @Test
  public void testChoosePurchaseTwoSharesSameHotel() {
    Map<HotelName, Integer> availableShares = new HashMap<HotelName, Integer>();
    availableShares.put(HotelName.AMERICAN, 2);

    List<HotelName> names = _strategy.choosePurchase(availableShares, _banker,
        Integer.MAX_VALUE, StrategyHelperTest.makeChainsMap()).getNames();
    assertThat(names, hasSize(2));
    assertThat(names, contains(HotelName.AMERICAN, HotelName.AMERICAN));
  }

  // @Test
  // public void testChoosePurchaseTwoSharesDiffHotel() {
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
  // Map<HotelName, Integer> availableShares = new HashMap<HotelName,
  // Integer>();
  // availableShares.put(HotelName.AMERICAN, 1);
  // availableShares.put(HotelName.TOWER, 2);
  //
  // List<HotelName> names = _strategy.choosePurchase(availableShares,
  // _perspective).getNames();
  // // assertThat(names, hasSize(3));
  // assertThat(names, hasSize(2));
  // // assertThat(
  // // names,
  // // containsInAnyOrder(HotelName.AMERICAN, HotelName.TOWER,
  // // HotelName.TOWER));
  // assertThat(names, containsInAnyOrder(HotelName.AMERICAN, HotelName.TOWER));
  // }

  @Test
  public void testChoosePurchaseThreeSharesSameHotel() {
    Map<HotelName, Integer> availableShares = new HashMap<HotelName, Integer>();
    availableShares.put(HotelName.AMERICAN, 8);
    availableShares.put(HotelName.TOWER, 2);

    List<HotelName> names = _strategy.choosePurchase(availableShares, _banker,
        Integer.MAX_VALUE, StrategyHelperTest.makeChainsMap()).getNames();
    // assertThat(names, hasSize(3));
    assertThat(names, hasSize(2));
    // assertThat(names, containsInAnyOrder(HotelName.AMERICAN,
    // HotelName.AMERICAN, HotelName.AMERICAN));
    assertThat(names,
        containsInAnyOrder(HotelName.AMERICAN, HotelName.AMERICAN));
  }
}
