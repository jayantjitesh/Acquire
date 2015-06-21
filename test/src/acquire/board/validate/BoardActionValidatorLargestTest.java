package acquire.board.validate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.Before;
import org.junit.Test;
import acquire.basic.HotelChain;
import acquire.basic.HotelChainI.HotelName;
import acquire.board.Board;

public class BoardActionValidatorLargestTest {
  private Board _board;
  private BoardActionValidator _validator;
  private HotelChain _chain;
  private HotelChain _chain2;
  private HotelChain _chain3;

  @Before
  public void setUp() throws Exception {
    _board = mock(Board.class);
    _validator = new BoardActionValidator();
    _chain = mock(HotelChain.class);
    _chain2 = mock(HotelChain.class);
    _chain3 = mock(HotelChain.class);
    when(_board.getHotelChain(HotelName.AMERICAN)).thenReturn(_chain);
    when(_board.getHotelChain(HotelName.CONTINENTAL)).thenReturn(_chain2);
    when(_board.getHotelChain(HotelName.TOWER)).thenReturn(_chain3);
  }

  @Test
  public void testGetLargestOnlyOne() {
    when(_chain.getSize()).thenReturn(2);

    assertThat(_validator.getLargest(_board, new HashSet<HotelName>(Arrays
        .asList(HotelName.AMERICAN))), contains(HotelName.AMERICAN));
  }

  @Test
  public void testGetLargest() {
    when(_chain.getSize()).thenReturn(2);
    when(_chain2.getSize()).thenReturn(1);

    assertThat(_validator.getLargest(_board, new HashSet<HotelName>(Arrays
        .asList(HotelName.AMERICAN, HotelName.CONTINENTAL))),
        contains(HotelName.AMERICAN));
  }

  @Test
  public void testGetLargestSame() {
    when(_chain.getSize()).thenReturn(2);
    when(_chain2.getSize()).thenReturn(2);

    assertThat(_validator.getLargest(_board, new HashSet<HotelName>(Arrays
        .asList(HotelName.AMERICAN, HotelName.CONTINENTAL))),
        containsInAnyOrder(HotelName.AMERICAN, HotelName.CONTINENTAL));
  }

  @Test
  public void testGetLargestSmallerFirst() {
    when(_chain.getSize()).thenReturn(1);
    when(_chain2.getSize()).thenReturn(2);

    assertThat(_validator.getLargest(_board, new HashSet<HotelName>(Arrays
        .asList(HotelName.AMERICAN, HotelName.CONTINENTAL))),
        contains(HotelName.CONTINENTAL));
  }
}
