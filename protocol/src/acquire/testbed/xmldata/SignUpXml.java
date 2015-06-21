package acquire.testbed.xmldata;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SignUpXml implements XMLData {
  private final String _name;

  public SignUpXml(String name) {
    _name = name;
  }

  public String getName() {
    return _name;
  }

  @Override
  public Element generateXML(Document doc) {
    Element element = doc.createElement(TagType.SIGNUP);
    element.setAttribute("name", _name);
    return element;
  }

  @Override
  public String getTag() {
    return TagType.SIGNUP;
  }
}
