package assembler;

import static org.junit.Assert.*;

import org.junit.Test;

import assembler.SyntaxChecker;


/**
 * Test assembler syntax rules:
 *    INC AL           --> ok
 *    INC [AL]         --> ok
 *    
 *    INC              --> Expected XL/SP or [
 *    INC xy           --> Expected XL/SP or [, got xy
 *    INC AL xy        --> Expected comment, got xy
 *    INC [            --> Expected XL/SP
 *    INC [xy          --> Expected XL/SP, got xy
 *    INC [AL          --> Expected ]
 *    INC [AL xy       --> Expected ], got xy
 *    INC [AL] xy      --> Expected comment, got xy
 *    INC al           --> Expected XL/SP or [, got al
 *    INC [xy          --> Expected XL/SP, got al
 * 
 * @author ruedi.mueller
 */
public class Group7Test {
  @Test
  public void test_all_correct_INCs() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group7/all_correct_INCs.asm");
    assertEquals("", sc.getErrorMessage());
  }
  
  @Test
  public void test_INC() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group7/INC.asm");
    assertEquals(700L, sc.getErrorNum());
  }
  
  @Test
  public void test_INC_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group7/INC_xy.asm");
    assertEquals(701L, sc.getErrorNum());
  }
  
  @Test
  public void test_INC_AL_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group7/INC_AL_xy.asm");
    assertEquals(706L, sc.getErrorNum());
  }
  
  @Test
  public void test_INC_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group7/INC_left.asm");
    assertEquals(720L, sc.getErrorNum());
  }
  
  @Test
  public void test_INC_left_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group7/INC_left_xy.asm");
    assertEquals(721L, sc.getErrorNum());
  }
  
  @Test
  public void test_INC_left_AL() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group7/INC_left_AL.asm");
    assertEquals(729L, sc.getErrorNum());
  }
  
  @Test
  public void test_INC_left_AL_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group7/INC_left_AL_xy.asm");
    assertEquals(730L, sc.getErrorNum());
  }
  
  @Test
  public void test_INC_left_AL_right_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group7/INC_left_AL_right_xy.asm");
    assertEquals(735L, sc.getErrorNum());
  }
  
  @Test
  public void test_INC_al() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group7/INC_low_al.asm");
    assertEquals(701L, sc.getErrorNum());
  }
  
  @Test
  public void test_INC_left_al() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group7/INC_left_low_al.asm");
    assertEquals(721L, sc.getErrorNum());
  }
}
