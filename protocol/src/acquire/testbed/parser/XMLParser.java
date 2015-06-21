package acquire.testbed.parser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import acquire.basic.HotelChain;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.basic.StockDescription;
import acquire.board.Board;
import acquire.board.BoardWithNoValidation;
import acquire.board.Spot;
import acquire.enforcer.tile.OrderedTileSelection;
import acquire.exception.ArgumentException;
import acquire.exception.InvalidMoneyException;
import acquire.exception.InvalidPlayerNameException;
import acquire.exception.InvalidStockCountException;
import acquire.exception.InvalidTileSetException;
import acquire.exception.testbed.ParseException;
import acquire.exception.testbed.RepeatedLocationException;
import acquire.state.State;
import acquire.state.player.PlayerState;
import acquire.testbed.xmldata.XMLData;

/**
 * 
 * Base class for parsing all of the requests. It contains some common parsing
 * 
 */
public abstract class XMLParser {
  protected static final String ROW = "row";
  protected static final String COLUMN = "column";
  protected static final String NAME = "name";
  protected static final String LABEL = "label";
  protected static final String TILE = "tile";
  protected static final String HOTEL = "hotel";
  protected static final String BOARD = "board";
  protected static final String PLAYER = "player";
  protected static final String CASH = "cash";
  protected static final String SHARE = "share";
  protected static final String COUNT = "count";
  protected static final String STATE = "state";

  protected final String _tag;

  public XMLParser(String tag) {
    _tag = tag;
  }

  public boolean matches(String tag) {
    return _tag.equals(tag);
  }

  public abstract XMLData parse(Node node, String xml) throws ParseException,
      ArgumentException;

  /**
   * Converts a given stateNode to the <code>State</code> object
   * 
   * @param stateNode XML representation.
   * @param xml content that sourced the element.
   * @return State of xml
   * @throws ParseException XML could not be parsed correctly.
   */

  protected State parseState(Node stateNode, String xml) throws ParseException {

    NodeList children = stateNode.getChildNodes();
    Map<String, PlayerState> players = new HashMap<String, PlayerState>();
    Queue<String> playerNames = new ArrayDeque<String>();
    Board board = null;
    for (int i = 0; i < children.getLength(); i++) {
      Node node = children.item(i);
      String name = node.getNodeName();
      if (name.equals(BOARD)) {
        board = parseBoard(node, xml);
      } else if (name.equals(PLAYER)) {
        PlayerState player = parsePlayer(node, xml);
        players.put(player.getName(), player);
        playerNames.add(player.getName());
      } else {
        throw new ParseException("Invalid xml element: " + name, xml);
      }
    }

    return new State(board, playerNames, players, new OrderedTileSelection());
  }

  /**
   * Converts a given playerNode to the <code>Player</code> object
   * 
   * @param playerNode XML representation.
   * @param xml content that sourced the element.
   * @return State of xml
   * @throws ParseException XML could not be parsed correctly.
   */

  public PlayerState parsePlayer(Node playerNode, String xml)
      throws ParseException {
    NamedNodeMap attrMap = playerNode.getAttributes();
    String name = getAttributeValue(attrMap, NAME, NAME, xml);
    String cash = getAttributeValue(attrMap, CASH, CASH, xml);
    int money = Integer.parseInt(cash);

    NodeList children = playerNode.getChildNodes();
    Set<Location> tilesInHand = new HashSet<Location>();
    List<StockDescription> stocks = new ArrayList<StockDescription>();

    for (int i = 0; i < children.getLength(); i++) {
      Node node = children.item(i);
      String nodeName = node.getNodeName();
      if (nodeName.equals(SHARE)) {
        stocks.add(parseStock(node, xml));
      } else if (nodeName.equals(TILE)) {
        tilesInHand.add(parseTile(node, xml));
      } else {
        throw new ParseException("Invalid xml element: " + nodeName, xml);
      }
    }

    try {
      PlayerState player = new PlayerState(name, money, tilesInHand);
      for (StockDescription stockDescription : stocks) {
        player.setStock(stockDescription);
      }
      return player;
    } catch (InvalidMoneyException ex) {
      throw new ParseException(ex.getMessage(), xml);
    } catch (InvalidTileSetException ex) {
      throw new ParseException(ex.getMessage(), xml);
    } catch (InvalidPlayerNameException ex) {
      throw new ParseException(ex.getMessage(), xml);
    }

  }

  protected StockDescription parseStock(Node stockNode, String xml)
      throws ParseException {
    try {
      NamedNodeMap attrMap = stockNode.getAttributes();
      String name = getAttributeValue(attrMap, NAME, NAME, xml);
      String count = getAttributeValue(attrMap, COUNT, COUNT, xml);

      return new StockDescription(HotelName.valueFrom(name),
          Integer.parseInt(count));
    } catch (InvalidStockCountException ex) {
      throw new ParseException(ex.getMessage(), xml);
    }
  }

  /**
   * Convert board xml to <code>Board</code>.
   * 
   * @param boardNode XML representation.
   * @param xml Content that sourced the element.
   * @return Board of xml.
   * @throws ParseException XML could not be parsed correctly.
   */
  protected Board parseBoard(Node boardNode, String xml) throws ParseException {
    NodeList children = boardNode.getChildNodes();
    Set<Location> singletons = new HashSet<Location>();
    Map<HotelName, HotelChain> hotels = new HashMap<HotelName, HotelChain>();
    for (int i = 0; i < children.getLength(); i++) {
      Node node = children.item(i);
      String name = node.getNodeName();
      if (name.equals(TILE)) {
        Location location = parseTile(node, xml);
        if (!singletons.add(location)) {
          throw new RepeatedLocationException(location, xml);
        }
      } else if (name.equals(HOTEL)) {
        HotelChain hotelChain = parseHotel(node, xml);
        hotels.put(hotelChain.getName(), hotelChain);
      } else {
        throw new ParseException("Invalid xml element: " + name, xml);
      }
    }
    Map<Location, Spot> spots = Board.generateBoardMap();
    for (Location location : singletons) {
      spots.put(location, new Spot(location).changeToSingleton());
    }

    for (HotelName name : hotels.keySet()) {
      HotelChain hotelChain = hotels.get(name);
      for (Location location : hotelChain.getLocations()) {
        spots.put(location, new Spot(location).changeName(name));
      }
    }
    Board board = new BoardWithNoValidation(hotels, spots);
    return board;
  }

  /**
   * Convert hotel xml to <code>HotelChain</code>.
   * 
   * @param hotelNode XML representation.
   * @param xml Content that sourced the element.
   * @return Hotel chain of xml.
   * @throws ParseException XML could not be parsed correctly.
   */
  protected HotelChain parseHotel(Node hotelNode, String xml)
      throws ParseException {
    NamedNodeMap attrMap = hotelNode.getAttributes();
    String name = getAttributeValue(attrMap, NAME, NAME, xml);
    NodeList locationNodes = hotelNode.getChildNodes();
    Set<Location> locations = new HashSet<Location>();
    for (int i = 0; i < locationNodes.getLength(); i++) {
      Node tileNode = locationNodes.item(i);
      Location location = parseTile(tileNode, xml);
      if (!locations.add(location)) {
        throw new RepeatedLocationException(location, xml);
      }
    }
    try {
      return new HotelChain(HotelName.valueFrom(name), locations);
    } catch (IllegalArgumentException ex) {
      throw new ParseException(ex.getMessage(), xml);
    } catch (ArgumentException ex) {
      throw new ParseException(ex.getMessage(), xml);
    }
  }

  /**
   * Convert tile xml to <code>Location</code>.
   * 
   * @param tileNode XML representation.
   * @param xml Content that sourced the element.
   * @return Location of tile.
   * @throws ParseException XML could not be parsed correctly.
   */
  protected Location parseTile(Node tileNode, String xml) throws ParseException {
    NamedNodeMap attrMap = tileNode.getAttributes();
    String row = getAttributeValue(attrMap, ROW, ROW, xml);
    String column = getAttributeValue(attrMap, COLUMN, COLUMN, xml);
    try {
      return new Location(Row.valueOf(row), Column.valueFrom(column));
    } catch (IllegalArgumentException ex) {
      throw new ParseException(ex.getMessage(), xml);
    }
  }

  /**
   * Retrieves value of attribute.
   * 
   * @param attrMap Attribute mapping.
   * @param name Attribute name.
   * @param type Description of value being tested.
   * @param xml Content that sourced the element.
   * @return Value of attribute.
   * @throws ParseException Attribute did not exist.
   */
  protected String getAttributeValue(NamedNodeMap attrMap, String name,
      String type, String xml) throws ParseException {
    Node item = attrMap.getNamedItem(name);
    if (item == null) {
      throw new ParseException("Invalid " + type + ". One was not provided",
          xml);
    }
    return item.getNodeValue();
  }
}
