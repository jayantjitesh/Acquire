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
import acquire.exception.validate.HotelAcquirerNotFoundException;
import acquire.exception.validate.HotelAcquirerNotLargestException;
import acquire.exception.validate.ValidationException;

public class BoardActionValidatorMergeHotelNameTest extends
    BoardActionValidatorTest_ {

  private HotelChain _chain;

  @Test
  public void testNameLargest() throws ValidationException,
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
    when(chain2.getSize()).thenReturn(2);

    assertThat(_validator.validateMergingHotels(_board, D10,
        HotelName.CONTINENTAL), containsInAnyOrder(HotelName.AMERICAN,
        HotelName.CONTINENTAL));
  }

  @Test
  public void testNameSameSize() throws ValidationException,
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
    when(_chain.getSize()).thenReturn(2);
    HotelChain chain2 = mock(HotelChain.class);
    when(_board.getHotelChain(HotelName.CONTINENTAL)).thenReturn(chain2);
    when(chain2.getSize()).thenReturn(2);

    assertThat(_validator.validateMergingHotels(_board, D10,
        HotelName.CONTINENTAL), containsInAnyOrder(HotelName.AMERICAN,
        HotelName.CONTINENTAL));
  }

  @Test(expected = HotelAcquirerNotLargestException.class)
  public void testNameSmallerSize() throws ValidationException,
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
    when(_chain.getSize()).thenReturn(5);
    HotelChain chain2 = mock(HotelChain.class);
    when(_board.getHotelChain(HotelName.CONTINENTAL)).thenReturn(chain2);
    when(chain2.getSize()).thenReturn(2);

    _validator.validateMergingHotels(_board, D10, HotelName.CONTINENTAL);
  }

  @Test(expected = HotelAcquirerNotFoundException.class)
  public void testNameNotFound() throws ValidationException,
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
    when(_chain.getSize()).thenReturn(2);
    HotelChain chain2 = mock(HotelChain.class);
    when(_board.getHotelChain(HotelName.CONTINENTAL)).thenReturn(chain2);
    when(chain2.getSize()).thenReturn(2);
    HotelChain chain3 = mock(HotelChain.class);
    when(_board.getHotelChain(HotelName.TOWER)).thenReturn(chain3);

    _validator.validateMergingHotels(_board, D10, HotelName.TOWER);
  }
}
