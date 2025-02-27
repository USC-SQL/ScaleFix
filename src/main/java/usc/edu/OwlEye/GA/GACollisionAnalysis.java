//package usc.edu.OwlEye.GA;
//
//import gatech.xpert.dom.DomNode;
//import gatech.xpert.dom.Node;
//import org.tinylog.Logger;
//import usc.edu.OwlEye.AUCII.Collision;
//import usc.edu.OwlEye.AUCII.Cutoff;
//import usc.edu.OwlEye.ElementsProperties.*;
//import usc.edu.OwlEye.GAChanges.GAChange;
//import usc.edu.OwlEye.GAChanges.GAElementToChange;
//import usc.edu.OwlEye.OwlConstants;
//import usc.edu.OwlEye.OwlEye;
//import usc.edu.OwlEye.UIModels.*;
//import usc.edu.OwlEye.util.Utils;
//import usc.edu.SALEM.util.Util;
//import usc.edu.layoutgraph.LayoutGraph;
//import usc.edu.layoutgraph.edge.NeighborEdge;
//
//import java.util.*;
//
//public class GACollisionAnalysis {
//
//    private static GAChange addMinChange2(GAElementToChange elementToChange, String propName, String dynamicVal) {
//        String changeVal;
//        String changeType;
//        GAChange minChange = null;
//        Property minProp = new MinHeight();
//        String minProperty = MinHeight.propertyName;
//        if (propName.equalsIgnoreCase(Width.propertyName)) {
//            minProperty = MinWidth.propertyName;
//            minProp = new MinWidth();
//        }
//        propName = minProperty;
//
//        String miPropVal = Utils.getValueFromElement(elementToChange.getNode().getData(), minProperty);
//        boolean minPropSet = false;
//        if (miPropVal != null) {
//            List<Double> y = Util.getNumbersFromString(miPropVal);
//            if (y.size() > 0) { // val exists
//                if (y.get(0) > 0) {
//                    minPropSet = true;
//                    Double numericalMinProp = y.get(0);
//                    double n = numericalMinProp + 20;
//                    changeVal = n + "";
//
//                    changeType = OwlConstants.CHANGE_INCREASE;
//
//                    minChange = new GAChange(propName, OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(propName), changeType, changeVal, elementToChange, minProp);
//                    // elementToChange.addChange(propName, change);
//                }
//            }
//        }
//
//        if (!minPropSet) {
//            //it does not exist so we add it = dynamic height + 10
//            double n = Double.parseDouble(dynamicVal) + 20;
//            changeVal = n + "";
//
//            changeType = OwlConstants.CHANGE_INCREASE;
//            minChange = new GAChange(propName, OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(propName), changeType, changeVal, elementToChange, null);
//        }
//        return minChange;
//
//    }
//
//    private static GAChange generateMaxLinesChange(String propName, double currentMaxLine, double newMaxLine, GAElementToChange element, Property property) {
//
//        String changeVal = newMaxLine + "";
//        String changeType = OwlConstants.CHANGE_INCREASE;
//        //changeType, changeVal,element,sizeProp);
//        GAChange change3 = new GAChange(propName, OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(propName), changeType, changeVal, element, property);
//        return change3;
//    }
//
//
//    /************************************************************ COLLISION ************************************************************************************************/
//
//    public HashMap<String, HashMap<String, GAChange>> CutOffDependencyAnalysis(HashMap<String, Cutoff> cutOffIssues) {
//
//
//        HashMap<String, HashMap<String, HashMap<String, GAElementToChange>>> issuesToElements = new HashMap
//                <String, HashMap<String, HashMap<String, GAElementToChange>>>(); // the fix set for each cutoff issue
//        boolean december30Approach = true;
//        HashMap<String, HashMap<String, HashMap<String, GAElementToChange>>> FixSetForCutOffIssues = new HashMap();
//
//        for (String issue : cutOffIssues.keySet()) {
//            Cutoff cutoff = cutOffIssues.get(issue);
//
//            // (1) get the fix set based on problematic elements and the UI models
//            //  HashMap<String, GAElementToChange> elementsToChange = GetFixSet(cutoff);
//            // HashMap<String, GAElementToChange> elementsToChange = GetFixSet(cutoff);
//            HashMap<String, HashMap<String, GAElementToChange>> setOfElementsToChange = GetFixSetForCutoff(cutoff);
//            FixSetForCutOffIssues.put(issue, setOfElementsToChange);
//
//        }
//
////
//        HashMap<String, HashMap<String, GAElementToChange>> combinedCutOffFixedSet = combineFixSets(FixSetForCutOffIssues);
//        Logger.debug("combinedCutoffFixedSet size: " + combinedCutOffFixedSet.size());
//        HashMap<String, HashMap<String, GAChange>> groupsOfChanges = new HashMap<>();
//        groupsOfChanges.put("group1", new HashMap<>()); // size 1 only + srg + vsrg
//        groupsOfChanges.put("group2", new HashMap<>()); // size 2 only + srg + vsrg
//
//        groupsOfChanges.put("group3", new HashMap<>()); // same as group 1 but with we include padding and margin
//        groupsOfChanges.put("group4", new HashMap<>()); // same as group 2 but with we include padding and margin
//        groupsOfChanges.put("group5", new HashMap<>()); // padding and margins only
////                        groupsOfChanges.put("group3", new HashMap<>()); // only for padding and margins
//
//        createGroupOfChangesCutoff(combinedCutOffFixedSet, FixSetForCutOffIssues, groupsOfChanges);
//
//        return groupsOfChanges;
//
//
//    }
//
//    public HashMap<String, HashMap<String, GAChange>> CollisionDependencyAnalysis(HashMap<String, Collision> collisionIssues) {
//        HashMap<String, HashMap<String, GAChange>> groupsOfChanges = new HashMap<>();
//
//        groupsOfChanges.put("group1", new HashMap<>());
//        for (String key : collisionIssues.keySet()) {
//            Collision collision = collisionIssues.get(key);
//
//            HashMap<String, HashMap<String, GAElementToChange>> fixSetForCollision = GetFixSetForCollision(collision);
//            Logger.debug("Fix set for collision issue " + collision.getIssueID() + " is " + fixSetForCollision);
//
//            createGroupOfChangesCollision(collision, fixSetForCollision, groupsOfChanges);
//
//
//        }
//
//        Logger.debug("Done Analyzing all collision issues " + groupsOfChanges);
//        return groupsOfChanges;
//
////
////        HashMap<String, ArrayList<Node<DomNode>>> elementsToFocusOn = new HashMap<>(); // type, node
////        elementsToFocusOn.put("scrollableTextView", new ArrayList<>());
////        elementsToFocusOn.put("layout_direction", new ArrayList<>());
////        elementsToFocusOn.put("layout_weight", new ArrayList<>());
////        elementsToFocusOn.put("normal_size", new ArrayList<>());
////
////        // go through the issues and analyze the problematic elements First
////        for (String key : collisionIssues.keySet()
////        ) {
////            Collision collision = collisionIssues.get(key);
////            String prop = Height.propertyName;
////            if (collision.getIssueType().equalsIgnoreCase("horizontal")) {
////                prop = Width.propertyName;
////            }
////
////            // get the problematic elements and analyze them
////            ArrayList<Node<DomNode>> problematicNodes = collision.getProblematicElementsNodes();
////
////            // scrollable textview
////            Node<DomNode> foundNode = checkIfThereIsAlongScrollableTextView(problematicNodes);
////            if (foundNode != null) {
//////                GAElementToChange gaElementToChange = new GAElementToChange();
////                elementsToFocusOn.get("scrollableTextView").add(foundNode);
////            }
//
//
//        // layout Weigth
//
////            HashMap<String, Node<DomNode>> nodesWithWeight = checkIfThereAreWeightsDefinedForCollision(problematicNodes, prop);
////            if(nodesWithWeight.size()>0){
////                layout_weight.putAll(nodesWithWeight);
////            }
//        // layout direction
//
//
//        // }
//    }
//
//    /*********************************************************************************************************************************************************************/
//
//    private void createGroupOfChangesCollision(Collision collision, HashMap<String, HashMap<String, GAElementToChange>> fixSetForCollision, HashMap<String, HashMap<String, GAChange>> groupsOfChanges) {
//        boolean includeSRG = false;
//        boolean includeVSrg = false;
//        boolean includePadding = false;
//        boolean includeMargin = false;
//        boolean includeRegularLayout = true;
//        boolean includeLayoutWeight = false;
//        boolean includeWRG = true;
//        boolean includeregSPLRG = true;
//        boolean includeScrollableText = true;
//
//        createGroupOfChangesForProblematicCollision(fixSetForCollision, groupsOfChanges, fixSetForCollision.get("problematic"), collision);
//
//        if (includeregSPLRG) {
//            HashMap<String, GAElementToChange> regSPLRGElements = fixSetForCollision.get("regSPLRG");
//            if (regSPLRGElements != null && regSPLRGElements.size() > 0) {
//
//                createGroupOfChangesForRegularLayout(fixSetForCollision, groupsOfChanges, regSPLRGElements, collision);
//            }
//        }
//
//        if (includeWRG) {
//            HashMap<String, GAElementToChange> wrgElements = fixSetForCollision.get("wrg");
//            if (wrgElements != null && wrgElements.size() > 0) {
//
//                createGroupOfChangesForWRGCollision(wrgElements, groupsOfChanges);
//            }
//
//            if (includeScrollableText) {
//                HashMap<String, GAElementToChange> scrollableElements = fixSetForCollision.get("scrollable");
//                if (scrollableElements != null && scrollableElements.size() > 0) {
//                    createGroupOfChangesForScrollableTextCollision(scrollableElements, groupsOfChanges);
//
//                }
//
//            }
//
//        }
//
//
//    }
//
//    private void createGroupOfChangesForScrollableTextCollision(HashMap<String, GAElementToChange> scrollableElements, HashMap<String, HashMap<String, GAChange>>
//            groupsOfChanges) {
//        HashMap<String, GAChange> group1 = groupsOfChanges.get("group1");
//        for (Map.Entry<String, GAElementToChange> entry : scrollableElements.entrySet()
//             ) {
//            String key = entry.getKey();
//            GAElementToChange gaElementToChange = entry.getValue();
//            String changeType=gaElementToChange.getTypeOfChange();
//            Lines lines = new Lines();
//            String text = gaElementToChange.getNode().getData().getText();
//            // count how many "\n in a text
//            int countTotalLines = text.length() - text.replace("\n", "").length();
//            lines.setNoOfLines(countTotalLines);
//            double newLinesVal;
//            if(changeType.equalsIgnoreCase(OwlConstants.CHANGE_CHANGE)){
//                // it is not defined so we need to add it
//                 newLinesVal=Math.floor(countTotalLines/2);}
//            else {
//                newLinesVal = Math.floor(countTotalLines / 2);
//                String currVal = Utils.getValueFromElement(gaElementToChange.getNode().getData(), Lines.propertyName);
//                if(currVal!=null){
//                    double currValNo=Util.getNumbersFromString(currVal).get(0);
//                    lines.currentStaticVal=currValNo+"";
//                    lines.setCurrentVal(currValNo);
//                    newLinesVal=Math.floor(currValNo/2);
//                }
//            }
//                int changeVal= (int) newLinesVal;
//                GAChange change = new GAChange(Lines.propertyName, OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(Lines.propertyName),
//                        changeType, changeVal+"", gaElementToChange, lines);
//                group1.put(gaElementToChange.getId(), change);
//            }
//
//
//        }
//
//
//
//
////        double parentTotalWeight;
////        for(WRGNode wrgNode:parentNodeWRG){
////            parentTotalWeight=  wrgNode.getParentTotal();
////            if(wrgNode.getXmlNode().getData().getId().equals(problematicElementID)){
////                Logger.debug("Found the problematic element in the WRG || INCREASE WEIGHT");
////                // problematic element for cuttoff  -- > increase the weight
////                GAElementToChange gaElementToChange = new GAElementToChange(wrgNode.getXmlNode(), "wrg", OwlConstants.CHANGE_INCREASE, prop, wrgNode);
////                wrgElementsToChange.put(wrgNode.getXmlNode().getData().getId(), gaElementToChange);
////
////
////            }
////            else{
////                // NOT problematic element for cuttoff  -- > decrease
////                GAElementToChange gaElementToChange = new GAElementToChange(wrgNode.getXmlNode(), "wrg", OwlConstants.CHANGE_DECREASE, prop, wrgNode);
////                wrgElementsToChange.put(wrgNode.getXmlNode().getData().getId(), gaElementToChange);
////                Logger.debug("Found other element in the WRG || DECREASE WEIGHT");
////            }
//
////    }
//
//    private void createGroupOfChangesForRegularLayout(HashMap<String, HashMap<String, GAElementToChange>> fixSetForCollision,
//                                                      HashMap<String, HashMap<String, GAChange>> groupsOfChanges, HashMap<String, GAElementToChange> regSPLRG, Collision collision) {
//        Logger.debug("Start of the Regular Layout changes method");
//        Node<DomNode> root = OwlEye.getOriginalDefaultUI().getXMLTree().getRoot();
//        LayoutGraph layoutGraph = new LayoutGraph(root);
//        String prop = Height.propertyName;
//        if (collision.getIssueType().equalsIgnoreCase("horizontal")) {
//            prop = Width.propertyName;
//        }
//        Node<DomNode> node0;
//        Node<DomNode> node1;
//        node0 = collision.getProblematicElementsNodes().get(0);
//        node1 = collision.getProblematicElementsNodes().get(1);
//        String node0ID = node0.getData().getId();
//        String node1ID = node1.getData().getId();
//        String node0IDWithPrefix = "@id/" + node0ID.replace("id/", "");
//        String node1IDWithPrefix = "@id/" + node1ID.replace("id/", "");
//        NeighborEdge edge = layoutGraph.findEdge(node0, node1);
//        if (prop.equalsIgnoreCase(Height.propertyName)) {
//            // we look for top to bottom
//            SPLRGNode node0SPLRG = (SPLRGNode) regSPLRG.get(node0ID).getGraphNode();
//            SPLRGNode node1SPLRG = (SPLRGNode) regSPLRG.get(node1ID).getGraphNode();
//            String node0CurrentProp = node0SPLRG.getProperty();
//            String node1CurrentProp = node1SPLRG.getProperty();
//            edge = layoutGraph.findEdge(node0, node1);
//            if (edge.isTopBottom()) {
//                //  GAChange change0 = new GAChange("layout_above", "android:layout_above", OwlConstants.CHANGE_CHANGE, node1IDWithPrefix,regSPLRG.get(node0ID));
//                GAChange change1 = new GAChange("layout_below", "android:layout_below", OwlConstants.CHANGE_CHANGE, node0IDWithPrefix, regSPLRG.get(node1ID));
//                //  groupsOfChanges.get("group1").put(node0ID,change0);
//                groupsOfChanges.get("group1").put(node1ID, change1);
//            }
//        }
//
//
//    }
//
//
//    /******************************************************** CUTOFF METHODS ****************************************************************************************/
//
//    private void createGroupOfChangesForProblematicCollision(HashMap<String, HashMap<String, GAElementToChange>> fixSetForCollision,
//                                                             HashMap<String, HashMap<String, GAChange>> groupsOfChanges,
//                                                             HashMap<String, GAElementToChange> problematicElementsSet, Collision collision) {
//        String mainProperty = Height.propertyName;
//        if (collision.getIssueType().equalsIgnoreCase("horizontal")) {
//            mainProperty = Width.propertyName;
//        }
//
//
//        // 1- main height and width
//
//        // 2- layout weight
//        // we usually want to increase weight of problematic text view and decrease the other weight of the other textview
//
//        Logger.debug("Start of the Problematic Layout changes method");
////
////        TreeMap<String, List<WRGNode>> propWRG = OwlEye.getOriginalDefaultUIWRG().getDependentNodesMap().get(mainProperty);
////
////        for (Map.Entry<String, GAElementToChange> problematicElement : problematicElementsSet.entrySet()) {
////            String id = problematicElement.getKey();
////            GAElementToChange gaElementToChange = problematicElement.getValue();
////            String parentXpath = gaElementToChange.getNode().getData().getParent().getxPath();
////            List<WRGNode> parentNodeWRG = propWRG.descendingMap().get(parentXpath);
////            if (parentNodeWRG == null) {
////                continue;
////            }
////            for (WRGNode wrgNode : parentNodeWRG) {
////                double parentTotalWeight;
////
////                if (wrgNode.getXmlNode().getData().getId().equals("problematicElementID")) {
////
////                }
////            }
////        }
//    }
//
//    private HashMap<String, HashMap<String, GAElementToChange>> GetFixSetForCollision(Collision collision) {
//
//        HashMap<String, HashMap<String, GAElementToChange>> elementsToChange = new HashMap<>(); //string: id or xpath of the element
//
//        String mainProperty = Height.propertyName;
//        if (collision.getIssueType().equalsIgnoreCase("horizontal")) {
//            mainProperty = Width.propertyName;
//        }
//
//        elementsToChange.put("problematic", new HashMap<>());
//        elementsToChange.put("wrg", new HashMap<>());
//        elementsToChange.put("srg", new HashMap<>());
//        elementsToChange.put("vsrg", new HashMap<>());
//        elementsToChange.put("regSPLRG", new HashMap<>());
//        elementsToChange.put("padding", new HashMap<>()); // we add all elements that sibiling and parent of the problematic element and have padding (including the problematic element itself)
//        elementsToChange.put("margin", new HashMap<>());
//        elementsToChange.put("scrollable", new HashMap<>());
//
//        ArrayList<String> problematicIDs = new ArrayList<>();
//
//        /** 1- problematicElements **/
//        // add all of them
//        ArrayList<Node<DomNode>> problematicNodes = collision.getProblematicElementsNodes();
//        for (Node<DomNode> problematicElement : problematicNodes) {
//            problematicIDs.add(problematicElement.getData().getId());
//            GAElementToChange problematicElementToChange = new GAElementToChange(problematicElement, "problematic", OwlConstants.CHANGE_DECREASE, mainProperty); // usually we know that we need to increase or change something. For now I will keep it as decrease for default
//            elementsToChange.get("problematic").put(problematicElement.getData().getId(), problematicElementToChange);
//        }
//        /** 2- elements with weights. **/
//
//        WRG wrg = OwlEye.getOriginalDefaultUIWRG();
//        TreeMap<String, List<WRGNode>> wrgMainProperty = wrg.getDependentNodesMap().get(mainProperty);
//        ArrayList<String> handledProblematicElement = new ArrayList<>();
//
//        for (Node<DomNode> problematicElement : problematicNodes
//        ) {
//            if (handledProblematicElement.contains(problematicElement.getData().getId())) {
//                continue;
//            }
//            Node<DomNode> parent = problematicElement.getParent();
//            if (parent == null || wrgMainProperty.get(parent.getData().getxPath()) == null) {
//                continue;
//
//            }
//
//            List<WRGNode> siblings = wrgMainProperty.get(parent.getData().getxPath());
//            for (WRGNode sibling : siblings
//            ) {
//                String siblingID = sibling.getXmlNode().getData().getId();
//                String changeType = OwlConstants.CHANGE_DECREASE;
//                String weightProperty = sibling.getProperty();
//                // it is problematic so handle it now
//
//                if (problematicIDs.contains(siblingID)) {
//                    changeType = OwlConstants.CHANGE_INCREASE;
//                    handledProblematicElement.add(siblingID);
//                }
//                GAElementToChange gaElementToChange = new GAElementToChange(sibling.getXmlNode(), "wrg", changeType, weightProperty, sibling);
//                // add the property to change
//                elementsToChange.get("wrg").put(siblingID, gaElementToChange);
//
//            }
//
//
////        WRG wrg= OwlEye.getOriginalDefaultUIWRG();
////        TreeMap<String, List<WRGNode>> wrgMainProperty = wrg.getDependentNodesMap().get(mainProperty);
////        for (Node<DomNode> problematicElement : problematicNodes
////             ) {
////            List<WRGNode> dependentNodeMap = wrgMainProperty.get(problematicElement.getData().getxPath());
////            if (dependentNodeMap.size() >= 0) {
////                boolean problematicAdded=false;
////                for (WRGNode dependentNode : dependentNodeMap
////                     ) {
////                    String currNodeXpath = dependentNode.getXmlNode().getData().getxPath();
////                    GAElementToChange gaElementToChange = new GAElementToChange(dependentNode.getXmlNode(), "wrg", OwlConstants.CHANGE_DECREASE, mainProperty);
////                    elementsToChange.get("wrg").put(dependentNode.getXmlNode().getData().getId(), gaElementToChange);
////                    // I also need to add the problematic element itself to wrg list so lets find it and add it from the one of the wrg nodes because it is both ways relationships
////                    if(!problematicAdded){
////                        List<WRGNode> tempWRG = wrgMainProperty.get(currNodeXpath);
////                        if (dependentNodeMap.size() >= 0) {
////                            for (WRGNode tempWRGNode : tempWRG
////                            ) {
////                                if (tempWRGNode.getXmlNode().getData().getId().equalsIgnoreCase(problematicElement.getData().getId())) {
////                                    GAElementToChange problematicElementToChange = new GAElementToChange(problematicElement, "wrg", OwlConstants.CHANGE_DECREASE, mainProperty); // usually we know that we need to increase or change something. For now I will keep it as decrease for default
////                                    elementsToChange.get("wrg").put(problematicElement.getData().getId(), problematicElementToChange);
////                                    problematicAdded=true;
////                                    break;
////                                }
////                            }
////                        }
////
////                    }
////                }
////                Logger.debug("There are dependent nodes for problematic element: " + problematicElement.getData().getId());
//
//            //}
//        }
//
//        /** 3- layout changes. **/
//
//        SPLRG splrg = OwlEye.getOriginalDefaultUISPLRG();
//        // I only want the regular layout relations
//        TreeMap<String, List<SPLRGNode>> regularDependentNodesMap = splrg.getDependentNodesMap().get(OwlConstants.REGULAR_LAYOUT_RELATION);
//        HashMap<String, Node<DomNode>> nodesWithLayoutDirection = new HashMap<>();
//        for (Node<DomNode> problematicNode : problematicNodes) {
//            String id = problematicNode.getData().getId();
//            List<SPLRGNode> dependentNodes = regularDependentNodesMap.get(id);
//            for (SPLRGNode dependentNode : dependentNodes) {
//                String dependPropType = dependentNode.getPropValue().getValType();
//                if (dependPropType.equalsIgnoreCase("id")) { // direction with Other element
//                    nodesWithLayoutDirection.put(problematicNode.getData().getId(), problematicNode);
//                    GAElementToChange problematicElementToChange = new GAElementToChange(problematicNode, "regSPLRG", OwlConstants.CHANGE_CHANGE, mainProperty, dependentNode); // usually we know that we need to increase or change something. For now I will keep it as decrease for default
//
//                    elementsToChange.get("regSPLRG").put(problematicNode.getData().getId(), problematicElementToChange);
//                }
//
//            }
//
//        }
//
//
//        //** scrollable textViews changes **/
//        for (Node<DomNode> problematicElement : problematicNodes) {
//            problematicIDs.add(problematicElement.getData().getId());
//            boolean isScrollable = checkIfElementIsAlongScrollableTextView(problematicElement);
//            if (isScrollable) {
//                // check if there is a android:lines property defined
//                String linesVal = Utils.getValueFromElement(problematicElement.getData(), Lines.propertyName);
//                String changeType = OwlConstants.CHANGE_CHANGE;
//                Lines lines = new Lines();
//                if (linesVal != null) {
//                    changeType = OwlConstants.CHANGE_DECREASE;
//                    lines.setCurrentVal(Integer.parseInt(linesVal));
//                    lines.setCurrentStaticVal(linesVal);
//                }
//                GAElementToChange problematicElementToChange = new GAElementToChange(problematicElement, "scrollable",
//                        changeType, Lines.propertyName); // usually we know that we need to increase or change something. For now I will keep it as decrease for default
//                elementsToChange.get("scrollable").put(problematicElement.getData().getId(), problematicElementToChange);
//            }
//
//        }
//
//
//        return elementsToChange;
//    }
//
//    /****************************************************************************************************************************************************************/
//
//    private HashMap<String, HashMap<String, GAElementToChange>> GetFixSetForCutoff(Cutoff cutoff) {
//        // Find the elements that we may need to change to fix the problem
//        HashMap<String, HashMap<String, GAElementToChange>> elementsToChange = new HashMap<>(); //string: id or xpath of the element
//
//
//        // 1- The problematic element
//        Node<DomNode> problematicElement = cutoff.getProblematicElements().get(0); // this is cutoff so it is always one element
//        elementsToChange.put("problematic", new HashMap<>());
//        elementsToChange.put("wrg", new HashMap<>());
//        elementsToChange.put("srg", new HashMap<>());
//        elementsToChange.put("vsrg", new HashMap<>());
//        elementsToChange.put("padding", new HashMap<>()); // we add all elements that sibiling and parent of the problematic element and have padding (including the problematic element itself)
//        elementsToChange.put("margin", new HashMap<>());
//        // size dependent elements (parent)
//        String prop = Height.propertyName;
//        if (cutoff.getIssueType().equalsIgnoreCase("horizontal")) {
//            prop = Width.propertyName;
//            // this has to be done later
//        }
//        GAElementToChange problematicElementToChange = new GAElementToChange(problematicElement, "problematic", OwlConstants.CHANGE_INCREASE, prop); // usually we know it is increase fo the problematic element
//        elementsToChange.get("problematic").put(problematicElement.getData().getId(), problematicElementToChange);
//
////
//
//        // Now that we got the very basic which is the problematic element.
//        // Now we refer to the UI models to find other elements that we may need to change and we keep each seperate so we can decide later how to change.
//        boolean checkWRG = true;
//        boolean checkSRG = true;
//        boolean checkVSRG = true;
//        boolean checkPadding = true;
//        boolean checkMargin = true;
//        boolean checkLayout = false;
//        // (1) elements based on SRG
//
//        if (checkWRG) {
//            findImpactedElementsBasedWRG(cutoff, elementsToChange.get("wrg"), problematicElement, prop);
//        }
//        if (checkSRG) {
//            findImpactedElementsBasedOnSRG(cutoff, elementsToChange.get("srg"), problematicElement, prop);
//
//
//        }
//
//        if (checkVSRG) {
//            // (2) elements based on VSRG
//            findImpactedElementsBasedOnVSRG(cutoff, elementsToChange.get("vsrg"), problematicElement, prop);
//        }
//        if (checkPadding) {
//            // (3) elements based on padding
//            findImpactedElementsBasedOnPadding(cutoff, elementsToChange.get("padding"), problematicElement, prop);
//        }
//        if (checkMargin) {
//            // (4) elements based on margin
//            findImpactedElementsBasedOnMargin(cutoff, elementsToChange.get("margin"), problematicElement, prop);
//        }
//        if (checkLayout) {
//            findImpactedElementsBasedOnLayout(cutoff, elementsToChange.get("layout"), problematicElement);
//        }
//
//
//        // 3- Other elements that we may need to change?
////System.out.println("SYSTEM EXIS INSIDE GET FIX SET METHOD");
////        System.exit(1);
//        return elementsToChange;
//    }
//
//    private HashMap<String, HashMap<String, GAElementToChange>> combineFixSets(HashMap<String, HashMap<String, HashMap<String, GAElementToChange>>> fixSetForCutOffIssues) {
//        HashMap<String, HashMap<String, GAElementToChange>> combinedFixSet = new HashMap<>(); //string: id or xpath of the element
//
////        combinedFixSet.put("problematic", new HashMap<>());
////        combinedFixSet.put("srg", new HashMap<>());
////        combinedFixSet.put("vsrg", new HashMap<>());
////        combinedFixSet.put("padding", new HashMap<>()); // we add all elements that sibiling and parent of the problematic element and have padding (including the problematic element itself)
////        combinedFixSet.put("margin", new HashMap<>());
//        HashMap<String, GAElementToChange> problematic = new HashMap<>();
//        for (String issue : fixSetForCutOffIssues.keySet()
//        ) {
//            HashMap<String, GAElementToChange> problematicForIssue = fixSetForCutOffIssues.get(issue).get("problematic");
//            problematic.putAll(problematicForIssue);
//            combinedFixSet.put("problematic", problematic);
//        }
//        // 2- extracting all the SRG elements
//        HashMap<String, GAElementToChange> srg = new HashMap<>();
//        for (String issue : fixSetForCutOffIssues.keySet()
//        ) {
//            HashMap<String, GAElementToChange> srgForIssue = fixSetForCutOffIssues.get(issue).get("srg");
//            srg.putAll(srgForIssue);
//            combinedFixSet.put("srg", srg);
//        }
//        // 3- extracting all vsrg elements
//        HashMap<String, GAElementToChange> vsrg = new HashMap<>();
//        for (String issue : fixSetForCutOffIssues.keySet()
//        ) {
//            HashMap<String, GAElementToChange> vsrgForIssue = fixSetForCutOffIssues.get(issue).get("vsrg");
//            vsrg.putAll(vsrgForIssue);
//            combinedFixSet.put("vsrg", vsrg);
//        }
//
//        // 4- extracting all padding elements
//        HashMap<String, GAElementToChange> padding = new HashMap<>();
//        for (String issue : fixSetForCutOffIssues.keySet()
//        ) {
//            HashMap<String, GAElementToChange> paddingForIssue = fixSetForCutOffIssues.get(issue).get("padding");
//            padding.putAll(paddingForIssue);
//            combinedFixSet.put("padding", padding);
//        }
//
//        // 5- extracting all margin elements
//        HashMap<String, GAElementToChange> margin = new HashMap<>();
//        for (String issue : fixSetForCutOffIssues.keySet()
//        ) {
//            HashMap<String, GAElementToChange> marginForIssue = fixSetForCutOffIssues.get(issue).get("margin");
//            margin.putAll(marginForIssue);
//            combinedFixSet.put("margin", margin);
//        }
//
//
//        // 5- extracting all weight elements
//        HashMap<String, GAElementToChange> weight = new HashMap<>();
//        for (String issue : fixSetForCutOffIssues.keySet()
//        ) {
//            HashMap<String, GAElementToChange> weightForIssue = fixSetForCutOffIssues.get(issue).get("wrg");
//            weight.putAll(weightForIssue);
//            combinedFixSet.put("wrg", weight);
//        }
//
//        return combinedFixSet;
//    }
//
//    private HashMap<String, HashMap<String, GAChange>> createGroupOfChangesCutoff
//            (HashMap<String, HashMap<String, GAElementToChange>> combinedCutOffFixedSet,
//             HashMap<String, HashMap<String, HashMap<String, GAElementToChange>>> fixSetForCutOffIssues,
//             HashMap<String, HashMap<String, GAChange>> groupsOfChanges) {
//
//        boolean includeWRG = true;
//        boolean includeSRG = true;
//        boolean includeVSrg = true;
//        boolean includePadding = true;
//        boolean includeMargin = true;
//
//        HashMap<String, GAElementToChange> problematicElements = combinedCutOffFixedSet.get("problematic");
//        createGroupOfChangesForProblematicElement(problematicElements, groupsOfChanges);
//
//
//        if (includeWRG) {
//            HashMap<String, GAElementToChange> wrgElements = combinedCutOffFixedSet.get("wrg");
//            createGroupOfChangesForWRG(wrgElements, groupsOfChanges);
//        }
//        if (includeSRG) {
//            HashMap<String, GAElementToChange> srgElements = combinedCutOffFixedSet.get("srg");
//            createGroupOfChangesForSRGElements(srgElements, groupsOfChanges);
//        }
//        if (groupsOfChanges.get("group1").size() < 1) {
//            Logger.debug("group1 is null");
//
//        }
//
//
//        if (includePadding) {
//            HashMap<String, GAElementToChange> paddingElements = combinedCutOffFixedSet.get("padding");
//            createGroupOfChangesForPaddingElements(paddingElements, groupsOfChanges);
//        }
//
//        if (includeMargin) {
//            HashMap<String, GAElementToChange> marginElements = combinedCutOffFixedSet.get("margin");
//            createGroupOfChangesForMarginElements(marginElements, groupsOfChanges);
//        }
//        Logger.debug("Group of Changes size: " + groupsOfChanges.size());
//        for (String key : groupsOfChanges.keySet()
//        ) {
//            HashMap<String, GAChange> group = groupsOfChanges.get(key);
//            for (String key2 : group.keySet()
//            ) {
//                GAChange change = group.get(key2);
//                if (change != null) {
//                    Logger.debug("Change of " + key2 + ": " + change.getPropertyName() + " | val= " + change.getValue());
//                }
//            }
//
//        }
//
//        return groupsOfChanges;
//
//    }
//
//    private void findImpactedElementsBasedWRG(Cutoff cutoff, HashMap<String, GAElementToChange> wrgElementsToChange, Node<DomNode> problematicElement,
//                                              String prop) {
//        // get the WRG
//        TreeMap<String, List<WRGNode>> propWRG = OwlEye.getOriginalDefaultUIWRG().getDependentNodesMap().get(prop);
//        String problematicElementID = problematicElement.getData().getId();
//        String parentXpath = problematicElement.getParent().getData().getxPath();
//        List<WRGNode> parentNodeWRG = propWRG.descendingMap().get(parentXpath);
//        if (parentNodeWRG == null) {
//            return;
//        }
//        double parentTotalWeight;
//        for (WRGNode wrgNode : parentNodeWRG) {
//            parentTotalWeight = wrgNode.getParentTotal();
//            if (wrgNode.getXmlNode().getData().getId().equals(problematicElementID)) {
//                Logger.debug("Found the problematic element in the WRG || INCREASE WEIGHT");
//                // problematic element for cuttoff  -- > increase the weight
//                GAElementToChange gaElementToChange = new GAElementToChange(wrgNode.getXmlNode(), "wrg", OwlConstants.CHANGE_INCREASE, prop, wrgNode);
//                wrgElementsToChange.put(wrgNode.getXmlNode().getData().getId(), gaElementToChange);
//
//
//            } else {
//                // NOT problematic element for cuttoff  -- > decrease
//                GAElementToChange gaElementToChange = new GAElementToChange(wrgNode.getXmlNode(), "wrg", OwlConstants.CHANGE_DECREASE, prop, wrgNode);
//                wrgElementsToChange.put(wrgNode.getXmlNode().getData().getId(), gaElementToChange);
//                Logger.debug("Found other element in the WRG || DECREASE WEIGHT");
//            }
//
////            GAElementToChange elementToChangeGA = new GAElementToChange(elementToChange, "wrg", OwlConstants.CHANGE_INCREASE, prop);
////            wrgElementsToChange.put(elementToChange.getData().getId(), elementToChangeGA);
////
//        }
//
//
//    }
//
//    private void findImpactedElementsBasedOnSRG(Cutoff cutoff, HashMap<String, GAElementToChange> elementsToChange, Node<DomNode> problematicElement, String prop) {
//
////        String prop = Height.propertyName;
////        if (cutoff.getIssueType().equalsIgnoreCase("vertical")) {
////            prop = Width.propertyName;
////            // this has to be done later
////        }
//
//
//        SRG srg = OwlEye.getOriginalDefaultUISRG();
//        String xPath = problematicElement.getData().getxPath();
//        List<SRGNode> sizeDependentElements = srg.getDependentNodesMap2().get(prop).get(xPath); // for the property
//        //System.out.println(srg);
//        if (sizeDependentElements != null && sizeDependentElements.size() > 0) {
//            for (SRGNode sizeDependentElement : sizeDependentElements
//            ) {
//                double ratio = sizeDependentElement.getRatio();
//
//                Node<DomNode> node = sizeDependentElement.getXmlNode();
//                GAElementToChange e = new GAElementToChange(node, "SRG", OwlConstants.CHANGE_INCREASE, prop);
//                if (prop.equalsIgnoreCase(Height.propertyName)) {
//                    e.setHeightRatio(ratio);
//                } else {
//                    // width
//                    e.setWidthRatio(ratio);
//                }
//                elementsToChange.put(node.getData().getId(), e);
//
//            }
//        }
//
//    }
//
//    private void findImpactedElementsBasedOnMargin(Cutoff cutoff, HashMap<String, GAElementToChange> margin, Node<DomNode> problematicElement, String changeDirection) {
//        // for margins we need to check the margins of the element itself and the margins of the parent (I am not going to checkthe margins of the sibilings for now)
//        Logger.trace("Cutoff findImpactedElementsBasedOnMargins");
//        ArrayList<Node<DomNode>> nodesToConsiderMargin = new ArrayList<>();
//        Map<String, String> marginAttributesToFind;
//
//        if (cutoff.getIssueType().equals("Horizontal".toLowerCase())) {
//            marginAttributesToFind = OwlConstants.MARGINS_FOR_HORIZONTAL_CUTOFF_ISSUES;
//        } else {
//            marginAttributesToFind = OwlConstants.MARGINS_FOR_VERTICAL_CUTOFF_ISSUES;
//        }
//
//        Node<DomNode> firstParent = problematicElement.getParent();
//        if (firstParent != null) {
//            List<Node<DomNode>> siblings = firstParent.getChildren();
//            if (siblings.size() > 0) {
//                nodesToConsiderMargin.addAll(siblings);
//            }
//
//        }
//        Node<DomNode> parent = problematicElement.getParent();
//        while (parent != null) {
//            nodesToConsiderMargin.add(parent);
//            parent = parent.getParent(); // all parent of the problematic element
//        }
//        boolean addSiblingsOfParent = false;
//        if (addSiblingsOfParent) {
//            Node<DomNode> parentOfParent = firstParent.getParent();
//            if (parentOfParent != null) {
//                List<Node<DomNode>> siblingsOfParent = parentOfParent.getChildren();
//                if (siblingsOfParent.size() > 0) {
//                    nodesToConsiderMargin.addAll(siblingsOfParent);
//                }
//            }
//        }
//
//
//        nodesToConsiderMargin.add(problematicElement);
//
//        // List<Node<DomNode>> siblings = parent.getChildren();
////        if(siblings.size()>0) {
////            nodesToConsiderPadding.addAll(siblings);
////        }
//
//        for (Node<DomNode> node : nodesToConsiderMargin
//        ) {
//            Map<String, String> attributes = node.getData().getAttributes();
//            for (String key : attributes.keySet()
//            ) {
//                if (key.contains("_margin")) {
//                    String att = key;
//                    if (marginAttributesToFind.containsKey(att)) {
//                        String value = attributes.get(key);
//                        Double marginValue = Util.getNumbersFromString(value).get(0);
//                        if (marginValue > 0) {
//                            Logger.debug("Margin Attribute: " + att + " Value: " + value);
//                            GAElementToChange elementToChange = new GAElementToChange(node, "margin", OwlConstants.CHANGE_DECREASE, changeDirection);
//                            margin.put(node.getData().getId(), elementToChange);
//                        }
//                    }
////                    String value = attributes.get(key);
////                    Double paddingValue = Util.getNumbersFromString(value).get(0);
////                    if (paddingValue > 0) {
////                        Logger.debug("Margin Attribute: " + att + " Value: " + value);
////                        if (att.contains("_margin")) {
////                            GAElementToChange elementToChange = new GAElementToChange(node, "margin", OwlConstants.Decrease, changeDirection);
////                            margin.put(node.getData().getId(), elementToChange);
////                        }
////                    }
//                }
//            }
//        }
//    }
//
//    private void findImpactedElementsBasedOnPadding(Cutoff cutoff, HashMap<String, GAElementToChange> padding, Node<DomNode> problematicElement, String changeDirection) {
//        // We check the padding of the problematic element and its siblings and if they have padding we add them to the list of elements to change
//        Logger.trace("Cutoff findImpactedElementsBasedOnPadding");
//        Map<String, String> paddingAttributesToFind;
//        if (cutoff.getIssueType().equals("Horizontal".toLowerCase())) {
//            paddingAttributesToFind = OwlConstants.PADDING_FOR_HORIZONTAL_CUTOFF_ISSUES;
//        } else {
//            paddingAttributesToFind = OwlConstants.PADDING_FOR_VERTICAL_CUTOFF_ISSUES;
//        }
//
//        ArrayList<Node<DomNode>> nodesToConsiderPadding = new ArrayList<>();
//        Node<DomNode> parent = problematicElement.getParent();
//        if (parent != null) {
//            nodesToConsiderPadding.add(parent); // Now I add the parent for infini app just testing
//        }
//        List<Node<DomNode>> siblings = parent.getChildren();
//        if (siblings.size() > 0) {
//            nodesToConsiderPadding.addAll(siblings);
//        }
//
//        for (Node<DomNode> node : nodesToConsiderPadding
//        ) {
//            Map<String, String> attributes = node.getData().getAttributes();
//            for (String key : attributes.keySet()
//            ) {
//                if (key.contains("padding")) {
//                    String att = key;
//                    if (paddingAttributesToFind.containsKey(att)) {
//                        String value = attributes.get(key);
//                        Double paddingValue = Util.getNumbersFromString(value).get(0);
//                        if (paddingValue > 0) {
//                            Logger.trace("Padding Attribute: " + att + " Value: " + value);
//
//                            GAElementToChange elementToChange = new GAElementToChange(node, "padding", OwlConstants.CHANGE_DECREASE, changeDirection);
//                            padding.put(node.getData().getId(), elementToChange);
//
//                        }
//                    }
////                    String value = attributes.get(key);
////                    Double paddingValue = Util.getNumbersFromString(value).get(0);
////                    if (paddingValue > 0) {
////                        Logger.trace("Padding Attribute: " + att + " Value: " + value);
////                        if (att.contains("padding")) {
////                            GAElementToChange elementToChange = new GAElementToChange(node, "padding", OwlConstants.Decrease, changeDirection);
////                            padding.put(node.getData().getId(), elementToChange);
////                        }
////                    }
//                }
//            }
//        }
//
//
//    }
//
//    private void findImpactedElementsBasedOnVSRG(Cutoff cutoff, HashMap<String, GAElementToChange> elementsToChange, Node<DomNode> problematicElement, String prop) {
//        VSRG vsrg = OwlEye.getOriginalDefaultUIVSRG();
//        String xPath = problematicElement.getData().getxPath(); // ToDo: do we need to get the SRG elements based on other SRG elements?? so if we get a node that is size dependent on a problematic element do we also seacrch for nodes that this new nodd depends on?
//        List<VSRGNode> visualDependentElements = vsrg.getDependentNodesMap().get(xPath);
//        if (visualDependentElements != null && visualDependentElements.size() > 0) {
//            for (VSRGNode visualNode : visualDependentElements
//            ) {
//                Node<DomNode> node = visualNode.getXmlNode();
//                elementsToChange.put(node.getData().getId(), new GAElementToChange(node, "VSRG", OwlConstants.CHANGE_INCREASE, prop)); // we also assume that the
//
//            }
//        }
//
//    }
//
//    private void findImpactedElementsBasedOnLayout(Cutoff cutoff, HashMap<String, GAElementToChange> elementsToChange, Node<DomNode> problematicElement) {
//    }
//
//    /**** creating groups ******/
//
//
//    private void createGroupOfChangesForProblematicElement(HashMap<String, GAElementToChange> problematicElements, HashMap<String, HashMap<String, GAChange>> groupsOfChanges) {
//        HashMap<String, HashMap<String, GAChange>> groups = new HashMap<>();
//        HashMap<String, GAChange> group1 = new HashMap<>();
//        HashMap<String, GAChange> group2 = new HashMap<>();
//        for (String problematicElementId : problematicElements.keySet()) {
//            HashMap<String, Property> propertiesToChangeObject = new HashMap<>();
//            GAElementToChange element = problematicElements.get(problematicElementId);
//            String directionFocus = element.getDirectionFocus();
//            Property sizeProp = null;
//            if (directionFocus.equalsIgnoreCase(Height.propertyName)) {
//                sizeProp = new Height();
//                String dynamicVal = Utils.getDynamicValueInDDP(element.getNode().getData(), Height.propertyName);
//                sizeProp.setCurrentDynamicVal(dynamicVal);
//                String staticVal = Utils.getValueFromElement(element.getNode().getData(), Height.propertyName);
//                sizeProp.setCurrentStaticVal(staticVal);
//                propertiesToChangeObject.put(Height.propertyName, sizeProp);
//            } else if (directionFocus.equalsIgnoreCase(Width.propertyName)) {
//                sizeProp = new Width();
//                String dynamicVal = Utils.getDynamicValueInDDP(element.getNode().getData(), Width.propertyName);
//                sizeProp.setCurrentDynamicVal(dynamicVal);
//                String staticVal = Utils.getValueFromElement(element.getNode().getData(), Width.propertyName);
//                sizeProp.setCurrentStaticVal(staticVal);
//                propertiesToChangeObject.put(Width.propertyName, sizeProp);
//            }
//
//
//            // gene group 1 for main size --> do not change how it is set (if it is fixed directly increase it or if it is constraints add Min)
//            GAChange change = createDoNotChangeSizeTypeGroup(element, sizeProp, problematicElements, group1);
//            if (change != null) {
//                group1.put(element.getId(), change);
//            }
//            GAChange change2 = createChangeSizeTypeGroup(element, sizeProp, problematicElements);
//            // GAChange change2=change;   // Activate this if  we are not considering the case where the size is set to wrap content for so I am adding group 1 to group 2
//            if (change2 != null) {
//
//                group2.put(element.getId(), change2);
//            }
//
////            }
//            // gene group 2 for main size --> change how it was set --> if it is fixed change it to wrap_content with minWidth = the dynamic value + 10  or if it is constraints changes to to fixed and increase it
//
//            // for each group add the text attribute once and once do not add it at all ( no change to text attributes) || if no text attributes defined then we just have 1 and 2
//            // gene group 1-1 (group 1 above + text attribute changed )
//            // gene group 1-2 (group 1 above + text attributes deleted if it exists)
//            // gene group 2-1 (group 2 above + text attribute changed )
//            // gene group 2-2 (group 2 above + text attributes deleted if it exists)
//
//
//            // Now regardless of the direction focus we should check the text attributes
//            // if the node is in TRG then we should add those attributes to the properties to change
//            TRG trg = OwlEye.getOriginalDefaultUITRG();
//            if (trg.getDependentNodesMap().containsKey(element.getNode().getData().getId())) {
//                // This means that the node is in TRG
//                TRGNode trgNode = trg.getDependentNodesMap().get(element.getNode().getData().getId());
//                Logger.trace("The node is in TRG: " + trgNode.getXpath());
//                HashMap<String, Property> textAtt = trgNode.getTextAttributes();
//                for (Map.Entry<String, Property> entry : textAtt.entrySet()) {
//                    String key = entry.getKey();
//                    Property value = entry.getValue();
//                    Logger.trace("The key is " + key + " and the value is " + value);
//                    switch (key) {
//                        case MaxLine.propertyName: {
//                            Logger.trace("MaxLine");
//                            MaxLine maxLine = (MaxLine) value;
//                            int currentMaxLine = maxLine.getCurrentVal();
//                            int newMaxLine = currentMaxLine + 1; // Group 1 will have + 1 and group 2 will have + 2
//                            GAChange change3 = generateMaxLinesChange(key, currentMaxLine, newMaxLine, element, maxLine);
//                            propertiesToChangeObject.put(key, (MaxLine) value);
//                            if (change3 != null) {
//                                group1.put(element.getId(), change3);
//                            }
//                            newMaxLine = currentMaxLine + 2; // Group 2 will have + 2
//                            change3 = generateMaxLinesChange(key, currentMaxLine, newMaxLine, element, maxLine);
//                            propertiesToChangeObject.put(key, (MaxLine) value);
//                            if (change3 != null) {
//                                group2.put(element.getId(), change3);
//                            }
//
//                            break;
//                        }
//                        case Lines.propertyName: {
//
//                            Logger.trace("Lines");
//                            Lines line = (Lines) value;
//                            double currentLine = line.getCurrentVal();
//                            double newLine = currentLine + 1; // Group 1 will have + 1 and group 2 will have + 2
//                            GAChange change3 = generateMaxLinesChange(key, currentLine, newLine, element, line);
//                            propertiesToChangeObject.put(key, (Lines) value);
//                            if (change3 != null) {
//                                group1.put(element.getId(), change3);
//                            }
//                            newLine = currentLine + 2; // Group 2 will have + 2
//                            change3 = generateMaxLinesChange(key, currentLine, newLine, element, line);
//                            propertiesToChangeObject.put(key, (Lines) value);
//                            if (change3 != null) {
//                                group2.put(element.getId(), change3);
//                            }
//
//                            break;
//                        }
//                    }
//
////                if (key.equalsIgnoreCase(MaxLine.propertyName)) {
////                    MaxLine maxLine = new MaxLine();
////                    propertiesToChangeObject.put(key, maxLine);
////                } else if (key.equalsIgnoreCase(Lines.propertyName)) {
////                    Lines lines = new Lines();
////                    propertiesToChangeObject.put(key, lines);
////                }
//
//
//                }
//
//            }
//
//        }
//        groupsOfChanges.get("group1").putAll(group1);
//        groupsOfChanges.get("group2").putAll(group2);
////        groups.put("group1", group1);
////        groups.put("group2", group2);
//        // return groups;
//    }
//
//    private void createGroupOfChangesForWRGCollision(HashMap<String, GAElementToChange> wrgElements, HashMap<String, HashMap<String, GAChange>> groupsOfChanges) {
//
//        // to handle weight for cutoff
//        HashMap<String, HashMap<String, ArrayList<GAElementToChange>>> parentChildrenGroups = findGroupsOfSiblingsForWRGFixSet(wrgElements);
//        HashMap<String, GAChange> group1 = new HashMap<>();
//        // iterate through parentChildrenGroups as keyset
//        for (Map.Entry<String, HashMap<String, ArrayList<GAElementToChange>>> entry : parentChildrenGroups.entrySet()
//        ) {
//            double parentTotal = Double.parseDouble(entry.getKey().split("#")[1]);
//            double parentRemainingWeight = parentTotal;
//            HashMap<String, ArrayList<GAElementToChange>> children = entry.getValue();
//            // start with decrease first
//
//            double generalDecreasePercentage = 0.5;
//            double imageViewDecreasePercentage = 0.65;
//            ArrayList<GAElementToChange> decreaseChildren = children.get(OwlConstants.CHANGE_DECREASE);
//            for (GAElementToChange element : decreaseChildren) {
//                WRGNode wrgNode = (WRGNode) (element.getGraphNode());
//                // get the current weight of the node
//                double currentWeight = wrgNode.getNumerator();
//                String property = wrgNode.getProperty();
//                Property propertyObject = wrgNode.getPropertyObj();
//                double decreasePercentage = generalDecreasePercentage;
//                String tagName = element.getNode().getData().getTagName();
//                if (element.getNode().getData().getTagName().equalsIgnoreCase("imageview")) {
//                    decreasePercentage = imageViewDecreasePercentage;
//                }
//                double newWeight = Math.ceil(currentWeight - (currentWeight * decreasePercentage));
//                if (newWeight < 0) { // so we do not overflow
//                    newWeight = 0;
//                }
//                if (newWeight > parentRemainingWeight) {
//                    newWeight = parentRemainingWeight;
//                }
//                GAChange change = new GAChange(property, OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(property), OwlConstants.CHANGE_DECREASE,
//                        newWeight + "", element, propertyObject);
//                group1.put(element.getId(), change);
//                parentRemainingWeight = parentRemainingWeight - newWeight;
//                Logger.debug(element.getId() + " old weight is " + currentWeight + ", new weight after" + OwlConstants.CHANGE_DECREASE + " is " + newWeight);
//
//            }
//
//
//            // now increase
//            ArrayList<GAElementToChange> increaseChildren = children.get(OwlConstants.CHANGE_INCREASE);
//            double sizeOfIncrease = increaseChildren.size();
//            double decreaseWeightSoFar = parentTotal - parentRemainingWeight;
//            double weightForEachIncrease = Math.floor((parentTotal - decreaseWeightSoFar) / sizeOfIncrease);
//
//            for (GAElementToChange element : increaseChildren) {
//                WRGNode wrgNode = (WRGNode) (element.getGraphNode());
//                // get the current weight of the node
//                double currentWeight = wrgNode.getNumerator();
//                String property = wrgNode.getProperty();
//                Property propertyObject = wrgNode.getPropertyObj();
//                double newWeight = Math.ceil(currentWeight + (currentWeight * 0.5));
//                if (newWeight < 0) { // so we do not overflow
//                    newWeight = 0;
//                }
//                if (newWeight < weightForEachIncrease) {
//                    newWeight = weightForEachIncrease;
//                }
//                if (newWeight > parentRemainingWeight) {
//                    newWeight = parentRemainingWeight;
//                }
//
//                parentRemainingWeight = parentRemainingWeight - newWeight;
//                GAChange change = new GAChange(property, OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(property), OwlConstants.CHANGE_DECREASE,
//                        newWeight + "", element, propertyObject);
//                group1.put(element.getId(), change);
//                Logger.debug(element.getId() + " old weight is " + currentWeight + ", new weight after" + OwlConstants.CHANGE_INCREASE + " is " + newWeight);
//
//            }
//
//
////            double parentRemainingWeight=parentTotal;
////            for (GAElementToChange element : children) {
////                WRGNode wrgNode = (WRGNode)(element.getGraphNode());
////                // get the current weight of the node
////                double currentWeight = wrgNode.getNumerator();
////                String typeOfChange = element.getTypeOfChange();
////                switch (typeOfChange){
////                    case OwlConstants.CHANGE_INCREASE:{
////                        // increase the weight by %50
////
////                        double newWeight = Math.ceil(currentWeight + (currentWeight * 0.5));
////                        if (newWeight > parentRemainingWeight) { // so we do not overflow
////                            newWeight = parentRemainingWeight;
////                        }
////                        parentRemainingWeight=parentRemainingWeight-newWeight;
////                        Logger.debug(element.getId() + " old weight is " + currentWeight + ", new weight after" +OwlConstants.CHANGE_INCREASE+" is " + newWeight);
////
////                    break;
////                    }
////                    case OwlConstants.CHANGE_DECREASE:{
////                        // decrease the weight by %50
////                        double newWeight = Math.ceil(currentWeight - (currentWeight * 0.5));
////                        if (newWeight < 0) { // so we do not overflow
////                            newWeight = 0;
////                        }
////                        if(newWeight>parentRemainingWeight){
////                            newWeight=parentRemainingWeight;
////                        }
////                        parentRemainingWeight=parentRemainingWeight-newWeight;
////                        Logger.debug(element.getId() + " old weight is " + currentWeight + ", new weight after" +OwlConstants.CHANGE_DECREASE+" is " + newWeight);
////
////                    break;
////                    }
////                }
////
////            }
////
//        }
//        groupsOfChanges.get("group1").putAll(group1);
//        Logger.debug("Done ");
//
//    }
//
//    private void createGroupOfChangesForWRG(HashMap<String, GAElementToChange> wrgElements, HashMap<String, HashMap<String, GAChange>> groupsOfChanges) {
//
//        // to handle weight for cutoff
//        HashMap<String, HashMap<String, ArrayList<GAElementToChange>>> parentChildrenGroups = findGroupsOfSiblingsForWRGFixSet(wrgElements);
//        HashMap<String, GAChange> group1 = new HashMap<>();
//        // iterate through parentChildrenGroups as keyset
//        for (Map.Entry<String, HashMap<String, ArrayList<GAElementToChange>>> entry : parentChildrenGroups.entrySet()
//        ) {
//            double parentTotal = Double.parseDouble(entry.getKey().split("#")[1]);
//            double parentRemainingWeight = parentTotal;
//            HashMap<String, ArrayList<GAElementToChange>> children = entry.getValue();
//            // start with decrease first
//
//            double generalDecreasePercentage = 0.3;
//            double imageViewDecreasePercentage = 0.65;
//            ArrayList<GAElementToChange> decreaseChildren = children.get(OwlConstants.CHANGE_DECREASE);
//            for (GAElementToChange element : decreaseChildren) {
//                WRGNode wrgNode = (WRGNode) (element.getGraphNode());
//                // get the current weight of the node
//                double currentWeight = wrgNode.getNumerator();
//                String property = wrgNode.getProperty();
//                Property propertyObject = wrgNode.getPropertyObj();
//                double decreasePercentage = generalDecreasePercentage;
//                String tagName = element.getNode().getData().getTagName();
//                if (element.getNode().getData().getTagName().equalsIgnoreCase("imageview")) {
//                    decreasePercentage = imageViewDecreasePercentage;
//                }
//                double newWeight = Math.ceil(currentWeight - (currentWeight * decreasePercentage));
//                if (newWeight < 0) { // so we do not overflow
//                    newWeight = 0;
//                }
//                if (newWeight > parentRemainingWeight) {
//                    newWeight = parentRemainingWeight;
//                }
//                GAChange change = new GAChange(property, OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(property), OwlConstants.CHANGE_DECREASE,
//                        newWeight + "", element, propertyObject);
//                group1.put(element.getId(), change);
//                parentRemainingWeight = parentRemainingWeight - newWeight;
//                Logger.debug(element.getId() + " old weight is " + currentWeight + ", new weight after" + OwlConstants.CHANGE_DECREASE + " is " + newWeight);
//
//            }
//
//
//            // now increase
//            ArrayList<GAElementToChange> increaseChildren = children.get(OwlConstants.CHANGE_INCREASE);
//            double sizeOfIncrease = increaseChildren.size();
//            double decreaseWeightSoFar = parentTotal - parentRemainingWeight;
//            double weightForEachIncrease = Math.floor((parentTotal - decreaseWeightSoFar) / sizeOfIncrease);
//
//            for (GAElementToChange element : increaseChildren) {
//                WRGNode wrgNode = (WRGNode) (element.getGraphNode());
//                // get the current weight of the node
//                double currentWeight = wrgNode.getNumerator();
//                String property = wrgNode.getProperty();
//                Property propertyObject = wrgNode.getPropertyObj();
//                double newWeight = Math.ceil(currentWeight + (currentWeight * 0.5));
//                if (newWeight < 0) { // so we do not overflow
//                    newWeight = 0;
//                }
//                if (newWeight < weightForEachIncrease) {
//                    newWeight = weightForEachIncrease;
//                }
//                if (newWeight > parentRemainingWeight) {
//                    newWeight = parentRemainingWeight;
//                }
//
//                parentRemainingWeight = parentRemainingWeight - newWeight;
//                GAChange change = new GAChange(property, OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(property), OwlConstants.CHANGE_DECREASE,
//                        newWeight + "", element, propertyObject);
//                group1.put(element.getId(), change);
//                Logger.debug(element.getId() + " old weight is " + currentWeight + ", new weight after" + OwlConstants.CHANGE_INCREASE + " is " + newWeight);
//
//            }
//
//
////            double parentRemainingWeight=parentTotal;
////            for (GAElementToChange element : children) {
////                WRGNode wrgNode = (WRGNode)(element.getGraphNode());
////                // get the current weight of the node
////                double currentWeight = wrgNode.getNumerator();
////                String typeOfChange = element.getTypeOfChange();
////                switch (typeOfChange){
////                    case OwlConstants.CHANGE_INCREASE:{
////                        // increase the weight by %50
////
////                        double newWeight = Math.ceil(currentWeight + (currentWeight * 0.5));
////                        if (newWeight > parentRemainingWeight) { // so we do not overflow
////                            newWeight = parentRemainingWeight;
////                        }
////                        parentRemainingWeight=parentRemainingWeight-newWeight;
////                        Logger.debug(element.getId() + " old weight is " + currentWeight + ", new weight after" +OwlConstants.CHANGE_INCREASE+" is " + newWeight);
////
////                    break;
////                    }
////                    case OwlConstants.CHANGE_DECREASE:{
////                        // decrease the weight by %50
////                        double newWeight = Math.ceil(currentWeight - (currentWeight * 0.5));
////                        if (newWeight < 0) { // so we do not overflow
////                            newWeight = 0;
////                        }
////                        if(newWeight>parentRemainingWeight){
////                            newWeight=parentRemainingWeight;
////                        }
////                        parentRemainingWeight=parentRemainingWeight-newWeight;
////                        Logger.debug(element.getId() + " old weight is " + currentWeight + ", new weight after" +OwlConstants.CHANGE_DECREASE+" is " + newWeight);
////
////                    break;
////                    }
////                }
////
////            }
////
//        }
//        groupsOfChanges.get("group1").putAll(group1);
//        Logger.debug("Done ");
//
//    }
//
//    private HashMap<String, HashMap<String, ArrayList<GAElementToChange>>> findGroupsOfSiblingsForWRGFixSet(HashMap<String, GAElementToChange> wrgElements) {
//        HashMap<String, HashMap<String, ArrayList<GAElementToChange>>> parentsWRG = new HashMap<>(); // seperate increase and decrease
//        for (Map.Entry<String, GAElementToChange> entry : wrgElements.entrySet()
//        ) {
//            String elementID = entry.getKey();
//            GAElementToChange element = entry.getValue();
//            WRGNode wrgNode = ((WRGNode) element.getGraphNode());
//            String parentXpath = wrgNode.getParentXpath();
//            double parentTotal = wrgNode.getParentTotal();
//            if (parentsWRG.get(parentXpath + "#" + parentTotal) == null) {
//                HashMap<String, ArrayList<GAElementToChange>> childrenWithWeight = new HashMap<>();
//                childrenWithWeight.put(OwlConstants.CHANGE_INCREASE, new ArrayList<>());
//                childrenWithWeight.put(OwlConstants.CHANGE_DECREASE, new ArrayList<>());
//                childrenWithWeight.get(element.getTypeOfChange()).add(element); // weather it is OwlConstants.CHANGE_INCREASE or OwlConstants.CHANGE_DECREASE
//                parentsWRG.put(parentXpath + "#" + parentTotal, childrenWithWeight);
//            } else {
//                parentsWRG.get(parentXpath + "#" + parentTotal).get(element.getTypeOfChange()).add(element);
////             parentsWRG.get(element.getTypeOfChange()).get(parentXpath+"#"+parentTotal,childrenWithWeight);
//
//            }
//        }
//        return parentsWRG;
//    }
//
//    private void createGroupOfChangesForMarginElements(HashMap<String, GAElementToChange> marginElements,
//                                                       HashMap<String, HashMap<String, GAChange>> groupsOfChanges) {
//
//        // we start from group 3  see the main method for more details of the groups
//        HashMap<String, GAChange> group3 = new HashMap<>(); //with size 1
//        HashMap<String, GAChange> group4 = new HashMap<>(); // with size 2
//        HashMap<String, GAChange> group5 = new HashMap<>(); // only for padding and margins
//
//        for (String elementId : marginElements.keySet()
//        ) {
//            GAElementToChange elementToChange = marginElements.get(elementId);
//            Node<DomNode> currNode = elementToChange.getNode();
//            // I have not created classes or modeled padding atts so right now I am just extracting the values from the element
//            Map<String, String> atts = currNode.getData().getAttributes();
//            for (String attName : atts.keySet()
//            ) {
//                if (attName.contains("margin")) {
//
//                    String currentValue = atts.get(attName);
//                    Double currentNumericalValue = 2.0; // default
//                    if (Utils.isValueNumerical(currentValue)) {
//
//                        currentNumericalValue = Util.getNumbersFromString(currentValue).get(0);
//                    }
//
//                    if (currentValue != null && currentNumericalValue > 0) {
//                        Padding padding = new Padding(attName);
//                        padding.setCurrentVal(currentNumericalValue);
//
//                        // group 1: decrease padding by 50%
//                        double calculateNewVal = Math.floor(currentNumericalValue * .50);
//                        if (calculateNewVal < 0) {
//                            calculateNewVal = 0;
//                        }
//                        String changeVal = calculateNewVal + "";
//                        String changeType = OwlConstants.CHANGE_DECREASE;
//                        GAChange change = new GAChange("margin", attName, changeType, changeVal, elementToChange, padding);
//                        elementToChange.addChange(attName, change);
//                        if (change != null) {
//                            group3.put(elementToChange.getId() + "#" + attName, change);
//                        }
//
//                        // group 2: decreae padding by 100%
//                        calculateNewVal = 0.0;
//
//                        changeVal = calculateNewVal + "";
//                        changeType = OwlConstants.CHANGE_DECREASE;
//                        change = new GAChange("margin", attName, changeType, changeVal, elementToChange, padding);
//                        elementToChange.addChange(attName, change);
//                        if (change != null) {
//                            group4.put(elementToChange.getId() + "#" + attName, change);
//                        }
//                        // group 3: increase padding by 70% (remaining is 0.30) and it is only this no height or width
//                        calculateNewVal = Math.floor(currentNumericalValue * .30);
//                        if (calculateNewVal < 0) {
//                            calculateNewVal = 0;
//                        }
//                        changeVal = calculateNewVal + "";
//                        changeType = OwlConstants.CHANGE_DECREASE;
//                        change = new GAChange("margin", attName, changeType, changeVal, elementToChange, padding);
//                        elementToChange.addChange(attName, change);
//                        if (change != null) {
//                            group5.put(elementToChange.getId() + "#" + attName, change);
//                        }
//
//
//                    }
//                }
//            }
//
//        }
//
//        HashMap<String, GAChange> group1 = groupsOfChanges.get("group1"); // these already added for size and SRG
//        HashMap<String, GAChange> group2 = groupsOfChanges.get("group2");
//
//        // we only add those if there is margin and padding because otherwise it is bascially the same as group 1 and 2
//        if (group3.size() > 0) {
//            group3.putAll(group1);
//            group4.putAll(group2);
//        }
//        if (group4.size() > 0) {
//            group4.putAll(group1);
//            group4.putAll(group2);
//        }
//
//        groupsOfChanges.get("group3").putAll(group3);
//        groupsOfChanges.get("group4").putAll(group4);
//        groupsOfChanges.get("group5").putAll(group5);
//
//    }
//
//    private void createGroupOfChangesForPaddingElements(HashMap<String, GAElementToChange> paddingElements,
//                                                        HashMap<String, HashMap<String, GAChange>> groupsOfChanges) {
//
//        // we start from group 3  see the main method for more details of the groups
//        HashMap<String, GAChange> group3 = new HashMap<>(); //with size 1
//        HashMap<String, GAChange> group4 = new HashMap<>(); // with size 2
//        HashMap<String, GAChange> group5 = new HashMap<>(); // only for padding and margins
//
//        for (String elementId : paddingElements.keySet()
//        ) {
//            GAElementToChange elementToChange = paddingElements.get(elementId);
//            Node<DomNode> currNode = elementToChange.getNode();
//            // I have not created classes or modeled padding atts so right now I am just extracting the values from the element
//            Map<String, String> atts = currNode.getData().getAttributes();
//            for (String attName : atts.keySet()
//            ) {
//                if (attName.contains("padding")) {
//
//                    String currentValue = atts.get(attName);
//                    Double currentNumericalValue = 2.0; // default
//                    if (Utils.isValueNumerical(currentValue)) {
//
//                        currentNumericalValue = Util.getNumbersFromString(currentValue).get(0);
//                    }
//
//                    if (currentValue != null && currentNumericalValue > 0) {
//                        Padding padding = new Padding(attName);
//                        padding.setCurrentVal(currentNumericalValue);
//
//                        // group 3: decrease padding by 50%
//                        double calculateNewVal = Math.floor(currentNumericalValue * .50);
//                        if (calculateNewVal < 0) {
//                            calculateNewVal = 0;
//                        }
//                        String changeVal = calculateNewVal + "";
//                        String changeType = OwlConstants.CHANGE_DECREASE;
//                        GAChange change = new GAChange("padding", attName, changeType, changeVal, elementToChange, padding);
//                        elementToChange.addChange(attName, change);
//                        if (change != null) {
//                            group3.put(elementToChange.getId() + "#" + attName, change);
//                        }
//
//                        // group 4: decrease padding by 100%
//                        calculateNewVal = 0.0;
//
//                        changeVal = calculateNewVal + "";
//                        changeType = OwlConstants.CHANGE_DECREASE;
//                        change = new GAChange("padding", attName, changeType, changeVal, elementToChange, padding);
//                        elementToChange.addChange(attName, change);
//                        if (change != null) {
//                            group4.put(elementToChange.getId() + "#" + attName, change);
//                        }
//                        // group 5: increase padding by 70% (remaining is 0.30) and it is only this no height or width
//                        calculateNewVal = Math.floor(currentNumericalValue * .30);
//                        if (calculateNewVal < 0) {
//                            calculateNewVal = 0;
//                        }
//                        changeVal = calculateNewVal + "";
//                        changeType = OwlConstants.CHANGE_DECREASE;
//                        change = new GAChange("padding", attName, changeType, changeVal, elementToChange, padding);
//                        elementToChange.addChange(attName, change);
//                        if (change != null) {
//                            group5.put(elementToChange.getId() + "#" + attName, change);
//                        }
//
//
//                    }
//                }
//            }
//
//        }
//        HashMap<String, GAChange> group1 = groupsOfChanges.get("group1"); // these already added for size and SRG
//        HashMap<String, GAChange> group2 = groupsOfChanges.get("group2");
//
//        // we only add those if there is margin and padding because otherwise it is bascially the same as group 1 and 2
//        if (group3.size() > 0) {
//            group3.putAll(group1);
//            group4.putAll(group2);
//        }
//        if (group4.size() > 0) {
//            group4.putAll(group1);
//            group4.putAll(group2);
//        }
//
//        groupsOfChanges.get("group3").putAll(group3);
//        groupsOfChanges.get("group4").putAll(group4);
//        groupsOfChanges.get("group5").putAll(group5);
//    }
//
//    private void createGroupOfChangesForSRGElements(HashMap<String, GAElementToChange> srgElements,
//                                                    HashMap<String, HashMap<String, GAChange>> groups) {
//
//        HashMap<String, GAChange> group1 = new HashMap<>();
//        HashMap<String, GAChange> group2 = new HashMap<>();
//        for (String problematicElementId : srgElements.keySet()) {
//            HashMap<String, Property> propertiesToChangeObject = new HashMap<>();
//            GAElementToChange element = srgElements.get(problematicElementId);
//            String directionFocus = element.getDirectionFocus();
//            Property sizeProp = null;
//            if (directionFocus.equalsIgnoreCase(Height.propertyName)) {
//                sizeProp = new Height();
//                String dynamicVal = Utils.getDynamicValueInDDP(element.getNode().getData(), Height.propertyName);
//                sizeProp.setCurrentDynamicVal(dynamicVal);
//                String staticVal = Utils.getValueFromElement(element.getNode().getData(), Height.propertyName);
//                sizeProp.setCurrentStaticVal(staticVal);
//                propertiesToChangeObject.put(Height.propertyName, sizeProp);
//            } else if (directionFocus.equalsIgnoreCase(Width.propertyName)) {
//                sizeProp = new Width();
//                String dynamicVal = Utils.getDynamicValueInDDP(element.getNode().getData(), Width.propertyName);
//                sizeProp.setCurrentDynamicVal(dynamicVal);
//                String staticVal = Utils.getValueFromElement(element.getNode().getData(), Width.propertyName);
//                sizeProp.setCurrentStaticVal(staticVal);
//                propertiesToChangeObject.put(Width.propertyName, sizeProp);
//            }
//
//
//            // gene group 1 for main size --> do not change how it is set (if it is fixed directly increase it or if it is constraints add Min)
//            GAChange change = createDoNotChangeSizeTypeGroup(element, sizeProp, srgElements, group1);
//            if (change != null) {
//                group1.put(element.getId(), change);
//            }
//
//            GAChange change2 = createChangeSizeTypeGroup(element, sizeProp, srgElements);
//            //change2=change;
//            if (change2 != null) {
//                group2.put(element.getId(), change2);
//            }
//
//
//        }
//        groups.get("group1").putAll(group1);
//        groups.get("group2").putAll(group2);
//
//
//    }
//
//    /***** main Size changes methods **********/
//
//    private GAChange createChangeSizeTypeGroup(GAElementToChange element, Property sizeProp, HashMap<String, GAElementToChange> problematicElements) {
//        // get the size attribute set type: fixed or constraints (ie wrap_content or match_parent)
//        GAChange change = null;
//        String staticVal = sizeProp.getCurrentStaticVal();
//        if (Utils.isValueNumerical(staticVal)) {
//            Double numericalStaticVal = null;
//            List<Double> x = Util.getNumbersFromString(staticVal);
//            if (x.size() > 0) {  // it is fixed so we should do wrap_content
//                numericalStaticVal = x.get(0);
//                String changeVal;
//                String changeType;
//                if (numericalStaticVal > 0) { // it is set as fixed value in the app so we change it to wrap_content
//                    changeVal = "-2";
//                    changeType = OwlConstants.CHANGE_INCREASE;
//                    change = new GAChange(sizeProp.getPropertyName(), OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(sizeProp.getPropertyName()), changeType, changeVal, element, sizeProp);
//                } else if (numericalStaticVal == -2) { // it is set as wrap_content or match_parent so we change it to fixed value
//                    String dynamicVal = sizeProp.getCurrentDynamicVal();
//                    if (dynamicVal != null) {
//                        List<Double> y = Util.getNumbersFromString(dynamicVal);
//                        if (y.size() > 0) {
//                            Double numericalDynamicVal = y.get(0);
//                            changeVal = String.valueOf(numericalDynamicVal + 20);
//                            changeType = OwlConstants.CHANGE_INCREASE;
//                            change = new GAChange(sizeProp.getPropertyName(), OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(sizeProp.getPropertyName()), changeType, changeVal, element, sizeProp);
//                        }
//                    }
//                }
//
//
//            }
//
//        } else { // it is an exceptional  case that I will handle later
//
//        }
//
//        return change;
//    }
//
//    private GAChange createDoNotChangeSizeTypeGroup(GAElementToChange element, Property sizeProp,
//                                                    HashMap<String, GAElementToChange> problematicElements,
//                                                    HashMap<String, GAChange> weightChanges) {
//        GAChange change = null;
//        String staticVal = sizeProp.getCurrentStaticVal();
//        if (Utils.isValueNumerical(staticVal)) {  //we are replacing wrap_content and match_parent with -2 and -1 in getValueFromElement method so basically we are always going to get numerical unless execptional cases
//            Double numericalStaticVal = null;
//            List<Double> x = Util.getNumbersFromString(staticVal);
//            if (x.size() > 0) {
//                numericalStaticVal = x.get(0);
//                String changeVal;
//                String changeType;
//                if (numericalStaticVal > 0) { // it is set as fixed value in the app
//                    double n = numericalStaticVal + 20;
//                    changeVal = n + "";
//                    changeType = OwlConstants.CHANGE_INCREASE;
//                    change = new GAChange(sizeProp.getPropertyName(), OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(sizeProp.getPropertyName()), changeType, changeVal, element, sizeProp);
//
//                } else if (numericalStaticVal == 0) { // it is set with weight  ToDo: How to handle this case? maybe we look for other type of changes not the layout_height
////                    handleWeightForMainSize(element, sizeProp.getPropertyName(), staticVal, sizeProp.getCurrentDynamicVal(), weightChanges);
//
//                } else if (numericalStaticVal == -1) { // it is set with match_parent
//                    // Should we go with min_height so we get dynamic height and add 10  as min_height and then leave the change to a parent in the SRG
//
//                    change = addMinChange2(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal());
//
//                } else if (numericalStaticVal == -2) { // it is set with wrap_content
//                    // since it is wrap_content we should go with min_hgight
////                                   changeVal = numericalStaticVal + 10 + "";
////                                   changeType = OwlConstants.Increase;
//
//                    change = addMinChange2(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal());
//                }
//            }
//
//
//        } else { // it is an exceptional  case that I will handle later
//
//        }
//
//        return change;
//    }
//
//    /******************************************************************************************************************************************************************* */
//    /*************************************************************************** HELPING METHODS *********************************************************************** */
//
//    /********************************************************************************************************************************************************************/
//
//
//    private Node<DomNode> checkIfThereIsAlongScrollableTextView(ArrayList<Node<DomNode>> problematicNodes) {
//        for (Node<DomNode> node : problematicNodes
//        ) {
//            String className = node.getData().getTagName();
//            String id = node.getData().getId();
//            if (className.equals("TextView")) {
//                String scrollbars = Utils.getValueFromElement(node.getData(), "scrollbars");
//                if (scrollbars != null) {
//                    // there is a scroll so we do not need to worry about cuttoff but maybe add lines
//                    Logger.debug("There is a scrollable text view");
//                    return node;
//                }
//
//
//            }
//        }
//        return null;
//    }
//
//    private boolean checkIfElementIsAlongScrollableTextView(Node<DomNode> node) {
//
//        String className = node.getData().getTagName();
//        String id = node.getData().getId();
//        if (className.equals("TextView")) {
//            String scrollbars = Utils.getValueFromElement(node.getData(), "scrollbars");
//            if (scrollbars != null) {
//                Logger.debug("There is a scrollable text view");
//                return true;
//            }
//
//
//        }
//
//        return false;
//    }
//
//
//}
//
