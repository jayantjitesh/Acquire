package acquire.state.perspective;

import acquire.state.State;

public class DebugPerspective extends Perspective {
  public DebugPerspective(State gameState, int turnsExecuted) {
    super(gameState, turnsExecuted);
  }

  @Override
  public String getDisplayText(boolean showBoard, boolean showTiles) {
    return super.getDisplayText(showBoard, showTiles);
  }
}
