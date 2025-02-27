package usc.edu.OwlEye.UIModels;

import usc.edu.OwlEye.ElementsProperties.Height;
import usc.edu.OwlEye.ElementsProperties.Width;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class WRG2 {

    String WRGId;
    // private TreeMap<String, List<WRGNode>> dependentNodesMap;        // <xpath, {list of dependent nodes}>
    private HashMap<String, TreeMap<String, List<WRGNode_OLD>>> dependentNodesMap; // <property, <xpath, {list of dependent nodes}>> property is height, width for now


    public WRG2(String SRGId) {
        // dependentNodesMap = new TreeMap<String, List<SRGNode>>();
        this.WRGId = SRGId;
        this.dependentNodesMap = new HashMap<String, TreeMap<String, List<WRGNode_OLD>>>();
        this.dependentNodesMap.put(Height.propertyName, new TreeMap<String, List<WRGNode_OLD>>()); // weight for height
        this.dependentNodesMap.put(Width.propertyName, new TreeMap<String, List<WRGNode_OLD>>()); // weight for width
    }


    public String getWRGId() {
        return WRGId;
    }

    public void setWRGId(String WRGId) {
        this.WRGId = WRGId;
    }

    public HashMap<String, TreeMap<String, List<WRGNode_OLD>>> getDependentNodesMap() {
        return dependentNodesMap;
    }

    public void setDependentNodesMap(HashMap<String, TreeMap<String, List<WRGNode_OLD>>> dependentNodesMap) {
        this.dependentNodesMap = dependentNodesMap;
    }

    @Override
    public String toString() {
        String returnValue = "WRG: " + WRGId + "\n";
        for (String property : dependentNodesMap.keySet()) {
            //List<SRGNode> list = dependentNodesMap.get(xpath);
            returnValue = returnValue + property+"  = (size = " + dependentNodesMap.get(property).size() + ")\n";
        }
        for (String property : dependentNodesMap.keySet()) {
            TreeMap<String, List<WRGNode_OLD>> dependentNodes = dependentNodesMap.get(property);
            for (String xpath : dependentNodes.keySet()) {
                //List<SRGNode> list = dependentNodesMap.get(xpath);
                returnValue = returnValue + xpath + " -> " + dependentNodes.get(xpath) + "\n";
            }
            System.out.println("*************\n");

        }

        return returnValue;
    }

}
