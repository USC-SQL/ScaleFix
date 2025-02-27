//package usc.edu.OwlEye.BuildModels;
//
//import gatech.xpert.dom.DomNode;
//import gatech.xpert.dom.Node;
//import usc.edu.OwlEye.ElementsProperties.*;
//import usc.edu.OwlEye.UIModels.WRG;
//import usc.edu.OwlEye.UIModels.WRGNode_OLD;
//import usc.edu.OwlEye.VH.UI;
//import usc.edu.OwlEye.VHTree.XMLUtils;
//import usc.edu.OwlEye.util.Utils;
//import usc.edu.SALEM.util.Util;
//
//import java.util.*;
//
//public class ConstructWRG {
//
//    protected static ConstructWRG instance = null;
//
//
//    private ConstructWRG(String subjectID) {
//    }
//
//    public static ConstructWRG getInstance() {
//
//        if (instance == null)
//            instance = new ConstructWRG(null);  // do whatever else you need to here
//
//        return instance;
//    }
//
//    public WRG buildGraph(UI ui) {
//        XMLUtils xmlTree = ui.getXMLTree();
//        // Initialize the WRG
//        WRG wrg = new WRG(ui.getUITitle());
//        // (1) create height relationship
//        createPropertyWRG(wrg,xmlTree, Height.propertyName);
//        // (2) create width relationship
//        createPropertyWRG(wrg,xmlTree, Width.propertyName);
//
//        return wrg;
//
//    }
//
//
//
//
//    private void createPropertyWRG(WRG wrg, XMLUtils xmlTree, String property) {
//        // get the height or widht WRG
//        TreeMap<String, List<WRGNode_OLD>>  currPropertyWRG = wrg.getDependentNodesMap().get(property);
//        //TreeMap<String, List<WRGNode>> dependentNodesMap = srg.getDependentNodesMap();
//        Node<DomNode> root = xmlTree.getRoot();
//
//        // add children of the root to the queue
//        Queue<Node<DomNode>> q = new LinkedList<Node<DomNode>>();
//        q.add(root);
//        if (root.getChildren() != null) {
//            for (Node<DomNode> child : root.getChildren()) {
//                q.add(child);
//            }
//        }
//
//
//        //Start the BFS traversal
//
//        while (!q.isEmpty()) {
//            // get a node then look at its children to see if they have weight and add those and handle their weights and record parent total then move on to next
//
//
//            Node<DomNode> parentNode = q.remove();
//
//
//
//            List<Node<DomNode>> childrenNodes = parentNode.getChildren();
////            HashMap<String, List<Node<DomNode>>> childrenWithWeight = new HashMap<String, List<Node<DomNode>>>();
////            childrenWithWeight.put(Weight.propertyName, new ArrayList<Node<DomNode>>());
////            childrenWithWeight.put(ConstraintVerticalWeight.propertyName, new ArrayList<Node<DomNode>>());
////            childrenWithWeight.put(ConstraintHorizontalWeight.propertyName, new ArrayList<Node<DomNode>>());
//            HashMap<Node<DomNode>, Double> childrenWithWeight = new HashMap<Node<DomNode>, Double>();
//            if (childrenNodes != null) {
//                double parentTotal=0;
//                String foundWeightPropertyForChildren=null; // same property should be used for all children so it is ok to assume if we find one then all children have the same one
//
//                // find children with weight related to Height
//                for (Node<DomNode> child : childrenNodes) {
//                    String propVal = Utils.getValueFromElement(child.getData(), property);
//                   if(!Utils.isValueNumerical(propVal)){ // if it is wrap_content or match parent then skip we are looking for applicable cases for weight
//                       continue;
//                   }
//                   double numberVal=Util.getNumbersFromString(propVal).get(0);
//                    if (numberVal!=0){ // if it is not zero then we skip it
//                        continue;
//                    }
//
//                    // now we know it is zero so we should look for weights attributes defined
//
//                    // normal weight att
//                    String[] res = Utils.getDefinedWeightForElement(child, property); // if null then no weight found
//                    if (res != null) {
//                        foundWeightPropertyForChildren=res[0];
//                        childrenWithWeight.put(child, Double.parseDouble(res[1]));
//                        parentTotal+=Double.parseDouble(res[1]);
//
//
//                    }
//                    q.add(child);
//                }
//
//                // now we have all the children with weight we handle them and build the nodes
//                // iterate through all the nodes and create WRG nodes with sibilings that have weight
//                for (Node<DomNode> node : childrenWithWeight.keySet()
//                     ) {
//                    // create the node
//                    // get other noes in the childrenWithWeight
//
//                    ArrayList currentNodeDependentNode=new ArrayList<WRGNode_OLD>();
//                    double currNodeVal=childrenWithWeight.get(node);
//                    for (Node<DomNode> sibling : childrenWithWeight.keySet()
//                         ) {
//                        if (node.equals(sibling)) {
//                            continue;
//                        }
//
//                        double sibilingVal=childrenWithWeight.get(sibling);
//                        double ratio=currNodeVal/sibilingVal;
//                        WRGNode_OLD wrgNode = new WRGNode_OLD(sibling.getData().getxPath(),sibling,foundWeightPropertyForChildren,ratio,currNodeVal,sibilingVal,parentTotal);
//                        currentNodeDependentNode.add(wrgNode);
//                    }
//                    currPropertyWRG.put(node.getData().getxPath(),currentNodeDependentNode);
//
//                }
//
//            }
//
//
//
//                if (parentNode.getChildren() != null) {
//                    for (Node<DomNode> child : parentNode.getChildren()) {
//                        q.add(child);
//                    }
//                }
//            }
//
//
//        } //end of    while (!q.isEmpty())  loop
//
//
//
//
//
//
//
//
//}
//
//
//
//
//
//
//
//
//
