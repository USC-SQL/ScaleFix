package usc.edu.OwlEye.BuildModels;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import org.tinylog.Logger;
import usc.edu.OwlEye.OwlConstants;
import usc.edu.OwlEye.OwlEye;
import usc.edu.OwlEye.UIModels.*;
import usc.edu.OwlEye.VH.UI;
import usc.edu.OwlEye.VHTree.XMLUtils;
import usc.edu.OwlEye.util.Utils;
import usc.edu.SALEM.util.Util;

import java.util.*;
//import java.util.logging.Logger;

public class ConstructSPLRG {


    protected static ConstructSPLRG instance = null;


    private ConstructSPLRG(String subjectID) {
    }

    public static ConstructSPLRG getInstance() {

        if (instance == null)
            instance = new ConstructSPLRG(null);  // do whatever else you need to here

        return instance;
    }

    public SPLRG buildGraph(UI ui) {
     //   XMLUtils xmlTree = ui;
        // Initialize the SRG
        SPLRG splrg = new SPLRG("");
//        // (1) create CONSTRAINTS_LAYOUT_RELATION relationship
//        extractLayoutRelationships(splrg,ui);
//        // (2) create REGULAR_LAYOUT_RELATION relationship
//        extractLayoutRelationships(splrg,ui);
        ;
        extractLayoutRelationships(splrg,ui);
        // (2) create width relationship
        return splrg;

    }




    private void extractLayoutRelationships(SPLRG splrg, UI ui) {
     ///   TreeMap<String, List<SPLRGNode>> dependentNodesMap = splrg.getDependentNodesMap();
        TreeMap<String, List<SPLRGNode>> generalRelationshipDependentNodesMap= splrg.getDependentNodesMap().get(OwlConstants.REGULAR_LAYOUT_RELATION);
        TreeMap<String, List<SPLRGNode>> constraintRelationshipDependentNodesMap= splrg.getDependentNodesMap().get(OwlConstants.CONSTRAINTS_LAYOUT_RELATION);

        Node<DomNode> root = ui.getXMLTree().getRoot();

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
            // check for these layout properties
            HashMap<String, String> foundLayoutAttributes = extractElementLayoutProperties(e);
            HashMap<String, String> foundConstraintsLayoutAttributes = extractElementConstraintsLayoutProperties(e);
            //System.out.println("ID: "+ e.getId() );
            List<SPLRGNode> generalDependentNodes = new ArrayList<SPLRGNode>();
            List<SPLRGNode> constraintDependentNodes = new ArrayList<SPLRGNode>();
            for (String property : foundLayoutAttributes.keySet()
                 ) {
                String value = foundLayoutAttributes.get(property);
                if (value.startsWith("@id/")) {
                    value = value.substring(4, value.length());
                    System.out.println("value: " + value);
                    Node<DomNode> foundNode = OwlEye.getOriginalDefaultUI().getXMLTree().searchByID_T(value, null);
                    if (foundNode != null) {
                        //System.out.println("foundNode: " + foundNode.getData().getxPath());
                        propertyValue<String> pv = new propertyValue<String>(property, value,"id");
                        SPLRGNode dependentNode = new SPLRGNode(foundNode.getData().getxPath(), value, property,pv);
                        generalDependentNodes.add(dependentNode);
                    }

                }
            }

            for (String property : foundConstraintsLayoutAttributes.keySet()
            ) {
                String value = foundConstraintsLayoutAttributes.get(property);
                boolean isParent=false;
                Node<DomNode> foundNode=null;
                String valType=""; // id / parent /or specific value (bias percentage) depending on how the constriant is defined
//                if (value.startsWith("@id/") || value.startsWith("parent")) {
                    if (value.startsWith("@id/")) {
                        value = value.substring(4, value.length());
                       foundNode = OwlEye.getOriginalDefaultUI().getXMLTree().searchByID_T(value, null);
                        valType="id";
                    }
                    else if(value.startsWith("parent")){
                        foundNode = node.getParent();
                        isParent=true;
                        valType="parent";
                    }
                    else {
                        valType="number";
                        foundNode=node;
                    }
                    if (foundNode != null) {
                        //System.out.println("foundNode: " + foundNode.getData().getxPath());

                        propertyValue<String> pv = new propertyValue<String>(property, value,valType);
                        SPLRGNode dependentNode = new SPLRGNode(foundNode.getData().getxPath(), value, property,pv);
                        constraintDependentNodes.add(dependentNode);
                    }

                }

           if(e.getId()!=null) {

               generalRelationshipDependentNodesMap.put(e.getId(), generalDependentNodes);
                constraintRelationshipDependentNodesMap.put(e.getId(), constraintDependentNodes);
           }

            if (node.getChildren() != null) {
                for (Node<DomNode> child : node.getChildren()) {
                    q.add(child);
                }
            }


        }  //end of    while (!q.isEmpty())  loop




    }

    private HashMap<String, String> extractElementConstraintsLayoutProperties(DomNode e) {

        HashMap<String, String> elementProperties =new HashMap<>();
        Map<String, String> attributes = e.getAttributes();
        for (String key : attributes.keySet()
        ) {
            //System.out.println("key: " + key);
            if (OwlConstants.CONSTRAINTS_LAYOUT_ATTRIBUTES_MAP.containsKey(key)
            || OwlConstants.CONSTRAINTS_LAYOUT_ATTRIBUTES_MAP.containsValue(key)){
                String value = attributes.get(key);
                //System.out.println("Found layout attribute: "+key+" with value: "+value);
                elementProperties.put(key,value);

            }
        }
//        if (elementProperties!=null && elementProperties.size()>0){
            return elementProperties;
//        }
//        else {
//            return null;
//        }

    }
    private HashMap<String, String> extractElementLayoutProperties(DomNode e) {
        
    HashMap<String, String> elementProperties =new HashMap<>();
        Map<String, String> attributes = e.getAttributes();
        for (String key : attributes.keySet()
             ) {

            if (OwlConstants.LAYOUT_ATTRIBUTES_MAP.containsKey(key)){
                String value = attributes.get(key);
                //System.out.println("Found layout attribute: "+key+" with value: "+value);
                elementProperties.put(key,value);

        }
    }
//        if (elementProperties!=null && elementProperties.size()>0){
//            return elementProperties;
//        }
//        else {
            return elementProperties;
       // }

    }
}

