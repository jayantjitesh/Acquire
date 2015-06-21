package acquire.basic;

import org.apache.log4j.Logger;
import acquire.exception.AcquireRuntimeException;

/**
 * All type enum related to actions.
 */
public class Action {
  private static final Logger LOGGER = Logger.getLogger(Action.class);

  public static enum PlacementType {
    SINGLETON, FOUND, GROW, MERGE;
  }

  public static enum ActionType {
    INITIAL, PURCHASE, SINGLETON, FOUND, GROW, MERGE, KICK, END_TURN;

    public static ActionType fromValue(PlacementType type) {
      switch (type) {
        case SINGLETON:
          return SINGLETON;
        case FOUND:
          return FOUND;
        case GROW:
          return GROW;
        case MERGE:
          return MERGE;
        default:
          throw AcquireRuntimeException.logAndReturn(LOGGER,
              "make compiler happy", null);
      }
    }
  }
}
