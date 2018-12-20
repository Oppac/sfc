import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Parser {
  private Lexer scanner;
  private Symbol lookahead;

  /**
  * Constructor for the Parser.
  * Set up a scanner and assigns a value to each rules of
  * the grammar. It is either the number of the rule in the grammar or
  * it's name if verbose is active.
  * @param filePath path to the file to parse
  * @param v tell if the verbose option is active or not
  * @param t tell if the option for drawing the tree is active or not
  */

  public Parser(BufferedReader filePath, boolean v, boolean t) throws IOException {
    this.scanner = new Lexer(filePath);
    this.lookahead = scanner.yylex();
  }

  /*
  * Fetch the next token to parse.
  */
  private void nextToken() throws IOException {
    this.lookahead = scanner.yylex();
  }

  /**
  * Compare the expected token to the current token.
  * @param token the expected token.
  * @return if the tokens match, return a AbstractSyntaxTree (either null or with
  * the proper label if draw tree is active).
  * @throws IOException give an error if the tokens do not match.
  */
  private AbstractSyntaxTree compareToken(LexicalUnit token) throws IOException {
    if (!(lookahead.getType().equals(token))){
      throw new Error("\nError at line " + lookahead.getLine() + ": " +
      lookahead.getType() + " expected " + token);
    }
    nextToken();
    return new AbstractSyntaxTree();
  }

  private AbstractSyntaxTree compareTokenAdd(LexicalUnit token) throws IOException {
    if (!(lookahead.getType().equals(token))){
      throw new Error("\nError at line " + lookahead.getLine() + ": " +
      lookahead.getType() + " expected " + token);
    }
    String label = lookahead.getValue().toString();
    nextToken();
    return new AbstractSyntaxTree(label);
  }

  /**
  * Start the parsing of the input file at the initial symbol of the grammar.
  * @return if the tree drawing is not active the parser return an empty AbstractSyntaxTree. If
  * the execution was without errors the proper rules numbers/names have been written
  * on the standard output. We don't need retrieve the tree in this case so all the nodes are null.
  * @return if the tree drawing option was active, the parser return a AbstractSyntaxTree
  * containing the nodes with their labels and children. The ParsTree can then be
  * retrieve by Main in order to write the tree on the specified file.
  */
  public AbstractSyntaxTree startParse() throws IOException {
    return program();
  }

  /**
  * We allowed the input program to have as many endlines has it want at some specific
  * points in the program. They are ignored by the "standard" parser and return a node
  * called "SkipLines" for the AbstractSyntaxTree. It allow to see in the tree where extra
  * endlines are.
  * @return a "SkipLines" node for the AbstractSyntaxTree.
  */
  private void skipEndline() throws IOException {
    while (lookahead.getType().equals(LexicalUnit.ENDLINE)) {
      nextToken();
    }
  }

  /**
  * The first step of the parsing. The function has two "modes". One where the option to
  * draw the tree is inactive and the other one where it is active. In the "inactive mode"
  * the parser only check that the input is correct. It returns a null node as the
  * AbstractSyntaxTree will be discarded at the end anyway. In the "active mode", a new AbstractSyntaxTree
  * following the rules defined in AbstractSyntaxTree.java is returned instead. In both case
  * the rule number/name is printed on the standard output.
  * The other functions will not be detailed but they all follow the same model.
  * @return a AbstractSyntaxTree either correct or null depending on the selected option.
  */
  //[01] <Program> -> BEGINPROG [ProgName] [EndLine] <Variables> <Code> ENDPROG
  private AbstractSyntaxTree program() throws IOException {
    AbstractSyntaxTree ast = new AbstractSyntaxTree("Program");
    skipEndline();
    compareToken(LexicalUnit.BEGINPROG);
    compareToken(LexicalUnit.PROGNAME);
    compareToken(LexicalUnit.ENDLINE);
    skipEndline();
    ast.add_child(variables());
    skipEndline();
    ast.add_child(code());
    skipEndline();
    compareToken(LexicalUnit.ENDPROG);
    skipEndline();
    compareToken(LexicalUnit.EOS);
    return ast;
  }

  //[02] <Variables> -> VARIABLES <VarList> [EndLine]
  //[03] <Variables> -> EPSILON
  private AbstractSyntaxTree variables() throws IOException {
    if (lookahead.getType().equals(LexicalUnit.VARIABLES)) {
      compareToken(LexicalUnit.VARIABLES);
      List<AbstractSyntaxTree> var = varlist();
      return new AbstractSyntaxTree("Variables", var);
    } else {
      return new AbstractSyntaxTree();
    }
  }

  //[04] <VarList> -> [VarName] <VarListEnd>
  private List<AbstractSyntaxTree> varlist() throws IOException {
    List<AbstractSyntaxTree> var = new ArrayList<AbstractSyntaxTree>();
    var.add(compareTokenAdd(LexicalUnit.VARNAME));
    var.addAll(varlistend());
    return var;
  }

  //[05] <VarListEnd> -> COMMA <VarList>
  //[06] <VarListEnd> -> EPSILON
  private List<AbstractSyntaxTree> varlistend() throws IOException {
    if (lookahead.getType().equals(LexicalUnit.COMMA)) {
      List<AbstractSyntaxTree> var = new ArrayList<AbstractSyntaxTree>();
      compareToken(LexicalUnit.COMMA);
      var.add(compareTokenAdd(LexicalUnit.VARNAME));
      var.addAll(varlistend());
      return var;
    } else {
      return new ArrayList<AbstractSyntaxTree>();
    }
  }

  //[07] <Code> -> <Instruction> [EndLine] <Code>
  //[08] <Code> -> EPSILON
  private AbstractSyntaxTree code() throws IOException {
    AbstractSyntaxTree ast = new AbstractSyntaxTree("Code");
    switch(lookahead.getType()) {
      case VARNAME:
      case IF:
      case WHILE:
      case FOR:
      case PRINT:
      case READ:
        ast.add_child(instruction());
        skipEndline();
        ast.add_child(code());
        return ast;
      default:
        return ast;
    }
  }

  //[09] <Instruction> -> <Assign>
  //[10] <Instruction> -> <If>
  //[11] <Instruction> -> <While>
  //[12] <Instruction> -> <For>
  //[13] <Instruction> -> <Print>
  //[14] <Instruction> -> <Read>
  private AbstractSyntaxTree instruction() throws IOException {
    switch(lookahead.getType()) {
      case VARNAME:
        return new AbstractSyntaxTree("Assign", assign());
      case IF:
        return new AbstractSyntaxTree("If", parse_if());
      case WHILE:
        return new AbstractSyntaxTree("While", parse_while());
      case FOR:
        return new AbstractSyntaxTree("For", parse_for());
      case PRINT:
        return new AbstractSyntaxTree("Print", parse_print());
      case READ:
        return new AbstractSyntaxTree("Read", parse_read());
      default:
        return new AbstractSyntaxTree();
    }
  }

  //[15] <Assign> -> [VarName] ASSIGN <ExprArith>
  private List<AbstractSyntaxTree> assign() throws IOException {
    List<AbstractSyntaxTree> arr = new ArrayList<AbstractSyntaxTree>();
    arr.add(compareTokenAdd(LexicalUnit.VARNAME));
    compareToken(LexicalUnit.ASSIGN);
    arr.add(exprArith());
    return arr;
  }

  //[16] <ExprArith> -> <HpProd> <LpExpr>
  private AbstractSyntaxTree exprArith() throws IOException {
      AbstractSyntaxTree ast = new AbstractSyntaxTree("Expr");
      ast.add_child(hpProd());
      ast.add_child(lpExpr());
      return ast;
    }

    //[17] <HpProd> -> <SimpleExpr> <HpExpr>
    private AbstractSyntaxTree hpProd() throws IOException {
      AbstractSyntaxTree ast = new AbstractSyntaxTree("Expr");
      ast.add_child(simpleExpr());
      ast.add_child(hpExpr());
      return ast;
    }

    //[18] <HpExpr> -> <HpOp> <SimpleExpr> <HpExpr>
    //[19] <HpExpr> -> EPSILON
    private List<AbstractSyntaxTree> hpExpr() throws IOException {
      if (lookahead.getType().equals(LexicalUnit.TIMES) ||
      lookahead.getType().equals(LexicalUnit.DIVIDE)) {
        List<AbstractSyntaxTree> arr = new ArrayList<AbstractSyntaxTree>();
        arr.addAll(hpOp());
        arr.addAll(simpleExpr());
        arr.addAll(hpExpr());
        return arr;
      } else {
        return new ArrayList<AbstractSyntaxTree>();
      }
    }

    //[20] <LpExpr> -> <LpOp> <HpProd> <LpExpr>
    //[21] <LpExpr> -> EPSILON
    private List<AbstractSyntaxTree> lpExpr() throws IOException {
      if (lookahead.getType().equals(LexicalUnit.PLUS) ||
      lookahead.getType().equals(LexicalUnit.MINUS)) {
        List<AbstractSyntaxTree> arr = new ArrayList<AbstractSyntaxTree>();
        arr.addAll(lpOp());
        arr.add(hpProd());
        arr.addAll(lpExpr());
        return arr;
      } else {
        return new ArrayList<AbstractSyntaxTree>();
      }
    }

    //[22] <SimpleExpr> -> [VarName]
    //[23] <SimpleExpr> -> [Number]
    //[24] <SimpleExpr>	-> LPAREN <ExprArith> RPAREN
    //[25] <SimpleExpr>	-> MINUS <SimpleExpr>
    private List<AbstractSyntaxTree> simpleExpr() throws IOException {
      switch(lookahead.getType()) {
        case VARNAME:
          List<AbstractSyntaxTree> arr1 = new ArrayList<AbstractSyntaxTree>();
          arr1.add(compareTokenAdd(LexicalUnit.VARNAME));
          return arr1;
        case NUMBER:
          List<AbstractSyntaxTree> arr2 = new ArrayList<AbstractSyntaxTree>();
          arr2.add(compareTokenAdd(LexicalUnit.NUMBER));
          return arr2;
        case LPAREN:
          List<AbstractSyntaxTree> arr3 = new ArrayList<AbstractSyntaxTree>();
          compareToken(LexicalUnit.LPAREN);
          arr3.add(exprArith());
          compareToken(LexicalUnit.RPAREN);
          return arr3;
        case MINUS:
          List<AbstractSyntaxTree> arr4 = new ArrayList<AbstractSyntaxTree>();
          arr4.add(compareTokenAdd(LexicalUnit.MINUS));
          arr4.addAll(simpleExpr());
          return arr4;
        default:
          throw new Error("\nError at line " + lookahead.getLine() + ": " +
          lookahead.getType() + " expected a number, a variable or an arithmetic expression");
      }
    }

    //[26] <LpOp> -> PLUS
    //[27] <LpOp> -> MINUS
    private List<AbstractSyntaxTree> lpOp() throws IOException {
      if (lookahead.getType().equals(LexicalUnit.PLUS)) {
        List<AbstractSyntaxTree> arr = new ArrayList<AbstractSyntaxTree>();
        arr.add(compareTokenAdd(LexicalUnit.PLUS));
        return arr;
      } else if (lookahead.getType().equals(LexicalUnit.MINUS)) {
        List<AbstractSyntaxTree> arr = new ArrayList<AbstractSyntaxTree>();
        arr.add(compareTokenAdd(LexicalUnit.MINUS));
        return arr;
      } else {
        throw new Error("\nError at line " + lookahead.getLine() + ": " +
        lookahead.getType() + " expected addition or substraction operator");
      }
    }

    //[28] <HpOp> -> TIMES
    //[29] <HpOp> -> DIVIDE
    private List<AbstractSyntaxTree> hpOp() throws IOException {
      if (lookahead.getType().equals(LexicalUnit.TIMES)) {
        List<AbstractSyntaxTree> arr = new ArrayList<AbstractSyntaxTree>();
        arr.add(compareTokenAdd(LexicalUnit.TIMES));
        return arr;
      } else if (lookahead.getType().equals(LexicalUnit.DIVIDE)) {
        List<AbstractSyntaxTree> arr = new ArrayList<AbstractSyntaxTree>();
        arr.add(compareTokenAdd(LexicalUnit.DIVIDE));
        return arr;
      } else {
        throw new Error("\nError at line " + lookahead.getLine() + ": " +
        lookahead.getType() + " expected multiplication or division operator");
      }
    }

    //[30] <If> -> IF <Cond> THEN <Code> <IfElse> ENDIF
    private List<AbstractSyntaxTree> parse_if() throws IOException {
      List<AbstractSyntaxTree> arr = new ArrayList<AbstractSyntaxTree>();
      compareTokenAdd(LexicalUnit.IF);
      compareToken(LexicalUnit.LPAREN);
      arr.add(cond());
      compareToken(LexicalUnit.RPAREN);
      compareToken(LexicalUnit.THEN);
      skipEndline();
      arr.add(code());
      arr.addAll(ifElse());
      compareToken(LexicalUnit.ENDIF);
      return arr;
    }

    //[31] <IfElse> -> ELSE [EndLine] <Code>
    //[32] <IfElse> -> EPSILON
    private List<AbstractSyntaxTree> ifElse() throws IOException {
      if (lookahead.getType().equals(LexicalUnit.ELSE)) {
        List<AbstractSyntaxTree> arr = new ArrayList<AbstractSyntaxTree>();
        compareTokenAdd(LexicalUnit.ELSE);
        compareToken(LexicalUnit.ENDLINE);
        arr.add(code());
        return arr;
      } else {
        return new ArrayList<AbstractSyntaxTree>();
      }
    }

    //[33] <Cond> -> <PCond> <LpCond>
    private AbstractSyntaxTree cond() throws IOException {
      List<AbstractSyntaxTree> treeList = Arrays.asList(
      pCond(),
      lpCond()
      );
      return new AbstractSyntaxTree("Cond", treeList);
    }

    //[34] <PCond> -> <SimpleCond> <HpCond>
    private AbstractSyntaxTree pCond() throws IOException {
      List<AbstractSyntaxTree> treeList = Arrays.asList(
      simpleCond(),
      hpCond()
      );
      return new AbstractSyntaxTree(treeList);
    }

    //[35] <HpCond> -> AND <SimpleCond> <HpCond>
    //[36] <HpCond> -> EPSILON
    private AbstractSyntaxTree hpCond() throws IOException {
      if (lookahead.getType().equals(LexicalUnit.AND)) {
        List<AbstractSyntaxTree> treeList = Arrays.asList(
        compareToken(LexicalUnit.AND),
        simpleCond(),
        hpCond()
        );
        return new AbstractSyntaxTree(treeList);
      } else {
        return new AbstractSyntaxTree();
      }
    }

    //[37] <LpCond> -> OR <PCond> <LpCond>
    //[38] <LpCond> -> EPSILON
    private AbstractSyntaxTree lpCond() throws IOException {
      if (lookahead.getType().equals(LexicalUnit.OR)) {
        List<AbstractSyntaxTree> treeList = Arrays.asList(
        compareToken(LexicalUnit.OR),
        pCond(),
        lpCond()
        );
        return new AbstractSyntaxTree(treeList);
      } else {
        return new AbstractSyntaxTree();
      }
    }

    //[39] <SimpleCond> -> NOT <SimpleCond>
    //[40] <SimpleCond> -> <ExprArith> <Comp> <ExprArith>
    private AbstractSyntaxTree simpleCond() throws IOException {
      if (lookahead.getType().equals(LexicalUnit.NOT)) {
        List<AbstractSyntaxTree> treeList = Arrays.asList(
        compareTokenAdd(LexicalUnit.NOT),
        simpleCond()
        );
        return new AbstractSyntaxTree(treeList);
      } else {
        List<AbstractSyntaxTree> treeList = Arrays.asList(
        //exprArith(),
        comp()
        //exprArith()
        );
        return new AbstractSyntaxTree(treeList);
      }
    }

    //[41] <Comp>	-> EQ
    //[42] <Comp> -> GEQ
    //[43] <Comp> -> GT
    //[44] <Comp> -> LEQ
    //[45] <Comp> -> LT
    //[46] <Comp> -> NEQ
    private AbstractSyntaxTree comp() throws IOException {
      switch(lookahead.getType()) {
        case EQ:
        return new AbstractSyntaxTree(Arrays.asList(compareTokenAdd(LexicalUnit.EQ)));
        case GEQ:
        return new AbstractSyntaxTree(Arrays.asList(compareTokenAdd(LexicalUnit.GEQ)));
        case GT:
        return new AbstractSyntaxTree(Arrays.asList(compareTokenAdd(LexicalUnit.GT)));
        case LEQ:
        return new AbstractSyntaxTree(Arrays.asList(compareTokenAdd(LexicalUnit.LEQ)));
        case LT:
        return new AbstractSyntaxTree(Arrays.asList(compareTokenAdd(LexicalUnit.LT)));
        case NEQ:
        return new AbstractSyntaxTree(Arrays.asList(compareTokenAdd(LexicalUnit.NEQ)));
        default:
        throw new Error("\nError at line " + lookahead.getLine() + ": " +
        lookahead.getType() + " expected a comparison operator");
      }
    }

    //[47] <While> -> WHILE <Cond> DO <Code> ENDWHILE
    private List<AbstractSyntaxTree> parse_while() throws IOException {
      List<AbstractSyntaxTree> arr = new ArrayList<AbstractSyntaxTree>();
      compareToken(LexicalUnit.WHILE);
      arr.add(cond());
      compareToken(LexicalUnit.DO);
      skipEndline();
      arr.add(code());
      compareToken(LexicalUnit.ENDWHILE);
      return arr;
    }

    //[48] <For> -> FOR [VarName] ASSIGN <ExprArith> TO <ExprArith> DO <Code> ENDFOR
    private List<AbstractSyntaxTree> parse_for() throws IOException {
      List<AbstractSyntaxTree> arr = new ArrayList<AbstractSyntaxTree>();
      compareToken(LexicalUnit.FOR);
      compareTokenAdd(LexicalUnit.VARNAME);
      compareToken(LexicalUnit.ASSIGN);
      arr.add(exprArith());
      compareToken(LexicalUnit.TO);
      arr.add(exprArith());
      compareToken(LexicalUnit.DO);
      skipEndline();
      arr.add(code());
      compareToken(LexicalUnit.ENDFOR);
      return arr;
    }

    //[49] <Print> -> PRINT LPAREN <ExprList> RPAREN
    private List<AbstractSyntaxTree> parse_print() throws IOException {
      List<AbstractSyntaxTree> arr = new ArrayList<AbstractSyntaxTree>();
      compareTokenAdd(LexicalUnit.PRINT);
      compareToken(LexicalUnit.LPAREN);
      arr.addAll(exprList());
      compareToken(LexicalUnit.RPAREN);
      return arr;
    }

    //[50] <Read> -> READ LPAREN <VarList> RPAREN
    private List<AbstractSyntaxTree> parse_read() throws IOException {
      List<AbstractSyntaxTree> arr = new ArrayList<AbstractSyntaxTree>();
      compareTokenAdd(LexicalUnit.READ);
      compareToken(LexicalUnit.LPAREN);
      arr.addAll(varlist());
      compareToken(LexicalUnit.RPAREN);
      return arr;
    }

    //[51] <ExpList> -> <ExprArith> <ExpListEnd>
    private List<AbstractSyntaxTree> exprList() throws IOException {
      List<AbstractSyntaxTree> arr = new ArrayList<AbstractSyntaxTree>();
      arr.add(exprArith());
      arr.addAll(expListEnd());
      return arr;
    }

    //[52] <ExpListEnd> -> COMMA <ExpList>
    //[53] <ExpListEnd> -> EPSILON
    private List<AbstractSyntaxTree> expListEnd() throws IOException {
      if (lookahead.getType().equals(LexicalUnit.COMMA)) {
        List<AbstractSyntaxTree> arr = new ArrayList<AbstractSyntaxTree>();
        compareToken(LexicalUnit.COMMA);
        arr.addAll(exprList());
        return arr;
      } else {
        return new ArrayList<AbstractSyntaxTree>();
      }
    }

  }
