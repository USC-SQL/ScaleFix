package gatech.xpert.dom.visitors;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.DomUtils;
//import usc.edu.SALEM.VHTree.Node;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
import java.util.Map;

public class NaiveMatchVisitor2 extends DomVisitor2 {
    Node<DomNode> ref;
    Map<Node<DomNode>, Node<DomNode>> matched;
    boolean found;
    int cnt = 0;

    public NaiveMatchVisitor2(Node<DomNode> node, Map<Node<DomNode>, Node<DomNode>> matched) {
        ref = node;
        this.matched = matched;
    }

    @Override
    public void visit(Node<DomNode> node) {
        cnt++;

        if (!found && !node.isMatched()) {
            if (DomUtils.isNaiveMatch(ref, node)) {
                matched.put(ref, node);
                ref.setMatched(true);
                node.setMatched(true);
                found = true;
            }

        }
    }

    @Override
    public String toString() {
        return "NMV visited #nodes:" + cnt;
    }
}
