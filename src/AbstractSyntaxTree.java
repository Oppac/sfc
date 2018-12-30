import java.util.ArrayList;
import java.util.List;

/** A class to represent the abstract syntax trees.
* The class is inspired by the ParseTree class used in Part2. It is different from
* the Parse Tree because it only contain the nodes relevant to the code generation,
* while the Parser Tree had a node for every rule in the grammar.
* Each node is itself a AST. It contains a label and a list of children.
* The label and children are optional the node can have one, neither or both.
*/

public class AbstractSyntaxTree {

  private String label;
  private List<AbstractSyntaxTree> children = new ArrayList<AbstractSyntaxTree>();

  /** Initialize a bare AST without label or children.
  */
  public AbstractSyntaxTree() {
  }

  /** Initialize a AST with a label and no children.
  * @param label : the label of the node
  */
  public AbstractSyntaxTree(String label) {
    this.label = label;
  }

  /** Initialize a AST with no label and a list of children.
  * @param children : list of AST that are the children of the node
  */
  public AbstractSyntaxTree(List<AbstractSyntaxTree> children) {
    this.children = children;
  }

  /** Initialize a AST with both label and children.
  * @param label : the label of the node
  * @param children : list of AST that are the children of the node
  */
  public AbstractSyntaxTree(String label, List<AbstractSyntaxTree> children) {
    this.label = label;
    this.children = children;
  }

  /** Label setter.
  * @param label : the label to give to the node
  */
  public void addLabel(String label) {
    this.label = label;
  }

  /** Add a child to the node.
  * @param child : AST node to be added as a child.
  */
  public void addChild(AbstractSyntaxTree child) {
    this.children.add(child);
  }

  /** Overload of addChild() method that add multiple child at the same time.
  * @param children : list of AST nodes to be added as children.
  */
  public void addChild(List<AbstractSyntaxTree> children) {
    this.children.addAll(children);
  }

  /** Set label of a child and add its children.
  * @param child : child node to get label and children from.
  */
  public void addChildLabel(AbstractSyntaxTree child) {
    this.label = child.getLabel();
    for (AbstractSyntaxTree c: child.getChildren()) {
      this.children.add(c);
    }
  }

  /** Label getter.
  */
  public String getLabel() {
      return this.label;
  }

  /** Get a specific child.
  * @param n : index of the wanted child.
  */
  public AbstractSyntaxTree getChild(int n) {
    return children.get(n);
  }

    /** Return the list of all children nodes.
    */
  public List<AbstractSyntaxTree> getChildren() {
    return this.children;
  }

  /** Remove the nodes labeled "Epsilon" in the AST.
  */
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

  /** Remove the duplicate Minus node that sometimes appears.
  * Ideally this method shouldn't exist but we haven't be able prevent it from
  * appearing in the AST. This method get rid of it. To be used after then final
  * AST has been generated.
  */
  public void removeBadMinus() {
  List<AbstractSyntaxTree> toRemove = new ArrayList<AbstractSyntaxTree>();
  List<AbstractSyntaxTree> toAdd = new ArrayList<AbstractSyntaxTree>();
  for (AbstractSyntaxTree child: children) {
    if (label.equals(child.getLabel()) && label.equals("-") && child.getChildren().size() == 1) {
      toAdd.addAll(child.getChildren());
      toRemove.add(child);
    } else {
      child.removeBadMinus();
    }
  }
  children.removeAll(toRemove);
  children.addAll(toAdd);
  }

  /** The NOT operator call this method to reverse the condition.
  */
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

  /**Print the tree to the console. Can be draw online at http://mshang.ca/syntree/.
  */
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
