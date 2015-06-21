package acquire.board.validate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;
import acquire.basic.HotelChain;
import acquire.basic.Location;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.board.Board;
import acquire.board.Spot;
import acquire.exception.ChainNotFoundException;
import acquire.exception.validate.TouchingSafeHotelException;
import acquire.exception.validate.TouchingUnassociatedRegionExceptions;

public class MergingHotelDelegateTest {
  private static final Location D10 = new Location(Row.D, Column.TEN);
  private static final Location D9 = new Location(Row.D, Column.NINE);
  private static final Location D8 = new Location(Row.D, Column.EIGHT);
  private Board _board;
  private HotelChain _chain;
  private MergingHotelDelegate _delegate;

  @Before
  public void setUp() throws Exception {
    _board = mock(Board.class);
    _chain = mock(HotelChain.class);
    _delegate = new MergingHotelDelegate();
  }

  @Test
  public void testAllEmpty() throws TouchingSafeHotelException,
      ChainNotFoundException, TouchingUnassociatedRegionExceptions {
    when(_board.getSpot(D10)).thenReturn(new Spot(D10));
    when(_board.getHotelChain(HotelName.AMERICAN)).thenReturn(_chain);

    _delegate.validateNeighbor(_board, D10);

    assertThat(_delegate.getFoundHotels(), hasSize(0));
  }

  @Test
  public void testNoHotel() throws TouchingSafeHotelException,
      ChainNotFoundException, TouchingUnassociatedRegionExceptions {
    when(_board.getSpot(D10)).thenReturn(new Spot(D10));
    when(_board.getHotelChain(HotelName.AMERICAN)).thenReturn(_chain);

    _delegate.validateNeighbor(_board, D10);

    assertThat(_delegate.getFoundHotels(), hasSize(0));
  }

  @Test(expected = TouchingUnassociatedRegionExceptions.class)
  public void testTouchingUnassociatedTile() throws TouchingSafeHotelException,
      ChainNotFoundException, TouchingUnassociatedRegionExceptions {
    when(_board.getSpot(D10)).thenReturn(new Spot(D10).changeToSingleton());
    when(_board.getHotelChain(HotelName.AMERICAN)).thenReturn(_chain);

    _delegate.validateNeighbor(_board, D10);

    assertThat(_delegate.getFoundHotels(), hasSize(0));
  }

  @Test
  public void testOneHotel() throws TouchingSafeHotelException,
      ChainNotFoundException, TouchingUnassociatedRegionExceptions {
    when(_board.getSpot(D10)).thenReturn(
        new Spot(D10).changeName(HotelName.AMERICAN));
    when(_board.getHotelChain(HotelName.AMERICAN)).thenReturn(_chain);

    _delegate.validateNeighbor(_board, D10);

    assertThat(_delegate.getFoundHotels(), contains(HotelName.AMERICAN));
  }

  @Test(expected = TouchingSafeHotelException.class)
  public void testOneHotelSafe() throws TouchingSafeHotelException,
      ChainNotFoundException, TouchingUnassociatedRegionExceptions {
    when(_board.getSpot(D10)).thenReturn(
        new Spot(D10).changeName(HotelName.AMERICAN));
    when(_board.getHotelChain(HotelName.AMERICAN)).thenReturn(_chain);
    when(_chain.isSafe()).thenReturn(true);

    _delegate.validateNeighbor(_board, D10);

    assertThat(_delegate.getFoundHotels(), contains(HotelName.AMERICAN));
  }

  @Test
  public void testTwoHotelDiff() throws TouchingSafeHotelException,
      ChainNotFoundException, TouchingUnassociatedRegionExceptions {
    when(_board.getSpot(D10)).thenReturn(
        new Spot(D10).changeName(HotelName.AMERICAN));
    when(_board.getSpot(D8)).thenReturn(
        new Spot(D8).changeName(HotelName.CONTINENTAL));
    when(_board.getHotelChain(HotelName.AMERICAN)).thenReturn(_chain);
    when(_board.getHotelChain(HotelName.CONTINENTAL)).thenReturn(_chain);

    _delegate.validateNeighbor(_board, D10);
    _delegate.validateNeighbor(_board, D8);

    assertThat(_delegate.getFoundHotels(), containsInAnyOrder(
        HotelName.AMERICAN, HotelName.CONTINENTAL));
  }

  @Test(expected = TouchingSafeHotelException.class)
  public void testTwoHotelDiffSafe() throws TouchingSafeHotelException,
      ChainNotFoundException, TouchingUnassociatedRegionExceptions {
    when(_board.getSpot(D10)).thenReturn(
        new Spot(D10).changeName(HotelName.AMERICAN));
    when(_board.getSpot(D8)).thenReturn(
        new Spot(D8).changeName(HotelName.CONTINENTAL));
    when(_board.getHotelChain(HotelName.AMERICAN)).thenReturn(_chain);
    HotelChain chain2 = mock(HotelChain.class);
    when(_board.getHotelChain(HotelName.CONTINENTAL)).thenReturn(chain2);
    when(chain2.isSafe()).thenReturn(true);

    _delegate.validateNeighbor(_board, D10);
    _delegate.validateNeighbor(_board, D8);

    assertThat(_delegate.getFoundHotels(), containsInAnyOrder(
        HotelName.AMERICAN, HotelName.CONTINENTAL));
  }

  @Test
  public void testTwoHotel() throws TouchingSafeHotelException,
      ChainNotFoundException, TouchingUnassociatedRegionExceptions {
    when(_board.getSpot(D10)).thenReturn(
        new Spot(D10).changeName(HotelName.AMERICAN));
    when(_board.getSpot(D8)).thenReturn(
        new Spot(D8).changeName(HotelName.AMERICAN));
    when(_board.getHotelChain(HotelName.AMERICAN)).thenReturn(_chain);

    _delegate.validateNeighbor(_board, D10);
    _delegate.validateNeighbor(_board, D8);

    assertThat(_delegate.getFoundHotels(), contains(HotelName.AMERICAN));
  }

  @Test
  public void testEmptyNoHotelHotel() throws TouchingSafeHotelException,
      ChainNotFoundException, TouchingUnassociatedRegionExceptions {
    when(_board.getSpot(D10)).thenReturn(new Spot(D10));
    when(_board.getSpot(D9)).thenReturn(new Spot(D9));
    when(_board.getSpot(D8)).thenReturn(
        new Spot(D8).changeName(HotelName.AMERICAN));
    when(_board.getHotelChain(HotelName.AMERICAN)).thenReturn(_chain);

    _delegate.validateNeighbor(_board, D10);
    _delegate.validateNeighbor(_board, D9);
    _delegate.validateNeighbor(_board, D8);

    assertThat(_delegate.getFoundHotels(), contains(HotelName.AMERICAN));
  }

  @Test(expected = TouchingUnassociatedRegionExceptions.class)
  public void testTouchingSingletonHotel() throws TouchingSafeHotelException,
      ChainNotFoundException, TouchingUnassociatedRegionExceptions {
    when(_board.getSpot(D10)).thenReturn(new Spot(D10));
    when(_board.getSpot(D9)).thenReturn(new Spot(D9).changeToSingleton());
    when(_board.getSpot(D8)).thenReturn(
        new Spot(D8).changeName(HotelName.AMERICAN));
    when(_board.getHotelChain(HotelName.AMERICAN)).thenReturn(_chain);

    _delegate.validateNeighbor(_board, D10);
    _delegate.validateNeighbor(_board, D9);
    _delegate.validateNeighbor(_board, D8);

    assertThat(_delegate.getFoundHotels(), contains(HotelName.AMERICAN));
  }
}
