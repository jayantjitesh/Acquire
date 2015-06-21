package acquire.testbed.response;

import java.util.List;
import acquire.state.State;

/**
 * 
 * Stores the information about the place request
 * 
 */
public class DoneResponse extends Response {

  public DoneResponse(List<State> states) {
    super(states, "done");
  }

}
