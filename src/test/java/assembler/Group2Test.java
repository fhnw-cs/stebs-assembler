package assembler;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.BeforeClass;

import assembler.SyntaxChecker;


/**
 * Test assembler syntax rules.
 *    ROL AL           --> ok
 *    
 *    ROL              --> Expected register
 *    ROL xy           --> Expected register, got xy
 *    ROL ,            --> Expected register, got ,
 *    ROL [            --> Expected register, got [
 *    ROL AL xy        --> Expected comment, got xy
 *    ROL AL ,         --> Expected comment, got ,
 *    ROL AL [         --> Expected comment, got [
 *    ROL al           --> Expected register, got al
 * 
 * @author ruedi.mueller
 */
public class Group2Test {

  @BeforeClass
  public static void setUp() {
    AllTests.prepare();
  }

  @Test
  public void test_all_correct_ROLs() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group2/all_correct_ROLs.asm");
    assertEquals("", sc.getErrorMessage());
  }
  
  @Test
  public void test_ROL() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group2/ROL.asm");
    assertEquals(200L, sc.getErrorNum());
  }
 
  @Test
  public void test_ROL_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group2/ROL_xy.asm");
    assertEquals(201L, sc.getErrorNum());
  }
  
  @Test
  public void test_ROL_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group2/ROL_comma.asm");
    assertEquals(201L, sc.getErrorNum());
  }
  
  @Test
  public void test_ROL_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group2/ROL_left.asm");
    assertEquals(201L, sc.getErrorNum());
  }
  
  @Test
  public void test_ROL_AL_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group2/ROL_AL_xy.asm");
    assertEquals(202L, sc.getErrorNum());
  }
  
  @Test
  public void test_ROL_AL_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group2/ROL_AL_comma.asm");
    assertEquals(202L, sc.getErrorNum());
  }
  
  @Test
  public void test_ROL_AL_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group2/ROL_AL_left.asm");
    assertEquals(202L, sc.getErrorNum());
  }
  
  @Test
  public void test_ROL_al() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group2/ROL_low_al.asm");
    assertEquals(201L, sc.getErrorNum());
  }
}
