package assembler;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.BeforeClass;

import assembler.SyntaxChecker;


/**
 * Test assembler syntax rules:
 *    ADD AL,20        --> ok
 *    ADD AL,BL        --> ok
 *    
 *    ADD              --> Expected register
 *    ADD xy           --> Expected register, got xy
 *    ADD ,            --> Expected register, got ,
 *    ADD [            --> Expected register, got [
 *    ADD AL           --> Expected comma (,)
 *    ADD AL xy        --> Expected comma (,), got xy
 *    ADD AL [         --> Expected comma (,), got [
 *    ADD AL,          --> Expected hexadecimal number (range 00..FF) or XL/SP
 *    ADD AL,xy        --> Expected hexadecimal number (range 00..FF) or XL/SP, got xy
 *    ADD AL,,         --> Expected hexadecimal number (range 00..FF) or XL/SP, got ,
 *    ADD AL,[         --> Expected hexadecimal number (range 00..FF) or XL/SP, got [
 *    ADD AL,20 xy     --> Expected comment, got xy
 *    ADD AL,20 ,      --> Expected comment, got ,
 *    ADD AL,20 [      --> Expected comment, got [
 *    ADD AL,BL xy     --> Expected comment, got xy
 *    ADD AL,BL ,      --> Expected comment, got ,
 *    ADD AL,BL [      --> Expected comment, got [
 *    ADD al           --> Expected register, got al
 *    ADD AL,bl        --> Expected hexadecimal number (range 00..FF) or XL/SP, got bl
 * 
 * @author ruedi.mueller
 */
public class Group6Test {

  @BeforeClass
  public static void setUp() {
    AllTests.prepare();
  }

  @Test
  public void test_all_correct_ADDs() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group6/all_correct_ADDs.asm");
    assertEquals("", sc.getErrorMessage());
  }
  
  @Test
  public void test_ADD() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group6/ADD.asm");
    assertEquals(600L, sc.getErrorNum());
  }
  
  @Test
  public void test_ADD_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group6/ADD_xy.asm");
    assertEquals(601L, sc.getErrorNum());
  }
  
  @Test
  public void test_ADD_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group6/ADD_comma.asm");
    assertEquals(601L, sc.getErrorNum());
  }
  
  @Test
  public void test_ADD_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group6/ADD_left.asm");
    assertEquals(601L, sc.getErrorNum());
  }
  
  @Test
  public void test_ADD_AL() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group6/ADD_AL.asm");
    assertEquals(602L, sc.getErrorNum());
  }
  
  @Test
  public void test_ADD_AL_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group6/ADD_AL_xy.asm");
    assertEquals(603L, sc.getErrorNum());
  }
  
  @Test
  public void test_ADD_AL_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group6/ADD_AL_left.asm");
    assertEquals(603L, sc.getErrorNum());
  }
  
  @Test
  public void test_ADD_AL_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group6/ADD_AL_comma.asm");
    assertEquals(604L, sc.getErrorNum());
  }
  
  @Test
  public void test_ADD_AL_comma_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group6/ADD_AL_comma_xy.asm");
    assertEquals(605L, sc.getErrorNum());
  }
  
  @Test
  public void test_ADD_AL_comma_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group6/ADD_AL_comma_comma.asm");
    assertEquals(605L, sc.getErrorNum());
  }
  
  @Test
  public void test_ADD_AL_comma_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group6/ADD_AL_comma_left.asm");
    assertEquals(605L, sc.getErrorNum());
  }
  
  @Test
  public void test_ADD_AL_comma_hex_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group6/ADD_AL_comma_hex_xy.asm");
    assertEquals(606L, sc.getErrorNum());
  }
  
  @Test
  public void test_ADD_AL_comma_hex_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group6/ADD_AL_comma_hex_comma.asm");
    assertEquals(606L, sc.getErrorNum());
  }
  
  @Test
  public void test_ADD_AL_comma_hex_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group6/ADD_AL_comma_hex_left.asm");
    assertEquals(606L, sc.getErrorNum());
  }

  @Test
  public void test_ADD_AL_comma_BL_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group6/ADD_AL_comma_BL_xy.asm");
    assertEquals(607L, sc.getErrorNum());
  }
  
  @Test
  public void test_ADD_AL_comma_BL_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group6/ADD_AL_comma_BL_comma.asm");
    assertEquals(607L, sc.getErrorNum());
  }
  
  @Test
  public void test_ADD_AL_comma_BL_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group6/ADD_AL_comma_BL_left.asm");
    assertEquals(607L, sc.getErrorNum());
   }

  @Test
  public void test_ADD_al() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group6/ADD_low_al.asm");
    assertEquals(601L, sc.getErrorNum());
  }
  
  @Test
  public void test_ADD_AL_comma_bl() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group6/ADD_AL_comma_low_bl.asm");
    assertEquals(605L, sc.getErrorNum());
  }
}
