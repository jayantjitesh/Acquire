package acquire.remoteproxy;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.Matchers.any;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Before;
import org.junit.Test;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.exception.AcquireRuntimeException;
import acquire.exception.DuplicateCallException;
import acquire.exception.NotValidAcquirerException;
import acquire.exception.NotValidMergerException;
import acquire.exception.testbed.ParseException;
import acquire.player.Player;
import acquire.testbed.parser.Parser;
import acquire.testbed.xmldata.KeepDecisionXml;
import acquire.testbed.xmldata.KeepXml;
import acquire.testbed.xmldata.PlaceXml;
import acquire.testbed.xmldata.PlayersXml;
import acquire.testbed.xmldata.SignUpXml;
import acquire.testbed.xmldata.TagType;
import acquire.testbed.xmldata.WriteOut;
import acquire.testbed.xmldata.XMLReader;

public class RemoteRuleEnforcerTest {
  private RemoteRuleEnforcer _ruleEnforcer;
  private Player _player;
  private WriteOut _writeOut;
  private InputStream _input;
  private static Location A1 = new Location(Row.A, Column.ONE);
  private XMLReader _reader;

  // private StateI _state;

  @Before
  public void setUp() throws ParserConfigurationException {
    _writeOut = mock(WriteOut.class);
    _input = mock(InputStream.class);
    _player = mock(Player.class);
    _reader = mock(XMLReader.class);
    _ruleEnforcer = new RemoteRuleEnforcer(_writeOut, _input, _player, _reader);
  }

  @Test
  public void testPlace() throws NotValidMergerException,
      NotValidAcquirerException, DuplicateCallException, ParseException,
      IOException {
    KeepXml keepXml = mock(KeepXml.class);
    PlayersXml playersXml = mock(PlayersXml.class);

    when(keepXml.getTag()).thenReturn(TagType.KEEP);
    when(playersXml.getTag()).thenReturn(TagType.PLAYERS);
    when(
        _reader.readData(argThat(any(Parser.class)),
            argThat(any(InputStream.class)))).thenReturn(keepXml).thenReturn(
        playersXml);
    _ruleEnforcer.place(A1, HotelName.AMERICAN);
    verify(_writeOut).writeOut(argThat(any(PlaceXml.class)));
    verify(_writeOut).writeOut(argThat(any(KeepDecisionXml.class)));
  }

  @Test(expected = AcquireRuntimeException.class)
  public void testPlaceInvalidResponse() throws NotValidMergerException,
      NotValidAcquirerException, DuplicateCallException, ParseException,
      IOException {
    KeepXml keepXml = mock(KeepXml.class);
    PlayersXml playersXml = mock(PlayersXml.class);

    when(keepXml.getTag()).thenReturn(TagType.KEEP);
    when(playersXml.getTag()).thenReturn(TagType.STATE);
    when(
        _reader.readData(argThat(any(Parser.class)),
            argThat(any(InputStream.class)))).thenReturn(keepXml).thenReturn(
        playersXml);
    _ruleEnforcer.place(A1, HotelName.AMERICAN);

  }

  @Test
  public void testSignUp() throws ParseException, IOException {
    SignUpXml signXml = mock(SignUpXml.class);

    when(signXml.getTag()).thenReturn(TagType.SIGNUP);
    when(
        _reader.readData(argThat(any(Parser.class)),
            argThat(any(InputStream.class)))).thenReturn(signXml);
    _ruleEnforcer.signUp("JAYANT");
    verify(_writeOut).writeOut(argThat(any(SignUpXml.class)));
  }

  @Test(expected = AcquireRuntimeException.class)
  public void testSignUpInvalidResponse() throws ParseException, IOException {
    SignUpXml signXml = mock(SignUpXml.class);

    when(signXml.getTag()).thenReturn(TagType.STATE);
    when(
        _reader.readData(argThat(any(Parser.class)),
            argThat(any(InputStream.class)))).thenReturn(signXml);
    _ruleEnforcer.signUp("JAYANT");
    verify(_writeOut).writeOut(argThat(any(SignUpXml.class)));
  }
}
