package assembler;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.BeforeClass;

import assembler.SyntaxChecker;


/**
 * Test assembler syntax rules:
 *    CMP AL,20        --> ok
 *    CMP AL,BL        --> ok
 *    CMP AL,[20]      --> ok
 *    
 *    CMP              --> Expected register
 *    CMP xy           --> Expected register, got xy
 *    CMP AL           --> Expected comma (,)
 *    CMP AL xy        --> Expected comma (,), got xy
 *    CMP AL,          --> Expected hexadecimal number (range 00..FF), XL/SP or [
 *    CMP AL,xy        --> Expected hexadecimal number (range 00..FF), XL/SP or [, got xy
 *    CMP AL,20 xy     --> Expected comment, got xy
 *    CMP AL,BL xy     --> Expected comment, got xy
 *    CMP AL,[         --> Expected hexadecimal number (range 00..FF)
 *    CMP AL,[xy       --> Expected hexadecimal number (range 00..FF), got xy
 *    CMP AL,[20       --> Expected ]
 *    CMP AL,[20 xy    --> Expected ], got xy
 *    CMP AL,[20] xy   --> Expected comment, got xy
 *    CMP al           --> Expected register, got al
 *    CMP AL,bl        --> Expected hexadecimal number (range 00..FF), register or '[', got bl
 * 
 * @author ruedi.mueller
 */
public class Group4Test {

  @BeforeClass
  public static void setUp() {
    AllTests.prepare();
  }

  @Test
  public void test_all_correct_CMPs() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group4/all_correct_CMPs.asm");
    assertEquals("", sc.getErrorMessage());
  }
  
  @Test
  public void test_CMP() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group4/CMP.asm");
    assertEquals(400L, sc.getErrorNum());
  }
  
  @Test
  public void test_CMP_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group4/CMP_xy.asm");
    assertEquals(401L, sc.getErrorNum());
  }
  
  @Test
  public void test_CMP_AL() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group4/CMP_AL.asm");
    assertEquals(402L, sc.getErrorNum());
  }
  
  @Test
  public void test_CMP_AL_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group4/CMP_AL_xy.asm");
    assertEquals(403L, sc.getErrorNum());
  }
  
  @Test
  public void test_CMP_AL_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group4/CMP_AL_comma.asm");
    assertEquals(404L, sc.getErrorNum());
  }
  
  @Test
  public void test_CMP_AL_comma_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group4/CMP_AL_comma_xy.asm");
    assertEquals(405L, sc.getErrorNum());
  }
  
  @Test
  public void test_CMP_AL_comma_hex_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group4/CMP_AL_comma_hex_xy.asm");
    assertEquals(406L, sc.getErrorNum());
  }
  
  @Test
  public void test_CMP_AL_comma_BL_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group4/CMP_AL_comma_BL_xy.asm");
    assertEquals(407L, sc.getErrorNum());
  }
  
  @Test
  public void test_CMP_AL_comma_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group4/CMP_AL_comma_left.asm");
    assertEquals(408L, sc.getErrorNum());
  }
  
  @Test
  public void test_CMP_AL_comma_left_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group4/CMP_AL_comma_left_xy.asm");
    assertEquals(409L, sc.getErrorNum());
  }
  
  @Test
  public void test_CMP_AL_comma_left_hex() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group4/CMP_AL_comma_left_hex.asm");
    assertEquals(410L, sc.getErrorNum());
  }
  
  @Test
  public void test_CMP_AL_comma_left_hex_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group4/CMP_AL_comma_left_hex_xy.asm");
    assertEquals(411L, sc.getErrorNum());
  }
  
  @Test
  public void test_CMP_AL_comma_left_hex_right_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group4/CMP_AL_comma_left_hex_right_xy.asm");
    assertEquals(412L, sc.getErrorNum());
  }
  
  @Test
  public void test_CMP_al() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group4/CMP_low_al.asm");
    assertEquals(401L, sc.getErrorNum());
  }
  
  @Test
  public void test_CMP_AL_comma_bl() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group4/CMP_AL_comma_low_bl.asm");
    assertEquals(405L, sc.getErrorNum());
  }
}
