import java.util.ArrayList;
import java.util.List;

public class AbstractSyntaxTree {

  private String label;
  private List<AbstractSyntaxTree> children;
  private boolean node_to_remove = false;

  public AbstractSyntaxTree() {
    this.node_to_remove = true;
  }

  public AbstractSyntaxTree(String label) {
    this.label = label;
  }

  public AbstractSyntaxTree(String label, List<AbstractSyntaxTree> children) {
    this.label = label;
    this.children = children;
  }

  public String print_tree() {
      StringBuilder treeTeX = new StringBuilder();
      treeTeX.append("(");
      if (node_to_remove != true) {
        treeTeX.append(label);
        treeTeX.append(" ");
        if (children != null) {
            for (AbstractSyntaxTree child: children) {
                treeTeX.append(child.print_tree());
              }
            }
        treeTeX.append(")");
      }
      return treeTeX.toString();
  }
}
