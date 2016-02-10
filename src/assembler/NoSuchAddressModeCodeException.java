package assembler;

/**
 * The class NoSuchAddressModeCodeException is used when building the
 * addressModeCode to determine the group.
 * 
 * @author ruedi.mueller
 */
public class NoSuchAddressModeCodeException extends Exception {
  private static final long serialVersionUID = 1L;

  public NoSuchAddressModeCodeException(String errorMessage) {
    super(errorMessage);
  }
}
