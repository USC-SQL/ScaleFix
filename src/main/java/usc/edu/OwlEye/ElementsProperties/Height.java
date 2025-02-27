package usc.edu.OwlEye.ElementsProperties;

import usc.edu.OwlEye.OwlConstants;

import java.util.ArrayList;
import java.util.Arrays;

public class Height extends Property {


    //String texts
    public final static String  propertyName ="layoutHeight";
    public static Boolean  canBeNumerical=true;
    public static Boolean isBooleanValue=false;
    public static Boolean canBeNull=false;
    public String currentStaticVal;
    public String currentDynamicVal;
     static ArrayList<String> possibleValues = new ArrayList<>(Arrays.asList(OwlConstants.wrapContent, OwlConstants.matchParent, OwlConstants.numericalValue));

    public Height()
    {
        super(propertyName);
        possibleValues = new ArrayList<>(Arrays.asList(OwlConstants.wrapContent, OwlConstants.matchParent, OwlConstants.numericalValue));
    }
    //Boolean values
    public Boolean isLayoutHeight;


    public Boolean getLayoutHeight() {
        return isLayoutHeight;
    }

    public void setLayoutHeight(Boolean layoutHeight) {
        isLayoutHeight = layoutHeight;
    }

    public  ArrayList<String> getPossibleValues(){
        return possibleValues;
    }



    public static Boolean getCanBeNumerical() {
        return canBeNumerical;
    }

    public static void setCanBeNumerical(Boolean canBeNumerical) {
        Height.canBeNumerical = canBeNumerical;
    }

    public static Boolean getIsBooleanValue() {
        return isBooleanValue;
    }

    public static void setIsBooleanValue(Boolean isBooleanValue) {
        Height.isBooleanValue = isBooleanValue;
    }

    public static Boolean getCanBeNull() {
        return canBeNull;
    }

    public static void setCanBeNull(Boolean canBeNull) {
        Height.canBeNull = canBeNull;
    }

    public String getCurrentStaticVal() {
        return currentStaticVal;
    }

    public void setCurrentStaticVal(String currentStaticVal) {
        this.currentStaticVal = currentStaticVal;
    }

    public String getCurrentDynamicVal() {
        return currentDynamicVal;
    }

    public void setCurrentDynamicVal(String currentDynamicVal) {
        this.currentDynamicVal = currentDynamicVal;
    }

    public static void setPossibleValues(ArrayList<String> possibleValues) {
        Height.possibleValues = possibleValues;
    }
}
