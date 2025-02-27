package usc.edu.OwlEye.VHTree;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import usc.edu.SALEM.SALEM;
import usc.edu.SALEM.util.ApplyFixToDecompiled;

import java.io.File;
import java.util.*;

public class Merger_TT extends Merger {
//    public static GUIAnalyzer guiAnalyzer;

    public void merge(String staticLayoutFolderPath, String dynamicLayoutFolderPath, Map<String, Set<String>> act2xmls, String resourcePath) {
        File dynamicLayoutFolder = new File(dynamicLayoutFolderPath);
        /*** since we now have the package already no need to call this but get mfix ***/
        //        String appPkg = dynamicLayoutFolderPath.substring(staticLayoutFolderPath.lastIndexOf(File.separator) + 1);
//        String realPkg = appPkg;
//        if (realPkg.contains("refined")) {
//            realPkg = realPkg.substring(0, realPkg.lastIndexOf("/"));
//        }
//        realPkg.substring(0, realPkg.indexOf("/"));
//        String[] rr = realPkg.split("/refined");
//        realPkg = rr[0].substring(rr[0].indexOf("/") + 1);
//        if (realPkg.contains("_")) {
//            realPkg = realPkg.substring(0, realPkg.indexOf("_"));
//        }
//        realPkg = realPkg.replace("/", "");
        String realPkg = SALEM.getCurrentApkPackage();
        act2DomRoot = Maps.newHashMap();
        for (File f : dynamicLayoutFolder.listFiles()) {
            String fileName = f.getName();
            String activityName = getActivityNameFromXMLFileName(fileName);
//            if (!activityName.startsWith(realPkg) || fileName.contains(".png")) {
            if (fileName.contains(".png")) {

                continue;  //Ali stopped this while initial code running

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
            //Ali
            String firstFilePath = staticLayoutFolderPath + File.separator + activityName + ".xml";
            //  s = merge.XMLUtils.buildTree(firstFilePath);
            File staticFF = new File(firstFilePath);
            if (staticFF.exists() && !staticFF.isDirectory()) {
                DomNode staticRoot = XMLUtils.buildTree(firstFilePath);
                System.out.println("Static View Number: " + getViewNumber(staticRoot));
                System.out.println("Dynamic View Number: " + getViewNumber(dynamicRoot)); // after merge
                // Step 2: merge the static with the final tree of dynamic XMLs
                if (staticRoot != null) {
                    TreePair_TT pair_TT = new TreePair_TT();
                    dynamicRoot = pair_TT.merge(staticRoot, dynamicRoot);
                    System.out.println("Hybrid View Number: " + getViewNumber(staticRoot)); // after merge
                    //      Map<DomNode, DomNode> listMatched = pair.get_matched(staticRoot, dynamicRoot);
                }
            }

            add_missing_origins(dynamicRoot, resourcePath);
            if (act2DomRoot.containsKey(activityName)) {
                System.err.println(activityName + "has more than 1 static layout file!");
                continue;
            } else {
                act2DomRoot.put(activityName, dynamicRoot);
            }
        }
        dumpAllMergeFiles(realPkg, act2DomRoot);
    }

    public void add_missing_originsInspector(DomNode dynamicRoot, String resourcePath) {
        // Iterate over nodes and if a node is missing origin attribute then do dummay matching these are nodes not found in static analysis

        System.out.println(" ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        DomNode d = dynamicRoot;


        List<String> layoutFiles = get_list_of_layout(resourcePath);
        // add children of the root to the queue
        Queue<DomNode> q = new LinkedList<DomNode>();
        q.add(d);
        if (d.getChildren() != null) {
            for (DomNode child : d.getChildren()) {
                q.add(child);
            }
        }

        while (!q.isEmpty()) {
            DomNode node = q.remove();
            String originVal = node.getAttr("origin");
            if (originVal == null) { // node1 is from static analysis tree
                System.out.println(" this node has no origin: " + node.getAttr("resource-id"));
                String resource_id = node.getAttr("resource-id");
//                if (resource_id != null && resource_id != "") {   // right now I am only comparing with id ToDO: later on maybe I need to set Xpath
//                if (resource_id != null && !resource_id.isEmpty()) {   // right now I am only comparing with id ToDO: later on maybe I need to set Xpath

                find_origin_for_node(node, layoutFiles, true);
                System.out.println(node.getAttr("resource-id") + "  |ORIGIN| " + node.getAttr("origin"));
//                node.
            }
            //  }
            if (node.getChildren() != null) {
                for (DomNode child : node.getChildren()) {
                    q.add(child);
                }
            }
        }


    }

    public void add_missing_origins(DomNode dynamicRoot, String resourcePath) {
        // Iterate over nodes and if a node is missing origin attribute then do dummay matching these are nodes not found in static analysis

        System.out.println(" ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        DomNode d = dynamicRoot;


        List<String> layoutFiles = get_list_of_layout(resourcePath);
        // add children of the root to the queue
        Queue<DomNode> q = new LinkedList<DomNode>();
        q.add(d);
        if (d.getChildren() != null) {
            for (DomNode child : d.getChildren()) {
                q.add(child);
            }
        }

        while (!q.isEmpty()) {
            DomNode node = q.remove();
            String originVal = node.getAttr("origin");
            if (originVal == null) { // node1 is from static analysis tree
                System.out.println(" this node has no origin: " + node.getAttr("resource-id"));
                String resource_id = node.getAttr("resource-id");
//                if (resource_id != null && resource_id != "") {   // right now I am only comparing with id ToDO: later on maybe I need to set Xpath
                if (resource_id != null) {   // right now I am only comparing with id ToDO: later on maybe I need to set Xpath
                    find_origin_for_node(node, layoutFiles, false);
//                node.
                }
            }
            if (node.getChildren() != null) {
                for (DomNode child : node.getChildren()) {
                    q.add(child);
                }
            }
        }


    }

    private List<String> get_list_of_layout(String resourcePath) {
        List<String> layouts = new ArrayList<>();
        File resourcesFiles = new File(resourcePath);
        for (File f : resourcesFiles.listFiles()) {
            String fileName = f.getName();

            System.out.println("---------------------: " + fileName);
            if (fileName.contains("layout")) {
                System.out.println("Is layout");
                layouts.add(f.getAbsolutePath());
            }

        }
        if (layouts.size() > 0) {
            return layouts;
        }
        return null;
    }

    private void find_origin_for_node(DomNode dynNode, List<String> layoutFiles, boolean isLayoutInspector) {
        boolean staticNodeFound = false;
        String resource_id = dynNode.getAttr("resource-id");
        String effective_id = null;
//                if (resource_id != null && resource_id != "") {   // right now I am only comparing with id ToDO: later on maybe I need to set Xpath
        boolean searchRootOnly = false;
        if (resource_id != null && !resource_id.isEmpty()) {
            resource_id = resource_id.substring(resource_id.lastIndexOf("/") + 1);
            if (isLayoutInspector) {
                /** If it is layout inspector we need to see if effective layout is there then we should find the origin of that not the id***/
                effective_id = dynNode.getAttr("effective-id");

                if (effective_id != null) {
                    //THis means it is really effective so we should only checkf for the root of the file if it matches the dynamic id
                    // we do t becasue this is a case of include so it should only match root of the static file since include always map to the root of file
                    // I do that t oeliminate possible mistkaes of matching to other views within or inside layout
                    effective_id = effective_id.substring(effective_id.lastIndexOf("/") + 1);
                    if (!effective_id.equalsIgnoreCase(resource_id)) {
                        searchRootOnly = true;
                    }

                }
            }


            Collections.sort(layoutFiles, Collections.reverseOrder());

//        Util.sortAndFilterLayoutFiles()layoutFiles
            String dynamicID = resource_id;
            if (effective_id != null && !effective_id.isEmpty()) {
                dynamicID = effective_id;  // if effective-id exists then use it
            }
            dynamicID = dynamicID.substring(dynamicID.lastIndexOf("/") + 1);

            for (String folder : layoutFiles
            ) {
                System.out.println("Folder: " + folder);
                File layoutF = new File(folder);
                if (layoutF.getName().contains("-") && !layoutF.getName().contains("layout-v")) {  // to skip -land -large and other different layout
                    continue;
                }
                // how to sort by latest api

                for (File f : layoutF.listFiles()) {
                    Element staticFileRoot = ApplyFixToDecompiled.readStaticXML(f);
//                    String dynamicID = dynNode.getAttr("resource-id");


//                    if (dynamicID == null || dynamicID.isEmpty()) {
//                        continue;
//                    }

                    Element foundElement = ApplyFixToDecompiled.searchForNode(staticFileRoot, dynamicID, searchRootOnly);
                    if (foundElement == null) {  // Not Found
                        continue;
                    }

                    staticNodeFound = true;
                    Node isLayoutExitss = foundElement.getAttributes().getNamedItem("layout");
                    if (isLayoutExitss != null) {
                        String includedLayout = isLayoutExitss.getNodeValue();
                        if (includedLayout.contains("@layout")) {
                            includedLayout = includedLayout.replace("@layout/", "");
                        }
//                                String currXpath=foundNode.getxPath();
//                                String newXpath=currXpath.substring(0,currXpath.lastIndexOf("/"));
                        foundElement = getRootOfIncludedLayout(includedLayout, layoutF);
//                                newXpath=newXpath+foundNode.getxPath();
//                                dynNode.setxPath(newXpath);

//                        Node effective_id = foundElement.getAttributes().getNamedItem("android:id");
//                        if (effective_id != null) {
//                            String newID = effective_id.getNodeValue();
//                            newID = newID.substring(newID.lastIndexOf("/") + 1);
//                            dynNode.setAttr("effective-id", newID);
//                        }
                    }
                    //  dynNode.setTagName(foundElement.getTagName());
//                resolveStyleAndDimension(foundElement);//removestyle and dimensions
                    ApplyFixToDecompiled.writestaticXML(staticFileRoot, f.getAbsolutePath());
                    dynNode.setAttr("origin", f.getAbsolutePath());
                    String node2Tag = foundElement.getTagName();
                    dynNode.setAttr("StaticTagName", node2Tag);
                    dynNode.setAttr("naive_xml_file_matching", "true");
                    dynNode.setAttr("origin", f.getAbsolutePath());
                    NamedNodeMap staticElementAtts = foundElement.getAttributes();

                    if (!isLayoutInspector) {
//                    for (String importantNode : Constants.ATTRIBUTES_TO_COPY_FROM_STATIC_FILE.keySet()
//                    ) {
//                        Node attValue = staticElementAtts.getNamedItem(importantNode);
//
//                        if (attValue != null) { // Attribute exists so add it
//                            String val = attValue.getNodeValue();
//                            dynNode.setAttr(importantNode, val);
//                            //if magin or padding replace with its sub tyopes: left rigth up down
//                        }
//
//
//                        System.out.println("File: " + f.getName());
//                    }
                    }
                    return;
                }
            }
        }
        if (!staticNodeFound) {

            //ToDo: what to do when we can not find the dynamic element ysing naive matching (neither by Mian's analys)?
            //for now I am just considering it match parent (-1) so not to include it in dependancy graph but Todo: better solution need to be done
            String OriginVal = dynNode.getAttr("origin");
            if (OriginVal == null) { // No origin defined yet
                dynNode.setAttr("origin", "NOTFOUND");
                dynNode.setAttr("android:layout_height", "fill_parent");
                dynNode.setAttr("android:layout_width", "fill_parent");
                // To record that this was found using naive search in XML
                dynNode.setAttr("naive_xml_file_matching", "true");
            }
        }

    }



    private DomNode getIDofRootOfLayout(String includedLayout, File layoutF) {
        String includedLayoutFile = layoutF.getAbsolutePath() + File.separator + includedLayout + ".xml";
        DomNode root = XMLUtils.buildTree(includedLayoutFile);

        return root;
    }

    private Element getRootOfIncludedLayout(String includedLayout, File layoutF) {
        String includedLayoutFile = layoutF.getAbsolutePath() + File.separator + includedLayout + ".xml";
        File f = new File(includedLayoutFile);
        Element root = ApplyFixToDecompiled.readStaticXML(f);

        return root;
    }

    private DomNode search_node_with_ID(DomNode root, String att, String resource_id) {

        Queue<DomNode> q = new LinkedList<DomNode>();
        q.add(root);
        if (root.getChildren() != null) {
            for (DomNode child : root.getChildren()) {
                q.add(child);
            }
        }

        while (!q.isEmpty()) {
            DomNode node = q.remove();
            String android_id = node.getAttr(att);
            if (android_id != null && android_id != "") { // node1 is from static analysis tree
                System.out.println("ID: " + android_id);
                boolean is_same = compare_ids(android_id, resource_id);
                //String resource_id= node.getAttr("resource-id");
                if (is_same) {
                    return node;
                }
            }
            if (node.getChildren() != null) {
                for (DomNode child : node.getChildren()) {
                    q.add(child);
                }
            }
        }


        return null;
    }

    private boolean compare_ids(String android_id, String resource_id) {
        //System.out.println("[************ "+android_id+"  |vs| "+resource_id);
        String static_id = android_id;
        if (android_id.contains("/")) {
            static_id = android_id.substring(android_id.lastIndexOf("/"));
        }
        String dynamic_id = resource_id;
        if (resource_id.contains("/")) {
            dynamic_id = resource_id.substring(resource_id.lastIndexOf("/"));
        }

        // System.out.println("[[[[[TRUE]]]]]]]");
        return static_id.trim().equalsIgnoreCase(dynamic_id.trim().toLowerCase());
    }
}