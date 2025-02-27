package usc.edu.layoutgraph.node;

import java.io.Serializable;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
/**
 * Layout Graph Node
 *
 * @author Abdulmajeed
 */
public class LayoutNode implements Serializable {

    private static final long serialVersionUID = 1L;
    long area;
    private Node<DomNode> domNode;

    public LayoutNode(Node<DomNode> domNode) {
        this.setDomNode(domNode);
        area = (getX2() - getX1()) * (getY2() - getY1());
    }

    public long getArea() {
        return area;
    }

    public boolean contains(LayoutNode n) {
        return this.getX1() <= n.getX1() && this.getY1() <= n.getY1()
                && this.getX2() >= n.getX2() && this.getY2() >= n.getY2();
    }

    public int[] getCenter() {
        int[] center = {(getX1() + getX2()) / 2, (getY1() + getY2()) / 2};
        return center;
    }


    /**
     * @return the domNode
     */
    public Node<DomNode> getDomNode() {
        return domNode;
    }

    /**
     * @param domNode the domNode to set
     */
    public void setDomNode(Node<DomNode> domNode) {
        this.domNode = domNode;
    }


    /**
     * @return the x1
     */
    public int getX1() {
        return domNode.getData().getCoords()[0];
    }


    /**
     * @return the y1
     */
    public int getY1() {
        return domNode.getData().getCoords()[1];
    }


    /**
     * @return the x2
     */
    public int getX2() {
        return (domNode.getData().getCoords()[2] + domNode.getData().getCoords()[0]);
    }


    /**
     * @return the y2
     */
    public int getY2() {
        return (domNode.getData().getCoords()[3] + domNode.getData().getCoords()[1]);
    }


    public String toString() {
        return getDomNode().getData().getxPath() + " coords:(" + getX1() + "," + getY1() + "," + getX2() + "," + getY2() + ")";
    }
}
