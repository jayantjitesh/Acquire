package acquire.state.player;

import java.util.Map;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.StockDescription;

public interface PlayerStateI extends Comparable<PlayerStateI> {

  /**
   * Max number of tiles a player can have.
   */
  public static final int MAX_TILES = 6;
  /**
   * Max characters a player name can have.
   */
  public static final int MAX_NAME_LENGTH = 20;

  /**
   * @return Name of this player.
   */
  String getName();

  /**
   * @return Non-negative amount of dollars this player has.
   */
  int getMoney();

  /**
   * @return Mapping of hotel names to stock owned by this player at the time of
   *         this call.
   */
  Map<HotelName, StockDescription> getStockOptions();

}