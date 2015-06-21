package acquire.protocol.request;

import java.util.ArrayList;
import java.util.List;
import acquire.basic.HotelChainI.HotelName;
import acquire.exception.InvalidPurchaseListException;
import acquire.exception.InvalidPurchaseRequestException;

/**
 * Description of shares a player would like to purchase.<br />
 * A hotel's name should be passed in multiple times if multiple shares are
 * requested.
 * 
 */
public class PurchaseRequest {
  public static final int MAX_SHARES = 2;
  private final List<HotelName> _names = new ArrayList<HotelName>();

  /**
   * Request to buy no shares.
   * 
   * @throws InvalidPurchaseRequestException
   */
  // public PurchaseRequest() {
  // this(null, null, null);
  // }

  public PurchaseRequest() throws InvalidPurchaseRequestException {
    this(null, null);
  }

  /**
   * Request to buy a single share.
   * 
   * @param name Hotel to buy share for.
   * @throws InvalidPurchaseRequestException
   */
  public PurchaseRequest(HotelName name) throws InvalidPurchaseRequestException {
    // this(name, null, null);
    this(name, null);
  }

  /**
   * Request to buy two shares.
   * 
   * @param name Hotel to buy share for.
   * @param name2 Another hotel to buy share for. Can be same as
   *          <code>name</code>.
   * @throws InvalidPurchaseRequestException if both the share doesn't belong to
   *           same HotelChain
   */
  public PurchaseRequest(HotelName name, HotelName name2)
      throws InvalidPurchaseRequestException {

    if ((null != name && null != name2) && !name.equals(name2))
      throw new InvalidPurchaseRequestException(
          "Both the share should belong to same hotel");
    addToNames(name);
    addToNames(name2);
    // this(name, name2,null);
  }

  /**
   * Request to buy three shares.
   * 
   * @param name Hotel to buy share for.
   * @param name2 Another hotel to buy share for. Can be same as
   *          <code>name</code>.
   * @param name3 Another hotel to buy share for. Can be same as
   *          <code>name</code> or <code>name2</code>.
   */
  // public PurchaseRequest(HotelName name, HotelName name2, HotelName name3) {
  // addToNames(name);
  // addToNames(name2);
  // addToNames(name3);
  // }

  private void addToNames(HotelName name) {
    if (name != null) {
      _names.add(name);
    }
  }

  public List<HotelName> getNames() {
    return new ArrayList<HotelName>(_names);
  }

  /**
   * Creates a
   * 
   * @param labels
   * @return
   * @throws InvalidPurchaseListException
   * @throws InvalidPurchaseRequestException
   */
  public static PurchaseRequest createPurchaseRequest(List<HotelName> labels)
      throws InvalidPurchaseListException, InvalidPurchaseRequestException {
    if (labels.size() > MAX_SHARES)
      throw new InvalidPurchaseListException(labels);

    HotelName[] values = new HotelName[MAX_SHARES];
    for (int i = 0; i < labels.size(); i++) {
      values[i] = labels.get(i);
    }
    // return new PurchaseRequest(values[0], values[1], values[2]);
    return new PurchaseRequest(values[0], values[1]);

  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_names == null) ? 0 : _names.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PurchaseRequest other = (PurchaseRequest) obj;
    if (_names == null) {
      if (other._names != null)
        return false;
    } else if (!_names.equals(other._names))
      return false;
    return true;
  }
}
