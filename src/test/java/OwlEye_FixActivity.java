import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;
import usc.edu.SALEM.Constants;
import usc.edu.SALEM.RunSALEM;
import usc.edu.SALEM.SALEM;
import usc.edu.SALEM.VHTree.XMLUtils;
import usc.edu.SALEM.fitness.FitnessFunction;
import usc.edu.SALEM.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

//import static TTFix.TestTTFIX_ACTIVITIES.*;

public class OwlEye_FixActivity {
    public static Logger logger = Logger.getLogger(OwlEye_FixActivity.class.getName());


    private static final ArrayList<String> ListOfActivities = new ArrayList<>();

    public static void main(String[] args) throws SAXException, IOException, InterruptedException {
        /*** general setup
         * You have to set the paths and everything in SALEM class***/


        /*Change based on your environment*/
        String screen_res = "Physical size: 1440x2560";  //adb shell  wm size  // It can also be automatically obtained
        String device_name = "192.168.56.101:5555";
        String device_label = "Nexus P6";
        if (device_label.equalsIgnoreCase("Nexus P6")) {
            Constants.PHONE_DENSITY = 3.5;
        }
        String user = "ali";  // for debugging, keep it for ALi
        String basePath = "/home/ali/TTFIX_DATA/ASE2021";
        if (user.contains("paul")) {
            basePath = "/home/paul-sql/touchTarget/";
        }


        /* Change based on the app you run*/
        System.out.println("ARGS:" + args.length);
        if (args.length > 0) {
            System.out.println("SUBJECT: " + args[0]);
            String activity = args[0];
            String apkName = args[1];
            ListOfActivities.add(activity);
            ListOfActivities.add(apkName);
        } else {
            ListOfActivities.add("com.groundspeak.geocaching.intro.activities.LoginActivity");

            ListOfActivities.add("com.groundspeak.geocaching.intro");
            System.exit(1);
        }

        boolean IsThreadExecuted = false;
        String RUNNING_MODE = "activity"; // or "app" // are we running at app level or going through activities seperately
        String DYNAMIC_LAYOUT_APPROACH = "sasha";

        String activitiesToRun = "/activities_to_scritpt_mapping.csv";
        boolean increaseTextSize=false;
        boolean considerHeightWidthRatio=true; // when we change height or width we check the other
        SALEM.initialize(IsThreadExecuted, device_name, screen_res, basePath, RUNNING_MODE,
                DYNAMIC_LAYOUT_APPROACH, activitiesToRun,increaseTextSize,considerHeightWidthRatio);


        String originalStaticFiles = SALEM.basePath + File.separator + "SUBJECTS/decompiled_apks/";
        String crawledFiles = SALEM.basePath + File.separator + "VHs/dynamic_layouts/";
//        String refineDynamicPath = mFix.basePath+File.separator+"apks_folder/dynamic_layouts/";
        String mergedLayoutPath = SALEM.basePath + File.separator + "VHs/merged_layouts/";
//        String issuesListFolder = mFix.basePath+File.separator+"apks_folder/issues_list/";
        String issuesListFolder = SALEM.Detection_Tool_APK_PATH;
        String copiedAPKPath = SALEM.basePath + File.separator + "apks_folder/repaired_apks_search/";
//        String guianalzyerPath = mFix.basePath + File.separator + "apks_folder/guianalzyer/";





        try {

            resetEverything();
            String activity = ListOfActivities.get(0);
            String subject = ListOfActivities.get(1);
            SALEM.generateNewMerged = false;
            String apkCrawledFiles = crawledFiles + File.separator + subject;
            String apkMergedLayoutFiles = mergedLayoutPath + File.separator + subject;
            String apkIssuesListFiles = issuesListFolder;//+ File.separator + subject+ Constants.TOUCH_TARGET_ISSUES_OUTPUT_SUFFIX;
//            String guianalzyerAPKPath = guianalzyerPath + File.separator + subject;



            RunSALEM tm = new RunSALEM();
            SALEM.final_fix_output = SALEM.base_final_fix_output + "/" + activity + "/" + subject;
            String originalDecompilePath = copyFolder(originalStaticFiles, copiedAPKPath, subject, activity);
            String[] testXbiArgs = {subject, originalDecompilePath, apkCrawledFiles,
                    apkMergedLayoutFiles, apkIssuesListFiles};
            SALEM.setCurrentActivityName(activity);
            SALEM.setOriginalDecompiled(originalDecompilePath);

            long startTime = System.nanoTime();
            SALEM.runtime_map.put("startTime", startTime);
            tm.runApproach(testXbiArgs);
            logger.info("Final Solution :" + SALEM.finalChromosome.get(0));// chroromse id
            logger.info("Final Solution :" + SALEM.finalChromosome.get(1));// chroromse genes
            logger.info("Final Solution :" + SALEM.finalChromosome.get(2));// chroromse fitness

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            System.exit(0);
        }


    }


    static void resetEverything() {
        // Reset mFix variables
        SALEM.reset();
        // reset chromosome
        FitnessFunction.setFitnessCalls(0);
        XMLUtils.resetInstance();
    }



    public static String copyFolder(String originalStaticFiles, String copiedAPKPath, String apkName, String activity) {
//        String patchID = "_original_" + mfix.util.Util.getFormattedTimestamp();
        String patchID = Util.getFormattedTimestamp();

        SALEM.setCopiedApkFileName(apkName + patchID);

        String appDecompiledFolder = originalStaticFiles + File.separator + apkName;
        String appDecombiledDestination = copiedAPKPath + File.separator + activity + patchID;

        File directory = new File(appDecombiledDestination);
        if (!directory.exists()) {
            directory.mkdirs();
            // If you require it to make the entire directory path including parents,
            // use directory.mkdirs(); here instead.
        }
        try {
            // Copy source directory into destination directory
            // including its child directories and files. When
            // the destination directory is not exists it will
            // be created. This copy process also preserve the
            // date information of the file.
            File srcDir = new File(appDecompiledFolder);
            File destDir = new File(appDecombiledDestination);
            FileUtils.copyDirectory(srcDir, destDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return appDecombiledDestination;
    }

}
