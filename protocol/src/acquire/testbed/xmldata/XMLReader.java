package acquire.testbed.xmldata;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.w3c.dom.Node;
import acquire.exception.testbed.ParseException;
import acquire.testbed.parser.Parser;

public class XMLReader {
  private static final int BUFFER_SIZE = 5120;

  public XMLData readData(Parser parser, InputStream input)
      throws ParseException, IOException {
    StringBuilder builder = new StringBuilder();
    byte[] buffer = new byte[BUFFER_SIZE];
    int num;
    List<XMLData> result = new ArrayList<XMLData>();
    while ((num = input.read(buffer)) != -1) {
      builder.append(new String(Arrays.copyOf(buffer, num)));
      buffer = new byte[BUFFER_SIZE];
      String xml = builder.toString();
      List<Node> nodes = null;
      try {
        nodes = parser.parseToNodes(xml);
        System.out.println(xml);
      } catch (ParseException ex) {
        continue;
      }
      // valid xml document, reset builder for new text
      builder = new StringBuilder();
      for (Node node : nodes) {
        result.add(parser.parse(node, xml));
      }
      break;
    }
    return result.get(0);
  }
}
