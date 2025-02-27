package usc.edu.OwlEye.fitness;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.MatchResult2;
import gatech.xpert.dom.Matcher;
import gatech.xpert.dom.Node;
import org.tinylog.Logger;
import usc.edu.OwlEye.OwlEye;
import usc.edu.OwlEye.VH.UI;

import java.util.*;

public class MinimumChangesObjective {

    public double objectiveScore;
    public double sizeRawScore;
    public double locationRawScore;
    private Map<String, Node<DomNode>> doms;

    private MatchResult2 matchResult; // stores only xpath to xpath mapping
    private Map<Node<DomNode>, Node<DomNode>> matchedNodes; //stores DomNode to DomNode mapping
    private List<Node<DomNode>> unmatchedNodes;


    private Map<String, Node<DomNode>> doms2; // for largest
    private MatchResult2 matchResult2; // stores only xpath to xpath mapping
    private Map<Node<DomNode>, Node<DomNode>> matchedNodes2; //stores DomNode to DomNode mapping
    private final String[] keys = {"original", "repair"};

    public MinimumChangesObjective() {
        double objectiveScore = Double.MAX_VALUE;
    }

    public double calculateObjectiveScore(Node<DomNode> newLayoutRoot) {

        // TODO: implement this method
        // (1) total change in the location based on bounds of the elements compared to the baseline
        // (2) total change in the size of the elements compared to the baseline


        UI uiDefault = OwlEye.getOriginalDefaultUI();
        UI uiLargest = OwlEye.getOriginalLargestUI();
        Node<DomNode> originalRoot = uiDefault.getXMLTree().getRoot();
        Node<DomNode> originalLargeRoot = uiLargest.getXMLTree().getRoot();

        // Node<DomNode> originalRoot = XMLUtils.getRoot();
        Node<DomNode> repairedRoot = newLayoutRoot;
        this.doms = new HashMap<>();
        this.doms2 = new HashMap<>();

        // maps each key to the root node of the page
        Node<DomNode> baselineRoot = originalRoot;
        Node<DomNode> putRoot = repairedRoot;
        this.doms.put(keys[0], baselineRoot);
        this.doms.put(keys[1], putRoot);

        //creates xpath to xpath matching and saves it in MatchResult object
        this.matchResult = matchDoms(doms);
        this.matchedNodes = matchResult.getMatched();
        this.unmatchedNodes= matchResult.getUnmatched1();// Right now we are only considering the unmatched nodes from the original layout
        List<Node<DomNode>> unmatchedNodes2 = matchResult.getUnmatched2();
        Logger.trace("Unmatched nodes from the original layout: "+unmatchedNodes.size());  // key is the original VH, value is the repaired VH
        Logger.debug("Matched nodes from the original layout: "+matchedNodes.size());
        Logger.trace("Unmatched nodes from the new layout: "+unmatchedNodes2.size());



        // maps each key to the large root node of the page
        Node<DomNode> baselineLargeRoot = originalLargeRoot;
//        Node<DomNode> putRoot = repairedRoot;
        this.doms2.put(keys[0], baselineLargeRoot);
        this.doms2.put(keys[1], putRoot);


        //creates xpath to xpath matching and saves it in MatchResult object for largest layout
        this.matchResult2 = matchDoms(doms2);
        this.matchedNodes2 = matchResult2.getMatched();
//        this.unmatchedNodes2= matchResult.getUnmatched1();// Right now we are only considering the unmatched nodes from the original layout
//        List<Node<DomNode>> unmatchedNodes2 = matchResult.getUnmatched2();
        Logger.trace("Unmatched nodes from the original large layout: "+unmatchedNodes2.size());  // key is the original VH, value is the repaired VH
        Logger.debug("Matched nodes from the original large layout: "+matchedNodes2.size());
//        Logger.trace("Unmatched nodes from the new layout: "+unmatchedNodes2.size());

        boolean newApproach = true;
        double totalChange = 0;
        HashMap<String, Double> sizeChange = new HashMap<>();
        HashMap<String, Double> locationChange = new HashMap<>();
        if(newApproach) {
            // 1- location of the elements based on origianl layout
            //HashMap<String, Double> locationChange = new HashMap<>();
            for (Map.Entry<Node<DomNode>, Node<DomNode>> entry : matchedNodes.entrySet()) {
                Node<DomNode> originalNode = entry.getKey();
                Node<DomNode> repairedNode = entry.getValue();
                int[] originalBounds = originalNode.getData().getBounds();
                int[] repairedBounds = repairedNode.getData().getBounds();

                double[] locationRates = calculateLocationChange(originalBounds, repairedBounds); // with
                double locationChangeValue = ((locationRates[0] + locationRates[1]) / 2) * 100; //
                locationChange.put(originalNode.getData().getId(), locationChangeValue);
            }
            // 2- size of the elements based on origianl layout
           // HashMap<String, Double> sizeChange = new HashMap<>();
            for (Map.Entry<Node<DomNode>, Node<DomNode>> entry : matchedNodes2.entrySet()) {
                Node<DomNode> originalNode = entry.getKey();
                Node<DomNode> repairedNode = entry.getValue();
                int[] originalBounds = originalNode.getData().getBounds();
                int[] repairedBounds = repairedNode.getData().getBounds();
                String id = originalNode.getData().getId();
                int orgHeight = originalNode.getData().height;
                int orgWidth = originalNode.getData().width;
                int repHeight = repairedNode.getData().height;
                int repWidth = repairedNode.getData().width;
                double sizeChangeValue = calculateSizeChange(orgHeight, orgWidth, repHeight, repWidth);
                double originalSize = orgHeight + orgWidth;
                double rateOfSizeChange = sizeChangeValue / originalSize * 100;
                if (rateOfSizeChange > 100) {
                    Logger.debug("Size change is greater than 1");
                    rateOfSizeChange = 100;
//               System.exit(0);
                }
                sizeChange.put(originalNode.getData().getId(), rateOfSizeChange);
            }
        }
        else{ // old approach
        // iterate through the matched nodes and calculate the total change in the location and size of the elements
       // double totalChange = 0;
       // HashMap<String, Double> sizeChange = new HashMap<>();
        //HashMap<String, Double> locationChange = new HashMap<>();
        for (Map.Entry<Node<DomNode>, Node<DomNode>> entry : matchedNodes.entrySet()) {
            Node<DomNode> originalNode = entry.getKey();
            Node<DomNode> repairedNode = entry.getValue();
            int[] originalBounds = originalNode.getData().getBounds();
            int[] repairedBounds = repairedNode.getData().getBounds();
            String id = originalNode.getData().getId();
            int orgHeight = originalNode.getData().height;
            int orgWidth = originalNode.getData().width;
            int repHeight = repairedNode.getData().height;
            int repWidth = repairedNode.getData().width;


            double sizeChangeValue = calculateSizeChange(orgHeight, orgWidth, repHeight, repWidth);
            double[] locationRates = calculateLocationChange(originalBounds, repairedBounds); // with
            double locationChangeValue = ((locationRates[0] + locationRates[1])/2) *100; // height and width change rates
           Logger.trace(id + "\t\t\th: \"" + locationRates[0] + "\" w: \"" + locationRates[1]+"\"");
           double originalSize= orgHeight+orgWidth;
           double rateOfSizeChange=sizeChangeValue/originalSize*100;
           if(rateOfSizeChange>100){
              Logger.debug("Size change is greater than 1");
              rateOfSizeChange=100;
//               System.exit(0);
           }
            sizeChange.put(originalNode.getData().getId(), rateOfSizeChange);
           // sizeChange.put(originalNode.getData().getId(), sizeChangeValue);

            locationChange.put(originalNode.getData().getId(), locationChangeValue);
        }
        }
        double sizeChangeTotal = 0;
        double locationChangeTotal = 0;
        for (Map.Entry<String, Double> entry : sizeChange.entrySet()) {
            sizeChangeTotal += entry.getValue();
        }
        for (Map.Entry<String, Double> entry : locationChange.entrySet()) {
            locationChangeTotal += entry.getValue();
        }
        double sizeChangeAverage = sizeChangeTotal / sizeChange.size();
        double locationChangeAverage = locationChangeTotal / locationChange.size();
        // weighted sum of the size and location change
        //totalChange = (sizeChangeAverage * 0.45) + (locationChangeAverage * 0.55);
         totalChange = (locationChangeAverage * 0.55); //commented this and uncommented the above line to test repeat April 3rd
        this.sizeRawScore = sizeChangeAverage;
        this.locationRawScore = locationChangeAverage;
        this.objectiveScore = totalChange;
    return totalChange;
    }

    private double [] calculateLocationChange(int [] originalBounds, int [] repairedBounds) {


        String originalBounds1 = String.valueOf(originalBounds[0]);
        String originalBounds2 = String.valueOf(originalBounds[1]);
        String originalBounds3 = String.valueOf(originalBounds[2]);
        String originalBounds4 = String.valueOf(originalBounds[3]);
        String repairedBounds1 = String.valueOf(repairedBounds[0]);
        String repairedBounds2 = String.valueOf(repairedBounds[1]);
        String repairedBounds3 = String.valueOf(repairedBounds[2]);
        String repairedBounds4 = String.valueOf(repairedBounds[3]);

//        double originalX = Double.parseDouble(originalBounds1) + (Double.parseDouble(originalBounds3) / 2);
//        double originalY = Double.parseDouble(originalBounds2) + (Double.parseDouble(originalBounds4) / 2);
//        double repairedX = Double.parseDouble(repairedBounds1) + (Double.parseDouble(repairedBounds3) / 2);
//        double repairedY = Double.parseDouble(repairedBounds2) + (Double.parseDouble(repairedBounds4) / 2);
    // trying to focus on x1 and y1 instead of the center of the element
        double originalX = Double.parseDouble(originalBounds1);
        double originalY = Double.parseDouble(originalBounds2);
        double repairedX = Double.parseDouble(repairedBounds1) ;
        double repairedY = Double.parseDouble(repairedBounds2);
        double heightChange = Math.abs(originalY - repairedY);
        double widthChange = Math.abs(originalX - repairedX);
        double heightChangeRate= heightChange / 2344;// 2344 is the height of the screen of the device
        double widthChangeRate= widthChange / 1440;  // 1440 is the width of the screen of the device
        double locationChangeValueAbs = Math.abs(originalX - repairedX) + Math.abs(originalY - repairedY);
        double locationChangeValue = Math.sqrt(Math.pow(originalX - repairedX, 2) + Math.pow(originalY - repairedY, 2));
        return new double[]{heightChangeRate,widthChangeRate};
    }

    private double calculateSizeChange(int orgHeight,int orgWidth,int repHeight,int repWidth) {


        double originalWidth = orgWidth;
        double originalHeight = orgHeight;
        double repairedWidth = repWidth;
        double repairedHeight = repHeight;
        double Test=Math.abs(originalWidth - repairedWidth) + Math.abs(originalHeight - repairedHeight);
        double sizeChangeValue = Math.sqrt(Math.pow(originalWidth - repairedWidth, 2) + Math.pow(originalHeight - repairedHeight, 2));
        return Test;
    }

    public MatchResult2 matchDoms(Map<String, Node<DomNode>> doms) {

        Matcher matcher = new Matcher();
        return matcher.doMatch2(doms.get("original"), doms.get("repair"));
        //return res;
    }

    public static Map<String, Node<DomNode>> getElementSetFromDomTree(Node<DomNode> root) {
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

    public double getObjectiveScore() {
        return objectiveScore;
    }
    public void setObjectiveScore(double objectiveScore) {
        this.objectiveScore = objectiveScore;
    }
}
