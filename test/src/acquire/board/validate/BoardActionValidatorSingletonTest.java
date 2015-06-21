package acquire.board.validate;

import static org.mockito.Mockito.when;
import org.junit.Test;
import acquire.basic.HotelChainI.HotelName;
import acquire.board.Spot;
import acquire.exception.validate.NonEmptyLocationException;
import acquire.exception.validate.ValidationException;

public class BoardActionValidatorSingletonTest extends
    BoardActionValidatorTest_ {
  @Test
  public void testValidateSingleton() throws ValidationException {
    when(_board.getSpot(C10)).thenReturn(new Spot(C10));
    when(_board.getSpot(E10)).thenReturn(new Spot(E10));
    when(_board.getSpot(D9)).thenReturn(new Spot(D9));
    when(_board.getSpot(D11)).thenReturn(new Spot(D11));
    when(_board.getSpot(D10)).thenReturn(new Spot(D10));

    _validator.validateSingleton(_board, D10);
  }

  @Test(expected = NonEmptyLocationException.class)
  public void testValidateSingletonNonEmpty() throws ValidationException {
    when(_board.getSpot(C10)).thenReturn(new Spot(C10));
    when(_board.getSpot(E10)).thenReturn(new Spot(E10));
    when(_board.getSpot(D9)).thenReturn(new Spot(D9));
    when(_board.getSpot(D11)).thenReturn(new Spot(D11));
    when(_board.getSpot(D10)).thenReturn(
        new Spot(D10).changeName(HotelName.AMERICAN));

    _validator.validateSingleton(_board, D10);
  }

  @Test(expected = NonEmptyLocationException.class)
  public void testValidateSingletonNeighborHotel() throws ValidationException {
    when(_board.getSpot(C10)).thenReturn(new Spot(C10));
    when(_board.getSpot(E10)).thenReturn(new Spot(E10));
    when(_board.getSpot(D9)).thenReturn(new Spot(D9));
    when(_board.getSpot(D11)).thenReturn(
        new Spot(D11).changeName(HotelName.AMERICAN));
    when(_board.getSpot(D10)).thenReturn(new Spot(D10));

    _validator.validateSingleton(_board, D10);
  }

}
