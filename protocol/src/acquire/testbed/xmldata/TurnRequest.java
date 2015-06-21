package acquire.testbed.xmldata;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import acquire.state.perspective.PlayerPerspective;
import acquire.testbed.XMLSerializer;

/**
 * 
 * Stores the information about the a state that's used by other request having
 * state inside it
 * 
 */
public class TurnRequest implements XMLData {

  private final PlayerPerspective _perspective;

  public TurnRequest(PlayerPerspective perspective) {
    _perspective = perspective;
  }

  public PlayerPerspective getPerspective() {
    return _perspective;
  }

  @Override
  public Element generateXML(Document doc) {
    XMLSerializer serializer = new XMLSerializer();
    return serializer.serialize(_perspective, doc);
  }

  @Override
  public String getTag() {
    return TagType.TURN;
  }

}
