package gatech.xpert.dom.visitors;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.DomUtils;

import java.util.Map;

public class ExactMatchVisitor extends DomVisitor {

    DomNode ref;
    Map<DomNode, DomNode> matched;
    boolean found;
    int cnt = 0;

    public ExactMatchVisitor(DomNode node, Map<DomNode, DomNode> matched) {
        ref = node;
        this.matched = matched;
    }

    @Override
    public void visit(DomNode node) {
        cnt++;

        if (!found && !node.isMatched()) {
            if (ref.getTagName().equals(node.getTagName())) {
                float matchIndex = DomUtils.calculateMatchIndex(ref, node);
                if (matchIndex == 1.0) {
                    matched.put(ref, node);
                    ref.setMatched(true);
                    node.setMatched(true);
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
