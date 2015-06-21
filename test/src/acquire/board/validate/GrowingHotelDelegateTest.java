package acquire.board.validate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;
import acquire.basic.Location;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.board.Board;
import acquire.board.Spot;
import acquire.exception.validate.DifferentHotelException;
import acquire.exception.validate.NotTouchingHotelException;
import acquire.exception.validate.TouchingUnassociatedRegionExceptions;

public class GrowingHotelDelegateTest {
  private static final Location D10 = new Location(Row.D, Column.TEN);
  private static final Location D9 = new Location(Row.D, Column.NINE);
  private static final Location D8 = new Location(Row.D, Column.EIGHT);
  private Board _board;
  private GrowingHotelDelegate _delegate;

  @Before
  public void setUp() throws Exception {
    _board = mock(Board.class);
    _delegate = new GrowingHotelDelegate(D10);
  }

  @Test(expected = NotTouchingHotelException.class)
  public void testAllEmpty() throws DifferentHotelException,
      NotTouchingHotelException, TouchingUnassociatedRegionExceptions {
    when(_board.getSpot(D10)).thenReturn(new Spot(D10));

    _delegate.validateNeighbor(_board, D10);

    _delegate.getHotelName();
  }

  @Test(expected = NotTouchingHotelException.class)
  public void testNoHotel() throws DifferentHotelException,
      NotTouchingHotelException, TouchingUnassociatedRegionExceptions {
    when(_board.getSpot(D10)).thenReturn(new Spot(D10));

    _delegate.validateNeighbor(_board, D10);

    _delegate.getHotelName();
  }

  @Test
  public void testOneHotel() throws DifferentHotelException,
      NotTouchingHotelException, TouchingUnassociatedRegionExceptions {
    when(_board.getSpot(D10)).thenReturn(
        new Spot(D10).changeName(HotelName.AMERICAN));

    _delegate.validateNeighbor(_board, D10);

    assertThat(_delegate.getHotelName(), is(HotelName.AMERICAN));
  }

  @Test(expected = DifferentHotelException.class)
  public void testTwoHotelDiff() throws DifferentHotelException,
      TouchingUnassociatedRegionExceptions {
    when(_board.getSpot(D10)).thenReturn(
        new Spot(D10).changeName(HotelName.AMERICAN));
    when(_board.getSpot(D8)).thenReturn(
        new Spot(D8).changeName(HotelName.CONTINENTAL));

    _delegate.validateNeighbor(_board, D10);
    _delegate.validateNeighbor(_board, D8);
  }

  @Test
  public void testTwoHotel() throws DifferentHotelException,
      NotTouchingHotelException, TouchingUnassociatedRegionExceptions {
    when(_board.getSpot(D10)).thenReturn(
        new Spot(D10).changeName(HotelName.AMERICAN));
    when(_board.getSpot(D8)).thenReturn(
        new Spot(D8).changeName(HotelName.AMERICAN));

    _delegate.validateNeighbor(_board, D10);
    _delegate.validateNeighbor(_board, D8);

    assertThat(_delegate.getHotelName(), is(HotelName.AMERICAN));
  }

  @Test(expected = TouchingUnassociatedRegionExceptions.class)
  public void testEmptyNoHotelHotel() throws DifferentHotelException,
      NotTouchingHotelException, TouchingUnassociatedRegionExceptions {
    when(_board.getSpot(D10)).thenReturn(new Spot(D10));
    when(_board.getSpot(D9)).thenReturn(new Spot(D9).changeToSingleton());
    when(_board.getSpot(D8)).thenReturn(
        new Spot(D8).changeName(HotelName.AMERICAN));

    _delegate.validateNeighbor(_board, D10);
    _delegate.validateNeighbor(_board, D9);
    _delegate.validateNeighbor(_board, D8);

    assertThat(_delegate.getHotelName(), is(HotelName.AMERICAN));
  }
}
