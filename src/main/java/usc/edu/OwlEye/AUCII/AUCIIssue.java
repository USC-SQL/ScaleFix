package usc.edu.OwlEye.AUCII;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import usc.edu.OwlEye.ElementsProperties.Property;
import usc.edu.OwlEye.VHTree.XMLUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class AUCIIssue {
    String issueScore ;
    String issueType;
    Set<String>  applicableProperties;
    HashMap<Node<DomNode>, HashMap<String, Set<String>>>  concreteValuesForElements;
    ArrayList<Node<DomNode>> impactedElements;
    public AUCIIssue(String issueScore,String issueType) {
        this.issueScore = issueScore;
        this.issueType = issueType;

    }


    public void setImpactedElements(ArrayList<Node<DomNode>> impactedElements) {
        this.impactedElements = impactedElements;
    }


    public String getIssueScore() {
        return issueScore;
    }

    public void setIssueScore(String issueScore) {
        this.issueScore = issueScore;
    }

    public Set<Property> getApplicableProperties() {
        System.out.println("Parent Applicable Properties");
       return null;
    }

    public ArrayList<Node<DomNode>> getImpactedElements(XMLUtils vh){
        return null;
    }
    public Set<String> getPossibleValues(Property property) {
        return null;
    }

    public HashMap<Node<DomNode>, HashMap<String, String>> calculateSuggestedValues(ArrayList<Node<DomNode>> impactedElements, Property cssProperty, Set<String> possibleValues) {
        return null;
    }

    public HashMap<Node<DomNode>, HashMap<String, Set<String>>> computeConcreteValues(HashMap<Node<DomNode>, HashMap<String, String>> suggestedValuesForElements, Property cssProperty, Set<String> possibleValues) {
        return concreteValuesForElements;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }
}
