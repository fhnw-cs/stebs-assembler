package assembler.support;

import java.util.Arrays;
import java.util.List;

import assembler.TokenList;
import assembler.Memory;


/**
 * The class Common provides static data and methods used throughout
 * the application.
 * Enhancements and changes to the simulator code have to made in
 * this class.
 * 
 * @author ruedi.mueller
 */
public class Common {
  // Title and version
  public static final String title = "Stebs Assembler";
  // Branched from version 1.5
  public static final String version = "V4.7";
  public static final String copyright = "\n";
//  public static final String copyright = ", (c) 2015 by R. Müller, FHNW SGI chp\n";

  // File with assembler commands, opcode and mpm entry point.
  public static final String INSTRUCTION_FILENAME = "res/INSTRUCTION.data";
  
  // Maximum number of addressing mode groups identified, cp. file syntax.txt
  public static final int MAX_NOF_GROUPS = 20;
  
  // The available registers
  public static final List<String> REGISTERS =
    Arrays.asList(new String[] {"AL", "BL", "CL", "DL", "SP"});
  
  // New line symbol
  public static final String NL = "\\n";   // MUST BE A STRING OF LENGTH 1 !
  
  // Temporary END to designate first found END
  public static final String TEMP_END = "$END$";
  
  
  // The available jump commands
  public static List<String> JUMPS;
  
  // The available absolute jump commands
  public static List<String> ABSOLUTE_JUMPS;
  
  // Global string with error message
  public static String ERROR_MESSAGE;

  // The file to be assembled
  private static String filename;
  
  // List with all tokens their line number and position within the line
  private static TokenList tokenList;

  // The single array representing RAM for machine code
  private static Memory memory = new Memory();
  
  // The single code list
  private static StringBuilder codeListSB;


  // Static getters and static setters
  public static TokenList getTokenList() {
    return tokenList;
  }
  
  public static void setTokenList(TokenList tokenList) {
    Common.tokenList = tokenList;
  }

  public static Memory getMemory() {
    return memory;
  }

  public static String getFilename() {
    return filename;
  }
  
  public static void setFilename(String filename) {
    Common.filename = filename;
  }
  
  
  // Convenience methods
  public static String getToken(int index) {
    return tokenList.get(index);
  }
  
  public static void setENDToken(int index) {
    tokenList.setEND(index);
  }
  
  public static void setModifiedENDToken(int index) {
    tokenList.setModifiedEND(index);
  }
  
  public static int[] getRam() {
    return memory.getRam();
  }

  public static void setRam(Memory memory) {
    Common.memory.setRam(memory.getRam());
  }

  public static boolean[] getUsedRamSlots() {
    return memory.getUsedRamSlots();
  }
  
  public static StringBuilder getCodeList() {
    return codeListSB;
  }

  public static void setCodeList(StringBuilder codeListSB) {
    Common.codeListSB = codeListSB;
  }

  
  /**
   * Convert a decimal in the range 0..15 into a hex number string.
   * 
   * @param
   *   value - The decimal number 0..15 to be converted into a hex string
   *   
   * @return
   *   The hex nibble string representation
   */
  static private String toHexNibbleString(int value) {
    switch (value) {
      case 10: return "A";
      case 11: return "B";
      case 12: return "C";
      case 13: return "D";
      case 14: return "E";
      case 15: return "F";
      default: return "" + value;
    }
  }


  /**
   * Convert a decimal into a hex byte string.
   * 
   * @param
   *   value - The decimal number 0..255 to be converted into a hex string
   *   
   * @return
   *   The hex byte string representation
   */
  static public String toHexByteString(int value) {
    StringBuilder sb = new StringBuilder();
    sb.append(toHexNibbleString(value / 0x10));
    sb.append(toHexNibbleString(value % 0x10));
    return sb.toString();
  }
}
