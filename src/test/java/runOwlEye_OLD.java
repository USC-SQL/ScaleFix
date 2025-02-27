import org.tinylog.Logger;
import org.xml.sax.SAXException;
import usc.edu.OwlEye.AUCII.Collision;
import usc.edu.OwlEye.AUCII.Cutoff;
import usc.edu.OwlEye.AUCII.Missing;
import usc.edu.OwlEye.OwlConstants;
import usc.edu.OwlEye.OwlEye;
import usc.edu.OwlEye.RunOwl;
import usc.edu.OwlEye.util.Utils;
import usc.edu.SALEM.util.LoadConfig;
import usc.edu.SALEM.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class runOwlEye_OLD {


    public static void main(String[] args) throws SAXException, IOException, InterruptedException {


        HashMap<String, String> propConfig = LoadConfig.getInstance().getConfig_data();
        String subjectID = "";
        String appName = "";
        String activityName = "";
        String activityToRun = "";
        String compileStyle = "";// if 1 then normal apk compile, if 2 then use --use-aapt2
        String appNeedUninstall = "";// if 1 then need uninstall, if 2 then not need
        String appNeedScrolling="";
        String searchOutputSubFolder=""; // if running from python then specify the output folder
        String maintain_original_layout=""; // if 2 then create sw folder
        String versionToRun;
        String[] availableEmulators = null;
        Logger.debug("ARGS:" + args.length);
        OwlEye.addSleepIntervals=false;
//
        //subject_id + ' '+ apk_name + ' ' + activity_name + ' ' + activity_to_run + ' ' + \
        //                   compile_style + ' '+uninstall_first+ ' '+need_scrolling+ ' '+output_folder
//        args=new String[]{"kaba","kaba.yucata.envoy","kaba.yucata.envoy.GameCountActivity","kaba.yucata.envoy.GameCountActivity","1","1","1","2","Feb1_output_DL","DL"};
        if (args.length > 0) {
            Logger.debug("subjectID: " + args[0]);
            subjectID = args[0];
            appName = args[1];
            activityName = args[2];
            activityToRun = args[3];
            compileStyle = args[4];
            appNeedUninstall = args[5];
            appNeedScrolling = args[6];
            maintain_original_layout=args[7];
            searchOutputSubFolder=args[8];
            versionToRun=args[9];
            String  availalabeEmualatorsString=args[10];
            availableEmulators=availalabeEmualatorsString.split("#");

            Logger.debug("DEBUG ARRAY:  subjectID " + args[0] + ", appName " + args[1] + ", activityName " + args[2] + ", activityToRun" + args[3]
                    + ",compileStyle " + args[4] + ",appNeedUninstall " + args[5] + ",searchOutputSubFolder " + args[6]+ ",maintain_original_layout " + args[7]+ ",versionToRun " + args[9]);
            //
            //kaba kaba.yucata.envoy kaba.yucata.envoy.GameCountActivity kaba.yucata.envoy.GameCountActivity 1 1 1 2 Feb1_output_DL DL
            //
//            Logger.debug("DEBUG ARRAY:  subjectID" + args[0] + ", appName " + args[1] + ", activityName " + args[2] + ", activityToRun" + args[3]
//                    + ",compileStyle " + args[4] + ",appNeedUninstall " + args[5] + ",searchOutputSubFolder " + args[6]);
        }
        else {
            subjectID = "vlc";
            String[] subjectsInfo = Utils.readSubjectsCSV(propConfig.get("subjects_csv"), subjectID);
            if (subjectsInfo == null) {
                Logger.error("Subject not found");
                System.exit(1);
            }

                appName = subjectsInfo[0];
            activityName = subjectsInfo[1];
            activityToRun = subjectsInfo[2];
            compileStyle = subjectsInfo[3];
            appNeedUninstall = subjectsInfo[4];
            appNeedScrolling = subjectsInfo[5];
            maintain_original_layout=subjectsInfo[6];// create sw folder
            versionToRun="LL";

            availableEmulators=new String[]{"emulator-5554","emulator-5556","emulator-5558","emulator-5560","emulator-5562","emulator-5564"};
            searchOutputSubFolder="FEB23_Inteliji_"+versionToRun;

        }



        /** set up the emulators **/
        if (availableEmulators==null || availableEmulators.length==0){
            System.out.println("No emulators available");
            System.exit(1);
        }
        else{
            System.out.println("Available emulators: "+availableEmulators.length);
            OwlConstants.NUM_EMULATORS=availableEmulators.length;
            OwlConstants.availableEmulators=new ArrayList<>();
            for (String emulator:availableEmulators){
                System.out.println(emulator);
                OwlConstants.availableEmulators.add(emulator);
            }
        }

        /*** IDENTIFIER TO IDENTIFY THE CURRENT RUN AND CREATE FOLDERS FOR IT ***/
        String current_run_identifier = subjectID + "_" + Util.getFormattedTimestamp(); // USED TO IDENTIFY THE CURRENT RUN


        String VHPathPlaceHolder = propConfig.get("subjects_base_path") + File.separator + subjectID + File.separator + "%s" + File.separator + propConfig.get("device_title");

        String VHPath = String.format(VHPathPlaceHolder, propConfig.get("complete_vh"));
        String deviceName = propConfig.get("device_name");

        String dynamicVH = String.format(VHPathPlaceHolder, propConfig.get("dynamic_vh"));
        String adbPath = propConfig.get("adb_path");
//        OwlConstants.ADB = adbPath + " -s  " + deviceName;
       OwlConstants.setUpADBCommands(adbPath); // set the adb path

        String baseSubjectsPath= propConfig.get("subjects_base_path");
        String mainSearchOutputFolder = propConfig.get("search_output_path")+File.separator+searchOutputSubFolder;
//        String dynamicVH=propConfig.get("subjects_base_path")+ File.separator+propConfig.get("DynamicVH");
        String decompiledAPK = baseSubjectsPath+ File.separator + subjectID + File.separator + propConfig.get("decompiled_apk");
        String issuesPath = baseSubjectsPath+ File.separator + subjectID + File.separator + propConfig.get("issues_path")+File.separator+"%s";
        String candidateRepairsFolders = mainSearchOutputFolder+ File.separator + subjectID +File.separator + current_run_identifier + File.separator + propConfig.get("candidate_repairs_folder");
        String crawlingScript = baseSubjectsPath+ File.separator + subjectID + File.separator + propConfig.get("crawl_python_script");
        String compiledCandidateRepairsFolders = mainSearchOutputFolder+ File.separator + subjectID + File.separator + current_run_identifier + File.separator + propConfig.get("compiled_candidate_repairs_folder") + File.separator + current_run_identifier + "/";
        String finalRepairOutputFolder = mainSearchOutputFolder+ File.separator + subjectID + File.separator + current_run_identifier + File.separator + propConfig.get("final_repair_output_folder") + File.separator + current_run_identifier + "/";
        String crawledRepairsUIAutomatorVH = mainSearchOutputFolder+ File.separator + subjectID + File.separator + current_run_identifier + File.separator + propConfig.get("crawled_repairs_layouts_output_folder") + "/";
        String pythonInterpreter = LoadConfig.getConfig_data().get("python_interpreter");
        String androidSignature = LoadConfig.getConfig_data().get("ANDROID_SIGNATURE".toLowerCase());
        // make sure search_output_folders are created
        Util.createFolder(compiledCandidateRepairsFolders);
        Util.createFolder(finalRepairOutputFolder);
        Util.createFolder(crawledRepairsUIAutomatorVH);


        OwlEye.CAPTURE_GENERATED_UI_MODE = OwlConstants.AUTOMATOR2_UI_CAPTURE_MODE;


        String originalDecompilePath = Util.copyAppFolder(decompiledAPK, candidateRepairsFolders, appName, subjectID);
        //String originalDecompilePath = Util.copyAppFolder(decompiledAPK, candidateRepairsFolders, appName, current_run_identifier);


        OwlEye.init(); // reset everything
        // Start running the approach
        RunOwl tm = new RunOwl();
        String[] approachArgs = {subjectID, appName, activityName, originalDecompilePath, VHPath, dynamicVH, candidateRepairsFolders,
                compiledCandidateRepairsFolders, finalRepairOutputFolder, crawledRepairsUIAutomatorVH, issuesPath, crawlingScript, deviceName,
                compileStyle, activityToRun, appNeedUninstall,versionToRun,maintain_original_layout,pythonInterpreter,androidSignature,baseSubjectsPath,appNeedScrolling};


//        System.out.println("Number of issues: "+OwlEye.getOriginalIssues().size());
//        OwlEye.run_time_reset();


        // we read the isseus and store them in the OwlEye class now we are ready to start the approach
        long startTime = System.nanoTime();
        OwlEye.runtime_map.put("startTime", startTime);
        tm.runApproach(approachArgs);
    }

    private static void readIssues(String issuesPath) {
        //Read the issues files and put them in a map with corresponding objects
        File issuesFolder = new File(issuesPath);
        File[] issuesFiles = issuesFolder.listFiles();
        for (File issueFile : issuesFiles) {
            if (issueFile.isFile()) {
                String issueFileName = issueFile.getName();
                String issue_type = issueFileName.replace(".csv", "");
                extractIssues(issueFile, issue_type);

            }
        }
    }

    private static void extractIssues(File issueFile, String issue_type) {
        //Read the issues files and put them in a map with corresponding objects
        // read csv file
        String[] issues_data = Utils.readCSV(issueFile.getAbsolutePath());
        switch (issue_type) {
            case OwlConstants.CUT_OFF_ISSUE: {
                // read the cut-off issues and store them in the ArrayList of cutoff issues
                for (String issue : issues_data) {
                    Cutoff cutoff = new Cutoff(issue.split(","));
                    OwlEye.originalCutoffIssues.put(cutoff.getProblematicElement(), cutoff);
                }
            }
            break;
            case OwlConstants.COLLISION_ISSUE: {
                for (String issue : issues_data) {
                    Collision collision = new Collision(issue.split(","));
                    OwlEye.originalCollisionIssues.put(collision.getIssueID(), collision);
                }
                break;
            }
            case OwlConstants.MISSING_ISSUE: {
                for (String issue : issues_data) {
                    Missing missing = new Missing(issue.split(","));
                    OwlEye.originalMissingIssues.put(missing.getIssueID(), missing);
                }
                break;
            }

        }

    }

}
