package acquire.protocol.description;

/**
 * Explanation of why a request was invalid.
 */
public class ErrorDescription {
  private final String _msg;

  public ErrorDescription(String msg) {
    _msg = msg;
  }

  /**
   * @return Explanation of the error case.
   */
  public String getMessage() {
    return _msg;
  }

}
