package gatech.xpert.dom.visitors;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.DomUtils;
//import usc.edu.SALEM.VHTree.Node;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
import java.util.Map;

public class ExactMatchVisitor2 extends DomVisitor2 {

    Node<DomNode> ref;
    Map<Node<DomNode>, Node<DomNode>> matched;
    boolean found;
    int cnt = 0;

    public ExactMatchVisitor2(Node<DomNode> node, Map<Node<DomNode>, Node<DomNode>> matched) {
        ref = node;
        this.matched = matched;
    }

    @Override
    public void visit(Node<DomNode> node) {
        cnt++;

        if (!found && !node.getData().isMatched()) {
            if (ref.getData().getTagName().equals(node.getData().getTagName())) {
                float matchIndex = DomUtils.calculateMatchIndex(ref, node);
                if (matchIndex == 1.0) {
                    matched.put(ref, node);
                    ref.getData().setMatched(true);
                    node.getData().setMatched(true);
                    found = true;
                }
            }
        }
    }

    @Override
    public String toString() {
        return "EMV visited #nodes:" + cnt;
    }
}
