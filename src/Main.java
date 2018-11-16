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
      System.out.println("Usage: java -jar Part2.jar (-v) (-wt output.tex) input.sf");
    }

    if (args.length > 1 && args[0].equals("-v")) {
      verbose = true;
    }

    if (args.length > 1 && args[0].equals("-wt")) {
      try {
        tree = true;
        drawTree(args[1]);
      } catch (Exception e) {
        System.out.println("Please specify a lex file to draw the tree");
      }
    } else if (args.length > 1 && args[1].equals("-wt")) {
      if (args[0].equals("-v")) {
        verbose = true;
      } else {
        System.out.println("Usage: java -jar Part2.jar (-v) (-wt output.tex) input.sf");
      }
      try {
        tree = true;
        drawTree(args[2]);
      } catch (Exception e) {
        System.out.println("Please specify a lex file to draw the tree");
      }
    }
    startCompilation(args[(args.length)-1], verbose, tree);
  }

  private static void startCompilation(String filePath, boolean verbose, boolean tree) {
    try {
      Parser parser = new Parser(new BufferedReader(new FileReader(filePath)), verbose, tree);
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
