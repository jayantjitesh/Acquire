package acquire.testbed.response;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * Stores the information about the Error response
 * 
 */
public class ErrorResponse {

  private final String _msg;

  public ErrorResponse(String msg) {
    _msg = msg;
  }

  public Element generateXML(Document doc) {
    Element element = doc.createElement("error");
    element.setAttribute("msg", _msg);
    return element;
  }

}
