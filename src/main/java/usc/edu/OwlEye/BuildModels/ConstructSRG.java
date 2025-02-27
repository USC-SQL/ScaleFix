package usc.edu.OwlEye.BuildModels;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import usc.edu.OwlEye.ElementsProperties.Height;
import usc.edu.OwlEye.ElementsProperties.MinHeight;
import usc.edu.OwlEye.ElementsProperties.MinWidth;
import usc.edu.OwlEye.ElementsProperties.Width;
import usc.edu.OwlEye.UIModels.SRG;
import usc.edu.OwlEye.UIModels.SRGNode;
import usc.edu.OwlEye.VH.UI;
import usc.edu.OwlEye.VHTree.XMLUtils;
import usc.edu.OwlEye.util.Utils;
import usc.edu.SALEM.util.Util;


import java.util.*;

public class ConstructSRG {

    protected static ConstructSRG instance = null;


    private ConstructSRG(String subjectID) {
    }

    public static ConstructSRG getInstance() {

        if (instance == null)
            instance = new ConstructSRG(null);  // do whatever else you need to here

        return instance;
    }

    public SRG buildGraph(UI ui) {
        XMLUtils xmlTree = ui.getXMLTree();
        // Initialize the SRG
        SRG srg = new SRG(ui.getUITitle());
        // (1) create height relationship
        createHeightSRG(srg,xmlTree, Height.propertyName);
        // (2) create width relationship
        createWidthSRG(srg,xmlTree, Width.propertyName);

        return srg;

    }




    private void createHeightSRG(SRG srg, XMLUtils xmlTree, String height) {
        // get the height SRG
        TreeMap<String, List<SRGNode>>  heightSRG = srg.getDependentNodesMap2().get(height);
        //TreeMap<String, List<SRGNode>> dependentNodesMap = srg.getDependentNodesMap();
        Node<DomNode> root = xmlTree.getRoot();
        String rootValue = Utils.getValueFromElement(root.getData(), Height.propertyName);
        String property = Height.propertyName;
        if (rootValue.matches(".*\\d+.*")) {
            double rootHeight = Util.getNumbersFromString(rootValue).get(0);
            if (rootHeight > 0 || rootHeight != -1) {
                heightSRG.put(root.getData().getxPath() , new ArrayList<SRGNode>());
            }
        }
            // add children of the root to the queue
            Queue<Node<DomNode>> q = new LinkedList<Node<DomNode>>();
            if (root.getChildren() != null) {
                for (Node<DomNode> child : root.getChildren()) {
                    q.add(child);
                }
            }


            //Start the BFS traversal

            while (!q.isEmpty()) {


                Node<DomNode> node = q.remove();
                DomNode e = node.getData();

                String childHeight = Utils.getValueFromElement(e, property);
                Double childHeightNumber=Util.getNumbersFromString(childHeight).get(0);
                if (childHeight != null) {
                    childHeightNumber = Util.getNumbersFromString(childHeight).get(0);
                }

                if(childHeight!=null){
                    if (childHeightNumber == -1) { // what to do in that case?

                    }
                    if (childHeightNumber == -2.0) {


                    }

                    Node<DomNode> ancestor = node.getParent();
                    boolean isAncestorFound = false;
                    boolean isAncestorGraphKey = false;
                    String parentHeight = "";
                    Double parentHeightNumber = -100.0;

                    while (!isAncestorFound && ancestor != null) {
                        parentHeight = Utils.getValueFromElement(ancestor.getData(), property);
                        parentHeightNumber = -100.0;
                        if (parentHeight != null) {
                            parentHeightNumber = Util.getNumbersFromString(parentHeight).get(0);
                        }
                        if ((parentHeightNumber != -1)) {
                            isAncestorFound = true;
                            break;
                        }

                        if (!isAncestorFound) {
                            ancestor = ancestor.getParent();
                        }
                    }

                    if (isAncestorFound) {
                        // We already computed the values so no need to do it agian
//                        String ancestorValue = Utils.getValueFromElement(ancestor.getData().getxPath(), property);
//                        String nodeValue = Utils.getValueFromElement(e.getxPath(), property);
                        if (parentHeight.matches(".*\\d+.*") && childHeight.matches(".*\\d+.*")) {
                            String parentDynamicHeight = Utils.getDynamicValueInDDP(ancestor.getData(), property);
                            String childDynamicHeight = Utils.getDynamicValueInDDP(e, property);
                            double parentDynamicHeightNumber = Util.getNumbersFromString(parentDynamicHeight).get(0);
                            double childDynamicHeightNumber = Util.getNumbersFromString(childDynamicHeight).get(0);
                            double ratio = parentDynamicHeightNumber / childDynamicHeightNumber;
//                            String parentDependantProperty = property;  // by the default it is the main property but if it parent is min then we make it min-height or min-width

                            List<SRGNode> dependentNodes = new ArrayList<SRGNode>();
                            dependentNodes.add(new SRGNode(ancestor.getData().getxPath(),ancestor, property, 1 / ratio, childDynamicHeightNumber, parentDynamicHeightNumber));
                            heightSRG.put(e.getxPath() , dependentNodes);

                        }

                    }

                    if (node.getChildren() != null) {
                        for (Node<DomNode> child : node.getChildren()) {
                            q.add(child);
                        }
                    }
                        }


                    } //end of    while (!q.isEmpty())  loop




                }

    private void createWidthSRG(SRG srg, XMLUtils xmlTree, String property) {
        // get the height SRG
        TreeMap<String, List<SRGNode>>  propSRG = srg.getDependentNodesMap2().get(property);
        //TreeMap<String, List<SRGNode>> dependentNodesMap = srg.getDependentNodesMap();
        Node<DomNode> root = xmlTree.getRoot();
        String rootValue = Utils.getValueFromElement(root.getData(), property);
//        String property = Height.propertyName;
        if (rootValue.matches(".*\\d+.*")) {
            double rootProp = Util.getNumbersFromString(rootValue).get(0);
            if (rootProp > 0 || rootProp != -1) {
                propSRG.put(root.getData().getxPath() , new ArrayList<SRGNode>());
            }
        }
        // add children of the root to the queue
        Queue<Node<DomNode>> q = new LinkedList<Node<DomNode>>();
        if (root.getChildren() != null) {
            for (Node<DomNode> child : root.getChildren()) {
                q.add(child);
            }
        }


        //Start the BFS traversal

        while (!q.isEmpty()) {

            String parentAtt = property; // usually it is width or height but it maybe min-width or min-height if it is -1 or -2 but defined min-width or min-height
            Node<DomNode> node = q.remove();
            DomNode e = node.getData();

            String childProp = Utils.getValueFromElement(e, property);
            Double childHeightNumber=Util.getNumbersFromString(childProp).get(0);
            if (childProp != null) {
                childHeightNumber = Util.getNumbersFromString(childProp).get(0);
            }

            if(childProp!=null){
                if (childHeightNumber == -1) { // what to do in that case?

                }
                if (childHeightNumber == -2.0) {


                }

                Node<DomNode> ancestor = node.getParent();
                boolean isAncestorFound = false;
                boolean isAncestorGraphKey = false;
                String parentProp = "";
                Double parentPropNumber = -100.0;

                while (!isAncestorFound && ancestor != null) {
                    parentProp = Utils.getValueFromElement(ancestor.getData(), property);
                    parentPropNumber = -100.0;
                    if (parentProp != null) {
                        parentPropNumber = Util.getNumbersFromString(parentProp).get(0);
                    }
                    if ((parentPropNumber != -1)) {
                        isAncestorFound = true;
                        parentAtt= property;
                        break;
                    }
                    else {
                        // if ancestor -1 then we need to double-check if there is min_width or min_height defined if there is then we need to use it
                        String minProp= MinHeight.propertyName;
//                        if (property.equalsIgnoreCase(Height.propertyName)){
//                            minProp= MinHeight.propertyName;
//                        }
                        if (property.equalsIgnoreCase(Width.propertyName)){
                            minProp= MinWidth.propertyName;
                        }


                        String ancestorMinWidth = Utils.getValueFromElement(ancestor.getData(), minProp );
                        if (ancestorMinWidth != null) {
                            parentPropNumber = Util.getNumbersFromString(ancestorMinWidth).get(0);
                            if(parentPropNumber>0){
                                isAncestorFound = true;
                                parentAtt= minProp;
                                break;
                            }
                        }
                    }

                    if (!isAncestorFound) {
                        ancestor = ancestor.getParent();
                    }
                }

                if (isAncestorFound) {
                    // We already computed the values so no need to do it agian
//                        String ancestorValue = Utils.getValueFromElement(ancestor.getData().getxPath(), property);
//                        String nodeValue = Utils.getValueFromElement(e.getxPath(), property);
                    if (parentProp.matches(".*\\d+.*") && childProp.matches(".*\\d+.*")) {
                        String parentDynamicProp = Utils.getDynamicValueInDDP(ancestor.getData(), property);
                        String childDynamicProp = Utils.getDynamicValueInDDP(e, property);
                        double parentDynamicPropNumber = Util.getNumbersFromString(parentDynamicProp).get(0);
                        double childDynamicPropNumber = Util.getNumbersFromString(childDynamicProp).get(0);
                        double ratio = parentDynamicPropNumber / childDynamicPropNumber;
//                            String parentDependantProperty = property;  // by the default it is the main property but if it parent is min then we make it min-height or min-width

                        List<SRGNode> dependentNodes = new ArrayList<SRGNode>();
                        dependentNodes.add(new SRGNode(ancestor.getData().getxPath(),ancestor, parentAtt, 1 / ratio, childDynamicPropNumber, parentDynamicPropNumber)); // after considering min-height and min-width

//                        dependentNodes.add(new SRGNode(ancestor.getData().getxPath(),ancestor, property, 1 / ratio, childDynamicPropNumber, parentDynamicPropNumber));
                        propSRG.put(e.getxPath() , dependentNodes);

                    }

                }

                if (node.getChildren() != null) {
                    for (Node<DomNode> child : node.getChildren()) {
                        q.add(child);
                    }
                }
            }


        } //end of    while (!q.isEmpty())  loop




    }


            }









