package acquire.enforcer.listener;

import acquire.protocol.description.ActionDescription;
import acquire.state.State;
import acquire.state.perspective.EndPerspective;

public interface StateChangeListener {
  /**
   * Called to indicate that the state of the game has changed.
   * 
   * @param state Complete state of the game.
   * @param description Description of action that caused state to change.
   * @param turnsExecuted Non-negative number of turns that have completed
   *          completely.
   * @param maxTurns Non-negative number of the maximum number of turns that
   *          will be executed. <code>RuleEnforcerModel.NO_MAX_TURNS</code> if
   *          no max is set.
   */
  void stateChanged(State state, ActionDescription description,
      int turnsExecuted, int maxTurns);

  /**
   * Called to indicate that the game has ended.
   * 
   * @param perspective description of the ending state.
   */
  void gameOver(EndPerspective perspective);
}
