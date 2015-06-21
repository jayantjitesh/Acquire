package acquire.testbed.xmldata;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface XMLData {

  String getTag();

  Element generateXML(Document doc);

}
