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
    boolean toFile = false;
    boolean toExec = false;
    String output = "";

    if (args.length < 1 || args.length > 4) {
      System.out.println("Usage: java -jar Part3.jar input.sf --option [-o [output.ll]] [-exec]");
    }

    //Verbose option
    if (args.length > 1 && args[1].equals("-o")) {
      toFile = true;
      if (args.length > 2 && !(args[2].equals("-exec"))) {
        output += args[2];
        if (args.length > 3 && !(args[3].equals("-exec"))) {
          toExec = true;
        }
      } else if (args.length > 2 && args[2].equals("-exec")) {
        toExec = true;
      }
    }
    startCompilation(args[0], toFile, toExec, output);
  }

  private static void startCompilation(String filePath, boolean toFile, boolean toExec, String output) {
    try {
      Parser parser = new Parser(new BufferedReader(new FileReader(filePath)));
      AbstractSyntaxTree ast = parser.startParse();
      String tree_string = ast.print_tree();
      System.out.println(tree_string);
      CodeGenerator generator = new CodeGenerator(ast);
      String llvmCode = generator.generateLLVM();
      System.out.println(llvmCode);
      if (toFile) {
        generator.writeToFile(llvmCode, output);
      }
      if (toExec) {
        if (output.isEmpty()) {
          String llFileName = ast.getLabel().toLowerCase() + ".ll";
          String bcFileName = ast.getLabel().toLowerCase() + ".bc";
          ProcessBuilder pb = new ProcessBuilder("llvm-as", llFileName, "-o", bcFileName);
          pb.inheritIO();
          pb.start().waitFor();
          ProcessBuilder pb2 = new ProcessBuilder("lli", bcFileName);
          pb2.inheritIO();
          pb2.start().waitFor();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Failed to compile " + filePath);
    }
  }

}
