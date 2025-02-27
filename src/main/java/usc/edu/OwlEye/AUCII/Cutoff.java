package usc.edu.OwlEye.AUCII;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import usc.edu.OwlEye.ElementsProperties.*;
import usc.edu.OwlEye.OwlConstants;
import usc.edu.OwlEye.VHTree.XMLUtils;
import usc.edu.SALEM.util.Util;

import java.util.*;

public class Cutoff extends AUCIIssue {

    ArrayList<Node<DomNode>> problematicElements;



    ArrayList<Node<DomNode>> impactedElements;
    String problematicElement = "";  //right now I am using the ID

    String fullText = "";
    String OCRText = "";

    String issueType = ""; // Vertical or Horizontal cutoff (top or down)
    String issueLocation; // horizontal: top or down, vertical: left or right

    public Cutoff(String impactedElement, String fullText, String OCRText, String issueScore, String issueType, String issueLocation) {
        super(issueScore,issueType);
        this.problematicElement = impactedElement;
        this.fullText = fullText;
        this.OCRText = OCRText;
        //this.issueScore = issueScore;
        this.issueType = issueType;
        this.issueLocation = issueLocation;
        this.problematicElements = new ArrayList<>();


    }

    public Cutoff(String[] issueData) {
        // issue data extracted from CSV
        this(issueData[0], issueData[1], issueData[2], issueData[3], issueData[4], issueData[5]);
//        String elementID=issueData[0];
//        String fullText=issueData[1];
//        String OCRText=issueData[2];
//        String issueScore=issueData[3];
//        String issueType=issueData[4];
//        String issueLocation=issueData[5];

    }

    public String getProblematicElement() {
        return problematicElement;
    }

    public void setProblematicElement(String problematicElement) {
        this.problematicElement = problematicElement;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public String getOCRText() {
        return OCRText;
    }

    public void setOCRText(String OCRText) {
        this.OCRText = OCRText;
    }


    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getIssueLocation() {
        return issueLocation;
    }
    public ArrayList<Node<DomNode>> getProblematicElements() {
        return problematicElements;
    }

    public void setProblematicElements(ArrayList<Node<DomNode>> problematicElements) {
        this.problematicElements = problematicElements;
    }
    public void setIssueLocation(String issueLocation) {
        this.issueLocation = issueLocation;
    }

    public Set<Property> getApplicableProperties() {
        System.out.println("Cutoff Applicable Properties");
        Set<Property> applicableProperties = new HashSet<Property>();
        // We know height and width is a must so we directly add them. The rest depends on the issue and the attributes inside the element

        if (issueType.equals("Horizontal".toLowerCase())) {
            applicableProperties.add(new Height());

            //return new HashSet<>(Arrays.asList(new Height(),new MaxLine()));
            //return new HashSet<>(Arrays.asList(new MaxLine()));
        } else {
            applicableProperties.add(new Width());
            //return new HashSet<>(Arrays.asList(new Width()));
        }
        // In theory we should check the attributes of the element and add the applicable properties maybe ToDo: we examine type of issue and the aviailable attributes of the impacted elements. If for example it contains Maxline we consider this as applicable property?
        HashMap<String, String> definedProperties = findRelativeAttributesDefinedInElement(impactedElements, applicableProperties);
        for (Map.Entry<String, String> entry : definedProperties.entrySet()) {
            String key = entry.getKey();
            if (key.contains("padding")){
                String propText = OwlConstants.Full_To_Short_Attributes.get(key);
                Padding padding = new Padding(propText);
                applicableProperties.add(padding);
            }
            else if(key.contains("margin")){
                String propText = OwlConstants.Full_To_Short_Attributes.get(key);
                Margin margin = new Margin(propText);
                applicableProperties.add(margin);
            }
        }
      //  applicableProperties.addAll(definedProperties.keySet());
//        System.exit(1);
        applicableProperties.add(new MaxLine());

        return applicableProperties;
//        Set<String> properties= new HashSet<>();
//        properties.addAll(Arrays.asList(new String[]{"android:layout_width", "android:layout_height"}));
//        return  properties;
    }

    private HashMap<String, String> findRelativeAttributesDefinedInElement(ArrayList<Node<DomNode>> impactedElements, Set<Property> applicableProperties) {
        // We check the attributes of the element and add the applicable properties
        System.out.println("Cutoff findRelativeAttributesDefinedInElement");

        HashMap<String,String> definedProperties = new HashMap<>();
        // We start by the problematic elements that contain the issues then later on we can also check the impacted elements
        for (Node<DomNode> node : problematicElements
             ) {
            DomNode data = node.getData();
            Map<String, String> attributes = data.getAttributes();
            for (String key : attributes.keySet()
                 ) {
                String att = key;
                String value = attributes.get(key);
                if(OwlConstants.ATTRIBUTES_TEXT_CUTOFF_VERTICAL.keySet().contains(att.trim())){
                    System.out.println("Attribute: " + att + " Value: " + value);
                    definedProperties.put(att,value);
                }


            }
        }

        return definedProperties;
    }

    public void setImpactedElements(ArrayList<Node<DomNode>> impactedElements) {
        this.impactedElements = impactedElements;
    }
    public Set<String> getPossibleValues(Property property) {
        System.out.println("Cutoff Applicable Values");
        ArrayList<String> possibleValues;

        possibleValues = property.getPossibleValues();

        // Right now I am just returning all possible values for all properties maybe laterwe can return only the possible values for the property after analyzing the issue especcially for AlignParent
        if (property instanceof Height) {
            System.out.println("Height");


        } else if (property instanceof Width) {
            System.out.println("Width");

        }

        return new HashSet<>(possibleValues);
    }


    public ArrayList<Node<DomNode>> getImpactedElements(XMLUtils vh) {
        //Right now I am just returning the impacted element
        ArrayList<Node<DomNode>> impactedElements = new ArrayList<>();
        Node<DomNode> element = vh.searchByID_T(this.getProblematicElement(), null);
        if (element != null) {
            impactedElements.add(element);
        }
        return impactedElements;
    }

public void addProblematicElement(Node<DomNode> problematicElement) {
        this.problematicElements.add(problematicElement);
    }

    public void addImpactedElement(Node<DomNode> impactedElement) {
        this.impactedElements.add(impactedElement);
    }
    public HashMap<Node<DomNode>, HashMap<String, String>> calculateSuggestedValues(ArrayList<Node<DomNode>> impactedElements, Property cssProperty, Set<String> possibleValues) {
        // Based on the type of issue and our analysis as well as the impacted elemnents we specify the suggested values for each element in the ArrayList<Node<DomNode>> impactedElements and return Element,Property, Value which can be used to generate a gene
        HashMap<Node<DomNode>, HashMap<String, String>> suggestedValuesForGenes = new HashMap<>();
        for (Node<DomNode> element : impactedElements) {
            Map<String, String> allAttributes = element.getData().getAttributes();
            String attribute = null;
            if (cssProperty instanceof Height) {
                attribute = Height.propertyName;
            } else if (cssProperty instanceof Width) {
                attribute = Width.propertyName;
            }
            else if (cssProperty instanceof MaxLine) {
                attribute = MaxLine.propertyName;
            }
            String full_attribute = OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(attribute);
            String value = allAttributes.get(full_attribute);
            if (value != null) { // Right now I am only checking  if the attribute exist in the element but maybe I need to also check when it does not exist
                //see if the value is one of the possible values and select the other ones
                HashMap<String, String> filteredPossibleValue = isElementCurrentValueInSuggestedValue(element, cssProperty, possibleValues, attribute, value);
                suggestedValuesForGenes.put(element, filteredPossibleValue);
            }
        }


        return suggestedValuesForGenes;
    }

    private HashMap<String, String> isElementCurrentValueInSuggestedValue(Node<DomNode> element, Property cssProperty, Set<String> possibleValues, String attribute, String value) {
        //  given an element and attribute and value check if the value is one of the possible values. If it is one of them select the other ones as possible values (unless numerical)
        HashMap<String, String> filteredPossibleValues = new HashMap<>();
//        if(Util.getNumbersFromString(value).size()>0){ // it is numerical

        for (String possibleValue : possibleValues) {
            if (possibleValue.equals(OwlConstants.numericalValue)) {
//                if (value.matches(OwlConstants.numericalRegex)) { // it is numerical
                    filteredPossibleValues.put(OwlConstants.numericalValue,attribute);
     //           }
            }else {

                if (!possibleValue.equals(value)) {
                    filteredPossibleValues.put(possibleValue,attribute);
                }
            }
          //  filteredPossibleValues.put( possibleValue,attribute);
        }
//        if (value.matches(OwlConstants.numericalRegex)) { // it is numerical
//            filteredPossibleValues.put(attribute, OwlConstants.numericalValue);
//        } else { // it is not numerical, so we just remove it and return the rest
//            for (String possibleValue : possibleValues) {
//                if (!possibleValue.equals(value)) {
//                    filteredPossibleValues.put(attribute, possibleValue);
//                }
//            }
            //if the value is numerical then we can't suggest any other values
            return filteredPossibleValues;


    }


    public HashMap<Node<DomNode>, HashMap<String, Set<String>>> computeConcreteValues(HashMap<Node<DomNode>, HashMap<String, String>> suggestedValuesForElements, Property cssProperty, Set<String> possibleValues) {
        // After getting applicable properties and possible values we can compute the concrete values for each element

        HashMap<Node<DomNode>, HashMap<String, Set<String>>> concreteValuesForElements = new HashMap<>();
        for (Node<DomNode> element : suggestedValuesForElements.keySet()) {
            HashMap<String, String> suggestedValues = suggestedValuesForElements.get(element);
            if (suggestedValues.size() == 0) {
                continue;
            }
            HashMap<String, Set<String>> concreteValues = new HashMap<>();
            for (String suggVal : suggestedValues.keySet()) {

                String attribute = suggestedValues.get(suggVal);
                String actualValue = element.getData().getAttr(OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(attribute));
                if (actualValue == null) { // maybe it does not exist and we are adding this attribute, righ now I am not hanlding that but TODO: I shuld handle that
                    System.out.println("Value does not exist for attribute "+attribute+" in element "+element.getData().getTagName());

                }
                else {
                    String valueToAdd = null;
                    if (suggVal.contains(OwlConstants.numericalValue)) { // if numerical we already set OwlConstants.numerical
                        Double number = actualValue.matches(OwlConstants.numericalRegex) ? Util.getNumbersFromString(actualValue).get(0) : null;
                        number = computeNumericalValue(element, attribute, number); // we compute new value based on the current value in the element

                        if (number != null) {
                            valueToAdd = number.toString();
                        } else {
                            // suggested is numerical but the attribute value right now is not numerical ( maybe wrap_content or match_parent) so we need to find new way to calculate the value
                        }
                    }else if (suggVal.contains(OwlConstants.shouldRemoveAttribute)) { // if none then we know it means we should remove the attribute from the element
                        valueToAdd = suggVal; // right now i am handling that later when we apply the changes
                    }
                        else{


                        valueToAdd = suggVal;// it is not numerical so we just add it, but maybe we should do some analysis to see if it is a valid value

                    }
                    if (valueToAdd != null) {
                        if (concreteValues.get(attribute) == null) { // first time we add a value for this attribute
                            Set<String> attValues = new HashSet<>();
                            attValues.add(valueToAdd);
                            concreteValues.put(attribute, attValues);
                        } else {
                            concreteValues.get(attribute).add(valueToAdd);
                        }
                    } // if null we don't add it

                }


            }

            concreteValuesForElements.put(element, concreteValues);
        }
        return concreteValuesForElements;
    }

    private Double computeNumericalValue(Node<DomNode> element, String attribute, Double number) {

        if(number!=null){
            // already there is a number so we just get a new number based on it based on the issue type and value. Right now I am adding 10 to the number
            number+=15;
        }
        else{
            // there is no numerical value so how can we generate a new one? Right now I am just getting it dynamic value for height and width and adding 10 to it
            if (attribute.equals(Height.propertyName)) {
               number= Double.valueOf(element.getData().height)+15;
            } else if (attribute.equals(Width.propertyName)) {
                number= Double.valueOf(element.getData().width)+15;
            }
        }

        return number;
    }



}