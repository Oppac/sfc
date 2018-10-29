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
  private static final int HPEXPR_EPSILON = 15;
  private static final int LPEXPR = 16;
  private static final int LPEXPR_EPSILON = 16;
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
  private static final int IFELSE_EPSILON = 26;
  private static final int COND = 27;
  private static final int PCOND = 28;
  private static final int HPCOND_AND = 29;
  private static final int HPCOND_EPSILON = 29;
  private static final int LPCOND_OR = 30;
  private static final int LPCOND_EPSILON = 30;
  private static final int SIMPLECOND_NOT = 31;
  private static final int SIMPLECOND_COMP = 32;
  private static final int COMP_EQ = 33;
  private static final int COMP_GEQ = 34;
  private static final int COMP_GT = 35;
  private static final int COMP_LEQ = 36;
  private static final int COMP_LT = 37;
  private static final int COMP_NEQ = 38;
  private static final int WHILE = 39;
  private static final int FOR = 40;
  private static final int PRINT = 41;
  private static final int READ = 42;
  private static final int EXPLIST = 43;
  private static final int EXPLISTEND = 44;


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
    nextToken();
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
      skipEndline();
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
     return;
   }

  }

  private void code() throws IOException {
    switch(lookahead.getType()) {
      case VARNAME:
      case IF:
      case WHILE:
      case FOR:
      case PRINT:
      case READ:
        System.out.print("CODE" + " ");
        instruction();
        if (lookahead.getType().equals(LexicalUnit.ENDLINE)) {
          nextToken();
          code();
        }
        break;
      default:
        nextToken();
        return;
    }
  }

  private void instruction() throws IOException {
    switch(lookahead.getType()) {
      case VARNAME:
        System.out.print("INSTRUCTION_ASSIGN" + " ");
        assign();
        break;
      case IF:
        System.out.print("INSTRUCTION_IF" + " ");
        parse_if();
        break;
      case WHILE:
        System.out.print("INSTRUCTION_WHILE" + " ");
        parse_while();
        break;
      case FOR:
        System.out.print("INSTRUCTION_FOR" + " ");
        parse_for();
        break;
      case PRINT:
        System.out.print("INSTRUCTION_PRINT" + " ");
        parse_print();
        break;
      case READ:
        System.out.print("INSTRUCTION_READ" + " ");
        parse_read();
        break;
    }
  }

  private void assign() throws IOException {
    System.out.print("ASSIGN" + " ");
    compareToken(LexicalUnit.VARNAME);
    nextToken();
    compareToken(LexicalUnit.ASSIGN);
    nextToken();
    exprArith();
    nextToken();
    skipEndline();
    code();
  }

  private void parse_while() throws IOException {

  }

  private void parse_for() throws IOException {

  }

  private void parse_print() throws IOException {

  }

  private void parse_read() throws IOException {
    System.out.print("READ" + " ");
    compareToken(LexicalUnit.READ);
    nextToken();
    compareToken(LexicalUnit.LPAREN);
    nextToken();
    varlist();
    compareToken(LexicalUnit.RPAREN);
    nextToken();
    skipEndline();
    code();
  }

  private void exprArith() throws IOException {
    System.out.print("EXPRARITH" + " ");
    hpProd();
    lpExpr();
  }

  private void hpProd() throws IOException {
    System.out.print("HPPROD" + " ");
    simpleExpr();
    hpExpr();
  }

  private void hpExpr() throws IOException {
    if (lookahead.getType().equals(LexicalUnit.TIMES) ||
        lookahead.getType().equals(LexicalUnit.DIVIDE)) {
          System.out.print("HPEXPR" + " ");
          hpOp();
          simpleExpr();
          hpExpr();
    } else {
      System.out.print("HPEXPR_EPSILON" + " ");
      return;
    }
  }

  private void lpExpr() throws IOException {
    if (lookahead.getType().equals(LexicalUnit.PLUS) ||
        lookahead.getType().equals(LexicalUnit.MINUS)) {
        lpOp();
        hpProd();
    } else {
      System.out.print("LPEXPR_EPSILON" + " ");
    }
  }

  private void simpleExpr() throws IOException {
    switch(lookahead.getType()) {
      case VARNAME:
        System.out.print("SIMPLEEXPR_VARNAME" + " ");
        nextToken();
        return;
      case NUMBER:
        System.out.print("SIMPLEEXPR_NUMBER" + " ");
        nextToken();
        return;
      case LPAREN:
        System.out.print("SIMPLEEXPR_PAREN" + " ");
        nextToken();
        exprArith();
        compareToken(LexicalUnit.RPAREN);
        nextToken();
        break;
      case MINUS:
        System.out.print("SIMPLEEXPR_MINUS" + " ");
        nextToken();
        exprArith();
        break;
    }
  }

  private void hpOp() throws IOException {
    if (lookahead.getType().equals(LexicalUnit.TIMES)) {
      System.out.print("HPOP_TIMES" + " ");
      nextToken();
    } else if (lookahead.getType().equals(LexicalUnit.DIVIDE)) {
      System.out.print("HPOP_DIVIDE" + " ");
      nextToken();
    }
  }

  private void lpOp() throws IOException {
    if (lookahead.getType().equals(LexicalUnit.PLUS)) {
      System.out.print("LPOP_PLUS" + " ");
      nextToken();
    } else if (lookahead.getType().equals(LexicalUnit.MINUS)) {
      System.out.print("LPOP_MINUS" + " ");
    }
  }

  private void parse_if() throws IOException {
    System.out.print("IF" + " ");
    compareToken(LexicalUnit.IF);
    nextToken();
    compareToken(LexicalUnit.LPAREN);
    nextToken();
    cond();
    compareToken(LexicalUnit.RPAREN);
    nextToken();
    compareToken(LexicalUnit.THEN);
    nextToken();
    code();
    ifElse();
    compareToken(LexicalUnit.ENDIF);
    nextToken();
    code();
  }

  private void ifElse() throws IOException {
    if (lookahead.getType().equals(LexicalUnit.ELSE)) {
      System.out.print("IFELSE" + " ");
      compareToken(LexicalUnit.ELSE);
      nextToken();
      compareToken(LexicalUnit.ENDLINE);
      nextToken();
      code();
    } else {
      System.out.print("IFELSE_EPSILON" + " ");
      return;
    }
  }

  private void cond() throws IOException {
    System.out.print("COND" + " ");
    pCond();
    lpCond();
  }

  private void pCond() throws IOException {
    System.out.print("PCOND" + " ");
    simpleCond();
    hpCond();
  }

  private void hpCond() throws IOException {
    if (lookahead.getType().equals(LexicalUnit.AND)) {
          System.out.print("HPCOND_AND" + " ");
          compareToken(LexicalUnit.AND);
          nextToken();
          simpleCond();
          hpCond();
    } else {
      System.out.print("HPCOND_EPSILON" + " ");
      return;
    }
  }

  private void lpCond() throws IOException {
    if (lookahead.getType().equals(LexicalUnit.OR)) {
      System.out.print("LPCOND_OR" + " ");
      compareToken(LexicalUnit.OR);
      nextToken();
      pCond();
      lpCond();
    } else {
      System.out.print("LPCOND_EPSILON" + " ");
    }
  }

  private void simpleCond() throws IOException {
    if (lookahead.getType().equals(LexicalUnit.NOT)) {
      System.out.print("SIMPLECOND_NOT");
      compareToken(LexicalUnit.NOT);
      nextToken();
      simpleCond();
    } else {
      exprArith();
      comp();
      exprArith();
    }
  }

  private void comp() throws IOException {
    switch(lookahead.getType()) {
      case EQ:
        System.out.print("COMP_EQ");
        compareToken(LexicalUnit.EQ);
        nextToken();
        break;
      case GEQ:
        System.out.print("COMP_GEQ");
        compareToken(LexicalUnit.GEQ);
        nextToken();
        break;
      case GT:
        System.out.print("COMP_GT");
        compareToken(LexicalUnit.GT);
        nextToken();
        break;
      case LEQ:
        System.out.print("COMP_LEQ");
        compareToken(LexicalUnit.LEQ);
        nextToken();
        break;
      case LT:
        System.out.print("COMP_LT");
        compareToken(LexicalUnit.LT);
        nextToken();
        break;
      case NEQ:
        System.out.print("COMP_NEQ");
        compareToken(LexicalUnit.NEQ);
        nextToken();
        break;
    }
  }


}
