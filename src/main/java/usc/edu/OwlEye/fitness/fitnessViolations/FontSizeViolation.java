package usc.edu.OwlEye.fitness.fitnessViolations;

public class FontSizeViolation {
    String elementXpath;
    double defaultUIFontSize;
    double largestUIFntSize;
    double amountOfViolations;


    public FontSizeViolation(){
        elementXpath = "";
        defaultUIFontSize = 0;
        largestUIFntSize = 0;
        amountOfViolations = 0;
    }
}
