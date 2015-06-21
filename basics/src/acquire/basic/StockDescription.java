package acquire.basic;

import acquire.basic.HotelChainI.HotelName;
import acquire.exception.InvalidStockCountException;

public class StockDescription implements Comparable<StockDescription> {
  public static final int MAX_COUNT = 25;
  private final HotelName _name;
  private final int _count;

  /**
   * Constructor for a description with zero shares.
   * 
   * @param name Hotel being described.
   * 
   */
  public StockDescription(HotelName name) {

    _name = name;
    _count = 0;
  }

  /**
   * Constructor
   * 
   * @param name Hotel being described.
   * @param count Number of shares for hotel.
   * @throws InvalidStockCountException <code>count</code> is less than 0 or
   *           greater than <code>MAX_COUNT</code>.
   */
  public StockDescription(HotelName name, int count)
      throws InvalidStockCountException {
    validateCount(count);
    _name = name;
    _count = count;
  }

  public HotelName getName() {
    return _name;
  }

  public int getCount() {
    return _count;
  }

  /**
   * Create a new description for this hotel with a different number of shares.
   * 
   * @param delta Number of share the new description should be differ by. A
   *          negative number will produce a description with fewer shares.
   * @return New description of this hotel and new share count.
   * @throws InvalidStockCountException <code>delta</code> would create a count
   *           that is less than 0 or greater than <code>MAX_COUNT</code>.
   */
  public StockDescription changeCount(int delta)
      throws InvalidStockCountException {

    return new StockDescription(_name, _count + delta);

  }

  /**
   * Check if a number is a valid stock count.
   * 
   * @param count Number to check.
   * @return True if <code>count</code> is less than 0 or greater than
   *         <code>MAX_COUNT</code>.
   */
  public static boolean isCountInvalid(int count) {
    return count < 0 || count > MAX_COUNT;
  }

  /**
   * Check if a number is a valid stock count, throwing an exception if it is
   * not.
   * 
   * @param count Number to check.
   * @throws InvalidStockCountException <code>count</code> is less than 0 or
   *           greater than <code>MAX_COUNT</code>.
   */
  public static void validateCount(int count) throws InvalidStockCountException {
    if (isCountInvalid(count)) {
      throw new InvalidStockCountException(count);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + _count;
    result = prime * result + ((_name == null) ? 0 : _name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    StockDescription other = (StockDescription) obj;
    if (_count != other._count) {
      return false;
    }
    if (_name != other._name) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return _name + ":" + _count;
  }

  @Override
  public int compareTo(StockDescription other) {
    return _name.compareTo(other.getName());
  }
}
