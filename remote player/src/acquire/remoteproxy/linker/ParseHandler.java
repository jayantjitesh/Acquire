package acquire.remoteproxy.linker;

import java.util.List;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import acquire.player.Player;
import acquire.protocol.ProxyRuleEnforcer;
import acquire.protocol.Turn;
import acquire.protocol.request.OrderResponse;
import acquire.protocol.request.Response;
import acquire.protocol.request.TurnResponse;
import acquire.testbed.XMLSerializer;
import acquire.testbed.xmldata.KeepDecisionXml;
import acquire.testbed.xmldata.KeepXml;
import acquire.testbed.xmldata.OrderXml;
import acquire.testbed.xmldata.PlaceXml;
import acquire.testbed.xmldata.ScoreXml;
import acquire.testbed.xmldata.StateXml;
import acquire.testbed.xmldata.TileXml;
import acquire.testbed.xmldata.TurnRequest;
import acquire.testbed.xmldata.TurnResponseXml;
import acquire.testbed.xmldata.VoidXml;
import acquire.testbed.xmldata.WriteOut;

/**
 * 
 * This is a handler class to delegate the calls to the actual player
 * 
 */
public class ParseHandler {
  private final WriteOut _writeOut;
  private final Player _player;
  private final ProxyRuleEnforcer _ruleEnforcer;

  public ParseHandler(WriteOut writeOut, Player player,
      ProxyRuleEnforcer ruleEnforcer) {
    _writeOut = writeOut;
    _player = player;
    _ruleEnforcer = ruleEnforcer;
  }

  public void handleTurn(TurnRequest request) {
    Response takeTurn = _player.takeTurn(new Turn(_ruleEnforcer, request
        .getPerspective()));
    if (takeTurn instanceof TurnResponse) {
      TurnResponse response = (TurnResponse) takeTurn;
      _writeOut.writeOut(new TurnResponseXml(PlaceXml.convert(response
          .getPlaceRequest()), response.getPurchaseRequest()));
    } else if (takeTurn instanceof OrderResponse) {
      OrderResponse response = (OrderResponse) takeTurn;
      _writeOut.writeOut(new OrderXml(response.getPurchaseRequest()));
    }
  }

  public void handleSetup(StateXml request) {
    _player.startGame(request.getState());
    _writeOut.writeOut(new VoidXml());

  }

  public void handleNewTile(TileXml request) {
    _player.newTile(request.getTile());
    _writeOut.writeOut(new VoidXml());
  }

  public void handleEndGame(ScoreXml request) {
    Document document = XMLSerializer.newDocument();
    document.appendChild(request.generateXML(document));
    String xml;
    try {
      xml = XMLSerializer.toXML(document);
      System.out.println(xml);
    } catch (TransformerException ex) {
      // TODO Auto-generated catch block
      ex.printStackTrace();
    }

    _player.gameOver(request.getPerspective());
    _writeOut.writeOut(new VoidXml());
  }

  public void handleInform(StateXml request) {
    _player.actionOccured(request.getState(), null);
    _writeOut.writeOut(new VoidXml());

  }

  public void handleKeep(KeepXml request) {
    List<Boolean> keep = _player.keep(request.getLabels());
    _writeOut.writeOut(new KeepDecisionXml(keep));

  }

}
