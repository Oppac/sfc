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
          if (codeChild.getLabel() == "Assign") {
            System.out.println("Assssignement");
          } else if (codeChild.getLabel() == "Read") {
            System.out.println(generateRead(codeChild));
          }
        }
      }
    }
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


}
