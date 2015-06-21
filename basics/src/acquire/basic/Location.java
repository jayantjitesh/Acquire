package acquire.basic;

import java.util.HashSet;
import java.util.Set;

/**
 * A possible location on the board.
 */
public class Location implements Comparable<Location> {

  private final Location.Row _row;
  private final Location.Column _col;

  /**
   * Constructor
   * 
   * @param row the row value
   * @param col the column value
   */
  public Location(Location.Row row, Location.Column col) {
    _row = row;
    _col = col;
  }

  public Location.Row getRow() {
    return _row;
  }

  public Location.Column getCol() {
    return _col;
  }

  /**
   * Finds the neighbor of this location
   * 
   * @return the set of neighbor location
   */
  public Set<Location> getNeighbors() {
    Set<Location> neighbors = new HashSet<Location>();
    for (Row row : Row.getNeighbors(_row)) {
      neighbors.add(new Location(row, _col));
    }
    for (Column col : Column.getNeighbors(_col)) {
      neighbors.add(new Location(_row, col));
    }
    return neighbors;
  }

  @Override
  public String toString() {
    return "Location [_col=" + _col + ", _row=" + _row + "]";
  }

  public String getLabel() {
    return _row.toString() + _col.getValue();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_col == null) ? 0 : _col.hashCode());
    result = prime * result + ((_row == null) ? 0 : _row.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Location other = (Location) obj;
    if (_col == null) {
      if (other._col != null) {
        return false;
      }
    } else if (!_col.equals(other._col)) {
      return false;
    }
    if (_row == null) {
      if (other._row != null) {
        return false;
      }
    } else if (!_row.equals(other._row)) {
      return false;
    }
    return true;
  }

  /**
   * Represents a row on the board
   */
  public static enum Row {
    A, B, C, D, E, F, G, H, I;

    /**
     * Finds the neighboring row on the board for the given row value
     * 
     * @param row to find the neighbor for
     * @return the set of Row neighbor
     */
    public static Set<Row> getNeighbors(Row row) {
      return Location.getNeighbors(row, Row.values());
    }
  }

  /**
   * Represents a column on the board
   */
  public static enum Column {
    ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(
        9), TEN(10), ELEVEN(11), TWELVE(12);

    private final int _value;

    private Column(int value) {
      _value = value;
    }

    public int getValue() {
      return _value;
    }

    public static Column valueFrom(String value) {
      try {
        return valueFrom(Integer.parseInt(value));
      } catch (NumberFormatException ex) {
        throw new IllegalArgumentException(value
            + " does not map to a Column value.");
      }
    }

    public static Column valueFrom(int value) {
      for (Column column : values()) {
        if (column._value == value) {
          return column;
        }
      }
      throw new IllegalArgumentException(value
          + " does not map to a Column value.");
    }

    /**
     * Finds the neighboring column on the board for the given column value
     * 
     * @param column to find the neighbor for
     * @return the set of Column neighbor
     */
    public static Set<Column> getNeighbors(Column column) {
      return Location.getNeighbors(column, Column.values());
    }
  }

  /**
   * Finds the neighboring values on the board for the given value
   * 
   * @param curr the value to find the neighbor for
   * @param values all the values
   * @return the set of neighbor
   */
  public static <T extends Enum<T>> Set<T> getNeighbors(T curr, T[] values) {
    Set<T> neighbors = new HashSet<T>();
    int currentIndex = curr.ordinal();
    int previousIndex = currentIndex - 1;
    int nextIndex = currentIndex + 1;
    if (previousIndex > -1) {
      neighbors.add(values[previousIndex]);
    }
    if (nextIndex < values.length) {
      neighbors.add(values[nextIndex]);
    }

    return neighbors;
  }

  @Override
  public int compareTo(Location other) {
    int result = _row.compareTo(other.getRow());
    if (result == 0) {
      result = _col.getValue() - other.getCol().getValue();
    }
    return result;
  }
}
