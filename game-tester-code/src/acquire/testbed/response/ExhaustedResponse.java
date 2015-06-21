package acquire.testbed.response;

import java.util.List;
import acquire.state.State;

/**
 * 
 * Stores the information about the place request
 * 
 */
public class ExhaustedResponse extends Response {

  public ExhaustedResponse(List<State> states) {
    super(states, "exhausted");
  }

}
