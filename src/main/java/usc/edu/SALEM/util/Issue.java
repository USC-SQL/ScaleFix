package usc.edu.SALEM.util;

import java.util.ArrayList;

public class Issue {

    public String nodeId;


    public String issueType;
    public String issueOrigin;
    public String widgetClass;

    public String getWidgetClass() {
        return widgetClass;
    }

    public void setWidgetClass(String widgetClass) {
        this.widgetClass = widgetClass;
    }

    public String getIssueOrigin() {
        return issueOrigin;
    }

    public void setIssueOrigin(String issueOrigin) {
        this.issueOrigin = issueOrigin;
    }

    // public Attributes attribute;
    public ArrayList<Attributes> attributes;

    public Issue() {
        attributes = new ArrayList<>();
    }

//    public Attributes getAttribute() {
//        return attribute;
//    }

//    public void setAttribute(Attributes attribute) {
//        this.attribute = attribute;
//    }

    public ArrayList<Attributes> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<Attributes> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(Attributes attribute) {
        attributes.add(attribute);
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }
}
