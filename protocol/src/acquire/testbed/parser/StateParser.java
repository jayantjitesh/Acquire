package acquire.testbed.parser;

import org.w3c.dom.Node;
import acquire.exception.ArgumentException;
import acquire.exception.testbed.ParseException;
import acquire.testbed.xmldata.StateXml;
import acquire.testbed.xmldata.TagType;
import acquire.testbed.xmldata.XMLData;

public class StateParser extends XMLParser {

  public StateParser() {
    super(TagType.STATE);
  }

  @Override
  public XMLData parse(Node node, String xml) throws ParseException,
      ArgumentException {
    return new StateXml(parseState(node, xml));
  }

}
