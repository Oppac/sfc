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
    this.children.add(child);
  }

  public void add_child(List<AbstractSyntaxTree> children) {
    this.children.addAll(children);
  }

  public String getLabel() {
      return this.label;
  }

  public AbstractSyntaxTree getChild(int n) {
    return children.get(n);
  }

  public List<AbstractSyntaxTree> getChildren() {
    return this.children;
  }


  public String print_tree() {
      StringBuilder treeTeX = new StringBuilder();
      treeTeX.append("\n{");
      treeTeX.append(label);
      if (children != null) {
          for (AbstractSyntaxTree child: children) {
              treeTeX.append(child.print_tree());
          }
      }
      treeTeX.append("}\n");
      return treeTeX.toString();
  }

}
