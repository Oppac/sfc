import java.util.LinkedHashMap;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class CodeGenerator {

  private AbstractSyntaxTree ast;
  private LinkedHashMap<String, Integer> symbolicTable;
  private int count;

  private String printFunction = (
  "@.strP = private unnamed_addr constant [4 x i8] c\"%d\\0A\\00\", align 1\n"
  + "define void @println(i32 %x) {\n"
  + "%1 = alloca i32, align 4\n"
  + "store i32 %x, i32* %1, align 4\n"
  + "%2 = load i32, i32* %1, align 4\n"
  + "%3 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.strP, i32 0, i32 0), i32 %2)\n"
  + "ret void\n"
  + "}\n"
  + "declare i32 @printf(i8*, ...)\n"
  );

  private String readFunction = ("");

  public CodeGenerator(AbstractSyntaxTree ast) {
    this.ast  = ast;
    this.symbolicTable = new LinkedHashMap<String, Integer>();
    this.count = 1;
  }

  public void writeToFile(String llvmCode) {
    try {
      String fileName = ast.getLabel().toLowerCase() + ".ll";
      BufferedWriter llvmFile = new BufferedWriter(new FileWriter(fileName));
      llvmFile.write(llvmCode);
      llvmFile.close();
    } catch (Exception e) {
      System.err.println("Failed to generate llvm file");
    }
  }

  public void generateLLVM() {
    String llvmCode = "";
    llvmCode += printFunction;
    llvmCode += "\ndefine void @main() {\n";
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
    writeToFile(llvmCode);
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

  public String computeExprArith(AbstractSyntaxTree exprArith) {
    String llvmCode = "";
    String value = exprArith.getLabel();
    int leftExpr, rightExpr;
    if (exprArith.getChildren().size() == 0) {
      if (symbolicTable.containsKey(exprArith.getLabel())) {
        llvmCode += "%" + count + " = load i32, i32* %" + exprArith.getLabel() + "\n";
        count++;
        llvmCode += "%" + count + " = add i32 0, %" + (count-1) + "\n";
      } else {
        llvmCode += "%" + count + " = add i32 0, " + exprArith.getLabel() + "\n";
      }
    } else {
      llvmCode += computeExprArith(exprArith.getChild(0));
      leftExpr = count-1;
      llvmCode += computeExprArith(exprArith.getChild(1));
      rightExpr = count-1;
      if (value.equals("+")) {
        llvmCode += "%" + count + " = add i32 %" + leftExpr + ", %" + rightExpr + "\n";
      } else if (value.equals("-") || value.equals("-e")) {
        llvmCode += "%" + count + " = sub i32 %" + leftExpr + ", %" + rightExpr + "\n";
      } else if (value.equals("*")) {
        llvmCode += "%" + count + " = mul i32 %" + leftExpr + ", %" + rightExpr + "\n";
      } else if (value.equals("/")) {
        llvmCode += "%" + count + " = sdiv i32 %" + leftExpr + ", %" + rightExpr + "\n";
      }
    }
    count++;
    return llvmCode;
  }

  public String generateCond(AbstractSyntaxTree cond) {
    String llvmCode = "";
    int leftCond, rightCond;
    String simpleCond = cond.getChild(0).getLabel();
    llvmCode += computeExprArith(cond.getChild(0).getChild(0));
    leftCond = count-1;
    llvmCode += computeExprArith(cond.getChild(0).getChild(1));
    rightCond = count-1;
    if (simpleCond.equals("=")) {
      llvmCode += "%" + count + " = icmp eq i32 %" + leftCond + ", %" + rightCond + "\n";
    } else if (simpleCond.equals(">=")) {
      llvmCode += "%" + count + " = icmp sge i32 %" + leftCond + ", %" + rightCond + "\n";
    } else if (simpleCond.equals(">")) {
      llvmCode += "%" + count + " = icmp sgt i32 %" + leftCond + ", %" + rightCond + "\n";
    } else if (simpleCond.equals("<=")) {
      llvmCode += "%" + count + " = icmp sle i32 %" + leftCond + ", %" + rightCond + "\n";
    } else if (simpleCond.equals("<")) {
      llvmCode += "%" + count + " = icmp slt i32 %" + leftCond + ", %" + rightCond + "\n";
    } else if (simpleCond.equals("<>")) {
      llvmCode += "%" + count + " = icmp ne i32 %" + leftCond + ", %" + rightCond + "\n";
    }
    count++;
    return llvmCode;
  }

  public String createVariables(AbstractSyntaxTree vars) {
    String llvmCode = "";
    for (AbstractSyntaxTree child: vars.getChildren()) {
      String varName = child.getLabel();
      llvmCode += "%" + varName + " = alloca i32\n";
      symbolicTable.put(varName, null);
    }
    return llvmCode;
  }

  public String generateAssign(AbstractSyntaxTree assign) {
    String llvmCode = "";
    if (symbolicTable.containsKey(assign.getChild(0).getLabel())) {
      llvmCode += computeExprArith(assign.getChild(1));
      llvmCode += "store i32 %" + (count-1) + ", i32* %" + assign.getChild(0).getLabel() + "\n";
    } else {
      System.err.println("Variable not declared");
    }
    return llvmCode;
  }

  public String generateIf(AbstractSyntaxTree ifGen) {
    String llvmCode = "";
    llvmCode += generateCond(ifGen.getChild(0));
    llvmCode += "br i1 %" + count + "," + "label %iftrue, label %iffalse\n";
    llvmCode += "iftrue:\n";
    llvmCode += generateCode(ifGen.getChild(1).getChild(0));
    llvmCode += "iffalse:\n";
    llvmCode += generateCode(ifGen.getChild(2).getChild(0));
    return llvmCode;
  }

  public String generateWhile(AbstractSyntaxTree whileGen) {
  String llvmCode = "";
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

  public String generatePrint(AbstractSyntaxTree print) {
    String llvmCode = "";
    for (AbstractSyntaxTree child: print.getChildren()) {
      String varName = child.getLabel();
      if (symbolicTable.containsKey(varName)) {
        llvmCode += "%" + count + " = load i32, i32* %" + varName + "\n";
        llvmCode += "call void @println(i32 %" + count + ")" + "\n";
        count++;
      } else {
        llvmCode += computeExprArith(child);
        llvmCode += "call void @println(i32 %" + (count-1) + ")" + "\n";
      }
    }
    return llvmCode;
  }

  public String generateRead(AbstractSyntaxTree read) {
    String llvmCode = "";
    for (AbstractSyntaxTree child: read.getChildren()) {
      String varName = child.getLabel();
      llvmCode += "%" + count + "= call i32 @readInt()\n";
      llvmCode += "store i32 %" + count + ", i32* %" + varName + "\n";
      count++;
    }
    return llvmCode;
  }

}
