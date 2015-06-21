package acquire.enforcer;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.apache.log4j.Logger;
import acquire.basic.Location;
import acquire.board.Board;
import acquire.enforcer.tile.OrderedTileSelection;
import acquire.enforcer.tile.TileSelectionModel;
import acquire.exception.AcquireRuntimeException;
import acquire.exception.InvalidMoneyException;
import acquire.exception.InvalidPlayerNameException;
import acquire.exception.InvalidTileSetException;
import acquire.exception.InvalidTurnCountException;
import acquire.exception.NoAvailableTileException;
import acquire.exception.TooFewPlayersException;
import acquire.exception.TooFewTilesException;
import acquire.exception.TooManyPlayersException;
import acquire.player.Player;
import acquire.state.State;
import acquire.state.player.PlayerState;
import acquire.state.player.PlayerStateI;

public class RuleEnforcerBuilder {
  private static final Logger LOGGER = Logger
      .getLogger(RuleEnforcerBuilder.class);
  public static final int MIN_PLAYERS = 2;
  public static final int MAX_PLAYERS = 6;
  public static final int INITIAL_CASH = 8000;
  private final Map<String, Player> _players = new LinkedHashMap<String, Player>();
  private int _maxTurns = RuleEnforcerModelTree.NO_MAX_TURN;
  private TileSelectionModel _selectionModel = new OrderedTileSelection();

  /**
   * Construct a builder with an ordered <code>TileSelectionModel</code>
   */
  public RuleEnforcerBuilder() {

  }

  /**
   * Add a player that should compete in the game.
   * 
   * @param player Player to be added.
   * @return The actual name used by the administrator.
   * @throws TooManyPlayersException This method has already been called more
   *           than <code>MAX_PLAYERS</code> times.
   * @throws InvalidPlayerNameException The given player claims to have an
   *           invalid name.
   */
  public String addPlayer(Player player, String name)
      throws TooManyPlayersException, InvalidPlayerNameException {
    PlayerState.ValidatePlayerName(name);
    if (_players.size() == MAX_PLAYERS) {
      throw new TooManyPlayersException();
    }
    while (_players.containsKey(name)) {
      int i = (int) Math.random() * 1000;
      name = name + "_" + i;
    }
    player.setName(name);
    _players.put(name, player);
    return name;

  }

  /**
   * Specify the maximum number of turns the game should have.
   * 
   * @param turns Highest number of turns the game should have.
   * @return This RuleEnforcerBuilder.
   * @throws InvalidTurnCountException Given number of turns is not allowed.
   */
  public RuleEnforcerBuilder setMaxTurns(int turns)
      throws InvalidTurnCountException {
    if (turns != RuleEnforcerModelTree.NO_MAX_TURN && turns < 0) {
      throw new InvalidTurnCountException(turns);
    }
    _maxTurns = turns;
    return this;
  }

  /**
   * Set how tiles should be handed out in the game.
   * 
   * @param selectionModel Implementation of tile selection.
   * @return This RuleEnforcerBuilder.
   */
  public RuleEnforcerBuilder setTileSelectionModel(
      TileSelectionModel selectionModel) {
    _selectionModel = selectionModel;
    return this;
  }

  /**
   * Build the RuleEnforcer.
   * 
   * @return RuleEnforcer that was described by this builder.
   * @throws TooFewPlayersException Not enough players were added to the builder
   *           to create a valid game.
   */
  public RuleEnforcerTree constructInitialRuleEnforcer()
      throws TooFewPlayersException {
    if (_players.size() < MIN_PLAYERS) {
      throw new TooFewPlayersException();
    }

    Board board = new Board();
    Set<Location> tiles = board.getSpots().keySet();

    Queue<String> turnOrder = new ArrayDeque<String>();
    Map<String, PlayerState> playerStates = new HashMap<String, PlayerState>();
    for (Player player : _players.values()) {
      String name = player.getName();
      turnOrder.add(name);
      try {
        playerStates.put(name, makeInitialPlayer(name, tiles, _selectionModel));
      } catch (InvalidPlayerNameException ex) {
        AcquireRuntimeException.logAndThrow(LOGGER,
            "addPLayer() should be validating this.", ex);
      } catch (TooFewTilesException ex) {
        AcquireRuntimeException.logAndThrow(LOGGER,
            "A clean board should have enough tiles.", ex);
      }
    }

    State state = new State(board, turnOrder, playerStates, _selectionModel);

    return new RuleEnforcerTree(new RuleEnforcerModelTree(state, _maxTurns),
        _players);
  }

  public static PlayerState makeInitialPlayer(String name,
      Set<Location> availableTiles, TileSelectionModel selectionModel)
      throws InvalidPlayerNameException, TooFewTilesException {
    try {
      return new PlayerState(name, INITIAL_CASH, selectTiles(availableTiles,
          selectionModel));
    } catch (InvalidTileSetException ex) {
      throw AcquireRuntimeException.logAndReturn(LOGGER,
          "This should never happen. " + "We are generating the set of tiles.",
          ex);
    } catch (InvalidMoneyException ex) {
      throw AcquireRuntimeException.logAndReturn(LOGGER,
          "This should never happen. " + "We are passing in the money amount.",
          ex);
    }
  }

  /**
   * Produce a set of random locations from the given set. The selected
   * locations are removed from the available set.
   * 
   * @param available Locations to choose from.
   * @param selectionModel Implementation of selection strategy.
   * @return Chosen locations.
   * @throws TooFewTilesException Fewer than <code>PlayerStateI.MAX_TILES</code>
   *           in <code>available</code>.
   */
  public static Set<Location> selectTiles(Set<Location> available,
      TileSelectionModel selectionModel) throws TooFewTilesException {
    if (available.size() < PlayerStateI.MAX_TILES) {
      throw new TooFewTilesException(available.size());
    }
    Set<Location> selection = new HashSet<Location>();

    for (int i = 0; i < PlayerStateI.MAX_TILES; i++) {
      Location singleTile = null;
      try {
        singleTile = selectionModel.selectTile(available);
      } catch (NoAvailableTileException ex) {
        AcquireRuntimeException.logAndReturn(LOGGER,
            "Number of tiles is already validated", ex);
      }
      available.remove(singleTile);
      selection.add(singleTile);
    }
    return selection;
  }
}
