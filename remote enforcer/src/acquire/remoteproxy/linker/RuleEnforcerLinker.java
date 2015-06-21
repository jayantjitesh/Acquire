package acquire.remoteproxy.linker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import acquire.enforcer.RuleEnforcerBuilder;
import acquire.enforcer.RuleEnforcerTree;
import acquire.exception.InvalidPlayerNameException;
import acquire.exception.TooFewPlayersException;
import acquire.exception.TooManyPlayersException;
import acquire.exception.testbed.ParseException;
import acquire.player.Player;
import acquire.remoteproxy.RemotePlayer;
import acquire.state.perspective.EndPerspective;
import acquire.testbed.parser.Parser;
import acquire.testbed.xmldata.SignUpXml;
import acquire.testbed.xmldata.TagType;
import acquire.testbed.xmldata.WriteOut;
import acquire.testbed.xmldata.XMLData;
import acquire.testbed.xmldata.XMLReader;

public class RuleEnforcerLinker {
  public static final String PLAYERS = "-players";
  public static final String PORT = "-port";
  public static final int NO_ARGUMENTS = 1;
  public static final int INVAILD_IP = 2;
  public static final int INVAILD_PORT = 3;
  public static final int INVALID_PARSER = 4;
  public static final int MAX_PLAYERS = 6;
  public static final int TIMEOUT = 3000;

  /**
   * The Main class to start the player for remote server It should be run with
   * args -ip <server-ip> -port <server-port> <player-name>
   */

  public static void main(String[] args) {
    if (args.length < 4) {
      System.err.println("No arguments passed.\n\t [-port] [-players] ");
      System.exit(NO_ARGUMENTS);
    }

    try {
      int port = -1;

      if (args[0].equals(PORT)) {
        port = Integer.parseInt(args[1]);
      }
      int maxPlayers = MAX_PLAYERS;
      if (args[2].equals(PLAYERS)) {
        maxPlayers = Integer.parseInt(args[3]);
      }
      ServerSocket socket = new ServerSocket(port);
      int playerSize = 0;
      RuleEnforcerBuilder builder = new RuleEnforcerBuilder();
      while (playerSize < maxPlayers) {
        Socket playerSocket = socket.accept();
        playerSocket.setSoTimeout(TIMEOUT);
        Writer writer = new OutputStreamWriter(playerSocket.getOutputStream());
        WriteOut writeOut = new WriteOut(writer);
        InputStream input = playerSocket.getInputStream();

        try {
          XMLReader reader = new XMLReader();
          XMLData data = reader.readData(Parser.makeDefaultParser(), input);
          if (!data.getTag().equals(TagType.SIGNUP)) {
            input.close();
            playerSocket.close();
            continue;
          }
          String name = ((SignUpXml) data).getName();
          Player player = new RemotePlayer(writeOut, input, name);
          name = builder.addPlayer(player, name);
          writeOut.writeOut(new SignUpXml(name));
          playerSize++;
        } catch (InvalidPlayerNameException ex) {
          playerSocket.close();
          continue;
        } catch (TooManyPlayersException ex) {
          playerSocket.close();
          continue;
        } catch (ParseException ex) {
          playerSocket.close();
          continue;
        }
      }
      RuleEnforcerTree enforcer = builder.constructInitialRuleEnforcer();
      System.out.println("game started");
      EndPerspective runGame = enforcer.runGame();
      Map<String, Integer> finalScore = runGame.getFinalScore();
      for (String player : finalScore.keySet()) {
        System.out.println("Player :" + player + " score is :"
            + finalScore.get(player));
      }

    } catch (ParserConfigurationException ex) {
      System.out.println("Cannot initialize the parser");
      System.err.println(ex.getMessage());
      System.exit(INVALID_PARSER);
    } catch (IOException ex) {
      System.err.println(ex.getMessage());
      System.exit(INVAILD_PORT);
    } catch (TooFewPlayersException ex) {
      System.out.println("This should not happen");
      System.err.println(ex.getMessage());
      System.exit(INVALID_PARSER);
    }

  }
}
