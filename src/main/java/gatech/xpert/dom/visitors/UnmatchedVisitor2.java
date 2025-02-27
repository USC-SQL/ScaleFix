package gatech.xpert.dom.visitors;

import gatech.xpert.dom.DomNode;
//import usc.edu.SALEM.VHTree.Node;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class UnmatchedVisitor2 extends DomVisitor2 {

    List<Node<DomNode>> unmatched;

    public UnmatchedVisitor2() {
        init();
    }

    public void init() {
        unmatched = new ArrayList<>();
    }

    @Override
    public void visit(Node<DomNode> node) {
        if (!node.isMatched()) {
            unmatched.add(node);
        }
    }

    public List<Node<DomNode>> getUnmatched() {
        return unmatched;
    }
}
