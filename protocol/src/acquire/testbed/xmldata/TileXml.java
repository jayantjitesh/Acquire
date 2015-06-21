package acquire.testbed.xmldata;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import acquire.basic.Location;

public class TileXml implements XMLData {
  private final Location _tile;

  public TileXml(Location loc) {
    _tile = loc;
  }

  public Location getTile() {
    return _tile;
  }

  @Override
  public String getTag() {
    return TagType.TILE;
  }

  @Override
  public Element generateXML(Document doc) {
    Element element = doc.createElement("tile");
    element.setAttribute("column", Integer.toString(_tile.getCol().getValue()));
    element.setAttribute("row", _tile.getRow().toString());
    return element;
  }

}
