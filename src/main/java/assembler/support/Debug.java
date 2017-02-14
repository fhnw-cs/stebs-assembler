package assembler.support;


/**
 * The class Debug is used to compile debug helper strings ot not.
 * 
 * @author ruedi.mueller
 */
public final class Debug {
  // Set to false to allow compiler to identify and not compile
  // unreachable code
  // Set to true to display debug helper strings
  
  /**
   * Flag to display or suppress tokenizer messages
   */
  public static final boolean T_ON = false;
  
  /**
   * Flag to display or suppress syntax checker messages
   */
  public static final boolean SC_ON = false;
  
  /**
   * Flag to display or suppress code list builder messages
   */
  public static final boolean CBL_ON = false;
}
