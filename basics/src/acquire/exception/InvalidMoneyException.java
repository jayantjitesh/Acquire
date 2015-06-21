package acquire.exception;

public class InvalidMoneyException extends ArgumentException {

  public InvalidMoneyException() {
    super("Money must be positive");
  }

}
