package acquire.exception;

public class InvalidTurnCountException extends AcquireException {

  public InvalidTurnCountException(int turns) {
    super("A positive number of turns is required, was given " + turns);
  }

}
