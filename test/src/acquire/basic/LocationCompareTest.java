package acquire.basic;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;

@RunWith(value = Parameterized.class)
public class LocationCompareTest {

  private final Location _loc1;
  private final Location _loc2;

  public LocationCompareTest(Location loc1, Location loc2) {
    _loc1 = loc1;
    _loc2 = loc2;
  }

  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
        { new Location(Row.A, Location.Column.SEVEN),
            new Location(Row.B, Location.Column.SEVEN) },
        { new Location(Row.A, Location.Column.FOUR),
            new Location(Row.A, Location.Column.SEVEN) },
        { new Location(Row.B, Location.Column.FOUR),
            new Location(Row.C, Location.Column.SEVEN) } });
  }

  @Test
  public void testCompareTo() {
    assertThat(_loc1.compareTo(_loc2), lessThan(0));
  }

  @Test
  public void testCompareToOtherWay() {
    assertThat(_loc2.compareTo(_loc1), greaterThan(0));
  }

  @Test
  public void testCompareToEquals() {
    assertThat(new Location(Row.A, Column.SEVEN).compareTo(new Location(Row.A,
        Location.Column.SEVEN)), is(0));
  }

}
