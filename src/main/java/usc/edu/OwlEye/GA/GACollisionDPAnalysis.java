package usc.edu.OwlEye.GA;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import org.tinylog.Logger;
import usc.edu.OwlEye.AUCII.AUCIIssue;
import usc.edu.OwlEye.AUCII.Collision;
import usc.edu.OwlEye.ElementsProperties.*;
import usc.edu.OwlEye.GAChanges.GAChange;
import usc.edu.OwlEye.GAChanges.GAElementToChange;
import usc.edu.OwlEye.OwlConstants;
import usc.edu.OwlEye.OwlEye;
import usc.edu.OwlEye.UIModels.*;
import usc.edu.OwlEye.util.Utils;
import usc.edu.SALEM.util.Util;
import usc.edu.layoutgraph.LayoutGraph;
import usc.edu.layoutgraph.edge.NeighborEdge;

import java.util.*;

public class GACollisionDPAnalysis extends GADependencyAnalysis{















    public HashMap<String, HashMap<String, GAChange>> CollisionDependencyAnalysis(HashMap<String, Collision> collisionIssues) {
        HashMap<String, HashMap<String, GAChange>> groupsOfChanges = new HashMap<>();

        groupsOfChanges.put("group1", new HashMap<>());
        groupsOfChanges.put("group2", new HashMap<>());

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
        groupsOfChanges.put("group13", new HashMap<>());
        groupsOfChanges.put("group14", new HashMap<>());
        groupsOfChanges.put("group15", new HashMap<>());

        groupsOfChanges.put("group16", new HashMap<>());
        groupsOfChanges.put("group17", new HashMap<>());
        groupsOfChanges.put("group18", new HashMap<>());
        groupsOfChanges.put("group19", new HashMap<>());

        for (String key : collisionIssues.keySet()) {
            Collision collision = collisionIssues.get(key);

            HashMap<String, HashMap<String, GAElementToChange>> fixSetForCollision = GetFixSetForCollision(collision);
            Logger.debug("Fix set for collision issue " + collision.getIssueID() + " is " + fixSetForCollision);

            createGroupOfChangesCollision(collision, fixSetForCollision, groupsOfChanges);


        }

        Logger.debug("Done Analyzing all collision issues " + groupsOfChanges);
        return groupsOfChanges;


    }
    private HashMap<String, HashMap<String, GAElementToChange>> GetFixSetForCollision(Collision collision) {
        boolean checkWRG = true;
        boolean checkSRG = true;
        boolean checkVSRG = true;
        boolean checkPadding = true;
        boolean checkMargin = true;
        boolean checkLayout = true;
        HashMap<String, HashMap<String, GAElementToChange>> elementsToChange = new HashMap<>(); //string: id or xpath of the element

        String mainProperty = Height.propertyName;
        if (collision.getIssueType().equalsIgnoreCase("width")) {
            mainProperty = Width.propertyName;
        }

        elementsToChange.put("problematic", new HashMap<>());
        elementsToChange.put("wrg", new HashMap<>());
        elementsToChange.put("srg", new HashMap<>());
        elementsToChange.put("vsrg", new HashMap<>());
        elementsToChange.put("regSPLRG", new HashMap<>());
        elementsToChange.put("constSPLRG", new HashMap<>());

        elementsToChange.put("padding", new HashMap<>()); // we add all elements that sibiling and parent of the problematic element and have padding (including the problematic element itself)
        elementsToChange.put("margin", new HashMap<>());
        elementsToChange.put("scrollable", new HashMap<>());

        ArrayList<String> problematicIDs = new ArrayList<>();
        ArrayList<String> problematicXpaths = new ArrayList<>();
        /** 1- problematicElements **/
        // add all of them
        ArrayList<Node<DomNode>> problematicNodes = collision.getProblematicElementsNodes();
        for (Node<DomNode> problematicElement : problematicNodes) {
            problematicIDs.add(problematicElement.getData().getId());
            problematicXpaths.add(problematicElement.getData().getxPath());
//            double height = problematicElement.getData().height;
            double height = Double.parseDouble(Utils.getValueFromElement(problematicElement.getData(), mainProperty));
            if(height>0 && height<2){
                continue;
            }
            GAElementToChange problematicElementToChange = new GAElementToChange(problematicElement, "problematic", OwlConstants.CHANGE_DECREASE, mainProperty); // usually we know that we need to increase or change something. For now I will keep it as decrease for default
            elementsToChange.get("problematic").put(problematicElement.getData().getId(), problematicElementToChange);
            List<Node<DomNode>> children = problematicElement.getChildren();

            if(children!=null &&children.size()>0){
                int noOfChildren=children.size();
                String problematicTag=problematicElement.getData().getTagName();
                if (noOfChildren>1){
                    // we add the children to the list of problematic elements
                    for (Node<DomNode> child : children) {
                        double childHeight = Double.parseDouble(Utils.getValueFromElement(child.getData(), mainProperty));
                        if(height>0 && height<2){
                            continue;
                        }
                        problematicIDs.add(child.getData().getId());
                        GAElementToChange problematicChildElementToChange = new GAElementToChange(child, "problematic", OwlConstants.CHANGE_DECREASE, mainProperty); // usually we know that we need to increase or change something. For now I will keep it as decrease for default
                        elementsToChange.get("problematic").put(child.getData().getId(), problematicChildElementToChange);
                    }
                }
            }

        }

        if (checkSRG) {
            findImpactedElementsBasedOnSRGCollision(problematicXpaths, elementsToChange.get("srg"), mainProperty);


        }
        /** VSRG **/
        VSRG vsrg = OwlEye.getOriginalDefaultUIVSRG();
        TreeMap<String, List<VSRGNode>> dependentNodesMap = vsrg.getDependentNodesMap();
        for (String xpath : problematicXpaths) {

            //TreeMap<String, List<VSRGNode>> vsrgMainProperty = vsrg.getDependentNodesMap().get(mainProperty);
            List<VSRGNode> nodesList = dependentNodesMap.get(xpath);
            if (nodesList == null) {
                continue;
            }
            HashMap<String, GAElementToChange> vsrgElements = elementsToChange.get("vsrg");
            for (VSRGNode vsrgNode : nodesList) {
                String id = vsrgNode.getXmlNode().getData().getId();
                if (problematicIDs.contains(id)) {
                    continue;
                }

                GAElementToChange vsrgElementToChange = new GAElementToChange(vsrgNode.getXmlNode(), "vsrg", OwlConstants.CHANGE_DECREASE, mainProperty);
                //GraphNode gNode=vsrgNode.getGraphNode();
                vsrgElementToChange.setGraphNode(vsrgNode);
                vsrgElements.put(id, vsrgElementToChange);
            }



            System.out.println("problematic element id: " + xpath);
        }




        /** 2- elements with weights. **/


        // iterate through problematic elements and get their VSRG

        WRG wrg = OwlEye.getOriginalDefaultUIWRG();
        TreeMap<String, List<WRGNode>> wrgMainProperty = wrg.getDependentNodesMap().get(mainProperty);
        ArrayList<String> handledProblematicElement = new ArrayList<>();

        for (Node<DomNode> problematicElement : problematicNodes
        ) {
            if (handledProblematicElement.contains(problematicElement.getData().getId())) {
                continue;
            }
            Node<DomNode> parent = problematicElement.getParent();
            if (parent == null || wrgMainProperty.get(parent.getData().getxPath()) == null) {
                continue;

            }

            List<WRGNode> siblings = wrgMainProperty.get(parent.getData().getxPath());
            for (WRGNode sibling : siblings
            ) {
                String siblingID = sibling.getXmlNode().getData().getId();
                String changeType = OwlConstants.CHANGE_DECREASE;
                String weightProperty = sibling.getProperty();
                // it is problematic so handle it now

                if (problematicIDs.contains(siblingID)) {
                    changeType = OwlConstants.CHANGE_INCREASE;
                    handledProblematicElement.add(siblingID);
                }
                GAElementToChange gaElementToChange = new GAElementToChange(sibling.getXmlNode(), "wrg", changeType, weightProperty, sibling);
                // add the property to change
                elementsToChange.get("wrg").put(siblingID, gaElementToChange);

            }


//        WRG wrg= OwlEye.getOriginalDefaultUIWRG();
//        TreeMap<String, List<WRGNode>> wrgMainProperty = wrg.getDependentNodesMap().get(mainProperty);
//        for (Node<DomNode> problematicElement : problematicNodes
//             ) {
//            List<WRGNode> dependentNodeMap = wrgMainProperty.get(problematicElement.getData().getxPath());
//            if (dependentNodeMap.size() >= 0) {
//                boolean problematicAdded=false;
//                for (WRGNode dependentNode : dependentNodeMap
//                     ) {
//                    String currNodeXpath = dependentNode.getXmlNode().getData().getxPath();
//                    GAElementToChange gaElementToChange = new GAElementToChange(dependentNode.getXmlNode(), "wrg", OwlConstants.CHANGE_DECREASE, mainProperty);
//                    elementsToChange.get("wrg").put(dependentNode.getXmlNode().getData().getId(), gaElementToChange);
//                    // I also need to add the problematic element itself to wrg list so lets find it and add it from the one of the wrg nodes because it is both ways relationships
//                    if(!problematicAdded){
//                        List<WRGNode> tempWRG = wrgMainProperty.get(currNodeXpath);
//                        if (dependentNodeMap.size() >= 0) {
//                            for (WRGNode tempWRGNode : tempWRG
//                            ) {
//                                if (tempWRGNode.getXmlNode().getData().getId().equalsIgnoreCase(problematicElement.getData().getId())) {
//                                    GAElementToChange problematicElementToChange = new GAElementToChange(problematicElement, "wrg", OwlConstants.CHANGE_DECREASE, mainProperty); // usually we know that we need to increase or change something. For now I will keep it as decrease for default
//                                    elementsToChange.get("wrg").put(problematicElement.getData().getId(), problematicElementToChange);
//                                    problematicAdded=true;
//                                    break;
//                                }
//                            }
//                        }
//
//                    }
//                }
//                Logger.debug("There are dependent nodes for problematic element: " + problematicElement.getData().getId());

            //}
        }

        /** 3- layout changes. **/

        SPLRG splrg = OwlEye.getOriginalDefaultUISPLRG();
        // I only want the regular layout relations
        TreeMap<String, List<SPLRGNode>> regularDependentNodesMap = splrg.getDependentNodesMap().get(OwlConstants.REGULAR_LAYOUT_RELATION);
        HashMap<String, Node<DomNode>> nodesWithLayoutDirection = new HashMap<>();
        for (Node<DomNode> problematicNode : problematicNodes) {
            String id = problematicNode.getData().getId();
            List<SPLRGNode> dependentNodes = regularDependentNodesMap.get(id);
            for (SPLRGNode dependentNode : dependentNodes) {
                String dependPropType = dependentNode.getPropValue().getValType();
                if (dependPropType.equalsIgnoreCase("id")) { // direction with Other element
                    nodesWithLayoutDirection.put(problematicNode.getData().getId(), problematicNode);
                    GAElementToChange problematicElementToChange = new GAElementToChange(problematicNode, "regSPLRG", OwlConstants.CHANGE_CHANGE, mainProperty, dependentNode); // usually we know that we need to increase or change something. For now I will keep it as decrease for default

                    elementsToChange.get("regSPLRG").put(problematicNode.getData().getId(), problematicElementToChange);
                }

            }

        }



        /** 4- layout Constraints changes. **/


        // I only want the regular layout relations
        TreeMap<String, List<SPLRGNode>> constraintsDependentNodesMap = splrg.getDependentNodesMap().get(OwlConstants.CONSTRAINTS_LAYOUT_RELATION);
        HashMap<String, Node<DomNode>> nodesWithConstraintsDirection = new HashMap<>();
        for (Node<DomNode> problematicNode : problematicNodes) {
            String id = problematicNode.getData().getId();
            List<SPLRGNode> dependentNodes = constraintsDependentNodesMap.get(id);
            for (SPLRGNode dependentNode : dependentNodes) {
                String dependPropType = dependentNode.getPropValue().getValType();
                if (dependPropType.equalsIgnoreCase("id")) { // direction with Other element
                    nodesWithConstraintsDirection.put(problematicNode.getData().getId(), problematicNode);
                    GAElementToChange problematicElementToChange = new GAElementToChange(problematicNode, "constSPLRG", OwlConstants.CHANGE_CHANGE, mainProperty, dependentNode); // usually we know that we need to increase or change something. For now I will keep it as decrease for default

                    elementsToChange.get("constSPLRG").put(problematicNode.getData().getId(), problematicElementToChange);
                }

            }

        }



        /** 4- Scrollable textViews changes. **/

        for (Node<DomNode> problematicElement : problematicNodes) {
            problematicIDs.add(problematicElement.getData().getId());
            boolean isScrollable = checkIfElementIsAlongScrollableTextView(problematicElement);
            if (isScrollable) {
                // check if there is a android:lines property defined
                String linesVal = Utils.getValueFromElement(problematicElement.getData(), Lines.propertyName);
                String changeType = OwlConstants.CHANGE_CHANGE;
                Lines lines = new Lines();
                if (linesVal != null) {
                    changeType = OwlConstants.CHANGE_DECREASE;
                    lines.setCurrentVal(Integer.parseInt(linesVal));
                    lines.setCurrentStaticVal(linesVal);
                }
                GAElementToChange problematicElementToChange = new GAElementToChange(problematicElement, "scrollable",
                        changeType, Lines.propertyName); // usually we know that we need to increase or change something. For now I will keep it as decrease for default
                elementsToChange.get("scrollable").put(problematicElement.getData().getId(), problematicElementToChange);
            }

        }

        /** 5- Margins changes. **/
        if(checkMargin) {
            findImpactedElementsBasedOnMarginCollision(collision, elementsToChange.get("margin"), problematicNodes, mainProperty);
        }

        return elementsToChange;
    }



    private void createGroupOfChangesCollision(Collision collision, HashMap<String, HashMap<String, GAElementToChange>> fixSetForCollision,
                                               HashMap<String, HashMap<String, GAChange>> groupsOfChanges) {
        boolean includeSRG = true;
        boolean includeVSrg = true;
        boolean includePadding = false;
        boolean includeMargin = true;
        boolean includeRegularLayout = true;
        boolean includeConstraintLayout = true;

        boolean includeLayoutWeight = false;
        boolean includeWRG = true;
        boolean includeRegSPLRG = true;
        boolean includeConstSPLRG = true;

        boolean includeScrollableText = true;
        HashMap<String, GAElementToChange> problematicElement = fixSetForCollision.get("problematic");
        // check if only weight one and it is image we consider changing dimension
        HashMap<String, GAElementToChange> wrgs = fixSetForCollision.get("wrg");
        if (wrgs != null && wrgs.size() ==1) {
            GAElementToChange wrgElement = wrgs.get(wrgs.keySet().iterator().next());
            if ( wrgElement.getNode().getData().getTagName().equals("ImageView") ) {
                //
                GAElementToChange orginalProb = problematicElement.get(problematicElement.keySet().iterator().next());
                String prop1=orginalProb.getDirectionFocus();
                // GAElementToChange gW= new GAElementToChange()
                GAElementToChange gW = new GAElementToChange(wrgElement.getNode(), "problematic1", OwlConstants.CHANGE_DECREASE, prop1); // usually we know that we need to increase or change something. For now I will keep it as decrease for default

                problematicElement.put(wrgElement.getNode().getData().getId(), gW);
            }
        }
        createGroupOfChangesForProblematicCollision(fixSetForCollision, groupsOfChanges, fixSetForCollision.get("problematic"), collision);

        if (includeSRG) {
            HashMap<String, GAElementToChange> srgElements = fixSetForCollision.get("srg");

            if (srgElements != null && srgElements.size() > 0) {
                createGroupOfChangesForSRGElementsCollisions(srgElements, groupsOfChanges);
            }
        }
        if (includeVSrg) {
            HashMap<String, GAElementToChange> vsrgElements = fixSetForCollision.get("vsrg");

            if (vsrgElements != null && vsrgElements.size() > 0) {
                createGroupOfChangesForVSRGElementsCollisions(vsrgElements,problematicElement, groupsOfChanges);
            }
        }
        if (includeMargin) {
            HashMap<String, GAElementToChange> marginElements = fixSetForCollision.get("margin");

            if (marginElements != null && marginElements.size() > 0) {
                createGroupOfChangesForMarginElementsCollision(marginElements, groupsOfChanges);
            }
        }
        if (includeRegSPLRG) {
            HashMap<String, GAElementToChange> regSPLRGElements = fixSetForCollision.get("regSPLRG");
            if (regSPLRGElements != null && regSPLRGElements.size() > 0) {

                createGroupOfChangesForRegularLayout(fixSetForCollision, groupsOfChanges, regSPLRGElements, collision);
            }
        }

        if (includeConstSPLRG) {
            HashMap<String, GAElementToChange> constSPLRGElements = fixSetForCollision.get("constSPLRG");
            if (constSPLRGElements != null && constSPLRGElements.size() > 0) {

                createGroupOfChangesForConstraintLayout(fixSetForCollision, groupsOfChanges, constSPLRGElements, collision);
            }
        }

        if (includeWRG) {
            HashMap<String, GAElementToChange> wrgElements = fixSetForCollision.get("wrg");
            if (wrgElements != null && wrgElements.size() > 0) {

                createGroupOfChangesForWRGCollision(wrgElements, groupsOfChanges);
            }
        }
            if (includeScrollableText) {
                HashMap<String, GAElementToChange> scrollableElements = fixSetForCollision.get("scrollable");
                if (scrollableElements != null && scrollableElements.size() > 0) {
                    createGroupOfChangesForScrollableTextCollision(scrollableElements, groupsOfChanges);

                }

            }




    }


    private void findImpactedElementsBasedOnMarginCollision(AUCIIssue AUCIIssue, HashMap<String, GAElementToChange> margin,
                                                            ArrayList<Node<DomNode>> problematicNodes, String changeDirection) {
        // for margins we need to check the margins of the element itself and the margins of the parent (I am not going to checkthe margins of the sibilings for now)
        Logger.trace("Cutoff findImpactedElementsBasedOnMargins");
        ArrayList<Node<DomNode>> nodesToConsiderMargin = new ArrayList<>();
        Map<String, String> marginAttributesToFind;

        if (AUCIIssue.getIssueType().equals("height".toLowerCase())) {
            marginAttributesToFind = OwlConstants.MARGINS_FOR_HORIZONTAL_CUTOFF_ISSUES;
        } else {
            marginAttributesToFind = OwlConstants.MARGINS_FOR_VERTICAL_CUTOFF_ISSUES;
        }

        for (Node<DomNode> currProblematic : problematicNodes
        ) {
            Node<DomNode> firstParent = currProblematic.getParent();
            if (firstParent != null) {
                List<Node<DomNode>> siblings = firstParent.getChildren();
                if (siblings.size() > 0) {
                    nodesToConsiderMargin.addAll(siblings);
                }

            }
            Node<DomNode> parent = currProblematic.getParent();
            while (parent != null) {
                nodesToConsiderMargin.add(parent);
                parent = parent.getParent(); // all parent of the problematic element
            }
            boolean addSiblingsOfParent = false;
            if (addSiblingsOfParent) {
                Node<DomNode> parentOfParent = firstParent.getParent();
                if (parentOfParent != null) {
                    List<Node<DomNode>> siblingsOfParent = parentOfParent.getChildren();
                    if (siblingsOfParent.size() > 0) {
                        nodesToConsiderMargin.addAll(siblingsOfParent);
                    }
                }
            }


            nodesToConsiderMargin.add(currProblematic);
        }


        // List<Node<DomNode>> siblings = parent.getChildren();
//        if(siblings.size()>0) {
//            nodesToConsiderPadding.addAll(siblings);
//        }

        for (Node<DomNode> node : nodesToConsiderMargin
        ) {
            Map<String, String> attributes = node.getData().getAttributes();
            for (String key : attributes.keySet()
            ) {
                if (key.contains("_margin")) {
                    String att = key;
                    if (marginAttributesToFind.containsKey(att)) {
                        String value = attributes.get(key);
                        Double marginValue = Util.getNumbersFromString(value).get(0);
                        if (marginValue > 0) {
                            Logger.debug("Margin Attribute: " + att + " Value: " + value);
                            GAElementToChange elementToChange = new GAElementToChange(node, "margin", OwlConstants.CHANGE_DECREASE, changeDirection);
                            margin.put(node.getData().getId(), elementToChange);
                        }
                    }
//                    String value = attributes.get(key);
//                    Double paddingValue = Util.getNumbersFromString(value).get(0);
//                    if (paddingValue > 0) {
//                        Logger.debug("Margin Attribute: " + att + " Value: " + value);
//                        if (att.contains("_margin")) {
//                            GAElementToChange elementToChange = new GAElementToChange(node, "margin", OwlConstants.Decrease, changeDirection);
//                            margin.put(node.getData().getId(), elementToChange);
//                        }
//                    }
                }
            }
        }
    }


    private void createGroupOfChangesForProblematicCollision(HashMap<String, HashMap<String, GAElementToChange>> fixSetForCollision,
                                                             HashMap<String, HashMap<String, GAChange>> groupsOfChanges,
                                                             HashMap<String, GAElementToChange> problematicElements, AUCIIssue issue) {
        String mainProperty = Height.propertyName;
        if (issue.getIssueType().equalsIgnoreCase("width")) {
            mainProperty = Width.propertyName;
        }


        // 1- main height and width
        double changeAmountPercentage = 20;  // percentage of the change
        double currentChangeAmountPercent =changeAmountPercentage; // - or + depending on the direction of the change

        HashMap<String, GAChange> group1 = new HashMap<>(); // decrease
        HashMap<String, GAChange> group2 = new HashMap<>(); //decrease
        HashMap<String, GAChange> group3 = new HashMap<>(); // increase
        HashMap<String, GAChange> group4 = new HashMap<>();// increase
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
            // for the main change 20% decrease
            GAChange[] fourChanges = createFourDimensionChanges(element, sizeProp, changeAmountPercentage);
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
            double newChangeAmount =5.0;
            GAChange[] fourChanges2 = createFourDimensionChanges(element, sizeProp, newChangeAmount);
            if (fourChanges2[0] != null) {
                addElementToGroupOfChange(group3, element.getId(), fourChanges2[0]);
            }
            if (fourChanges2[1] != null) {
                addElementToGroupOfChange(group4, element.getId(), fourChanges2[1]);
            }
            if (fourChanges2[2] != null) {
                //    addElementToGroupOfChange(group3, element.getId(), fourChanges[2]);
            }
            if (fourChanges2[3] != null) {
                // addElementToGroupOfChange(group4, element.getId(), fourChanges[3]);
            }
            if (fourChanges2[4] != null) {
                addElementToGroupOfChange(group4, element.getId(), fourChanges[4]); //this is the min
            }

//            String changeType = OwlConstants.CHANGE_DECREASE;
//            if(changeType.equalsIgnoreCase(OwlConstants.CHANGE_DECREASE)){
//                 currentChangeAmountPercent = -changeAmountPercentage;
//            }
//
//            // gene group 1 for main size --> do not change how it is set (if it is fixed directly increase it or if it is constraints add Min)
//
//            GAChange change1 = createDoNotChangeSizeTypeGroupCollision(element, sizeProp, changeType,currentChangeAmountPercent);
//            if (change1 != null) {
//                addElementToGroupOfChange(group1, element.getId(), change1);
//                //group1.put(element.getId(), change);1
//            }
//            GAChange change2 = createChangeSizeTypeGroupCollision(element, sizeProp, changeType,currentChangeAmountPercent);
//            // GAChange change2=change;   // Activate this if  we are not considering the case where the size is set to wrap content for so I am adding group 1 to group 2
//            if (change2 != null) {
//                addElementToGroupOfChange(group2, element.getId(), change2);
//
//               // group2.put(element.getId(), change2);
//            }
//             changeType = OwlConstants.CHANGE_INCREASE;
//            if(changeType.equalsIgnoreCase(OwlConstants.CHANGE_DECREASE)) {
//                currentChangeAmountPercent = Math.abs(changeAmountPercentage);
//            }
//            GAChange change3 = createDoNotChangeSizeTypeGroupCollision(element, sizeProp, changeType,currentChangeAmountPercent);
//            if (change1 != null) {
//                addElementToGroupOfChange(group3, element.getId(), change3);
//                //group1.put(element.getId(), change);1
//            }
//            GAChange change4 = createChangeSizeTypeGroupCollision(element, sizeProp, changeType,currentChangeAmountPercent);
//            // GAChange change2=change;   // Activate this if  we are not considering the case where the size is set to wrap content for so I am adding group 1 to group 2
//            if (change4 != null) {
//                addElementToGroupOfChange(group4, element.getId(), change4);
//
//                // group2.put(element.getId(), change2);
//            }



        }



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

        groupsOfChanges.get("group1").putAll(group1);
        groupsOfChanges.get("group2").putAll(group2);
        groupsOfChanges.get("group3").putAll(group3);
        groupsOfChanges.get("group4").putAll(group4);
    }


    private void createGroupOfChangesForSRGElementsCollisions(HashMap<String, GAElementToChange> srgElements,
                                                              HashMap<String, HashMap<String, GAChange>> groups) {

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
        groups.get("group1").putAll(group1);
       // groups.get("group2").putAll(group2);


    }


    private void findImpactedElementsBasedOnSRGCollision(ArrayList<String> problematicNodesXPaths,
                                                         HashMap<String, GAElementToChange> elementsToChange, String prop) {

//        String prop = Height.propertyName;
//        if (cutoff.getIssueType().equalsIgnoreCase("vertical")) {
//            prop = Width.propertyName;
//            // this has to be done later
//        }
        SRG srg = OwlEye.getOriginalDefaultUISRG();
        for (String problematicElementXPath : problematicNodesXPaths
             ) {
            String xPath = problematicElementXPath;
            List<SRGNode> sizeDependentElements = srg.getDependentNodesMap2().get(prop).get(xPath); // for the property
            //System.out.println(srg);
            if (sizeDependentElements != null && sizeDependentElements.size() > 0) {
                for (SRGNode sizeDependentElement : sizeDependentElements
                ) {
                    double ratio = sizeDependentElement.getRatio();

                    Node<DomNode> node = sizeDependentElement.getXmlNode();
                    GAElementToChange e = new GAElementToChange(node, "SRG", OwlConstants.CHANGE_INCREASE, prop);
                    if (prop.equalsIgnoreCase(Height.propertyName)) {
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

    private void createGroupOfChangesForVSRGElementsCollisions(HashMap<String, GAElementToChange> srgElements,
                                                               HashMap<String, GAElementToChange> problematicElements,
                                                               HashMap<String, HashMap<String, GAChange>> groups) {
        HashMap<String, GAChange> group1 = new HashMap<>();
        HashMap<String, GAChange> group2 = new HashMap<>();
        HashMap<String, GAChange> group3 = new HashMap<>();
        HashMap<String, GAChange> group4 = new HashMap<>();
        VSRG vsrg = OwlEye.getOriginalDefaultUIVSRG();
        TreeMap<String, List<VSRGNode>> dependentNodesMap = vsrg.getDependentNodesMap();


        for (String problematicElementId : problematicElements.keySet()) {
            GAElementToChange problematicElementGA = problematicElements.get(problematicElementId);
            String directionFocus = problematicElementGA.getDirectionFocus();
            Property sizeProp = null;

            String xpath=problematicElementGA.getNode().getData().getxPath();
            List<VSRGNode> nodesList = dependentNodesMap.get(xpath);
            if (nodesList == null) {
                continue;
            }

            HashMap<String, Property> propertiesToChangeObject = new HashMap<>();

            if (directionFocus.equalsIgnoreCase(Height.propertyName)) {
                sizeProp = new Height();


                String dynamicVal = Utils.getDynamicValueInDDP(problematicElementGA.getNode().getData(), Height.propertyName);
                sizeProp.setCurrentDynamicVal(dynamicVal);
                String staticVal = Utils.getValueFromElement(problematicElementGA.getNode().getData(), Height.propertyName);
                sizeProp.setCurrentStaticVal(staticVal);
                propertiesToChangeObject.put(Height.propertyName, sizeProp);
            } else if (directionFocus.equalsIgnoreCase(Width.propertyName)) {
                sizeProp = new Width();
                String dynamicVal = Utils.getDynamicValueInDDP(problematicElementGA.getNode().getData(), Width.propertyName);
                sizeProp.setCurrentDynamicVal(dynamicVal);
                String staticVal = Utils.getValueFromElement(problematicElementGA.getNode().getData(), Width.propertyName);
                sizeProp.setCurrentStaticVal(staticVal);
                propertiesToChangeObject.put(Width.propertyName, sizeProp);
            }



            double changeAmountPercentage=20;
            for (VSRGNode vsrgNode : nodesList) {
                String id = vsrgNode.getXmlNode().getData().getId();
                GAElementToChange element = srgElements.get(id);
                if (element == null) {
                    continue;
                }
                GAChange[] fourChanges = createFourDimensionChanges(element, sizeProp, changeAmountPercentage);
                if (fourChanges[0] != null) {
                    addElementToGroupOfChange(group1, element.getId(), fourChanges[0]);
                }
                if (fourChanges[1] != null) {
                    addElementToGroupOfChange(group2, element.getId(), fourChanges[1]);
                }
                if (fourChanges[2] != null) {
                    addElementToGroupOfChange(group3, element.getId(), fourChanges[2]);
                }
                if (fourChanges[3] != null) {
                    addElementToGroupOfChange(group4, element.getId(), fourChanges[3]);
                }
//                }
            }

        }


        groups.get("group1").putAll(group1);
        groups.get("group2").putAll(group2);
        groups.get("group3").putAll(group3);
        groups.get("group4").putAll(group4);


    }

    private void createGroupOfChangesForMarginElementsCollision(HashMap<String, GAElementToChange> marginElements,
                                                                HashMap<String, HashMap<String, GAChange>> groupsOfChanges) {

        // we start from group 3  see the main method for more details of the groups
        HashMap<String, GAChange> groupM1 = new HashMap<>(); // pure margin 50%
        HashMap<String, GAChange> groupM2 = new HashMap<>(); // pure margin 0%
        HashMap<String, GAChange> groupM3 = new HashMap<>(); // pure margin 30%
        HashMap<String, GAChange> groupM4 = new HashMap<>(); // pure margin 50% with height group1
        HashMap<String, GAChange> groupM5 = new HashMap<>(); // pure margin 50% with height group2
        HashMap<String, GAChange> groupM6 = new HashMap<>(); // pure margin 50% with height group3
        HashMap<String, GAChange> groupM7 = new HashMap<>(); // pure margin 50% with height group4
        HashMap<String, GAChange> groupM8 = new HashMap<>(); // pure margin 30% with height group1
        HashMap<String, GAChange> groupM9 = new HashMap<>(); // pure margin 30% with height group2
        HashMap<String, GAChange> groupM10 = new HashMap<>(); // pure margin 30% with height group3
        HashMap<String, GAChange> groupM11 = new HashMap<>(); // pure margin 30% with height group4


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

                        // group 1: decrease padding by 50%
                        double calculateNewVal = Math.floor(currentNumericalValue * .50);
                        if (calculateNewVal < 0) {
                            calculateNewVal = 0;
                        }
                        String changeVal = calculateNewVal + "";
                        String changeType = OwlConstants.CHANGE_DECREASE;
                        GAChange change = new GAChange("margin", attName, changeType, changeVal, elementToChange, margin);
                        elementToChange.addChange(attName, change);
                        if (change != null) {
                            addElementToGroupOfChange( groupM1, elementToChange.getId() , change);

                            // group3.put(elementToChange.getId() + "#" + attName, change);
                        }

                        // group 2: decreae padding by 100%
                        calculateNewVal = Math.floor(currentNumericalValue * .90);

                        changeVal = calculateNewVal + "";
                        changeType = OwlConstants.CHANGE_DECREASE;
                        change = new GAChange("margin", attName, changeType, changeVal, elementToChange, margin);
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
                        change = new GAChange("margin", attName, changeType, changeVal, elementToChange, margin);
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
        HashMap<String, GAChange> group3 = groupsOfChanges.get("group3"); // height increase
        HashMap<String, GAChange> group4 = groupsOfChanges.get("group4"); // height increase

//        groupsOfChanges.get("group3").putAll(groupM1);  // pure margins 50%
//        groupsOfChanges.get("group4").putAll(groupM2);  // pure margins 0%
//        groupsOfChanges.get("group5").putAll(groupM3);// pure margins 30%
//        groupsOfChanges.get("group5").putAll(groupM1);  // pure margins 50%
//        groupsOfChanges.get("group6").putAll(groupM2);  // pure margins 0%
//        groupsOfChanges.get("group7").putAll(groupM3);// pure margins 30%

        /** now we handle the cases where there is a height or width **/
        // we only add those if there is margin and padding because otherwise it is bascially the same as group 1 and 2
        if (groupM1.size() > 0) { // m1 is 50% so we add that to all size groups
            groupM4.putAll(groupM1);
            groupM5.putAll(groupM1);
            groupM6.putAll(groupM1);
            groupM7.putAll(groupM1);

            // adding dimensions
            groupM4.putAll(group1);
            groupM5.putAll(group2);
            groupM6.putAll(group3);
            groupM7.putAll(group4);
        }

        if (groupM3.size() > 0) { // m3 is 30% so we add that to all size groups
            groupM8.putAll(groupM3);
            groupM9.putAll(groupM3);
            groupM10.putAll(groupM3);
            groupM11.putAll(groupM3);
            // adding dimensions
            groupM9.putAll(group1);
            groupM9.putAll(group2);
            groupM10.putAll(group3);
            groupM11.putAll(group4);
        }
        if (group4.size() > 0) {
            group4.putAll(group1);
            group4.putAll(group2);
        }


        groupsOfChanges.get("group5").putAll(groupM1);
        groupsOfChanges.get("group6").putAll(groupM2);
        groupsOfChanges.get("group7").putAll(groupM3);
        groupsOfChanges.get("group8").putAll(groupM4);
        groupsOfChanges.get("group9").putAll(groupM5);
        groupsOfChanges.get("group10").putAll(groupM6);
        groupsOfChanges.get("group11").putAll(groupM7);
        groupsOfChanges.get("group12").putAll(groupM8);
        groupsOfChanges.get("group13").putAll(groupM9);
        groupsOfChanges.get("group14").putAll(groupM10);
        groupsOfChanges.get("group15").putAll(groupM11);



    }


    private void createGroupOfChangesForRegularLayout(HashMap<String, HashMap<String, GAElementToChange>> fixSetForCollision,
                                                      HashMap<String, HashMap<String, GAChange>> groupsOfChanges, HashMap<String, GAElementToChange>
                                                              regSPLRG, Collision collision) {
        Logger.debug("Start of the Regular Layout changes method");
        HashMap<String, GAChange> group1 = new HashMap<>();
        Node<DomNode> root = OwlEye.getOriginalDefaultUI().getXMLTree().getRoot();
        LayoutGraph layoutGraph = new LayoutGraph(root);
        String prop = Height.propertyName;
        if (collision.getIssueType().equalsIgnoreCase("width")) {
            prop = Width.propertyName;
        }
        Node<DomNode> node0;
        Node<DomNode> node1;
        node0 = collision.getProblematicElementsNodes().get(0);
        node1 = collision.getProblematicElementsNodes().get(1);
        String node0ID = node0.getData().getId();
        String node1ID = node1.getData().getId();
        String node0IDWithPrefix = "@id/" + node0ID.replace("id/", "");
        String node1IDWithPrefix = "@id/" + node1ID.replace("id/", "");
        NeighborEdge edge = layoutGraph.findEdge(node0, node1);
        String mainProperty = "";
        if (prop.equalsIgnoreCase(Height.propertyName)) {
            mainProperty= Height.propertyName;
            // we look for top to bottom
            GAElementToChange x = regSPLRG.get(node0ID);
            GAElementToChange y=regSPLRG.get(node1ID);
            SPLRGNode node0SPLRG=null;
            SPLRGNode node1SPLRG=null;
            if(x!=null) {
                node0SPLRG = (SPLRGNode) regSPLRG.get(node0ID).getGraphNode();
            }
            if(y!=null) {
                node1SPLRG = (SPLRGNode) regSPLRG.get(node1ID).getGraphNode();
            }
//            SPLRGNode node1SPLRG = (SPLRGNode) regSPLRG.get(node1ID).getGraphNode();
//            String node0CurrentProp = node0SPLRG.getProperty();
//            String node1CurrentProp = node1SPLRG.getProperty();
            edge = layoutGraph.findEdge(node0, node1);
//            GAElementToChange n0 = regSPLRG.get(node0ID);
//            GAElementToChange n1 =  regSPLRG.get(node1ID);
//            if(n0==null || n1==null){
//                return;
//            }
//            if (n1 == null) {
//
//                GAElementToChange gaElementToChange = new GAElementToChange(node1, "regSPLRG", OwlConstants.CHANGE_CHANGE, mainProperty,node1SPLRG ); // usually we know that we need to increase or change something. For now I will keep it as decrease for default
//                regSPLRG.put(node1ID, gaElementToChange);
//
//            }
            GAChange change1 = null;
            String propLayout=  "android:layout_above" ;
            String nodeID;
            String propetyName;
            SPLRGNode splgNodeToUse =null;
            Node<DomNode>nodeToUse=null;
            if (node0SPLRG != null) {
                propetyName=  "layout_above" ;
                propLayout=  "android:layout_above" ;
                nodeID=node1IDWithPrefix;
                splgNodeToUse=node0SPLRG;
                nodeToUse=node0;
            }

            else if (node1SPLRG == null) {
                propetyName=  "layout_below" ;
                propLayout=  "android:layout_below" ;
                nodeID=node0IDWithPrefix;
                splgNodeToUse=node1SPLRG;
                nodeToUse=node1;
            }
            else{
                return;
            }
            if (edge.isTopBottom()) {
                GAElementToChange gaElementToChange = new GAElementToChange(nodeToUse, "regSPLRG", OwlConstants.CHANGE_CHANGE, mainProperty,splgNodeToUse ); // usually we know that we need to increase or change something. For now I will keep it as decrease for default

                change1= new GAChange(propetyName, propLayout, OwlConstants.CHANGE_CHANGE, nodeID, gaElementToChange);
            }
            if(change1!=null) {
                addElementToGroupOfChange(group1, nodeID, change1);
//                group1.put(nodeID, change1);
//                groupsOfChanges.put("group1", group1);
//                groupsOfChanges.get("group1").put(node0ID, change1);
            }
//            Logger.debug(change1.getAttName());
//            if (edge.isTopBottom()) {
//                //  GAChange change0 = new GAChange("layout_above", "android:layout_above", OwlConstants.CHANGE_CHANGE, node1IDWithPrefix,regSPLRG.get(node0ID));
//                GAChange change1 = new GAChange("layout_below", "android:layout_below", OwlConstants.CHANGE_CHANGE, node0IDWithPrefix, regSPLRG.get(node1ID));
//                //  groupsOfChanges.get("group1").put(node0ID,change0);
//                addElementToGroupOfChange( groupsOfChanges.get("group1"), node1ID, change1);
////                GAChange change2 = new GAChange("layout_above", "android:layout_above", OwlConstants.CHANGE_CHANGE, node1IDWithPrefix, regSPLRG.get(node0ID));
////                addElementToGroupOfChange( groupsOfChanges.get("group2"), node0ID, change2);
//
//
//                addElementToGroupOfChange( groupsOfChanges.get("group2"), node1ID, change1);
//
////                groupsOfChanges.get("group1").put(node1ID, change1);
////                groupsOfChanges.get("group2").put(node1ID, change1);// just adding it Feb 5th
//            }
        }

        if(groupsOfChanges.get("group1").size()>0) {
            groupsOfChanges.get("group1").putAll(group1);
        }
        if(groupsOfChanges.get("group2").size()>0) {
            groupsOfChanges.get("group2").putAll(group1);
        }
        if(groupsOfChanges.get("group3").size()>0) {
            groupsOfChanges.get("group3").putAll(group1);
        }
        if(groupsOfChanges.get("group4").size()>0) {
            groupsOfChanges.get("group4").putAll(group1);
        }
        if(groupsOfChanges.get("group5").size()>0) {
            groupsOfChanges.get("group5").putAll(group1);
        }
        if(groupsOfChanges.get("group6").size()>0) {
            groupsOfChanges.get("group6").putAll(group1);
        }
        if(groupsOfChanges.get("group7").size()>0) {
            groupsOfChanges.get("group7").putAll(group1);
        }
        if(groupsOfChanges.get("group8").size()>0) {
            groupsOfChanges.get("group8").putAll(group1);
        }
        if(groupsOfChanges.get("group9").size()>0) {
            groupsOfChanges.get("group9").putAll(group1);
        }
        if(groupsOfChanges.get("group10").size()>0) {
            groupsOfChanges.get("group10").putAll(group1);
        }
        if(groupsOfChanges.get("group11").size()>0) {
            groupsOfChanges.get("group11").putAll(group1);
        }
        if(groupsOfChanges.get("group12").size()>0) {
            groupsOfChanges.get("group12").putAll(group1);
        }
        groupsOfChanges.get("group16").putAll(group1);  /** ONLY LAYOUT CHANGE FOR THIS GROUP **/




    }


    private void createGroupOfChangesForWRGCollision(HashMap<String, GAElementToChange> wrgElements, HashMap<String,
            HashMap<String, GAChange>> groupsOfChanges) {

        // to handle weight for cutoff
        HashMap<String, HashMap<String, ArrayList<GAElementToChange>>> parentChildrenGroups = findGroupsOfSiblingsForWRGFixSet(wrgElements);
        HashMap<String, GAChange> group1 = new HashMap<>();
        // iterate through parentChildrenGroups as keyset
        for (Map.Entry<String, HashMap<String, ArrayList<GAElementToChange>>> entry : parentChildrenGroups.entrySet()
        ) {
            double parentTotal = Double.parseDouble(entry.getKey().split("#")[1]);
            double parentRemainingWeight = parentTotal;
            HashMap<String, ArrayList<GAElementToChange>> children = entry.getValue();
            // start with decrease first

            double generalDecreasePercentage = 0.5;
            double imageViewDecreasePercentage = 0.65;
            ArrayList<GAElementToChange> decreaseChildren = children.get(OwlConstants.CHANGE_DECREASE);
            if(decreaseChildren!=null && decreaseChildren.size()==1){
                // only one child so just do it
                GAElementToChange element = decreaseChildren.get(0);

            }
            for (GAElementToChange element : decreaseChildren) {
                WRGNode wrgNode = (WRGNode) (element.getGraphNode());
                // get the current weight of the node
                double currentWeight = wrgNode.getNumerator();
                String property = wrgNode.getProperty();
                Property propertyObject = wrgNode.getPropertyObj();
                double decreasePercentage = generalDecreasePercentage;
                String tagName = element.getNode().getData().getTagName();
                if (element.getNode().getData().getTagName().equalsIgnoreCase("imageview")) {
                    decreasePercentage = imageViewDecreasePercentage;
                }
                double newWeight = Math.ceil(currentWeight - (currentWeight * decreasePercentage));
                if (newWeight < 0) { // so we do not overflow
                    newWeight = 0;
                }
                if (newWeight > parentRemainingWeight) {
                    newWeight = parentRemainingWeight;
                }
                GAChange change = new GAChange(property, OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(property), OwlConstants.CHANGE_DECREASE,
                        newWeight + "", element, propertyObject);
                group1.put(element.getId(), change);
                parentRemainingWeight = parentRemainingWeight - newWeight;
                Logger.debug(element.getId() + " old weight is " + currentWeight + ", new weight after" + OwlConstants.CHANGE_DECREASE + " is " + newWeight);

            }


            // now increase
            ArrayList<GAElementToChange> increaseChildren = children.get(OwlConstants.CHANGE_INCREASE);
            double sizeOfIncrease = increaseChildren.size();
            double decreaseWeightSoFar = parentTotal - parentRemainingWeight;
            double weightForEachIncrease = Math.floor((parentTotal - decreaseWeightSoFar) / sizeOfIncrease);

            for (GAElementToChange element : increaseChildren) {
                WRGNode wrgNode = (WRGNode) (element.getGraphNode());
                // get the current weight of the node
                double currentWeight = wrgNode.getNumerator();
                String property = wrgNode.getProperty();
                Property propertyObject = wrgNode.getPropertyObj();
                double newWeight = Math.ceil(currentWeight + (currentWeight * 0.5));
                if (newWeight < 0) { // so we do not overflow
                    newWeight = 0;
                }
                if (newWeight < weightForEachIncrease) {
                    newWeight = weightForEachIncrease;
                }
                if (newWeight > parentRemainingWeight) {
                    newWeight = parentRemainingWeight;
                }

                parentRemainingWeight = parentRemainingWeight - newWeight;
                GAChange change = new GAChange(property, OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(property), OwlConstants.CHANGE_DECREASE,
                        newWeight + "", element, propertyObject);
                group1.put(element.getId(), change);
                Logger.debug(element.getId() + " old weight is " + currentWeight + ", new weight after" + OwlConstants.CHANGE_INCREASE + " is " + newWeight);

            }


//            double parentRemainingWeight=parentTotal;
//            for (GAElementToChange element : children) {
//                WRGNode wrgNode = (WRGNode)(element.getGraphNode());
//                // get the current weight of the node
//                double currentWeight = wrgNode.getNumerator();
//                String typeOfChange = element.getTypeOfChange();
//                switch (typeOfChange){
//                    case OwlConstants.CHANGE_INCREASE:{
//                        // increase the weight by %50
//
//                        double newWeight = Math.ceil(currentWeight + (currentWeight * 0.5));
//                        if (newWeight > parentRemainingWeight) { // so we do not overflow
//                            newWeight = parentRemainingWeight;
//                        }
//                        parentRemainingWeight=parentRemainingWeight-newWeight;
//                        Logger.debug(element.getId() + " old weight is " + currentWeight + ", new weight after" +OwlConstants.CHANGE_INCREASE+" is " + newWeight);
//
//                    break;
//                    }
//                    case OwlConstants.CHANGE_DECREASE:{
//                        // decrease the weight by %50
//                        double newWeight = Math.ceil(currentWeight - (currentWeight * 0.5));
//                        if (newWeight < 0) { // so we do not overflow
//                            newWeight = 0;
//                        }
//                        if(newWeight>parentRemainingWeight){
//                            newWeight=parentRemainingWeight;
//                        }
//                        parentRemainingWeight=parentRemainingWeight-newWeight;
//                        Logger.debug(element.getId() + " old weight is " + currentWeight + ", new weight after" +OwlConstants.CHANGE_DECREASE+" is " + newWeight);
//
//                    break;
//                    }
//                }
//
//            }
//
        }
        groupsOfChanges.get("group1").putAll(group1);
        //add to group2?
        groupsOfChanges.get("group2").putAll(group1);

        Logger.debug("Done ");

    }


    private void createGroupOfChangesForScrollableTextCollision(HashMap<String, GAElementToChange> scrollableElements, HashMap<String, HashMap<String, GAChange>>
            groupsOfChanges) {
        HashMap<String, GAChange> group1 = new HashMap<>();
        HashMap<String, GAChange> group2 = new HashMap<>();
        HashMap<String, GAChange> group3 = new HashMap<>();
        HashMap<String, GAChange> group4 = new HashMap<>();
        // HashMap<String, GAChange> group1 = groupsOfChanges.get("group1");
        for (Map.Entry<String, GAElementToChange> entry : scrollableElements.entrySet()
        ) {
            String key = entry.getKey();
            GAElementToChange gaElementToChange = entry.getValue();
            String changeType=gaElementToChange.getTypeOfChange();
            Lines lines = new Lines();
            String text = gaElementToChange.getNode().getData().getText();
            // count how many "\n in a text
            int countTotalLines = text.length() - text.replace("\n", "").length();
            lines.setNoOfLines(countTotalLines);
            double newLinesVal;
            if(changeType.equalsIgnoreCase(OwlConstants.CHANGE_CHANGE)){
                // it is not defined so we need to add it
                newLinesVal=Math.floor(countTotalLines/4);}
            else {
                newLinesVal = Math.floor(countTotalLines / 4);
                String currVal = Utils.getValueFromElement(gaElementToChange.getNode().getData(), Lines.propertyName);
                if(currVal!=null){
                    double currValNo=Util.getNumbersFromString(currVal).get(0);
                    lines.currentStaticVal=currValNo+"";
                    lines.setCurrentVal(currValNo);
                    newLinesVal=Math.floor(currValNo/2);
                }
            }
            int changeVal= (int) newLinesVal;
            GAChange change = new GAChange(Lines.propertyName, OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(Lines.propertyName),
                    changeType, changeVal+"", gaElementToChange, lines);



            addElementToGroupOfChange(group1, gaElementToChange.getId(), change);
            addElementToGroupOfChange(group2, gaElementToChange.getId(), change);
            addElementToGroupOfChange(group3, gaElementToChange.getId(), change);
            addElementToGroupOfChange(group4, gaElementToChange.getId(), change);


            groupsOfChanges.get("group1").putAll(group1);
            groupsOfChanges.get("group2").putAll(group2);
            groupsOfChanges.get("group3").putAll(group3);
            groupsOfChanges.get("group4").putAll(group4);
            groupsOfChanges.get("group15").putAll(group4); //special for scrollable text


//                group1.put(gaElementToChange.getId(), change);
        }


    }


}
