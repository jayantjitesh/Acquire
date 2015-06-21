package acquire.board;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.xmlmatchers.XmlMatchers.hasXPath;
import static org.xmlmatchers.transform.XmlConverters.the;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.board.inspect.BoardInspector;
import acquire.board.validate.BoardActionValidator;
import acquire.exception.ArgumentException;
import acquire.exception.validate.ValidationException;
import acquire.testbed.XMLSerializer;

public class BoardXMLTest {
  public static final String ENCODING = "UTF-8";
  protected static final Location D10 = new Location(Row.D, Column.TEN);
  protected static final Location D9 = new Location(Row.D, Column.NINE);
  protected static final Location A9 = new Location(Row.A, Column.NINE);

  private Board _board;
  private BoardActionValidator _validator;
  private XMLSerializer _serializer;

  @Before
  public void setUp() throws Exception {
    _validator = mock(BoardActionValidator.class);
    _board = new Board(_validator, mock(BoardInspector.class));
    _serializer = new XMLSerializer();
  }

  @Test
  public void testGenerateXML() {
    Element xml = _serializer.serialize(_board, XMLSerializer.newDocument());
    assertThat(the(xml), hasXPath("/board"));
    assertThat(the(xml), not(hasXPath("/board/tile")));
    assertThat(the(xml), not(hasXPath("/board/hotel")));
  }

  @Test
  public void testGenerateXMLWithHotelChain() throws ValidationException,
      ArgumentException {
    _board.addSingleton(D9);
    when(_validator.validateAddHotel(_board, D10, HotelName.AMERICAN))
        .thenReturn(new HashSet<Location>(Arrays.asList(D9, D10)));
    _board.addHotel(HotelName.AMERICAN, D10);
    Element xml = _serializer.serialize(_board, XMLSerializer.newDocument());
    assertThat(the(xml), hasXPath("/board"));
    assertThat(the(xml), not(hasXPath("/board/tile")));
    assertThat(the(xml), hasXPath("count(/board/hotel)", equalTo("1")));
    assertThat(the(xml), hasXPath("count(/board/hotel/tile)", equalTo("2")));
  }

  @Test
  public void testGenerateXMLWithSingleton() throws ValidationException {
    _board.addSingleton(D10);
    Element xml = _serializer.serialize(_board, XMLSerializer.newDocument());
    assertThat(the(xml), hasXPath("/board"));
    assertThat(the(xml), hasXPath("/board/tile"));
    assertThat(the(xml), not(hasXPath("/board/hotel")));
    assertThat(the(xml),
        hasXPath("count(/board/tile)", equalTo(Integer.toString(1))));
  }

  @Test
  public void testGenerateXMLWithSingletonHotel() throws ValidationException,
      ArgumentException {
    _board.addSingleton(A9);
    _board.addSingleton(D9);
    when(_validator.validateAddHotel(_board, D10, HotelName.AMERICAN))
        .thenReturn(new HashSet<Location>(Arrays.asList(D9, D10)));
    _board.addHotel(HotelName.AMERICAN, D10);
    Element xml = _serializer.serialize(_board, XMLSerializer.newDocument());
    assertThat(the(xml), hasXPath("/board"));
    assertThat(the(xml), hasXPath("/board/tile"));
    assertThat(the(xml), hasXPath("/board/hotel"));
    assertThat(the(xml),
        hasXPath("count(/board/hotel)", equalTo(Integer.toString(1))));
    assertThat(the(xml),
        hasXPath("count(/board/tile)", equalTo(Integer.toString(1))));
  }

  @Test
  public void testLocationOrder() throws ValidationException {
    _board.addSingleton(A9);
    _board.addSingleton(D9);
    _board.addSingleton(D10);
    Element xml = _serializer.serialize(_board, XMLSerializer.newDocument());
    assertThat(the(xml), hasXPath("/board"));
    assertThat(the(xml), not(hasXPath("count(/board/hotel/tile)")));
    assertThat(the(xml),
        hasXPath("count(/board/tile)", equalTo(Integer.toString(3))));
    assertThat(the(xml), hasXPath("/board/tile[1]/@row", equalTo("A")));
    assertThat(the(xml), hasXPath("/board/tile[1]/@column", equalTo("9")));
    assertThat(the(xml), hasXPath("/board/tile[2]/@row", equalTo("D")));
    assertThat(the(xml), hasXPath("/board/tile[2]/@column", equalTo("9")));
    assertThat(the(xml), hasXPath("/board/tile[3]/@row", equalTo("D")));
    assertThat(the(xml), hasXPath("/board/tile[3]/@column", equalTo("10")));
  }
}
