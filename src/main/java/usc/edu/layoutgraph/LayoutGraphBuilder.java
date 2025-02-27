package usc.edu.layoutgraph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gatech.xpert.dom.*;
import gatech.xpert.dom.DomNode;
//import usc.edu.SALEM.VHTree.Node;
import gatech.xpert.dom.Node;
//import gatech.xpert.dom.Rectangle;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


//import config.Config;
import org.tinylog.Logger;
import usc.edu.OwlEye.OwlEye;
import usc.edu.layoutgraph.edge.NeighborEdge;
import usc.edu.layoutissue.Issue;


public class LayoutGraphBuilder implements Serializable {

    /*
     *
     */
    private static final long serialVersionUID = 1L;

    private Map<String, Node<DomNode>> doms;
    private MatchResult2 matchResult2; // stores only xpath to xpath mapping
    private Map<Node<DomNode>, Node<DomNode>> matchedNodes; //stores DomNode to DomNode mapping
    private LayoutGraph baseLG;
    private LayoutGraph putLG;

    //contains all elements for the baseline and put including non layout Elements;
    private Map<String, Node<DomNode>> baselineElements;
    private Map<String, Node<DomNode>> testpageElements;


//    final static Logger logger = LoggerFactory.getLogger(LayoutGraphBuilder.class);


    private final String[] keys = {"original", "repair"};

    private String url1;
    private String url2;


//		public LayoutGraphBuilder(FirefoxDriver driver1, FirefoxDriver driver2){
//			init(driver1, driver2);
//		}

    public LayoutGraphBuilder(Node<DomNode> rootOfGeneratedUI) {
       // we already have the original layout built so now it is time to build the repair layout

        initNew(rootOfGeneratedUI);
    }
public LayoutGraphBuilder(Node<DomNode> originalNode,String crawler) {
    // Just for crawl checking single VTree

    initSingle(originalNode);
}
    public LayoutGraphBuilder(Node<DomNode> originalNode, Node<DomNode> repairedNode) {
        init(originalNode, repairedNode);
    }



    public void initSingle(Node<DomNode> originalNode) {
        this.doms = new HashMap<>();
        Node<DomNode> baselineRoot = originalNode;
        this.doms.put(keys[0], baselineRoot);
        this.baselineElements = getElementSetFromDomTree(baselineRoot);
        this.baseLG = new LayoutGraph(baselineRoot);
    }

    public void initNew( Node<DomNode> repairedNode) {

        this.doms = new HashMap<String, Node<DomNode>>();

        // maps each key to the root node of the page
        Node<DomNode> baselineRoot = OwlEye.getOriginalDefaultUI().getXMLTree().getRoot();
        Node<DomNode> putRoot = repairedNode;

        this.doms.put(keys[0], baselineRoot);
        this.doms.put(keys[1], putRoot);

        //initialize lists of baseline and put elements
        if(OwlEye.originalDefaultUIElements == null) {
            this.baselineElements = getElementSetFromDomTree(baselineRoot);
            OwlEye.originalDefaultUIElements = this.baselineElements;
        }
        else {
            this.baselineElements = OwlEye.originalDefaultUIElements;
        }
//        this.baselineElements = OwlEye.originalDefaultUIElements;
        this.testpageElements = getElementSetFromDomTree(putRoot);


        //creates xpath to xpath matching and saves it in MatchResult object
        this.matchResult2 = matchDoms(doms);
        this.matchedNodes = matchResult2.getMatched();

        //create layoutgraph objects
        //create layoutgraph objects
      //  if(OwlEye.originalDefaultUILayoutGraph==null){
            this.baseLG = new LayoutGraph(baselineRoot);
           // OwlEye.setOriginalDefaultUILayoutGraph(this.baseLG);
      // }
      //  else{
          //  this.baseLG = OwlEye.originalDefaultUILayoutGraph;
      //  }
//        this.baseLG = new LayoutGraph(baselineRoot);
        this.putLG = new LayoutGraph(putRoot);

    }
    public void init(Node<DomNode> originalNode, Node<DomNode> repairedNode) {

        this.doms = new HashMap<String, Node<DomNode>>();

        // maps each key to the root node of the page
        Node<DomNode> baselineRoot = originalNode;
        Node<DomNode> putRoot = repairedNode;

        this.doms.put(keys[0], baselineRoot);
        this.doms.put(keys[1], putRoot);

        //initialize lists of baseline and put elements
        this.baselineElements = getElementSetFromDomTree(baselineRoot);
        this.testpageElements = getElementSetFromDomTree(putRoot);


        //creates xpath to xpath matching and saves it in MatchResult object
        this.matchResult2 = matchDoms(doms);
        this.matchedNodes = matchResult2.getMatched();

        //create layoutgraph objects
        this.baseLG = new LayoutGraph(baselineRoot);
        this.putLG = new LayoutGraph(putRoot);

    }




    public MatchResult2 matchDoms(Map<String, Node<DomNode>> doms) {

        Matcher matcher = new Matcher();
        return matcher.doMatch2(doms.get("original"), doms.get("repair"));
    }

    private Map<String, Node<DomNode>> getElementSetFromDomTree(Node<DomNode> root) {
        Map<String, Node<DomNode>> pageElements = new HashMap<String, Node<DomNode>>();
        List<Node<DomNode>> worklist = new ArrayList<>();
        worklist.add(root);

        // Populate Nodes
        while (!worklist.isEmpty()) {
            Node<DomNode> node = worklist.remove(0);

            // This code is to normalize the xpath, so they are all lower case and indexed
            // TODO: remove this code once we update getMBR to report normalized xpaths from the beginning
//				if(Config.normalizePath){
//					String xpath = Utils.normalizeXPATH(node.getxPath());
//					node.setxPath(xpath);
//				}
            pageElements.put(node.getData().getxPath(), node);
            if (node.getChildren() != null) {
                worklist.addAll(node.getChildren());
            }
        }
        return pageElements;
    }


//		public double getMatchRatio(){
//			return matchResult.getMatchRatio();
//		}

    public ArrayList<Issue> compareLayoutGraphs() {

        Map<String, String> matchedXpaths = new HashMap<>(); //stores DomNode to DomNode mapping


        Map<Node<DomNode>, Node<DomNode>> matched = matchResult2.getMatched();
        for (Map.Entry<Node<DomNode>, Node<DomNode>> entry : matched.entrySet()) {
            Node<DomNode> original = entry.getKey();
            Node<DomNode> repaired = entry.getValue();
            matchedXpaths.put(original.getData().getxPath(), repaired.getData().getxPath());
            // do something with key and/or tab
        }
        for (String baseXPath : matchedXpaths.keySet()) {
            Logger.trace(baseXPath + " <is matched with> " + matchedXpaths.get(baseXPath));
        }
        //TimeMeasurement.toc();
        //TimeMeasurement.tic("BuildingGraphs");


        LayoutGraph lg1 = baseLG;
        LayoutGraph lg2 = putLG;


        // handle RTL languages by converting putLG;
//			if(Config.CHECK_RTL){
//				if(isMirrored(baseLG, putLG)){
//					convertRTLPutLayoutGraph(baseLG,putLG);
//				}
//			}
    //    printLayoutGraph(lg1);
        //printLayoutGraph(lg2);

        //TimeMeasurement.toc();
        //TimeMeasurement.tic("ComparingGraphs");
//tvDailyFoodFact

        LayoutGraphComparator lgsComparator = new LayoutGraphComparator(matchedNodes, lg1, lg2);

        ArrayList<Issue> layoutIssues = new ArrayList<Issue>();

        layoutIssues = lgsComparator.compareGraphs();

        return layoutIssues;
    }


    private void printLayoutGraph(LayoutGraph lg) {
        Logger.debug("***** LAYOUT GRAPH *****");
        Logger.debug(lg.toString());
    }




    public int GetNoOfRelations() {
        int allRelations = 0;
        LayoutGraph lg1 = new LayoutGraph(doms.get(keys[0])),
                lg2 = new LayoutGraph(doms.get(keys[1]));

        ArrayList<NeighborEdge> baselineEdges = lg1.edges;
        ArrayList<NeighborEdge> putEdges = lg2.edges;
        for (int i = 0; i < 2; i++) {
            ArrayList<NeighborEdge> lgEdges;
            if (i == 0)
                lgEdges = baselineEdges;
            else
                lgEdges = putEdges;

            for (NeighborEdge neighborEdge : lgEdges) {
                if (neighborEdge.isNearestNeighbor()) {
                    if (neighborEdge.isContains())
                        allRelations++;
                    if (neighborEdge.isIntersect())
                        allRelations++;
                    if (neighborEdge.isLeftEdgeAligned())
                        allRelations++;
                    if (neighborEdge.isRightEdgeAligned())
                        allRelations++;
                    if (neighborEdge.isTopEdgeAligned())
                        allRelations++;
                    if (neighborEdge.isBottomEdgeAligned())
                        allRelations++;
                    if (neighborEdge.isLeftRight())
                        allRelations++;
                    if (neighborEdge.isRightLeft())
                        allRelations++;
                    if (neighborEdge.isTopBottom())
                        allRelations++;
                    if (neighborEdge.isBottomTop())
                        allRelations++;
                }

            }

        }
        return allRelations;
    }

    public boolean isMirrored(LayoutGraph baseLine, LayoutGraph put) {
        int matchingRelations = 0;
        int mirroredRelations = 0;
        boolean isMirrored = false;
        Map<Node<DomNode>, Node<DomNode>> matchMap = matchedNodes;

        for (NeighborEdge baseEdge : baseLine.getEdges()) {
            NeighborEdge putEdge = baseEdge.getMatchedEdge(matchMap, put);
            if (putEdge == null)
                continue;
            if (baseEdge.isRightLeft() && putEdge.isRightLeft())
                matchingRelations++;
            if (baseEdge.isLeftRight() && putEdge.isLeftRight())
                matchingRelations++;
            if (baseEdge.isRightLeft() && putEdge.isLeftRight())
                mirroredRelations++;
            if (baseEdge.isLeftRight() && putEdge.isRightLeft())
                mirroredRelations++;
        }

        isMirrored = mirroredRelations > matchingRelations;

        return isMirrored;

    }


    public LayoutGraph getBaselineLG() {
        return baseLG;
    }

    public LayoutGraph getTestPageLG() {
        return putLG;
    }

    public ArrayList<Node<DomNode>> getBaselineElements() {
        ArrayList<Node<DomNode>> baseElements = new ArrayList<Node<DomNode>>(baselineElements.values());
        return baseElements;
    }

    public ArrayList<Node<DomNode>> getTestpageElements() {
        ArrayList<Node<DomNode>> testElements = new ArrayList<Node<DomNode>>(testpageElements.values());
        return testElements;
    }

    public Map<Node<DomNode>, Node<DomNode>> getMatchedNodes() {
        return this.matchedNodes;
    }


}


