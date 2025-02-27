package usc.edu.OwlEye.ElementsProperties;

import java.util.ArrayList;

public class Property {

    public static String propertyName;
    public static Boolean  canBeNumerical;
    public static Boolean isBooleanValue;
    public static Boolean canBeNull;
    protected static ArrayList<String> possibleValues;
    public String currentStaticVal;
    public String currentDynamicVal;

    public Property(String name){
        canBeNumerical=false;
        isBooleanValue=false;
        canBeNull=false;
        possibleValues = new ArrayList<>();
        this.propertyName =name;
    }


    public  ArrayList<String> getPossibleValues(){
        return possibleValues;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setName(String propertyName) {
        this.propertyName = propertyName;
    }

    public static Boolean getCanBeNumerical() {
        return canBeNumerical;
    }

    public static void setCanBeNumerical(Boolean canBeNumerical) {
        Property.canBeNumerical = canBeNumerical;
    }

    public static Boolean getIsBooleanValue() {
        return isBooleanValue;
    }

    public static void setIsBooleanValue(Boolean isBooleanValue) {
        Property.isBooleanValue = isBooleanValue;
    }

    public static Boolean getCanBeNull() {
        return canBeNull;
    }

    public static void setCanBeNull(Boolean canBeNull) {
        Property.canBeNull = canBeNull;
    }

    public static void setPossibleValues(ArrayList<String> possibleValues) {
       Property.possibleValues = possibleValues;
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
}
