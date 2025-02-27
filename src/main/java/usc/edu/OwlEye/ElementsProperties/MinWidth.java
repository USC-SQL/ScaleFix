package usc.edu.OwlEye.ElementsProperties;

import usc.edu.OwlEye.OwlConstants;

import java.util.ArrayList;
import java.util.Arrays;

public class MinWidth extends Property{
    public final static String  propertyName ="minWidth";
    public String currentStaticVal;
    public String currentDynamicVal;
    static ArrayList<String> possibleValues = new ArrayList<>(Arrays.asList(OwlConstants.numericalValue));

    public MinWidth(String name) {
        super(name);

    }

    public MinWidth()
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
        MinWidth.possibleValues = possibleValues;
    }
}
