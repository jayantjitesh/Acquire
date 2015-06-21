package acquire.protocol.description;

import acquire.basic.Action.ActionType;
import acquire.state.player.PlayerStateI;

public class ActionDescription {
  private final ActionType _type;
  private final PlayerStateI _player;

  public ActionDescription(ActionType type, PlayerStateI player) {
    _type = type;
    _player = player;
  }

  /**
   * @return Player that performed action.
   */
  public PlayerStateI getPlayer() {
    return _player;
  }

  /**
   * @return Indicator of action type.
   */
  public ActionType getType() {
    return _type;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_player == null) ? 0 : _player.hashCode());
    result = prime * result + ((_type == null) ? 0 : _type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ActionDescription other = (ActionDescription) obj;
    if (_player == null) {
      if (other._player != null) {
        return false;
      }
    } else if (!_player.equals(other._player)) {
      return false;
    }
    if (_type != other._type) {
      return false;
    }
    return true;
  }

}
