package usc.edu.issuesfilter;

import java.util.ArrayList;

import usc.edu.layoutissue.Issue;

public interface LayoutIssuesFilter {
    ArrayList<Issue> filter(ArrayList<Issue> issues);

}
