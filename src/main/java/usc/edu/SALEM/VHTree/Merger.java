package usc.edu.SALEM.VHTree;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.DomUtils;
import gatech.xpert.dom.MatchResult;
import usc.edu.SALEM.SALEM;
//import edu.usc.sql.color.RGB;
//import edu.usc.sql.repair.ElementModification;
//import edu.usc.sql.repair.JavaRepair;
//import edu.usc.sql.repair.JimpleRepair;
//import edu.usc.sql.repair.XMLRepair;
//import edu.usc.sql.scheme.BackgroundColorScheme;
//import edu.usc.sql.scheme.CCGNode;
//import edu.usc.sql.scheme.ColorConflictGraph;
//import edu.usc.sql.scheme.TextColorScheme;

import java.io.*;
import java.util.*;

public class Merger {
    public Map<DomNode, DomNode> listMatched;
    public static String resultFile = "matchTmp.csv";
    public static String separator = ",";
    public Map<String, DomNode> act2DomRoot;
    public static String staticPath = "/home/ali/AppSet/accessibility/scripts/apks_folder/guianalzyer/";
    public static String resourcePath = "/home/ali/AppSet/accessibility/scripts/apks_folder/decompiled_apks/";
    public static String dynamicPath = "/home/ali/AppSet/accessibility/scripts/apks_folder/dynamic_layouts/";
    public static String refinePath = "/home/ali/AppSet/accessibility/scripts/apks_folder/dynamic_layouts/";
//    public static String mergedPath = "/home/ali/AppSet/accessibility/scripts/apks_folder/merged_layouts/";

    public static void main(String[] args) {
        String apk_name = "com.bytecode.aiodownloader";
        String activity_name = "MainActivity";
//
//      String before_refine="/home/ali/AppSet/accessibility/scripts/apks_folder/dynamic_layouts/"+apk_name+"/"
//             +activity_name+".xml";
//      String refined_path="/home/ali/AppSet/accessibility/scripts/apks_folder/dynamic_layouts/"+apk_name+"/refined/"
//              ;
//      XMLUtils.extractActivityLayout(before_refine,refined_path);
//     System.exit(0);
        String staticPath = "/home/ali/AppSet/accessibility/scripts/apks_folder/guianalzyer/";
        String resourcePath = "/home/ali/AppSet/accessibility/scripts/apks_folder/decompiled_apks/";
        String dynamicPath = "/home/ali/AppSet/accessibility/scripts/apks_folder/dynamic_layouts/";
        String refinePath = "/home/ali/AppSet/accessibility/scripts/apks_folder/dynamic_layouts/";

        staticPath = staticPath + apk_name;
        dynamicPath = dynamicPath + apk_name + "/";
        refinePath = refinePath + apk_name + "/refined/";
        resourcePath = resourcePath + apk_name + "/res/";


//        try {
//            Scanner scanner = new Scanner(new File("app-list"));
//            while (scanner.hasNext()) {
//                String s = scanner.nextLine();
        long start = System.nanoTime();
        //      String appPkg = args[0];//s.substring(0, s.length() - 4);
        String path = dynamicPath;
        String refineRootFolder = refinePath;
        String staticRootFolder = staticPath;
//                Merger merger = new Merger();
        Merger_TT merger = new Merger_TT();
        merger.preprocessDynamicFolder(path, refineRootFolder);
        Map<String, Set<String>> act2XML = merger.buildActivityMap(refineRootFolder);
        System.out.println("*********** Start matching...");
        merger.merge(staticRootFolder, refineRootFolder, act2XML, resourcePath);
        double runtime = (System.nanoTime() - start) * 1.0e-09;
        System.out.println("Total time: " + runtime);
        // For study the corner cases
//                String matchResult = merger.matchStaticWithDynamic(staticRootFolder, refineRootFolder + appPkg + File.separator + "refined", act2XML);
//                merger.saveResultForApp(appPkg + separator + matchResult);
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

    }


    // Ali : Method for Search based
    public void prepare_app_layout(String apk_name, Set<String> problematicActivities, String originalDecompiledPath,
                                   String dynamicPath, String guianalzyerPath) {

        String resourcePath = originalDecompiledPath + "/res/";
        String preprocessedDynamicPath = dynamicPath + "/refined/";
        // (1) First preprocess activities to extract content only as perfromed by Mian
        Merger_TT merger = new Merger_TT();
        merger.preprocessDynamicFolder(dynamicPath, preprocessedDynamicPath); // I am adding an option to trim the children of webviews (keeping only the the webview)
        Map<String, Set<String>> act2XML = merger.buildActivityMap(preprocessedDynamicPath);
        // (2) Match static and dynamic as well as perfrom dummy matching
        merger.merge(guianalzyerPath, preprocessedDynamicPath, act2XML, resourcePath);


//        long start = System.nanoTime();
//        //      String appPkg = args[0];//s.substring(0, s.length() - 4);
//        String path = dynamicPath;
//        String refineRootFolder = refinePath;
//        String staticRootFolder = staticPath;
////                Merger merger = new Merger();
//        Merger_TT merger = new Merger_TT();
//        merger.preprocessDynamicFolder(path, refineRootFolder);
//        Map<String, Set<String>> act2XML = merger.buildActivityMap(refineRootFolder);
//        System.out.println("*********** Start matching...");
//        merger.merge(staticRootFolder, refineRootFolder, act2XML, resourcePath);
//        double runtime = (System.nanoTime() - start) * 1.0e-09;
//        System.out.println("Total time: " + runtime);
//
//        return mergedPath;
    }

    public String prepare_app_layout(String apk_name) {
        staticPath = staticPath + apk_name;
        dynamicPath = dynamicPath + apk_name + "/";
        refinePath = refinePath + apk_name + "/refined/";
        resourcePath = resourcePath + apk_name + "/res/";


        long start = System.nanoTime();
        //      String appPkg = args[0];//s.substring(0, s.length() - 4);
        String path = dynamicPath;
        String refineRootFolder = refinePath;
        String staticRootFolder = staticPath;
//                Merger merger = new Merger();
        Merger_TT merger = new Merger_TT();
        merger.preprocessDynamicFolder(path, refineRootFolder);
        Map<String, Set<String>> act2XML = merger.buildActivityMap(refineRootFolder);
        System.out.println("*********** Start matching...");
        merger.merge(staticRootFolder, refineRootFolder, act2XML, resourcePath);
        double runtime = (System.nanoTime() - start) * 1.0e-09;
        System.out.println("Total time: " + runtime);

//        return mergedPath;
        return "mergedPath";
    }
//
//    public void getMatchedElements(String appName) {
//        String dynamicRootFolder = dynamicPath + appName;
//        String refineRootFolder = refinePath + appName + File.separator + "refined";
//        String staticRootFolder = staticPath + appName;
//        Merger merger = new Merger();
//        merger.preprocessDynamicFolder(dynamicRootFolder, refineRootFolder);
//        Map<String, Set<String>> act2XML = merger.buildActivityMap(refineRootFolder);
//        merger.merge(staticRootFolder, refineRootFolder, act2XML);
//    }

    public String getWidgetOriginXML(String id) {
        listMatched.keySet();
        return "not yet";
    }

    public void preprocessDynamicFolder(String dynamicResultPath, String refineRootPath) {
        String layoutPath = dynamicResultPath;
        //   String appName = dynamicResultPath.substring(dynamicResultPath.lastIndexOf( File.separator) + 1);
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

    public Map<String, Set<String>> buildActivityMap(String refineFolderPath) {
        File folder = new File(refineFolderPath);
        Map<String, Set<String>> activityToDynamicXMLs = Maps.newHashMap();
        for (File f : folder.listFiles()) {
            String fileName = f.getName();
            if (!fileName.endsWith("xml")) {
                continue;
            }
            String activityName = fileName.substring(0, fileName.lastIndexOf("."));
            //  String activityName = fileName.substring(0, fileName.lastIndexOf("_"));

            Set<String> actFileSet = activityToDynamicXMLs.get(activityName);
            if (actFileSet == null) {
                actFileSet = Sets.newHashSet();
                activityToDynamicXMLs.put(activityName, actFileSet);
            }
            actFileSet.add(fileName);
        }

//        for (Map.Entry<String, Set<String>> entry : activityToDynamicXMLs.entrySet()) {
//            String activity = entry.getKey();
//            System.out.println("Checking " + activity + "...");
//            Set<String> xmlSet = entry.getValue();
//            Iterator<String> xmlIt = xmlSet.iterator();
//            String controlFileName = xmlIt.hasNext() ? xmlIt.next() : "";
//            File controlFile = new File(refineFolderPath + File.separator + controlFileName);
//            while (xmlIt.hasNext()) {
//                String testFileName = xmlIt.next();
//                System.out.println(controlFileName + " vs. " + testFileName);
//                File testFile = new File(refineFolderPath + File.separator + testFileName);
//                XMLUtils.isXMLContentEqualWithDetails(controlFile, testFile);
//            }
//        }
        return activityToDynamicXMLs;
    }

    public void merge(String staticLayoutFolderPath, String dynamicLayoutFolderPath, Map<String, Set<String>> act2xmls) {
        File staticResultFolder = new File(staticLayoutFolderPath);
        String appPkg = staticLayoutFolderPath.substring(staticLayoutFolderPath.lastIndexOf(File.separator) + 1);
        String realPkg = appPkg;
        if (realPkg.contains("_")) {
            realPkg = appPkg.substring(0, appPkg.indexOf("_"));
        }

        act2DomRoot = Maps.newHashMap();
        for (File f : staticResultFolder.listFiles()) {
            String fileName = f.getName();
            String activityName = getActivityNameFromXMLFileName(fileName);
            if (!activityName.startsWith(realPkg)) {
                //continue;  Ali stopped this while initial code running
            }

            System.out.println("\nCurrent Activity: " + activityName);

            Set<String> activityFileSet = act2xmls.get(activityName);
            DomNode dynamicRoot = null;
            TreePair pair = new TreePair();  //Ali using the extendedclass to use the new merge method
            if (activityFileSet != null && !activityFileSet.isEmpty()) {
                Iterator<String> it = activityFileSet.iterator();
                String firstFilePath = dynamicLayoutFolderPath + File.separator + it.next();
                dynamicRoot = XMLUtils.buildTree(firstFilePath);
                int i = 0;
                // Step 1 (optional): merge dynamic XML files
                if (activityFileSet.size() > 1) {
                    System.out.println("====== Dynamic vs. Dynamic ======");
                    while (it.hasNext()) {
                        String fPath = dynamicLayoutFolderPath + File.separator + it.next();
                        DomNode nextRoot = XMLUtils.buildTree(fPath);
                        dynamicRoot = pair.merge(dynamicRoot, nextRoot);
                        // Update new XPath
                        List<DomNode> workList = Lists.newArrayList(dynamicRoot);
                        while (!workList.isEmpty()) {
                            DomNode curr = workList.remove(0);
                            // reset the matched value to false
                            curr.setMatched(false);
                            String newXPath = DomUtils.getXPath(curr);
                            //if (!newXPath.equals(curr.getxPath())) {
                            if (curr.getxPath() == null) {
                                curr.setxPath(newXPath);
                            }
                            workList.addAll(curr.getChildren());
                        }
                        XMLUtils.dump(dynamicRoot, "dynamic" + (i++) + ".xml");
                    }
                }
            }

            System.out.println("====== Static vs. Dynamic ======");
            DomNode staticRoot = XMLUtils.buildTree(f.getPath());
            System.out.println("Static View Number: " + getViewNumber(staticRoot));
            System.out.println("Dynamic View Number: " + getViewNumber(dynamicRoot)); // after merge
            // Step 2: merge the static with the final tree of dynamic XMLs
            if (dynamicRoot != null) {
                TreePair_TT pair_TT = new TreePair_TT();
                staticRoot = pair_TT.merge(staticRoot, dynamicRoot);
                System.out.println("Hybrid View Number: " + getViewNumber(staticRoot)); // after merge
                //      Map<DomNode, DomNode> listMatched = pair.get_matched(staticRoot, dynamicRoot);
            }
            if (act2DomRoot.containsKey(activityName)) {
                System.err.println(activityName + "has more than 1 static layout file!");
                continue;
            } else {
                act2DomRoot.put(activityName, staticRoot);
            }
        }
        dumpAllMergeFiles(appPkg, act2DomRoot);
    }

    public static void dumpAllMergeFiles(String appPkg, Map<String, DomNode> activityToDomRoot) {
//        String savingPath = mFix.getMergePath() + File.separator + appPkg;
        String savingPath = SALEM.getMergePath(); //for search based the path contain subject
        System.out.println("SAVE PATH: " + savingPath);
        File savingFile = new File(savingPath);
        if (!savingFile.exists()) {
            savingFile.mkdirs();
        }
        for (Map.Entry<String, DomNode> entry : activityToDomRoot.entrySet()) {
            String activity = entry.getKey();
            DomNode root = entry.getValue();
            XMLUtils.dump(root, savingPath + File.separator + activity + ".xml");
            String inputFolder = SALEM.getOriginalDynamicLayoutFilePath();
            String outputFolder = savingPath;
            //  copy_image_file(activity + ".xml", inputFolder, outputFolder);
        }
    }

    public static void copy_image_file(String fileName, String inputFolder, String outputFolder) {
        String pngFile = fileName.replace("xml", "png");

        File srcPng = new File(inputFolder + File.separator + pngFile);

        if(srcPng.exists() && !srcPng.isDirectory()) {

            File desPng = new File(outputFolder + File.separator + pngFile.substring(pngFile.indexOf("_") + 1));
            try {
                Files.copy(srcPng, desPng);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    static int getViewNumber(DomNode root) {
        int cnt = 0;
        Queue<DomNode> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            DomNode cur = queue.poll();
            cnt++;
            queue.addAll(cur.getChildren());
        }
        return cnt;
    }

    /**
     * Return a result entry string for large scale of study
     *
     * @param staticResultPath
     * @param dynamicResultPath
     * @param act2xml
     * @return
     */
    public String matchStaticWithDynamic(String staticResultPath, String dynamicResultPath, Map<String, Set<String>> act2xml) {
        File staticResultFolder = new File(staticResultPath);
        String appPkg = staticResultPath.substring(staticResultPath.lastIndexOf(File.separator) + 1);
        if (appPkg.contains("_")) {
            appPkg = appPkg.substring(0, appPkg.indexOf("_"));
        }
        File[] staticFiles = staticResultFolder.listFiles();
        if (staticFiles.length == 0 || act2xml.isEmpty())
            return "true,,";
        boolean staticUnique = false;
        boolean dynamicUnique = false;
        boolean staticMore = false;
        for (File f : staticFiles) {
            String fileName = f.getName();
            String activityName = getActivityNameFromXMLFileName(fileName);
//            if (!activityName.startsWith(appPkg)) {
//                continue;
//            }
            System.out.println("\nCurrent Activity: " + activityName);
            DomNode staticRoot = XMLUtils.readXML(f.getPath());

            Set<String> activityFileSet = act2xml.get(activityName);
            TreePair pair = new TreePair();
            if (activityFileSet != null && !activityFileSet.isEmpty()) {
                Iterator<String> it = activityFileSet.iterator();
                while (it.hasNext()) {
                    String fPath = dynamicResultPath + File.separator + it.next();
                    System.out.println("===Matching " + fPath.substring(fPath.lastIndexOf(File.separator) + 1));
                    DomNode currentDynamicRoot = XMLUtils.readXML(fPath);
                    MatchResult matchResult = pair.match(staticRoot, currentDynamicRoot);
                    if (!matchResult.getUnmatched1().isEmpty()) {
                        staticUnique = true;
                    }
                    if (!matchResult.getUnmatched2().isEmpty()) {
                        dynamicUnique = true;
                    }
                }
            } else {
                staticMore = true;
            }
        }

        return "false," + staticUnique + separator + dynamicUnique + separator + staticMore;
    }

    public static String getActivityNameFromXMLFileName(String xmlName) {
        String fileName = xmlName.substring(0, xmlName.lastIndexOf("."));
        char[] array = fileName.toCharArray();
        int firstDigitIdx = array.length;
        if (Character.isDigit(fileName.charAt(fileName.length() - 1))) {
            for (int i = array.length - 5; i >= 0; i--) {
                char c = array[i];
                if (Character.isDigit(c)) {
                    firstDigitIdx = i;
                }
            }
        }
        return fileName.substring(0, firstDigitIdx);
    }

    public void saveResultForApp(String recordLine) {
        File file = new File(resultFile);
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(file, true));
            if (file.exists() && file.length() == 0) {
                pw.println("AppName" + separator + "static/dynamicNull" + separator + "staticUnique" + separator +
                        "dynamicUnique" + separator + "staticMore");
            }
            pw.println(recordLine);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public DomNode getDomRoot(String activityName) {
        return act2DomRoot.get(activityName);
    }
}
