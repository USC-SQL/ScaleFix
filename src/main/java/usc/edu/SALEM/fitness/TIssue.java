package usc.edu.SALEM.fitness;

import usc.edu.SALEM.Constants;

public class TIssue {
//    com.adzenze.FashionDesignFlatSketch
//    com.adzenze.FashionDesignFlatSketch.ReadBookActivity
//    checks.TouchTargetSizeCheck
//    RESULT_ID_SMALL_TOUCH_TARGET_HEIGHT
//    android.widget.LinearLayout
//    com.adzenze.FashionDesignFlatSketch:id/btn_home
//    Rect(0: 95 - 168: 252)
//    /data/user/0/com.aziz.accessibilityEval/files/screenX-297852127.PNG

    private String issueType;  // Height, width, or both for now
    private String className;
    private String widgetID;
    private String widgetXpath;
    private String rectangle;
    private String chromosomeID;  // What chromosome this was part of
    private boolean isInitial;  // Is this issue part of the initial issue
    private String activityName;

    public TIssue(String issueType, String chromosomeID, boolean isInitial, String activity, String widgetID, String widgetXpath, String widgetClass, String widgetBounds) {
        setIssueType(issueType);
        setChromosomeID(chromosomeID);
        setInitial(isInitial);
        setActivityName(activity);
        setWidgetID(widgetID);
        setWidgetXpath(widgetXpath);
        setClassName(widgetClass);
        setRectangle(widgetBounds);

    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }


    public String getChromosomeID() {
        return chromosomeID;
    }

    public void setChromosomeID(String chromosomeID) {
        this.chromosomeID = chromosomeID;
    }

    public boolean isInitial() {
        return isInitial;
    }

    public void setInitial(boolean initial) {
        isInitial = initial;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        /*  3 types of issues
        Height
        Width
        Both
         */
        //get the type by mapping with the detection tool restult
        String type = Constants.DETECTION_TOOL_TO_ISSUES_MAPPING.get(issueType);
        this.issueType = type;
//
//

    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getWidgetID() {
        return widgetID;
    }

    public void setWidgetID(String widgetID) {
        this.widgetID = widgetID;
    }

    public String getWidgetXpath() {
        return widgetXpath;
    }

    public void setWidgetXpath(String widgetXpath) {
        this.widgetXpath = widgetXpath;
    }

    public String getRectangle() {
        return rectangle;
    }

    public void setRectangle(String rectangle) {
        this.rectangle = rectangle;
    }
//    private  int x, y, width, height;


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getWidgetID() == null) ? 0 : getWidgetID().hashCode());
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
        TIssue other = (TIssue) obj;
        if (getWidgetID() == null) {
            if (other.getWidgetID() != null)
                return false;
        }
        if (getClassName() == null) {
            if (other.getClassName() != null)
                return false;
        }
        return getWidgetID().equals(other.getWidgetID()) &&
                getClassName().equals(other.getClassName());
    }


}
