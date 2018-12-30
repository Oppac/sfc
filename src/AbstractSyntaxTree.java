import java.util.ArrayList;
import java.util.List;

public class AbstractSyntaxTree {

  private String label;
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

  public void removeSingleExpr() {
    List<AbstractSyntaxTree> toRemove = new ArrayList<AbstractSyntaxTree>();
    List<AbstractSyntaxTree> toAdd = new ArrayList<AbstractSyntaxTree>();
    for (AbstractSyntaxTree child: children) {
      if (child.getLabel().equals("Expr") && child.getChildren().size() == 1) {
        toAdd.addAll(child.getChildren());
        toRemove.add(child);
      } else if (label.equals(child.getLabel()) && child.getChildren().size() == 0) {
        toAdd.addAll(child.getChildren());
        toRemove.add(child);
      } else if (label.equals("-e") && child.getLabel().equals("-e")) {
        toAdd.addAll(child.getChildren());
        toRemove.add(child);
      } else {
        child.removeSingleExpr();
      }
    }
    children.removeAll(toRemove);
    children.addAll(toAdd);
  }

  public AbstractSyntaxTree reverseCond(AbstractSyntaxTree cond) {
    if (cond.getLabel().equals("=")) {
      cond.addLabel("<>");
    } else if (cond.getLabel().equals(">=")) {
      cond.addLabel("<");
    } else if (cond.getLabel().equals(">")) {
      cond.addLabel("<=");
    } else if (cond.getLabel().equals("<=")) {
      cond.addLabel(">");
    } else if (cond.getLabel().equals("<")) {
      cond.addLabel(">=");
    } else if (cond.getLabel().equals("<>")) {
      cond.addLabel("=");
    }
    return cond;
  }

  public void simplifyExpr() {
    List<AbstractSyntaxTree> toRemove = new ArrayList<AbstractSyntaxTree>();
    for (AbstractSyntaxTree child: children) {
      if (child.getLabel().equals("+") || child.getLabel().equals("*") ||
          child.getLabel().equals("/") || child.getLabel().equals("-")) {
        label = child.getLabel();
        child.addLabel(child.getChild(0).getLabel());
        toRemove.add(child.getChild(0));
      }
      child.simplifyExpr();
    }
    children.removeAll(toRemove);
  }

  public String printTree() {
      StringBuilder tree = new StringBuilder();
      tree.append("\n[");
      tree.append(label);
      if (children != null) {
          for (AbstractSyntaxTree child: children) {
              tree.append(child.printTree());
          }
      }
      tree.append("]\n");
      return tree.toString();
  }

}
