package acquire.basic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.xmlmatchers.XmlMatchers.hasXPath;
import static org.xmlmatchers.transform.XmlConverters.the;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.testbed.XMLSerializer;

public class LocationXMLTest {
  public static final String ENCODING = "UTF-8";
  protected static final Location D10 = new Location(Row.D, Column.TEN);
  protected static final Location C9 = new Location(Row.C, Column.NINE);
  private XMLSerializer _serializer;

  @Before
  public void setUp() throws Exception {
    _serializer = new XMLSerializer();
  }

  @Test
  public void testGenerateXML() {
    Element xml = _serializer.serialize(D10, XMLSerializer.newDocument());
    assertThat(the(xml), hasXPath("/tile"));
    assertThat(the(xml), hasXPath("/tile/@row", equalTo("D")));
    assertThat(the(xml), hasXPath("/tile/@column", equalTo("10")));
  }

  @Test
  public void testGenerateXML2() {
    Element xml = _serializer.serialize(C9, XMLSerializer.newDocument());
    assertThat(the(xml), hasXPath("/tile"));
    assertThat(the(xml), hasXPath("/tile/@row", equalTo("C")));
    assertThat(the(xml), hasXPath("/tile/@column", equalTo("9")));
  }

}
