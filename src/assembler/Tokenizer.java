package assembler;
import java.io.*;

import assembler.support.*;


/**
 * The class Tokenizer is used to scan an assembler file, typically an *.asm.
 * With method tokenize tokens are extracted from the file and stored in a list
 * of strings according to the rules given below.
 * 
 * @author ruedi.mueller
 */
public class Tokenizer {
  private TokenList tokenList = new TokenList();
  
  // StringBuilder to collect characters while reading file
  private StringBuilder sb = new StringBuilder();
  
  // Manage location of tokens
  private int linenum = 1;
  private int linepos = 1;

  
  /**
   * Read assembler source file (*.asm) and extract tokens into a list.
   * 
   * The following rules apply:
   * <br> a) Empty lines accepted
   * <br> b) cr/lf is replaced by Common.NL and entered into the list as an individual entry
   * <br> c) Delimiters for tokens are ' ', ',', '[', ']', ':', <eol> and <eof>
   * <br> d) A single list entry for each ',', '[' and ']'
   * <br> e) A single list entry for word ending with a colon
   * <br> f) Each comment (semicolon and text) to <eol> or <eof> in a single list entry
   * <br> g) Each char enclosed in "'" in a single list entry
   * <br> h) Each string enclosed in '"' in a single list entry
   * <br> i) Linefeeds are ignored
   *     
   * @param asmString String to be tokenized
   * @return the list of strings with all tokens, $ (= carriage return) and comments.
   */
  public void tokenize(String asmString) {
    BufferedReader br = new BufferedReader(new StringReader(asmString));
    
    try {
      // Holds read "character"
      int r;

      // Read file char by char
      while ((r = br.read()) != -1) {
        char chr = (char) r;
        
        // Scrutinize character
        switch (chr) {
          // Carriage return
          case 0x0D:
            carriageReturn();
            break;
          
          // Ignore possible linefeed
          case 0x0A:
            break;
          
          // Space
          case ' ':
            space();
            break;
          
          // Colon used for labels
          case ':':
            colon();
            break;
          
          // Comma used to separate parameters
          // Brackets to enclose hex numbers or registers to select addressing mode
          case ',':
          case '[':
          case ']':
            commaBrackets(chr);
            break;
          
          // Quote to enclose a character
          case '\'':
            quote(br);
            break;

          // Double quotes to enclose a string
          case '"':
            doublequote(br);
            break;

          // Semicolon for comments (till end of line).
          // Accept all comment chars other than cr, lf and eof
          case ';':
            semicolon(br);
            break;
          
          // Default char in code
          default:
            sb.append(chr);
            linepos++;
            break;
        }
      }
    }
    catch (FileNotFoundException fnfe) {
      System.err.println(
          "Could not find the assembler file " + Common.getFilename() + "\n" +
          "Are file name and path correct?\n" +
          "--> exit application"
        );
        System.exit(-1);
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }      
    finally {
      try {
        if (br != null)   br.close();
//      if (fstream != null)   fstream.close();    TODO: not used any more
      }
      catch (IOException ioEx) {
        ioEx.printStackTrace();
      }
    }

    // Just in case the file doesn't end with a newline
    if (sb.length() != 0 && sb.charAt(sb.length() - 1) != Common.NL.charAt(0)) {
      addTokenEntries();
      // Add a newline on same line
      sb = new StringBuilder();
      sb.append(Common.NL);
      linepos++;
      addTokenEntries();
    }
    
    // Make result known to Common
    Common.setTokenList(tokenList);
  }

  
  /**
   * Treat a carriage return.
   * Adjust line position and line number appropriately.
   */
  private void carriageReturn() {
    if (sb.length() != 0) {
      addTokenEntries();
    }
    sb = new StringBuilder();
    sb.append(Common.NL);   // newline symbol
    linepos++;
    addTokenEntries();
    sb = new StringBuilder();
    linenum++;
    linepos = 1;
  }
  
  
  /**
   * Treat a blank.
   * Adjust line position and line number appropriately.
   */
  private void space() {
    if (sb.length() != 0) {
      addTokenEntries();
    }
    linepos++;
    sb = new StringBuilder();
  }
  
  
  /**
   * Treat a colon.
   * Adjust line position and line number appropriately.
   */
  private void colon() {
    sb.append(":");
    linepos++;
    addTokenEntries();
    sb = new StringBuilder();
  }
  
  
  /**
   * Treat a comma, an opening or a closing bracket.
   * Adjust line position and line number appropriately.
   * 
   * @param chr character comma, left or right bracket.
   */
  private void commaBrackets(char chr) {
    if (sb.length() != 0) {
      addTokenEntries();
    }
    sb = new StringBuilder();
    sb.append(chr);
    linepos++;
    addTokenEntries();
    sb = new StringBuilder();
  }
  
  
  /**
   * Treat a quote, char(s) and endquote.
   * Adjust line position and line number appropriately.
   * 
   * @param br The buffered reader to read the chars.
   * @throws IOException
   */
  private void quote(BufferedReader br) throws IOException {
    if (sb.length() != 0) {
      addTokenEntries();
    }
    sb.append('\'');
    linepos++;
    // Exit flag for inner loop: DB-char
    boolean myloop = true;
    int r;
    // Instead of a single DB-char there might be several ones
    // which is an error to be detected by the syntax checker
    while (myloop && (r = br.read()) != -1) {
      char c = (char)r;
      switch (c) {
      // cr in DB-char: forgot closing '
      case 0x0D:
        if (sb.length() != 0) {
          addTokenEntries();
        }
        sb = new StringBuilder();
        sb.append(Common.NL);
        linepos++;
        addTokenEntries();
        sb = new StringBuilder();
        linenum++;
        linepos = 1;
        myloop = false;
        break;
      // Ignore possible linefeed
      case 0x0A:
        break;
      // ' closing DB-char
      case '\'':
        sb.append('\'');
        linepos++;
        addTokenEntries();
        sb = new StringBuilder();
        myloop = false;
        break;
      // chars in DB-char
      default:
        sb.append(c);
        linepos++;
        break;
      }
    }
  }
  
  
  /**
   * Treat a doublequote, char(s) and enddoublequote.
   * Adjust line position and line number appropriately.
   * 
   * @param br The buffered reader to read the chars.
   * @throws IOException
   */
  private void doublequote(BufferedReader br) throws IOException {
    if (sb.length() != 0) {
      addTokenEntries();
    }
    sb.append('"');
    linepos++;
    // Exit flag for inner loop: DB-string
    boolean yourloop = true;
    int r;
    while (yourloop && (r = br.read()) != -1) {
      char c = (char)r;
      switch (c) {
      // cr in string: forgot closing "
      case 0x0D:
        addTokenEntries();
        sb = new StringBuilder();
        sb.append(Common.NL);
        linepos++;
        addTokenEntries();
        sb = new StringBuilder();
        linenum++;
        linepos = 1;
        yourloop = false;
        break;
      // Ignore possible linefeed
      case 0x0A:
        break;
      // " closing string
      case '"':
        sb.append('"');
        linepos++;
        addTokenEntries();
        sb = new StringBuilder();
        yourloop = false;
        break;
      // Chars in string
      default:
        sb.append(c);
        linepos++;
        break;
      }
    }
  }
  
  
  /**
   * Treat a semicolon.
   * Adjust line position and line number appropriately.
   * 
   * @param br The buffered reader to read the chars.
   * @throws IOException
   */
  private void semicolon(BufferedReader br) throws IOException {
    if (sb.length() != 0) {
      addTokenEntries();
    }
    sb = new StringBuilder();
    sb.append(";");
    linepos++;
    
    // Exit flag for inner loop: comments
    boolean loop = true;
    int r;
    while ((r = br.read()) != -1) {
      char c = (char) r;
      // If eof reached then exit from inner loop
      if (!loop) break;
      
      switch (c) {
      // cr in comment
      case 0x0D:
        addTokenEntries();
        
        sb = new StringBuilder();
        sb.append(Common.NL);
        linepos++;
        addTokenEntries();
        sb = new StringBuilder();
        linenum++;
        linepos = 1;
        loop = false;
        break;
      // lf in comment, ignore it
      case 0x0A:
        loop = false;
        break;
      // chars in comment
      default:
        sb.append(c);
        linepos++;
        break;
      }
    }
  }
  
  
  /**
   * Add the line number (uppermost line in file designated with line 1),
   * add start position of the token (leftmost position designated wit line 1)
   * and add token into the list. Each token needs therefore 3 list entries.
   */
  private void addTokenEntries() {
    // Add line number of the token
    tokenList.add(String.valueOf(linenum));
    // Add line position, i.e. start address of the token
    tokenList.add(String.valueOf(linepos - sb.length()));
    // Add token itself
    tokenList.add(sb.toString());
  }
  
  
  /**
   * Return a string representation of the token list.
   */
  public String toString() {
    return tokenList.toString();
  }
}
