package usc.edu.SALEM.VHTree;

import gatech.xpert.dom.DomNode;
import usc.edu.SALEM.Constants;
import usc.edu.SALEM.SALEM;
import usc.edu.SALEM.segmentation.Segment;
import usc.edu.SALEM.util.Util;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
import java.util.*;

public class SegmentRelationGraph {
    private final TreeMap<String, List<DependentNode>> dependentNodesMap;        // <xpath, {list of dependent nodes}>
    private int segmentId;

    public SegmentRelationGraph(int segmentId) {
        dependentNodesMap = new TreeMap<String, List<DependentNode>>();
        this.segmentId = segmentId;
    }

    public TreeMap<String, List<DependentNode>> getDependentNodesMap() {
        return dependentNodesMap;
    }


    public int getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(int segmentId) {
        this.segmentId = segmentId;
    }


    public static String DecideTheCorrectValue(String xpath, String cssProperty) {
        /*** Decide what prperty to select based on whether -1,-2, or fixed ***/
        // (1) if it is fixed then return the same main property ( height or width) can be modified directly
        //(2) if it is -2, then the value should be applied through min_width ( adding it if it does not exist)
        //(3) if it is -1, then we can not directly apply the value so we return null so we can mark is as "parent need to be modified"
        Node<DomNode> node = XMLUtils.searchVHTreeByXpath(xpath, XMLUtils.getRoot());


        String currentValue = Util.getValueFromElement(xpath, cssProperty);

        String newProperty = cssProperty;
        if (currentValue != null) {
            double currVal = Util.getNumbersFromString(currentValue).get(0);  // if -2 or -1
            if (currVal >= 0) { //ToDo: think about weight when size is set as zero
                return cssProperty;
            }
            if (currVal == -2) { //if it is wrap content then get the value of minWidth or height instead
                // get the corresponding min property
                String minProperty = Constants.MAIN_PROPERTY_TO_MIN_PROPERTY.get(cssProperty);
                currentValue = Util.getValueFromElement(xpath, minProperty);
                if (currentValue == null) {  // min-height or width does not exist then create it
                    //  (1) if the value does  not exist then add these attributes with zero  ToDo: How to make sure they do not exist in style or default value
                    String unit = "dp"; // ToDo: this should be the same unit using in the activity or the same app
                    String newVal = "0" + unit;

                    node.getData().setAttr(Constants.PROPERTY_TO_FULL_ANDROID_NAME.get(minProperty), newVal);

                }

                return minProperty;
            }
            if (currVal == -1) {
                return null;  // We can not apply the value directly
            }
        }


        return newProperty;
    }

    public String[] createSegmentRelationGraph(Node<DomNode> root, String issue) {
//        switch (issue) {
//            case Constants.TOUCH_TARGET_SIZE_ISSUE: //sizing
        Segment seg = null;
        for (Segment e : SALEM.getOriginalUISegments()) {
            if (e.getId() == segmentId) {
                seg = e;
                break;
            }

        }
        HashMap<String, String> smallestTouchTarget = getSmallestTouchTargetInSegments(seg);
        String[] smallestTouchTargetsXpath = null;
        if (smallestTouchTarget != null) {
            smallestTouchTargetsXpath = createSegmentSizeRelationGraph(seg, smallestTouchTarget, issue);
        }
        return smallestTouchTargetsXpath;
//                    break;
//            case Constants.TOUCH_TARGET_SPACE_ISSUE: //spacing
//                createIncreaseSizeDependancyGraph(root, issueType);
//                break;

        //       }
    }


    private void createNodeDependentPropertyRelation(Node<DomNode> node, String property, double nodeStaticValueNumber) {
        /*** the new approach: create ratio between padding and min_height | or width..... if the property is not there then add them with zero ***/
        double ratio = 0;
        for (String prop : Constants.PROPERTIES_TO_DEPENDENCY_MAPPING.get(property)) {
            if (nodeStaticValueNumber == -2 && prop.contains("min")) {
                continue;  // if the static is set -2 then we do not add relation with min-heigth/min-width
            }
            double nodeDynamicValueNumber = Double.parseDouble(Util.getDynamicValueInDDP(node.getData(), property)); //getting the dynamic value of the node

            String dependentPropValue = Util.getValueFromElement(node.getData().getxPath(), prop); // to see if the value was set already
            if (dependentPropValue != null) {
                if (dependentPropValue.matches(".*\\d+.*")) { /*** What if it set as att in style so no number: handle it later***/
                    double dependentPropValueNumber = Util.getNumbersFromString(dependentPropValue).get(0);
                    if (dependentPropValueNumber == 0) { // if the dependant property is set as zero then the ratio will be infinity and when we divide the new value by infinity it will be zero and that is what we want
                        // it set as zero then the ratio will be
                        ratio = 0;
                    }
                    if (dependentPropValueNumber >= 0) { // if the dependant property is set then we take the ratio
//                        //  03/17/2021 4 am
//                        ratio = nodeStaticValueNumber / dependentPropValueNumber;
                        ratio = nodeDynamicValueNumber / dependentPropValueNumber;
                    }
                    // ToDo: Ratio is it going to be the height or width dynamic or static, right now if height or width set as -2 I am using the current dynamic height set in the min-height or widht


                    DependentNode dn = new DependentNode(node.getData().getxPath(), prop, ratio, nodeDynamicValueNumber, dependentPropValueNumber);
                    if (!dependentNodesMap.containsKey(node.getData().getxPath() + "#" + property)) {
                        // if the node is not already in the map then just initialize its array of dependant node. We d oth at ot avoid null pointer
                        List<DependentNode> dependentNodes = new ArrayList<DependentNode>();
                        dependentNodesMap.put(node.getData().getxPath() + "#" + property, dependentNodes);
                    }
                    dependentNodesMap.get(node.getData().getxPath() + "#" + property).add(dn);
                } else { // if it is not a number then we need to parse it later

                    //ToDO: parse the non-number value
                }
            } else { // the dependant value was not set
                if (prop.contains("padding")) {
                    /***   add padding only if node is:
                     * 1-it is a leave(no children) and it contains an image (TODO: what are elements that can contain images? in Android)
                     * 2- has one child has one child that is an image or image button ( not a leave but has one child that adhere to condition 1
                     *
                     * ***/
                    ratio = Double.POSITIVE_INFINITY; // since in reality we will not use ration but instead add the diff between gene value and dynamic value as the new vlaue
                    if ((node.getChildren() == null || node.getChildren().size() == 0) && Util.doesContainImage(node)) {  //condition 1

                        DependentNode dn = new DependentNode(node.getData().getxPath(), prop, ratio, nodeDynamicValueNumber, Double.POSITIVE_INFINITY);
                        if (!dependentNodesMap.containsKey(node.getData().getxPath() + "#" + property)) {
                            // if the node is not already in the map then just initialize its array of dependant node. We d oth at ot avoid null pointer
                            List<DependentNode> dependentNodes = new ArrayList<DependentNode>();
                            dependentNodesMap.put(node.getData().getxPath() + "#" + property, dependentNodes);
                        }
                        dependentNodesMap.get(node.getData().getxPath() + "#" + property).add(dn);
                    }
                }
            }

        }


    }


    private HashMap<String, String> getSmallestTouchTargetInSegments(Segment seg) {
        // (1) Iterate over the members in the segments
        // (2) based on the issue type find the smallest height, or width

//        HashMap<String,Node<DomNode>> smallestTouchTarget = new HashMap<>();
        HashMap<String, String> smallestTouchTarget = new HashMap<>();

        Node<DomNode> smallestHeightNode = null;
        Node<DomNode> smallestWidthNode = null;
        String smallestHeightXpath = null;
        String smallestWidthXpath = null;
        double minimumHeight = Integer.MAX_VALUE;
        double minimumWidth = Integer.MAX_VALUE;

        for (String elementXpath : seg.getMembers()
        ) {
            Node<DomNode> foundNode = XMLUtils.searchVHTreeByXpath(elementXpath, XMLUtils.getRoot());
            if (foundNode == null || !Util.isElementClickable(foundNode.getData())) {  // we are only interested in touch targets hence checking if element is clickable
                continue;
            }

            // get the height and width in dp

            double height = Util.getNumbersFromString(Util.getDynamicValueInDDP(foundNode.getData(), "height")).get(0);
            double width = Util.getNumbersFromString(Util.getDynamicValueInDDP(foundNode.getData(), "width")).get(0);
            if (height <= minimumHeight) {
                minimumHeight = height;
                smallestHeightNode = foundNode;
                smallestHeightXpath = foundNode.getData().getxPath() + "#" + minimumHeight;
            }
            if (width <= minimumWidth) {
                minimumWidth = width;
                smallestWidthNode = foundNode;
                smallestWidthXpath = foundNode.getData().getxPath() + "#" + minimumWidth;
            }

        }

//if(smallestHeightNode.getData().getxPath()!=null && smallestWidthNode.getData().getxPath()!=null ){
//    if( smallestWidthNode.getData().getxPath().equalsIgnoreCase(smallestHeightNode.getData().getxPath())){
//        smallestTouchTarget.put("both",smallestHeightNode);
//    }
// }else{
//    smallestTouchTarget.put("height",smallestHeightNode);
//    smallestTouchTarget.put("width",smallestWidthNode);
        smallestTouchTarget.put("height", smallestHeightXpath);
        smallestTouchTarget.put("width", smallestWidthXpath);
        //}


        return smallestTouchTarget;

    }

    private String[] createSegmentSizeRelationGraph(Segment seg, HashMap<String, String> smallestTouchTarget, String segmentIssue) {

        List<Element> dependentElements = new ArrayList<>();

        // (1) Retrieve the xpaths of min height nad width and the values
        String valH = smallestTouchTarget.get("height");
        String[] valArr = valH.split("#");
        String smallestHeightXpath = valArr[0];
        double minimumHeight = Util.getNumbersFromString(valArr[1]).get(0);

        String valW = smallestTouchTarget.get("width");
        valArr = valW.split("#");
        String smallestWidthXpath = valArr[0];
        double minimumWidth = Util.getNumbersFromString(valArr[1]).get(0);

        //(2) check what types of issue affect the segemnt? height only, width only or both
        String segmentIssueType = seg.getSizeIssueType();

        String[] smallestTouchTargetsXpath = {smallestHeightXpath, smallestWidthXpath};

        dependentNodesMap.put(smallestHeightXpath + "#" + "height", new ArrayList<DependentNode>());

        dependentNodesMap.put(smallestWidthXpath + "#" + "width", new ArrayList<DependentNode>());


        for (String elementXpath : seg.getMembers()
        ) {
            // (1) reterieve the element dynamic  height and width in DP
            Node<DomNode> currNode = XMLUtils.searchVHTreeByXpath(elementXpath, XMLUtils.getRoot());
            double elementHeight = minimumHeight;  // if it is the same smallest element then no need to retrieve the value again
            if (!elementXpath.equalsIgnoreCase(smallestHeightXpath)) {
                // Not element with smallest Height
                elementHeight = Util.getNumbersFromString(Util.getDynamicValueInDDP(currNode.getData(), "height")).get(0);

            }
            double elementWidth = minimumWidth;
            if (!elementXpath.equalsIgnoreCase(smallestWidthXpath)) {
                // Not element with smallest width
                elementWidth = Util.getNumbersFromString(Util.getDynamicValueInDDP(currNode.getData(), "width")).get(0);
            }


            if (!dependentNodesMap.containsKey(elementXpath + "#" + "height")) {  // key does not exist yet
                dependentNodesMap.put(elementXpath + "#" + "height", new ArrayList<DependentNode>());
            }
            //(2) Model relationship between element width and height  ( edge between element and itself
            double heightToWidthRatio = elementHeight / elementWidth;
            dependentNodesMap.get(elementXpath + "#" + "height").add(new DependentNode(elementXpath, "width", heightToWidthRatio, elementHeight, elementWidth)); // min-height depends on height
            // (3) model teh relationship between node and its dependent properties  || Once for width and once for height
            String val = Util.getValueFromElement(currNode.getData(), "height");
            double nodeStaticVal = Util.getNumbersFromString(val).get(0);
            createNodeDependentPropertyRelation(currNode, "height", nodeStaticVal);

            val = Util.getValueFromElement(currNode.getData(), "width");
            nodeStaticVal = Util.getNumbersFromString(val).get(0);
            createNodeDependentPropertyRelation(currNode, "width", nodeStaticVal);

            //(3) for all other elements in the segments get their ratio to the smallest ratio
//            if (segmentIssueType.equalsIgnoreCase("both") || segmentIssueType.equalsIgnoreCase("height")) { // Only consider increasing the height when the segment suffer from that
            if (!elementXpath.equalsIgnoreCase(smallestHeightXpath)) { // because if it is the smallest height then we are going to start with that so no need to add relationship here
                // Not element with smallest Height
                double ratio = minimumHeight / elementHeight;
                /***  NOTE that we are  adding that to the smallest element key not the current key   ***/
                dependentNodesMap.get(smallestHeightXpath + "#" + "height").add(new DependentNode(elementXpath, "height", ratio, minimumHeight, elementHeight)); // min-height depends on height

            }
            //  }
//            if (segmentIssueType.equalsIgnoreCase("both") || segmentIssueType.equalsIgnoreCase("width")) {
            if (!elementXpath.equalsIgnoreCase(smallestWidthXpath)) { // same for width, only caclculate when it is not the elelemt with smallest width
                // Not element with smallest Width
                double ratio = minimumWidth / elementWidth;
                /***  NOTE that we are  adding that to the smallest element key not the current key   ***/
                dependentNodesMap.get(smallestWidthXpath + "#" + "width").add(new DependentNode(elementXpath, "width", ratio, minimumWidth, elementWidth)); // min-height depends on height
            }

            // }

        }


        return smallestTouchTargetsXpath;
    }


    public List<Element> getElementsToChange(String segmentIssueId, String cssProperty, String newValue, String issueType,
                                             ArrayList<String> smallestElementXpath, HashMap<String, ArrayList<MatchParentElement>> matchParentMap) {
        List<Element> elementsToChange = new ArrayList<>();

        switch (issueType) {
//            case Constants.TOUCH_TARGET_SPACE_ISSUE:
//                elementsToChange = getElementsToChangeForTouchTargetSpace(cssProperty, newValue);
//                break;
            case Constants.TOUCH_TARGET_SIZE_ISSUE:
                elementsToChange = getElementsToChangeForTouchTargetSize(segmentIssueId, cssProperty, newValue, smallestElementXpath, matchParentMap);
                break;
//            case Constants.TOUCH_TARGET_HEIGHT_ISSUE:
//                elementsToChange = getElementsToChangeForTouchTargetSize(segmentIssueId, cssProperty, newValue, smallestElementXpath);
//
//                break;
//            case Constants.TOUCH_TARGET_WIDTH_ISSUE:
//                elementsToChange = getElementsToChangeForTouchTargetSize(segmentIssueId, cssProperty, newValue, smallestElementXpath);
//                break;
//			case Constants.CONTENT_SIZE_PROBLEM:
//				elementsToChange = getElementsToChangeForContentSizeIssue(cssProperty, value);
//				break;
            default:
                System.out.println("Issue type " + issueType + " not supported");
        }
        return elementsToChange;
    }

    private List<Element> getElementsToChangeForTouchTargetSize(String segmentIssueId, String cssProperty, String geneNewValue,
                                                                ArrayList<String> smallestElementsXpath, HashMap<String, ArrayList<MatchParentElement>> matchParentMap) {
        int segIndex = segmentId - 1;// since we are reteriving from a list where index start from zero then -1
        Segment seg = SALEM.getOriginalUISegments().get(segIndex);
        String segIssueType = seg.getSizeIssueType();
        List<Element> dependentElements = new ArrayList<>();
        DependencyGraph dg = null;
        TreeMap<String, List<DependentNode>> dgTreeMap = null;
        String unit = "dp"; //// ToDo: Placeholder until I figure out the size
        String smallestHeightXpath = smallestElementsXpath.get(0);
        String smallestWidthXpath = smallestElementsXpath.get(0);
        String xpath;
        if (smallestElementsXpath.size() > 1) {
            //    that means the  element with  the smallest width is different from the element with smallest height
            //ToDo: what should we do in that case?
            smallestWidthXpath = smallestElementsXpath.get(1);
        }

        switch (segIssueType) {
            case Constants.TOUCH_TARGET_HEIGHT_ISSUE:
                xpath = smallestHeightXpath;
                getElementsToChangeFromSegmentsGraph(Constants.TOUCH_TARGET_HEIGHT_ISSUE, dependentElements, matchParentMap,
                        "height", unit, xpath, geneNewValue);
                break;

            case Constants.TOUCH_TARGET_WIDTH_ISSUE:
                /*** (2) if the segment has only size width issue then
                 * start with smallest width then add values for all width in the segment according to the ratio
                 * then if height-to-width enabled apply height to with ratio for each element ToDo: HOW??
                 */
                xpath = smallestWidthXpath;
                getElementsToChangeFromSegmentsGraph(Constants.TOUCH_TARGET_WIDTH_ISSUE, dependentElements, matchParentMap,
                        "width", unit, xpath, geneNewValue);
                break;

            case Constants.TOUCH_TARGET_BOTH_ISSUE:
                /*** (3) if the segment has both size height and width issue then
                 * start with smallest height then add values for all height in the segment according to the ratio
                 * then Go to  smallest width element then add values for all width in the segment according to the ratio
                 * then IMPORTANTLY DECIDE HOW TO COMBINE THAT WITH  height-to-width  ToDo: HOW??
                 */

                xpath = smallestHeightXpath;
                getElementsToChangeFromSegmentsGraph(Constants.TOUCH_TARGET_BOTH_ISSUE, dependentElements, matchParentMap,
                        "height", unit, xpath, geneNewValue);

                xpath = smallestWidthXpath;
                getElementsToChangeFromSegmentsGraph(Constants.TOUCH_TARGET_BOTH_ISSUE, dependentElements, matchParentMap,
                        "width", unit, xpath, geneNewValue);

                break;

        }
//        for (String key: matchParentMap.keySet()){
//        ArrayList<MatchParentElement> matchParentArr=    matchParentMap.getOrDefault(key,null);
//        double maxHeightIncrease=-100.0;
//        String heightProp="";
//            String widthProp="";
//        double maxWidthIncrease=-100.0;
//
//            for (MatchParentElement mt:matchParentArr
//                 ) {
//              String val=  mt.getValue();
//              String prop=mt.getCssProperty();
//                if (prop.equalsIgnoreCase("height") ||
//                        prop.equalsIgnoreCase(Constants.MAIN_PROPERTY_TO_MIN_PROPERTY.get("height"))) {
//                    double curr_val = Util.getNumbersFromString(val).get(0);
//                    maxHeightIncrease = Double.max(curr_val, maxHeightIncrease);
//                    heightProp=prop;
//                }
//                else if (mt.getCssProperty().equalsIgnoreCase("width") ||
//                        mt.getCssProperty().equalsIgnoreCase(Constants.MAIN_PROPERTY_TO_MIN_PROPERTY.get("width"))) {
//                    double curr_val = Util.getNumbersFromString(val).get(0);
//                    maxWidthIncrease = Double.max(curr_val, maxWidthIncrease);
//                    widthProp=prop;
//                }
//
//                }
//        if(maxWidthIncrease>0){
//            Element m=new Element(key,widthProp,String.valueOf(maxWidthIncrease),true,matchParentArr.get(0).getNode());
//        }
//            if(maxHeightIncrease>0){
//                Element m=new Element(key,heightProp,String.valueOf(maxHeightIncrease),true,matchParentArr.get(0).getNode());
//            }
//        }
        return dependentElements;
    }

    private void getElementsToChangeFromSegmentsGraph(String increaseType, List<Element> dependentElements, HashMap<String, ArrayList<MatchParentElement>> matchParentMap,
                                                      String property, String unit, String smallestPropertyXpath, String geneNewValue) {
        /*** Add the new value to the smallest element of the "property" (e.g., height or width)
         *         then add to the other elements in the segments based on the ratio
         *
         */
        // (1) Add the value for the smallest height element
        String segmentIssueId = "";
        if (property.equalsIgnoreCase("height")) {
            segmentIssueId = "S0_TouchTargetHeightIssue";
        } else if (property.equalsIgnoreCase("width")) {
            segmentIssueId = "S0_TouchTargetWidthIssue";
        }
        DependencyGraph dg = null;
        TreeMap<String, List<DependentNode>> dgTreeMap = null;
        Map<String, DependencyGraph> xxxW = SALEM.getSegmentToDG();
        dg = SALEM.getSegmentToDG().get(segmentIssueId); // get the dependancy graph for that issue
        if (dg != null) {  // No dependency for height
            dgTreeMap = dg.getDependentNodesMap();
        }
        String xpath = smallestPropertyXpath;
        String correctProperty = DecideTheCorrectValue(xpath, property); // if height is fixed then just add the value as height , if it is -2 then instead add min_height | ToDO: check again aidownloacer lower bar issue
        Node<DomNode> currNode = XMLUtils.searchVHTreeByXpath(xpath, XMLUtils.getRoot());
        double geneValNo = Util.getNumbersFromString(geneNewValue).get(0);
        if (correctProperty == null) { // the size is set as -1 so we can not directly change it directly but instead we need to directly modify its dependant parent
            Node<DomNode> childNode = currNode;

            if (childNode != null) {

                String parentValue = handleElementsWithMatchParent(xpath, property, geneNewValue, dependentElements, dgTreeMap, matchParentMap);// if element can not be increase directly because of -1 then inreae parent
                if (parentValue == null) {
                    String min_property = Constants.MAIN_PROPERTY_TO_MIN_PROPERTY.get(property);
                    addDependentElements(xpath, min_property, geneNewValue + unit, false, currNode, dependentElements);
                    addDependentElements(xpath, property, "-2", false, currNode, dependentElements);


                }
            }
        } else {
            addDependentElements(xpath, correctProperty, geneNewValue + unit, false,
                    currNode, dependentElements);

//            dependentElements.add(new Element(xpath, correctProperty, geneNewValue + unit, false, currNode)); //Todo: Add the unit

        }
        if (increaseType.equalsIgnoreCase(Constants.TOUCH_TARGET_HEIGHT_ISSUE) ||
                increaseType.equalsIgnoreCase(Constants.TOUCH_TARGET_BOTH_ISSUE)) {
            // if segment issue is both or widht then no need to handle height width ratio

            addHeightWidthRatioUpdate(xpath, currNode, property, geneValNo, dependentElements);
        }
        //For remaining nodes

        List<DependentNode> dependentNodes = dependentNodesMap.get(xpath + "#" + property); // get all the property(height,width) dependant nodes, which basically mean the other nodes in the segment
        String currXpath;
        if (dependentNodes != null && dependentNodes.size() > 0) {
            for (DependentNode dNode : dependentNodes) {
                if (dNode.getXpath().equalsIgnoreCase(smallestPropertyXpath)) {
                    // than means it is dpenendt property (padding, min-heigth etc) that we already handled so skip them
                    continue;
                }
                // (3 a) first add the new height value based on the ratio with the smallest
                double dRatio = dNode.getRatio();
                if (property.equalsIgnoreCase("width")) {// ToDo: when width .. How to calculate the ration? 1/ ratio?
                    dRatio = 1 / dRatio;
                }
                double dElementValue = geneValNo / dRatio;
                currXpath = dNode.getXpath();
                Node<DomNode> currSiblingNode = XMLUtils.searchVHTreeByXpath(currXpath, XMLUtils.getRoot());

                // Now we need to check if we can add the value directly or do we need to consider mi-height or add it to the list where parent need increase if it is -1
                correctProperty = DecideTheCorrectValue(currXpath, property); // if height is fixed then just add the value as height , if it is -2 then instead add min_height | ToDO: check again aidownloacer lower bar issue

                if (correctProperty == null) { // the size is set as -1 so we can not directly change it directly but instead we need to directly modify its dependant parent


                    if (currSiblingNode != null) {

                        String parentDependencyExist = handleElementsWithMatchParent(currXpath, property, dElementValue + "", dependentElements, dgTreeMap, matchParentMap);// if element can not be increase directly because of -1 then inreae parent
                        if (parentDependencyExist == null) {
                            String min_property = Constants.MAIN_PROPERTY_TO_MIN_PROPERTY.get(property);
                            addDependentElements(currXpath, min_property, dElementValue + unit, false, currSiblingNode, dependentElements);


                        }
                    }
                } else { // not set as match pareth
                    addDependentElements(currXpath, correctProperty, dElementValue + unit, false, currSiblingNode, dependentElements);
                    addDependentElements(currXpath, correctProperty, dElementValue + unit, false, currSiblingNode, dependentElements);

                    // dependentElements.add(new Element(currXpath, correctProperty, dElementValue + unit, false, currSiblingNode)); //Todo: Add the unit

                }
                if (increaseType.equalsIgnoreCase(Constants.TOUCH_TARGET_HEIGHT_ISSUE) ||
                        increaseType.equalsIgnoreCase(Constants.TOUCH_TARGET_BOTH_ISSUE)) {
                    // if segment issue is both or widht then no need to handle height width ratio

                    addHeightWidthRatioUpdate(currXpath, currSiblingNode, property, dElementValue, dependentElements);
                }
            }
        }


    }

    private void addDependentElements(String xpath, String property, String newValue, boolean b, Node<DomNode> currNode, List<Element> dependentElements) {
        double valToAdd = Util.getNumbersFromString(newValue).get(0);
        for (Element dNode : dependentElements
        ) {
            if (dNode.getXpath().equalsIgnoreCase(xpath)) {
                if (dNode.getCssProperty().equalsIgnoreCase(property)) {
                    double elementVal = Util.getNumbersFromString(dNode.getValue()).get(0);
                    valToAdd = Double.max(valToAdd, elementVal);
                }
            }
        }

        dependentElements.add(new Element(xpath, property, valToAdd + "dp", b, currNode)); //Todo: Add the unit


    }

    private void addElementDependentPropertyChange(List<Element> dependentElements, String xpath,
                                                   String property, String geneNewVal) {

        double geneNewValue = Util.getNumbersFromString(geneNewVal).get(0);
        Node<DomNode> node = XMLUtils.searchVHTreeByXpath(xpath, XMLUtils.getRoot());
        String unit = "dp";
        double nodeDynamicValue = Double.valueOf(Util.getDynamicValueInDDP(node.getData(), property));
        List<DependentNode> dependentNodes = dependentNodesMap.get(xpath + "#" + property); // get all the height dependant nodes, which basically mean the other nodes in the segment
        if (dependentNodes != null && dependentNodes.size() > 0) {
            Element dElement;
            for (DependentNode dNode : dependentNodes) {
                dElement = null;
                if (!dNode.getXpath().equalsIgnoreCase(xpath)) {// we are only interested in depenendant property for the node so ignore any dependency with different xpath
                    continue;
                }
                if (dNode.getProperty().equalsIgnoreCase("width")) { // that means it is node's height to width property, we handle this in different method so ignore that
                    continue;
                }
                double dependentPropertyNewValue;
                Node<DomNode> currNode = XMLUtils.searchVHTreeByXpath(dNode.getXpath(), XMLUtils.getRoot());
                if (dNode.getRatio() == Double.POSITIVE_INFINITY) {
                    //that means this propery was not set originally but we add it (such as pading for images)
                    // To handled these we add its value as difference between gene new value and the dynamic size of the element( i.e., same size increas)
                    /***   Since it is padding and we are taking left/right or top/bottom so we divide by two ***/


                    dependentPropertyNewValue = (geneNewValue - nodeDynamicValue) / 2.0;
                    if (dependentPropertyNewValue > 0) {
                        dElement = new Element(dNode.getXpath(), dNode.getProperty(), dependentPropertyNewValue + unit, false, currNode);
                    }
                } else {
                    // value exists so apply the ratio value
                    dependentPropertyNewValue = geneNewValue / dNode.getRatio();
                }
                dElement = new Element(dNode.getXpath(), dNode.getProperty(), dependentPropertyNewValue + unit, false, currNode);
                dependentElements.add(dElement);
            }
        }
    }


    private String handleElementsWithMatchParent(String xpath, String property, String newValue, List<Element> dependentElements,
                                                 TreeMap<String, List<DependentNode>> dgTreeMap,
                                                 HashMap<String, ArrayList<MatchParentElement>> matchParentMap) {
        /*** for eleemnts that has -1 as size we can not directly increase them, we increase the parent that that element
         *  depened on
         *  This method return add to the parentIncreaseList
         *
         *  ***/
        boolean hasParentDependnet = false;
        String parentNewValue = null;
        String parentXpath = null;
        String propKey = xpath + "#" + property;
        if (dgTreeMap != null && dgTreeMap.containsKey(propKey.trim())) {  // check if the element has dependancy node to get the parent
            List<DependentNode> dependentNodes = dgTreeMap.get(propKey);


            for (DependentNode dNode : dependentNodes
            ) {
//                    if (!dNode.getProperty().equalsIgnoreCase("height")) { // only the dependant parent have height propery, other dNode are padding and min-height
                if (dNode.getXpath().equalsIgnoreCase(xpath)) { // Ignore dependancy of the element to itself

                    continue;
                } else {
                    // now two cases, either the parent  exists in the dependntElement that means it was already increased...
                    // and now we need to see if new value is larger the value already increased
                    // The second cases is that it does not exist in  not which mean the size was set as -1 so we could not add it to the list of changes
                    hasParentDependnet = true;
                    parentXpath = dNode.getXpath();// get the dependant node that we need to increase


                    // (1) get the child and parent dynamic size
                    Node<DomNode> childNode = XMLUtils.searchVHTreeByXpath(xpath, XMLUtils.getRoot());
                    if (childNode == null) {
                        continue;
                    }
                    Node<DomNode> parentNode = XMLUtils.searchVHTreeByXpath(parentXpath, XMLUtils.getRoot());
                    if (parentNode == null) {
                        continue;
                    }
                    // get child dynamic value and parent dynamic value and then get the diff value
                    String childV = Util.getDynamicValueInDDP(childNode.getData(), property);
                    double childDynamicValForP = Util.getNumbersFromString(childV).get(0);
                    String parentV = Util.getDynamicValueInDDP(parentNode.getData(), property);
                    double parentDynamicValForP = Util.getNumbersFromString(parentV).get(0);

                    double geneVal = Util.getNumbersFromString(newValue).get(0);  // the gene value to be applied
                    double diff = geneVal - childDynamicValForP; // between the needed increase for the child and gene value

                    double newParentValue = parentDynamicValForP + diff;
                    parentNewValue = String.valueOf(newParentValue);

                    String cXpath = xpath;
                    if (!matchParentMap.containsKey(parentNode.getData().getxPath())) {
                        ArrayList<MatchParentElement> mElements = new ArrayList<>();
                        matchParentMap.put(parentXpath, mElements);
                    }
                    String correctParentProperty = DecideTheCorrectValue(parentXpath, property);// Most likely it won't be null because it can not be -1 otherwise it won't be in dependancy graph
                    MatchParentElement matchElement = new MatchParentElement(parentXpath, correctParentProperty, parentNewValue, true,
                            parentNode, childNode, cXpath, property);

                    matchParentMap.get(parentXpath).add(matchElement);


                }

            }
//    return hasParentDependnet;
        }


        if (parentNewValue != null && parentXpath != null) {

            cascadeParentChange(parentXpath, parentNewValue, property, dgTreeMap, matchParentMap);
            return parentXpath + "#" + parentNewValue;

        } else {
            return null;
        }
    }

    private void cascadeParentChange(String pxpath, String newValue, String property, TreeMap<String, List<DependentNode>> dgTreeMap, HashMap<String, ArrayList<MatchParentElement>> matchParentMap) {
        String cascadeType = "Single";
        Queue<String> q = new LinkedList<String>();
        q.add(pxpath);
        while (!q.isEmpty()) {
            String xpath = q.remove();

            String propKey = xpath + "#" + property;
            if (dgTreeMap != null && dgTreeMap.containsKey(propKey.trim())) {  // check if the element has dependancy node to get the parent
                List<DependentNode> dependentNodes = dgTreeMap.get(propKey);


                for (DependentNode dNode : dependentNodes
                ) {
                    if (dNode.getXpath().equalsIgnoreCase(xpath)) { // Ignore dependancy of the element to itself

                        continue;
                    } else {
                        String parentXpath = dNode.getXpath();// get the dependant node that we need to increase


                        // (1) get the child and parent dynamic size
                        Node<DomNode> childNode = XMLUtils.searchVHTreeByXpath(xpath, XMLUtils.getRoot());
                        if (childNode == null) {
                            continue;
                        }
                        Node<DomNode> parentNode = XMLUtils.searchVHTreeByXpath(parentXpath, XMLUtils.getRoot());
                        if (parentNode == null) {
                            continue;
                        }
                        // get child dynamic value and parent dynamic value and then get the diff value
                        String childV = Util.getDynamicValueInDDP(childNode.getData(), property);
                        double childDynamicValForP = Util.getNumbersFromString(childV).get(0);
                        String parentV = Util.getDynamicValueInDDP(parentNode.getData(), property);
                        double parentDynamicValForP = Util.getNumbersFromString(parentV).get(0);

                        double geneVal = Util.getNumbersFromString(newValue).get(0);  // the gene value to be applied
                        double diff = geneVal - childDynamicValForP; // between the needed increase for the child and gene value

                        double newParentValue = parentDynamicValForP + diff;
                        String parentNewValue = String.valueOf(newParentValue);

                        String cXpath = xpath;
                        if (!matchParentMap.containsKey(parentNode.getData().getxPath())) {
                            ArrayList<MatchParentElement> mElements = new ArrayList<>();
                            matchParentMap.put(parentXpath, mElements);
                        }
                        String correctParentProperty = DecideTheCorrectValue(parentXpath, property);// Most likely it won't be null because it can not be -1 otherwise it won't be in dependancy graph
                        MatchParentElement matchElement = new MatchParentElement(parentXpath, correctParentProperty, parentNewValue, true,
                                parentNode, childNode, cXpath, property);

                        matchParentMap.get(parentXpath).add(matchElement);
                        q.add(parentXpath);
                    }
                }


            }
            if (cascadeType.equalsIgnoreCase("Single")) {
                return;
            }
        }

    }
//    private String increaseParentSize(String xpath, String property, String geneNewValue, List<Element> dependentElements,
//                                      TreeMap<String, List<DependentNode>> dgTreeMap) {
//        /*** for eleemnts that has -1 as size we can not directly increase them, we increase the parent that that element
//         *  depened on
//         *  This method return parentXpath#New value for parent
//         *
//         *  ***/
//
//        // (1) check if parent already increased before, and if yes check if required increase is less or more than before
//
//        //ToDo: add
//        //(2)
//        String parentNewValue = null;
//        String parentXpath = null;
//        String propKey = xpath + "#" + property;
//        if (dgTreeMap != null && dgTreeMap.containsKey(propKey.trim())) {  // check if the element has dependancy node to get the parent
//            List<DependentNode> dependentNodes = dgTreeMap.get(propKey);
//            for (DependentNode dNode : dependentNodes
//            ) {
////                    if (!dNode.getProperty().equalsIgnoreCase("height")) { // only the dependant parent have height propery, other dNode are padding and min-height
//                if (dNode.getXpath().equalsIgnoreCase(xpath)) { // Ignore dependancy of the element to itself
//
//                    continue;
//                } else {
//                    // now two cases, either the parent  exists in the dependntElement that means it was already increased...
//                    // and now we need to see if new value is larger the value already increased
//                    // The second cases is that it does not exist in  not which mean the size was set as -1 so we could not add it to the list of changes
//                    parentXpath = dNode.getXpath();// get the dependant node that we need to increase
//
//
//                    //(1) get the child and parent dynamic size
//                    Node<DomNode> childNode = XMLUtils.searchHtmlDomTreeByXpath(xpath, XMLUtils.getRoot());
//
//                    if(childNode==null){
//                        continue;
//                    }
//                    double childTotalDynamicValForP= getTotalChildVal(childNode,property); // include dynamic val for property + margin
//                    Node<DomNode> parentNode = XMLUtils.searchHtmlDomTreeByXpath(parentXpath, XMLUtils.getRoot());
//                    if(parentNode==null){
//                        continue;
//                    }
//                    double parentTotalAvailableSizeForP= getTotalParentAvialableSpaceVal(parentNode,childNode,property); // include dynamic val for property + padding ( not margin since it is outside of parent)
//
//                    double geneVal = Util.getNumbersFromString(geneNewValue).get(0);  // the gene value to be applied
//
//                    String parentV = Util.getDynamicValueInDDP(parentNode.getData(), property);
//                    double parentDynamicValForP = Util.getNumbersFromString(parentV).get(0);
//                    //(2) get  the difference between it and the newgenevalue
//
//                    double diff = geneVal - childTotalDynamicValForP; // between the needed increase for the child and gene value
//                    double newParentValue = parentDynamicValForP;
////                    double parentChildDiff=newParentValue-childTotalDynamicValForP; // later find the available space more accuretly
////                    if (diff > childVal) {
//                    if (diff +2 > parentTotalAvailableSizeForP) {
//                        newParentValue = newParentValue + diff+2;
//                    }
//                    Element currElement = Util.findElementbyXpath(dependentElements, parentXpath);
//                    if (currElement == null) { // the element was not part of depenentElements so it was not added yet
////                            double geneValue = Util.getNumbersFromString(geneNewValue).get(0);
////                            double newParentValue = geneValue / dNode.getRatio();
//                        // ToDo:do we need to increase with the same ratio?
//                        //  I think here we are not interested in increasing the parent to keep the ratio but
//                        //  rather make sure the parent can accomidate hte change
//
//                        //(3) add the difference to the parent
//                        parentNewValue = newParentValue + "dp";
//                        dependentElements.add(new Element(dNode.getXpath(), dNode.getProperty(), parentNewValue));
//                    } else {
//                        // The parent dependent already exist in the dependentNode that means a change is already proposed
//                        // so now we have to check if the new value is larger than the suggested increase
//                        Element pElement = Util.findElementbyXpath(dependentElements, parentXpath);  // Just checking if the parent was part of the segment so we need to consider the larger value
//                        double pElementValue = Util.getNumbersFromString(pElement.getValue()).get(0);
//                        if (newParentValue > pElementValue) {  // if the newParentValue is greater than the value already added in the ElementNodes
//                            parentNewValue = newParentValue + "dp";
//                            pElement.setValue(parentNewValue);
//                        }
//                    }
//                }
//
//            }
//
//        }
//
//
//        if (parentNewValue != null && parentXpath != null) {
//            return parentXpath + "#" + parentNewValue;
//        } else {
//            return null;
//        }
//    }

    private double getTotalParentAvialableSpaceVal(Node<DomNode> parentNode, Node<DomNode> childInReference, String property) {
        /*** childInReference --> The child we need to increase and we need to check its parent ***/

        // right now I am simple considereing the child
        double totalchildVal = getTotalChildVal(childInReference, property);
        String parentV = Util.getDynamicValueInDDP(parentNode.getData(), property);
        double parentVal = Util.getNumbersFromString(parentV).get(0);

        return parentVal - totalchildVal;
    }

    private double getTotalChildVal(Node<DomNode> childNode, String property) {
        //get property: height or witdh then calcualte the dnamic value for that + margins
        String childV = Util.getDynamicValueInDDP(childNode.getData(), property);
        double childVal = Util.getNumbersFromString(childV).get(0);
        return childVal;
    }


    private void addChangeToSizeDependencyGraph(TreeMap<String, List<DependentNode>> dgTreeMap, List<Element> dependentElements,
                                                HashMap<String, ArrayList<String>> elementsNeedParentIncrease, List<Element> dependentParentElements, HashMap<String, Double> parentMaxIncrease,
                                                String property, String geneNewValue) {

        for (String dgKey : dgTreeMap.keySet()) {
            if (dependentNodesMap.containsKey(dgKey.trim())) { // if this element is part of the SegmentSizeRelationGraph nodes
                List<DependentNode> dependentNodes = dgTreeMap.get(dgKey);
                String dgElementXpath = Util.getXpathFromDGkey(dgKey); // to get xpath from xpath#property
                for (DependentNode dNode : dependentNodes
                ) {
                    if (dNode.getXpath().equalsIgnoreCase(dgElementXpath)) { // Ignore dependency of the element to itself (that include relation to min-heigth, padding and so on)
                        continue;
                    } else {
                        // now two cases, either the node exists in the dependntElement and we need to see if increase is needed, or it does not which mean the size was set as -1 so we could not add it to the list of changes
                        Element currElement = Util.findElementbyXpath(dependentElements, dgElementXpath);
                        String parentXpath = dNode.getXpath(); //ToDo: check if the parent is really in the denominator
                        Node<DomNode> parentNode = XMLUtils.searchVHTreeByXpath(parentXpath, XMLUtils.getRoot());
                        String parentV = Util.getDynamicValueInDDP(parentNode.getData(), property); // get the dynamic value for the parent to see if it can fit the increase
                        double parentVal = Util.getNumbersFromString(parentV).get(0);


                        if (currElement == null) { // the element was not part of depenentElements so maybe it was set as -1?
//                            double geneValue = Util.getNumbersFromString(geneNewValue).get(0);
//                            double newParentValue = geneValue / dNode.getRatio();  //Todo: check * or /
//                            dependentParentElements.add(new Element(dNode.getXpath(), dNode.getProperty(), newParentValue + "dp"));
                            /*** Above approach increase parent size using ratio to chid size.. I do not think we need that increase,,
                             * Instead, I am going to increase parent by only the amount of increase needed ( newgeneval- currdynamicChildVal)
                             */

                            Node<DomNode> childNode = XMLUtils.searchVHTreeByXpath(dgElementXpath, XMLUtils.getRoot());
                            String childV = Util.getDynamicValueInDDP(childNode.getData(), property);
                            double childVal = Util.getNumbersFromString(childV).get(0);
                            double geneVal = Util.getNumbersFromString(geneNewValue).get(0);

                            // get  the difference between newgenevalue and dynamic child val

                            double diff = geneVal - childVal;
                            double newParentValue = parentVal;
                            if (diff > childVal) {
                                newParentValue = parentVal + diff;
                            }
                            dependentParentElements.add(new Element(dNode.getXpath(), dNode.getProperty(), newParentValue + "dp", false, parentNode));

                        } else {

                            boolean res = Util.canParentAccomidateChange(dgElementXpath, parentXpath, property, currElement.getValue());
                            double childSuggestedIncreaseVal = Util.getNumbersFromString(currElement.getValue()).get(0);

                            if (!res) { // parent can not accommodate the change so we need to increase it
                                if (!parentMaxIncrease.containsKey(parentXpath)) {
                                    parentMaxIncrease.put(parentXpath, 0.0);
                                }
                                double lastValueComparedWithParent = parentMaxIncrease.get(parentXpath);

                                if (lastValueComparedWithParent < childSuggestedIncreaseVal) { // the new value is larger than the previous then we  have to update the value
                                    double newValue = (childSuggestedIncreaseVal + 4);
                                    dependentParentElements.add(new Element(parentXpath, property, newValue + "dp", false, parentNode)); //Todo: I am randomly adding the value
                                    parentMaxIncrease.put(parentXpath, newValue);
                                }
                            }
                        }
                    }

                }
            }
        } // end of for


        for (Element pElement : dependentParentElements
        ) {
            String pXpath = pElement.getXpath();
            Element cElement = Util.findElementbyXpath(dependentElements, pXpath);  // Just checking if the parent was part of the segment so we need to consider the larger value
            if (cElement != null) { // the node already exist
                double pValue = Util.getNumbersFromString(pElement.getValue()).get(0);
                double cValue = Util.getNumbersFromString(cElement.getValue()).get(0);
                if (pValue > cValue) { //the new value is larger than the value already exist in the list of Element ot be changed so we need to update element value with the new parent value
                    cElement.setValue(pElement.getValue());
                }
            } else {  // if the element was not already added as part of segment modification then just add that to the list of element to be changed
                dependentElements.add(pElement);
            }
        }


    }

    private void addHeightWidthRatioUpdate(String xpath, Node<DomNode> currNode, String property,
                                           double geneValNo, List<Element> dependentElements) {

        if (!SALEM.CONSIDER_HEIGHT_WIDTH_RATIO) {
            return; // no need to cosider height width ratio
        }
        // consider and update width  based on height change to make sure it is not stretched
        if (property.equalsIgnoreCase("height")) {
            List<DependentNode> dependentNodes = dependentNodesMap.get(xpath + "#" + property); // get all the property(height,width) dependant nodes, which basically mean the other nodes in the segment

            String currXpath;
            if (dependentNodes != null && dependentNodes.size() > 0) {
                for (DependentNode dNode : dependentNodes) {
                    if (dNode.getXpath().equalsIgnoreCase(xpath)) {
                        // that means it is relationship between element and itself
                        if (!dNode.getProperty().equalsIgnoreCase("width")) {
                            continue;   // we do not want dependent properties, we only need width
                        }
                        // now we have width
                        // هنا انا قاعد اضيف العرض مباشره مع اني مفروض اشيك هل اجط minwidth or widht مباشرة
                        double widthNewValue = (geneValNo) / dNode.getRatio();
                        double newVal = widthNewValue;
                        double newRatio = geneValNo / widthNewValue;
                        double ratioWithOldWidth = geneValNo / dNode.getDenominator();
                        if (ratioWithOldWidth - dNode.getRatio() > 0.40 && ratioWithOldWidth > 0.85) {
                            Node<DomNode> node = XMLUtils.searchVHTreeByXpath(dNode.getXpath(), XMLUtils.getRoot());
                            if (node.getData().getTagName().contains("Button")) {
                                addDependentElements(xpath, "width", newVal + "dp", false,
                                        currNode, dependentElements);
                            }
                        }
                        if (dNode.getRatio() < 0.30) {
                            //continue;
                            newRatio = geneValNo / widthNewValue;
                            if ((newRatio) > 0.80) { // ratio changed significantlly
                                System.out.println("Add ratio: " + newRatio);
                                newVal = geneValNo / 0.5;
//                                    addDependentElements(xpath,"width",newVal + "dp" , false,
//                                            currNode,dependentElements);
                            }
                        } else if (dNode.getRatio() > 0.95) {
                            // almost same
                            newRatio = geneValNo / widthNewValue;
                            if (newRatio > 1) { //relation switched
                                newVal = widthNewValue;
//                                    addDependentElements(xpath,"width",newVal + "dp" , false,
//                                            currNode,dependentElements);
                            }

                        }


//                            dependentElements.add(new Element(xpath, "width",
//                                    widthNewValue + "dp", false, currNode)); //Todo: Add the unit

                    }
                }
            }
        }


    }
}


