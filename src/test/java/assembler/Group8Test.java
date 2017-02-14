package assembler;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.BeforeClass;

import assembler.SyntaxChecker;


/**
 * Test assembler syntax rules.
 *    JPA symbol       --> ok
 *    
 *    JPA              --> Expected symbol
 *    JPA xy           --> Malformed symbol (use alphanumerics and '_' only), got xy
 *    JPA symbol xy    --> Expected comment, got xy
 * 
 * @author ruedi.mueller
 */
public class Group8Test {

  @BeforeClass
  public static void setUp() {
    AllTests.prepare();
  }

  @Test
  public void test_JPA_symbol() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group8/JPA_symbol.asm");
    assertEquals("", sc.getErrorMessage());
  }

  @Test
  public void test_JPA_labelReachableForward() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group8/JPA_labelReachableForward.asm");
    assertEquals(28L, sc.getErrorNum());
  }

  @Test
  public void test_JPA_labelReachableBackward() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group8/JPA_labelReachableBackward.asm");
    assertEquals("", sc.getErrorMessage());
  }
  

  @Test
  // Correct syntax for different group8 commands
  public void test_JPA_foreback() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group8/JPA_foreback.asm");
    assertEquals("", sc.getErrorMessage());
  }

  @Test
  // Correct syntax for jumps with reachable labels
  public void test_JPA_foreback_at_limits() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group8/JPA_foreback_at_limits.asm");
    assertEquals("", sc.getErrorMessage());
  }

  @Test
  // Correct syntax for different group8 commands
  public void test_group8_commands() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group8/group8_commands.asm");
    assertEquals("", sc.getErrorMessage());
  }
    
  @Test
  public void test_JPA() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group8/JPA.asm");
    assertEquals(800L, sc.getErrorNum());
  }

  @Test
  public void test_JPA_hexlabel1() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group8/JPA_hexlabel1.asm");
    assertEquals(301L, sc.getErrorNum());
  }

  @Test
  public void test_JPA_hexlabel2() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group8/JPA_hexlabel2.asm");
    assertEquals(301L, sc.getErrorNum());
  }

  @Test
  public void test_JPA_invalidSymbol() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group8/JPA_invalidSymbol.asm");
    assertEquals(302L, sc.getErrorNum());
  }

  @Test
  public void test_JPA_symbol_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group8/JPA_symbol_xy.asm");
    assertEquals(803L, sc.getErrorNum());
  }

  @Test
  public void test_JPA_symbol_endOfRAM() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group8/JPA_symbol_endOfRAM.asm");
    assertEquals(12L, sc.getErrorNum());
  }

  @Test
  public void test_ORG_JPA_symbol() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group8/ORG_JPA_symbol.asm");
    assertEquals("", sc.getErrorMessage());
  }
  
  @Test
  public void test_missing_label() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group8/missing_label.asm");
    assertEquals(24L, sc.getErrorNum());
  }
  
  @Test
  public void test_missing_label2() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group8/missing_label2.asm");
    assertEquals(24L, sc.getErrorNum());
  }
  
  @Test
  public void test_multiple_labels_ok() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group8/multiple_labels_ok.asm");
    assertEquals("", sc.getErrorMessage());
  }
  
}
