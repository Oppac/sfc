import java.util.ArrayList;
import java.util.List;

public class AbstractSyntaxTree {

  private String label;
  private AbstractSyntaxTree parent;
  private List<AbstractSyntaxTree> children = new ArrayList<AbstractSyntaxTree>();

  public AbstractSyntaxTree() {
  }

  public AbstractSyntaxTree(String label) {
    this.label = label;
  }

  public AbstractSyntaxTree(List<AbstractSyntaxTree> children) {
    this.children = children;
  }

  public AbstractSyntaxTree(String label, List<AbstractSyntaxTree> children) {
    this.label = label;
    this.children = children;
  }

  public void add_child(AbstractSyntaxTree child) {
    System.out.println(child.print_tree());
    this.children.add(child);
  }

 /*
  public void clean_tree() {
    if ((label == null) && (children != null)) {
      List<AbstractSyntaxTree> children
      parent.children.addAll(children);
    }
    if (children != null) {
      for (AbstractSyntaxTree child: children) {
          child.clean_tree();
      }
    }
  }
  */

  public String print_tree() {
      StringBuilder treeTeX = new StringBuilder();
      treeTeX.append("{");
      treeTeX.append(label);
      treeTeX.append(" ");
      if (children != null) {
          for (AbstractSyntaxTree child: children) {
              treeTeX.append(child.print_tree());
          }
      }
      treeTeX.append("}");
      return treeTeX.toString();
  }
}
