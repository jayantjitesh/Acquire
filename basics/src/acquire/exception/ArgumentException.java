package acquire.exception;

/**
 * Exception throw in a class constructor.
 */
public class ArgumentException extends AcquireException {

  public ArgumentException(String message) {
    super(message);
  }
}
