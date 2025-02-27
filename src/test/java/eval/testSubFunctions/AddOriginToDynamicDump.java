package eval.testSubFunctions;

import usc.edu.OwlEye.OwlConstants;
import usc.edu.SALEM.VHTree.XMLUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import gatech.xpert.dom.DomNode;
import usc.edu.SALEM.SALEM;
import usc.edu.SALEM.VHTree.Merger;
import usc.edu.SALEM.VHTree.Merger_TT;
import usc.edu.SALEM.util.ApplyFixToDecompiled;
import org.w3c.dom.Element;
import usc.edu.SALEM.util.LoadConfig;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AddOriginToDynamicDump {
    public static boolean IS_LAYOUT_INSPECTOR;
    //    public String subject="";
    public static HashMap<String, DomNode> act2DomRoot;

    public static void main(String[] args) {


        HashMap<String, String> propConfig =   LoadConfig.getInstance().getConfig_data();
//        String subject = "";
        String subject = "com.adzenze.FashionDesignFlatSketch";
//        String subject= "com.ahorcado";
//        subject="ca.rmen.android.poetassistant";
//        subject="com.crmdev.safadometro";
//        subject="com.bytecode.aiodownloader";
//        subject="com.daily.calling";
//        subject="arity.calculator";
//        subject="com.android.keepass";
//            subject="com.danhasting.radar";
//            subject="ca.rmen.android.frenchcalendar";
//            subject = "com.bytesforge.linkasanote.mock";
//            subject="androdns.android.leetdreams.ch.androdns";
//        subject="com.crmdev.safadometro";
//        subject="cz.test.calculator";
//        subject="crisar.aege.com.leydeohm";
//        subject="uk.co.bitethebullet.android.token";
//        subject = "com.americanexpress.android.acctsvcs.us";
//        subject = "souch.smp";
        String apk_id="";
        String apkName = "";
//        subject = "org.varunverma.INRTrainStatusAlarm";
        String activity_name="";

        String ui_version_type= "default_font";
        System.out.println("ARGS:" + args.length);
        if (args.length > 0) {
            System.out.println("SUBJECT: " + args[0]);
            apk_id = args[0];
            apkName = args[1];
             activity_name= args[2];
             ui_version_type= args[3];
        }
        boolean copyPNG = true; // copy the PNG
        IS_LAYOUT_INSPECTOR = true;
        if (SALEM.basePath == null) {
            SALEM.basePath = "/home/testing/AppSet/accessibility/TT_scripts";
        }

        String originalStaticFiles = propConfig.get("decompiled_apks") + subject;;
        String augmented_path= propConfig.get("augmented_layouts") + File.separator+propConfig.get("device_title")+File.separator+subject+File.separator+ui_version_type;
        String mergedLayoutPath = propConfig.get("merged_layouts") +File.separator+propConfig.get("device_title")+File.separator+ subject+ File.separator+ui_version_type;
        String refinedDynamicPath = propConfig.get("dynamic_layout_folder")+File.separator+propConfig.get("device_title")+File.separator+ subject +File.separator+ ui_version_type+ File.separator+
                "refined/";

        //SALEM.basePath + File.separator + "apks_folder/dynamic_layouts/" +
          //      File.separator + subject + "/refined/";
        //SALEM.basePath + File.separator + "apks_folder/decompiled_apks/" + File.separator + subject;
//        String augmented_path = SALEM.basePath + File.separator + "apks_folder/AndroidDumpMerger/" + File.separator + subject;
//        String mergedLayoutPath = SALEM.basePath + File.separator + "apks_folder/merged_layouts/" + File.separator + subject + "/New";

//        String originalStaticFiles = SALEM.basePath + File.separator + "apks_folder/decompiled_apks/" + File.separator + subject;
//        String augmented_path = SALEM.basePath + File.separator + "apks_folder/AndroidDumpMerger/" + File.separator + subject;
//        String mergedLayoutPath = SALEM.basePath + File.separator + "apks_folder/merged_layouts/" + File.separator + subject + "/New";
        SALEM.setOriginalDecompiled(originalStaticFiles);
        SALEM.setOriginalDynamicLayoutFilePath(augmented_path);
        SALEM.setCurrentApkPackage(subject);
        SALEM.setMergePath(mergedLayoutPath);
        Merger_TT merger = new Merger_TT();
        String resourcePath = SALEM.getOriginalDecompiled() + "/res/";
        resourcePath=resourcePath.trim();
        File dynamicLayoutFolder = new File(augmented_path);
        act2DomRoot = Maps.newHashMap();
//        String refinedDynamicPath = SALEM.basePath + File.separator + "apks_folder/dynamic_layouts/" +
//                File.separator + subject + "/refined/";
//        addOriginFiles(dynamicLayoutFolder, resourcePath); I used this for SALEM
        addOriginForSingleFile(dynamicLayoutFolder, resourcePath, activity_name);

        // also copy the corresponding png files

        File mergedFolder = new File(mergedLayoutPath);

        for (File f : mergedFolder.listFiles()) {
            String fileName = f.getName();
            if (fileName.endsWith(".xml")) {
                if (copyPNG) {
                    // copy the corresponding png to the outputFolder
                    String pngFile = fileName.replace("xml", "png");
                    File srcPng = new File(refinedDynamicPath + File.separator + pngFile);
                    File desPng = new File(mergedLayoutPath + File.separator + pngFile.substring(pngFile.indexOf("_") + 1));
                    try {
                        Files.copy(srcPng, desPng);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


// Before fixing origin files
//        for (File f : dynamicLayoutFolder.listFiles()) {
//            String fileName = f.getName();
//            String activityName = merger.getActivityNameFromXMLFileName(fileName);
////            if (!activityName.startsWith(realPkg) || fileName.contains(".png")) {
//            if (fileName.contains(".png")) {
//
//                continue;  //Ali stopped this while initial code running
//
//            }
//            System.out.println("\nCurrent Activity: " + activityName);
//            DomNode dynamicMergedRoot = mfix.merge.XMLUtils.buildTree(f.getAbsolutePath());
//            merger.add_missing_originsInspector(dynamicMergedRoot, resourcePath);
//            act2DomRoot.put(activityName, dynamicMergedRoot);
//            merger.dumpAllMergeFiles(subject, act2DomRoot);
//
//
//        }

    }
    private static void addOriginForSingleFile(File dynamicLayoutFolder, String resourcePath,String activityName) {
        // iterate over activities

        List<String> layoutFiles = get_list_of_layout(resourcePath);
        Merger_TT merger = new Merger_TT();
        HashMap<String, String> staticLayoutSearched = new HashMap<>();
        for (File f : dynamicLayoutFolder.listFiles()) {
            String fileName = f.getName();
            if (fileName.contains(".png") || !fileName.endsWith(".xml")) {

                continue;  //Ali stopped this while initial code running

            }

            String fileAct = Merger.getActivityNameFromXMLFileName(fileName);
            if (!activityName.equalsIgnoreCase(fileAct)){
                //Not same activity so continue
                continue;
            }
            System.out.println("Activity-Ali: " + fileName);
            // get the root of the activity
            HashMap<String, DomNode> foundDomNodes = new HashMap<>();
            DomNode dynamicMergedRoot = XMLUtils.buildTree(f.getAbsolutePath());
            // find the layout that has the root
            String rootOrigin = findCorrectLayoutForActivityRoot(dynamicMergedRoot, layoutFiles, staticLayoutSearched);
            System.out.println(" Root Origin file: " + rootOrigin);
            if (rootOrigin != null) {
                File originFile = new File(rootOrigin);
                org.w3c.dom.Element staticFileRoot = ApplyFixToDecompiled.readStaticXML(originFile);
                add_originAtt(dynamicMergedRoot, staticFileRoot, rootOrigin);
                foundDomNodes.put(dynamicMergedRoot.getxPath(), dynamicMergedRoot);
                findchildrenINSameOrigin(dynamicMergedRoot, staticFileRoot, foundDomNodes, rootOrigin);
                staticLayoutSearched.put(rootOrigin, rootOrigin);
            }
            // now we know the origin of the root
//            int x =0;
            searchRemainingNodes(dynamicMergedRoot, foundDomNodes, layoutFiles, staticLayoutSearched);
            int x = 0;
            addNotFoundForRemainingNodes(dynamicMergedRoot);
            act2DomRoot.put(activityName, dynamicMergedRoot);
        }
        Merger.dumpAllMergeFiles(SALEM.getCurrentApkPackage(), act2DomRoot);


    }
    private static void addOriginFiles(File dynamicLayoutFolder, String resourcePath) {
        // iterate over activities

        List<String> layoutFiles = get_list_of_layout(resourcePath);
        Merger_TT merger = new Merger_TT();
        HashMap<String, String> staticLayoutSearched = new HashMap<>();
        for (File f : dynamicLayoutFolder.listFiles()) {
            String fileName = f.getName();
            if (fileName.contains(".png") || !fileName.endsWith(".xml")) {

                continue;  //Ali stopped this while initial code running

            }
            String activityName = Merger.getActivityNameFromXMLFileName(fileName);

            System.out.println("Activity-Ali: " + fileName);
            // get the root of the activity
            HashMap<String, DomNode> foundDomNodes = new HashMap<>();
            DomNode dynamicMergedRoot = XMLUtils.buildTree(f.getAbsolutePath());
            // find the layout that has the root
            String rootOrigin = findCorrectLayoutForActivityRoot(dynamicMergedRoot, layoutFiles, staticLayoutSearched);
            System.out.println(" Root Origin file: " + rootOrigin);
            if (rootOrigin != null) {
                File originFile = new File(rootOrigin);
                org.w3c.dom.Element staticFileRoot = ApplyFixToDecompiled.readStaticXML(originFile);
                add_originAtt(dynamicMergedRoot, staticFileRoot, rootOrigin);
                foundDomNodes.put(dynamicMergedRoot.getxPath(), dynamicMergedRoot);
                findchildrenINSameOrigin(dynamicMergedRoot, staticFileRoot, foundDomNodes, rootOrigin);
                staticLayoutSearched.put(rootOrigin, rootOrigin);
            }
            // now we know the origin of the root
//            int x =0;
            searchRemainingNodes(dynamicMergedRoot, foundDomNodes, layoutFiles, staticLayoutSearched);
            int x = 0;
            addNotFoundForRemainingNodes(dynamicMergedRoot);
            act2DomRoot.put(activityName, dynamicMergedRoot);
        }
        Merger.dumpAllMergeFiles(SALEM.getCurrentApkPackage(), act2DomRoot);


    }

    private static void addNotFoundForRemainingNodes(DomNode dynamicMergedRoot) {
        List<DomNode> workList = Lists.newArrayList(dynamicMergedRoot.getChildren());
        while (!workList.isEmpty()) {
            DomNode curr = workList.remove(0);
            String OriginVal = curr.getAttr("origin");
            if (OriginVal == null) { // No origin defined yet
                curr.setAttr("origin", "NOTFOUND");
                if (!IS_LAYOUT_INSPECTOR) {


                    curr.setAttr("android:layout_height", "fill_parent");
                    curr.setAttr("android:layout_width", "fill_parent");

                } else {
                    if (curr.getId() != null && !curr.getId().isEmpty()) {
                        curr.setAttr("origin", "CHECKNOTFOUND");

                    } else {

                        curr.setAttr("android:layout_height", "fill_parent");
                        curr.setAttr("android:layout_width", "fill_parent");
                    }
                }
                // To record that this was found using naive search in XML
                curr.setAttr("naive_xml_file_matching", "true");
            }
            workList.addAll(curr.getChildren());
        }
    }

    private static void searchRemainingNodes(DomNode dynamicMergedRoot, HashMap<String, DomNode> foundDomNodes, List<String> layoutFiles, HashMap<String, String> staticLayoutSearched) {
        List<DomNode> workList = Lists.newArrayList(dynamicMergedRoot.getChildren());
        while (!workList.isEmpty()) {
            DomNode curr = workList.remove(0);
            if (!foundDomNodes.containsKey(curr.getxPath())) {
                String nodeOrigin = findCorrectLayoutForActivityRoot(curr, layoutFiles, staticLayoutSearched);
                if (nodeOrigin != null) {
                    File originFile = new File(nodeOrigin);
                    org.w3c.dom.Element staticFileRoot = ApplyFixToDecompiled.readStaticXML(originFile);
                    add_originAtt(curr, staticFileRoot, nodeOrigin);
                    foundDomNodes.put(dynamicMergedRoot.getxPath(), dynamicMergedRoot);
                    findchildrenINSameOrigin(curr, staticFileRoot, foundDomNodes, nodeOrigin);
                    staticLayoutSearched.put(nodeOrigin, nodeOrigin);
                }
            }
            workList.addAll(curr.getChildren());
        }
    }

    private static void findchildrenINSameOrigin(DomNode dynamicMergedRoot, Element staticFileRoot, HashMap<String, DomNode> foundDomNodes, String staticFilePath) {
        // Iterate over children of staticFileRoot and    check if you cannnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn find any
        List<DomNode> workList = Lists.newArrayList(dynamicMergedRoot);
        while (!workList.isEmpty()) {
            DomNode curr = workList.remove(0);
            if (!foundDomNodes.containsKey(curr.getxPath())) {

                String effective_id = curr.getAttributes().get("effective-id");
                if (effective_id == null) {
                    effective_id = curr.getId();
                }
                if (effective_id != null) {
                    effective_id = effective_id.substring(effective_id.lastIndexOf("/") + 1);
                    Element foundElement = ApplyFixToDecompiled.searchForNode(staticFileRoot, effective_id, false);
                    if (foundElement == null) {  // Not Found
                        continue;
                    } else {
                        add_originAtt(curr, foundElement, staticFilePath);
                        foundDomNodes.put(curr.getxPath(), curr);
                    }

                }
            }//not cointed in key
            workList.addAll(curr.getChildren());

        }
    }

    private static String findCorrectLayoutForActivityRoot(DomNode dynamicMergedRoot, List<String> layoutFile, HashMap<String, String> staticLayoutSearched) {
        String rootXpath = dynamicMergedRoot.getxPath();
        boolean layoutFound = false;
        String filePath = "";
        for (String folder : layoutFile
        ) {
            if (layoutFound) {
                break;
            }
            System.out.println("Folder: " + folder);
            File layoutF = new File(folder);
            if (layoutF.getName().contains("-") && !layoutF.getName().contains("layout-v")) {  // to skip -land -large and other different layout
                continue;
            }
            // how to sort by latest api

            for (File f : layoutF.listFiles()) {

                if (staticLayoutSearched.containsKey(f.getAbsolutePath())) {
                    continue;
                }
                if (layoutFound) {
                    break;
                }
//                45479df7-be95-11ea-96cc-844bf5a5b2fd

//                Node<DomNode> r = XMLUtils.readCrawledXML_T(f.getAbsolutePath());
                DomNode r = XMLUtils.buildTree(f.getAbsolutePath());


                boolean found = isElemenstEqual(dynamicMergedRoot, r);
                if (found) {
                    boolean allChildrenNoID = true; // in case all children without ids
                    if (r.getChildren() != null && r.getChildren().size() > 0) {

                        for (DomNode dChild : dynamicMergedRoot.getChildren()
                        ) {
                            if (dChild.getId() != null && !dChild.getId().isEmpty()) {
                                allChildrenNoID = false;
                            }
                            for (DomNode sCh : r.getChildren()) {
                                if (isElemenstEqual((dChild), sCh)) {
                                    layoutFound = true;
                                    filePath = f.getAbsolutePath();
                                    return filePath;
                                }
//                                else{
//                                    if(sCh.getId()!=null && !sCh.getId().isEmpty()){
//                                        allChildrenNoID=false;
//                                    }
//                                }
                            }
                        }
                        if (allChildrenNoID) {
                            layoutFound = true;
                            filePath = f.getAbsolutePath();
                            return filePath;
                        }
                    } else {
                        layoutFound = true;
                        filePath = f.getAbsolutePath();
                        return filePath;
                    }
//                dynamicRoot=r.getData().get
//                Element staticFileRoot = ApplyFixToDecompiled.readStaticXML(f);

                }
            }
        }
        if (layoutFound) {
            return filePath;
        }
        return null;
    }

    private static boolean isElemenstEqual(DomNode dynamicMergedRoot, DomNode r) {

        if (dynamicMergedRoot.getId() != null && !dynamicMergedRoot.getId().isEmpty()) {
            String mergedID = dynamicMergedRoot.getId();
            String effective_id = dynamicMergedRoot.getAttributes().get("effective-id");
            if (effective_id != null) {
                mergedID = effective_id;
            }
            if (r.getId() != null && !r.getId().isEmpty()) {
//                String mergedID = dynamicMergedRoot.getId();
                String staticID = r.getId();
                return mergedID.equalsIgnoreCase(staticID);
            }
        }

        return false;
    }

    public static void add_originAtt(DomNode mergedNode, org.w3c.dom.Element foundElement, String orignalFilePath) {
        String node2Tag = foundElement.getTagName();
        mergedNode.setAttr("StaticTagName", node2Tag);
        mergedNode.setAttr("naive_xml_file_matching", "true");
        mergedNode.setAttr("origin", orignalFilePath);

    }
//    public void add_missing_originsInspector(DomNode dynamicRoot, String resourcePath) {
//        // Iterate over nodes and if a node is missing origin attribute then do dummay matching these are nodes not found in static analysis
//
//        System.out.println(" ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
//        DomNode d = dynamicRoot;
//
//
//        List<String> layoutFiles = get_list_of_layout(resourcePath);
//        // add children of the root to the queue
//        Queue<DomNode> q = new LinkedList<DomNode>();
//        q.add(d);
//        if (d.getChildren() != null) {
//            for (DomNode child : d.getChildren()) {
//                q.add(child);
//            }
//        }
//
//        while (!q.isEmpty()) {
//            DomNode node = q.remove();
//            String originVal = node.getAttr("origin");
//            if (originVal == null) { // node1 is from static analysis tree
//                System.out.println(" this node has no origin: " + node.getAttr("resource-id"));
//                String resource_id = node.getAttr("resource-id");
////                if (resource_id != null && resource_id != "") {   // right now I am only comparing with id ToDO: later on maybe I need to set Xpath
////                if (resource_id != null && !resource_id.isEmpty()) {   // right now I am only comparing with id ToDO: later on maybe I need to set Xpath
//
//                find_origin_for_node(node, layoutFiles, true);
//                System.out.println(node.getAttr("resource-id") + "  |ORIGIN| " + node.getAttr("origin"));
////                node.
//            }
//            //  }
//            if (node.getChildren() != null) {
//                for (DomNode child : node.getChildren()) {
//                    q.add(child);
//                }
//            }
//        }
//
////
//    }
// ToDO: Check for latest API used

    private static List<String> get_list_of_layout(String resourcePath) {
        List<String> layouts = new ArrayList<>();
      // resourcePath="/home/ali/OWlRepair/Subjects/decompiled_apks/org.varunverma.INRTrainStatusAlarm/res/";
        File resourcesFiles = new File(resourcePath);
        System.out.println("Name: "+resourcesFiles.getName());
        System.out.println(resourcesFiles.getAbsolutePath());
        File [] files = resourcesFiles.listFiles();
        for (File f : resourcesFiles.listFiles()) {
            String fileName = f.getName();

            System.out.println("---------------------: " + fileName);
            if (fileName.contains("layout")) {
                System.out.println("Is layout");
                layouts.add(f.getAbsolutePath());
            }

        }

        Collections.sort(layouts, Collections.reverseOrder());

        return layouts;
    }

}



