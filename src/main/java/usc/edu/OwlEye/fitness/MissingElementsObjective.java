package usc.edu.OwlEye.fitness;

import gatech.xpert.dom.*;
import org.tinylog.Logger;
import usc.edu.OwlEye.OwlEye;
import usc.edu.OwlEye.VH.UI;
import usc.edu.layoutgraph.LayoutGraph;
import usc.edu.layoutgraph.LayoutGraphBuilder;
import usc.edu.layoutissue.Issue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static usc.edu.OwlEye.util.Utils.skipMissingInScrollView;

public class MissingElementsObjective {

    private Map<String, Node<DomNode>> doms;
    private MatchResult2 matchResult2; // stores only xpath to xpath mapping
    private Map<Node<DomNode>, Node<DomNode>> matchedNodes; //stores DomNode to DomNode mapping
    private List<Node<DomNode>> unmatchedNodes;
    private LayoutGraph baseLG;
    private LayoutGraph putLG;
    private int numberOfMissingElements;
    //contains all elements for the baseline and put including non layout Elements;
    private Map<String, Node<DomNode>> baselineElements;
    private Map<String, Node<DomNode>> testpageElements;


    private final String[] keys = {"original", "repair"};

//    private double MissingElements =Double.MIN_VALUE;
    private ArrayList<Issue> violations;
    private double FormulaScore;
    private double rawScore;


    public MissingElementsObjective() {

        this.FormulaScore = Double.MAX_VALUE;
        this.rawScore = Double.MAX_VALUE;
        violations = new ArrayList<>();
        numberOfMissingElements = 0;
    }


    public double calculateMissingElementsScore(Node<DomNode> newLayoutRoot) {
        double score = 0.0;
        UI uiDefault = OwlEye.getOriginalDefaultUI();
        Node<DomNode> originalRoot= uiDefault.getXMLTree().getRoot();
        // Node<DomNode> originalRoot = XMLUtils.getRoot();
        Node<DomNode> repairedRoot = newLayoutRoot;

        int numberOfMissingElements = 0;
        double areaOfMissingElements = 0.0;
        int numberOfInteractiveElements = 0;
        double areaOfInteractiveElements = 0.0;
        int numberOfElementsWithText = 0;
        double areaOfElementsWithText = 0.0;
        int numberOfNonInteractiveNoText = 0;
        double areaOfNonInteractiveNoText = 0.0;
        // Do the matching to find what are the missing elements
        this.doms = new HashMap<>();


        // maps each key to the root node of the page
        Node<DomNode> baselineRoot = originalRoot;
        Node<DomNode> putRoot = repairedRoot;

        this.doms.put(keys[0], baselineRoot);
        this.doms.put(keys[1], putRoot);

        //initialize lists of baseline and put elements
        this.baselineElements =getElementSetFromDomTree(baselineRoot);
        this.testpageElements = getElementSetFromDomTree(putRoot);


        //creates xpath to xpath matching and saves it in MatchResult object
        this.matchResult2 = matchDoms(doms);
        this.matchedNodes = matchResult2.getMatched();
        this.unmatchedNodes=matchResult2.getUnmatched1();// Right now we are only considering the unmatched nodes from the original layout
        List<Node<DomNode>> unmatchedNodes2 = matchResult2.getUnmatched2();
        Logger.trace("Unmatched nodes from the original layout: "+unmatchedNodes.size());
        Logger.trace("Matched nodes from the original layout: "+matchedNodes.size());
        Logger.trace("Unmatched nodes from the new layout: "+unmatchedNodes2.size());

        //calculate the score
        for (Node<DomNode> node : unmatchedNodes) {
            boolean isPartOfScrollView=skipMissingInScrollView(node);
            if (isPartOfScrollView) {
                //continue;
            }
            numberOfMissingElements++;
            double area = node.getData().getAreaInDP();// we want the area in DP
             if(isNodeInteractive(node)!=null) // it is interactive so we add more penality
             {
                    numberOfInteractiveElements++;
                    areaOfInteractiveElements+=area;
             }
             else
             {
                 String text=node.getData().getText();
                 if(text!=null && !text.isEmpty())
                 {
                     numberOfElementsWithText++;
                        areaOfElementsWithText+=area;
                 }else{
                     // not interactive and no text
                     numberOfNonInteractiveNoText++;
                     areaOfNonInteractiveNoText+=area;
                 }
             }
        }

        //calculate the score
        // formula is #Interactive * (its area) * 5 + #Text * (its area)* 2 +   #NonInteractiveNoText * 1 * (its area)
//        score = numberOfInteractiveElements * areaOfInteractiveElements * 10 +
//                numberOfElementsWithText * areaOfElementsWithText * 5 +
//                numberOfNonInteractiveNoText * areaOfNonInteractiveNoText * 3; //commented by me Apr3
        score = numberOfInteractiveElements * areaOfInteractiveElements*2.5 +
                numberOfElementsWithText * areaOfElementsWithText*2  + // was not multiplied by 2
                numberOfNonInteractiveNoText * areaOfNonInteractiveNoText *0.5; //commented by me Apr3

        double originalUITotalArea = OwlEye.getOriginalDefaultUI().getTotalArea();
        double missingAreaScore=score;

        //# calculate the percentage of the missingAreaScore from the original UI total area

        double PercentageScore = (missingAreaScore / originalUITotalArea) * 100;
        this.numberOfMissingElements = numberOfMissingElements;
        if (PercentageScore > 100) {
            PercentageScore = 100;
        }
        else if (PercentageScore < 0) {
            PercentageScore = 0;
        }
        this.FormulaScore = PercentageScore;
        this.rawScore=missingAreaScore;
return PercentageScore;
    }

    private String isNodeInteractive(Node<DomNode> node) {

            /** null --> not visible or not interactive at all **/
//        if (node.isVisibleToUser() && node.isEnabled()) {

            // maybe we do not need it to be enabled.
            if (node.getData().isVisible()) {

                if (node.getData().isClickable()) {
                    return "Clickable";
                }
                if (node.getData().isLongClickable()) {
                    return "LongClickable";
                }
                if (node.getData().isCheckable()) {
                    return "Checkable";
                }
//            if(node.isContextClickable())
//                return "ContextClickable";
                if (node.getData().isScrollable()) {
                    return "Scrollable";
                }

            }
            return null;
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

    public double getFormulaScore() {
        return FormulaScore;
    }
    public void setFormulaScore(double formulaScore) {
        FormulaScore = formulaScore;
    }
    public double getRawScore() {
        return rawScore;
    }
    public void setRawScore(double rawScore) {
        this.rawScore = rawScore;
    }
public void setNumberOfMissingElements(int numberOfMissingElements){
        this.numberOfMissingElements=numberOfMissingElements;
    }
    public int getNumberOfMissingElements(){
        return numberOfMissingElements;
}
}
