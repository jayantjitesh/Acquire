package acquire.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class EventSourceTest {
  private EventSource<Object> _source;
  private Object _listener;
  private Object _listener2;

  @Before
  public void setUp() throws Exception {
    _source = new EventSource<Object>() {};
    _listener = new Object();
    _listener2 = new Object();
  }

  @Test
  public void testAddListener() {
    _source.addListener(_listener);

    assertThat(_source.getListeners(), contains(_listener));
  }

  @Test
  public void testRemoveListener() {
    _source.addListener(_listener);
    _source.removeListener(_listener);

    assertThat(_source.getListeners(), hasSize(0));
  }

  @Test
  public void testRemoveOtherListener() {
    _source.addListener(_listener);
    _source.addListener(_listener2);
    _source.removeListener(_listener);

    Set<Object> listeners = _source.getListeners();
    assertThat(listeners, hasSize(1));
    assertThat(listeners, contains(_listener2));
  }

  @Test
  public void testGetListenersNotBackingCollection() {
    _source.addListener(_listener);

    Set<Object> listeners = _source.getListeners();
    listeners.add(_listener2);
    listeners = _source.getListeners();
    assertThat(listeners, hasSize(1));
    assertThat(listeners, contains(_listener));
  }

}
