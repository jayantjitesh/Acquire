package acquire.state.perspective;

import java.util.List;
import java.util.Map;
import java.util.Set;
import acquire.basic.Location;
import acquire.board.BoardI;
import acquire.state.money.BankerI;
import acquire.state.player.MyPlayerState;
import acquire.state.player.PlayerState;

public class EndPlayerPerspective extends EndPerspective {
  private final MyPlayerState _myself;

  public EndPlayerPerspective(EndPerspective endPerspective,
      MyPlayerState myself) {
    this(endPerspective.getCause(), endPerspective.getAllPlayerStates(),
        myself, endPerspective.getBoard(), endPerspective.getBanker(),
        endPerspective.getTurnsExecuted(), endPerspective.getAvailableTiles(),
        endPerspective.getFinalScore());
  }

  public EndPlayerPerspective(Cause cause, List<PlayerState> players,
      MyPlayerState myself, BoardI board, BankerI banker, int turnsExecuted,
      Set<Location> availableTiles, Map<String, Integer> finalScore) {
    super(cause, players, board, banker, turnsExecuted, availableTiles,
        finalScore);
    _myself = myself;
  }

  public MyPlayerState getMyself() {
    return _myself;
  }
}
