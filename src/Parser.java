import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.List;
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
  * @return if the tokens match, return a ParseTree (either null or with
  * the proper label if draw tree is active).
  * @throws IOException give an error if the tokens do not match.
  */
  private ParseTree compareToken(LexicalUnit token) throws IOException {
    if (!(lookahead.getType().equals(token))){
      throw new Error("\nError at line " + lookahead.getLine() + ": " +
      lookahead.getType() + " expected " + token);
    }
    Symbol label = lookahead;
    nextToken();
    return new ParseTree(label);
    // else {
    //  nextToken();
    //
    //}
  }

  /**
  * Start the parsing of the input file at the initial symbol of the grammar.
  * @return if the tree drawing is not active the parser return an empty ParseTree. If
  * the execution was without errors the proper rules numbers/names have been written
  * on the standard output. We don't need retrieve the tree in this case so all the nodes are null.
  * @return if the tree drawing option was active, the parser return a ParseTree
  * containing the nodes with their labels and children. The ParsTree can then be
  * retrieve by Main in order to write the tree on the specified file.
  */
  public ParseTree startParse() throws IOException {
    return program();
  }

  /**
  * We allowed the input program to have as many endlines has it want at some specific
  * points in the program. They are ignored by the "standard" parser and return a node
  * called "SkipLines" for the ParseTree. It allow to see in the tree where extra
  * endlines are.
  * @return a "SkipLines" node for the ParseTree.
  */
  private ParseTree skipEndline() throws IOException {
    while (lookahead.getType().equals(LexicalUnit.ENDLINE)) {
      nextToken();
    }
    return new ParseTree("SkipLines");
  }

  /**
  * The first step of the parsing. The function has two "modes". One where the option to
  * draw the tree is inactive and the other one where it is active. In the "inactive mode"
  * the parser only check that the input is correct. It returns a null node as the
  * ParseTree will be discarded at the end anyway. In the "active mode", a new ParseTree
  * following the rules defined in ParseTree.java is returned instead. In both case
  * the rule number/name is printed on the standard output.
  * The other functions will not be detailed but they all follow the same model.
  * @return a ParseTree either correct or null depending on the selected option.
  */
  //[01] <Program> -> BEGINPROG [ProgName] [EndLine] <Variables> <Code> ENDPROG
  private ParseTree program() throws IOException {
    List<ParseTree> treeList = Arrays.asList(
    skipEndline(),
    compareToken(LexicalUnit.BEGINPROG),
    compareToken(LexicalUnit.PROGNAME),
    compareToken(LexicalUnit.ENDLINE),
    skipEndline(),
    variables(),
    skipEndline(),
    code(),
    skipEndline(),
    compareToken(LexicalUnit.ENDPROG),
    skipEndline(),
    compareToken(LexicalUnit.EOS)
    );
    return new ParseTree("Program", treeList);
  }

  //[02] <Variables> -> VARIABLES <VarList> [EndLine]
  //[03] <Variables> -> EPSILON
  private ParseTree variables() throws IOException {
    if (lookahead.getType().equals(LexicalUnit.VARIABLES)) {
      List<ParseTree> treeList = Arrays.asList(
      compareToken(LexicalUnit.VARIABLES),
      varlist()
      );
      return new ParseTree("Variables", treeList);
    } else {
      List<ParseTree> treeList = Arrays.asList(new ParseTree("EPSILON"));
      return new ParseTree("Variables", treeList);
    }
  }

  //[04] <VarList> -> [VarName] <VarListEnd>
  private ParseTree varlist() throws IOException {
    List<ParseTree> treeList = Arrays.asList(
    compareToken(LexicalUnit.VARNAME),
    varlistend()
    );
    return new ParseTree("Varlist", treeList);
  }

  //[05] <VarListEnd> -> COMMA <VarList>
  //[06] <VarListEnd> -> EPSILON
  private ParseTree varlistend() throws IOException {
    if (lookahead.getType().equals(LexicalUnit.COMMA)) {
      List<ParseTree> treeList = Arrays.asList(
      compareToken(LexicalUnit.COMMA),
      compareToken(LexicalUnit.VARNAME),
      varlistend()
      );
      return new ParseTree("Varlistend", treeList);
    } else {
      List<ParseTree> treeList = Arrays.asList(new ParseTree("EPSILON"));
      return new ParseTree("Varlistend", treeList);
    }
  }

  //[07] <Code> -> <Instruction> [EndLine] <Code>
  //[08] <Code> -> EPSILON
  private ParseTree code() throws IOException {
    switch(lookahead.getType()) {
      case VARNAME:
      case IF:
      case WHILE:
      case FOR:
      case PRINT:
      case READ:
        List<ParseTree> treeList = Arrays.asList(
        instruction(),
        skipEndline(),
        code()
        );
        return new ParseTree("Code", treeList);
      default:
        return new ParseTree("End");
    }
  }

  //[09] <Instruction> -> <Assign>
  //[10] <Instruction> -> <If>
  //[11] <Instruction> -> <While>
  //[12] <Instruction> -> <For>
  //[13] <Instruction> -> <Print>
  //[14] <Instruction> -> <Read>
  private ParseTree instruction() throws IOException {
    switch(lookahead.getType()) {
      case VARNAME:
        return new ParseTree("Instruction", Arrays.asList(assign()));
      case IF:
        return new ParseTree("Instruction", Arrays.asList(parse_if()));
      case WHILE:
        return new ParseTree("Instruction", Arrays.asList(parse_while()));
      case FOR:
        return new ParseTree("Instruction", Arrays.asList(parse_for()));
      case PRINT:
        return new ParseTree("Instruction", Arrays.asList(parse_print()));
      case READ:
        return new ParseTree("Instruction", Arrays.asList(parse_read()));
      default:
        return new ParseTree("End");
    }
  }

  //[15] <Assign> -> [VarName] ASSIGN <ExprArith>
  private ParseTree assign() throws IOException {
    List<ParseTree> treeList = Arrays.asList(
    compareToken(LexicalUnit.VARNAME),
    compareToken(LexicalUnit.ASSIGN),
    exprArith()
    );
    return new ParseTree("Assign", treeList);
  }

  //[16] <ExprArith> -> <HpProd> <LpExpr>
  private ParseTree exprArith() throws IOException {
      List<ParseTree> treeList = Arrays.asList(
      hpProd(),
      lpExpr()
      );
      return new ParseTree("ExprArith", treeList);
    }

    //[17] <HpProd> -> <SimpleExpr> <HpExpr>
    private ParseTree hpProd() throws IOException {
      List<ParseTree> treeList = Arrays.asList(
      simpleExpr(),
      hpExpr()
      );
      return new ParseTree("HpProd", treeList);
    }

    //[18] <HpExpr> -> <HpOp> <SimpleExpr> <HpExpr>
    //[19] <HpExpr> -> EPSILON
    private ParseTree hpExpr() throws IOException {
      if (lookahead.getType().equals(LexicalUnit.TIMES) ||
      lookahead.getType().equals(LexicalUnit.DIVIDE)) {
        List<ParseTree> treeList = Arrays.asList(
        hpOp(),
        simpleExpr(),
        hpExpr()
        );
        return new ParseTree("HpExpr", treeList);
      } else {
        List<ParseTree> treeList = Arrays.asList(new ParseTree("EPSILON"));
        return new ParseTree("HpExpr", treeList);
      }
    }

    //[20] <LpExpr> -> <LpOp> <HpProd> <LpExpr>
    //[21] <LpExpr> -> EPSILON
    private ParseTree lpExpr() throws IOException {
      if (lookahead.getType().equals(LexicalUnit.PLUS) ||
      lookahead.getType().equals(LexicalUnit.MINUS)) {
        List<ParseTree> treeList = Arrays.asList(
        lpOp(),
        hpProd(),
        lpExpr()
        );
        return new ParseTree("LpExpr", treeList);
      } else {
        List<ParseTree> treeList = Arrays.asList(new ParseTree("EPSILON"));
        return new ParseTree("LpExpr", treeList);
      }
    }

    //[22] <SimpleExpr> -> [VarName]
    //[23] <SimpleExpr> -> [Number]
    //[24] <SimpleExpr>	-> LPAREN <ExprArith> RPAREN
    //[25] <SimpleExpr>	-> MINUS <SimpleExpr>
    private ParseTree simpleExpr() throws IOException {
      switch(lookahead.getType()) {
        case VARNAME:
          List<ParseTree> treeList_VARNAME = Arrays.asList(
          compareToken(LexicalUnit.VARNAME)
          );
          return new ParseTree("SimpleExpr", treeList_VARNAME);
        case NUMBER:
          List<ParseTree> treeList_NUMBER = Arrays.asList(
          compareToken(LexicalUnit.NUMBER)
          );
          return new ParseTree("SimpleExpr", treeList_NUMBER);
        case LPAREN:
          List<ParseTree> treeList_LPAREN = Arrays.asList(
          compareToken(LexicalUnit.LPAREN),
          exprArith(),
          compareToken(LexicalUnit.RPAREN)
          );
          return new ParseTree("SimpleExpr", treeList_LPAREN);
        case MINUS:
          List<ParseTree> treeList_MINUS = Arrays.asList(
          compareToken(LexicalUnit.MINUS),
          exprArith()
          );
          return new ParseTree("SimpleExpr", treeList_MINUS);
        default:
          throw new Error("\nError at line " + lookahead.getLine() + ": " +
          lookahead.getType() + " expected a number, a variable or an arithmetic expression");
      }
    }

    //[26] <LpOp> -> PLUS
    //[27] <LpOp> -> MINUS
    private ParseTree lpOp() throws IOException {
      if (lookahead.getType().equals(LexicalUnit.PLUS)) {
        List<ParseTree> treeList = Arrays.asList(
        compareToken(LexicalUnit.PLUS)
        );
        return new ParseTree("LpOp", treeList);
      } else if (lookahead.getType().equals(LexicalUnit.MINUS)) {
        List<ParseTree> treeList = Arrays.asList(
        compareToken(LexicalUnit.MINUS)
        );
        return new ParseTree("LpOp", treeList);
      } else {
        throw new Error("\nError at line " + lookahead.getLine() + ": " +
        lookahead.getType() + " expected addition or substraction operator");
      }
    }

    //[28] <HpOp> -> TIMES
    //[29] <HpOp> -> DIVIDE
    private ParseTree hpOp() throws IOException {
      if (lookahead.getType().equals(LexicalUnit.TIMES)) {
        List<ParseTree> treeList = Arrays.asList(
        compareToken(LexicalUnit.TIMES)
        );
        return new ParseTree("HpOp", treeList);
      } else if (lookahead.getType().equals(LexicalUnit.DIVIDE)) {
        List<ParseTree> treeList = Arrays.asList(
        compareToken(LexicalUnit.DIVIDE)
        );
        return new ParseTree("HpOp", treeList);
      } else {
        throw new Error("\nError at line " + lookahead.getLine() + ": " +
        lookahead.getType() + " expected multiplication or division operator");
      }
    }

    //[30] <If> -> IF <Cond> THEN <Code> <IfElse> ENDIF
    private ParseTree parse_if() throws IOException {
      List<ParseTree> treeList = Arrays.asList(
      compareToken(LexicalUnit.IF),
      compareToken(LexicalUnit.LPAREN),
      cond(),
      compareToken(LexicalUnit.RPAREN),
      compareToken(LexicalUnit.THEN),
      skipEndline(),
      code(),
      ifElse(),
      compareToken(LexicalUnit.ENDIF)
      );
      return new ParseTree("If", treeList);
    }

    //[31] <IfElse> -> ELSE [EndLine] <Code>
    //[32] <IfElse> -> EPSILON
    private ParseTree ifElse() throws IOException {
      if (lookahead.getType().equals(LexicalUnit.ELSE)) {
        List<ParseTree> treeList = Arrays.asList(
        compareToken(LexicalUnit.ELSE),
        compareToken(LexicalUnit.ENDLINE),
        code()
        );
        return new ParseTree("IfElse", treeList);
      } else {
        List<ParseTree> treeList = Arrays.asList(new ParseTree("EPSILON"));
        return new ParseTree("IfElse", treeList);
      }
    }

    //[33] <Cond> -> <PCond> <LpCond>
    private ParseTree cond() throws IOException {
      List<ParseTree> treeList = Arrays.asList(
      pCond(),
      lpCond()
      );
      return new ParseTree("Cond", treeList);
    }

    //[34] <PCond> -> <SimpleCond> <HpCond>
    private ParseTree pCond() throws IOException {
      List<ParseTree> treeList = Arrays.asList(
      simpleCond(),
      hpCond()
      );
      return new ParseTree("PCond", treeList);
    }

    //[35] <HpCond> -> AND <SimpleCond> <HpCond>
    //[36] <HpCond> -> EPSILON
    private ParseTree hpCond() throws IOException {
      if (lookahead.getType().equals(LexicalUnit.AND)) {
        List<ParseTree> treeList = Arrays.asList(
        compareToken(LexicalUnit.AND),
        simpleCond(),
        hpCond()
        );
        return new ParseTree("HpCond", treeList);
      } else {
        List<ParseTree> treeList = Arrays.asList(new ParseTree("EPSILON"));
        return new ParseTree("HpCond", treeList);
      }
    }

    //[37] <LpCond> -> OR <PCond> <LpCond>
    //[38] <LpCond> -> EPSILON
    private ParseTree lpCond() throws IOException {
      if (lookahead.getType().equals(LexicalUnit.OR)) {
        List<ParseTree> treeList = Arrays.asList(
        compareToken(LexicalUnit.OR),
        pCond(),
        lpCond()
        );
        return new ParseTree("LpCond", treeList);
      } else {
        List<ParseTree> treeList = Arrays.asList(new ParseTree("EPSILON"));
        return new ParseTree("LpCond", treeList);
      }
    }

    //[39] <SimpleCond> -> NOT <SimpleCond>
    //[40] <SimpleCond> -> <ExprArith> <Comp> <ExprArith>
    private ParseTree simpleCond() throws IOException {
      if (lookahead.getType().equals(LexicalUnit.NOT)) {
        List<ParseTree> treeList = Arrays.asList(
        compareToken(LexicalUnit.NOT),
        simpleCond()
        );
        return new ParseTree("SimpleCond", treeList);
      } else {
        List<ParseTree> treeList = Arrays.asList(
        exprArith(),
        comp(),
        exprArith()
        );
        return new ParseTree("SimpleCond", treeList);
      }
    }

    //[41] <Comp>	-> EQ
    //[42] <Comp> -> GEQ
    //[43] <Comp> -> GT
    //[44] <Comp> -> LEQ
    //[45] <Comp> -> LT
    //[46] <Comp> -> NEQ
    private ParseTree comp() throws IOException {
      switch(lookahead.getType()) {
        case EQ:
        return new ParseTree("Comp", Arrays.asList(compareToken(LexicalUnit.EQ)));
        case GEQ:
        return new ParseTree("Comp", Arrays.asList(compareToken(LexicalUnit.GEQ)));
        case GT:
        return new ParseTree("Comp", Arrays.asList(compareToken(LexicalUnit.GT)));
        case LEQ:
        return new ParseTree("Comp", Arrays.asList(compareToken(LexicalUnit.LEQ)));
        case LT:
        return new ParseTree("Comp", Arrays.asList(compareToken(LexicalUnit.LT)));
        case NEQ:
        return new ParseTree("Comp", Arrays.asList(compareToken(LexicalUnit.NEQ)));
        default:
        throw new Error("\nError at line " + lookahead.getLine() + ": " +
        lookahead.getType() + " expected a comparison operator");
      }
    }

    //[47] <While> -> WHILE <Cond> DO <Code> ENDWHILE
    private ParseTree parse_while() throws IOException {
      List<ParseTree> treeList = Arrays.asList(
      compareToken(LexicalUnit.WHILE),
      cond(),
      compareToken(LexicalUnit.DO),
      skipEndline(),
      code(),
      compareToken(LexicalUnit.ENDWHILE)
      );
      return new ParseTree("While", treeList);
    }

    //[48] <For> -> FOR [VarName] ASSIGN <ExprArith> TO <ExprArith> DO <Code> ENDFOR
    private ParseTree parse_for() throws IOException {
      List<ParseTree> treeList = Arrays.asList(
      compareToken(LexicalUnit.FOR),
      compareToken(LexicalUnit.VARNAME),
      compareToken(LexicalUnit.ASSIGN),
      exprArith(),
      compareToken(LexicalUnit.TO),
      exprArith(),
      compareToken(LexicalUnit.DO),
      skipEndline(),
      code(),
      compareToken(LexicalUnit.ENDFOR)
      );
      return new ParseTree("For", treeList);
    }

    //[49] <Print> -> PRINT LPAREN <ExprList> RPAREN
    private ParseTree parse_print() throws IOException {
      List<ParseTree> treeList = Arrays.asList(
      compareToken(LexicalUnit.PRINT),
      compareToken(LexicalUnit.LPAREN),
      exprList(),
      compareToken(LexicalUnit.RPAREN)
      );
      return new ParseTree("Print", treeList);
    }

    //[50] <Read> -> READ LPAREN <VarList> RPAREN
    private ParseTree parse_read() throws IOException {
      List<ParseTree> treeList = Arrays.asList(
      compareToken(LexicalUnit.READ),
      compareToken(LexicalUnit.LPAREN),
      varlist(),
      compareToken(LexicalUnit.RPAREN)
      );
      return new ParseTree("Read", treeList);
    }

    //[51] <ExpList> -> <ExprArith> <ExpListEnd>
    private ParseTree exprList() throws IOException {
      List<ParseTree> treeList = Arrays.asList(
      exprArith(),
      expListEnd()
      );
      return new ParseTree("ExprList", treeList);
    }

    //[52] <ExpListEnd> -> COMMA <ExpList>
    //[53] <ExpListEnd> -> EPSILON
    private ParseTree expListEnd() throws IOException {
      if (lookahead.getType().equals(LexicalUnit.COMMA)) {
        List<ParseTree> treeList = Arrays.asList(
        compareToken(LexicalUnit.COMMA),
        exprList()
        );
        return new ParseTree("ExpListEnd", treeList);
      } else {
        List<ParseTree> treeList = Arrays.asList(new ParseTree("EPSILON"));
        return new ParseTree("ExpListEnd", treeList);
      }
    }

  }
