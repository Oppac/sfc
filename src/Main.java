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
    if (args.length < 1) {
      System.out.println("Please enter a file to compile");
    } else if (args.length > 1) {
      System.out.println("Please enter only one file");
    } else {
      for (String filePath : args) {
        startCompilation(filePath);
      }
    }
  }

  private static void startCompilation(String filePath) {
    try {
      //Generated lexer
      Lexer scanner = new Lexer(new BufferedReader(new FileReader(filePath)));
      //Output the result of the compilation
      scanner.yylex();
    } catch (Exception e) {
      System.err.println("Failed to compile " + filePath);
    }
  }
}
