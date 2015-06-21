package acquire.state.perspective;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import acquire.basic.Location;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.basic.StockDescription;
import acquire.board.BoardI;
import acquire.board.Spot;
import acquire.state.State;
import acquire.state.money.BankerI;
import acquire.state.player.PlayerState;
import acquire.state.player.PlayerStateI;

public class Perspective {
  protected final List<PlayerState> _players;
  protected final BoardI _board;
  protected final BankerI _banker;
  protected final int _turnsExecuted;
  protected final Set<Location> _availableTiles;

  /**
   * Constructor
   * 
   * @param gameState Current state of the game.
   * @param turnsExecuted Non-negative number of turns that have occurred since
   *          the start of the game.
   */
  public Perspective(State gameState, int turnsExecuted) {
    this(gameState.getPlayers(), gameState.getBoard(), gameState.getBanker(),
        turnsExecuted, gameState.getAvailableTiles());
  }

  /**
   * Constructor
   * 
   * @param players All of the game players. Order is based on turn order, with
   *          the current player as the first element.
   * @param board Description of the tiles and hotels in play.
   * @param banker Description of available stocks and share prices.
   * @param turnsExecuted Non-negative number of turns that have occurred since
   *          the start of the game.
   */
  public Perspective(List<PlayerState> players, BoardI board, BankerI banker,
      int turnsExecuted, Set<Location> availableTiles) {
    _players = players;
    _board = board;
    _banker = banker;
    _turnsExecuted = turnsExecuted;
    _availableTiles = availableTiles;
  }

  public Set<Location> getAvailableTiles() {
    return _availableTiles;
  }

  /**
   * Retrieve all of the game players. Order is based on turn order, with the
   * current player as the first element.
   * 
   * @return Description of all players in the game.
   */
  public List<PlayerState> getAllPlayers() {
    return new ArrayList<PlayerState>(_players);
  }

  /**
   * @return Description of the player who's turn it is presently.
   */
  public PlayerStateI getCurrentPlayer() {
    return _players.get(0);
  }

  /**
   * @return Description of the tiles and hotels in play.
   */
  public BoardI getBoard() {
    return _board;
  }

  /**
   * @return Description of available stocks and share prices.
   */
  public BankerI getBanker() {
    return _banker;
  }

  /**
   * @return Non-negative number of turns that have completed.
   */
  public int getTurnsExecuted() {
    return _turnsExecuted;
  }

  /**
   * @return Textual description of state with board.
   */
  public String getDisplayText() {
    return getDisplayText(true, false);
  }

  /**
   * 
   * @param showBoard True if the board should be displayed.
   * @param showTiles True if each player's tiles should be displayed.
   * @return Textual description of state.
   */
  protected String getDisplayText(boolean showBoard, boolean showTiles) {
    StringBuilder builder = new StringBuilder();
    if (showBoard) {
      Map<Location, Spot> spots = _board.getSpots();
      for (Row row : Row.values()) {
        for (Column column : Column.values()) {
          Spot spot = spots.get(new Location(row, column));
          builder.append("| ");
          builder.append(spot.getLabel());
          builder.append(":");
          if (spot.isEmpty()) {
            builder.append(" ");
          } else if (spot.isSingleton()) {
            builder.append("-");
          } else {
            builder.append(spot.getSpotType().name().substring(0, 1));
          }
          builder.append(" ");
        }
        builder.append("|\n");
      }
    }
    List<PlayerState> sortedPlayers = new ArrayList<PlayerState>(_players);
    Collections.sort(sortedPlayers, new PlayerStateComparator());
    for (PlayerState player : sortedPlayers) {
      builder.append(player.getName());
      builder.append(" - ");
      builder.append(player.getMoney());
      List<StockDescription> stocks = new ArrayList<StockDescription>(player
          .getStockOptions().values());
      if (stocks.isEmpty()) {
        builder.append(" No Stocks");
      } else {
        Collections.sort(stocks, new StockDescriptionComparator());
        for (StockDescription stockDescription : stocks) {
          builder.append(", ");
          builder.append(stockDescription.toString());
        }
      }
      if (showTiles) {
        ArrayList<Location> sortedTiles = new ArrayList<Location>(
            player.getTilesInHand());
        Collections.sort(sortedTiles);
        for (Location tile : sortedTiles) {
          builder.append(", ");
          builder.append(tile.getLabel());
        }
      }
      builder.append("\n");
    }
    builder.append("Turns executed: ");
    builder.append(_turnsExecuted);
    return builder.toString();
  }

  private static class PlayerStateComparator implements
      Comparator<PlayerStateI> {
    @Override
    public int compare(PlayerStateI ps1, PlayerStateI ps2) {
      return ps2.getMoney() - ps1.getMoney();
    }
  }

  private static class StockDescriptionComparator implements
      Comparator<StockDescription> {
    @Override
    public int compare(StockDescription ps1, StockDescription ps2) {
      return ps1.getName().toString().compareTo(ps2.getName().toString());
    }
  }
}