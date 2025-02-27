package usc.edu.OwlEye.util;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import org.tinylog.Logger;
import usc.edu.OwlEye.ElementsProperties.*;
import usc.edu.OwlEye.GA.GAChromosome;
import usc.edu.OwlEye.GA.GAGene;
import usc.edu.OwlEye.GAChanges.GAChange;
import usc.edu.OwlEye.OwlConstants;
import usc.edu.OwlEye.OwlEye;
import usc.edu.OwlEye.fitness.FitnessFunction;
import usc.edu.SALEM.Constants;
//import usc.edu.SALEM.GA.GAChromosome;
//import usc.edu.SALEM.GA.GAGene;
import usc.edu.SALEM.SALEM;
import usc.edu.SALEM.VHTree.*;
import usc.edu.SALEM.util.LoadConfig;
import usc.edu.SALEM.util.StaticLayoutUtil;
import usc.edu.SALEM.util.Util;
import usc.edu.layoutissue.Issue;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static usc.edu.OwlEye.OwlConstants.TO_FuLL_ATTRIBUTES_BI_MAPPING;
import static usc.edu.SALEM.util.Util.runCommand;

//import static usc.edu.SALEM.util.Util.*;

public class Utils {
    private static Map<String, StaticLayoutUtil> staticLayoutFilesCache;  // path to the file and the read util

    public static String pythonInterpreter ;
    public static String[] readSubjectsCSV(String fileName, String id) {
        //read CSV file
        BufferedReader br = null;
        java.lang.String line = "";
        java.lang.String cvsSplitBy = ",";
        String[] foundSubject = new String[4];
        int i = 0;
        try {
            br = new BufferedReader(new FileReader(fileName));
            while ((line = br.readLine()) != null) {
                if (i == 0) {
                    i = 1;
                    continue;
                }
                String[] subject = line.split(cvsSplitBy);
                String subjectID = subject[0].trim();
//                String apkName = subject[1].trim();
//                String activityName = subject[2].trim();
//                String activityToRun = subject[3].trim();
//                String compileStyle=subject[4].trim();

                if (id.equalsIgnoreCase(subjectID)) {
//                    foundSubject[0] = apkName;
//                    foundSubject[1] = activityName;
//                    foundSubject[2] = activityToRun;
//                    foundSubject[3] = compileStyle;
                    String apkName = subject[1].trim();
                    String activityName = subject[2].trim();
                    String activityToRun = subject[3].trim();
                    String compileStyle=subject[4].trim();
                    String appNeedUninstall=subject[5].trim();
                    String needsScrolling=subject[6].trim();
                    String createSWFolder=subject[7].trim();
                    return new String[]{apkName, activityName,activityToRun,compileStyle,appNeedUninstall, needsScrolling, createSWFolder};
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String[] readCSV(String absolutePath) {
        BufferedReader br = null;
        java.lang.String line = "";
        java.lang.String cvsSplitBy = ",";
        ArrayList<String> lines = new ArrayList<String>();
        int i = 0;
        try {
            br = new BufferedReader(new FileReader(absolutePath));

            while ((line = br.readLine()) != null) {
                if (i == 0) {
                    i++;
                    continue;
                }
                lines.add(line);

            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lines.toArray(new String[lines.size()]);
    }




    public static String WriteChromosomeChangesToFile(GAChromosome chromosome, String outputPath) {
        String suffix = chromosome.getChromosomeIdentifier();
        //Copy to the same folder (so get the parent) but with the chromosome id
        File dest = new File(OwlEye.getOriginalDecompiled()).getParentFile();
        File source = new File(OwlEye.getOriginalDecompiled());

        String chromosomeFolderPath =   usc.edu.SALEM.util.Util.copyAppFolder(source, dest, suffix);
        List<Element> ChromosomeElementsToChange = new ArrayList<>();  // elementsToChange for all elements
        for (GAGene gene : chromosome.getGenes()) {
            // if gene change value is OwlConstants.CHANGE_SKIP, then skip it
            if(gene.getChangeType().equalsIgnoreCase(OwlConstants.CHANGE_SKIP)) {
                continue;
            }
            if(!gene.getChangeType().equalsIgnoreCase(OwlConstants.CHANGE_ADD_NEW_ELEMENT)) { // this is false when it is not adding scrollview only for new elements
                ChromosomeElementsToChange.add(new Element(gene.getXpaths().get(0), gene.getCssProperty(), gene.getValue(), false, null));
            }
            else {
                // it is add element gene
                // gene.get
                String xpath=gene.getXpaths().get(0);
                Node<DomNode> domNodeFound = OwlEye.getOriginalDefaultUI().getXMLTree().searchVHTreeByXpath(xpath);
                // we need its parent
                //Node<DomNode> parent = domNodeFound.getParent();
                // now I am testing getting the same child
                Node<DomNode> parent = domNodeFound;


                if(parent!=null) {


                    String originFIle = parent.getData().getAttributes().get("origin");
                    StaticLayoutUtil originLayout= findOriginalStaticLayoutFile(outputPath, originFIle);
                    if(originLayout!=null) {
                        originLayout.addElementToLayout(parent,outputPath);
                    }
                }
            }

        }
        applyChangeToElements(ChromosomeElementsToChange, outputPath);



    //Once all chromosome genes  are applied we then write them.
    writeMofidiedStaticFilesToFile();  // once we finalized the genes now we can write the updatet s the decompiled apl
        return chromosomeFolderPath;
    }

    public static StaticLayoutUtil findOriginalStaticLayoutFile(String outputPath, String originFIle) {
        if (originFIle != null && !originFIle.equalsIgnoreCase("NOTFOUND")) {
            /*** If element does not contain origin then we can not write it so SKIP ***/
            String[] layout = originFIle.split("/res/");
//                    System.out.println(" Res before crash: " + originFIle);
            String layoutPath = "/res/" + layout[1];
            String modify_origin = outputPath + File.separator + layoutPath;
            modify_origin = modify_origin.replaceAll("//", "/"); // In case
//                    modify_origin=path[0]+File.separator+TTFIX.apk_name+File.separator+path[1]+File.separator+modify_origin;
            StaticLayoutUtil originLayout = getStaticAppLayout(modify_origin);
            return originLayout;
            // Now we just create the element and add it to the right place

        }
        return null;
    }

    private static void writeMofidiedStaticFilesToFile() {
        Map<String, StaticLayoutUtil> cachedLayouts = getStaticLayoutFilesCache();
        for (String staticLayoutFilePath :
                cachedLayouts.keySet()) {
            StaticLayoutUtil staticLayoutObj = cachedLayouts.get(staticLayoutFilePath);
            staticLayoutObj.writeStaticXML();
        }
    }

    public static StaticLayoutUtil getStaticAppLayout(String file) {
        Map<String, StaticLayoutUtil> cached = getStaticLayoutFilesCache();
        if (cached.containsKey(file)) {
            return cached.get(file);
        }
        // first time reading the layout
        StaticLayoutUtil staticLayout = new StaticLayoutUtil();
        org.w3c.dom.Element root = staticLayout.readStaticXML(new File(file));
        staticLayoutFilesCache.put(file, staticLayout);
        return staticLayout;
    }
    public static Map<String, StaticLayoutUtil> getStaticLayoutFilesCache() {

        if (staticLayoutFilesCache == null) {
            staticLayoutFilesCache = new HashMap<>();
        }
        return staticLayoutFilesCache;
    }

    public static String  getLowestCommonAncestor(List<String> xpaths) {
        if (xpaths.size() == 0) {
            return "";
        }

        String lowestCommonSubstring = "";
        List<String> xpathsList = new ArrayList<>(xpaths);    // to allow get by index
        String[] xpath1Array = xpathsList.get(0).split("/");

        for (int i = 1; i < xpath1Array.length; i++)    // xpath1Array[0] = ""
        {
            for (int j = 1; j < xpathsList.size(); j++) {
                String[] xpath2Array = xpathsList.get(j).split("/");
                if (i >= xpath2Array.length) {
                    return lowestCommonSubstring;
                }

                if (!xpath1Array[i].equalsIgnoreCase(xpath2Array[i])) {
                    return lowestCommonSubstring;
                }
            }
            lowestCommonSubstring = lowestCommonSubstring + "/" + xpath1Array[i];
        }
        return lowestCommonSubstring;
    }
    private static void applyChangeToElements(List<Element> elementsToChange, String outputPath) {
//        Map<String, StaticLayoutUtil> staticLayout = getStaticLayoutFilesCache();
//        Node<DomNode> root = XMLUtils.getRoot();
        Node<DomNode> root = OwlEye.getOriginalDefaultUI().getXMLTree().getRoot();
        Node<DomNode> domNodeFound;
        String unit = "";
        for (Element element : elementsToChange) {
            String valueString = element.getValue();
            usc.edu.OwlEye.VHTree.XMLUtils xnlTree = OwlEye.getOriginalDefaultUI().getXMLTree();
            //domNodeFound = XMLUtils.searchVHTreeByXpath(element.getXpath(), root);
            domNodeFound = xnlTree.searchByID_T(element.getXpath(),null); // Place holder for now i need to continue to use xpath
            if (domNodeFound != null) {
//                System.out.println("Patch: Applying " + element.getCssProperty() + ": " + valueString + " to " + element.getXpath());
                //(1) update the node value in the XMLUtil ( so we do not overwrite the value when we write the second gene like whathappendd with lower bar of aidownload
//                updateElementInCurrentInstance(domNodeFound, Constants.SIZE_SPACE_ATTRIBUTES.get(element.getCssProperty()), valueString + unit);

                String originFIle = domNodeFound.getData().getAttributes().get("origin");
                if (originFIle != null && !originFIle.equalsIgnoreCase("NOTFOUND")) {
                    /*** If element does not contain origin then we can not write it so SKIP ***/
                    String[] layout = originFIle.split("/res/");
//                    System.out.println(" Res before crash: " + originFIle);
                    String layoutPath = "/res/" + layout[1];
                    String modify_origin = outputPath + File.separator + layoutPath;
                    modify_origin = modify_origin.replaceAll("//", "/"); // In case
//                    modify_origin=path[0]+File.separator+TTFIX.apk_name+File.separator+path[1]+File.separator+modify_origin;
                    StaticLayoutUtil originLayout = getStaticAppLayout(modify_origin);
                    String effective_id = domNodeFound.getData().getAttr("effective-id");
                    String id = domNodeFound.getData().getId();
                    if (effective_id != null) {
                        id = effective_id;
                    }
//                    System.out.println("ID: " + id);
                    if (id != null) {
                        org.w3c.dom.Element elementToWrite = originLayout.searchForNode(originLayout.getRoot(), id);
                        //String prop = Constants.SIZE_SPACE_ATTRIBUTES.get(element.getCssProperty()); // right now I am using the same property for width and height
                        String prop = element.getCssProperty(); // right now I am using the same property for width and height

                        if (OwlEye.debugCompile) {
                            Logger.trace("ID: " + id + " val: " + valueString + " prop " + prop);
                        }

                        // owleye for now to handle wrap and match_parent
                        if(elementToWrite==null){
                            continue;
                        }
                       String finalStringVal= preProcessAndCleanValueBeforeWritingToXML(valueString,prop);


                        //e.setAttribute(Constants.SIZE_SPACE_ATTRIBUTES.get(element.getCssProperty()), finalStringVal);
                        if(finalStringVal.equalsIgnoreCase(OwlConstants.shouldRemoveAttribute)){ // just added this Sep30 2022 to handle the case of removing the attribute from an element
                            elementToWrite.removeAttribute(element.getCssProperty());

                            //This is just to remove android:lines with removing maxlines ToDO: handle this in a better way
                            String attToRemove = OwlConstants.ATTRIBUTES_TO_REMOVE_TOGETHER_MAPPING.get(element.getCssProperty());
                            elementToWrite.removeAttribute(attToRemove);

                        }
                        else {
                            elementToWrite.setAttribute(element.getCssProperty(), finalStringVal); // owl short cut for now
//                        nodex.addAtrribute(Constants.SIZE_SPACE_ATTRIBUTES.get(element.getCssProperty()), valueString + unit); //TODO: Check if attribute already added
                        }
                        }
                }
            }
        }

    }

    private static String preProcessAndCleanValueBeforeWritingToXML(String valueString, String prop) {

        List<Double> x =   usc.edu.SALEM.util.Util.getNumbersFromString(valueString);
        String finalStringVal = valueString;
        if(x.size()>0){ // if it is numerical
            double doubleVal= x.get(0);
            if(doubleVal<0){
               if (doubleVal == -2) {
                    finalStringVal = OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(OwlConstants.wrapContent);
                }
               else if (doubleVal == -1) {
                    finalStringVal = OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(OwlConstants.matchParent);
                }
                else{
                  Logger.error("Negative value found in the gene that is not wrap_content or match_parent: "+valueString);
                }
            }
            else{
            if(OwlConstants.ATTRIBUTES_INT_VALUES_MAPPING.containsKey(prop)){
//                double doubleVal= x.get(0);
                int intVal = (int) doubleVal;
                finalStringVal =intVal+"";
            }

            String unit=OwlConstants.ATTRIBUTES_TO_UNIT_SUFFIX_MAPPING.get(prop); // mapping the property to the unit we should use
            if(unit==null){ // to avoid null pointer exception
                unit="";
            }
            finalStringVal = finalStringVal+unit; // I changed doubleVal to finalStringVal I am guessing we do not need to parse to double before assigining it a unit Oct 17 2022
        }
        }

        return finalStringVal;
    }

    public static void installAPK(String apkPath, String chromosomeAPKName,String packageName,String device_name) throws IOException {
        String cmd;
        //(1) First uninstall the apk if needed for the app

        boolean add_permission_tag=true;

        if (OwlEye.getAppNeedUninstall()) {


            cmd =  String.format(OwlConstants.UNINSTALL_APK , device_name,packageName); // the current package name was set in the readIssue method
            Logger.debug("uninstalling the apk");
            runCommand(cmd, null, null);

        }
        //(2) install  the chromosome
         cmd=OwlConstants.INSTALL_APK;
        if(add_permission_tag){
            cmd=OwlConstants.INSTALL_APK_WITH_PERMISSION;
        }
        cmd =  String.format(cmd , device_name,chromosomeAPKName);
        runCommand(cmd, apkPath, null);
    }

    public static String captureTheGeneratedUIScrollView(String apkPath, String chromosomeAPKName, String capturedUIFolder,String device_name) throws IOException, InterruptedException {
        Logger.debug("captureTheGeneratedUIScrollView : "+apkPath+" "+chromosomeAPKName+" "+capturedUIFolder);
        installAPK(apkPath, chromosomeAPKName, OwlEye.getOriginalApkName(),device_name);
        /*** Run the cralwing script to reach the correct state of the activity then capture the UI with UI automator***/
        String pythonScript = OwlEye.getPythonCrawlingScript();

        String args = device_name;

        String packageName = OwlEye.getOriginalApkName();
        String actName = OwlEye.getOriginalActivityName() ;
        String subject_id = OwlEye.getOriginalAppID();
        String needScrolling= OwlEye.getAppNeedScrolling();
        boolean scrollViewChromosome = true;

        args =   args+ " "+subject_id+" " + packageName + " " + actName+ " "+capturedUIFolder+" "+needScrolling+ " "+scrollViewChromosome;
        Logger.debug("Running the python script with args: " + args);
        boolean test=true;
        String result = runPythonScript2(pythonScript, args, pythonScript.replace("navigate_to_state.py", ""), "navigate_to_state");
        if(test){
            Logger.debug("Python script finished");
            Logger.debug("Capturing the UI");
            Logger.debug(result);

        }
        if(result=="False"){
            Logger.debug("Python script failed");
            return null;
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "Done";


    }
    public static String captureTheGeneratedUI(String apkPath, String chromosomeAPKName, String capturedUIFolder,String device_name) throws IOException, InterruptedException {
        Logger.debug("captureTheGeneratedUI method, with args: "+apkPath+" "+chromosomeAPKName+" "+capturedUIFolder);
        installAPK(apkPath, chromosomeAPKName, OwlEye.getOriginalApkName(),device_name);
        /*** Run the cralwing script to reach the correct state of the activity then capture the UI with UI automator***/
//        ArrayList<String> actScriptInfo = OwlEye.getPythonCrawlingScript();
        String pythonScript = OwlEye.getPythonCrawlingScript();
//        String activityToRunWithScript = actScriptInfo.get(1);
//        String scriptFileName = actScriptInfo.get(2);
        String args = device_name;

        String packageName = OwlEye.getOriginalApkName();
        String actName = OwlEye.getOriginalActivityName() ;
        String subject_id = OwlEye.getOriginalAppID();
        String needScrolling= OwlEye.getAppNeedScrolling();
//        if(needScrolling.equalsIgnoreCase("1")){
//            needScrolling="False";
//        }
//        else{
//            needScrolling="True";
//        }
        //OwlEye.get
        boolean scrollViewChromosome = false; // this the default. If the app needs scrolling, then we will set it to true but in the other method "captureTheGeneratedUIScrollView"
        args =   args+ " "+subject_id+" " + packageName + " " + actName+ " "+capturedUIFolder+" "+needScrolling + " "+scrollViewChromosome;
        Logger.debug("Running the python script with args: " + args);
        boolean test=true;
        String result = runPythonScript2(pythonScript, args, pythonScript.replace("navigate_to_state.py", ""), "navigate_to_state");
        if(test){
            Logger.debug("Python script finished");
            Logger.debug("Capturing the UI");
            Logger.debug(result);

        }
        if(result=="False"){
            Logger.debug("Python script failed");
            return null;
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "Done";


    }

    public static String[] compileApp(String apkSuffix, String originalStaticFiles, String outputPath) {
        int compileStyle=OwlEye.getAppCompileStyle();
        String signature = LoadConfig.getConfig_data().get("ANDROID_SIGNATURE".toLowerCase());
        String outputApk = OwlEye.getOriginalApkName() + "_" + apkSuffix + ".apk";
//        String apktool_command = "apktool -f b " + originalStaticFiles + "/" + mFix.getApkName() + " -o " + outputPath + "/" + outputApk;
        String apktool_command="";
        if(compileStyle==2){
            apktool_command = "apktool -f b --use-aapt2 " + originalStaticFiles + " -o " + outputPath + "/" + outputApk;
        }
        else{
        // String apktool_command = "apktool -f b " + originalStaticFiles + " -o " + outputPath + "/" + outputApk;

            apktool_command = "apktool -f b " + originalStaticFiles + " -o " + outputPath + "/" + outputApk;
        }

//        String signature_command = "jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore " + signature + " -storepass android "
//                + outputPath + "/" + outputApk + " alias_name";
        String signature_command ="jarsigner -verbose -keystore "+signature+" -storepass android -keypass android "+ outputPath + "/" + outputApk + " androiddebugkey";
        String cmd = apktool_command;
        Logger.debug("Running compile");
        String apkPath = "";
        try {
            runCommand(cmd, null, null);

            if (OwlEye.debugCompile) {
                Logger.debug("Running signature");
            }
            runCommand(signature_command, null, null);
            apkPath = outputPath + "/" + outputApk;
        } catch (Exception IOException) {
            Logger.error("error in compiling or signing the app");
            System.exit(0);
        }
        String[] res = {outputApk, apkPath};
        return res;  // The full path to the apk folder
    }

//    public static String prepareAppToGetToCorrectState(String currentActivityName, String apkPath, String chromosomeAPKName) throws IOException, InterruptedException {
//
//        installAPK(apkPath, chromosomeAPKName, OwlEye.getOriginalApkName());
//
//        /*** Run the cralwing script to reach the correct state of the activity then capture the UI with UI automator***/
////        ArrayList<String> actScriptInfo = OwlEye.getPythonCrawlingScript();
//        String pythonScript = OwlEye.getPythonCrawlingScript();
////        String activityToRunWithScript = actScriptInfo.get(1);
////        String scriptFileName = actScriptInfo.get(2);
//        String args = OwlEye.getDeviceName();
//
//        String packageName = OwlEye.getOriginalApkName();
//        String actName = OwlEye.getOriginalActivityName() ;
//
//        args =   packageName + " " + actName;
//
//
//        runPythonScript(pythonScript,args,pythonScript.replace("navigate_to_state.py",""),null);
//
//
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        String activityName = getDeviceCurrentRunningActivityName();
//
//
//        //(2) dump the layout using ui automator to the phone
//        if (activityName.equalsIgnoreCase(OwlEye.getOriginalActivityName())) {  // if it is NOT the correct activity
//            return activityName;
//        } else {
//            return null;  // Not Correct
//        }
//
//
//
//    }
    public static String runPythonScript2(String pythonScript, String crawlingScript, String scriptFolder, String commandType) throws IOException {
        String cmd = pythonScript + " " + crawlingScript;
//        String pythonInterpreter = LoadConfig.getConfig_data().get("python_interpreter");
        cmd = pythonInterpreter + " " + cmd ;
        String results = runCommand(cmd, scriptFolder, commandType);
        return results;
    }
    public static String runPythonScript(String pythonScript, String crawlingScript, String scriptFolder, String commandType) throws IOException {
        String cmd = pythonScript+" "+crawlingScript;
//        String pythonInterpreter = LoadConfig.getConfig_data().get("python_interpreter");
        cmd = pythonInterpreter+" " +cmd;
        String results = runCommand(cmd, scriptFolder, commandType);
        return results;
    }
    public static String getDeviceCurrentRunningActivityName() throws IOException {
        String cmd = OwlConstants.GREP_ACTIVITY_NAME;
        cmd=String.format(cmd);
        String activityresult =   runCommand(cmd, null, "grepActivityName");
        if (activityresult == null) {
            Logger.error("Error Could capture the UI");
            System.exit(1); //"Error
        }
        String activityName = null;
        if (activityresult.contains("/")) {
            String[] arr = activityresult.split("/");
            String temp = arr[1];
//                    arr= temp.split("/");
            if (temp.contains("}")) {
                activityName = temp.replace("}", "");
            }
        }
        return activityName;
    }


    public static String[] dumpActivityLayout(String dumpedFileLayout) throws IOException {
        /*** Dump the ui using UI automotor and add temp suffix | then extract the content tag using Mian's method  ***/

        // (1) if chromosome folder does not exist, create it
        File directory = new File(dumpedFileLayout);
        if (!directory.exists()) {
            directory.mkdir();
        }

        String activityName = getDeviceCurrentRunningActivityName();


        //(2) dump the layout using ui automator to the phone
        if (!activityName.equalsIgnoreCase(OwlEye.getOriginalActivityName())) {  // if it is NOT the correct activity
            return null;
        }
        String cmd;
        cmd = Constants.UIAUTOMATOR_DUMP;
        runCommand(cmd, null, null);

        //(3) pull the xml layout to the pc
        String xmlFileName = OwlEye.getOriginalActivityName() + ".xml";
//        cmd = Constants.PULL + " " + "/sdcard/window_dump.xml " + dumpedFileLayout + "/" + xmlFileName;
        cmd = Constants.PULL;
        cmd = String.format(cmd, "/sdcard/window_dump.xml", dumpedFileLayout + "/" + xmlFileName);
        runCommand(cmd, null, null);
        //(4) capture the screenshot
        cmd = Constants.SCREEN_CAP + " " + "/sdcard/screen.png";
        runCommand(cmd, null, null);
        //(5) pull the screenshot to the pc
        String pngFileName = dumpedFileLayout + "/" + OwlEye.getOriginalActivityName() + ".png";
//        cmd = Constants.PULL + " " + "/sdcard/screen.png " + dumpedFileLayout + "/" + mFix.getCurrentActivityName() + ".png";
        cmd = Constants.PULL;
//                + " " + "/sdcard/screen.png " + dumpedFileLayout + "/" + mFix.getCurrentActivityName() + ".png";
        cmd = String.format(cmd, "/sdcard/screen.png", dumpedFileLayout + "/" + OwlEye.getOriginalActivityName() + ".png");

        runCommand(cmd, null, null);

        String[] result = {xmlFileName, pngFileName};
        return result;


    }

    public static String getDynamicValueInDDP(DomNode node, String cssProperty) {

        String value = null;
        int valNumber= 0;

        if (cssProperty.equalsIgnoreCase(Height.propertyName)){
             valNumber = node.height;
        }else if (cssProperty.equalsIgnoreCase(Width.propertyName)){
             valNumber = node.width;}

        if (OwlConstants.ATTRIBUTES_TO_DIVIDE_BY_DP.keySet().contains(cssProperty)){
            value = String.valueOf(valNumber / OwlConstants.PHONE_DENSITY);
        }

//        if (cssProperty.equalsIgnoreCase("height") || cssProperty.equalsIgnoreCase(Constants.MAIN_PROPERTY_TO_MIN_PROPERTY.get("height"))) {
//            value = String.valueOf(node.height / Constants.PHONE_DENSITY);
//        } else if (cssProperty.equalsIgnoreCase("width") || cssProperty.equalsIgnoreCase(Constants.MAIN_PROPERTY_TO_MIN_PROPERTY.get("width"))) {
//            value = String.valueOf(node.width / Constants.PHONE_DENSITY);
//        }
        return value;

    }
    public static boolean skipMissingInScrollView(Node<DomNode> node) {
        // iterate through the parents and check if one of them is a scroll view

        Node<DomNode> parent = node.getParent();

//        boolean leaveWithDirectScrollView=directParentAndLeaveNode(node, parent);
//        if (leaveWithDirectScrollView) {
//            // see if it is a leave node and the parent is a scroll view if yes, we can NOT skip it
//
//            return false;
//        }
        while (parent != null) {
            if (parent.getData().getTagName().toLowerCase().contains("scrollview")) {
                return true;
            }
            parent = parent.getParent();
        }

        return false;
    }

    private static boolean directParentAndLeaveNode(Node<DomNode> node, Node<DomNode> parent) {
        // see if it is a leave node and the parent is a scroll view if yes, we can NOT skip it
        if (parent != null) {
            if (parent.getData().getTagName().toLowerCase().contains("scrollview")&&isLeaveNode(node)) {
                return true;
            }

        }
        return false;

    }

    public static boolean skipLayoutIssueDueToScrollView(Issue issue) {
        DomNode node1 = issue.getPageUnderTestEdge().getNode1().getDomNode().getData();
        DomNode node2= issue.getPageUnderTestEdge().getNode2().getDomNode().getData();
        String new_node1=node1.getAttr("appear_after_scroll");
        String new_node2=node2.getAttr("appear_after_scroll");
        int no_of_new_nodes=0;
        if  (new_node1!=null){
            no_of_new_nodes++;
        }
        if  (new_node2!=null){
            no_of_new_nodes++;
        }

        if  (no_of_new_nodes==1) // only one new node with one old node so we skip it
        {
            return true;
        }
        return false;
    }
    public static String getValueFromElement(DomNode node, String cssProperty) {
    //    String xpath = node.getxPath();


        String property = OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(cssProperty);
        String val = node.getAttr(property);
        if (val == null) {
            return null;
        }
        if (val.equalsIgnoreCase("match_parent") || val.equalsIgnoreCase("fill_parent")) {
//			if(cssProperty.equalsIgnoreCase("width"))
//			{
            val = "-1";
            //	}
        } else if (val.equalsIgnoreCase("wrap_content")) {
            val = "-2";
        }

        return val;
    }
    public static boolean isValueNumerical(String string) {
        List<Double> numbers = new ArrayList<Double>();

        // Pattern p = Pattern.compile("(\\d+(?:\\.\\d+)?)"); //Ali: mfix original
        Pattern p = Pattern.compile("(\\-?\\d+(?:\\.\\d+)?)"); // Ali: I added to include (-)
        Matcher m = p.matcher(string);
        while (m.find()) {
            numbers.add(Double.valueOf(m.group()));
        }

        if (numbers.size() > 0) {
            return true;
        }
        else {
            return false;
        }

    }

    public static List<Double> generateGaussianInputsForDP(String property, GAChange gaChange, String changeType, String currentVal) {
        // list of inputs in this order {mean, min value, max value}

        // we need to consider change type in deciding the values (e.g. if the change type is increase, then the min value should be the current value)
        List<Double> vals = new ArrayList<>();
        double currentGeneNumericalVal = 0;
        Property prop= gaChange.getProperty();
        String str = "";
        double currentDynamicVal = 0;
        currentGeneNumericalVal=Util.getNumbersFromString(currentVal).get(0);
        String reverseProperty=TO_FuLL_ATTRIBUTES_BI_MAPPING.inverse().get(property);
        if (reverseProperty.contains("padding")){
            reverseProperty=Padding.propertyName;
        }
        else if(reverseProperty.contains("margin")){
            reverseProperty=Margin.propertyName;
        }

        switch (reverseProperty) {
            case Height.propertyName: {
                double mean=50, min=0, max=100;

                // currentValNumerical = Double.parseDouble(currentVal);
                if (prop != null) {
                    Height heightProp = (Height) prop;
                    String dynamicVal = heightProp.currentDynamicVal;
                    if (dynamicVal != null) {
                        currentDynamicVal = Util.getNumbersFromString(dynamicVal).get(0);
                    }
                }


                switch (changeType) {
                    case OwlConstants.CHANGE_INCREASE: {
                        if (currentDynamicVal > 0) { // if value exist and it is not 0 or negative we consider it as the min for height
                            min = currentDynamicVal + 5;
                        } else { // for some reason we could not find it so we consider the change value as the min
                            min = currentGeneNumericalVal;


                        }
//                        if(gaChange.getPropertyName().equalsIgnoreCase(Height.propertyName) ||
//                                gaChange.getPropertyName().equalsIgnoreCase(Width.propertyName)|| gaChange.getPropertyName().equalsIgnoreCase(MinWidth.propertyName) ||
//                                gaChange.getPropertyName().equalsIgnoreCase(MinHeight.propertyName) || gaChange.getPropertyName().equalsIgnoreCase(MaxWidth.propertyName) ||
//                                gaChange.getPropertyName().equalsIgnoreCase(MaxHeight.propertyName)){
                        mean = Math.ceil(currentGeneNumericalVal * 1.40);
                        max = currentGeneNumericalVal * 1.60;

//                    }
//                        else {
//                            mean = currentGeneNumericalVal + 10;
//                            max = currentGeneNumericalVal + 20;
//                        }

//                        mean = currentGeneNumericalVal + 50;
//                        mean= currentGeneNumericalVal /2;
//
//                        max = currentGeneNumericalVal + 250;
//                        max= currentGeneNumericalVal + 3;
                        break;
                    }
                    case OwlConstants.CHANGE_DECREASE: {
                        if (currentDynamicVal > 0) { // if value exist and it is not 0 or negative we consider it as the min for height
                            min = currentDynamicVal - 5;
                        } else { // for some reason we could not find it so we consider the change value as the min
                            min = currentGeneNumericalVal;


                        }
//                        if(gaChange.getPropertyName().equalsIgnoreCase(Height.propertyName) ||
//                                gaChange.getPropertyName().equalsIgnoreCase(Width.propertyName)|| gaChange.getPropertyName().equalsIgnoreCase(MinWidth.propertyName) ||
//                                gaChange.getPropertyName().equalsIgnoreCase(MinHeight.propertyName) || gaChange.getPropertyName().equalsIgnoreCase(MaxWidth.propertyName) ||
//                                gaChange.getPropertyName().equalsIgnoreCase(MaxHeight.propertyName)){
                        min = Math.ceil(currentGeneNumericalVal * .30);
                        if (min < 0) {
                            min = 0;
                        }
                        mean = Math.ceil(currentGeneNumericalVal * .70);
                        if (currentDynamicVal > 0) {
                            max = currentDynamicVal;
                        } else {
                            max = currentGeneNumericalVal;
                        }

//                        }
//                        else
                        break;
                    }


                    default: {
                        str = "change";
                        break;
                    }

//                double mean, min, max;
//                if(currentDynamicVal>0){ // if value exist and it is not 0 or negative we consider it as the min for height
//                    min=currentDynamicVal+5;
//                    min=0;
//                }
//                else{ // for some reason we could not find it so we consider the change value as the min
//                    min=currentGeneNumericalVal+5;
//                    min=0;
//
//                }
//                mean = currentGeneNumericalVal + 50;
//                mean= currentGeneNumericalVal /2;
//
//                max = currentGeneNumericalVal + 250;
//                max= currentGeneNumericalVal + 3;


                }
                vals.add(mean);
                vals.add(min );
                vals.add(max );
                break;
            }
        //    }

            case Width.propertyName:case MinHeight.propertyName:case MinWidth.propertyName:case MaxHeight.propertyName:case MaxWidth.propertyName: {
                double mean=50, min=0, max=100;

                // currentValNumerical = Double.parseDouble(currentVal);
                if (prop != null) {
                    Property heightProp =  prop;
                    String dynamicVal = heightProp.currentDynamicVal;
                    if (dynamicVal != null) {
                        currentDynamicVal = Util.getNumbersFromString(dynamicVal).get(0);
                    }
                }


                switch (changeType) {
                    case OwlConstants.CHANGE_INCREASE: {
                        if (currentDynamicVal > 0) { // if value exist and it is not 0 or negative we consider it as the min for height
                            min = currentDynamicVal + 5;
                        } else { // for some reason we could not find it so we consider the change value as the min
                            min = currentGeneNumericalVal;


                        }
//                        if(gaChange.getPropertyName().equalsIgnoreCase(Height.propertyName) ||
//                                gaChange.getPropertyName().equalsIgnoreCase(Width.propertyName)|| gaChange.getPropertyName().equalsIgnoreCase(MinWidth.propertyName) ||
//                                gaChange.getPropertyName().equalsIgnoreCase(MinHeight.propertyName) || gaChange.getPropertyName().equalsIgnoreCase(MaxWidth.propertyName) ||
//                                gaChange.getPropertyName().equalsIgnoreCase(MaxHeight.propertyName)){
                        mean = Math.ceil(currentGeneNumericalVal * 1.40);
                        max = currentGeneNumericalVal * 1.60;

//                    }
//                        else {
//                            mean = currentGeneNumericalVal + 10;
//                            max = currentGeneNumericalVal + 20;
//                        }

//                        mean = currentGeneNumericalVal + 50;
//                        mean= currentGeneNumericalVal /2;
//
//                        max = currentGeneNumericalVal + 250;
//                        max= currentGeneNumericalVal + 3;
                        break;
                    }
                    case OwlConstants.CHANGE_DECREASE: {
                        if (currentDynamicVal > 0) { // if value exist and it is not 0 or negative we consider it as the min for height
                            min = currentDynamicVal - 5;
                        } else { // for some reason we could not find it so we consider the change value as the min
                            min = currentGeneNumericalVal;


                        }
//                        if(gaChange.getPropertyName().equalsIgnoreCase(Height.propertyName) ||
//                                gaChange.getPropertyName().equalsIgnoreCase(Width.propertyName)|| gaChange.getPropertyName().equalsIgnoreCase(MinWidth.propertyName) ||
//                                gaChange.getPropertyName().equalsIgnoreCase(MinHeight.propertyName) || gaChange.getPropertyName().equalsIgnoreCase(MaxWidth.propertyName) ||
//                                gaChange.getPropertyName().equalsIgnoreCase(MaxHeight.propertyName)){
                        min = Math.ceil(currentGeneNumericalVal * .30);
                        if (min < 0) {
                            min = 0;
                        }
                        mean = Math.ceil(currentGeneNumericalVal * .70);
                        if (currentDynamicVal > 0) {
                            max = currentDynamicVal;
                        } else {
                            max = currentGeneNumericalVal;
                        }

//                        }
//                        else
                        break;
                    }


                    default: {
                        str = "change";
                        break;
                    }



                }
                vals.add(mean);
                vals.add(min );
                vals.add(max );
                break;
            }
//            case Width.propertyName: {
//                if (prop!=null) {
//                    Width widthProp = (Width) prop;
//                    String dynamicVal = widthProp.currentDynamicVal;
//                    if (dynamicVal != null) {
//                        currentDynamicVal = Util.getNumbersFromString(dynamicVal).get(0);
//                    }
//                }
//                double mean, min, max;
//                if(currentDynamicVal>0){ // if value exist and it is not 0 or negative we consider it as the min for height
//                    min=currentDynamicVal+5;
//                }
//                else{ // for some reason we could not find it so we consider the change value as the min
//                    min=currentGeneNumericalVal+5;
//
//                }
//                min=0;
//                mean = currentGeneNumericalVal + 50;
//                mean = currentGeneNumericalVal /2;
//                max = currentGeneNumericalVal + 250;
//                max= currentGeneNumericalVal + 3;
//
//                vals.add(mean);
//                vals.add(min);
//                vals.add(max);
//                break;
//            }
//            case MinHeight.propertyName: {
//                currentGeneNumericalVal = Double.parseDouble(currentVal);
//                double mean = currentGeneNumericalVal + 10;
//                vals.add(mean);
//                vals.add(mean - 25);
//                vals.add(mean + 25);
//                break;
//            }
//
//            case MinWidth.propertyName: {
//                currentGeneNumericalVal = Double.parseDouble(currentVal);
//                double mean = currentGeneNumericalVal + 10;
//                vals.add(mean);
//                vals.add(mean - 25);
//                vals.add(mean + 25);
//                break;
//            }
//            case MaxHeight.propertyName: {
//                currentGeneNumericalVal = Double.parseDouble(currentVal);
//                double mean = currentGeneNumericalVal + 10;
//                vals.add(mean);
//                vals.add(mean - 25);
//                vals.add(mean + 25);
//                break;
//            }
//
//            case MaxWidth.propertyName: {
//                currentGeneNumericalVal = Double.parseDouble(currentVal);
//                double mean = currentGeneNumericalVal + 10;
//                vals.add(mean);
//                vals.add(mean - 25);
//                vals.add(mean + 25);
//                break;
//            }
            case Padding.propertyName: {
                currentGeneNumericalVal = Double.parseDouble(currentVal);
                double mean = currentGeneNumericalVal/2 ; // mean is current value /2
                vals.add(mean);
                vals.add(0.0); // min is 0
                vals.add(currentGeneNumericalVal); // max is current value
                break;
            }

            case Margin.propertyName: {
                currentGeneNumericalVal = Double.parseDouble(currentVal);
                double mean = currentGeneNumericalVal/2 ; // mean is current value /2
                vals.add(mean);
                vals.add(0.0); // min is 0
                vals.add(currentGeneNumericalVal); // max is current value
                break;
            }
        }

        return vals;
    }
    public static List<Double> generateGaussianInputsForNumerical(String property, GAChange gaChange, String changeType, String currentVal) {
        // list of inputs in this order {mean, min value, max value}

        // we need to consider change type in deciding the values (e.g. if the change type is increase, then the min value should be the current value)
        List<Double> vals = new ArrayList<>();
        double currentGeneNumericalVal = 0;
        Property prop = gaChange.getProperty();
        String str = "";
        double currentDynamicVal = 0;
        currentGeneNumericalVal = Util.getNumbersFromString(currentVal).get(0);
        String reverseProperty = TO_FuLL_ATTRIBUTES_BI_MAPPING.inverse().get(property);
//        if (reverseProperty.contains("padding")){
//            reverseProperty=Padding.propertyName;
//        }
//        else if(reverseProperty.contains("margin")){
//            reverseProperty=Margin.propertyName;
//        }
        switch (reverseProperty) {
            case Weight.propertyName: {
                // currentValNumerical = Double.parseDouble(currentVal);
                if (prop != null) {
                    Weight weightProp = (Weight) prop;
                    String dynamicVal = weightProp.currentDynamicVal;
                    if (dynamicVal != null) {
                        currentDynamicVal = Util.getNumbersFromString(dynamicVal).get(0);
                    }
                }
                else{
                    currentDynamicVal=currentGeneNumericalVal;
                }
                // if increase then min is current value
                // if decrease then max is current value
                double mean = 2, min = 0, max = 4;
                if (changeType.equals(OwlConstants.CHANGE_INCREASE)) {
                    mean = currentDynamicVal + currentDynamicVal * 2;
                    min = currentDynamicVal;
                    max = currentDynamicVal + currentDynamicVal * 5;
                } else if (changeType.equals(OwlConstants.CHANGE_DECREASE)) {
                    mean = currentDynamicVal / 2;
                    min = 0;
                    max = currentDynamicVal;
                }

                vals.add(Math.ceil(mean));
                vals.add(Math.ceil(min));
                vals.add(Math.ceil(max));
                break;
            }

            case ConstraintVerticalWeight.propertyName: {
                // currentValNumerical = Double.parseDouble(currentVal);
                if (prop != null) {
                    ConstraintVerticalWeight weightProp = (ConstraintVerticalWeight) prop;
                    String dynamicVal = weightProp.currentDynamicVal;
                    if (dynamicVal != null) {
                        currentDynamicVal = Util.getNumbersFromString(dynamicVal).get(0);
                    }
                }
                else{
                    currentDynamicVal=currentGeneNumericalVal;
                }
                // if increase then min is current value
                // if decrease then max is current value
                double mean = 2, min = 0, max = 4;
                if (changeType.equals(OwlConstants.CHANGE_INCREASE)) {
                    mean = currentDynamicVal + currentDynamicVal * 2;
                    min = currentDynamicVal;
                    max = currentDynamicVal + currentDynamicVal * 5;
                } else if (changeType.equals(OwlConstants.CHANGE_DECREASE)) {
                    mean = currentDynamicVal / 2;
                    min = 0;
                    max = currentDynamicVal;
                }

                vals.add(Math.ceil(mean));
                vals.add(Math.ceil(min));
                vals.add(Math.ceil(max));
                break;

            }
            case ConstraintHorizontalWeight.propertyName: {
                // currentValNumerical = Double.parseDouble(currentVal);
                if (prop != null) {
                    ConstraintHorizontalWeight weightProp = (ConstraintHorizontalWeight) prop;
                    String dynamicVal = weightProp.currentDynamicVal;
                    if (dynamicVal != null) {
                        currentDynamicVal = Util.getNumbersFromString(dynamicVal).get(0);
                    }
                }
                else{
                    currentDynamicVal=currentGeneNumericalVal;
                }
                // if increase then min is current value
                // if decrease then max is current value
                double mean = 2, min = 0, max = 4;
                if (changeType.equals(OwlConstants.CHANGE_INCREASE)) {
                    mean = currentDynamicVal + currentDynamicVal * 2;
                    min = currentDynamicVal;
                    max = currentDynamicVal + currentDynamicVal * 5;
                } else if (changeType.equals(OwlConstants.CHANGE_DECREASE)) {
                    mean = currentDynamicVal / 2;
                    min = 0;
                    max = currentDynamicVal;
                }

                vals.add(Math.ceil(mean));
                vals.add(Math.ceil(min));
                vals.add(Math.ceil(max));
                break;
            }

            case MaxHeight.propertyName : {
                // currentValNumerical = Double.parseDouble(currentVal);
                if (prop != null) {
                    MaxHeight maxProp = (MaxHeight) prop;
                    double dynamicVal = maxProp.getCurrentVal();
                    if (dynamicVal != Double.MIN_VALUE) {
                        currentDynamicVal = dynamicVal;
                    }
                }
                else{
                    currentDynamicVal=currentGeneNumericalVal;
                }
                // if increase then min is current value
                // if decrease then max is current value
                double mean = 3, min = 1, max = 4;
                if (changeType.equals(OwlConstants.CHANGE_INCREASE)) {
                    mean = currentDynamicVal + currentDynamicVal * 2;
                    min = currentDynamicVal;
                    max = currentDynamicVal + currentDynamicVal * 5;
                } else if (changeType.equals(OwlConstants.CHANGE_DECREASE)) {
                    mean = currentDynamicVal / 2;
                    min = 0;
                    max = currentDynamicVal;
                }
                mean=Math.ceil(mean);
                min=Math.ceil(min);
                max=Math.ceil(max);
                vals.add(mean);
                vals.add(min);
                vals.add(max);
                break;
            }
            case MaxWidth.propertyName : {
                // currentValNumerical = Double.parseDouble(currentVal);
                if (prop != null) {
                    MaxWidth maxProp = (MaxWidth) prop;
                    double dynamicVal = maxProp.getCurrentVal();
                    if (dynamicVal != Double.MIN_VALUE) {
                        currentDynamicVal = dynamicVal;
                    }
                }
                else{
                    currentDynamicVal=currentGeneNumericalVal;
                }
                // if increase then min is current value
                // if decrease then max is current value
                double mean = 3, min = 1, max = 4;
                if (changeType.equals(OwlConstants.CHANGE_INCREASE)) {
                    mean = currentDynamicVal + currentDynamicVal * 2;
                    min = currentDynamicVal;
                    max = currentDynamicVal + currentDynamicVal * 5;
                } else if (changeType.equals(OwlConstants.CHANGE_DECREASE)) {
                    mean = currentDynamicVal / 2;
                    min = 0;
                    max = currentDynamicVal;
                }
                mean=Math.ceil(mean);
                min=Math.ceil(min);
                max=Math.ceil(max);
                vals.add(mean);
                vals.add(min);
                vals.add(max);
                break;
            }
            case Lines.propertyName : {
                // currentValNumerical = Double.parseDouble(currentVal);
                if (prop != null) {
                    Lines lineProp = (Lines) prop;
                    double dynamicVal = lineProp.getCurrentVal();
                    if (dynamicVal != Double.MIN_VALUE) {
                        currentDynamicVal = dynamicVal;
                    }
                }
                else{
                    currentDynamicVal=currentGeneNumericalVal;
                }
                // if increase then min is current value
                // if decrease then max is current value
                double mean = 3, min = 1, max = 4;
                if (changeType.equals(OwlConstants.CHANGE_INCREASE)) {
                    mean = currentDynamicVal + currentDynamicVal * 2;
                    min = currentDynamicVal;
                    max = currentDynamicVal + currentDynamicVal * 5;
                } else if (changeType.equals(OwlConstants.CHANGE_DECREASE)) {
                    mean = currentDynamicVal / 2;
                    min = 0;
                    max = currentDynamicVal;
                }
                mean=Math.ceil(mean);
                min=Math.ceil(min);
                max=Math.ceil(max);
                vals.add(mean);
                vals.add(min);
                vals.add(max);
                break;
            }


        }
        return vals;
    }


    public static String[] getDefinedWeightForElement(Node<DomNode> node, String mainProperty) {
        String weightProperty = null;
        String value;
        // 1- check main weight
        String weightVal = Utils.getValueFromElement(node.getData(), Weight.propertyName); // we do TO_FuLL_ATTRIBUTES_MAPPING mapping in the method
        if (weightVal==null){
            // look for constraint weight
            String prop= OwlConstants.HEIGHT_WIDTH_WEIGHT_MAPPING.get(mainProperty);
            weightVal = Utils.getValueFromElement(node.getData(), prop);
            if (weightVal!=null){
                weightProperty=prop;

            }
        }
        else{
            weightProperty=Weight.propertyName;
        }



        if (weightProperty != null && weightVal != null ) {
            boolean isNumber = Utils.isValueNumerical(weightVal);

            if (isNumber) {
               // double weightValue=Util.getNumbersFromString(weightVal).get(0);
                // return weightProperty and weightValue
                Logger.debug("getDefinedWeightForElement method: " + weightProperty + " is "+weightVal);

                return new String []{weightProperty, String.valueOf(weightVal)};
            }
            // it is a normal weight
        }
        return null;
    }
    public static void writeOptimalChromosomeToFile(GAChromosome optimalSolution) throws FileNotFoundException, UnsupportedEncodingException {
        String dest=OwlEye.getFinalRepairOutputFolder();
        String source=OwlEye.getCrawledDynamicRepairVHPath() +File.separator+ optimalSolution.getNewApkName();
        usc.edu.SALEM.util.Util.copyAppFolder(source, dest,null);
//        PrintWriter writer = new PrintWriter(outputFolder + File.separator + "solution_details.txt", "UTF-8");
//
//        writer.println("Activity Name:");
//        writer.println(OwlEye.getOriginalActivityName());
//        writer.println("Chromosome ID:");
//        writer.println(optimalSolution.getChromosomeIdentifier());
//        writer.println("Genes:");
////        writer.println(chromosome.toString());
//        for (GAGene gene: optimalSolution.getGenes()){
//            writer.println("\t"+gene.toString());
//        }
//        writer.println("------------------------------------------");
//        writer.println("fitness score:");
//        writer.println(optimalSolution.getFitnessFunctionObj().getFitnessScoreSummary());
//        writer.close();
    }
    public static void writeChromosomeToFile(GAChromosome chromosome) throws FileNotFoundException, UnsupportedEncodingException {
        String specificChromosomeOutputFolder=OwlEye.getCrawledDynamicRepairVHPath() +File.separator+ chromosome.getNewApkName();
        String mainRunFolder=OwlEye.getCrawledDynamicRepairVHPath() +File.separator;
        String summaryTxtFileName="runSummary.txt";



        // 1- write specific chromosome info to its specific folder

        PrintWriter writer = new PrintWriter(specificChromosomeOutputFolder + File.separator + "solution_details.txt", "UTF-8");

        writer.println("Activity Name:");
        writer.println(OwlEye.getOriginalActivityName());
        writer.println("Chromosome ID:");
        writer.println(chromosome.getChromosomeIdentifier());
        writer.println("Genes:");
//        writer.println(chromosome.toString());
        for (GAGene gene: chromosome.getGenes()){
            writer.println("\t"+gene.toString());
        }
        writer.println("------------------------------------------");
        writer.println("fitness score:");
        writer.println(chromosome.getFitnessFunctionObj().getFitnessScoreSummary());
        writer.println("------------------------------------------");
        writer.println("Number of Issues After: " + chromosome.getFitnessFunctionObj().printNumberOfIssuesAfter());
        writer.close();


        // 2- write summary to main folder
        String mainSummaryFilePath=mainRunFolder+File.separator+summaryTxtFileName;
        PrintWriter writer2 = new PrintWriter(new FileOutputStream(new File(mainSummaryFilePath),true)); // true = append mode
        writer2.println("\n|||*****------------------------------------------*****|||\n");
        writer2.println("Chromosome ID:");
        writer2.println(chromosome.getChromosomeIdentifier());
        writer2.println("fitness score:");
        writer2.println(chromosome.getFitnessFunctionObj().getFitnessScoreSummary());
        writer2.println("------------------------------------------");
        writer2.println("Number of Issues After: " + chromosome.getFitnessFunctionObj().printNumberOfIssuesAfter());
        writer2.close();
    }

    public static void writeChromosomeToFilePareto(GAChromosome chromosome,String outputFolder) throws FileNotFoundException, UnsupportedEncodingException {
       String dest=outputFolder+File.separator+ chromosome.getNewApkName();
        // we look for the chromosome folder in the generated folder and copy it to the output folder

        String src=OwlEye.getCrawledDynamicRepairVHPath() +File.separator+ chromosome.getNewApkName();
        usc.edu.SALEM.util.Util.copyAppFolder(src, dest,null);
        String compiledApkPath=OwlEye.getCompileOutputPath()+File.separator+chromosome.getNewApkName();
        // if the compiled apk exists then copy it to the dest folder
        if (new File(compiledApkPath).exists()){
            // copy the compiled apk to the dest folder

            copyAPKFile(compiledApkPath,dest+File.separator+chromosome.getNewApkName());
        }
        else{
            Logger.debug("compiled apk does not exist for chromosome "+compiledApkPath);
        }


    }

    public static double changeCurrentValueByPercentage(double numericalDynamicVal, double changeAmountPercentage) {

        // if changeAmountPercentage is positive then increase the value by that percentage. If negative then decrease the value by that percentage
        double percentage=Math.abs(changeAmountPercentage)/100; // to convert it to decimal value for percentage
        if(changeAmountPercentage>0){
            // increase
            double increaseAmount=numericalDynamicVal*percentage;
            numericalDynamicVal=numericalDynamicVal+increaseAmount;
        }
        else{
            // decrease
            double decreaseAmount=numericalDynamicVal*percentage;
            numericalDynamicVal=numericalDynamicVal-decreaseAmount;
        }

        return Math.floor(numericalDynamicVal);
    }


    public static void CreateSWLayout(String originalAppID, String originalApkName, String originalDecompiled, String originalDefaultXMLPath) throws IOException {
        String args =  originalAppID + " " + originalApkName + " " + originalDecompiled + " " + originalDefaultXMLPath;
//        String pythonScript = OwlEye.getPythonScriptsFolder() + File.separator + "create_sw_layout.py";


        String baseSubjectsPath= OwlEye.getBaseSubjectPath()+"/";
        String pythonScript= baseSubjectsPath+File.separator+"maintain_origin_layout_files.py";

        runPythonScript2(pythonScript,args,baseSubjectsPath,null);


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Logger.debug("Running the python script with args: " + args);
        boolean test=true;
//        String result = runPythonScript2(pythonScript, args, pythonScript.replace("navigate_to_state.py", ""), "navigate_to_state");

    }

    public static void setScreenSizeAndDensity(String currentScalingVersionFolder) throws IOException {
        // set screen size and density based on the scaling version being tested
        // font scale: 1.0 is default, 1.3 is largest
        // screen size: 560 is default, 720 is largest
        double default_font=1.0;
        int default_screen_size=560;
        double largest_font=1.3;
        int largest_screen_size=720;
        String font_cmd = OwlConstants.ADB + " shell settings put system font_scale ";
        String screen_size_cmd = OwlConstants.ADB + " shell wm density ";
//        currentScalingVersionFolder="LL";
        if(currentScalingVersionFolder.contains("LL")) {
            screen_size_cmd = screen_size_cmd + largest_screen_size;
            font_cmd = font_cmd + largest_font;

        }
        else if(currentScalingVersionFolder.contains("LD")) {
            screen_size_cmd = screen_size_cmd + largest_screen_size;
            font_cmd = font_cmd + default_font;

        }
        else if(currentScalingVersionFolder.contains("DL")) {
            screen_size_cmd = screen_size_cmd + default_screen_size;
            font_cmd = font_cmd + largest_font;
        }
        else{
            Logger.debug("No correct scaling is specified. aborting...");
            System.exit(0);
        }

        // excute the commands for all devices
        for (String device : OwlConstants.availableEmulators) {
            // set the device
            String curr_font_cmd= String.format(font_cmd, device);
            String curr_screen_size_cmd= String.format(screen_size_cmd, device);
            // run the commands
            runCommand(curr_screen_size_cmd, null, null);
            runCommand(curr_font_cmd, null, null);
        }
            // excute the commands
            // set the screen size

        }

    public static double biasedRandomFactor(double x) {

        // for mutation
        Random random = new Random();
        double base = random.nextDouble() * x;
        double tail = (random.nextDouble() < 0.3) ? (1 - x) * random.nextDouble() : 0; // Adjust the probability (0.1) to control how often the value exceeds x.
        return base + tail;

    }

    public static boolean isFabInCollisionIssue(Issue issue) {

        DomNode node1 = null;
        try {
            node1 = issue.getPageUnderTestEdge().getNode1().getDomNode().getData();
        } catch (NullPointerException e) {
            Logger.error("Null pointer exception in isFabInCollisionIssue");
        }
        DomNode node2 = null;
        try {
            node2 = issue.getPageUnderTestEdge().getNode2().getDomNode().getData();
        } catch (NullPointerException e) {
            Logger.error(e);
            //Logger.error("Null pointer exception in isFabInCollisionIssue");
        }
        String node1ID="";
        String node2ID="";
    if(node1!=null ){
        // get the ids of the two nodes
         node1ID=node1.getId();
         if(node1ID==null){
             node1ID="";
         }
    }
    if(node2!=null) {
         node2ID = node2.getId();
        if(node2ID==null) {
            node2ID = "";
        }
    }
        //check if either of them is fab
        if(node1ID.toLowerCase().contains("fab") || node2ID.toLowerCase().contains("fab")){
            return true;
        }
        return false;
    }

    public static boolean ScrollCollideWithTabs(Issue issue) {
        //TabLayout

        Node<DomNode> node1 = null;
        try {
            node1 = issue.getBaselineEdge().getNode1().getDomNode();
        } catch (NullPointerException e) {
            Logger.error(e);
            Logger.error("Null pointer exception in ScrollCollideWithTabs");
        }
        Node<DomNode> node2 = null;
        try {
            node2 = issue.getBaselineEdge().getNode2().getDomNode();

        } catch (NullPointerException e) {
            Logger.error(e);
            Logger.error("Null pointer exception in ScrollCollideWithTabs");
        }
        if (node1 == null || node2 == null) {
            return true;
        }

        Node<DomNode> parent1 = node1.getParent();
        Node<DomNode> parent2 = node2.getParent();


    // Check if either node1 or node2 is a child of a ScrollView or TabLayout
        boolean isChildOfScrollViewOrTabLayout = false;

        while (parent1 != null && parent2 != null) {
            if ((isElementScrollView(parent1) && isElementTabLayout(parent2)) ||
                    (isElementTabLayout(parent1) && isElementScrollView(parent2))) {
//                isChildOfScrollViewOrTabLayout = true;
//                break;
                return true;
            }

            if (parent1 != null) {
                parent1 = parent1.getParent();
            }

            if (parent2 != null) {
                parent2 = parent2.getParent();
            }
        }
        
        
        
        return false;
    }

    private static boolean isElementScrollView(Node<DomNode> parent1) {
String tagName=parent1.getData().getTagName();
        if(tagName!=null && tagName.toLowerCase().contains("scrollview")){
            return true;
        }
        return false;
    }

    private static boolean isElementTabLayout(Node<DomNode> parent1) {
        String att= parent1.getData().getAttr("StaticTagName");
        if(att!=null && att.toLowerCase().contains("tabs")){
            return true;
        }
        return false;
    }

    private static void isParentScrollView(Node<DomNode> node1) {
        // check if the node is a child of a scroll view and return true if it is or false if it is not. Please write the code
        

    }

    public static boolean isNotLeaveNodes(Issue issue) {
        //
        Node<DomNode> node1 = null;
        try {
            node1 = issue.getBaselineEdge().getNode1().getDomNode();
        } catch (NullPointerException e) {
            Logger.error(e);
            Logger.error("Null pointer exception in ScrollCollideWithTabs");
        }
        Node<DomNode> node2 = null;
        try {
            node2 = issue.getBaselineEdge().getNode2().getDomNode();

        } catch (NullPointerException e) {
            Logger.error(e);
            Logger.error("Null pointer exception in ScrollCollideWithTabs");
        }
        if(node1==null || node2==null){
            return true;
        }
        boolean notAllNodesAreLeaveNodes=false;
        // check if node1 is a leave node
        if(node1.getChildren()!=null &&node1.getChildren().size()>0){
            return true;
        }
        // check if node2 is a leave node
        if(node2.getChildren()!=null &&node2.getChildren().size()>0){
            return true;
        }
        return false;
    }

    public static int getRandomOneOrZero(double percentageOfOne) {
        Random random = new Random();
        double randomDouble = random.nextDouble();
       Logger.debug("Random number: " + randomDouble);
        int result = randomDouble < percentageOfOne ? 1 : 0;
        Logger.debug("Generated number: " + result);

        return result;
    }
    public static boolean isLeaveNode(Node<DomNode> node){
        if(node.getChildren()!=null &&node.getChildren().size()>0){
            return false;
        }
        return true;
    }

    public static void copyAPKFile(String source, String dest) {
        // Specify the source and target paths
        Path sourcePath = Paths.get(source);
        Path targetPath = Paths.get(dest);

        // Copy the APK file from the source to the target path
        try {
            Files.copy(sourcePath, targetPath);
            System.out.println("APK file copied successfully!");
        } catch (IOException e) {
            System.err.println("An error occurred while copying the APK file: " + e.getMessage());
        }
    }
}
