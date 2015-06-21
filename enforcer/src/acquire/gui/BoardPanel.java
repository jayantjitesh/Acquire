package acquire.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import acquire.basic.Action.ActionType;
import acquire.basic.Location;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.board.BoardI;
import acquire.board.Spot;
import acquire.board.Spot.SpotType;
import acquire.enforcer.RuleEnforcerModelTree;
import acquire.enforcer.listener.StateChangeListener;
import acquire.exception.AcquireRuntimeException;
import acquire.protocol.description.ActionDescription;
import acquire.state.State;
import acquire.state.perspective.DebugPerspective;
import acquire.state.perspective.EndPerspective;

public class BoardPanel extends JPanel implements StateChangeListener {
  private static final Logger LOGGER = Logger.getLogger(BoardPanel.class);
  private static final double SLEEP_BASE = 99;
  private static final long SLEEP_DEFAULT = 100;
  private static Map<Location, JPanel> _cells = new HashMap<Location, JPanel>();

  public BoardPanel() {
    LayoutManager layout = new GridLayout(Row.values().length,
        Column.values().length);
    setLayout(layout);
    JLabel square;
    JPanel suqreFrame;
    for (Row row : Row.values()) {
      for (Column column : Column.values()) {
        square = new JLabel(row.toString() + column.getValue(), JLabel.CENTER);
        square.setForeground(Color.BLACK);
        suqreFrame = new JPanel();
        suqreFrame.add(square, JPanel.CENTER_ALIGNMENT);
        suqreFrame.setBackground(SpotType.NONE.getColor());
        suqreFrame.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(suqreFrame);
        _cells.put(new Location(row, column), suqreFrame);
      }
    }

  }

  public void drawBoard(BoardI board) {
    Map<Location, Spot> spots = board.getSpots();
    for (Location location : _cells.keySet()) {
      JPanel jPanel = _cells.get(location);
      Spot spot = spots.get(location);
      Color color = null;
      if (spot.isSingleton()) {
        color = Spot.SINGELTON_COLOR;
      } else {
        color = spot.getSpotType().getColor();
      }
      jPanel.setBackground(color);
      _cells.put(location, jPanel);
    }
  }

  public JPanel getLabel(Location location) {
    return _cells.get(location);
  }

  @Override
  public void stateChanged(State state, ActionDescription description,
      int turnsExecuted, int maxTurns) {
    drawBoard(state.getBoard());
    logStateChange(state, description, turnsExecuted);
    try {
      Thread.sleep(getSleepTime(turnsExecuted, maxTurns));
    } catch (InterruptedException ex) {
      // there is nothing to do.
    }
  }

  @Override
  public void gameOver(EndPerspective perspective) {
    drawBoard(perspective.getBoard());
    LOGGER.debug("---------- GAME OVER ---------------");
    LOGGER.debug("\n" + perspective.getDisplayText());
    JOptionPane
        .showMessageDialog(this, perspective.getDisplayText(false, true));
  }

  private static long getSleepTime(int turnsExecuted, int maxTurns) {
    if (maxTurns == RuleEnforcerModelTree.NO_MAX_TURN) {
      return SLEEP_DEFAULT;
    }
    return (long) (SLEEP_BASE / (maxTurns - turnsExecuted) * 1000);
  }

  private static void logStateChange(State state,
      ActionDescription description, int turnsExecuted) {
    DebugPerspective perspective = new DebugPerspective(state, turnsExecuted);
    boolean showBoard = false;
    ActionType type = description.getType();
    switch (type) {
      case SINGLETON:
      case GROW:
      case FOUND:
      case MERGE:
        showBoard = true;
        break;
      case END_TURN:
      case KICK:
      case PURCHASE:
        showBoard = false;
        break;
      default:
        AcquireRuntimeException.logAndThrow(LOGGER, "Unexpected action type: "
            + type);
    }
    LOGGER.debug("\n" + perspective.getDisplayText(showBoard, true));
    // special logging
    switch (type) {
      case END_TURN:
        LOGGER.info("======Turn Over======\n");
        break;
      case KICK:
        LOGGER.warn("======Player Kicked======\n");
        break;
    }
  }
}