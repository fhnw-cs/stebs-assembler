package assemblertests;
import static org.junit.Assert.*;

import org.junit.Test;

import assembler.SyntaxChecker;


/**
 * Test assembler syntax rules:
 *    CPYD [AL],20     --> ok
 *    
 *    CPYD             --> Expected [
 *    CPYD xy          --> Expected [, got xy
 *    CPYD [           --> Expected XL/SP
 *    CPYD [xy         --> Expected XL/SP, got xy
 *    CPYD [AL         --> Expected ]
 *    CPYD [AL xy      --> Expected ], got xy
 *    CPYD [AL] xy     --> Expected comma (,), got xy
 *    CPYD [AL],       --> Expected hexadecimal number (range 00..FF) or XL/SP
 *    CPYD [AL],xy     --> Expected hexadecimal number (range 00..FF) or XL/SP, got xy
 *    CPYD [AL],20 xy  --> Expected comment, got xy
 *    CPYD [al         --> Expected XL/SP, got al
 * 
 * @author ruedi.mueller
 */
public class Group12Test {
  @Test
  public void test_all_correct_CPYDs() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group12/all_correct_CPYDs.asm");
    assertEquals("", sc.getErrorMessage());
  }
  
  @Test
  public void test_CPYD() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group12/CPYD.asm");
    assertEquals(1200L, sc.getErrorNum());
  }
  
  @Test
  public void test_CPYD_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group12/CPYD_xy.asm");
    assertEquals(1201L, sc.getErrorNum());
  }
  
  @Test
  public void test_CPYD_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group12/CPYD_left.asm");
    assertEquals(1220L, sc.getErrorNum());
  }
  
  @Test
  public void test_CPYD_left_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group12/CPYD_left_xy.asm");
    assertEquals(1221L, sc.getErrorNum());
  }
  
  @Test
  public void test_CPYD_left_AL() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group12/CPYD_left_AL.asm");
    assertEquals(1229L, sc.getErrorNum());
  }
  
  @Test
  public void test_CPYD_left_AL_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group12/CPYD_left_AL_xy.asm");
    assertEquals(1230L, sc.getErrorNum());
  }
  
  @Test
  public void test_CPYD_left_AL_right() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group12/CPYD_left_AL_right.asm");
    assertEquals(1202L, sc.getErrorNum());
  }

  @Test
  public void test_CPYD_left_AL_right_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group12/CPYD_left_AL_right_xy.asm");
    assertEquals(1203L, sc.getErrorNum());
  }

  @Test
  public void test_CPYD_left_AL_right_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group12/CPYD_left_AL_right_comma.asm");
    assertEquals(1204L, sc.getErrorNum());
  }

  @Test
  public void test_CPYD_left_AL_right_comma_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group12/CPYD_left_AL_right_comma_xy.asm");
    assertEquals(1205L, sc.getErrorNum());
  }

  @Test
  public void test_CPYD_left_AL_right_comma_hex_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group12/CPYD_left_AL_right_comma_hex_xy.asm");
    assertEquals(1206L, sc.getErrorNum());
  }

  @Test
  public void test_CPYD_left_al() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group12/CPYD_left_low_al.asm");
    assertEquals(1221L, sc.getErrorNum());
  }
}

