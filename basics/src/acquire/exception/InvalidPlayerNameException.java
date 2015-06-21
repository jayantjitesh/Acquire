package acquire.exception;


public class InvalidPlayerNameException extends ArgumentException {

  public InvalidPlayerNameException(String name) {
    super("Player name can't be greater than 20 characters. Given " + name);
  }

}
