import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import org.tinylog.Logger;
import usc.edu.OwlEye.GA.GARunner;
import usc.edu.OwlEye.OwlConstants;
import usc.edu.OwlEye.OwlEye;
import usc.edu.OwlEye.RunOwl;
import usc.edu.OwlEye.UIModels.ConstructUIModels;
import usc.edu.OwlEye.VH.UI;
import usc.edu.OwlEye.VHTree.XMLUtils;
import usc.edu.OwlEye.fitness.FitnessFunction;
import usc.edu.OwlEye.fitness.MissingElementsObjective;
import usc.edu.OwlEye.util.Utils;
import usc.edu.SALEM.Constants;
import usc.edu.SALEM.SALEM;
import usc.edu.SALEM.util.LoadConfig;
import usc.edu.SALEM.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static usc.edu.OwlEye.OwlConstants.setGrepActivityNameCommand;

public class testFitnessFunction {
    public static void main(String[] args) throws IOException {


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
        Logger.debug("ARGS:" + args.length);
        OwlEye.addSleepIntervals=false;
//
        //subject_id + ' '+ apk_name + ' ' + activity_name + ' ' + activity_to_run + ' ' + \
        //                   compile_style + ' '+uninstall_first+ ' '+need_scrolling+ ' '+output_folder
//        args=new String[]{"kaba","kaba.yucata.envoy","kaba.yucata.envoy.GameCountActivity","kaba.yucata.envoy.GameCountActivity","1","1","1","2","Feb1_output_DL","DL"};
        String newLayoutFolder=null;
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
            Logger.debug("DEBUG ARRAY:  subjectID " + args[0] + ", appName " + args[1] + ", activityName " + args[2] + ", activityToRun" + args[3]
                    + ",compileStyle " + args[4] + ",appNeedUninstall " + args[5] + ",searchOutputSubFolder " + args[6]+ ",maintain_original_layout " + args[7]+ ",versionToRun " + args[9]);
            //
            //kaba kaba.yucata.envoy kaba.yucata.envoy.GameCountActivity kaba.yucata.envoy.GameCountActivity 1 1 1 2 Feb1_output_DL DL
            //
//            Logger.debug("DEBUG ARRAY:  subjectID" + args[0] + ", appName " + args[1] + ", activityName " + args[2] + ", activityToRun" + args[3]
//                    + ",compileStyle " + args[4] + ",appNeedUninstall " + args[5] + ",searchOutputSubFolder " + args[6]);
        } else {
            subjectID = "cnet1";
             newLayoutFolder = "/home/aalotaib/OWlRepair/SearchOutput/April7_collision_LL/cnet1/cnet1_04-08-2023-05-41-56-PM/finalRepairOutput/cnet1_04-08-2023-05-41-56-PM/" +
                    File.separator;
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

            searchOutputSubFolder="FEB23_Inteliji_LL"+versionToRun;

        }
//        String cmd =  String.format(OwlConstants.UNINSTALL_APK , appName); // the current package name was set in the readIssue method
//        System.out.println(cmd);
//        Logger.debug("uninstalling the apk");
//        runCommand(cmd, null, null);


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
        runApproach(approachArgs);


















        /***** START TESTING FITNESS FUNCTION *****/
        FitnessFunction ff = new FitnessFunction();

        try {
            ff.calculateFitnessScore(null, newLayoutFolder,true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void runApproach(String[] args) throws IOException {
        //        String[] approachArgs = {subjectID,appName,activityName,originalDecompilePath,VHPath,dynamicVH,candidateRepairsFolders,
//                compiledCandidateRepairsFolders,finalRepairOutputFolder,issuesPath,crawlingScript};


        // run search
        String csvFileName = "Solution" + Constants.POPULATION_SIZE + "-" + Constants.NUM_GENERATIONS + "_" +
                Constants.WEIGHT1 + "-" + Constants.WEIGHT2 + "-" + Constants.WEIGHT4 + ".csv";
//        SALEM.SOLUTION_CSV_OUTPUT = SALEM.basePath+"/apks_folder/final_chromosome_fixes/" + csvFileName;
        SALEM.DYNAMIC_LAYOUT_APPROACH="sasha";

        setGrepActivityNameCommand();
        OwlEye.setOriginalAppID(args[0]);
        OwlEye.setOriginalApkName(args[1]);
        OwlEye.setOriginalActivityName(args[2]);
        OwlEye.setOriginalDecompiled(args[3]);
        OwlEye.setCompileOutputPath(args[7]);
        OwlEye.setFinalRepairOutputFolder(args[8]);
        OwlEye.setCrawledDynamicRepairVHPath(args[9]);

        OwlEye.setPythonCrawlingScript(args[11]);
        OwlEye.setDeviceName(args[12]);
        OwlEye.setMergePath(args[4]);
        OwlEye.setOriginalDynamicLayoutFilePath(args[5]);
        OwlEye.setAppCompileStyle(args[13]);
        OwlEye.setActivityToRunWithUIAutomator(args[14]);
        String appNeedUninstall=args[15];
        OwlConstants.CURRENT_SCALING_VERSION_FOLDER=args[16]; // either DL,LD,or LL
        String MaintainOriginalLayout=args[17];
        OwlEye.setPythonInterpreter(args[18]);
        Utils.pythonInterpreter =args[18];

        OwlEye.setAndroidSignature(args[19]);
        OwlEye.setBaseSubjectPath(args[20]);
        String issuesPath=String.format(args[10], OwlConstants.CURRENT_SCALING_VERSION_FOLDER);;
        if(appNeedUninstall.equals("2")){
            OwlEye.setAppNeedUninstall(true);}
        else{
            OwlEye.setAppNeedUninstall(false);
        }
        boolean createOriginalLayoutFolder=false;
        if(MaintainOriginalLayout.equals("2") && !OwlConstants.CURRENT_SCALING_VERSION_FOLDER.equals("DL")){ // because if it is DL, we don't need to create the folder since display size does not change
            createOriginalLayoutFolder=true;
        }

        OwlEye.setAppNeedScrolling(args[21]);
        long startTime = System.nanoTime();

        // Ensure the screen size and density is according to the version being tested
        Utils.setScreenSizeAndDensity(OwlConstants.CURRENT_SCALING_VERSION_FOLDER);


        // Read the issues
        Logger.trace("Number of cutoff issues: " + OwlEye.getOriginalCutoffIssues().size());
        Logger.trace("Number of collision issues: " + OwlEye.getOriginalCollisionIssues().size());

        // Owl. Before we run the search we should prepare two instances of the original default and large, store the issues and handle it
        //We should read Issues from the original UI that we computed earlier.

        // 1- read XML for original-default and for original_largest
        String baseVHPath= OwlEye.getMergePath();
        //1a read default
        String VHPlaceHolder=baseVHPath+ File.separator+"%s"+File.separator
                +OwlEye.getOriginalActivityName()+".xml";


        String originalDefaultXMLPath=String.format(VHPlaceHolder,OwlConstants.DEFAULT_FONT_FOLDER);
        String originalDefaultPNGPath=originalDefaultXMLPath.replace(".xml",".png");

        String originalLargestXMLPath=String.format(VHPlaceHolder,OwlConstants.CURRENT_SCALING_VERSION_FOLDER);
        String originalLargestPNGPath=originalLargestXMLPath.replace(".xml",".png");
        if(!new File(originalDefaultXMLPath).exists()){
            Logger.error("No DD VH exists for "+OwlEye.getOriginalActivityName());
            System.exit(1);
        }
        if(!new File(originalLargestXMLPath).exists()){
            Logger.error("No " + OwlConstants.CURRENT_SCALING_VERSION_FOLDER+ " VH exists for "+OwlEye.getOriginalActivityName());
            System.exit(1);
        }
        XMLUtils defaultOrgVH= new XMLUtils(originalDefaultXMLPath);
        Map<String, Node<DomNode>> baselineElements = MissingElementsObjective.getElementSetFromDomTree(defaultOrgVH.getRoot());
        XMLUtils largestOrgVH= new XMLUtils(originalLargestXMLPath);
        UI originalDefaultUI=new UI("original_default",defaultOrgVH,originalDefaultXMLPath,originalDefaultPNGPath);

        UI originalLargestUI=new UI("original_largest",largestOrgVH,originalLargestXMLPath,originalLargestPNGPath);
        OwlEye.setOriginalDefaultUI(originalDefaultUI); //originalDefaultUI
        OwlEye.setOriginalLargestUI(originalLargestUI); //originalLargestUI

        String tempToTestLarge=OwlEye.getOriginalDynamicLayoutFilePath();
        String default_font_size_VH= baseVHPath+ File.separator+OwlConstants.DEFAULT_FONT_FOLDER +File.separator
                +OwlEye.getOriginalActivityName()+".xml";
        String largest_font_size_VH= tempToTestLarge+File.separator+OwlConstants.CURRENT_SCALING_VERSION_FOLDER +File.separator
                +"refined/";

        originalDefaultUI.calculateMaxAreaForAllElements();
        originalDefaultUI.calculateNoOfLeaveNodes();
        int noOfLeaveNodes=originalDefaultUI.getNoOfLeaveNodes();
        double totalArea=originalDefaultUI.getTotalArea()/OwlConstants.PHONE_DENSITY;
        double height=originalDefaultUI.getHeight()/OwlConstants.PHONE_DENSITY;
        double width=originalDefaultUI.getWidth()/OwlConstants.PHONE_DENSITY;


        originalLargestUI.calculateMaxAreaForAllElements();
        originalLargestUI.calculateNoOfLeaveNodes();
        double height2=originalLargestUI.getHeight()/OwlConstants.PHONE_DENSITY;
        double width2=originalLargestUI.getWidth()/OwlConstants.PHONE_DENSITY;






        // build UI models (all of them)

        // get max area for all elements in the original UI
        //
        originalDefaultUI.calculateMaxAreaForAllElements();
        ConstructUIModels constructUIModels = new ConstructUIModels(originalDefaultUI);
        constructUIModels.constructSRG();
        constructUIModels.constructVSRG();
        constructUIModels.constructTRG();
        constructUIModels.constructSPLRG();
        constructUIModels.constructWRG();
//        System.exit(1);

        // After we define everything, it is a good time to revisist the issues and add the problematic DomNodes

        //1- Cutoff issues

//        XMLUtils.getInstance(default_font_size_VH);
        GARunner mi = new GARunner();
        //   prepareAppToGetToCorrectState();
//

        long endTime = System.nanoTime();


        //   System.out.println("-------------------------------------------------------------------------------");


    }
}
