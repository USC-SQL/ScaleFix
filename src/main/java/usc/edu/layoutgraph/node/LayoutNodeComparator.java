package usc.edu.layoutgraph.node;

import java.util.Comparator;


public class LayoutNodeComparator implements Comparator<LayoutNode> {

    @Override
    public int compare(LayoutNode a, LayoutNode b) {
        int diff = (int) (a.getArea() - b.getArea()); //small area
        return (diff == 0) ? (b.getDomNode().getData().getxPath().length() - a.getDomNode().getData().getxPath().length()) : diff; // big xPath
    }

}
