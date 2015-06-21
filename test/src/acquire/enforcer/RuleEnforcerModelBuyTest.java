package acquire.enforcer;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.Matchers.containingAll;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.StockDescription;
import acquire.exception.AcquireException;
import acquire.exception.InvalidStockCountException;
import acquire.player.Player;
import acquire.player.StubPlayer;
import acquire.protocol.request.PurchaseRequest;
import acquire.state.State;
import acquire.state.player.PlayerState;

@RunWith(Parameterized.class)
public class RuleEnforcerModelBuyTest {
  private final List<HotelName> _names;
  private final Collection<StockDescription> _stocks;
  private Player _player;
  private PlayerState _playerState;
  private PurchaseRequest _purchaseRequest;
  private State _state;
  private RuleEnforcerModel _model;

  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[] { new ArrayList<HotelName>() },
        new Object[] { Arrays.asList(HotelName.AMERICAN) },
        new Object[] { Arrays.asList(HotelName.AMERICAN, HotelName.AMERICAN) },
        new Object[] { Arrays.asList(HotelName.AMERICAN, HotelName.AMERICAN,
            HotelName.AMERICAN) }, new Object[] { Arrays.asList(
            HotelName.AMERICAN, HotelName.TOWER, HotelName.AMERICAN) },
        new Object[] { Arrays.asList(HotelName.AMERICAN, HotelName.TOWER,
            HotelName.TOWER) });
  }

  public RuleEnforcerModelBuyTest(List<HotelName> names)
      throws InvalidStockCountException {
    _names = names;
    Map<HotelName, Integer> counts = new HashMap<HotelName, Integer>();
    for (HotelName name : _names) {
      Integer count = counts.get(name);
      if (count == null) {
        count = 1;
      } else {
        count++;
      }
      counts.put(name, count);
    }
    _stocks = new ArrayList<StockDescription>();
    for (Entry<HotelName, Integer> e : counts.entrySet()) {
      _stocks.add(new StockDescription(e.getKey(), e.getValue()));
    }
  }

  @Before
  public void setUp() throws Exception {
    _playerState = mock(PlayerState.class);
    _state = mock(State.class);
    _player = new StubPlayer();

    _purchaseRequest = mock(PurchaseRequest.class);
    when(_purchaseRequest.getNames()).thenReturn(_names);

    _model = new RuleEnforcerModel(_state, 0);
  }

  @Test
  public void testHandleMove() throws AcquireException {
    _model.handlePurchase(_purchaseRequest);

    verify(_state).purchaseStock(argThat(containingAll(_stocks)));
  }

}
