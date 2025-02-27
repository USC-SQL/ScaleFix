package usc.edu.OwlEye.ElementsProperties;

import usc.edu.OwlEye.OwlConstants;

import java.util.ArrayList;
import java.util.Arrays;

public class MaxHeight extends Property{
    public final static String  propertyName ="maxHeight";
//    public String currentStaticVal;
//    public String currentDynamicVal;
    public double  currentVal=0;
    private int noOfLines;
    static ArrayList<String> possibleValues = new ArrayList<>(Arrays.asList(OwlConstants.numericalValue));


    public Boolean isLines;

    public MaxHeight(String name) {
        super(name);

    }

    public MaxHeight()
    {
        super(propertyName);
        possibleValues = new ArrayList<>(Arrays.asList(OwlConstants.numericalValue));
    }

    @Override
    public String getCurrentStaticVal() {
        return currentStaticVal;
    }

    @Override
    public void setCurrentStaticVal(String currentStaticVal) {
        this.currentStaticVal = currentStaticVal;
    }

    @Override
    public String getCurrentDynamicVal() {
        return currentDynamicVal;
    }

    @Override
    public void setCurrentDynamicVal(String currentDynamicVal) {
        this.currentDynamicVal = currentDynamicVal;
    }



    public static void setPossibleValues(ArrayList<String> possibleValues) {
        MaxHeight.possibleValues = possibleValues;
    }

    public ArrayList<String> getPossibleValues() {
        return possibleValues;
    }
    public void setLines(Boolean maxLines) {
        isLines = maxLines;
    }

    public Boolean getLines() {
        return isLines;
    }
    public double getCurrentVal() {
        return currentVal;
    }
    public void setCurrentVal(double currentVal) {
        this.currentVal = currentVal;
    }
    public int getNoOfLines() {
        return noOfLines;
    }
}
