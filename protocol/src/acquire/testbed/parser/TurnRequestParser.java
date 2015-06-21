package acquire.testbed.parser;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import acquire.board.Board;
import acquire.enforcer.tile.OrderedTileSelection;
import acquire.exception.ArgumentException;
import acquire.exception.testbed.ParseException;
import acquire.state.State;
import acquire.state.perspective.PlayerPerspective;
import acquire.state.player.MyPlayerState;
import acquire.state.player.PlayerState;
import acquire.testbed.xmldata.TagType;
import acquire.testbed.xmldata.TurnRequest;
import acquire.testbed.xmldata.XMLData;

public class TurnRequestParser extends XMLParser {

  public TurnRequestParser() {
    super(TagType.TURN);
  }

  @Override
  public XMLData parse(Node stateNode, String xml) throws ParseException,
      ArgumentException {
    NodeList children = stateNode.getChildNodes();
    Map<String, PlayerState> players = new HashMap<String, PlayerState>();
    Queue<String> playerNames = new ArrayDeque<String>();
    Board board = null;
    MyPlayerState myState = null;
    for (int i = 0; i < children.getLength(); i++) {
      Node node = children.item(i);
      String name = node.getNodeName();
      if (name.equals(BOARD)) {
        board = parseBoard(node, xml);
      } else if (name.equals(PLAYER)) {
        PlayerState player = parsePlayer(node, xml);
        players.put(player.getName(), player);
        if (playerNames.size() == 0)
          myState = player;
        playerNames.add(player.getName());
      } else {
        throw new ParseException("Invalid xml element: " + name, xml);
      }
    }
    State state = new State(board, playerNames, players,
        new OrderedTileSelection());
    PlayerPerspective perspective = new PlayerPerspective(state, myState, 0);
    return new TurnRequest(perspective);
  }
}
