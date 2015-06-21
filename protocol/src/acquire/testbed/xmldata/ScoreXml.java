package acquire.testbed.xmldata;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import acquire.state.perspective.EndPerspective;
import acquire.testbed.XMLSerializer;

public class ScoreXml implements XMLData {
  private final EndPerspective _perspective;

  public ScoreXml(EndPerspective perspective) {
    _perspective = perspective;
  }

  public EndPerspective getPerspective() {
    return _perspective;
  }

  @Override
  public Element generateXML(Document doc) {
    Element element = doc.createElement(TagType.SCORE);
    for (String playerName : _perspective.getFinalScore().keySet()) {
      Element scoreElement = doc.createElement("result");
      scoreElement.setAttribute("name", playerName);
      scoreElement.setAttribute("score",
          Integer.toString(_perspective.getFinalScore().get(playerName)));
      element.appendChild(scoreElement);
    }
    XMLSerializer serializer = new XMLSerializer();
    element.appendChild(serializer.serialize(_perspective, doc));
    return element;
  }

  @Override
  public String getTag() {
    return TagType.SCORE;
  }

}
