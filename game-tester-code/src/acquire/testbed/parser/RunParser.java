package acquire.testbed.parser;

import java.util.LinkedHashSet;
import java.util.Set;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import acquire.exception.ArgumentException;
import acquire.exception.testbed.ParseException;
import acquire.testbed.request.RunRequest;

/**
 * Parses the buy request in xml format to the <code>BuyRequest</code>
 * 
 */
public class RunParser {
  public static final int MIN_PLAYER = 3;
  public static final int MAX_PLAYER = 6;
  private static final String ROUNDS = "rounds";
  private static final String PLAYER = "player";
  private static final String NAME = "name";
  private static final int MAX_NAME_LENGTH = 20;

  public RunRequest parse(Node node, String xml) throws ParseException,
      ArgumentException {

    NamedNodeMap attrMap = node.getAttributes();
    int rounds = getRounds(attrMap, xml);
    NodeList children = node.getChildNodes();
    Set<String> players = new LinkedHashSet<String>();
    for (int i = 0; i < children.getLength(); i++) {
      Node childNode = children.item(i);
      String nodeName = childNode.getNodeName();
      if (nodeName.equals(PLAYER)) {
        String playerName = getPlayer(childNode.getAttributes(), xml);
        players.add(playerName);
      } else {
        throw new ParseException("Invalid xml element: " + nodeName, xml);
      }
    }
    return new RunRequest(rounds, players);
  }

  /**
   * Retrieves rounds
   * 
   * @param attrMap Attribute mapping.
   * @return rounds
   * @throws ParseException Attribute did not exist.
   */
  private int getRounds(NamedNodeMap attrMap, String xml) throws ParseException {
    Node item = attrMap.getNamedItem(ROUNDS);
    if (item == null) {
      throw new ParseException("Invalid " + ROUNDS + ". One was not provided",
          xml);
    }
    String value = item.getNodeValue();
    int rounds;
    try {
      rounds = Integer.parseInt(value);
      if (rounds < 0) {
        throw new ParseException(
            "The  number of rounds should be non-negative", xml);
      }
    } catch (NumberFormatException ex) {
      throw new ParseException("The  number of rounds were too much", xml);
    }

    return rounds;
  }

  /**
   * Retrieves player name
   * 
   * @param attrMap Attribute mapping.
   * @return Player name
   * @throws ParseException Attribute did not exist.
   */
  private String getPlayer(NamedNodeMap attrMap, String xml)
      throws ParseException {
    Node item = attrMap.getNamedItem(NAME);
    if (item == null) {
      throw new ParseException("Invalid " + NAME + ". One was not provided",
          xml);
    }
    String name = item.getNodeValue();

    if (name.length() > MAX_NAME_LENGTH) {
      throw new ParseException("The name should be of at amost "
          + MAX_NAME_LENGTH, xml);
    }

    return name;
  }
}
