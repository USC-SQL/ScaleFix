package usc.edu.SALEM.util;

import usc.edu.SALEM.Constants;
import usc.edu.SALEM.SALEM;

import java.io.IOException;

import static usc.edu.SALEM.util.Util.*;

public class tempDeleteAndroidFile {


    public static void main(String[] args) throws IOException, InterruptedException {


//        stopAccessibiltyScanner();
//        Thread.sleep(2000);
//        deleteAccessbFiles();
//        Thread.sleep(5000);
//        startAccessibiltyScanner();
//        String cmd = Constants.UIAUTOMATOR_DUMP;
//        runCommand(cmd, null, null);
//
//        Thread.sleep(5000);
//        startAnApp("com.adzenze.FashionDesignFlatSketch");
        SALEM.setCurrentApkPackage("com.adzenze.FashionDesignFlatSketch");
        SALEM.setCurrentActivityName("com.adzenze.FashionDesignFlatSketch.ReadBookActivity");
        try {
            logCatDump();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logCatDump() throws IOException, InterruptedException {
        //     runCommand(Constants.CLEAN_LOGCAT_CMD,null,null); // Clean logCat
        String output = Constants.LOGCAT_FILE_DUMP_PATH + "/" + SALEM.getCurrentApkPackage() + "CORR" + Constants.DETECTION_TOOL_LOGCAT_SUFFIX;
        //output=Constants.LOGCAT_FILE_DUMP_PATH+"/88.txt";
        String filteredIssueoutput = Constants.LOGCAT_FILE_DUMP_PATH + "/" + SALEM.getCurrentApkPackage() + "CORRECT" + Constants.TOUCH_TARGET_ISSUES_OUTPUT_SUFFIX;

//        Util.runCommand(Constants.ADB+" shell 'logcat -d --pid=$(pidof -s com.aziz.accessibilityEval)' /home/ali/AppSet/accessibility/scripts/apks_folder/testIssueList/logcat/aaa.txt",null,null);


        Util.dumpLogCat(output);
        ReadIssues.parseIssuesFromLogCat(output, filteredIssueoutput);

    }

    public static void testLOGCATDump() throws IOException {
        runCommand(Constants.CLEAN_LOGCAT_CMD, null, null); // Clean logCat
        startAnApp("com.adzenze.FashionDesignFlatSketch");

        String destination = Constants.LOGCAT_FILE_DUMP_PATH + "com.adzenze.FashionDesignFlatSketchXX.txt"; // Where to dump the logcat
        String cmd = String.format(Constants.PULL_LOGCAT_CMD, destination);
        cmd = Constants.ADB + " logcat -d > " + destination;
        cmd = Constants.ADB + " logcat -d | grep " + Constants.Accessibility_SERVICE_PACKAGE;
        runCommand(cmd, null, "logcat");
        startAnApp("com.adzenze.FashionDesignFlatSketch");
    }

    public static void testGettingIssues() throws IOException {
        //uninstallA App First
        SALEM.setCurrentApkPackage("com.adzenze.FashionDesignFlatSketch");
        String cmd = String.format(Constants.UNINSTALL_APK, "com.adzenze.FashionDesignFlatSketch");
        runCommand(cmd, null, null);
        installAPK("/home/ali/AppSet/accessibility/scripts/apks_folder/apks_with_ids", "com.adzenze.FashionDesignFlatSketch.apk",SALEM.getCurrentApkPackage());
        deleteIssueFileInEmulator();  // delete csv file to start recording app again
        startAnApp(SALEM.getCurrentApkPackage());  // check if the command is correct and accurate

        /*** Run the cralwing script to reach the correct state of the activity then capture the UI with UI automator***/
        String scriptPath = Constants.CRAWLING_SCRIPTS_PATH + "/" + "com.adzenze.FashionDesignFlatSketch.ReadBookActivity" + Constants.CRAWLING_SCRIPTS_SUFFIX;
        //(2) Once the app has started, we run the crawler script to get to the correct the activity state
        // scripts file naming: currentActivityName+"/"+constant.CRAWLING_SCRIPTS_SUFFIX
        String crawlingScriptFile = scriptPath;

        runCrawlingscript(crawlingScriptFile);
        String destination = "/home/ali/AppSet/accessibility/scripts/apks_folder/testIssueList/";
        pullFileFromEmulator("issueFile", destination);
    }


}
