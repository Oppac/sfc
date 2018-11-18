import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;

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
    String output = "None";

    if (args.length < 1 || args.length > 4) {
      System.out.println("Usage: java -jar Part2.jar (-v) (-wt output.tex) input.sf");
    }

    if (args.length > 1 && args[0].equals("-v")) {
      verbose = true;
    }

    if (args.length > 1 && args[0].equals("-wt")) {
      try {
        tree = true;
        output = args[1];
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
        output = args[2];
      } catch (Exception e) {
        System.out.println("Please specify a lex file to draw the tree");
      }
    }
  startCompilation(args[(args.length)-1], verbose, tree, output);
  }

  private static void startCompilation(String filePath, boolean verbose, boolean tree, String output) {
    try {
      Parser parser = new Parser(new BufferedReader(new FileReader(filePath)), verbose, tree);
      if (tree) {
        ParseTree parserTree = parser.startParse();
        try {
          BufferedWriter outputFile = new BufferedWriter(new FileWriter(output));
          outputFile.write(parserTree.toLaTeX());
          outputFile.close();
        } catch (Exception e) {
          System.err.println("Failed to draw tree");
        }
      } else {
        parser.startParse();
      }
    } catch (Exception e) {
      System.err.println("Failed to compile " + filePath);
    }
  }

}
