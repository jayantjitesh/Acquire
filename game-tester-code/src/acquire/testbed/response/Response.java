package acquire.testbed.response;

import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import acquire.state.State;
import acquire.testbed.XMLSerializer;

public abstract class Response {
  private final List<State> _states;
  protected final String _tag;

  public Response(List<State> states, String tag) {
    _states = states;
    _tag = tag;
  }

  public Element generateXML(Document doc) {
    Element element = doc.createElement(_tag);
    XMLSerializer serializer = new XMLSerializer();
    // Collections.reverse(_states);
    for (State state : _states) {
      element.appendChild(serializer.serialize(state, doc));
    }

    return element;
  }
}
