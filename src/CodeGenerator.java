import java.util.LinkedHashMap;

public class CodeGenerator {

  private AbstractSyntaxTree ast;
  private LinkedHashMap<String, Integer> symbolicTable;
  private int count;

  public CodeGenerator(AbstractSyntaxTree ast) {
    this.ast  = ast;
    this.symbolicTable = new LinkedHashMap<String, Integer>();
    this.count = 0;
  }

  public void generateLLVM() {
    String llvmCode = "";
    llvmCode += "define void "+ ast.getLabel() +"() {\n";
    for (AbstractSyntaxTree child: ast.getChildren()) {
      if (child.getLabel() == "Variables") {
        llvmCode += createVariables(child);
      } else if (child.getLabel() == "Code") {
        for (AbstractSyntaxTree codeChild: child.getChildren()) {
          llvmCode += generateCode(codeChild);
        }
      }
    }
    llvmCode += "\nret void \n}";
    System.out.println(llvmCode);
  }

  public String generateCode(AbstractSyntaxTree code) {
    String llvmCode = "";
    if (code.getLabel() == "Assign") {
      llvmCode += generateAssign(code);
    } else if (code.getLabel() == "If") {
      llvmCode += generateIf(code);
    } else if (code.getLabel() == "While") {
      llvmCode += generateWhile(code);
    } else if (code.getLabel() == "For") {
      llvmCode += generateFor(code);
    } else if (code.getLabel() == "Print") {
      llvmCode += generatePrint(code);
    } else if (code.getLabel() == "Read") {
      llvmCode += generateRead(code);
    } else if (code.getLabel() == "Code") {
        for (AbstractSyntaxTree codeChild: code.getChildren()) {
          llvmCode += generateCode(codeChild);
        }
    }
    return llvmCode;
  }

  //To do the i
  public String computeExprArith(AbstractSyntaxTree exprArith) {
    String llvmCode = "";
    String value = exprArith.getLabel();
    int rightChild, leftChild;
    for (AbstractSyntaxTree child: exprArith.getChildren()) {
      computeExprArith(child);
    }
    leftChild = count - 1;
    rightChild = count - 1;
    if (value == "+") {
      llvmCode += "% " + count + " = add i32 %" + leftChild + ", %" + rightChild;
    } else if (value == "-") {
      llvmCode += "% " + count + " = sub i32 %" + leftChild + ", %" + rightChild;
    } else if (value == "*") {
      llvmCode += "% " + count + " = mul i32 %" + leftChild + ", %" + rightChild;
    } else if (value == "/") {
      llvmCode += "% " + count + " = sdiv i32 %" + leftChild + ", %" + rightChild;
    }
    count++;
    return llvmCode;
  }

  public String generateCond(AbstractSyntaxTree cond) {
    String llvmCode = "";
    return llvmCode;
  }

  public String createVariables(AbstractSyntaxTree vars) {
    String llvmCode = "";
    llvmCode += "\n<Variables>\n";
    for (AbstractSyntaxTree child: vars.getChildren()) {
      String varName = child.getLabel();
      llvmCode += "%" + varName + " = alloca i32\n";
      symbolicTable.put(varName, null);
    }
    return llvmCode;
  }

  public String generateAssign(AbstractSyntaxTree assign) {
    String llvmCode = "";
    llvmCode += "\n<Assign>\n";
    if (symbolicTable.containsKey(assign.getChild(0).getLabel())) {
      String varName = computeExprArith(assign.getChild(1));
      llvmCode += "store i32 %" + assign.getChild(0).getLabel() + ", i32* %" + varName + "\n";
    } else {
      System.out.println("Variable not declared");
    }
    return llvmCode;
  }

  public String generatePrint(AbstractSyntaxTree print) {
    String llvmCode = "";
    llvmCode += "\n<Print>\n";
    for (AbstractSyntaxTree child: print.getChildren()) {
      String varName = computeExprArith(child);
      llvmCode += "call void @println(i32* %" + varName + ")" + "\n";
    }
    return llvmCode;
  }

  public String generateIf(AbstractSyntaxTree ifGen) {
    String llvmCode = "";
    llvmCode += "\n<IF>\n";
    llvmCode += generateCond(ifGen.getChild(0));
    llvmCode += "\nbr i1 %" + count + "," + "label %iftrue, label %iffalse\n";
    llvmCode += "\niftrue:\n";
    llvmCode += generateCode(ifGen.getChild(1).getChild(0));
    llvmCode += "\niffalse:\n";
    llvmCode += generateCode(ifGen.getChild(2).getChild(0));
    return llvmCode;
  }

  public String generateWhile(AbstractSyntaxTree whileGen) {
  String llvmCode = "";
  llvmCode += "\n<While>\n";
  llvmCode += generateCond(whileGen.getChild(0));
  llvmCode += "\nbr i1 %" + count + ", label %beginLoop, label %endLoop\n";
  llvmCode += "beginLoop:\n";
  llvmCode += generateCode(whileGen.getChild(1));
  llvmCode += generateCond(whileGen.getChild(0));
  llvmCode += "endLoop\n";
  return llvmCode;
  }

  public String generateFor(AbstractSyntaxTree forGen) {
  String llvmCode = "";
  String varName = forGen.getChild(0).getLabel();
  llvmCode += "\n<For>\n";
  if (symbolicTable.containsKey(varName)) {
    String value = computeExprArith(forGen.getChild(1));
    llvmCode += "\nstore i32 %" + varName + ", i32* %" + value + "\n";
  } else {
    System.out.println("Variable not declared");
  }
  llvmCode += computeExprArith(forGen.getChild(2));
  llvmCode += "\n%" + count + " = load i32, i32* %" + varName + "\n";
  llvmCode += "br i1 %" + count + ", label %beginLoop, label %endLoop\n";
  llvmCode += "beginLoop:\n";
  llvmCode += generateCode(forGen.getChild(3));
  llvmCode += "\n%" + count + " = add i32 %" + varName + ", %1" + "\n";
  llvmCode += "endLoop\n";
  return llvmCode;
  }

  public String generateRead(AbstractSyntaxTree read) {
    String llvmCode = "";
    llvmCode += "\n<Read>\n";
    for (AbstractSyntaxTree child: read.getChildren()) {
      String varName = child.getLabel();
      llvmCode += "%" + count + "= call i32 @readInt()\n";
      llvmCode += "store i32 %" + count + ", i32* %" + varName + "\n";
      count++;
    }
    return llvmCode;
  }

}
