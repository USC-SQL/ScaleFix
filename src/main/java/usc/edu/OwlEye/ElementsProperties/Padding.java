package usc.edu.OwlEye.ElementsProperties;

import usc.edu.OwlEye.OwlConstants;

import java.util.ArrayList;
import java.util.Arrays;

public class Padding extends Property {

    //String texts
    public static String propertySubName ="";  // exact name of the property for example: android:paddingLeft
    public final static String propertyName = "padding";
    private double currentVal;
    public static Boolean  canBeNumerical=true;
    public static Boolean isBooleanValue=false;
    public static Boolean canBeNull=false;
    public static ArrayList<String> possibleValues = new ArrayList<>(Arrays.asList(OwlConstants.numericalValue));

    public Padding(String propertySubName)
    {
        super(propertyName);
        this.propertySubName =propertySubName;
        possibleValues = new ArrayList<>(Arrays.asList(OwlConstants.numericalValue));
    }
    //Boolean values
    public Boolean isLayoutPadding;

    public  ArrayList<String> getPossibleValues(){
        return possibleValues;
    }

    public static String getPropertySubName() {
        return propertySubName;
    }

    public static void setPropertySubName(String propertySubName) {
        Padding.propertySubName = propertySubName;
    }

    public double getCurrentVal() {
        return currentVal;
    }

    public void setCurrentVal(double currentVal) {
        this.currentVal = currentVal;
    }
}