package assemblertests;
import static org.junit.Assert.*;
import org.junit.Test;

import assembler.SyntaxChecker;


/**
 * Test assembler syntax rules:
 *    DB 20        --> ok
 *    DB 'a'       --> ok
 *    DB "hello"   --> ok
 *    
 *    DB           --> Expected hexadecimal number, inverted comma (') or quote (")
 *    DB xy        --> Expected hexadecimal number (range 00..FF), inverted comma (') or quote (")
 *    DB ,         --> Expected hexadecimal number (range 00..FF), inverted comma (') or quote (")
 *    DB [         --> Expected hexadecimal number (range 00..FF), inverted comma (') or quote (")
 *    DB '         --> Expected character
 *    DB 'xy       --> Expected character, got xy
 *    DB 'a        --> Expected inverted comma (')
 *    DB 'a xy     --> Expected inverted comma ('), got xy
 *    DB 'a' xy    --> Expected comment, got xy
 *    DB "         --> Expected at least one character
 *    DB ""        --> Expected at least one character other than quote("), got "
 *    DB "hello    --> Expected quote (")
 *    DB "hello" xy -> Expected comment, got xy
 * 
 * @author ruedi.mueller
 */
public class DbTest {
  @Test
  public void test_all_correct_DBs() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/db/all_correct_DBs.asm");
    assertEquals("", sc.getErrorMessage());
  }  
  
  @Test
  public void test_DB() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/db/DB.asm");
    assertEquals(1000, sc.getErrorNum());
  }

  @Test
  public void test_DB_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/db/DB_xy.asm");
    assertEquals(1001, sc.getErrorNum());
  }

  @Test
  public void test_DB_comma() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/db/DB_comma.asm");
    assertEquals(1001, sc.getErrorNum());
  }

  @Test
  public void test_DB_left() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/db/DB_left.asm");
    assertEquals(1001, sc.getErrorNum());
  }

  @Test
  public void test_DB_hex_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/db/DB_hex_xy.asm");
    assertEquals(1002, sc.getErrorNum());
  }

  @Test
  public void test_DB_endquote_missing() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/db/DB_endquote_missing.asm");
    assertEquals(1003, sc.getErrorNum());
  }

  @Test
  public void test_DB_quote_invalid_char() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/db/DB_quote_invalid_char.asm");
    assertEquals(1004, sc.getErrorNum());
  }
  
  @Test
  public void test_DB_quote_char_invalid_endquote() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/db/DB_quote_char_invalid_endquote.asm");
    assertEquals(1005, sc.getErrorNum());
  }
  
  @Test
  public void test_DB_char_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/db/DB_char_xy.asm");
    assertEquals(1006, sc.getErrorNum());
  }


  @Test
  public void test_DB_doublequote_empty() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/db/DB_doublequote_empty.asm");
    assertEquals(1007, sc.getErrorNum());
  }

  @Test
  public void test_DB_doubleendquote_missing() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/db/DB_doubleendquote_missing.asm");
    assertEquals(1008, sc.getErrorNum());
  }
  
  @Test
  public void test_DB_doublequote_invalid_char() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/db/DB_doublequote_invalid_char.asm");
    assertEquals(1009, sc.getErrorNum());
  }

  @Test
  public void test_DB_RAM_exceeded() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/db/DB_RAM_exceeded.asm");
    assertEquals(1010, sc.getErrorNum());
  }

  @Test
  public void test_DB_duplicate_RAM_allocation() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/db/DB_duplicate_RAM_allocation.asm");
    assertEquals(1011, sc.getErrorNum());
  }

  @Test
  public void test_DB_doublequote_xy() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/db/DB_doublequote_xy.asm");
    assertEquals(1012, sc.getErrorNum());
  }
}
