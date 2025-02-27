import  usc.edu.OwlEye.clustring.ClustersSorter;
import eval.testSubFunctions.TestClustering;
import  usc.edu.OwlEye.clustring.ElementsClusterer;
import  usc.edu.OwlEye.clustring.UtilIfix;
import usc.edu.OwlEye.clustring.ElementsClusterer;
import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;
import usc.edu.OwlEye.VH.UI;
import usc.edu.OwlEye.util.Utils;
import usc.edu.SALEM.Constants;
import usc.edu.SALEM.SALEM;
import usc.edu.SALEM.VHTree.Merger;
import usc.edu.SALEM.VHTree.XMLUtils;
import usc.edu.SALEM.fitness.TIssue;
import usc.edu.SALEM.util.LoadConfig;
import usc.edu.SALEM.util.ReadIssues;
import usc.edu.SALEM.util.Util;

import java.io.*;
import java.util.*;

import static usc.edu.OwlEye.clustring.UtilIfix.getClusterRectangles;

public class testVisualSimilarities {

    private static boolean onlyShowRelevantClusters=false;  // Print relevant clusters that are part of the issues

    public static void testClustering(UI originalDefaultUI, String subjectID) throws IOException {


        //  merger.prepare_app_layout(mFix.getOriginalApkName(), null, mFix.getOriginalDecompiled(), apkCrawledFiles, guianalzyerPath);
        String outputDir = "/home/ali/OWlRepair/clustering_output/" + subjectID + "/";
        //File inputFolder = new File(originalDefaultUI);
        String sourceXML=originalDefaultUI.getXmlFilePath();
        File outputFolder = new File(outputDir + File.separator + "original_clustering");
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }


        // 1. Get clustering for the original UI
        ElementsClusterer clusterer = new ElementsClusterer(originalDefaultUI.getXMLTree().getRoot());
        clusterer.perfomrClustering();
        Map<String, List<Rectangle>> clusters = getClustersDBSCAN(null, clusterer);
        printClusters(clusters);
//        String sourcepngFileName = sourceXML.replace(".xml", ".png");
        String sourcePNG = sourceXML.replace(".xml", ".png");
        String fileName=sourcePNG.substring(sourcePNG.lastIndexOf("/")+1,sourcePNG.length());
        String destPngFileName = fileName.replace(".png", "cluster_" + Util.getFormattedTimestamp() + ".png");
//        String destPngFilePath = outputFolder.getAbsolutePath() + File.separator + destPngFileName;
        //fileName.replace(".xml", "cluster_" + Util.getFormattedTimestamp()+".png");

        String clusterFolderName=destPngFileName.replace(".png","");
        String clusterFolderPath=outputFolder.getAbsolutePath() + File.separator + clusterFolderName;
        File newClusterFolder=new File(clusterFolderPath);
        if (!newClusterFolder.exists()) {
            newClusterFolder.mkdirs();
        }
        String destPngFilePath = clusterFolderPath+ File.separator + destPngFileName;
        File file1 = new File(sourcePNG);
        File file2 = new File(destPngFilePath);

        FileUtils.copyFile(file1, file2);
        UtilIfix.drawClusters2(newClusterFolder,destPngFilePath, clusters);
        System.out.println("destPngFilePath\t"+destPngFilePath);
        //UtilIfix.drawClusters(destPngFilePath, clusters);
        // Iterate over each xml




        long endTime = System.nanoTime();


    }

    private static void printClusters(Map<String, List<Rectangle>> clusters) {

        for (String clusterID : clusters.keySet()) {
            int size = clusters.get(clusterID).size();
            System.out.print("Cluster\t" + clusterID + "\t" + size+"\t");
            for (int i = 0; i < size; i++) {
                Rectangle r = clusters.get(clusterID).get(i);
                String split="||";
                if (i==size-1){
                    split="";
                }
                System.out.print(r.toString()+split);
            }

            System.out.println();

        }



    }

    public static Map<String, List<Rectangle>> getClustersDBSCAN(List<String> potentiallyFaultyElements, ElementsClusterer clusterer) {


        ArrayList<ArrayList<Node<DomNode>>> pageClusters = clusterer.getClustringResultsDomNodes();
        ArrayList<ArrayList<Node<DomNode>>> relevantClusterDomNodes;
        System.out.println("Getting relevant clusters");
        if (potentiallyFaultyElements!=null &&potentiallyFaultyElements.size() > 0 && onlyShowRelevantClusters) {
            relevantClusterDomNodes = ClustersSorter.getRelevantDomNodeClusters(pageClusters, new ArrayList<>(potentiallyFaultyElements));

        } else {
            relevantClusterDomNodes = pageClusters;  // Ali: I just want to test the pagenull
        }
        System.out.println("\nRelevant clusters: (size = " + relevantClusterDomNodes.size() + ")");
        int count = 0;
        Map<String, List<Node<DomNode>>> clusters = new HashMap<>();
        if (Constants.TESTING_SEGMENTS) { // To output the segments for me to test
            try {
                System.setOut(new PrintStream(new FileOutputStream(SALEM.segmentsLogoutPath, true)));

            } catch (Exception e) {

            }


        }
        for (ArrayList<Node<DomNode>> c : relevantClusterDomNodes) {
            System.out.println("\nRelevant cluster " + (count++) + ". (size = " + c.size() + ") = ");
            for (Node<DomNode> node : c) {
                System.out.println(node.getData().getxPath()+ "\t "+node.getData().getCoord());
            }
            clusters.put("C" + count, c);
        }
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

        return getClusterRectangles(clusters);
    }

    public static void main(String[] args) throws SAXException, IOException, InterruptedException {
        String default_ui_version_type = "default_font";
        String largest_ui_version_type = "largest_font";
        String subjectID = "openfoodm";
        String appName = "";
        String activityName = "";
        HashMap<String, String> propConfig = LoadConfig.getInstance().getConfig_data();


        String[] subjectsInfo = Utils.readSubjectsCSV(propConfig.get("subjects_csv"), subjectID);
        if (subjectsInfo == null) {
            System.out.println("Subject not found");
            System.exit(1);
        }
        appName = subjectsInfo[0];
        activityName = subjectsInfo[1];


        String VHPathPlaceHolder = propConfig.get("subjects_base_path") + File.separator + subjectID + File.separator + "%s" + File.separator + propConfig.get("device_title");//+File.separator+appName;

        String completeVH = String.format(VHPathPlaceHolder, propConfig.get("complete_vh"));


        String dynamicVH = String.format(VHPathPlaceHolder, propConfig.get("dynamic_vh"));//refinedDynamicPath
        String augmentedVH = String.format(VHPathPlaceHolder, propConfig.get("augmented_vh"));//augmented_path
        String decompiledAPK = propConfig.get("subjects_base_path") + File.separator + subjectID + File.separator + propConfig.get("decompiled_apk") + File.separator + appName;//originalStaticFiles


        // Getting the default and largest font sizes VHs
        String defaultFontDynamicVH = dynamicVH + File.separator + default_ui_version_type;
        String largestFontDynamicVH = dynamicVH + File.separator + largest_ui_version_type;
        String defaultFontAugmentedVH = augmentedVH + File.separator + default_ui_version_type;
        String largestFontAugmentedVH = augmentedVH + File.separator + largest_ui_version_type;
        String defaultFontCompleteVH = completeVH + File.separator + default_ui_version_type;
        String largestFontCompleteVH = completeVH + File.separator + largest_ui_version_type;


        String originalFile = defaultFontCompleteVH + File.separator +
                activityName + ".xml";
        String originalPNGFile = defaultFontCompleteVH + File.separator +
                activityName + ".png";
//        String chromosome="uk.co.bitethebullet.android.token_601-05-2021-01-42-57-PM";
        String repaired = largestFontCompleteVH + File.separator +
                activityName + ".xml";
//        repaired=originalFile;

        usc.edu.OwlEye.VHTree.XMLUtils defaultOrgVH = new usc.edu.OwlEye.VHTree.XMLUtils(originalFile);
        usc.edu.OwlEye.VHTree.XMLUtils largestOrgVH = new usc.edu.OwlEye.VHTree.XMLUtils(repaired);
        Node<DomNode> originalRoot = defaultOrgVH.getRoot();
        Node<DomNode> repairedRoot = largestOrgVH.getRoot();


        UI originalDefaultUI = new UI("original_default", defaultOrgVH, originalFile, originalPNGFile);
        UI repairedDefaultUI = new UI("repaired_default", largestOrgVH, repaired, originalPNGFile);
        onlyShowRelevantClusters = true;  // true: Only show clusters that are part of the issues | false: show all clusters
        testClustering(originalDefaultUI,subjectID);
    }
}
