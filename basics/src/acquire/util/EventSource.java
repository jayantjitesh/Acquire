package acquire.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Collection that holds references to objects that implement a given interface.<br />
 * Child classes should add fireEVENT() methods to allow for calling each
 * listener for the respective event.<br />
 * Implementation is thread safe.
 * 
 * @param <T> Interface a listener must implement.
 */
public abstract class EventSource<T> {
  private final Object _lock = new Object();
  protected final Set<T> _listeners = new HashSet<T>();

  public void addListener(T listener) {
    synchronized (_lock) {
      _listeners.add(listener);
    }
  }

  public void removeListener(T listener) {
    synchronized (_lock) {
      _listeners.remove(listener);
    }
  }

  protected Set<T> getListeners() {
    synchronized (_lock) {
      return new HashSet<T>(_listeners);
    }
  }
}
