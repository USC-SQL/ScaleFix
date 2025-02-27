package usc.edu.OwlEye;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import usc.edu.OwlEye.ElementsProperties.*;
import usc.edu.SALEM.segmentation.EdgeLabel;
import usc.edu.SALEM.util.LoadConfig;

import java.util.*;

public class OwlConstants {
//    public static final String NEW_ELEMENT_GENE = "NewElementGene";
    public static final String CHANGE_PROPERTY_GENE = "ChangePropertyGene";
    public static  int NUM_EMULATORS = 1;

    public static  List<String> availableEmulators = new ArrayList<>(NUM_EMULATORS);
//    static {
//        availableEmulators.add("emulator-5554");
//        availableEmulators.add("emulator-5556");
//        availableEmulators.add("emulator-5558");
//        availableEmulators.add("emulator-5560");
//        availableEmulators.add("emulator-5562");
////        availableEmulators.add("emulator-5564");
////        availableEmulators.add("emulator-5566");
////        availableEmulators.add("emulator-5568");
//
//    }


    // The next are for the gene value types
    public static final String SCON_VALUE = "sCon";
    public static final String ID_VALUE = "id";
    public static final String BOOLEAN_VALUE = "boolean";
    public static final String STRING_VALUE = "stringVal";
    public static final String NUMERICAL_DP_VALUE = "dp";
    public static final String NUMERICAL_INT_VALUE = "int";
    public static final String AUTOMATOR2_UI_CAPTURE_MODE = "automator2";
    public static final String LEGACY_UI_CAPTURE_MODE = "legacy";
    public static final String CONSTRAINTS_LAYOUT_RELATION ="constraintsRelation" ;
    public static final String REGULAR_LAYOUT_RELATION = "regularRelation";

    public static String configFile="../TTFIXHelper/config_data.ini";
//    public static String configFile="./config_data.ini";

//    public static String configFile="/home/ali/SQL_Repo/TTFIXHelper/config_data.ini"; my ubuntu sys in surface book


    public static final String DEFAULT_FONT_FOLDER =LoadConfig.getInstance().getConfig_data().get("default_font_ui");
    public static final String LARGEST_DISPLAY_FOLDER = LoadConfig.getInstance().getConfig_data().get("largest_display_ui");
    public static final String LARGEST_FONT_FOLDER = LoadConfig.getInstance().getConfig_data().get("largest_font_ui");

    public static  String CURRENT_SCALING_VERSION_FOLDER = null; // this gets set in the run of the approach
public static final String LARGEST_DISPLAY_LARGEST_FONT_FOLDER = LoadConfig.getInstance().getConfig_data().get("largest_display_largest_font_ui");


    //Paths
    public static final String ISSUES_PATH= LoadConfig.getInstance().getConfig_data().get("issues_path");

    //Type of issues
    public static final String CUT_OFF_ISSUE = "cutoff";
    public static final String COLLISION_ISSUE = "collision";
    public static final String MISSING_ISSUE = "missing";


    public static final String HORIZONTAL_ISSUE = "horizontal";
    public static final String VERTICAL_ISSUE = "vertical";





    /*
     * Constants copied from SALEM
     */
    /* GENERAL CONFIG PARAMETERS */
    public static boolean RUN_IN_DEBUG_MODE = false;

    // Phone or emulator density
    //https://material.io/resources/devices/
    public static double PHONE_DENSITY = 3.5;



    public static final String DETECTION_TOOL_LOGCAT_SUFFIX = "_LOGCAT_DUMP.txt";
    public static final int SATURATION_POINT = 2;
    public static final double CROSSOVER_RATE = 0.6;//0.2  //April 2023 was 0.4 but after removing duplicate chromosomes, we set it  0.6
    public static final double CROSSOVER_RATE2 = 0.4;//0.2  /April 2023 was 0.3 but after removing duplicate chromosomes, we set it  0.6

    public static final double MUTATION_RATE = 0.6;
    // weights for the two objective functions in the fitness score
    // Fitness function
    public static final double EXP3 = 3 ;//2;    // exponents
    public static final double EXP25 = 2.5 ;//2;    // exponents

    public static final double EXP2 = 2 ;//2;    // exponents
    public static final double EXP1 = 1 ;//2;
    public static final double WEIGHT2 = 2.0; // accessibility objective
    public static final double WEIGHT3 = 2.0; // accessibility objective
    public static final double WEIGHT25 = 2.0; // accessibility objective

    public static final double WEIGHT1 = 1.0; // accessibility objective
//    public static final double WEIGHT2 = 0.5; // asethitic  objective

    public static final double WEIGHT4 = 0.2; // space size  objective
    public static final double WEIGHT5 = 0.3; // changing size  objective


    public static final double EXPECTED_POSITIONING_AND_ALIGN_SCORE = 0;
    public static final double EXPECTED_TEXT_SCORE = 0;
    public static final double EXPECTED_COLLISION_SCORE = 0;
    public static final double EXPECTED_MISSING_ELEMENTS_SCORE = 0;

// Genetic Algorithm Search
//    public static int POPULATION_SIZE = 100;
    public static int POPULATION_SIZE = 7;//7//7; // Ali: to test March 20 was 3
    public static int NUM_GENERATIONS = 6;//8//6;

    public static final double GAUSSIAN_MEAN_DIFF_WIDTH = 100;
    public static final double GAUSSUIAN_MEAN_DIFF_FONT = 20;
    public static final double GAUSSUIAN_MEAN_DIFF_MARGIN = 8;

    // Pure random search
    public static final boolean IS_PURE_RANDOM_SEARCH = false;  //Ali: Originally mf true
    //Path to the signature, other files and data important to files



    //Android adb command
    public static String AAPT = "aapt";
       public static String ADB = "adb -s %s";
    //public static String ADB = "adb -s 192.168.56.101:5555"; //Genymotion
    public static String GREP_ACTIVITY_NAME;

    public static String UIAUTOMATOR_DUMP = ADB + " shell " + "uiautomator dump";
    public static String PULL = ADB + " " + "pull %s %s";
    public static String SCREEN_CAP = ADB + " " + "shell screencap -p";
    public static String startAppCommand = OwlConstants.ADB + " shell monkey -p %s -c android.intent.category.LAUNCHER 1"; // Not fully correct ToDo: Test and veirfy
    public static String DELETE_ANDROID_FILE_CMD = ADB + " shell " + " rm -f %s";
    public static String FORCE_STOP_APP = ADB + " shell " + " am force-stop %s";
    //    public static  String FORCE_STOP_APP=ADB+ " shell "+ " am force-stop %s";
    public static String Accessibility_SERVICE_PACKAGE = "com.aziz.accessibilityEval";
    public static String UNINSTALL_APK = ADB + " uninstall %s";
    public static String INSTALL_APK = ADB + " install -r %s";
    public static String INSTALL_APK_WITH_PERMISSION = ADB + " install -r -g %s";

    public static String CLICK_HOME_BUTTON = ADB + " shell input keyevent 3";
    public static String CLEAN_LOGCAT_CMD = ADB + " logcat -b all -c ; " + ADB + "logcat -b all -c"; // %s to export the file
    public static String PULL_LOGCAT_CMD = ADB + " shell 'logcat -d --pid=$(pidof -s " + Accessibility_SERVICE_PACKAGE + ")'";


    public static String matchParent="matchParent";
    public static String wrapContent="wrapContent";
    public static String numericalValue="numerical";
    public static String shouldRemoveAttribute="RemoveAttribute";
    public static String numericalRegex=".*\\d+.*";


    //Type of changes
    public static final String CHANGE_INCREASE ="Increase";
    public static final String CHANGE_DECREASE ="Decrease";
    public static final  String CHANGE_CHANGE ="change";
    public static final String CHANGE_REMOVE ="remove";
    public static final String CHANGE_ADD ="add";
    public static final String CHANGE_ADD_NEW_ELEMENT ="NewElementGene";
    public static final String CHANGE_SKIP ="SKIP";


    public static final boolean IS_FITNESS_SCORE_MAXIMIZING = false;

    // Weights for edge labels
    public static final double EDGE_LABEL_INTERSECTION_WEIGHT = 1;
    public static final double EDGE_LABEL_CONTAINED_BY_WEIGHT = 0.25;
    public static final double EDGE_LABEL_CONTAINS_WEIGHT = 0.25;
    public static final double EDGE_LABEL_ABOVE_WEIGHT = 0.5;
    public static final double EDGE_LABEL_BELOW_WEIGHT = 0.5;
    public static final double EDGE_LABEL_LEFT_WEIGHT = 0.5;
    public static final double EDGE_LABEL_RIGHT_WEIGHT = 0.5;





    // Issues types

    public static final Set<String> GENERAL_ISSUES_TYPES = new HashSet<String>() {
        private static final long serialVersionUID = 1L;

        {

            add(CUT_OFF_ISSUE);
            add(COLLISION_ISSUE);
            add(MISSING_ISSUE);
        }
    };


    public static final Map<String, String> ATTRIBUTES_INT_VALUES_MAPPING = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {

            put( "android:maxLines","int");
            put( "android:lines","int");


        }
    };
    public static final Map<String, String> ATTRIBUTES_TO_UNIT_SUFFIX_MAPPING = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {
            put( "android:layout_height","dp");
            put( "android:layout_width","dp");
            put("android:minHeight","dp");
            put("android:minWidth","dp");
            put("android:maxHeight","dp");
            put("android:maxWidth","dp");

            // padding
            put("android:paddingTop", "dp");
            put("android:paddingBottom", "dp");
            put("android:paddingLeft", "dp");
            put("android:paddingRight", "dp");
            put("android:paddingStart", "dp");
            put("android:paddingEnd", "dp");
            put("android:padding", "dp");
            // margin
            put("android:layout_marginTop", "dp");
            put("android:layout_marginBottom", "dp");
            put("android:layout_marginLeft", "dp");
            put("android:layout_marginRight", "dp");
            put("android:layout_marginStart", "dp");
            put("android:layout_marginEnd", "dp");
            put("android:layout_margin", "dp");

            //AlignParent
            put("android:layout_alignParentBottom","");
            put("android:layout_alignParentTop","");
            put("android:layout_alignParentLeft","");
            put( "android:layout_alignParentRight","");
            put( "android:layout_alignParentStart","");
            put( "android:layout_alignParentEnd","");
            put( "android:layout_alignWithParentIfMissing","");
            put( "android:layout_centerInParent","");
            put( "android:maxLines","");
            put( "android:lines","");
            put("ns1:layout_constraintVertical_weight","");
            put("ns1:layout_constraintHorizontal_weight","");
            put("android:layout_weight","");
            put("android:autoSizeTextType","");

        }
    };
    public static final Map<String, String> PADDING_FOR_VERTICAL_CUTOFF_ISSUES = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {

            put("android:paddingLeft", "android:paddingLeft");
            put("android:paddingRight", "android:paddingRight");
        }
    };

    public static final Map<String, String> PADDING_FOR_HORIZONTAL_CUTOFF_ISSUES = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {

            put("android:paddingTop", "android:paddingTop");
            put("android:paddingBottom", "android:paddingBottom");
        }
    };


    public static final Map<String, String> MARGINS_FOR_VERTICAL_CUTOFF_ISSUES = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {

            put("android:layout_marginLeft", "android:layout_marginLeft");
            put("android:layout_marginRight", "android:layout_marginRight");
            put("android:layout_marginStart", "android:layout_marginStart");
            put("android:layout_marginEnd", "android:layout_marginEnd");
        }
    };

    public static final Map<String, String> MARGINS_FOR_HORIZONTAL_CUTOFF_ISSUES = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {

            put("android:layout_marginTop", "android:layout_marginTop");
            put("android:layout_marginBottom", "android:layout_marginBottom");



        }
    };
    public static final Map<String, String> ATTRIBUTES_TEXT_CUTOFF_VERTICAL = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {
            put("android:paddingTop", "ns1:paddingTop");
            put("android:paddingBottom", "ns1:paddingBottom");
//            put("android:paddingLeft", "ns1:paddingLeft");
//            put("android:paddingRight", "ns1:paddingRight");
            put("android:minHeight", "ns1:minHeight");
//            put("android:minWidth", "ns1:minWidth");
            put("android:layout_marginTop", "ns1:layout_marginTop");
            put("android:layout_marginBottom", "ns1:layout_marginBottom");
//            put("android:layout_marginLeft", "ns1:layout_marginLeft");
//            put("android:layout_marginRight", "ns1:layout_marginRight");
            put("android:layout_margin", "ns1:layout_margin");
            put("android:padding", "ns1:padding");
        }
    };

    public static final Map<String, String> Full_To_Short_Attributes = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {
            put("android:layout_height",Height.propertyName);
            put( "android:layout_width",Width.propertyName);
            put("android:paddingTop", "paddingTop");
            put("android:paddingBottom", "paddingBottom");
            put("android:paddingLeft", "paddingLeft");
            put("android:paddingRight", "paddingRight");
            put("android:minHeight", "minHeight");
            put("android:minWidth", "minWidth");
            put("android:maxHeight", MaxHeight.propertyName);
            put("android:maxWidth", MaxWidth.propertyName);
            put("android:autoSizeTextType",AutoText.propertyName);
            put("android:layout_marginTop", "marginTop");
            put("android:layout_marginBottom", "marginBottom");
            put("android:layout_marginLeft", "marginLeft");
            put("android:layout_marginRight", "marginRight");
            put("android:layout_margin", "margin");
            put("android:padding", "padding");

        }
    };



    public static final Map<String, String> TO_FuLL_ATTRIBUTES_MAPPING = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {
            put(Height.propertyName, "android:layout_height");
            put(Width.propertyName, "android:layout_width");
            put(MinHeight.propertyName,"android:minHeight");
            put(MinWidth.propertyName,"android:minWidth");
            put(MaxHeight.propertyName,"android:maxHeight");
            put(MaxWidth.propertyName,"android:maxWidth");
            put(AutoText.propertyName,"android:autoSizeTextType");
            //AlignParent
            put(AlignParent.alignParentBottom, "ns1:layout_alignParentBottom");
            put(AlignParent.alignParentTop, "ns1:layout_alignParentTop");
            put(AlignParent.alignParentLeft, "ns1:layout_alignParentLeft");
            put(AlignParent.alignParentRight, "ns1:layout_alignParentRight");
            put(AlignParent.alignParentStart, "ns1:layout_alignParentStart");
            put(AlignParent.alignParentEnd, "ns1:layout_alignParentEnd");
            put(AlignParent.alignWithParentIfMissing, "ns1:layout_alignWithParentIfMissing");
            put(AlignParent.centerInParent, "ns1:layout_centerInParent");
            put(MaxLine.propertyName, "android:maxLines");
            put(Lines.propertyName, "android:lines");
            put(Weight.propertyName, "android:layout_weight");
            put(ConstraintVerticalWeight.propertyName, "ns1:layout_constraintVertical_weight");
            put(ConstraintHorizontalWeight.propertyName, "ns1:layout_constraintHorizontal_weight");
//            put(ConstraintVerticalWeight.propertyName, "android:layout_constraintVertical_weight");
//            put(ConstraintHorizontalWeight.propertyName, "android:layout_constraintHorizontal_weight");
            // Values
            put(wrapContent, "wrap_content");
            put(matchParent, "match_parent");
            put("paddingTop","android:paddingTop");
            put("paddingBottom","android:paddingBottom");
            put("paddingLeft","android:paddingLeft");
            put("paddingRight","android:paddingRight" );

            put("marginTop","android:layout_marginTop");
            put("marginBottom","android:layout_marginBottom");
            put("marginLeft","android:layout_marginLeft");
            put( "marginRight","android:layout_marginRight");
            put("margin","android:layout_margin");
            put( "padding","android:padding");
            put("scrollbars","android:scrollbars");


        }
    };

    public static final BiMap<String, String> TO_FuLL_ATTRIBUTES_BI_MAPPING = HashBiMap.create(TO_FuLL_ATTRIBUTES_MAPPING);

    public static final Map<String, String> NUMERICAL_INT_VALUE_PROPS = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {
            put(Lines.propertyName,TO_FuLL_ATTRIBUTES_MAPPING.get(Lines.propertyName));
            put(MaxLine.propertyName,TO_FuLL_ATTRIBUTES_MAPPING.get(MaxLine.propertyName));
            put(Weight.propertyName,TO_FuLL_ATTRIBUTES_MAPPING.get(Weight.propertyName));
            put(ConstraintVerticalWeight.propertyName,TO_FuLL_ATTRIBUTES_MAPPING.get(ConstraintVerticalWeight.propertyName));
            put(ConstraintHorizontalWeight.propertyName,TO_FuLL_ATTRIBUTES_MAPPING.get(ConstraintHorizontalWeight.propertyName));


        }
    };
    public static final Map<String, String> CONSTRAINTS_LAYOUT_ATTRIBUTES_MAP = new HashMap<String, String>() {    //
        // Constraints layout attributes, These are used in the AAoriginVHElements to the elements
        private static final long serialVersionUID = 1L;

        {
            put("android:layout_constraintLeft_toLeftOf", "ns1:layout_constraintLeft_toLeftOf");
            put("android:layout_constraintLeft_toRightOf", "ns1:layout_constraintLeft_toRightOf");
            put("android:layout_constraintRight_toLeftOf", "ns1:layout_constraintRight_toLeftOf");
            put("android:layout_constraintRight_toRightOf", "ns1:layout_constraintRight_toRightOf");
            put("android:layout_constraintTop_toTopOf", "ns1:layout_constraintTop_toTopOf");
            put("android:layout_constraintTop_toBottomOf", "ns1:layout_constraintTop_toBottomOf");
            put("android:layout_constraintBottom_toTopOf", "ns1:layout_constraintBottom_toTopOf");
            put("android:layout_constraintBottom_toBottomOf", "ns1:layout_constraintBottom_toBottomOf");
            put("android:layout_constraintBaseline_toBaselineOf", "ns1:layout_constraintBaseline_toBaselineOf");
            put("android:layout_constraintStart_toEndOf", "ns1:layout_constraintStart_toEndOf");
            put("android:layout_constraintStart_toStartOf", "ns1:layout_constraintStart_toStartOf");
            put("android:layout_constraintEnd_toStartOf", "ns1:layout_constraintEnd_toStartOf");
            put("android:layout_constraintEnd_toEndOf", "ns1:layout_constraintEnd_toEndOf");
            put("android:layout_constraintHorizontal_bias", "ns1:layout_constraintHorizontal_bias");
            put("android:layout_constraintVertical_bias", "ns1:layout_constraintVertical_bias");
            put("android:layout_constraintDimensionRatio", "ns1:layout_constraintDimensionRatio");
            put("android:layout_constraintHorizontal_weight", "ns1:layout_constraintHorizontal_weight");
            put("android:layout_constraintVertical_weight", "ns1:layout_constraintVertical_weight");
            put("android:layout_constraintHorizontal_chainStyle", "ns1:layout_constraintHorizontal_chainStyle");
            put("android:layout_constraintVertical_chainStyle", "ns1:layout_constraintVertical_chainStyle");
            put("android:layout_constraintWidth_default", "ns1:layout_constraintWidth_default");
            put("android:layout_constraintHeight_default", "ns1:layout_constraintHeight_default");
            put("android:layout_constraintWidth_min", "ns1:layout_constraintWidth_min");
            put("android:layout_constraintWidth_max", "ns1:layout_constraintWidth_max");
            put("android:layout_constraintHeight_min", "ns1:layout_constraintHeight_min");
            put("android:layout_constraintHeight_max", "ns1:layout_constraintHeight_max");
            put("android:layout_goneMarginLeft", "ns1:layout_goneMarginLeft");
            put("android:layout_goneMarginTop", "ns1:layout_goneMarginTop");
            put("android:layout_goneMarginRight", "ns1:layout_goneMarginRight");
            put("android:layout_goneMarginBottom", "ns1:layout_goneMarginBottom");
            put("android:layout_goneMarginStart", "ns1:layout_goneMarginStart");
            put("android:layout_goneMarginEnd", "ns1:layout_goneMarginEnd");
        }
    };
    public static final Map<String, String> LAYOUT_ATTRIBUTES_MAP = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {
            put("android:layout_toRightOf","android:layout_toRightOf");
            put("android:layout_toLeftOf","android:layout_toLeftOf");
            put("android:layout_below","android:layout_below");
            put("android:layout_above","android:layout_above");
            put("android:layout_alignBaseline","android:layout_alignBaseline");
            put("android:layout_alignBottom","android:layout_alignBottom");
            put("android:layout_alignLeft","android:layout_alignLeft");
            put("android:layout_alignRight","android:layout_alignRight");
            put("android:layout_alignTop","android:layout_alignTop");
            put("android:layout_alignParentBottom","android:layout_alignParentBottom");
            put("android:layout_alignParentLeft","android:layout_alignParentLeft");
            put("android:layout_alignParentRight","android:layout_alignParentRight");
            put("android:layout_alignParentTop","android:layout_alignParentTop");
            put("android:layout_centerHorizontal","android:layout_centerHorizontal");
            put("android:layout_centerVertical","android:layout_centerVertical");
            put("android:layout_centerInParent","android:layout_centerInParent");
            put("android:layout_toStartOf","android:layout_toStartOf");
            put("android:layout_toEndOf","android:layout_toEndOf");
            put("android:layout_alignStart","android:layout_alignStart");
            put("android:layout_alignEnd","android:layout_alignEnd");
            put("android:layout_alignParentStart","android:layout_alignParentStart");
            put("android:layout_alignParentEnd","android:layout_alignParentEnd");

            put("android:layout_alignWithParentIfMissing","android:layout_alignWithParentIfMissing");
        }


    };
    public static final Map<String, String> ATTRIBUTES_TO_DIVIDE_BY_DP = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {
            put(Height.propertyName, "android:layout_height");
            put(Width.propertyName, "android:layout_width");




        }
    };
    public static final Map<String, String> ATTRIBUTES_TO_REMOVE_TOGETHER_MAPPING = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {
            put(TO_FuLL_ATTRIBUTES_MAPPING.get(MaxLine.propertyName), TO_FuLL_ATTRIBUTES_MAPPING.get(Lines.propertyName));
        }
    };

    public static final Map<String, String> HEIGHT_WIDTH_WEIGHT_MAPPING = new HashMap<String, String>() {    //
        private static final long serialVersionUID = 1L;

        {
            put(Height.propertyName,ConstraintVerticalWeight.propertyName);
            put(Width.propertyName, ConstraintHorizontalWeight.propertyName);
            put(Weight.propertyName, Weight.propertyName);
        }
    };


    public static void setGrepActivityNameCommand() {

    int API=29;
        if (API>26) { // Ali PC
            GREP_ACTIVITY_NAME = ADB + " shell dumpsys window displays | grep -E 'mCurrentFocus|mFocusedApp'";
        } else {
            GREP_ACTIVITY_NAME = ADB + " shell dumpsys window windows | grep -E 'mCurrentFocus|mFocusedApp'";
       }
    }

 public static void setUpADBCommands(String adbPath){
      ADB = adbPath+" -s %s";
     UIAUTOMATOR_DUMP = ADB + " shell " + "uiautomator dump";
      PULL = ADB + " " + "pull %s %s";
     SCREEN_CAP = ADB + " " + "shell screencap -p";
      startAppCommand = OwlConstants.ADB + " shell monkey -p %s -c android.intent.category.LAUNCHER 1"; // Not fully correct ToDo: Test and veirfy
      DELETE_ANDROID_FILE_CMD = ADB + " shell " + " rm -f %s";
      FORCE_STOP_APP = ADB + " shell " + " am force-stop %s";
     //    public static  String FORCE_STOP_APP=ADB+ " shell "+ " am force-stop %s";
      Accessibility_SERVICE_PACKAGE = "com.aziz.accessibilityEval";
      UNINSTALL_APK = ADB + " uninstall %s";
      INSTALL_APK = ADB + " install -r %s";
      INSTALL_APK_WITH_PERMISSION = ADB + " install -r -g %s";

      CLICK_HOME_BUTTON = ADB + " shell input keyevent 3";
      CLEAN_LOGCAT_CMD = ADB + " logcat -b all -c ; " + ADB + "logcat -b all -c"; // %s to export the file
      PULL_LOGCAT_CMD = ADB + " shell 'logcat -d --pid=$(pidof -s " + Accessibility_SERVICE_PACKAGE + ")'";
 }





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
