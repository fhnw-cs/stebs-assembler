package assembler;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.BeforeClass;

import assembler.SyntaxChecker;


/**
 * Test assembler syntax rules.
 *    JMP symbol       --> ok
 *    
 *    JMP              --> Expected symbol
 *    JMP xy           --> Malformed symbol (use alphanumerics and '_' only), got xy
 *    JMP symbol xy    --> Expected comment, got xy
 * 
 * @author ruedi.mueller
 */
public class Group3Test {

  @BeforeClass
  public static void setUp() {
    AllTests.prepare();
  }

  @Test
  public void test_JMP_symbol() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group3/JMP_symbol.asm");
    assertEquals("", sc.getErrorMessage());
  }

  @Test
  public void test_JMP() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group3/JMP.asm");
    assertEquals(300L, sc.getErrorNum());
  }

  @Test
  public void test_JMP_hexlabel1() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group3/JMP_hexlabel1.asm");
    assertEquals(301L, sc.getErrorNum());
  }

  @Test
  public void test_JMP_hexlabel2() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group3/JMP_hexlabel2.asm");
    assertEquals(301L, sc.getErrorNum());
  }

  @Test
  public void test_JMP_invalidSymbol() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group3/JMP_invalidSymbol.asm");
    assertEquals(302L, sc.getErrorNum());
  }

  @Test
  public void test_JMP_symbol_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group3/JMP_symbol_xy.asm");
    assertEquals(303L, sc.getErrorNum());
  }

  @Test
  public void test_JMP_symbol_endOfRAM() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group3/JMP_symbol_endOfRAM.asm");
    assertEquals(12L, sc.getErrorNum());
  }

  @Test
  public void test_JMP_labelNotReachableForward() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group3/JMP_labelNotReachableForward.asm");
    assertEquals(28L, sc.getErrorNum());
  }

  @Test
  public void test_JMP_labelNotReachableBackward() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group3/JMP_labelNotReachableBackward.asm");
    assertEquals(28L, sc.getErrorNum());
  }

  @Test
  // Correct syntax for different group3 commands
  public void test_JMP_foreback() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group3/JMP_foreback.asm");
    assertEquals("", sc.getErrorMessage());
  }

  @Test
  // Correct syntax for jumps with reachable labels
  public void test_JMP_foreback_at_limits() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group3/JMP_foreback_at_limits.asm");
    assertEquals("", sc.getErrorMessage());
  }

  @Test
  // Correct syntax for different group3 commands
  public void test_group3_commands() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group3/group3_commands.asm");
    assertEquals("", sc.getErrorMessage());
  }

  @Test
  public void test_ORG_JMP_symbol() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group3/ORG_JMP_symbol.asm");
    assertEquals("", sc.getErrorMessage());
  }
  
  @Test
  public void test_missing_label() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group3/missing_label.asm");
    assertEquals(24L, sc.getErrorNum());
  }
  
  @Test
  public void test_missing_label2() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group3/missing_label2.asm");
    assertEquals(24L, sc.getErrorNum());
  }
  
  @Test
  public void test_multiple_labels_ok() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group3/multiple_labels_ok.asm");
    assertEquals("", sc.getErrorMessage());
  }
  
}
