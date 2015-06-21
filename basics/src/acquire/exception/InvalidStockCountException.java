package acquire.exception;

public class InvalidStockCountException extends ArgumentException {

  public InvalidStockCountException(int count) {
    super("Not a valid stock count (" + count + ").");
  }

}
