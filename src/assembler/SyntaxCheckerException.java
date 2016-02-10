package assembler;

/**
 * The class SyntaxCheckerException is used in connection with
 * syntax checking.
 * 
 * @author ruedi.mueller
 */
public class SyntaxCheckerException extends Exception {
  private static final long serialVersionUID = 1L;

  public SyntaxCheckerException(String errorMessage) {
    super(errorMessage);
  }
}
