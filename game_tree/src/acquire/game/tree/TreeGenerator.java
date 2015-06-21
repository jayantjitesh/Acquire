package acquire.game.tree;

import acquire.state.State;

/**
 * 
 * Used to generate the Tree to represent Acquire game
 * 
 */
public class TreeGenerator {

  public static AdminTree generateAdminTree(State state) {
    return new AdminTree(state);
  }

  public static PlayerTree generatePlayerTree(State state) {
    return new PlayerTree(state);
  }

}
