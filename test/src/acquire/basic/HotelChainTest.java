package acquire.basic;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.exception.DuplicateSpotException;
import acquire.exception.InvalidSizeException;

public class HotelChainTest {
  private final static Location LOC_C5 = new Location(Row.C, Column.FIVE);
  private final static Location LOC_C6 = new Location(Row.C, Column.SIX);
  private final static Location LOC_D5 = new Location(Row.D, Column.FIVE);
  private Set<Location> _locations;

  @Before
  public void setUp() {
    _locations = new HashSet<Location>();
    _locations.add(LOC_C5);
    _locations.add(LOC_C6);
  }

  @Test(expected = InvalidSizeException.class)
  public void testHotelChainLessSpot() throws InvalidSizeException {
    new HotelChain(HotelName.AMERICAN, new HashSet<Location>());
  }

  @Test
  public void testConstructor() throws InvalidSizeException {
    HotelChainI hotelChain = new HotelChain(HotelName.AMERICAN, _locations);
    Set<Location> locations = hotelChain.getLocations();
    assertThat(locations, hasSize(2));
    for (Location spot : _locations) {
      assertThat(locations, hasItem(spot));
    }
  }

  @Test
  public void testAddSpot() throws InvalidSizeException, DuplicateSpotException {
    HotelChain hotelChain = new HotelChain(HotelName.AMERICAN, _locations);
    hotelChain.addSpot(LOC_D5);
    assertThat(hotelChain.getLocations(), hasItem(LOC_D5));
  }

  @Test(expected = DuplicateSpotException.class)
  public void testAddSpotException() throws InvalidSizeException,
      DuplicateSpotException {
    HotelChain hotelChain = new HotelChain(HotelName.AMERICAN, _locations);
    hotelChain.addSpot(LOC_C5);
  }
}
