package acquire.player;

import java.util.List;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.protocol.TurnI;
import acquire.protocol.description.ActionDescription;
import acquire.protocol.description.ErrorDescription;
import acquire.protocol.request.TurnResponse;
import acquire.state.StateI;
import acquire.state.perspective.EndPerspective;

public class StubPlayer implements Player {

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void kickedOut(ErrorDescription description) {
    // TODO Auto-generated method stub

  }

  @Override
  public void startGame(StateI state) {
    // TODO Auto-generated method stub

  }

  @Override
  public TurnResponse takeTurn(TurnI turn) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Boolean> keep(List<HotelName> labels) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void actionOccured(StateI state, ActionDescription action) {
    // TODO Auto-generated method stub

  }

  @Override
  public void newTile(Location tile) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setName(String name) {
    // TODO Auto-generated method stub

  }

  @Override
  public void gameOver(EndPerspective perspective) {
    // TODO Auto-generated method stub

  }

}
