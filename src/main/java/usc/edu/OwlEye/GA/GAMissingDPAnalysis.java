package usc.edu.OwlEye.GA;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import org.tinylog.Logger;
import usc.edu.OwlEye.AUCII.AUCIIssue;
import usc.edu.OwlEye.AUCII.Missing;
import usc.edu.OwlEye.ElementsProperties.*;
import usc.edu.OwlEye.GAChanges.GAChange;
import usc.edu.OwlEye.GAChanges.GAElementToChange;
import usc.edu.OwlEye.OwlConstants;
import usc.edu.OwlEye.OwlEye;
import usc.edu.OwlEye.UIModels.SRG;
import usc.edu.OwlEye.UIModels.SRGNode;
import usc.edu.OwlEye.util.Utils;
import usc.edu.SALEM.util.Util;

import java.util.*;

public class GAMissingDPAnalysis extends GADependencyAnalysis {

    public HashMap<String, HashMap<String, GAChange>> MissingDependencyAnalysis(HashMap<String, Missing> missingIssues) {
        HashMap<String, HashMap<String, GAChange>> groupsOfChanges = new HashMap<>();

        groupsOfChanges.put("group1", new HashMap<>()); // size 1 only + srg + vsrg
        groupsOfChanges.put("group2", new HashMap<>()); // size 2 only + srg + vsrg

        groupsOfChanges.put("group3", new HashMap<>());
        groupsOfChanges.put("group4", new HashMap<>());
        groupsOfChanges.put("group5", new HashMap<>());
        groupsOfChanges.put("group6", new HashMap<>());
        groupsOfChanges.put("group7", new HashMap<>());
        groupsOfChanges.put("group8", new HashMap<>());
        groupsOfChanges.put("group9", new HashMap<>());
        groupsOfChanges.put("group10", new HashMap<>());
        groupsOfChanges.put("group11", new HashMap<>());
        groupsOfChanges.put("group12", new HashMap<>());
        groupsOfChanges.put("groupScrollView", new HashMap<>()); // ScrollView

        for (String key : missingIssues.keySet()) {
            Missing missing = missingIssues.get(key);
            int noOfMissingElements=missing.getNumberOfMissingElements();


            HashMap<String, HashMap<String, GAElementToChange>> fixSetForMissing = GetFixSetForMissing(missing);
            Logger.debug("Fix set for collision issue " + missing.getIssueID() + " is " + fixSetForMissing);

            createGroupOfChangesMissing(missing, fixSetForMissing, groupsOfChanges);


        }

        Logger.debug("Done Analyzing all collision issues " + groupsOfChanges);
        return groupsOfChanges;


    }





    private HashMap<String, HashMap<String, GAElementToChange>> GetFixSetForMissing(Missing missing) {

        HashMap<String, HashMap<String, GAElementToChange>> elementsToChange = new HashMap<>(); //string: id or xpath of the element

        elementsToChange.put("problematic", new HashMap<>());
        elementsToChange.put("wrg", new HashMap<>());
        elementsToChange.put("srg", new HashMap<>());
        elementsToChange.put("vsrg", new HashMap<>());
        elementsToChange.put("padding", new HashMap<>()); // we add all elements that sibiling and parent of the problematic element and have padding (including the problematic element itself)
        elementsToChange.put("margin", new HashMap<>());
        // size dependent elements (parent)
        String mainProperty = Height.propertyName;
        if (missing.getIssueType().equalsIgnoreCase("width")) {
            mainProperty = Width.propertyName;
            // this has to be done later
        }

        boolean checkWRG = true;
        boolean checkSRG = true;
        boolean checkVSRG = true;
        boolean checkPadding = true;
        boolean checkMargin = true;
        boolean checkLayout = false;
        // (1) elements based on SRG

        ArrayList<Node<DomNode>> problematicNodes = missing.getProblematicElementsNodes();
        ArrayList<String> problematicXpaths = new ArrayList<>();

        for (Node<DomNode> problematicElement : problematicNodes) {
//            problematicIDs.add(problematicElement.getData().getId());
            problematicXpaths.add(problematicElement.getData().getxPath());
            GAElementToChange problematicElementToChange = new GAElementToChange(problematicElement, "problematic", OwlConstants.CHANGE_DECREASE, mainProperty); // usually we know that we need to increase or change something. For now I will keep it as decrease for default
            elementsToChange.get("problematic").put(problematicElement.getData().getId(), problematicElementToChange);
            List<Node<DomNode>> children = problematicElement.getChildren();
            if(children!=null &&children.size()>0){
                int noOfChildren=children.size();
                String problematicTag=problematicElement.getData().getTagName();
                if (noOfChildren>1){
                    // we add the children to the list of problematic elements
                    for (Node<DomNode> child : children) {
//                        problematicIDs.add(child.getData().getId());
                        GAElementToChange problematicChildElementToChange = new GAElementToChange(child, "problematic", OwlConstants.CHANGE_DECREASE, mainProperty); // usually we know that we need to increase or change something. For now I will keep it as decrease for default
                       elementsToChange.get("problematic").put(child.getData().getId(), problematicChildElementToChange);// stopped this March 26
                    }
                }



            }


            // add sibilings of the problematic elements only if they are stricly left or right TODO: consider only strict left and right
            List<Node<DomNode>> siblings = problematicElement.getParent().getChildren();
            if(siblings!=null &&siblings.size()>0){
                for (Node<DomNode> sib : siblings) {
//                        problematicIDs.add(child.getData().getId());
                    GAElementToChange problematicChildElementToChange = new GAElementToChange(sib, "problematic", OwlConstants.CHANGE_DECREASE, mainProperty); // usually we know that we need to increase or change something. For now I will keep it as decrease for default
                    elementsToChange.get("problematic").put(sib.getData().getId(), problematicChildElementToChange);
                }
            }
        }

        if (checkSRG) {
            findImpactedElementsBasedOnSRGMissing(problematicXpaths, elementsToChange.get("srg"), mainProperty);


        }
        if (checkMargin) {
            // (4) elements based on margin
            findImpactedElementsBasedOnMarginMissing(missing, elementsToChange.get("margin"),  mainProperty);
        }

        if (checkPadding) {
            // (4) elements based on margin
            findImpactedElementsBasedOnPaddingMissing(missing, elementsToChange.get("padding"),  mainProperty);
        }



        return elementsToChange;

    }

    private void findImpactedElementsBasedOnSRGMissing(ArrayList<String> problematicXpaths, HashMap<String, GAElementToChange>
            elementsToChange, String mainProperty) {

        SRG srg = OwlEye.getOriginalDefaultUISRG();
        for (String problematicElementXPath : problematicXpaths
        ) {
            String xPath = problematicElementXPath;
            List<SRGNode> sizeDependentElements = srg.getDependentNodesMap2().get(mainProperty).get(xPath); // for the property
            //System.out.println(srg);
            if (sizeDependentElements != null && sizeDependentElements.size() > 0) {
                for (SRGNode sizeDependentElement : sizeDependentElements
                ) {
                    double ratio = sizeDependentElement.getRatio();

                    Node<DomNode> node = sizeDependentElement.getXmlNode();
                    GAElementToChange e = new GAElementToChange(node, "SRG", OwlConstants.CHANGE_INCREASE, mainProperty);
                    if (mainProperty.equalsIgnoreCase(Height.propertyName)) {
                        e.setHeightRatio(ratio);
                    } else {
                        // width
                        e.setWidthRatio(ratio);
                    }
                    elementsToChange.put(node.getData().getId(), e);

                }
            }
        }



    }


    /************** supportive methods **************/
    private void createGroupOfChangesForMarginElementsMissing(HashMap<String, GAElementToChange> marginElements,
                                                              HashMap<String, HashMap<String, GAChange>> groupsOfChanges) {

        // we start from group 3  see the main method for more details of the groups
        // for missing we stricly focus on height decrease so we do not need all the increase group
        HashMap<String, GAChange> groupM1 = new HashMap<>(); // pure margin 00%
        HashMap<String, GAChange> groupM2 = new HashMap<>(); // pure margin 30%
        HashMap<String, GAChange> groupM3 = new HashMap<>(); // pure margin 50%
        HashMap<String, GAChange> groupM4 = new HashMap<>(); // pure margin 00% with height group1
        HashMap<String, GAChange> groupM5 = new HashMap<>(); // pure margin 00% with height group2
        HashMap<String, GAChange> groupM6 = new HashMap<>(); // pure margin 30% with height group1
        HashMap<String, GAChange> groupM7 = new HashMap<>(); // pure margin 30% with height group2
        HashMap<String, GAChange> groupM8 = new HashMap<>(); // pure margin 50% with height group1
        HashMap<String, GAChange> groupM9 = new HashMap<>(); // pure margin 50% with height group2


//        HashMap<String, GAChange> groupM3 = new HashMap<>(); // only for padding and margins 50%
//        HashMap<String, GAChange> groupM4 = new HashMap<>(); // only for padding and margins 0%
//        HashMap<String, GAChange> groupM5 = new HashMap<>(); //only for padding and margins 30%
        for (String elementId : marginElements.keySet()
        ) {
            GAElementToChange elementToChange = marginElements.get(elementId);
            String changeDirection=elementToChange.getDirectionFocus();
            Node<DomNode> currNode = elementToChange.getNode();
            Map<String, String> marginAttributesToFind;
            if (changeDirection.equalsIgnoreCase(Width.propertyName)) {
                marginAttributesToFind = OwlConstants.MARGINS_FOR_VERTICAL_CUTOFF_ISSUES;

            } else {
                marginAttributesToFind = OwlConstants.MARGINS_FOR_HORIZONTAL_CUTOFF_ISSUES;
            }
            // I have not created classes or modeled padding atts so right now I am just extracting the values from the element
            Map<String, String> atts = currNode.getData().getAttributes();
            for (String attName : atts.keySet()
            ) {
                if(marginAttributesToFind.containsKey(attName)){
                    //         if (attName.contains("margin")) {

                    String currentValue = atts.get(attName);
                    Double currentNumericalValue = 2.0; // default
                    if (Utils.isValueNumerical(currentValue)) {

                        currentNumericalValue = Util.getNumbersFromString(currentValue).get(0);
                    }

                    if (currentValue != null && currentNumericalValue > 0) {
                        Margin margin = new Margin(attName);
                        margin.setCurrentVal(currentNumericalValue);



                        // group 1: decreae padding by 100%
                        double calculateNewVal = 0.0;
                        String changeVal = calculateNewVal + "";
                        String changeType = OwlConstants.CHANGE_DECREASE;
                        GAChange change = new GAChange("margin", attName, changeType, changeVal, elementToChange, margin);
                        elementToChange.addChange(attName, change);
                        if (change != null) {
                            addElementToGroupOfChange( groupM1, elementToChange.getId() , change);
                            //group4.put(elementToChange.getId() + "#" + attName, change);
                        }

                        // group 2: increase padding by 70% (remaining is 0.30) and it is only this no height or width
                        calculateNewVal = Math.floor(currentNumericalValue * .30);
                        if (calculateNewVal < 0) {
                            calculateNewVal = 0;
                        }
                        changeVal = calculateNewVal + "";
                        changeType = OwlConstants.CHANGE_DECREASE;
                        change = new GAChange("margin", attName, changeType, changeVal, elementToChange, margin);
                        elementToChange.addChange(attName, change);
                        if (change != null) {
                            addElementToGroupOfChange( groupM2, elementToChange.getId() , change);
                        }

                        // group 3: decrease padding by 50%
                         calculateNewVal = Math.floor(currentNumericalValue * .50);
                        if (calculateNewVal < 0) {
                            calculateNewVal = 0;
                        }
                         changeVal = calculateNewVal + "";
                         changeType = OwlConstants.CHANGE_DECREASE;
                         change = new GAChange("margin", attName, changeType, changeVal, elementToChange, margin);
                        elementToChange.addChange(attName, change);
                        if (change != null) {
                            addElementToGroupOfChange( groupM3, elementToChange.getId() , change);

                        }

                    }
                }
            }

        }

        HashMap<String, GAChange> group1 = groupsOfChanges.get("group1"); // these already added for size and SRG
        HashMap<String, GAChange> group2 = groupsOfChanges.get("group2");


        groupsOfChanges.get("group3").putAll(groupM1);  // pure margins 0%
        groupsOfChanges.get("group4").putAll(groupM2);  // pure margins 30%
        groupsOfChanges.get("group5").putAll(groupM3);// pure margins 50%

        /** now we handle the cases where there is a height or width **/
        // we only add those if there is margin and padding because otherwise it is bascially the same as group 1 and 2
        if (groupM1.size() > 0) { // m1 is 0% so we add that to all size groups
            groupM4.putAll(groupM1);
            groupM5.putAll(groupM1);


            // adding dimensions
            groupM4.putAll(group1);
            groupM5.putAll(group2);

        }

        if (groupM2.size() > 0) { // m2 is 30% so we add that to all size groups
            groupM6.putAll(groupM2);
            groupM7.putAll(groupM2);

            // adding dimensions
            groupM6.putAll(group1);
            groupM7.putAll(group2);

        }
        if (groupM3.size() > 0) { // m3 is 50% so we add that to all size groups
            groupM8.putAll(groupM3);
            groupM9.putAll(groupM3);

            // adding dimensions
            groupM8.putAll(group1);
            groupM9.putAll(group2);

        }



        groupsOfChanges.get("group3").putAll(groupM1);
        groupsOfChanges.get("group4").putAll(groupM2);
        groupsOfChanges.get("group5").putAll(groupM3);
        groupsOfChanges.get("group6").putAll(groupM4);
        groupsOfChanges.get("group7").putAll(groupM5);
        groupsOfChanges.get("group8").putAll(groupM6);
        groupsOfChanges.get("group9").putAll(groupM7);




    }


    private void createGroupOfChangesForPaddingElementsMissing2(HashMap<String, GAElementToChange> marginElements,
                                                              HashMap<String, HashMap<String, GAChange>> groupsOfChanges) {

        // we start from group 3  see the main method for more details of the groups
        // for missing we stricly focus on height decrease so we do not need all the increase group
        HashMap<String, GAChange> groupM1 = new HashMap<>(); // pure margin 00%
        HashMap<String, GAChange> groupM2 = new HashMap<>(); // pure margin 30%
        HashMap<String, GAChange> groupM3 = new HashMap<>(); // pure margin 50%
        HashMap<String, GAChange> groupM4 = new HashMap<>(); // pure margin 00% with height group1
        HashMap<String, GAChange> groupM5 = new HashMap<>(); // pure margin 00% with height group2
        HashMap<String, GAChange> groupM6 = new HashMap<>(); // pure margin 30% with height group1
        HashMap<String, GAChange> groupM7 = new HashMap<>(); // pure margin 30% with height group2
        HashMap<String, GAChange> groupM8 = new HashMap<>(); // pure margin 50% with height group1
        HashMap<String, GAChange> groupM9 = new HashMap<>(); // pure margin 50% with height group2


//        HashMap<String, GAChange> groupM3 = new HashMap<>(); // only for padding and margins 50%
//        HashMap<String, GAChange> groupM4 = new HashMap<>(); // only for padding and margins 0%
//        HashMap<String, GAChange> groupM5 = new HashMap<>(); //only for padding and margins 30%
        for (String elementId : marginElements.keySet()
        ) {
            GAElementToChange elementToChange = marginElements.get(elementId);
            String changeDirection=elementToChange.getDirectionFocus();
            Node<DomNode> currNode = elementToChange.getNode();
            Map<String, String> marginAttributesToFind;
            if (changeDirection.equalsIgnoreCase(Width.propertyName)) {
                marginAttributesToFind = OwlConstants.PADDING_FOR_VERTICAL_CUTOFF_ISSUES;

            } else {
                marginAttributesToFind = OwlConstants.PADDING_FOR_HORIZONTAL_CUTOFF_ISSUES;
            }
            // I have not created classes or modeled padding atts so right now I am just extracting the values from the element
            Map<String, String> atts = currNode.getData().getAttributes();
            for (String attName : atts.keySet()
            ) {
                if(marginAttributesToFind.containsKey(attName)){
                    //         if (attName.contains("margin")) {

                    String currentValue = atts.get(attName);
                    Double currentNumericalValue = 2.0; // default
                    if (Utils.isValueNumerical(currentValue)) {

                        currentNumericalValue = Util.getNumbersFromString(currentValue).get(0);
                    }

                    if (currentValue != null && currentNumericalValue > 0) {
                        Margin margin = new Margin(attName);
                        margin.setCurrentVal(currentNumericalValue);



                        // group 1: decreae padding by 100%
                        double calculateNewVal = 0.0;
                        String changeVal = calculateNewVal + "";
                        String changeType = OwlConstants.CHANGE_DECREASE;
                        GAChange change = new GAChange("padding", attName, changeType, changeVal, elementToChange, margin);
                        elementToChange.addChange(attName, change);
                        if (change != null) {
                            addElementToGroupOfChange( groupM1, elementToChange.getId() , change);
                            //group4.put(elementToChange.getId() + "#" + attName, change);
                        }

                        // group 2: increase padding by 70% (remaining is 0.30) and it is only this no height or width
                        calculateNewVal = Math.floor(currentNumericalValue * .30);
                        if (calculateNewVal < 0) {
                            calculateNewVal = 0;
                        }
                        changeVal = calculateNewVal + "";
                        changeType = OwlConstants.CHANGE_DECREASE;
                        change = new GAChange("padding", attName, changeType, changeVal, elementToChange, margin);
                        elementToChange.addChange(attName, change);
                        if (change != null) {
                            addElementToGroupOfChange( groupM2, elementToChange.getId() , change);
                        }

                        // group 3: decrease padding by 50%
                        calculateNewVal = Math.floor(currentNumericalValue * .50);
                        if (calculateNewVal < 0) {
                            calculateNewVal = 0;
                        }
                        changeVal = calculateNewVal + "";
                        changeType = OwlConstants.CHANGE_DECREASE;
                        change = new GAChange("padding", attName, changeType, changeVal, elementToChange, margin);
                        elementToChange.addChange(attName, change);
                        if (change != null) {
                            addElementToGroupOfChange( groupM3, elementToChange.getId() , change);

                        }

                    }
                }
            }

        }

        HashMap<String, GAChange> group1 = groupsOfChanges.get("group1"); // these already added for size and SRG
        HashMap<String, GAChange> group2 = groupsOfChanges.get("group2");


        groupsOfChanges.get("group3").putAll(groupM1);  // pure margins 0%
        groupsOfChanges.get("group4").putAll(groupM2);  // pure margins 30%
        groupsOfChanges.get("group5").putAll(groupM3);// pure margins 50%

        /** now we handle the cases where there is a height or width **/
        // we only add those if there is margin and padding because otherwise it is bascially the same as group 1 and 2
        if (groupM1.size() > 0) { // m1 is 0% so we add that to all size groups
            groupM4.putAll(groupM1);
            groupM5.putAll(groupM1);


            // adding dimensions
            groupM4.putAll(group1);
            groupM5.putAll(group2);

        }

        if (groupM2.size() > 0) { // m2 is 30% so we add that to all size groups
            groupM6.putAll(groupM2);
            groupM7.putAll(groupM2);

            // adding dimensions
            groupM6.putAll(group1);
            groupM7.putAll(group2);

        }
        if (groupM3.size() > 0) { // m3 is 50% so we add that to all size groups
            groupM8.putAll(groupM3);
            groupM9.putAll(groupM3);

            // adding dimensions
            groupM8.putAll(group1);
            groupM9.putAll(group2);

        }



        groupsOfChanges.get("group3").putAll(groupM1);
        groupsOfChanges.get("group4").putAll(groupM2);
        groupsOfChanges.get("group5").putAll(groupM3);
        groupsOfChanges.get("group6").putAll(groupM4);
        groupsOfChanges.get("group7").putAll(groupM5);
        groupsOfChanges.get("group8").putAll(groupM6);
        groupsOfChanges.get("group9").putAll(groupM7);




    }

    private void createGroupOfChangesForPaddingElementsMissing(HashMap<String, GAElementToChange> paddingElements,
                                                               HashMap<String, HashMap<String, GAChange>> groupsOfChanges) {

        // we start from group 3  see the main method for more details of the groups
        // for missing we stricly focus on height decrease so we do not need all the increase group
        HashMap<String, GAChange> groupM1 = new HashMap<>(); // pure margin 50%
        HashMap<String, GAChange> groupM2 = new HashMap<>(); // pure margin 0%
        HashMap<String, GAChange> groupM3 = new HashMap<>(); // pure margin 30%
        HashMap<String, GAChange> groupM4 = new HashMap<>(); // pure margin 50% with height group1
        HashMap<String, GAChange> groupM5 = new HashMap<>(); // pure margin 50% with height group2
        HashMap<String, GAChange> groupM6 = new HashMap<>(); // pure margin 30% with height group1
        HashMap<String, GAChange> groupM7 = new HashMap<>(); // pure margin 30% with height group2



//        HashMap<String, GAChange> groupM3 = new HashMap<>(); // only for padding and margins 50%
//        HashMap<String, GAChange> groupM4 = new HashMap<>(); // only for padding and margins 0%
//        HashMap<String, GAChange> groupM5 = new HashMap<>(); //only for padding and margins 30%
        for (String elementId : paddingElements.keySet()
        ) {
            GAElementToChange elementToChange = paddingElements.get(elementId);
            String changeDirection=elementToChange.getDirectionFocus();
            Node<DomNode> currNode = elementToChange.getNode();
            Map<String, String> marginAttributesToFind;
            if (changeDirection.equalsIgnoreCase(Width.propertyName)) {
                marginAttributesToFind = OwlConstants.PADDING_FOR_VERTICAL_CUTOFF_ISSUES;

            } else {
                marginAttributesToFind = OwlConstants.PADDING_FOR_HORIZONTAL_CUTOFF_ISSUES;
            }
            // I have not created classes or modeled padding atts so right now I am just extracting the values from the element
            Map<String, String> atts = currNode.getData().getAttributes();
            for (String attName : atts.keySet()
            ) {
                if(marginAttributesToFind.containsKey(attName)){
                    //         if (attName.contains("margin")) {

                    String currentValue = atts.get(attName);
                    Double currentNumericalValue = 2.0; // default
                    if (Utils.isValueNumerical(currentValue)) {

                        currentNumericalValue = Util.getNumbersFromString(currentValue).get(0);
                    }

                    if (currentValue != null && currentNumericalValue > 0) {
                        Padding padding = new Padding(attName);
                        padding.setCurrentVal(currentNumericalValue);

                        // group 1: decrease padding by 50%
                        double calculateNewVal = Math.floor(currentNumericalValue * .50);
                        if (calculateNewVal < 0) {
                            calculateNewVal = 0;
                        }
                        String changeVal = calculateNewVal + "";
                        String changeType = OwlConstants.CHANGE_DECREASE;
                        GAChange change = new GAChange("padding", attName, changeType, changeVal, elementToChange, padding);
                        elementToChange.addChange(attName, change);
                        if (change != null) {
                            addElementToGroupOfChange( groupM1, elementToChange.getId() , change);

                            // group3.put(elementToChange.getId() + "#" + attName, change);
                        }

                        // group 2: decreae padding by 100%
                        calculateNewVal = 0.0;

                        changeVal = calculateNewVal + "";
                        changeType = OwlConstants.CHANGE_DECREASE;
                        change = new GAChange("padding", attName, changeType, changeVal, elementToChange, padding);
                        elementToChange.addChange(attName, change);
                        if (change != null) {
                            addElementToGroupOfChange( groupM2, elementToChange.getId() , change);
                            //group4.put(elementToChange.getId() + "#" + attName, change);
                        }
                        // group 3: increase padding by 70% (remaining is 0.30) and it is only this no height or width
                        calculateNewVal = Math.floor(currentNumericalValue * .30);
                        if (calculateNewVal < 0) {
                            calculateNewVal = 0;
                        }
                        changeVal = calculateNewVal + "";
                        changeType = OwlConstants.CHANGE_DECREASE;
                        change = new GAChange("padding", attName, changeType, changeVal, elementToChange, padding);
                        elementToChange.addChange(attName, change);
                        if (change != null) {
                            addElementToGroupOfChange( groupM3, elementToChange.getId() , change);
                            //group5.put(elementToChange.getId() + "#" + attName, change);
                        }


                    }
                }
            }

        }

        HashMap<String, GAChange> group1 = groupsOfChanges.get("group1"); // these already added for size and SRG
        HashMap<String, GAChange> group2 = groupsOfChanges.get("group2");


        groupsOfChanges.get("group3").putAll(groupM1);  // pure margins 50%
        groupsOfChanges.get("group4").putAll(groupM2);  // pure margins 0%
        groupsOfChanges.get("group5").putAll(groupM3);// pure margins 30%

        /** now we handle the cases where there is a height or width **/
        // we only add those if there is margin and padding because otherwise it is bascially the same as group 1 and 2
        if (groupM2.size() > 0) { // m1 is 0% so we add that to all size groups
            groupM4.putAll(groupM2);
            groupM5.putAll(groupM2);


            // adding dimensions
            groupM4.putAll(group1);
            groupM5.putAll(group2);

        }

        if (groupM3.size() > 0) { // m3 is 30% so we add that to all size groups
            groupM6.putAll(groupM3);
            groupM7.putAll(groupM3);

            // adding dimensions
            groupM6.putAll(group1);
            groupM7.putAll(group2);

        }



        groupsOfChanges.get("group3").putAll(groupM1);
        groupsOfChanges.get("group4").putAll(groupM2);
        groupsOfChanges.get("group5").putAll(groupM3);
        groupsOfChanges.get("group6").putAll(groupM4);
        groupsOfChanges.get("group7").putAll(groupM5);
        groupsOfChanges.get("group8").putAll(groupM6);
        groupsOfChanges.get("group9").putAll(groupM7);




    }
    private void findImpactedElementsBasedOnMarginMissing(Missing missing, HashMap<String, GAElementToChange> margin, String changeDirection) {
        Logger.trace("Cutoff findImpactedElementsBasedOnMargins");
        Map<String, String> marginAttributesToFind;

        if (missing.getIssueType().equals("height".toLowerCase())) {
            marginAttributesToFind = OwlConstants.MARGINS_FOR_HORIZONTAL_CUTOFF_ISSUES;
        } else {
            marginAttributesToFind = OwlConstants.MARGINS_FOR_VERTICAL_CUTOFF_ISSUES;
        }

        // instead of parent & siblings in cuttoff we search the entire tree for marigns and we start from the root
        Node<DomNode> root = OwlEye.getOriginalDefaultUI().getXMLTree().getRoot();
        Queue<Node<DomNode>> q = new LinkedList<>();
        q.add(root);

        while (!q.isEmpty()) {
            Node<DomNode> currNode = q.remove();
            Map<String, String> attributes = currNode.getData().getAttributes();
            for (String key : attributes.keySet()
            ) {
                if (key.contains("_margin")) {
                    String att = key;
                    if (marginAttributesToFind.containsKey(att)) {
                        String value = attributes.get(key);
                        Double marginValue = Util.getNumbersFromString(value).get(0);
                        if (marginValue > 0) {
                            Logger.debug("Margin Attribute: " + att + " Value: " + value);
                            GAElementToChange elementToChange = new GAElementToChange(currNode, "margin", OwlConstants.CHANGE_DECREASE, changeDirection);
                            margin.put(currNode.getData().getId(), elementToChange);
                        }
                    }
                }
            }

            if (currNode.getChildren() != null) {
                for (Node<DomNode> child : currNode.getChildren()) {
                    q.add(child);
                }
            }
        }



    }
    private void findImpactedElementsBasedOnPaddingMissing(Missing missing, HashMap<String, GAElementToChange> margin, String changeDirection) {
        Map<String, String> marginAttributesToFind;

        if (missing.getIssueType().equals("height".toLowerCase())) {
            marginAttributesToFind = OwlConstants.PADDING_FOR_HORIZONTAL_CUTOFF_ISSUES;
        } else {
            marginAttributesToFind = OwlConstants.PADDING_FOR_VERTICAL_CUTOFF_ISSUES;
        }

        // instead of parent & siblings in cuttoff we search the entire tree for marigns and we start from the root
        Node<DomNode> root = OwlEye.getOriginalDefaultUI().getXMLTree().getRoot();
        Queue<Node<DomNode>> q = new LinkedList<>();
        q.add(root);

        while (!q.isEmpty()) {
            Node<DomNode> currNode = q.remove();
            Map<String, String> attributes = currNode.getData().getAttributes();
            for (String key : attributes.keySet()
            ) {
                if (key.contains("padding")) {
                    String att = key;
                    if (marginAttributesToFind.containsKey(att)) {
                        String value = attributes.get(key);
                        Double marginValue = Util.getNumbersFromString(value).get(0);
                        if (marginValue > 0) {
                            Logger.debug("Margin Attribute: " + att + " Value: " + value);
                            GAElementToChange elementToChange = new GAElementToChange(currNode, "margin", OwlConstants.CHANGE_DECREASE, changeDirection);
                            margin.put(currNode.getData().getId(), elementToChange);
                        }
                    }
                }
            }

            if (currNode.getChildren() != null) {
                for (Node<DomNode> child : currNode.getChildren()) {
                    q.add(child);
                }
            }
        }



    }

    private void createGroupOfChangesMissing(Missing missing, HashMap<String, HashMap<String, GAElementToChange>>
            fixSetForMissing, HashMap<String, HashMap<String, GAChange>> groupsOfChanges) {
        boolean includeSRG = true;
        boolean includeVSrg = false;
        boolean includePadding = true;
        boolean includeMargin = true;
        boolean includeRegularLayout = true;
        boolean includeLayoutWeight = false;
        boolean includeWRG = true;
        boolean includeregSPLRG = true;
        boolean includeScrollableText = true;
        boolean includeScrollView = false;
        createGroupOfChangesForProblematicMissing(fixSetForMissing, groupsOfChanges, fixSetForMissing.get("problematic"), missing);


        if (includeMargin) {
            HashMap<String, GAElementToChange> marginElements = fixSetForMissing.get("margin");
            createGroupOfChangesForMarginElementsMissing(marginElements, groupsOfChanges);
        }
        if (includePadding) {
            HashMap<String, GAElementToChange> marginElements = fixSetForMissing.get("padding");
            createGroupOfChangesForPaddingElementsMissing2(marginElements, groupsOfChanges); // these are the new padding changes method after Mrach 26

          //  createGroupOfChangesForPaddingElementsMissing(marginElements, groupsOfChanges);
        }
        if (includeSRG) {
            HashMap<String, GAElementToChange> srgElements = fixSetForMissing.get("srg");

            if (srgElements != null && srgElements.size() > 0) {
                createGroupOfChangesForSRGElementsMissing(srgElements, groupsOfChanges);
            }
        }
        if(OwlConstants.CURRENT_SCALING_VERSION_FOLDER.equalsIgnoreCase(OwlConstants.LARGEST_DISPLAY_LARGEST_FONT_FOLDER)){
            includeScrollView = true;
        }
        if (includeScrollView){
            fixSetForMissing.put("scrollView", new HashMap<String, GAElementToChange>());
            HashMap<String, GAElementToChange> scrollViewElements = fixSetForMissing.get("scrollView");
            //just add the current problematic element to the scrollview group and later we iterate through them to find the least common ancestor and create scollView there
            for (GAElementToChange elementToChange : fixSetForMissing.get("problematic").values()) {
                scrollViewElements.put(elementToChange.getId(), elementToChange);
                scrollViewElements.put(elementToChange.getId(), new GAElementToChange(elementToChange.getNode(), "scrollView", OwlConstants.CHANGE_ADD, ""));
                GAChange change = new GAChange("scroll", "",  OwlConstants.CHANGE_ADD, "", elementToChange, null);
                groupsOfChanges.get("groupScrollView").put(elementToChange.getId(), change);
            }



        }

    }

    private void createGroupOfChangesForSRGElementsMissing(HashMap<String, GAElementToChange> srgElements,
                                                           HashMap<String, HashMap<String, GAChange>> groupsOfChanges) {

        HashMap<String, GAChange> group1 = new HashMap<>();
        HashMap<String, GAChange> group2 = new HashMap<>();
        for (String problematicElementId : srgElements.keySet()) {
            HashMap<String, Property> propertiesToChangeObject = new HashMap<>();
            GAElementToChange element = srgElements.get(problematicElementId);
            String directionFocus = element.getDirectionFocus();
            Property sizeProp = null;
            if (directionFocus.equalsIgnoreCase(Height.propertyName)) {
                sizeProp = new Height();
                String dynamicVal = Utils.getDynamicValueInDDP(element.getNode().getData(), Height.propertyName);
                sizeProp.setCurrentDynamicVal(dynamicVal);
                String staticVal = Utils.getValueFromElement(element.getNode().getData(), Height.propertyName);
                sizeProp.setCurrentStaticVal(staticVal);
                propertiesToChangeObject.put(Height.propertyName, sizeProp);
            } else if (directionFocus.equalsIgnoreCase(Width.propertyName)) {
                sizeProp = new Width();
                String dynamicVal = Utils.getDynamicValueInDDP(element.getNode().getData(), Width.propertyName);
                sizeProp.setCurrentDynamicVal(dynamicVal);
                String staticVal = Utils.getValueFromElement(element.getNode().getData(), Width.propertyName);
                sizeProp.setCurrentStaticVal(staticVal);
                propertiesToChangeObject.put(Width.propertyName, sizeProp);
            }
            double currentChangeAmountPercent = -20;
            String changeType = OwlConstants.CHANGE_DECREASE;
            // gene group 1 for main size --> do not change how it is set (if it is fixed directly increase it or if it is constraints add Min)
            GAChange change = createDoNotChangeSizeTypeGroupCollision(element, sizeProp, changeType,currentChangeAmountPercent);
//            GAChange change = createDoNotChangeSizeTypeGroup(element, sizeProp,  changeType);
//            if (change != null) {
//                group1.put(element.getId(), change);
//            }
            if (change != null) {
                addElementToGroupOfChange(group1, element.getId(), change);
                addElementToGroupOfChange(group2, element.getId(), change);
            }


//            ArrayList<GAChange> change2 = createChangeSizeTypeGroup(element, sizeProp, changeType);
//            //change2=change;
//            if (change2 != null) {
//                for (GAChange gaChange : change2){
//                    if(gaChange!=null)
//                        group2.put(element.getId(), gaChange);}
//
//            }



        }
        groupsOfChanges.get("group1").putAll(group1);
        groupsOfChanges.get("group2").putAll(group2);

    }


    private void createGroupOfChangesForProblematicMissing(HashMap<String, HashMap<String, GAElementToChange>> fixSetForMissing,
                                                           HashMap<String, HashMap<String, GAChange>> groupsOfChanges,
                                                           HashMap<String, GAElementToChange> problematicElements, AUCIIssue issue) {
        String mainProperty = Height.propertyName;
        if (issue.getIssueType().equalsIgnoreCase("width")) {
            mainProperty = Width.propertyName;
        }


        // 1- main height and width


        HashMap<String, GAChange> group1 = new HashMap<>();
        HashMap<String, GAChange> group2 = new HashMap<>();
        for (String problematicElementId : problematicElements.keySet()) {
            HashMap<String, Property> propertiesToChangeObject = new HashMap<>();
            GAElementToChange element = problematicElements.get(problematicElementId);
            String directionFocus = element.getDirectionFocus();
            Property sizeProp = null;
            if (directionFocus.equalsIgnoreCase(Height.propertyName)) {
                sizeProp = new Height();
                String dynamicVal = Utils.getDynamicValueInDDP(element.getNode().getData(), Height.propertyName);
                sizeProp.setCurrentDynamicVal(dynamicVal);
                String staticVal = Utils.getValueFromElement(element.getNode().getData(), Height.propertyName);
                sizeProp.setCurrentStaticVal(staticVal);
                propertiesToChangeObject.put(Height.propertyName, sizeProp);
            } else if (directionFocus.equalsIgnoreCase(Width.propertyName)) {
                sizeProp = new Width();
                String dynamicVal = Utils.getDynamicValueInDDP(element.getNode().getData(), Width.propertyName);
                sizeProp.setCurrentDynamicVal(dynamicVal);
                String staticVal = Utils.getValueFromElement(element.getNode().getData(), Width.propertyName);
                sizeProp.setCurrentStaticVal(staticVal);
                propertiesToChangeObject.put(Width.propertyName, sizeProp);
            }

            String changeType = OwlConstants.CHANGE_DECREASE;
            double changeAmountPercentage = 5;
            GAChange[] fourChanges = createFourDimensionChangesMissing(element, sizeProp, changeAmountPercentage);
            // March 26 was just fourChanges[0]
//            if (fourChanges[0] != null) {
//                addElementToGroupOfChange(group1, element.getId(), fourChanges[0]);
//            }
            if (fourChanges[0] != null) {
                addElementToGroupOfChange(group1, element.getId(), fourChanges[0]);
            }
            if (fourChanges[1] != null) {
                addElementToGroupOfChange(group2, element.getId(), fourChanges[1]);
            }
            if (fourChanges[2] != null) {
                //    addElementToGroupOfChange(group3, element.getId(), fourChanges[2]);
            }
            if (fourChanges[3] != null) {
                // addElementToGroupOfChange(group4, element.getId(), fourChanges[3]);
            }
            if (fourChanges[4] != null) {
                addElementToGroupOfChange(group2, element.getId(), fourChanges[4]); //this is the min
            }

        }
        // gene group 1 for main size --> do not change how it is set (if it is fixed directly increase it or if it is constraints add Min)

//            GAChange change = createDoNotChangeSizeTypeGroupMissing(element, sizeProp, changeType);
//            if (change != null) {
//                group1.put(element.getId(), change);
//            }
//            ArrayList<GAChange> change2 = createChangeSizeTypeGroupMissing(element, sizeProp, changeType);
//            // GAChange change2=change;   // Activate this if  we are not considering the case where the size is set to wrap content for so I am adding group 1 to group 2
//            if (change2 != null) {
//                for (GAChange gaChange : change2) {
//                    if (gaChange != null)
//                        group2.put(element.getId(), gaChange);
//
//                }
//                }
//
//        }
        // 2- layout weight
        // we usually want to increase weight of problematic text view and decrease the other weight of the other textview

        Logger.debug("Start of the Problematic Layout changes method");
//
//        TreeMap<String, List<WRGNode>> propWRG = OwlEye.getOriginalDefaultUIWRG().getDependentNodesMap().get(mainProperty);
//
//        for (Map.Entry<String, GAElementToChange> problematicElement : problematicElementsSet.entrySet()) {
//            String id = problematicElement.getKey();
//            GAElementToChange gaElementToChange = problematicElement.getValue();
//            String parentXpath = gaElementToChange.getNode().getData().getParent().getxPath();
//            List<WRGNode> parentNodeWRG = propWRG.descendingMap().get(parentXpath);
//            if (parentNodeWRG == null) {
//                continue;
//            }
//            for (WRGNode wrgNode : parentNodeWRG) {
//                double parentTotalWeight;
//
//                if (wrgNode.getXmlNode().getData().getId().equals("problematicElementID")) {
//
//                }
//            }
//        }
        // Iterate through the problematic elements and for each one check all of its children and the children of its children looking for an image views.
        // If there is an image view then we want to increase the weight of the problematic element and decrease the weight of the image view
        ArrayList<String> imageXpaths;
        for (String problematicElementId : problematicElements.keySet()) {
            GAElementToChange problematicElement = problematicElements.get(problematicElementId);
            Node<DomNode> currNode = problematicElement.getNode();
            List<Node<DomNode>> currNodeChildren = currNode.getChildren();
            if(currNodeChildren==null || currNodeChildren.size()==0){
                continue;}
            for (Node<DomNode> child : currNodeChildren) {
                if (child.getData().getTagName().equalsIgnoreCase("ImageView")) {
                    // get height of problematic element
                    // get height of image view
                    String id=child.getData().getId();
                    if(id==null || id.isEmpty()){
                        continue;}
                    GAChange imgChange=handleImageView(child,id);
                    if(imgChange!=null){
                        addElementToGroupOfChange(group1, id, imgChange);
                        addElementToGroupOfChange(group2, id, imgChange);
                         imageXpaths = new ArrayList<>();
                        imageXpaths.add(child.getData().getxPath());
                        findImpactedElementsBasedOnSRGMissing(imageXpaths, fixSetForMissing.get("srg"), mainProperty);
                      //  fixSetForMissing.get("srg").put(id, imgChange.getElement());
                    }
                } else { // check if current node has children
                    List<Node<DomNode>> childChildren = child.getChildren();
                    if(childChildren==null || childChildren.size()==0){
                        continue;}
                    for (Node<DomNode> childChild : childChildren) {
                        if (childChild.getData().getTagName().equalsIgnoreCase("ImageView")) {
                            // get height of problematic element
                            // get height of image view
                            String id=childChild.getData().getId();
                            if(id==null || id.isEmpty()){
                                continue;}
                            GAChange imgChange=handleImageView(childChild,id);
                            if(imgChange!=null){
                                addElementToGroupOfChange(group1, id, imgChange);
                                addElementToGroupOfChange(group2, id, imgChange);
                                imageXpaths = new ArrayList<>();
                                imageXpaths.add(childChild.getData().getxPath());
                                findImpactedElementsBasedOnSRGMissing(imageXpaths, fixSetForMissing.get("srg"), mainProperty);
                                //fixSetForMissing.get("srg").put(id, imgChange.getElement());
                            }
                        }
                    }
                }

//                groupsOfChanges.get("group1").putAll(group1);
//                groupsOfChanges.get("group2").putAll(group2);
            }


        }
        groupsOfChanges.get("group1").putAll(group1);
        groupsOfChanges.get("group2").putAll(group2);
    }

    private GAChange handleImageView(Node<DomNode> child,String id) {
//        String id=child.getData().getId();
        // search this imageView in the original Large UI
        Node<DomNode> foundElement = OwlEye.getOriginalLargestUI().getXMLTree().searchByID_T(id, "ImageView");
        if(foundElement!=null){
            // get the height of the problematic element
            String height = Utils.getValueFromElement(foundElement.getData(), Height.propertyName);
            String dynamicHeight = Utils.getDynamicValueInDDP(foundElement.getData(), Height.propertyName);
            double heightDouble=0;
            if(dynamicHeight!=null && !dynamicHeight.isEmpty()){
                heightDouble=Util.getNumbersFromString(dynamicHeight).get(0);
            }
            Logger.debug("The height of the problematic element is "+height);

            if(heightDouble>100){
                // this is a large image view
                double changeAmountPercentage=-30;
                GAElementToChange gFound = new GAElementToChange(foundElement, "problematic", OwlConstants.CHANGE_DECREASE, Height.propertyName); // usually we know that we need to increase or change something. For now I will keep it as decrease for default

                GAChange change = addMaxDimensionChange(gFound, Height.propertyName, dynamicHeight, OwlConstants.CHANGE_DECREASE, changeAmountPercentage);
                if (change != null) {
                    Logger.debug("Created image change "+change);
                    return change;
                }
                // we want to decrease the weight of the problematic element and increase the weight of the image view

            }

        }
        return null;
    }

    protected GAChange[] createFourDimensionChangesMissing(GAElementToChange element, Property sizeProp, double changeAmountPercentage) {
        double currentChangeAmountPercent = changeAmountPercentage;
        String changeType = OwlConstants.CHANGE_DECREASE;
        if(changeType.equalsIgnoreCase(OwlConstants.CHANGE_DECREASE)){
            currentChangeAmountPercent = -changeAmountPercentage;
        }
        GAChange change1 = createDoNotChangeSizeTypeGroupCollision(element, sizeProp, changeType,currentChangeAmountPercent);

        ArrayList<GAChange> changes = createChangeSizeTypeGroupMissing(element, sizeProp, changeType,currentChangeAmountPercent);
        // GAChange change2=change;   // Activate this if  we are not considering the case where the size is set to wrap content for so I am adding group 1 to group 2
        GAChange change2=null;
        GAChange change5=null;
        if(changes.size()>0) {
            change2 = changes.get(0);
        }
        if(changes.size()>1) {
            change5=changes.get(1);
        }


        changeType = OwlConstants.CHANGE_INCREASE;
        if(changeType.equalsIgnoreCase(OwlConstants.CHANGE_INCREASE)) {
            currentChangeAmountPercent = Math.abs(changeAmountPercentage);
        }
        GAChange change3 = createDoNotChangeSizeTypeGroupCollision(element, sizeProp, changeType,currentChangeAmountPercent);

        ArrayList<GAChange> changes2 = createChangeSizeTypeGroupCollision(element, sizeProp, changeType,currentChangeAmountPercent);
        GAChange change4=null;
        GAChange change6=null;
        if(changes.size()>0) {
            change4 = changes.get(0);
        }
        if(changes.size()>1) {
            change6=changes.get(1);
        }
        // GAChange change2=change;   // Activate this if  we are not considering the case where the size is set to wrap content for so I am adding group 1 to group 2

        // return array of the four changes
        return new GAChange[]{change1, change2, change3, change4,change5};
    }

    private ArrayList<GAChange> createChangeSizeTypeGroupMissing(GAElementToChange element, Property sizeProp,
                                                                   String changeType,double changeAmountPercentage) {
        // get the size attribute set type: fixed or constraints (ie wrap_content or match_parent)
        GAChange change = null;
        GAChange change2 = null;
//        double changeAmountPercentage=-20;
        String staticVal = sizeProp.getCurrentStaticVal();
        if (Utils.isValueNumerical(staticVal)) {
            Double numericalStaticVal = null;
            List<Double> x = Util.getNumbersFromString(staticVal);
            if (x.size() > 0) {  // it is fixed so we should do wrap_content
                numericalStaticVal = x.get(0);
                String changeVal;

                if (numericalStaticVal > 0) { // it is set as fixed value in the app so we change it to wrap_content
                    changeVal = "-2";
                    change2 = null;
                    if (element.getNode().getData().getTagName().equalsIgnoreCase("EditText")||element.getNode().getData().getTagName().equalsIgnoreCase("TextView")) {
                        String minProp=sizeProp.getPropertyName();
                        String currDyn = sizeProp.getCurrentDynamicVal();

                        change2 = addMinChange2(element,minProp, currDyn,changeType,-10);
                    }
                    change = new GAChange(sizeProp.getPropertyName(), OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(sizeProp.getPropertyName()), changeType, changeVal, element, sizeProp);
                } else if (numericalStaticVal == -2 ){//|| numericalStaticVal==-1 ) { // it is set as wrap_content or match_parent so we change it to fixed value

                    boolean changeToFixed = false; /**FEB 6th: disabled this part to test*/
                    if (changeToFixed) {
                        String dynamicVal = sizeProp.getCurrentDynamicVal();
                        if (dynamicVal != null) {
                            List<Double> y = Util.getNumbersFromString(dynamicVal);
                            if (y.size() > 0) {
                                double numericalDynamicVal = y.get(0);
                                double newVal = Utils.changeCurrentValueByPercentage(numericalDynamicVal, changeAmountPercentage);
                                changeVal = String.valueOf(newVal);
                                change = new GAChange(sizeProp.getPropertyName(), OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(sizeProp.getPropertyName()), changeType, changeVal, element, sizeProp);
                            }
                        }
                    }
                }



            }

        } else { // it is an exceptional  case that I will handle later

        }
        ArrayList<GAChange> changes = new ArrayList<>();
        if (change != null) {
            changes.add(change);
        }
        if (change2 != null) {
            changes.add(change2);
        }
        return changes;
    }

    }