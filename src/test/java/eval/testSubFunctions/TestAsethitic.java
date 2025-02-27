//package eval.clustering;
//
//import gatech.xpert.dom.DomNode;
//import main.ReadIssues;
//import main.TIssue;
//import main.TestTTFix;
//import mfix.Constants;
//import mfix.GA.GAChromosome;
//import mfix.GA.GASearch;
//import mfix.GA.GAWrapper;
//import mfix.domTree.DependencyGraph;
//import mfix.domTree.Node;
//import mfix.domTree.SegmentRelationGraph;
//import mfix.fitness.FitnessFunction;
//import mfix.fitness.GoogleAPIResults;
//import mfix.mFix;
//import mfix.merge.Merger;
//import mfix.merge.Merger_TT;
//import mfix.merge.XMLUtils;
//import mfix.segmentation.InterSegmentEdge;
//import mfix.segmentation.Segment;
//import mfix.segmentation.SegmentModel;
//import mfix.util.Util;
//import org.xml.sax.SAXException;
//
//import java.io.*;
//import java.util.*;
//
//public class TestAsethitic {
//
//
//    public static String[] appActivities;  // manually list problematic activitites | just to test segmentation
//
//
//    public static void main(String[] args) throws SAXException, IOException, InterruptedException {
//
//        String device_name = "Nexus P6";
//
//        if (device_name.equalsIgnoreCase("Nexus P6")) {
//            Constants.PHONE_DENSITY = 3.5;
//        }
//        List<String> subjectsList = new ArrayList<>();
//        // subjectsList.add("com.adzenze.FashionDesignFlatSketch");
////        String activityName="com.adzenze.FashionDesignFlatSketch.ReadBookActivity.xml";
////
//        subjectsList.add("com.bytecode.aiodownloader");
////           subjectsList.add("com.daily.calling");
////        subjectsList.add("arity.calculator_27");
////        subjectsList.add("ch.logixisland.anuto_21");
////        subjectsList.add("com.android.keepass_181");
//        //  subjectsList.add("souch.smp_16");
////        subjectsList.add("arity.calculator_27");
////        subjectsList.add("ca.rmen.android.frenchcalendar_1182");
//        // subjectsList.add("br.com.frs.foodrestrictions_2");
//
//        appActivities = new String[]{"com.bytecode.aiodownloader.activity.MainActivity"};
//
////        appActivities= new String[]{"com.adzenze.FashionDesignFlatSketch.ReadBookActivity"};
//        String originalStaticFiles = mFix.basePath + File.separator + "apks_folder/decompiled_apks/";
//        String crawledFiles = mFix.basePath + File.separator + "apks_folder/dynamic_layouts/";
////        String refineDynamicPath = mFix.basePath+File.separator+"apks_folder/dynamic_layouts/";
//        String mergedLayoutPath = mFix.basePath + File.separator + "apks_folder/merged_layouts/";
////        String issuesListFolder = mFix.basePath+File.separator+"apks_folder/issues_list/";
//        String issuesListFolder = Constants.Detection_Tool_APK_PATH;
//        String copiedAPKPath = mFix.basePath + File.separator + "apks_folder/repaired_apks_search/";
//        String guianalzyerPath = mFix.basePath + File.separator + "apks_folder/guianalzyer/";
//
//
//        mFix.segmentsLogoutPath = mergedLayoutPath + File.separatorChar + "log_" + Util.getFormattedTimestamp() + ".txt";  // To output the segments to file to debug the segmentation algorithm
//
//        int NUMBER_OF_RUNS = 1;
//        mFix.segmentsLogoutPath = mergedLayoutPath + File.separatorChar + "log_" + Util.getFormattedTimestamp() + ".txt";  // To output the segments to file to debug the segmentation algorithm
//        for (int i = 0; i < NUMBER_OF_RUNS; i++) {
//
//            for (String subject : subjectsList) {
//                System.err.println("\n" + subject);
//                if (Constants.TESTING_SEGMENTS) {
////                    appActivities= new String[]{
////                            "com.android.keepass.fileselect.FileSelectActivity"}
////                    ;// manually listing activities with issues
//                }
////                String url = "http://sonal.usc.edu/mfix/" + subject + "/";
//
//                // This is the apk folder not the layout
////                String apkStaticFiles = originalStaticFiles + File.separator + subject;
//                String apkCrawledFiles = crawledFiles + File.separator + subject;
//                String apkMergedLayoutFiles = mergedLayoutPath + File.separator + subject;
//                String apkIssuesListFiles = issuesListFolder;//+ File.separator + subject+ Constants.TOUCH_TARGET_ISSUES_OUTPUT_SUFFIX;
//
//                XMLUtils.resetInstance();
//
//                System.out.println("Finding page clusters");
//
//                apkIssuesListFiles = apkIssuesListFiles + subject + Constants.TOUCH_TARGET_ISSUES_OUTPUT_SUFFIX;
//
//                String guianalzyerAPKPath = guianalzyerPath + File.separator + subject;
//                originalStaticFiles = originalStaticFiles + File.separator + subject;
//                String[] testXbiArgs = {subject, originalStaticFiles, apkCrawledFiles, apkMergedLayoutFiles, guianalzyerAPKPath, apkIssuesListFiles};
//                TestTTFix tm = new TestTTFix();
////                tm.runApproach(testXbiArgs);
//                TestClustering tc = new TestClustering();
////                onlyShowRelevantClusters = false;  // true: Only show clusters that are part of the issues | false: show all clusters
//                tc.testiFixClustring(testXbiArgs);
//                run(subject, apkMergedLayoutFiles);
//            }
//        }
//    }
//
//    public static void run(String apkName, String originalMerged
//    ) throws IOException, InterruptedException {
////        public void runIterator(String url, String filePath) {
//
//
//        long startInitTime = System.nanoTime();
////        init(url, filePath);
//
//
//        init();
//        //ToDo: mfix handle pages, I handle activities
//        // Build segment model for the original page
//
//        // Now after teh init we have merged the activities based on the initial dynamic layout and already have the list of issues
//        Set<String> x = mFix.getProblematicActivities();
//        for (String activityName : mFix.getProblematicActivities()
//        ) {
//            //For testing list activity only
//            if (activityName.equalsIgnoreCase("com.js.rssreader.DetailActivity")) {
//                continue;
//            }
//
//            /* build View Tree for the activity       */
//            //(1) Set the path variables for the activity
//            mFix.setCurrentActivityName(activityName);
//            String xx = mFix.getMergePath() + File.separator + activityName + ".xml";
//            mFix.setCurrentActivityMergedFilePath(mFix.getMergePath() + File.separator + activityName + ".xml");
////            mFix.setCurrentActivityMergedFilePath(mFix.basePath+File.separator+"apks_folder/crawled_layouts/testaseth/" +
////                    "com.bytecode.aiodownloader/refined" + File.separator + activityName + ".xml");
//
//
//            //(2) Build the layout of activity based on the merged layout of that activity
//            XMLUtils.getInstance(mFix.getCurrentActivityMergedFilePath());
////            XMLUtils.getInstance(mFix.basePath+File.separator+"apks_folder/crawled_layouts/testaseth/com.bytecode.aiodownloader/refined/"+activityName);
//            Node<DomNode> root = XMLUtils.getRoot();
//            // From now on these steps will be done for each chromosome
//            SegmentModel segModel = new SegmentModel();
//            segModel.buildSegmentModel();
//
//            if (Constants.TESTING_SEGMENTS) { // To output the segments for me to test
//                try {
//                    System.setOut(new PrintStream(new FileOutputStream(mFix.segmentsLogoutPath, true)));
//
//                } catch (Exception e) {
//                }
//                System.out.println("Segements for " + mFix.getCurrentActivityName());
//                System.out.println("\nSegment terminate threshold value = " + segModel.getSegmentationObject().getSegmentTerminateThreshold());
//                System.out.println("Segments (size = " + segModel.getSegmentationObject().getSegments().size() + "): ");
//
//
//                for (Segment seg : segModel.getSegmentationObject().getSegments()) {
//                    System.out.println(seg);
//                    System.out.println("\nIntra-segment edges for segment S" + seg.getId());
//                    System.out.println(seg.getEdges());
//                }
//                System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
//                //continue;
//
//
////                System.out.println("\nSegment Model: " + segModel.getSegmentationObject().getSegments().get());
////                System.out.println("\nSegment Model: " + segModel);
//
//
//            }
//            double testSigmoid = Util.sigmoid(1500);
//
//            mFix.setOriginalPageSegmentModel(segModel);
//
//
//            /* Stage 1: initial mobile friendly problems detection */
//            System.out.println("++++++++++++++++ Stage 1: initial mobile friendly problems detection ++++++++++++++++");
//
//            Set<Segment> segments = new HashSet<Segment>();
//            SegmentModel xy = mFix.getOriginalPageSegmentModel();
//            for (InterSegmentEdge e : mFix.getOriginalPageSegmentModel().getEdges()) {
//                segments.add(e.getSegment1());
//                segments.add(e.getSegment2());
//            }
//            if (segments.size() == 0) { // In case there us only one segment
//                segments.add(mFix.getOriginalPageSegmentModel().getSegmentationObject().getSegments().get(0));
//
//            }
//            String detectionOutPutPath = ""; //Todo: Where?
//
//            detectionOutPutPath = Constants.Detection_Tool_APK_PATH;
//            FitnessFunction fn = new FitnessFunction();
//            double baseChaScore = FitnessFunction.calculateSizeChanging();
//            String newLayoutFolder = captureUpdatedDynamicUI(mFix.getCurrentActivityName(), detectionOutPutPath, apkName);
//
//            Util.updateLayoutWithNewValues(newLayoutFolder);
//            double aestheticScore = fn.calculateAestheticScore();
//            double changeScore = FitnessFunction.calculateSizeChanging();
//            double sigmoid = Util.sigmoid(changeScore);
//            System.out.println("aestheticScore Score is " + ": " + aestheticScore);
//            System.out.println("changeScore Score is " + ": " + changeScore);
//        }
//    }
//
//    public static void init() {
//
//        // Ali: initialize variables, set the apk names and input, output folders
//
//
//
//        /*
//        Load Initial list of issues and initial activities
//         */
//        Set<String> problematicActivities = null;
//
//        problematicActivities = new HashSet<>();
//        for (String act : appActivities) {
//            problematicActivities.add(act);
//        }
//        mFix.setProblematicActivities(problematicActivities);
//
//
//        FitnessFunction.setFitnessCalls(0);
//        FitnessFunction.setFitnessTimeInSec(0);
//        XMLUtils.resetInstance();
//        mFix.setMobileFriendly(false);
//        mFix.setSegmentToDG(new HashMap<String, DependencyGraph>());
//        mFix.setSegmentToSG(new HashMap<String, SegmentRelationGraph>());
//        mFix.setOriginalPageSegmentModel(new SegmentModel());
//        mFix.setOriginalPageSegments(new ArrayList<Segment>());
//
//
//        mfix.util.Util.setElementPropValueCache(new HashMap<String, String>());
//        String logFilePath = new File(mFix.getOriginalDecompiled()).getParent();
//        if (Constants.RUN_IN_DEBUG_MODE) {
//            try {
//                System.setOut(new PrintStream(new FileOutputStream(logFilePath + File.separatorChar + "log_" + Util.getFormattedTimestamp() + ".txt")));
//            } catch (Exception e) {
//            }
//        }
//    }
//
//    public static String captureUpdatedDynamicUI(String currentActivityName, String apkPath, String apkName) throws IOException {
////        /*** Run the cralwing script to reach the correct state of the activity then capture the UI with UI automator***/
////        String scriptPath = Constants.CRAWLING_SCRIPTS_PATH + "/" + currentActivityName + Constants.CRAWLING_SCRIPTS_SUFFIX;
////        //(1) we already compiled the folder when we ran the detection so we need to directly install the app and then run the app
////        installAPK(apkPath, chromosomeAPKName);
////        startAnApp(mFix.getCurrentApkPackage());  // check if the command is correct and accurate
////        //(2) Once the app has started, we run the crawler script to get to the correct the activity state
////        // scripts file naming: currentActivityName+"/"+constant.CRAWLING_SCRIPTS_SUFFIX
////        String crawlingScriptFile = scriptPath;
////        // By now I should have gotten the issue recorded so I should pull the file
////
////        runCrawlingscript(crawlingScriptFile);
//
//        /*** the commendted code above was already moved to prepareAppToGetUpdateIssueandLayout method above  so this method expect that wea are already at the correct state
//         * and we are ready to dump the layout***/
//        // (3) Now it is time to use UIAutomator ot dump the UI
//        String dumpedFileLayout = Constants.Crawled_UI_Dynamic_Layout + "/testaseth/" + apkName;
//        String[] dumpResult = dumpActivityLayout(dumpedFileLayout);
//        String xmlLayout = dumpResult[0];
//        String pngLayout = dumpResult[1];
//        // (4) Preprocess the dynamic layout by extacting the content of the layout
//        Merger_TT merger = new Merger_TT();
//
//        merger.preprocessDynamicFolder(dumpedFileLayout, dumpedFileLayout + "/refined/");
//        return dumpedFileLayout + "/refined/";
//    }
//
//    public static String[] dumpActivityLayout(String dumpedFileLayout) throws IOException {
//        /*** Dump the ui using UI automotor and add temp suffix | then extract the content tag using Mian's method  ***/
//
//        // (1) if chromosome folder does not exist, create it
//        File directory = new File(dumpedFileLayout);
//        if (!directory.exists()) {
//            directory.mkdir();
//        }
//
//        String cmd = Constants.GREP_ACTIVITY_NAME;
//        String activityresult = Util.runCommand(cmd, null, "grepActivityName");
//        if (activityresult == null) {
//            System.exit(1); //"Error
//        }
//        String activityName = null;
//        if (activityresult.contains("/")) {
//            String[] arr = activityresult.split("/");
//            String temp = arr[1];
////                    arr= temp.split("/");
//            if (temp.contains("}")) {
//                activityName = temp.replace("}", "");
//            }
//        }
//
//        //(2) dump the layout using ui automator to the phone
//        cmd = Constants.UIAUTOMATOR_DUMP;
//        Util.runCommand(cmd, null, null);
//
//        //(3) pull the xml layout to the pc
//        String xmlFileName = mFix.getCurrentActivityName() + ".xml";
////        cmd = Constants.PULL + " " + "/sdcard/window_dump.xml " + dumpedFileLayout + "/" + xmlFileName;
//        cmd = Constants.PULL;
//        cmd = String.format(cmd, "/sdcard/window_dump.xml", dumpedFileLayout + "/" + xmlFileName);
//        Util.runCommand(cmd, null, null);
//        //(4) capture the screenshot
//        cmd = Constants.SCREEN_CAP + " " + "/sdcard/screen.png";
//        Util.runCommand(cmd, null, null);
//        //(5) pull the screenshot to the pc
//        String pngFileName = dumpedFileLayout + "/" + mFix.getCurrentActivityName() + ".png";
////        cmd = Constants.PULL + " " + "/sdcard/screen.png " + dumpedFileLayout + "/" + mFix.getCurrentActivityName() + ".png";
//        cmd = Constants.PULL;
////                + " " + "/sdcard/screen.png " + dumpedFileLayout + "/" + mFix.getCurrentActivityName() + ".png";
//        cmd = String.format(cmd, "/sdcard/screen.png", dumpedFileLayout + "/" + mFix.getCurrentActivityName() + ".png");
//
//        Util.runCommand(cmd, null, null);
//
//        String[] result = {xmlFileName, pngFileName};
//        return result;
//
//
//    }
//
//}
