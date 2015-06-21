package acquire.testbed.xmldata;

import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class KeepDecisionXml implements XMLData {
  private final List<Boolean> _decision;

  public KeepDecisionXml(List<Boolean> decision) {
    _decision = decision;
  }

  public List<Boolean> getDecision() {
    return _decision;
  }

  @Override
  public Element generateXML(Document doc) {
    Element element = doc.createElement(TagType.KEEP);

    for (Boolean result : _decision) {
      Element resElement;
      if (result)
        resElement = doc.createElement("true");
      else
        resElement = doc.createElement("false");
      element.appendChild(resElement);
    }
    return element;
  }

  @Override
  public String getTag() {
    return TagType.KEEP;
  }

}
