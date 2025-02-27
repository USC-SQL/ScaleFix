package eval.testSubFunctions;

import DBSCAN.clustring.ClustersSorter;
import DBSCAN.clustring.ElementsClusterer;
//import edu.gatech.xpert.dom.DomNode;
//import cluster.ClustersSorter;
//import cluster.ElementsClusterer;
//import edu.usc.config.Config;
//import edu.usc.gwali.Gwali;
//import edu.usc.util.CSSParser;
//import edu.usc.util.Utils;
//import eval.Subject;
import gatech.xpert.dom.DomNode;
//import ifix.input.ReadInput;
import usc.edu.SALEM.util.ReadIssues;
import usc.edu.SALEM.fitness.TIssue;
import usc.edu.SALEM.RunSALEM;
import usc.edu.SALEM.Constants;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
import usc.edu.SALEM.SALEM;
import usc.edu.SALEM.VHTree.Merger;
import usc.edu.SALEM.VHTree.XMLUtils;
import usc.edu.SALEM.util.Util;
//import org.openqa.selenium.By;
//import org.openqa.selenium.JavascriptExecutor;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.firefox.FirefoxDriver;
import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;
import DBSCAN.clustring.UtilIfix;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class TestClustering {


    private static boolean onlyShowRelevantClusters;  // Print relevant clusters that are part of the issues

    public static void main(String[] args) throws SAXException, IOException {
        String device_label = "Nexus P6";
        if (device_label.equalsIgnoreCase("Nexus P6")) {
            Constants.PHONE_DENSITY = 3.5;
        }
        String user = "ali";

        String screen_res = "Physical size: 1440x2560";  //adb shell  wm size
        boolean IsThreadExecuted = false;
        String device_name = "emulator-5554";
        String RUNNING_MODE = "activity"; // or "app" // are we running at app level or going through activities seperately
        String DYNAMIC_LAYOUT_APPROACH = "sasha";
        String basePath = "/home/ali/TTFIX_DATA/ASE2021";
        if (user.contains("paul")) {
            basePath = "/home/paul-sql/touchTarget/";
        }
        String activitiesToRun = "/activities_to_scritpt_mapping.csv";
        boolean increaseTextSize=false;
        boolean considerHeightWidthRatio=true; // when we change height or width we check the other
        SALEM.initialize(IsThreadExecuted, device_name, screen_res, basePath, RUNNING_MODE,
                DYNAMIC_LAYOUT_APPROACH, activitiesToRun,increaseTextSize,considerHeightWidthRatio);




        List<String> subjectsList = new ArrayList<>();
        //   subjectsList.add("com.adzenze.FashionDesignFlatSketch");
//        String activityName="com.adzenze.FashionDesignFlatSketch.ReadBookActivity.xml";
//
        subjectsList.add("com.americanexpress.android.acctsvcs.us");



        String originalStaticFiles = SALEM.basePath + File.separator + "SUBJECTS/decompiled_apks/";
        String crawledFiles = SALEM.basePath + File.separator + "VHs/dynamic_layouts/";
//        String refineDynamicPath = mFix.basePath+File.separator+"apks_folder/dynamic_layouts/";
        String mergedLayoutPath = SALEM.basePath + File.separator + "VHs/merged_layouts/";
//        String issuesListFolder = mFix.basePath+File.separator+"apks_folder/issues_list/";
        String issuesListFolder = SALEM.Detection_Tool_APK_PATH;
        String copiedAPKPath = SALEM.basePath + File.separator + "apks_folder/repaired_apks_search/";
//        String guianalzyerPath = mFix.basePath + File.separator + "apks_folder/guianalzyer/";

        SALEM.segmentsLogoutPath = mergedLayoutPath + File.separatorChar + "log_" + Util.getFormattedTimestamp() + ".txt";  // To output the segments to file to debug the segmentation algorithm

        int NUMBER_OF_RUNS = 1;
        SALEM.segmentsLogoutPath = mergedLayoutPath + File.separatorChar + "log_" + Util.getFormattedTimestamp() + ".txt";  // To output the segments to file to debug the segmentation algorithm
        for (int i = 0; i < NUMBER_OF_RUNS; i++) {

            for (String subject : subjectsList) {
                System.err.println("\n" + subject);
                if (Constants.TESTING_SEGMENTS) {
//                    appActivities= new String[]{
//                            "com.android.keepass.fileselect.FileSelectActivity"}
//                    ;// manually listing activities with issues
                }
//                String url = "http://sonal.usc.edu/mfix/" + subject + "/";

                // This is the apk folder not the layout
//                String apkStaticFiles = originalStaticFiles + File.separator + subject;
                String apkCrawledFiles = crawledFiles + File.separator + subject;
                String apkMergedLayoutFiles = mergedLayoutPath + File.separator + subject;
                String apkIssuesListFiles = issuesListFolder;//+ File.separator + subject+ Constants.TOUCH_TARGET_ISSUES_OUTPUT_SUFFIX;

                XMLUtils.resetInstance();

                System.out.println("Finding page clusters");

                apkIssuesListFiles = apkIssuesListFiles + subject + Constants.TOUCH_TARGET_ISSUES_OUTPUT_SUFFIX;

                originalStaticFiles = originalStaticFiles + File.separator + subject;
                String originalDecompilePath="";
                String[] testXbiArgs = {subject, originalDecompilePath, apkCrawledFiles,
                        apkMergedLayoutFiles, apkIssuesListFiles};
                RunSALEM tm = new RunSALEM();
//                tm.runApproach(testXbiArgs);
                TestClustering tc = new TestClustering();
                onlyShowRelevantClusters = true;  // true: Only show clusters that are part of the issues | false: show all clusters
                tc.testiFixClustring(testXbiArgs);
            }
        }
    }

    //    public static void main(String[] args) throws IOException
//    {
//        Config.applyConfig();
//
//        String basepath = "/Users/sonal/USC/search-based-repair/ifix/TestCases/ScrapBook/data";//"/home/ifix/ifix/TestCases/ScrapBook/data";
//        String resultsPath = "/Users/sonal/USC/search-based-repair/ifix/eval-results/clustering_dbscan_" + System.nanoTime();//"/home/ifix/ifix/eval-results/clustering_dbscan_" + System.nanoTime();
//
//        new File(resultsPath).mkdir();
//
//        List<Subject> subjects = new ArrayList<Subject>();
////		subjects.add(new Subject("akamai", basepath, "", ""));
////		subjects.add(new Subject("bestrestaurants", basepath, "", ""));
////		subjects.add(new Subject("calottery", basepath, "", ""));
////		subjects.add(new Subject("designsponge", basepath, "", ""));
////		subjects.add(new Subject("dmv", basepath, "", ""));
////		subjects.add(new Subject("els", basepath, "", ""));
////		subjects.add(new Subject("facebook", basepath, "www.facebook.com/index.html", "bg-bg.facebook.com/index.html"));
////		subjects.add(new Subject("flynas", basepath, "", ""));
////		subjects.add(new Subject("googleearth", basepath, "", ""));
////		subjects.add(new Subject("googlelogin", basepath, "accounts.google.com/index.html", "accounts.google.com/index.html"));
////		subjects.add(new Subject("hightail", basepath, "", ""));
////		subjects.add(new Subject("hotwire", basepath, "www.hotwire.com/index.html", "www.hotwire.com/index.html"));
////		subjects.add(new Subject("ixigo", basepath, "", ""));
////		subjects.add(new Subject("linkedin", basepath, "", ""));
////		subjects.add(new Subject("museum", basepath, "", ""));
////		subjects.add(new Subject("myplay", basepath, "", ""));
////		subjects.add(new Subject("qualitrol", basepath, "", ""));
//        subjects.add(new Subject("rentalcars", basepath, "", ""));
////		subjects.add(new Subject("skype", basepath, "", ""));
////		subjects.add(new Subject("skyscanner", basepath, "www.skyscanner.com/index.html", "www.skyscanner.com/index.html"));
////		subjects.add(new Subject("surgeon", basepath, "", ""));
////		subjects.add(new Subject("twitterhelp", basepath, "support.twitter.com/groups/50-welcome-to-twitter.html", "support.twitter.com/groups/50-welcome-to-twitter.html"));
////		subjects.add(new Subject("westin", basepath, "", ""));
//
//        for(Subject subject : subjects)
//        {
//            new File(resultsPath + File.separatorChar + subject.getSubject()).mkdir();
//
//            FirefoxDriver refDriver = Utils.getNewFirefoxDriver();
//            refDriver.get("file:///" + subject.getRefFilepathRelFromBasepath());
//
//            FirefoxDriver testDriver = Utils.getNewFirefoxDriver();
//            testDriver.get("file:///" + subject.getTestFilepathRelFromBasepath());
//
//
//            String imageFilePath = resultsPath + File.separatorChar + subject.getSubject() + File.separatorChar + "index-clustering.png";
//            Util.takeScreenShot(testDriver, imageFilePath);
//
//            ReadInput.setRefDriver(refDriver);
//            ReadInput.setTestDriver(testDriver);
//            ReadInput.setRefFilepath(subject.getRefFilepathRelFromBasepath());
//            ReadInput.setTestFilepath(subject.getTestFilepathRelFromBasepath());
//
//            if(Constants.RUN_IN_DEBUG_MODE)
//            {
//                try
//                {
//                    String logPath = resultsPath + File.separatorChar + subject.getSubject() + File.separatorChar + "index-clustering-log.txt";
//                    System.setOut(new PrintStream(new FileOutputStream(logPath)));
//                }
//                catch (Exception e)
//                {
//                    System.err.println("Cannot run in debug mode. All log statements will be displayed in the console.");
//                }
//            }
//
//            Gwali gwali = new Gwali(refDriver, testDriver);
//            gwali.runGwali();
//            ArrayList<String> potentiallyFaultyElements = gwali.getPotentiallyFaultyElements();
//            System.out.println("\nPotentially faulty elements (size = " + potentiallyFaultyElements.size() + ")");
//            for(String xpath : potentiallyFaultyElements)
//            {
//                System.out.println(xpath);
//            }
//
//            TestClustering tc = new TestClustering();
//
//            Map<String, List<Rectangle>> clusters = tc.getClustersDBSCAN(potentiallyFaultyElements, gwali);
//            //List<List<Rectangle>> clusters = tc.getClustersRtree(potentiallyFaultyElements);
//            //List<List<Rectangle>> clusters = tc.getClustersVIPS(subject);
//            //List<List<Rectangle>> clusters = tc.getClustersBlockOMatic(imageFilePath);
//
//            Util.drawClusters(imageFilePath, clusters);
//
//            refDriver.quit();
//            testDriver.quit();
//        }
//    }
    public Map<String, List<Rectangle>> getClustersDBSCAN(List<String> potentiallyFaultyElements, ElementsClusterer clusterer) {


        ArrayList<ArrayList<DomNode>> pageClusters = clusterer.getClustringResultsDomNodes();
        ArrayList<ArrayList<DomNode>> relevantClusterDomNodes;
        System.out.println("Getting relevant clusters");
        if (potentiallyFaultyElements.size() > 0 && TestClustering.onlyShowRelevantClusters) {
            relevantClusterDomNodes = ClustersSorter.getRelevantDomNodeClusters(pageClusters, new ArrayList<>(potentiallyFaultyElements));

        } else {
            relevantClusterDomNodes = pageClusters;  // Ali: I just want to test the pagenull
        }
        System.out.println("\nRelevant clusters: (size = " + relevantClusterDomNodes.size() + ")");
        int count = 1;
        Map<String, List<DomNode>> clusters = new HashMap<>();
        if (Constants.TESTING_SEGMENTS) { // To output the segments for me to test
            try {
                System.setOut(new PrintStream(new FileOutputStream(SALEM.segmentsLogoutPath, true)));

            } catch (Exception e) {

            }


        }
        for (ArrayList<DomNode> c : relevantClusterDomNodes) {
            System.out.println("\nRelevant cluster " + (count++) + ". (size = " + c.size() + ") = ");
            for (DomNode node : c) {
                System.out.println(node.getxPath());
            }
            clusters.put("C" + count, c);
        }
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

        return UtilIfix.getClusterRectangles(clusters);
    }

    public void testiFixClustring(String[] args) throws IOException {
        Set<String> problematicActivities = null;
        // run search
        SALEM.setOriginalApkName(args[0]);
        SALEM.setMergePath(args[3]);
        SALEM.setOriginalDecompiled(args[1]);
        SALEM.setOriginalDynamicLayoutFilePath(args[2]);
        long startTime = System.nanoTime();
        Merger merger = new Merger();
        String x = SALEM.getMergePath();


        String apkCrawledFiles = args[2];

        String issuesFilePath = args[4];
        HashMap<String, ArrayList<TIssue>> listOfIssues = null;
        File issueFile = new File(issuesFilePath);
        if (issueFile.exists()) {
            //issueFile exists
            ReadIssues read = new ReadIssues(issuesFilePath);
//            listOfIssues = read.parseIssue("initial");

        }

      //  merger.prepare_app_layout(mFix.getOriginalApkName(), null, mFix.getOriginalDecompiled(), apkCrawledFiles, guianalzyerPath);

        File inputFolder = new File(SALEM.getMergePath());

        File outputFolder = new File(SALEM.getMergePath() + File.separator + "ifixClustring");
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        // Iterate over each xml
        for (File f : inputFolder.listFiles()) {
            String fileName = f.getName();
            System.out.println("FILE:::::::::::::::::::: "+ fileName);
            if (fileName.endsWith(".xml")) {
                XMLUtils.resetInstance();
                XMLUtils.getInstance(f.getAbsolutePath());
//                if (!fileName.contains("com.creditsesame.subscribe.CreateAccount")) {
//                    continue;
//                }

                ElementsClusterer clusterer = new ElementsClusterer(XMLUtils.getRoot());
                clusterer.perfomrClustering();
                String currentActivity = fileName.replace(".xml", "").trim();  // the activity name should be the same as the activity name without .xml;
                SALEM.setCurrentActivityName(currentActivity);
                List<String> potentiallyFaultyElements = new ArrayList<>();
                ArrayList<TIssue> activityIssue = null;
                if (listOfIssues != null) {
                    if (listOfIssues.size() > 0) {
                        activityIssue = listOfIssues.get(currentActivity);
                    }
                }
                if (activityIssue != null)
                    for (TIssue issue : activityIssue
                    ) {
                        String id = issue.getWidgetID();
                        String className = issue.getClassName();
                        if (id != "") {
                            Node<DomNode> node = XMLUtils.searchByID_T(id, className);
                            String xpath = node.getData().getxPath();
                            if (xpath != null && xpath != "") {
                                potentiallyFaultyElements.add(xpath);
                            }
                        }
                    }
                Map<String, List<gatech.xpert.dom.Rectangle>> clusters = getClustersDBSCAN(potentiallyFaultyElements, clusterer);
                String sourcepngFileName = fileName.replace(".xml", ".png");
                String sourcepngFilePath = f.getAbsolutePath().replace(".xml", ".png");

                String destPngFileName = sourcepngFileName.replace(".xml", "cluster_" + Util.getFormattedTimestamp() + ".png");
                String destPngFilePath = outputFolder.getAbsolutePath() + File.separator + destPngFileName;
                //fileName.replace(".xml", "cluster_" + Util.getFormattedTimestamp()+".png");
                File file1 = new File(sourcepngFilePath);
                File file2 = new File(destPngFilePath);
                FileUtils.copyFile(file1, file2);
                UtilIfix.drawClusters(destPngFilePath, clusters);
            }
        }


//        List<String> potentiallyFaultyElements = null;
//        ElementsClusterer clusterer = new ElementsClusterer(XMLUtils.getRoot());
//        clusterer.perfomrClustering();
//
//        Map<String, List<Rectangle>> clusters = getClustersDBSCAN(potentiallyFaultyElements,clusterer);
//        String pngImagePath=mFix.getMergePath()+File.separator+mFix.getCurrentActivityName()+".png";
//        String pngNewImagePath=mFix.getMergePath()+File.separator+mFix.getCurrentActivityName()+ "cluster_" + Util.getFormattedTimestamp() +".png";
//        File file1 = new File(pngImagePath);
//        File file2 = new File(pngNewImagePath);
//        org.apache.commons.io.FileUtils.copyFile(file1, file2);
////        clusterer.drawSegments(pngNewImagePath);
//        UtilIfix.drawClusters(pngNewImagePath,clusters);

        long endTime = System.nanoTime();


    }

}
