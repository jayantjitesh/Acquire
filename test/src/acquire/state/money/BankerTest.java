package acquire.state.money;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.StockDescription;
import acquire.exception.InvalidHotelPurchaseException;
import acquire.exception.InvalidSharePriceException;
import acquire.exception.InvalidStockCountException;

public class BankerTest {
  private Banker _banker;

  @Before
  public void setUp() throws Exception {
    _banker = new Banker();
  }

  @Test
  public void testInitializeStockOptions() {
    Map<HotelName, StockDescription> initializeStockOptions = Banker
        .initializeStockOptions();

    Set<HotelName> keySet = initializeStockOptions.keySet();
    assertThat(keySet.size(), is(7));

    for (HotelName hotelName : keySet) {

      StockDescription stockDescription = initializeStockOptions.get(hotelName);
      assertThat(stockDescription.getCount(), is(StockDescription.MAX_COUNT));
    }
  }

  @Test
  public void testPurchaseStocks() throws InvalidStockCountException,
      InvalidHotelPurchaseException {
    StockDescription stockRequest = new StockDescription(HotelName.AMERICAN, 3);
    _banker.purchaseStocks(stockRequest);
    assertThat(_banker.getAvailableShareCount(HotelName.AMERICAN),
        is(StockDescription.MAX_COUNT - 3));
  }

  @Test(expected = InvalidHotelPurchaseException.class)
  public void testPurchaseStocksInsufficientStock()
      throws InvalidStockCountException, InvalidHotelPurchaseException {
    StockDescription stockRequest = new StockDescription(HotelName.AMERICAN, 22);
    _banker.purchaseStocks(stockRequest);
    stockRequest = new StockDescription(HotelName.AMERICAN, 4);
    _banker.purchaseStocks(stockRequest);

  }

  @Test
  public void testGetSharePriceExactMatch() throws InvalidSharePriceException {
    assertThat(_banker.getSharePrice(HotelName.AMERICAN, 2), is(300));
  }

  @Test
  public void testGetSharePrice() throws InvalidSharePriceException {
    assertThat(_banker.getSharePrice(HotelName.TOWER, 8), is(800));
  }

  @Test
  public void testGetSharePriceNotFoundedHotel()
      throws InvalidSharePriceException {
    assertThat(_banker.getSharePrice(HotelName.TOWER, 0), is(300));
  }

  @Test(expected = InvalidSharePriceException.class)
  public void testGetSharePriceException() throws InvalidSharePriceException {
    _banker.getSharePrice(HotelName.WORLDWIDE, 0);
  }
}
