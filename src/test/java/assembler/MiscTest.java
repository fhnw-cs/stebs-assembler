package assembler;

import static org.junit.Assert.*;
import org.junit.Test;

import assembler.SyntaxChecker;


/**
 * Test assembler syntax rules.
 * Special cases.
 * 
 * @author ruedi.mueller
 */
public class MiscTest {
  @Test
  public void test_emptyfile() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/misc/emptyfile.asm");
    assertEquals(14L, sc.getErrorNum());
  }

  @Test
  public void test_comma_file() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/misc/comma_file.asm");
    assertEquals(8L, sc.getErrorNum());
  }

  @Test
  public void test_left_file() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/misc/left_file.asm");
    assertEquals(8L, sc.getErrorNum());
  }

  
  // Lettercase tests
  @Test
  public void test_lowercaseMnemonic() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/misc/lowercase_mnemonic.asm");
    assertEquals(8L, sc.getErrorNum());
  }

  @Test
  public void test_lowercaseOrg() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/misc/lowercase_org.asm");
    assertEquals(8L, sc.getErrorNum());
  }

  @Test
  public void test_lowercaseDb() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/misc/lowercase_db.asm");
    assertEquals(8L, sc.getErrorNum());
  }

  @Test
  public void test_lowercaseEnd() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/misc/lowercase_end.asm");
    assertEquals(8L, sc.getErrorNum());
  }

  
  
  @Test
  public void test_lowercaseParam1() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/misc/lowercase_param1.asm");
    assertEquals(501L, sc.getErrorNum());
  }

  @Test
  public void test_lowercaseParam2() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/misc/lowercase_param2.asm");
    assertEquals(505L, sc.getErrorNum());
  }

  
  // END tests
  @Test
  public void test_END_noNewline() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/misc/END_noNewline.asm");
    assertEquals("", sc.getErrorMessage());
  }

  @Test
  public void test_END_comment_noNewline() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/misc/END_comment_noNewline.asm");
    assertEquals("", sc.getErrorMessage());
  }

  @Test
  public void test_END_xy_noNewline() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/misc/END_xy_noNewline.asm");
    assertEquals("", sc.getErrorMessage());
  }

  @Test
  public void test_END_comma_noNewline() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/misc/END_comma_noNewline.asm");
    assertEquals("", sc.getErrorMessage());
  }

  @Test
  public void test_END_left_noNewline() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/misc/END_left_noNewline.asm");
    assertEquals("", sc.getErrorMessage());
  }

  @Test
  public void test_END() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/misc/END.asm");
    assertEquals("", sc.getErrorMessage());
  }

  @Test
  public void test_newline_END() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/misc/newline_END.asm");
    assertEquals("", sc.getErrorMessage());
  }

  @Test
  public void test_END_is_missing() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/misc/END_is_missing.asm");
    assertEquals(14L, sc.getErrorNum());
  }


  // Label tests
  @Test
  public void test_label_noNewline() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/misc/label_noNewline.asm");
    assertEquals(14L, sc.getErrorNum());
  }

  @Test
  public void test_label_END() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/misc/label_END.asm");
    assertEquals("", sc.getErrorMessage());
  }

  @Test
  public void test_label_END__noNewline() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/misc/label_END_noNewline.asm");
    assertEquals("", sc.getErrorMessage());
  }

  @Test
  public void test_hex2label_END() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/misc/hex2label_END.asm");
    assertEquals(19L, sc.getErrorNum());
  }

  @Test
  public void test_hex1label_END() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/misc/hex1label_END.asm");
    assertEquals(20L, sc.getErrorNum());
  }

  @Test
  public void test_label_invalidChar() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/misc/label_invalidChar.asm");
    assertEquals(22L, sc.getErrorNum());
  }

  @Test
  public void test_label_onePerLineOnly() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/misc/label_onePerLineOnly.asm");
    assertEquals(6L, sc.getErrorNum());
  }

  @Test
  public void test_ram_space_exceeded() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/misc/ram_space_exceeded.asm");
    assertEquals(12L, sc.getErrorNum());
  }

  
  // Tests with SP
  @Test
  public void test_sp_commands() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/misc/sp_commands.asm");
    assertEquals("", sc.getErrorMessage());

  }
}
