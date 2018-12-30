import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;

/** Main class that is used to launch the compilation.
* Main function that fetch the relevant parameters on the
* standard input and start the compilation.
* It outputs the IR code to the console and if asked, generate a .ll file and
* execute it.
* Give an error message if the input file was not found or the compilation failed.
* Options -o: generate the IR code in a .ll file having the same name as the .sf file.
* Option -o output.ll : generate the IR code in the specified file.
* Option -o [output.ll] -exec: execute the .sf program after compilation
*
* @param args the arguments given to the compiler
*/

public class Main {

  public static void main(String[] args) {
    boolean toFile = false;
    boolean toExec = false;
    String output = "";

    if (args.length < 1 || args.length > 4) {
      System.out.println("Usage: java -jar Part3.jar input.sf --option [-o [output.ll] [-exec]]");
    }

    if (args.length > 1 && args[1].equals("-o")) {
      toFile = true;
      if (args.length > 2 && !(args[2].equals("-exec"))) {
        output += args[2];
        if (args.length > 3 && (args[3].equals("-exec"))) {
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
      //System.out.println(ast.printTree());
      CodeGenerator generator = new CodeGenerator(ast);
      String llvmCode = generator.generateLLVM();
      System.out.println(llvmCode);

      if (toFile) {
        generator.writeToFile(llvmCode, output);
      }

      try {
        if (toExec) {
          String llFileName, bcFileName;
          if (output.isEmpty()) {
            llFileName = ast.getLabel().toLowerCase() + ".ll";
            bcFileName = ast.getLabel().toLowerCase() + ".bc";
          } else {
            llFileName = output;
            bcFileName = output.replace(".ll", ".bc");
          }
          ProcessBuilder pb = new ProcessBuilder("llvm-as", llFileName, "-o", bcFileName);
          pb.inheritIO();
          pb.start().waitFor();
          ProcessBuilder pb2 = new ProcessBuilder("lli", bcFileName);
          pb2.inheritIO();
          pb2.start().waitFor();
        }
      } catch (Exception e) {
        System.err.println("Failed to execute the llvm file");
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Failed to compile " + filePath);
    }
  }

}
