package assembler;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import assembler.support.Common;
import assembler.support.Debug;
import assembler.Memory;


/**
 * The class SyntaxChecker is responsible for the syntax checking of
 * the token list.
 * If a syntax error is found, checking is interrupted and an error
 * message with the error line is printed.
 * During checking the RAM is continuously provided with machine code.
 * At the end label address resolving is applied to appropriate RAM
 * slots.
 * 
 * @author ruedi.mueller
 */
public class SyntaxChecker {
  private final static int HEX_BASE = 16;
  
  // Flag to mark an unknown mnemonic
  private final static int UNKNOWN_MNEM = -1;
  
  // Lists with labels and symbols organized as follows:
  //   labels -->     | start: | start | start |
  //   labelrefs -->  | 22     | 11    | 25    |
  // Meaning:
  //   Label start: found at address 22hex
  //   Symbols start  found at addresses 11hex and 25hex
  // The list is needed to test if duplicates labels are present and to
  // resolve forward/backward addresses.
  private List<String> labels = new ArrayList<String>();
  private List<Integer> labelrefs = new ArrayList<Integer>();
  // Corresponding label line numbers
  private List<String> labelLinenum = new ArrayList<String>();
  
  // New line symbol
  private final static String NL = Common.NL;
  // Invalid character string, cp. below
  private final static String CHAR_STR = "@|§°ç~<>+\"*%&/()=?^'-.{}äöü!ÄÖÜ$£";
  // Characters which are not allowed in labels/symbols
  private final static char[] INVALID_LABELCHAR_ARRAY = CHAR_STR.toCharArray();
  // Characters which are not allowed in labels, symbols
  private final static char[] INVALID_SYMBOLCHAR_ARRAY = (CHAR_STR + ":").toCharArray();
  
  // Magical numbers used with label address dereferencing
  private final static int DEFINED = 999;
  private final static int USED = DEFINED;
  
  // Corresponding error number, used in testing
  // Assign number in code as long, e.g. 6L for easy finding
  private long errorNum;
  
  // State variables
  private int numOfMnemsPerLine = 0;      // No mnemonic found yet
  private int numOfLabelsPerLine = 0;     // No label found yet

  // RAM for machine code
  private Memory memory = new Memory();
  // Pointer to RAM and allocation field slot
  private int ramLocator = 0;
  
  // Each list contains commands of a group. The syntax checker relies on groups
  // as each group is syntactically treated in a different way: Cp. description in
  // test files.
  private List<ArrayList<String>> commandSymGroupList;
  private List<ArrayList<String>> commandHexGroupList;
  private List<ArrayList<String>> commandPatternGroupList;

  // Pointer into tokenList pointing to token
  private int tokenListIndex;
  // The current token
  private String token;
  
  
  // Convenience method for JUnit testing purposes
  public int[] getRam() {
    return memory.getRam();
  }
  
  
  /**
   * Construct a syntax checker to read a token list built from a *.asm file,
   * check syntax and assemble it into machine code.
   * 
   * @param asm The assembler of this checker.
   */
  public SyntaxChecker(GroupListBuilder groupListBuilder) {
    commandSymGroupList = groupListBuilder.commandSymGroupList;
    commandHexGroupList = groupListBuilder.commandHexGroupList;
    commandPatternGroupList = groupListBuilder.commandPatternGroupList;
    if (Debug.SC_ON) {
      System.out.println();
      System.out.println("commandSymGroupList (SyntaxChecker):\n  " + commandSymGroupList);
      System.out.println("commandHexGroupList (SyntaxChecker):\n  " + commandHexGroupList);
      System.out.println("commandPatternGroupList (SyntaxChecker):\n  " + commandPatternGroupList);
    }
  }
  
  
  /**
   * Check the syntax by reading the lines in the token list.
   * Collect the labels in a list.
   * In case of an error set up the error message (line in which error occurs
   * and message) and stop further checking.
   *
   * @throws SyntaxCheckerException if context error found
   */
  public void checkSyntax() throws SyntaxCheckerException {
    Common.ERROR_MESSAGE = "";              // No error to report
    // Loop on a token-by-token basis
    for (tokenListIndex = 2; tokenListIndex < Common.getTokenList().size(); tokenListIndex += 3) {
      token = Common.getToken(tokenListIndex);
       // Check if first token in the line is a comment
      if (isComment())   continue;
      // Check if end of line
      else if (isNewline())   continue;
      // Check if a label
      else if (isLabel())   continue;
      // Check if ORG
      else if (isOrg())   continue;
      // Check if DB
      else if (isDb())   continue;
      // Check if END
      else if (isEnd())   return;     // return!
      // Check if mnemonic
      else if (isMnemonic())   continue;
      // Should never reach this point
      else {
        Common.ERROR_MESSAGE = MS.ERROR_IN_CHECKSYNTAX;
        throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
      }
    }
    // END is missing
    Common.ERROR_MESSAGE = MS.END_IS_MISSING;
    errorNum = 14L;
    throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
  }
  
  
  /**
   * Check the lines in the token list.
   * Collect the labels in a list and resolve forward references.
   * In case of an error set up the error message (line in which error occurs
   * and message) and stop further checking.
   *
   * @throws SyntaxCheckerException if context error found
   */
  public void check() throws SyntaxCheckerException {
    checkSyntax();
    resolveLabelAddresses();

    // For debugging purposes
    if (Debug.SC_ON) {
      System.out.println("labels (SyntaxChecker):\n  " + labels);
      System.out.println("labelrefs (SyntaxChecker):\n  " + labelrefs);
      System.out.println("ram (SyntaxChecker):\n  " + Arrays.toString(memory.getRam()));
      System.out.println("usedRamSlots (SyntaxChecker):\n  " + Arrays.toString(memory.getUsedRamSlots()));
      System.out.println("Common.JUMPS (SyntaxChecker):\n  " + Common.JUMPS);
    }
    
    // Make RAM known to Common
    Common.setRam(memory);
  }

  
  /**
   * Check whether token is a comment.
   *
   * @return true if a comment, else false
   */
  private boolean isComment() {
    return token.startsWith(";");
  }
  
  
  /**
   * Check whether token is the newline symbol.
   * 
   * @return true if the newline symbol, else false
   */
  private boolean isNewline() {
    if (token.equals(NL)) {
      numOfLabelsPerLine = 0;   // New code line, label allowed again
      numOfMnemsPerLine = 0;    // New code line, mnemonic allowed again
      return true;
    }
    return false;
  }
  
  
  /**
   * Check whether token is the 'END' command which must reside in
   * group 0.
   * Change 'END' to Common.TEMP_END so that it can be easily recognised by the code
   * list builder which ignores tokens beyond the first Common.TEMP_END encountered.
   * 
   * @return true if 'END' command found, else false
   */
  private boolean isEnd() throws SyntaxCheckerException {
    if (token.equals("END")) {
      checkMnemonicGroup(0);
      
      Common.getTokenList().setModifiedEND(tokenListIndex);
      return true;
    }
    return false;
  }
  
  
  /**
   * Check whether token is the 'ORG' command followed by a hex number.
   * 
   * @return true if the 'ORG hex', else false
   * @throws SyntaxCheckerException if context error found
   */
  private boolean isOrg() throws SyntaxCheckerException {
    if (token.equals("ORG")) {
      nextToken();
      if (!(isHexNumber(token))) {
        ifNewlineThrowCheckerException(
            MS.EXPECTED_HEX, 9000L, 9001L);
      }
      // ORG 20
      if (isHexNumber(token)) {
        // Change RAM locator
        int address = Integer.parseInt(token, HEX_BASE);
        ramLocator = address;
      
        nextToken();
        ifNotNewlineNotCommentThrowCheckerException(9002L);
      }
      return true;
    }
    else {
      // not an ORG
      return false;
    }
  }
    
    
  /**
   * Check whether token is the 'DB' directive followed by a hex, a quote or
   * a double quote.
   *
   * @return true if the 'DB hex', "DB 'char'" or 'DB "string"' directive else false
   * @throws SyntaxCheckerException if context error found
   */
  private boolean isDb() throws SyntaxCheckerException {
    if (token.equals("DB")) {
      nextToken();
      if (!(isHexNumber(token) || token.startsWith("'") || token.startsWith("\""))) {
        ifNewlineThrowCheckerException(MS.EXPECTED_HEX, 1000L, 1001L);
      }
      // DB 20
      if (isHexNumber(token)) {
        writeByte(Integer.parseInt(token, HEX_BASE));
        
        nextToken();
        ifNotNewlineNotCommentThrowCheckerException(1002L);
        return true;
      }
      
      // DB 'a'
      if (token.startsWith("'")) {
        if (token.length() != 3) {
          markError(MS.EXPECTED_CHAR_ENCLOSED + token, 1003L);
          throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
        }
        char c = token.charAt(1);
        if (c < 0x20 || c > 0x7F) {
          markError(MS.INVALID_CHAR + token +"'", 1004L);
          throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
        }
        writeByte(c);
        
        c = token.charAt(2);
        if (c != '\'') {
          markError(MS.EXPECTED_CLOSING_QUOTE + c +"'", 1005L);
          throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
        }
        
        nextToken();
        ifNotNewlineNotCommentThrowCheckerException(1006L);
        return true;
      }
      
      // DB "hello"
      if (token.startsWith("\"")) {
        // Empty string
        if (token.length() == 2 && token.charAt(1) == '"') {
          markError(MS.EMPTY_STRINGS_NOT_ALLOWED, 1007L);
          throw new SyntaxCheckerException(Common.ERROR_MESSAGE); 
        }
        
        // Missing end-"
        char c;
        if ((c = token.charAt(token.length() - 1)) != '"') {
          markError(MS.EXPECTED_CLOSING_DOUBLE_QUOTE + c + "'", 1008L);
          throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
        }
      
        c = areAllAlphaNumPlusChar();
        if (c != ' ') {
          markError(MS.INVALID_STRING + c +"'", 1009L);
          throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
        }
        
        writeBytes();    // Errors 1010L, 1011L

        nextToken();
        ifNotNewlineNotCommentThrowCheckerException(1012L);
        return true;
      }
    }
    return false;
  }
  
  
  /**
   * Test if token contains valid characters (printable ASCII chars).
   * 
   * @return char ' ' if ok, else return invalid char
   */
  private char areAllAlphaNumPlusChar() {
    for (int i = 0; i < token.length(); ++i) {
      char c = token.charAt(i);
      if (c < 0x20 || c > 0x7F) {
        return c;
      }
    }
    return ' ';
  }
  
  
  /**
   * Check whether token is a label and if so, whether it is correctly
   * found in context.
   *
   * @return true if a label, else false
   * @throws SyntaxCheckerException if context error found
   */
  private boolean isLabel() throws SyntaxCheckerException {
	  if (token.endsWith(":")) {
	    // See if colon alone
	    // Notice: Consecutive colons not possible due to parsing
	    if (token.equals(":")) {
	      // See if an only label
	      if (numOfLabelsPerLine == 0) {
	        markError(MS.EXPECTED_LABEL_MNEMONIC_COMMENT, 3L);
	      }
	      else {
	        markError(MS.EXPECTED_MNEMONIC_DIRECTIVE_COMMENT, 4L);
	      }
	      throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
	    }

	    // See if label is a hex number only
      if (token.length() == 3) {
        if (isHexNumber(token.substring(0, 2))) {
          markError(MS.LABEL_MUST_NOT_BE_HEX + token.substring(0, 2), 19L);
          throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
        }
      }
      if (token.length() == 2) {
        if (isHexNumber(token.substring(0, 1))) {
          markError(MS.LABEL_MUST_NOT_BE_HEX + token.substring(0, 1), 20L);
          throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
        }
      }

      // See if name doesn't contain invalid characters
      testCharsInLabel();
      
	    // See if any duplicates
	    if (labels.contains(token.toUpperCase())) {
	      // No duplicate labels allowed
	      markError(MS.DUPLICATE_LABEL_NAMES_NOT_ALLOWED, 5L);
	      throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
	    }
	    
	    // See if multiple labels per line
	    else if (numOfLabelsPerLine != 0) {
	      // More than 1 label per line
	      markError(MS.ONLY_ONE_LABEL_PER_LINE, 6L);
	      throw new SyntaxCheckerException(Common.ERROR_MESSAGE);    
	    }
	    else {
	      // Enter a label into labels list
	      labels.add(token.toUpperCase());
	      // Enter the label's address into labelrefs list
	      labelrefs.add(ramLocator);
	      // Remember line number
	      String lineStr = Common.getToken(tokenListIndex - 2);
	      labelLinenum.add(lineStr);
	      numOfLabelsPerLine++;
	      return true;
	    }
	  }
	  else {
	    // Not a label
	    return false;
	  }
  }
  
  
  /**
   * Return if token is a mnemonic
   * 
   * @return true if a mnemonic else false
   * @throws SyntaxCheckerException if context error found
   */
  private boolean isMnemonic() throws SyntaxCheckerException {
    // Mnemonic group number each mnemonic is associated to
    int mnemGroup;
    
    // Check if mnemonic, parameters etc. ok
    if ((mnemGroup = findGroupNum()) != UNKNOWN_MNEM) {
      // Found mnemonic and its associated group number
      // Only one mnemonic per line?
      if (numOfMnemsPerLine == 0) {
        // Switch to appropriate mnemonic group and treat the command
        // Groups are defined with their opcode in file named COMMAND_FILENAME
        switch (mnemGroup) {
        case 0:
          checkMnemonicGroup_0();    // MNEM
          break;
        case 1:
          checkMnemonicGroup_1();    // MNEM ADDR
          break;
        case 2:
          checkMnemonicGroup_2();    // MNEM REG
          break;
        case 3:
          checkMnemonicGroup_3();    // MNEM OFFSET (Symbol)
          break;
        case 4:
          checkMnemonicGroup_4();    // MNEM REG,CONST; MNEM REG,REG; MNEM REG,[ADDR]
          break;
        case 5:
          checkMnemonicGroup_5();    // MNEM REG,CONST; MNEM REG,REG; MNEM REG,[ADDR]; MNEM REG,[REG]; MNEM [ADDR],REG; MNEM [REG],REG
          break;
        case 6:
          checkMnemonicGroup_6();    // MNEM REG,CONST; MNEM REG,REG
          break;
        case 7:
          checkMnemonicGroup_7();    // MNEM REG; MNEM [REG]
          break;
        case 8:
          checkMnemonicGroup_8();    // NMEM ABSOLUTE (Symbol)
          break;
        case 9:
          checkMnemonicGroup_9();    // MNEM REG; MNEM [ADDR]
          break;
        case 10:
          checkMnemonicGroup_10();   // MNEM [ADDR]
          break;
        case 11:
          checkMnemonicGroup_11();   // MNEM [REG]
          break;
        case 12:
          checkMnemonicGroup_12();   // MNEM [REG],CONST
          break;
        case 13:
          checkMnemonicGroup_13();   // MNEM REG,REG
          break;
        // Command groups not recognised by stebs, ready to be added, cp. syntax.txt
        // case 14:  break;
        // etc.
        default:
          Common.ERROR_MESSAGE = MS.ERROR_IN_ISMNEMONIC;
          throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
        }
        // Mnemonic, parameters all ok
        return true;
      }
      else {
        // More than 1 mnemonic per line
        markError(MS.ONLY_ONE_MNEMONIC_PER_LINE, 7L);
        throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
      }
    }
    else {
      // No mnemonic found
      markError(MS.EXPECTED_MNEMONIC + token + "'", 8L);
      throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
    }
  }

  
  /**
   * Test if the token is a mnemonic and answer its associated group number.
   * 
   * @return group number in the range (0..Common.MAX_NOF_GROUPS-1), or -1
   *         if not a mnemonic
   */
  private int findGroupNum() {
    int mnemGroup = UNKNOWN_MNEM;      // Assume not a mnemonic
    
    // Test in which mnemonic group the token resides. Answer group number or
    // -1 if not found.
    for (int group = 0; group < commandSymGroupList.size(); ++group) {
      if (commandSymGroupList.get(group).contains(token)) {
        mnemGroup = group;
        break;
      }
    }
    return mnemGroup;
  }


  /**
   * Check the mnemonic of the specified group.
   * Enter opcode into RAM unless an error condition is detected.
   * 
   * @param groupNum The group which the token (= mnemonic) is member of.
   * @throws SyntaxCheckerException if context error found
   */
  private void checkMnemonicGroup(int groupNum) throws SyntaxCheckerException {
    // Find the associated opcode of the mnemonic
    List<String> list = commandSymGroupList.get(groupNum);
    for (int i = 0; i < list.size(); ++i) {
      if (list.get(i).equals(token)) {
        // Found mnemonic
        int opcode = Integer.parseInt(commandHexGroupList.get(groupNum).get(i), HEX_BASE);
        // First test if RAM space exceeded
        writeByte(opcode);
        // Mnemonic found in this line
        numOfMnemsPerLine++;
        break;
      }
    }
  }
  
  
  /**
   * Adjust the opcode of a command which has the same mnemonic but different address
   * modes. Such an address mode can only be recognised after evalution of the
   * parameter/s.
   * 
   * @param groupNum The group which the token (= mnemonic) is member of
   * @param mnemonic The mnemonic of interest
   * @param nofCommandBytes The number of bytes this command consists of
   * @parameters The parameter string (one or two parameters)
   * @return the opcode of this variant
   */
  private void adjustOpcode(int groupNum, String mnemonic, int nofCommandBytes, String parameters)  throws SyntaxCheckerException {
    // Return the associated commandPattern of the mnemonic
    List<String> list = commandPatternGroupList.get(groupNum);
    List<String> csgList = commandSymGroupList.get(groupNum);
    for (int i = 0; i < list.size(); ++i) {
      StringTokenizer tokens = new StringTokenizer(list.get(i), " ");
      tokens.nextToken();                  // mnemonic
      String params = tokens.nextToken();  // parameters
      if (csgList.get(i).equals(mnemonic) && params.equals(parameters)) {
        // Found mnemonic with command pattern, now return opcode
        int opcode = Integer.parseInt(commandHexGroupList.get(groupNum).get(i), HEX_BASE);
        
        // Adjust opcode in ram
        memory.getRam()[ramLocator - nofCommandBytes] = opcode;
        return;
      }
    }
    // Should never reach this line
    Common.ERROR_MESSAGE = MS.ERROR_IN_PICK_ADDRESS_MODE_VARIANT;
    throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
  }
  
  
  /**
   * Check syntax of mnemonic group 0, i.e. commands:
   * MNEM
   * Test if no  duplicate RAM allocation occurred else throw
   * a checker exception.
   * Allocate memory and enter opcode into RAM.
   *
   * @throws SyntaxCheckerException if context error found
   */
  private void checkMnemonicGroup_0() throws SyntaxCheckerException {
    // Find the associated opcode of the mnemonic
    checkMnemonicGroup(0);
    
    // Test whether next token is newline or comment
    nextToken();
    ifNotNewlineNotCommentThrowCheckerException(909L);
  }
  
  
  /**
   * Check syntax of mnemonic group 1, i.e. commands:
   * MNEM ADDR
   * Test if no  duplicate RAM allocation occurred else throw
   * a checker exception.
   * Allocate memory and enter opcode and hex number into ram.
   *
   * @throws SyntaxCheckerException if context error found
   */
  private void checkMnemonicGroup_1() throws SyntaxCheckerException {
    // Find the associated opcode of the mnemonic
    checkMnemonicGroup(1);
    
    // Check if next token is a hex parameter
    nextToken();
    if (!isHexNumber(token)) {
      ifNewlineThrowCheckerException(MS.EXPECTED_HEX, 100L, 101L);
    }
    writeByte(Integer.parseInt(token, HEX_BASE));

    // See if next token is end of line or comment
    nextToken();
    ifNotNewlineNotCommentThrowCheckerException(102L);
  }

  
  /**
   * Check syntax of mnemonic group 2, i.e. commands:
   * MNEM REG
   * Test if no  duplicate RAM allocation occurred else throw
   * a checker exception.
   * Allocate memory and enter opcode and register code into ram.
   *
   * @throws SyntaxCheckerException if context error found
   */
  private void checkMnemonicGroup_2() throws SyntaxCheckerException {
    // Find the associated opcode of the mnemonic
    checkMnemonicGroup(2);
    
    // Check if register
    nextToken();
    if (!isRegister()) {
      ifNewlineThrowCheckerException(MS.EXPECTED_REGISTER, 200L, 201L);
    }
    writeByte(getRegisterCode());
    
    // See if next token is end of line or comment
    nextToken();
    ifNotNewlineNotCommentThrowCheckerException(202L);
  }
  

  /**
   * Check syntax of mnemonic group 3, i.e. commands:
   * MNEM OFFSET
   * Test if no  duplicate RAM allocation occurred else throw
   * an checker exception.
   * Allocate memory and enter opcode and jump distance (if jump reference
   * already known) into ram. Otherwise allocate space for later correction
   * of the jump distance. Enter symbol address into list labelrefs.
   *
   * @throws SyntaxCheckerException if context error found
   */
  private void checkMnemonicGroup_3() throws SyntaxCheckerException {
    // Find the associated opcode of the mnemonic
    checkMnemonicGroup(3);
    
    // Check if next token is a symbol
    nextToken();
    
    if (isNewline()) {
      markError(MS.EXPECTED_SYMBOL, 300L);
      throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
    }

    if (isSymbol3()) {
      // Write byte into RAM: dummy value, to be corrected
      writeByte(ramLocator);
      
      // See if next token is end of line or comment
      nextToken();
      ifNotNewlineNotCommentThrowCheckerException(303L);
      return;
    }
    else {
      // Should never reach this line
      Common.ERROR_MESSAGE = MS.ERROR_IN_CHECKMNEMONICGROUP_3;
      throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
    }
  }
  
  
  /**
   * Throw a CheckerException if a newline is encountered or another
   * remaining string.
   * Describe error with an accompanied error number.
   *  
   * @param message Description of error
   * @param errNum0 Error number if a newline token
   * @param errNum1 Error number if another token
   * @throws SyntaxCheckerException if context error found
   */
  private void ifNewlineThrowCheckerException(String message, long errNum0, long errNum1) throws SyntaxCheckerException {
    if (isNewline()) {
      markError(message, errNum0);
      throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
    }
    else {
      markError(message + ", got '" + token + "'", errNum1);
      throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
    }
  }
  
  
  /**
   * Throw a CheckerException if not a newline or a comment is encountered.
   * Describe error with an accompanied error number.
   *  
   * @param message Description of error
   * @param errNum Error number if a newline token
   * @throws SyntaxCheckerException if context error found
   */
  private void ifNotNewlineNotCommentThrowCheckerException(long errNum) throws SyntaxCheckerException {
    if (!(isNewline() || isComment())) {
      markError(MS.EXPECTED_COMMENT + token + "'", errNum);
      throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
    }
  }
  
  
  /**
   * Check syntax of mnemonic group 4, i.e. commands:
   * MNEM REG,CONST  or  MNEM REG,REG  or MNEM REG,[ADDR]
   * Test if no  duplicate RAM allocation occurred else throw
   * a checker exception.
   * Allocate memory and enter opcode and parameters.
   *
   * @throws SyntaxCheckerException if context error found
   */
  private void checkMnemonicGroup_4() throws SyntaxCheckerException {
    // Find the temporary opcode of the mnemonic to be changed later on
    // depending on the address mode.
    int group4 = 4;
    checkMnemonicGroup(group4);
    String mnemonic = token;        // Save mnemonic for possible opcode adjustment
    
    // Check if register
    nextToken();
    if (!isRegister()) {
      ifNewlineThrowCheckerException(MS.EXPECTED_REGISTER, 400L, 401L);
    }
    writeByte(getRegisterCode());
    
    // Check if comma
    nextToken();
    if (!isComma()) {
      ifNewlineThrowCheckerException(MS.EXPECTED_COMMA, 402L, 403L);
    }
    
    // Check if hex, register or '['
    nextToken();
    if (!(isHexNumber(token) || isRegister() || isLeftBracket())) {
      ifNewlineThrowCheckerException(MS.EXPECTED_HEX_REGISTER_BRACKET, 404L, 405L);
    }
    
    // Take care of one out of three possibilities
    if (isHexNumber(token)) {
      writeByte(Integer.parseInt(token, HEX_BASE));
      
      nextToken();
      ifNotNewlineNotCommentThrowCheckerException(406L);

      // Adjust opcode due to addressing mode,
      // CMP reg,const  (cp. commands.txt)
      adjustOpcode(group4, mnemonic, 3, "REG,CONST");
      return;
    }
    
    if (isRegister()) {
      writeByte(getRegisterCode());

      nextToken();
      ifNotNewlineNotCommentThrowCheckerException(407L);
      
      // Adjust opcode due to addressing mode,
      // CMP reg,reg  (cp. commands.txt)
      adjustOpcode(group4, mnemonic, 3, "REG,REG");
      return;
    }
    
    if (isLeftBracket()) {
      nextToken();
      if (!isHexNumber(token)) {
        ifNewlineThrowCheckerException(MS.EXPECTED_HEX, 408L, 409L);
      }
      writeByte(Integer.parseInt(token, HEX_BASE));
      
      nextToken();
      if (!isRightBracket()) {
        ifNewlineThrowCheckerException(MS.EXPECTED_RIGHT_BRACKET, 410L, 411L);
      }
      
      nextToken();
      ifNotNewlineNotCommentThrowCheckerException(412L);
      
      // Adjust opcode due to addressing mode,
      // CMP reg,[addr]  (cp. commands.txt)
      adjustOpcode(group4, mnemonic, 3, "REG,|ADDR|");
      return;
    }
    else {
      // Should never reach this line
      Common.ERROR_MESSAGE = MS.ERROR_IN_CHECKMNEMONICGROUP_4;
      throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
    }
  }
  
  
  /**
   * Check syntax of mnemonic group 5, i.e. mnemonic:
   * MNEM REG,CONST  or  MNEM REG,REG  or  MNEM REG,[ADDR]  or
   * MNEM REG,[REG]  or  MNEM [ADDR],REG  or  MNEM [REG],REG
   * Test if no  duplicate RAM allocation occurred else throw
   * a checker exception.
   * Allocate memory and enter opcode and parameters.
   *
   * @throws SyntaxCheckerException if context error found
   */
  private void checkMnemonicGroup_5() throws SyntaxCheckerException {
    // Find the temporary opcode of the mnemonic to be changed later on
    // depending on the address mode.
    int group5 = 5;
    checkMnemonicGroup(group5);
    String mnemonic = token;        // Save mnemonic for possible opcode adjustment
    
    nextToken();
    if (!(isRegister() || isLeftBracket())) {
      ifNewlineThrowCheckerException(MS.EXPECTED_REGISTER_BRACKET, 500L, 501L);
    }
    // Check if "MOV AL..."
    if (isRegister()) {
      writeByte(getRegisterCode());

      // Check if "MOV AL,..."
      nextToken();
      if (!isComma()) {
        ifNewlineThrowCheckerException(MS.EXPECTED_COMMA, 502L, 503L);
      }

      nextToken();
      if (!(isHexNumber(token) || isRegister() || isLeftBracket())) {
        ifNewlineThrowCheckerException(MS.EXPECTED_HEX_REGISTER_BRACKET, 504L, 505L);
      }
      // Check if "MOV AL,20..."
      if (isHexNumber(token)) {
        writeByte(Integer.parseInt(token, HEX_BASE));
        
        nextToken();
        ifNotNewlineNotCommentThrowCheckerException(506L);
        
        // Adjust opcode due to addressing mode in group 5 (cp. commands.txt)
        adjustOpcode(group5, mnemonic, 3, "REG,CONST");
        return;
      }
      // Check if  "MOV AL,BL..."
      if (isRegister()) {
        writeByte(getRegisterCode());

        nextToken();
        ifNotNewlineNotCommentThrowCheckerException(507L);
        
        // Adjust opcode due to addressing mode in group 5 (cp. commands.txt)
        adjustOpcode(group5, mnemonic, 3, "REG,REG");
        return;
      }
      // Check if  "MOV AL,[..."
      if (isLeftBracket()) {
        nextToken();
        if (!(isHexNumber(token) || isRegister())) {
          ifNewlineThrowCheckerException(MS.EXPECTED_HEX_REGISTER, 508L, 509L);
        }
        // Check if  "MOV AL,[20..."
        if (isHexNumber(token)) {
          writeByte(Integer.parseInt(token, HEX_BASE));
          
          nextToken();
          // Check if  "MOV AL,[20]..."
          if (!isRightBracket()) {
            ifNewlineThrowCheckerException(MS.EXPECTED_RIGHT_BRACKET, 510L, 511L);
          }
          
          nextToken();
          ifNotNewlineNotCommentThrowCheckerException(512L);
          
          // Adjust opcode due to addressing mode in group 5 (cp. commands.txt)
          adjustOpcode(group5, mnemonic, 3, "REG,|ADDR|");
          return;
        }
        // Check if  "MOV AL,[BL..."
        if (isRegister()) {
          writeByte(getRegisterCode());
          
          nextToken();
          // Check if  "MOV AL,[BL]..."
          if (!isRightBracket()) {
            ifNewlineThrowCheckerException(MS.EXPECTED_RIGHT_BRACKET, 513L, 514L);
          }

          nextToken();
          ifNotNewlineNotCommentThrowCheckerException(515L);
          
          // Adjust opcode due to addressing mode in group 5 (cp. commands.txt)
          adjustOpcode(group5, mnemonic, 3, "REG,|REG|");
          return;
        }
      }
      return;
    }
    // Check if "MOV [..."
    if (isLeftBracket()) {
      nextToken();
      if (!(isHexNumber(token) || isRegister())) {
        ifNewlineThrowCheckerException(MS.EXPECTED_HEX_REGISTER, 520L, 521L);
      }
      // Check if  "MOV [20..."
      if (isHexNumber(token)) {
        writeByte(Integer.parseInt(token, HEX_BASE));
        
        nextToken();
        // Check if  "MOV [20]..."
        if (!isRightBracket()) {
          ifNewlineThrowCheckerException(MS.EXPECTED_RIGHT_BRACKET, 522L, 523L);
        }
        
        nextToken();
        // Check if  "MOV [20],..."
        if (!isComma()) {
          ifNewlineThrowCheckerException(MS.EXPECTED_COMMA, 524L, 525L);
        }

        nextToken();
        // Check if "MOV [20],AL..."
        if (!isRegister()) {
          ifNewlineThrowCheckerException(MS.EXPECTED_REGISTER, 526L, 527L);
        }
        writeByte(getRegisterCode());

        nextToken();
        ifNotNewlineNotCommentThrowCheckerException(528L);
        
        // Adjust opcode due to addressing mode in group 5 (cp. commands.txt)
        adjustOpcode(group5, mnemonic, 3, "|ADDR|,REG");
        return;
      }
      // Check if  "MOV [AL..."
      if (isRegister()) {
        writeByte(getRegisterCode());

        nextToken();
        // Check if  "MOV [AL]..."
        if (!isRightBracket()) {
          ifNewlineThrowCheckerException(MS.EXPECTED_RIGHT_BRACKET, 529L, 530L);
        }
          
        nextToken();
        // Check if  "MOV [AL],..."
        if (!isComma()) {
          ifNewlineThrowCheckerException(MS.EXPECTED_COMMA, 531L, 532L);
        }

        nextToken();
        // Check if "MOV [AL],BL..."
        if (!isRegister()) {
          ifNewlineThrowCheckerException(MS.EXPECTED_REGISTER, 533L, 534L);
        }
        writeByte(getRegisterCode());
          
        nextToken();
        ifNotNewlineNotCommentThrowCheckerException(535L);

        // Adjust opcode due to addressing mode in group 5 (cp. commands.txt)
        adjustOpcode(group5, mnemonic, 3, "|REG|,REG");
        return;
      }
    }
    else {
      // Should never reach this line
      Common.ERROR_MESSAGE = MS.ERROR_IN_CHECKMNEMONICGROUP_5;
      throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
    }
  }

  
  /**
   * Check syntax of mnemonic group 6, i.e. commands:
   * MNEM REG,CONST  or  MNEM REG,REG
   * Test if no duplicate RAM allocation occurred else throw
   * a checker exception.
   * Allocate memory and enter opcode and parameters.
   *
   * @throws SyntaxCheckerException if context error found
   */
  private void checkMnemonicGroup_6() throws SyntaxCheckerException {
    // Find the temporary opcode of the mnemonic to be changed later on
    // depending on the address mode.
    int group6 = 6;
    checkMnemonicGroup(group6);
    String mnemonic = token;        // Save mnemonic for possible opcode adjustment
    
    // Check if register
    nextToken();
    if (!isRegister()) {
      ifNewlineThrowCheckerException(MS.EXPECTED_REGISTER, 600L, 601L);
    }
    writeByte(getRegisterCode());
    
    // Check if comma
    nextToken();
    if (!isComma()) {
      ifNewlineThrowCheckerException(MS.EXPECTED_COMMA, 602L, 603L);
    }
    
    // Check if hex or register
    nextToken();
    if (!(isHexNumber(token) || isRegister())) {
      ifNewlineThrowCheckerException(MS.EXPECTED_HEX_REGISTER, 604L, 605L);
    }
    
    // Take care of one out of two possibilities
    if (isHexNumber(token)) {
      writeByte(Integer.parseInt(token, HEX_BASE));
      
      nextToken();
      ifNotNewlineNotCommentThrowCheckerException(606L);
      
      // Adjust opcode due to addressing mode,
      // ADD reg,const  (cp. commands.txt)
      adjustOpcode(group6, mnemonic, 3, "REG,CONST");
      return;
    }
    
    if (isRegister()) {
      writeByte(getRegisterCode());

      nextToken();
      ifNotNewlineNotCommentThrowCheckerException(607L);
      
      // Adjust opcode due to addressing mode,
      // ADD reg,reg  (cp. commands.txt)
      adjustOpcode(group6, mnemonic, 3, "REG,REG");
      return;
    }
    else {
      // Should never reach this line
      Common.ERROR_MESSAGE = MS.ERROR_IN_CHECKMNEMONICGROUP_6;
      throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
    }
  }
  
  
  /**
   * Check syntax of mnemonic group 7, i.e. commands:
   * MNEM REG; MNEM [REG]
   * Test if no duplicate RAM allocation occurred else throw
   * a checker exception.
   * Allocate memory and enter opcode and parameter.
   *
   * @throws SyntaxCheckerException if context error found
   */
  private void checkMnemonicGroup_7() throws SyntaxCheckerException {
    // Find the temporary opcode of the mnemonic to be changed later on
    // depending on the address mode.
    int group7 = 7;
    checkMnemonicGroup(group7);
    String mnemonic = token;        // Save mnemonic for possible opcode adjustment
   
    // Check if register or bracket
    nextToken();
    if (!(isRegister() || isLeftBracket())) {
      ifNewlineThrowCheckerException(MS.EXPECTED_REGISTER_BRACKET, 700L, 701L);
    }
   
    // Take care of one out of two possibilities
    // Check if "INC AL"
    if (isRegister()) {
      writeByte(getRegisterCode());
      
      nextToken();
      ifNotNewlineNotCommentThrowCheckerException(706L);
      
      // Adjust opcode due to addressing mode,
      // INC reg  (cp. commands.txt)
      adjustOpcode(group7, mnemonic, 2, "REG");
      return;
    }
    // Check if "INC [..."
    if (isLeftBracket()) {
      nextToken();
      if (!isRegister()) {
        ifNewlineThrowCheckerException(MS.EXPECTED_REGISTER, 720L, 721L);
      }
      writeByte(getRegisterCode());

      nextToken();
      // Check if  "INC [AL]..."
      if (!isRightBracket()) {
        ifNewlineThrowCheckerException(MS.EXPECTED_RIGHT_BRACKET, 729L, 730L);
      }
          
      nextToken();
      ifNotNewlineNotCommentThrowCheckerException(735L);

      // Adjust opcode due to addressing mode in group 7 (cp. commands.txt)
      adjustOpcode(group7, mnemonic, 2, "|REG|");
      return;
    }
  }
  
  
  /**
   * Check syntax of mnemonic group 8, i.e. commands:
   * NMEM ABSOLUTE
   * Test if no  duplicate RAM allocation occurred else throw
   * an checker exception.
   * Allocate memory and enter opcode and jump absolute address (if jump reference
   * already known) into ram. Otherwise allocate space for later correction
   * of the jump absolute addres. Enter symbol address into list labelrefs.
   *
   * @throws SyntaxCheckerException if context error found
   */
  private void checkMnemonicGroup_8() throws SyntaxCheckerException {
    // Find the associated opcode of the mnemonic
    checkMnemonicGroup(8);
    
    // Check if next token is a symbol
    nextToken();
    
    if (isNewline()) {
      markError(MS.EXPECTED_SYMBOL, 800L);
      throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
    }

    if (isSymbol8()) {
      // Write byte into RAM: dummy value, to be corrected
      writeByte(ramLocator);
      
      // See if next token is end of line or comment
      nextToken();
      ifNotNewlineNotCommentThrowCheckerException(803L);
      return;
    }
    else {
      // Should never reach this line
      Common.ERROR_MESSAGE = MS.ERROR_IN_CHECKMNEMONICGROUP_8;
      throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
    }
  }

  
  /**
   * Check syntax of mnemonic group 9, i.e. commands:
   * MNEM REG  or  MNEM [ADDR]
   * Test if no duplicate RAM allocation occurred else throw
   * a checker exception.
   * Allocate memory and enter opcode and parameter.
   *
   * @throws SyntaxCheckerException if context error found
   */
  private void checkMnemonicGroup_9() throws SyntaxCheckerException {
    // Find the temporary opcode of the mnemonic to be changed later on
    // depending on the address mode.
    int group9 = 9;
    checkMnemonicGroup(group9);
    String mnemonic = token;        // Save mnemonic for possible opcode adjustment
   
    // Check if register or bracket
    nextToken();
    if (!(isRegister() || isLeftBracket())) {
      ifNewlineThrowCheckerException(MS.EXPECTED_REGISTER_BRACKET, 900L, 901L);
    }
   
    // Take care of one out of two possibilities
    // Check if "DEC AL"
    if (isRegister()) {
      writeByte(getRegisterCode());
      
      nextToken();
      ifNotNewlineNotCommentThrowCheckerException(906L);
      
      // Adjust opcode due to addressing mode,
      // DEC reg  (cp. commands.txt)
      adjustOpcode(group9, mnemonic, 2, "REG");
      return;
    }
    // Check if "DEC [..."
    if (isLeftBracket()) {
      nextToken();
      if (!isHexNumber(token)) {
        ifNewlineThrowCheckerException(MS.EXPECTED_HEX, 920L, 921L);
      }
      writeByte(Integer.parseInt(token, HEX_BASE));
      
      nextToken();
      // Check if  "DEC [20]..."
      if (!isRightBracket()) {
        ifNewlineThrowCheckerException(MS.EXPECTED_RIGHT_BRACKET, 929L, 930L);
      }
          
      nextToken();
      ifNotNewlineNotCommentThrowCheckerException(935L);

      // Adjust opcode due to addressing mode in group 9 (cp. commands.txt)
      adjustOpcode(group9, mnemonic, 2, "|ADDR|");
      return;
    }
  }

  
  /**
   * Check syntax of mnemonic group 10, i.e. commands:
   * MNEM [ADDR]
   * Test if no duplicate RAM allocation occurred else throw
   * a checker exception.
   * Allocate memory and enter opcode and parameter.
   *
   * @throws SyntaxCheckerException if context error found
   */
  private void checkMnemonicGroup_10() throws SyntaxCheckerException {
    // Find the temporary opcode of the mnemonic to be changed later on
    // depending on the address mode.
    int group10 = 10;
    checkMnemonicGroup(group10);
   
    // Check if bracket
    nextToken();
    if (!isLeftBracket()) {
      ifNewlineThrowCheckerException(MS.EXPECTED_LEFT_BRACKET, 1000L, 1001L);
    }
   
    // Check if "SWAP [..."
    if (isLeftBracket()) {
      nextToken();
      if (!isHexNumber(token)) {
        ifNewlineThrowCheckerException(MS.EXPECTED_HEX, 1020L, 1021L);
      }
      writeByte(Integer.parseInt(token, HEX_BASE));
      
      nextToken();
      // Check if  "SWAP [20]..."
      if (!isRightBracket()) {
        ifNewlineThrowCheckerException(MS.EXPECTED_RIGHT_BRACKET, 1029L, 1030L);
      }
          
      nextToken();
      ifNotNewlineNotCommentThrowCheckerException(1035L);
      return;
    }
  }

  
  /**
   * Check syntax of mnemonic group 11, i.e. commands:
   * MNEM [REG]
   * Test if no duplicate RAM allocation occurred else throw
   * a checker exception.
   * Allocate memory and enter opcode and parameter.
   *
   * @throws SyntaxCheckerException if context error found
   */
  private void checkMnemonicGroup_11() throws SyntaxCheckerException {
    // Find the temporary opcode of the mnemonic to be changed later on
    // depending on the address mode.
    int group11 = 11;
    checkMnemonicGroup(group11);
   
    // Check if bracket
    nextToken();
    if (!isLeftBracket()) {
      ifNewlineThrowCheckerException(MS.EXPECTED_LEFT_BRACKET, 1100L, 1101L);
    }
   
    // Check if "CLINC [..."
    if (isLeftBracket()) {
      nextToken();
      if (!isRegister()) {
        ifNewlineThrowCheckerException(MS.EXPECTED_REGISTER, 1120L, 1121L);
      }
      writeByte(getRegisterCode());
      
      nextToken();
      // Check if  "CLINC [AL]..."
      if (!isRightBracket()) {
        ifNewlineThrowCheckerException(MS.EXPECTED_RIGHT_BRACKET, 1129L, 1130L);
      }
          
      nextToken();
      ifNotNewlineNotCommentThrowCheckerException(1135L);
      return;
    }
  }

  

  
  /**
   * Check syntax of mnemonic group 12, i.e. commands:
   * MNEM [REG],CONST
   * Test if no duplicate RAM allocation occurred else throw
   * a checker exception.
   * Allocate memory and enter opcode and parameter.
   *
   * @throws SyntaxCheckerException if context error found
   */
  private void checkMnemonicGroup_12() throws SyntaxCheckerException {
    int group12 = 12;
    checkMnemonicGroup(group12);
   
    // Check if bracket
    nextToken();
    if (!isLeftBracket()) {
      ifNewlineThrowCheckerException(MS.EXPECTED_LEFT_BRACKET, 1200L, 1201L);
    }
   
    // Check if "CPYD [..."
    if (isLeftBracket()) {
      nextToken();
      if (!isRegister()) {
        ifNewlineThrowCheckerException(MS.EXPECTED_REGISTER, 1220L, 1221L);
      }
      writeByte(getRegisterCode());
      
      nextToken();
      // Check if  "CPYD [AL]..."
      if (!isRightBracket()) {
        ifNewlineThrowCheckerException(MS.EXPECTED_RIGHT_BRACKET, 1229L, 1230L);
      }

      // Check if comma
      nextToken();
      if (!isComma()) {
        ifNewlineThrowCheckerException(MS.EXPECTED_COMMA, 1202L, 1203L);
      }
      
      // Check if hex
      nextToken();
      if (!isHexNumber(token)) {
        ifNewlineThrowCheckerException(MS.EXPECTED_HEX_REGISTER, 1204L, 1205L);
      }
      
      writeByte(Integer.parseInt(token, HEX_BASE));
        
      nextToken();
      ifNotNewlineNotCommentThrowCheckerException(1206L);
      return;
    }
  }

  
  /**
   * Check syntax of mnemonic group 13, i.e. commands:
   * MNEM REG,REG
   * Test if no duplicate RAM allocation occurred else throw
   * a checker exception.
   * Allocate memory and enter opcode and parameter.
   *
   * @throws SyntaxCheckerException if context error found
   */
  private void checkMnemonicGroup_13() throws SyntaxCheckerException {
    int group13 = 13;
    checkMnemonicGroup(group13);
    
    // Check if register
    nextToken();
    if (!isRegister()) {
      ifNewlineThrowCheckerException(MS.EXPECTED_REGISTER, 1300L, 1301L);
    }
    writeByte(getRegisterCode());
    
    // Check if comma
    nextToken();
    if (!isComma()) {
      ifNewlineThrowCheckerException(MS.EXPECTED_COMMA, 1302L, 1303L);
    }

    // Check if register
    nextToken();
    if (!isRegister()) {
      ifNewlineThrowCheckerException(MS.EXPECTED_REGISTER, 1304L, 1305L);
    }
    writeByte(getRegisterCode());

    nextToken();
    ifNotNewlineNotCommentThrowCheckerException(1307L);
    return;
  }
  

  /**
   * Write a byte into RAM.
   * 
   * @param hex The hexadecimal to be written
   * @throws SyntaxCheckerException if context error found
   */
  private void writeByte(int hex) throws SyntaxCheckerException {
    // First test if RAM space exceeded
    if (ramLocator > 0xFF) {
      markError(MS.RAM_SPACE_EXCEEDED, 12L);
      throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
    }
    // Test if code is already allocated in RAM
    else if (memory.getUsedRamSlots()[ramLocator]) {
      markError(MS.DUPLICATE_RAM_ALLOCATION + Common.toHexByteString(ramLocator), 13L);
      throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
    }
    else {
      // Write opcode, mark allocation field and increment RAM locator
      memory.getRam()[ramLocator] = hex;
      memory.getUsedRamSlots()[ramLocator] = true;
      ramLocator++;
    }
  }

  
  /**
   * Write DB string bytes into RAM.
   * 
   * @throws SyntaxCheckerException if context error found
   */
  private void writeBytes() throws SyntaxCheckerException {
    String str = token.substring(1, token.length() - 1);
    for (int i = 0; i < str.length(); ++i) {
      // First test if RAM space exceeded
      if (ramLocator > 0xFF) {
        markError(MS.RAM_SPACE_EXCEEDED, 1010L);
        throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
      }
      // Test if code is already allocated in RAM
      else if (memory.getUsedRamSlots()[ramLocator]) {
        markError(
          MS.DUPLICATE_RAM_ALLOCATION + Common.toHexByteString(ramLocator), 1011L);
        throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
      }
      else {
        // Write opcode, mark used RAM slots and increment RAM locator
        memory.getRam()[ramLocator] = str.charAt(i);
        memory.getUsedRamSlots()[ramLocator] = true;
        ramLocator++;
      }
    }
  }

  
  /**
   * Grab next token.
   */
  private void nextToken() {
    token = Common.getToken(tokenListIndex += 3);   // Token in every third slot
  }
  
  
  /**
   * Test if token is a hex number with one or two digits.
   * 
   * @param string The hex number string candidate
   * @return true if hex and format ok, else answer false
   */
  private boolean isHexNumber(String string) {
    if (string.length() > 2) {
      return false;
    }
    return (Pattern.matches("[0-9A-F]+", string.toUpperCase()));
  }
  
  
  /**
   * Test if token is a register, i.e. AL, BL, CL, DL or SP.
   * 
   * @return true if a register, else answer false
   */
  private boolean isRegister() {
    return Common.REGISTERS.contains(token);
  }
  
  
  /**
   * Test if token is a comma.
   * 
   * @return true if a comma, else answer false
   */
  private boolean isComma() {
    return token.equals(",");
  }
  
    
  /**
   * Test if token is a left bracket '['.
   * 
   * @return true if a left bracket, else answer false
   */
  private boolean isLeftBracket() {
    return token.equals("[");
  }

  
  /**
   * Test if token is a right bracket ']'.
   * 
   * @return true if a right bracket, else answer false
   */
  private boolean isRightBracket() {
    return token.equals("]");
  }

  
  /**
   * Get the code of a register defined in list registers.
   * 
   * @return the register code, i.e. the index of the rgeister in list registers
   * @throws SyntaxCheckerException if context error found
   */
  private int getRegisterCode() throws SyntaxCheckerException {
    for (int i = 0; i < Common.REGISTERS.size(); ++i) {
      if (Common.REGISTERS.get(i).equals(token)) {
        return i;
      }
    }
    // Should never reach this line
    Common.ERROR_MESSAGE = MS.ERROR_IN_GETREGISTERCODE;
    throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
  }
  

  /**
   * Test if token is a symbol, used in connection with labels.
   * If ok, enter symbol into label list etc.
   * 
   * @return true if a valid symbol, else answer false
   * @throws SyntaxCheckerException if context error found
   */
  private boolean isSymbol3() throws SyntaxCheckerException {
    // See if symbol is a hex number only
    if (token.length() == 2 || token.length() == 1) {
      if (Pattern.matches("[0-9A-F]+", token)) {
        markError(MS.LABEL_MUST_NOT_BE_HEX, 301L);
        throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
      }
    }
    
    // See if token (=symbol) contains an invalid character
    for (char c : INVALID_SYMBOLCHAR_ARRAY) {
      if (token.contains("" + c)) {
        markError(MS.INVALID_CHAR_IN_SYMBOL + c + "'", 302L);
        throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
      }
    }

    // Enter the symbol into labels list
    labels.add(token.toUpperCase());
    // Enter the symbols's address into labelrefs list
    labelrefs.add(ramLocator);
    // Remember line number
    String lineStr = Common.getToken(tokenListIndex - 2);
    labelLinenum.add(lineStr);

    return true;
  }

  
  /**
   * Test if token is a symbol, used in connection with labels.
   * If ok, enter symbol into label list etc.
   * 
   * @return true if a valid symbol, else answer false
   * @throws SyntaxCheckerException if context error found
   */
  private boolean isSymbol8() throws SyntaxCheckerException {
    // See if symbol is a hex number only
    if (token.length() == 2 || token.length() == 1) {
      if (Pattern.matches("[0-9A-F]+", token)) {
        markError(MS.LABEL_MUST_NOT_BE_HEX, 301L);
        throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
      }
    }
    
    // See if token (=symbol) contains an invalid character
    for (char c : INVALID_SYMBOLCHAR_ARRAY) {
      if (token.contains("" + c)) {
        markError(MS.INVALID_CHAR_IN_SYMBOL + c + "'", 302L);
        throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
      }
    }

    // Enter the symbol into labels list
    labels.add(token.toUpperCase());
    // Or 0xFF00 to mark that the absolute address for JPA should be used
    labelrefs.add(ramLocator | 0xFF00);
    // Remember line number
    String lineStr = Common.getToken(tokenListIndex - 2);
    labelLinenum.add(lineStr);

    return true;
  }

  
  /**
   * Test if there are any invalid characters in the symbol name.
   * 
   * @throws SyntaxCheckerException if context error found
   */
  private void testCharsInLabel() throws SyntaxCheckerException {
    // See if token (=symbol) contains an invalid character
    for (char c : INVALID_LABELCHAR_ARRAY) {
      if (token.contains("" + c)) {
        markError(MS.INVALID_CHAR_IN_SYMBOL + c + "'", 22L);
        throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
      }
    }
  }
    
 
  /**
   * Mark an error and store it in the global variable Common.ERROR_MESSAGE.
   * 
   * @param linenum The number of the line the error was found in
   * @param message The error message for this error
   */
  private void markError(String message, long errorNum) {
    String lineStr = Common.getToken(tokenListIndex - 2);
    this.errorNum = errorNum;
    
    Common.ERROR_MESSAGE = Common.title + " " + Common.version + "\n\n" +
        "Line " + lineStr + ": " + message;

    if (Debug.SC_ON) { 
       Common.ERROR_MESSAGE += "\n{error: " + errorNum + "}";
    }
  }
  
  
  /**
   * Resolve symbols and labels and test if all symbols have a corresponding
   * label.
   * Labels and symbols are collected during parsing in the lists labels and
   * labelrefs. Defined labels and used symbols are marked during the dereferencing
   * process. This makes it possible to check if all symbols are paired with their
   * corresponding label.
   * 
   * @throws SyntaxCheckerException
   */
  private void resolveLabelAddresses() throws SyntaxCheckerException {
    // Get the "LABEL:" string and its address
    for (int pos = 0; pos < labels.size(); ++pos) {
      String label = labels.get(pos);
      
      if (label.endsWith(":")) {
        String ref = label.substring(0, label.length() - 1);
        int labelAddr = labelrefs.get(pos);
        labelrefs.set(pos, DEFINED);
        
        // Get the possible "LABEL" = "SYMBOL"
        for (int loc = 0; loc < labels.size(); ++loc) {
          String lbl = labels.get(loc);
          int symbolAddr = labelrefs.get(loc);
          // See if they match
          if (ref.equals(lbl)) {
            // Offset or absolute address?
            if ((symbolAddr & 0xFF00) == 0xFF00) {
              // Absolute jump address symbol
              symbolAddr &= 0xFF;
              memory.getRam()[symbolAddr] = labelAddr;
              labelrefs.set(loc, USED);
            }
            else {
              // Calculate relative jump address
              int relDisplacement = labelAddr - symbolAddr + 1 ;
              // Label reachable?
              if (relDisplacement < -128 || relDisplacement >= 128) {
                String linenum = labelLinenum.get(pos);
                Common.ERROR_MESSAGE = "\nLine " + linenum + ": Label '" + labels.get(pos) + "' not reachable";
                errorNum = 28L;
                throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
              }
              // Forward or backward jump?
              if (relDisplacement < 0) {
                relDisplacement += 0x100;
              }
              memory.getRam()[symbolAddr] = relDisplacement;
              labelrefs.set(loc, USED);
            }
          }
        }
      }
    }
    
    // Check if all symbols paired with a label
    for (int pos = 0; pos < labelrefs.size(); ++pos) {
      if (labelrefs.get(pos) != USED) {
        String linenum = labelLinenum.get(pos);

        Common.ERROR_MESSAGE = "\nLine " + linenum + MS.CANNOT_FIND_LABEL_FOR_SYMBOL + labels.get(pos) + "'";
        errorNum = 24L;
        throw new SyntaxCheckerException(Common.ERROR_MESSAGE);
      }
    }
  }
  
  
  
  // Methods for testing
  public String getErrorMessage() {
    return Common.ERROR_MESSAGE;
  }
  
  public long getErrorNum() {
    return errorNum;
  }
}
