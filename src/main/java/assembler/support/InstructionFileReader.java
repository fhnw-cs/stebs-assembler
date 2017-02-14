package assembler.support;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import assembler.Assembler;


public class InstructionFileReader {

  public String execute(String filename) {
    StringBuilder sb = new StringBuilder();
    
    try {
      // Helpers to read the file
      ClassLoader cl = Assembler.class.getClassLoader();
      BufferedReader br = new BufferedReader(
    		  new InputStreamReader(cl.getResourceAsStream(Common.INSTRUCTION_FILENAME)));
      
      // Read file char by char
      int r;
      while ((r = br.read()) != -1) {
        sb.append((char) r);
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
      System.exit(-1);
    }
    return sb.toString();
  }
}
