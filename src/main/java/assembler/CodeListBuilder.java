package assembler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import assembler.support.Common;
import assembler.support.Debug;


/**
 * The class CodeListBuilder is responsible for producing a formatted code list.
 * The code list can be accessed from outside CodeListBuilder via Common.getCodeList().
 * 
 * @author ruedi.mueller
 */
public class CodeListBuilder {
  // New line symbol
  private final static String NL = Common.NL;
  
  // Tab spaces for code list aligning
  private final static int MNEM_TAB = 4;
  private final static int PARAM_TAB = 10;
  private final static int CODE_TAB = 21;
  private final static int COMMENT_TAB = 41;
  
  
  // Set with commands
  private Set<String> commandList = new HashSet<String>();

  // List with all tokens, without line number and without line position,
  // a copy from Tokenizer.
  private List<String> tokenList = new ArrayList<String>();
  // Pointer into tokenList pointing to token
  private int tokenListPointer;
  // The current token
  String token;
  
  // The string builder with the code list
  private StringBuilder codeListSB = new StringBuilder(
    "; " + Common.title + " " + Common.version + Common.copyright);
  // Helper string builder to build the codeListSB
  private StringBuilder tempSB;

  // The position within a code line
  private int linePosition;
  
  // Pointer to ram
  private int ramLocator = 0;
  
  // Value of the current opcode
  private int opcode;
  // Values of the current parameter 1 and parameter 2, if present
  private int param1;
  private int param2;
  
  // Array which matches each command machine code byte with the line number of the command
  // in the source code.
  // Introduced in C# stebs to highlight line in execution.
  // index: ram address
  // data: line number
  private int[] codeToLineArr = new int[256];
  // The sourceCodeLineCounter to hold the current line number of the command in the source code
  // while building the code list.
  // Incremented when a new line in the source code is being assembled.
  private int sourceCodeLineCounter = 0;
  
  
  // Getters
  public StringBuilder getCodeListSB() {
    return codeListSB;
  }


  public int[] getCodeToLineArr() {
    return codeToLineArr;
  }


  /**
   * Answer a string of a size to allow aligning. Used
   * for a formatted code list.
   * 
   * @param tabPosition The alignment position
   * @return the number of spaces needed for alignment
   */
  private String spacing(int tabPosition) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < tabPosition - linePosition; ++i) {
      sb.append(" ");
    }
    return sb.toString();
  }
  
  
  /**
   * Instantiate a code lister and copy a modified token list.
   */
  public CodeListBuilder(Set<String> commandList) {
    // Reference the command list read in via GroupListBuilder.
    this.commandList = commandList;
    // Copy a modified token list from the assembler asm.
    TokenList list = Common.getTokenList();
    for (int pos = 2; pos < list.size(); pos += 3) {
      tokenList.add(list.get(pos));
    }
    
    // Last token must be a newline
    if (!tokenList.get(tokenList.size() - 1).equals(NL)) {
      tokenList.add(NL);
    }

    tokenListPointer = 0;

    if (Debug.CBL_ON) { 
      System.out.println("tokenList (CodeListBuilder) 1:\n  " + tokenList);
    }
    
    Arrays.fill(codeToLineArr, -1);
  }
  
  
  /**
   * Build the list with assembler code, machine code and the comments.
   * The assembler code is considered to be error free, therefore no
   * error conditions are tested.
   */
  public void buildCodeList() throws CodeListBuilderException {
    while (tokenListPointer < tokenList.size()) {
      if (buildCodeline()) {
        // First END encountered, exit
        break;
      }
    }
    codeListSB.append(MS.SUCCESS);
    codeListSB.append("   (" + new Date() + ")");
    
    // Make code list known to Common
    Common.setCodeList(codeListSB);
  }
  
  
  /**
   * Build a code line.
   * The assembler code is considered to be error free as checked by the
   * syntax checker, therefore no error conditions are tested.
   * 
   * @return true if first END encountered, else false
   * @throws CodeListBuilderException if context error found
   */
  private boolean buildCodeline() throws CodeListBuilderException {
    token = tokenList.get(tokenListPointer);
    tempSB = new StringBuilder();
    linePosition = 0;
    
    if (isNLorCommentNL())   return false;
    if (isLabel())   return false;
    if (isDb())   return false;
    if (isOrg())   return false;
    if (isJump())   return false;
    if (isCommand())   return false;
    if (isEnd())   return true;
    // Should never reach this point
    throw new CodeListBuilderException(MS.ERROR_IN_BUILDCODELINE);
  }

  
  /**
   * Test if a modified END is encountered which tells the
   * code list builder that no lines beyond this point should
   * be dispayed.
   * 
   * Notice:
   * END modified into Common.TEMP_END by SyntaxChecker>>isEnd()
   * 
   * @return true if modified END found, else false
   */
  private boolean isEnd() {
    if (token.equals(Common.TEMP_END)) {
      tokenList.set(tokenListPointer, "END");
      token = tokenList.get(tokenListPointer);
      if (Debug.CBL_ON) { 
        System.out.println("tokenList (CodeListBuilder) 2:\n  " + tokenList);
      }
      // END...
      tempSB.append(spacing(MNEM_TAB) + token);
      linePosition = tempSB.length();
      
      ++tokenListPointer;
      token = tokenList.get(tokenListPointer);
      if (token.equals(NL)) {
        // END \n
        setOpcode();
        tempSB.append(spacing(CODE_TAB) + toAddress() + toOpcode());
        
        if (Debug.CBL_ON) {
          tempSB.append("{Mnem \\n}");
        }
        
        tempSB.append("\n");
        sourceCodeLineCounter++; 
        ramLocator++;
        codeListAppend(tempSB, 1);
        return true;
      }
      if (token.startsWith(";")) {
        // END ;xy \n
        setOpcode();
        tempSB.append(spacing(CODE_TAB) + toAddress() + toOpcode());
        ramLocator++;
        linePosition = tempSB.length();

        ++tokenListPointer;
        tempSB.append(spacing(COMMENT_TAB) + token);
        
        if (Debug.CBL_ON) {
          tempSB.append("{Mnem ;xy \\n}");
        }
        
        tempSB.append("\n");
        sourceCodeLineCounter++;
        codeListAppend(tempSB, 1);
        return true;
      }
      
      // Other irrelevant string found. Ignore it and return
      setOpcode();
      tempSB.append(spacing(CODE_TAB) + toAddress() + toOpcode());
      codeListAppend(tempSB, 1);
      return true;
    }
    // Could not find Common.TEMP_END
    return false;
  }
    
  
  /**
   * Test if newline or comment plus newline. If so, then
   * append it/them to the codeListSB.
   * Take care of tokenListPointer management.
   * 
   * @return true if found and appended, else false
   */
  private boolean isNLorCommentNL() {
    if (token.equals(NL)) {
      if (Debug.CBL_ON) {
        tempSB.append("{\\n}");
      }

      codeListSB.append("\n");
      sourceCodeLineCounter++;
      ++tokenListPointer;
      return true;
    }
    if (token.startsWith(";")) {
      tempSB.append(spacing(COMMENT_TAB) + token);

      if (Debug.CBL_ON) {
        tempSB.append("{;xy \\n}");
      }
      
      tempSB.append("\n");
      sourceCodeLineCounter++;
      codeListAppend(tempSB, 2);
      return true;
    }
    return false;
  }
  
  
  /**
   * Test if a label. If so, then append it to the codeListSB.
   * Take care of tokenListPointer management.
   * 
   * @return true if found and appended, else false
   */
  private boolean isLabel() {
    if (token.endsWith(":")) {
      tempSB.append(token);
      linePosition = tempSB.length();
      
      ++tokenListPointer;
      token = tokenList.get(tokenListPointer);
      if (token.equals(NL)) {
        // Label \n
        if (Debug.CBL_ON) {
          tempSB.append("{label \\n}");
        }
        
        tempSB.append("\n");
        sourceCodeLineCounter++;
        codeListAppend(tempSB, 1);
        return true;
      }
      if (token.startsWith(";")) {
        // Label ;xy \n
        tempSB.append(spacing(COMMENT_TAB) + token);
        
        if (Debug.CBL_ON) {
          tempSB.append("{label ;xy \\n}");
        }
        
        tempSB.append("\n");
        sourceCodeLineCounter++;
        codeListAppend(tempSB, 2);
        return true;
      }
      if (isMnemonic() || token.equals("ORG") || token.equals("DB") || token.equals(Common.TEMP_END)) {
        codeListSB.append(tempSB +"\n");
        return true;
      }
    }
    return false;
  }
  
  
  /**
   * Test if DB directive. If so, then append it to the codeListSB.
   * Take care of tokenListPointer management.
   * 
   * @return true if found and appended, else false
   */
  private boolean isDb() {
    if (token.toUpperCase().equals("DB")) {
      tempSB.append(spacing(MNEM_TAB) + token.toUpperCase());
      linePosition = tempSB.length();
      
      ++tokenListPointer;
      token = tokenList.get(tokenListPointer);
      if (isHexNumber()) {
        tempSB.append(spacing(PARAM_TAB) + hexNumber(token));
        linePosition = tempSB.length();
        param1 = Common.getRam()[ramLocator];
        tempSB.append(spacing(CODE_TAB) + toAddress() + Common.toHexByteString(param1));
        ++ramLocator;
        linePosition = tempSB.length();
        
        ++tokenListPointer;
        token = tokenList.get(tokenListPointer);
        if (token.equals(NL)) {
          // DB 20 \n
          if (Debug.CBL_ON) {
            tempSB.append("{DB hex \\n}");
          }
          
          tempSB.append("\n");
          sourceCodeLineCounter++;
          codeListAppend(tempSB, 1);
          return true;
        }
        if (token.startsWith(";")) {
          // DB 20 ;xy \n
          tempSB.append(spacing(COMMENT_TAB) + token);
          
          if (Debug.CBL_ON) {
            tempSB.append("{DB hex ;xy \\n}");
          }
          
          tempSB.append("\n");
          sourceCodeLineCounter++;
          codeListAppend(tempSB, 2);
          return true;
        }
      }

      if (token.startsWith("'")) {
        tempSB.append(spacing(PARAM_TAB) + token);
        linePosition = tempSB.length();
        param1 = Common.getRam()[ramLocator];
        tempSB.append(spacing(CODE_TAB) + toAddress() + Common.toHexByteString(param1));
        ++ramLocator;
        linePosition = tempSB.length();
        
        ++tokenListPointer;
        token = tokenList.get(tokenListPointer);
        if (token.equals(NL)) {
          // DB 'a' \n
          if (Debug.CBL_ON) {
            tempSB.append("{DB char \\n}");
          }
          
          tempSB.append("\n");
          sourceCodeLineCounter++;
          codeListAppend(tempSB, 1);
          return true;
        }
        if (token.startsWith(";")) {
          // DB 'a' ;xy \n
          tempSB.append(spacing(COMMENT_TAB) + token);
          
          if (Debug.CBL_ON) {
            tempSB.append("{DB char ;xy \\n}");
          }
          
          tempSB.append("\n");
          sourceCodeLineCounter++;
          codeListAppend(tempSB, 2);
          return true;
        }
      }
      
      if (token.startsWith("\"")) {
        String tok = token;
        tempSB.append(spacing(PARAM_TAB) + token);
        linePosition = tempSB.length();
        
        ++tokenListPointer;
        token = tokenList.get(tokenListPointer);
        if (token.equals(NL)) {
          // DB "hello" \n
          if (Debug.CBL_ON) {
            tempSB.append("{DB string \\n}");
          }
          
          tempSB.append("\n");
          sourceCodeLineCounter++;
          ++tokenListPointer;
        }
        else if (token.startsWith(";")) {
          // DB "hello" ;xy \n
          tempSB.append(spacing(COMMENT_TAB) + token);
          
          if (Debug.CBL_ON) {
            tempSB.append("{DB string ;xy \\n}");
          }
          
          tempSB.append("\n");
          sourceCodeLineCounter++;
          tokenListPointer += 2;    // Jump over \n
        }
        codeListAppend(tempSB, 0);
        
        for (int i = 1; i < tok.length() - 1; ++i) {
          tempSB = new StringBuilder();
          linePosition = 0;
          param1 = Common.getRam()[ramLocator];
          tempSB.append(spacing(CODE_TAB) + toAddress() + Common.toHexByteString(param1));
          linePosition = tempSB.length();
          tempSB.append(spacing(COMMENT_TAB) + "; " + tok.charAt(i));
          
          if (Debug.CBL_ON) {
            tempSB.append("{DB char ;ascii \\n}");
          }
          
          tempSB.append("\n");
          sourceCodeLineCounter++;
          codeListAppend(tempSB, 0);
          ++ramLocator;
        }
        return true;
      }
    }
    return false;
  }
  
  
  /**
   * Test if ORG. If so, then append it to the codeListSB.
   * Take care of tokenListPointer management.
   * 
   * @return true if found and appended, else false
   */
  private boolean isOrg() {
    if (token.toUpperCase().equals("ORG")) {
      tempSB.append(spacing(MNEM_TAB) + token.toUpperCase());
      linePosition = tempSB.length();
      
      ++tokenListPointer;
      token = tokenList.get(tokenListPointer);
      tempSB.append(spacing(PARAM_TAB) + hexNumber(token));
      ramLocator = Integer.parseInt(token, 0x10);
      linePosition = tempSB.length();

      tempSB.append(spacing(CODE_TAB) + toAddress());

      ++tokenListPointer;
      token = tokenList.get(tokenListPointer);
      if (token.equals(NL)) {
        // ORG 20 \n
        if (Debug.CBL_ON) {
          tempSB.append("{ORG hex \\n}");
        }
        
        tempSB.append("\n");
        sourceCodeLineCounter++;
        codeListAppend(tempSB, 1);
        return true;
      }
      if (token.startsWith(";")) {
        // ORG 20 ;xy \n
        tempSB.append(spacing(COMMENT_TAB) + token);
        
        if (Debug.CBL_ON) {
          tempSB.append("{ORG hex ;xy \\n}");
        }
        
        tempSB.append("\n");
        sourceCodeLineCounter++;
        codeListAppend(tempSB, 2);
        return true;
      }
    }
    return false;
  }
  
  
  /**
   * Test if a jump command. If so, then append it to the codeListSB.
   * Take care of tokenListPointer management.
   * Add a \n if the label is too long to align machine code:
   * Example source code:
   *     DEC   CL
   *     JNZ   RestoreContext
   * Result code list:
   *     DEC   CL         ; [47]  A5 02
   *     JNZ   RestoreContext
   *                      ; [49]  C2 F7
   *                      
   * @return true if found and appended, else false
   */
  private boolean isJump() {
    String tok = token.toUpperCase();
    if (isJumpMnem(tok) || isJumpAbsoluteMem(tok)) {
      tempSB.append(spacing(MNEM_TAB) + token);
      linePosition = tempSB.length();
      
      ++tokenListPointer;
      token = tokenList.get(tokenListPointer);
      tempSB.append(spacing(PARAM_TAB) + token);
      linePosition = tempSB.length();
      
      if (linePosition > CODE_TAB) {
        // Start on new line as label is too long to align machine code
        if (Debug.CBL_ON) {
          tempSB.append("{Jump symbol \\n; code on newline}");
        }
        
        tempSB.append("\n");
        // No "sourceCodeLineCounter++;" added here as \n is only inserted in the code list
        // (sourceCodeLineCounter manages the source code, not the code list).
        codeListAppend(tempSB, 0);
        tempSB = new StringBuilder();
        linePosition = 0;
        tempSB.append(spacing(CODE_TAB));
        linePosition = tempSB.length();
      }
      
      setOpcodeParam();
      tempSB.append(spacing(CODE_TAB) + toAddress() + toOpcodeParam());
      linePosition = tempSB.length();
      ramLocator += 2;

      ++tokenListPointer;
      token = tokenList.get(tokenListPointer);
      if (token.equals(NL)) {
        // JMP symbol \n
        if (Debug.CBL_ON) {
          tempSB.append("{Jump symbol \\n}");
        }
        
        tempSB.append("\n");
        sourceCodeLineCounter++;         // Increment source code line counter
        codeListAppend(tempSB, 1);
        return true;
      }
      if (token.startsWith(";")) {
        // JMP symbol ;xy \n
        tempSB.append(spacing(COMMENT_TAB) + token);
        
        if (Debug.CBL_ON) {
          tempSB.append("{Jump symbol ;xy \\n}");
        }
        
        tempSB.append("\n");
        sourceCodeLineCounter++;
        codeListAppend(tempSB, 2);
        return true;
      }
    }
    return false;
  }
  
  
  /**
   * Test if a command. If so, then append it to the codeListSB.
   * Take care of tokenListPointer management.
   * 
   * @return true if found and appended, else false
   */
  private boolean isCommand() {
    if (isMnemonic())  {
      // MOV ...
      tempSB.append(spacing(MNEM_TAB) + token);
      linePosition = tempSB.length();
      
      ++tokenListPointer;
      token = tokenList.get(tokenListPointer);
      if (token.equals(NL)) {
        // NOP \n
        setOpcode();
        tempSB.append(spacing(CODE_TAB) + toAddress() + toOpcode());
        
        if (Debug.CBL_ON) {
          tempSB.append("{Mnem \\n}");
        }
        
        tempSB.append("\n");
        sourceCodeLineCounter++;
        ramLocator++;
        codeListAppend(tempSB, 1);
        return true;
      }
      if (token.startsWith(";")) {
        // NOP ;xy \n
        setOpcode();
        tempSB.append(spacing(CODE_TAB) + toAddress() + toOpcode());
        ramLocator++;
        linePosition = tempSB.length();

        ++tokenListPointer;
        tempSB.append(spacing(COMMENT_TAB) + token);
        
        if (Debug.CBL_ON) {
          tempSB.append("{Mnem ;xy \\n}");
        }
        
        tempSB.append("\n");
        sourceCodeLineCounter++;
        codeListAppend(tempSB, 1);
        return true;
      }
      
      if (isHexNumber()) {
        // CALL 20 etc
        tempSB.append(spacing(PARAM_TAB) + hexNumber(token));
        linePosition = tempSB.length();
        setOpcodeParam();
        tempSB.append(spacing(CODE_TAB) + toAddress() + toOpcodeParam());
        ramLocator += 2;
        linePosition = tempSB.length();
        
        ++tokenListPointer;
        token = tokenList.get(tokenListPointer);
        if (token.equals(NL)) {
          // CALL 20 \n
          if (Debug.CBL_ON) {
            tempSB.append("{Mnem hex \\n}");
          }
          
          tempSB.append("\n");
          sourceCodeLineCounter++;
          codeListAppend(tempSB, 1);
          return true;
        }
        if (token.startsWith(";")) {
          // CALL 20 ;xy \n
          tempSB.append(spacing(COMMENT_TAB) + token);
          
          if (Debug.CBL_ON) {
            tempSB.append("{Mnem hex ;xy \\n}");
          }
          
          tempSB.append("\n");
          sourceCodeLineCounter++;
          codeListAppend(tempSB, 2);
          return true;
        }
      }

      if (isRegister()) {
        // MOV AL etc
        tempSB.append(spacing(PARAM_TAB) + token);
        linePosition = tempSB.length();
        
        ++tokenListPointer;
        token = tokenList.get(tokenListPointer);
        if (token.equals(NL)) {
          // ROL AL \n
          setOpcodeParam();
          tempSB.append(spacing(CODE_TAB) + toAddress() + toOpcodeParam());
          ramLocator += 2;
          
          if (Debug.CBL_ON) {
            tempSB.append("{Mnem reg \\n}");
          }
          
          tempSB.append("\n");
          sourceCodeLineCounter++;
          codeListAppend(tempSB, 1);
          return true;
        }
        if (token.startsWith(";")) {
          // ROL AL ;xy \n
          setOpcodeParam();
          tempSB.append(spacing(CODE_TAB) + toAddress() + toOpcodeParam());
          ramLocator += 2;
          linePosition = tempSB.length();
          tempSB.append(spacing(COMMENT_TAB) + token);
          
          if (Debug.CBL_ON) {
            tempSB.append("{Mnem reg ;xy \\n}");
          }
          
          tempSB.append("\n");
          sourceCodeLineCounter++;
          codeListAppend(tempSB, 2);
          return true;
        }        

        if (token.equals(",")) {
          // MOV AL, etc
          tempSB.append(",");

          ++tokenListPointer;
          token = tokenList.get(tokenListPointer);
          if (isHexNumber()) {
            // MOV AL,20
            tempSB.append(hexNumber(token));
            linePosition = tempSB.length();
            
            ++tokenListPointer;
            token = tokenList.get(tokenListPointer);
            if (token.equals(NL)) {
              // MOV AL,20 \n
              setOpcodeParam12();
              tempSB.append(spacing(CODE_TAB) + toAddress() + toOpcodeParam12());
              ramLocator += 3;

              if (Debug.CBL_ON) {
                tempSB.append("{Mnem reg,hex \\n}");
              }
              
              tempSB.append("\n");
              sourceCodeLineCounter++;
              codeListAppend(tempSB, 1);
              return true;
            }
            if (token.startsWith(";")) {
              // MOV AL,20 ;xy \n
              setOpcodeParam12();
              tempSB.append(spacing(CODE_TAB) + toAddress() + toOpcodeParam12());
              ramLocator += 3;
              linePosition = tempSB.length();
              tempSB.append(spacing(COMMENT_TAB) + token);
              
              if (Debug.CBL_ON) {
                tempSB.append("{Mnem reg,hex ;xy \\n}");
              }
              
              tempSB.append("\n");
              sourceCodeLineCounter++;
              codeListAppend(tempSB, 2);
              return true;
            }        
          }
          else {
            // MOV AL, etc
            if (token.equals("[")) {
              tempSB.append(token);
              
              ++tokenListPointer;
              token = tokenList.get(tokenListPointer);
              if (isHexNumber()) {
                // MOV AL,[20]
                tempSB.append(hexNumber(token) + "]");
              }
              else {
                // MOV AL,[20]
                // MOV AL,[BL]
                tempSB.append(token + "]");
              }
              tokenListPointer += 2;
              
              token = tokenList.get(tokenListPointer);
              if (token.equals(NL)) {
                // MOV AL,[20] \n
                // MOV AL,[BL] \n
                linePosition = tempSB.length();
                setOpcodeParam12();
                tempSB.append(spacing(CODE_TAB) + toAddress() + toOpcodeParam12());
                ramLocator += 3;

                if (Debug.CBL_ON) {
                  tempSB.append("{Mnem ,[hex|reg] \\n}");
                }
                
                tempSB.append("\n");
                sourceCodeLineCounter++;
                codeListAppend(tempSB, 1);
                return true;
              }
              if (token.startsWith(";")) {
                // MOV AL,[20] ;xy \n
                // MOV AL,[BL] ;xy \n
                linePosition = tempSB.length();
                setOpcodeParam12();
                tempSB.append(spacing(CODE_TAB) + toAddress() + toOpcodeParam12());
                ramLocator += 3;
                linePosition = tempSB.length();
                tempSB.append(spacing(COMMENT_TAB) + token);
                
                if (Debug.CBL_ON) {
                  tempSB.append("{Mnem reg,[hex|reg] ;xy \\n}");
                }
                
                tempSB.append("\n");                
                sourceCodeLineCounter++;
                codeListAppend(tempSB, 2);
                return true;
              }        
            }
            else {
              // MOV AL,20
              // MOV AL,BL
              tempSB.append(token);
              ++tokenListPointer;
              token = tokenList.get(tokenListPointer);
              if (token.equals(NL)) {
                // MOV AL,20 \n
                // MOV AL,BL \n
                linePosition = tempSB.length();
                setOpcodeParam12();
                tempSB.append(spacing(CODE_TAB) + toAddress() + toOpcodeParam12());
                ramLocator += 3;
                
                if (Debug.CBL_ON) {
                  tempSB.append("{Mnem reg,hex|reg \\n}");
                }
                
                tempSB.append("\n");
                sourceCodeLineCounter++;
                codeListAppend(tempSB, 1);
                return true;
              }
              if (token.startsWith(";")) {
                // MOV AL,[20] ;xy \n
                // MOV AL,[BL] ;xy \n
                linePosition = tempSB.length();
                setOpcodeParam12();
                tempSB.append(spacing(CODE_TAB) + toAddress() + toOpcodeParam12());
                ramLocator += 3;
                linePosition = tempSB.length();
                tempSB.append(spacing(COMMENT_TAB) + token);
                
                if (Debug.CBL_ON) {
                  tempSB.append("{Mnem reg,hex|reg ;xy \\n}");
                }
                
                tempSB.append("\n");
                sourceCodeLineCounter++;
                codeListAppend(tempSB, 2);
                return true;
              }   
            }     
          }
        }
      }
        
      if (token.equals("[")) {
        // MOV [  or  INC [
        tempSB.append(spacing(PARAM_TAB) + token);
        
        ++tokenListPointer;
        token = tokenList.get(tokenListPointer);
        if (isHexNumber()) {
          // See if  MNEM [hex]  or  MNEM [hex],...
          int previewPointer = tokenListPointer;
          String previewToken = tokenList.get(previewPointer + 2);
          if (!previewToken.equals(",")) {
            // MNEM [hex]
            tempSB.append(token + "]");
            tokenListPointer += 2;
            token = tokenList.get(tokenListPointer);

            if (token.equals(NL)) {
              // MNEM [hex] \n
              linePosition = tempSB.length();
              setOpcodeParam();
              tempSB.append(spacing(CODE_TAB) + toAddress() + toOpcodeParam());
              ramLocator += 2;
              
              if (Debug.CBL_ON) {
                tempSB.append("{Mnem [hex] \\n}");
              }
              
              tempSB.append("\n");
              sourceCodeLineCounter++;
              codeListAppend(tempSB, 1);
              return true;
            }
            if (token.startsWith(";")) {
              // MNEM [hex] ;xy \n
              linePosition = tempSB.length();
              setOpcodeParam();
              tempSB.append(spacing(CODE_TAB) + toAddress() + toOpcodeParam());
              ramLocator += 2;
              linePosition = tempSB.length();
              tempSB.append(spacing(COMMENT_TAB) + token);
              
              if (Debug.CBL_ON) {
                tempSB.append("{Mnem [hex] ;xy \\n}");
              }
              
              tempSB.append("\n");
              sourceCodeLineCounter++;
              codeListAppend(tempSB, 2);
              return true;
            }   
          }
          // MNEM [hex
          tempSB.append(token + "],");
        }
        else {
          // See if  MNEM [reg]  or  MNEM [reg],...
          int previewPointer = tokenListPointer;
          String previewToken = tokenList.get(previewPointer + 2);
          if (!previewToken.equals(",")) {
            // MNEM [reg]
            tempSB.append(token + "]");
            tokenListPointer += 2;
            token = tokenList.get(tokenListPointer);

            if (token.equals(NL)) {
              // INC [AL] \n
              linePosition = tempSB.length();
              setOpcodeParam();
              tempSB.append(spacing(CODE_TAB) + toAddress() + toOpcodeParam());
              ramLocator += 2;
              
              if (Debug.CBL_ON) {
                tempSB.append("{Mnem [reg] \\n}");
              }
              
              tempSB.append("\n");
              sourceCodeLineCounter++;
              codeListAppend(tempSB, 1);
              return true;
            }
            if (token.startsWith(";")) {
              // INC [AL] ;xy \n
              linePosition = tempSB.length();
              setOpcodeParam();
              tempSB.append(spacing(CODE_TAB) + toAddress() + toOpcodeParam());
              ramLocator += 2;
              linePosition = tempSB.length();
              tempSB.append(spacing(COMMENT_TAB) + token);
              
              if (Debug.CBL_ON) {
                tempSB.append("{Mnem [reg] ;xy \\n}");
              }
              
              tempSB.append("\n");
              sourceCodeLineCounter++;
              codeListAppend(tempSB, 2);
              return true;
            }   
          }
          // MNEM [reg
          tempSB.append(token + "],");
        }
        tokenListPointer += 2;

        ++tokenListPointer;
        token = tokenList.get(tokenListPointer);
        // MOV [AL],BL
        // MOV [20],AL
        tempSB.append(token);
        
        ++tokenListPointer;
        token = tokenList.get(tokenListPointer);

        if (token.equals(NL)) {
          // MOV [AL],BL \n
          // MOV [20],AL \n
          linePosition = tempSB.length();
          setOpcodeParam12();
          tempSB.append(spacing(CODE_TAB) + toAddress() + toOpcodeParam12());
          ramLocator += 3;
          
          if (Debug.CBL_ON) {
            tempSB.append("{Mnem [reg],reg \\n}");
          }
          
          tempSB.append("\n");
          sourceCodeLineCounter++;
          codeListAppend(tempSB, 1);
          return true;
        }
        if (token.startsWith(";")) {
          // MOV [AL],BL ;xy \n
          // MOV [20],AL ;xy \n
          linePosition = tempSB.length();
          setOpcodeParam12();
          tempSB.append(spacing(CODE_TAB) + toAddress() + toOpcodeParam12());
          ramLocator += 3;
          linePosition = tempSB.length();
          tempSB.append(spacing(COMMENT_TAB) + token);
          
          if (Debug.CBL_ON) {
            tempSB.append("{Mnem [reg],reg ;xy \\n}");
          }
          
          tempSB.append("\n");
          sourceCodeLineCounter++;
          codeListAppend(tempSB, 2);
          return true;
        }   
      }
      return true;
    }
    return false;
  }
  
  
  
  /**
   * Append temporary string builder tempSB (for a line) to the code list
   * string builder. Take care of the tokenlistPointer management.
   * 
   * @param str
   * @param increment
   */
  private void codeListAppend(StringBuilder stringBuilder, int increment) {
    codeListSB.append(stringBuilder);
    tokenListPointer += increment;
  }
  
  
  /**
   * Return string representation of the code list address.
   * 
   * @return the string of the address
   */
  private String toAddress() {
    return "; [" + Common.toHexByteString(ramLocator) + "]  ";
  }
  
  
  /**
   * Answer the string representation of the opcode.
   * 
   * @return The opcode as a string.
   */
  private String toOpcode() {
    return Common.toHexByteString(opcode);
  }
  
  
  /**
   * Answer the string representation of the opcode and the first parameter.
   * 
   * @return The opcode and the first parameter as a string.
   */
  private String toOpcodeParam() {
    return toOpcode() + " " + Common.toHexByteString(param1);
  }
  
  
  /**
   * Answer the string representation of the opcode and the first and second parameter.
   * 
   * @return The opcode and the first and second parameter as a string.
   */  private String toOpcodeParam12() {
    return toOpcodeParam() + " " + Common.toHexByteString(param2);
  }
  
  
  /**
   * Set the opcode alone.
   */
  private void setOpcode() {
    opcode = Common.getRam()[ramLocator];
    codeToLineArr[ramLocator] = sourceCodeLineCounter;  
  }
  
  
  /**
   * Set the opcode and the first parameter.
   */
  private void setOpcodeParam() {
    setOpcode();
    param1 = Common.getRam()[ramLocator + 1];
    codeToLineArr[ramLocator + 1] = sourceCodeLineCounter;  
  }
  
  
  /**
   * Set the opcode the first and the second parameter.
   */
  private void setOpcodeParam12() {
    setOpcodeParam();
    param2 = Common.getRam()[ramLocator + 2];
    codeToLineArr[ramLocator + 2] = sourceCodeLineCounter;  
  }
  
  
  /**
   * Return a hex number consisting of 2 cyphers.
   * 
   * @return a hex number
   */
  private String hexNumber(String hex) {
    return (hex.length() == 1 ? "0" : "") + hex;
  }
  
  
  /**
   * Test if a jump command.
   * 
   * @param str The string to be tested.
   * @return true, if a jump command, else false.
   */
   private boolean isJumpMnem(String str) {
     return (Common.JUMPS.contains(str.toUpperCase()));
   }

   
   private boolean isJumpAbsoluteMem(String str) {
     return (Common.ABSOLUTE_JUMPS.contains(str.toUpperCase()));
   }
   
  /**
   * Check if token is a mnemonic.
   * 
   * @return true, if it is a mnemonic, else false
   */
  private boolean isMnemonic() {
    return commandList.contains(token);
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
   * Test if token is a hex number with one or two digits.
   * 
   * @return true if hex and format ok, else answer false
   */
  private boolean isHexNumber() {
    if (token.length() > 2) {
      return false;
    }
    return (Pattern.matches("[0-9A-F]+", token.toUpperCase()));
  }
  
  
  /**
   * Return a string representation of the codelist.
   */
  public String toString() {
    if (Debug.CBL_ON) {
      String line = new String(new char[80]).replace('\0', '=') + "\n";

      codeListSB.insert(0, "\n" + line);
      codeListSB.append(line);
    }
    return codeListSB.toString();
  }
}
