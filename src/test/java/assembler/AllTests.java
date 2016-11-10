package assembler;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import assembler.Assembler;
import assembler.GroupListBuilder;
import assembler.SyntaxChecker;
import assembler.SyntaxCheckerException;
import assembler.Tokenizer;
import assembler.support.AsmFileReader;
import assembler.support.Common;
import assembler.support.InstructionFileReader;


/**
 * The class AllTests collects the above stated test classes in a test suite
 * to easily test all cases.
 * 
 * @author ruedi.mueller
 */
public class AllTests {
  // The syntax checker to be tested
  private static SyntaxChecker sc;
  // The string to be read from INSTRUCTION.data
  private static String instructionDataString;
  
  
  @BeforeClass
  public static void setUpOnce() {
    // Build string from file INSTRUCTION.data
    instructionDataString = new InstructionFileReader().execute(Common.INSTRUCTION_FILENAME);
  }
  
  
  /**
   * Assemble the specified file and invoke the syntax checker.
   * This allows for the testing of success or failure messages.
   * 
   * @param filename The name of the file to be assembled
   * @return The checker with its error message and error number
   */
  public static SyntaxChecker assemble(String filename) {
    // Create assembler to store filename
    new Assembler(filename);

    // Assemble code from *.asm file with support from INSTRUCTION.data
    String asmString = new AsmFileReader().execute(Common.getFilename());
    
    // Create token list with tokens from asmString
    Tokenizer t = new Tokenizer();
    t.tokenize(asmString);
    
    // Build mnemonic list, opcode list and mnemonic set
    GroupListBuilder groupListBuilder = new GroupListBuilder(instructionDataString);
    
    // Check tokens syntactically etc.
    sc = new SyntaxChecker(groupListBuilder);
    try {
      sc.check();
    }
    catch (SyntaxCheckerException ce) {
      System.out.println(ce.getMessage());
    }
    return sc;
  }
}
