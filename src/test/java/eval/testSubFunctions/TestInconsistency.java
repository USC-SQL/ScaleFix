package eval.testSubFunctions;

import gatech.xpert.dom.DomNode;
//import issuesfilter.*;
import usc.edu.issuesfilter.*;
import usc.edu.layoutgraph.LayoutGraphBuilder;
import usc.edu.layoutissue.Issue;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
import usc.edu.SALEM.SALEM;
import usc.edu.SALEM.VHTree.XMLUtils;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TestInconsistency {
    private static LayoutGraphBuilder lgb;


    public static void main(String[] args) throws SAXException, IOException {
//
//        String subject="cz.test.calculator";
//        String activitiy="cz.test.calculator.MainActivity";
//        String chromosome="cz.test.calculator01-20-2021-10-49-19-PM";

//        String subject="androdns.android.leetdreams.ch.androdns";
//        String activitiy="androdns.android.leetdreams.ch.androdns.DNSFormActivity";
//        String chromosome="androdns.android.leetdreams.ch.androdns01-20-2021-08-47-47-PM";

//        String subject="com.android.keepass";
//        String activitiy="com.keepassdroid.fileselect.FileSelectActivity";
//        String chromosome="com.android.keepass02-10-2021-07-17-06-PM_chromosome_initialization_0_3.apk";

//        String subject="";
//        String activitiy="";
//        String chromosome="";

//        String subject="";
//        String activitiy="";
//        String chromosome="";

//        String subject="com.adzenze.FashionDesignFlatSketch";
//        String activitiy="com.adzenze.FashionDesignFlatSketch.ReadBookActivity";
//        String chromosome="com.adzenze.FashionDesignFlatSketch02-02-2021-10-10-09-PM";

        String subject = "com.bytecode.aiodownloader";
        String activitiy = "com.bytecode.aiodownloader.activity.MainActivity";
        String chromosome = "com.bytecode.aiodownloader01-20-2021-04-42-36-PM";

//        String subject="com.bytecode.aiodownloader";
        //        String activitiy="com.bytecode.aiodownloader.activity.MainActivity";

///        String activitiy="com.bisoft.live.weather.activities.SettingActivity";


//        String subject="com.daily.calling";
////        String activitiy="com.js.rssreader.ListActivity";
////
//        String subject="uk.co.bitethebullet.android.token";


//        String activitiy="uk.co.bitethebullet.android.token.TokenAdd";
        String originalFile = SALEM.basePath + File.separator + "apks_folder/merged_layouts/" + subject +
                "/" + activitiy + ".xml";
//        String chromosome="uk.co.bitethebullet.android.token_601-05-2021-01-42-57-PM";
        String repaired = SALEM.basePath + File.separator + "apks_folder/potentialChanges/" + subject +
                "/" + chromosome + "/CrawledData/refined/" + activitiy + ".xml";
//        repaired=originalFile;
        XMLUtils.getInstance(originalFile);
        Node<DomNode> originalRoot = XMLUtils.getRoot();
        Node<DomNode> repairedRoot = XMLUtils.readCrawledXML_T(repaired);  //Just reading it to get a ne

        lgb = new LayoutGraphBuilder(originalRoot, repairedRoot);
        ArrayList<Issue> potentialLayoutIssues = lgb.compareLayoutGraphs();
        int potentialscore = computeAmountOfInconsistancy(potentialLayoutIssues);
        LayoutIssuesFilterProcessor filters = new LayoutIssuesFilterProcessor();

        filters.addFilter(new ContainmentIssueFilter());
        filters.addFilter(new DirectionIssueFilter());
//        filters.addFilter(new OptionElementFilter());
        filters.addFilter(new OnePixelIssueFilter());
        filters.addFilter(new CenteredIssueFilter());
        filters.addFilter(new AlignmentIssueFilter());
//        LayoutIssuesFilterProcessor filter = new LayoutIssuesFilterProcessor();
        ArrayList<Issue> filteredLayoutIssues = filters.filterissues(potentialLayoutIssues);
        //filter issues

        int filteredscore = computeAmountOfInconsistancy(filteredLayoutIssues);

        System.out.println("PotentialSize: " + potentialLayoutIssues.size() + " || " +
                "FilteredDize: " + filteredLayoutIssues.size());
        System.out.println("PotentialScore: " + potentialscore + " || " +
                "FilteredScore: " + filteredscore);

    }

    //2728
    public static int computeAmountOfInconsistancy(ArrayList<Issue> filteredLayoutIssues) {

        int amount = 0;

        for (Issue issue : filteredLayoutIssues) {
            amount += issue.getIssueAmount();
        }

        return amount;
    }
} //  size = 39     //5574
