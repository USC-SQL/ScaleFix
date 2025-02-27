package usc.edu.layoutgraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.tinylog.Logger;
import usc.edu.layoutgraph.edge.NeighborEdge;
import usc.edu.layoutgraph.node.LayoutNode;
import usc.edu.layoutgraph.node.LayoutNodeComparator;
import gatech.xpert.dom.DomNode;
//import usc.edu.SALEM.VHTree.Node;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;

public class LayoutGraph {

    ArrayList<LayoutNode> vertices;
    ArrayList<NeighborEdge> edges;
    Map<String, LayoutNode> vMap;


    public LayoutGraph(Node<DomNode> root) {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        vMap = new HashMap<>();

        init(root);
    }

    public void init(Node<DomNode> root) {
        List<Node<DomNode>> worklist = new ArrayList<>();
        worklist.add(root);

        // Populate Nodes
        while (!worklist.isEmpty()) {
            Node<DomNode> node = worklist.remove(0);

            // This code is to normalize the xpath, so they are all lower case and indexed
            // TODO: remove this code once we update getMBR to report normalized xpath
//			if(Config.normalizePath){
//				String xpath = Utils.normalizeXPATH(node.getData().getxPath());
//				node.getData().setxPath(xpath);
//			}
//			if (root.getData().isLayout() && root.getData().isVisible()) {
            if (node.getData().isVisible()) {

                LayoutNode n = new LayoutNode(node);
                vertices.add(n);
                vMap.put(node.getData().getxPath(), n);
            }
            if (node.getChildren() != null) {
                worklist.addAll(node.getChildren());
            }
        }

        // Sort the vertices based on Area and DOM hierarchy
        Collections.sort(vertices, new LayoutNodeComparator());


        // complete graph.. all vertices connected to each other..
        for (LayoutNode v : vertices) {
            for (LayoutNode w : vertices) {

//                String id1=v.getDomNode().getData().getxPath();
//                String id2=w.getDomNode().getData().getxPath();
//                if (id1.equals(id2)) {
//                    Logger.debug("same node");
//                }
                if (!(v == w)) {

                    edges.add(new NeighborEdge(v, w));
                }
            }
        }
    }


    @SuppressWarnings("unused")
    private void addToMap(Map<LayoutNode, ArrayList<LayoutNode>> cMap, LayoutNode parent, LayoutNode a) {
        if (!cMap.containsKey(parent)) {
            cMap.put(parent, new ArrayList<LayoutNode>());
        }
        cMap.get(parent).add(a);
    }


    public ArrayList<LayoutNode> getVertices() {
        return vertices;
    }

    public void setVertices(ArrayList<LayoutNode> vertices) {
        this.vertices = vertices;
    }

    public ArrayList<NeighborEdge> getEdges() {
        return edges;
    }

    public void setEdges(ArrayList<NeighborEdge> edges) {
        this.edges = edges;
    }

    @Override
    public String toString() {
        StringBuffer out = new StringBuffer("Vertices:" + vertices.size());
        out.append("\nEdges   :" + edges.size());
        out.append("\n");
//        for (LayoutNode v : vertices) {
//            out.append(v.toString());
//            out.append("\n");
//        }
        for (NeighborEdge e : edges) {
//            if(e.getNode2().getDomNode().getData().getId()!=null){
                out.append(e.toString());
                out.append("\n*****\t"+ e.printProperties());
                out.append("\n distance: "+ e.getDistance());
//                out.append(
                out.append("-------------------------\n");
           // }
//            out.append(e.toString());
            //out.append("\n");

        }
        return out.toString();
    }

    public NeighborEdge findEdge(Node<DomNode> v, Node<DomNode> w) {
        for (NeighborEdge neighborEdge : edges) {
            if (neighborEdge.getNode1().getDomNode().getData() == v.getData() && neighborEdge.getNode2().getDomNode().getData() == w.getData()){
//                if (neighborEdge.getNode1().getDomNode().getData().getxPath()!= v.getData().getxPath()
//                        || neighborEdge.getNode2().getDomNode().getData().getxPath() != w.getData().getxPath()){
//                    return null;
//                }
                return neighborEdge;
            }
        }
        return null;
    }

    public NeighborEdge findEdge(String xpath1, String xpath2) {
        for (NeighborEdge neighborEdge : edges) {
            if (neighborEdge.getNode1().getDomNode().getData().getxPath().equals(xpath1)
                    && neighborEdge.getNode2().getDomNode().getData().getxPath().equals(xpath2))
                return neighborEdge;
        }
        return null;
    }
}
