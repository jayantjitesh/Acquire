package acquire.gui;

import javax.swing.JFrame;
import acquire.board.Board;
import acquire.enforcer.RuleEnforcerTree;

public class GameGui extends JFrame {
  private final BoardPanel _boardPanel;

  public GameGui() {
    super("Acquire");

    _boardPanel = new BoardPanel();
    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setContentPane(_boardPanel);
  }

  public void subscribeToEvents(RuleEnforcerTree enforcer) {
    enforcer.addStateChangeListener(_boardPanel);
  }

  public void display() {
    // an empty board until the game starts
    _boardPanel.drawBoard(new Board());
    pack();
    setVisible(true);
    setResizable(true);
  }

}
