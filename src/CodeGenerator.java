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

  private String readFunction = (
  "@.strR = private unnamed_addr constant [3 x i8] c\"%d\\00\", align 1\n"
  + "define i32 @readInt() {\n"
  + "%x = alloca i32, align 4\n"
  + "%1 = call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.strR, i32 0, i32 0), i32* %x)\n"
  + "%2 = load i32, i32* %x, align 4\n"
  + "ret i32 %2\n"
  + "}\n"
  + "declare i32 @__isoc99_scanf(i8*, ...)\n"
  );

  public CodeGenerator(AbstractSyntaxTree ast) {
    this.ast  = ast;
    this.symbolicTable = new LinkedHashMap<String, Integer>();
    this.count = 1;
  }

  public void writeToFile(String llvmCode, String filePath) {
    String fileName;
    try {
      if (filePath.isEmpty()) {
        fileName = ast.getLabel().toLowerCase() + ".ll";
      } else {
        fileName = filePath;
      }
      BufferedWriter llvmFile = new BufferedWriter(new FileWriter(fileName));
      llvmFile.write(llvmCode);
      llvmFile.close();
    } catch (Exception e) {
      System.err.println("Failed to generate llvm file");
    }
  }

  public String generateLLVM() {
    String llvmCode = "";
    llvmCode += printFunction;
    llvmCode += readFunction;
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
    llvmCode += "ret void \n}\n";
    return llvmCode;
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
    llvmCode += "br i1 %" + (count-1) + "," + "label %ifTrue, label %ifFalse\n";
    llvmCode += "ifTrue:\n";
    for (AbstractSyntaxTree child: ifGen.getChild(1).getChildren()) {
      llvmCode += generateCode(child);
    }
    llvmCode += "br label %ifNoElse\n";
    llvmCode += "ifFalse:\n";
    for (AbstractSyntaxTree child: ifGen.getChild(2).getChildren()) {
      llvmCode += generateCode(child);
    }
    llvmCode += "br label %ifNoElse\n";
    llvmCode += "ifNoElse:\n";
    return llvmCode;
  }

  public String generateWhile(AbstractSyntaxTree whileGen) {
    String llvmCode = "";
    llvmCode += generateCond(whileGen.getChild(0));
    llvmCode += "br i1 %" + (count-1) + ", label %startLoop, label %endLoop\n";
    llvmCode += "startLoop:\n";
    for (AbstractSyntaxTree child: whileGen.getChild(1).getChildren()) {
        llvmCode += generateCode(child);
    }
    llvmCode += generateCond(whileGen.getChild(0));
    llvmCode += "br i1 %" + (count-1) + ", label %startLoop, label %endLoop\n";
    llvmCode += "endLoop:\n";
    return llvmCode;
  }

  public String generateFor(AbstractSyntaxTree forGen) {
    String llvmCode = "";
    String varName = forGen.getChild(0).getLabel();
    if (symbolicTable.containsKey(varName)) {
      llvmCode += computeExprArith(forGen.getChild(2));
      llvmCode += "store i32 %" + (count-1) + ", i32* %" + varName + "\n";
    } else {
      System.out.println("Variable not declared");
    }
    llvmCode += computeExprArith(forGen.getChild(3));
    llvmCode += "%" + count + " = load i32, i32* %" + varName + "\n";
    count++;
    if (symbolicTable.containsKey(forGen.getChild(3).getLabel())) {
      llvmCode += "%" + count + " = load i32, i32* %" + forGen.getChild(3).getLabel() + "\n";
      count++;
      llvmCode += "%" + count + " = icmp slt i32 %" + (count-2) + ", %" + (count-1) + "\n";
    } else {
      llvmCode += "%" + count + " = icmp slt i32 %" + (count-1) + ", " + forGen.getChild(3).getLabel() + "\n";
    }
    llvmCode += "br i1 %" + count + ", label %startLoop, label %endLoop\n";
    count++;
    llvmCode += "startLoop:\n";
    llvmCode += generateCode(forGen.getChild(1));
    llvmCode += "%" + count + " = load i32, i32* %" + varName + "\n";
    count++;
    llvmCode += "%" + count + " = add i32 1, %" + (count-1) + "\n";
    count++;
    llvmCode += "store i32 %" + (count-1) + ", i32* %" + varName + "\n";
    if (symbolicTable.containsKey(forGen.getChild(3).getLabel())) {
      llvmCode += "%" + count + " = load i32, i32* %" + forGen.getChild(3).getLabel() + "\n";
      count++;
      llvmCode += "%" + count + " = icmp slt i32 %" + (count-2) + ", %" + (count-1) + "\n";
    } else {
      llvmCode += "%" + count + " = icmp slt i32 %" + (count-1) + ", " + forGen.getChild(3).getLabel() + "\n";
    }
    llvmCode += "br i1 %" + count + ", label %startLoop, label %endLoop\n";
    count++;
    llvmCode += "endLoop:\n";
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
      if (symbolicTable.containsKey(varName)) {
        llvmCode += "store i32 %" + count + ", i32* %" + varName + "\n";
      } else {
        System.err.println("Variable is not declared");
      }
      count++;
    }
    return llvmCode;
  }

}
