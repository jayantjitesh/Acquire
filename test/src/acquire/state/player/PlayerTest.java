package acquire.state.player;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.basic.StockDescription;
import acquire.exception.AlreadyMaxTilesException;
import acquire.exception.InvalidMoneyException;
import acquire.exception.InvalidPlayerNameException;
import acquire.exception.InvalidTileSetException;

public class PlayerTest {

  private PlayerState _player;
  Location A1 = new Location(Row.A, Column.ONE);
  Location A2 = new Location(Row.A, Column.TWO);
  Location A3 = new Location(Row.A, Column.THREE);
  Location A4 = new Location(Row.A, Column.FOUR);
  Location A5 = new Location(Row.A, Column.FIVE);
  Location A6 = new Location(Row.A, Column.SIX);
  Location A7 = new Location(Row.A, Column.SEVEN);
  Location A8 = new Location(Row.A, Column.EIGHT);

  @Before
  public void setUp() throws Exception {
    _player = new PlayerState("Jayant", 0, new HashSet<Location>(Arrays.asList(
        A1, A2, A3)));

  }

  @Test(expected = InvalidPlayerNameException.class)
  public void testPlayerName() throws InvalidMoneyException,
      InvalidTileSetException, InvalidPlayerNameException {
    _player = new PlayerState("abcdefghijklmnopqrstuvwx", 0,
        new HashSet<Location>(Arrays.asList(A1, A2, A3)));
  }

  @Test
  public void testSetStock() {
    StockDescription stockDescription = new StockDescription(HotelName.AMERICAN);
    _player.setStock(stockDescription);
    assertThat(_player.getStock(HotelName.AMERICAN), is(stockDescription));
  }

  @Test
  public void testAddTileToHand() throws AlreadyMaxTilesException {
    _player.addTileToHand(A4);
    Set<Location> tilesInHand = _player.getTilesInHand();
    assertThat(tilesInHand.size(), is(4));
    assertThat(tilesInHand, hasItem(A4));
  }

  @Test(expected = AlreadyMaxTilesException.class)
  public void testAddTileToHandMaxLimitReached()
      throws AlreadyMaxTilesException {
    _player.addTileToHand(A4);
    _player.addTileToHand(A5);
    _player.addTileToHand(A6);
    _player.addTileToHand(A7);
  }

  @Test
  public void testAddTileToHandDuplicateTile() throws AlreadyMaxTilesException {
    _player.addTileToHand(A4);
    _player.addTileToHand(A5);
    _player.addTileToHand(A4);
    Set<Location> tilesInHand = _player.getTilesInHand();
    assertThat(tilesInHand.size(), is(5));
    assertThat(tilesInHand, containsInAnyOrder(A1, A2, A3, A4, A5));
  }

  @Test
  public void testRemoveTileFromHand() {
    _player.removeTileFromHand(A3);
    Set<Location> tilesInHand = _player.getTilesInHand();
    assertThat(tilesInHand.size(), is(2));
    assertThat(tilesInHand, not(containsInAnyOrder(A3)));
  }

  @Test
  public void testRemoveTileFromHandTileNotPresent() {
    _player.removeTileFromHand(A4);
    Set<Location> tilesInHand = _player.getTilesInHand();
    assertThat(tilesInHand.size(), is(3));
    assertThat(tilesInHand, containsInAnyOrder(A3, A2, A1));
    assertThat(tilesInHand, not(hasItem((A4))));

  }

}
