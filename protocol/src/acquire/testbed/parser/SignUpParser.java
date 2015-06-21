package acquire.testbed.parser;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import acquire.exception.ArgumentException;
import acquire.exception.testbed.ParseException;
import acquire.testbed.xmldata.SignUpXml;
import acquire.testbed.xmldata.TagType;
import acquire.testbed.xmldata.XMLData;

public class SignUpParser extends XMLParser {

  public SignUpParser() {
    super(TagType.SIGNUP);
  }

  @Override
  public XMLData parse(Node node, String xml) throws ParseException,
      ArgumentException {
    NamedNodeMap attributes = node.getAttributes();
    String name = attributes.getNamedItem(NAME).getNodeValue();
    return new SignUpXml(name);
  }

}
