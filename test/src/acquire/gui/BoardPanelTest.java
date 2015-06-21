package acquire.gui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import java.util.HashSet;
import javax.swing.JPanel;
import org.junit.Test;
import acquire.basic.HotelChain;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.board.Board;
import acquire.board.Spot.SpotType;
import acquire.exception.InvalidSizeException;
import acquire.exception.validate.ValidationException;

public class BoardPanelTest {
  Location D10 = new Location(Row.D, Column.TEN);
  Location C10 = new Location(Row.C, Column.TEN);
  Location C1 = new Location(Row.C, Column.ONE);
  Location A10 = new Location(Row.A, Column.TEN);
  Location B10 = new Location(Row.B, Column.TEN);

  @Test
  public void testBoardGui() {
    BoardPanel boardGui = new BoardPanel();
    JPanel label = boardGui.getLabel(D10);
    assertThat(label.getBackground(), is(SpotType.NONE.getColor()));

  }

  @Test
  public void testDrawBoardWithNoHotel() throws InvalidSizeException,
      ValidationException {
    Board board = new Board();
    BoardPanel boardGui = new BoardPanel();
    boardGui.drawBoard(board);

    assertThat(boardGui.getLabel(D10).getBackground(),
        is(SpotType.NONE.getColor()));
    assertThat(boardGui.getLabel(C10).getBackground(),
        is(SpotType.NONE.getColor()));
    assertThat(boardGui.getLabel(C1).getBackground(),
        is(SpotType.NONE.getColor()));
  }

  @Test
  public void testDrawBoardWithOneHotel() throws InvalidSizeException,
      ValidationException {
    Board board = new Board();
    HashSet<Location> locations = new HashSet<Location>();

    locations.add(C10);
    locations.add(D10);
    HotelChain chain = new HotelChain(HotelName.AMERICAN, locations);
    board.addHotel(chain);

    BoardPanel boardGui = new BoardPanel();
    boardGui.drawBoard(board);

    assertThat(boardGui.getLabel(D10).getBackground(),
        is(SpotType.AMERICAN.getColor()));
    assertThat(boardGui.getLabel(C10).getBackground(),
        is(SpotType.AMERICAN.getColor()));
    assertThat(boardGui.getLabel(C1).getBackground(),
        is(SpotType.NONE.getColor()));
  }

  @Test
  public void testDrawBoardWithTwoHotel() throws InvalidSizeException,
      ValidationException {
    Board board = new Board();
    HashSet<Location> locations = new HashSet<Location>();

    locations.add(C10);
    locations.add(D10);
    HotelChain chain = new HotelChain(HotelName.AMERICAN, locations);
    board.addHotel(chain);

    locations = new HashSet<Location>();
    locations.add(A10);
    locations.add(B10);
    chain = new HotelChain(HotelName.TOWER, locations);
    board.addHotel(chain);

    BoardPanel boardGui = new BoardPanel();
    boardGui.drawBoard(board);

    assertThat(boardGui.getLabel(D10).getBackground(),
        is(SpotType.AMERICAN.getColor()));
    assertThat(boardGui.getLabel(C10).getBackground(),
        is(SpotType.AMERICAN.getColor()));
    assertThat(boardGui.getLabel(A10).getBackground(),
        is(SpotType.TOWER.getColor()));
    assertThat(boardGui.getLabel(B10).getBackground(),
        is(SpotType.TOWER.getColor()));
    assertThat(boardGui.getLabel(C1).getBackground(),
        is(SpotType.NONE.getColor()));
  }

}
