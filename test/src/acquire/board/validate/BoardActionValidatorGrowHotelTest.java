package acquire.board.validate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import org.junit.Test;
import acquire.basic.HotelChainI.HotelName;
import acquire.board.Spot;
import acquire.exception.validate.DifferentHotelException;
import acquire.exception.validate.NonEmptyLocationException;
import acquire.exception.validate.NotTouchingHotelException;
import acquire.exception.validate.TouchingUnassociatedRegionExceptions;
import acquire.exception.validate.ValidationException;

public class BoardActionValidatorGrowHotelTest extends
    BoardActionValidatorTest_ {

  @Test(expected = NonEmptyLocationException.class)
  public void testSpotNonEmpty() throws ValidationException {
    when(_board.getSpot(C10)).thenReturn(new Spot(C10).changeToSingleton());
    when(_board.getSpot(E10)).thenReturn(new Spot(E10));
    when(_board.getSpot(D9)).thenReturn(new Spot(D9));
    when(_board.getSpot(D11)).thenReturn(new Spot(D11));
    when(_board.getSpot(D10)).thenReturn(
        new Spot(D10).changeName(HotelName.AMERICAN));

    _validator.validateGrowHotel(_board, D10);
  }

  @Test(expected = NotTouchingHotelException.class)
  public void testNoNeigbors() throws ValidationException {
    when(_board.getSpot(C10)).thenReturn(new Spot(C10));
    when(_board.getSpot(E10)).thenReturn(new Spot(E10));
    when(_board.getSpot(D9)).thenReturn(new Spot(D9));
    when(_board.getSpot(D11)).thenReturn(new Spot(D11));
    when(_board.getSpot(D10)).thenReturn(new Spot(D10));

    _validator.validateGrowHotel(_board, D10);
  }

  @Test(expected = NotTouchingHotelException.class)
  public void testNoHotelNeigbors() throws ValidationException {
    when(_board.getSpot(C10)).thenReturn(new Spot(C10));
    when(_board.getSpot(E10)).thenReturn(new Spot(E10));
    when(_board.getSpot(D9)).thenReturn(new Spot(D9));
    when(_board.getSpot(D11)).thenReturn(new Spot(D11));
    when(_board.getSpot(D10)).thenReturn(new Spot(D10));
    when(_board.isHotelAvailable(HotelName.AMERICAN)).thenReturn(false);

    _validator.validateGrowHotel(_board, D10);
  }

  @Test(expected = TouchingUnassociatedRegionExceptions.class)
  public void testTouchingUnassociatedTile() throws ValidationException {
    when(_board.getSpot(C10)).thenReturn(new Spot(C10).changeToSingleton());
    when(_board.getSpot(E10)).thenReturn(new Spot(E10));
    when(_board.getSpot(D9)).thenReturn(new Spot(D9));
    when(_board.getSpot(D11)).thenReturn(new Spot(D11));
    when(_board.getSpot(D10)).thenReturn(new Spot(D10));
    when(_board.isHotelAvailable(HotelName.AMERICAN)).thenReturn(false);

    _validator.validateGrowHotel(_board, D10);
  }

  @Test
  public void testHotelNeighbor() throws ValidationException {
    when(_board.getSpot(C10)).thenReturn(
        new Spot(C10).changeName(HotelName.AMERICAN));
    when(_board.getSpot(E10)).thenReturn(new Spot(E10));
    when(_board.getSpot(D9)).thenReturn(new Spot(D9));
    when(_board.getSpot(D11)).thenReturn(new Spot(D11));
    when(_board.getSpot(D10)).thenReturn(new Spot(D10));

    assertThat(_validator.validateGrowHotel(_board, D10),
        is(HotelName.AMERICAN));
  }

  @Test
  public void testHotelTwoNeighbor() throws ValidationException {
    when(_board.getSpot(C10)).thenReturn(
        new Spot(C10).changeName(HotelName.AMERICAN));
    when(_board.getSpot(E10)).thenReturn(
        new Spot(E10).changeName(HotelName.AMERICAN));
    when(_board.getSpot(D9)).thenReturn(new Spot(D9));
    when(_board.getSpot(D11)).thenReturn(new Spot(D11));
    when(_board.getSpot(D10)).thenReturn(new Spot(D10));

    assertThat(_validator.validateGrowHotel(_board, D10),
        is(HotelName.AMERICAN));
  }

  @Test(expected = DifferentHotelException.class)
  public void testHotelTwoNeighborDiff() throws ValidationException {
    when(_board.getSpot(C10)).thenReturn(
        new Spot(C10).changeName(HotelName.AMERICAN));
    when(_board.getSpot(E10)).thenReturn(
        new Spot(E10).changeName(HotelName.TOWER));
    when(_board.getSpot(D9)).thenReturn(new Spot(D9));
    when(_board.getSpot(D11)).thenReturn(new Spot(D11));
    when(_board.getSpot(D10)).thenReturn(new Spot(D10));

    _validator.validateGrowHotel(_board, D10);
  }
}
