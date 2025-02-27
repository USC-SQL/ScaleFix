package usc.edu.OwlEye.UIModels;

import usc.edu.OwlEye.ElementsProperties.Height;
import usc.edu.OwlEye.ElementsProperties.Width;
import usc.edu.OwlEye.OwlConstants;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class SPLRG {


    String SPLRGId;
//    private TreeMap<String, List<SPLRGNode>> dependentNodesMap;        // <xpath, {list of dependent nodes}>

    private HashMap<String, TreeMap<String, List<SPLRGNode>>> dependentNodesMap; // key: constraints or regular

       // <xpath, {list of dependent nodes}>


    public SPLRG(String SPLRGId) {

        this.SPLRGId = SPLRGId;
        this.dependentNodesMap = new HashMap<String, TreeMap<String, List<SPLRGNode>>>();
        this.dependentNodesMap.put(OwlConstants.CONSTRAINTS_LAYOUT_RELATION, new TreeMap<String, List<SPLRGNode>>());
        this.dependentNodesMap.put(OwlConstants.REGULAR_LAYOUT_RELATION, new TreeMap<String, List<SPLRGNode>>());
    }


    @Override
    public String toString() {
        String returnValue = "SPLRGId "+this.SPLRGId+"  = (size = " + dependentNodesMap.size() + ")\n";
        for (String xpath : dependentNodesMap.keySet()) {
            //List<SPLRGNode> list = dependentNodesMap.get(xpath);
            returnValue = returnValue + xpath + " -> " + dependentNodesMap.get(xpath) + "\n";
        }
        return returnValue;
    }


    public String getSPLRGId() {
        return SPLRGId;
    }

    public void setSPLRGId(String SPLRGId) {
        this.SPLRGId = SPLRGId;
    }

    public HashMap<String, TreeMap<String, List<SPLRGNode>>> getDependentNodesMap() {
        return dependentNodesMap;
    }

    public void setDependentNodesMap(HashMap<String, TreeMap<String, List<SPLRGNode>>> dependentNodesMap) {
        this.dependentNodesMap = dependentNodesMap;
    }


}
