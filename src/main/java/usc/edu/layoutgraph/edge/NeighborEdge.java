package usc.edu.layoutgraph.edge;


import java.io.Serializable;
import java.util.Map;

import gatech.xpert.dom.DomNode;
import org.tinylog.Logger;
import usc.edu.layoutgraph.LayoutGraph;
import usc.edu.layoutgraph.node.LayoutNode;
import usc.edu.layoutissue.Issue.IssueType;
//import usc.edu.SALEM.VHTree.Node;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
//import util.Utils;

public class NeighborEdge implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    LayoutNode node1, node2;
    boolean topBottom, bottomTop, leftRight, rightLeft;
    boolean topEdgeAligned, bottomEdgeAligned, leftEdgeAligned,
            rightEdgeAligned;
    boolean contains, boundedBy, intersect,contains2;

    boolean centered;

    double angleDegree;
    private double distance;


    //an edge is a nearest neighbor edge if the edge is among the closest from one of the corners or the centroid
    protected boolean isNearestNeighbor;

    int deltaW = 0, deltaH = 0;


    public NeighborEdge(LayoutNode a, LayoutNode b) {
        node1 = a;
        node2 = b;
        String id1=a.getDomNode().getData().getxPath();
        String id2=b.getDomNode().getData().getxPath();
//        if (id1.equalsIgnoreCase(id2)) {
//            Logger.debug("same node");
//        }
        populateProperties();
    }

    /*
     * lowest common ancestor (LCA) of two nodes v and w in a tree
     * is the lowest (i.e. deepest) node that has both v and w as descendant
     * please note that we define each node to be a descendant of itself
     * in other words, LCA is the first intersection of the paths from v and w to the root
     */
    public Node<DomNode> lowestCommonAncestor() {
        Node<DomNode> current1 = node1.getDomNode();
        while (current1 != null) {
            Node<DomNode> current2 = node2.getDomNode();
            while (current2 != null) {
                if (current2 == current1)
                    return current1;
                else current2 = current2.getParent();
            }
            current1 = current1.getParent();
        }
        return null;
    }


    //    | N  | Not North Not East
    //____N___N|____
    //  W | OBJ|E
    //____|____|_E__
    //	  | S  |
    //    | S S|

    public boolean isStrictTopBottom(LayoutNode a, LayoutNode b) {
        //left edge is north west & right edge is north east
        return (a.getY2() <= b.getY1()) &&
                ((a.getX2() >= b.getX1() && a.getX2() <= b.getX2())  //right edge is on north
                        || (a.getX1() >= b.getX1() && a.getX1() <= b.getX2())  //left edge is on north
                        || (a.getX1() <= b.getX1() && a.getX2() >= b.getX2()));
    }

    /**
     * @return the angle degree between the two MBRs
     */
    public double getAngleDegree() {
        return angleDegree;
    }

    /**
     * @param angleDegree the Angle to set in degree
     */
    public void setAngleDegree(double angleDegree) {
        this.angleDegree = angleDegree;
    }


    /**
     * @return the distance
     */
    public double getDistance() {
        return distance;
    }

    /**
     * @param distance the distance to set
     */
    protected void setDistance(double distance) {
        this.distance = distance;
    }

    protected boolean isTopBottom(LayoutNode a, LayoutNode b) {
        return a.getY2() <= b.getY1();
    }


    public boolean isStrictLeftRight(LayoutNode a, LayoutNode b) {
        //top edge is north west & bottom edge is south west
        int ax1 = a.getX1();
        int ax2 = a.getX2();
        int ay1 = a.getY1();
        int ay2 = a.getY2();

        int bx1 = b.getX1();
        int bx2 = b.getX2();
        int by1 = b.getY1();
        int by2 = b.getY2();
        boolean bool1 = a.getX2() <= b.getX1();
        boolean boo2 = a.getY2() >= b.getY1();
        boolean boo3 = a.getY2() <= b.getY2();
        boolean boo4 = a.getY1() >= b.getY1();
        boolean boo5 = a.getY1() <= b.getY2();
        boolean boo6 = a.getY1() <= b.getY1();
        boolean boo7 = a.getY2() >= b.getY2();
        return (a.getX2() <= b.getX1()) &&
                ((a.getY2() >= b.getY1() && a.getY2() <= b.getY2())  //bottom edge is on west
                        || (a.getY1() >= b.getY1() && a.getY1() <= b.getY2())  //top edge is on west
                        || (a.getY1() <= b.getY1() && a.getY2() >= b.getY2()));
    }

    protected boolean isLeftRight(LayoutNode a, LayoutNode b) {
        return a.getX2() <= b.getX1();
    }


    public void populateProperties() {

        if (isContains(node1, node2)) {
            setContains(true);
        }

        if (isContains(node2, node1)) {
            setBoundedBy(true);
        }

        if (isOverlap(node1, node2) && !isContains(node2, node1) && !isContains(node1, node2)) {
            setIntersect(true);
        }

        if (equals(node1.getX1(), node2.getX1(), deltaW)) {
            setLeftEdgeAligned(true);
        }

        if (equals(node1.getX2(), node2.getX2(), deltaW)) {
            setRightEdgeAligned(true);
        }

        if (equals(node1.getY1(), node2.getY1(), deltaH)) {
            setTopEdgeAligned(true);
        }

        if (equals(node1.getY2(), node2.getY2(), deltaH)) {
            setBottomEdgeAligned(true);
        }

        if (isLeftRight(node1, node2)) {
            setLeftRight(true);
        }

        if (isLeftRight(node2, node1)) {
            setRightLeft(true);
        }

        if (isTopBottom(node1, node2)) {
            setTopBottom(true);
        }

        if (isTopBottom(node2, node1)) {
            setBottomTop(true);
        }

        if (isCentered(node1, node2)) {
            setCentered(true);
        }

        computeDistance();
        computeDegree();
    }

    public String printProperties(){
        StringBuilder sb = new StringBuilder();
        sb.append("contains: ").append(isContains());
        sb.append("\nboundedBy: ").append(isBoundedBy());
        sb.append("\nintersect: ").append(isIntersect());
        sb.append("\nleftEdgeAligned: ").append(isLeftEdgeAligned());
        sb.append("\nrightEdgeAligned: ").append(isRightEdgeAligned());
        sb.append("\ntopEdgeAligned: ").append(isTopEdgeAligned());
        sb.append("\nbottomEdgeAligned: ").append(isBottomEdgeAligned());
        sb.append("\nleftRight: ").append(isLeftRight());
        sb.append("\nrightLeft: ").append(isRightLeft());
        sb.append("\ntopBottom: ").append(isTopBottom());
        sb.append("\nbottomTop: ").append(isBottomTop());
//        sb.append("centered: ").append(isCentered());
//        sb.append("distance: ").append(getDistance());
//        sb.append("angleDegree: ").append(getAngleDegree());
        return sb.toString();
    }
    private boolean isCentered(LayoutNode node1, LayoutNode node2) {
        //checking the horizontal (x) centring only
        return node1.getCenter()[0] == node2.getCenter()[0];
    }


    private void computeDistance() {
        setDistance(Math.sqrt(Math.pow((node2.getCenter()[0] - node1.getCenter()[0]), 2) +
                Math.pow((node2.getCenter()[1] - node1.getCenter()[1]), 2)));
    }

    private void computeDegree() {
        double yDiff = node1.getCenter()[1] - node2.getCenter()[1];
        double xDiff = node1.getCenter()[0] - node2.getCenter()[0];
        angleDegree = Math.toDegrees(Math.atan2(yDiff, xDiff));
        angleDegree = (angleDegree + 360) % 360;
    }

    private boolean isOverlap(LayoutNode node1, LayoutNode node2) {
        return node1.getX1() < node2.getX2() && node1.getX2() > node2.getX1() &&
                node1.getY1() < node2.getY2() && node1.getY2() > node2.getY1();
    }


    @SuppressWarnings("unused")
    private boolean isPartiallyInside(LayoutNode node1, LayoutNode node2) {
        boolean ulCornerInside, urCornerInside, llCornerInside, lrCornerInside;
        ulCornerInside = isPointInsideNodesMBR(node2.getX1(), node2.getY1(), node1);
        urCornerInside = isPointInsideNodesMBR(node2.getX2(), node2.getY1(), node1);
        llCornerInside = isPointInsideNodesMBR(node2.getX1(), node2.getY2(), node1);
        lrCornerInside = isPointInsideNodesMBR(node2.getX2(), node2.getY2(), node1);

        if (ulCornerInside && urCornerInside && llCornerInside && lrCornerInside) {
            return false;
        }
        // at least one of the corners is inside, but not all of them
        else return ulCornerInside || urCornerInside || llCornerInside || lrCornerInside;
        // total disjoint
    }


    private boolean isPointInsideNodesMBR(int x, int y, LayoutNode node) {
        return x >= node.getX1() && x <= node.getX2() && y >= node.getY1() && y <= node.getY2();
    }

    @SuppressWarnings("unused")
    private boolean isPointStrictlyInsideNodesMBR(int x, int y, LayoutNode node) {
        return x > node.getX1() && x < node.getX2() && y > node.getY1() && y < node.getY2();

    }

    private boolean isContains(LayoutNode n1, LayoutNode n2) {
        int n1Right = n1.getX2();
        int n1Left = n1.getX1();
        int n1Top = n1.getY1();
        int n1Btm = n1.getY2();

        int n2Right = n2.getX2();
        int n2Left = n2.getX1();
        int n2Top = n2.getY1();
        int n2Btm = n2.getY2();

        //compute paddings in case of input field TODO: make it general
        //Todo: ALi check
//		if(n1.getDomNode().getData().getTagName().toUpperCase().equals("INPUT") &&
//				n2.getDomNode().getData().isInputText()){
//
//			n1Left = n1.getDomNode().getData().getInnerCoords()[0];
//			n1Top = n1.getDomNode().getData().getInnerCoords()[1];
//			n1Right = n1.getDomNode().getData().getInnerCoords()[2];
//			n1Btm = n1.getDomNode().getData().getInnerCoords()[3];
//		}


        return n1Left <= n2Left && n1Top <= n2Top
                && n1Right >= n2Right && n1Btm >= n2Btm;
    }

    public boolean isNearestNeighbor() {
        return isNearestNeighbor;
    }

    public void setNearestNeighbor(boolean isNearestNeighbor) {
        this.isNearestNeighbor = isNearestNeighbor;
    }

    public LayoutNode getNode1() {
        return node1;
    }

    public LayoutNode getNode2() {
        return node2;
    }

    @Override
    public String toString() {
        return this.node1.getDomNode().getData().getxPath() + "->"
                + this.node2.getDomNode().getData().getxPath() + " -- neighbors? " + isNearestNeighbor;
    }

    // Setters and Getters

    public boolean isTopBottom() {
        return topBottom;
    }

    public void setTopBottom(boolean topBottom) {
        this.topBottom = topBottom;
    }

    public boolean isBottomTop() {
        return bottomTop;
    }

    public void setBottomTop(boolean bottomTop) {
        this.bottomTop = bottomTop;
    }

    public boolean isLeftRight() {
        return leftRight;
    }

    public void setLeftRight(boolean leftRight) {
        this.leftRight = leftRight;
    }

    public boolean isRightLeft() {
        return rightLeft;
    }

    public void setRightLeft(boolean rightLeft) {
        this.rightLeft = rightLeft;
    }

    public boolean isCentered() {
        return centered;
    }

    public void setCentered(boolean b) {
        centered = true;
    }

    public boolean isTopEdgeAligned() {
        return topEdgeAligned;
    }

    public void setTopEdgeAligned(boolean topEdgeAligned) {
        this.topEdgeAligned = topEdgeAligned;
    }

    public boolean isBottomEdgeAligned() {
        return bottomEdgeAligned;
    }

    public void setBottomEdgeAligned(boolean bottomEdgeAligned) {
        this.bottomEdgeAligned = bottomEdgeAligned;
    }

    public boolean isLeftEdgeAligned() {
        return leftEdgeAligned;
    }

    public void setLeftEdgeAligned(boolean leftEdgeAligned) {
        this.leftEdgeAligned = leftEdgeAligned;
    }

    public boolean isRightEdgeAligned() {
        return rightEdgeAligned;
    }

    public void setRightEdgeAligned(boolean rightEdgeAligned) {
        this.rightEdgeAligned = rightEdgeAligned;
    }

    public boolean isContains() {
        return contains;
    }

    public void setContains(boolean contains) {
        this.contains = contains;
    }

    public boolean isBoundedBy() {
        return boundedBy;
    }

    public void setBoundedBy(boolean boundedBy) {
        this.boundedBy = boundedBy;
    }

    public boolean isIntersect() {
        return intersect;
    }

    public void setIntersect(boolean intersect) {
        this.intersect = intersect;
    }

    public boolean getValueForIssueType(IssueType issueType) {
        switch (issueType) {
            case BOTTOMTOP:
                return bottomTop;
            case TOPBOTTOM:
                return topBottom;
            case RIGHTLEFT:
                return rightLeft;
            case LEFTRIGHT:
                return leftRight;
            case CONTAINMENT:
                return contains;
            case CONTAINMENT2:
                return contains2;
            case INTERSECTION:
                return intersect;
            case RIGHTEDGEALIGNMENT:
                return rightEdgeAligned;
            case LEFTEDGEALIGNMENT:
                return leftEdgeAligned;
            case BOTTOMEDGEALIGNMENT:
                return bottomEdgeAligned;
            case TOPEDGEALIGNMENT:
                return topEdgeAligned;
            case CENTERED:
                return centered;
            default:
                return false;
        }
    }

    public boolean isReverseEdge(NeighborEdge edge) {
        return this.node1 == edge.node2 && this.node2 == edge.node1;
    }

    public boolean isSubsumes(NeighborEdge edge) {
        if (this.node1 == edge.node1 && this.node2 == edge.node2)
            return true;
        if (this.node1 == edge.node1 && this.node2.contains(edge.node2))
            return true;
        if (this.node2 == edge.node2 && this.node1.contains(edge.node1))
            return true;
        return this.node1.contains(edge.node1) && this.node2.contains(edge.node2);
    }

    protected boolean equals(int a, int b, int delta) {
        return a <= b + delta && a >= b - delta;
    }

    public boolean isReverseSubsumes(NeighborEdge edge) {
        NeighborEdge reversedEdge = new NeighborEdge(edge.node2, edge.node1);
        return isSubsumes(reversedEdge);
    }

    public int getRelationshipsSize() {
        int size = 0;
        if (isContains())
            size++;
        if (isIntersect())
            size++;
        if (isLeftEdgeAligned())
            size++;
        if (isRightEdgeAligned())
            size++;
        if (isTopEdgeAligned())
            size++;
        if (isBottomEdgeAligned())
            size++;
        if (isLeftRight())
            size++;
        if (isRightLeft())
            size++;
        if (isTopBottom())
            size++;
        if (isBottomTop())
            size++;
        if (isCentered())
            size++;
        return size;
    }

    public NeighborEdge getMatchedEdge(Map<Node<DomNode>, Node<DomNode>> matchedNodes, LayoutGraph layoutGraph2) {
        Node<DomNode> v1 = this.getNode1().getDomNode();
        Node<DomNode> w1 = this.getNode2().getDomNode();

        Node<DomNode> v2 = matchedNodes.get(v1);
        Node<DomNode> w2 = matchedNodes.get(w1);
        NeighborEdge matchedEdge;
        if (v2 == null || w2 == null)
            matchedEdge = null;
        else
            matchedEdge = layoutGraph2.findEdge(v2, w2);
        return matchedEdge;
    }


}
