import java.io.BufferedReader;
import java.io.FileReader;

public class Main {

  /**
  * Call the generated lexer and output the result of the computation on
  * the standard output. Print "Failed to compile" if something went wrong.
  *
  * @param args the path to the file to compile. Only one file allowed.
  *
  */
  public static void main(String[] args) {
    boolean verbose = false;
    if (args.length < 1) {
      System.out.println("Please enter a file to compile");
    } else if (args.length > 1 && args[1].equals("-v")) {
      verbose = true;
      startCompilation(args[0], verbose);
    } else {
      startCompilation(args[0], verbose);
    }
  }

  private static void startCompilation(String filePath, boolean verbose) {
    try {
      //Generated lexer
      Parser parser = new Parser(new BufferedReader(new FileReader(filePath)), verbose);
      parser.startParse();

    } catch (Exception e) {
      System.err.println("Failed to compile " + filePath);
    }
  }
}
