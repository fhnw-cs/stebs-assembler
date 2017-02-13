package assembler;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.BeforeClass;

import assembler.SyntaxChecker;


/**
 * Test assembler syntax rules:
 *    NOP              --> ok
 *    
 *    NOP xy           --> Expected comment, got xy
 *    NOP ,            --> Expected comment, got ,
 *    NOP [            --> Expected comment, got [
 * 
 * @author ruedi.mueller
 */
public class Group0Test {

  @BeforeClass
  public static void setUp() {
    AllTests.prepare();
  }

  @Test
  public void test_all_correct_NOPs() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group0/all_correct_NOPs.asm");
    assertEquals("", sc.getErrorMessage());
  }

  @Test
  public void test_NOP_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group0/NOP_xy.asm");
    assertEquals(909L, sc.getErrorNum());
  }

  @Test
  public void test_NOP_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group0/NOP_comma.asm");
    assertEquals(909L, sc.getErrorNum());
  }

  @Test
  public void test_NOP_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group0/NOP_left.asm");
    assertEquals(909L, sc.getErrorNum());
  }
}
