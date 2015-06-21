package acquire.testbed;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import acquire.AcquireMain;
import acquire.enforcer.RuleEnforcer;
import acquire.enforcer.listener.StateChangeListener;
import acquire.exception.AcquireRuntimeException;
import acquire.exception.DuplicateNameException;
import acquire.exception.InvalidPlayerNameException;
import acquire.exception.InvalidTurnCountException;
import acquire.exception.TooFewPlayersException;
import acquire.exception.TooManyPlayersException;
import acquire.protocol.description.ActionDescription;
import acquire.state.State;
import acquire.state.perspective.EndPerspective;
import acquire.testbed.request.RunRequest;
import acquire.testbed.response.DoneResponse;
import acquire.testbed.response.ErrorResponse;
import acquire.testbed.response.ExhaustedResponse;
import acquire.testbed.response.ImpossibleResponse;
import acquire.testbed.response.Response;
import acquire.testbed.response.ScoreResponse;

/**
 * 
 * This is the default handler class to handle all the requests
 * 
 */
public class DefaultParseHandler implements ParseHandler, StateChangeListener {
  private static final Logger LOGGER = Logger
      .getLogger(DefaultParseHandler.class);
  private final WriteOut _writeOut;
  private List<State> _states;
  private static EndPerspective endPerspective;

  public DefaultParseHandler(WriteOut writeOut) {
    _writeOut = writeOut;
  }

  @Override
  public void handleRun(RunRequest request) {

    try {
      _states = new ArrayList<State>();
      RuleEnforcer ruleEnforcer = AcquireMain.buildOrderedPlayers(
          new ArrayList<String>(request.getPlayerNames()), request.getTurns());
      ruleEnforcer.addStateChangeListener(this);
      endPerspective = ruleEnforcer.runGame();
      _writeOut.writeOut(createResonse(endPerspective));
    } catch (InvalidPlayerNameException ex) {
      _writeOut.writeError(new ErrorResponse(ex.getMessage()));
    } catch (TooManyPlayersException ex) {
      _writeOut.writeError(new ErrorResponse(ex.getMessage()));
    } catch (DuplicateNameException ex) {
      _writeOut.writeError(new ErrorResponse(ex.getMessage()));
    } catch (TooFewPlayersException ex) {
      _writeOut.writeError(new ErrorResponse(ex.getMessage()));
    } catch (InvalidTurnCountException ex) {
      _writeOut.writeError(new ErrorResponse(ex.getMessage()));
    }
  }

  private Response createResonse(EndPerspective endPerspective) {
    switch (endPerspective.getCause()) {
      case MAX_TURNS:
        return new DoneResponse(_states);
      case NO_MORE_TILES:
        return new ExhaustedResponse(_states);
      case PLAYER_CANT_PLAY_TILE:
        return new ImpossibleResponse(_states);
      case FINISHED:
        return new ScoreResponse(_states);
      case ONLY_PLAYER_LEFT:
        return new ImpossibleResponse(_states);
      default:
        AcquireRuntimeException.logAndThrow(LOGGER, "Make compiler happy.");
    }

    return null;
  }

  @Override
  public void stateChanged(State state, ActionDescription description,
      int turnsExecuted, int maxTurns) {
    _states.add(new State(state));
  }

  @Override
  public void gameOver(EndPerspective perspective) {
    endPerspective = perspective;

  }

}
