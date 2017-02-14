package assembler.support;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;


public class AsmFileReader {

  public String execute(String filename) {
    FileInputStream fstream = null;
    BufferedReader br = null;
    StringBuilder sb = new StringBuilder();
    
    try {
      // Helpers to read the file
      fstream = new FileInputStream(filename);
      br = new BufferedReader(new InputStreamReader(fstream));
      
      int r;
      // Read file char by char
      while ((r = br.read()) != -1) {
        sb.append((char) r);
      }
    }
    catch (FileNotFoundException fnfe) {
      System.err.println("Could not find the assembler file "
          + Common.getFilename() + "\n" + "Are file name and path correct?\n"
          + "--> exit application");
      System.exit(-1);
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    finally {
      try {
        if (br != null)
          br.close();
        if (fstream != null)
          fstream.close();
      }
      catch (IOException ioEx) {
        ioEx.printStackTrace();
      }
    }

    return sb.toString();
  }
}
