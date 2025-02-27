package usc.edu.OwlEye.AUCII;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;

import java.util.ArrayList;

public class Collision extends AUCIIssue {
    String issueID;
    int numberOfCollidingElements = 0;

//    ArrayList<String> collidingElements = new ArrayList<String>();//right now I am using the ID of the element as the identifier but later maybe we need XPath
    ArrayList<Node<DomNode>> problematicElementsNodes;
    ArrayList<String> problematicElements;

    String issueType = ""; // Vertical or Horizontal cutoff (top or down)
//    String issueLocation; // horizontal: top or down, vertical: left or right
    String issueSeverity; // complete collision or partial collision


    public Collision(String issueID, String rawProblematicElements, String issueType,String issueScore) {
        super(issueScore,issueType);
        this.issueID = issueID;
        this.issueType = issueType;
        this.problematicElementsNodes = new ArrayList<Node<DomNode>>();
        this.problematicElements = new ArrayList<String>();
        String [] IdOfElements=rawProblematicElements.split("-");
        for (int i=0;i<IdOfElements.length;i++){
             problematicElements.add(IdOfElements[i].trim());

        }

        this.numberOfCollidingElements = problematicElements.size();



        this.issueType = issueType;

    }

    public Collision(String[] issueData) {

        // issue data extracted from CSV
        this(issueData[0], issueData[1], issueData[2], issueData[3]);


    }


    public int getNumberOfCollidingElements() {
        return numberOfCollidingElements;
    }

    public void setNumberOfCollidingElements(int numberOfCollidingElements) {
        this.numberOfCollidingElements = numberOfCollidingElements;
    }





    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }



    public String getIssueSeverity() {
        return issueSeverity;
    }

    public String getIssueID() {
        return issueID;
    }

    public void setIssueID(String issueID) {
        this.issueID = issueID;
    }

    public ArrayList<Node<DomNode>> getProblematicElementsNodes() {
        return problematicElementsNodes;
    }

    public void setProblematicElementsNodes(ArrayList<Node<DomNode>> problematicElementsNodes) {
        this.problematicElementsNodes = problematicElementsNodes;
    }

    public void setIssueSeverity(String issueSeverity) {
        this.issueSeverity = issueSeverity;
    }

    public ArrayList<String> getProblematicElements() {
        return problematicElements;
    }

    public void setProblematicElements(ArrayList<String> problematicElements) {
        this.problematicElements = problematicElements;
    }

    public void addProblematicElement(Node<DomNode> problematicElement) {
        this.problematicElementsNodes.add(problematicElement);
    }
}
