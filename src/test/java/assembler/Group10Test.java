package assembler;

import static org.junit.Assert.*;

import org.junit.Test;

import assembler.SyntaxChecker;


/**
 * Test assembler syntax rules:
 *    SWAP [20]        --> ok
 *    
 *    SWAP             --> Expected [
 *    SWAP xy          --> Expected [, got xy
 *    SWAP [           --> Expected hexadecimal number (range 00..FF)
 *    SWAP [xy         --> Expected hexadecimal number (range 00..FF), got xy
 *    SWAP [20         --> Expected ]
 *    SWAP [20 xy      --> Expected ], got xy
 *    SWAP [20] xy     --> Expected comment, got xy
 * 
 * @author ruedi.mueller
 */
public class Group10Test {
  @Test
  public void test_all_correct_SWAPs() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group10/all_correct_SWAPs.asm");
    assertEquals("", sc.getErrorMessage());
  }
  
  @Test
  public void test_SWAP() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group10/SWAP.asm");
    assertEquals(1000L, sc.getErrorNum());
  }
  
  @Test
  public void test_SWAP_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group10/SWAP_xy.asm");
    assertEquals(1001L, sc.getErrorNum());
  }
  
  @Test
  public void test_SWAP_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group10/SWAP_left.asm");
    assertEquals(1020L, sc.getErrorNum());
  }
  
  @Test
  public void test_SWAP_left_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group10/SWAP_left_xy.asm");
    assertEquals(1021L, sc.getErrorNum());
  }
  
  @Test
  public void test_SWAP_left_hex() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group10/SWAP_left_hex.asm");
    assertEquals(1029L, sc.getErrorNum());
  }
  
  @Test
  public void test_SWAP_left_hex_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group10/SWAP_left_hex_xy.asm");
    assertEquals(1030L, sc.getErrorNum());
  }
  
  @Test
  public void test_SWAP_left_hex_right_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group10/SWAP_left_hex_right_xy.asm");
    assertEquals(1035L, sc.getErrorNum());
  }
  
}
