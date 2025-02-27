package usc.edu.OwlEye.ElementsProperties;

import usc.edu.OwlEye.OwlConstants;

import java.util.ArrayList;
import java.util.Arrays;

public class Width extends Property {



    //String texts
    public final static String propertyName ="layoutWidth";
    //Boolean values
    public Boolean isLayoutWidth;

    public static Boolean  canBeNumerical=true;
    public static Boolean isBooleanValue=false;
    public static Boolean canBeNull=false;
    public static ArrayList<String> possibleValues = new ArrayList<>(Arrays.asList(OwlConstants.wrapContent, OwlConstants.matchParent, OwlConstants.numericalValue));

    public Width()
    {
        super(propertyName);
        possibleValues = new ArrayList<>(Arrays.asList(OwlConstants.wrapContent, OwlConstants.matchParent, OwlConstants.numericalValue));
    }

    public Boolean getLayoutWidth() {
        return isLayoutWidth;
    }

    public void setLayoutWidth(Boolean layoutWidth) {
        isLayoutWidth = layoutWidth;
    }
    public String getPropertyName() {
        return propertyName;
    }
}
