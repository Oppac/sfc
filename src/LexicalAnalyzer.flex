import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.Map;

%%

%class Lexer
%unicode
%line
%column
%type Symbol

%{

  //The symbolic table data structure
  static LinkedHashMap<String, Integer> symbolicTable = new LinkedHashMap<String, Integer>();

  //Create the symbolic table, keep the input order
  public static void table(Object varName, int line) {
    if (!symbolicTable.containsKey(varName.toString())) {
      symbolicTable.put(varName.toString(), line);
    }
  }

  //Sort in lexical order than print the symbolic table
  private static void printTable() {
    System.out.println("\nIdentifiers");
    TreeMap<String, Integer> sorted = new TreeMap<>();
    sorted.putAll(symbolicTable);
    for (Map.Entry<String, Integer> entry : sorted.entrySet()) {
      System.out.println(entry.getKey() + "\t" + entry.getValue());
    }

  }

  //Print then return the symbol
  public static Symbol token(LexicalUnit tokenType, int line, int column, Object value) {
    Symbol sym = new Symbol(tokenType, line, column, value);
    //printToken(sym);
    return(sym);
  }

  //Print the token, \n is print as a string and not an end of file
  private static void printToken(Symbol sym) {
    if (sym.getType().equals(LexicalUnit.ENDLINE)) {
      System.out.println(String.format("token: %-15slexical unit: %s", "\\n", sym.getType().toString()));
    } else {
      System.out.println(sym.toString());
    }
  }

  //Default syntax error
  private void syntaxError(int line, Object value) {
    throw new Error("Syntax error at line "
    + line + " -> " + value.toString());
  }

  //Specific errors

  //Error when a number has one or more leading 0
  private void illegalNumError(int line, Object value) {
    throw new Error("Number starting by 0 at line "
    + line + " -> " + value.toString());
  }

  //Error when a variable name contains a upper case letter
  private void varCapError(int line, Object value) {
    throw new Error("Variable name must only contain lower case letters, at line "
    + line + " -> " + value.toString());
  }

  //Error when a variable name begin with a number
  private void varNumError(int line, Object value) {
    throw new Error("Variable name must not begin by a number, at line "
    + line + " -> " + value.toString());
  }

  //Error when a comment symbol is found alone
  private void commentError(int line, Object value) {
    throw new Error("Comments must be closed and opened properly, at line "
    + line + " -> " + value.toString());
  }

%}

%eofval{
  return token(LexicalUnit.EOS, yyline, yycolumn, yytext());
%eofval}

%eof{
  //printTable();
%eof}


digits           = [0-9]
lower_letters    = [a-z]
upper_letters    = [A-Z]
letters          = [a-zA-Z]
alphanumeric     = {lower_letters}|{digits}

VarName          = {lower_letters}+ {alphanumeric}*
ProgName         = {upper_letters}+ ({letters}* {alphanumeric}+ {letters}*)
Endline          = "\r"|"\n"|"\r\n"
Number           = [1-9]+ {digits}* | "0"
IllegalNum       = (0{digits}*)
IllegalVarNum    = {digits}+{lower_letters}+ {alphanumeric}*
IllegalVarCap    = {lower_letters}+ ({upper_letters}+{alphanumeric}*|{alphanumeric}*{upper_letters}+)
ErrorCap         = {upper_letters}+ {upper_letters}*

BlockComment     = "/*" [^*] ~"*/" | "/*" "*"+ "/"
LineComment      = "//" [^\r\n]* {Endline}?
Comments         = {BlockComment} | {LineComment}
SoloComment      = "/*" | "*/"
BlankSpace       = [\ \t\f]

%xstate YYINITIAL

%%

<YYINITIAL> {
  {VarName}       { table(yytext(), yyline);
                    return token(LexicalUnit.VARNAME, yyline, yycolumn, yytext());
                  }
  {Number}        {return token(LexicalUnit.NUMBER, yyline, yycolumn, yytext());}
  {ProgName}      {return token(LexicalUnit.PROGNAME, yyline, yycolumn, yytext());}
  {Endline}       {return token(LexicalUnit.ENDLINE, yyline, yycolumn, yytext());}

  "BEGINPROG"     {return token(LexicalUnit.BEGINPROG, yyline, yycolumn, yytext());}
  "ENDPROG"       {return token(LexicalUnit.ENDPROG, yyline, yycolumn, yytext());}
  "VARIABLES"     {return token(LexicalUnit.VARIABLES, yyline, yycolumn, yytext());}

  "PRINT"         {return token(LexicalUnit.PRINT, yyline, yycolumn, yytext());}
  "READ"          {return token(LexicalUnit.READ, yyline, yycolumn, yytext());}

  "IF"            {return token(LexicalUnit.IF, yyline, yycolumn, yytext());}
  "THEN"          {return token(LexicalUnit.THEN, yyline, yycolumn, yytext());}
  "ELSE"          {return token(LexicalUnit.ELSE, yyline, yycolumn, yytext());}
  "ENDIF"         {return token(LexicalUnit.ENDIF, yyline, yycolumn, yytext());}

  "WHILE"         {return token(LexicalUnit.WHILE, yyline, yycolumn, yytext());}
  "DO"            {return token(LexicalUnit.DO, yyline, yycolumn, yytext());}
  "ENDWHILE"      {return token(LexicalUnit.ENDWHILE, yyline, yycolumn, yytext());}

  "FOR"           {return token(LexicalUnit.FOR, yyline, yycolumn, yytext());}
  "TO"            {return token(LexicalUnit.TO, yyline, yycolumn, yytext());}
  "ENDFOR"        {return token(LexicalUnit.ENDFOR, yyline, yycolumn, yytext());}

  ","             {return token(LexicalUnit.COMMA, yyline, yycolumn, yytext());}
  ":="            {return token(LexicalUnit.ASSIGN, yyline, yycolumn, yytext());}
  "("             {return token(LexicalUnit.LPAREN, yyline, yycolumn, yytext());}
  ")"             {return token(LexicalUnit.RPAREN, yyline, yycolumn, yytext());}

  "-"             {return token(LexicalUnit.MINUS, yyline, yycolumn, yytext());}
  "+"             {return token(LexicalUnit.PLUS, yyline, yycolumn, yytext());}
  "*"             {return token(LexicalUnit.TIMES, yyline, yycolumn, yytext());}
  "/"             {return token(LexicalUnit.DIVIDE, yyline, yycolumn, yytext());}

  "AND"           {return token(LexicalUnit.AND, yyline, yycolumn, yytext());}
  "OR"            {return token(LexicalUnit.OR, yyline, yycolumn, yytext());}
  "NOT"           {return token(LexicalUnit.NOT, yyline, yycolumn, yytext());}

  "="             {return token(LexicalUnit.EQ, yyline, yycolumn, yytext());}
  ">="            {return token(LexicalUnit.GEQ, yyline, yycolumn, yytext());}
  ">"             {return token(LexicalUnit.GT, yyline, yycolumn, yytext());}
  "<="            {return token(LexicalUnit.LEQ, yyline, yycolumn, yytext());}
  "<"             {return token(LexicalUnit.LT, yyline, yycolumn, yytext());}
  "<>"            {return token(LexicalUnit.NEQ, yyline, yycolumn, yytext());}

  //Specific error for misspelled comparator
  "=>"            {syntaxError(yyline, yytext());}
  "=<"            {syntaxError(yyline, yytext());}

  {Comments}      { }
  {BlankSpace}    { }
  {IllegalNum}    {illegalNumError(yyline, yytext());}
  {IllegalVarNum} {varNumError(yyline, yytext());}
  {IllegalVarCap} {varCapError(yyline, yytext());}
  {ErrorCap}      {syntaxError(yyline, yytext());}
  {SoloComment}   {commentError(yyline, yytext());}
  .               {syntaxError(yyline, yytext());}
}
