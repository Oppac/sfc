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
}
