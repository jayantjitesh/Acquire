package acquire.enforcer.listener;

import acquire.protocol.description.ActionDescription;
import acquire.state.State;
import acquire.state.perspective.EndPerspective;
import acquire.util.EventSource;

public class StageChangeEventSource extends EventSource<StateChangeListener> {
  /**
   * Tell all listeners that the state of the game has changed.
   * 
   * @param state Complete state of the game.
   * @param description Description of action that caused state to change.
   * @param turnsExecuted Non-negative number of turns that have completed
   *          completely.
   * @param maxTurns Non-negative number of the maximum number of turns that
   *          will be executed. <code>RuleEnforcerModel.NO_MAX_TURNS</code> if
   *          no max is set.
   */
  public void fireStateChangeEvent(State state, ActionDescription description,
      int turnsExecuted, int maxTurns) {
    for (StateChangeListener listener : getListeners()) {
      listener.stateChanged(state, description, turnsExecuted, maxTurns);
    }
  }

  /**
   * Tell all listeners that the game has ended.
   * 
   * @param perspective Description of the end state of the game.
   */
  public void fireGameOverEvent(EndPerspective perspective) {
    for (StateChangeListener listener : getListeners()) {
      listener.gameOver(perspective);
    }
  }
}
