package assembler;


/**
 * The class MS (MessageString) is used to collect all message
 * strings in one place.
 * 
 * @author ruedi.mueller
 */
public class MS {
  // Success
  public static final String SUCCESS =
      "\n; Success: No errors found";
  
  
  // Assembler error messages due to wrong syntax
  public static final String END_IS_MISSING =
      "END is missing";

  public static final String EXPECTED_HEX =
      "Expected hexadecimal number (range 00..FF)";

  public static final String EXPECTED_HEX_QUOTE_DOUBLEQUOTE =
      "Expected hexadecimal number (range 00..FF), a quote (') or double quote (\")";

  public static final String EXPECTED_HEX_REGISTER =
      "Expected hexadecimal number (range 00..FF) or register";

  public static final String EXPECTED_CHAR_ENCLOSED =
      "Expected character enclosed in ', got ";

  public static final String INVALID_CHAR =
      "Invalid character '";

  public static final String INVALID_STRING =
      "Invalid character in string, found '";

  public static final String EXPECTED_CLOSING_QUOTE =
      "Expected closing ', got '";

  public static final String EMPTY_STRINGS_NOT_ALLOWED =
      "Empty strings not allowed";

  public static final String EXPECTED_CLOSING_DOUBLE_QUOTE =
      "Expected closing \", got '";

  public static final String EXPECTED_LABEL_MNEMONIC_COMMENT =
      "Expected label, mnemonic, directive or comment, got a ':'";

  public static final String EXPECTED_MNEMONIC =
      "Expected mnemonic, got '";

  public static final String EXPECTED_MNEMONIC_DIRECTIVE_COMMENT =
      "Expected mnemonic, directive or comment, got a ':'";

  public static final String LABEL_MUST_NOT_BE_HEX =
      "Label must not be hexadecimal number, found: ";

  public static final String DUPLICATE_LABEL_NAMES_NOT_ALLOWED =
      "Duplicate (case insensitive) label names not allowed";

  public static final String ONLY_ONE_LABEL_PER_LINE =
      "Only one label allowed per line";

  public static final String ONLY_ONE_MNEMONIC_PER_LINE =
      "Only one mnemonic allowed per line";

  public static final String EXPECTED_REGISTER =
      "Expected register";

  public static final String EXPECTED_SYMBOL =
      "Expected symbol";

  public static final String EXPECTED_COMMENT =
      "Expected a comment, got '";

  public static final String EXPECTED_COMMA =
      "Expected comma";

  public static final String EXPECTED_HEX_REGISTER_BRACKET =
      "Expected hexadecimal number (range 00..FF), register or '['";

  public static final String EXPECTED_LEFT_BRACKET =
      "Expected '['";

  public static final String EXPECTED_RIGHT_BRACKET =
      "Expected ']'";

  public static final String EXPECTED_REGISTER_BRACKET =
      "Expected register or '['";

  public static final String RAM_SPACE_EXCEEDED =
      "RAM space exceeded";

  public static final String DUPLICATE_RAM_ALLOCATION =
      "Duplicate RAM allocation at address ";

  public static final String INVALID_CHAR_IN_SYMBOL =
      "Invalid character in label or symbol, found '";

  public static final String CANNOT_FIND_LABEL_FOR_SYMBOL =
      ": Cannot find label for symbol '";
  
  
  // ERROR messages to report programming errors in syntax checker
  public static final String ERROR_IN_CHECKSYNTAX =
      "Error in checkSyntax(): Should never reach this point";

  public static final String ERROR_IN_ISMNEMONIC =
      "Error in isMnemonic(): Should never reach this point";
  
  public static final String ERROR_IN_PICK_ADDRESS_MODE_VARIANT =
      "Error in pickAdressModeVariant(...): Should never reach this point";

  public static final String ERROR_IN_CHECKMNEMONICGROUP_3 =
      "Error in checkMnemonicGroup_3(): Should never reach this point";

  public static final String ERROR_IN_CHECKMNEMONICGROUP_4 =
      "Error in checkMnemonicGroup_4(): Should never reach this point";

  public static final String ERROR_IN_CHECKMNEMONICGROUP_5 =
      "Error in checkMnemonicGroup_5(): Should never reach this point";

  public static final String ERROR_IN_CHECKMNEMONICGROUP_6 =
      "Error in checkMnemonicGroup_6(): Should never reach this point";

  public static final String ERROR_IN_CHECKMNEMONICGROUP_8 =
      "Error in checkMnemonicGroup_8(): Should never reach this point";

  public static final String ERROR_IN_GETREGISTERCODE =
      "Error in getRegisterCode(): Should never reach this point";

  
  // ERROR messages to report programming errors in code list builder
  public static final String ERROR_IN_BUILDCODELINE =
      "Error in buildCodeline(): Should never reach this point";
}
