package usc.edu.OwlEye.ElementsProperties;

import usc.edu.OwlEye.OwlConstants;

import java.util.ArrayList;
import java.util.Arrays;

public class MaxLine extends Property {

    //android:maxLines="1" android:lines="1".
    public final static String propertyName = "maxLines";

    private int currentVal;

    public Boolean isMaxLines;

    static ArrayList<String> possibleValues = new ArrayList<>(Arrays.asList("1", "2", OwlConstants.shouldRemoveAttribute)); // none means just remove the attribute

    public MaxLine() {
        super(propertyName);
        possibleValues = new ArrayList<>(Arrays.asList("1", "2", OwlConstants.shouldRemoveAttribute));
    }
    //B

    public ArrayList<String> getPossibleValues() {
        return possibleValues;
    }


    public void setMaxLines(Boolean maxLines) {
        isMaxLines = maxLines;
    }

    public int getCurrentVal() {
        return currentVal;
    }

    public void setCurrentVal(int currentVal) {
        this.currentVal = currentVal;
    }
}
