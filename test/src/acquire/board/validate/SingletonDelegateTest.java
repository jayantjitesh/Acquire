package acquire.board.validate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.board.Board;
import acquire.board.Spot;
import acquire.exception.validate.NonEmptyNeighborException;

public class SingletonDelegateTest {
  private static final Location D10 = new Location(Row.D, Column.TEN);
  private Board _board;
  private SingletonDelegate _delegate;

  @Before
  public void setUp() throws Exception {
    _board = mock(Board.class);
    when(_board.getAvailableHotelCount()).thenReturn(4);
    _delegate = new SingletonDelegate();
  }

  @Test
  public void testAllEmpty() throws NonEmptyNeighborException {
    when(_board.getSpot(D10)).thenReturn(new Spot(D10));

    _delegate.validateNeighbor(_board, D10);
  }

  @Test(expected = NonEmptyNeighborException.class)
  public void testNonEmptyHotel() throws NonEmptyNeighborException {
    when(_board.getSpot(D10)).thenReturn(
        new Spot(D10).changeName(HotelName.AMERICAN));

    _delegate.validateNeighbor(_board, D10);
  }

  @Test(expected = NonEmptyNeighborException.class)
  public void testNonEmptyNoHotelHotelsAvailable()
      throws NonEmptyNeighborException {
    when(_board.getSpot(D10)).thenReturn(new Spot(D10).changeToSingleton());

    _delegate.validateNeighbor(_board, D10);
  }

  @Test
  public void testNonEmptyNoHotelNoHotelsAvailable()
      throws NonEmptyNeighborException {
    reset(_board);
    when(_board.getSpot(D10)).thenReturn(new Spot(D10).changeToSingleton());
    when(_board.getAvailableHotelCount()).thenReturn(0);

    _delegate.validateNeighbor(_board, D10);
  }
}
