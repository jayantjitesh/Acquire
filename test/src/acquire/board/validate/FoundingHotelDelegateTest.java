package acquire.board.validate;

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
import acquire.exception.validate.CannotAddHotelException;

public class FoundingHotelDelegateTest {
  private static final Location D10 = new Location(Row.D, Column.TEN);
  private static final Location D9 = new Location(Row.D, Column.NINE);
  private static final Location D11 = new Location(Row.D, Column.ELEVEN);
  private static final Location C10 = new Location(Row.C, Column.TEN);
  private static final Location E10 = new Location(Row.E, Column.TEN);
  private Board _board;
  private FoundingHotelDelegate _delegate;

  @Before
  public void setUp() throws Exception {
    _board = mock(Board.class);
    when(_board.getAvailableHotelCount()).thenReturn(4);
    _delegate = new FoundingHotelDelegate();
  }

  @Test
  public void testAllEmpty() throws CannotAddHotelException {
    when(_board.getSpot(D10)).thenReturn(new Spot(D10));
    _delegate.validateNeighbor(_board, D10);
  }

  @Test(expected = CannotAddHotelException.class)
  public void testNonEmptyHotel() throws CannotAddHotelException {
    when(_board.getSpot(D10)).thenReturn(
        new Spot(D10).changeName(HotelName.AMERICAN));
    _delegate.validateNeighbor(_board, D10);
  }

  @Test
  public void testNonEmptyNoHotelHotelsAvailable()
      throws CannotAddHotelException {
    when(_board.getSpot(D10)).thenReturn(new Spot(D10).changeToSingleton());
    when(_board.getSpot(D9)).thenReturn(new Spot(D9));
    when(_board.getSpot(C10)).thenReturn(new Spot(C10));
    when(_board.getSpot(D11)).thenReturn(new Spot(D11));
    when(_board.getSpot(E10)).thenReturn(new Spot(E10));
    _delegate.validateNeighbor(_board, D10);
  }

}
