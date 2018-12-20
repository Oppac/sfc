import java.util.LinkedHashMap;

public class CodeGenerator {

  private AbstractSyntaxTree ast;
  private LinkedHashMap<String, Integer> symbolicTable;

  public CodeGenerator(AbstractSyntaxTree ast) {
    this.ast  = ast;
    this.symbolicTable = new LinkedHashMap<String, Integer>();
  }

  public void generateCode() {
    for (AbstractSyntaxTree child: ast.getChildren()) {
      if (child.getLabel() == "Variables") {
        System.out.println(createVariables(child));
      } else if (child.getLabel() == "Code") {
        for (AbstractSyntaxTree codeChild: child.getChildren()) {
          printCode(codeChild);
        }
      }
    }
  }

  public void printCode(AbstractSyntaxTree code) {
    if (code.getLabel() == "Assign") {
      System.out.println(generateAssign(code));
    } else if (code.getLabel() == "Read") {
      System.out.println(generateRead(code));
    } else if (code.getLabel() == "Print") {
      System.out.println(generatePrint(code));
    } else if (code.getLabel() == "Code") {
        for (AbstractSyntaxTree codeChild: code.getChildren()) {
          printCode(codeChild);
        }
    }
  }

  //To keep or not ?
  public String computeExprArith(AbstractSyntaxTree exprArith) {
    return exprArith.getChild(0).getLabel();
  }

  public String createVariables(AbstractSyntaxTree vars) {
    System.out.println("\n<Variables>");
    int i = 0;
    String llvmCode = "";
    for (AbstractSyntaxTree child: vars.getChildren()) {
      String varName = child.getLabel();
      llvmCode += "%" + varName + " = alloca i32\n";
      symbolicTable.put(varName, null);
      i++;
    }
    return llvmCode;
  }

  public String generateAssign(AbstractSyntaxTree assign) {
    System.out.println("\n<Assign>");
    int i = 0;
    String llvmCode = "";
    if (symbolicTable.containsKey(assign.getChild(0).getLabel())) {
      String varName = computeExprArith(assign.getChild(1));
      llvmCode += "store i32 %" + assign.getChild(0).getLabel() + ", i32* %" + varName + "\n";
    } else {
      System.out.println("Variable not declared");
    }
    return llvmCode;
  }

  public String generateRead(AbstractSyntaxTree read) {
    System.out.println("\n<Read>");
    int i = 0;
    String llvmCode = "";
    for (AbstractSyntaxTree child: read.getChildren()) {
      String varName = child.getLabel();
      llvmCode += "%" + i + "= call i32 @readInt()\n";
      llvmCode += "store i32 %" + i + ", i32* %" + varName + "\n";
      i++;
    }
    return llvmCode;
  }

  public String generatePrint(AbstractSyntaxTree print) {
    System.out.println("\n<Print>");
    String llvmCode = "";
    for (AbstractSyntaxTree child: print.getChildren()) {
      String varName = computeExprArith(child);
      llvmCode += "call void @println(i32* %" + varName + ")" + "\n";
    }
    return llvmCode;
  }


}
