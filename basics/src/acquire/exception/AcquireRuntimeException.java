package acquire.exception;

import org.apache.log4j.Logger;

public class AcquireRuntimeException extends RuntimeException {

  public AcquireRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public AcquireRuntimeException(String message) {
    super(message);
  }

  public static void logAndThrow(Logger logger, String msg) {
    logger.fatal(msg);
    throw new AcquireRuntimeException(msg);
  }

  public static void logAndThrow(Logger logger, String msg, Exception cause) {
    logger.fatal(msg, cause);
    throw new AcquireRuntimeException(msg, cause);
  }

  public static AcquireRuntimeException logAndReturn(Logger logger, String msg,
      Exception cause) {
    logger.fatal(msg, cause);
    return new AcquireRuntimeException(msg, cause);
  }
}
