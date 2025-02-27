package usc.edu.SALEM.VHTree;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.MatchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreePair_TT extends TreePair {

    public DomNode merge(DomNode staticNode, DomNode dynamicNode) {
        MatchResult matchResult = match(dynamicNode, staticNode);

        Map<DomNode, DomNode> matched = matchResult.getMatched();
        Map<DomNode, DomNode> unmatched2ToNodeCopy = new HashMap<>();
        if (matched.isEmpty()) {
            System.err.println("No pairs of nodes are matched!");
        } else {
            System.out.println("\nStart merging...");
            List<DomNode> unmatched2 = matchResult.getUnmatched2();
            // start merging in a BFS order
            // I do not want to merge the static values... ALi
//            for (DomNode node : unmatched2) {
//                DomNode parent = node.getParent();
//                DomNode targetInTree1;
//                if (unmatched2ToNodeCopy.containsKey(parent)) {
//                    targetInTree1 = unmatched2ToNodeCopy.get(parent);
//                } else {
//                    targetInTree1 = findMatchedInTree1(matched, parent);
//                }
//                DomNode copy = new DomNode(node.getTagName());
//                Map<String, String> attributes = Maps.newHashMap(node.getAttributes());
//                copy.setAttributes(attributes);
//                copy.setId(node.getId());
//                copy.setAttr("isCopy", "true");
//                targetInTree1.addChild(copy);
//                unmatched2ToNodeCopy.put(node, copy);
//            }

            // copy some properties of tree2 nodes to tree1 nodes
            for (Map.Entry<DomNode, DomNode> entry : matched.entrySet()) {
                DomNode node1 = entry.getKey();
                DomNode node2 = entry.getValue();
                // Warning: the copy may have problem due to multiple values for bounds
                String boundsVal = node2.getAttr("origin");
                if (boundsVal != null) { // node1 is from static analysis tree
                    String node2Tag = node2.getTagName();
                    node1.setAttr("origin", node2.getAttr("origin"));
                    node1.setAttr("StaticTagName", node2Tag);
                    String backgroundVal = node2.getAttr("android:background");
                    if (backgroundVal != null) {
                        node1.setAttr("android:background", backgroundVal);
                    }
                    String layout_height = node2.getAttr("android:layout_height");
                    if (layout_height != null) {
                        node1.setAttr("android:layout_height", layout_height);
                    }

                    String layout_width = node2.getAttr("android:layout_width");
                    if (layout_width != null) {
                        node1.setAttr("android:layout_width", layout_width);
                    }

                    String src = node2.getAttr("android:src");
                    if (src != null) {
                        node1.setAttr("android:src", src);
                    }

                    String directBackground = node2.getAttr("directBackground");
                    if (directBackground != null) {
                        node1.setAttr("directBackground", directBackground);
                    }
//                    String backgroundVal1 = node1.getAttr("android:background");
//                    if (backgroundVal1 != null && hasUnresolvableResult(backgroundVal1)){
                    // now always copy to static result, and let following part to choose use the info or not
                    String backgroundVal2 = node2.getAttr("background");
                    // String node1Tag = node1.getTagName();
                    if (!node2Tag.contains("MapView")) {
                        if (backgroundVal2 != null) {
                            node1.setAttr("background", backgroundVal2);
                        }
                    }
//                    }
                    // now always copy to static result, and let following part to choose use the info or not
//                    String textColorVal1 = node1.getAttr("android:textColor");
//                    if (textColorVal1 != null && hasUnresolvableResult(textColorVal1)) {
                    String textColorVal2 = node2.getAttr("textColor");
                    if (textColorVal2 != null) {
                        node1.setAttr("textColor", textColorVal2);
                    }
//                    }
                } else {
                    // dynamic vs. dynamic
                    mergeAttrToNode1(node1, node2, "background");
                    mergeAttrToNode1(node1, node2, "textColor");
                }
            }
        }
        return dynamicNode;
    }
}
