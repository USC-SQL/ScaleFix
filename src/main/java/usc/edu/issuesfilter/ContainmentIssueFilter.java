package usc.edu.issuesfilter;

import java.util.ArrayList;

import gatech.xpert.dom.DomNode;
import usc.edu.layoutissue.Issue;
//import usc.edu.SALEM.VHTree.Node;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
public class ContainmentIssueFilter implements LayoutIssuesFilter {


    @Override
    public ArrayList<Issue> filter(ArrayList<Issue> issues) {
        ArrayList<Issue> filteredIssues = new ArrayList<>();

        for (Issue issue : issues) {
            if (issue.isContainmentIssue()) {
//                Node<DomNode> domNode1 = issue.getBaselineEdge().getNode1().getDomNode();
//                Node<DomNode> domNode2 = issue.getBaselineEdge().getNode2().getDomNode();
//                String node1ID=domNode1.getData().getId();
//                String node2ID=domNode2.getData().getId();
////                String node1Xpath= domNode1.getData().getxPath();
////                String node2Xpath= domNode2.getData().getxPath();
//                if(node1ID.trim().equalsIgnoreCase(node2ID.trim())){
//                    continue;
//                }
                //filter: only tag nodes are checked alignment
//				if(    domNode1.getData().isTag() && (domNode2.isText() || domNode2.isInputText())
//					|| domNode2.isTag() && (domNode1.isText() || domNode1.isInputText())
//						)
//                if(issue.getIssueAmount()<100){
//                    continue;
//                }
                filteredIssues.add(issue);
            } else { //non containment issue
                filteredIssues.add(issue);
            }
        }

        return filteredIssues;

    }

}
