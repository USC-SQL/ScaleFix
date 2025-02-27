package usc.edu.SALEM;

import usc.edu.SALEM.segmentation.EdgeLabel;

import java.io.File;
import java.util.*;

public class Constants {
    //Final Constants
//    public static final String linuxuser = "testing";

    /**
     * RECOMMEND SIZES AND SPACING
     ***/
    public static double RECOMMENDED_TOUCH_TARGET_SPACING = 8.0; //dp

    public static double RECOMMENDED_TOUCH_TARGET_HEIGHT = 48.0;  //dp
    public static double RECOMMENDED_TOUCH_TARGET_WIDTH = 48.0; //dp

    // Reference: https://support.google.com/accessibility/android/faq/6376582?hl=en#zippy=%2Ctouch-target-size
    public static double RECOMMENDED_EDGE_TOUCH_TARGET_HEIGHT = 32.0;// dp
    public static double RECOMMENDED_EDGE_TOUCH_TARGET_WIDTH = 32.0; // dp
    /**** Parameteres  ****/
    public static String UTILS_PATH;

    public static String COMPILE_SIGNATURE_KEY;
    public static String Detection_Tool_Path;


    // General problem types: Size or Space
    public static final String TOUCH_TARGET_SIZE_ISSUE = "TouchTargetSizeIssue";
    public static final String TOUCH_TARGET_SPACE_ISSUE = "TouchTargetSpaceIssue";

    // Types of size issues as suggested by the detection tool
    public static final String TOUCH_TARGET_HEIGHT_ISSUE = "TouchTargetHeightIssue";
    public static final String TOUCH_TARGET_WIDTH_ISSUE = "TouchTargetWidthIssue";
    public static final String TOUCH_TARGET_BOTH_ISSUE = "TouchTargetBothIssue";


    /**
     * Size properties naming mapping
     **/

    //Margin
    public static final String MARGIN_LEFT = "marginLeft";
    public static final String MARGIN_RIGHT = "marginRight";
    public static final String MARGIN_TOP = "marginTop";
    public static final String MARGIN_BOTTOM = "marginBottom";

    //Padding
    public static final String PADDING_LEFT = "paddingLeft";
    public static final String PADDING_RIGHT = "paddingRight";
    public static final String PADDING_TOP = "paddingTop";
    public static final String PADDING_BOTTOM = "paddingBottom";

    //Text Size

    public static final String TEXT_SIZE = "textSize";
    public static final String TEXT_SIZE_APPROACH = "ratio"; //AMOUNTOFINCREASE OR Ratio
    public static final String PARENTS_INCREASE_APPROACH = "ratio";  // ratio= add genes for parents to increase || transformation == add increase by transformation
    /**** HashMap ****/

    public static final Map<String, String> LAYOUT_VIEWGROUP_MAP = new HashMap<String, String>() {
        // To see if it is layout
        private static final long serialVersionUID = 1L;

        {
            put("RelativeLayout", "RelativeLayout");
            put("LinearLayout", "LinearLayout");
            put("TableLayout", "TableLayout");
            put("AbsoluteLayout", "AbsoluteLayout");
            put("FrameLayout", "FrameLayout");
            put("ListView", "ListView");
            put("ViewGroup", "ViewGroup");


        }
    };

    public static final Map<String, String> DETECTION_TOOL_TO_ISSUES_MAPPING = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {
            put("RESULT_ID_SMALL_TOUCH_TARGET_HEIGHT", TOUCH_TARGET_HEIGHT_ISSUE);
            put("RESULT_ID_SMALL_TOUCH_TARGET_WIDTH", TOUCH_TARGET_WIDTH_ISSUE);
            put("RESULT_ID_SMALL_TOUCH_TARGET_BOTH", TOUCH_TARGET_BOTH_ISSUE);
            put("RESULT_ID_SMALL_TOUCH_TARGET_WIDTH_AND_HEIGHT", TOUCH_TARGET_BOTH_ISSUE);


        }
    };
    public static final Map<String, String> PROPERTY_TO_FULL_ANDROID_NAME = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {
            put("width", "android:layout_width");
            put("height", "android:layout_height");
            put("padding", "android:padding");
            put(PADDING_LEFT, "android:paddingLeft");
            put(PADDING_RIGHT, "android:paddingRight");
            put(PADDING_TOP, "android:paddingTop");
            put(PADDING_BOTTOM, "android:paddingBottom");
            put("min-width", "android:minWidth");
            put("min-height", "android:minHeight");

            // Dynamic size
            //   put("currentWdith", "android:paddingBottom");
            //  put("android:layout_height", "int");
        }


    };

    public static final Map<String, Double> PROBLEM_IMPACT = new HashMap<String, Double>() {
        private static final long serialVersionUID = 1L;

        {
            put(TOUCH_TARGET_SPACE_ISSUE, 0.5);
            put(TOUCH_TARGET_SIZE_ISSUE, 1.0);
            put(TOUCH_TARGET_HEIGHT_ISSUE, 1.0);
            put(TOUCH_TARGET_WIDTH_ISSUE, 1.0);
            put(TOUCH_TARGET_BOTH_ISSUE, 2.0);
        }
    };

    public static final Set<String> GENERAL_ISSUES_TYPES_SEGMENT_TRY = new HashSet<String>() {
        private static final long serialVersionUID = 1L;

        {

            add(TOUCH_TARGET_SIZE_ISSUE);
            add(TOUCH_TARGET_SPACE_ISSUE);
        }
    };
    public static final Set<String> GENERAL_ISSUES_TYPES = new HashSet<String>() {
        private static final long serialVersionUID = 1L;

        {
            add(TOUCH_TARGET_HEIGHT_ISSUE);
            add(TOUCH_TARGET_WIDTH_ISSUE);
//            add(TOUCH_TARGET_BOTH_ISSUE); FOR NOW
            //  add(TOUCH_TARGET_SPACE_ISSUE); FOR NOW To test that it runs
        }
    };

    public static final Set<String> SIZE_SUB_ISSUES_TYPES = new HashSet<String>() {
        private static final long serialVersionUID = 1L;

        {
            add(TOUCH_TARGET_HEIGHT_ISSUE);
            add(TOUCH_TARGET_WIDTH_ISSUE);
//            add(TOUCH_TARGET_BOTH_ISSUE); FOR NOW
            //  add(TOUCH_TARGET_SPACE_ISSUE); FOR NOW To test that it runs
        }
    };
    //For now just the basic attribute to create the dependancy

    public static final Map<String, String> BASIC_HEIGHT_WIDTH_ATTRIBUTES = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {
            put("height", "android:layout_height");
            put("width", "android:layout_width");
//            put(PADDING_BOTTOM, "android:paddingBottom");
//            put("min-height", "android:minHeight");

        }
    };
    public static final Map<String, String> BASIC_HEIGHT_ATTRIBUTES = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {
            put("height", "android:layout_height");
//            put(PADDING_TOP, "android:paddingTop");
//            put(PADDING_BOTTOM, "android:paddingBottom");
//            put("min-height", "android:minHeight");

        }
    };

    public static final Map<String, String> BASIC_WIDTH_ATTRIBUTES = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {
            put("width", "android:layout_width");
//            put(PADDING_RIGHT, "android:paddingRight");
//            put(PADDING_LEFT, "android:paddingLeft");
//            put("min-width", "android:minWidth");
        }
    };
    public static final Map<String, String> BASIC_SPACE_ATTRIBUTES = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {
            put("minSpace", "margin");


        }
    };


    public static final Map<String, Map> ISSUES_TO_BASIC_ATTRIBUTES_MAP = new HashMap<String, Map>() {    //
        private static final long serialVersionUID = 1L;

        {
            put(TOUCH_TARGET_HEIGHT_ISSUE, BASIC_HEIGHT_ATTRIBUTES);
            put(TOUCH_TARGET_SIZE_ISSUE, BASIC_HEIGHT_WIDTH_ATTRIBUTES);
            put(TOUCH_TARGET_BOTH_ISSUE, BASIC_HEIGHT_WIDTH_ATTRIBUTES);
            put(TOUCH_TARGET_WIDTH_ISSUE, BASIC_WIDTH_ATTRIBUTES);
            put(TOUCH_TARGET_SPACE_ISSUE, BASIC_SPACE_ATTRIBUTES);
        }
    };

    public static final Map<String, Map> ISSUES_TO_FUll_TTRIBUTES_MAP = new HashMap<String, Map>() {    //
        private static final long serialVersionUID = 1L;

        {
            put(TOUCH_TARGET_HEIGHT_ISSUE, FULL_HEIGHT_ATTRIBUTES);
            put(TOUCH_TARGET_WIDTH_ISSUE, FULL_WIDTH_ATTRIBUTES);
            put(TOUCH_TARGET_SPACE_ISSUE, FULL_SPACE_ATTRIBUTES);
        }
    };

    public static final Map<String, String> FULL_HEIGHT_ATTRIBUTES = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {
            put("height", "android:layout_height");
            put(PADDING_TOP, "android:paddingTop");
            put(PADDING_BOTTOM, "android:paddingBottom");
            put("min-height", "android:minHeight");
        }
    };
    public static final Map<String, String> FULL_WIDTH_ATTRIBUTES = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {
            put("width", "android:layout_width");
            put(PADDING_RIGHT, "android:paddingRight");
            put(PADDING_LEFT, "android:paddingLeft");
            put("min-width", "android:minWidth");
        }
    };

    public static final Map<String, String> FULL_SPACE_ATTRIBUTES = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {
            put("margin", "android:margin");

        }
    };

    public static final Map<String, String> SPACE_ATTRIBUTES = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {


            put(MARGIN_LEFT, "android:layout_marginLeft");
            put(MARGIN_RIGHT, "android:layout_marginRight");
            put(MARGIN_TOP, "android:layout_marginTop");
            put(MARGIN_BOTTOM, "android:layout_marginBottom");

        }
    };
    public static final boolean TESTING_SEGMENTS = false;
    public static final String SEGMENTATION_APPROACH = "if"; // "mf" or "if";
    public static final String DISTORTION_APPROACH = "if"; // "mf" or "if";
    public static final String SPACE_CALCULATION_APPROACH = "ALL"; // SEGMENTS --> between segments
    //    public static final String Accessibility_SCORE_APPROACH = "missingSize"; // "numberOfIssues" or "missingSize";
    public static final String Accessibility_SCORE_APPROACH = "FROM_LAYOUT"; // "FROM_ISSUES" or "FROM_LAYOUT";

    public static int ANGEL_THRISHOLD = 60; // original 60
    public static float SEARCH_RADIUS = 500; // original 200 increased it to 1000
    public static double RECOMMENDED_AREA_FOR_TOUCH_TARGET = RECOMMENDED_TOUCH_TARGET_HEIGHT * RECOMMENDED_TOUCH_TARGET_WIDTH;
    public static double RECOMMENDED_AREA_FOR_EDGES_TOUCH_TARGET = RECOMMENDED_EDGE_TOUCH_TARGET_HEIGHT * RECOMMENDED_EDGE_TOUCH_TARGET_WIDTH;
//    public static final Map<String, String> GENERAL_ISSUES_TYPES_MAP = new HashMap<String, String>() {
//        private static final long sifixerialVersionUID = 1L;
//
//        {
//            put(TOUCH_TARGET_HEIGHT_ISSUE, "TouchTargetSpace");
//            put(TOUCH_TARGET_WIDTH_ISSUE, "TouchTargetSize");
//            put(TOUCH_TARGET_BOTH_ISSUE, "TouchTargetSpace");
//            put(TOUCH_TARGET_SPACE_ISSUE, "TouchTargetSize");
//        }
//    };


    /* GENERAL CONFIG PARAMETERS */
    public static boolean RUN_IN_DEBUG_MODE = false;

    // Phone or emulator density
    //https://material.io/resources/devices/
    public static double PHONE_DENSITY = 2.6;
    public static final Map<String, String> ATTRIBUTES_TO_COPY_FROM_STATIC_FILE = new HashMap<String, String>() {    //
        /**TO USE WHEN ReSOLVE STYLE ATT from styles when merging layouts ***/
        private static final long serialVersionUID = 1L;

        {
            put("android:layout_width", "android:layout_width");
            put("android:layout_height", "android:layout_height");
            put("android:padding", "android:padding");
            put("android:paddingLeft", "android:paddingLeft");
            put("android:paddingRight", "android:paddingRight");
            put("android:paddingTop", "android:paddingTop");
            put("android:paddingBottom", "android:paddingBottom");
            put("android:minWidth", "android:minWidth");
            put("android:minHeight", "android:minHeight");
            put("android:layout_marginLeft", "android:layout_marginLeft");
            put("android:layout_marginRight", "android:layout_marginRight");
            put("android:layout_marginTop", "android:layout_marginTop");
            put("android:layout_marginBottom", "android:layout_marginBottom");
            put("android:minWidth", "android:minWidth");
            put("android:minHeight", "android:minHeight");

        }
    };
    public static final Map<String, String> SIZE_SPACE_ATTRIBUTES = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {
            put("width", "android:layout_width");
            put("height", "android:layout_height");
            put("padding", "android:padding");
            put(PADDING_LEFT, "android:paddingLeft");
            put(PADDING_RIGHT, "android:paddingRight");
            put(PADDING_TOP, "android:paddingTop");
            put(PADDING_BOTTOM, "android:paddingBottom");
            put("min-width", "android:minWidth");
            put("min-height", "android:minHeight");
            put(MARGIN_LEFT, "android:layout_marginLeft");
            put(MARGIN_RIGHT, "android:layout_marginRight");
            put(MARGIN_TOP, "android:layout_marginTop");
            put(MARGIN_BOTTOM, "android:layout_marginBottom");
            put(TEXT_SIZE, "android:textSize");
        }


    };



    public static final Map<String, String> SIZE_ATTRIBUTES = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {
            put("width", "android:layout_width");
            put("height", "android:layout_height");
            put("padding", "android:padding");
            put(PADDING_LEFT, "android:paddingLeft");
            put(PADDING_RIGHT, "android:paddingRight");
            put(PADDING_TOP, "android:paddingTop");
            put(PADDING_BOTTOM, "android:paddingBottom");
            put("min-width", "android:minWidth");
            put("min-height", "android:minHeight");

        }


    };
    public static final Map<String, List<String>> PROPERTIES_TO_DEPENDENCY_MAPPING = new HashMap<String, List<String>>() {
        private static final long serialVersionUID = 1L;

        {
            put("width", Arrays.asList("min-width", PADDING_LEFT, PADDING_RIGHT));
            put("height", Arrays.asList("min-height", PADDING_TOP, PADDING_BOTTOM));
        }
    };
    public static final double SEGMENT_TERMINATE_THRESHOLD = 4;  //mf uses 4
    public static final int VIEWPORT_SEGMENT_ID = 0;
    public static final Map<String, String> PADDING_WIDTH_ATTRIBUTES = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {

            put(PADDING_LEFT, "android:paddingLeft");
            put(PADDING_RIGHT, "android:paddingRight");


        }


    };


    public static final String DETECTION_TOOL_LOGCAT_SUFFIX = "_LOGCAT_DUMP.txt";
    public static final int SATURATION_POINT = 2;
    public static final double CROSSOVER_RATE = 0.4; /// march 20 was 0.2
    public static final double MUTATION_RATE = 0.6;
    // weights for the two objective functions in the fitness score
    public static final double WEIGHT1 = 1.0; // accessibility objective
    public static final double WEIGHT2 = 0.5; // asethitic  objective
 //   public static final double WEIGHT3 = 0.3; // spacing  objective
    //ase test
    public static final double WEIGHT4 = 0.2; // space size  objective
    public static final double WEIGHT5 = 0.3; // changing size  objective
    //August
//    public static final double WEIGHT1 = 0.7; // accessibility objective
//    public static final double WEIGHT2 = 0.3; // asethitic  objective
//    public static final double WEIGHT3 = 0.3; // spacing  objective
//    //ase test
//    public static final double WEIGHT4 = 0.2; // space size  objective
//    public static final double WEIGHT5 = 0.3; // changing size  objective

    //    };
// Genetic Algorithm Search
//    public static int POPULATION_SIZE = 100;
    public static int POPULATION_SIZE = 1;//7//7; // Ali: to test
    public static int NUM_GENERATIONS = 2;//8//6;

    public static final double GAUSSIAN_MEAN_DIFF_WIDTH = 100;
    public static final double GAUSSUIAN_MEAN_DIFF_FONT = 20;
    public static final double GAUSSUIAN_MEAN_DIFF_MARGIN = 8;

    // Pure random search
    public static final boolean IS_PURE_RANDOM_SEARCH = false;  //Ali: Originally mf true
    //Path to the signature, other files and data important to files

    // Fitness function
    public static final double EXP1 = 2 ;//2;    // exponents
    public static final double EXP2 = 1 ;//2;
    public static final double EXPECTED_USABILITY_SCORE = 100;
    public static final double EXPECTED_AESTHETIC_SCORE = 0;
    public static final double EXPECTED_SPACING_SCORE = 100;


    public static String CRAWLING_SCRIPTS_PATH;
    public static String Compiled_output_path;
    public static String Crawled_UI_Dynamic_Layout;  // Path to store the dumped layout during search based
    public static String REPAIR_ISSUES_PATH;
    public static String ORIGINAL_ISSUES_PATH;
    public static String LOGCAT_FILE_DUMP_PATH; //Location where to dump locat to analyze
    public static String LOGCAT_DUMP_FILE_SCRIPT;
    public static String CRAWLING_SCRIPTS_SUFFIX = "_crawling.py";
    public static String DETECTION_TOOL_OUTPUT_SUFFIX = "_ACCESS_ISSUES.csv";
    public static String TOUCH_TARGET_ISSUES_OUTPUT_SUFFIX = "_TT_ISSUES.csv";


    //Android adb command
    public static String AAPT = "aapt";
//    public static String ADB = "adb -s emulator-5554";
//    public static String ADB = "adb -s 192.168.56.101:5555"; //Genymotion
    public static String ADB = "adb "; //Genymotion

    public static String GREP_ACTIVITY_NAME;

    public static String UIAUTOMATOR_DUMP = ADB + " shell " + "uiautomator dump";
    public static String PULL = ADB + " " + "pull %s %s";
    public static String SCREEN_CAP = ADB + " " + "shell screencap -p";
    public static String startAppCommand = Constants.ADB + " shell monkey -p %s -c android.intent.category.LAUNCHER 1"; // Not fully correct ToDo: Test and veirfy
    public static String DELETE_ANDROID_FILE_CMD = ADB + " shell " + " rm -f %s";
    public static String FORCE_STOP_APP = ADB + " shell " + " am force-stop %s";
    //    public static  String FORCE_STOP_APP=ADB+ " shell "+ " am force-stop %s";
    public static String Accessibility_SERVICE_PACKAGE = "com.aziz.accessibilityEval";
    public static String UNINSTALL_APK = ADB + " uninstall %s";
    public static String CLICK_HOME_BUTTON = ADB + " shell input keyevent 3";
    public static String CLEAN_LOGCAT_CMD = ADB + " logcat -b all -c ; " + ADB + "logcat -b all -c"; // %s to export the file
    public static String PULL_LOGCAT_CMD = ADB + " shell 'logcat -d --pid=$(pidof -s " + Accessibility_SERVICE_PACKAGE + ")'";


    //    public static final Map<String, String> FIXED_BOTH_ATTRIBUTES = new HashMap<String, String>() {    //
//        private static final long serialVersionUID = 1L;
//
//        {
//            put("android:layout_height", "48dp");
//            put("android:layout_width", "48dp");
//            //  put("android:layout_height", "int");
//        }
//    };

    public static void SET_GREP_ACTIVITY_NAME() {
        if (SALEM.basePath.contains("paul")) { // Ali PC
            GREP_ACTIVITY_NAME = ADB + " shell dumpsys window displays | grep -E 'mCurrentFocus|mFocusedApp'";
        } else {
            GREP_ACTIVITY_NAME = ADB + " shell dumpsys window windows | grep -E 'mCurrentFocus|mFocusedApp'";
        }
    }


    public static void SET_CONSTANTS_BASE_PATH() {
        CRAWLING_SCRIPTS_PATH = SALEM.basePath + File.separator + "SCRIPTS/crawling_scripts/";
        Compiled_output_path = SALEM.basePath + File.separator + "apks_folder/repaired_apks_search/";
        Crawled_UI_Dynamic_Layout = SALEM.basePath + File.separator + "apks_folder/crawled_layouts/";  // Path to store the dumped layout during search based
        REPAIR_ISSUES_PATH = SALEM.basePath + File.separator + "apks_folder/testIssueList/";
        ORIGINAL_ISSUES_PATH = SALEM.basePath + File.separator + "apks_folder/detection_apks/";
        LOGCAT_FILE_DUMP_PATH = SALEM.basePath + File.separator + "apks_folder/testIssueList/logcat/"; //Location where to dump locat to analyze
//        UTILS_PATH = TTFix.basePath + File.separator + "apks_folder";
        UTILS_PATH = SALEM.basePath + File.separator;
        COMPILE_SIGNATURE_KEY = UTILS_PATH + "/" + "app_with_ids_key.keystore";
//         Detection_Tool_Path = "/home" + File.separator + "testing" + File.separator + "Downloads/accessibility_eval-master/Artifact/Eval_tools/Access_runner/";


        LOGCAT_DUMP_FILE_SCRIPT = LOGCAT_FILE_DUMP_PATH + "dump_logcat.sh";

    }

    public static final Map<String, String> DYNAMIC_HEIGHT_ATTRIBUTES = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {
            put("android:minHeight", "dp");
            //  put("android:layout_height", "int");
        }
    };

    public static final Map<String, String> PADDING_HEIGHT_ATTRIBUTES = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {


            put(PADDING_TOP, "android:paddingTop");
            put(PADDING_BOTTOM, "android:paddingBottom");


        }


    };
    public static HashMap<String, String> ACCESSIBILITY_SUGGESTED_VALUES_FOR_TOUCH_TARGET_SPACE_PROBLEM = new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;

        {
            // legible font-sizes
            put("minSpace", String.valueOf(RECOMMENDED_TOUCH_TARGET_SPACING));
        }
    };
    public static HashMap<String, String> ACCESSIBILITY_SUGGESTED_VALUES_FOR_TOUCH_TARGET_SIZE_PROBLEM = new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;

        {
            // tap targets
            put("height", String.valueOf(RECOMMENDED_TOUCH_TARGET_HEIGHT));
            put("width", String.valueOf(RECOMMENDED_TOUCH_TARGET_WIDTH));
            put("min-height", String.valueOf(RECOMMENDED_TOUCH_TARGET_HEIGHT));
            put("min-width", String.valueOf(RECOMMENDED_TOUCH_TARGET_WIDTH));
        }
    };
    public static HashMap<String, String> PARENT_INCREASE = new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;

        {
            // tap targets
            put("height", String.valueOf(0.25));

        }
    };
    public static HashMap<String, String> TEXT_INCREASE = new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;

        {
            // tap targets
            put("textSize", String.valueOf(1));

        }
    };


    public static Map<String, HashMap<String, String>> ACCESSIBILITY_SUGGESTED_VALUES = new HashMap<String, HashMap<String, String>>() {
        private static final long serialVersionUID = 1L;

        {
            put(TOUCH_TARGET_SPACE_ISSUE, ACCESSIBILITY_SUGGESTED_VALUES_FOR_TOUCH_TARGET_SPACE_PROBLEM);
            put(TOUCH_TARGET_SIZE_ISSUE, ACCESSIBILITY_SUGGESTED_VALUES_FOR_TOUCH_TARGET_SIZE_PROBLEM);
            put(TOUCH_TARGET_HEIGHT_ISSUE, ACCESSIBILITY_SUGGESTED_VALUES_FOR_TOUCH_TARGET_SIZE_PROBLEM);
            put(TOUCH_TARGET_WIDTH_ISSUE, ACCESSIBILITY_SUGGESTED_VALUES_FOR_TOUCH_TARGET_SIZE_PROBLEM);
            put(TOUCH_TARGET_BOTH_ISSUE, ACCESSIBILITY_SUGGESTED_VALUES_FOR_TOUCH_TARGET_SIZE_PROBLEM);
            put("PARENT_INCREASE", PARENT_INCREASE);
            put("TEXT_INCREASE", TEXT_INCREASE);
            // expected value of content (viewport) width is added dynamically
            // from the API
        }
    };

    public static final Map<String, String> MAIN_PROPERTY_TO_MIN_PROPERTY = new HashMap<String, String>() {
        // Map height and width to its min-height and min-width
        private static final long serialVersionUID = 1L;

        {
            put("width", "min-width");
            put("height", "min-height");
        }
    };
    public static final Map<String, List<String>> CSS_PROPERTIES_DEPENDENCY = new HashMap<String, List<String>>() {
        private static final long serialVersionUID = 1L;

        {
            put("width", Arrays.asList("min-width"));
            put("height", Arrays.asList("min-height", ""));
        }
    };

    public static boolean TESTING;


    // dependent elements
    public enum RELATIONSHIP {
        BIDIRECTIONAL, UNIDIRECTIONAL
    }

    public static final Map<String, RELATIONSHIP> DEPENDENCY_RELATIONSHIP = new HashMap<String, RELATIONSHIP>() {
        private static final long serialVersionUID = 1L;

        {
            put("width", RELATIONSHIP.BIDIRECTIONAL);
            put("height", RELATIONSHIP.BIDIRECTIONAL);
            put("min-height", RELATIONSHIP.BIDIRECTIONAL);
            put("min-width", RELATIONSHIP.BIDIRECTIONAL);
        }
    };
    // Map issue types to the map that gets the size attributes
    public static final double TAP_TARGETS_RADIUS = RECOMMENDED_TOUCH_TARGET_SPACING; // dp


    public static final Map<String, String> DYNAMIC_WIDTH_ATTRIBUTES = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {
            put("android:minWidth", "dp");
            //  put("android:layout_height", "int");
        }
    };

    public static final Map<String, String> DYNAMIC_BOTH_ATTRIBUTES = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {
            put("android:minWidth", "dp");
            put("android:minHeight", "dp");
            //  put("android:layout_height", "int");
        }
    };


    public static final boolean IS_FITNESS_SCORE_MAXIMIZING = false;

    // Weights for edge labels
    public static final double EDGE_LABEL_INTERSECTION_WEIGHT = 1;
    public static final double EDGE_LABEL_CONTAINED_BY_WEIGHT = 0.25;
    public static final double EDGE_LABEL_CONTAINS_WEIGHT = 0.25;
    public static final double EDGE_LABEL_ABOVE_WEIGHT = 0.5;
    public static final double EDGE_LABEL_BELOW_WEIGHT = 0.5;
    public static final double EDGE_LABEL_LEFT_WEIGHT = 0.5;
    public static final double EDGE_LABEL_RIGHT_WEIGHT = 0.5;

    public static final Map<EdgeLabel, Double> EDGE_LABEL_WEIGHTS = new HashMap<EdgeLabel, Double>() {
        private static final long serialVersionUID = 1L;

        {
            put(EdgeLabel.INTERSECTION, EDGE_LABEL_INTERSECTION_WEIGHT);
            put(EdgeLabel.CONTAINED_BY, EDGE_LABEL_CONTAINED_BY_WEIGHT);
            put(EdgeLabel.CONTAINS, EDGE_LABEL_CONTAINS_WEIGHT);
            put(EdgeLabel.ABOVE, EDGE_LABEL_ABOVE_WEIGHT);
            put(EdgeLabel.BELOW, EDGE_LABEL_BELOW_WEIGHT);
            put(EdgeLabel.LEFT, EDGE_LABEL_LEFT_WEIGHT);
            put(EdgeLabel.RIGHT, EDGE_LABEL_RIGHT_WEIGHT);
        }
    };


}
