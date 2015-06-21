package acquire.testbed;

import java.io.IOException;
import java.io.Writer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import acquire.testbed.response.ErrorResponse;
import acquire.testbed.response.Response;

/**
 * 
 * It will write the xml resonse to the outputstream.
 * 
 */
public class WriteOut {
  private static final Logger LOGGER = Logger.getLogger(WriteOut.class);
  private final Writer _output;

  public WriteOut(Writer output) {
    _output = output;
  }

  /**
   * Write the response to the output and log any errors.
   * 
   * @param response Response to write.
   */
  public void writeOut(Response response) {
    String xml = null;
    XMLSerializer serializer = new XMLSerializer();
    try {
      Document document = XMLSerializer.newDocument();
      document.appendChild(response.generateXML(document));
      xml = XMLSerializer.toXML(document);

      LOGGER.info(xml);
      try {
        _output.write(xml + "\n");
        _output.flush();
      } catch (IOException ex) {
        LOGGER.error("Failed to write out response. \"" + xml + "\"", ex);
      }
    } catch (TransformerConfigurationException ex) {
      LOGGER.fatal("Unable to generate XML", ex);
    } catch (TransformerException ex) {
      LOGGER.fatal("Unable to generate XML", ex);
    }
  }

  public void writeError(ErrorResponse response) {
    String xml = null;
    XMLSerializer serializer = new XMLSerializer();
    try {
      Document document = XMLSerializer.newDocument();
      document.appendChild(response.generateXML(document));
      xml = XMLSerializer.toXML(document);

      LOGGER.info(xml);
      try {
        _output.write(xml + "\n");
        _output.flush();
      } catch (IOException ex) {
        LOGGER.error("Failed to write out response. \"" + xml + "\"", ex);
      }
    } catch (TransformerConfigurationException ex) {
      LOGGER.fatal("Unable to generate XML", ex);
    } catch (TransformerException ex) {
      LOGGER.fatal("Unable to generate XML", ex);
    }
  }
}
