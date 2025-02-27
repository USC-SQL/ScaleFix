package usc.edu.OwlEye.VHTree;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;

public class MatchParentElement extends Element {
    Node<DomNode> childDomNode;
    String childXpath;
    String childProp;



    public MatchParentElement(String xpath, String cssProperty, String value, boolean isParentIncrease,
                              Node<DomNode> domNode, Node<DomNode> childDomNode, String childXpath, String childProp) {
        super(xpath, cssProperty, value, isParentIncrease, domNode);
        setChildDomNode(childDomNode);
        setChildXpath(childXpath);
        setChildProp(childProp);
    }


    public Node<DomNode> getChildDomNode() {
        return childDomNode;
    }

    public void setChildDomNode(Node<DomNode> childDomNode) {
        this.childDomNode = childDomNode;
    }

    public String getChildXpath() {
        return childXpath;
    }

    public void setChildXpath(String childXpath) {
        this.childXpath = childXpath;
    }

    public String getChildProp() {
        return childProp;
    }

    public void setChildProp(String childProp) {
        this.childProp = childProp;
    }

}
