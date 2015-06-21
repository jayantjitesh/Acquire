package acquire.remoteproxy;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.board.BoardI;
import acquire.board.inspect.InspectResult;
import acquire.board.inspect.PossibleResult;
import acquire.exception.AcquireRuntimeException;
import acquire.exception.DuplicateCallException;
import acquire.exception.InvalidMoveExcpetion;
import acquire.exception.NotValidAcquirerException;
import acquire.exception.NotValidMergerException;
import acquire.exception.testbed.ParseException;
import acquire.player.Player;
import acquire.protocol.TurnI;
import acquire.protocol.description.ActionDescription;
import acquire.protocol.description.ErrorDescription;
import acquire.protocol.request.FoundingRequest;
import acquire.protocol.request.GrowingRequest;
import acquire.protocol.request.MergingRequest;
import acquire.protocol.request.PlaceRequest;
import acquire.protocol.request.SingletonRequest;
import acquire.protocol.request.TurnResponse;
import acquire.state.StateI;
import acquire.state.perspective.EndPerspective;
import acquire.state.player.PlayerState;
import acquire.testbed.parser.Parser;
import acquire.testbed.xmldata.KeepDecisionXml;
import acquire.testbed.xmldata.KeepXml;
import acquire.testbed.xmldata.OrderXml;
import acquire.testbed.xmldata.PlaceXml;
import acquire.testbed.xmldata.PlayersXml;
import acquire.testbed.xmldata.ScoreXml;
import acquire.testbed.xmldata.StateXml;
import acquire.testbed.xmldata.TagType;
import acquire.testbed.xmldata.TileXml;
import acquire.testbed.xmldata.TurnRequest;
import acquire.testbed.xmldata.TurnResponseXml;
import acquire.testbed.xmldata.WriteOut;
import acquire.testbed.xmldata.XMLData;
import acquire.testbed.xmldata.XMLReader;

/**
 * Remote object of the Player. It delegates the calls to the actual Player
 * through network
 * 
 */
public class RemotePlayer implements Player {

  private static final Logger LOGGER = Logger.getLogger(RemotePlayer.class);
  private final WriteOut _writeOut;
  private final InputStream _input;
  private String _name;
  private final XMLReader _reader;

  public RemotePlayer(WriteOut writeOut, InputStream input, String name) {
    this(writeOut, input, name, new XMLReader());

  }

  public RemotePlayer(WriteOut writeOut, InputStream input, String name,
      XMLReader reader) {
    _writeOut = writeOut;
    _input = input;
    _name = name;
    _reader = reader;

  }

  @Override
  public String getName() {
    return _name;
  }

  @Override
  public void setName(String name) {
    _name = name;
  }

  @Override
  public void startGame(StateI state) {
    _writeOut.writeOut(new StateXml(state));
    try {
      XMLData data = _reader.readData(Parser.makeDefaultParser(), _input);
      if (!data.getTag().equals(TagType.VOID)) {
        AcquireRuntimeException.logAndThrow(LOGGER,
            "The Player is messing around");
      }
    } catch (ParseException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "The Player is messing around");
    } catch (IOException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "Some problem in reading the data");
    } catch (ParserConfigurationException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "Parsers are not configured properly");
    }

  }

  @Override
  public TurnResponse takeTurn(TurnI turn) {
    _writeOut.writeOut(new TurnRequest(turn.getPerspective()));
    try {
      XMLData data = _reader.readData(Parser.makeDefaultParser(), _input);
      if (data.getTag().equals(TagType.PLACE)) {
        return handleMerge(turn, (PlaceXml) data);
      }

      if (!data.getTag().equals(TagType.TURN_RESPONSE))
        AcquireRuntimeException.logAndThrow(LOGGER,
            "The Player is messing around");

      TurnResponseXml turnResponse = (TurnResponseXml) data;
      PlaceXml place = turnResponse.getPlace();
      PlaceRequest request = convert(place, turn.getPerspective().getBoard());
      return new TurnResponse(request, turnResponse.getPurchase());

    } catch (ParseException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "The Player is messing around");
    } catch (IOException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "Some problem in reading the data");
    } catch (ParserConfigurationException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "Parsers are not configured properly");
    } catch (InvalidMoveExcpetion ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "The Player is messing around");
    }

    return null;
  }

  @Override
  public List<Boolean> keep(List<HotelName> labels) {
    _writeOut.writeOut(new KeepXml(labels));
    XMLData data;
    try {
      data = _reader.readData(Parser.makeDefaultParser(), _input);
      if (!data.getTag().equals(TagType.KEEP))
        AcquireRuntimeException.logAndThrow(LOGGER,
            "The Player is messing around");
      return ((KeepDecisionXml) data).getDecision();
    } catch (ParseException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "The Player is messing around");
    } catch (IOException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "Some problem in reading the data");
    } catch (ParserConfigurationException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "Parsers are not configured properly");
    }

    return new ArrayList<Boolean>();
  }

  @Override
  public void gameOver(EndPerspective perspective) {
    _writeOut.writeOut(new ScoreXml(perspective));
    try {
      XMLData data = _reader.readData(Parser.makeDefaultParser(), _input);
      if (!data.getTag().equals(TagType.VOID)) {
        AcquireRuntimeException.logAndThrow(LOGGER,
            "The Player is messing around");
      }
    } catch (ParseException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "The Player is messing around");
    } catch (IOException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "Some problem in reading the data");
    } catch (ParserConfigurationException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "Parsers are not configured properly");
    }
  }

  @Override
  public void newTile(Location tile) {
    _writeOut.writeOut(new TileXml(tile));
    try {
      XMLData data = _reader.readData(Parser.makeDefaultParser(), _input);
      if (!data.getTag().equals(TagType.VOID)) {
        AcquireRuntimeException.logAndThrow(LOGGER,
            "The Player is messing around");
      }
    } catch (ParseException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "The Player is messing around");
    } catch (IOException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "Some problem in reading the data");
    } catch (ParserConfigurationException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "Parsers are not configured properly");
    }
  }

  @Override
  public void actionOccured(StateI state, ActionDescription action) {
    _writeOut.writeOut(new StateXml(state));
    try {
      XMLData data = _reader.readData(Parser.makeDefaultParser(), _input);
      if (!data.getTag().equals(TagType.VOID)) {
        AcquireRuntimeException.logAndThrow(LOGGER,
            "The Player is messing around");
      }
    } catch (ParseException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "The Player is messing around");
    } catch (IOException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "Some problem in reading the data");
    } catch (ParserConfigurationException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "Parsers are not configured properly");
    }
  }

  @Override
  public void kickedOut(ErrorDescription description) {

  }

  private TurnResponse handleMerge(TurnI turn, PlaceXml dataReq)
      throws ParseException, IOException, ParserConfigurationException {

    try {
      PlaceRequest placeRequest = convert(dataReq, turn.getPerspective()
          .getBoard());
      MergingRequest merging = (MergingRequest) placeRequest;
      List<PlayerState> place = turn.place(merging.getLocation(),
          merging.getName());
      _writeOut.writeOut(new PlayersXml(place));

      XMLData data = _reader.readData(Parser.makeDefaultParser(), _input);

      if (data.getTag().equals(TagType.ORDER)) {

        return new TurnResponse(placeRequest, ((OrderXml) data).getPurchase());

      } else if (data.getTag().equals(TagType.TURN_RESPONSE)) {
        TurnResponseXml turnResponse = (TurnResponseXml) data;
        PlaceXml placeXml = turnResponse.getPlace();
        PlaceRequest request = convert(placeXml, turn.getPerspective()
            .getBoard());
        return new TurnResponse(request, turnResponse.getPurchase());
      } else
        AcquireRuntimeException.logAndThrow(LOGGER,
            "The Player is messing around");

    } catch (InvalidMoveExcpetion ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "The Player is messing around");
    } catch (NotValidMergerException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "The Player is messing around");
    } catch (NotValidAcquirerException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "The Player is messing around");
    } catch (DuplicateCallException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "The Player is messing around");
    }
    return null;
  }

  public static PlaceRequest convert(PlaceXml request, BoardI board)
      throws InvalidMoveExcpetion {
    InspectResult inspect = board.inspect(request.getLocation());
    if (!inspect.isValid())
      throw new InvalidMoveExcpetion("Invalid move");
    PossibleResult result = (PossibleResult) inspect;
    switch (result.getType()) {
      case FOUND:
        FoundingRequest found = new FoundingRequest(request.getLocation(),
            request.getName());
        return found;
      case GROW:
        GrowingRequest grow = new GrowingRequest(request.getLocation());
        return grow;
      case MERGE:
        MergingRequest merge = new MergingRequest(request.getLocation(),
            request.getName());
        return merge;
      case SINGLETON:
        SingletonRequest single = new SingletonRequest(request.getLocation());
        return single;
      default:
        return null;
    }
  }

}
