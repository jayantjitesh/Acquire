package acquire.protocol;

import java.util.List;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.exception.DuplicateCallException;
import acquire.exception.NotValidAcquirerException;
import acquire.exception.NotValidMergerException;
import acquire.state.player.PlayerState;

/**
 * 
 * The proxy object of the RuleEnforcer
 * 
 */

public interface ProxyRuleEnforcer {
  /**
   * In the case of merger, this method will be used to call this method. Return
   * the current state of player keeping the shares of the acquired hotel
   * 
   * @param loc tile to place
   * @param label the acquirer hotel name
   * @return Return the current state of player keeping the shares of the
   *         acquired hotel
   * @throws NotValidAcquirerException
   * @throws NotValidMergerException
   * @throws DuplicateCallException if this call has been already mad during
   *           this turn
   */
  List<PlayerState> place(Location loc, HotelName label)
      throws NotValidMergerException, NotValidAcquirerException,
      DuplicateCallException;
}
