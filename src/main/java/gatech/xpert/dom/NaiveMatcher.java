//package gatech.xpert.dom;
//
//import gatech.xpert.dom.visitors.DomVisitor;
//import gatech.xpert.dom.visitors.NaiveMatchVisitor;
//import gatech.xpert.dom.visitors.UnmatchedVisitor;
//import mfix.domTree.Node;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class NaiveMatcher extends Matcher {
//    @Override
//    public MatchResult doMatch(DomNode root1, DomNode root2) {
//        // 1. Perfect Match Visitor
//        List<DomNode> worklist = new ArrayList<>();
//        worklist.add(root1);
//        while (!worklist.isEmpty()) {
//            DomNode node = worklist.remove(0);
//            DomVisitor pmv = new NaiveMatchVisitor(node, matched);
//            root2.accept(pmv);
//
//            for (DomNode child : node.getChildren()) {
//                worklist.add(child);
//            }
//        }
//
//        UnmatchedVisitor uv = new UnmatchedVisitor();
//        root1.accept(uv);
//        unmatched1 = uv.getUnmatched();
//
//        uv.init();
//        root2.accept(uv);
//        unmatched2 = uv.getUnmatched();
//
//        return new MatchResult(matched, unmatched1, unmatched2);
//    }
//}
