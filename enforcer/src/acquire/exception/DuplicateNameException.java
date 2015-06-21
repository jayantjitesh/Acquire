package acquire.exception;

public class DuplicateNameException extends AcquireException {

  public DuplicateNameException(String name) {
    super("There is already a player named " + name);
  }

}
