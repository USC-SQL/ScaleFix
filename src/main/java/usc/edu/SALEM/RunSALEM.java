package usc.edu.SALEM;

import usc.edu.SALEM.GA.GARunner;
//import mfix.merge.Merger_TT;
import usc.edu.SALEM.util.Util;
import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class RunSALEM {
    // public  static Logger logger = LogManager.getLogger(main.class);
    public static Logger logger = Logger.getLogger(RunSALEM.class.getName());
    public static String[] appActivities;  // manually list problematic activitites | just to test segmentation

    public static void main(String[] args) throws SAXException, IOException {

        String device_name = "Nexus P6";

        if (device_name.equalsIgnoreCase("Nexus P6")) {
            Constants.PHONE_DENSITY = 3.5;
        }
        HashMap<String, Boolean> subjectsMaps = new HashMap<>(); // apkname, needto generate new merge?
        Util.runCommand(Constants.ADB + " root", null, null);
//        List<String> subjectsList = new ArrayList<>();


//        subjectsMaps.put("com.adzenze.FashionDesignFlatSketch", false);
//          subjectsMaps.put("com.bytecode.aiodownloader",false);
//        subjectsMaps.put("com.daily.calling", false);
//        subjectsMaps.put("arity.calculator", false);
//        subjectsMaps.put("ch.logixisland.anuto", false);
//        subjectsMaps.put("com.android.keepass", false);
//           subjectsMaps.put("com.danhasting.radar",false);
//        subjectsMaps.put("ca.rmen.android.frenchcalendar", false);
//            subjectsMaps.put("com.bytesforge.linkasanote.mock",false);
//            subjectsMaps.put("com.ahorcado",false);
//           subjectsMaps.put("androdns.android.leetdreams.ch.androdns",false);
//            subjectsMaps.put("com.crmdev.safadometro",false);
//        subjectsMaps.put("com.cochibo.gpsstatus", false);// error
//        subjectsMaps.put("laki_dev.hitclub.hitprojectctrirlv", false);
//        subjectsMaps.put("cz.test.calculator", false);
//        subjectsMaps.put("crisar.aege.com.leydeohm", false);  // not good fix
//         subjectsMaps.put("uk.co.bitethebullet.android.token", false);
//        subjectsMaps.put("uk.co.yahoo.p1rpp.calendartrigger", false);
//            subjectsMaps.put("com.americanexpress.android.acctsvcs.us",false);
//        subjectsMaps.put("com.activehours", false);


//        subjectsMaps.put("com.alibaba.aliexpresshd",false);
//        subjectsMaps.put("com.amolatina",false);
//        subjectsMaps.put("com.appxy.pocketexpense",false);
//        subjectsMaps.put("com.bisoft.live.weather", false);
//        subjectsMaps.put("com.callapp.contacts",false);
//        subjectsMaps.put("com.carmax.carmax",false);
//        subjectsMaps.put("com.chase.sig.android",false);
//        subjectsMaps.put("com.catchingnow.tinyclipboardmanager",false);
//        subjectsMaps.put("com.colorsnap",false);
//        subjectsMaps.put("com.comcast.cvs.android",false);
//        subjectsMaps.put("com.contorra.golfpad",false);
//        subjectsMaps.put("com.cvs.launchers.cvs",false);
//        subjectsMaps.put("com.droid27.d3flipclockweather",false);
//        subjectsMaps.put("com.discovery.idsgo",false);
//        subjectsMaps.put("com.duckduckgo.mobile.android",false);
//        subjectsMaps.put("com.ehi.enterprise.android",false);
//        subjectsMaps.put("com.everydollar.android",false);
//        subjectsMaps.put("com.creditsesame", false);
//        subjectsMaps.put("sanity.freeaudiobooks",false);
//        subjectsMaps.put("ca.rmen.android.poetassistant",false);
//        subjectsMaps.put("com.fitbit.FitbitMobile",false);
//        subjectsMaps.put("",false);
//        subjectsMaps.put("",false);
//        subjectsMaps.put("",false);

        
        String originalStaticFiles = SALEM.basePath + File.separator + "apks_folder/decompiled_apks/";
        String crawledFiles = SALEM.basePath + File.separator + "apks_folder/dynamic_layouts/";
//        String refineDynamicPath = mFix.basePath+File.separator+"apks_folder/dynamic_layouts/";
        String mergedLayoutPath = SALEM.basePath + File.separator + "apks_folder/merged_layouts/";
//        String issuesListFolder = mFix.basePath+File.separator+"apks_folder/issues_list/";
        String issuesListFolder = SALEM.Detection_Tool_APK_PATH;
        String copiedAPKPath = SALEM.basePath + File.separator + "apks_folder/repaired_apks_search/";
        String guianalzyerPath = SALEM.basePath + File.separator + "apks_folder/guianalzyer/";

        HashMap<String, ArrayList<String>> activitiesToCrawlingSctipt = readingCrawlingScriptMapping(SALEM.ACTIVITIES_TO_PYTHON_SCRIPTS_FILEPATH);
        SALEM.setActivitiesToCrawlingScripts(activitiesToCrawlingSctipt);
        //TTFix.setDeviceName("emulator-5554");

        SALEM.setDeviceName("192.168.57.101:5555"); //Genymotion
        int NUMBER_OF_RUNS = 1;

        SALEM.segmentsLogoutPath = mergedLayoutPath + File.separatorChar + "log_" + Util.getFormattedTimestamp() + ".txt";  // To output the segments to file to debug the segmentation algorithm

        for (int i = 0; i < NUMBER_OF_RUNS; i++) {

            for (String subject : subjectsMaps.keySet()) {
                System.err.println("\n" + subject);
                if (Constants.TESTING_SEGMENTS) {
                    appActivities = new String[]{
                            "com.android.keepass.fileselect.FileSelectActivity"}
                    ;// manually listing activities with issues
                }

                // This is the apk folder not the layout
//                String apkStaticFiles = originalStaticFiles + File.separator + subject;
                SALEM.generateNewMerged = subjectsMaps.get(subject);
                String apkCrawledFiles = crawledFiles + File.separator + subject;
                String apkMergedLayoutFiles = mergedLayoutPath + File.separator + subject;
                String apkIssuesListFiles = issuesListFolder;//+ File.separator + subject+ Constants.TOUCH_TARGET_ISSUES_OUTPUT_SUFFIX;
                String guianalzyerAPKPath = guianalzyerPath + File.separator + subject;
                RunSALEM tm = new RunSALEM();
                SALEM.final_fix_output = SALEM.base_final_fix_output + "/" + subject;
                String originalDecompilePath = copyFolder(originalStaticFiles, copiedAPKPath, subject);
                String[] testXbiArgs = {subject, originalDecompilePath, apkCrawledFiles, apkMergedLayoutFiles, guianalzyerAPKPath, apkIssuesListFiles};


//        gui.readStyles("/home/testing/AppSet/accessibility/TT_scripts/apks_folder/" +
//                "decompiled_apks/com.activehours/res/values/styles.xml", false);
                SALEM.setOriginalDecompiled(originalDecompilePath);
//                GUIAnalyzer gui = new GUIAnalyzer();
//                gui.readPublicXML();
//                gui.readStyles();
//                HashMap<String, HashMap<String, Integer>> s = gui.sysRGeneralIdMap;
//                HashMap<String, HashMap<String, String>> inv = gui.invSysRGeneralIdMap;
//                gui.readDimens();
//
//                HashMap<String, CallGraphNode> st = gui.rStyleAndStyleNode;
//                HashMap<String, String> chdParent = gui.childStyleAndParent;
//                HashMap<String, CallGraphNode> syst = gui.sysRStyleAndStyleNode;
//                HashMap<String, String> syschdParent = gui.sysChildStyleAndParent;
//                HashMap<Integer, String> intDimn = gui.intAndDimenValues;
//                HashMap<String, String> rDimn = gui.rDimenAndDimenValues;
//                Merger_TT.guiAnalyzer = gui;
//                CallGraphNode value=st.get("SignupInputField");
//                StyleNode styleN=(StyleNode) value;
//                for (String it:styleN.getItems().keySet()
//                     ) {
//                    String x=it;
//                    System.out.println("X :"+x);
//                }
//                for (Map.Entry<String,CallGraphNode> entry : st.entrySet()) {
//                    String name = entry.getKey();
//                    CallGraphNode value = entry.getValue();
//                    StyleNode styleN=(StyleNode) value;
//                    styleN.getItems()
//                    if (value == null) {
//                        System.out.println(name);
//                    }
//                }
                tm.runApproach(testXbiArgs);
                //SignupInputField
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static HashMap<String, ArrayList<String>> readingCrawlingScriptMapping(String activitiesToPythonScripts) {
        /*** reading the mapping to all the acripts***/
        HashMap<String, ArrayList<String>> activitiesMapping = new HashMap<>();
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        int i = 0;
        try {

            br = new BufferedReader(new FileReader(activitiesToPythonScripts));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                if (i == 0) {
                    i = 1;
                    continue;
                }
                String[] activityInfo = line.split(cvsSplitBy);
//                System.out.println(activityInfo);
                String activityName = activityInfo[0].trim();
                String activityToRunWithScript = activityInfo[1].trim();
                String scriptFileName = activityInfo[2].trim();
                String type = null;
                String content = null;
                String extra_content = null;

                if (activityInfo.length > 3) {
                    type = activityInfo[3];

                    if (activityInfo.length > 4) {
                        content = activityInfo[4];
                    }
                    if (activityInfo.length > 5) {
                        extra_content = activityInfo[5];
                    }
                }

                ArrayList<String> act = new ArrayList<>();
                act.add(activityName);
                act.add(activityToRunWithScript);
                act.add(scriptFileName);
                act.add(type);
                act.add(content);
                act.add(extra_content);

                activitiesMapping.put(activityName, act); // Add the issue to the list of issues

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }

        return activitiesMapping;
    }

    public static String copyFolder(String originalStaticFiles, String copiedAPKPath, String apkName) {
//        String patchID = "_original_" + mfix.util.Util.getFormattedTimestamp();
        String patchID = Util.getFormattedTimestamp();

        SALEM.setCopiedApkFileName(apkName + patchID);

        String appDecompiledFolder = originalStaticFiles + File.separator + apkName;
        String appDecombiledDestination = copiedAPKPath + File.separator + apkName + patchID;

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

    public void runApproach(String[] args) throws SAXException, IOException {
//        subject,originalDecompilePath, apkCrawledFiles,apkMergedLayoutFiles,guianalzyerPath,apkIssuesListFiles
        // args[0] -> apk_name
        // args[1] -> originalDecompilePath
        //args[2] -> originalDynamicLayout
        //args[3] -> apkMergedLayoutFiles

        //args[4] -> guianalzyerPath
        // //args[5] -> apkIssuesListFiles


        // run search
        String csvFileName = "Solution" + Constants.POPULATION_SIZE + "-" + Constants.NUM_GENERATIONS + "_" +
                Constants.WEIGHT1 + "-" + Constants.WEIGHT2 + "-" + Constants.WEIGHT4 + ".csv";
        SALEM.SOLUTION_CSV_OUTPUT = SALEM.basePath+"/apks_folder/final_chromosome_fixes/" + csvFileName;

        SALEM.setOriginalApkName(args[0]);
        SALEM.setMergePath(args[3]);
        SALEM.setOriginalDecompiled(args[1]);
        SALEM.setOriginalDynamicLayoutFilePath(args[2]);
        long startTime = System.nanoTime();
        GARunner mi = new GARunner();

        try {

            mi.runIteratorForSegmentRelationGraph(args[2], args[4]);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime = System.nanoTime();

//        System.out.println("\n\n--------------------- FINAL RESULTS ---------------------------------");
//        System.out.println("Is page mobile friendly? = " + TTFix.isMobileFriendly());
//        System.out.println("# before usability score = " + TTFix.getBeforeAccessibilityScore());
//        System.out.println("# after usability score = " + TTFix.getAfterAccessibilityScore());
//        System.out.println("Improvement in usability score = " + Math.round(((TTFix.getAfterAccessibilityScore() - TTFix.getBeforeAccessibilityScore()) / TTFix.getBeforeAccessibilityScore()) * 100.0) + "%");
//        System.out.println("Total time = " + Util.convertNanosecondsToSeconds(endTime - startTime) + " sec");
////		System.out.println("Avg. Total search time = " + (Search.getSearchTimeInSec() / MainIterator.getGeneration()));
//        System.out.println("No. of fitness calls = " + FitnessFunction.getFitnessCalls());
//        System.out.println("Avg. time for fitness call = " + (FitnessFunction.getFitnessTimeInSec() / (double) FitnessFunction.getFitnessCalls()) + " sec");
        //ToDO: Ali fix and uncomment this

//        System.out.println("No. of mobile friendly API calls = " + GoogleAPIResults.getMobileFriendlyAPICalls());
//        System.out.println("Avg. time for mobile friendly API call = " + (GoogleAPIResults.getMobileFriendlyAPITotalTimeInSec() / (double) GoogleAPIResults.getMobileFriendlyAPICalls()) + " sec");
//        System.out.println("No. of usability score API calls = " + GoogleAPIResults.getUsabilityScoreAPICalls());
//        System.out.println("Avg. time for usability score API call = " + (AccessibilityScannerResults.getUsabilityScoreAPITotalTimeInSec() / (double) AccessibilityScannerResults.getUsabilityScoreAPICalls()) + " sec");
        System.out.println("-------------------------------------------------------------------------------");
    }


}
