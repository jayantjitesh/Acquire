package acquire.state.money;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.StockDescription;
import acquire.exception.AcquireRuntimeException;
import acquire.exception.ArgumentException;
import acquire.exception.InvalidHotelPurchaseException;
import acquire.exception.InvalidSharePriceException;
import acquire.exception.InvalidStockCountException;
import acquire.exception.InvalidStockReturnException;

/**
 * Contains information about what stocks are available and how much they cost.
 */
public class Banker implements BankerI {
  private static final Logger LOGGER = Logger.getLogger(Banker.class);

  private final Map<HotelName, StockDescription> _availableStocks;

  public Banker() {
    _availableStocks = initializeStockOptions();
  }

  public Banker(Banker oldBanker) {
    _availableStocks = new HashMap<HotelName, StockDescription>();

    for (Entry<HotelName, StockDescription> entry : oldBanker
        .getAvailableStocks().entrySet()) {
      try {
        _availableStocks.put(entry.getKey(), new StockDescription(entry
            .getValue().getName(), entry.getValue().getCount()));
      } catch (InvalidStockCountException ex) {
        AcquireRuntimeException.logAndThrow(LOGGER,
            "This should never happen. "
                + "we are validating current is at least as big as request.",
            ex);
      }
    }

  }

  public Banker(Map<HotelName, StockDescription> stocks) {
    _availableStocks = stocks;
  }

  /**
   * Generate the initial mapping of stocks. All the seven Hotel Names are
   * initialized with MAX_COUNT
   * 
   * @return Mapping of hotel name to stock information.
   */
  public static Map<HotelName, StockDescription> initializeStockOptions() {
    Map<HotelName, StockDescription> stockOptions = new HashMap<HotelName, StockDescription>();
    for (HotelName name : HotelName.values()) {

      try {
        stockOptions.put(name, new StockDescription(name,
            StockDescription.MAX_COUNT));
      } catch (ArgumentException ex) {
        AcquireRuntimeException.logAndThrow(LOGGER,
            "This should not happen. Name is already validated.", ex);
        return null;
      }

    }
    return stockOptions;
  }

  @Override
  public int getAvailableShareCount(HotelName name) {
    return _availableStocks.get(name).getCount();
  }

  @Override
  public int getSharePrice(HotelName hotelName, int size)
      throws InvalidSharePriceException {
    return PriceList.getSharePrice(hotelName, size);
  }

  /**
   * Remove number of stocks from given hotel. Checks if the stock requested is
   * less than or equal to the stocks available
   * 
   * @param stockRequest Description of stocks to buy.
   * @throws InvalidHotelPurchaseException Purchase can not be completed.
   */
  public void purchaseStocks(StockDescription stockRequest)
      throws InvalidHotelPurchaseException {
    StockDescription currStock = _availableStocks.get(stockRequest.getName());
    if (currStock.getCount() < stockRequest.getCount()) {
      throw new InvalidHotelPurchaseException(stockRequest.getName(),
          currStock.getCount(), stockRequest.getCount());
    }
    try {
      _availableStocks.put(currStock.getName(),
          currStock.changeCount(-stockRequest.getCount()));
    } catch (InvalidStockCountException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER, "This should never happen. "
          + "we are validating current is at least as big as request.", ex);
    }
  }

  /**
   * Add number of stocks for a given hotel.
   * 
   * @param stockDescription Description of stocks to return.
   * @throws InvalidStockReturnException Return can not be completed.
   */
  public void returnStocksToBank(StockDescription stockDescription)
      throws InvalidStockReturnException {
    StockDescription currStock = _availableStocks.get(stockDescription
        .getName());
    if (StockDescription.isCountInvalid(currStock.getCount()
        + stockDescription.getCount())) {
      throw new InvalidStockReturnException(stockDescription.getName(),
          currStock.getCount(), stockDescription.getCount());
    }
    try {
      alterStockCount(currStock, stockDescription.getCount());
    } catch (InvalidStockCountException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER, "This should never happen. "
          + "we are validating current is at least as big as request.", ex);
    }
  }

  /**
   * Change the number of avail shares for a hotel.
   * 
   * @param currStock Current available shares
   * @param alter Amount to alter the share count.
   * @throws InvalidStockCountException
   */
  private void alterStockCount(StockDescription currStock, int alter)
      throws InvalidStockCountException {
    _availableStocks.put(currStock.getName(), currStock.changeCount(alter));
  }

  public Map<HotelName, StockDescription> getAvailableStocks() {
    return _availableStocks;
  }
}
