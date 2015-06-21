package acquire.state.money;

import acquire.basic.HotelChainI.HotelName;
import acquire.exception.InvalidSharePriceException;

public interface BankerI {

  /**
   * Retrieve the number of available shares for a hotel.
   * 
   * @param name Hotel to get count for.
   * @return Count of available shares (between 0 and
   *         <code>StockDescription.MAX_COUNT</code>
   */
  int getAvailableShareCount(HotelName name);

  /**
   * Retrieve price of a share for a hotel chain;
   * 
   * @param hotelName HotelName to get share price for.
   * @param size the current size of the Hotel
   * @return Current price of a share.
   * @throws InvalidSharePriceException if the price is not listed
   */
  int getSharePrice(HotelName hotelName, int size)
      throws InvalidSharePriceException;

}