package usc.edu.OwlEye.run;/*
New class for the new Owl Paper
 */


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import gatech.xpert.dom.DomNode;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import usc.edu.OwlEye.OwlConstants;
import usc.edu.OwlEye.VHTree.XMLUtils;
import usc.edu.OwlEye.util.Utils;
import usc.edu.SALEM.VHTree.Merger;
import usc.edu.SALEM.VHTree.Merger_TT;
//import usc.edu.SALEM.VHTree.XMLUtils;
import usc.edu.SALEM.util.ApplyFixToDecompiled;
import usc.edu.SALEM.util.LoadConfig;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

public class AddOriginToVHElements {

    public static boolean IS_LAYOUT_INSPECTOR;
    //    public String subject="";
    public static HashMap<String, DomNode> act2DomRoot;
    private static Logger logger = Logger.getLogger(AddOriginToVHElements.class.getName());

    private static void copyPNGs(String dynamicVH, String completeVH, boolean copyPNG) {
        // now if we have a PNG, copy it to the merged folder if copyPNG is true
        File mergedFolder = new File(completeVH);
        String refinedDynamicVH = dynamicVH + File.separator + "refined";
        for (File f : mergedFolder.listFiles()) {
            String fileName = f.getName();
            if (fileName.endsWith(".xml")) {
                if (copyPNG) {
                    // copy the corresponding png to the outputFolder
                    String pngFile = fileName.replace("xml", "png");
                    File srcPng = new File(refinedDynamicVH + File.separator + pngFile);
                    File desPng = new File(completeVH + File.separator + pngFile.substring(pngFile.indexOf("_") + 1));
                    try {
                        Files.copy(srcPng, desPng);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private static List<String> get_list_of_layout(String resourcePath) {
        List<String> layouts = new ArrayList<>();
        // resourcePath="/home/ali/OWlRepair/Subjects/decompiled_apks/org.varunverma.INRTrainStatusAlarm/res/";
        File resourcesFiles = new File(resourcePath);


        for (File f : resourcesFiles.listFiles()) {
            String fileName = f.getName();

            logger.info("---------------------: " + fileName);
            if (fileName.contains("layout")) {
                Logger.getGlobal().info("layout file: " + fileName);
                String[] baseFileName = fileName.split("-v");
                if (baseFileName.length>1){

                    layouts.add(f.getAbsolutePath());
                }
                else{
                    if(baseFileName[0].equalsIgnoreCase("layout")){
                        layouts.add(f.getAbsolutePath());
                    }
                }
                //layouts.add(f.getAbsolutePath());
            }

        }

        Collections.sort(layouts, Collections.reverseOrder());

        return layouts;
    }
    public static void add_originAtt(DomNode mergedNode, Element foundElement, String orignalFilePath) {
        String node2Tag = foundElement.getTagName();
        mergedNode.setAttr("StaticTagName", node2Tag);
        mergedNode.setAttr("naive_xml_file_matching", "true");
        mergedNode.setAttr("origin", orignalFilePath);
        // Search for constraints layouts attributes Nov 21, 2022
        NamedNodeMap atts = foundElement.getAttributes();
        for (int i = 0; i < atts.getLength(); i++) {
            if(OwlConstants.CONSTRAINTS_LAYOUT_ATTRIBUTES_MAP.containsKey(atts.item(i).getNodeName())
            || (OwlConstants.CONSTRAINTS_LAYOUT_ATTRIBUTES_MAP.containsValue(atts.item(i).getNodeName()))){
                org.w3c.dom.Node att = atts.item(i);
                String attName = att.getNodeName();

                String attValue = att.getNodeValue();

                    mergedNode.setAttr(attName, attValue);

            }
            }


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
    private static String findCorrectLayoutForActivityRoot(DomNode dynamicMergedRoot, List<String> layoutFile, HashMap<String, String> staticLayoutSearched) {
        String rootXpath = dynamicMergedRoot.getxPath();
        boolean layoutFound = false;
        String filePath = "";
        for (String folder : layoutFile
        ) {
            if (layoutFound) {
                break;
            }

            logger.info("Folder: " + folder);
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

    private static void searchRemainingNodes(DomNode dynamicMergedRoot, HashMap<String, DomNode> foundDomNodes, List<String> layoutFiles, HashMap<String, String> staticLayoutSearched) {
        List<DomNode> workList = Lists.newArrayList(dynamicMergedRoot.getChildren());
        while (!workList.isEmpty()) {
            DomNode curr = workList.remove(0);
            if (!foundDomNodes.containsKey(curr.getxPath())) {
                String nodeOrigin = findCorrectLayoutForActivityRoot(curr, layoutFiles, staticLayoutSearched);
                if (nodeOrigin != null) {
                    File originFile = new File(nodeOrigin);
                    Element staticFileRoot = ApplyFixToDecompiled.readStaticXML(originFile);
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
            }//do not contained in key
            workList.addAll(curr.getChildren());

        }
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
                        curr.setAttr("origin", "CheckOriginManually");

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
    private static void addOriginForSingleFile(File augmentedVHFolder, String completeVH,String resourcePath,String activityName,String apkName) {
        logger.info("addOriginForSingleFile: "+ augmentedVHFolder.getAbsolutePath()+" "+ completeVH +" "+ resourcePath+" "+ activityName+" "+ apkName);

        List<String> layoutFiles = get_list_of_layout(resourcePath);
        Merger_TT merger = new Merger_TT();
        HashMap<String, String> staticLayoutSearched = new HashMap<>();
        for (File f : augmentedVHFolder.listFiles()) {
            String fileName = f.getName();
            if (fileName.contains(".png") || !fileName.endsWith(".xml")) {

                continue;  //Ali stopped this while initial code running

            }

            String fileAct = Merger.getActivityNameFromXMLFileName(fileName);
            if (!activityName.equalsIgnoreCase(fileAct)){
                //Not same activity so continue
                continue;
            }

            // get the root of the activity
            HashMap<String, DomNode> foundDomNodes = new HashMap<>();
            DomNode dynamicMergedRoot = XMLUtils.buildTree(f.getAbsolutePath());
            // find the layout that has the root
            String rootOrigin = findCorrectLayoutForActivityRoot(dynamicMergedRoot, layoutFiles, staticLayoutSearched);
            logger.info(" Root Origin file: " + rootOrigin);
            if (rootOrigin != null) {
                File originFile = new File(rootOrigin);
                Element staticFileRoot = ApplyFixToDecompiled.readStaticXML(originFile);
                add_originAtt(dynamicMergedRoot, staticFileRoot, rootOrigin);
                foundDomNodes.put(dynamicMergedRoot.getxPath(), dynamicMergedRoot);
                findchildrenINSameOrigin(dynamicMergedRoot, staticFileRoot, foundDomNodes, rootOrigin);
                staticLayoutSearched.put(rootOrigin, rootOrigin);
            }

            searchRemainingNodes(dynamicMergedRoot, foundDomNodes, layoutFiles, staticLayoutSearched);
            int x = 0;
            addNotFoundForRemainingNodes(dynamicMergedRoot);
            act2DomRoot.put(activityName, dynamicMergedRoot);
        }
       dumpAllMergeFiles(apkName, act2DomRoot, completeVH,augmentedVHFolder.getAbsolutePath());

    }

    public static void dumpAllMergeFiles(String appPkg, Map<String, DomNode> activityToDomRoot, String outputDir,String augmentedVHPath) {
//        String savingPath = mFix.getMergePath() + File.separator + appPkg;
        String savingPath = outputDir; //for search based the path contain subject
        logger.info("SAVE PATH: " + savingPath);
        File savingFile = new File(savingPath);
        if (!savingFile.exists()) {
            savingFile.mkdirs();
        }
        for (Map.Entry<String, DomNode> entry : activityToDomRoot.entrySet()) {
            String activity = entry.getKey();
            DomNode root = entry.getValue();
            XMLUtils.dump(root, savingPath + File.separator + activity + ".xml");
            String inputFolder = augmentedVHPath;
            String outputFolder = savingPath;
              Merger.copy_image_file(activity + ".xml", inputFolder, outputFolder);
        }
    }
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        boolean processDefaultFont = false;
        boolean processLargestFont = false;

        HashMap<String, String> propConfig = LoadConfig.getInstance().getConfig_data();
        act2DomRoot = Maps.newHashMap();

        String subjectID = "";
        String appName = "";
        String activityName = "";
        String ui_version= "";
        logger.info("ARGS:" + args.length);
        if (args.length > 0) {
            logger.info("subjectID: " + args[0]);
            subjectID = args[0];
            appName = args[1];
            activityName = args[2];
            ui_version=args[3];
        }
        logger.info("full list of args:"+Arrays.toString(args));
        String default_ui_version_type = "default_font";
        String largest_ui_version_type = "largest_font";
        if(ui_version.equalsIgnoreCase(default_ui_version_type)){
            processDefaultFont = true;
            processLargestFont = false;
        }
        else if(ui_version.equalsIgnoreCase(largest_ui_version_type)){
            processDefaultFont = false;
            processLargestFont = true;
        }
        else{
            processDefaultFont = false;
            processLargestFont = false;
        }


        boolean copyPNG = true; // copy the PNG


        // Setting file paths
        String VHPathPlaceHolder = propConfig.get("subjects_base_path") + File.separator + subjectID + File.separator + "%s" + File.separator + propConfig.get("device_title");//+File.separator+appName;

        String completeVH = String.format(VHPathPlaceHolder, propConfig.get("complete_vh")); //mergedLayoutPath

        String dynamicVH = String.format(VHPathPlaceHolder, propConfig.get("dynamic_vh"));//refinedDynamicPath
        String augmentedVH = String.format(VHPathPlaceHolder, propConfig.get("augmented_vh"));//augmented_path
        String decompiledAPK = propConfig.get("subjects_base_path") + File.separator + subjectID + File.separator + propConfig.get("decompiled_apk")+File.separator+appName;//originalStaticFiles


        // Getting the default and largest font sizes VHs
        String defaultFontDynamicVH = dynamicVH + File.separator + default_ui_version_type;
        String largestFontDynamicVH = dynamicVH + File.separator + largest_ui_version_type;
        String defaultFontAugmentedVH = augmentedVH + File.separator + default_ui_version_type;
        String largestFontAugmentedVH = augmentedVH + File.separator + largest_ui_version_type;
        String defaultFontCompleteVH = completeVH + File.separator + default_ui_version_type;
        String largestFontCompleteVH = completeVH + File.separator + largest_ui_version_type;


        // Resource path for the decomplied APK
        String resourcePath = decompiledAPK+ "/res/";
        if (processDefaultFont) {
            logger.info("Processing default font");
            // Copying the default font VHs
            File defaultFontAugmentedVHFolder = new File(defaultFontAugmentedVH);
            addOriginForSingleFile(defaultFontAugmentedVHFolder, defaultFontCompleteVH,resourcePath, activityName, appName);
            copyPNGs(defaultFontDynamicVH,  defaultFontCompleteVH, copyPNG);


        }


        if (processLargestFont) {
            logger.info("Processing largest font");
            // Copying the default font VHs
            File largestFontAugmentedVHFolder = new File(largestFontAugmentedVH);
            addOriginForSingleFile(largestFontAugmentedVHFolder,largestFontCompleteVH, resourcePath, activityName, appName);
            copyPNGs(largestFontDynamicVH,  largestFontCompleteVH, copyPNG);


        }

        //Starting the process of adding origins.
        // addOriginForSingleFile(dynamicLayoutFolder, resourcePath, activity_name);


    }



    }








