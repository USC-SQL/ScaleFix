package gatech.xpert.dom.visitors;

import gatech.xpert.dom.DomNode;
//import usc.edu.SALEM.VHTree.Node;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class LevelAssignVisitor2 extends DomVisitor2 {

    List<List<Node<DomNode>>> levels;

    public LevelAssignVisitor2() {
        init();
    }

    public void init() {
        levels = new ArrayList<>();
    }

    @Override
    public void visit(Node<DomNode> node) {
        Node<DomNode> parent = node.getParent();
        int level;
        if (parent == null || node.getData().isAssignedRoot()) { // add the case of making certain node to root of subtree
            level = 0;
        } else {
            level = parent.getData().getLevel() + 1;
        }
        if (level >= levels.size()) {
            List<Node<DomNode>> l = new ArrayList<>();
            l.add(node);
            levels.add(l);
        } else {
            levels.get(level).add(node);
        }
        node.getData().setLevel(level);
    }

    public List<List<Node<DomNode>>> getLevels() {
        return levels;
    }
}
