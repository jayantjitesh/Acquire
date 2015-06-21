package acquire.game.tree;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import org.junit.Before;
import org.junit.Test;
import acquire.game.tree.exception.FinalStateException;
import acquire.game.tree.exception.InvalidPlayerTurnException;
import acquire.state.State;

public class TreeFinalStateTest {
  private Tree _tree;
  private State _state;

  @Before
  public void setUp() {
    _state = mock(State.class);
    _tree = new Tree(_state);
  }

  @Test
  public void testIsFinalState() {
    assertThat(_tree.isFinalState(), is(true));
  }

  @Test(expected = FinalStateException.class)
  public void testGetPlaceOptionsString() throws InvalidPlayerTurnException,
      FinalStateException {
    _tree.getPlaceOptions("kk");
  }

  @Test(expected = FinalStateException.class)
  public void testGetFutureTilesOptions() throws FinalStateException {
    _tree.getFutureTilesOptions();
  }

  @Test(expected = FinalStateException.class)
  public void testGetPurchaseOptions() throws InvalidPlayerTurnException,
      FinalStateException {
    PlayerTree tree = TreeGenerator.generatePlayerTree(_state);
    tree.getMyPurchaseOptions("jj");
  }

}
