package acquire.board;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static util.Matchers.any;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import acquire.basic.HotelChainI;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.board.Spot.SpotType;
import acquire.board.inspect.BoardInspector;
import acquire.board.validate.BoardActionValidator;
import acquire.exception.ChainNotFoundException;
import acquire.exception.InvalidSizeException;
import acquire.exception.validate.ChainAlreadyExistsExcpetion;
import acquire.exception.validate.ValidationException;

public class BoardTest {
  protected static final Location D10 = new Location(Row.D, Column.TEN);
  protected static final Location C10 = new Location(Row.C, Column.TEN);
  private Board _board;
  private BoardActionValidator _validator;

  @Before
  public void setUp() throws Exception {
    _validator = mock(BoardActionValidator.class);
    _board = new Board(_validator, mock(BoardInspector.class));
  }

  @Test
  public void testBoard() {
    BoardI board = new Board();

    assertSpots(board.getSpots());
    assertThat(board.getAvailableHotelNames(),
        hasSize(HotelName.values().length));

  }

  @Test
  public void testIsHotelAvailable() throws ValidationException,
      InvalidSizeException {
    mockValidateAddHotel();

    for (HotelName name : HotelName.values()) {

      _board.addHotel(name, D10);
    }

    for (HotelName name : HotelName.values()) {
      assertThat(_board.isHotelAvailable(name), is(false));
    }
  }

  @Test
  public void testGetHotelChain() throws ValidationException,
      InvalidSizeException, ChainNotFoundException {
    mockValidateAddHotel();
    _board.addHotel(HotelName.AMERICAN, D10);

    HotelChainI chain = _board.getHotelChain(HotelName.AMERICAN);
    assertThat(chain.getLocations(), containsInAnyOrder(D10, C10));
    assertThat(chain.getName(), is(HotelName.AMERICAN));
  }

  @Test(expected = ChainNotFoundException.class)
  public void testGetHotelChainNoChain() throws ValidationException,
      InvalidSizeException, ChainNotFoundException {
    _board.getHotelChain(HotelName.AMERICAN);
  }

  @Test
  public void testGetSpot() {
    Spot spot = _board.getSpot(D10);

    assertThat(spot.isEmpty(), is(true));
    assertThat(spot.getLocation(), is(D10));
    assertThat(spot.getSpotType(), is(SpotType.NONE));
  }

  @Test
  public void testGetSpotSingleton() throws ValidationException {
    _board.addSingleton(D10);
    Spot spot = _board.getSpot(D10);

    assertThat(spot.isEmpty(), is(false));
    assertThat(spot.getLocation(), is(D10));
    assertThat(spot.getSpotType(), is(SpotType.NONE));
  }

  @Test
  public void testGenerateBoardMap() {
    Map<Location, Spot> map = Board.generateBoardMap();

    assertSpots(map);
  }

  public void assertSpots(Map<Location, Spot> map) {
    assertThat("matches board size", map.size(), is(Board.BOARD_SIZE));
    assertThat("All values are initialized", map.containsValue(null), is(false));
    for (Entry<Location, Spot> entry : map.entrySet()) {
      assertThat(entry.getValue().getLocation(), is(entry.getKey()));
    }
  }

  public void mockValidateAddHotel() throws ChainAlreadyExistsExcpetion,
      ValidationException {
    Set<Location> locations = new HashSet<Location>(Arrays.asList(D10, C10));
    when(
        _validator.validateAddHotel(eq(_board), eq(D10),
            argThat(any(HotelName.class)))).thenReturn(locations);
  }
}
