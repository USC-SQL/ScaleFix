package eval.testSubFunctions;

import com.google.common.io.Files;
import usc.edu.OwlEye.util.Utils;
import usc.edu.SALEM.Constants;
import usc.edu.SALEM.VHTree.XMLUtils;
import usc.edu.SALEM.util.LoadConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class RefineDynamicVH {



    private static Logger logger = Logger.getLogger(RefineDynamicVH.class.getName());



    public static void main(String[] args) {
        HashMap<String, String> propConfig = LoadConfig.getInstance().getConfig_data();
        String device_name = propConfig.get("device_name");
        String device_title = propConfig.get("device_title");

        if (device_title.contains("Nexus26P")) {
            Constants.PHONE_DENSITY = 3.5;
        }

        String subjectID = "";
        String appName = "";
        String activityName = "";
        System.out.println("ARGS:" + args.length);
        if (args.length > 0) {
            System.out.println("subjectID: " + args[0]);
            subjectID = args[0];
        } else {
            subjectID = "openfoodm";
//             appName = "openfoodfacts.github.scrachx.openfood";
//             activityName = ".views.MainActivity";

        }


        String[] subjectsInfo = Utils.readSubjectsCSV(propConfig.get("subjects_csv"), subjectID);
        if (subjectsInfo == null) {
            System.out.println("Subject not found");
            System.exit(1);
        }
        appName = subjectsInfo[0];
        activityName = subjectsInfo[1];
        String default_ui_version_type = "default_font";
        String largest_ui_version_type = "largest_font";
       boolean processDefaultFont = true;
        boolean processLargestFont = false;


        // Setting file paths
        String VHPathPlaceHolder = propConfig.get("subjects_base_path") + File.separator + subjectID + File.separator + "%s" + File.separator + propConfig.get("device_title");//+File.separator+appName;

        String completeVH = String.format(VHPathPlaceHolder, propConfig.get("complete_vh")); //mergedLayoutPath

        String dynamicVH = String.format(VHPathPlaceHolder, propConfig.get("dynamic_vh"));//refinedDynamicPath
        String augmentedVH = String.format(VHPathPlaceHolder, propConfig.get("augmented_vh"));//augmented_path
        String deviceName = propConfig.get("device_name");
//        String decompiledAPK = propConfig.get("subjects_base_path") + File.separator + subjectID + File.separator + propConfig.get("decompiled_apk")+File.separator+appName;//originalStaticFiles
//        String issuesPath    = propConfig.get("subjects_base_path") + File.separator + subjectID + File.separator + propConfig.get("issues_path");
//        String candidateRepairsFolders = propConfig.get("search_output_path") + File.separator + subjectID + File.separator + propConfig.get("candidate_repairs_folder");
//        String crawlingScript = propConfig.get("subjects_base_path") + File.separator + subjectID + File.separator + propConfig.get("crawl_python_script");
//        String compiledCandidateRepairsFolders = propConfig.get("search_output_path") + File.separator + subjectID + File.separator + propConfig.get("compiled_candidate_repairs_folder");
//        String finalRepairOutputFolder = propConfig.get("search_output_path") + File.separator + subjectID + File.separator + propConfig.get("final_repair_output_folder");

        // Getting the default and largest font sizes VHs
        String defaultFontDynamicVH = dynamicVH + File.separator + default_ui_version_type;
        String largestFontDynamicVH = dynamicVH + File.separator + largest_ui_version_type;
//        String defaultFontAugmentedVH = augmentedVH + File.separator + default_ui_version_type;
//        String largestFontAugmentedVH = augmentedVH + File.separator + largest_ui_version_type;
//        String defaultFontCompleteVH  = completeVH + File.separator + default_ui_version_type;
//        String largestFontCompleteVH  = completeVH + File.separator + largest_ui_version_type;



        if (processDefaultFont) {
            logger.info("Processing default font");
            ArrayList<String> activities = getListOfActivities(defaultFontDynamicVH, activityName);
            String refinedDynamicVH = defaultFontDynamicVH+ File.separator+
                    "refined/";
            preprocessDynamicFolder(defaultFontDynamicVH, refinedDynamicVH, activities);

        }


        if (processLargestFont) {
            logger.info("Processing largest font");
            ArrayList<String> activities = getListOfActivities(defaultFontDynamicVH, activityName);
            String refinedDynamicVH = defaultFontDynamicVH+ File.separator+
                    "refined/";
            preprocessDynamicFolder(defaultFontDynamicVH, refinedDynamicVH, activities);



        }











    }

    private static void preprocessDynamicFolder(String defaultFontDynamicVH, String refinedDynamicVH, ArrayList<String> activities) {
        String layoutPath = defaultFontDynamicVH;
        String refinedPath = refinedDynamicVH;

        File inputFolder = new File(layoutPath);
        File outputFolder = new File(refinedPath);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        // Extract the activity layout only into new XMLs
        for (File f : inputFolder.listFiles()) {
            String fileName = f.getName();
            if (fileName.endsWith(".xml")) {
                if (fileName.contains("com.sec.android.app.launchercom.android.launcher2.Launcher")) {
                    // The default launcher of Android
                    continue;
                }
                if (!activities.contains(fileName)) {
                    continue;
                }
                boolean success = XMLUtils.extractActivityLayout(f.getPath(), refinedPath);
                // also copy the corresponding png files
                if (success) {
                    // copy the corresponding png to the outputFolder
                    String pngFile = fileName.replace("xml", "png");
                    File srcPng = new File(inputFolder + File.separator + pngFile);
                    File desPng = new File(outputFolder + File.separator + pngFile.substring(pngFile.indexOf("_") + 1));
                    try {
                        Files.copy(srcPng, desPng);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static ArrayList<String> getListOfActivities(String dynamicVH, String activityName) {
        ArrayList<String> activities = new ArrayList<>();
//        String dynamic_path = SALEM.basePath + File.separator + dynamic_layouts_folder +
//                File.separator + subject+"/"+ui_version;
//        dynamic_path =  dynamic_layouts_folder +
//                File.separator + subject+"/"+ui_version;
        File dynamicLayoutFolder = new File(dynamicVH);
        for (File f : dynamicLayoutFolder.listFiles()) {
            String fileName = f.getName();
            System.out.println("fileName: " + fileName);
            if (fileName.contains(".png")) {

                continue;  //Ali stopped this while initial code running

            }
            String d = fileName.replace(".xml", "");
            if (d.equalsIgnoreCase(activityName)) {
                activities.add(fileName);
            }

        }
        return activities;
    }
}
