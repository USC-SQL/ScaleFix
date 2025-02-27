package gatech.xpert.dom;

import gatech.xpert.dom.visitors.*;
//import usc.edu.SALEM.VHTree.Node;
import gatech.xpert.dom.Node;
import usc.edu.SALEM.VHTree.XMLUtils;

import java.util.*;

public class Matcher {

    float THRESHOLD_LEVEL = 0.70f;
    Map<String, String> matchedXpath;
    protected Map<Node<DomNode>, Node<DomNode>> nodematched;
    protected List<Node<DomNode>> nodeunmatched1, nodeunmatched2;
    protected Map<DomNode, DomNode> matched;
    protected List<DomNode> unmatched1, unmatched2;

    public Matcher() {
        matched = new HashMap<>();
        unmatched1 = new ArrayList<>();
        unmatched2 = new ArrayList<>();
        nodematched = new HashMap<>();
        nodeunmatched1 = new ArrayList<>();
        nodeunmatched2 = new ArrayList<>();
    }

    public Map<Node<DomNode>, Node<DomNode>> getNodematched() {
        return nodematched;
    }

    public void setNodematched(Map<Node<DomNode>, Node<DomNode>> nodematched) {
        this.nodematched = nodematched;
    }

    public List<Node<DomNode>> getNodeunmatched1() {
        return nodeunmatched1;
    }

    public void setNodeunmatched1(List<Node<DomNode>> nodeunmatched1) {
        this.nodeunmatched1 = nodeunmatched1;
    }

    public List<Node<DomNode>> getNodeunmatched2() {
        return nodeunmatched2;
    }

    public void setNodeunmatched2(List<Node<DomNode>> nodeunmatched2) {
        this.nodeunmatched2 = nodeunmatched2;
    }

    //    public MatchResult doMatchIfix(Map<String, Node<DomNode>> doms, String[] urls) {
//
//        if(doms.size() < 2) {
//            System.err.println("DOM compare error: list size="+doms.size());
//            return null;
//        }
//
//        Node<DomNode> root1 = doms.get(urls[0]);
//        Node<DomNode> root2 = doms.get(urls[1]);
//
//        // 1: Perfect Match Visitor
//        List<Node<DomNode>> worklist = new ArrayList<Node<DomNode>>();
//        worklist.add(root1);
//        while(!worklist.isEmpty()) {
//            Node<DomNode> node = worklist.remove(0);
//            DomVisitor pmv = new ExactMatchVisitor(node.getData(), matched);
//            root2.getData().accept(pmv, false);
//            for(Node<DomNode> child: node.getChildren()) {
//                worklist.add(child);
//            }
//        }
//
//        UnmatchedVisitor uv = new UnmatchedVisitor();
//        root1.getData().accept(uv);
//        List<DomNode> unmatchedNodes = uv.getUnmatched();
//
//        // 2: ignore font tag matching.
//        // used to handle cases where <font> tag is added to the text
//        // for example google translate pads text elements with font tags
//        // this matcher ignores these added font tags
////        for(DomNode node : unmatchedNodes) {
////            DomVisitor iftMatchVisitor = new IgnoreFontTagMatchVisitor(node, matched);
////            root2.getData().accept(iftMatchVisitor, false);
////        }
//
//
//
//        // Assign levels
//        LevelAssignVisitor lvl = new LevelAssignVisitor();
//        root1.accept(lvl, true);
//        lvl.init();
//        root2.accept(lvl, true);
//        List<List<DomNode>> levels2 = lvl.getLevels();
//
//        uv = new UnmatchedVisitor();
//        root1.accept(uv);
//        unmatchedNodes = uv.getUnmatched();
//
//        // 3: Level Match Visitor
//        for(DomNode node : unmatchedNodes) {
//            int level = node.getLevel();
//            if(level < levels2.size()) {
//                List<DomNode> lNodes = levels2.get(level);
//                float bestMatchIndex = 0;
//                DomNode bestMatchNode = null;
//                for(DomNode ln : lNodes) {
//                    if(!ln.isMatched() && ln.isTag()) {
//                        float matchIdx = DomUtils.calculateMatchIndex(node, ln);
//                        if(matchIdx >= THRESHOLD_LEVEL && matchIdx > bestMatchIndex) {
//                            bestMatchIndex = matchIdx;
//                            bestMatchNode = ln;
//                        }
//                    }
//                }
//                if(bestMatchNode != null) {
//                    node.setMatched(true);
//                    bestMatchNode.setMatched(true);
//                    matched.put(node, bestMatchNode);
//                } else {
//                    worklist.add(node);
//                }
//            }
//            else{
//                worklist.add(node);
//            }
//        }
//
//        int numberOfLevelMatch = matched.size();
//
//
//        // 4: Approximate global matching
//        for(DomNode node : worklist) {
//            ApproxMatchVisitor amv = new ApproxMatchVisitor(node, matched);
//            root2.accept(amv);
//            amv.matchPost();
//            if(!node.isMatched()) {
//                unmatched1.add(node);
//            }
//        }
//
//        uv.init();
//        root2.accept(uv);
//        unmatched2 = uv.getUnmatched();
//
//
//        return new MatchResult(matched, unmatched1, unmatched2, numberOfLevelMatch);
//    }
    public MatchResult2 doMatch2(Node<DomNode> root1, Node<DomNode> root2) {
        searchMatched(root1, root2);
        return new MatchResult2(nodematched, nodeunmatched1, nodeunmatched2);

    }

    public void searchMatched(Node<DomNode> merged, Node<DomNode> crawled) {


        Queue<Node<DomNode>> q = new LinkedList<Node<DomNode>>();
        q.add(merged);

//        // process descendants of the root in a bread first fashion
        while (!q.isEmpty()) {
            Node<DomNode> currMergedNode = q.remove();
//            DomNode currMergedNode = node.getData();

            String currentNodeXpath = currMergedNode.getData().getxPath();
//            System.out.println("currentNodeXpath: " + currentNodeXpath);
//            if(currentNodeXpath.contains("tvDailyFoodFact")){
//                System.out.println("debug");
//            }
            Node<DomNode> foundCrawledNode = XMLUtils.searchVHTreeByXpath(currentNodeXpath, crawled);// Find the node in the crawled Version
            // handling only difference in scrollView
            if (foundCrawledNode != null) {
                nodematched.put(currMergedNode, foundCrawledNode);
//                if (!success) {
//                    System.exit(1);
//                    System.out.println("Could not update the bounds from the crawled app");
//                }
            }
            else{
                nodeunmatched1.add(currMergedNode);
            }


            if (currMergedNode.getChildren() != null) {
                for (Node<DomNode> child : currMergedNode.getChildren()) {
                    q.add(child);
                }
            }
        }

    }

    //    public MatchResult doMatch(Node<DomNode> root1, Node<DomNode> root2) {
//        // 1. Perfect Match Visitor
//        List<Node<DomNode>> worklist = new ArrayList<>();
//        worklist.add(root1);
//        while (!worklist.isEmpty()) {
//            Node<DomNode> node = worklist.remove(0);
//            DomVisitor pmv = new ExactMatchVisitor(node, matched);
//            root2.accept(pmv);
//
//            for (Node<DomNode> child : node.getChildren()) {
//                worklist.add(child);
//            }
//        }
//
//        // Assign levels
//        LevelAssignVisitor2 lvl = new LevelAssignVisitor2();
//        root1.accept(lvl);
//        List<List<Node<DomNode>>> levels1 = lvl.getLevels();
//        lvl.init();
//        root2.accept(lvl);
//        List<List<Node<DomNode>>> levels2 = lvl.getLevels();
//
//        UnmatchedVisitor2 uv = new UnmatchedVisitor2();
//        root1.accept(uv);
//        List<Node<DomNode>> unmatchedNodes = uv.getUnmatched();
//
//        // 2. Level Match Visitor
//        for (Node<DomNode> node : unmatchedNodes) {
//            int level = node.getLevel();
//            if (level < levels2.size()) {
//                List<Node<DomNode>> lNodes = levels2.get(level);
//                float bestMatchIndex = 0;
//                Node<DomNode> bestMatchNode = null;
//                for (Node<DomNode> ln : lNodes) {
//                    if (!ln.isMatched()) {
//                        float matchIdx = DomUtils.calculateMatchIndex(node, ln);
//                        if (matchIdx >= THRESHOLD_LEVEL && matchIdx > bestMatchIndex) {
//                            bestMatchIndex = matchIdx;
//                            bestMatchNode = ln;
//                        }
//                    }
//                }
//                if (bestMatchNode != null) {
//                    node.setMatched(true);
//                    bestMatchNode.setMatched(true);
//                    matched.put(node, bestMatchNode);
//                } else {
//                    worklist.add(node);
//                }
//            }
//        }
//
//        // 3. possible global matching, but empty for now
//        for (Node<DomNode> node : worklist) {
//            if (!node.isMatched()) {
//                unmatched1.add(node);
//            }
//        }
//
//        uv.init();
//        root2.accept(uv);
//        unmatched2 = uv.getUnmatched();
//
//        return new MatchResult(matched, unmatched1, unmatched2);
//    }
    public MatchResult doMatch(DomNode root1, DomNode root2) {
        // 1. Perfect Match Visitor
        List<DomNode> worklist = new ArrayList<>();
        worklist.add(root1);
        while (!worklist.isEmpty()) {
            DomNode node = worklist.remove(0);
            DomVisitor pmv = new ExactMatchVisitor(node, matched);
            root2.accept(pmv);

            for (DomNode child : node.getChildren()) {
                worklist.add(child);
            }
        }

        // Assign levels
        LevelAssignVisitor lvl = new LevelAssignVisitor();
        root1.accept(lvl);
        List<List<DomNode>> levels1 = lvl.getLevels();
        lvl.init();
        root2.accept(lvl);
        List<List<DomNode>> levels2 = lvl.getLevels();

        UnmatchedVisitor uv = new UnmatchedVisitor();
        root1.accept(uv);
        List<DomNode> unmatchedNodes = uv.getUnmatched();

        // 2. Level Match Visitor
        for (DomNode node : unmatchedNodes) {
            int level = node.getLevel();
            if (level < levels2.size()) {
                List<DomNode> lNodes = levels2.get(level);
                float bestMatchIndex = 0;
                DomNode bestMatchNode = null;
                for (DomNode ln : lNodes) {
                    if (!ln.isMatched()) {
                        float matchIdx = DomUtils.calculateMatchIndex(node, ln);
                        if (matchIdx >= THRESHOLD_LEVEL && matchIdx > bestMatchIndex) {
                            bestMatchIndex = matchIdx;
                            bestMatchNode = ln;
                        }
                    }
                }
                if (bestMatchNode != null) {
                    node.setMatched(true);
                    bestMatchNode.setMatched(true);
                    matched.put(node, bestMatchNode);
                } else {
                    worklist.add(node);
                }
            }
        }

        // 3. possible global matching, but empty for now
        for (DomNode node : worklist) {
            if (!node.isMatched()) {
                unmatched1.add(node);
            }
        }

        uv.init();
        root2.accept(uv);
        unmatched2 = uv.getUnmatched();

        return new MatchResult(matched, unmatched1, unmatched2);
    }

}
