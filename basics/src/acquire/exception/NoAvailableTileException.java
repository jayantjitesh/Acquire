package acquire.exception;

public class NoAvailableTileException extends AcquireException {

  public NoAvailableTileException() {
    super("There are no more available tiles.");
  }

}
