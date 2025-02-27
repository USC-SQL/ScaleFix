package gatech.xpert.dom;

import java.util.List;
import java.util.Map;

public class MatchResult {
    Map<DomNode, DomNode> matched;
    List<DomNode> unmatched1, unmatched2;

    public MatchResult(Map<DomNode, DomNode> matched, List<DomNode> unmatched1,
                       List<DomNode> unmatched2) {
        this.matched = matched;
        this.unmatched1 = unmatched1;
        this.unmatched2 = unmatched2;
    }

    // Setters and Getters

    public Map<DomNode, DomNode> getMatched() {
        return matched;
    }

    public void setMatched(Map<DomNode, DomNode> matched) {
        this.matched = matched;
    }

    public List<DomNode> getUnmatched1() {
        return unmatched1;
    }

    public void setUnmatched1(List<DomNode> unmatched1) {
        this.unmatched1 = unmatched1;
    }

    public List<DomNode> getUnmatched2() {
        return unmatched2;
    }

    public void setUnmatched2(List<DomNode> unmatched2) {
        this.unmatched2 = unmatched2;
    }
//    public Map<Node<DomNode>, Node<DomNode>> getMatchedNodes(Map<String, Node<DomNode>> baseElements, Map<String,Node<DomNode>> testElements){
//        Map<Node<DomNode>, Node<DomNode>> matchedNodes = new HashMap<Node<DomNode>, Node<DomNode>>();
//        for (String baseXpath : matchedXpath.keySet()) {
//            String putXpath = matchedXpath.get(baseXpath);
//            Node<DomNode> baseNode = baseElements.get(baseXpath);
//            Node<DomNode> putNode = testElements.get(putXpath);
//            matchedNodes.put(baseNode, putNode);
//        }
//        return matchedNodes;
//    }
}
