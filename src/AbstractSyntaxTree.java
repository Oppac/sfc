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

  public void addLabel(String label) {
    this.label = label;
  }

  public void addChild(AbstractSyntaxTree child) {
    this.children.add(child);
  }

  public void addChild(List<AbstractSyntaxTree> children) {
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


  public void removeEpsilons() {
    List<AbstractSyntaxTree> toRemove = new ArrayList<AbstractSyntaxTree>();
    for (AbstractSyntaxTree child: children) {
      if (child.getLabel() == "Epsilon") {
        toRemove.add(child);
      } else {
        child.removeEpsilons();
      }
    }
    children.removeAll(toRemove);
  }

  public String print_tree() {
      StringBuilder tree = new StringBuilder();
      tree.append("\n[");
      tree.append(label);
      if (children != null) {
          for (AbstractSyntaxTree child: children) {
              tree.append(child.print_tree());
          }
      }
      tree.append("]\n");
      return tree.toString();
  }

}
