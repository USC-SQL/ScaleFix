package gatech.xpert.dom.visitors;


import gatech.xpert.dom.DomNode;

public abstract class DomVisitor {
    public abstract void visit(DomNode node);
}
