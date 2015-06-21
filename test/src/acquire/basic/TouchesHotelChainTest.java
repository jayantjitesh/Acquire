package acquire.basic;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.exception.AcquireException;
import acquire.exception.InvalidSizeException;

public class TouchesHotelChainTest {
  private final static Location LOC_A1 = new Location(Row.A, Column.ONE);
  private final static Location LOC_C5 = new Location(Row.C, Column.FIVE);
  private final static Location LOC_C6 = new Location(Row.C, Column.SIX);
  private final static Location LOC_D5 = new Location(Row.D, Column.FIVE);
  private final static Location LOC_D6 = new Location(Row.D, Column.SIX);
  private Set<Location> _locations;

  @Before
  public void setUp() {
    _locations = new HashSet<Location>();
    _locations.add(LOC_C5);
    _locations.add(LOC_C6);
  }

  @Test
  public void testTouchesOne() throws AcquireException {
    HotelChainI chain = makeChain(_locations);
    assertThat(HotelChain.touches(chain, LOC_D6), is(true));
  }

  @Test
  public void testTouchesTwo() throws AcquireException {
    _locations.add(LOC_D6);
    HotelChainI chain = makeChain(_locations);
    assertThat(HotelChain.touches(chain, LOC_D5), is(true));
  }

  @Test
  public void testTouchesNo() throws AcquireException {
    HotelChainI chain = makeChain(_locations);
    assertThat(HotelChain.touches(chain, LOC_A1), is(false));
  }

  private static HotelChainI makeChain(Set<Location> spots)
      throws InvalidSizeException {
    return new HotelChain(HotelName.AMERICAN, spots);
  }
}
