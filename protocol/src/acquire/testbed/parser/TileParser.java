package acquire.testbed.parser;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import acquire.basic.Location;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.exception.ArgumentException;
import acquire.exception.testbed.ParseException;
import acquire.testbed.xmldata.TagType;
import acquire.testbed.xmldata.TileXml;
import acquire.testbed.xmldata.XMLData;

public class TileParser extends XMLParser {

  public TileParser() {
    super(TagType.TILE);
  }

  @Override
  public XMLData parse(Node node, String xml) throws ParseException,
      ArgumentException {
    NamedNodeMap attrMap = node.getAttributes();
    String row = getAttributeValue(attrMap, ROW, ROW, xml);
    String column = getAttributeValue(attrMap, COLUMN, COLUMN, xml);
    try {
      return new TileXml(new Location(Row.valueOf(row),
          Column.valueFrom(column)));
    } catch (IllegalArgumentException ex) {
      throw new ParseException(ex.getMessage(), xml);
    }
  }

}
