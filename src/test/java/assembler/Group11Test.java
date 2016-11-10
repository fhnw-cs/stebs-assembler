package assembler;

import static org.junit.Assert.*;

import org.junit.Test;

import assembler.SyntaxChecker;


/**
 * Test assembler syntax rules:
 *    CLINC [AL]       --> ok
 *    
 *    CLINC            --> Expected [
 *    CLINC xy         --> Expected [, got xy
 *    CLINC [          --> Expected XL/SP
 *    CLINC [xy        --> Expected XL/SP, got xy
 *    CLINC [AL        --> Expected ]
 *    CLINC [AL xy     --> Expected ], got xy
 *    CLINC [AL] xy    --> Expected comment, got xy
 *    CLINC [al        --> Expected XL/SP, got al
 * 
 * @author ruedi.mueller
 */
public class Group11Test {
  @Test
  public void test_all_correct_CLINCs() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group11/all_correct_CLINCs.asm");
    assertEquals("", sc.getErrorMessage());
  }
  
  @Test
  public void test_CLINC() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group11/CLINC.asm");
    assertEquals(1100L, sc.getErrorNum());
  }
  
  @Test
  public void test_CLINC_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group11/CLINC_xy.asm");
    assertEquals(1101L, sc.getErrorNum());
  }
  
  @Test
  public void test_CLINC_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group11/CLINC_left.asm");
    assertEquals(1120L, sc.getErrorNum());
  }
  
  @Test
  public void test_CLINC_left_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group11/CLINC_left_xy.asm");
    assertEquals(1121L, sc.getErrorNum());
  }
  
  @Test
  public void test_CLINC_left_AL() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group11/CLINC_left_AL.asm");
    assertEquals(1129L, sc.getErrorNum());
  }
  
  @Test
  public void test_CLINC_left_AL_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group11/CLINC_left_AL_xy.asm");
    assertEquals(1130L, sc.getErrorNum());
  }
  
  @Test
  public void test_CLINC_left_AL_right_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group11/CLINC_left_AL_right_xy.asm");
    assertEquals(1135L, sc.getErrorNum());
  }
  
  @Test
  public void test_CLINC_left_al() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group11/CLINC_left_low_al.asm");
    assertEquals(1121L, sc.getErrorNum());
  }
}
