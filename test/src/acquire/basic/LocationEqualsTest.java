package acquire.basic;

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
public class LocationEqualsTest {

  private final Location _loc1;
  private final Location _loc2;
  private final boolean _same;

  public LocationEqualsTest(Location loc1, Location loc2, boolean same) {
    _loc1 = loc1;
    _loc2 = loc2;
    _same = same;
  }

  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
        { new Location(Row.A, Column.SEVEN),
            new Location(Row.A, Location.Column.SEVEN), true },
        { new Location(Row.B, Location.Column.SEVEN),
            new Location(Row.A, Location.Column.SEVEN), false },
        { new Location(Row.A, Location.Column.FOUR),
            new Location(Row.A, Location.Column.SEVEN), false },
        { new Location(Row.B, Location.Column.FOUR),
            new Location(Row.C, Location.Column.SEVEN), false } });
  }

  @Test
  public void testEquals() {
    assertThat(_loc1.equals(_loc2), is(_same));
  }

  @Test
  public void testEqualsOtherWay() {
    assertThat(_loc2.equals(_loc1), is(_same));
  }

}
