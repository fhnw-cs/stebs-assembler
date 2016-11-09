package assemblertests;
import static org.junit.Assert.*;

import org.junit.Test;

import assembler.SyntaxChecker;


/**
 * Test assembler syntax rules:
 *    XCHG AL,BL       --> ok
 *    
 *    XCHG             --> Expected register
 *    XCHG xy          --> Expected register, got xy
 *    XCHG AL          --> Expected comma (,)
 *    XCHG AL xy       --> Expected comma (,), got xy
 *    XCHG AL,         --> Expected XL/SP
 *    XCHG AL,xy       --> Expected XL/SP, got xy
 *    XCHG AL,BL xy    --> Expected comment, got xy
 *    XCHG al          --> Expected register, got al
 *    XCHG AL,bl       --> Expected XL/SP, got bl
 * 
 * @author ruedi.mueller
 */
public class Group13Test {
  @Test
  public void test_all_correct_CMPs() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group13/all_correct_XCHGs.asm");
    assertEquals("", sc.getErrorMessage());
  }
  
  @Test
  public void test_XCHG() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group13/XCHG.asm");
    assertEquals(1300L, sc.getErrorNum());
  }
  
  @Test
  public void test_XCHG_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group13/XCHG_xy.asm");
    assertEquals(1301L, sc.getErrorNum());
  }
  
  @Test
  public void test_XCHG_AL() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group13/XCHG_AL.asm");
    assertEquals(1302L, sc.getErrorNum());
  }
  
  @Test
  public void test_XCHG_AL_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group13/XCHG_AL_xy.asm");
    assertEquals(1303L, sc.getErrorNum());
  }
  
  @Test
  public void test_XCHG_AL_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group13/XCHG_AL_comma.asm");
    assertEquals(1304L, sc.getErrorNum());
  }
  
  @Test
  public void test_XCHG_AL_comma_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group13/XCHG_AL_comma_xy.asm");
    assertEquals(1305L, sc.getErrorNum());
  }
  
  @Test
  public void test_XCHG_AL_comma_BL_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group13/XCHG_AL_comma_BL_xy.asm");
    assertEquals(1307L, sc.getErrorNum());
  }

  @Test
  public void test_XCHG_al() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group13/XCHG_low_al.asm");
    assertEquals(1301L, sc.getErrorNum());
  }

  @Test
  public void test_XCHG_AL_comma_bl() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group13/XCHG_AL_comma_low_bl.asm");
    assertEquals(1305L, sc.getErrorNum());
  }
}

