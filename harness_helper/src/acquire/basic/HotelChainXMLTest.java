package acquire.basic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.xmlmatchers.XmlMatchers.hasXPath;
import static org.xmlmatchers.transform.XmlConverters.the;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.exception.InvalidSizeException;
import acquire.testbed.XMLSerializer;

public class HotelChainXMLTest {
  public static final String ENCODING = "UTF-8";
  protected static final Location D10 = new Location(Row.D, Column.TEN);
  protected static final Location C9 = new Location(Row.C, Column.NINE);
  private XMLSerializer _serializer;

  @Before
  public void setUp() throws Exception {
    _serializer = new XMLSerializer();
  }

  @Test
  public void testGenerateXML() throws InvalidSizeException {
    HotelChain chain = new HotelChain(HotelName.AMERICAN,
        new HashSet<Location>(Arrays.asList(D10, C9)));
    Element xml = _serializer.serialize(chain, XMLSerializer.newDocument());
    assertThat(the(xml), hasXPath("/hotel"));
    assertThat(the(xml), hasXPath("/hotel/@name", equalTo("American")));
    assertThat(the(xml), hasXPath("count(/hotel/tile)", equalTo("2")));
  }

  @Test
  public void testGenerateXML2() throws InvalidSizeException {
    HotelChain chain = new HotelChain(HotelName.TOWER, new HashSet<Location>(
        Arrays.asList(D10, C9, new Location(Row.F, Column.ONE))));
    Element xml = _serializer.serialize(chain, XMLSerializer.newDocument());
    assertThat(the(xml), hasXPath("/hotel"));
    assertThat(the(xml), hasXPath("/hotel/@name", equalTo("Tower")));
    assertThat(the(xml), hasXPath("count(/hotel/tile)", equalTo("3")));
  }

  @Test
  public void testLocationOrder() throws InvalidSizeException {
    HotelChain chain = new HotelChain(HotelName.TOWER, new HashSet<Location>(
        Arrays.asList(D10, C9, new Location(Row.C, Column.ONE))));
    Element xml = _serializer.serialize(chain, XMLSerializer.newDocument());
    assertThat(the(xml), hasXPath("/hotel"));
    assertThat(the(xml), hasXPath("count(/hotel/tile)", equalTo("3")));
    assertThat(the(xml), hasXPath("/hotel/tile[1]/@row", equalTo("C")));
    assertThat(the(xml), hasXPath("/hotel/tile[1]/@column", equalTo("1")));
    assertThat(the(xml), hasXPath("/hotel/tile[2]/@row", equalTo("C")));
    assertThat(the(xml), hasXPath("/hotel/tile[2]/@column", equalTo("9")));
    assertThat(the(xml), hasXPath("/hotel/tile[3]/@row", equalTo("D")));
    assertThat(the(xml), hasXPath("/hotel/tile[3]/@column", equalTo("10")));
  }
}
