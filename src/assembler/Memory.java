package assembler;

import assembler.support.Common;

import java.util.Arrays;


/**
 * The class Memory bundles RAM in a int array and a corresponding used RAM slot
 * array.
 * A written RAM slot will always be marked in its used RAM slot array as "smudged".
 * Used to detect duplicate RAM writes which is an error.
 * 
 * @author ruedi.mueller
 */
public class Memory {
  // The array representing RAM for machine code
  private int[] ram = new int[256];
  
  // The array representing used RAM slots in the RAM
  // Initially each slot set to false, programmatically changed to true, if
  // corresponding RAM slot is used
  private boolean[] usedRamSlots = new boolean[256];
  
  
  // Getters and setters
  public int[] getRam() {
    return ram;
  }

  public void setRam(int[] ram) {
    this.ram = ram;
  }

  public boolean[] getUsedRamSlots() {
    return usedRamSlots;
  }

  public void setUsedRamSlots(boolean[] usedRamSlots) {
    this.usedRamSlots = usedRamSlots;
  }

  
  /**
   * Instantiate memory consisting of a RAM array and its associated used
   * RAM slot array.
   */
  public Memory() {
    // Initialize RAM and corresponding (= same indices) used RAM slots
    Arrays.fill(ram, 0);
    Arrays.fill(usedRamSlots, false);
  }
  
  
  /**
   * Answer a string showing all RAM contents in the form of a memory dump.
   * 
   * @return the string containing all memory data
   */
  private String toRamString() {
    StringBuilder sb = new StringBuilder();
    sb.append("\nRAM:\n");
    sb.append("       0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F\n");

    for (int i = 0; i < ram.length; i = i + 0x10) {
      sb.append("  " + Common.toHexByteString(i) + "  ");
      for (int j = 0; j < 0x10; j++) {
        sb.append(Common.toHexByteString(ram[i + j]) + " ");
      }
      sb.append("  ");
      for (int j = 0; j < 0x10; j++) {
        char c = (char) ram[i + j];
        sb.append((c <= 0x20 || c > 0x7E) ? '.' : c);
      }
      sb.append("\n");
    }

    return sb.toString();
  }
  
  
  @Override
  public String toString() {
    return toRamString().toString();
  }
}
