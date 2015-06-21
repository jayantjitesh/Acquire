package acquire.game.tree.exception;

import acquire.exception.AcquireException;
import acquire.game.tree.Edge;

public class InvalidEdgeException extends AcquireException {

  public InvalidEdgeException(Edge e) {
    super("The edge:" + e.toString() + " doesn't belong to this tree");
  }

}
