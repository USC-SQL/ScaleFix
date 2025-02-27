package usc.edu.issuesfilter;

import java.util.ArrayList;

import usc.edu.layoutgraph.LayoutGraphComparator;
import usc.edu.layoutissue.Issue;
import usc.edu.SALEM.Constants;

public class DirectionIssueFilter implements LayoutIssuesFilter {


    @Override
    public ArrayList<Issue> filter(ArrayList<Issue> issues) {
        ArrayList<Issue> filteredIssues = new ArrayList<>();

        for (Issue issue : issues) {
            if (issue.isDirectionIssue()) {
                double baselineAngle = issue.getBaselineEdge().getAngleDegree();
                double PUTAngle = issue.getPageUnderTestEdge().getAngleDegree();
                double angelDiff = LayoutGraphComparator.AngleDiff(baselineAngle, PUTAngle);
                if (angelDiff > Constants.ANGEL_THRISHOLD) {
                    filteredIssues.add(issue);

                }
//                else {
//                    continue;
//                }
            } else { // not direction issue
                filteredIssues.add(issue);
            }
        }

        return filteredIssues;

    }


}
