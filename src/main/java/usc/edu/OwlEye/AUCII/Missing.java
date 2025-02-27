package usc.edu.OwlEye.AUCII;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;

import java.util.ArrayList;

public class Missing extends AUCIIssue {
    String issueID;
    int numberOfMissingElements = 0;

    ArrayList<Node<DomNode>> problematicElementsNodes;
    ArrayList<String> problematicElements;

    String issueType = ""; // Vertical or Horizontal mising (top or down)
//    String issueLocation; // horizontal: top or down, vertical: left or right
    String issueSeverity; // complete collision or partial collision


    public Missing(String issueID, String rawProblematicElements, String issueType, String issueScore) {
        super(issueScore,issueType);
        this.issueID = issueID;
        this.issueType = issueType;
        this.problematicElementsNodes = new ArrayList<Node<DomNode>>();
        this.problematicElements = new ArrayList<String>();
        String [] IdOfElements=rawProblematicElements.split("-");
        for (int i=0;i<IdOfElements.length;i++){
             problematicElements.add(IdOfElements[i].trim());

        }

        this.numberOfMissingElements = problematicElements.size();



        this.issueType = issueType;

    }

    public Missing(String[] issueData) {

        // issue data extracted from CSV
        this(issueData[0], issueData[1], issueData[2], issueData[3]);


    }


    public int getNumberOfMissingElements() {
        return numberOfMissingElements;
    }

    public void setNumberOfMissingElements(int numberOfCollidingElements) {
        this.numberOfMissingElements = numberOfCollidingElements;
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
