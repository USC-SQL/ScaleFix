package usc.edu.OwlEye.GAChanges;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import usc.edu.OwlEye.ElementsProperties.Property;
import usc.edu.OwlEye.OwlConstants;

public class GAChange {
    String propertyName; // such as height
    String attName; // such as layout_height
    String changeType; // such as add, remove, increase, decrease, etc
    String value; // the value of the change such as 100dp, wrap_content, etc
    GAElementToChange element;
    Property property;
    String issueToFix; // OwlConstants.CUT_OFF_ISSUE, OwlConstants.COLLISION_ISSUE, OwlConstants.MISSING_ISSUE

    public GAChange(String propertyName, String attName, String changeType, String value) {
        this.propertyName = propertyName;
        this.attName = attName;
        this.changeType = changeType;
        this.value = value;
        this.element = null;
    }
    public GAChange(String propertyName, String attName, String changeType, String value, GAElementToChange element, Property property) {
        this.propertyName = propertyName;
        this.attName = attName;
        this.changeType = changeType;
        this.value = value;
        this.element = element;
        this.property = property;
    }
    public GAChange(String propertyName, String attName, String changeType, String value, GAElementToChange element) {
        this.propertyName = propertyName;
        this.attName = attName;
        this.changeType = changeType;
        this.value = value;
        this.element = element;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getAttName() {
        return attName;
    }

    public void setAttName(String attName) {
        this.attName = attName;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public GAElementToChange getElement() {
        return element;
    }

    public void setNode(GAElementToChange element) {
        this.element = element;
    }

    public Property getProperty() {
        return property;
    }
    public void setProperty(Property property) {
        this.property = property;
    }

    public String getIssueToFix() {
        return issueToFix;
    }
    public void setIssueToFix(String issueToFix) {
        this.issueToFix = issueToFix;
    }
}
