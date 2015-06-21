package acquire.testbed.xmldata;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import acquire.state.StateI;
import acquire.testbed.XMLSerializer;

public class StateXml implements XMLData {
  private final StateI _state;

  public StateXml(StateI state) {
    _state = state;

  }

  public StateI getState() {
    return _state;
  }

  @Override
  public Element generateXML(Document doc) {
    XMLSerializer serializer = new XMLSerializer();
    return serializer.serialize(_state, doc);
  }

  @Override
  public String getTag() {
    return TagType.STATE;
  }
}
