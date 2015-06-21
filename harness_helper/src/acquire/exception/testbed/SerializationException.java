package acquire.exception.testbed;

import acquire.exception.AcquireRuntimeException;

public class SerializationException extends AcquireRuntimeException {

  public SerializationException(String message, Throwable cause) {
    super(message, cause);
  }

  public SerializationException(String message) {
    super(message);
  }

}
