

public class CodeGenerator {

  private AbstractSyntaxTree ast;

  public CodeGenerator(AbstractSyntaxTree ast) {
    this.ast  = ast;
  }


  public void generateCode() {
    for (AbstractSyntaxTree child: ast.getChildren()) {
      System.out.println(child.getLabel());
      if (child.getLabel() == "Variables") {
        System.out.println("The varibles will be stored\n");
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
