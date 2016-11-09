package assemblertests;
import static org.junit.Assert.*;

import org.junit.Test;

import assembler.SyntaxChecker;


/**
 * Test assembler syntax rules:
 *    ORG 20           --> ok
 *    
 *    ORG              --> Expected hexadecimal number
 *    ORG xy           --> Expected hexadecimal number, got xy
 *    ORG 20 xy        --> Expected comment, got xy
 * 
 * @author ruedi.mueller
 */
public class OrgTest {
  @Test
  public void test_all_correct_ORGs() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/org/all_correct_ORGs.asm");
    assertEquals("", sc.getErrorMessage());
  }
  
  @Test
  public void test_ORG() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/org/ORG.asm");
    assertEquals(9000L, sc.getErrorNum());
  }
 
  @Test
  public void test_ORG_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/org/ORG_xy.asm");
    assertEquals(9001L, sc.getErrorNum());
  }
  
  @Test
  public void test_ORG_hex_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/org/ORG_hex_xy.asm");
    assertEquals(9002L, sc.getErrorNum());
  }
}
