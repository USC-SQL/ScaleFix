package usc.edu.OwlEye.ElementsProperties;

import usc.edu.OwlEye.OwlConstants;

import java.util.ArrayList;
import java.util.Arrays;

public class Lines extends Property {

    //android:maxLines="1" android:lines="1".
    public final static String propertyName = "lines";
    public double  currentVal=0;
    private int noOfLines;


    public Boolean isLines;

    static ArrayList<String> possibleValues = new ArrayList<>(Arrays.asList("1", "2", OwlConstants.shouldRemoveAttribute)); // none means just remove the attribute

    public Lines() {
        super(propertyName);
        possibleValues = new ArrayList<>(Arrays.asList("1", "2", OwlConstants.shouldRemoveAttribute));

    }
    //B

    public ArrayList<String> getPossibleValues() {
        return possibleValues;
    }


    public void setLines(Boolean maxLines) {
        isLines = maxLines;
    }

    public double getCurrentVal() {
        return currentVal;
    }

    public void setCurrentVal(double currentVal) {
        this.currentVal = currentVal;
//        setCurrentDynamicVal(this.currentVal);
    }

    public int getNoOfLines() {
        return noOfLines;
    }
    public void setNoOfLines(int noOfLines) {
        this.noOfLines = noOfLines;
    }
}
