package acquire.game.tree;

import acquire.game.tree.exception.FinalStateException;
import acquire.game.tree.exception.NotFinalStateException;
import acquire.state.State;
import acquire.state.perspective.EndPerspective.Cause;

/**
 * The Tree interface with the basic methods
 */

public interface TreeI {
  /**
   * Return the current state of the game
   * 
   * @return current state
   */
  State getNodeValue();

  /**
   * Validate the given edge e belongs to the current player
   * 
   * @param e the given edge
   * @return true if e belongs to the current player
   * @throws FinalStateException if the current state is final state
   */

  boolean validateEdge(Edge e) throws FinalStateException;

  /**
   * 
   * @return if true then current state is end of game
   */
  boolean isFinalState();

  /**
   * Returns the reason to reach the final state
   * 
   * @return <code>Cause</code> the reason to reach the final state
   * @throws NotFinalStateException if the current state is not a final state
   */

  Cause getReasonForFinalState() throws NotFinalStateException;

}
