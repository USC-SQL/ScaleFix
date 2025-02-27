package usc.edu.OwlEye.fitness;

import gatech.xpert.dom.DomNode;
import org.tinylog.Logger;
import usc.edu.OwlEye.OwlEye;
import usc.edu.OwlEye.VH.UI;
import usc.edu.OwlEye.util.Utils;
import usc.edu.SALEM.Constants;
import gatech.xpert.dom.Node;
//import usc.edu.SALEM.VHTree.XMLUtils;
import usc.edu.issuesfilter.*;
import usc.edu.layoutgraph.LayoutGraphBuilder;
import usc.edu.layoutissue.Issue;

import java.util.ArrayList;


public class PositioningAndAlignmentsObjective {
/* A class that implements the fitness score for the Relative Positioning and Alignments objective.*/
private double PositioningAndAlignments =Double.MIN_VALUE;
private double directionalScore =Double.MIN_VALUE;
private double alignmentScore =Double.MIN_VALUE;
private int noOfDirectionalIssues = 0;
private int noOfAlignmentIssues = 0;
private double positionalFormula;
private double alignmentFormula;
private ArrayList<Issue> violations;
    public PositioningAndAlignmentsObjective() {

        this.PositioningAndAlignments = Double.MIN_VALUE;
        violations = new ArrayList<>();
    }


    public double calculatePositioningAndAlignmentsScore(Node<DomNode> newLayoutRoot) {
        double layoutInconsistencyScore = 0;
        double changeAmount = 0;


            LayoutGraphBuilder lgb;
         //  UI uiDefault = OwlEye.getOriginalDefaultUI();
//           Node<DomNode> originalRoot= uiDefault.getXMLTree().getRoot();
           // Node<DomNode> originalRoot = XMLUtils.getRoot();
            Node<DomNode> repairedRoot = newLayoutRoot;

          //  lgb = new LayoutGraphBuilder(originalRoot, repairedRoot);
           lgb = new LayoutGraphBuilder(repairedRoot);
            ArrayList<Issue> potentialLayoutIssues = lgb.compareLayoutGraphs();
            //int potentialscore = computeAmountOfInconsistency(potentialLayoutIssues);
            LayoutIssuesFilterProcessor filters = new LayoutIssuesFilterProcessor();

            filters.addFilter(new ContainmentIssueFilter());
            filters.addFilter(new DirectionIssueFilter());
           // filters.addFilter(new OptionElementFilter());
            filters.addFilter(new OnePixelIssueFilter());
            filters.addFilter(new CenteredIssueFilter());
            filters.addFilter(new AlignmentIssueFilter());
            filters.addFilter(new DuplicatesIssueFilter());
//        LayoutIssuesFilterProcessor filter = new LayoutIssuesFilterProcessor();
            ArrayList<Issue> filteredLayoutIssues = filters.filterissues(potentialLayoutIssues);
            //filter issues

           violations.addAll(filteredLayoutIssues);
            /*** (1) filtered the output ***/

           String[] entireConsistencyScore = computeAmountOfInconsistency(filteredLayoutIssues);
           String totalInconsistencyScore = entireConsistencyScore[0];
           String directionalInconsistencyScore = entireConsistencyScore[1];
              String alignmentInconsistencyScore = entireConsistencyScore[2];
              Logger.debug("totalInconsistencyScore: " + totalInconsistencyScore);






           Logger.trace("PotentialSize: " + potentialLayoutIssues.size() + " || " +
                    "FilteredSize: " + filteredLayoutIssues.size());


           // layountInconsistencyScore = filteredAlignmentScore;






        /*** (3) calculate ***/
         this.PositioningAndAlignments= Double.parseDouble(totalInconsistencyScore.split("-")[0]);
        this.directionalScore= Double.parseDouble(directionalInconsistencyScore.split("-")[0]);
        this.noOfDirectionalIssues = Integer.parseInt(directionalInconsistencyScore.split("-")[1]);
        this.alignmentScore= Double.parseDouble(alignmentInconsistencyScore.split("-")[0]);
        this.noOfAlignmentIssues = Integer.parseInt(alignmentInconsistencyScore.split("-")[1]);
        double totalPositional=300000;
        double totalAlignment=300000;

        // formuala: ((directionalScore +noOfDirectionalIssues*10)/ totalPositional) / 100 + (alignmentScore / totalAlignment) * 100 + noOfAlignmentIssues * 100
        this.positionalFormula = ((directionalScore +noOfDirectionalIssues*10)/ totalPositional) *100;
        this.alignmentFormula = ((alignmentScore +noOfAlignmentIssues) / totalAlignment) *100;
//        this.PositioningAndAlignments = layoutInconsistencyScore;
        Logger.trace(
                "FilteredScore: " + PositioningAndAlignments);
        this.PositioningAndAlignments = positionalFormula*0.90 + alignmentFormula*0.10;
        if(this.PositioningAndAlignments>100){
            this.PositioningAndAlignments=100;}
        else if(this.PositioningAndAlignments<0){
            this.PositioningAndAlignments=0;
        }
        return PositioningAndAlignments;
//        return new double[]{layountInconsistencyScore, changeAmount};
    }

    public static String[] computeAmountOfInconsistency(ArrayList<Issue> filteredLayoutIssues) {
        int amountOfInconsistency = 0;
        int numberOfInconsistency = 0;
        int amountOfAlignments = 0;
        int numberOfAlignments = 0;
        int amountOfPositioning = 0;
        int numberOfPositioning = 0;
        int amount = 0;
        // We do not need the collision (overlap and crossed)this is going to be calculated in the collision objective
        int numberOfCollision = 0;
        int amountOfCollision = 0;
        ArrayList<Issue> intersectionIssues = new ArrayList<>();
        for (Issue issue : filteredLayoutIssues) {
            if (Utils.skipLayoutIssueDueToScrollView(issue)){
                continue;
            }
            String type = issue.getIssueType().name();
            if (!type.equalsIgnoreCase("intersection")){// we calculate this in the collision objective
                boolean useID = false;
                String node1Identifier = null;
                String node2Identifier = null;
                if(useID) {
                    node1Identifier = issue.getBaselineEdge().getNode1().getDomNode().getData().getId();
                    node2Identifier = issue.getBaselineEdge().getNode2().getDomNode().getData().getId();
                }
                if (!useID || node1Identifier==null || node2Identifier==null)
                {
                    node1Identifier=issue.getBaselineEdge().getNode1().getDomNode().getData().getxPath();
                    node2Identifier = issue.getBaselineEdge().getNode2().getDomNode().getData().getxPath();
                }
//                }else{
//                    //use xpath
//                    node1Identifier=issue.getBaselineEdge().getNode1().getDomNode().getData().getxPath();
//                    node1Identifier = issue.getBaselineEdge().getNode2().getDomNode().getData().getxPath();
//                }


                if(node1Identifier.trim().equalsIgnoreCase(node2Identifier.trim())){
                    continue;
                }
                // alignment is less important than directional and containment is even less
                double calcWeight=1 ;
                if(issue.isAlignmentIssue()){
                    amountOfAlignments += issue.getIssueAmount();
                    numberOfAlignments++;
                    calcWeight = 0.5;}
            else if(issue.isContainmentIssue()){
                    calcWeight = 0.3;
                }
            else if(issue.isDirectionIssue()){
                    calcWeight = 1;
                    amountOfPositioning += issue.getIssueAmount();
                    numberOfPositioning++;
                }
                amountOfInconsistency += issue.getIssueAmount()*calcWeight;
                numberOfInconsistency++;
            } else {
                intersectionIssues.add(issue);
            }
        }
        OwlEye.setCurrentIntersectionLayoutIssues(intersectionIssues);
//        Logger.trace("No. Collisions violations: " + numberOfCollision+ " || " +
//                "Amount of Collisions violations: " + amountOfCollision);
        Logger.trace("No. RelativePositioning And Alignments: " + numberOfInconsistency+ " || " +
                "Amount of  violations: " + amountOfInconsistency);
    // return an array of the amount of inconsistency and the number of inconsistency

        return new String[]{amountOfInconsistency+"-"+numberOfInconsistency,amountOfPositioning+"-"+numberOfPositioning,amountOfAlignments+"-"+numberOfAlignments};
    }
    public double getPositioningAndAlignments() {
        return PositioningAndAlignments;
    }

    public ArrayList<Issue> getViolations() {
        return violations;
    }

    public void setPositioningAndAlignments(double positioningAndAlignments) {
        PositioningAndAlignments = positioningAndAlignments;
    }

    public double getDirectionalScore() {
        return directionalScore;
    }

    public void setDirectionalScore(double directionalScore) {
        this.directionalScore = directionalScore;
    }

    public double getAlignmentScore() {
        return alignmentScore;
    }

    public void setAlignmentScore(double alignmentScore) {
        this.alignmentScore = alignmentScore;
    }

    public int getNoOfDirectionalIssues() {
        return noOfDirectionalIssues;
    }

    public void setNoOfDirectionalIssues(int noOfDirectionalIssues) {
        this.noOfDirectionalIssues = noOfDirectionalIssues;
    }

    public int getNoOfAlignmentIssues() {
        return noOfAlignmentIssues;
    }

    public void setNoOfAlignmentIssues(int noOfAlignmentIssues) {
        this.noOfAlignmentIssues = noOfAlignmentIssues;
    }

    public void setViolations(ArrayList<Issue> violations) {
        this.violations = violations;
    }

    public double getPositionalFormula() {
        return positionalFormula;
    }

    public void setPositionalFormula(double positionalFormula) {
        this.positionalFormula = positionalFormula;
    }

    public double getAlignmentFormula() {
        return alignmentFormula;
    }

    public void setAlignmentFormula(double alignmentFormula) {
        this.alignmentFormula = alignmentFormula;
    }
}
