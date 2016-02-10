package assembler;

/**
 * The class NoSuchCommandPatternException is used when building the
 * addressModeCode to determine the group.
 * 
 * @author ruedi.mueller
 */
public class NoSuchCommandPatternException extends Exception {
  private static final long serialVersionUID = 1L;

  public NoSuchCommandPatternException(String errorMessage) {
    super(errorMessage);
  }
}
