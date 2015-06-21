package acquire.player;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import acquire.basic.HotelChainI;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.basic.Location.Column;
import acquire.basic.Location.Row;
import acquire.basic.StockDescription;
import acquire.board.Board;
import acquire.board.inspect.FoundingResult;
import acquire.board.inspect.GrowResult;
import acquire.board.inspect.ImposibleResult;
import acquire.board.inspect.PossibleResult;
import acquire.board.inspect.SingletonResult;
import acquire.exception.InvalidPurchaseListException;
import acquire.exception.InvalidPurchaseRequestException;
import acquire.exception.InvalidShareNameException;
import acquire.player.strategy.StrategyHelper;
import acquire.protocol.request.FoundingRequest;
import acquire.protocol.request.GrowingRequest;
import acquire.protocol.request.PlaceRequest;
import acquire.protocol.request.PurchaseRequest;
import acquire.protocol.request.SingletonRequest;
import acquire.state.money.Banker;
import acquire.state.perspective.PlayerPerspective;
import acquire.state.player.MyPlayerState;

public class BasicPlayerTest {
  protected static final Location D10 = new Location(Row.D, Column.TEN);
  protected static final Location C10 = new Location(Row.C, Column.TEN);
  protected PossibleResult _possible = mock(PossibleResult.class);
  protected ImposibleResult _impossible = mock(ImposibleResult.class);
  protected Board _board;
  protected Banker _banker;
  protected MyPlayerState _myself;
  protected PlayerPerspective _perspective;
  private Map<HotelName, HotelChainI> _chains;

  @Before
  public void setUp() throws Exception {
    _board = mock(Board.class);
    _myself = mock(MyPlayerState.class);
    _banker = mock(Banker.class);
    _perspective = mock(PlayerPerspective.class);
    _chains = makeChains();

    when(_perspective.getBoard()).thenReturn(_board);
    when(_board.getHotelChains()).thenReturn(_chains);
    when(_perspective.getMyself()).thenReturn(_myself);
    when(_perspective.getBanker()).thenReturn(_banker);
    when(_possible.isValid()).thenReturn(true);
  }

  @Test
  public void testGetValidMovesNoTiles() {
    when(_myself.getTilesInHand()).thenReturn(new HashSet<Location>());

    assertThat(BasicPlayer.getValidMoves(_perspective).values(), hasSize(0));
  }

  @Test
  public void testGetValidMovesOneTile() {
    when(_myself.getTilesInHand()).thenReturn(
        new HashSet<Location>(Arrays.asList(D10)));
    when(_board.inspect(D10)).thenReturn(_possible);

    Map<Location, PossibleResult> moves = BasicPlayer
        .getValidMoves(_perspective);
    assertThat(moves.values(), hasSize(1));
    assertThat(moves, hasEntry(D10, _possible));
  }

  @Test
  public void testGetValidMovesOneTileImpossible() {
    when(_myself.getTilesInHand()).thenReturn(
        new HashSet<Location>(Arrays.asList(D10)));
    when(_board.inspect(D10)).thenReturn(_impossible);

    assertThat(BasicPlayer.getValidMoves(_perspective).values(), hasSize(0));
  }

  @Test
  public void testGetValidMovesOnePossibleOneImpossible() {
    when(_myself.getTilesInHand()).thenReturn(
        new HashSet<Location>(Arrays.asList(D10, C10)));
    when(_board.inspect(D10)).thenReturn(_possible);
    when(_board.inspect(C10)).thenReturn(_impossible);

    Map<Location, PossibleResult> moves = BasicPlayer
        .getValidMoves(_perspective);
    assertThat(moves.values(), hasSize(1));
    assertThat(moves, hasEntry(D10, _possible));
  }

  @Test
  public void testGetAvailableSharesNone() {
    assertThat(BasicPlayer.getAvailableShares(_perspective).values(),
        hasSize(7));
  }

  @Test
  public void testGetAvailableSharesOne() {
    when(_banker.getAvailableShareCount(HotelName.AMERICAN)).thenReturn(1);

    Map<HotelName, Integer> moves = BasicPlayer
        .getAvailableShares(_perspective);
    assertThat(moves.values(), hasSize(7));
    assertThat(moves, hasEntry(HotelName.AMERICAN, StockDescription.MAX_COUNT));
  }

  // @Test
  // public void testRemoveShare() {
  // when(_banker.getAvailableShareCount(HotelName.AMERICAN)).thenReturn(7);
  //
  // Map<HotelName, Integer> moves = BasicPlayer
  // .getAvailableShares(_perspective);
  // assertThat(moves.values(), hasSize(7));
  // assertThat(moves, hasEntry(HotelName.AMERICAN, 1));
  // }

  @Test
  public void testGetAvailableSharesMore() {
    when(_banker.getAvailableShareCount(HotelName.AMERICAN)).thenReturn(1);
    when(_banker.getAvailableShareCount(HotelName.TOWER)).thenReturn(2);

    Map<HotelName, Integer> moves = BasicPlayer
        .getAvailableShares(_perspective);
    assertThat(moves.values(), hasSize(7));
    assertThat(moves, hasEntry(HotelName.AMERICAN, 25));
    assertThat(moves, hasEntry(HotelName.TOWER, 25));
  }

  @Test(expected = InvalidShareNameException.class)
  public void testRemoveShareNone() throws InvalidShareNameException {
    StrategyHelper.removeShare(new HashMap<HotelName, Integer>(),
        HotelName.AMERICAN);
  }

  @Test
  public void testRemoveShareOne() throws InvalidShareNameException {
    Map<HotelName, Integer> map = new HashMap<HotelName, Integer>();
    map.put(HotelName.AMERICAN, 1);
    StrategyHelper.removeShare(map, HotelName.AMERICAN);

    assertThat(map.values(), hasSize(0));
  }

  @Test
  public void testRemoveShareTwo() throws InvalidShareNameException {
    Map<HotelName, Integer> map = new HashMap<HotelName, Integer>();
    map.put(HotelName.AMERICAN, 2);
    StrategyHelper.removeShare(map, HotelName.AMERICAN);

    assertThat(map.values(), hasSize(1));
    assertThat(map, hasEntry(HotelName.AMERICAN, 1));
  }

  @Test
  public void testRemoveUnaffectOtherOne() throws InvalidShareNameException {
    Map<HotelName, Integer> map = new HashMap<HotelName, Integer>();
    map.put(HotelName.AMERICAN, 1);
    map.put(HotelName.TOWER, 2);
    StrategyHelper.removeShare(map, HotelName.AMERICAN);

    assertThat(map.values(), hasSize(1));
    assertThat(map, hasEntry(HotelName.TOWER, 2));
  }

  @Test
  public void testRemoveUnaffectOtherTwo() throws InvalidShareNameException {
    Map<HotelName, Integer> map = new HashMap<HotelName, Integer>();
    map.put(HotelName.AMERICAN, 2);
    map.put(HotelName.TOWER, 2);
    StrategyHelper.removeShare(map, HotelName.AMERICAN);

    assertThat(map.values(), hasSize(2));
    assertThat(map, hasEntry(HotelName.AMERICAN, 1));
    assertThat(map, hasEntry(HotelName.TOWER, 2));
  }

  @Test(expected = InvalidPurchaseListException.class)
  public void testMakePurchaseRequestTooMany()
      throws InvalidPurchaseListException, InvalidPurchaseRequestException {
    StrategyHelper.makePurchaseRequest(Arrays.asList(HotelName.AMERICAN,
        HotelName.AMERICAN, HotelName.AMERICAN, HotelName.AMERICAN));
  }

  @Test
  public void testMakePurchaseRequestNone() throws InvalidPurchaseListException {
    PurchaseRequest request = StrategyHelper
        .makePurchaseRequest(new ArrayList<HotelName>());
    assertThat(request.getNames(), hasSize(0));
  }

  @Test
  public void testMakePurchaseRequestNoneEvenNulls()
      throws InvalidPurchaseListException {
    List<HotelName> names = new ArrayList<HotelName>();
    names.add(null);
    names.add(null);
    // names.add(null);
    PurchaseRequest request = StrategyHelper.makePurchaseRequest(names);
    assertThat(request.getNames(), hasSize(0));
  }

  @Test
  public void testMakePurchaseRequestOne() throws InvalidPurchaseListException {
    PurchaseRequest request = StrategyHelper.makePurchaseRequest(Arrays
        .asList(HotelName.AMERICAN));
    assertThat(request.getNames(), hasSize(1));
    assertThat(request.getNames(), containsInAnyOrder(HotelName.AMERICAN));
  }

  @Test
  public void testMakePurchaseRequestTwo() throws InvalidPurchaseListException {
    PurchaseRequest request = StrategyHelper.makePurchaseRequest(Arrays.asList(
        HotelName.AMERICAN, HotelName.AMERICAN));
    assertThat(request.getNames(), hasSize(2));
    assertThat(request.getNames(),
        containsInAnyOrder(HotelName.AMERICAN, HotelName.AMERICAN));
  }

  @Test(expected = InvalidPurchaseListException.class)
  public void testMakePurchaseRequestTwoDistinct()
      throws InvalidPurchaseListException {
    PurchaseRequest request = StrategyHelper.makePurchaseRequest(Arrays.asList(
        HotelName.AMERICAN, HotelName.TOWER));
    assertThat(request.getNames(), hasSize(2));
    assertThat(request.getNames(),
        containsInAnyOrder(HotelName.AMERICAN, HotelName.TOWER));
  }

  // @Test
  // public void testMakePurchaseRequestThree()
  // throws InvalidPurchaseListException {
  // PurchaseRequest request = StrategyHelper.makePurchaseRequest(Arrays.asList(
  // HotelName.AMERICAN, HotelName.TOWER, HotelName.TOWER));
  // assertThat(request.getNames(), hasSize(3));
  // assertThat(
  // request.getNames(),
  // containsInAnyOrder(HotelName.AMERICAN, HotelName.TOWER, HotelName.TOWER));
  // }

  @Test
  public void testConvertRequestSingelton() {
    PlaceRequest request = StrategyHelper.convertToRequest(new SingletonResult(
        C10));
    assertThat(request, instanceOf(SingletonRequest.class));
    assertThat(request.getLocation(), is(C10));
  }

  @Test
  public void testConvertRequestGrow() {
    PlaceRequest request = StrategyHelper.convertToRequest(new GrowResult(C10,
        HotelName.AMERICAN));
    assertThat(request, instanceOf(GrowingRequest.class));
    assertThat(request.getLocation(), is(C10));
  }

  @Test
  public void testConvertRequestFound() {
    PlaceRequest request = StrategyHelper.convertToRequest(new FoundingResult(
        C10, HotelName.AMERICAN, new HashSet<Location>()));
    assertThat(request, instanceOf(FoundingRequest.class));
    assertThat(request.getLocation(), is(C10));
    assertThat(((FoundingRequest) request).getName(), is(HotelName.AMERICAN));
  }

  public static Map<HotelName, HotelChainI> makeChains() {
    Map<HotelName, HotelChainI> chains = new EnumMap<HotelName, HotelChainI>(
        HotelName.class);
    for (HotelName name : HotelName.values()) {
      HotelChainI chain = mock(HotelChainI.class);
      when(chain.getName()).thenReturn(name);
      chains.put(name, chain);
    }
    return chains;
  }
}
