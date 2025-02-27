package usc.edu.OwlEye.fitness;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import usc.edu.OwlEye.OwlConstants;
import usc.edu.OwlEye.OwlEye;
import usc.edu.OwlEye.util.Utils;
import usc.edu.layoutissue.Issue;

import java.util.ArrayList;

public class CollisionObjective extends FitnessObjective{
//    private double collisionScore;
//    private int noOfCollisions;
//    private double collisionFormula;
//    private double rawScore;

        private ArrayList<Issue> violations;
    public CollisionObjective() {
//        this.collisionScore = Double.MIN_VALUE;
//        violations = new ArrayList<>();
    }

    public double calculateObjectiveScore(Node<DomNode> newLayoutRoot, ArrayList<Issue> inconsistencyIssues) {
        int numberOfCollision = 0;
        int amountOfCollision = 0;
        this.violations = new ArrayList<>();

        for (Issue issue : inconsistencyIssues) {
            if (Utils.skipLayoutIssueDueToScrollView(issue)){
                continue;
            }
            if (Utils.ScrollCollideWithTabs(issue)){
                continue;
            }
            if(Utils.isFabInCollisionIssue(issue)){
                continue;
            }
            if(Utils.isNotLeaveNodes(issue)){
                continue;
            }


            numberOfCollision++;
            amountOfCollision += issue.getIssueAmount();
            violations.add(issue);
        }
        // collision formula= (number of collisions * amount of collision) / total area
        //double totalArea = OwlEye.getOriginalDefaultUI().getTotalArea(); // too much I guess so lets go with 100,000
//        Utils.isNotLeaveNodes()
        double totalArea= determineTheTotalArea(1440.0*2.0);
//        String subject=OwlEye.getOriginalAppID();
//        if (OwlEye.getOriginalAppID().contains("cnet")){
//            totalArea=3000;
//        }
       // double totalArea = 1440*2;
        this.amountOfViolation = ((numberOfCollision * amountOfCollision) / totalArea)*100;
        this.numberOfViolations=numberOfCollision;
        this.rawScore=amountOfCollision;
       // this.rawScore = ((numberOfCollision * amountOfCollision) / totalArea)*100;
        this.objectiveScore = ((numberOfCollision * amountOfCollision) / totalArea)*100;
        if(this.objectiveScore>100){
            this.objectiveScore=100;
        }
        else if(this.objectiveScore<0){
            this.objectiveScore=0;
        }


    return this.objectiveScore;
    }

    private double determineTheTotalArea(double maxArea) {
        double returnedArea=maxArea;
        OwlEye.getOriginalLargestUI();
        //double height=OwlEye.getOriginalLargestUI().getHeight()/ OwlConstants.PHONE_DENSITY;
        // right now I am considering the width of the phone as 1440
        double width=OwlEye.getOriginalLargestUI().getWidth()/OwlConstants.PHONE_DENSITY;
        if(width>0 &&width<maxArea){
            returnedArea=width;
        }
        return returnedArea;
    }


//    public double getCollisionScore() {
//        return collisionScore;
//    }


//    public double calculateCollisionScore(Node<DomNode> newLayoutRoot, ArrayList<Issue> inconsistencyIssues) {
//        int numberOfCollision = 0;
//        int amountOfCollision = 0;
//        this.violations = new ArrayList<>();
//
//        for (Issue issue : inconsistencyIssues) {
//            if (Utils.skipLayoutIssueDueToScrollView(issue)){
//                continue;
//            }
//
//
//            numberOfCollision++;
//            amountOfCollision += issue.getIssueAmount();
//            violations.add(issue);
//        }
//        // collision formula= (number of collisions * amount of collision) / total area
////        double totalArea = OwlEye.getOriginalDefaultUI().getTotalArea(); // too much I guess so lets go with 100,000
//        double totalArea = 1440*3;
//        this.collisionFormula = ((numberOfCollision * amountOfCollision) / totalArea)*100;
//        if(this.collisionFormula>100){
//            this.collisionFormula=100;
//        }
//        else if(this.collisionFormula<0){
//            this.collisionFormula=0;
//        }
//
//        this.collisionScore = amountOfCollision;
//        this.noOfCollisions = numberOfCollision;
//        this.rawScore = this.collisionScore;
//        this.collisionScore = this.collisionFormula;
//        return this.collisionScore;
//    }


//        public double calculateCollisionScore(Node<DomNode> newLayoutRoot, ArrayList<Issue> inconsistencyIssues) {
//        int numberOfCollision = 0;
//        int amountOfCollision = 0;
//        this.violations = new ArrayList<>();
//        int numberOfIssuesCalculatedForSameNode=0;
//        for (Issue issue : inconsistencyIssues) {
//            String type = issue.getIssueType().name();
//            if (type.equalsIgnoreCase("intersection") ){//|| type.equalsIgnoreCase("containment")) {
//                String node1_id=issue.getBaselineEdge().getNode2().getDomNode().getData().getId();
//                String node2_id = issue.getBaselineEdge().getNode1().getDomNode().getData().getId();
//                if (node1_id==null || node2_id==null)
//                {
//                    node1_id=issue.getBaselineEdge().getNode2().getDomNode().getData().getxPath();
//                    node2_id = issue.getBaselineEdge().getNode1().getDomNode().getData().getxPath();
//                }
//                if(node1_id.trim().equalsIgnoreCase(node2_id.trim())){
//                    continue;
//                }
//
//                numberOfCollision++;
//                amountOfCollision += issue.getIssueAmount();
//                violations.add(issue);
//            }
//        }
//        Logger.debug("Number of issues calculated for same node: " + numberOfIssuesCalculatedForSameNode);
//        Logger.debug("Number of collisions issues counted: " + numberOfCollision);
//        return amountOfCollision;
//    }
//    public double calculateCollisionScore0(Node<DomNode> newLayoutRoot, ArrayList<Issue> inconsistencyIssues) {
//        int numberOfCollision = 0;
//        int amountOfCollision = 0;
//        this.violations = new ArrayList<>();
//        int numberOfIssuesCalculatedForSameNode=0;
//        for (Issue issue : inconsistencyIssues) {
//            String type = issue.getIssueType().name();
//            if (type.equalsIgnoreCase("intersection") || type.equalsIgnoreCase("containment")) {
//                String node1_id=issue.getBaselineEdge().getNode2().getDomNode().getData().getId();
//                String node2_id = issue.getBaselineEdge().getNode1().getDomNode().getData().getId();
//                if(node1_id.equalsIgnoreCase(node2_id)){
//                    numberOfIssuesCalculatedForSameNode++;
//                    continue;
//                }
//
//                numberOfCollision++;
//                amountOfCollision += issue.getIssueAmount();
//                violations.add(issue);
//            }
//        }
//        Logger.debug("Number of issues calculated for same node: " + numberOfIssuesCalculatedForSameNode);
//        Logger.debug("Number of collisions issues counted: " + numberOfCollision);
//        return amountOfCollision;
//    }


//    public void setCollisionScore(double collisionScore) {
//        this.collisionScore = collisionScore;
//    }
//
//    public int getNoOfCollisions() {
//        return noOfCollisions;
//    }
//
//    public void setNoOfCollisions(int noOfCollisions) {
//        this.noOfCollisions = noOfCollisions;
//    }
//public double getCollisionFormula() {
//        return collisionFormula;
//    }
//    public void setCollisionFormula(double collisionFormula) {
//        this.collisionFormula = collisionFormula;
//    }
//    public ArrayList<Issue> getViolations() {
//        return violations;
//    }
//
//    public void setViolations(ArrayList<Issue> violations) {
//        this.violations = violations;
//    }
}
