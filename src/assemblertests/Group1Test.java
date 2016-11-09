package assemblertests;
import static org.junit.Assert.*;
import org.junit.Test;

import assembler.SyntaxChecker;


/**
 * Test assembler syntax rules.
 *    CALL 20          --> ok
 *    
 *    CALL             --> Expected hexadecimal number
 *    CALL xy          --> Expected hexadecimal number, got xy
 *    CALL ,           --> Expected hexadecimal number, got ,
 *    CALL [           --> Expected hexadecimal number, got [
 *    CALL 20 xy       --> Expected comment, got xy
 *    CALL 20 ,        --> Expected comment, got ,
 *    CALL 20 [        --> Expected comment, got [
 * 
 * @author ruedi.mueller
 */
public class Group1Test {
  @Test
  public void test_all_correct_CALLs() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group1/all_correct_CALLs.asm");
    assertEquals("", sc.getErrorMessage());
  }
  
  @Test
  public void test_CALL() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group1/CALL.asm");
    assertEquals(100L, sc.getErrorNum());
  }
 
  @Test
  public void test_CALL_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group1/CALL_xy.asm");
    assertEquals(101L, sc.getErrorNum());
  }
  
  @Test
  public void test_CALL_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group1/CALL_comma.asm");
    assertEquals(101L, sc.getErrorNum());
  }
  
  @Test
  public void test_CALL_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group1/CALL_left.asm");
    assertEquals(101L, sc.getErrorNum());
  }
  
  @Test
  public void test_CALL_hex_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group1/CALL_hex_xy.asm");
    assertEquals(102L, sc.getErrorNum());
  }
  
  @Test
  public void test_CALL_hex_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group1/CALL_hex_comma.asm");
    assertEquals(102L, sc.getErrorNum());
  }
  
  @Test
  public void test_CALL_hex_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group1/CALL_hex_left.asm");
    assertEquals(102L, sc.getErrorNum());
  }
}
