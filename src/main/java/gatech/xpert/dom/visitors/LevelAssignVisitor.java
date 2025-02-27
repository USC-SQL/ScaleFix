package gatech.xpert.dom.visitors;

import gatech.xpert.dom.DomNode;

import java.util.ArrayList;
import java.util.List;

public class LevelAssignVisitor extends DomVisitor {

    List<List<DomNode>> levels;

    public LevelAssignVisitor() {
        init();
    }

    public void init() {
        levels = new ArrayList<>();
    }

    @Override
    public void visit(DomNode node) {
        DomNode parent = node.getParent();
        int level;
        if (parent == null || node.isAssignedRoot()) { // add the case of making certain node to root of subtree
            level = 0;
        } else {
            level = parent.getLevel() + 1;
        }
        if (level >= levels.size()) {
            List<DomNode> l = new ArrayList<>();
            l.add(node);
            levels.add(l);
        } else {
            levels.get(level).add(node);
        }
        node.setLevel(level);
    }

    public List<List<DomNode>> getLevels() {
        return levels;
    }
}
