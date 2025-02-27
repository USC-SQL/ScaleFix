package usc.edu.OwlEye.UIModels;


import usc.edu.OwlEye.ElementsProperties.Height;
import usc.edu.OwlEye.ElementsProperties.Width;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class SRG {

    String SRGId;
   // private TreeMap<String, List<SRGNode>> dependentNodesMap;        // <xpath, {list of dependent nodes}>
    private HashMap<String, TreeMap<String, List<SRGNode>>> dependentNodesMap2; // <property, <xpath, {list of dependent nodes}>> property is height, width for now


    public SRG(String SRGId) {
       // dependentNodesMap = new TreeMap<String, List<SRGNode>>();
        this.SRGId = SRGId;
        this.dependentNodesMap2 = new HashMap<String, TreeMap<String, List<SRGNode>>>();
        this.dependentNodesMap2.put(Height.propertyName, new TreeMap<String, List<SRGNode>>());
        this.dependentNodesMap2.put(Width.propertyName, new TreeMap<String, List<SRGNode>>());

    }



    @Override
    public String toString() {
        String returnValue = "SRG: " + SRGId + "\n";
        for (String property : dependentNodesMap2.keySet()) {
            //List<SRGNode> list = dependentNodesMap.get(xpath);
            returnValue = returnValue + property+"  = (size = " + dependentNodesMap2.get(property).size() + ")\n";
        }
        for (String property : dependentNodesMap2.keySet()) {
            TreeMap<String, List<SRGNode>> dependentNodes = dependentNodesMap2.get(property);
            for (String xpath : dependentNodes.keySet()) {
                //List<SRGNode> list = dependentNodesMap.get(xpath);
                returnValue = returnValue + xpath + " -> " + dependentNodes.get(xpath) + "\n";
            }
            System.out.println("*************\n");

        }

        return returnValue;
    }
//    @Override
//    public String toString() {
//        String returnValue = "SRG "+this.SRGId+"  = (size = " + dependentNodesMap.size() + ")\n";
//        for (String xpath : dependentNodesMap.keySet()) {
//            //List<SRGNode> list = dependentNodesMap.get(xpath);
//            returnValue = returnValue + xpath + " -> " + dependentNodesMap.get(xpath) + "\n";
//        }
//        return returnValue;
//    }


    public String getSRGId() {
        return SRGId;
    }

    public void setSRGId(String SRGId) {
        this.SRGId = SRGId;
    }

//    public TreeMap<String, List<SRGNode>> getDependentNodesMap() {
//        return dependentNodesMap;
//    }

//    public void setDependentNodesMap(TreeMap<String, List<SRGNode>> dependentNodesMap) {
//        this.dependentNodesMap = dependentNodesMap;
//    }

    public HashMap<String, TreeMap<String, List<SRGNode>>> getDependentNodesMap2() {
        return dependentNodesMap2;
    }
    public void setDependentNodesMap2(HashMap<String, TreeMap<String, List<SRGNode>>> dependentNodesMap2) {
        this.dependentNodesMap2 = dependentNodesMap2;
    }
}
