package assembler;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import assembler.support.Common;


/**
 * The class GroupListBuilder reads the file INSTRUCTION.data, parses mnemonics,
 * command patterns and opcodes. It builds two lists with mnemonics and opcodes
 * grouped according to the address modes to be checked by the assembler.
 * 
 * @author ruedi.mueller
 */
public class GroupListBuilder {
  // The set with all command mnemonics read in from file INSTRUCTION.data.
  public Set<String> mnemonicSet;
  // Each list contains assembler commands of a group. The syntax checker relies on groups
  // as each group is syntactically treated in a different way: Cp. description in
  // test files.
  // List of mnemonic lists. Each mnemonic list represents a group for specific addressing
  // modes, cp. commands.txt
  public List<ArrayList<String>> commandSymGroupList;
  // List of opcode-string lists. Each opcode-string list matches the mnemonic with its
  // opcode-string, cp. commands.txt
  public List<ArrayList<String>> commandHexGroupList;
  // List of commandPattern lists. Each commandPattern list matches the mnemonic with its
  // opcode-string, cp. commands.txt
  public List<ArrayList<String>> commandPatternGroupList;
  
 
  /**
   * Build lists with addressing mode groups and a set with all known mnemonics
   * read in from file INSTRUCTION.data.
   * 
   * @param instructionDataString String from INSTRUCTION.data
   */
  public GroupListBuilder(String instructionDataString) {
    commandSymGroupList = new ArrayList<ArrayList<String>>();
    commandHexGroupList = new ArrayList<ArrayList<String>>();
    commandPatternGroupList = new ArrayList<ArrayList<String>>();

    try {
      BufferedReader br = new BufferedReader(new StringReader(instructionDataString));

      // Set up lists in order to later determine the command group of each command
      List<String> mnemonicList = new ArrayList<String>();
      List<String> opcodeList = new ArrayList<String>();
      List<String> commandPatternList = new ArrayList<String>();
      List<Integer> addressmodeCodeList = new ArrayList<Integer>();
      mnemonicSet = new HashSet<String>();
      String strLine;
      // Read file line by line
      while ((strLine = br.readLine()) != null) {
        // Ignore comment lines with "//"" as well as empty lines
        if (strLine.trim().startsWith("//") || strLine.trim().isEmpty()) {
          continue;
        }
        strLine= strLine.toUpperCase();        
      
        // Separate line into substrings
        StringTokenizer tokens = new StringTokenizer(strLine, ";");
        // Ignore mpmEntry which is not needed here
        /* String mpmEntry = */ tokens.nextToken();
        String opcode = tokens.nextToken();
        String commandPattern = tokens.nextToken();

        // Parse mnemonic
        StringTokenizer elements = new StringTokenizer(commandPattern, " ,");
        String mnemonic = elements.nextToken();
        
        // Build lists in parallel
        mnemonicList.add(mnemonic);
        int addressmodeCode = getFromCommandPattern(strLine);
        addressmodeCodeList.add(addressmodeCode);
        opcodeList.add(opcode);
        commandPatternList.add(commandPattern);
        mnemonicSet.add(mnemonic);
      }
    
      // Create empty commands lists and add them to the respective group list
      for (int group = 0; group < Common.MAX_NOF_GROUPS; ++group) {
        commandSymGroupList.add(new ArrayList<String>());
        commandHexGroupList.add(new ArrayList<String>());
        commandPatternGroupList.add(new ArrayList<String>());
      }
      
      // Iterate over mnemonic set
      Iterator<String> mnemonicIt = mnemonicSet.iterator();
      while (mnemonicIt.hasNext()) {
        String mnemonic = mnemonicIt.next();
        
        List<String> tempSymCommands = new ArrayList<String>();
        List<String> tempHexCommands = new ArrayList<String>();
        List<String> tempCommandPatterns = new ArrayList<String>();
        int code = 0;   // Build code for a new mnemonic
        // For a chosen mnemonic search for same mnemonics in the mnemonicList
        for (int index = 0; index < mnemonicList.size(); ++index) {
          if (mnemonicList.get(index).equals(mnemonic)) {
            // Chosen mnemonic and mnemonic in mnemonicList are equal.
            // Add code
            code += addressmodeCodeList.get(index);
            
            tempSymCommands.add(mnemonicList.get(index));
            tempHexCommands.add(opcodeList.get(index));
            tempCommandPatterns.add(commandPatternList.get(index));
          }
        }

        int group = addressmodeCodeListToGroup(code);
        
        commandSymGroupList.get(group).addAll(tempSymCommands);
        commandHexGroupList.get(group).addAll(tempHexCommands);
        commandPatternGroupList.get(group).addAll(tempCommandPatterns);
      }
      // Add directive
      commandSymGroupList.get(0).add("END");
      commandHexGroupList.get(0).add("00");
      commandPatternGroupList.get(0).add("END");

      // Remember all jump commands in groups 3 and 8 for later use in CodeListBuilder
      Common.JUMPS = commandSymGroupList.get(3);
      Common.ABSOLUTE_JUMPS = commandSymGroupList.get(8);
      
      // Close the input stream
      br.close();
    }
    catch (Exception ex) {
      ex.printStackTrace();
      System.exit(-1);
    }
  }
  
  
  /**
   * Translate from addressmodeCode to the addressing mode group.
   * The case numbers are built in adding all address modeCodes. Example:
   *   case 224:   equals     case  2^5 +           2^6 +         2^7: ,
   *   i.e. group 4 contains         ^               ^             ^
   *   command patterns as follows: MNEM REG,CONST; MNEM REG,REG; MNEM REG,[ADDR]
   *
   * TODO: Add codes for remaining addressing modes according to syntax.txt
   *       Extra code is needed in class SyntaxChecker to test such commands.
   * 
   * @param code
   * @return
   * @throws NoSuchAddressModeCodeException 
   */
  private int addressmodeCodeListToGroup(int code) throws NoSuchAddressModeCodeException {
    switch (code) {
      case 0:      return 0;   // group 0
      case 2:      return 1;   // group 1       case 2^1:
      case 8:      return 2;   // group 2       case 2^3:
      case 1:      return 3;   // group 3       case 2^0:
      case 224:    return 4;   // group 4       case 2^5 + 2^6 + 2^7:
      case 2016:   return 5;   // group 5       etc.
      case 96:     return 6;   // group 6
      case 24:     return 7;   // group 7
      case 2048:   return 8;   // group 8
      case 12:     return 9;   // group 9
      case 4:      return 10;  // group 10
      case 16:     return 11;  // group 11      case 2^4:
      case 4096:   return 12;  // group 12      case 2^12:
      case 64:     return 13;  // group 13      case 2^6:
     default:
        throw new NoSuchAddressModeCodeException("Error in addressmodeCodeListToGroup(int code)");
    }
  }
  
  
  /**
   * Determine what group this command pattern belongs to and
   * answer the addressmodeCode.
   *   commandPattern   addressmodeCode
   *   MNEM              0
   *   MNEM offset       2^0
   *   MNEM addr         2^1
   *   MNEM |addr|       2^2
   *   MNEM reg          2^3
   *   MNEM |reg|        2^4
   *   MNEM reg,const    2^5
   *   MNEM reg,reg      2^6
   *   MNEM reg,|addr|   2^7
   *   MNEM reg,|reg|    2^8
   *   MNEM |reg|,reg    2^9
   *   MNEM |addr|,reg   2^10
   *   MNEM absolute     2^11
   *   MNEM |reg|,const  2^12
   *   
   * @param strLine
   * @return the addressmodeCode
   * @throws NoSuchCommandPatternException 
   */
  private int getFromCommandPattern(String commandPattern) throws NoSuchCommandPatternException {
    StringTokenizer elements = new StringTokenizer(commandPattern, " ,");
    /* String mnemonic = */ elements.nextToken();
 
    if (elements.countTokens() == 0)   return 0;          // MNEM

    String firstParam = elements.nextToken();
    if (elements.countTokens() == 0) {
      // Commands with one parameter
      if (firstParam.equals("OFFSET"))   return 1;        // MNEM offset
      if (firstParam.equals("ADDR"))     return 1 << 1;   // MNEM addr
      if (firstParam.equals("|ADDR|"))   return 1 << 2;   // MNEM |addr|
      if (firstParam.equals("REG"))      return 1 << 3;   // MNEM reg
      if (firstParam.equals("|REG|"))    return 1 << 4;   // MNEM |reg|
      if (firstParam.equals("ABSOLUTE")) return 1 << 11;  // MNEM absolute
      throw new NoSuchCommandPatternException("Error while trying to find an addressing mode");
    }

    String secondParam = elements.nextToken();
    if (elements.countTokens() == 0) {
      // Commands with two parameters
      if (firstParam.equals("REG") && secondParam.equals("CONST"))   return 1 << 5;   // MNEM reg,const
      if (firstParam.equals("REG") && secondParam.equals("REG"))     return 1 << 6;   // MNEM reg,reg
      if (firstParam.equals("REG") && secondParam.equals("|ADDR|"))  return 1 << 7;   // MNEM reg,|addr|
      if (firstParam.equals("REG") && secondParam.equals("|REG|"))   return 1 << 8;   // MNEM reg,|reg|
      if (firstParam.equals("|REG|") && secondParam.equals("REG"))   return 1 << 9;   // MNEM |reg|,reg
      if (firstParam.equals("|ADDR|") && secondParam.equals("REG"))  return 1 << 10;  // MNEM |addr|,reg
      if (firstParam.equals("|REG|") && secondParam.equals("CONST")) return 1 << 12;  // MNEM |reg|,const
      throw new NoSuchCommandPatternException("Error while trying to find an addressing mode");
    }
    throw new NoSuchCommandPatternException("Error while trying to find an addressing mode");
  }
}
