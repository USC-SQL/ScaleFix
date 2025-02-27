package usc.edu.OwlEye.GA;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import org.tinylog.Logger;
import usc.edu.OwlEye.AUCII.AUCIIssue;
import usc.edu.OwlEye.AUCII.Collision;
import usc.edu.OwlEye.AUCII.Cutoff;
import usc.edu.OwlEye.AUCII.Missing;
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

public class GADependencyAnalysisORG {

    private static GAChange addMinChange2(GAElementToChange elementToChange, String propName, String dynamicVal,
                                          String changeType,double changeAmountPercentage) {
        String changeVal;
        GAChange minChange = null;
        Property minProp = new MinHeight();
        String minProperty = MinHeight.propertyName;
        if (propName.equalsIgnoreCase(Width.propertyName)) {
            minProperty = MinWidth.propertyName;
            minProp = new MinWidth();
        }
        propName = minProperty;

        String miPropVal = Utils.getValueFromElement(elementToChange.getNode().getData(), minProperty);
        boolean minPropSet = false;
        if (miPropVal != null) {
            List<Double> y = Util.getNumbersFromString(miPropVal);
            if (y.size() > 0) { // val exists
                if (y.get(0) > 0) {
                    minPropSet = true;
                    Double numericalMinProp = y.get(0);
                    double newVal= Utils.changeCurrentValueByPercentage(numericalMinProp, changeAmountPercentage);

//                    double n = Math.floor(numericalMinProp + changeAmount);
//                    if(n<=0){
//                        n=1;
//                    }
                    changeVal = newVal + "";


                    minChange = new GAChange(propName, OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(propName), changeType, changeVal, elementToChange, minProp);
                    // elementToChange.addChange(propName, change);
                }
            }
        }

        if (!minPropSet) {
            //it does not exist so we add it by counting the dynamic height
            double newVal= Utils.changeCurrentValueByPercentage(Double.parseDouble(dynamicVal), changeAmountPercentage);

//            double n = Math.floor(Double.parseDouble(dynamicVal) + changeAmount);
//            if(n<=0){
//                n=1;
//            }
            changeVal = newVal + "";

            minChange = new GAChange(propName, OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(propName), changeType, changeVal, elementToChange, null);
        }
        return minChange;

    }
    private static GAChange addMaxDimensionChange(GAElementToChange elementToChange, String propName, String dynamicVal,
                                         String changeType,double changeAmountPercentage) {
        String changeVal;
        GAChange minChange = null;
        Property minProp = new Height();
        String maxProperty = MaxHeight.propertyName;
        if (propName.equalsIgnoreCase(Width.propertyName)) {
            minProp = new Width();
            maxProperty = MaxWidth.propertyName;

        }
        propName = maxProperty;

        String miPropVal = Utils.getValueFromElement(elementToChange.getNode().getData(), maxProperty);
        boolean minPropSet = false;
        if (miPropVal != null) {
            List<Double> y = Util.getNumbersFromString(miPropVal);
            if (y.size() > 0) { // val exists
                if (y.get(0) > 0) {
                    minPropSet = true;
                    Double numericalMinProp = y.get(0);
                    double newVal= Utils.changeCurrentValueByPercentage(numericalMinProp, changeAmountPercentage);

//                    double n = Math.floor(numericalMinProp + changeAmount);
//                    if(n<=0){
//                        n=1;
//                    }
                    changeVal = newVal + "";


                    minChange = new GAChange(propName, OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(propName), changeType, changeVal, elementToChange, minProp);
                    // elementToChange.addChange(propName, change);
                }
            }
        }

        if (!minPropSet) {
            //it does not exist so we add it by counting the dynamic height
            double newVal= Utils.changeCurrentValueByPercentage(Double.parseDouble(dynamicVal), changeAmountPercentage);

//            double n = Math.floor(Double.parseDouble(dynamicVal) + changeAmount);
//            if(n<=0){
//                n=1;
//            }
            changeVal = newVal + "";

            minChange = new GAChange(propName, OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(propName), changeType, changeVal, elementToChange, null);
        }
        return minChange;

    }
    private static GAChange generateMaxLinesChange(String propName, double currentMaxLine, double newMaxLine, GAElementToChange element, Property property) {

        String changeVal = newMaxLine + "";
        String changeType = OwlConstants.CHANGE_INCREASE;
        //changeType, changeVal,element,sizeProp);
        GAChange change3 = new GAChange(propName, OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(propName), changeType, changeVal, element, property);
        return change3;
    }


    /************************************************************ CUTOFF ************************************************************************************************/

    public HashMap<String, HashMap<String, GAChange>> CutOffDependencyAnalysis(HashMap<String, Cutoff> cutOffIssues) {


        HashMap<String, HashMap<String, HashMap<String, GAElementToChange>>> issuesToElements = new HashMap
                <String, HashMap<String, HashMap<String, GAElementToChange>>>(); // the fix set for each cutoff issue
        boolean december30Approach = true;
        HashMap<String, HashMap<String, HashMap<String, GAElementToChange>>> FixSetForCutOffIssues = new HashMap();

        for (String issue : cutOffIssues.keySet()) {
            Cutoff cutoff = cutOffIssues.get(issue);

            // (1) get the fix set based on problematic elements and the UI models
            //  HashMap<String, GAElementToChange> elementsToChange = GetFixSet(cutoff);
            // HashMap<String, GAElementToChange> elementsToChange = GetFixSet(cutoff);
            HashMap<String, HashMap<String, GAElementToChange>> setOfElementsToChange = GetFixSetForCutoff(cutoff);
            FixSetForCutOffIssues.put(issue, setOfElementsToChange);

        }

//
        HashMap<String, HashMap<String, GAElementToChange>> combinedCutOffFixedSet = combineFixSets(FixSetForCutOffIssues);
        Logger.debug("combinedCutoffFixedSet size: " + combinedCutOffFixedSet.size());
        HashMap<String, HashMap<String, GAChange>> groupsOfChanges = new HashMap<>();
        groupsOfChanges.put("group1", new HashMap<>());
        groupsOfChanges.put("group2", new HashMap<>());

        groupsOfChanges.put("group3", new HashMap<>());
        groupsOfChanges.put("group4", new HashMap<>());
        groupsOfChanges.put("group5", new HashMap<>());
        groupsOfChanges.put("group6", new HashMap<>());  // until 6 it is occupied by the dimensions group 1 and 2 plu text lines and ,maxline
        groupsOfChanges.put("group7", new HashMap<>());
        groupsOfChanges.put("group8", new HashMap<>());
        groupsOfChanges.put("group9", new HashMap<>());
        groupsOfChanges.put("group10", new HashMap<>());
        groupsOfChanges.put("group11", new HashMap<>());
        groupsOfChanges.put("group12", new HashMap<>());
        groupsOfChanges.put("group13", new HashMap<>());
        groupsOfChanges.put("group14", new HashMap<>());
        groupsOfChanges.put("group15", new HashMap<>());// for autosize
        groupsOfChanges.put("group16", new HashMap<>()); // for padding and margins
        groupsOfChanges.put("group17", new HashMap<>()); // for padding and margins
//                        groupsOfChanges.put("group3", new HashMap<>()); // only for padding and margins

        createGroupOfChangesCutoff(combinedCutOffFixedSet, FixSetForCutOffIssues, groupsOfChanges);

        return groupsOfChanges;


    }



    /*********************************************************************************************************************************************************************/

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




    private void createGroupOfChangesForConstraintLayout(HashMap<String, HashMap<String, GAElementToChange>> fixSetForCollision,
                                                      HashMap<String, HashMap<String, GAChange>> groupsOfChanges, HashMap<String, GAElementToChange> regSPLRG, Collision collision) {
        Logger.debug("Start of the Regular Layout changes method");
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
        if (prop.equalsIgnoreCase(Height.propertyName)) {
            // we look for top to bottom
            GAElementToChange n0 = regSPLRG.get(node0ID);
            GAElementToChange n1 =  regSPLRG.get(node1ID);
            if(n0==null || n1==null){
                return;
            }
//            SPLRGNode node0SPLRG = (SPLRGNode) n0.getGraphNode();
//            SPLRGNode node1SPLRG = (SPLRGNode) n1.getGraphNode();
//            String node0CurrentProp = node0SPLRG.getProperty();
//            String node1CurrentProp = node1SPLRG.getProperty();
            edge = layoutGraph.findEdge(node0, node1);
            if (edge.isTopBottom()) {
                //  GAChange change0 = new GAChange("layout_above", "android:layout_above", OwlConstants.CHANGE_CHANGE, node1IDWithPrefix,regSPLRG.get(node0ID));
                GAChange change1 = new GAChange("ns1:layout_constraintBottom_toTopOf", "ns1:layout_constraintBottom_toTopOf", OwlConstants.CHANGE_CHANGE, node1IDWithPrefix, regSPLRG.get(node0ID));
                //  groupsOfChanges.get("group1").put(node0ID,change0);
                groupsOfChanges.get("group1").put(node0ID, change1);
            }
        }


    }

    private void createGroupOfChangesForRegularLayout(HashMap<String, HashMap<String, GAElementToChange>> fixSetForCollision,
                                                      HashMap<String, HashMap<String, GAChange>> groupsOfChanges, HashMap<String, GAElementToChange> regSPLRG, Collision collision) {
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
    groupsOfChanges.get("group12").putAll(group1);  /** ONLY LAYOUT CHANGE FOR THIS GROUP **/




    }

    private void addElementToGroupOfChange(HashMap<String, GAChange> group, String id, GAChange change) {
        //ArrayList<GAChange> changes;
        String changeValue = change.getValue();
        String changeAttribute = change.getAttName();
        String changeID = id +"#"+  changeAttribute+"#"+changeValue;
        group.put(changeID,change);
//        if(group.get(id)==null){
//             changes = new ArrayList<>();
//            changes.add(change);
//            group.put(id,changes);
//        }
//        else {
//            changes = group.get(id);
//            changes.add(change);
////            change.addChange(.getChange(s));
//        }
    }


    /******************************************************** CUTOFF METHODS ****************************************************************************************/

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

    private GAChange[] createFourDimensionChanges(GAElementToChange element, Property sizeProp, double changeAmountPercentage) {
       double currentChangeAmountPercent = changeAmountPercentage;
        String changeType = OwlConstants.CHANGE_DECREASE;
        if(changeType.equalsIgnoreCase(OwlConstants.CHANGE_DECREASE)){
            currentChangeAmountPercent = -changeAmountPercentage;
        }
        GAChange change1 = createDoNotChangeSizeTypeGroupCollision(element, sizeProp, changeType,currentChangeAmountPercent);

        ArrayList<GAChange>  changes = createChangeSizeTypeGroupCollision(element, sizeProp, changeType,currentChangeAmountPercent);
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



    /****************************************************************************************************************************************************************/

    private HashMap<String, HashMap<String, GAElementToChange>> GetFixSetForCutoff(Cutoff cutoff) {
        // Find the elements that we may need to change to fix the problem
        HashMap<String, HashMap<String, GAElementToChange>> elementsToChange = new HashMap<>(); //string: id or xpath of the element


        // 1- The problematic element
        Node<DomNode> problematicElement = cutoff.getProblematicElements().get(0); // this is cutoff so it is always one element
        elementsToChange.put("problematic", new HashMap<>());
        elementsToChange.put("wrg", new HashMap<>());
        elementsToChange.put("srg", new HashMap<>());
        elementsToChange.put("vsrg", new HashMap<>());
        elementsToChange.put("padding", new HashMap<>()); // we add all elements that sibiling and parent of the problematic element and have padding (including the problematic element itself)
        elementsToChange.put("margin", new HashMap<>());
        // size dependent elements (parent)
        String prop = Height.propertyName;
        if (cutoff.getIssueType().equalsIgnoreCase("width")) {
            prop = Width.propertyName;
            // this has to be done later
        }
        GAElementToChange problematicElementToChange = new GAElementToChange(problematicElement, "problematic", OwlConstants.CHANGE_INCREASE, prop); // usually we know it is increase fo the problematic element
        elementsToChange.get("problematic").put(problematicElement.getData().getId(), problematicElementToChange);

//

        // Now that we got the very basic which is the problematic element.
        // Now we refer to the UI models to find other elements that we may need to change and we keep each seperate so we can decide later how to change.
        boolean checkWRG = true;
        boolean checkSRG = true;
        boolean checkVSRG = true;
        boolean checkPadding = true;
        boolean checkMargin = true;
        boolean checkLayout = false;
        // (1) elements based on SRG

        if (checkWRG) {
            findImpactedElementsBasedWRG(cutoff, elementsToChange.get("wrg"), problematicElement, prop);
        }
        if (checkSRG) {
            findImpactedElementsBasedOnSRG(cutoff, elementsToChange.get("srg"), problematicElement, prop);


        }

        if (checkVSRG) {
            // (2) elements based on VSRG
            findImpactedElementsBasedOnVSRG(cutoff, elementsToChange.get("vsrg"), problematicElement, prop);
        }
        if (checkPadding) {
            // (3) elements based on padding
            findImpactedElementsBasedOnPadding(cutoff, elementsToChange.get("padding"), problematicElement, prop);
        }
        if (checkMargin) {
            // (4) elements based on margin
            findImpactedElementsBasedOnMarginCutoff(cutoff, elementsToChange.get("margin"), problematicElement, prop);
        }
        if (checkLayout) {
            findImpactedElementsBasedOnLayout(cutoff, elementsToChange.get("layout"), problematicElement);
        }


        // 3- Other elements that we may need to change?
//System.out.println("SYSTEM EXIS INSIDE GET FIX SET METHOD");
//        System.exit(1);
        return elementsToChange;
    }

    private HashMap<String, HashMap<String, GAElementToChange>> combineFixSets(HashMap<String, HashMap<String, HashMap<String, GAElementToChange>>> fixSetForCutOffIssues) {
        HashMap<String, HashMap<String, GAElementToChange>> combinedFixSet = new HashMap<>(); //string: id or xpath of the element

//        combinedFixSet.put("problematic", new HashMap<>());
//        combinedFixSet.put("srg", new HashMap<>());
//        combinedFixSet.put("vsrg", new HashMap<>());
//        combinedFixSet.put("padding", new HashMap<>()); // we add all elements that sibiling and parent of the problematic element and have padding (including the problematic element itself)
//        combinedFixSet.put("margin", new HashMap<>());
        HashMap<String, GAElementToChange> problematic = new HashMap<>();
        for (String issue : fixSetForCutOffIssues.keySet()
        ) {
            HashMap<String, GAElementToChange> problematicForIssue = fixSetForCutOffIssues.get(issue).get("problematic");
            problematic.putAll(problematicForIssue);
            combinedFixSet.put("problematic", problematic);
        }
        // 2- extracting all the SRG elements
        HashMap<String, GAElementToChange> srg = new HashMap<>();
        for (String issue : fixSetForCutOffIssues.keySet()
        ) {
            HashMap<String, GAElementToChange> srgForIssue = fixSetForCutOffIssues.get(issue).get("srg");
            srg.putAll(srgForIssue);
            combinedFixSet.put("srg", srg);
        }
        // 3- extracting all vsrg elements
        HashMap<String, GAElementToChange> vsrg = new HashMap<>();
        for (String issue : fixSetForCutOffIssues.keySet()
        ) {
            HashMap<String, GAElementToChange> vsrgForIssue = fixSetForCutOffIssues.get(issue).get("vsrg");
            vsrg.putAll(vsrgForIssue);
            combinedFixSet.put("vsrg", vsrg);
        }

        // 4- extracting all padding elements
        HashMap<String, GAElementToChange> padding = new HashMap<>();
        for (String issue : fixSetForCutOffIssues.keySet()
        ) {
            HashMap<String, GAElementToChange> paddingForIssue = fixSetForCutOffIssues.get(issue).get("padding");
            padding.putAll(paddingForIssue);
            combinedFixSet.put("padding", padding);
        }

        // 5- extracting all margin elements
        HashMap<String, GAElementToChange> margin = new HashMap<>();
        for (String issue : fixSetForCutOffIssues.keySet()
        ) {
            HashMap<String, GAElementToChange> marginForIssue = fixSetForCutOffIssues.get(issue).get("margin");
            margin.putAll(marginForIssue);
            combinedFixSet.put("margin", margin);
        }


        // 5- extracting all weight elements
        HashMap<String, GAElementToChange> weight = new HashMap<>();
      boolean newApproach=true;  // April 14
        for (String issue : fixSetForCutOffIssues.keySet()
        ) {

            HashMap<String, GAElementToChange> weightForIssue = fixSetForCutOffIssues.get(issue).get("wrg");
            // iterate over the weight elements and add them to the combined fix set one by one after checking if they are already in the combined fix set
         if(newApproach) {
             System.out.println("issue is " + issue);
             for (String id : weightForIssue.keySet()
             ) {
                 GAElementToChange curr_w = weightForIssue.get(id);
                 String typeOfChange = curr_w.getTypeOfChange();
                    System.out.println("\ttype of change is " + typeOfChange);
                 if (typeOfChange.equalsIgnoreCase(OwlConstants.CHANGE_DECREASE) && weight.containsKey(id)) {
                     System.out.println("WEIGHT ELEMENT IS ALREADY IN THE COMBINED FIX SET");
                     continue;
                 } else {
                     weight.put(id, curr_w);
                 }

             }
         }
         else {
             weight.putAll(weightForIssue);
         }
            combinedFixSet.put("wrg", weight);
        }

        return combinedFixSet;
    }

    private HashMap<String, HashMap<String, GAChange>> createGroupOfChangesCutoff
            (HashMap<String, HashMap<String, GAElementToChange>> combinedCutOffFixedSet,
             HashMap<String, HashMap<String, HashMap<String, GAElementToChange>>> fixSetForCutOffIssues,
             HashMap<String, HashMap<String, GAChange>> groupsOfChanges) {

        boolean includeWRG = true;
        boolean includeSRG = true;
        boolean includeVSrg = false;
        boolean includePadding = true;
        boolean includeMargin = true;

        HashMap<String, GAElementToChange> problematicElements = combinedCutOffFixedSet.get("problematic");
        createGroupOfChangesForProblematicElement(problematicElements, groupsOfChanges);


        if (includeWRG) {
            HashMap<String, GAElementToChange> wrgElements = combinedCutOffFixedSet.get("wrg");
            createGroupOfChangesForWRG(wrgElements, groupsOfChanges);
        }
        if (includeSRG) {
            HashMap<String, GAElementToChange> srgElements = combinedCutOffFixedSet.get("srg");
            createGroupOfChangesForSRGElements(srgElements, groupsOfChanges);
        }
        if (groupsOfChanges.get("group1").size() < 1) {
            Logger.debug("group1 is null");

        }

        if (includeVSrg) {
            HashMap<String, GAElementToChange> vsrgElements = combinedCutOffFixedSet.get("vsrg");

            if (vsrgElements != null && vsrgElements.size() > 0) {
                createGroupOfChangesForVSRGElementsCollisions(vsrgElements,problematicElements, groupsOfChanges);
            }
        }

        if (includeMargin) {
            HashMap<String, GAElementToChange> marginElements = combinedCutOffFixedSet.get("margin"); // we should start from group 7 as the first six groups are for the problematic element
            createGroupOfChangesForMarginElementsCutoff(marginElements, groupsOfChanges);
        }
        Logger.debug("Group of Changes size: " + groupsOfChanges.size());
        for (String key : groupsOfChanges.keySet()
        ) {
            HashMap<String, GAChange> group = groupsOfChanges.get(key);
            for (String key2 : group.keySet()
            ) {
                GAChange change = group.get(key2);
                if (change != null) {
                    Logger.debug("Change of " + key2 + ": " + change.getPropertyName() + " | val= " + change.getValue());
                }
            }

        }

        if (includePadding) {
            HashMap<String, GAElementToChange> paddingElements = combinedCutOffFixedSet.get("padding");
            createGroupOfChangesForPaddingElementsCutoff(paddingElements, groupsOfChanges);
        }


        return groupsOfChanges;

    }

    private void findImpactedElementsBasedWRG(Cutoff cutoff, HashMap<String, GAElementToChange> wrgElementsToChange, Node<DomNode> problematicElement,
                                              String prop) {
        // get the WRG
        TreeMap<String, List<WRGNode>> propWRG = OwlEye.getOriginalDefaultUIWRG().getDependentNodesMap().get(prop);
        String problematicElementID = problematicElement.getData().getId();
        String parentXpath = problematicElement.getParent().getData().getxPath();
        List<WRGNode> parentNodeWRG = propWRG.descendingMap().get(parentXpath);
        if (parentNodeWRG == null) {
            return;
        }
        double parentTotalWeight;
        for (WRGNode wrgNode : parentNodeWRG) {
            parentTotalWeight = wrgNode.getParentTotal();
            if (wrgNode.getXmlNode().getData().getId().equals(problematicElementID)) {
                Logger.debug("Found the problematic element in the WRG || INCREASE WEIGHT");
                // problematic element for cuttoff  -- > increase the weight
                GAElementToChange gaElementToChange = new GAElementToChange(wrgNode.getXmlNode(), "wrg", OwlConstants.CHANGE_INCREASE, prop, wrgNode);
                wrgElementsToChange.put(wrgNode.getXmlNode().getData().getId(), gaElementToChange);


            } else {
                // NOT problematic element for cuttoff  -- > decrease
                GAElementToChange gaElementToChange = new GAElementToChange(wrgNode.getXmlNode(), "wrg", OwlConstants.CHANGE_DECREASE, prop, wrgNode);
                // April 14, check if it already exists as increase (in case of multiple cutoffs)
                wrgElementsToChange.put(wrgNode.getXmlNode().getData().getId(), gaElementToChange);
                Logger.debug("Found other element in the WRG || DECREASE WEIGHT");
            }

//            GAElementToChange elementToChangeGA = new GAElementToChange(elementToChange, "wrg", OwlConstants.CHANGE_INCREASE, prop);
//            wrgElementsToChange.put(elementToChange.getData().getId(), elementToChangeGA);
//
        }


    }

    private void findImpactedElementsBasedOnSRG(Cutoff cutoff, HashMap<String, GAElementToChange> elementsToChange, Node<DomNode> problematicElement, String prop) {

//        String prop = Height.propertyName;
//        if (cutoff.getIssueType().equalsIgnoreCase("vertical")) {
//            prop = Width.propertyName;
//            // this has to be done later
//        }


        SRG srg = OwlEye.getOriginalDefaultUISRG();
        String xPath = problematicElement.getData().getxPath();
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

    private void findImpactedElementsBasedOnMarginCutoff(AUCIIssue AUCIIssue, HashMap<String, GAElementToChange> margin, Node<DomNode> problematicElement,
                                                         String changeDirection) {
        // for margins we need to check the margins of the element itself and the margins of the parent (I am not going to checkthe margins of the sibilings for now)
        Logger.trace("Cutoff findImpactedElementsBasedOnMargins");
        ArrayList<Node<DomNode>> nodesToConsiderMargin = new ArrayList<>();
        Map<String, String> marginAttributesToFind;

        if (AUCIIssue.getIssueType().equals("height".toLowerCase())) {
            marginAttributesToFind = OwlConstants.MARGINS_FOR_HORIZONTAL_CUTOFF_ISSUES;
        } else {
            marginAttributesToFind = OwlConstants.MARGINS_FOR_VERTICAL_CUTOFF_ISSUES;
        }

        Node<DomNode> firstParent = problematicElement.getParent();
        if (firstParent != null) {
            List<Node<DomNode>> siblings = firstParent.getChildren();
            if (siblings.size() > 0) {
                nodesToConsiderMargin.addAll(siblings);
            }

        }
        Node<DomNode> parent = problematicElement.getParent();
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


        nodesToConsiderMargin.add(problematicElement);

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

    private void findImpactedElementsBasedOnPadding(Cutoff cutoff, HashMap<String, GAElementToChange> padding, Node<DomNode> problematicElement, String changeDirection) {
        // We check the padding of the problematic element and its siblings and if they have padding we add them to the list of elements to change
        Logger.trace("Cutoff findImpactedElementsBasedOnPadding");
        Map<String, String> paddingAttributesToFind;
        if (cutoff.getIssueType().equals("height".toLowerCase())) {
            paddingAttributesToFind = OwlConstants.PADDING_FOR_HORIZONTAL_CUTOFF_ISSUES;
        } else {
            paddingAttributesToFind = OwlConstants.PADDING_FOR_VERTICAL_CUTOFF_ISSUES;
        }

        ArrayList<Node<DomNode>> nodesToConsiderPadding = new ArrayList<>();
        Node<DomNode> parent = problematicElement.getParent();
        if (parent != null) {
            nodesToConsiderPadding.add(parent); // Now I add the parent for infini app just testing
        }
        List<Node<DomNode>> siblings = parent.getChildren();
        if (siblings.size() > 0) {
            nodesToConsiderPadding.addAll(siblings);
        }

        for (Node<DomNode> node : nodesToConsiderPadding
        ) {
            Map<String, String> attributes = node.getData().getAttributes();
            for (String key : attributes.keySet()
            ) {
                if (key.contains("padding")) {
                    String att = key;
                    if (paddingAttributesToFind.containsKey(att)) {
                        String value = attributes.get(key);
                        Double paddingValue = Util.getNumbersFromString(value).get(0);
                        if (paddingValue > 0) {
                            Logger.trace("Padding Attribute: " + att + " Value: " + value);

                            GAElementToChange elementToChange = new GAElementToChange(node, "padding", OwlConstants.CHANGE_DECREASE, changeDirection);
                            padding.put(node.getData().getId(), elementToChange);

                        }
                    }
//                    String value = attributes.get(key);
//                    Double paddingValue = Util.getNumbersFromString(value).get(0);
//                    if (paddingValue > 0) {
//                        Logger.trace("Padding Attribute: " + att + " Value: " + value);
//                        if (att.contains("padding")) {
//                            GAElementToChange elementToChange = new GAElementToChange(node, "padding", OwlConstants.Decrease, changeDirection);
//                            padding.put(node.getData().getId(), elementToChange);
//                        }
//                    }
                }
            }
        }


    }

    private void findImpactedElementsBasedOnVSRG(Cutoff cutoff, HashMap<String, GAElementToChange> elementsToChange, Node<DomNode> problematicElement, String prop) {
        VSRG vsrg = OwlEye.getOriginalDefaultUIVSRG();
        String xPath = problematicElement.getData().getxPath(); // ToDo: do we need to get the SRG elements based on other SRG elements?? so if we get a node that is size dependent on a problematic element do we also seacrch for nodes that this new nodd depends on?
        List<VSRGNode> visualDependentElements = vsrg.getDependentNodesMap().get(xPath);
        if (visualDependentElements != null && visualDependentElements.size() > 0) {
            for (VSRGNode visualNode : visualDependentElements
            ) {
                Node<DomNode> node = visualNode.getXmlNode();
                elementsToChange.put(node.getData().getId(), new GAElementToChange(node, "VSRG", OwlConstants.CHANGE_INCREASE, prop)); // we also assume that the

            }
        }

    }

    private void findImpactedElementsBasedOnLayout(Cutoff cutoff, HashMap<String, GAElementToChange> elementsToChange, Node<DomNode> problematicElement) {
    }

    /**** creating groups ******/


    private void createGroupOfChangesForProblematicElement(HashMap<String, GAElementToChange> problematicElements, HashMap<String, HashMap<String, GAChange>> groupsOfChanges) {
        HashMap<String, HashMap<String, GAChange>> groups = new HashMap<>();
        HashMap<String, GAChange> group1 = new HashMap<>(); // height not change
        HashMap<String, GAChange> group2 = new HashMap<>(); //height change
        HashMap<String, GAChange> group3 = new HashMap<>(); // +1 line alone
        HashMap<String, GAChange> group4 = new HashMap<>(); // +2 line alone
        HashMap<String, GAChange> group5 = new HashMap<>(); // height1 +  +1 line alone
        HashMap<String, GAChange> group6 = new HashMap<>(); // height2 +  +1 line alone
        HashMap<String, GAChange> group15 = new HashMap<>(); // autosizeAlone
        HashMap<String, GAChange> group16 = new HashMap<>(); // autosize + height not change
        HashMap<String, GAChange> group17 = new HashMap<>(); // autosize + height change
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


            double changeAmountPercentage = 20;
            String changeType = OwlConstants.CHANGE_INCREASE;
            // gene group 1 for main size --> do not change how it is set (if it is fixed directly increase it or if it is constraints add Min)
            GAChange change1 = createDoNotChangeSizeTypeGroupCutOff(element, sizeProp, changeType, changeAmountPercentage);
            // GAChange change = createDoNotChangeSizeTypeGroup(element, sizeProp,changeType);
            if (change1 != null) {
                //  group1.put(element.getId(), change);
                addElementToGroupOfChange(group1, element.getId(), change1);

            }
            ArrayList<GAChange> changes = createChangeSizeTypeGroupCutoff(element, sizeProp, changeType, changeAmountPercentage);

            if (changes != null && changes.size() > 0) {
                for (GAChange change2 : changes) {
                    if (change2 != null)
                        addElementToGroupOfChange(group2, element.getId(), change2);

                }
            }
//            ArrayList<GAChange> changeArr = createChangeSizeTypeGroup(element, sizeProp,changeType);
//            // GAChange change2=change;   // Activate this if  we are not considering the case where the size is set to wrap content for so I am adding group 1 to group 2
//            if (changeArr != null) {
//                for (GAChange change2 : changeArr) {
//                 if(change2!=null)
//                    group2.put(element.getId(), change2);
//                }
//            }

//            }
            // gene group 2 for main size --> change how it was set --> if it is fixed change it to wrap_content with minWidth = the dynamic value + 10  or if it is constraints changes to to fixed and increase it

            // for each group add the text attribute once and once do not add it at all ( no change to text attributes) || if no text attributes defined then we just have 1 and 2
            // gene group 1-1 (group 1 above + text attribute changed )
            // gene group 1-2 (group 1 above + text attributes deleted if it exists)
            // gene group 2-1 (group 2 above + text attribute changed )
            // gene group 2-2 (group 2 above + text attributes deleted if it exists)


            // Now regardless of the direction focus we should check the text attributes
            // if the node is in TRG then we should add those attributes to the properties to change
            TRG trg = OwlEye.getOriginalDefaultUITRG();
            if (trg.getDependentNodesMap().containsKey(element.getNode().getData().getId())) {
                // This means that the node is in TRG
                TRGNode trgNode = trg.getDependentNodesMap().get(element.getNode().getData().getId());
                Logger.trace("The node is in TRG: " + trgNode.getXpath());
                HashMap<String, Property> textAtt = trgNode.getTextAttributes();
                for (Map.Entry<String, Property> entry : textAtt.entrySet()) {
                    String key = entry.getKey();
                    Property value = entry.getValue();
                    Logger.trace("The key is " + key + " and the value is " + value);
                    boolean skipMaxLine = true;
                    switch (key) {
                        case MaxLine.propertyName: {
                            if (skipMaxLine) {
                                continue;
                            }
                            Logger.trace("MaxLine");
                            MaxLine maxLine = (MaxLine) value;
                            int currentMaxLine = maxLine.getCurrentVal();
                            int newMaxLine = currentMaxLine + 1; // Group 1 will have + 1 and group 2 will have + 2
                            GAChange change3 = generateMaxLinesChange(key, currentMaxLine, newMaxLine, element, maxLine);
                            propertiesToChangeObject.put(key, (MaxLine) value);
                            if (change3 != null) {
                                addElementToGroupOfChange(group3, element.getId(), change3); // alone

                                if (group1.size() > 0) { // only add if there is height
                                    group4.putAll(group1);
                                    addElementToGroupOfChange(group4, element.getId(), change3); // with height 1

                                }
                                if (group2.size() > 0) { // only add if there is height2
                                    group5.putAll(group2);
                                    addElementToGroupOfChange(group5, element.getId(), change3); // with height 1

                                }
                                //group1.put(element.getId(), change3);
                            }
                            newMaxLine = currentMaxLine + 2; // Group 2 will have + 2 by itself only
                            GAChange change4 = generateMaxLinesChange(key, currentMaxLine, newMaxLine, element, maxLine);
                            propertiesToChangeObject.put(key, (MaxLine) value);
                            if (change4 != null) {
                                addElementToGroupOfChange(group6, element.getId(), change4); // alone
                            }

                            break;
                        }
                        case Lines.propertyName: {

                            Logger.trace("Lines");
                            Lines line = (Lines) value;
                            double currentLine = line.getCurrentVal();
                            double newLine = currentLine + 1; // Group 1 will have + 1 and group 2 will have + 2
                            GAChange change3 = generateMaxLinesChange(key, currentLine, newLine, element, line);
                            propertiesToChangeObject.put(key, (Lines) value);
                            if (change3 != null) { // by itself
                                addElementToGroupOfChange(group3, element.getId(), change3); // alone
                                if (group1.size() > 0) { // only add if there is height
                                    group4.putAll(group1);
                                    addElementToGroupOfChange(group4, element.getId(), change3); // with height 1

                                }
                                if (group2.size() > 0) { // only add if there is height2
                                    group5.putAll(group2);
                                    addElementToGroupOfChange(group5, element.getId(), change3); // with height 1

                                }
                            }
                            newLine = currentLine + 2; // Group 2 will have + 2
                            GAChange change4 = generateMaxLinesChange(key, currentLine, newLine, element, line);
                            propertiesToChangeObject.put(key, (Lines) value);
                            if (change4 != null) {
                                addElementToGroupOfChange(group6, element.getId(), change4); // alone

                            }

                            break;
                        }
                    }

//                if (key.equalsIgnoreCase(MaxLine.propertyName)) {
//                    MaxLine maxLine = new MaxLine();
//                    propertiesToChangeObject.put(key, maxLine);
//                } else if (key.equalsIgnoreCase(Lines.propertyName)) {
//                    Lines lines = new Lines();
//                    propertiesToChangeObject.put(key, lines);
//                }


                }

            }

            boolean includeAutoText = true;
            if (includeAutoText) {
                addAutoTextProperty(group15, element);

                if (group1 != null && group1.size() > 0) {
                    group16.putAll(group1);
                    addAutoTextProperty(group16, element);
                }
                if (group2 != null && group2.size() > 0) {
                    group17.putAll(group2);
                    addAutoTextProperty(group17, element);
                }

            }
        }

        if(group1!=null && group1.size()>0) {

        }
        groupsOfChanges.get("group1").putAll(group1);
        groupsOfChanges.get("group2").putAll(group2);
        groupsOfChanges.get("group3").putAll(group3);
        groupsOfChanges.get("group4").putAll(group4);
        groupsOfChanges.get("group5").putAll(group5);
        groupsOfChanges.get("group6").putAll(group6);
        groupsOfChanges.get("group15").putAll(group15);
        groupsOfChanges.get("group16").putAll(group16);
        groupsOfChanges.get("group17").putAll(group17);
//        groups.put("group1", group1);
//        groups.put("group2", group2);
        // return groups;
    }

    private void addAutoTextProperty(HashMap<String, GAChange> group, GAElementToChange element) {
        AutoText autoText = new AutoText()  ;
        GAChange autoChange = new GAChange(AutoText.propertyName, OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(AutoText.propertyName) ,
                OwlConstants.CHANGE_CHANGE, "uniform", element, autoText);
        addElementToGroupOfChange(group, element.getId(), autoChange);
    }

    private void createGroupOfChangesForWRGCollision(HashMap<String, GAElementToChange> wrgElements, HashMap<String, HashMap<String, GAChange>> groupsOfChanges) {

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

    private void createGroupOfChangesForWRG(HashMap<String, GAElementToChange> wrgElements, HashMap<String, HashMap<String, GAChange>> groupsOfChanges) {

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

            double generalDecreasePercentage = 0.3;
            double imageViewDecreasePercentage = 0.65;
            ArrayList<GAElementToChange> decreaseChildren = children.get(OwlConstants.CHANGE_DECREASE);
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
//            double weightForEachIncrease = Math.floor((parentTotal - decreaseWeightSoFar) / sizeOfIncrease);
            double weightForEachIncrease = (parentTotal - decreaseWeightSoFar) / sizeOfIncrease;

            for (GAElementToChange element : increaseChildren) {
                WRGNode wrgNode = (WRGNode) (element.getGraphNode());
                // get the current weight of the node
                double currentWeight = wrgNode.getNumerator();
                String property = wrgNode.getProperty();
                Property propertyObject = wrgNode.getPropertyObj();
               // double newWeight = Math.ceil(currentWeight + (currentWeight * 0.5));
                double newWeight =currentWeight + (currentWeight * 0.5);

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



        }
        groupsOfChanges.get("group1").putAll(group1);
        Logger.debug("Done ");

    }

    private HashMap<String, HashMap<String, ArrayList<GAElementToChange>>> findGroupsOfSiblingsForWRGFixSet(HashMap<String, GAElementToChange> wrgElements) {
        HashMap<String, HashMap<String, ArrayList<GAElementToChange>>> parentsWRG = new HashMap<>(); // seperate increase and decrease
        for (Map.Entry<String, GAElementToChange> entry : wrgElements.entrySet()
        ) {
            String elementID = entry.getKey();
            GAElementToChange element = entry.getValue();
            WRGNode wrgNode = ((WRGNode) element.getGraphNode());
            String parentXpath = wrgNode.getParentXpath();
            double parentTotal = wrgNode.getParentTotal();
            if (parentsWRG.get(parentXpath + "#" + parentTotal) == null) {
                HashMap<String, ArrayList<GAElementToChange>> childrenWithWeight = new HashMap<>();
                childrenWithWeight.put(OwlConstants.CHANGE_INCREASE, new ArrayList<>());
                childrenWithWeight.put(OwlConstants.CHANGE_DECREASE, new ArrayList<>());
                childrenWithWeight.get(element.getTypeOfChange()).add(element); // weather it is OwlConstants.CHANGE_INCREASE or OwlConstants.CHANGE_DECREASE
                parentsWRG.put(parentXpath + "#" + parentTotal, childrenWithWeight);
            } else {
                parentsWRG.get(parentXpath + "#" + parentTotal).get(element.getTypeOfChange()).add(element);
//             parentsWRG.get(element.getTypeOfChange()).get(parentXpath+"#"+parentTotal,childrenWithWeight);

            }
        }
        return parentsWRG;
    }
    private void createGroupOfChangesForMarginElementsMissing(HashMap<String, GAElementToChange> marginElements,
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
                        calculateNewVal = 0.0;

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
                        Margin margin = new Margin(attName);
                        margin.setCurrentVal(currentNumericalValue);

                        // group 1: decrease padding by 50%
                        double calculateNewVal = Math.floor(currentNumericalValue * .50);
                        if (calculateNewVal < 0) {
                            calculateNewVal = 0;
                        }
                        String changeVal = calculateNewVal + "";
                        String changeType = OwlConstants.CHANGE_DECREASE;
                        GAChange change = new GAChange("padding", attName, changeType, changeVal, elementToChange, margin);
                        elementToChange.addChange(attName, change);
                        if (change != null) {
                            addElementToGroupOfChange( groupM1, elementToChange.getId() , change);

                            // group3.put(elementToChange.getId() + "#" + attName, change);
                        }

                        // group 2: decreae padding by 100%
                        calculateNewVal = 0.0;

                        changeVal = calculateNewVal + "";
                        changeType = OwlConstants.CHANGE_DECREASE;
                        change = new GAChange("padding", attName, changeType, changeVal, elementToChange, margin);
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
                        change = new GAChange("padding", attName, changeType, changeVal, elementToChange, margin);
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
                        calculateNewVal = 0.0;

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

        groupsOfChanges.get("group3").putAll(groupM1);  // pure margins 50%
        groupsOfChanges.get("group4").putAll(groupM2);  // pure margins 0%
        groupsOfChanges.get("group5").putAll(groupM3);// pure margins 30%

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
            groupM7.putAll(group1);
            groupM8.putAll(group2);
            groupM9.putAll(group3);
            groupM10.putAll(group4);
        }
        if (group4.size() > 0) {
            group4.putAll(group1);
            group4.putAll(group2);
        }


        groupsOfChanges.get("group5").putAll(groupM2);
        groupsOfChanges.get("group6").putAll(groupM3);
        groupsOfChanges.get("group7").putAll(groupM4);
        groupsOfChanges.get("group8").putAll(groupM5);
        groupsOfChanges.get("group9").putAll(groupM6);
        groupsOfChanges.get("group10").putAll(groupM7);
        groupsOfChanges.get("group11").putAll(groupM8);
        groupsOfChanges.get("group12").putAll(groupM9);
        groupsOfChanges.get("group13").putAll(groupM10);
        groupsOfChanges.get("group14").putAll(groupM11);



    }


//    private void createGroupOfChangesForMarginElementsMissing(HashMap<String, GAElementToChange> marginElements,
//                                                             HashMap<String, HashMap<String, GAChange>> groupsOfChanges) {
//
//        // we start from group 3  see the main method for more details of the groups
//        HashMap<String, GAChange> group3 = new HashMap<>(); //with size 1
//        HashMap<String, GAChange> group4 = new HashMap<>(); // with size 2
//        HashMap<String, GAChange> group5 = new HashMap<>(); // only for padding and margins
//
//        for (String elementId : marginElements.keySet()
//        ) {
//            GAElementToChange elementToChange = marginElements.get(elementId);
//            String changeDirection=elementToChange.getDirectionFocus();
//            Node<DomNode> currNode = elementToChange.getNode();
//            Map<String, String> marginAttributesToFind;
//            if (changeDirection.equalsIgnoreCase(Width.propertyName)) {
//                marginAttributesToFind = OwlConstants.MARGINS_FOR_VERTICAL_CUTOFF_ISSUES;
//
//            } else {
//                marginAttributesToFind = OwlConstants.MARGINS_FOR_HORIZONTAL_CUTOFF_ISSUES;
//            }
//            // I have not created classes or modeled padding atts so right now I am just extracting the values from the element
//            Map<String, String> atts = currNode.getData().getAttributes();
//            for (String attName : atts.keySet()
//            ) {
//                if(marginAttributesToFind.containsKey(attName)){
//                    //         if (attName.contains("margin")) {
//
//                    String currentValue = atts.get(attName);
//                    Double currentNumericalValue = 2.0; // default
//                    if (Utils.isValueNumerical(currentValue)) {
//
//                        currentNumericalValue = Util.getNumbersFromString(currentValue).get(0);
//                    }
//
//                    if (currentValue != null && currentNumericalValue > 0) {
//                        Margin margin = new Margin(attName);
//                        margin.setCurrentVal(currentNumericalValue);
//
//                        // group 1: decrease padding by 50%
//                        double calculateNewVal = Math.floor(currentNumericalValue * .50);
//                        if (calculateNewVal < 0) {
//                            calculateNewVal = 0;
//                        }
//                        String changeVal = calculateNewVal + "";
//                        String changeType = OwlConstants.CHANGE_DECREASE;
//                        GAChange change = new GAChange("margin", attName, changeType, changeVal, elementToChange, margin);
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
//                        change = new GAChange("margin", attName, changeType, changeVal, elementToChange, margin);
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
//                        change = new GAChange("margin", attName, changeType, changeVal, elementToChange, margin);
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

    private void createGroupOfChangesForMarginElementsCutoff(HashMap<String, GAElementToChange> marginElements,
                                                             HashMap<String, HashMap<String, GAChange>> groupsOfChanges) {

        // unlike collisions and missing, we only have two groups for marigns: 60% (40 remaining) off and 40% (60 remaining) off and 70% (30 remaining) off
        HashMap<String, GAChange> group1 = new HashMap<>(); // margin 60% off alone
        HashMap<String, GAChange> group2 = new HashMap<>(); // margin 40% off alone
        HashMap<String, GAChange> group3 = new HashMap<>(); // margin 70% off alone
        HashMap<String, GAChange> group4 = new HashMap<>(); // margin 60% off alone with height 1
        HashMap<String, GAChange> group5 = new HashMap<>(); // margin 40% off alone with height 1
        HashMap<String, GAChange> group6 = new HashMap<>(); // margin 60% off alone with height 2
        HashMap<String, GAChange> group7 = new HashMap<>(); // margin 40% off alone with height 2

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

                        // group 1: decrease padding by 60%
                        double calculateNewVal = Math.floor(currentNumericalValue * .40);
                        if (calculateNewVal < 0) {
                            calculateNewVal = 0;
                        }
                        String changeVal = calculateNewVal + "";
                        String changeType = OwlConstants.CHANGE_DECREASE;
                        GAChange change = new GAChange("margin", attName, changeType, changeVal, elementToChange, margin);
                        elementToChange.addChange(attName, change);
                        if (change != null) {
                            addElementToGroupOfChange(group1, elementToChange.getId(), change);
                        }

                        // group 2: decreae padding by 40%
                         calculateNewVal = Math.floor(currentNumericalValue * .60);
                        if (calculateNewVal < 0) {
                            calculateNewVal = 0;
                        }

                        changeVal = calculateNewVal + "";
                        changeType = OwlConstants.CHANGE_DECREASE;
                        change = new GAChange("margin", attName, changeType, changeVal, elementToChange, margin);
                        elementToChange.addChange(attName, change);
                        if (change != null) {
                            addElementToGroupOfChange(group2, elementToChange.getId(), change);
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
                            addElementToGroupOfChange(group3, elementToChange.getId(), change);

//                            group5.put(elementToChange.getId() + "#" + attName, change);
                        }


                    }
                }
            }

        }


        // first add margins alone
        groupsOfChanges.get("group7").putAll(group1);
        groupsOfChanges.get("group8").putAll(group2);
        groupsOfChanges.get("group9").putAll(group3);

        // now consider dimensions

        HashMap<String, GAChange> groupDimension1 = groupsOfChanges.get("group1"); // these already added for size and SRG
        HashMap<String, GAChange> groupDimension2 = groupsOfChanges.get("group2");

        // we only add those if there is margin and padding because otherwise it is bascially the same as group 1 and 2
        if (groupDimension1.size() > 0) {
            group4.putAll(groupDimension1);
            group4.putAll(group1);
            group5.putAll(groupDimension1);
            group5.putAll(group2);

        }
        if (groupDimension2.size() > 0) {
            group6.putAll(groupDimension2);
            group6.putAll(group1);
            group7.putAll(groupDimension2);
            group7.putAll(group2);

        }
        //added afer SCI servey Arpil13

        groupsOfChanges.get("group9").putAll(group4);
        groupsOfChanges.get("group10").putAll(group5);
        groupsOfChanges.get("group11").putAll(group6);
        groupsOfChanges.get("group12").putAll(group7);


//        groupsOfChanges.get("group3").putAll(group3);
//        groupsOfChanges.get("group4").putAll(group4);
//        groupsOfChanges.get("group5").putAll(group5);

    }
    private void createGroupOfChangesForPaddingElementsCutoff(HashMap<String, GAElementToChange> marginElements,
                                                             HashMap<String, HashMap<String, GAChange>> groupsOfChanges) {

        // unlike collisions and missing, we only have two groups for marigns: 60% (40 remaining) off and 40% (60 remaining) off and 70% (30 remaining) off
        HashMap<String, GAChange> group1 = new HashMap<>(); // margin 60% off alone
        HashMap<String, GAChange> group2 = new HashMap<>(); // margin 40% off alone
        HashMap<String, GAChange> group3 = new HashMap<>(); // margin 70% off alone
        HashMap<String, GAChange> group4 = new HashMap<>(); // margin 60% off alone with height 1
        HashMap<String, GAChange> group5 = new HashMap<>(); // margin 40% off alone with height 1
        HashMap<String, GAChange> group6 = new HashMap<>(); // margin 60% off alone with height 2
        HashMap<String, GAChange> group7 = new HashMap<>(); // margin 40% off alone with height 2

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

                        // group 1: decrease padding by 60%
                        double calculateNewVal = Math.floor(currentNumericalValue * .40);
                        if (calculateNewVal < 0) {
                            calculateNewVal = 0;
                        }
                        String changeVal = calculateNewVal + "";
                        String changeType = OwlConstants.CHANGE_DECREASE;
                        GAChange change = new GAChange("padding", attName, changeType, changeVal, elementToChange, margin);
                        elementToChange.addChange(attName, change);
                        if (change != null) {
                            addElementToGroupOfChange(group1, elementToChange.getId(), change);
                        }

                        // group 2: decreae padding by 40%
                        calculateNewVal = Math.floor(currentNumericalValue * .60);
                        if (calculateNewVal < 0) {
                            calculateNewVal = 0;
                        }

                        changeVal = calculateNewVal + "";
                        changeType = OwlConstants.CHANGE_DECREASE;
                        change = new GAChange("padding", attName, changeType, changeVal, elementToChange, margin);
                        elementToChange.addChange(attName, change);
                        if (change != null) {
                            addElementToGroupOfChange(group2, elementToChange.getId(), change);
                        }
                        // group 3: increase padding by 70% (remaining is 0.30) and it is only this no height or width
                        calculateNewVal = Math.floor(currentNumericalValue * .30);
                        if (calculateNewVal < 0) {
                            calculateNewVal = 0;
                        }
                        changeVal = calculateNewVal + "";
                        changeType = OwlConstants.CHANGE_DECREASE;
                        change = new GAChange("padding", attName, changeType, changeVal, elementToChange, margin);
                        elementToChange.addChange(attName, change);
                        if (change != null) {
                            addElementToGroupOfChange(group3, elementToChange.getId(), change);

//                            group5.put(elementToChange.getId() + "#" + attName, change);
                        }


                    }
                }
            }

        }


        // first add margins alone
        groupsOfChanges.get("group7").putAll(group1);
        groupsOfChanges.get("group8").putAll(group2);
        groupsOfChanges.get("group9").putAll(group3);

        // now consider dimensions

        HashMap<String, GAChange> groupDimension1 = groupsOfChanges.get("group1"); // these already added for size and SRG
        HashMap<String, GAChange> groupDimension2 = groupsOfChanges.get("group2");

        // we only add those if there is margin and padding because otherwise it is bascially the same as group 1 and 2
        if (groupDimension1.size() > 0) {
            group4.putAll(groupDimension1);
            group4.putAll(group1);
            group5.putAll(groupDimension1);
            group5.putAll(group2);

        }
        if (groupDimension2.size() > 0) {
            group6.putAll(groupDimension2);
            group6.putAll(group1);
            group7.putAll(groupDimension2);
            group7.putAll(group2);

        }
        // added after sci survey April13
        groupsOfChanges.get("group9").putAll(group4);
        groupsOfChanges.get("group10").putAll(group5);
        groupsOfChanges.get("group11").putAll(group6);
        groupsOfChanges.get("group12").putAll(group7);
//        groupsOfChanges.get("group3").putAll(group3);
//        groupsOfChanges.get("group4").putAll(group4);
//        groupsOfChanges.get("group5").putAll(group5);

    }
//    private void createGroupOfChangesForPaddingElementsCutoff(HashMap<String, GAElementToChange> paddingElements,
//                                                              HashMap<String, HashMap<String, GAChange>> groupsOfChanges) {
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

    private void createGroupOfChangesForSRGElements(HashMap<String, GAElementToChange> srgElements,
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

            String changeType = OwlConstants.CHANGE_INCREASE;
            // gene group 1 for main size --> do not change how it is set (if it is fixed directly increase it or if it is constraints add Min)
            GAChange change = createDoNotChangeSizeTypeGroup(element, sizeProp,  changeType);
            if (change != null) {
                addElementToGroupOfChange(group1, change.getElement().getId(),change);
             //   group1.put(element.getId(), change);
            }

            ArrayList<GAChange> change2 = createChangeSizeTypeGroup(element, sizeProp, changeType);
            //change2=change;
            if (change2 != null) {
                for (GAChange gaChange : change2){
                    if(gaChange!=null)
                        addElementToGroupOfChange(group2, gaChange.getElement().getId(),gaChange);
                       // group2.put(element.getId(), gaChange);
                }

            }



        }
        groups.get("group1").putAll(group1);
        groups.get("group2").putAll(group2);


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

            String changeType = OwlConstants.CHANGE_INCREASE;
            // gene group 1 for main size --> do not change how it is set (if it is fixed directly increase it or if it is constraints add Min)
            GAChange change = createDoNotChangeSizeTypeGroup(element, sizeProp,  changeType);
            if (change != null) {
                group1.put(element.getId(), change);
            }

            ArrayList<GAChange> change2 = createChangeSizeTypeGroup(element, sizeProp, changeType);
            //change2=change;
            if (change2 != null) {
                for (GAChange gaChange : change2){
                    if(gaChange!=null)
                        group2.put(element.getId(), gaChange);}

            }



        }
        groups.get("group1").putAll(group1);
        groups.get("group2").putAll(group2);


    }
    private void createGroupOfChangesForVSRGElementsCollisions(HashMap<String, GAElementToChange> srgElements,
                                                               HashMap<String, GAElementToChange> problematicElements, HashMap<String, HashMap<String, GAChange>> groups) {
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
    /***** main Size changes methods **********/
    private ArrayList<GAChange> createChangeSizeTypeGroupCollision(GAElementToChange element, Property sizeProp,
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
                        change2 = addMinChange2(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal(),changeType,-10);
                    }
                    change = new GAChange(sizeProp.getPropertyName(), OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(sizeProp.getPropertyName()), changeType, changeVal, element, sizeProp);
                } else if (numericalStaticVal == -2 ){//|| numericalStaticVal==-1 ) { // it is set as wrap_content or match_parent so we change it to fixed value

                    boolean changeToFixed = true; /**FEB 6th: disabled this part to test*/
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
    private GAChange createChangeSizeTypeGroupMissing(GAElementToChange element, Property sizeProp,
                                                        String changeType,double changeAmountPercentage) {
        // get the size attribute set type: fixed or constraints (ie wrap_content or match_parent)
        GAChange change = null;
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

                    change = new GAChange(sizeProp.getPropertyName(), OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(sizeProp.getPropertyName()), changeType, changeVal, element, sizeProp);
                } else if (numericalStaticVal == -2) { // it is set as wrap_content or match_parent so we change it to fixed value
                    String dynamicVal = sizeProp.getCurrentDynamicVal();
                    if (dynamicVal != null) {
                        List<Double> y = Util.getNumbersFromString(dynamicVal);
                        if (y.size() > 0) {
                            double numericalDynamicVal = y.get(0);
                            double newVal= Utils.changeCurrentValueByPercentage(numericalDynamicVal, changeAmountPercentage);
                            changeVal = String.valueOf(newVal);
                            change = new GAChange(sizeProp.getPropertyName(), OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(sizeProp.getPropertyName()), changeType, changeVal, element, sizeProp);
                        }
                    }
                }


            }

        } else { // it is an exceptional  case that I will handle later

        }

        return change;
    }


    private ArrayList<GAChange> createChangeSizeTypeGroupCutoff(GAElementToChange element, Property sizeProp,
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
                    change = new GAChange(sizeProp.getPropertyName(), OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(sizeProp.getPropertyName()), changeType, changeVal, element, sizeProp);
                    if (element.getNode().getData().getTagName().equalsIgnoreCase("EditText")) {
                        change2 = addMinChange2(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal(),changeType,0);
                    }
                } else if (numericalStaticVal == -2) { // it is set as wrap_content or match_parent so we change it to fixed value

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
        if(change!=null)
            changes.add(change);
        if(change2!=null)
            changes.add(change2);

        return changes;
    }
    private ArrayList<GAChange> createChangeSizeTypeGroup(GAElementToChange element, Property sizeProp,String changeType) {
        // get the size attribute set type: fixed or constraints (ie wrap_content or match_parent)
        GAChange change = null;
        GAChange change2 = null; // if we change to min and it is EditText we should also change add min
        double changeAmount = +20;
        String staticVal = sizeProp.getCurrentStaticVal();
        if (Utils.isValueNumerical(staticVal)) {
            Double numericalStaticVal = null;
            List<Double> x = Util.getNumbersFromString(staticVal);
            if (x.size() > 0) {  // it is fixed so we should do wrap_content
                numericalStaticVal = x.get(0);
                String changeVal;
                if (numericalStaticVal > 0) { // it is set as fixed value in the app so we change it to wrap_content
                    changeVal = "-2";
                    change = new GAChange(sizeProp.getPropertyName(), OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(sizeProp.getPropertyName()), changeType, changeVal, element, sizeProp);
                    if (element.getNode().getData().getTagName().equalsIgnoreCase("EditText")||element.getNode().getData().getTagName().equalsIgnoreCase("TextView")) {
                        change2 = addMinChange2(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal(),changeType,0);
                    }

                } else if (numericalStaticVal == -2) { // it is set as wrap_content or match_parent so we change it to fixed value
                    String dynamicVal = sizeProp.getCurrentDynamicVal();
                    if (dynamicVal != null) {
                        List<Double> y = Util.getNumbersFromString(dynamicVal);
                        if (y.size() > 0) {
                            Double numericalDynamicVal = y.get(0);
                            changeVal = String.valueOf(numericalDynamicVal + changeAmount);
                            change = new GAChange(sizeProp.getPropertyName(), OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(sizeProp.getPropertyName()), changeType, changeVal, element, sizeProp);
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
    private GAChange createDoNotChangeSizeTypeGroupCollision(GAElementToChange element, Property sizeProp, String changeType,double changeAmountPercentage) {
        GAChange change = null;
        String staticVal = sizeProp.getCurrentStaticVal();
        //double changeAmountPercentage= -20; // that we want to introduce to properties wether main ones or minHeight or minWidth
        if (Utils.isValueNumerical(staticVal)) {  //we are replacing wrap_content and match_parent with -2 and -1 in getValueFromElement method so basically we are always going to get numerical unless execptional cases
            Double numericalStaticVal = null;
            List<Double> x = Util.getNumbersFromString(staticVal);
            if (x.size() > 0) {
                numericalStaticVal = x.get(0);
                String changeVal;

                if (numericalStaticVal > 0) { // it is set as fixed value in the app
                    double newVal= Utils.changeCurrentValueByPercentage(numericalStaticVal, changeAmountPercentage);
                    changeVal = newVal + "";
                    change = new GAChange(sizeProp.getPropertyName(), OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(sizeProp.getPropertyName()), changeType, changeVal, element, sizeProp);

                } else if (numericalStaticVal == 0) { // it is set with weight  ToDo: How to handle this case? maybe we look for other type of changes not the layout_height
//                    handleWeightForMainSize(element, sizeProp.getPropertyName(), staticVal, sizeProp.getCurrentDynamicVal(), weightChanges);
                    if (changeType.equalsIgnoreCase(OwlConstants.CHANGE_DECREASE)) {
                        if (element.getSource().equalsIgnoreCase("problematic1")) {
                            change = addMaxDimensionChange(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal(), changeType, changeAmountPercentage);
                        } else {
//                        change = addMaxDimensionChange(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal(),changeType,changeAmountPercentage);
                        }
                    }

                } else if (numericalStaticVal == -1) { // it is set with match_parent
                    // Should we go with min_height so we get dynamic height and add 10  as min_height and then leave the change to a parent in the SRG

//                    change = addMinChange2(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal(),changeType,changeAmountPercentage);
                    if (changeType.equalsIgnoreCase(OwlConstants.CHANGE_INCREASE)) {
                        change = addMinChange2(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal(),changeType,changeAmountPercentage);
                    } else {
                        change = addMaxDimensionChange(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal(),changeType,changeAmountPercentage);

                    }

                } else if (numericalStaticVal == -2) { // it is set with wrap_content
                    // since it is wrap_content we should go with min_hgight
//                                   changeVal = numericalStaticVal + 10 + "";
//                                   changeType = OwlConstants.Increase;
                    // for collision we want to decrese the font so minwidht does
                    if (changeType.equalsIgnoreCase(OwlConstants.CHANGE_INCREASE)) {
                        change = addMinChange2(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal(),changeType,changeAmountPercentage);
                    } else {
                        change = addMaxDimensionChange(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal(),changeType,changeAmountPercentage);

                    }
//                    change = addMaxDimensionChange(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal(),changeType,changeAmountPercentage);

                    //change = addMinChange2(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal(),changeType,changeAmountPercentage);
                }
            }


        } else { // it is an exceptional  case that I will handle later

        }

        return change;
    }

    private GAChange createDoNotChangeSizeTypeGroupCutOff(GAElementToChange element, Property sizeProp, String changeType,double changeAmountPercentage) {
        GAChange change = null;
        String staticVal = sizeProp.getCurrentStaticVal();
        //double changeAmountPercentage= -20; // that we want to introduce to properties wether main ones or minHeight or minWidth
        if (Utils.isValueNumerical(staticVal)) {  //we are replacing wrap_content and match_parent with -2 and -1 in getValueFromElement method so basically we are always going to get numerical unless execptional cases
            Double numericalStaticVal = null;
            List<Double> x = Util.getNumbersFromString(staticVal);
            if (x.size() > 0) {
                numericalStaticVal = x.get(0);
                String changeVal;

                if (numericalStaticVal > 0) { // it is set as fixed value in the app
                    double newVal= Utils.changeCurrentValueByPercentage(numericalStaticVal, changeAmountPercentage);
                    changeVal = newVal + "";
                    change = new GAChange(sizeProp.getPropertyName(), OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(sizeProp.getPropertyName()), changeType, changeVal, element, sizeProp);

                } else if (numericalStaticVal == 0) { // it is set with weight  ToDo: How to handle this case? maybe we look for other type of changes not the layout_height
//                    handleWeightForMainSize(element, sizeProp.getPropertyName(), staticVal, sizeProp.getCurrentDynamicVal(), weightChanges);

                } else if (numericalStaticVal == -1) { // it is set with match_parent
                    // Should we go with min_height so we get dynamic height and add 10  as min_height and then leave the change to a parent in the SRG

//                    change = addMinChange2(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal(),changeType,changeAmountPercentage);
                    if (changeType.equalsIgnoreCase(OwlConstants.CHANGE_INCREASE)) {
                        change = addMinChange2(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal(),changeType,changeAmountPercentage);
                    } else {
                        change = addMaxDimensionChange(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal(),changeType,changeAmountPercentage);

                    }

                } else if (numericalStaticVal == -2) { // it is set with wrap_content
                    // since it is wrap_content we should go with min_hgight
//                                   changeVal = numericalStaticVal + 10 + "";
//                                   changeType = OwlConstants.Increase;
                    // for collision we want to decrese the font so minwidht does
                    if (changeType.equalsIgnoreCase(OwlConstants.CHANGE_INCREASE)) {
                        change = addMinChange2(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal(),changeType,changeAmountPercentage);
                    } else {
                        change = addMaxDimensionChange(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal(),changeType,changeAmountPercentage);

                    }
//                    change = addMaxDimensionChange(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal(),changeType,changeAmountPercentage);

                    //change = addMinChange2(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal(),changeType,changeAmountPercentage);
                }
            }


        } else { // it is an exceptional  case that I will handle later

        }

        return change;
    }
    private GAChange createDoNotChangeSizeTypeGroup(GAElementToChange element, Property sizeProp,
                                                    String chagneType) {
        GAChange change = null;
        String changeType = OwlConstants.CHANGE_INCREASE;
        double changeAmount= +20; // that we want to introduce to properties wether main ones or minHeight or minWidth
        String staticVal = sizeProp.getCurrentStaticVal();
        if (Utils.isValueNumerical(staticVal)) {  //we are replacing wrap_content and match_parent with -2 and -1 in getValueFromElement method so basically we are always going to get numerical unless execptional cases
            Double numericalStaticVal = null;
            List<Double> x = Util.getNumbersFromString(staticVal);
            if (x.size() > 0) {
                numericalStaticVal = x.get(0);
                String changeVal;
                if (numericalStaticVal > 0) { // it is set as fixed value in the app
                    double n = numericalStaticVal + changeAmount;
                    changeVal = n + "";
                    change = new GAChange(sizeProp.getPropertyName(), OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(sizeProp.getPropertyName()), changeType, changeVal, element, sizeProp);

                } else if (numericalStaticVal == 0) { // it is set with weight  ToDo: How to handle this case? maybe we look for other type of changes not the layout_height
//                    handleWeightForMainSize(element, sizeProp.getPropertyName(), staticVal, sizeProp.getCurrentDynamicVal(), weightChanges);

                } else if (numericalStaticVal == -1) { // it is set with match_parent
                    // Should we go with min_height so we get dynamic height and add 10  as min_height and then leave the change to a parent in the SRG

                    change = addMinChange2(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal(),changeType,changeAmount);

                } else if (numericalStaticVal == -2) { // it is set with wrap_content
                    // since it is wrap_content we should go with min_hgight
//                                   changeVal = numericalStaticVal + 10 + "";
//                                   changeType = OwlConstants.Increase;

                    change = addMinChange2(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal(),changeType,changeAmount);
                }
            }


        } else { // it is an exceptional  case that I will handle later

        }

        return change;
    }

    /***************************************************************************************************************************************************************/
    /********************************************************************** Missing Issues ************************************************************************/
    /*************************************************************************************************************************************************************/

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
        for (Node<DomNode> problematicElement : problematicNodes) {
//            problematicIDs.add(problematicElement.getData().getId());
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
                        elementsToChange.get("problematic").put(child.getData().getId(), problematicChildElementToChange);
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
        boolean includeSRG = false;
        boolean includeVSrg = false;
        boolean includePadding = false;
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
            createGroupOfChangesForPaddingElementsMissing(marginElements, groupsOfChanges);
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

    private void createGroupOfChangesForScrollableElementsMissing(HashMap<String, GAElementToChange> scrollableElements, HashMap<String, HashMap<String, GAChange>> groupsOfChanges) {

    }


    private void createGroupOfChangesForProblematicMissing(HashMap<String, HashMap<String, GAElementToChange>> fixSetForCollision,
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
            double changeAmountPercentage = 20;
            GAChange[] fourChanges = createFourDimensionChanges(element, sizeProp, changeAmountPercentage);
            if (fourChanges[0] != null) {
                addElementToGroupOfChange(group1, element.getId(), fourChanges[0]);
            }
//            if (fourChanges[1] != null) {
//                addElementToGroupOfChange(group2, element.getId(), fourChanges[1]);
//            }
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

        groupsOfChanges.get("group1").putAll(group1);
        groupsOfChanges.get("group2").putAll(group2);
    }



    private ArrayList<GAChange> createChangeSizeTypeGroupMissing(GAElementToChange element, Property sizeProp,
                                                        String changeType) {
        // get the size attribute set type: fixed or constraints (ie wrap_content or match_parent)
        GAChange change = null;
        GAChange change2 = null;
        GAChange change3 = null;
        double changeAmountPercentage=-20;
        String staticVal = sizeProp.getCurrentStaticVal();
        if (Utils.isValueNumerical(staticVal)) {
            Double numericalStaticVal = null;
            List<Double> x = Util.getNumbersFromString(staticVal);
            if (x.size() > 0) {  // it is fixed so we should do wrap_content
                numericalStaticVal = x.get(0);
                String changeVal;

                if (numericalStaticVal > 0) { // it is set as fixed value in the app so we change it to wrap_content
                    changeVal = "-2";


                    change = new GAChange(sizeProp.getPropertyName(), OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(sizeProp.getPropertyName()), changeType, changeVal, element, sizeProp);
                    if (element.getNode().getData().getTagName().equalsIgnoreCase("EditText")||element.getNode().getData().getTagName().equalsIgnoreCase("TextView")) {
                        change2 = addMinChange2(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal(),changeType,changeAmountPercentage);
                        change3 = addMaxDimensionChange(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal(),changeType,0);

                    }


                } else if (numericalStaticVal == -2) { // it is set as wrap_content or match_parent so we change it to fixed value
                    String dynamicVal = sizeProp.getCurrentDynamicVal();
                    if (dynamicVal != null) {
                        List<Double> y = Util.getNumbersFromString(dynamicVal);
                        if (y.size() > 0) {
                            double numericalDynamicVal = y.get(0);
                            double newVal= Utils.changeCurrentValueByPercentage(numericalDynamicVal, changeAmountPercentage);
                            changeVal = String.valueOf(newVal);
                            change = new GAChange(sizeProp.getPropertyName(), OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(sizeProp.getPropertyName()), changeType, changeVal, element, sizeProp);
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
        if (change3 != null) {
            changes.add(change3);
        }
        return changes;
    }


    private GAChange createDoNotChangeSizeTypeGroupMissing(GAElementToChange element, Property sizeProp, String changeType) {
        GAChange change = null;
        String staticVal = sizeProp.getCurrentStaticVal();
        double changeAmountPercentage= -30; // that we want to introduce to properties wether main ones or minHeight or minWidth
        if (Utils.isValueNumerical(staticVal)) {  //we are replacing wrap_content and match_parent with -2 and -1 in getValueFromElement method so basically we are always going to get numerical unless execptional cases
            Double numericalStaticVal = null;
            List<Double> x = Util.getNumbersFromString(staticVal);
            if (x.size() > 0) {
                numericalStaticVal = x.get(0);
                String changeVal;

                if (numericalStaticVal > 0) { // it is set as fixed value in the app
                    double newVal= Utils.changeCurrentValueByPercentage(numericalStaticVal, changeAmountPercentage);
                    changeVal = newVal + "";
                    change = new GAChange(sizeProp.getPropertyName(), OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(sizeProp.getPropertyName()), changeType, changeVal, element, sizeProp);

                } else if (numericalStaticVal == 0) { // it is set with weight  ToDo: How to handle this case? maybe we look for other type of changes not the layout_height
//                    handleWeightForMainSize(element, sizeProp.getPropertyName(), staticVal, sizeProp.getCurrentDynamicVal(), weightChanges);

                } else if (numericalStaticVal == -1) { // it is set with match_parent
                    // Should we go with min_height so we get dynamic height and add 10  as min_height and then leave the change to a parent in the SRG

//                    change = addMinChange2(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal(),changeType,changeAmountPercentage);
                    change = addMaxDimensionChange(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal(),changeType,changeAmountPercentage);


                } else if (numericalStaticVal == -2) { // it is set with wrap_content
                    // since it is wrap_content we should go with min_hgight
//                                   changeVal = numericalStaticVal + 10 + "";
//                                   changeType = OwlConstants.Increase;
                    // for collision we want to decrese the font so minwidht does
                    change = addMaxDimensionChange(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal(),changeType,changeAmountPercentage);

                    //change = addMinChange2(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal(),changeType,changeAmountPercentage);
                }
            }


        } else { // it is an exceptional  case that I will handle later

        }

        return change;
    }

    /******************************************************************************************************************************************************************* */
    /*************************************************************************** HELPING METHODS *********************************************************************** */

    /********************************************************************************************************************************************************************/


    private Node<DomNode> checkIfThereIsAlongScrollableTextView(ArrayList<Node<DomNode>> problematicNodes) {
        for (Node<DomNode> node : problematicNodes
        ) {
            String className = node.getData().getTagName();
            String id = node.getData().getId();
            if (className.equals("TextView")) {
                String scrollbars = Utils.getValueFromElement(node.getData(), "scrollbars");
                if (scrollbars != null) {
                    // there is a scroll so we do not need to worry about cuttoff but maybe add lines
                    Logger.debug("There is a scrollable text view");
                    return node;
                }


            }
        }
        return null;
    }

    private boolean checkIfElementIsAlongScrollableTextView(Node<DomNode> node) {

        String className = node.getData().getTagName();
        String id = node.getData().getId();
        if (className.equals("TextView")) {
            String scrollbars = Utils.getValueFromElement(node.getData(), "scrollbars");
            if (scrollbars != null) {
                Logger.debug("There is a scrollable text view");
                return true;
            }


        }

        return false;
    }


}

