package eval.testSubFunctions;

import com.google.common.collect.Maps;
import gatech.xpert.dom.DomNode;
import usc.edu.SALEM.SALEM;
import usc.edu.SALEM.VHTree.XMLUtils;

import java.io.File;
import java.util.*;

public class testMergeTrees {


    public static Map<String, DomNode> act2DomRoot;
    public static DomNode rootNode = null;

    public static void main(String[] args) {
//        String subject = "com.alibaba.aliexpresshd";
//        String activity = "com.alibaba.aliexpresshd.MainActivity";

        String subject = "uk.co.yahoo.p1rpp.calendartrigger";
        String activity = "uk.co.yahoo.p1rpp.calendartrigger.activites.SettingsActivity";
        SALEM.setCurrentActivityName(activity);
        String path = "/home/testing/AppSet/accessibility/TT_scripts/apks_folder/dynamicMergeTree" +
                "/" + subject + "/refined";
//        String path=mFix.basePath + File.separator + "dynamicMergeTree/uk.co.yahoo.p1rpp.calendartrigger/refined";
        mergeTrees(path);
    }


    public static void mergeTrees(String folder) {
        File dynamicResultFolder = new File(folder);
        List<String> files = new ArrayList<>();
        String activityName = SALEM.getCurrentActivityName();
        for (File f : dynamicResultFolder.listFiles()) {
            String fileName = f.getName();
            if (!fileName.contains(activityName) || !fileName.endsWith(".xml")) {
                continue;
            }
            files.add(f.getAbsolutePath());
        }


        act2DomRoot = Maps.newLinkedHashMap();
        Collections.sort(files);
//        String output=folder+"/"+activityName+"_Merged.xml";
        String output = folder;

        for (String fileAbsolutePath : files) {
            File f = new File(fileAbsolutePath);
            String fileName = f.getName();
            if (!fileName.contains(activityName) || !fileName.endsWith(".xml")) {
                continue;
            }
            DomNode nextRoot = XMLUtils.buildTree(f.getAbsolutePath());
            act2DomRoot.put(f.getName(), nextRoot);

        }
        mergeTreeOnIndex(act2DomRoot, output);


        for (String key : act2DomRoot.keySet()
        ) {


        }
    }

    private static void mergeTreeOnIndex(Map<String, DomNode> act2DomRoot, String outputFolder) {
        HashMap<String, List<DomNode>> sc = new LinkedHashMap<>();

        List<List<DomNode>> qw = new ArrayList<>();
        int i = 0;
        for (String key : act2DomRoot.keySet()
        ) {
            DomNode root = act2DomRoot.get(key);
            if (i == 0) {
                rootNode = root;
                i++;
            }
            List<DomNode> scrollable = findScrollable(root);
            sc.put(key, scrollable);
            qw.add(scrollable);
        }

        int x = 0;
        // now I am assuming only one scrollable exit each activity

        DomNode s1 = qw.get(0).get(0);
        DomNode s2 = qw.get(1).get(0);

        Queue<DomNode> q = new LinkedList<DomNode>();
        q.add(s1);

//        // process descendants of the root in a bread first fashion
        while (!q.isEmpty()) {
            DomNode currElement = q.remove();
            DomNode dMatch = findMatchedChild2(currElement, s2);
            if (dMatch != null) {
                mergeNodesChildrenOnIndex(currElement, dMatch);
            }
            q.addAll(currElement.getChildren());
        }

        String fileName = SALEM.getCurrentActivityName() + "_Merged";
        HashMap<String, DomNode> f = new HashMap<>();
        f.put(fileName, rootNode);

        dumpAllMergeFiles(outputFolder, f);
//        ApplyFixToDecompiled.writestaticXML(rootNode,output);

//        if(s1.getxPath().equalsIgnoreCase(s2.getxPath())){
//            for (DomNode d1:s1.getChildren()
//                 ) {
//                DomNode dMatch = findMatchedChild(d1, s2);
//                if(dMatch==null){
//                    continue;
//                }
//                mergeNodesChildrenOnIndex(d1,dMatch);
//
//            }
//        }
//        for (int i = 0; i < toMergeList.size(); i++) {
//
//            String fn = "/sdcard/haos-" + (i + 1) + ".dat";
//            Node<DomNode> toMergeRoot = loadTreeFromFile(fn, map.get(fn));
//            MergeNodeInfo hint = toMergeList.get(i);
//            Node<DomNode> found1 = MyAccessibilityNodeInfoSuite.findNode(originalTree, hint.source);
//            Node<DomNode> found2 = MyAccessibilityNodeInfoSuite.findNode(toMergeRoot, hint.source);
//
//            int fromIdx = Math.max(found1.getChildCount(), hint.fromIndex);
//            for (int j = fromIdx; j <= hint.toIndex; j++) {
//                Node<DomNode> child = found2.getChild(j - hint.fromIndex);
//
//                child.setNeedScrollRecursive(true);
//                found1.addChild(child);
//            }
//        }
    }

    public static void dumpAllMergeFiles(String folderPath, Map<String, DomNode> activityToDomRoot) {

        System.out.println("SAVE PATH: " + folderPath);
        File savingFile = new File(folderPath);
        if (!savingFile.exists()) {
            savingFile.mkdirs();
        }
        for (Map.Entry<String, DomNode> entry : activityToDomRoot.entrySet()) {
            String activity = entry.getKey();
            DomNode root = entry.getValue();
            XMLUtils.dump(root, folderPath + File.separator + activity + ".xml");
            String inputFolder = SALEM.getOriginalDynamicLayoutFilePath();
            String outputFolder = folderPath;
            //  copy_image_file(activity + ".xml", inputFolder, outputFolder);
        }
    }

    private static DomNode mergeNodesChildrenOnIndex(DomNode d1, DomNode dMatch) {
        DomNode newNode = d1.copy();
        if (d1.getChildren() != null && d1.getChildren().size() > 0) {
            int d1Leng = d1.getChildren().size();
            DomNode firstChild = d1.getChild(0);
            DomNode lastChild = d1.getChild(d1Leng - 1);

            // last child
            int lastChildIndex = Integer.parseInt(lastChild.getAttr("index"));
            int firstChildIndex = Integer.parseInt(firstChild.getAttr("index"));
            if (dMatch.getChildren() != null && dMatch.getChildren().size() > 0) {
                int matchLeng = dMatch.getChildren().size();

                DomNode matchFirstChild = dMatch.getChild(0);// firstChild of match
                DomNode matchLastChild = dMatch.getChild(matchLeng - 1);

                int matchFirstChildIndex = Integer.parseInt(matchFirstChild.getAttr("index"));
                int matchLastChildIndex = Integer.parseInt(matchLastChild.getAttr("index"));
                if (firstChildIndex == matchFirstChildIndex && lastChildIndex == matchLastChildIndex) {
                    return newNode;
                }
                if (firstChildIndex < matchFirstChildIndex || lastChildIndex < matchLastChildIndex) {

                    System.out.println("YESS");
                    ArrayList<DomNode> toBeMerged = findNodesToBeMerged(newNode, dMatch, firstChildIndex, lastChildIndex, matchFirstChildIndex, matchLastChildIndex);
                    for (DomNode node : toBeMerged
                    ) {
                        d1.addChild(node);
                    }
                }

            }

        }
        return newNode;
    }

    private static ArrayList<DomNode> findNodesToBeMerged(DomNode d1, DomNode dMatch, int firstChildIndex,
                                                          int lastChildIndex, int matchFirstChildIndex,
                                                          int matchLastChildIndex) {
        // Find the first element from the dmatch to merge into d1
        ArrayList<DomNode> toBeMerged = new ArrayList<>();
        int i = lastChildIndex + 1;
        for (DomNode currMatch : dMatch.getChildren()
        ) {
            int currIndex = Integer.parseInt(currMatch.getAttr("index"));
            if (currIndex == i) {
                toBeMerged.add(currMatch);
                i++;
            }
        }
//        for (int i = lastChildIndex+1; i <matchLastChildIndex ; i++) {
//            DomNode curr = dMatch.getChildren().remove(0);
//            if (Integer.parseInt(curr.getAttr("index")) == i) {
//                toBeMerged.add(curr);
//            }
//
//        }
        return toBeMerged;

    }

    private static DomNode findMatchedChild(DomNode d1, DomNode s2) {
        String d1Xpath = d1.getxPath();
        for (DomNode d2 : s2.getChildren()
        ) {
            if (d2.getxPath().equalsIgnoreCase(d1Xpath)) {
                return d2;
            }
        }
        return null;
    }

    private static DomNode findMatchedChild2(DomNode d1, DomNode s2) {
        String d1Xpath = d1.getxPath();
        DomNode d2 = XMLUtils.searchVHTreeByXpath(d1Xpath, s2);
//        for (DomNode d2:s2.getChildren()
//        ) {
//            if(d2.getxPath().equalsIgnoreCase(d1Xpath)){
//                return d2;
//            }
//        }
        return d2;
    }

    private static List<DomNode> findScrollable(DomNode root) {
        List<DomNode> scrollNode = new ArrayList<>();
        Queue<DomNode> q = new LinkedList<DomNode>();
        q.add(root);

//        // process descendants of the root in a bread first fashion
        while (!q.isEmpty()) {
            DomNode currElement = q.remove();
            if (currElement.getAttr("scrollable") != null) {
                boolean isScrollabe = Boolean.parseBoolean(currElement.getAttr("scrollable"));
                if (isScrollabe) {
                    scrollNode.add(currElement);
                }
            }
            q.addAll(currElement.getChildren());
        }

        return scrollNode;
    }
//    private void mergeTreeOnIndex(Node<DomNode> originalTree, List<MergeNodeInfo> toMergeList, Map<String, Integer> map) {
//        for (int i = 0; i < toMergeList.size(); i++) {
//
//            String fn = "/sdcard/haos-" + (i + 1) + ".dat";
//          Node<DomNode> toMergeRoot = loadTreeFromFile(fn, map.get(fn));
//            MergeNodeInfo hint = toMergeList.get(i);
//          Node<DomNode> found1 = MyAccessibilityNodeInfoSuite.findNode(originalTree, hint.source);
//          Node<DomNode> found2 = MyAccessibilityNodeInfoSuite.findNode(toMergeRoot, hint.source);
//
//            int fromIdx = Math.max(found1.getChildCount(), hint.fromIndex);
//            for (int j = fromIdx; j <= hint.toIndex; j++) {
//              Node<DomNode> child = found2.getChild(j - hint.fromIndex);
//
//                child.setNeedScrollRecursive(true);
//                found1.addChild(child);
//            }
//        }
//    }
}
