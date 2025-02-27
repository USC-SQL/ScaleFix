package usc.edu.OwlEye;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import org.tinylog.Logger;
import org.xml.sax.SAXException;
import usc.edu.OwlEye.AUCII.Collision;
import usc.edu.OwlEye.AUCII.Cutoff;
import usc.edu.OwlEye.AUCII.Missing;
import usc.edu.OwlEye.GA.GARunner;
import usc.edu.OwlEye.UIModels.ConstructUIModels;
import usc.edu.OwlEye.VH.UI;
import usc.edu.OwlEye.VHTree.XMLUtils;
import usc.edu.OwlEye.fitness.MissingElementsObjective;
import usc.edu.OwlEye.util.Utils;
import usc.edu.SALEM.Constants;
import usc.edu.SALEM.SALEM;
//import usc.edu.SALEM.VHTree.XMLUtils;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static usc.edu.OwlEye.OwlConstants.setGrepActivityNameCommand;
import static usc.edu.OwlEye.util.Utils.*;

public class RunOwl {







    public void runApproach(String[] args) throws SAXException, IOException, InterruptedException {
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
        String DD_refined= OwlEye.getOriginalDynamicLayoutFilePath()+File.separator+OwlConstants.DEFAULT_FONT_FOLDER +File.separator
                +"refined/"+OwlEye.getOriginalActivityName()+".xml";
        OwlEye.REFINED_DYNAMIC_DD_LAYOUT_FILE_PATH=DD_refined;
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
        readIssues(issuesPath);
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



        // if createOriginalLayoutFolder is true, we should create a folder for the original layout
       if(createOriginalLayoutFolder) {
           Utils.CreateSWLayout(OwlEye.getOriginalAppID(), OwlEye.getOriginalApkName(), OwlEye.getOriginalDecompiled(),
                   originalDefaultXMLPath);
       }




        // build UI models (all of them)

        // get max area for all elements in the original UI
        //
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
       ConstructUIModels constructUIModels = new ConstructUIModels(originalDefaultUI);
        constructUIModels.constructSRG();
        constructUIModels.constructVSRG();
        constructUIModels.constructTRG();
        constructUIModels.constructSPLRG();
        constructUIModels.constructWRG();
//        System.exit(1);

        // After we define everything, it is a good time to revisist the issues and add the problematic DomNodes

        //1- Cutoff issues
        AddProblematicNodesToIssues(defaultOrgVH);

//        XMLUtils.getInstance(default_font_size_VH);
        GARunner mi = new GARunner();
     //   prepareAppToGetToCorrectState();
//
        try {

            // mi.runIterator();
            mi.runIterator2();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime = System.nanoTime();


     //   System.out.println("-------------------------------------------------------------------------------");


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

    private void AddProblematicNodesToIssues(XMLUtils defaultOrgVH) {
        // pase the issues and read the ids of nodes and add them as DomNodes to the issues
        HashMap<String, Cutoff> foundCutoffIssues = new HashMap<>();
        HashMap<String,Collision> foundCollisionIssues = new HashMap<>();
        HashMap<String,Missing> foundMissingIssues = new HashMap<>();
      //  The hasmaps above just to remove the issues that are not found in the original layout
        // 1- cutoff issues

        HashMap<String, Cutoff> cutoffIssues = OwlEye.getOriginalCutoffIssues();
        for (Map.Entry<String, Cutoff> issue : cutoffIssues.entrySet()) {
            String problematicNodeId = issue.getKey();
            Cutoff cutoff = issue.getValue();
            Node<DomNode> found = defaultOrgVH.searchByID_T(problematicNodeId, null);
            if(found!=null){
                cutoff.addProblematicElement(found);
                foundCutoffIssues.put(problematicNodeId,cutoff);
            }
            else{
                System.out.println("Cutoff issue not found in the original layout");
            }
//            cutoff.addProblematicElement(found);

        }

        // 2- collision issues
        HashMap<String, Collision> collidingIssues = OwlEye.getOriginalCollisionIssues();
        for (Map.Entry<String, Collision> issue : collidingIssues.entrySet()) {
            Collision collisionIssue = issue.getValue();
            ArrayList<String> elementsIDs = collisionIssue.getProblematicElements();
            for (String elementID : elementsIDs) {
                Node<DomNode> found = defaultOrgVH.searchByID_T(elementID, null);
                if(found!=null){
                    collisionIssue.addProblematicElement(found);
                    foundCollisionIssues.put(collisionIssue.getIssueID(),collisionIssue);
                }
                else{
                    System.out.println("Collision issue not found in the original layout");
                }
//                collisionIssue.addProblematicElement(found);
            }


        }

        HashMap<String, Missing> missingIssues = OwlEye.getOriginalMissingIssues();
        for (Map.Entry<String, Missing> issue : missingIssues.entrySet()) {
            Missing missingIssue = issue.getValue();
            ArrayList<String> elementsIDs = missingIssue.getProblematicElements();
            for (String elementID : elementsIDs) {
                Node<DomNode> found = defaultOrgVH.searchByID_T(elementID, null);
                if(found!=null){
                    missingIssue.addProblematicElement(found);
                    foundMissingIssues.put(missingIssue.getIssueID(),missingIssue);
                }
                else{
                    System.out.println("Missing issue not found in the original layout");
                }
//                missingIssue.addProblematicElement(found);
            }


        }

    }

    public static String prepareAppToGetToCorrectState() throws IOException, InterruptedException {


        /*** Run the cralwing script to reach the correct state of the activity then capture the UI with UI automator***/
//        ArrayList<String> actScriptInfo = OwlEye.getPythonCrawlingScript();
        String pythonScript = OwlEye.getPythonCrawlingScript();
//        String activityToRunWithScript = actScriptInfo.get(1);
//        String scriptFileName = actScriptInfo.get(2);
        String args = OwlEye.getDeviceName();

        String packageName = OwlEye.getOriginalApkName();
        String actName = OwlEye.getOriginalActivityName() ;

        args =   args+ " "+ packageName + " " + actName;


        runPythonScript(pythonScript,args,pythonScript.replace("navigate_to_state.py",""),null);


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String activityName = getDeviceCurrentRunningActivityName();


        //(2) dump the layout using ui automator to the phone
        if (activityName.equalsIgnoreCase(OwlEye.getOriginalActivityName())) {  // if it is NOT the correct activity
            return activityName;
        } else {
            return null;  // Not Correct
        }



    }
}
