package acquire.board.validate;

import static org.mockito.Mockito.when;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.board.Spot;
import acquire.exception.InvalidSizeException;
import acquire.exception.validate.CannotAddHotelException;
import acquire.exception.validate.ChainAlreadyExistsExcpetion;
import acquire.exception.validate.NonEmptyLocationException;
import acquire.exception.validate.ValidationException;

public class BoardActionValidatorAddHotelTest extends BoardActionValidatorTest_ {

  @Test(expected = NonEmptyLocationException.class)
  public void testValidateAddHotelNonEmpty()
      throws ChainAlreadyExistsExcpetion, ValidationException {
    when(_board.getSpot(C10)).thenReturn(new Spot(C10).changeToSingleton());
    when(_board.getSpot(E10)).thenReturn(new Spot(E10));
    when(_board.getSpot(D9)).thenReturn(new Spot(D9));
    when(_board.getSpot(D11)).thenReturn(new Spot(D11));
    when(_board.getSpot(D10)).thenReturn(
        new Spot(D10).changeName(HotelName.AMERICAN));
    when(_board.isHotelAvailable(HotelName.AMERICAN)).thenReturn(true);

    _validator.validateAddHotel(_board, D10, HotelName.AMERICAN);
  }

  @Test(expected = ChainAlreadyExistsExcpetion.class)
  public void testValidateAddHotelNameAlreadyExist()
      throws ChainAlreadyExistsExcpetion, ValidationException {
    when(_board.getSpot(C10)).thenReturn(new Spot(C10).changeToSingleton());
    when(_board.getSpot(E10)).thenReturn(new Spot(E10));
    when(_board.getSpot(D9)).thenReturn(new Spot(D9));
    when(_board.getSpot(D11)).thenReturn(new Spot(D11));
    when(_board.getSpot(D10)).thenReturn(new Spot(D10));
    when(_board.isHotelAvailable(HotelName.AMERICAN)).thenReturn(false);

    _validator.validateAddHotel(_board, D10, HotelName.AMERICAN);
  }

  @Test(expected = CannotAddHotelException.class)
  public void testValidateAddHotelHotelNeighbor()
      throws ChainAlreadyExistsExcpetion, ValidationException {
    when(_board.getSpot(C10)).thenReturn(new Spot(C10));
    when(_board.getSpot(E10)).thenReturn(new Spot(E10));
    when(_board.getSpot(D9)).thenReturn(new Spot(D9));
    when(_board.getSpot(D11)).thenReturn(
        new Spot(D11).changeName(HotelName.CONTINENTAL));
    when(_board.getSpot(D10)).thenReturn(new Spot(D10));
    when(_board.isHotelAvailable(HotelName.AMERICAN)).thenReturn(true);

    _validator.validateAddHotel(_board, D10, HotelName.AMERICAN);
  }

  @Test(expected = CannotAddHotelException.class)
  public void testValidateAddHotelNoSingletonNeighbor()
      throws ChainAlreadyExistsExcpetion, ValidationException {
    when(_board.getSpot(C10)).thenReturn(new Spot(C10));
    when(_board.getSpot(E10)).thenReturn(new Spot(E10));
    when(_board.getSpot(D9)).thenReturn(new Spot(D9));
    when(_board.getSpot(D11)).thenReturn(
        new Spot(D11).changeName(HotelName.CONTINENTAL));
    when(_board.getSpot(D10)).thenReturn(new Spot(D10));
    when(_board.isHotelAvailable(HotelName.AMERICAN)).thenReturn(true);

    _validator.validateAddHotel(_board, D10, HotelName.AMERICAN);
  }

  @Test
  public void testValidateAddHotelWithHotelChain()
      throws ChainAlreadyExistsExcpetion, ValidationException,
      InvalidSizeException {
    Set<Location> locations = new HashSet<Location>();
    locations.add(C10);
    locations.add(E10);
    locations.add(D9);
    locations.add(D10);
    when(_board.getSpot(C10)).thenReturn(new Spot(C10));
    when(_board.getSpot(E10)).thenReturn(new Spot(E10));
    when(_board.getSpot(D9)).thenReturn(new Spot(D9));
    when(_board.getSpot(D10)).thenReturn(new Spot(D10));
    when(_board.isHotelAvailable(HotelName.SACKSON)).thenReturn(true);
    _validator.ValidateAddHotel(_board, locations, HotelName.SACKSON);
  }

  @Test(expected = ValidationException.class)
  public void testValidateAddHotelWithHotelChainNotConnected()
      throws ChainAlreadyExistsExcpetion, ValidationException,
      InvalidSizeException {
    Set<Location> locations = new HashSet<Location>();
    locations.add(C10);
    locations.add(E10);
    locations.add(D9);
    locations.add(D11);
    when(_board.getSpot(C10)).thenReturn(new Spot(C10));
    when(_board.getSpot(E10)).thenReturn(new Spot(E10));
    when(_board.getSpot(D9)).thenReturn(new Spot(D9));
    when(_board.getSpot(D11)).thenReturn(new Spot(D11));
    when(_board.isHotelAvailable(HotelName.SACKSON)).thenReturn(true);
    _validator.ValidateAddHotel(_board, locations, HotelName.SACKSON);
  }
}
