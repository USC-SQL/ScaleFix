package usc.edu.issuesfilter;

import java.util.ArrayList;

import gatech.xpert.dom.DomNode;
import usc.edu.layoutissue.Issue;
//import usc.edu.SALEM.VHTree.Node;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
public class AlignmentIssueFilter implements LayoutIssuesFilter {


    @Override
    public ArrayList<Issue> filter(ArrayList<Issue> issues) {
        ArrayList<Issue> filteredIssues = new ArrayList<>();

        for (Issue issue : issues) {
            if (issue.isAlignmentIssue()) {
                Node<DomNode> domNode1 = issue.getBaselineEdge().getNode1().getDomNode();

                Node<DomNode> domNode2 = issue.getBaselineEdge().getNode2().getDomNode();


                //filter: only tag nodes are checked alignment
                if (isAlignmentNeeded(domNode1) && isAlignmentNeeded(domNode2)) {
                    filteredIssues.add(issue);
                } else {
                    System.out.println("Filtered Alignment");
                }

            } else { //non Alignment issue
                filteredIssues.add(issue);
            }
        }

        return filteredIssues;
    }

    private boolean isAlignmentNeeded(Node<DomNode> domNode1) {


        return !domNode1.getData().isLayout();

    }

}
