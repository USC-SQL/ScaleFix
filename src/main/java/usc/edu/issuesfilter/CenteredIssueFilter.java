package usc.edu.issuesfilter;

import java.util.ArrayList;

import usc.edu.layoutissue.Issue;

public class CenteredIssueFilter implements LayoutIssuesFilter {

    @Override
    public ArrayList<Issue> filter(ArrayList<Issue> issues) {
        ArrayList<Issue> filteredLayoutIssues = new ArrayList<>();
        for (Issue issue : issues) {
            if (issue.getIssueType() != Issue.IssueType.CENTERED) {

                filteredLayoutIssues.add(issue);
            }
        }

        return filteredLayoutIssues;

    }

}
