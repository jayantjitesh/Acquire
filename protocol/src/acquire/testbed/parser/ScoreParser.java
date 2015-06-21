package acquire.testbed.parser;

import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import acquire.exception.ArgumentException;
import acquire.exception.testbed.ParseException;
import acquire.state.State;
import acquire.state.perspective.EndPerspective;
import acquire.state.perspective.EndPerspective.Cause;
import acquire.testbed.xmldata.ScoreXml;
import acquire.testbed.xmldata.TagType;
import acquire.testbed.xmldata.XMLData;

public class ScoreParser extends XMLParser {
  private static final String RESULT = "result";
  private static final String SCORE = "score";

  public ScoreParser() {
    super(TagType.SCORE);
  }

  @Override
  public XMLData parse(Node node, String xml) throws ParseException,
      ArgumentException {
    NodeList children = node.getChildNodes();
    Map<String, Integer> finalScore = new HashMap<String, Integer>();
    State state = null;
    for (int i = 0; i < children.getLength(); i++) {
      Node item = children.item(i);
      String name = item.getNodeName();
      if (name.equals(STATE)) {
        state = parseState(item, xml);
      } else if (name.equals(RESULT)) {
        NamedNodeMap attributes = item.getAttributes();
        String playerName = attributes.getNamedItem(NAME).getNodeValue();
        String score = attributes.getNamedItem(SCORE).getNodeValue();
        finalScore.put(playerName, Integer.parseInt(score));
      } else {
        throw new ParseException("Invalid xml element: " + name, xml);
      }
    }
    return new ScoreXml(
        new EndPerspective(Cause.FINISHED, state, 0, finalScore));
  }
}