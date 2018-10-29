import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Parser {
  private Lexer scanner;
  private Symbol lookahead;

  private static final int PROGRAM = 1;
  private static final int VARIABLES = 2;
  private static final int VARIABLES_EPSILON = 3;
  private static final int VARLIST = 3;
  private static final int VARLISTEND = 4;
  private static final int VARLISTEND_EPSILON = 4;
  private static final int CODE = 5;
  private static final int INSTRUCTION_ASSIGN = 6;
  private static final int INSTRUCTION_IF = 7;
  private static final int INSTRUCTION_WHILE = 8;
  private static final int INSTRUCTION_FOR = 9;
  private static final int INSTRUCTION_PRINT = 10;
  private static final int INSTRUCTION_READ = 11;
  private static final int ASSIGN = 12;
  private static final int EXPRARITH = 13;
  private static final int HPPROD = 14;
  private static final int HPEXPR = 15;
  private static final int LPEXPR = 16;
  private static final int SIMPLEEXPR_VARNAME = 17;
  private static final int SIMPLEEXPR_NUMBER = 18;
  private static final int SIMPLEEXPR_PAREN = 19;
  private static final int SIMPLEEXPRT_MINUS = 20;
  private static final int LPOP_PLUS = 21;
  private static final int LPOP_MINUS = 22;
  private static final int HPOP_TIMES = 23;
  private static final int HPOP_DIVIDE = 24;
  private static final int IF = 25;
  private static final int IFELSE = 26;
  private static final int COND = 27;
  private static final int PCOND = 28;
  private static final int HPCOND = 29;
  private static final int LPCOND = 30;
  private static final int SIMPLECOND_NOT = 31;
  private static final int SIMPLECOND_COMP = 32;
  private static final int COMP_EQ = 33;
  private static final int COMP_GEQ = 34;
  private static final int COMP_GT = 35;
  private static final int COMP_LEQ = 36;
  private static final int COMP_LT = 37;
  private static final int COMP_NEQ = 38;
  private static final int COMP_WHILE = 39;
  private static final int COMP_FOR = 40;
  private static final int COMP_PRINT = 41;
  private static final int COMP_READ = 42;
  private static final int COMP_EXPLIST = 43;
  private static final int COMP_EXPLISTEND = 44;


  public static boolean VERBOSE = false;

  public Parser(BufferedReader filePath) throws IOException {
    this.scanner = new Lexer(filePath);
    this.lookahead = scanner.yylex();
  }

  private void nextToken() throws IOException {
    this.lookahead = scanner.yylex();
  }

  private void compareToken(LexicalUnit token) {
    if (!(lookahead.getType().equals(token))){
      System.out.println("\nError " + lookahead.getType() + " != " + token);
    } else {
      System.out.println("\nOk " + lookahead.getType() + " == " + token);
    }
  }

  public void startParse() throws IOException {
    program();
  }

  private void skipEndline() throws IOException {
    while (lookahead.getType().equals(LexicalUnit.ENDLINE)) {
        nextToken();
        System.out.println("Skip");
    }
  }

  private void program() throws IOException {
    skipEndline();
    System.out.print(" " + "PROGRAM" + " ");
    compareToken(LexicalUnit.BEGINPROG);
    nextToken();
    compareToken(LexicalUnit.PROGNAME);
    nextToken();
    compareToken(LexicalUnit.ENDLINE);
    skipEndline();
    variables();
    skipEndline();
    code();
    //compareToken(LexicalUnit.ENDPROG);
    //nextToken();
    //compareToken(LexicalUnit.EOS);
  }

  private void variables() throws IOException {
    if (lookahead.getType().equals(LexicalUnit.VARIABLES)) {
      System.out.print("VARIABLES" + " ");
      compareToken(LexicalUnit.VARIABLES);
      nextToken();
      varlist();
      compareToken(LexicalUnit.ENDLINE);
    } else {
      System.out.print("VARIABLES_EPSILON" + " ");
      nextToken();
      return;
    }
  }

  private void varlist() throws IOException {
    System.out.print("VARLIST" + " ");
    compareToken(LexicalUnit.VARNAME);
    nextToken();
    varlistend();
  }

  private void varlistend() throws IOException {
    if (lookahead.getType().equals(LexicalUnit.COMMA)) {
      System.out.print("VARLISTEND" + " ");
      compareToken(LexicalUnit.COMMA);
      nextToken();
      compareToken(LexicalUnit.VARNAME);
      varlistend();
    } else {
     System.out.print("VARLISTEND_EPSILON" + " ");
     nextToken();
     return;
   }

  }

  private void code() throws IOException {
    System.out.print("Hello");
    nextToken();
  }


}
