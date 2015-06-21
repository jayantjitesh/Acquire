package acquire.board.validate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.Test;
import acquire.basic.HotelChain;
import acquire.basic.HotelChainI.HotelName;
import acquire.board.Spot;
import acquire.exception.ChainNotFoundException;
import acquire.exception.validate.NonEmptyLocationException;
import acquire.exception.validate.NotTouchingHotelException;
import acquire.exception.validate.TouchingSafeHotelException;
import acquire.exception.validate.ValidationException;

public class BoardActionValidatorMergeHotelTest extends
    BoardActionValidatorTest_ {

  private HotelChain _chain;

  @Test(expected = NonEmptyLocationException.class)
  public void testSpotNonEmpty() throws ValidationException {
    when(_board.getSpot(C10)).thenReturn(new Spot(C10).changeToSingleton());
    when(_board.getSpot(E10)).thenReturn(new Spot(E10));
    when(_board.getSpot(D9)).thenReturn(new Spot(D9));
    when(_board.getSpot(D11)).thenReturn(new Spot(D11));
    when(_board.getSpot(D10)).thenReturn(
        new Spot(D10).changeName(HotelName.AMERICAN));

    _validator.validateMergingHotels(_board, D10);
  }

  @Test(expected = NotTouchingHotelException.class)
  public void testNoNeigbors() throws ValidationException {
    when(_board.getSpot(C10)).thenReturn(new Spot(C10));
    when(_board.getSpot(E10)).thenReturn(new Spot(E10));
    when(_board.getSpot(D9)).thenReturn(new Spot(D9));
    when(_board.getSpot(D11)).thenReturn(new Spot(D11));
    when(_board.getSpot(D10)).thenReturn(new Spot(D10));

    _validator.validateMergingHotels(_board, D10);
  }

  @Test(expected = NotTouchingHotelException.class)
  public void testNoHotelNeigbors() throws ValidationException,
      ChainNotFoundException {
    when(_board.getSpot(C10)).thenReturn(
        new Spot(C10).changeName(HotelName.AMERICAN));
    when(_board.getSpot(E10)).thenReturn(new Spot(E10));
    when(_board.getSpot(D9)).thenReturn(new Spot(D9));
    when(_board.getSpot(D11)).thenReturn(new Spot(D11));
    when(_board.getSpot(D10)).thenReturn(new Spot(D10));
    _chain = mock(HotelChain.class);
    when(_board.getHotelChain(HotelName.AMERICAN)).thenReturn(_chain);

    _validator.validateMergingHotels(_board, D10);
  }

  @Test(expected = NotTouchingHotelException.class)
  public void testHotelNeighborSame() throws ValidationException,
      ChainNotFoundException {
    when(_board.getSpot(C10)).thenReturn(
        new Spot(C10).changeName(HotelName.AMERICAN));
    when(_board.getSpot(E10)).thenReturn(
        new Spot(E10).changeName(HotelName.AMERICAN));
    when(_board.getSpot(D9)).thenReturn(new Spot(D9));
    when(_board.getSpot(D11)).thenReturn(new Spot(D11));
    when(_board.getSpot(D10)).thenReturn(new Spot(D10));
    _chain = mock(HotelChain.class);
    when(_board.getHotelChain(HotelName.AMERICAN)).thenReturn(_chain);

    _validator.validateMergingHotels(_board, D10);
  }

  @Test
  public void testHotelNeighbor() throws ValidationException,
      ChainNotFoundException {
    when(_board.getSpot(C10)).thenReturn(
        new Spot(C10).changeName(HotelName.AMERICAN));
    when(_board.getSpot(E10)).thenReturn(
        new Spot(E10).changeName(HotelName.CONTINENTAL));
    when(_board.getSpot(D9)).thenReturn(new Spot(D9));
    when(_board.getSpot(D11)).thenReturn(new Spot(D11));
    when(_board.getSpot(D10)).thenReturn(new Spot(D10));
    _chain = mock(HotelChain.class);
    when(_board.getHotelChain(HotelName.AMERICAN)).thenReturn(_chain);
    HotelChain chain2 = mock(HotelChain.class);
    when(_board.getHotelChain(HotelName.CONTINENTAL)).thenReturn(chain2);

    assertThat(_validator.validateMergingHotels(_board, D10),
        containsInAnyOrder(HotelName.AMERICAN, HotelName.CONTINENTAL));
  }

  @Test(expected = TouchingSafeHotelException.class)
  public void testHotelTwoNeighborDiff() throws ValidationException,
      ChainNotFoundException {
    when(_board.getSpot(C10)).thenReturn(
        new Spot(C10).changeName(HotelName.AMERICAN));
    when(_board.getSpot(E10)).thenReturn(
        new Spot(E10).changeName(HotelName.CONTINENTAL));
    when(_board.getSpot(D9)).thenReturn(new Spot(D9));
    when(_board.getSpot(D11)).thenReturn(new Spot(D11));
    when(_board.getSpot(D10)).thenReturn(new Spot(D10));
    _chain = mock(HotelChain.class);
    when(_board.getHotelChain(HotelName.AMERICAN)).thenReturn(_chain);
    HotelChain chain2 = mock(HotelChain.class);
    when(_board.getHotelChain(HotelName.CONTINENTAL)).thenReturn(chain2);
    when(chain2.isSafe()).thenReturn(true);

    _validator.validateMergingHotels(_board, D10);
  }
}
