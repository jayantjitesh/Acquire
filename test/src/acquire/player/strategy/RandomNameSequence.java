package acquire.player.strategy;

import java.util.Collection;
import acquire.basic.HotelChainI.HotelName;
import acquire.util.RandomHelper;

public class RandomNameSequence extends RandomHelper {
  private static final int DEFAULT = 0;
  private final HotelName[] _sequence;
  private int i = DEFAULT;

  public RandomNameSequence(HotelName... sequence) {
    _sequence = sequence;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T randomElement(Collection<T> elements) {
    i++;
    if (i >= _sequence.length) {
      i = DEFAULT;
    }
    return (T) _sequence[i];
  }

  @Override
  public int randomInt(int max) {
    return 3;
  }
}