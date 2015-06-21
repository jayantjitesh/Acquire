package acquire.remoteproxy.linker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import javax.xml.parsers.ParserConfigurationException;
import acquire.player.BasicPlayer;
import acquire.player.MyPlayer;
import acquire.player.Player;
import acquire.player.strategy.LargestAlphaStrategy;
import acquire.player.strategy.OrderedStrategy;
import acquire.player.strategy.RandomStrategy;
import acquire.player.strategy.SmallestAntiStrategy;
import acquire.remoteproxy.RemoteRuleEnforcer;
import acquire.testbed.parser.Parser;
import acquire.testbed.xmldata.WriteOut;

/**
 * The Main class to start the player for remote server It should be run with
 * args -ip <server-ip> -port <server-port> <player-name> -strategy <strategy>
 * (This is optional)
 */
public class PlayerLinker {
  public static final String IP = "-ip";
  public static final String PORT = "-port";
  public static final String STRATEGY = "-strategy";
  public static final int NO_ARGUMENTS = 1;
  public static final int INVAILD_IP = 2;
  public static final int INVAILD_PORT = 3;
  public static final int IVALID_PARSER = 4;
  public static final int IVALID_SERVER = 5;

  public static void main(String[] args) {
    if (args.length < 5) {
      System.err
          .println("No or few arguments passed.\n\t [-ip] [-port] player_name -strategy(Optional)");
      System.exit(NO_ARGUMENTS);
    }

    try {
      String ip = null;
      int port = -1;

      if (args[0].equals(IP)) {
        ip = args[1];
      }
      if (args[2].equals(PORT)) {
        port = Integer.parseInt(args[3]);
      }

      String playerName = args[4];
      String strategy = null;
      if (args.length > 5 && args[5] != null && args[5].equals(STRATEGY)) {
        strategy = args[6];
      }
      Socket socket = new Socket(ip, port);
      Writer writer = new OutputStreamWriter(socket.getOutputStream());
      Player player = null;
      if (strategy == null)
        player = new BasicPlayer(playerName, new OrderedStrategy());
      else if (strategy.equals("R"))
        player = new BasicPlayer(playerName, new RandomStrategy());
      else if (strategy.equals("L"))
        player = new BasicPlayer(playerName, new LargestAlphaStrategy());
      else if (strategy.equals("S"))
        player = new BasicPlayer(playerName, new SmallestAntiStrategy());
      else if (strategy.equals("M"))
        player = new MyPlayer(playerName);

      WriteOut writeOut = new WriteOut(writer);
      InputStream input = socket.getInputStream();
      if (input == null)
        System.out.println("something is wrong");
      RemoteRuleEnforcer ruleEnforcer = new RemoteRuleEnforcer(writeOut, input,
          player);
      playerName = ruleEnforcer.signUp(playerName);
      player.setName(playerName);
      System.out.println("name :" + playerName);
      ParseHandler handler = new ParseHandler(writeOut, player, ruleEnforcer);
      input = socket.getInputStream();
      ParseManager manager = new ParseManager(input, handler,
          Parser.makeDefaultParser());
      manager.beginReading();

    } catch (ParserConfigurationException ex) {
      System.out.println("Cannot initialize the parser");
      System.err.println(ex.getMessage());
      System.exit(IVALID_PARSER);
    } catch (IOException ex) {
      System.out.println("Error in listening from server");
      System.err.println(ex.getMessage());
      System.exit(IVALID_SERVER);
    }

  }
}
