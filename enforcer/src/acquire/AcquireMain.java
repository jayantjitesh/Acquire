package acquire;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import acquire.enforcer.RuleEnforcerBuilder;
import acquire.enforcer.RuleEnforcerModelTree;
import acquire.enforcer.RuleEnforcerTree;
import acquire.exception.DuplicateNameException;
import acquire.exception.InvalidPlayerNameException;
import acquire.exception.InvalidTurnCountException;
import acquire.exception.TooFewPlayersException;
import acquire.exception.TooManyPlayersException;
import acquire.gui.GameGui;
import acquire.player.BasicPlayer;
import acquire.player.Player;
import acquire.player.strategy.LargestAlphaStrategy;
import acquire.state.perspective.EndPerspective;

/**
 * Main entry point to the game
 */
public class AcquireMain {
  private static final Logger LOGGER = Logger.getLogger(AcquireMain.class);
  public static final String UI_FLAG = "-ui";
  public static final String TURNS_FLAG = "-turns";
  public static final int NO_ARGUMENTS = 1;
  public static final int INVAILD_TURNS = 2;
  public static final int TOO_FEW_PLAYERS = 3;
  public static final int TOO_MANY_PLAYERS = 4;
  public static final int REPEATED_NAME = 5;
  public static final int ILLEGAL_NAME = 6;

  /**
   * @param args Command line arguments<br />
   *          [-ui] [-turns #] player_name ...
   */
  public static void main(String[] args) {
    LOGGER.info("Commnad line arguments: " + Arrays.toString(args));
    if (args.length == 0) {
      System.err
          .println("No arguments passed.\n\t[-ui] [-turns #] player_name ...");
      System.exit(NO_ARGUMENTS);
    }
    boolean ui = false;
    int index = 0;
    int maxTurns = RuleEnforcerModelTree.NO_MAX_TURN;

    if (args[0].equals(UI_FLAG)) {
      ui = true;
      index = 1;
    }

    if (args.length >= index + 1 && args[index].equals(TURNS_FLAG)) {
      if (args.length == index + 1) {
        System.err.println("No turn count provided.");
        System.exit(INVAILD_TURNS);
      }
      try {
        maxTurns = Integer.parseInt(args[index + 1]);
      } catch (NumberFormatException ex) {
        System.err.println("Number of turns must be a non-negative integer");
        System.exit(INVAILD_TURNS);
      }
      index += 2;
    }

    List<String> names = new ArrayList<String>();
    for (int i = index; i < args.length; i++) {
      names.add(args[i]);
    }
    RuleEnforcerTree ruleEnforcer = null;
    try {
      ruleEnforcer = buildOrderedPlayers(names, maxTurns);
    } catch (InvalidPlayerNameException ex) {
      System.err.println(ex.getMessage());
      System.exit(ILLEGAL_NAME);
    } catch (TooManyPlayersException ex) {
      System.err.println(ex.getMessage());
      System.exit(TOO_MANY_PLAYERS);
    } catch (DuplicateNameException ex) {
      System.err.println(ex.getMessage());
      System.exit(REPEATED_NAME);
    } catch (TooFewPlayersException ex) {
      System.err.println(ex.getMessage());
      System.exit(TOO_FEW_PLAYERS);
    } catch (InvalidTurnCountException ex) {
      System.err.println("Number of turns must be a non-negative integer");
      System.exit(INVAILD_TURNS);
    }

    if (ui) {
      GameGui gameGui = new GameGui();
      gameGui.subscribeToEvents(ruleEnforcer);
      gameGui.display();
    }

    EndPerspective endPerspective = ruleEnforcer.runGame();
    if (!ui) {
      System.out.println(endPerspective.getDisplayText());
    }
  }

  public static RuleEnforcerTree buildOrderedPlayers(List<String> playerNames,
      int maxTurns) throws InvalidPlayerNameException, TooManyPlayersException,
      DuplicateNameException, TooFewPlayersException, InvalidTurnCountException {
    RuleEnforcerBuilder builder = new RuleEnforcerBuilder();
    for (String name : playerNames) {
      Player player = new BasicPlayer(name, new LargestAlphaStrategy());
      String actualName = builder.addPlayer(player, name);
      player.setName(actualName);
    }
    builder.setMaxTurns(maxTurns);
    return builder.constructInitialRuleEnforcer();
  }
}
