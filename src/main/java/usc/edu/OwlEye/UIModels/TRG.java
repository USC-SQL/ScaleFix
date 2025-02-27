package usc.edu.OwlEye.UIModels;

import usc.edu.OwlEye.ElementsProperties.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TRG {

    // text relation graph. Not a graph per se but shows elements that has text constraitns or things that we need to consider or change


    String TRGId;
    public static final Map<String, String> TRG_ATTRIBUTES = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {
            put(MaxLine.propertyName, "android:maxLines");
            put(Lines.propertyName, "android:lines");
        }
    };
    private TreeMap<String,TRGNode> dependentNodesMap;        // <xpath, nodes>

    public TRG(String TRGId) {
        this.TRGId = TRGId;
        dependentNodesMap = new TreeMap<String, TRGNode>();
    }

    public String getTRGId() {
        return TRGId;
    }

    public void setTRGId(String TRGId) {
        this.TRGId = TRGId;
    }

    public TreeMap<String, TRGNode> getDependentNodesMap() {
        return dependentNodesMap;
    }

    public void setDependentNodesMap(TreeMap<String, TRGNode> dependentNodesMap) {
        this.dependentNodesMap = dependentNodesMap;
    }
}
