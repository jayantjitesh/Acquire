package acquire.state.money;

import java.util.LinkedHashMap;
import org.apache.log4j.Logger;
import acquire.basic.HotelChainI.HotelName;
import acquire.exception.AcquireRuntimeException;
import acquire.exception.InvalidSharePriceException;

public class PriceList {
  private static final LinkedHashMap<Integer, Integer> _wsPrices = new LinkedHashMap<Integer, Integer>();
  private static final LinkedHashMap<Integer, Integer> _fiapPrices = new LinkedHashMap<Integer, Integer>();
  private static final LinkedHashMap<Integer, Integer> _ctPrices = new LinkedHashMap<Integer, Integer>();
  private static final Logger LOGGER = Logger.getLogger(PriceList.class);

  private PriceList() {

  }

  static {
    setWSPriceMap();
    setFIAPriceMap();
    setCTPriceMap();
  }

  private static void setWSPriceMap() {
    _wsPrices.put(41, 1000);
    _wsPrices.put(31, 900);
    _wsPrices.put(21, 800);
    _wsPrices.put(11, 700);
    _wsPrices.put(6, 600);
    _wsPrices.put(5, 500);
    _wsPrices.put(4, 400);
    _wsPrices.put(3, 300);
    _wsPrices.put(2, 200);
  }

  private static void setFIAPriceMap() {
    _fiapPrices.put(41, 1100);
    _fiapPrices.put(31, 1000);
    _fiapPrices.put(21, 900);
    _fiapPrices.put(11, 800);
    _fiapPrices.put(6, 700);
    _fiapPrices.put(5, 600);
    _fiapPrices.put(4, 500);
    _fiapPrices.put(3, 400);
    _fiapPrices.put(2, 300);
    _fiapPrices.put(0, 200);
  }

  private static void setCTPriceMap() {
    _ctPrices.put(41, 1200);
    _ctPrices.put(31, 1100);
    _ctPrices.put(21, 1000);
    _ctPrices.put(11, 900);
    _ctPrices.put(6, 800);
    _ctPrices.put(5, 700);
    _ctPrices.put(4, 600);
    _ctPrices.put(3, 500);
    _ctPrices.put(2, 400);
    _ctPrices.put(0, 300);
  }

  /**
   * Calculates the current stock price of the given Hotel name
   * 
   * @param name Hotel chain Name
   * @param size Size of the chain
   * @return Current price
   * @throws InvalidSharePriceException There is no price.
   */
  public static int getSharePrice(HotelName name, int size)
      throws InvalidSharePriceException {
    switch (name) {
      case WORLDWIDE:
      case SACKSON:
        return PriceList.getSharePrice(_wsPrices, size, name);
      case FESTIVAL:
      case IMPERIAL:
      case AMERICAN:
        return PriceList.getSharePrice(_fiapPrices, size, name);
      case CONTINENTAL:
      case TOWER:
        return PriceList.getSharePrice(_ctPrices, size, name);
      default:
        throw AcquireRuntimeException.logAndReturn(LOGGER,
            "This should never happen. All valid names are covered.", null);
    }

  }

  /**
   * 
   * @param map Mapping of size to price. Pairings should be inserted in
   *          decreasing size order.
   * @param size Size of the chain.
   * @param name Hotel chain Name.
   * @return Current price.
   * @throws InvalidSharePriceException There is no price.
   */
  private static int getSharePrice(LinkedHashMap<Integer, Integer> map,
      int size, HotelName name) throws InvalidSharePriceException {
    for (int key : map.keySet()) {
      if (size >= key) {
        return map.get(key);
      }
    }
    throw new InvalidSharePriceException(name, size);
  }
}
