package acquire.testbed;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import acquire.exception.testbed.ParseException;
import acquire.testbed.request.RunRequest;
import acquire.testbed.response.ErrorResponse;

/**
 * 
 * This class reads the data from the stream and tray to parse it then dispatch
 * to the respective handlers
 * 
 */
public class ParseManager {
  private static final Logger LOGGER = Logger.getLogger(ParseManager.class);
  private static final int BUFFER_SIZE = 5120;
  private final InputStream _inStream;
  private final ParseHandler _handler;
  private final WriteOut _writeOut;
  private final Parser _parser;
  private byte[] _buffer = new byte[BUFFER_SIZE];

  public ParseManager(InputStream inStream, ParseHandler handler,
      WriteOut writeOut, Parser parser) throws ParserConfigurationException {
    _inStream = inStream;
    _handler = handler;
    _writeOut = writeOut;
    _parser = parser;
  }

  /**
   * Reads the stream and try to parse the requests.
   */
  public void beginReading() {
    try {
      StringBuilder builder = new StringBuilder();
      int num;
      while ((num = _inStream.read(_buffer)) != -1) {
        LOGGER.info("read in " + num + " bytes");
        builder.append(new String(Arrays.copyOf(_buffer, num)));
        _buffer = new byte[BUFFER_SIZE];
        LOGGER.info("builder contents|" + builder.toString() + "|");
        String xml = builder.toString();
        List<Node> nodes = null;
        try {
          nodes = _parser.parseToNodes(xml);
        } catch (ParseException ex) {
          LOGGER.info("xml can't be parsed, keep reading.");
          continue;
        }
        // valid xml document, reset builder for new text
        builder = new StringBuilder();
        for (Node node : nodes) {
          try {
            RunRequest request = _parser.parse(node, xml);
            dispatch(request);
          } catch (ParseException ex) {
            LOGGER.log(ex.getLogLevel(), ex.getLogMsg(), ex.getCause());
            _writeOut.writeError(new ErrorResponse(ex.getMessage()));
          }
        }
      }
    } catch (IOException ex) {
      LOGGER.error("Failed to read from input.", ex);
    }
  }

  /**
   * Call the correct callback based on description type.
   * 
   * @param request Request to handle.
   */
  public void dispatch(RunRequest request) {

    _handler.handleRun(request);

  }

}
