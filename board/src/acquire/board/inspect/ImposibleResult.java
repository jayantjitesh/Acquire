package acquire.board.inspect;


/**
 * If the placement is impossible
 */
public class ImposibleResult implements InspectResult {
  private final String _msg;

  public ImposibleResult(String msg) {
    _msg = msg;
  }

  @Override
  public boolean isValid() {
    return false;
  }

  public String getMessage() {
    return _msg;
  }

}
