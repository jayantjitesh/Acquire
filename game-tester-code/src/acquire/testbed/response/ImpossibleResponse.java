package acquire.testbed.response;

import java.util.List;
import acquire.state.State;

/**
 * 
 * Stores the information about the place request
 * 
 */
public class ImpossibleResponse extends Response {

  public ImpossibleResponse(List<State> states) {
    super(states, "impossible");
  }

}
