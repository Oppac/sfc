import java.io.BufferedReader;
import java.io.FileReader;

public class Main {

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
      Lexer scanner = new Lexer(new BufferedReader(new FileReader(filePath)));
      scanner.yylex();
    } catch (Exception e) {
      System.err.println("Failed to compile " + filePath);
    }
  }
}
