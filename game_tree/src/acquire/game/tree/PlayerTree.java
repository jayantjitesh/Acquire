package acquire.game.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import acquire.basic.Action.PlacementType;
import acquire.basic.Location;
import acquire.exception.BadPlayerExecption;
import acquire.game.tree.exception.FinalStateException;
import acquire.game.tree.exception.InvalidEdgeException;
import acquire.game.tree.exception.InvalidPlayerTurnException;
import acquire.protocol.request.PlaceRequest;
import acquire.protocol.request.PurchaseRequest;
import acquire.state.State;

/**
 * PlayerTree is a Tree with access to methods required by Player in order
 * to play his turn and identify the next possible state
 *
 */
public class PlayerTree extends Tree {

  public PlayerTree(State node) {
    super(node);
  }

  private PlayerTree(TreeI tree) {
    super(tree.getNodeValue());
  }

  @Override
  public PlayerTree next(Edge e) throws InvalidEdgeException,
      FinalStateException {
    return new PlayerTree(super.next(e));
  }
  /**
   * given the depth of the lookup, returns the value of all possible
   * occurrence of the given query type
   * 
   * @param level defines the depth remaining to calculate, 
   * 			  decrement by 1 in each recursive call
   * @param queryType a PlacementType
   * @param orders all possible buy share combinations at current game state
   * @return count of all different possible occurrence of queryType
   * @throws BadPlayerExecption
   * @throws InvalidPlayerTurnException
   * @throws InvalidEdgeException
   * @throws FinalStateException
   */
  public int getQueryResult(int level, PlacementType queryType,
      List<PurchaseRequest> orders) throws BadPlayerExecption,
      InvalidPlayerTurnException, InvalidEdgeException, FinalStateException {

    int result = 0;
    if (isFinalState())
      return 0;

    State previousState = getNodeValue();
    String currentPlayerName = previousState.getCurrentPlayerName();
    List<Edge> currentEdges = new ArrayList<Edge>();

    List<PlaceRequest> placeOptions = getPlaceOptions(currentPlayerName);
    Set<Location> futureTilesOptions = getFutureTilesOptions();

    PurchaseRequest purchaseRequest = orders.remove(0);
    for (PlaceRequest placeRequest : placeOptions) {
      if (placeRequest.getTag().equals(queryType)) {
        result++;
      }
      for (Location location : futureTilesOptions) {

        currentEdges.add(new Edge(location, purchaseRequest, placeRequest));
      }
    }

    for (Edge edge : currentEdges) {

      if (level == 1)
        continue;
      else {
        TreeI newTree = next(edge);
        int newlevel = level - 1;

        result = result
            + getQueryResult(newlevel, queryType,
                new ArrayList<PurchaseRequest>(orders));
        // System.out.println("total result is " + result);
      }

    }

    return result;
  }

  /**
   * returns all possible valid purchase option for the current Player
   * based on the Tile placement and current game state
   * 
   * @param playerName the name of the caller player
   * @param placeRequest the Tile that player wishes to place
   * @return all possible valid purchase options
   * @throws InvalidPlayerTurnException
   * @throws FinalStateException
   */
  public List<PurchaseRequest> getMyPurchaseOptions(String playerName,
      PlaceRequest placeRequest) throws InvalidPlayerTurnException,
      FinalStateException {
    if (isFinalState())
      throw new FinalStateException("This is the final state");
    if (!_state.getCurrentPlayerName().equals(playerName))
      throw new InvalidPlayerTurnException(playerName);

    return getPurchaseOptions(placeRequest);
  }

  /**
   * Returns all the possible place options for the given Player State.
   * 
   * @param myState
   * @return
   * @throws FinalStateException
   */

  public List<PurchaseRequest> getMyPurchaseOptions(String playerName)
      throws InvalidPlayerTurnException, FinalStateException {
    if (isFinalState())
      throw new FinalStateException("This is the final state");
    if (!_state.getCurrentPlayerName().equals(playerName))
      throw new InvalidPlayerTurnException(playerName);

    return getPurchaseOptions(null);
  }

  /**
   * Returns all the possible place options for the current Player
   * 
   * @param myState
   * @return
   * @throws FinalStateException
   */

  public List<PlaceRequest> getMyPlaceOptions(String playerName)
      throws InvalidPlayerTurnException, FinalStateException {
    if (isFinalState())
      throw new FinalStateException("This is the final state");
    if (!_state.getCurrentPlayerName().equals(playerName))
      throw new InvalidPlayerTurnException(playerName);

    return getPlaceOptions(_state.getCurrentPlayer());
  }
}
