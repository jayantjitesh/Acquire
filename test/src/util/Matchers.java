package util;

import java.util.Arrays;
import java.util.Collection;
import org.mockito.ArgumentMatcher;
import acquire.basic.HotelChainI;
import acquire.basic.HotelChainI.HotelName;

public class Matchers {

  public static class Any<T> extends ArgumentMatcher<T> {
    private final Class<T> _type;

    public Any(Class<T> type) {
      _type = type;
    }

    @Override
    public boolean matches(Object argument) {
      return _type.isInstance(argument);
    }
  }

  public static class AnyHotelNamed extends Any<HotelChainI> {
    private final HotelName _name;

    public AnyHotelNamed(HotelName name) {
      super(HotelChainI.class);
      _name = name;
    }

    @Override
    public boolean matches(Object argument) {
      return super.matches(argument)
          && ((HotelChainI) argument).getName() == _name;
    }
  }

  public static class ContainingSubset<T> extends
      ArgumentMatcher<Collection<T>> {
    private final Collection<T> _allowed;

    public ContainingSubset(Collection<T> allowed) {
      _allowed = allowed;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean matches(Object argument) {
      try {
        Collection<T> collection = (Collection<T>) argument;
        for (T name : collection) {
          if (!_allowed.contains(name)) {
            return false;
          }
        }
        return true;
      } catch (ClassCastException ex) {
        return false;
      }
    }
  }

  public static class ContainingAll<T> extends ArgumentMatcher<Collection<T>> {
    private final Collection<T> _allowed;

    public ContainingAll(Collection<T> allowed) {
      _allowed = allowed;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean matches(Object argument) {
      try {
        Collection<T> collection = (Collection<T>) argument;
        if (collection.size() != _allowed.size()) {
          return false;
        }
        for (T name : collection) {
          if (!_allowed.contains(name)) {
            return false;
          }
        }
        return true;
      } catch (ClassCastException ex) {
        return false;
      }
    }
  }

  // public static class ActionDescriptionMatcher extends
  // ArgumentMatcher<ActionDescription> {
  // private final Class<T> _type;
  //
  // public Any(Class<T> type) {
  // _type = type;
  // }
  //
  // @Override
  // public boolean matches(Object argument) {
  // return _type.isInstance(argument);
  // }
  // }

  /**
   * Argument matcher that DOES type checking.
   * 
   * @param clazz Type to check against.
   * @return ArgumentMatcher for type.
   */
  public static <T> Any<T> any(Class<T> clazz) {
    return new Any<T>(clazz);
  }

  /**
   * Argument matcher that checks hotel chain name.
   * 
   * @param name Expected hotel name.
   * @return ArgumentMatcher for chains.
   */
  public static AnyHotelNamed anyHotelNamed(HotelName name) {
    return new AnyHotelNamed(name);
  }

  /**
   * Argument matcher that checks if a collection only contains items.
   * 
   * @param items Allowed items.
   * @return ArgumentMatcher for chains.
   */
  public static <T> ContainingSubset<T> containingSubset(T... items) {
    return new ContainingSubset<T>(Arrays.asList(items));
  }

  /**
   * Argument matcher that checks if a collection only contains items.
   * 
   * @param items Allowed items.
   * @return ArgumentMatcher for chains.
   */
  public static <T> ContainingSubset<T> containingSubset(Collection<T> items) {
    return new ContainingSubset<T>(items);
  }

  /**
   * Argument matcher that checks if a collection contains all and only items.
   * 
   * @param items Required items.
   * @return ArgumentMatcher for chains.
   */
  public static <T> ContainingAll<T> containingAll(Collection<T> items) {
    return new ContainingAll<T>(items);
  }
}
