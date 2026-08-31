package by.alex.port.exception;

public class PortException extends Exception {

  private static final long serialVersionUID = 1L;

  public PortException(String message) {
    super(message);
  }

  public PortException(String message, Throwable cause) {
    super(message, cause);
  }
}
