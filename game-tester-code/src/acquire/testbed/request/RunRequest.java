package acquire.testbed.request;

import java.util.Set;

/**
 * 
 * Stores the information about the run request
 * 
 */
public class RunRequest {

  private final int _turns;
  private final Set<String> _playerNames;

  public int getTurns() {
    return _turns;
  }

  public Set<String> getPlayerNames() {
    return _playerNames;
  }

  public RunRequest(int turns, Set<String> playerNames) {
    super();
    _turns = turns;
    _playerNames = playerNames;
  }

}
