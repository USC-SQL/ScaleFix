package gatech.xpert.dom.visitors;


import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
//import usc.edu.SALEM.VHTree.Node;

public abstract class DomVisitor2 {
    public abstract void visit(Node<DomNode> node);
}
