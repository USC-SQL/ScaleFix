package usc.edu.SALEM;

import usc.edu.SALEM.VHTree.DependencyGraph;
import usc.edu.SALEM.VHTree.SegmentRelationGraph;
import usc.edu.SALEM.fitness.AccessibilityScannerResults;
import usc.edu.SALEM.segmentation.Segment;
import usc.edu.SALEM.segmentation.SegmentModel;
import usc.edu.SALEM.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class SALEM {

    public static Logger logger = Logger.getLogger(RunSALEM.class.getName());


    /*** Fixed variables regardless of apps or activity ***/
    public static boolean  CONSIDER_HEIGHT_WIDTH_RATIO;
    public static String RUNNING_MODE;// or "app" // are we running at app level or going through activities seperately
    public static String DYNAMIC_LAYOUT_APPROACH;
    public static int populationCalCount;
    public static int chromosomeCalCount;
    private transient static String androidFileIssuePath;
    public static String linuxUserPath; // newUser testing
    public static int[] CURRENT_SCREEN_COORDINATE;
    public static boolean generateNewMerged;
    public static boolean TestingAsethitic;
    public static boolean DEBUG_GET_ISSUES;
    public static String deviceName;
    public static LinkedHashMap<String, Long> runtime_map;
    public static final int MAX_NO_THREADS_FOR_APP_COMPILING = 30;
    public static boolean HandleTextField;

    private transient static String mergePath;  //This is the base path for the merged Path
    private transient static String originalApkName; // The original apk file name
    public static String SOLUTION_CSV_OUTPUT;

    private transient static String copiedApkFileName;  // the file name after copying it and adding the date ( current apk for the current run)
    private transient static String originalDecompiled;
    private transient static Set<String> problematicActivities;  // Activities that has Initial issues


    private transient static HashMap<String, String> activitiesToIssuesTypes;    // This tells if the current activity has height, width, or both TT issues based on the list of initial isseus

    private transient static Set<String> problematicSegments; // Segments that contains issues;
    private transient static String filepath;
    private transient static String currentActivityName;
    private transient static String currentActivityMergedFilePath;
    private transient static String currentApkPackage;
    private transient static String originalDynamicLayoutFilePath; // The original dynamic layout file


    public static double currentActivityOriginalNoOfTT;  // original number of touch targets in an activity ( before any repair)


    // Store the results of the detection tool
    private static HashMap<String, AccessibilityScannerResults> detectionToolResults; // Keep the list Of Issues for each chromosome with initial issue as "initial" for key and the remainings are using chromosomeID as key

    //    public static String basePath = "/home/testing/AppSet/accessibility/TT_scripts";
    public static String basePath;

    public static String modifiedApkPath;
    public static String decompiled_fixes;
    public static String decompiledFolder;
    public static String base_final_fix_output;
    public static String final_fix_output = "";
    public static String ACTIVITIES_TO_PYTHON_SCRIPTS_FILEPATH;
    public static String INITIAL_ISSUES_FOLD_PATH = null;
    public static String Detection_Tool_APK_PATH = null;
    public static boolean IsThreadExecuted;
    public static double totalOriginalArea;
    public static double totalOriginalHeight;
    public static double totalOriginalWidth;
    public static double totalRecommendedArea;

    public static HashMap<String, ArrayList<String>> ACTIVITIES_TO_CRAWLING_SCRIPTS;
    //    public static String issuesListFolder = basePath+File.separator+"apks_folder/issues_list/";
//    public static String dynmicBasePath;
//    public static String device_name = "Nexus P6";
//    public static String apk_name;

    //Ali url= path to the apk decompiled with ids
    private transient static String url;
    // Ali: where to write the output fixes
    private transient static String outputFolderPath;
    private transient static String issuesListFolder;
    // DO not filter for curr activity   || ONLY SET TRUE WHEN YOU LOOK FOR NEW APPS
//  public static String merged_layout_files;
    //map of <segment_issue_ID, DG>

    private static Map<String, DependencyGraph> segmentToDG;
    private static Map<String, SegmentRelationGraph> segmentToSG;  // for segment realtion Graph
    private static SegmentModel originalUISegmentModel;
    private static List<Segment> originalUISegments;

    private transient static double beforeAccessibilityScore;
    private transient static double afterAccessibilityScore;
    private transient static boolean isSizeAccessible;
    public transient static String segmentsLogoutPath;
    public static ArrayList<String> finalChromosome = new ArrayList<>();

//    public transient static HashMap<String, Table> initial_issues;
    // List of available Amazon cloud instances
//    private transient static List<String> awsInstances;

    public static void run_time_reset() {
        /*** Run Time calc Map ***/
        SALEM.runtime_map = new LinkedHashMap<>();
        chromosomeCalCount = 0;   // Reset runtime for population and chromosome
        populationCalCount = 0;
    }

    public static void initialize(boolean IsThreadExecuted, String deviceName, String screen_res, String path, String runningMode,
                                  String dynamicLayoutApproach, String activitiesToRun, boolean HandleTextField,
                                 boolean ConsiderHeightWidthRatio) throws IOException {
        SALEM.CONSIDER_HEIGHT_WIDTH_RATIO=ConsiderHeightWidthRatio;
        SALEM.HandleTextField=HandleTextField;
        SALEM.IsThreadExecuted = IsThreadExecuted;
        run_time_reset();  // Reset the runtime map and variables
        SALEM.RUNNING_MODE = runningMode; // or "app" // are we running at app level or going through activities seperately
        SALEM.DYNAMIC_LAYOUT_APPROACH = dynamicLayoutApproach;
        SALEM.linuxUserPath = path; // newUser testing
        SALEM.basePath = linuxUserPath;
        Constants.SET_GREP_ACTIVITY_NAME();
        Constants.SET_CONSTANTS_BASE_PATH();
        final_fix_output = "";
        SALEM.ACTIVITIES_TO_PYTHON_SCRIPTS_FILEPATH = basePath + File.separator +
                activitiesToRun;
        SALEM.androidFileIssuePath = "/data/data/com.aziz.accessibilityEval/files/ACCESS_ISSUES.csv";

        CURRENT_SCREEN_COORDINATE = null;
        generateNewMerged = false;
        TestingAsethitic = false;
        DEBUG_GET_ISSUES = false;
        SALEM.deviceName = deviceName;
        Util.setScreenEdge(screen_res);          // set screen res live using Util.runCommand(Constants.ADB + " shell  wm size",null, "screen_res");

        /*** Read and set crawling scripts ***/
        HashMap<String, ArrayList<String>> activitiesToCrawlingSctipt = RunSALEM.readingCrawlingScriptMapping(SALEM.ACTIVITIES_TO_PYTHON_SCRIPTS_FILEPATH);
        SALEM.setActivitiesToCrawlingScripts(activitiesToCrawlingSctipt);

        /*** Main ADB setup
         *
         */

        Util.runCommand(Constants.ADB + " root", null, null);
        if (Constants.Accessibility_SCORE_APPROACH.equalsIgnoreCase("FROM_ISSUES")) {
            // To clear images from device
            Util.runCommand(Constants.ADB + " shell rm /data/data/com.aziz.accessibilityEval/files/*.PNG", null, null);
        }
        //Get Screen resolution


        resetDeviceUIUsingADB();


    }

    public static void resetDeviceUIUsingADB() throws IOException {
        Util.runCommand(Constants.ADB + " shell input keyevent KEYCODE_HOME", null, null);
        Util.runCommand(Constants.ADB + " shell input keyevent KEYCODE_DPAD_CENTER", null, null);
        Util.runCommand(Constants.ADB + " shell input keyevent KEYCODE_BACK", null, null);
        Util.runCommand(Constants.ADB + " shell input keyevent KEYCODE_HOME", null, null);
    }

    public static void reset() {

        run_time_reset();  // Reset the runtime map and variables
    finalChromosome = new ArrayList<>();


        SALEM.copiedApkFileName = null;  // the file name after copying it and adding the date ( current apk for the current run)
        SALEM.originalDecompiled = null;
        SALEM.problematicActivities = null;  // Activities that has Initial issues


        SALEM.activitiesToIssuesTypes = null;    // This tells if the current activity has height, width, or both TT issues based on the list of initial isseus

        SALEM.problematicSegments = null; // Segments that contains issues;
        SALEM.filepath = null;
        SALEM.currentActivityName = null;
        SALEM.currentActivityMergedFilePath = null;
        SALEM.currentApkPackage = null;
        SALEM.originalDynamicLayoutFilePath = null;


        totalOriginalArea = Double.MIN_VALUE;
        totalOriginalHeight = Double.MIN_VALUE;
        totalOriginalWidth = Double.MIN_VALUE;
        totalRecommendedArea = Double.MIN_VALUE;
        currentActivityOriginalNoOfTT = Double.MIN_VALUE;

        // null

        beforeAccessibilityScore = Double.MIN_VALUE;
        afterAccessibilityScore = Double.MIN_VALUE;
        isSizeAccessible = false;
        segmentsLogoutPath = null;
        segmentToDG = null;
        segmentToSG = null;  // for segment realtion Graph
        originalUISegmentModel = null;
        originalUISegments = null;
        mergePath = null;  //This is the base path for the merged Path
        originalApkName = null;
        outputFolderPath = null;


        SALEM.INITIAL_ISSUES_FOLD_PATH = SALEM.basePath + File.separator + "TT_ISSUES/initial_issues_list/";
        SALEM.Detection_Tool_APK_PATH = SALEM.basePath + File.separator + "apks_folder/detection_apks/";
        SALEM.modifiedApkPath = SALEM.basePath + File.separator + "SUBJECTS/apks_with_ids/";
        SALEM.decompiled_fixes = SALEM.basePath + File.separator + "apks_folder/decompiled_fixed_apks/";
        SALEM.decompiledFolder = SALEM.basePath + File.separator + "SUBJECTS/decompiled_apks/";
        SALEM.base_final_fix_output = SALEM.basePath + File.separator + "apks_folder/final_chromosome_fixes";
        SALEM.detectionToolResults = new HashMap<>(); // Keep the list Of Issues for each chromosome with initial issue as "initial" for key and the remainings are using chromosomeID as key


    }

    public static String getOriginalApkName() {
        return originalApkName;
    }

    public static void setOriginalApkName(String originalApkName) {
        SALEM.originalApkName = originalApkName;
    }


    public static double getCurrentActivityOriginalNoOfTT() {
        return currentActivityOriginalNoOfTT;
    }

    public static void setCurrentActivityOriginalNoOfTT(double currentActivityOriginalNoOfTT) {
        SALEM.currentActivityOriginalNoOfTT = currentActivityOriginalNoOfTT;
    }

    public static String getDeviceName() {
        return deviceName;
    }

    public static String getAndroidFileIssuePath() {
        return androidFileIssuePath;
    }

    public static void setDeviceName(String deviceName) {
        SALEM.deviceName = deviceName;
    }

    public static String getMergePath() {
        return mergePath;
    }

    public static void setMergePath(String mergePath) {
        SALEM.mergePath = mergePath;
    }

    public static String getCurrentActivityMergedFilePath() {
        return currentActivityMergedFilePath;
    }

    public static String getCurrentApkPackage() {
        return currentApkPackage;
    }

    public static void setCurrentApkPackage(String currentApkPackage) {
        SALEM.currentApkPackage = currentApkPackage;
    }

    public static void setAndroidFileIssuePath(String androidFileIssuePath) {
        SALEM.androidFileIssuePath = androidFileIssuePath;
    }

    public static void setCurrentActivityMergedFilePath(String currentActivityMergedFilePath) {
        SALEM.currentActivityMergedFilePath = currentActivityMergedFilePath;
    }

    public static String getOriginalDynamicLayoutFilePath() {
        return originalDynamicLayoutFilePath;
    }

    public static String getCopiedApkFileName() {
        return copiedApkFileName;
    }

    public static void setCopiedApkFileName(String copiedApkFileName) {
        SALEM.copiedApkFileName = copiedApkFileName;
    }

    public static void setOriginalDynamicLayoutFilePath(String originalDynamicLayoutFilePath) {
        SALEM.originalDynamicLayoutFilePath = originalDynamicLayoutFilePath;
    }

    public static HashMap<String, AccessibilityScannerResults> getDetectionToolResults() {
        return detectionToolResults;
    }

    public static void setDetectionToolResults(HashMap<String, AccessibilityScannerResults> detectionToolResults) {
        SALEM.detectionToolResults = detectionToolResults;
    }

    public static HashMap<String, ArrayList<String>> getActivitiesToCrawlingScripts() {
        return ACTIVITIES_TO_CRAWLING_SCRIPTS;
    }

    public static void setActivitiesToCrawlingScripts(HashMap<String, ArrayList<String>> activitiesToCrawlingScripts) {
        ACTIVITIES_TO_CRAWLING_SCRIPTS = activitiesToCrawlingScripts;
    }

//    public static HashMap<String, Table> getInitial_issues() {
//        return initial_issues;
//    }

//    public static void setInitial_issues(HashMap<String, Table> initial_issues) {
//        mFix.initial_issues = initial_issues;
//    }

    public static String getCurrentActivityName() {
        return currentActivityName;
    }

    public static void setCurrentActivityName(String currentActivityName) {
        SALEM.currentActivityName = currentActivityName;
    }

    public static Set<String> getProblematicActivities() {
        return problematicActivities;
    }

    public static void setProblematicActivities(Set<String> problematicActivities) {
        SALEM.problematicActivities = problematicActivities;
    }

    public static Set<String> getProblematicSegments() {
        return problematicSegments;
    }

    public static void setProblematicSegments(Set<String> problematicSegments) {
        SALEM.problematicSegments = problematicSegments;
    }

    public static HashMap<String, String> getActivitiesToIssuesTypes() {
        return activitiesToIssuesTypes;
    }

    public static void setActivitiesToIssuesTypes(HashMap<String, String> activitiesToIssuesTypes) {
        SALEM.activitiesToIssuesTypes = activitiesToIssuesTypes;
    }

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        url = url;
    }

    public static String getFilepath() {
        return filepath;
    }

    public static void setFilepath(String filepath) {
        filepath = filepath;
    }

    public static double getBeforeAccessibilityScore() {
        return beforeAccessibilityScore;
    }

    public static void setBeforeAccessibilityScore(double beforeAccessibilityScore) {
        SALEM.beforeAccessibilityScore = beforeAccessibilityScore;
    }

    public static double getAfterAccessibilityScore() {
        return afterAccessibilityScore;
    }

    public static void setAfterAccessibilityScore(double afterAccessibilityScore) {
        SALEM.afterAccessibilityScore = afterAccessibilityScore;
    }

    public static boolean isMobileFriendly() {
        return isSizeAccessible;
    }

    public static void setMobileFriendly(boolean isMobileFriendly) {
        SALEM.isSizeAccessible = isMobileFriendly;
    }

    public static String getOriginalDecompiled() {
        return originalDecompiled;
    }

    public static void setOriginalDecompiled(String original) {
        originalDecompiled = original;
    }

    public static String getOutputFolderPath() {
        return outputFolderPath;
    }

    public static void setOutputFolderPath(String outputFolderPath) {
        SALEM.outputFolderPath = outputFolderPath;
    }

    public static Map<String, DependencyGraph> getSegmentToDG() {
        return segmentToDG;
    }

    public static Map<String, SegmentRelationGraph> getSegmentToSG() {  // for segment graph
        return segmentToSG;
    }

    public static void setSegmentToDG(Map<String, DependencyGraph> segmentToDG) {
        SALEM.segmentToDG = segmentToDG;
    }

    public static void setSegmentToSG(Map<String, SegmentRelationGraph> segmentToSG) {
        SALEM.segmentToSG = segmentToSG;
    }

    public static SegmentModel getOriginalUISegmentModel() {
        return originalUISegmentModel;
    }

    public static void setOriginalUISegmentModel(SegmentModel originalUISegmentModel) {
        SALEM.originalUISegmentModel = originalUISegmentModel;
    }

    public static List<Segment> getOriginalUISegments() {
        return originalUISegments;
    }

    public static void setOriginalUISegments(List<Segment> originalUISegments) {
        SALEM.originalUISegments = originalUISegments;
    }

//    public static List<String> getAwsInstances() {
//        return awsInstances;
//    }
//
//    public static void setAwsInstances(List<String> awsInstances) {
//        mFix.awsInstances = awsInstances;
//    }
}
