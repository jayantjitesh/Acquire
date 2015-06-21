package acquire.testbed.xmldata;

import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import acquire.state.player.PlayerState;
import acquire.testbed.XMLSerializer;

public class PlayersXml implements XMLData {

  private final List<PlayerState> _players;

  public PlayersXml(List<PlayerState> players) {
    _players = players;
  }

  public List<PlayerState> getPlayers() {
    return _players;
  }

  @Override
  public String getTag() {
    return TagType.PLAYERS;
  }

  @Override
  public Element generateXML(Document doc) {
    Element element = doc.createElement(TagType.PLAYERS);
    XMLSerializer serializer = new XMLSerializer();
    for (PlayerState player : _players) {
      Element newChild = serializer.serialize(player, doc);
      element.appendChild(newChild);
    }
    return element;
  }

}
