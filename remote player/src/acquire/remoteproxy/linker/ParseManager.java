package acquire.remoteproxy.linker;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import acquire.exception.testbed.ParseException;
import acquire.testbed.parser.Parser;
import acquire.testbed.xmldata.KeepXml;
import acquire.testbed.xmldata.ScoreXml;
import acquire.testbed.xmldata.StateXml;
import acquire.testbed.xmldata.TagType;
import acquire.testbed.xmldata.TileXml;
import acquire.testbed.xmldata.TurnRequest;
import acquire.testbed.xmldata.XMLData;

/**
 * 
 * This class reads the data from the stream and tray to parse it then dispatch
 * to the respective handlers
 * 
 */
public class ParseManager {
  private static final Logger LOGGER = Logger.getLogger(ParseManager.class);
  private static final int BUFFER_SIZE = 5120;
  private final InputStream inStream;
  private final ParseHandler _handler;
  private final Parser _parser;
  private byte[] _buffer = new byte[BUFFER_SIZE];

  public ParseManager(InputStream inStream, ParseHandler handler, Parser parser)
      throws ParserConfigurationException {
    this.inStream = inStream;
    _handler = handler;
    _parser = parser;
  }

  /**
   * Reads the stream and try to parse the requests.
   */
  public void beginReading() {
    try {
      StringBuilder builder = new StringBuilder();
      int num;
      while ((num = inStream.read(_buffer)) != -1) {
        LOGGER.info("read in " + num + " bytes");
        System.out.println("read in " + num + " bytes");
        // System.out.println(builder.toString());
        builder.append(new String(Arrays.copyOf(_buffer, num)));
        System.out.println(builder.toString());
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
            XMLData request = _parser.parse(node, xml);
            dispatch(request);
          } catch (ParseException ex) {
            LOGGER.log(ex.getLogLevel(), ex.getLogMsg(), ex.getCause());
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
  public void dispatch(XMLData request) {

    if (request.getTag().equals(TagType.SETUP))
      _handler.handleSetup((StateXml) request);
    else if (request.getTag().equals(TagType.SIGNUP))
      _handler.handleSetup((StateXml) request);
    else if (request.getTag().equals(TagType.TURN))
      _handler.handleTurn((TurnRequest) request);
    else if (request.getTag().equals(TagType.KEEP))
      _handler.handleKeep((KeepXml) request);
    else if (request.getTag().equals(TagType.STATE))
      _handler.handleInform((StateXml) request);
    else if (request.getTag().equals(TagType.TILE))
      _handler.handleNewTile((TileXml) request);
    else if (request.getTag().equals(TagType.SCORE))
      _handler.handleEndGame((ScoreXml) request);
    else
      LOGGER.info("xml can't be parsed, keep reading.");
  }
}
