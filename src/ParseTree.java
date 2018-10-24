import java.util.List;
import java.util.ArrayList;

public class ParseTree {
    private Symbol label;
    private List<ParseTree> children;

    public ParseTree(Symbol lbl) {
        this.label = lbl;
        this.children = new ArrayList<ParseTree>();
    }

    public ParseTree(String nonTerm) {
        this.label = new Symbol(nonTerm);
        this.children = new ArrayList<ParseTree>();
    }

    public ParseTree(Symbol lbl, List<ParseTree> chdn) {
        this.label = lbl;
        this.children = chdn;
    }

    public ParseTree(String nonTerm, List<ParseTree> chdn) {
        this.label = new Symbol(nonTerm);
        this.children = chdn;
    }

    public String toTeX() {
        StringBuilder treeTeX = new StringBuilder();
        treeTeX.append("[.");
        if (label.isEpsilon()){
            treeTeX.append("$\\varepsilon$");
        }
        else {
            treeTeX.append(label);
        }
        treeTeX.append(" ");
        for (ParseTree child: children) {
            treeTeX.append(child.toTeX());
                }
        treeTeX.append("]");
        return treeTeX.toString();
    }

    public String toTikZ() {
        StringBuilder treeTikZ = new StringBuilder();
        treeTikZ.append("node {");
        treeTikZ.append(label.toTeX());
        treeTikZ.append("}\n");
        for (ParseTree child: children) {
            treeTikZ.append("child { ");
            treeTikZ.append(child.toTikZ());
            treeTikZ.append(" }\n");
        }
        return treeTikZ.toString();
    }

    public String toTikZPicture() {
        return "\\begin{tikzpicture}[tree layout]\n\\" + toTikZ() + ";\n\\end{tikzpicture}";
    }

    public String toLaTeX() {
        return "\\RequirePackage{luatex85}\n\\documentclass{standalone}\n\n\\usepackage{tikz}\n\n\\usetikzlibrary{graphdrawing, graphdrawing.trees}\n\n\\begin{document}\n\n" + toTikZPicture() + "\n\n\\end{document}\n%% Local Variables:\n%% TeX-engine: luatex\n%% End:";
    }
}
