package usc.edu.SALEM.VHTree;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.MatchResult;
import gatech.xpert.dom.Matcher;

import java.io.File;
import java.util.*;

public class TreePair {

    public DomNode merge(File file1, File file2) {

        DomNode root1 = XMLUtils.readXML(file1.getAbsolutePath());
        DomNode root2 = XMLUtils.readXML(file2.getAbsolutePath());

        DomNode newRoot = merge(root1, root2);

        return newRoot;
    }

    public DomNode merge(String filePath1, String filePath2) {
        DomNode root1 = XMLUtils.readXML(filePath1);
        DomNode root2 = XMLUtils.readXML(filePath2);

        DomNode newRoot = merge(root1, root2);

        return newRoot;
    }

    void printUnmatchedNodes(List<DomNode> unmatched) {
        for (DomNode node : unmatched) {
            String parent = null;
            if (node.getParent() != null) {
                parent = node.getParent().getTagName();
            }
            System.out.println(node.getTagName() + "@xPath=" + node.getxPath() +
                    "@level=" + node.getLevel() + "@parent=" + parent);
        }
    }

    void printMatchedNodes(Map<DomNode, DomNode> matched) {
        for (Map.Entry<DomNode, DomNode> entry : matched.entrySet()) {
            DomNode node1 = entry.getKey();
            DomNode node2 = entry.getValue();
            System.out.println(node1.getTagName() + "@xPath=" + node1.getxPath() +
                    "@level=" + node1.getLevel() + "\t->\t" + node2.getTagName() + "@xPath=" + node2.getxPath() +
                    "@level=" + node2.getLevel());
        }
    }

    public Map<DomNode, DomNode> get_matched(DomNode root1, DomNode root2) {
        MatchResult matchResult = match(root1, root2);

        Map<DomNode, DomNode> matched = matchResult.getMatched();
        Map<DomNode, DomNode> unmatched2ToNodeCopy = new HashMap<>();
        if (matched.isEmpty()) {
            System.err.println("No pairs of nodes are matched!");
            return null;
        } else {
            return matched;
        }
    }

    public DomNode merge(DomNode root1, DomNode root2) {
        MatchResult matchResult = match(root1, root2);

        Map<DomNode, DomNode> matched = matchResult.getMatched();
        Map<DomNode, DomNode> unmatched2ToNodeCopy = new HashMap<>();
        if (matched.isEmpty()) {
            System.err.println("No pairs of nodes are matched!");
        } else {
            System.out.println("\nStart merging...");
            List<DomNode> unmatched2 = matchResult.getUnmatched2();
            // start merging in a BFS order
            for (DomNode node : unmatched2) {
                DomNode parent = node.getParent();
                DomNode targetInTree1;
                if (unmatched2ToNodeCopy.containsKey(parent)) {
                    targetInTree1 = unmatched2ToNodeCopy.get(parent);
                } else {
                    targetInTree1 = findMatchedInTree1(matched, parent);
                }
                DomNode copy = new DomNode(node.getTagName());
                Map<String, String> attributes = Maps.newHashMap(node.getAttributes());
                copy.setAttributes(attributes);
                copy.setId(node.getId());
                copy.setAttr("isCopy", "true");
                targetInTree1.addChild(copy);
                unmatched2ToNodeCopy.put(node, copy);
            }

            // copy some properties of tree2 nodes to tree1 nodes
            for (Map.Entry<DomNode, DomNode> entry : matched.entrySet()) {
                DomNode node1 = entry.getKey();
                DomNode node2 = entry.getValue();
                // Warning: the copy may have problem due to multiple values for bounds
                String boundsVal = node1.getAttr("bounds");
                if (boundsVal == null) { // node1 is from static analysis tree
                    node1.setAttr("bounds", node2.getAttr("bounds"));
                    node1.setAttr("clickable", node2.getAttr("clickable"));
//                    String backgroundVal1 = node1.getAttr("android:background");
//                    if (backgroundVal1 != null && hasUnresolvableResult(backgroundVal1)){
                    // now always copy to static result, and let following part to choose use the info or not
                    String backgroundVal2 = node2.getAttr("background");
                    String node1Tag = node1.getTagName();
                    if (!node1Tag.contains("MapView")) {
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
        return root1;
    }

    public MatchResult match(DomNode root1, DomNode root2) {
        Matcher matcher = new Matcher();
        MatchResult matchResult = matcher.doMatch(root1, root2);
        Map<DomNode, DomNode> matched = matchResult.getMatched();
        System.out.println("=======Matched nodes=======");
        printMatchedNodes(matched);
        // display the unmatched nodes
        List<DomNode> unmatched1 = matchResult.getUnmatched1();
        if (!unmatched1.isEmpty()) {
            System.out.println("=======Unmatched in file1=======");
        }
        printUnmatchedNodes(unmatched1);

        List<DomNode> unmatched2 = matchResult.getUnmatched2();
        if (!unmatched2.isEmpty()) {
            System.out.println("=======Unmatched in file2=======");
        }
        // Sort based on level
        Collections.sort(unmatched2, (n1, n2) -> {
            return n1.getLevel() - n2.getLevel();
        });
        printUnmatchedNodes(unmatched2);
        return matchResult;
    }

    DomNode findMatchedInTree1(Map<DomNode, DomNode> matched, DomNode node) {
        DomNode target = null;
        for (Map.Entry<DomNode, DomNode> entry : matched.entrySet()) {
            DomNode n2 = entry.getValue();
            if (n2.equals(node)) {
                target = entry.getKey();
                break;
            }
        }
        return target;
    }

    void mergeAttrToNode1(DomNode node1, DomNode node2, String attr) {
        Set<String> valueSet = Sets.newHashSet();
        // merge attr values
        // for background
        String attrValue = node1.getAttr(attr);
        if (attrValue != null) {
            if (attrValue.contains("|")) {
                valueSet.addAll(Arrays.asList(attrValue.split("|")));
            } else {
                valueSet.add(attrValue);
            }
        }
        attrValue = node2.getAttr(attr);
        if (attrValue != null) {
            if (attrValue.contains("|")) {
                valueSet.addAll(Arrays.asList(attrValue.split("|")));
            } else {
                valueSet.add(attrValue);
            }
        }
        StringBuilder sb = new StringBuilder();
        valueSet.forEach(c -> sb.append(c).append('|'));
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        attrValue = sb.toString();
        if (!attrValue.equals("")) {
            node1.setAttr(attr, attrValue);
        }
    }
    //If using Mian's Analysis
    boolean hasUnresolvableResult(String str) {
        String[] strArray = str.split("|");
        for (String s : strArray) {
            if (s.contains("<") || s.contains("$") || s.contains(">") || s.contains("[") || s.contains("]")) {
                return true;
            }
        }
        return false;
    }
}
