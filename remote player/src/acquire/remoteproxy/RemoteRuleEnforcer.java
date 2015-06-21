package acquire.remoteproxy;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.exception.AcquireRuntimeException;
import acquire.exception.DuplicateCallException;
import acquire.exception.NotValidAcquirerException;
import acquire.exception.NotValidMergerException;
import acquire.exception.testbed.ParseException;
import acquire.player.Player;
import acquire.protocol.ProxyRuleEnforcer;
import acquire.state.player.PlayerState;
import acquire.testbed.parser.Parser;
import acquire.testbed.xmldata.KeepDecisionXml;
import acquire.testbed.xmldata.KeepXml;
import acquire.testbed.xmldata.PlaceXml;
import acquire.testbed.xmldata.PlayersXml;
import acquire.testbed.xmldata.SignUpXml;
import acquire.testbed.xmldata.TagType;
import acquire.testbed.xmldata.WriteOut;
import acquire.testbed.xmldata.XMLData;
import acquire.testbed.xmldata.XMLReader;

/**
 * Remote object of the rule enforcer. It delegates the calls to the actual rule
 * enforcer through network
 * 
 */
public class RemoteRuleEnforcer implements ProxyRuleEnforcer {
  private static final Logger LOGGER = Logger
      .getLogger(RemoteRuleEnforcer.class);
  private final WriteOut _writeOut;
  private final InputStream _input;
  private final Player _player;
  private final XMLReader _reader;

  public RemoteRuleEnforcer(WriteOut writeOut, InputStream input, Player player) {
    this(writeOut, input, player, new XMLReader());
  }

  RemoteRuleEnforcer(WriteOut writeOut, InputStream input, Player player,
      XMLReader reader) {
    _writeOut = writeOut;
    _input = input;
    _player = player;
    _reader = reader;
  }

  @Override
  public List<PlayerState> place(Location loc, HotelName label)
      throws NotValidMergerException, NotValidAcquirerException,
      DuplicateCallException {
    _writeOut.writeOut(new PlaceXml(loc, label));
    try {
      XMLData data = _reader.readData(Parser.makeDefaultParser(), _input);

      if (data.getTag().equals(TagType.KEEP)) {
        KeepXml request = (KeepXml) data;
        List<Boolean> keep = _player.keep(request.getLabels());
        _writeOut.writeOut(new KeepDecisionXml(keep));
      }

      data = _reader.readData(Parser.makeDefaultParser(), _input);
      if (!data.getTag().equals(TagType.PLAYERS))
        AcquireRuntimeException.logAndThrow(LOGGER,
            "The rule enforcer is messing around");

      return ((PlayersXml) data).getPlayers();

    } catch (ParseException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "The rule enforcer is messing around");
    } catch (IOException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "The rule enforcer is messing around");
    } catch (ParserConfigurationException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "The rule enforcer is messing around");
    }

    return null;
  }

  public String signUp(String name) {
    _writeOut.writeOut(new SignUpXml(name));
    try {
      XMLData data = _reader.readData(Parser.makeDefaultParser(), _input);

      if (!data.getTag().equals(TagType.SIGNUP))
        AcquireRuntimeException.logAndThrow(LOGGER,
            "The rule enforcer is messing around");

      System.out.println(((SignUpXml) data).getName());
      return ((SignUpXml) data).getName();

    } catch (ParseException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "The rule enforcer is messing around");
    } catch (IOException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "The rule enforcer is messing around");
    } catch (ParserConfigurationException ex) {
      AcquireRuntimeException.logAndThrow(LOGGER,
          "The rule enforcer is messing around");
    }

    return null;
  }
}
