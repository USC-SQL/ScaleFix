//package usc.edu.OwlEye.VHTree;
//
//import gatech.xpert.dom.DomNode;
//import gatech.xpert.dom.Node;
//import usc.edu.SALEM.Constants;
////import usc.edu.SALEM.SALEM;
//import usc.edu.SALEM.util.Util;
//
//import java.util.*;
//
//
//public class DependencyGraph {
//    private TreeMap<String, List<DependentNode>> dependentNodesMap;        // <xpath, {list of dependent nodes}>
//    private int segmentId;
//
//    public DependencyGraph(int segmentId) {
//        dependentNodesMap = new TreeMap<String, List<DependentNode>>();
//        this.segmentId = segmentId;
//    }
//
//    public TreeMap<String, List<DependentNode>> getDependentNodesMap() {
//        return dependentNodesMap;
//    }
//
//    public void setDependentNodesMap(TreeMap<String, List<DependentNode>> dependentNodesMap) {
//        this.dependentNodesMap = dependentNodesMap;
//    }
//
//    public int getSegmentId() {
//        return segmentId;
//    }
//
//    public void setSegmentId(int segmentId) {
//        this.segmentId = segmentId;
//    }
//
//    public void createDependencyGraph(Node<DomNode> root, String issueType) {
//        switch (issueType) {
////            case Constants.TOUCH_TARGET_SIZE_ISSUE:
////                createIncreaseSizeDependancyGraph(root, issueType);
////                break;
//            case Constants.TOUCH_TARGET_SPACE_ISSUE:
//                createDependencyGraphForTouchTargetSpace(root);
//                break;
//            case Constants.TOUCH_TARGET_HEIGHT_ISSUE:
////                createDependencyGraphForContentSizeIssue(root);
//                //createDependancyAli(root);
//
//                //  createDependancyAliComplete(root, "height");
//                /*** Try the new algorithm ***/
//                createIncreaseSizeDependancyGraph(root, "height");
//
//                break;
//            case Constants.TOUCH_TARGET_WIDTH_ISSUE:
////                createDependencyGraphForContentSizeIssue(root);
//                //createDependancyAli(root);
////                createDependancyAliComplete(root, "width");
//                createIncreaseSizeDependancyGraph(root, "width");
//
//
//                break;
//            default:
//                System.out.println("Issue type " + issueType + " not supported");
//        }
//    }
//// Before the search based implementation
////    public List<Element> getElementsToChange(String cssProperty, String value, String issueType, String smallestTTXpath) {
////        List<Element> elementsToChange = new ArrayList<>();
////
////        switch (issueType) {
////            case Constants.TOUCH_TARGET_SPACE_ISSUE:
////                elementsToChange = getElementsToChangeForTouchTargetSpace(cssProperty, value);
////                break;
////            case Constants.TOUCH_TARGET_SIZE_ISSUE:
////                elementsToChange = getElementsToChangeForTouchTargetSize(cssProperty, value, smallestTTXpath);
////                break;
//////			case Constants.CONTENT_SIZE_PROBLEM:
//////				elementsToChange = getElementsToChangeForContentSizeIssue(cssProperty, value);
//////				break;
////            default:
////                System.out.println("Issue type " + issueType + " not supported");
////        }
////
////        return elementsToChange;
////    }
//
//    public List<Element> getElementsToChange(String cssProperty, String newValue, String smallestTTvalue, String issueType, String smallestTTXpath) {
//        List<Element> elementsToChange = new ArrayList<>();
//
//        switch (issueType) {
//            case Constants.TOUCH_TARGET_SPACE_ISSUE:
//                elementsToChange = getElementsToChangeForTouchTargetSpace(cssProperty, newValue);
//                break;
//            case Constants.TOUCH_TARGET_SIZE_ISSUE:
//                elementsToChange = getElementsToChangeForTouchTargetSize(cssProperty, newValue, smallestTTvalue, smallestTTXpath);
//                break;
//            case Constants.TOUCH_TARGET_HEIGHT_ISSUE:
//                elementsToChange = getElementsToChangeForTouchTargetSize(cssProperty, newValue, smallestTTvalue, smallestTTXpath);
//
//                break;
//            case Constants.TOUCH_TARGET_WIDTH_ISSUE:
//                elementsToChange = getElementsToChangeForTouchTargetSize(cssProperty, newValue, smallestTTvalue, smallestTTXpath);
//                break;
////			case Constants.CONTENT_SIZE_PROBLEM:
////				elementsToChange = getElementsToChangeForContentSizeIssue(cssProperty, value);
////				break;
//            default:
//                System.out.println("Issue type " + issueType + " not supported");
//        }
//
//        return elementsToChange;
//    }
//
//
////    // Commented this for OwlEye
////    private void createDependencyGraphForTouchTargetSpace(Node<DomNode> root) {
////        List<Node<DomNode>> tapTargets = new ArrayList<>();
////
////        // get all tap targets
////        Queue<Node<DomNode>> q = new LinkedList<Node<DomNode>>();
////        q.add(root);
////
////        while (!q.isEmpty()) {
////            Node<DomNode> node = q.remove();
////            DomNode e = node.getData();
////
//////            DomNode node2 = node;
////            // check if the element is a leaf or text node, if yes then check if it is a segment member
////            if (e == null) {  // Todo: Doe we only consider segmes member
////                boolean isMember = Util.isSegmentMember(e.getxPath(), segmentId);
////                if (!isMember) {
////                    continue;
////                }
////            }
////            // ToDo: in mFix they specified link, button, etc. but here in Android it could be anything div,button,etc.. So I am cgecking for clickable attribute
////            //
//////            if (Constants.TAP_TARGET_ELEMENTS.contains(e.getTagName()))
////            if (Util.isElementClickable(e)) //element has attribute clickable =true
////            {
////                tapTargets.add(node);
////            }
////
////            if (node.getChildren() != null) {
////                for (Node<DomNode> child : node.getChildren()) {
////                    q.add(child);
////                }
////            }
////        }
////
////        // check for dependencies among the tap target elements
////        for (int i = 0; i < tapTargets.size(); i++) {
////            Node<DomNode> i_touch_target = tapTargets.get(i);
////
////            DomNode e = i_touch_target.getData();
////            List<DependentNode> dependentNodes = new ArrayList<>();
////
////            // check if any other tap target is within the defined radius
////            for (int j = 0; j < tapTargets.size(); j++) {
////                Node<DomNode> j_touch_target = tapTargets.get(j);
////                DomNode n = j_touch_target.getData();
////                double[] result = Util.isElementCloseToAnotherWithDistance(i_touch_target, j_touch_target, Constants.TAP_TARGETS_RADIUS);
////                double minDistance = result[1];  // I do not use it now but maybe later
////                boolean isClose = result[0] == 1;
////                if (!e.getxPath().equalsIgnoreCase(n.getxPath()) && isClose) {
//////                if (!e.getxPath().equalsIgnoreCase(n.getxPath()) && Util.isElementCloseToAnother(e, n, Constants.TAP_TARGETS_RADIUS)) {
////
//////                    System.out.println("These two views are close: \n" + e.getxPath() + "\n" + n.getxPath() + "" +
//////                            "\n ****************************************");
////                    // check the position of the neighboring tap target and apply margin in that direction
////                    Rectangle eRect = e.getCoord();
////                    Rectangle nRect = n.getCoord();
////                    // n is above e: e.y1 > n.y2
//////                    double shortestDistance= Double.MAX_VALUE;
////                    String org = j_touch_target.getData().getAttr("origin");
////                    if (j_touch_target.getData().getAttr("origin").equalsIgnoreCase("NOTFOUND")) {
////                        continue;  // we can not apply the value so just ignore it we willl apply full value for hte other element
////                    }
////                    if (eRect.y >= (nRect.y + nRect.height)) {
////
//////                        System.out.println("margin-bottom Dist: " + (eRect.y - (nRect.y + nRect.height)));
////                        dependentNodes.add(new DependentNode(n.getxPath(), Constants.MARGIN_BOTTOM, 1.0, 1.0, 1.0));
////                    }
////                    // n is below e: e.y1 < n.y2
////                    if ((eRect.y + eRect.height) <= nRect.y) {
//////                        System.out.println("margin-top Dist: " + ((eRect.y + eRect.height) - nRect.y));
////                        dependentNodes.add(new DependentNode(n.getxPath(), Constants.MARGIN_TOP, 1.0, 1.0, 1.0));
////                    }
////                    // n is to the left of e: e.x1 > n.x2
////                    if (eRect.x >= (nRect.x + nRect.width)) {
//////                        System.out.println("margin-right Dist: " + (eRect.x - (nRect.x + nRect.width)));
////                        dependentNodes.add(new DependentNode(n.getxPath(), Constants.MARGIN_RIGHT, 1.0, 1.0, 1.0));
////                    }
////                    // n is to the right of e: e.x2 < n.x1
////                    if ((eRect.x + eRect.width) <= nRect.x) {
////
//////                        System.out.println("margin-left Dist: " + ((eRect.x + eRect.width) - nRect.x));
////                        dependentNodes.add(new DependentNode(n.getxPath(), Constants.MARGIN_LEFT, 1.0, 1.0, 1.0));
////                    }
////                }
////            }
////
////            dependentNodesMap.put(e.getxPath(), dependentNodes);
////        }
////    }
//
//    private void createIncreaseSizeDependancyGraph_before31_3_2021(Node<DomNode> root, String property) {
//        /*** Here we want to know what other elements need to increase if we need to make the element higher (if height or wider (if width) ***/
//        // propety : height or width so if it is not one of those then show error
//        if (!property.equalsIgnoreCase("height") && property.equalsIgnoreCase("width")) {
//            System.out.println("Property :" + property + " is not supported.");
//        }
//
//        Node<DomNode> newRoot = root;
//        // check if root has a non-zero width value, if not then find a new root (ancestor)
//        boolean rootFound = true;
//        String rootValue = Util.getValueFromElement(newRoot.getData().getxPath(), property);
//        double rootValueNumber = 0.0;
//        if (rootValue.matches(".*\\d+.*")) {
//            rootValueNumber = Util.getNumbersFromString(rootValue).get(0);
//            while (rootValueNumber <= 0 && rootValueNumber != -2 && newRoot != null) {
//                newRoot = newRoot.getParent();
//                if (newRoot != null) {
//                    rootValue = Util.getValueFromElement(newRoot.getData().getxPath(), property);
////                    System.out.println("Node: " + newRoot.getData().getxPath());
////                    System.out.println("activity: " + mFix.getCurrentActivityName());
////                    System.out.println("property: " + property);
////                    System.out.println("height: " + rootValue);
//
//                    if (rootValue.matches(".*\\d+.*")) {
//                        rootValueNumber = Util.getNumbersFromString(rootValue).get(0);
//                    }
//
//                } else {
//                    // Ali: If we reach the root and still null and no parent with -2 or number then just ignore it and do not add nor the original node nor any parent to nodemap
//                    // y here Todo: Check if that holds!
//                    rootFound = false;
//
//                }
//
//
//            } //end while
//        }
//
//        // add root to the dependency graph with the main property and all dependent properties
//        //Only if rootFound meaning there is element without -1
//        if (rootFound) {
//            root = newRoot;
//            //Ali: I need to check if it is Num then add it directly if it is not -2 then what to do? One possible way is to check if there is a parent with Num or no child with -2 or Num ( to eliminate -2 --> Num | -1 --> -2)
//            // Other approach is to just add the relationships now and then handle them later like what GJ suggested
//            dependentNodesMap.put(root.getData().getxPath() + "#" + property, new ArrayList<DependentNode>());
//
////            createNodeProperyRelatedGraph(root, property, rootValueNumber);
//            createNodeDependentPropertyRelation(root, property, rootValueNumber);  // new method after handling matchparentincrease FEB2021
//
//        } // end of if(rootFound)
//
//        // add children of the root to the queue
//        Queue<Node<DomNode>> q = new LinkedList<Node<DomNode>>();
//        if (root.getChildren() != null) {
//            for (Node<DomNode> child : root.getChildren()) {
//                q.add(child);
//            }
//        }
//
//        while (!q.isEmpty()) {
//
//
//            Node<DomNode> node = q.remove();
//            DomNode e = node.getData();
//            String tempId = e.getId();
////            if (tempId != null) {
////                System.out.println(tempId);
////            }
//            boolean elementHasProperty = false;
//            String tempVal1 = Util.getValueFromElement(e.getxPath(), property);
//
//            String eValue = Util.getValueFromElement(e, property);
////            if(!eValue.matches(".*\\d+.*")) {
////                eValue=1000+"dp";
////            }
//            Double eValueNumber = -10.0;
//            if (eValue != null) {
////                System.out.println("Value before crash : " + eValue);
//                eValueNumber = Util.getNumbersFromString(eValue).get(0);
//            }
//            if (eValue != null) {  // Ali: even if -1,  still it may depend on parents to be able to increase ( for exmaple imagine button with -1 inside a layout that is 45dp in taht case button depemd on its parent)
//                if (eValueNumber == -1) { // what to do in that case?
//
//                }
//                if (eValueNumber == -2.0) {
//                    // if the element has -2 then we should only consider the other dependant elements that can effect its size so we use them later. Right now I am only handling min-height and min-width
//                    String min_property = Constants.MAIN_PROPERTY_TO_MIN_PROPERTY.get(property); // min-height or mind-width
//                    String min_property_value = Util.getValueFromElement(e, min_property);
//                    if (min_property_value == null) {
//                        e.setAttr(Constants.PROPERTY_TO_FULL_ANDROID_NAME.get(min_property), Util.getDynamicValueFromElement(e, property));
//                    }
//                }
//                elementHasProperty = true;
//                // find ancestor that has a defined width property to establish an edge between the ancestor and node
//                Node<DomNode> ancestor = node.getParent();
//                boolean isAncestorFound = false;
//                boolean isAncestorGraphKey = false;
//                String parentValue = "";
//                Double parentValueNumber = -100.0;
//                while (!isAncestorFound && ancestor != null) {
//                    parentValue = Util.getValueFromElement(ancestor.getData(), property);
//                    parentValueNumber = -100.0;
//                    if (parentValue != null) {
//                        parentValueNumber = Util.getNumbersFromString(parentValue).get(0);
//                    }
//                    // Ali: I am also adding if it is -2 so only add relationship if parent is NUm
////                    if( parentValueNumber != -2 ){
//                    //ToDo: double check
//                    if ((parentValueNumber != -1 && eValueNumber == -1) || (parentValueNumber >= 0 && (eValueNumber == -2 || eValueNumber >= 0))) {  // Second condition to make sure if child is -1 but parent is number or -2 then we need to add relation
//                        isAncestorFound = true;
//                        isAncestorGraphKey = true;
//                        break;
//
////                        if (dependentNodesMap.containsKey(ancestor.getData().getxPath() +
////                                "#" + property)) {
////                            isAncestorFound = true;
////                            isAncestorGraphKey = true;
////                            break;
////                        } else {
////                            // check if the ancestor is present in the values (dependent nodes)
////                            for (List<DependentNode> dnList : dependentNodesMap.values()) {
////                                for (DependentNode dn : dnList) {
////                                    if (dn.getXpath().equalsIgnoreCase(ancestor.getData().getxPath()) && dn.getProperty().equalsIgnoreCase(property)) {
////                                        isAncestorFound = true;
////                                        isAncestorGraphKey = false;
////                                        break;
////                                    }
////                                }
////                                if (isAncestorFound)
////                                    break;
////                            }
////                        }
//                    }
//                    if (!isAncestorFound)
//                        ancestor = ancestor.getParent();
//                }
//
//
//                // add edge in dependency graph between ancestor and current node
//                if (isAncestorFound) {
//                    String ancestorValue = Util.getValueFromElement(ancestor.getData().getxPath(), property);
//                    String nodeValue = Util.getValueFromElement(e.getxPath(), property);
//
//                    // check if the css values contain digits to avoid null pointer exception
////                    System.out.println("Before Crash: " + ancestor.getData().getId());
//                    if (ancestorValue.matches(".*\\d+.*") && nodeValue.matches(".*\\d+.*")) {
//                        double ancestorValueNumber = Util.getNumbersFromString(ancestorValue).get(0);
//
//                        // Ali what if the node value is -2? then do not add dependacy for the height but rather on min_height/ min_width as shown below)
//                        double nodeValueNumber = Util.getNumbersFromString(nodeValue).get(0);
//                        // to avoid divide by zero error
//
//                        /*** I am trying the idea of using the dynamic sizes to claculate the ratio ***/
//                        String parentV = Util.getDynamicValueInDDP(ancestor.getData(), property);
//                        String childV = Util.getDynamicValueInDDP(e, property);
//                        double parentVal = Util.getNumbersFromString(parentV).get(0);
//                        double childVal = Util.getNumbersFromString(childV).get(0);
//                        double ratio = parentVal / childVal;
//                        String parentDependantProperty = property;  // by the default it is the main property but if it parent is min then we make it min-height or min-width
//                        ancestorValueNumber = parentVal;
//                        nodeValueNumber = childVal;
////                        if (ancestorValueNumber > 0 && nodeValueNumber > 0) {
////                            /*** Disabled this ratio calculation to try calculating the size from the dynamic values instead of dynamic  as shown above before the if ***/
//////                            double ratio = ancestorValueNumber / nodeValueNumber;
////
//////                            DependentNode dn = new DependentNode(e.getxPath(), property, ratio, ancestorValueNumber, nodeValueNumber);
////                        }else {
//                        // Decide the correct property and ratio
//                        String res = SegmentRelationGraph.DecideTheCorrectValue(e.getxPath(), property); // check for the child
//                        if (res != null && res.contains("min")) {
//                            // if null then the child is -1 so keep the height for now but the property will depend on the parent if it is number then property is height if it -2 then it is min-height
//
//                        }
//                        String pRes = SegmentRelationGraph.DecideTheCorrectValue(ancestor.getData().getxPath(), property);
//                        if (pRes != null && pRes.contains("min")) {
//                            parentDependantProperty = Constants.MAIN_PROPERTY_TO_MIN_PROPERTY.get(property);
//                            // then parent is set as -2 then we need to set property for the dependnacy relationship
//                        }
//
//                        /*** Ali: new algoruth no need to add from parent to the child ***/
//
////                            if (isAncestorGraphKey || dependentNodesMap.containsKey(ancestor.getData().getxPath() + "#" + property)) {//                                dependentNodesMap.get(ancestor.getData().getxPath() + "#" + property).add(dn);
////                            } else {
////                                List<DependentNode> dependentNodes = new ArrayList<DependentNode>();
////                                dependentNodes.add(dn);
////                                dependentNodesMap.put(ancestor.getData().getxPath() + "#" + property, dependentNodes);
////                            }
//                        // Add the inverse edge: child to parent
//                        if (Constants.DEPENDENCY_RELATIONSHIP.get(parentDependantProperty).equals(Constants.RELATIONSHIP.BIDIRECTIONAL)) {
//                            List<DependentNode> dependentNodes = new ArrayList<DependentNode>();
//                            dependentNodes.add(new DependentNode(ancestor.getData().getxPath(), parentDependantProperty, 1 / ratio, nodeValueNumber, ancestorValueNumber));
//                            dependentNodesMap.put(e.getxPath() + "#" + property, dependentNodes);
//
//                        }
//                        // add reverse edge
//							/*List<DependentNode> dependentNodes = new ArrayList<DependentNode>();
//							dependentNodes.add(new DependentNode(ancestor.getData().getxPath(), "width", 1/ratio, nodeValueNumber, ancestorValueNumber));
//							dependentNodesMap.put(e.getxPath() + "#" + "width", dependentNodes);*/
//                        //   }
//                    }
//
//                    /*** Ali: new algoruth no need as if no ancestor then no  parent need to be increased to accomedite the size increase  ***/
////                } else if (!isAncestorFound && elementHasProperty) {
////                    //Element has fixed or -2 height but no ancestor has fixed or -2 so it was not abel to find ancestor
////                    //for that case,  I am going to just add it for now  the dependant properties for now min-width | Todo: Ali check
////                    List<DependentNode> dependentNodes = new ArrayList<DependentNode>();
////                    String min_property = Constants.MAIN_PROPERTY_TO_MIN_PROPERTY.get(property); // min-height or mind-width
////                    String min_property_value = Util.getValueFromElement(e, min_property);
////                    dependentNodesMap.put(node.getData().getxPath() + "#" + min_property, dependentNodes);
//
//
//                }
//            }     //  if (eValue != null && (eValueNumber > 0 || eValueNumber == -2)) {
//
////            /*** The following loop is already handeled by just calling  createNodeProperyRelatedGraph method but ToDO: double check *///
////            //  for (String prop : Constants.CSS_PROPERTIES_DEPENDENCY.get(property)) {
////            createNodeProperyRelatedGraph(node, property, eValueNumber);
//            createNodeDependentPropertyRelation(node, property, eValueNumber);  // new method after handling matchparentincrease FEB2021
//            if (node.getChildren() != null) {
//                for (Node<DomNode> child : node.getChildren()) {
//                    q.add(child);
//                }
//            }
//
//
//        } // end of          while (!q.isEmpty()) {
//
//
//    }
//
//    private void createIncreaseSizeDependancyGraph(Node<DomNode> root, String property) {
//        /*** Here we want to know what other elements need to increase if we need to make the element higher (if height or wider (if width) ***/
//        // propety : height or width so if it is not one of those then show error
//        if (!property.equalsIgnoreCase("height") && property.equalsIgnoreCase("width")) {
//            System.out.println("Property :" + property + " is not supported.");
//        }
//
//        Node<DomNode> newRoot = root;
//        // check if root has a non-zero width value, if not then find a new root (ancestor)
//        boolean rootFound = true;
//        String rootValue = Util.getValueFromElement(newRoot.getData().getxPath(), property);
//        double rootValueNumber = 0.0;
//        if (rootValue.matches(".*\\d+.*")) {
//            rootValueNumber = Util.getNumbersFromString(rootValue).get(0);
//            while (rootValueNumber <= 0 && rootValueNumber != -2 && newRoot != null) {
//                newRoot = newRoot.getParent();
//                if (newRoot != null) {
//                    rootValue = Util.getValueFromElement(newRoot.getData().getxPath(), property);
////                    System.out.println("Node: " + newRoot.getData().getxPath());
////                    System.out.println("activity: " + mFix.getCurrentActivityName());
////                    System.out.println("property: " + property);
////                    System.out.println("height: " + rootValue);
//
//                    if (rootValue.matches(".*\\d+.*")) {
//                        rootValueNumber = Util.getNumbersFromString(rootValue).get(0);
//                    }
//
//                } else {
//                    // Ali: If we reach the root and still null and no parent with -2 or number then just ignore it and do not add nor the original node nor any parent to nodemap
//                    // y here Todo: Check if that holds!
//                    rootFound = false;
//
//                }
//
//
//            } //end while
//        }
//
//        // add root to the dependency graph with the main property and all dependent properties
//        //Only if rootFound meaning there is element without -1
//        if (rootFound) {
//            root = newRoot;
//            //Ali: I need to check if it is Num then add it directly if it is not -2 then what to do? One possible way is to check if there is a parent with Num or no child with -2 or Num ( to eliminate -2 --> Num | -1 --> -2)
//            // Other approach is to just add the relationships now and then handle them later like what GJ suggested
//            dependentNodesMap.put(root.getData().getxPath() + "#" + property, new ArrayList<DependentNode>());
//
////            createNodeProperyRelatedGraph(root, property, rootValueNumber);
//            createNodeDependentPropertyRelation(root, property, rootValueNumber);  // new method after handling matchparentincrease FEB2021
//
//        } // end of if(rootFound)
//
//        // add children of the root to the queue
//        Queue<Node<DomNode>> q = new LinkedList<Node<DomNode>>();
//        if (root.getChildren() != null) {
//            for (Node<DomNode> child : root.getChildren()) {
//                q.add(child);
//            }
//        }
//
//        while (!q.isEmpty()) {
//
//
//            Node<DomNode> node = q.remove();
//            DomNode e = node.getData();
//            String tempId = e.getId();
////            if (tempId != null) {
////                System.out.println(tempId);
////            }
//            boolean elementHasProperty = false;
//            String tempVal1 = Util.getValueFromElement(e.getxPath(), property);
//
//            String eValue = Util.getValueFromElement(e, property);
////            if(!eValue.matches(".*\\d+.*")) {
////                eValue=1000+"dp";
////            }
//            Double eValueNumber = -10.0;
//            if (eValue != null) {
////                System.out.println("Value before crash : " + eValue);
//                eValueNumber = Util.getNumbersFromString(eValue).get(0);
//            }
//            if (eValue != null) {  // Ali: even if -1,  still it may depend on parents to be able to increase ( for exmaple imagine button with -1 inside a layout that is 45dp in taht case button depemd on its parent)
//                if (eValueNumber == -1) { // what to do in that case?
//
//                }
//                if (eValueNumber == -2.0) {
//                    // if the element has -2 then we should only consider the other dependant elements that can effect its size so we use them later. Right now I am only handling min-height and min-width
//                    String min_property = Constants.MAIN_PROPERTY_TO_MIN_PROPERTY.get(property); // min-height or mind-width
//                    String min_property_value = Util.getValueFromElement(e, min_property);
//                    if (min_property_value == null) {
//                        Util.addAttributeToElement(e.getxPath(),Constants.PROPERTY_TO_FULL_ANDROID_NAME.get(min_property),
//                                Util.getDynamicValueFromElement(e, property));
////                        e.setAttr(Constants.PROPERTY_TO_FULL_ANDROID_NAME.get(min_property), Util.getDynamicValueFromElement(e, property));
//                    }
//                }
//                elementHasProperty = true;
//                // find ancestor that has a defined width property to establish an edge between the ancestor and node
//                Node<DomNode> ancestor = node.getParent();
//                boolean isAncestorFound = false;
//                boolean isAncestorGraphKey = false;
//                String parentValue = "";
//                Double parentValueNumber = -100.0;
//                while (!isAncestorFound && ancestor != null) {
//                    parentValue = Util.getValueFromElement(ancestor.getData(), property);
//                    parentValueNumber = -100.0;
//                    if (parentValue != null) {
//                        parentValueNumber = Util.getNumbersFromString(parentValue).get(0);
//                    }
//                    // Ali: I am also adding if it is -2 so only add relationship if parent is NUm
////                    if( parentValueNumber != -2 ){
//                    //ToDo: double check
//                    /*** TEST TEST 31-3-2021
//                     * Disabled the long if below and I tried to only consider if parent is not -1
//                     */
//                    if ((parentValueNumber != -1)){
////                    if ((parentValueNumber != -1 && eValueNumber == -1) || (parentValueNumber >= 0 && (eValueNumber == -2 || eValueNumber >= 0))) {  // Second condition to make sure if child is -1 but parent is number or -2 then we need to add relation
//                        isAncestorFound = true;
//                        isAncestorGraphKey = true;
//                        break;
//
////                        if (dependentNodesMap.containsKey(ancestor.getData().getxPath() +
////                                "#" + property)) {
////                            isAncestorFound = true;
////                            isAncestorGraphKey = true;
////                            break;
////                        } else {
////                            // check if the ancestor is present in the values (dependent nodes)
////                            for (List<DependentNode> dnList : dependentNodesMap.values()) {
////                                for (DependentNode dn : dnList) {
////                                    if (dn.getXpath().equalsIgnoreCase(ancestor.getData().getxPath()) && dn.getProperty().equalsIgnoreCase(property)) {
////                                        isAncestorFound = true;
////                                        isAncestorGraphKey = false;
////                                        break;
////                                    }
////                                }
////                                if (isAncestorFound)
////                                    break;
////                            }
////                        }
//                    }
//                    if (!isAncestorFound)
//                        ancestor = ancestor.getParent();
//                }
//
//
//                // add edge in dependency graph between ancestor and current node
//                if (isAncestorFound) {
//                    String ancestorValue = Util.getValueFromElement(ancestor.getData().getxPath(), property);
//                    String nodeValue = Util.getValueFromElement(e.getxPath(), property);
//
//                    // check if the css values contain digits to avoid null pointer exception
////                    System.out.println("Before Crash: " + ancestor.getData().getId());
//                    if (ancestorValue.matches(".*\\d+.*") && nodeValue.matches(".*\\d+.*")) {
//                        double ancestorValueNumber = Util.getNumbersFromString(ancestorValue).get(0);
//
//                        // Ali what if the node value is -2? then do not add dependacy for the height but rather on min_height/ min_width as shown below)
//                        double nodeValueNumber = Util.getNumbersFromString(nodeValue).get(0);
//                        // to avoid divide by zero error
//
//                        /*** I am trying the idea of using the dynamic sizes to claculate the ratio ***/
//                        String parentV = Util.getDynamicValueInDDP(ancestor.getData(), property);
//                        String childV = Util.getDynamicValueInDDP(e, property);
//                        double parentVal = Util.getNumbersFromString(parentV).get(0);
//                        double childVal = Util.getNumbersFromString(childV).get(0);
//                        double ratio = parentVal / childVal;
//                        String parentDependantProperty = property;  // by the default it is the main property but if it parent is min then we make it min-height or min-width
//                        ancestorValueNumber = parentVal;
//                        nodeValueNumber = childVal;
////                        if (ancestorValueNumber > 0 && nodeValueNumber > 0) {
////                            /*** Disabled this ratio calculation to try calculating the size from the dynamic values instead of dynamic  as shown above before the if ***/
//////                            double ratio = ancestorValueNumber / nodeValueNumber;
////
//////                            DependentNode dn = new DependentNode(e.getxPath(), property, ratio, ancestorValueNumber, nodeValueNumber);
////                        }else {
//                        // Decide the correct property and ratio
//                        String res = SegmentRelationGraph.DecideTheCorrectValue(e.getxPath(), property); // check for the child
//                        if (res != null && res.contains("min")) {
//                            // if null then the child is -1 so keep the height for now but the property will depend on the parent if it is number then property is height if it -2 then it is min-height
//
//                        }
//                        String pRes = SegmentRelationGraph.DecideTheCorrectValue(ancestor.getData().getxPath(), property);
//                        if (pRes != null && pRes.contains("min")) {
//                            parentDependantProperty = Constants.MAIN_PROPERTY_TO_MIN_PROPERTY.get(property);
//                            // then parent is set as -2 then we need to set property for the dependnacy relationship
//                        }
//
//                        /*** Ali: new algoruth no need to add from parent to the child ***/
//
////                            if (isAncestorGraphKey || dependentNodesMap.containsKey(ancestor.getData().getxPath() + "#" + property)) {//                                dependentNodesMap.get(ancestor.getData().getxPath() + "#" + property).add(dn);
////                            } else {
////                                List<DependentNode> dependentNodes = new ArrayList<DependentNode>();
////                                dependentNodes.add(dn);
////                                dependentNodesMap.put(ancestor.getData().getxPath() + "#" + property, dependentNodes);
////                            }
//                        // Add the inverse edge: child to parent
//                        if (Constants.DEPENDENCY_RELATIONSHIP.get(parentDependantProperty).equals(Constants.RELATIONSHIP.BIDIRECTIONAL)) {
//                            List<DependentNode> dependentNodes = new ArrayList<DependentNode>();
//                            dependentNodes.add(new DependentNode(ancestor.getData().getxPath(), parentDependantProperty, 1 / ratio, nodeValueNumber, ancestorValueNumber));
//                            dependentNodesMap.put(e.getxPath() + "#" + property, dependentNodes);
//
//                        }
//                        // add reverse edge
//							/*List<DependentNode> dependentNodes = new ArrayList<DependentNode>();
//							dependentNodes.add(new DependentNode(ancestor.getData().getxPath(), "width", 1/ratio, nodeValueNumber, ancestorValueNumber));
//							dependentNodesMap.put(e.getxPath() + "#" + "width", dependentNodes);*/
//                        //   }
//                    }
//
//                    /*** Ali: new algoruth no need as if no ancestor then no  parent need to be increased to accomedite the size increase  ***/
////                } else if (!isAncestorFound && elementHasProperty) {
////                    //Element has fixed or -2 height but no ancestor has fixed or -2 so it was not abel to find ancestor
////                    //for that case,  I am going to just add it for now  the dependant properties for now min-width | Todo: Ali check
////                    List<DependentNode> dependentNodes = new ArrayList<DependentNode>();
////                    String min_property = Constants.MAIN_PROPERTY_TO_MIN_PROPERTY.get(property); // min-height or mind-width
////                    String min_property_value = Util.getValueFromElement(e, min_property);
////                    dependentNodesMap.put(node.getData().getxPath() + "#" + min_property, dependentNodes);
//
//
//                }
//
//                /*** enabled that to account fot androdo DN app */
////                else if (eValueNumber==-1 && tempId!=null && tempId!=""  ){  // ancestor not found at all so no -2 or exact number in the descendants
////                    // this is the only case where we are going to consider changing that to -2
////                    if( node.getChildren() == null || node.getChildren().size() == 0) {
////                        String full_property_name = Constants.SIZE_SPACE_ATTRIBUTES.get(property);
////
////                        Util.addAttributeToElement(e.getxPath(), Constants.PROPERTY_TO_FULL_ANDROID_NAME.get(property),
////                                "-2");
////                        e.setAttr(full_property_name,"-2");
////                        eValueNumber = -2.0;
////                        String min_property = Constants.MAIN_PROPERTY_TO_MIN_PROPERTY.get(property); // min-height or mind-width
////                        String min_property_value = Util.getValueFromElement(e, min_property);
////                        Util.addAttributeToElement(e.getxPath(), Constants.PROPERTY_TO_FULL_ANDROID_NAME.get(min_property),
////                                Util.getDynamicValueFromElement(e, property));
////                        if (min_property_value == null) {
////                            e.setAttr(Constants.PROPERTY_TO_FULL_ANDROID_NAME.get(min_property), Util.getDynamicValueFromElement(e, property));
////                            e.setAttr(Constants.PROPERTY_TO_FULL_ANDROID_NAME.get(min_property),Util.getDynamicValueFromElement(e, property));
////                        }
////                    }
////                }
//
//            }     //  if (eValue != null && (eValueNumber > 0 || eValueNumber == -2)) {
//
////            /*** The following loop is already handeled by just calling  createNodeProperyRelatedGraph method but ToDO: double check *///
////            //  for (String prop : Constants.CSS_PROPERTIES_DEPENDENCY.get(property)) {
////            createNodeProperyRelatedGraph(node, property, eValueNumber);
//            createNodeDependentPropertyRelation(node, property, eValueNumber);  // new method after handling matchparentincrease FEB2021
//            if (node.getChildren() != null) {
//                for (Node<DomNode> child : node.getChildren()) {
//                    q.add(child);
//                }
//            }
//
//
//        } // end of          while (!q.isEmpty()) {
//
//
//    }
//
//
//    private void createNodeDependentPropertyRelation(Node<DomNode> node, String property, double nodeStaticValueNumber) {
//        /*** the new approach: create ratio between padding and min_height | or width..... if the property is not there then add them with zero ***/
//        double ratio = 0;
//        for (String prop : Constants.PROPERTIES_TO_DEPENDENCY_MAPPING.get(property)) {
//            if (nodeStaticValueNumber == -2 && prop.contains("min")) {
//                continue;  // if the static is set -2 then we do not add relation with min-heigth/min-width
//            }
//            double nodeDynamicValueNumber = Double.parseDouble(Util.getDynamicValueInDDP(node.getData(), property)); //getting the dynaimic value of the node
//            double dependentPropValueNumber = -100;
//            String dependentPropValue = Util.getValueFromElement(node.getData().getxPath(), prop); // to see if the value was set already
//            if (dependentPropValue != null && dependentPropValue.matches(".*\\d+.*")) { /*** What if it set as att in style so no number: handle it later***/
//                dependentPropValueNumber = Util.getNumbersFromString(dependentPropValue).get(0);
//            }
//            if (dependentPropValue != null && dependentPropValueNumber > 0.0) {
////                if (dependentPropValue.matches(".*\\d+.*")) { /*** What if it set as att in style so no number: handle it later***/
////                    double dependentPropValueNumber = Util.getNumbersFromString(dependentPropValue).get(0);
//                if (dependentPropValueNumber == 0) { // if the dependant property is set as zero then the ratio will be infinity and when we divide the new value by infinity it will be zero and that is what we want
//                    // it set as zero then the ratio will be
//                    ratio = 0;
//                }
//                if (dependentPropValueNumber >= 0) { // if the dependant property is set then we take the ratio
////                    ratio = nodeStaticValueNumber / dependentPropValueNumber;
//                    ratio = nodeDynamicValueNumber / dependentPropValueNumber;
//
//                }
//                // ToDo: Ratio is it going to be the height or width dynamic or static, right now if height or width set as -2 I am using the current dynamic height set in the min-height or widht
//
//
//                DependentNode dn = new DependentNode(node.getData().getxPath(), prop, ratio, nodeDynamicValueNumber, dependentPropValueNumber);
//                if (!dependentNodesMap.containsKey(node.getData().getxPath() + "#" + property)) {
//                    // if the node is not already in the map then just initialize its array of dependant node. We d oth at ot avoid null pointer
//                    List<DependentNode> dependentNodes = new ArrayList<DependentNode>();
//                    dependentNodesMap.put(node.getData().getxPath() + "#" + property, dependentNodes);
//                }
//                dependentNodesMap.get(node.getData().getxPath() + "#" + property).add(dn);
//
//            } else { // the dependant value was not set
//                if (prop.contains("padding")) {
//                    /***   add padding only if node is:
//                     * 1-it is a leave(no children) and it contains an image (TODO: what are elements that can contain images? in Android)
//                     * 2- has one child has one child that is an image or image button ( not a leave but has one child that adhere to condition 1
//                     *
//                     * ***/
//                    ratio = Double.POSITIVE_INFINITY; // since in reality we will not use ration but instead add the diff between gene value and dynamic value as the new vlaue
//                    if ((node.getChildren() == null || node.getChildren().size() == 0) && Util.doesContainImage(node)) {  //condition 1
//
//                        DependentNode dn = new DependentNode(node.getData().getxPath(), prop, ratio, nodeDynamicValueNumber, Double.POSITIVE_INFINITY);
//                        if (!dependentNodesMap.containsKey(node.getData().getxPath() + "#" + property)) {
//                            // if the node is not already in the map then just initialize its array of dependant node. We d oth at ot avoid null pointer
//                            List<DependentNode> dependentNodes = new ArrayList<DependentNode>();
//                            dependentNodesMap.put(node.getData().getxPath() + "#" + property, dependentNodes);
//                        }
//                        dependentNodesMap.get(node.getData().getxPath() + "#" + property).add(dn);
//                    }
//                }
//            }
//
//        }
//
//
//    }
//
//
//
//    private void addPaddingForImages(Node<DomNode> node, String property) {
//        /***   if element is
//         * 1-it is a leave(no children) and it contains an image (TODO: what are elements that can contain images? in Android)
//         * 2- has one child has one child that is an image or image button ( not a leave but has one child that adhere to condition 1
//         *
//         * ***/
//
//        if ((node.getChildren() == null || node.getChildren().size() == 0) && Util.doesContainImage(node)) {  //condition 1
//            // was padding already added? ( this happens when the padding was already specified by developer so we have already
//            // added teh relationship
////                    checkAndAddPadding(node,property );
//
//        }
//    }
//
//
//    private List<Element> getElementsToChangeForTouchTargetSize(String cssProperty, String value, String smallestTTvalue, String smallestTTXpath) {
//        /*** Value is the suggested value from the search based
//         * samllestTTValue is the value of the smallest touch target element in the segment :: I may use it later to generate values
//         * ***/
//        // (1) Mfix chooses randomly the first element, I should choose a touch target element not any element
//
//        //ToDo: Should The dependancy graph even contain elements that are not touch targets? or only keep them as dependant on touch target element
//        List<Element> dependentElements = new ArrayList<>();
//
//        //ToDo: @Ali: if I am applying simple heuristic to start with the smallest
//        // xpath = smallestTTXpath;  // If I apply the heuristic of choosing the smallest element
//
//
//        //(2) Iterate over the xpaths of the dependentNodesMap(Keys) and find the first touch target
//        String xpath = "";
//        for (String dNode : dependentNodesMap.keySet()
//        ) {
//            String dNodeXpath = Util.getXpathFromDGkey(dNode); // Extract the xpath from the dependancy graph keys
//            Node<DomNode> node = XMLUtils.searchVHTreeByXpath(dNodeXpath, XMLUtils.getRoot());
//            DomNode nodedata = node.getData();
//            boolean isClickable = Util.isElementClickable(nodedata);
//            if (isClickable) {  // We found clickable element
//                xpath = dNodeXpath;
//                break;
//            }
//        }
//		/*// check if the element passed as argument does not have fixed CSS property
//		if(!dependentNodesMap.containsKey(xpath + "#" + cssProperty))
//		{
//			// find first ancestor element with fixed CSS property
//			HtmlDomTree instance = HtmlDomTree.getInstance(mFix.getFilepath());
//			Node<DomNode> domNode = instance.searchHtmlDomTreeByXpath(xpath);
//			while(domNode != null)
//			{
//				domNode = domNode.getParent();
//				if(domNode.getData().getCssMap().containsKey(cssProperty))
//				{
//					xpath = domNode.getData().getxPath();
//					System.out.println("Ancestor element with fixed font-size = " + xpath);
//					break;
//				}
//			}
//		}*/
//        // (3) Once we get the first the element, we start iterating over the elements
//        boolean isFinished = false;// to make sure all nodes in the dependancy graph are processed.
//        Set<String> visitedElements = new HashSet<>();
//        while (!isFinished) {
//            Node<DomNode> d = XMLUtils.searchVHTreeByXpath(xpath, XMLUtils.getRoot());
//            String unit = "dp"; //// ToDo: Placeholder until I figure out the size
//            //(4) Apply the value to the element
//            //(4a) Add new change element that apply the new value for the TT element we chose before the loop
//            String correctProperty = DecideTheCorrectValue(xpath, cssProperty); // if height is fixed then just add the value as height , if it is -2 then instead add min_height | ToDO: check again aidownloacer lower bar issue
//            dependentElements.add(new Element(xpath, correctProperty, value + unit, false, d)); //Todo: Add the unit
//            double elementValue = Util.getNumbersFromString(value).get(0);
////            String unit = Util.getUnitFromStringValue(value);
//
//            //Comment the above 3 lines and Uncomment the next 7 lines when applying the smallest element
//            //String res = generateValueForTT(xpath, cssProperty, smallestTTvalue) + ""; // Generate the actual value from the specific minimum increase required
////            String[] result = res.split("#");
////            String newValue = result[0];
////            String newProp = result[1];
////            double elementValue = Util.getNumbersFromString(newValue).get(0);
//            // String unit = Util.getUnitFromStringValue(value);  //  Ali ToDO:I need to find the unit but right now  I assume it is dp
//            //String unit = "dp";
//            //      dependentElements.add(new Element(xpath, newProp, newValue + unit));
//
//            boolean isDone = false;
//
//
//            /*** (5) Iterate over the dependant nodes of the first node and then change the dependant nodes based on the ratio ***/
//            Queue<DependentNode> queue = new LinkedList<DependentNode>();
//            while (!isDone) {
//                visitedElements.add(xpath);
//                Node<DomNode> currDNode = XMLUtils.searchVHTreeByXpath(xpath, XMLUtils.getRoot());
//
//                List<DependentNode> dependentNodes = dependentNodesMap.get(xpath + "#" + cssProperty);
//                if (dependentNodes != null && dependentNodes.size() > 0) {
//                    for (DependentNode dNode : dependentNodes) {
//                        double dElementValue = elementValue / dNode.getRatio();
//                        Element dElement = new Element(dNode.getXpath(), dNode.getProperty(), dElementValue + unit, false, currDNode);
//                        // If it is not already added. ToDo: However, what if it was already added but the new value is larger or the correct one?
//                        if (!dependentElements.contains(dElement))
//                            dependentElements.add(dElement);
//
//                        if (!visitedElements.contains(dNode.getXpath()))
//                            queue.add(dNode);
//                    }
//                }
//
//
//                if (queue.isEmpty()) {
//                    isDone = true;
//                } else {
//                    DependentNode node = queue.remove();
//                    xpath = node.getXpath();
//                    String dvalue = "";
//                    for (Element e : dependentElements) {
//                        if (e.getXpath().equalsIgnoreCase(xpath)) {
//                            dvalue = e.getValue();
//                            break;
//                        }
//                    }
//                    elementValue = Util.getNumbersFromString(dvalue).get(0);
//                }
//            }
//            // Ali: if queue is empty then check if  there is an element that we did not get from the dependant elements then we are going to check if there is an element that was not explored yet.
//            boolean new_element_found = false;
//            if (visitedElements.size() < dependentNodesMap.keySet().size()) { //some elements are not processed so continue
//                /*** the element is not dependant on anything else but exists in the dependacy graph ( can this happen?)
//                 randomly chose something else that has not been visited ***/
//                for (Map.Entry<String, List<DependentNode>> entry : dependentNodesMap.entrySet()) {//find a new element from dependancy to work on
//                    String xp = entry.getKey();
//                    xp = Util.getXpathFromDGkey(xp);
//                    Node<DomNode> n = XMLUtils.searchVHTreeByXpath(xp, XMLUtils.getRoot());
//                    boolean isClickable = Util.isElementClickable(n.getData());
//                    if (!visitedElements.contains(xp) && isClickable) {
//                        xpath = xp;
////                        xpath=  Util.getXpathaFromDGkey(xpath);
//                        new_element_found = true;
//                        break;
//                    }
//                }
//
//
//            }
//            if (!new_element_found) {
//                isFinished = true;
//            }
//        }
//        return dependentElements;
//    }
//
//    private String DecideTheCorrectValue(String xpath, String cssProperty) {
//
//        /*** Decide what prperty to select based on whether -1,-2, or fixed ***/
//        Node<DomNode> node = XMLUtils.searchVHTreeByXpath(xpath, XMLUtils.getRoot());
//
//
//        String currentValue = Util.getValueFromElement(xpath, cssProperty); // Ali: should not this be the dynamic value?
//
//        String newProperty = cssProperty;
//        if (currentValue != null) {
//            double currVal = Util.getNumbersFromString(currentValue).get(0);  // if -2 or -1
//            if (currVal < 0 && currVal == -2) { //if it is wrap content then get the value of minWidth or height instead
//                currentValue = Util.getValueFromElement(xpath, "min-height");
//                newProperty = "min-height"; // to be added to the eleemnts to change instead of height
//                // currVal = Util.getNumbersFromString(currentValue).get(0);
//            }
//        }
//
//
//        return newProperty;
//    }
//
//
//    private List<Element> getElementsToChangeForTouchTargetSpace(String cssProperty, String value) {
//        Set<Element> dependentElements = new HashSet<>();
//        double elementValue = Util.getNumbersFromString(value).get(0);
//        String unit = Util.getUnitFromStringValue(value);
//        unit = "dp";  // unit and ceiling i added thme manually to check
//        String halfValueToApply = Math.ceil((elementValue / 2)) + unit;    // divide by 2 as the neighbor will also add margin
//        String fullValueToApply = elementValue + unit;
//        String actualValueToApply = "";
////
//        for (String xpath : dependentNodesMap.keySet()) {
//            // check if element has an image that is less than 48px
////         List<DomNode> imageElements = getImageElements(xpath);
////         String widthValue = Constants.ACCESSIBILITY_SUGGESTED_VALUES_FOR_TOUCH_TARGET_SIZE_PROBLEM.get("width");
////         String heightValue = Constants.ACCESSIBILITY_SUGGESTED_VALUES_FOR_TOUCH_TARGET_SIZE_PROBLEM.get("height");
////          Ali: commented on Jan 18 2021
////            for (DomNode e : imageElements) {
////                // process if elements don't contain background image position and size specified, as it messes up the image
////                //ToDo: @Ali right now I chagned the if but I will come back later to check what property to check
////                //if (!(e.getCssMap().containsKey("background-image") && e.getCssMap().get("background-image").contains("url")))
////                if (true) {
////                    if (e.getCoord().width < Util.getNumbersFromString(widthValue).get(0)) {
////                        dependentElements.add(new Element(e.getxPath(), "width", widthValue));
////                    }
////                    if (e.getCoord().height < Util.getNumbersFromString(heightValue).get(0)) {
////                        dependentElements.add(new Element(e.getxPath(), "height", heightValue));
////                    }
////                }
////            }
//
//            List<DependentNode> elements = dependentNodesMap.get(xpath);
//            actualValueToApply = halfValueToApply; //  normal case we split the value between the two nodes
//            Node<DomNode> node = XMLUtils.searchVHTreeByXpath(xpath, XMLUtils.getRoot());
//
//            if (node.getData().getAttr("origin").equalsIgnoreCase("NOTFOUND")) {
//                actualValueToApply = fullValueToApply;  // if then node is not found then we can only apply it to the other node
//            }
//            for (DependentNode dn : elements) {
//                Node<DomNode> dnNode = XMLUtils.searchVHTreeByXpath(dn.getXpath(), XMLUtils.getRoot());
//                if (dnNode.getData().getAttr("origin").equalsIgnoreCase("NOTFOUND")) {
//                    // if the other element is not found then we can not apply to it so we just ignore it
//                    // and add all teh value to the otehr eleet
//                    dependentElements.add(new Element(dn.getXpath(), dn.getProperty(), value, false, dnNode));
//
//                } else {
//                    dependentElements.add(new Element(dn.getXpath(), dn.getProperty(), halfValueToApply, false, dnNode));
//
//                }
//                //ToDO: @Ali does not apply to Android right?
//                // to avoid problem of collapsing margins, add display = inline-block
////				HtmlDomTree instance = HtmlDomTree.getInstance(mFix.getFilepath());
////				DomNode eDisplay = instance.searchHtmlDomTreeByXpath(dn.getxPath()).getData();
////				if(!eDisplay.getCssMap().containsKey("display") || !eDisplay.getCssMap().get("display").contains("inline"))
////				{
////					dependentElements.add(new Element(dn.getxPath(), "display", "inline-block"));
////				}
//
////             // check if element has an image that is less than 48px
////             imageElements = getImageElements(xpath);
////             for (DomNode e : imageElements) {
////                 if (e.getCoord().width < Util.getNumbersFromString(widthValue).get(0)) {
////                     dependentElements.add(new Element(e.getxPath(), "width", widthValue));
////                 }
////                 if (e.getCoord().height < Util.getNumbersFromString(heightValue).get(0)) {
////                     dependentElements.add(new Element(e.getxPath(), "height", heightValue));
////                 }
////             }
//            }
//        }
//        return new ArrayList<>(dependentElements);
//    }
//
//
//    private List<DomNode> getImageElements(String xpath) {
//        List<DomNode> imageElements = new ArrayList<>();
//
//        XMLUtils instance = XMLUtils.getInstance(SALEM.getFilepath());
//        List<Node<DomNode>> children = XMLUtils.searchVHTreeByXpath(xpath, XMLUtils.getRoot()).getChildren();
//        Queue<Node<DomNode>> queue = new LinkedList<Node<DomNode>>();
//
//        if (children != null) {
//            for (Node<DomNode> c : children) {
//                queue.add(c);
//            }
//        }
//
//        while (!queue.isEmpty()) {
//            Node<DomNode> e = queue.remove();
//            //ToDo: @Ali what are the image tags in Android. Only "ImageView"?
//            if (e.getData().getTagName().equalsIgnoreCase("ImageView")) {
//                imageElements.add(e.getData());
//            }
//
//            children = e.getChildren();
//            if (children != null) {
//                for (Node<DomNode> c : children) {
//                    queue.add(c);
//                }
//            }
//        }
//        return imageElements;
//    }
//
//
//    @Override
//    public String toString() {
//        String returnValue = "DependencyGraph = (size = " + dependentNodesMap.size() + ")\n";
//        for (String xpath : dependentNodesMap.keySet()) {
//            returnValue = returnValue + xpath + " -> " + dependentNodesMap.get(xpath) + "\n";
//        }
//        return returnValue;
//    }
//}
