package assemblertests;
import static org.junit.Assert.*;

import org.junit.Test;

import assembler.SyntaxChecker;


/**
 * Test assembler syntax rules:
 *    DEC AL           --> ok
 *    DEC [AL]         --> ok
 *    
 *    DEC              --> Expected XL/SP or [
 *    DEC xy           --> Expected XL/SP or [, got xy
 *    DEC AL xy        --> Expected comment, got xy
 *    DEC [            --> Expected hexadecimal number (range 00..FF)
 *    DEC [xy          --> Expected hexadecimal number (range 00..FF), got xy
 *    DEC [20          --> Expected ]
 *    DEC [20 xy       --> Expected ], got xy
 *    DEC [20] xy      --> Expected comment, got xy
 *    DEC al           --> Expected XL/SP or [, got al
 *    DEC [al          --> Expected hexadecimal number (range 00..FF), got al
 * 
 * @author ruedi.mueller
 */
public class Group9Test {
  @Test
  public void test_all_correct_DECs() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group9/all_correct_DECs.asm");
    assertEquals("", sc.getErrorMessage());
  }
  
  @Test
  public void test_DEC() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group9/DEC.asm");
    assertEquals(900L, sc.getErrorNum());
  }
  
  @Test
  public void test_DEC_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group9/DEC_xy.asm");
    assertEquals(901L, sc.getErrorNum());
  }
  
  @Test
  public void test_DEC_AL_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group9/DEC_AL_xy.asm");
    assertEquals(906L, sc.getErrorNum());
  }

  @Test
  public void test_DEC_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group9/DEC_left.asm");
    assertEquals(920L, sc.getErrorNum());
  }
  
  @Test
  public void test_DEC_left_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group9/DEC_left_xy.asm");
    assertEquals(921L, sc.getErrorNum());
  }
  
  @Test
  public void test_DEC_left_hex() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group9/DEC_left_hex.asm");
    assertEquals(929L, sc.getErrorNum());
  }
  
  @Test
  public void test_DEC_left_hex_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group9/DEC_left_hex_xy.asm");
    assertEquals(930L, sc.getErrorNum());
  }
  
  @Test
  public void test_DEC_left_hex_right_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group9/DEC_left_hex_right_xy.asm");
    assertEquals(935L, sc.getErrorNum());
  }
  
  @Test
  public void test_DEC_al() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group9/DEC_low_al.asm");
    assertEquals(901L, sc.getErrorNum());
  }
  
  @Test
  public void test_DEC_left_al() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group9/DEC_left_low_al.asm");
    assertEquals(921L, sc.getErrorNum());
  }
}
