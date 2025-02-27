package usc.edu.OwlEye.GA;

import usc.edu.OwlEye.GAChanges.GAChange;
import usc.edu.OwlEye.OwlConstants;
import usc.edu.OwlEye.util.Utils;
import usc.edu.SALEM.Constants;

import java.util.ArrayList;


public class GAGene {
    // root cause
    private String segmentIssueId;
    private ArrayList<String> xpaths;
    private String cssProperty;
    private String issueType;
    private String value;
    /*
    dp: numericalDP ; no. numerical number from 1 to 10; sCon: size constraint "match_parent" & "wrap_content";
    boolean: true & false; id: id of other element ; element: add new element; remove: remove property from the element

     */
    private String valueType;

    private String segmentID;
    private String originalValue;
    private double impactScore;
    private boolean isProcessed = false;
    private String changeType; // change Property or add new element (scrollView or linearLayout)
    private GAChange gaChange;

    public GAGene() {
        this.xpaths = new ArrayList<>();
    }

    public GAGene(ArrayList<String> xpaths, String cssProperty, String issue, String value, String segmentIssueId) {
        this.segmentIssueId = segmentIssueId;
        this.xpaths = new ArrayList<>();
        this.issueType = issue;
        for (String xpath : xpaths) {
            addXpath(xpath);
        }

        this.cssProperty = cssProperty;
        decideValueType(cssProperty, value);
        if (value.equals("")) {
            this.value = Constants.ACCESSIBILITY_SUGGESTED_VALUES.get(issueType).get(cssProperty);
        } else {
            this.value = value;
        }
    }

    public GAGene(String xpath, String issue, String cssProperty, String value, String segmentIssueId) {
        this.xpaths = new ArrayList<>();
        xpaths.add(xpath);
        this.cssProperty = cssProperty;
        this.issueType = issue;
        this.changeType = OwlConstants.CHANGE_PROPERTY_GENE;
        decideValueType(cssProperty, value);
        if (value.equals("")) {
            this.value = Constants.ACCESSIBILITY_SUGGESTED_VALUES.get(issueType).get(cssProperty);
        } else {
            this.value = value;
        }

        this.segmentIssueId = segmentIssueId;
    }

    private void decideValueType(String cssProperty, String value) {
        if (value.contains("match") || value.contains("wrap")|| value.contains("fill")
                || value.equals("-1") || value.equals("-2")) {
            // it is a sCon value
            this.setValueType(OwlConstants.SCON_VALUE);
        }
        else if(value.contains("@")){  // double check
            this.setValueType(OwlConstants.ID_VALUE);
        }
        else if (value.equalsIgnoreCase("true")||value.equalsIgnoreCase("false")){
            this.setValueType(OwlConstants.BOOLEAN_VALUE);
        }
        else if (Utils.isValueNumerical(value)){
            // it is numerical so now based on the property type we need to decide between value of dp and whether normal number (eg maxLines)
            //for now I am assuming that all numerical values are dp
//            if (cssProperty.contains("lines") ){
//                this.setValueType(OwlConstants.NUMERICAL_INT_VALUE);
//        } else {
//            this.setValueType(OwlConstants.NUMERICAL_DP_VALUE);
//        }
            String propName=OwlConstants.TO_FuLL_ATTRIBUTES_BI_MAPPING.inverse().get(cssProperty);
            if(OwlConstants.NUMERICAL_INT_VALUE_PROPS.containsValue(cssProperty)){
                this.setValueType(OwlConstants.NUMERICAL_INT_VALUE);
            }
            else {
                this.setValueType(OwlConstants.NUMERICAL_DP_VALUE);
            }


        }
        else {
            this.setValueType(OwlConstants.STRING_VALUE);
        }
    }

    public ArrayList<String> getXpaths() {
        return xpaths;
    }

    public void addXpath(String xpath) {
        if (!xpaths.contains(xpath)) {
            xpaths.add(xpath);
        }
    }

    public String getCssProperty() {
        return cssProperty;
    }

    public void setCssProperty(String cssProperty) {
        this.cssProperty = cssProperty;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (segmentIssueId!=null && segmentIssueId.contains("#")) {
            String[] split = segmentIssueId.split("#");
            String currVal="";
            if(split.length<3){
                 currVal=split[1];
            }
            else{
             currVal=split[2];}
           this.setSegmentIssueId(segmentIssueId.replace(currVal,value));
            //this.originalValue = this.value;
        }
        this.value = value;
    }

    public double getImpactScore() {
        return impactScore;
    }

    public void setImpactScore(double impactScore) {
        this.impactScore = impactScore;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(String originalValue) {
        this.originalValue = originalValue;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public GAGene copy() {
        GAGene copiedGAGene = new GAGene(this.xpaths, this.cssProperty, this.issueType, this.value, this.segmentIssueId);

        copiedGAGene.setOriginalValue(originalValue);
        copiedGAGene.setSegmentID(segmentID);
        copiedGAGene.setImpactScore(impactScore);
        copiedGAGene.setGAChange(gaChange);
        copiedGAGene.setChangeType(changeType);
        copiedGAGene.setValueType(valueType);

        return copiedGAGene;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((segmentIssueId == null) ? 0 : segmentIssueId.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GAGene other = (GAGene) obj;
        if (segmentIssueId == null) {
            if (other.segmentIssueId != null)
                return false;
        } else if (!segmentIssueId.equals(other.segmentIssueId))
            return false;
        if (value == null) {
            return other.value == null;
        } else
            return value.equals(other.value);
    }
//    @Override (april 2023)
//    public boolean equals(Object obj) {
//        if (this == obj)
//            return true;
//        if (obj == null)
//            return false;
//        if (getClass() != obj.getClass())
//            return false;
//        GAGene other = (GAGene) obj;
//        if (segmentIssueId == null) {
//            if (other.segmentIssueId != null)
//                return false;
//        } else if (!segmentIssueId.equals(other.segmentIssueId))
//            return false;
//        if (value == null) {
//            return other.value == null;
//        } else
//            return value.equals(other.value);
//    }

    @Override
    public String toString() {
        String StringVal = "- <" + segmentIssueId + ", " + cssProperty + ", " + value + ", "+valueType+", "+originalValue+">";
//        StringVal += "\tIDs:\n";
//        for (String xpath : xpaths) {
//            StringVal += xpath + "\n";
//
//        }

        return StringVal;

    }
//    @Override
//    public String toString() {
//        String StringVal = "<" + segmentIssueId + ", " + cssProperty + ", " + value + ", "+valueType+", "+originalValue+">";
//        StringVal += "\tXPaths:\n";
//        for (String xpath : xpaths) {
//            StringVal += xpath + "\n";
//
//        }
//        StringVal += "*******************";
//        return StringVal;
//
//    }
//    public String toString() {
//        return "<" + segmentIssueId + cssProperty + ", " + value + ">";
//    }


    public String getSegmentID() {
        return segmentID;
    }

    public void setSegmentID(String segmentID) {
        this.segmentID = segmentID;
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public void setProcessed(boolean isProcessed) {
        this.isProcessed = isProcessed;
    }

    public String getSegmentIssueId() {
        return segmentIssueId;
    }

    public void setSegmentIssueId(String segmentIssueId) {
        this.segmentIssueId = segmentIssueId;
    }

    public void setXpaths(ArrayList<String> xpaths) {
        this.xpaths = xpaths;
    }

    public String getChangeType() {
        return changeType;
    }
    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public GAChange getGAChange() {
        return gaChange;
    }

    public void setGAChange(GAChange gaChange) {
        this.gaChange = gaChange;
    }
}
