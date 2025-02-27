package gatech.xpert.dom;

//import usc.edu.SALEM.VHTree.Node;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
import java.util.List;
import java.util.Map;

public class MatchResult2 {
    Map<Node<DomNode>, Node<DomNode>> matched;
    List<Node<DomNode>> unmatched1, unmatched2;

    public MatchResult2(Map<Node<DomNode>, Node<DomNode>> matched, List<Node<DomNode>> unmatched1,
                        List<Node<DomNode>> unmatched2) {
        this.matched = matched;
        this.unmatched1 = unmatched1;
        this.unmatched2 = unmatched2;
    }

    // Setters and Getters

    public Map<Node<DomNode>, Node<DomNode>> getMatched() {
        return matched;
    }

    public void setMatched(Map<Node<DomNode>, Node<DomNode>> matched) {
        this.matched = matched;
    }

    public List<Node<DomNode>> getUnmatched1() {
        return unmatched1;
    }

    public void setUnmatched1(List<Node<DomNode>> unmatched1) {
        this.unmatched1 = unmatched1;
    }

    public List<Node<DomNode>> getUnmatched2() {
        return unmatched2;
    }

    public void setUnmatched2(List<Node<DomNode>> unmatched2) {
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
