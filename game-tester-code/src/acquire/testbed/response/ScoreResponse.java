package acquire.testbed.response;

import java.util.List;
import acquire.state.State;

/**
 * 
 * Stores the information about the place request
 * 
 */
public class ScoreResponse extends Response {

  public ScoreResponse(List<State> states) {
    super(states, "score");
  }

}
