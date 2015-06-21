package acquire.testbed;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import acquire.basic.HotelChainI;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.basic.StockDescription;
import acquire.board.BoardI;
import acquire.board.Spot;
import acquire.board.Spot.SpotType;
import acquire.board.inspect.FoundingResult;
import acquire.board.inspect.GrowResult;
import acquire.board.inspect.ImposibleResult;
import acquire.board.inspect.MergeResult;
import acquire.board.inspect.SingletonResult;
import acquire.exception.testbed.SerializationException;
import acquire.state.StateI;
import acquire.state.perspective.EndPerspective;
import acquire.state.perspective.PlayerPerspective;
import acquire.state.player.MyPlayerState;
import acquire.state.player.PlayerState;

public class XMLSerializer implements AcquireSerilizer<Element> {

  @Override
  public Element serialize(BoardI board, Document doc) {
    List<Location> singletons = new ArrayList<Location>();
    for (Spot spot : board.getSpots().values()) {
      if (!spot.isEmpty() && spot.getSpotType() == SpotType.NONE) {
        singletons.add(spot.getLocation());
      }
    }

    Element element = doc.createElement("board");
    Collections.sort(singletons);
    for (Location location : singletons) {
      element.appendChild(serialize(location, doc));
    }

    List<HotelChainI> chains = new ArrayList<HotelChainI>(board
        .getHotelChains().values());
    Collections.sort(chains);
    for (HotelChainI hotelChain : chains) {
      element.appendChild(serialize(hotelChain, doc));
    }
    return element;
  }

  @Override
  public Element serialize(HotelChainI hotelChain, Document doc) {
    Element element = doc.createElement("hotel");
    element.setAttribute("name", hotelChain.getName().toString());
    List<Location> ordered = new ArrayList<Location>(hotelChain.getLocations());
    Collections.sort(ordered);
    for (Location location : ordered) {
      element.appendChild(serialize(location, doc));
    }
    return element;
  }

  @Override
  public Element serialize(Location location, Document doc) {
    // this is a xml representation for tile
    // For us tile is nothing but location
    Element element = doc.createElement("tile");
    element.setAttribute("column",
        Integer.toString(location.getCol().getValue()));
    element.setAttribute("row", location.getRow().toString());
    return element;
  }

  @Override
  public Element serialize(StockDescription stockDescription, Document doc) {
    Element element = doc.createElement("share");
    element.setAttribute("name", stockDescription.getName().toString());
    element
        .setAttribute("count", Integer.toString(stockDescription.getCount()));
    return element;
  }

  @Override
  public Element serialize(StateI state, Document doc) {
    Element element = doc.createElement("state");
    element.appendChild(serialize(state.getBoard(), doc));
    List<PlayerState> players = state.getPlayers();
    Collections.sort(players);
    for (PlayerState player : players) {
      element.appendChild(serialize(player, doc));
    }
    return element;
  }

  @Override
  public Element serialize(EndPerspective endPerspective, Document doc) {
    Element element = doc.createElement("state");
    element.appendChild(serialize(endPerspective.getBoard(), doc));
    List<PlayerState> players = endPerspective.getAllPlayerStates();
    Collections.sort(players);
    for (PlayerState player : players) {
      element.appendChild(serialize(player, doc));
    }
    return element;
  }

  @Override
  public Element serialize(PlayerState playerState, Document doc) {
    Element element = doc.createElement("player");
    element.setAttribute("name", playerState.getName());
    element.setAttribute("cash", Integer.toString(playerState.getMoney()));
    List<StockDescription> stocks = new ArrayList<StockDescription>(playerState
        .getStockOptions().values());
    Collections.sort(stocks);
    for (StockDescription stock : stocks) {
      element.appendChild(serialize(stock, doc));
    }
    List<Location> ordered = new ArrayList<Location>(
        playerState.getTilesInHand());
    Collections.sort(ordered);
    for (Location location : ordered) {
      element.appendChild(serialize(location, doc));
    }
    return element;
  }

  @Override
  public Element serialize(FoundingResult founding, Document doc) {
    return doc.createElement("founding");
  }

  @Override
  public Element serialize(GrowResult grow, Document doc) {
    Element element = doc.createElement("growing");
    element.setAttribute("name", grow.getName().toString());
    return element;
  }

  @Override
  public Element serialize(ImposibleResult imposible, Document doc) {
    Element element = doc.createElement("impossible");
    element.setAttribute("msg", imposible.getMessage());
    return element;
  }

  @Override
  public Element serialize(MergeResult merge, Document doc) {
    // The XML output format does not support shared size acquirers.
    // Therefore, we will always uses the first largest as the acquirer.

    Set<HotelName> largest = merge.getLargest();
    HotelName acquierer = largest.toArray(new HotelName[largest.size()])[0];

    Element element = doc.createElement("merging");
    element.setAttribute("acquirer", acquierer.toString());
    int i = 1;
    for (HotelName name : merge.getHotels()) {
      if (name == acquierer) {
        continue;
      }
      element.setAttribute("acquired" + i, name.toString());
      i++;
    }
    return element;
  }

  @Override
  public Element serialize(SingletonResult singleton, Document doc) {
    return doc.createElement("singleton");
  }

  /**
   * Creates a new XML document
   * 
   * @return a new XML document
   * @throws SerializationException if the document can't be created
   */
  public static Document newDocument() {
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      return docBuilder.newDocument();
    } catch (ParserConfigurationException ex) {
      throw new SerializationException("Could not create document.", ex);
    }
  }

  /**
   * Converts a given XML doc to string
   * 
   * @param doc the given XML documents
   * @return the string representation
   * @throws TransformerException if not able to convert to string
   */
  public static String toXML(Document doc) throws TransformerException {
    TransformerFactory transFactory = TransformerFactory.newInstance();
    Transformer transformer = transFactory.newTransformer();
    StringWriter buffer = new StringWriter();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    transformer.transform(new DOMSource(doc), new StreamResult(buffer));
    return buffer.toString();
  }

  @Override
  public Element serialize(PlayerPerspective perspective, Document doc) {
    Element element = doc.createElement("turn");
    element.appendChild(serialize(perspective.getBoard(), doc));
    List<PlayerState> players = perspective.getAllPlayers();
    MyPlayerState currentPlayerState = perspective.getMyself();
    players.remove(currentPlayerState);
    element.appendChild(serialize((PlayerState) currentPlayerState, doc));
    // Collections.sort(players);
    for (PlayerState player : players) {
      element.appendChild(serialize(player, doc));
    }
    return element;
  }
}
