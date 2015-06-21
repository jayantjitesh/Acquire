package acquire.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import acquire.exception.AcquireRuntimeException;

/**
 * Helper class to allow for mocking randomness.
 */
public class RandomHelper {

  /**
   * Generate a random integer.
   * 
   * @param max Largest integer to return.
   * @return Value between 0 and <code>max</code>, excluding <code>max</code>
   * @throws AcquireRuntimeException <code>max</code> is less than 0.
   */
  public int randomInt(int max) {
    if (max < 0) {
      throw new AcquireRuntimeException("Max must be greated than 0.");
    }
    return (int) Math.random() * max;
  }

  /**
   * Generate a random integer.
   * 
   * @param elements Collection to pick from.
   * @return Random item in <code>elements</code>;
   * @throws AcquireRuntimeException <code>elements</code> is empty.
   */
  public <T> T randomElement(Collection<T> elements) {
    if (elements.isEmpty()) {
      throw new AcquireRuntimeException("The collection cannot be empty.");
    }
    List<T> indexed = new ArrayList<T>(elements);
    return indexed.get(randomInt(indexed.size()));
  }
}
