import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class Main {

  /**
  * Main function that fetch the relevant parameters on the
  * standard input and start the compilation.
  * Give an error message if the input file was not found or the compilation failed.
  * Options verbose: -v : give the name of grammar rule instead of its number
  * Options write tree: -wt output_file.tex : create a latex file that contain the parse tree
  *
  * @param args the arguments given to the parser
  */
  public static void main(String[] args) {
    boolean verbose = false;
    boolean tree = false;
    String output = "None";

    if (args.length < 1 || args.length > 4) {
      System.out.println("Usage: java -jar Part2.jar (-v) (-wt output.tex) input.sf");
    }

    //Verbose option
    if (args.length > 1 && args[0].equals("-v")) {
      verbose = true;
    }

    //Write tree option
    if (args.length > 1 && args[0].equals("-wt")) {
      try {
        tree = true;
        output = args[1];
      } catch (Exception e) {
        System.out.println("Please specify a lex file to draw the tree");
      }
    //Write tree and verbose
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
      //If write tree is active, get the ParseTree from the parser and write it at the specified output
      if (tree) {
        ParseTree parserTree = parser.startParse();
        try {
          BufferedWriter outputFile = new BufferedWriter(new FileWriter(output));
          outputFile.write(parserTree.toLaTeX());
          outputFile.close();
        } catch (Exception e) {
          System.err.println("Failed to draw tree");
        }
      //Otherwise, do the parse without collecting the ParseTree
      } else {
        parser.startParse();
      }
    } catch (Exception e) {
      System.err.println("Failed to compile " + filePath);
    }
  }

}
