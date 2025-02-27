package usc.edu.OwlEye.ElementsProperties;

import usc.edu.OwlEye.OwlConstants;

import java.util.ArrayList;
import java.util.Arrays;

public class MaxWidth extends Property{
    public final static String  propertyName ="maxWidth";
    public String currentStaticVal;
    public String currentDynamicVal;
    public double  currentVal=0;

    static ArrayList<String> possibleValues = new ArrayList<>(Arrays.asList(OwlConstants.numericalValue));

    public MaxWidth(String name) {
        super(name);

    }

    public MaxWidth()
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
        MaxWidth.possibleValues = possibleValues;
    }

    public ArrayList<String> getPossibleValues() {
        return possibleValues;
    }
    public double getCurrentVal() {
        return currentVal;
    }
}
