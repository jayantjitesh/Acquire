package acquire.remoteproxy.linker;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.Matchers.any;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Before;
import org.junit.Test;
import acquire.basic.HotelChainI;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.player.Player;
import acquire.protocol.ProxyRuleEnforcer;
import acquire.protocol.TurnI;
import acquire.protocol.request.SingletonRequest;
import acquire.protocol.request.TurnResponse;
import acquire.state.StateI;
import acquire.state.perspective.EndPerspective;
import acquire.testbed.xmldata.KeepDecisionXml;
import acquire.testbed.xmldata.KeepXml;
import acquire.testbed.xmldata.ScoreXml;
import acquire.testbed.xmldata.StateXml;
import acquire.testbed.xmldata.TileXml;
import acquire.testbed.xmldata.TurnRequest;
import acquire.testbed.xmldata.TurnResponseXml;
import acquire.testbed.xmldata.VoidXml;
import acquire.testbed.xmldata.WriteOut;

public class ParseHandlerTest {

  private ParseHandler _handler;
  private WriteOut _writeOut;
  private Player _player;
  private ProxyRuleEnforcer _ruleEnforcer;
  private static Location A1 = new Location(Row.A, Column.ONE);

  @Before
  public void setUp() throws ParserConfigurationException {
    _writeOut = mock(WriteOut.class);
    _player = mock(Player.class);
    _ruleEnforcer = mock(ProxyRuleEnforcer.class);
    _handler = new ParseHandler(_writeOut, _player, _ruleEnforcer);
  }

  @Test
  public void testHandleTurn() {
    TurnRequest request = mock(TurnRequest.class);
    TurnResponse turn = mock(TurnResponse.class);
    when(_player.takeTurn(argThat(any(TurnI.class)))).thenReturn(turn);
    SingletonRequest placeRequest = new SingletonRequest(A1);
    when(turn.getPlaceRequest()).thenReturn(placeRequest);

    _handler.handleTurn(request);
    verify(_player).takeTurn(argThat(any(TurnI.class)));
    verify(_writeOut).writeOut(argThat(any(TurnResponseXml.class)));
  }

  @Test
  public void testHandleSetup() {
    StateXml request = mock(StateXml.class);
    StateI state = mock(StateI.class);
    when(request.getState()).thenReturn(state);
    _handler.handleSetup(request);
    verify(_player).startGame(state);
    verify(_writeOut).writeOut(argThat(any(VoidXml.class)));
  }

  @Test
  public void testHandleNewTile() {
    TileXml request = mock(TileXml.class);
    when(request.getTile()).thenReturn(A1);
    _handler.handleNewTile(request);
    verify(_player).newTile(A1);
    verify(_writeOut).writeOut(argThat(any(VoidXml.class)));
  }

  @Test
  public void testHandleEndGame() {
    ScoreXml request = mock(ScoreXml.class);
    EndPerspective perspective = mock(EndPerspective.class);
    when(request.getPerspective()).thenReturn(perspective);
    _handler.handleEndGame(request);
    verify(_player).gameOver(perspective);
    verify(_writeOut).writeOut(argThat(any(VoidXml.class)));
  }

  @Test
  public void testHandleInform() {
    StateXml request = mock(StateXml.class);
    StateI state = mock(StateI.class);
    when(request.getState()).thenReturn(state);
    _handler.handleInform(request);
    verify(_player).actionOccured(state, null);
    verify(_writeOut).writeOut(argThat(any(VoidXml.class)));
  }

  @Test
  public void testHandleKeep() {
    KeepXml request = mock(KeepXml.class);
    List<HotelName> labels = new ArrayList<HotelChainI.HotelName>();
    when(request.getLabels()).thenReturn(labels);
    _handler.handleKeep(request);
    verify(_player).keep(labels);
    verify(_writeOut).writeOut(argThat(any(KeepDecisionXml.class)));
  }
}
