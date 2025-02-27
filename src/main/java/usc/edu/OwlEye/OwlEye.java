package usc.edu.OwlEye;


import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import usc.edu.OwlEye.AUCII.Collision;
import usc.edu.OwlEye.AUCII.Cutoff;
import usc.edu.OwlEye.AUCII.Missing;
import usc.edu.OwlEye.UIModels.*;
import usc.edu.OwlEye.VH.UI;
import usc.edu.layoutgraph.LayoutGraph;
import usc.edu.layoutissue.Issue;


import java.util.*;
import java.util.logging.Logger;

public class OwlEye {
    public static String REFINED_DYNAMIC_DD_LAYOUT_FILE_PATH = "";
    public static Object CAPTURE_GENERATED_UI_MODE ="automator2" ; // (1) legacy: using the adb to pull the UI as we did in the past, (2) automator2: using uiAutomator to capture the UI as part of the python script
    public static int appCompileStyle; //1 use default apktool command, 2 use --use-aapt2
    public static boolean appNeedUninstall;  // based on subjects.csv; 1 means need uninstall, 2 means not need

    public static String appNeedScrolling;
    public static String activityToRunWithUIAutomator; // The activity to run with UIAutomator when we run the repaired UI (it maybe different from the original activity name)
    public static SRG originalDefaultUISRG;

    public static VSRG originalDefaultUIVSRG;
    public static TRG originalDefaultUITRG;
    public static WRG originalDefaultUIWRG;
    public static SPLRG originalDefaultUISPLRG;

    public static final int MAX_NO_THREADS_FOR_APP_COMPILING = 30;
    public static Logger logger = Logger.getLogger(RunOwl.class.getName());
    public static LinkedHashMap<String, Long> runtime_map;
    //These are the original UIs
    public static UI originalDefaultUI;
    public static UI originalLargestUI;


    // To optimize the layoutGraph class to avoid re-computing the same layout graph
   // public static LayoutGraph originalDefaultUILayoutGraph;
    public static Map<String, Node<DomNode>> originalDefaultUIElements;
    public static ArrayList<Issue> currentIntersectionLayoutIssues;



    // end of layout graph related attributes
    public static String pythonCrawlingScript;
    public static boolean addSleepIntervals; // if true, we will add sleep intervals between each big steps

    //Issues from original UI
    public static HashMap<String, Cutoff> originalCutoffIssues;
    public static HashMap<String, Collision> originalCollisionIssues;
    public static HashMap<String, Missing> originalMissingIssues;

    /*** Fixed variables regardless of apps or activity ***/
    public static boolean CONSIDER_HEIGHT_WIDTH_RATIO;
    public static String RUNNING_MODE;// or "app" // are we running at app level or going through activities seperately
    public static String DYNAMIC_LAYOUT_APPROACH;
    public static int populationCalCount;
    public static int chromosomeCalCount;
    public static String linuxUserPath; // newUser testing
    public static int[] CURRENT_SCREEN_COORDINATE;
    public static boolean generateNewMerged;
    public static boolean TestingAsethitic;
    public static boolean DEBUG_GET_ISSUES;
    public static String deviceName;
    public static boolean HandleTextField;
    public static String SOLUTION_CSV_OUTPUT;
    public static boolean debugCompile;
    private transient static String androidFileIssuePath;
    private transient static String mergePath;  //This is the base path for the merged Path (complete VH)
    private transient static String originalApkName; // The original apk file name
    private transient static String originalAppID; // ID we give to the subject and activity
    private transient static String originalActivityName; // The original apk file path
    private transient static String copiedApkFileName;  // the file name after copying it and adding the date ( current apk for the current run)
    private transient static String originalDecompiled;
    private transient static String compileOutputPath; // where we will compile the apk
    private static String pythonInterpreter;
    private static String androidSignature;
    private static String baseSubjectsPath;

    public static String getCrawledDynamicRepairVHPath() {
        return crawledDynamicRepairVHPath;
    }

    public static void setCrawledDynamicRepairVHPath(String crawledDynamicRepairVHPath) {
        OwlEye.crawledDynamicRepairVHPath = crawledDynamicRepairVHPath;
    }

    public static void setFinalRepairOutputFolder(String finalRepairOutputFolder) {
        OwlEye.finalRepairOutputFolder = finalRepairOutputFolder;
    }
    public static String getFinalRepairOutputFolder() {
        return finalRepairOutputFolder;
    }
    private transient static String crawledDynamicRepairVHPath; // the path to where we dump the VH for the executed repairs
    private transient static String finalRepairOutputFolder; // the path to where we dump the VH for the executed repairs
    private transient static Set<String> problematicActivities;  // Activities that has Initial issues
    private transient static HashMap<String, String> activitiesToIssuesTypes;    // This tells if the current activity has height, width, or both TT issues based on the list of initial isseus
    private transient static Set<String> problematicSegments; // Segments that contains issues;
    private transient static String filepath;
    private transient static String currentActivityName;
    private transient static String currentActivityMergedFilePath;
    private transient static String currentApkPackage;
    private transient static String originalDynamicLayoutFilePath; // The original dynamic layout file
    public static SRG getOriginalDefaultUISRG() {
        return originalDefaultUISRG;
    }

    public static void setOriginalDefaultUISRG(SRG orgSRG) {
        originalDefaultUISRG = orgSRG;
    }


    public static SPLRG getOriginalDefaultUISPLRG() {
        return originalDefaultUISPLRG;
    }
    public static void setOriginalDefaultUISPLRG(SPLRG orgSPLRG) {
        originalDefaultUISPLRG = orgSPLRG;
    }
    public static VSRG getOriginalDefaultUIVSRG() {
        return originalDefaultUIVSRG;
    }

    public static void setOriginalDefaultUIVSRG(VSRG orgVSRG) {
        originalDefaultUIVSRG = orgVSRG;
    }

    public static TRG getOriginalDefaultUITRG() {
        return originalDefaultUITRG;
    }

    public static void setOriginalDefaultUITRG(TRG orgTRG) {
        originalDefaultUITRG = orgTRG;
    }
    public static String getPythonCrawlingScript() {
        return pythonCrawlingScript;
    }

    public static void setPythonCrawlingScript(String pythonCrawlingScript) {
        OwlEye.pythonCrawlingScript = pythonCrawlingScript;
    }

    public static String getDeviceName() {
        return deviceName;
    }

    public static void setDeviceName(String deviceName) {
        OwlEye.deviceName = deviceName;
    }

    public static String getOriginalAppID() {
        return originalAppID;
    }

    public static void setOriginalAppID(String originalAppID) {
        OwlEye.originalAppID = originalAppID;
    }

    public static String getOriginalApkName() {
        return originalApkName;
    }

    public static void setOriginalApkName(String originalApkName) {
        OwlEye.originalApkName = originalApkName;
    }

    public static String getMergePath() {
        return mergePath;
    }

    public static void setMergePath(String mergePath) {
        OwlEye.mergePath = mergePath;
    }

    public static String getOriginalActivityName() {
        return originalActivityName;
    }

    public static void setOriginalActivityName(String originalActivityName) {
        OwlEye.originalActivityName = originalActivityName;
    }

    public static String getCompileOutputPath() {
        return compileOutputPath;
    }

    public static void setCompileOutputPath(String compilePath) {
        compileOutputPath = compilePath;
    }

    public static String getOriginalDecompiled() {
        return originalDecompiled;
    }

    public static void setOriginalDecompiled(String original) {
        originalDecompiled = original;
    }

    public static String getOriginalDynamicLayoutFilePath() {
        return originalDynamicLayoutFilePath;
    }

    public static void setOriginalDynamicLayoutFilePath(String originalDynamicLayoutFilePath) {
        OwlEye.originalDynamicLayoutFilePath = originalDynamicLayoutFilePath;
    }

    public static WRG getOriginalDefaultUIWRG() {
        return originalDefaultUIWRG;
    }

    public static void setOriginalDefaultUIWRG(WRG originalDefaultUIWRG) {
        OwlEye.originalDefaultUIWRG = originalDefaultUIWRG;
    }

    public static UI getOriginalDefaultUI() {
        return originalDefaultUI;
    }

    public static void setOriginalDefaultUI(UI originalDefaultUI) {
        OwlEye.originalDefaultUI = originalDefaultUI;
    }

    public static UI getOriginalLargestUI() {
        return originalLargestUI;
    }

    public static void setOriginalLargestUI(UI originalLargestUI) {
        OwlEye.originalLargestUI = originalLargestUI;
    }

    public static void run_time_reset() {
        /*** Run Time calc Map ***/
        runtime_map = new LinkedHashMap<>();
        chromosomeCalCount = 0;   // Reset runtime for population and chromosome
        populationCalCount = 0;
    }

    public static HashMap<String, Cutoff> getOriginalCutoffIssues() {
        return originalCutoffIssues;
    }

    public static void setOriginalCutoffIssues(HashMap<String, Cutoff> originalCutoffIssues) {
        OwlEye.originalCutoffIssues = originalCutoffIssues;
    }

    public static HashMap<String, Collision> getOriginalCollisionIssues() {
        return originalCollisionIssues;
    }

    public static void setOriginalCollisionIssues(HashMap<String, Collision> originalCollisionIssues) {
        OwlEye.originalCollisionIssues = originalCollisionIssues;
    }

//    public static LayoutGraph getOriginalDefaultUILayoutGraph() {
//        return originalDefaultUILayoutGraph;
//    }
//
//    public static void setOriginalDefaultUILayoutGraph(LayoutGraph originalDefaultUILayoutGraph) {
//        OwlEye.originalDefaultUILayoutGraph = originalDefaultUILayoutGraph;
//    }

    public static Map<String, Node<DomNode>> getOriginalDefaultUIElements() {
        return originalDefaultUIElements;
    }

    public static void setOriginalDefaultUIElements(Map<String, Node<DomNode>> originalDefaultUIElements) {
        OwlEye.originalDefaultUIElements = originalDefaultUIElements;
    }

    public static ArrayList<Issue> getCurrentIntersectionLayoutIssues() {
        return currentIntersectionLayoutIssues;
    }

    public static void setCurrentIntersectionLayoutIssues(ArrayList<Issue> currentIntersectionLayoutIssues) {
        OwlEye.currentIntersectionLayoutIssues = currentIntersectionLayoutIssues;
    }

    // getter and setter for originalMissingIssues
    public static HashMap<String, Missing> getOriginalMissingIssues() {
        return originalMissingIssues;
    }

    public static void setOriginalMissingIssues(HashMap<String, Missing> originalMissingIssues) {
        OwlEye.originalMissingIssues = originalMissingIssues;
    }


    public static int getAppCompileStyle() {
        return appCompileStyle;
    }

    public static void setAppCompileStyle(String compileInfo) {
        appCompileStyle = Integer.parseInt(compileInfo);
    }

    public static String getActivityToRunWithUIAutomator() {
        return activityToRunWithUIAutomator;
    }

    public static void setActivityToRunWithUIAutomator(String activityToRun) {
        activityToRunWithUIAutomator =activityToRun;
    }



    public static boolean getAppNeedUninstall() {
        return appNeedUninstall;
    }

    public static void setAppNeedUninstall(boolean appNeedUninstall) {
        OwlEye.appNeedUninstall = appNeedUninstall;
    }

    public static void init() {
        run_time_reset();
        originalCutoffIssues = new HashMap<>();
        originalCollisionIssues = new HashMap<>();
        originalMissingIssues = new HashMap<>();
        originalDefaultUI = null;
        originalLargestUI = null;
        originalDynamicLayoutFilePath = null;
        originalDecompiled = null;
        originalActivityName = null;
        originalApkName = null;
        originalAppID = null;
        mergePath = null;

    }

    public static void setPythonInterpreter(String arg) {
         pythonInterpreter = arg;
    }
    public static String getPythonInterpreter() {
        return pythonInterpreter;
    }

    public static void setAndroidSignature(String arg) {
        androidSignature = arg;
    }
    public static String getAndroidSignature() {
        return androidSignature;
    }

    public static void setBaseSubjectPath(String arg) {
        baseSubjectsPath = arg;
    }
    public static String getBaseSubjectPath() {
        return baseSubjectsPath;
    }

    public static void setAppNeedScrolling(String needScrolling) {
        appNeedScrolling=needScrolling;
    }
    public static String getAppNeedScrolling() {
        return appNeedScrolling;
    }
}
