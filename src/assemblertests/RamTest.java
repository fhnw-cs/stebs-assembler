package assemblertests;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.StringTokenizer;

import assembler.SyntaxChecker;


/**
 * Test bytes written to RAM after assembling.
 * 
 * NOTICE for pasting machine code into tests see below.
 * 
 * @author ruedi.mueller
 */
public class RamTest {
  /**
   * Convert a hex string into an array of int. Typically used to
   * build an array with machine code taken from a clipboard copy of an
   * stebs assembled program.
   * 
   * @param stebsMachineCodeString The clipboard copy of a stebs assembled program
   *                               enclosed by "s.
   * @return the int array with the machine code
   */
  private int[] toIntArray(String stebsMachineCodeString) {
    StringTokenizer st = new StringTokenizer(stebsMachineCodeString, " \r\n");
    int[] stebsMachineCode = new int[256];
    int index = 0;
    while (st.hasMoreTokens()) {
      int code = Integer.valueOf(st.nextToken(), 16);
      stebsMachineCode[index++] = code;
    }
    return stebsMachineCode;
  }
  
  
  @Test
  // RAM bytes compared with "Students' Training Eight Bit Simulator" stebs based on
  // "Microprocessor Simulator for Students" by C Neil Bauers, 
  // http://www.softwareforeducation.com/sms32v50/
  
  /**
   * NOTICE:
   * To copy multi-line strings in eclipse set:
   * 1. Preferences/Java/Editor/Typing/ “Escape text when pasting into a string literal”
   *    (done for this project)
   * 2. Copy multiline string within ""
   */
  public void test_multitasker() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/ram/multitasker.asm");
    assertEquals("", sc.getErrorMessage());
    
    String stebsMachineCodeString =
      "C0 03 06 FC C0 72 FD EA E0 03 E0 02 E0 01 E0 00 \r\n" + 
      "D1 03 60 D3 03 03 D0 02 06 E1 00 D4 03 00 A4 03 \r\n" + 
      "A5 02 C2 F7 D1 00 60 A4 00 D3 03 00 DB 03 00 C2 \r\n" + 
      "08 D0 00 61 D1 03 61 D2 60 00 B0 03 05 D0 02 06 \r\n" + 
      "D3 00 03 E0 00 A5 03 A5 02 C2 F7 E1 00 E1 01 E1 \r\n" + 
      "02 E1 03 EB FC CD 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "61 70 9A C0 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 76 D0 03 87 D3 00 03 F1 02 A4 03 \r\n" + 
      "DB 03 91 C2 F6 C0 F1 3F 06 5B 4F 66 6D 7D 07 7F \r\n" + 
      "6F 00 00 00 00 00 00 00 00 00 00 00 00 00 00 A0 \r\n" + 
      "D0 03 B2 D3 00 03 F1 00 A4 03 DB 03 B6 C2 F6 C0 \r\n" + 
      "F1 00 82 C4 28 4C 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 C6 D0 01 14 F0 01 BA 00 40 DB 00 \r\n" + 
      "40 C1 0C D0 00 80 AB 00 01 F1 01 C0 EE D0 00 00 \r\n" + 
      "AB 00 01 F1 01 C0 E4 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" +
      "";
    
    int[] stebsMachineCode = toIntArray(stebsMachineCodeString);
    assertArrayEquals(stebsMachineCode, sc.getRam());
  }

  
  @Test
  public void test_group0_commands() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/ram/group0_commands.asm");
    assertEquals("", sc.getErrorMessage());
    
    String stebsMachineCodeString =
      "00 00 CB CD EA EB FC FD FF 00 00 00 00 00 00 00 \r\n" + 
      "00 00 CB CD EA EB FC FD FF 00 00 00 00 00 00 00 \r\n" + 
      "00 00 CB CD EA EB FC FD FF 00 00 00 00 00 00 00 \r\n" + 
      "00 00 CB CD EA EB FC FD FF 00 00 00 00 00 00 00 \r\n" + 
      "00 00 CB CD EA EB FC FD FF 00 00 00 00 00 00 00 \r\n" + 
      "00 00 CB CD EA EB FC FD FF 00 00 00 00 00 00 00 \r\n" + 
      "00 00 CB CD EA EB FC FD FF 00 00 00 00 00 00 00 \r\n" + 
      "00 00 CB CD EA EB FC FD FF 00 00 00 00 00 00 00 \r\n" + 
      "00 00 CB CD EA EB FC FD FF 00 00 00 00 00 00 00 \r\n" + 
      "00 00 CB CD EA EB FC FD FF 00 00 00 00 00 00 00 \r\n" + 
      "00 00 CB CD EA EB FC FD FF 00 00 00 00 00 00 00 \r\n" + 
      "00 00 CB CD EA EB FC FD FF 00 00 00 00 00 00 00 \r\n" + 
      "00 00 CB CD EA EB FC FD FF 00 00 00 00 00 00 00 \r\n" + 
      "00 00 CB CD EA EB FC FD FF 00 00 00 00 00 00 00 \r\n" + 
      "00 00 CB CD EA EB FC FD FF 00 00 00 00 00 00 00 \r\n" + 
      "00 00 CB CD EA EB FC FD FF 00 00 00 00 00 00 00 \r\n" + 
      "";
    
    int[] stebsMachineCode = toIntArray(stebsMachineCodeString);
    assertArrayEquals(stebsMachineCode, sc.getRam());
  }

  
  @Test
  public void test_group1_commands() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/ram/group1_commands.asm");
    assertEquals("", sc.getErrorMessage());
    
    String stebsMachineCodeString =
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 F1 00 F1 01 F1 02 F1 03 F1 \r\n" + 
      "04 F1 05 F1 06 F1 07 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "CA C8 CA C9 CA CA CA CB CA D8 CA E8 CA F8 CA 08 \r\n" + 
      "CA 18 CA 88 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "F0 0F F0 0E F0 0D F0 0C F0 0B F0 0A F0 09 F0 08 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "CC 02 CC 03 CC 04 CC 05 CC 06 CC 07 CC 08 CC 09 \r\n" + 
      "CC 0A 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "";
    
    int[] stebsMachineCode = toIntArray(stebsMachineCodeString);
    assertArrayEquals(stebsMachineCode, sc.getRam());
  }
  
  
  @Test
  public void test_group2_commands() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/ram/group2_commands.asm");
    assertEquals("", sc.getErrorMessage());
    
    String stebsMachineCodeString =
      "00 00 00 00 00 9A 00 9A 01 9A 02 9A 03 9A 04 00 \r\n" + 
      "00 00 00 00 00 9B 00 9B 01 9B 02 9B 03 9B 04 00 \r\n" + 
      "00 00 00 00 00 9C 00 9C 01 9C 02 9C 03 9C 04 00 \r\n" + 
      "00 00 00 00 00 9D 00 9D 01 9D 02 9D 03 9D 04 00 \r\n" + 
      "00 00 00 00 00 A4 00 A4 01 A4 02 A4 03 A4 04 00 \r\n" + 
      "00 00 00 00 00 A5 00 A5 01 A5 02 A5 03 A5 04 00 \r\n" + 
      "00 00 00 00 00 AD 00 AD 01 AD 02 AD 03 AD 04 00 \r\n" + 
      "00 00 00 00 00 E0 00 E0 01 E0 02 E0 03 E0 04 00 \r\n" + 
      "00 00 00 00 00 E1 00 E1 01 E1 02 E1 03 E1 04 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "";
    
    int[] stebsMachineCode = toIntArray(stebsMachineCodeString);
    assertArrayEquals(stebsMachineCode, sc.getRam());
  }

  
  @Test
  public void test_group3_commands() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/ram/group3_commands.asm");
    assertEquals("", sc.getErrorMessage());
    
    String stebsMachineCodeString =
      "C0 00 C1 FE C2 FC C5 FA C6 F8 C3 F6 C4 F4 C0 F2 \r\n" + 
      "C1 F0 C2 EE C5 EC C6 EA C3 E8 C4 E6 C0 E4 C1 E2 \r\n" + 
      "C2 E0 C5 DE C6 DC C3 DA C4 D8 C0 D6 C1 D4 C2 D2 \r\n" + 
      "C5 D0 C6 CE C3 CC C4 CA C0 C8 C1 C6 C2 C4 C5 C2 \r\n" + 
      "C6 C0 C3 BE C4 BC C0 00 C1 FE C2 FC C5 FA C6 F8 \r\n" + 
      "C3 F6 C4 F4 C0 F2 C1 F0 C2 EE C5 EC C6 EA C3 E8 \r\n" + 
      "C4 E6 C0 E4 C1 E2 C2 E0 C5 DE C6 DC C3 DA C4 D8 \r\n" + 
      "C0 D6 C1 D4 C2 D2 C5 D0 C6 CE C3 CC C4 CA C0 C8 \r\n" + 
      "C1 C6 C2 C4 C5 C2 C6 C0 C3 BE C4 BC C0 46 C1 44 \r\n" + 
      "C2 42 C5 40 C6 3E C3 3C C4 3A C0 38 C1 36 C2 34 \r\n" + 
      "C5 32 C6 30 C3 2E C4 2C C0 2A C1 28 C2 26 C5 24 \r\n" + 
      "C6 22 C3 20 C4 1E C0 1C C1 1A C2 18 C5 16 C6 14 \r\n" + 
      "C3 12 C4 10 C0 0E C1 0C C2 0A C5 08 C6 06 C3 04 \r\n" + 
      "C4 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "";
    
    int[] stebsMachineCode = toIntArray(stebsMachineCodeString);
    assertArrayEquals(stebsMachineCode, sc.getRam());
  }

  
  @Test
  public void test_group4_commands() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/ram/group4_commands.asm");
    assertEquals("", sc.getErrorMessage());
    
    String stebsMachineCodeString =
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 DB 00 7E DB 01 7F DB 02 80 DB 03 \r\n" + 
      "81 DB 04 82 DA 00 03 DA 01 03 DA 02 03 DA 04 03 \r\n" + 
      "DA 03 04 DC 00 EF DC 01 FF DC 02 1F DC 03 0F DC \r\n" + 
      "04 2F 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "";
    
    int[] stebsMachineCode = toIntArray(stebsMachineCodeString);
    assertArrayEquals(stebsMachineCode, sc.getRam());
  }

  
  @Test
  public void test_group5_commands() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/ram/group5_commands.asm");
    assertEquals("", sc.getErrorMessage());
    
    String stebsMachineCodeString =
      "D0 00 00 D0 01 99 D0 02 AA D0 03 7D D0 04 E8 D5 \r\n" + 
      "00 01 D5 01 02 D5 03 04 D5 01 01 D5 00 04 D1 04 \r\n" + 
      "01 D1 03 9A D1 02 AB D1 01 7C D1 00 D7 D3 00 01 \r\n" + 
      "D3 01 01 D3 02 03 D3 04 04 D3 04 00 D2 02 01 D2 \r\n" + 
      "17 01 D2 A3 01 D2 CD 01 D2 A1 01 D4 00 01 D4 02 \r\n" + 
      "01 D4 03 02 D4 04 01 D4 04 04 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 D0 00 00 D0 01 99 D0 02 AA \r\n" + 
      "D0 03 7D D0 04 E8 D5 00 01 D5 01 02 D5 03 04 D5 \r\n" + 
      "01 01 D5 00 04 D1 04 01 D1 03 9A D1 02 AB D1 01 \r\n" + 
      "7C D1 00 D7 D3 00 01 D3 01 01 D3 02 03 D3 04 04 \r\n" + 
      "D3 04 00 D2 02 01 D2 17 01 D2 A3 01 D2 CD 01 D2 \r\n" + 
      "A1 01 D4 00 01 D4 02 01 D4 03 02 D4 04 01 D4 04 \r\n" + 
      "04 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "";

    int[] stebsMachineCode = toIntArray(stebsMachineCodeString);
    assertArrayEquals(stebsMachineCode, sc.getRam());
  }

  
  @Test
  public void test_group6_commands() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/ram/group6_commands.asm");
    assertEquals("", sc.getErrorMessage());
    
    String stebsMachineCodeString =
      "B0 00 11 B0 01 AA B0 02 F9 B0 03 9F B0 04 00 A0 \r\n" + 
      "00 01 A0 01 02 A0 02 03 A0 03 00 A0 04 04 00 00 \r\n" + 
      "B1 00 11 B1 01 AA B1 02 F9 B1 03 9F B1 04 00 A1 \r\n" + 
      "00 01 A1 01 02 A1 02 03 A1 03 00 A1 04 04 00 00 \r\n" + 
      "B2 00 11 B2 01 AA B2 02 F9 B2 03 9F B2 04 00 A2 \r\n" + 
      "00 01 A2 01 02 A2 02 03 A2 03 00 A2 04 04 00 00 \r\n" + 
      "B3 00 11 B3 01 AA B3 02 F9 B3 03 9F B3 04 00 A3 \r\n" + 
      "00 01 A3 01 02 A3 02 03 A3 03 00 A3 04 04 00 00 \r\n" + 
      "B6 00 11 B6 01 AA B6 02 F9 B6 03 9F B6 04 00 A6 \r\n" + 
      "00 01 A6 01 02 A6 02 03 A6 03 00 A6 04 04 00 00 \r\n" + 
      "BA 00 11 BA 01 AA BA 02 F9 BA 03 9F BA 04 00 AA \r\n" + 
      "00 01 AA 01 02 AA 02 03 AA 03 00 AA 04 04 00 00 \r\n" + 
      "BB 00 11 BB 01 AA BB 02 F9 BB 03 9F BB 04 00 AB \r\n" + 
      "00 01 AB 01 02 AB 02 03 AB 03 00 AB 04 04 00 00 \r\n" + 
      "BC 00 11 BC 01 AA BC 02 F9 BC 03 9F BC 04 00 AC \r\n" + 
      "00 01 AC 01 02 AC 02 03 AC 03 00 AC 04 04 00 00 \r\n" + 
      "";
    
    int[] stebsMachineCode = toIntArray(stebsMachineCodeString);
    assertArrayEquals(stebsMachineCode, sc.getRam());
  }


  @Test
  public void test_orgs() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/ram/orgs.asm");
    assertEquals("", sc.getErrorMessage());
    
    String stebsMachineCodeString =
      "FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 FF 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 FF 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 FF 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 FF 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 FF 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 FF 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 FF 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 FF 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 FF 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 FF 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 FF 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 FF 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF 00 \r\n" + 
      "";
    
    int[] stebsMachineCode = toIntArray(stebsMachineCodeString);
    assertArrayEquals(stebsMachineCode, sc.getRam());
  }


  @Test
  public void test_jumps0() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/ram/jumps0.asm");
    assertEquals("", sc.getErrorMessage());
    
    String stebsMachineCodeString =
      "C0 7F C0 7D C0 7B C0 79 C0 77 C0 75 C0 73 C0 71 \r\n" + 
      "C0 6F C0 6D C0 6B C0 69 C0 67 C0 65 C0 63 C0 61 \r\n" + 
      "C0 5F C0 5D C0 5B C0 59 C0 57 C0 55 C0 53 C0 51 \r\n" + 
      "C0 4F C0 4D C0 4B C0 49 C0 47 C0 45 C0 43 C0 41 \r\n" + 
      "C0 3F C0 3D C0 3B C0 39 C0 37 C0 35 C0 33 C0 31 \r\n" + 
      "C0 2F C0 2D C0 2B C0 29 C0 27 C0 25 C0 23 C0 21 \r\n" + 
      "C0 1F C0 1D C0 1B C0 19 C0 17 C0 15 C0 13 C0 11 \r\n" + 
      "C0 0F C0 0D C0 0B C0 09 C0 07 C0 05 C0 03 C0 01 \r\n" + 
      "C0 FF C0 FD C0 FB C0 F9 C0 F7 C0 F5 C0 F3 C0 F1 \r\n" + 
      "C0 EF C0 ED C0 EB C0 E9 C0 E7 C0 E5 C0 E3 C0 E1 \r\n" + 
      "C0 DF C0 DD C0 DB C0 D9 C0 D7 C0 D5 C0 D3 C0 D1 \r\n" + 
      "C0 CF C0 CD C0 CB C0 C9 C0 C7 C0 C5 C0 C3 C0 C1 \r\n" + 
      "C0 BF C0 BD C0 BB C0 B9 C0 B7 C0 B5 C0 B3 C0 B1 \r\n" + 
      "C0 AF C0 AD C0 AB C0 A9 C0 A7 C0 A5 C0 A3 C0 A1 \r\n" + 
      "C0 9F C0 9D C0 9B C0 99 C0 97 C0 95 C0 93 C0 91 \r\n" + 
      "C0 8F C0 8D C0 8B C0 89 C0 87 C0 85 FF C0 82 00 \r\n" + 
      "";
    
    int[] stebsMachineCode = toIntArray(stebsMachineCodeString);
    assertArrayEquals(stebsMachineCode, sc.getRam());
  }


  @Test
  public void test_jumps1() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/ram/jumps1.asm");
    assertEquals("", sc.getErrorMessage());
    
    String stebsMachineCodeString =
      "00 C0 7F C0 7D C0 7B C0 79 C0 77 C0 75 C0 73 C0 \r\n" + 
      "71 C0 6F C0 6D C0 6B C0 69 C0 67 C0 65 C0 63 C0 \r\n" + 
      "61 C0 5F C0 5D C0 5B C0 59 C0 57 C0 55 C0 53 C0 \r\n" + 
      "51 C0 4F C0 4D C0 4B C0 49 C0 47 C0 45 C0 43 C0 \r\n" + 
      "41 C0 3F C0 3D C0 3B C0 39 C0 37 C0 35 C0 33 C0 \r\n" + 
      "31 C0 2F C0 2D C0 2B C0 29 C0 27 C0 25 C0 23 C0 \r\n" + 
      "21 C0 1F C0 1D C0 1B C0 19 C0 17 C0 15 C0 13 C0 \r\n" + 
      "11 C0 0F C0 0D C0 0B C0 09 C0 07 C0 05 C0 03 C0 \r\n" + 
      "01 C0 FF C0 FD C0 FB C0 F9 C0 F7 C0 F5 C0 F3 C0 \r\n" + 
      "F1 C0 EF C0 ED C0 EB C0 E9 C0 E7 C0 E5 C0 E3 C0 \r\n" + 
      "E1 C0 DF C0 DD C0 DB C0 D9 C0 D7 C0 D5 C0 D3 C0 \r\n" + 
      "D1 C0 CF C0 CD C0 CB C0 C9 C0 C7 C0 C5 C0 C3 C0 \r\n" + 
      "C1 C0 BF C0 BD C0 BB C0 B9 C0 B7 C0 B5 C0 B3 C0 \r\n" + 
      "B1 C0 AF C0 AD C0 AB C0 A9 C0 A7 C0 A5 C0 A3 C0 \r\n" + 
      "A1 C0 9F C0 9D C0 9B C0 99 C0 97 C0 95 C0 93 C0 \r\n" + 
      "91 C0 8F C0 8D C0 8B C0 89 C0 87 C0 85 FF C0 82 \r\n" + 
      "";
    
    int[] stebsMachineCode = toIntArray(stebsMachineCodeString);
    assertArrayEquals(stebsMachineCode, sc.getRam());
  }


  @Test
  public void test_jumps2() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/ram/jumps2.asm");
    assertEquals("", sc.getErrorMessage());
    
    String stebsMachineCodeString =
      "C0 7F C0 7D C0 7B C0 79 C0 77 C0 75 C0 73 C0 71 \r\n" + 
      "C0 6F C0 6D C0 6B C0 69 C0 67 C0 65 C0 63 C0 61 \r\n" + 
      "C0 5F C0 5D C0 5B C0 59 C0 57 C0 55 C0 53 C0 51 \r\n" + 
      "C0 4F C0 4D C0 4B C0 49 C0 47 C0 45 C0 43 C0 41 \r\n" + 
      "C0 3F C0 3D C0 3B C0 39 C0 37 C0 35 C0 33 C0 31 \r\n" + 
      "C0 2F C0 2D C0 2B C0 29 C0 27 C0 25 C0 23 C0 21 \r\n" + 
      "C0 1F C0 1D C0 1B C0 19 C0 17 C0 15 C0 13 C0 11 \r\n" + 
      "C0 0F C0 0D C0 0B C0 09 C0 07 C0 05 C0 03 C0 01 \r\n" + 
      "00 C0 FE C0 FC C0 FA C0 F8 C0 F6 C0 F4 C0 F2 C0 \r\n" + 
      "F0 C0 EE C0 EC C0 EA C0 E8 C0 E6 C0 E4 C0 E2 C0 \r\n" + 
      "E0 C0 DE C0 DC C0 DA C0 D8 C0 D6 C0 D4 C0 D2 C0 \r\n" + 
      "D0 C0 CE C0 CC C0 CA C0 C8 C0 C6 C0 C4 C0 C2 C0 \r\n" + 
      "C0 C0 BE C0 BC C0 BA C0 B8 C0 B6 C0 B4 C0 B2 C0 \r\n" + 
      "B0 C0 AE C0 AC C0 AA C0 A8 C0 A6 C0 A4 C0 A2 C0 \r\n" + 
      "A0 C0 9E C0 9C C0 9A C0 98 C0 96 C0 94 C0 92 C0 \r\n" + 
      "90 C0 8E C0 8C C0 8A C0 88 C0 86 C0 84 FF C0 81 \r\n" + 
      "";
    
    int[] stebsMachineCode = toIntArray(stebsMachineCodeString);
    assertArrayEquals(stebsMachineCode, sc.getRam());
  }

  @Test
  public void test_bubblesort() {
    SyntaxChecker sc = AllTests.assemble("assemblertests/ram/bubblesort.asm");
    assertEquals("", sc.getErrorMessage());
    
    String stebsMachineCodeString = 
      "D0 00 A0 D0 01 05 CA 20 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "DB 01 01 C1 37 DB 01 00 C1 32 E0 00 A0 01 00 E1 \r\n" + 
      "00 E0 00 A5 01 DA 01 00 C1 22 DA 01 00 C1 1B D3 \r\n" + 
      "02 00 A4 00 D3 03 00 DA 02 03 C3 0C D4 00 02 A5 \r\n" + 
      "00 D4 00 03 A4 00 C0 E4 C0 D7 E1 00 CB 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "09 04 F3 01 97 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 \r\n" + 
      "";
    
    int[] stebsMachineCode = toIntArray(stebsMachineCodeString);
    assertArrayEquals(stebsMachineCode, sc.getRam());
  }
}
