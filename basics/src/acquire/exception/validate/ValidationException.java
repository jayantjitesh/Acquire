package acquire.exception.validate;

import acquire.exception.AcquireException;

public class ValidationException extends AcquireException {
  public ValidationException(String msg) {
    super(msg);
  }

  public ValidationException(String msg, Exception cause) {
    super(msg, cause);
  }
}
