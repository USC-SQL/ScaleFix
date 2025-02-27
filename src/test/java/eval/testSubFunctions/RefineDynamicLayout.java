package eval.testSubFunctions;

import usc.edu.OwlEye.OwlConstants;
import usc.edu.OwlEye.OwlEye;
import usc.edu.SALEM.VHTree.XMLUtils;
import com.google.common.io.Files;
import usc.edu.SALEM.Constants;
import usc.edu.SALEM.SALEM;
import usc.edu.SALEM.util.LoadConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class RefineDynamicLayout {

    public static void main(String[] args) {
        String device_label = "Nexus P6";
        if (device_label.equalsIgnoreCase("Nexus P6")) {
            Constants.PHONE_DENSITY = 3.5;
        }
        String user = "ali";
        HashMap<String, String> propConfig = LoadConfig.getInstance().getConfig_data();
        String device_name = propConfig.get("device_name");
        String device_title = propConfig.get("device_title");
        String screen_res = "Physical size: 1440x2560";  //adb shell  wm size
        boolean IsThreadExecuted = false;
        String RUNNING_MODE = "activity"; // or "app" // are we running at app level or going through activities seperately
        String DYNAMIC_LAYOUT_APPROACH = "sasha";

        String subjectID = "";
        String subject = "";
        String activity="";
        String ui_version_type = "";
//        String subject = "org.varunverma.INRTrainStatusAlarm";
//        String activity=".UI.Main";
//        String ui_version_type = "largest_font";
        System.out.println("ARGS:" + args.length);
        if (args.length > 0) {
            System.out.println("SUBJECT: " + args[0]);
            System.out.println("Activity: " + args[1]);
            subjectID= args[0];
            subject = args[0];
            activity = args[1];
            ui_version_type= args[2];
        }


        String VHPathPlaceHolder=propConfig.get("subjects_base_path")+ File.separator+subjectID+File.separator+"%s"+File.separator+propConfig.get("device_title");


        String dynamicVH=String.format(VHPathPlaceHolder,propConfig.get("dynamic_vh"));
        String dynamicVHPlaceHolder=dynamicVH+ File.separator+"%s"+File.separator
                + OwlEye.getOriginalActivityName()+".xml";

        String fullDynamicVHPath=String.format(dynamicVHPlaceHolder,ui_version_type);










        String dynamic_layouts_folder=fullDynamicVHPath;
        String refinedDynamicPath = dynamic_layouts_folder+ File.separator+
                "refined/";



        ArrayList<String> activities = new ArrayList<>();
//        String dynamic_path = SALEM.basePath + File.separator + dynamic_layouts_folder +
//                File.separator + subject+"/"+ui_version;
//        dynamic_path =  dynamic_layouts_folder +
//                File.separator + subject+"/"+ui_version;
        File dynamicLayoutFolder = new File(dynamic_layouts_folder);
        for (File f : dynamicLayoutFolder.listFiles()) {
            String fileName = f.getName();
            System.out.println("fileName: " + fileName);
            if (fileName.contains(".png")) {

                continue;  //Ali stopped this while initial code running

            }
            String d = fileName.replace(".xml", "");
            if (d.equalsIgnoreCase(activity)) {
                activities.add(fileName);
            }

        }

        preprocessDynamicFolder(dynamic_layouts_folder, refinedDynamicPath, activities);

    }
//    public static void main_old(String[] args) {
//        String device_label = "Nexus P6";
//        if (device_label.equalsIgnoreCase("Nexus P6")) {
//            Constants.PHONE_DENSITY = 3.5;
//        }
//        String user = "ali";
//        LoadConfig config=new LoadConfig();
//        HashMap<String, String> propConfig = config.readIniConfig("/home/ali/PycharmProjects/TTFIXHelper/config_data.ini");
//        String device_name = propConfig.get("device_name");
//        String screen_res = "Physical size: 1440x2560";  //adb shell  wm size
//        boolean IsThreadExecuted = false;
////        String device_name = "emulator-5554";
////        device_name = "192.168.57.102:5555";
//        String RUNNING_MODE = "activity"; // or "app" // are we running at app level or going through activities seperately
//        String DYNAMIC_LAYOUT_APPROACH = "sasha";
//        String basePath = "/home/testing/AppSet/accessibility/TT_scripts";
//        basePath="/home/ali/OWlRepair/";
//        //String dynamic_layouts_folder = basePath+ "Subjects/dynamic_layouts/";
//
//        String ui_version="default_font";
//        if (user.contains("paul")) {
//            basePath = "/home/paul-sql/touchTarget/";
//        }
//        String activitiesToRun = "config";
//        try {
//            SALEM.initialize(IsThreadExecuted, device_name, screen_res, basePath, RUNNING_MODE,
//                    DYNAMIC_LAYOUT_APPROACH, activitiesToRun, false, false);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        String subject = "com.everydollar.android";
//        String activity = "com.everydollar.android.activities.form.signup.PersonalInfoFormActivity";
//        subject="";
//        activity="";
//        subject = "org.varunverma.INRTrainStatusAlarm";
//         activity=".UI.Main";
//
//        System.out.println("ARGS:" + args.length);
//        if (args.length > 0) {
//            System.out.println("SUBJECT: " + args[0]);
//            System.out.println("Activity: " + args[1]);
//            subject = args[0];
//            activity = args[1];
//            ui_version= args[2];
//
//        }
/////home/ali/OWlRepair/Subjects/dynamic_layouts/AndroidEmulator/largest_font/org.varunverma.INRTrainStatusAlarm
//        ArrayList<String> activities = new ArrayList<>();
//        String dynamic_path = SALEM.basePath + File.separator + dynamic_layouts_folder +
//                File.separator + subject+"/"+ui_version;
//         dynamic_path =  dynamic_layouts_folder +
//                File.separator + subject+"/"+ui_version;
//        File dynamicLayoutFolder = new File(dynamic_path);
//        System.out.println("dynamic_path22: " + dynamic_path);
//        for (File f : dynamicLayoutFolder.listFiles()) {
//            String fileName = f.getName();
//            System.out.println("fileName: " + fileName);
//            if (fileName.contains(".png")) {
//
//                continue;  //Ali stopped this while initial code running
//
//            }
//            String d = fileName.replace(".xml", "");
//            if (d.equalsIgnoreCase(activity)) {
//                activities.add(fileName);
//            }
//
//        }
//
//        preprocessDynamicFolder(dynamic_path, dynamic_path + "/refined/", activities);
//    }

    public static void preprocessDynamicFolder(String dynamicResultPath, String refineRootPath, ArrayList<String> activities) {
        String layoutPath = dynamicResultPath;
        String refinedPath = refineRootPath;

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

        // Remove repetitive XML files
//        File[] allFiles = outputFolder.listFiles();
//        Map<String, Set<String>> file2equalFiles = Maps.newHashMap();
//        Set<String> matchToPrevious = Sets.newHashSet();
//        for (int i = 0; i < allFiles.length; i++) {
//            File controlFile = allFiles[i];
//            String controlFileName = controlFile.getName();
//            if (controlFileName.endsWith(".xml")) {
//                String activity1 = controlFileName.substring(0, controlFileName.lastIndexOf("_"));
////                System.out.println("control: " + controlFileName);
//                // the current file has been matched to 1 previous file before
//                // no need to use it as control file
//                if (matchToPrevious.contains(controlFileName)) {
//                    continue;
//                }
//
//                Set<String> controlFileEqualSet = file2equalFiles.get(controlFileName);
//                if (controlFileEqualSet == null) {
//                    controlFileEqualSet = Sets.newHashSet();
//                }
//
//                for (int j = i + 1; j < allFiles.length; j++) {
//                    File testFile = allFiles[j];
//                    String testFileName = testFile.getName();
//                    if (testFileName.endsWith(".xml")) {
//                        String activity2 = testFileName.substring(0, testFileName.lastIndexOf("_"));
//
//                        if (activity1.equals(activity2)) {
////                            System.out.println("test: " + testFileName);
//                            boolean isEqual = XMLUtils.isXMLContentEqual(controlFile, testFile);
//                            if (isEqual) {
//                                controlFileEqualSet.add(testFileName);
//                                matchToPrevious.add(testFileName);
//                            }
//                        }
//                    }
//                }
//                file2equalFiles.put(controlFileName, controlFileEqualSet);
//            }
//        }
//
//        System.out.println("After removing repetitive files...");
//
//        for (String uniqueFileName : file2equalFiles.keySet()) {
//            System.out.println(uniqueFileName);
//            // Add color information by analyzing screenshots
//            String uniqueFilePath = refinedPath + File.separator + uniqueFileName;
//            Set<String> pngSet = Sets.newHashSet(uniqueFileName.replace(".xml", ".png"));
//            file2equalFiles.get(uniqueFileName).forEach(e -> pngSet.add(e.replace(".xml", ".png")));
//        //    merge.XMLUtils.appendColorInformation(uniqueFilePath, pngSet);
//            // delete those repetitive files
//            Set<String> equalFiles = file2equalFiles.get(uniqueFileName);
//            equalFiles.forEach(f -> new File(refinedPath + File.separator + f).delete());
//        }
    }

}
