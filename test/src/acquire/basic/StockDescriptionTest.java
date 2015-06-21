package acquire.basic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.Test;
import acquire.basic.HotelChainI.HotelName;
import acquire.exception.InvalidStockCountException;

public class StockDescriptionTest {
  StockDescription _stockDescription;

  @Test
  public void testConstructorWithName() {
    _stockDescription = new StockDescription(HotelName.AMERICAN);
    assertThat(_stockDescription.getCount(), is(0));
  }

  @Test
  public void testStockDescriptionHotelNameInt()
      throws InvalidStockCountException {
    _stockDescription = new StockDescription(HotelName.AMERICAN, 20);
    assertThat(_stockDescription.getCount(), is(20));
  }

  @Test(expected = InvalidStockCountException.class)
  public void testStockDescriptionHotelNameIntMaxCountExceed()
      throws InvalidStockCountException {
    _stockDescription = new StockDescription(HotelName.AMERICAN, 27);
  }

  @Test
  public void testChangeCount() throws InvalidStockCountException {
    _stockDescription = new StockDescription(HotelName.AMERICAN);
    StockDescription changeCount = _stockDescription.changeCount(10);
    assertThat(changeCount.getCount(), is(10));
  }

  @Test
  public void testChangeCountNegativeDelta() throws InvalidStockCountException {
    _stockDescription = new StockDescription(HotelName.AMERICAN, 20);
    StockDescription changeCount = _stockDescription.changeCount(-10);
    assertThat(changeCount.getCount(), is(10));
  }

  @Test(expected = InvalidStockCountException.class)
  public void testChangeCountInvalidCount() throws InvalidStockCountException {
    _stockDescription = new StockDescription(HotelName.AMERICAN);
    _stockDescription.changeCount(-10);

  }

  @Test
  public void testValidateCount() throws InvalidStockCountException {
    StockDescription.validateCount(StockDescription.MAX_COUNT);
  }

  @Test(expected = InvalidStockCountException.class)
  public void testValidateCountException() throws InvalidStockCountException {
    StockDescription.validateCount(StockDescription.MAX_COUNT + 1);
  }

}
