package usc.edu.issuesfilter;

import java.util.ArrayList;

import usc.edu.layoutissue.Issue;


//this filter is used to remove issues that are introduced because of rounding, 
//rounding could cause elements to have 1 pixel difference in their locations 
public class OnePixelIssueFilter implements LayoutIssuesFilter {

    @Override
    public ArrayList<Issue> filter(ArrayList<Issue> issues) {
        ArrayList<Issue> filteredLayoutIssuesUpdated = new ArrayList<>();
        for (Issue issue : issues) {
            int threshold = 20;
            if (issue.isIntersectionIssue()){
                threshold = 3;
            }
            if (issue.getIssueAmount() > threshold) { // final experiment was 20
                filteredLayoutIssuesUpdated.add(issue);
            }
        }

        return filteredLayoutIssuesUpdated;
    }

}
