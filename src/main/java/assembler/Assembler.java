package assembler;

import assembler.support.Common;
import assembler.support.Debug;
import assembler.support.AsmFileReader;
import assembler.support.InstructionFileReader;


/**
 * Assembler for stebs-like assembler commands and instructions.
 * Input is to be provided by a *.asm file, results are returned 
 * by executing Common.getRam() for the machine code image and
 * Common.getCodeList() for a formatted list with source code and
 * associated machine code as comments.
 * 
 * The assembler parses the file and creates an image for the stebs
 * simulator RAM, totally 256 bytes.
 * 
 * @author ruedi.mueller
 */
public class Assembler {
  // The code list builder of this assembler
  private CodeListBuilder cl;

  
  /**
   * Assemble assembly source code in the file passed in args[0]. Build
   * the machine code in the RAM and deliver a code list.
   * 
   * NOTICE:
   * In production set Debug.ON = false to suppress debug information
   *
   * @param args The filename of the file to assemble
   */
  public static void main(String[] args) {
    System.out.print(Common.title + " " + Common.version);
    System.out.println(Common.copyright);

	  // Create assembler to store filename (args[0])
    Assembler asm = new Assembler(args[0]);
    
    // Assemble code from *.asm file with support from INSTRUCTION.data
    String asmString = new AsmFileReader().execute(Common.getFilename());
    String instructionDataString = new InstructionFileReader().execute(Common.INSTRUCTION_FILENAME);
    
    if (asm.execute(asmString, instructionDataString)) {
      // How to access the machine code if needed
      // int[] ram = Common.getRam();
      
      // How to access the token list
      // Common.getTokenList();

      // How to display the code list
      System.out.println(Common.getCodeList());
      
      // How to display the array showing addresses with machine code associated with source
      // code line numbers, used for C# stebs.
      // System.out.println(java.util.Arrays.toString(asm.getCodeToLineArr()));

      // How to display the RAM
      // System.out.println(Common.getMemory().toString());
    }
    else {
      System.err.println(Common.ERROR_MESSAGE);
    }
  }
    
  
  /**
   * Answer the array with source code line numbers. The index designates
   * the address of a machine code byte, the value designates the line
   * number where the command in the source code file sits.
   * 
   * This array is necessary to support highlighting the source code lines
   * while executing a program in C# stebs.
   * 
   * @return the array with line numbers if a command (part), else -1
   *         (for ORG, DB and unused bytes)
   */
  public int[] getCodeToLineArr() {
    return cl.getCodeToLineArr();
  }


  /**
   * Invoke the assembler. Answer true if successful else false.
   *     
   * @param asmString String to be tokenized
   * @param instructionDataString String from INSTRUCTION.data
   * @return true if success, else false
   */
  public boolean execute(String asmString, String instructionDataString) {
    // Create token list with tokens from asmString
    Tokenizer t = new Tokenizer();
    t.tokenize(asmString);
    // For debugging purposes
    if (Debug.T_ON) {
      System.out.print(t);
    }
    
    // Build mnemonic list, opcode list and mnemonic set
    GroupListBuilder groupListBuilder = new GroupListBuilder(instructionDataString);
    
    // Check tokens syntactically etc.
    SyntaxChecker sc = new SyntaxChecker(groupListBuilder);
    try {
      sc.check();
    }
    catch (SyntaxCheckerException sce) {
      System.out.println(sce.getMessage());
      // Terminate execution indicating an error
      return false;
    }
    
    // Build a formatted code list
    cl = new CodeListBuilder(groupListBuilder.mnemonicSet);
    try {
      cl.buildCodeList();
    }
    catch (CodeListBuilderException clbe) {
      System.out.println("uuuuuuuuuu" + clbe.getMessage());
      // Terminate execution indicating an error
      return false;
    }
    // Terminate execution indicating success
    return true;
  }
  

  /**
   * Instantiate an assembler and save filename in Common.
   * 
   * @param filename to be saved
   */
  public Assembler(String filename) {
    Common.setFilename(filename);
  }
}
