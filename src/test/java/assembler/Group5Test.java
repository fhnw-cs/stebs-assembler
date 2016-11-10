package assembler;

import static org.junit.Assert.*;

import org.junit.Test;

import assembler.SyntaxChecker;


/**
 * Test assembler syntax rules:
 *    MOV AL,20        --> ok
 *    MOV AL,BL        --> ok
 *    MOV AL,[20]      --> ok
 *    MOV AL,[BL]      --> ok
 *    MOV [20],BL      --> ok
 *    MOV [AL],BL      --> ok
 *
 *    MOV              --> Expected XL/SP or [
 *    MOV xy           --> Expected XL/SP or [, got xy
 *    MOV ,            --> Expected XL/SP or [, got ,
 *    MOV AL           --> Expected comma (,)
 *    MOV AL xy        --> Expected comma (,), got xy
 *    MOV AL [         --> Expected comma (,), got [
 *    MOV AL,          --> Expected hexadecimal number (range 00..FF), XL/SP or [
 *    MOV AL,xy        --> Expected hexadecimal number (range 00..FF), XL/SP or [, got xy
 *    MOV AL,,         --> Expected hexadecimal number (range 00..FF), XL/SP or [, got ,
 *    MOV AL,20 xy     --> Expected comment, got xy
 *    MOV AL,BL xy     --> Expected comment, got xy
 *    MOV AL,[         --> Expected hexadecimal number (range 00..FF) or XL/SP
 *    MOV AL,[xy       --> Expected hexadecimal number (range 00..FF) or XL/SP, got xy
 *    MOV AL,[,        --> Expected hexadecimal number (range 00..FF) or XL/SP, got ,
 *    MOV AL,[[        --> Expected hexadecimal number (range 00..FF) or XL/SP, got [
 *    MOV AL,[20       --> Expected ]
 *    MOV AL,[20 xy    --> Expected ], got xy
 *    MOV AL,[20 ,     --> Expected ], got ,
 *    MOV AL,[20 [     --> Expected ], got [
 *    MOV AL,[BL       --> Expected ]
 *    MOV AL,[BL xy    --> Expected ], got xy
 *    MOV AL,[BL ,     --> Expected ], got ,
 *    MOV AL,[BL [     --> Expected ], got [
 *    MOV AL,[20] xy   --> Expected comment, got xy
 *    MOV AL,[20] ,    --> Expected comment, got ,
 *    MOV AL,[20] [    --> Expected comment, got [
 *    MOV AL,[BL] xy   --> Expected comment, got xy
 *    MOV AL,[BL] ,    --> Expected comment, got ,
 *    MOV AL,[BL] [    --> Expected comment, got [
 *    MOV [            --> Expected hexadecimal number (range 00..FF) or XL/SP
 *    MOV [xy          --> Expected hexadecimal number (range 00..FF) or XL/SP, got xy
 *    MOV [,           --> Expected hexadecimal number (range 00..FF) or XL/SP, got ,
 *    MOV [[           --> Expected hexadecimal number (range 00..FF) or XL/SP, got [
 *    MOV [20          --> Expected ]
 *    MOV [20 xy       --> Expected ], got xy
 *    MOV [20 ,        --> Expected ], got ,
 *    MOV [20 [        --> Expected ], got [
 *    MOV [AL          --> Expected ]
 *    MOV [AL xy       --> Expected ], got xy
 *    MOV [AL ,        --> Expected ], got ,
 *    MOV [AL [        --> Expected ], got [
 *    MOV [20]         --> Expected comma (,)
 *    MOV [20] xy      --> Expected comma (,), got xy
 *    MOV [20] [       --> Expected comma (,), got [
 *    MOV [20],        --> Expected XL/SP
 *    MOV [20],xy      --> Expected XL/SP, got xy
 *    MOV [20],,       --> Expected XL/SP, got ,
 *    MOV [20],[       --> Expected XL/SP, got [
 *    MOV [20],BL xy   --> Expected comment, got xy
 *    MOV [20],BL ,    --> Expected comment, got ,
 *    MOV [20],BL [    --> Expected comment, got [
 *    MOV [AL]         --> Expected comma (,)
 *    MOV [AL] xy      --> Expected comma (,), got xy
 *    MOV [AL] [       --> Expected comma (,), got [
 *    MOV [AL],        --> Expected hexadecimal number (range 00..FF) or XL/SP
 *    MOV [AL],xy      --> Expected hexadecimal number (range 00..FF) or XL/SP, got xy
 *    MOV [AL],,       --> Expected hexadecimal number (range 00..FF) or XL/SP, got ,
 *    MOV [AL],[       --> Expected hexadecimal number (range 00..FF) or XL/SP, got [
 *    MOV [AL],BL xy   --> Expected comment, got xy
 *    MOV [AL],BL ,    --> Expected comment, got ,
 *    MOV [AL],BL [    --> Expected comment, got [
 *    MOV al           --> Expected XL/SP or [, got al
 *    MOV AL,[bl       --> Expected hexadecimal number (range 00..FF) or XL/SP, got bl
 *    MOV [al          --> Expected hexadecimal number (range 00..FF) or XL/SP, got al
 *    MOV [20],bl      --> Expected XL/SP, got bl
 *    MOV [AL],bl      --> Expected hexadecimal number (range 00..FF) or XL/SP, got xy
 *
 * @author ruedi.mueller
 */
public class Group5Test {
  @Test
  public void test_all_correct_MOVs() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/all_correct_MOVs.asm");
    assertEquals("", sc.getErrorMessage());
  }

  @Test
  public void test_MOV() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV.asm");
    assertEquals(500L, sc.getErrorNum());
  }
  
  @Test
  public void test_MOV_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_xy.asm");
    assertEquals(501L, sc.getErrorNum());
  }
  
  @Test
  public void test_MOV_AL() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_AL.asm");
    assertEquals(502L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_AL_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_AL_xy.asm");
    assertEquals(503L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_AL_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_AL_comma.asm");
    assertEquals(504L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_AL_comma_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_AL_comma_xy.asm");
    assertEquals(505L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_AL_comma_hex_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_AL_comma_hex_xy.asm");
    assertEquals(506L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_AL_comma_BL_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_AL_comma_BL_xy.asm");
    assertEquals(507L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_AL_comma_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_AL_comma_left.asm");
    assertEquals(508L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_AL_comma_left_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_AL_comma_left_xy.asm");
    assertEquals(509L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_AL_comma_left_hex() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_AL_comma_left_hex.asm");
    assertEquals(510L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_AL_comma_left_hex_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_AL_comma_left_hex_xy.asm");
    assertEquals(511L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_AL_comma_left_hex_right_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_AL_comma_left_hex_right_xy.asm");
    assertEquals(512L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_AL_comma_left_BL() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_AL_comma_left_BL.asm");
    assertEquals(513L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_AL_comma_left_BL_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_AL_comma_left_BL_xy.asm");
    assertEquals(514L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_AL_comma_left_BL_right_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_AL_comma_left_BL_right_xy.asm");
    assertEquals(515L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left.asm");
    assertEquals(520L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_xy.asm");
    assertEquals(521L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_hex() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_hex.asm");
    assertEquals(522L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_hex_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_hex_xy.asm");
    assertEquals(523L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_hex_right() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_hex_right.asm");
    assertEquals(524L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_hex_right_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_hex_right_xy.asm");
    assertEquals(525L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_hex_right_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_hex_right_comma.asm");
    assertEquals(526L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_hex_right_comma_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_hex_right_comma_xy.asm");
    assertEquals(527L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_hex_right_comma_BL_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_hex_right_comma_BL_xy.asm");
    assertEquals(528L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_AL() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_AL.asm");
    assertEquals(529L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_AL_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_AL_xy.asm");
    assertEquals(530L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_AL_right() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_AL_right.asm");
    assertEquals(531L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_AL_right_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_AL_right_xy.asm");
    assertEquals(532L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_AL_right_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_AL_right_comma.asm");
    assertEquals(533L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_AL_right_comma_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_AL_right_comma_xy.asm");
    assertEquals(534L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_AL_right_comma_BL_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_AL_right_comma_BL_xy.asm");
    assertEquals(535L, sc.getErrorNum());
  }
  
  @Test
  public void test_MOV_al() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_low_al.asm");
    assertEquals(501L, sc.getErrorNum());
  }
  
  @Test
  public void test_MOV_AL_comma_left_bl() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_AL_comma_left_low_bl.asm");
    assertEquals(509L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_al() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_low_al.asm");
    assertEquals(521L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_hex_right_comma_bl() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_hex_right_comma_low_bl.asm");
    assertEquals(527L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_AL_right_comma_bl() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_AL_right_comma_low_bl.asm");
    assertEquals(534L, sc.getErrorNum());
  }
  
  @Test
  public void test_MOV_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_comma.asm");
    assertEquals(501L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_AL_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_AL_left.asm");
    assertEquals(503L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_AL_comma_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_AL_comma_comma.asm");
    assertEquals(505L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_AL_comma_left_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_AL_comma_left_comma.asm");
    assertEquals(509L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_AL_comma_left_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_AL_comma_left_left.asm");
    assertEquals(509L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_AL_comma_left_hex_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_AL_comma_left_hex_comma.asm");
    assertEquals(511L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_AL_comma_left_hex_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_AL_comma_left_hex_left.asm");
    assertEquals(511L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_comma.asm");
    assertEquals(521L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_left.asm");
    assertEquals(521L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_hex_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_hex_commma.asm");
    assertEquals(523L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_hex_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_hex_left.asm");
    assertEquals(523L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_AL_comma_left_BL_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_AL_comma_left_BL_comma.asm");
    assertEquals(514L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_AL_comma_left_BL_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_AL_comma_left_BL_left.asm");
    assertEquals(514L, sc.getErrorNum());
  }
  
  @Test
  public void test_MOV_AL_comma_left_hex_right_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_AL_comma_left_hex_right_comma.asm");
    assertEquals(512L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_AL_comma_left_hex_right_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_AL_comma_left_hex_right_left.asm");
    assertEquals(512L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_AL_comma_left_BL_right_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_AL_comma_left_BL_right_comma.asm");
    assertEquals(515L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_AL_comma_left_BL_right_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_AL_comma_left_BL_right_left.asm");
    assertEquals(515L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_AL_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_AL_comma.asm");
    assertEquals(530L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_AL_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_AL_left.asm");
    assertEquals(530L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_hex_right_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_hex_right_left.asm");
    assertEquals(525L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_hex_right_comma_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_hex_right_comma_comma.asm");
    assertEquals(527L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_hex_right_comma_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_hex_right_comma_left.asm");
    assertEquals(527L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_hex_right_comma_BL_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_hex_right_comma_BL_comma.asm");
    assertEquals(528L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_hex_right_comma_BL_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_hex_right_comma_BL_left.asm");
    assertEquals(528L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_AL_right_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_AL_right_left.asm");
    assertEquals(532L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_AL_right_comma_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_AL_right_comma_comma.asm");
    assertEquals(534L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_AL_right_comma_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_AL_right_comma_left.asm");
    assertEquals(534L, sc.getErrorNum());
  }

  @Test
  public void test_MOV_left_AL_right_comma_BL_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_AL_right_comma_BL_comma.asm");
    assertEquals(535L, sc.getErrorNum());
  }
  
  @Test
  public void test_MOV_left_AL_right_comma_BL_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/group5/MOV_left_AL_right_comma_BL_left.asm");
    assertEquals(535L, sc.getErrorNum());
  }
}
