import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Parser {
  private Lexer scanner;
  private Symbol lookahead;
  public boolean verbose;
  public boolean tree;

  private static String PROGRAM;
  private static String EPSILON;
  private static String VARIABLES;
  private static String VARIABLES_EPSILON;
  private static String VARLIST;
  private static String VARLISTEND;
  private static String VARLISTEND_EPSILON;
  private static String CODE;
  private static String INSTRUCTION_ASSIGN;
  private static String INSTRUCTION_IF;
  private static String INSTRUCTION_WHILE;
  private static String INSTRUCTION_FOR;
  private static String INSTRUCTION_PRINT;
  private static String INSTRUCTION_READ;
  private static String ASSIGN;
  private static String EXPRARITH;
  private static String HPPROD;
  private static String HPEXPR;
  private static String HPEXPR_EPSILON;
  private static String LPEXPR;
  private static String LPEXPR_EPSILON;
  private static String SIMPLEEXPR_VARNAME;
  private static String SIMPLEEXPR_NUMBER;
  private static String SIMPLEEXPR_PAREN;
  private static String SIMPLEEXPR_MINUS;
  private static String LPOP_PLUS;
  private static String LPOP_MINUS;
  private static String HPOP_TIMES;
  private static String HPOP_DIVIDE;
  private static String IF;
  private static String IFELSE;
  private static String IFELSE_EPSILON;
  private static String COND;
  private static String PCOND;
  private static String HPCOND_AND;
  private static String HPCOND_EPSILON;
  private static String LPCOND_OR;
  private static String LPCOND_EPSILON;
  private static String SIMPLECOND_NOT;
  private static String SIMPLECOND_COMP;
  private static String COMP_EQ;
  private static String COMP_GEQ;
  private static String COMP_GT;
  private static String COMP_LEQ;
  private static String COMP_LT;
  private static String COMP_NEQ;
  private static String WHILE;
  private static String FOR;
  private static String PRINT;
  private static String READ;
  private static String EXPLIST;
  private static String EXPLISTEND;
  private static String EXPLISTEND_EPSILON;

  public Parser(BufferedReader filePath, boolean v, boolean tree) throws IOException {
    this.scanner = new Lexer(filePath);
    this.lookahead = scanner.yylex();
    this.verbose = v;
    this.tree = tree;

    if (verbose) {
      this.PROGRAM = " PROGRAM ";
      this.EPSILON = "EPSILON ";
      this.VARIABLES = "VARIABLES ";
      this.VARIABLES_EPSILON = "VARIABLES_EPSILON ";
      this.VARLIST = "VARLIST ";
      this.VARLISTEND = "VARLISTEND " ;
      this.VARLISTEND_EPSILON = "VARLISTEND_EPSILON ";
      this.CODE = "CODE ";
      this.INSTRUCTION_ASSIGN = "INSTRUCTION_ASSIGN ";
      this.INSTRUCTION_IF = "INSTRUCTION_IF ";
      this.INSTRUCTION_WHILE = "INSTRUCTION_WHILE ";
      this.INSTRUCTION_FOR = "INSTRUCTION_FOR ";
      this.INSTRUCTION_PRINT = "INSTRUCTION_PRINT ";
      this.INSTRUCTION_READ = "INSTRUCTION_READ ";
      this.ASSIGN = "ASSIGN ";
      this.EXPRARITH = "EXPRARITH ";
      this.HPPROD = "HPPROD ";
      this.HPEXPR = "HPEXPR ";
      this.HPEXPR_EPSILON = "HPEXPR_EPSILON ";
      this.LPEXPR = "LPEXPR ";
      this.LPEXPR_EPSILON = "LPEXPR_EPSILON ";
      this.SIMPLEEXPR_VARNAME = "SIMPLEEXPR_VARNAME ";
      this.SIMPLEEXPR_NUMBER = "SIMPLEEXPR_NUMBER ";
      this.SIMPLEEXPR_PAREN = "SIMPLEEXPR_PAREN ";
      this.SIMPLEEXPR_MINUS = "SIMPLEEXPR_MINUS ";
      this.LPOP_PLUS = "LPOP_PLUS ";
      this.LPOP_MINUS = "LPOP_MINUS ";
      this.HPOP_TIMES = "HPOP_TIMES ";
      this.HPOP_DIVIDE = "HPOP_DIVIDE ";
      this.IF = "IF ";
      this.IFELSE = "IFELSE ";
      this.IFELSE_EPSILON = "IFELSE_EPSILON ";
      this.COND = "COND ";
      this.PCOND = "PCOND ";
      this.HPCOND_AND = "HPCOND_AND ";
      this.HPCOND_EPSILON = "HPCOND_EPSILON ";
      this.LPCOND_OR = "LPCOND_OR ";
      this.LPCOND_EPSILON = "LPCOND_EPSILON ";
      this.SIMPLECOND_NOT = "SIMPLECOND_NOT ";
      this.SIMPLECOND_COMP = "SIMPLECOND_COMP ";
      this.COMP_EQ = "COMP_EQ ";
      this.COMP_GEQ = "COMP_GEQ ";
      this.COMP_GT = "COMP_GT ";
      this.COMP_LEQ = "COMP_LEQ ";
      this.COMP_LT = "COMP_LT ";
      this.COMP_NEQ = "COMP_NEQ ";
      this.WHILE = "WHILE ";
      this.FOR = "FOR ";
      this.PRINT = "PRINT ";
      this.READ = "READ ";
      this.EXPLIST = "EXPLIST ";
      this.EXPLISTEND = "EXPLISTEND ";
      this.EXPLISTEND_EPSILON = "EXPLISTEND_EPSILON ";
    } else {
      this.PROGRAM = " 1 ";
      this.EPSILON = "2 ";
      this.VARIABLES = "3 ";
      this.VARIABLES_EPSILON = "4 ";
      this.VARLIST = "5 ";
      this.VARLISTEND = "6 ";
      this.VARLISTEND_EPSILON = "7 ";
      this.CODE = "8 ";
      this.INSTRUCTION_ASSIGN = "9 ";
      this.INSTRUCTION_IF = "10 ";
      this.INSTRUCTION_WHILE = "11 ";
      this.INSTRUCTION_FOR = "12 ";
      this.INSTRUCTION_PRINT = "13 ";
      this.INSTRUCTION_READ = "14 ";
      this.ASSIGN = "15 ";
      this.EXPRARITH = "16 ";
      this.HPPROD = "17 ";
      this.HPEXPR = "18 ";
      this.HPEXPR_EPSILON = "19 ";
      this.LPEXPR = "20 ";
      this.LPEXPR_EPSILON = "21 ";
      this.SIMPLEEXPR_VARNAME = "22 ";
      this.SIMPLEEXPR_NUMBER = "23 ";
      this.SIMPLEEXPR_PAREN = "24 ";
      this.SIMPLEEXPR_MINUS = "25 ";
      this.LPOP_PLUS = "26 ";
      this.LPOP_MINUS = "27 ";
      this.HPOP_TIMES = "28 ";
      this.HPOP_DIVIDE = "29 ";
      this.IF = "30 ";
      this.IFELSE = "31 ";
      this.IFELSE_EPSILON = "32 ";
      this.COND = "33 ";
      this.PCOND = "34 ";
      this.HPCOND_AND = "35 ";
      this.HPCOND_EPSILON = "36 ";
      this.LPCOND_OR = "37 ";
      this.LPCOND_EPSILON = "38 ";
      this.SIMPLECOND_NOT = "39 ";
      this.SIMPLECOND_COMP = "40 ";
      this.COMP_EQ = "41 ";
      this.COMP_GEQ = "42 ";
      this.COMP_GT = "43 ";
      this.COMP_LEQ = "44 ";
      this.COMP_LT = "45 ";
      this.COMP_NEQ = "46 ";
      this.WHILE = "47 ";
      this.FOR = "48 ";
      this.PRINT = "49 ";
      this.READ = "50 ";
      this.EXPLIST = "51 ";
      this.EXPLISTEND = "52 ";
      this.EXPLISTEND_EPSILON = "53 ";
    }
  }

  private void nextToken() throws IOException {
    this.lookahead = scanner.yylex();
  }

  private void compareToken(LexicalUnit token) throws IOException {
    if (!(lookahead.getType().equals(token))){
      System.out.println("\nError " + lookahead.getType() + " != " + token);
    } else {
      //System.out.println("\nOk " + lookahead.getType() + " == " + token);
    }
    nextToken();
  }

  public void startParse() throws IOException {
    program();
  }

  private void skipEndline() throws IOException {
    while (lookahead.getType().equals(LexicalUnit.ENDLINE)) {
        nextToken();
        //System.out.println("Skip");
    }
  }

  private void program() throws IOException {
    skipEndline();
    System.out.print(PROGRAM);
    compareToken(LexicalUnit.BEGINPROG);
    compareToken(LexicalUnit.PROGNAME);
    compareToken(LexicalUnit.ENDLINE);
    skipEndline();
    variables();
    skipEndline();
    code();
    skipEndline();
    compareToken(LexicalUnit.ENDPROG);
    skipEndline();
    compareToken(LexicalUnit.EOS);
  }

  private void variables() throws IOException {
    if (lookahead.getType().equals(LexicalUnit.VARIABLES)) {
      System.out.print(VARIABLES);
      compareToken(LexicalUnit.VARIABLES);
      varlist();
    } else {
      System.out.print(VARIABLES_EPSILON);
    }
  }

  private void varlist() throws IOException {
    System.out.print(VARLIST);
    compareToken(LexicalUnit.VARNAME);
    varlistend();
  }

  private void varlistend() throws IOException {
    if (lookahead.getType().equals(LexicalUnit.COMMA)) {
      System.out.print(VARLISTEND);
      compareToken(LexicalUnit.COMMA);
      compareToken(LexicalUnit.VARNAME);
      varlistend();
    } else {
     System.out.print(VARLISTEND_EPSILON);
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
        System.out.print(CODE);
        instruction();
        if (lookahead.getType().equals(LexicalUnit.ENDLINE)) {
          skipEndline();
          code();
        }
        break;
    }
  }

  private void instruction() throws IOException {
    switch(lookahead.getType()) {
      case VARNAME:
        System.out.print(INSTRUCTION_ASSIGN);
        assign();
        break;
      case IF:
        System.out.print(INSTRUCTION_IF);
        parse_if();
        break;
      case WHILE:
        System.out.print(INSTRUCTION_WHILE);
        parse_while();
        break;
      case FOR:
        System.out.print(INSTRUCTION_FOR);
        parse_for();
        break;
      case PRINT:
        System.out.print(INSTRUCTION_PRINT);
        parse_print();
        break;
      case READ:
        System.out.print(INSTRUCTION_READ);
        parse_read();
        break;
    }
  }

  private void assign() throws IOException {
    System.out.print(ASSIGN);
    compareToken(LexicalUnit.VARNAME);
    compareToken(LexicalUnit.ASSIGN);
    exprArith();
  }

  private void exprArith() throws IOException {
    System.out.print(EXPRARITH);
    hpProd();
    lpExpr();
  }

  private void hpProd() throws IOException {
    System.out.print(HPPROD);
    simpleExpr();
    hpExpr();
  }

  private void hpExpr() throws IOException {
    if (lookahead.getType().equals(LexicalUnit.TIMES) ||
        lookahead.getType().equals(LexicalUnit.DIVIDE)) {
          System.out.print(HPEXPR);
          hpOp();
          simpleExpr();
          hpExpr();
    } else {
      System.out.print(HPEXPR_EPSILON);
    }
  }

  private void lpExpr() throws IOException {
    if (lookahead.getType().equals(LexicalUnit.PLUS) ||
        lookahead.getType().equals(LexicalUnit.MINUS)) {
        System.out.print(LPEXPR);
        lpOp();
        hpProd();
    } else {
      System.out.print(LPEXPR_EPSILON);
    }
  }

  private void simpleExpr() throws IOException {
    switch(lookahead.getType()) {
      case VARNAME:
        System.out.print(SIMPLEEXPR_VARNAME);
        nextToken();
        break;
      case NUMBER:
        System.out.print(SIMPLEEXPR_NUMBER);
        nextToken();
        break;
      case LPAREN:
        System.out.print(SIMPLEEXPR_PAREN);
        nextToken();
        exprArith();
        compareToken(LexicalUnit.RPAREN);
        break;
      case MINUS:
        System.out.print(SIMPLEEXPR_MINUS);
        nextToken();
        exprArith();
        break;
    }
  }

  private void hpOp() throws IOException {
    if (lookahead.getType().equals(LexicalUnit.TIMES)) {
      System.out.print(HPOP_TIMES);
      nextToken();
    } else if (lookahead.getType().equals(LexicalUnit.DIVIDE)) {
      System.out.print(HPOP_DIVIDE);
      nextToken();
    }
  }

  private void lpOp() throws IOException {
    if (lookahead.getType().equals(LexicalUnit.PLUS)) {
      System.out.print(LPOP_PLUS);
      nextToken();
    } else if (lookahead.getType().equals(LexicalUnit.MINUS)) {
      System.out.print(LPOP_MINUS);
    }
  }

  private void parse_if() throws IOException {
    System.out.print(IF);
    compareToken(LexicalUnit.IF);
    compareToken(LexicalUnit.LPAREN);
    cond();
    compareToken(LexicalUnit.RPAREN);
    compareToken(LexicalUnit.THEN);
    skipEndline();
    code();
    ifElse();
    compareToken(LexicalUnit.ENDIF);
  }

  private void ifElse() throws IOException {
    if (lookahead.getType().equals(LexicalUnit.ELSE)) {
      System.out.print(IFELSE);
      compareToken(LexicalUnit.ELSE);
      compareToken(LexicalUnit.ENDLINE);
      code();
    } else {
      System.out.print(IFELSE_EPSILON);
    }
  }

  private void cond() throws IOException {
    System.out.print(COND);
    pCond();
    lpCond();
  }

  private void pCond() throws IOException {
    System.out.print(PCOND);
    simpleCond();
    hpCond();
  }

  private void hpCond() throws IOException {
    if (lookahead.getType().equals(LexicalUnit.AND)) {
          System.out.print(HPCOND_AND);
          compareToken(LexicalUnit.AND);
          simpleCond();
          hpCond();
    } else {
      System.out.print(HPCOND_EPSILON);
    }
  }

  private void lpCond() throws IOException {
    if (lookahead.getType().equals(LexicalUnit.OR)) {
      System.out.print(LPCOND_OR);
      compareToken(LexicalUnit.OR);
      pCond();
      lpCond();
    } else {
      System.out.print(LPCOND_EPSILON);
    }
  }

  private void simpleCond() throws IOException {
    if (lookahead.getType().equals(LexicalUnit.NOT)) {
      System.out.print(SIMPLECOND_NOT);
      compareToken(LexicalUnit.NOT);
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
        System.out.print(COMP_EQ);
        compareToken(LexicalUnit.EQ);
        break;
      case GEQ:
        System.out.print(COMP_GEQ);
        compareToken(LexicalUnit.GEQ);
        break;
      case GT:
        System.out.print(COMP_GT);
        compareToken(LexicalUnit.GT);
        break;
      case LEQ:
        System.out.print(COMP_LEQ);
        compareToken(LexicalUnit.LEQ);
        break;
      case LT:
        System.out.print(COMP_LT);
        compareToken(LexicalUnit.LT);
        break;
      case NEQ:
        System.out.print(COMP_NEQ);
        compareToken(LexicalUnit.NEQ);
        break;
    }
  }

  private void parse_while() throws IOException {
    System.out.print(WHILE);
    compareToken(LexicalUnit.WHILE);
    cond();
    compareToken(LexicalUnit.DO);
    skipEndline();
    code();
    compareToken(LexicalUnit.ENDWHILE);
  }

  private void parse_for() throws IOException {
    System.out.print(FOR);
    compareToken(LexicalUnit.FOR);
    compareToken(LexicalUnit.VARNAME);
    compareToken(LexicalUnit.ASSIGN);
    exprArith();
    compareToken(LexicalUnit.TO);
    exprArith();
    compareToken(LexicalUnit.DO);
    skipEndline();
    code();
    compareToken(LexicalUnit.ENDFOR);
  }

  private void parse_print() throws IOException {
    System.out.print(PRINT);
    compareToken(LexicalUnit.PRINT);
    compareToken(LexicalUnit.LPAREN);
    exprList();
    compareToken(LexicalUnit.RPAREN);
  }

  private void parse_read() throws IOException {
    System.out.print(READ);
    compareToken(LexicalUnit.READ);
    compareToken(LexicalUnit.LPAREN);
    varlist();
    compareToken(LexicalUnit.RPAREN);
  }

  private void exprList() throws IOException {
    System.out.print(EXPLIST);
    exprArith();
    expListEnd();
  }

  private void expListEnd() throws IOException {
    if (lookahead.getType().equals(LexicalUnit.COMMA)) {
      System.out.print(EXPLISTEND);
      compareToken(LexicalUnit.COMMA);
      exprList();
    } else {
      System.out.print(EXPLISTEND_EPSILON);
    }
  }

}
