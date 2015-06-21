package acquire.exception.testbed;

import org.apache.log4j.Level;
import acquire.exception.AcquireException;

public class ParseException extends AcquireException {
  private final String _xml;
  private final Level _level;

  public ParseException(String msg, String xml) {
    super(msg);
    _xml = xml;
    _level = Level.INFO;
  }

  public ParseException(String msg, String xml, Throwable cause) {
    super(msg, cause);
    _xml = xml;
    _level = Level.ERROR;
  }

  public String getLogMsg() {
    return getMessage() + "|" + _xml + "|";
  }

  public Level getLogLevel() {
    return _level;
  }
}
