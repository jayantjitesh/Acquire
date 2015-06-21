package acquire.board.validate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import acquire.basic.Location;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.board.Board;
import acquire.exception.validate.ValidationException;

public abstract class BoardActionValidatorTest_ {
  protected static final Location D10 = new Location(Row.D, Column.TEN);
  protected static final Location D9 = new Location(Row.D, Column.NINE);
  protected static final Location D11 = new Location(Row.D, Column.ELEVEN);
  protected static final Location C10 = new Location(Row.C, Column.TEN);
  protected static final Location E10 = new Location(Row.E, Column.TEN);
  protected Board _board;
  protected BoardActionValidator _validator;

  @Before
  public void setUp() throws Exception {
    _board = mock(Board.class);
    when(_board.getAvailableHotelCount()).thenReturn(4);
    _validator = new BoardActionValidator();
  }

  @Test
  public void testValidateNeighbors() throws ValidationException {
    ValidationDelegate delegate = mock(ValidationDelegate.class);
    Set<Location> neighbors = new HashSet<Location>(Arrays.asList(C10, E10));

    _validator.validateNeighbors(_board, neighbors, delegate);

    verify(delegate).validateNeighbor(_board, C10);
    verify(delegate).validateNeighbor(_board, E10);
  }

}
