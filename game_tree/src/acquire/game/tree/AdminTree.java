package acquire.game.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import acquire.basic.HotelChainI.HotelName;
import acquire.game.tree.exception.FinalStateException;
import acquire.game.tree.exception.InvalidEdgeException;
import acquire.player.Player;
import acquire.state.State;
import acquire.state.player.PlayerState;

/**
 * AdminTree is a Tree with access to methods required by Rule Enforcer to
 * execute the game starting from initial game state till end of game
 * 
 */
public class AdminTree extends Tree {

  public AdminTree(State node) {
    super(node);
  }

  private AdminTree(TreeI tree) {
    super(tree.getNodeValue());
  }

  /**
   * Copy constructor *
   * 
   */
  public AdminTree(AdminTree tree) {
    super(new State(tree.getNodeValue()));
  }

  /**
   * 
   * @return The list of all possible valid moves from the current game state
   *         for the current player
   * @throws FinalStateException
   */
  public List<Edge> getEdges() throws FinalStateException {
    if (isFinalState()) {
      throw new FinalStateException("This is the final state");
    }
    return new ArrayList<Edge>(getChildren(_state.getCurrentPlayer()));
  }

  @Override
  public List<PlayerState> askPlayerToKeepShare(List<HotelName> acquired,
      Map<String, Player> players) {
    return super.askPlayerToKeepShare(acquired, players);
  }

  @Override
  public AdminTree next(Edge e) throws InvalidEdgeException,
      FinalStateException {
    return new AdminTree(super.next(e));
  }
}
