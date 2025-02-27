package usc.edu.OwlEye.BuildModels;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
import usc.edu.OwlEye.UIModels.SRGNode;
import usc.edu.OwlEye.UIModels.VSRG;
import usc.edu.OwlEye.UIModels.VSRGNode;
import usc.edu.OwlEye.VH.UI;
import usc.edu.OwlEye.clustring.ClustersSorter;
import usc.edu.OwlEye.clustring.ElementsClusterer;

import java.util.*;

public class ConstructVSRG {


    protected static ConstructVSRG instance = null;


    private ConstructVSRG(String subjectID) {
    }

    public static ConstructVSRG getInstance() {

        if (instance == null)
            instance = new ConstructVSRG(null);  // do whatever else you need to here

        return instance;
    }

    public VSRG buildGraph(UI ui) {
     //   XMLUtils xmlTree = ui;
        // Initialize the SRG
        VSRG vsrg = new VSRG(ui.getUITitle());
        // (1) we first create the segments based  DBSCAN
        Map<String, List<Node<DomNode>>>  clusters = runDBSCAN(vsrg, ui);
        // (2) populate the vsrg graph based on the clusters created by DBSCAN
        // (2) create width relationship
     //   System.out.println(clusters);
        createVisualGraphBasedOnClusters(vsrg, clusters);
        return vsrg;

    }

    private void createVisualGraphBasedOnClusters(VSRG vsrg, Map<String, List<Node<DomNode>>>  clusters) {
        TreeMap<String, List<VSRGNode>> dependentNodesMap = vsrg.getDependentNodesMap();
        for (String clusterID : clusters.keySet()) {
            List<Node<DomNode>> cluster = clusters.get(clusterID);
          //  System.out.println("Cluster\t" + clusterID + "\t" + cluster.size());
            int size = cluster.size();

            for (  Node<DomNode> node : cluster) {
                //String xPath = node.getData().getxPath();
                VSRGNode vsrgNode = new VSRGNode(node, clusterID);
                if (!dependentNodesMap.containsKey(vsrgNode.getXpath())) {
                    ArrayList<VSRGNode> dependentNodes = new ArrayList<VSRGNode>();
                    for (  Node<DomNode> vNode : cluster) {
                        if (!vNode.getData().getxPath().equals(vsrgNode.getXpath())) {
                            dependentNodes.add(new VSRGNode(vNode, clusterID));
                        }
                    }
                    dependentNodesMap.put(vsrgNode.getXpath(), dependentNodes);
                }
            }


                }




    }

    private Map<String, List<Node<DomNode>>>  runDBSCAN(VSRG vsg, UI ui) {

        // 1. Get clustering for the original UI
        ElementsClusterer clusterer = new ElementsClusterer(ui.getXMLTree().getRoot());
        clusterer.perfomrClustering();
        Map<String, List<Node<DomNode>>> clusters = getClustersDBSCAN(null, clusterer);
        return clusters;
    }


    private static Map<String, List<Node<DomNode>>> getClustersDBSCAN(List<String> potentiallyFaultyElements, ElementsClusterer clusterer) {


        ArrayList<ArrayList<Node<DomNode>>> pageClusters = clusterer.getClustringResultsDomNodes();
        ArrayList<ArrayList<Node<DomNode>>> relevantClusterDomNodes;
       // System.out.println("Getting relevant clusters");
        if (potentiallyFaultyElements!=null &&potentiallyFaultyElements.size() > 0 && false) {
            relevantClusterDomNodes = ClustersSorter.getRelevantDomNodeClusters(pageClusters, new ArrayList<>(potentiallyFaultyElements));

        } else {
            relevantClusterDomNodes = pageClusters;  // Ali: I just want to test the pagenull
        }
      //  System.out.println("\nRelevant clusters: (size = " + relevantClusterDomNodes.size() + ")");
        int count = 0;
        Map<String, List<Node<DomNode>>> clusters = new HashMap<>();


        for (ArrayList<Node<DomNode>> c : relevantClusterDomNodes) {
        //    System.out.println("\nRelevant cluster " + (count++) + ". (size = " + c.size() + ") = ");
            clusters.put("C" + count++, c);
        }
       // System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

        return clusters;
    }

}

