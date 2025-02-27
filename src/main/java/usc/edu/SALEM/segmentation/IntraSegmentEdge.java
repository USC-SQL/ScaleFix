package usc.edu.SALEM.segmentation;

import java.util.ArrayList;
import java.util.List;

import gatech.xpert.dom.DomNode;
//import usc.edu.SALEM.VHTree.Node;
//import gatech.xpert.dom.mfix.domTree.Node<DomNode>;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
public class IntraSegmentEdge {
    private Node<DomNode> e1;
    private Node<DomNode> e2;
    private List<EdgeLabel> labels;    // label from e1 to e2

    public IntraSegmentEdge() {
        this.labels = new ArrayList<>();
    }

    public IntraSegmentEdge(Node<DomNode> e1, Node<DomNode> e2) {
        this.e1 = e1;
        this.e2 = e2;
        this.labels = new ArrayList<>();
    }

    public IntraSegmentEdge(Node<DomNode> e1, Node<DomNode> e2, List<EdgeLabel> labels) {
        this.e1 = e1;
        this.e2 = e2;
        this.labels = labels;
    }

    public Node<DomNode> getE1() {
        return e1;
    }

    public void setE1(Node<DomNode> e1) {
        this.e1 = e1;
    }

    public Node<DomNode> getE2() {
        return e2;
    }

    public void setE2(Node<DomNode> e2) {
        this.e2 = e2;
    }

    public List<EdgeLabel> getLabels() {
        return labels;
    }

    public void setLabels(List<EdgeLabel> labels) {
        this.labels = labels;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((e1 == null) ? 0 : e1.hashCode());
        result = prime * result + ((e2 == null) ? 0 : e2.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IntraSegmentEdge other = (IntraSegmentEdge) obj;
        if (e1 == null) {
            if (other.e1 != null)
                return false;
        } else if (!e1.equals(other.e1))
            return false;
        if (e2 == null) {
            return other.e2 == null;
        } else return e2.equals(other.e2);
    }

    public IntraSegmentEdge copy() {
        /*** Trying to test intrasegment: TestAsethiticclass***/
        IntraSegmentEdge copyEdge = new IntraSegmentEdge();
        copyEdge.e1 = e1.copy();
        copyEdge.e2 = e2.copy();
        copyEdge.labels = new ArrayList<>(labels);
        return copyEdge;
    }
//    public IntraSegmentEdge copy() {
//        IntraSegmentEdge copyEdge = new IntraSegmentEdge();
//        copyEdge.e1 = e1.copy();
//        copyEdge.e2 = e2.copy();
//        copyEdge.labels = new ArrayList<>(labels);
//        return copyEdge;
//    }

    //    @Override
//    public String toString() {
//        return "<" + e1.getxPath() + ", " + e2.getxPath() + ", " + labels + ">";
//    }
    public String toString() {
        return "<" + e1.getData().getxPath() + ", " + e2.getData().getxPath() + ", " + labels + ">";
    }

}
