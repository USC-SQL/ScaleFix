package usc.edu.issuesfilter;

import usc.edu.layoutgraph.edge.NeighborEdge;
import usc.edu.layoutissue.Issue;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class DuplicatesIssueFilter implements LayoutIssuesFilter {


    @Override
    public ArrayList<Issue> filter(ArrayList<Issue> issues) {
        ArrayList<Issue> uniqueIssues = new ArrayList<>();
        for (Issue issue : issues) {
            if (!uniqueIssues.contains(issue)) {
                uniqueIssues.add(issue);
            }


        }
        return uniqueIssues;
    }

    public static boolean isSameNodes(NeighborEdge baselineEdge, NeighborEdge putEdge) {

        Boolean NodesAreEqualInOrder = baselineEdge.getNode1().getDomNode().getData().getxPath().equals(putEdge.getNode1().getDomNode().getData().getxPath())
                &&
                baselineEdge.getNode2().getDomNode().getData().getxPath().equals(putEdge.getNode2().getDomNode().getData().getxPath());
        Boolean NodesAreEqualInOpposite = baselineEdge.getNode1().getDomNode().getData().getxPath().equals(putEdge.getNode2().getDomNode().getData().getxPath())
                &&
                baselineEdge.getNode2().getDomNode().getData().getxPath().equals(putEdge.getNode1().getDomNode().getData().getxPath());


        if (!NodesAreEqualInOrder && !NodesAreEqualInOpposite) {
            return false;
        }
        return true;

    }
}


//    public static boolean isEdgeAlreadyExist(NeighborEdge edge, LinkedHashMap<String, NeighborEdge> uniqueIssues){
//        for (String key : intersectionEdges.keySet()) {
//            NeighborEdge currEdge = intersectionEdges.get(key);
//            Boolean NodesAreEqualInOrder = edge.getNode1().toString().equals(currEdge.getNode1().toString()) && edge.getNode2().toString().equals(currEdge.getNode2().toString());
//            Boolean NodesAreEqualInOpposite = edge.getNode1().toString().equals(currEdge.getNode2().toString()) && edge.getNode2().toString().equals(currEdge.getNode1().toString());
//
//            if (NodesAreEqualInOrder || NodesAreEqualInOpposite) {
//                return true;
//            }
//
//        }
//
//        return false;
//
//
//    }




//    public static boolean isDuplicate(Issue issue1, Issue issue2) {
//        if (issue1.getIssueType().equals(issue2.getIssueType())) {
//            if (issue1.getIssueType().equals("DirectionIssue")) {
//                if (issue1.getDirection().equals(issue2.getDirection()))
//                    return true;
//            } else if (issue1.getIssueType().equals("ContainmentIssue")) {
//                if (issue1.getContainer().equals(issue2.getContainer()))
//                    return true;
//            } else if (issue1.getIssueType().equals("OverlapIssue")) {
//                if (issue1.getOverlappedElement().equals(issue2.getOverlappedElement()))
//                    return true;
//            }
//        }
//        return false;
//    }

//
//}
