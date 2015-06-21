package acquire.exception;

public class AcquireException extends Exception {

  public AcquireException(String message, Throwable cause) {
    super(message, cause);
  }

  public AcquireException(String message) {
    super(message);
  }

}
