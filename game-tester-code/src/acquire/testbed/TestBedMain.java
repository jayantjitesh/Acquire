package acquire.testbed;

import java.io.OutputStreamWriter;
import javax.xml.parsers.ParserConfigurationException;

public class TestBedMain {

  /**
   * The entry point for the testbed
   * 
   * @param args command line arguments
   */
  public static void main(String[] args) {
    WriteOut writeOut = new WriteOut(new OutputStreamWriter(System.out));
    DefaultParseHandler handler = new DefaultParseHandler(writeOut);
    Parser parser;
    try {
      parser = Parser.makeDefaultParser();
      ParseManager parseManager = new ParseManager(System.in, handler,
          writeOut, parser);
      parseManager.beginReading();
    } catch (ParserConfigurationException e) {
      System.out.println("Cannot initialize the parser");
      e.printStackTrace();
    }
  }
}
