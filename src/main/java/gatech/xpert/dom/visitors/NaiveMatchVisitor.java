package gatech.xpert.dom.visitors;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.DomUtils;

import java.util.Map;

public class NaiveMatchVisitor extends DomVisitor {
    DomNode ref;
    Map<DomNode, DomNode> matched;
    boolean found;
    int cnt = 0;

    public NaiveMatchVisitor(DomNode node, Map<DomNode, DomNode> matched) {
        ref = node;
        this.matched = matched;
    }

    @Override
    public void visit(DomNode node) {
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
