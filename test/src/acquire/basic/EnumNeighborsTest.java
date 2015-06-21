package acquire.basic;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;

public class EnumNeighborsTest {

  @Test
  public void testRowLowerBound() {
    assertThat(Row.getNeighbors(Row.A), containsInAnyOrder(Row.B));
  }

  @Test
  public void testRowUpperBound() {
    assertThat(Row.getNeighbors(Row.I), containsInAnyOrder(Row.H));
  }

  @Test
  public void testRowMiddle() {
    assertThat(Row.getNeighbors(Row.E), containsInAnyOrder(Row.D, Row.F));
  }

  @Test
  public void testColLowerBound() {
    assertThat(Column.getNeighbors(Column.ONE), containsInAnyOrder(Column.TWO));
  }

  @Test
  public void testColUpperBound() {
    assertThat(Column.getNeighbors(Column.TWELVE),
        containsInAnyOrder(Column.ELEVEN));
  }

  @Test
  public void testColMiddle() {
    assertThat(Column.getNeighbors(Column.SIX), containsInAnyOrder(Column.FIVE,
        Column.SEVEN));
  }
}
