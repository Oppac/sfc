import java.util.LinkedHashMap;

public class CodeGenerator {

  private AbstractSyntaxTree ast;
  private LinkedHashMap<String, Integer> symbolicTable;

  public CodeGenerator(AbstractSyntaxTree ast) {
    this.ast  = ast;
    this.symbolicTable = new LinkedHashMap<String, Integer>();
  }

  public void generateLLVM() {
    String llvmCode = "";
    for (AbstractSyntaxTree child: ast.getChildren()) {
      if (child.getLabel() == "Variables") {
        llvmCode += createVariables(child);
      } else if (child.getLabel() == "Code") {
        for (AbstractSyntaxTree codeChild: child.getChildren()) {
          llvmCode += generateCode(codeChild);
        }
      }
    }
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

  //To keep or not ?
  public String computeExprArith(AbstractSyntaxTree exprArith) {
    return exprArith.getChild(0).getLabel();
  }

  public String generateCond(AbstractSyntaxTree cond) {
    String llvmCode = "";
    return llvmCode;
  }

  public String createVariables(AbstractSyntaxTree vars) {
    int i = 0;
    String llvmCode = "";
    llvmCode += "\n<Variables>\n";
    for (AbstractSyntaxTree child: vars.getChildren()) {
      String varName = child.getLabel();
      llvmCode += "%" + varName + " = alloca i32\n";
      symbolicTable.put(varName, null);
      i++;
    }
    return llvmCode;
  }

  public String generateAssign(AbstractSyntaxTree assign) {
    int i = 0;
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
    int i = 0;
    String llvmCode = "";
    llvmCode += "\n<IF>\n";
    llvmCode += generateCond(ifGen.getChild(0));
    llvmCode += "\nbr i1 %" + i + "," + "label %iftrue" + i +
     ", label %iffalse" + i + "\n";
    llvmCode += "\niftrue" + i + ":\n";
    llvmCode += generateCode(ifGen.getChild(1).getChild(0));
    llvmCode += "\niffalse" + i + ":\n";
    llvmCode += generateCode(ifGen.getChild(2).getChild(0));
    return llvmCode;
  }

  public String generateWhile(AbstractSyntaxTree whileGen) {
  int i = 0;
  String llvmCode = "";
  llvmCode += "\n<While>\n";
  llvmCode += generateCond(whileGen.getChild(0));
  llvmCode += "\nbr i1 %" + i + "," + " label %beginLoop" + i +
   ", label %endLoop" + i + "\n";
  llvmCode += "\nbeginLoop:\n";
  llvmCode += generateCode(whileGen.getChild(1));
  llvmCode += generateCond(whileGen.getChild(0));
  llvmCode += "\nbr i1 %" + i + "," + " label %beginLoop" + i +
   ", label %endLoop" + i + "\n";
  llvmCode += "\nendLoop\n";
  return llvmCode;
  }

  public String generateFor(AbstractSyntaxTree forGen) {
  int i = 0;
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
  llvmCode += "\n%" + i + " = load i32, i32* %" + varName + "\n";
  llvmCode += "br i1 %" + i + "," + "label %beginLoop" + i +
  ", label %endLoop" + i + "\n";
  llvmCode += "\nbeginLoop:\n";
  llvmCode += generateCode(forGen.getChild(3));
  llvmCode += "\n%" + i + " = add i32 %" + varName + ", %1" + "\n";
  llvmCode += "\nendLoop\n";
  return llvmCode;
  }

  public String generateRead(AbstractSyntaxTree read) {
    int i = 0;
    String llvmCode = "";
    llvmCode += "\n<Read>\n";
    for (AbstractSyntaxTree child: read.getChildren()) {
      String varName = child.getLabel();
      llvmCode += "%" + i + "= call i32 @readInt()\n";
      llvmCode += "store i32 %" + i + ", i32* %" + varName + "\n";
      i++;
    }
    return llvmCode;
  }



}
