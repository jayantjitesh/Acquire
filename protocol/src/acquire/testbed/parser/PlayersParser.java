package acquire.testbed.parser;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import acquire.exception.ArgumentException;
import acquire.exception.testbed.ParseException;
import acquire.state.player.PlayerState;
import acquire.testbed.xmldata.PlayersXml;
import acquire.testbed.xmldata.TagType;
import acquire.testbed.xmldata.XMLData;

public class PlayersParser extends XMLParser {

  public PlayersParser() {
    super(TagType.PLAYERS);
  }

  @Override
  public XMLData parse(Node node, String xml) throws ParseException,
      ArgumentException {
    List<PlayerState> players = new ArrayList<PlayerState>();
    NodeList childNodes = node.getChildNodes();
    for (int i = 0; i < childNodes.getLength(); i++) {
      Node item = childNodes.item(i);
      if (item.getNodeName().equals(PLAYER)) {
        players.add(parsePlayer(item, xml));
      }

    }
    return new PlayersXml(players);
  }

}
