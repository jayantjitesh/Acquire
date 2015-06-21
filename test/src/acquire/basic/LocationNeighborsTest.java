package acquire.basic;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;

public class LocationNeighborsTest {

  @Test
  public void testGetNeighborsLowLow() {
    Location spot = new Location(Row.A, Column.ONE);
    assertThat(spot.getNeighbors(), containsInAnyOrder(new Location(Row.A,
        Column.TWO), new Location(Row.B, Column.ONE)));
  }

  @Test
  public void testGetNeighborsUpUp() {
    Location spot = new Location(Row.I, Column.TWELVE);
    assertThat(spot.getNeighbors(), containsInAnyOrder(new Location(Row.H,
        Column.TWELVE), new Location(Row.I, Column.ELEVEN)));
  }

  @Test
  public void testGetNeighborsLowUp() {
    Location spot = new Location(Row.A, Column.TWELVE);
    assertThat(spot.getNeighbors(), containsInAnyOrder(new Location(Row.B,
        Column.TWELVE), new Location(Row.A, Column.ELEVEN)));
  }

  @Test
  public void testGetNeighborsUpLow() {
    Location spot = new Location(Row.I, Column.ONE);
    assertThat(spot.getNeighbors(), containsInAnyOrder(new Location(Row.I,
        Column.TWO), new Location(Row.H, Column.ONE)));
  }

  @Test
  public void testGetNeighborsMiddle() {
    Location spot = new Location(Row.D, Column.SIX);
    assertThat(spot.getNeighbors(), containsInAnyOrder(new Location(Row.D,
        Column.FIVE), new Location(Row.D, Column.SEVEN), new Location(Row.C,
        Column.SIX), new Location(Row.E, Column.SIX)));
  }
}
