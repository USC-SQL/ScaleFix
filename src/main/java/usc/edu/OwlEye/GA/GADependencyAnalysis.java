package usc.edu.OwlEye.GA;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import org.tinylog.Logger;
import usc.edu.OwlEye.AUCII.Collision;
import usc.edu.OwlEye.ElementsProperties.*;
import usc.edu.OwlEye.GAChanges.GAChange;
import usc.edu.OwlEye.GAChanges.GAElementToChange;
import usc.edu.OwlEye.OwlConstants;
import usc.edu.OwlEye.OwlEye;
import usc.edu.OwlEye.UIModels.WRGNode;
import usc.edu.OwlEye.util.Utils;
import usc.edu.SALEM.util.Util;
import usc.edu.layoutgraph.LayoutGraph;
import usc.edu.layoutgraph.edge.NeighborEdge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GADependencyAnalysis {

    static GAChange addMinChange2(GAElementToChange elementToChange, String propName, String dynamicVal,
                                  String changeType, double changeAmountPercentage) {
        String changeVal;
        GAChange minChange = null;
        Property minProp = new MinHeight();
        String minProperty = MinHeight.propertyName;
        if (propName.equalsIgnoreCase(Width.propertyName)) {
            minProperty = MinWidth.propertyName;
            minProp = new MinWidth();
        }
        String currProp = minProperty;

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


                    minChange = new GAChange(currProp, OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(currProp), changeType, changeVal, elementToChange, minProp);
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

            minChange = new GAChange(currProp, OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(currProp), changeType, changeVal, elementToChange, null);
        }
        return minChange;

    }
    public static GAChange addMaxDimensionChange(GAElementToChange elementToChange, String propName, String dynamicVal,
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

    ArrayList<GAChange> createChangeSizeTypeGroupCollision(GAElementToChange element, Property sizeProp,
                                                           String changeType, double changeAmountPercentage) {
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
    GAChange createDoNotChangeSizeTypeGroupCollision(GAElementToChange element, Property sizeProp, String changeType, double changeAmountPercentage) {
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

    protected GAChange[] createFourDimensionChanges(GAElementToChange element, Property sizeProp, double changeAmountPercentage) {
        double currentChangeAmountPercent = changeAmountPercentage;
        String changeType = OwlConstants.CHANGE_DECREASE;
        if(changeType.equalsIgnoreCase(OwlConstants.CHANGE_DECREASE)){
            currentChangeAmountPercent = -changeAmountPercentage;
        }
        GAChange change1 = createDoNotChangeSizeTypeGroupCollision(element, sizeProp, changeType,currentChangeAmountPercent);

        ArrayList<GAChange> changes = createChangeSizeTypeGroupCollision(element, sizeProp, changeType,currentChangeAmountPercent);
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

    protected void addElementToGroupOfChange(HashMap<String, GAChange> group, String id, GAChange change) {
        //ArrayList<GAChange> changes;
        String changeValue = change.getValue();
        String changeAttribute = change.getAttName();
        String changeID = id +"#"+  changeAttribute+"#"+changeValue;
        group.put(changeID,change);

    }




    protected GAChange createDoNotChangeSizeTypeGroup(GAElementToChange element, Property sizeProp,
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


    protected ArrayList<GAChange> createChangeSizeTypeGroup(GAElementToChange element, Property sizeProp,String changeType) {
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


    protected void createGroupOfChangesForConstraintLayout(HashMap<String, HashMap<String, GAElementToChange>> fixSetForCollision,


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
            GAElementToChange n1 = regSPLRG.get(node1ID);
            if (n0 == null || n1 == null) {
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
                boolean originalApproach = false;
                if (originalApproach) {
                    groupsOfChanges.get("group1").put(node0ID, change1); // this was the only thing for adding group march 25 (original approach)
                } else {
                    /** now on new  (new approach)**/
                    HashMap<String, GAChange> group1 = new HashMap<>();
                    if (change1 != null) {
                        addElementToGroupOfChange(group1, node0ID, change1);
                        /** now on new **/
                        if (groupsOfChanges.get("group1").size() > 0) {
                            groupsOfChanges.get("group1").putAll(group1);
                        }
                        if (groupsOfChanges.get("group2").size() > 0) {
                            groupsOfChanges.get("group2").putAll(group1);
                        }
                        if (groupsOfChanges.get("group3").size() > 0) {
                            groupsOfChanges.get("group3").putAll(group1);
                        }
                        if (groupsOfChanges.get("group4").size() > 0) {
                            groupsOfChanges.get("group4").putAll(group1);
                        }
                        groupsOfChanges.get("group17").putAll(group1);

                    }
                }
            }
        }

    }
    protected HashMap<String, HashMap<String, ArrayList<GAElementToChange>>> findGroupsOfSiblingsForWRGFixSet(HashMap<String, GAElementToChange> wrgElements) {
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

    protected boolean checkIfElementIsAlongScrollableTextView(Node<DomNode> node) {

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
