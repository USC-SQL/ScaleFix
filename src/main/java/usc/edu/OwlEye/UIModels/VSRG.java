package usc.edu.OwlEye.UIModels;

import java.util.List;
import java.util.TreeMap;

public class VSRG {
    String VSRGId;
    private TreeMap<String, List<VSRGNode>> dependentNodesMap;        // <xpath, {list of dependent nodes}>

    public VSRG(String SRGId) {
        dependentNodesMap = new TreeMap<String, List<VSRGNode>>();
        this.VSRGId = SRGId;
    }

    @Override
    public String toString() {
        String returnValue = "SRG "+this.VSRGId+"  = (size = " + dependentNodesMap.size() + ")\n";
        for (String xpath : dependentNodesMap.keySet()) {
            //List< VSRGNode> list = dependentNodesMap.get(xpath);
            returnValue = returnValue + xpath + " -> " + dependentNodesMap.get(xpath) + "\n";
        }
        return returnValue;
    }


    public String getSRGId() {
        return VSRGId;
    }

    public void setSRGId(String VSRGId) {
        this.VSRGId = VSRGId;
    }

    public TreeMap<String, List< VSRGNode>> getDependentNodesMap() {
        return dependentNodesMap;
    }

    public void setDependentNodesMap(TreeMap<String, List<VSRGNode>> dependentNodesMap) {
        this.dependentNodesMap = dependentNodesMap;
    }
}