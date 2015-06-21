package acquire.testbed.xmldata;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class VoidXml implements XMLData {

  @Override
  public String getTag() {
    return TagType.VOID;
  }

  @Override
  public Element generateXML(Document doc) {
    Element element = doc.createElement(TagType.VOID);
    return element;
  }

}
