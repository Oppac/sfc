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
    boolean tree = false;

    if (args.length < 1 || args.length > 4) {
      System.out.println("Usage: java -jar Part2.jar input.sf -v -wt output.tex");
    }

    if (args.length > 1 && args[1].equals("-v")) {
      verbose = true;
    }

    if (args.length > 1 && args[1].equals("-wt")) {
      try {
        drawTree(args[2]);
      } catch (Exception e) {
        System.out.println("Please specify a lex file to draw the tree");
      }
    } else if (args.length > 2 && args[2].equals("-wt")) {
      try {
        drawTree(args[3]);
      } catch (Exception e) {
        System.out.println("Please specify a lex file to draw the tree");
      }
    }

    startCompilation(args[0], verbose);
  }

  private static void startCompilation(String filePath, boolean verbose) {
    try {
      Parser parser = new Parser(new BufferedReader(new FileReader(filePath)), verbose);
      parser.startParse();
    } catch (Exception e) {
      System.err.println("Failed to compile " + filePath);
    }
  }

  private static void drawTree(String filePath) {
    try {
      System.out.println("Drawing tree is not yet implemented");
    } catch (Exception e) {
      System.err.println("Failed to draw tree");
    }
  }

}
