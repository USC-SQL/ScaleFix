package eval.testSubFunctions;

import usc.edu.SALEM.util.ReadIssues;
import usc.edu.SALEM.Constants;
import usc.edu.SALEM.SALEM;
import usc.edu.SALEM.util.Util;

import java.io.File;
import java.io.IOException;

public class GetIssuesForActivity {


    public static void main(String[] args) throws IOException, InterruptedException {




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
        String basePath = "/home/testing/AppSet/accessibility/TT_scripts";
        if (user.contains("paul")) {
            basePath = "/home/paul-sql/touchTarget/";
        }
        String activitiesToRun = "apks_folder/crawling_scripts/activities_to_scritpt_mapping.csv";
        try {
            SALEM.initialize(IsThreadExecuted, device_name, screen_res, basePath, RUNNING_MODE,
                    DYNAMIC_LAYOUT_APPROACH, activitiesToRun,false,false);
            SALEM.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        String actName = "androdns.android.leetdreams.ch.androdns.DNSFormActivity";
        String apks_with_ids = SALEM.basePath + File.separator + "apks_folder/apks_with_ids/";
        String apkName = "sanity.freeaudiobooks"; // for androzoo apk =package
        //apkName = "com.bytecode.aiodownloader";
        // apkName="com.fitbit.FitbitMobile";
        apkName = "com.groupon";

        System.out.println("ARGS:" + args.length);
        if (args.length > 0) {
            System.out.println("SUBJECT: " + args[0]);
            apkName = args[0];

        }

        SALEM.DEBUG_GET_ISSUES = true;
        SALEM.setCurrentApkPackage(apkName);
//        mFix.setCurrentActivityName("com.blackwhitemeet");
        SALEM.setCurrentActivityName("com.ahorcado.JuegoActivity");


        //cleanLog();
        dumpLogCat(apkName);
        parseIssues(apkName);
        //getIssuesAndLayout(actName,apks_with_ids,apkName);
    }

    private static void parseIssues(String apkName) {
        String output = Constants.LOGCAT_FILE_DUMP_PATH + "/" + apkName + Constants.DETECTION_TOOL_LOGCAT_SUFFIX;
        //output=Constants.LOGCAT_FILE_DUMP_PATH+"/88.txt";
        String filteredIssueoutput = Constants.LOGCAT_FILE_DUMP_PATH + "/" + apkName + Constants.TOUCH_TARGET_ISSUES_OUTPUT_SUFFIX;

        try {
            ReadIssues.parseIssuesFromLogCat(output, filteredIssueoutput); //dumpLogCat will dump the log to a file then we read the file and parse it then write it to the file
        } catch (IOException e) {
            e.printStackTrace();
        }
        Util.parseIssuesFile_LogCat(filteredIssueoutput, apkName, "0"); // On
    }

    private static void dumpLogCat(String apkName) throws IOException, InterruptedException {
        String output = Constants.LOGCAT_FILE_DUMP_PATH + "/" + apkName + Constants.DETECTION_TOOL_LOGCAT_SUFFIX;
        //output=Constants.LOGCAT_FILE_DUMP_PATH+"/88.txt";
        String filteredIssueoutput = Constants.LOGCAT_FILE_DUMP_PATH + "/" + apkName + "_99" + Constants.TOUCH_TARGET_ISSUES_OUTPUT_SUFFIX;

        Util.dumpLogCat(output);
    }

    public static void cleanLog() throws IOException {
        Util.runCommand(Constants.CLEAN_LOGCAT_CMD, null, null); // Clean logCat
    }

    public static void getIssuesAndLayout(String actName, String apkPath, String apkName) throws IOException, InterruptedException {

        /*** Run the cralwing script to reach the correct state of the activity then capture the UI with UI automator***/
//        String scriptPath = Constants.CRAWLING_SCRIPTS_PATH + "/" + actName + Constants.CRAWLING_SCRIPTS_SUFFIX;
//        String crawlingScriptFile = scriptPath;
//        Util.runCommand(Constants.CLEAN_LOGCAT_CMD, null, null); // Clean logCat
//
//
//        Util.runCrawlingscript(crawlingScriptFile);
//        //     runCommand(Constants.CLICK_HOME_BUTTON,null,null);
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        Util.prepareAppToGetToCorrectStateLOGCAT(actName, apkPath, apkName);
        String destination = Constants.LOGCAT_FILE_DUMP_PATH; // Where to dump the logcat

        String output = Constants.LOGCAT_FILE_DUMP_PATH + "/" + apkName + Constants.DETECTION_TOOL_LOGCAT_SUFFIX;
        //output=Constants.LOGCAT_FILE_DUMP_PATH+"/88.txt";
        String filteredIssueoutput = Constants.LOGCAT_FILE_DUMP_PATH + "/" + apkName + "_99" + Constants.TOUCH_TARGET_ISSUES_OUTPUT_SUFFIX;

        Util.dumpLogCat(output); //Dump the logcat to the the logcat_file_dump_path
        ReadIssues.parseIssuesFromLogCat(output, filteredIssueoutput); //dumpLogCat will dump the log to a file then we read the file and parse it then write it to the file
        Util.parseIssuesFile_LogCat(filteredIssueoutput, apkPath, "0"); // Once this is done we have stored the issues in the map and we have already stored

        String newLayoutFolder = Util.captureUpdatedDynamicUI(actName, apkPath, apkName);
//        String chromosomedChrawled=newLayoutFolder.split("/refined")[0];
//        chromosome.setCrawledPath(chromosomedChrawled);
        //Util.updateLayoutWithNewValues(newLayoutFolder);
    }
}
