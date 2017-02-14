package assembler;

/**
 * The class CodeListBuilderException is used in connection with
 * code list building.
 * 
 * @author ruedi.mueller
 */
public class CodeListBuilderException extends Exception {
  private static final long serialVersionUID = 1L;

  public CodeListBuilderException(String errorMessage) {
    super(errorMessage);
  }
}
