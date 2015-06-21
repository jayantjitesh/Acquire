package acquire.testbed.parser;

import org.w3c.dom.Node;
import acquire.exception.ArgumentException;
import acquire.exception.testbed.ParseException;
import acquire.testbed.xmldata.TagType;
import acquire.testbed.xmldata.VoidXml;
import acquire.testbed.xmldata.XMLData;

public class VoidParser extends XMLParser {

  public VoidParser() {
    super(TagType.VOID);
  }

  @Override
  public XMLData parse(Node node, String xml) throws ParseException,
      ArgumentException {
    return new VoidXml();
  }

}
