package acquire.remoteproxy;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.Matchers.any;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Before;
import org.junit.Test;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.board.BoardI;
import acquire.board.inspect.SingletonResult;
import acquire.exception.AcquireRuntimeException;
import acquire.exception.testbed.ParseException;
import acquire.protocol.ProxyRuleEnforcer;
import acquire.protocol.Turn;
import acquire.protocol.TurnI;
import acquire.state.StateI;
import acquire.state.perspective.EndPerspective;
import acquire.state.perspective.PlayerPerspective;
import acquire.testbed.parser.Parser;
import acquire.testbed.xmldata.KeepDecisionXml;
import acquire.testbed.xmldata.KeepXml;
import acquire.testbed.xmldata.PlaceXml;
import acquire.testbed.xmldata.ScoreXml;
import acquire.testbed.xmldata.StateXml;
import acquire.testbed.xmldata.TagType;
import acquire.testbed.xmldata.TileXml;
import acquire.testbed.xmldata.TurnRequest;
import acquire.testbed.xmldata.TurnResponseXml;
import acquire.testbed.xmldata.VoidXml;
import acquire.testbed.xmldata.WriteOut;
import acquire.testbed.xmldata.XMLReader;

public class RemotePlayerTest {
  private RemotePlayer _player;
  private WriteOut _writeOut;
  private InputStream _input;
  private StateI _state;
  private ProxyRuleEnforcer _ruleEnforcer;
  private XMLReader _reader;

  private static Location A1 = new Location(Row.A, Column.ONE);

  @Before
  public void setUp() throws ParserConfigurationException {
    _writeOut = mock(WriteOut.class);
    _input = mock(InputStream.class);
    _reader = mock(XMLReader.class);
    _player = new RemotePlayer(_writeOut, _input, "JJ", _reader);
    _ruleEnforcer = mock(ProxyRuleEnforcer.class);
  }

  @Test
  public void testStartGame() throws ParseException, IOException,
      ParserConfigurationException {
    _state = mock(StateI.class);
    VoidXml voidXml = new VoidXml();
    when(
        _reader.readData(argThat(any(Parser.class)),
            argThat(any(InputStream.class)))).thenReturn(voidXml);
    _player.startGame(_state);
    verify(_writeOut).writeOut(argThat(any(StateXml.class)));
  }

  @Test(expected = AcquireRuntimeException.class)
  public void testStartGameInvalidResponse() throws ParseException,
      IOException, ParserConfigurationException {
    _state = mock(StateI.class);
    StateXml voidXml = new StateXml(_state);
    when(
        _reader.readData(argThat(any(Parser.class)),
            argThat(any(InputStream.class)))).thenReturn(voidXml);
    _player.startGame(_state);
    verify(_writeOut).writeOut(argThat(any(StateXml.class)));
  }

  @Test
  public void testTakeTurn() throws ParseException, IOException {
    PlayerPerspective perspective = mock(PlayerPerspective.class);
    TurnI turn = new Turn(_ruleEnforcer, perspective);
    BoardI board = mock(BoardI.class);
    PlaceXml place = new PlaceXml(A1);
    TurnResponseXml responseXml = mock(TurnResponseXml.class);
    when(perspective.getBoard()).thenReturn(board);
    when(responseXml.getTag()).thenReturn(TagType.TURN_RESPONSE);
    when(responseXml.getPlace()).thenReturn(place);
    when(
        _reader.readData(argThat(any(Parser.class)),
            argThat(any(InputStream.class)))).thenReturn(responseXml);
    when(board.inspect(A1)).thenReturn(new SingletonResult(A1));
    _player.takeTurn(turn);

    verify(_writeOut).writeOut(argThat(any(TurnRequest.class)));
  }

  @Test(expected = AcquireRuntimeException.class)
  public void testTakeTurnInvalidResponse() throws ParseException, IOException {
    PlayerPerspective perspective = mock(PlayerPerspective.class);
    TurnI turn = new Turn(_ruleEnforcer, perspective);
    StateXml voidXml = new StateXml(_state);
    when(
        _reader.readData(argThat(any(Parser.class)),
            argThat(any(InputStream.class)))).thenReturn(voidXml);
    _player.takeTurn(turn);
  }

  @Test
  public void testKeep() throws ParseException, IOException {
    List<HotelName> labels = new ArrayList<HotelName>();
    KeepDecisionXml keepXml = mock(KeepDecisionXml.class);
    when(
        _reader.readData(argThat(any(Parser.class)),
            argThat(any(InputStream.class)))).thenReturn(keepXml);
    when(keepXml.getTag()).thenReturn(TagType.KEEP);

    _player.keep(labels);
    verify(_writeOut).writeOut(argThat(any(KeepXml.class)));
  }

  @Test
  public void testGameOver() throws ParseException, IOException {
    EndPerspective perspective = mock(EndPerspective.class);
    VoidXml voidXml = new VoidXml();
    when(
        _reader.readData(argThat(any(Parser.class)),
            argThat(any(InputStream.class)))).thenReturn(voidXml);

    _player.gameOver(perspective);

    verify(_writeOut).writeOut(argThat(any(ScoreXml.class)));
  }

  @Test
  public void testNewTile() throws ParseException, IOException {
    VoidXml voidXml = new VoidXml();
    when(
        _reader.readData(argThat(any(Parser.class)),
            argThat(any(InputStream.class)))).thenReturn(voidXml);

    _player.newTile(A1);
    verify(_writeOut).writeOut(argThat(any(TileXml.class)));

  }

  @Test
  public void testActionOccured() throws ParseException, IOException {
    _state = mock(StateI.class);
    VoidXml voidXml = new VoidXml();
    when(
        _reader.readData(argThat(any(Parser.class)),
            argThat(any(InputStream.class)))).thenReturn(voidXml);

    _player.actionOccured(_state, null);

    verify(_writeOut).writeOut(argThat(any(StateXml.class)));
  }

}
