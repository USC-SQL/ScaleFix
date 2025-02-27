import gatech.xpert.dom.DomNode;
import org.xml.sax.SAXException;
import usc.edu.OwlEye.util.Utils;
import gatech.xpert.dom.Node;
import usc.edu.SALEM.util.LoadConfig;
import usc.edu.layoutgraph.LayoutGraphBuilder;
import usc.edu.layoutgraph.edge.NeighborEdge;
import usc.edu.layoutissue.Issue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class detecCollisionBetweenDefaultAndLargest {


    private static LayoutGraphBuilder lgb;

    public static void main(String[] args) throws SAXException, IOException, InterruptedException {
        String default_ui_version_type = "DD";
        String largest_ui_version_type = "LL";
        String subjectID= "budgetp";
        String appName="";
        String activityName="";
        HashMap<String, String> propConfig = LoadConfig.getInstance().getConfig_data();



        String[] subjectsInfo = Utils.readSubjectsCSV(propConfig.get("subjects_csv"), subjectID);
        if(subjectsInfo==null){
            System.out.println("Subject not found");
            System.exit(1);
        }
        appName=subjectsInfo[0];
        activityName=subjectsInfo[1];


        String VHPathPlaceHolder = propConfig.get("subjects_base_path") + File.separator + subjectID + File.separator + "%s" + File.separator + propConfig.get("device_title");//+File.separator+appName;

        String completeVH = String.format(VHPathPlaceHolder, propConfig.get("complete_vh"));


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




        String originalFile = defaultFontCompleteVH+File.separator +
                activityName + ".xml";
//        String chromosome="uk.co.bitethebullet.android.token_601-05-2021-01-42-57-PM";
        String repaired = largestFontCompleteVH+File.separator +
                activityName + ".xml";
//        repaired=originalFile;

        usc.edu.OwlEye.VHTree.XMLUtils defaultOrgVH= new usc.edu.OwlEye.VHTree.XMLUtils(originalFile);
        usc.edu.OwlEye.VHTree.XMLUtils largestOrgVH= new usc.edu.OwlEye.VHTree.XMLUtils(repaired);
        Node<DomNode> originalRoot=defaultOrgVH.getRoot();
        Node<DomNode> repairedRoot=largestOrgVH.getRoot();


        lgb = new LayoutGraphBuilder(originalRoot, repairedRoot);
        ArrayList<Issue> potentialLayoutIssues = lgb.compareLayoutGraphs();
        //System.out.println("Potential layout issues: " + potentialLayoutIssues.size());
        for (Issue issue : potentialLayoutIssues) {
            int x=0;

            if (issue.getIssueType().toString().equalsIgnoreCase("INTERSECTION") || issue.getIssueType().toString().equalsIgnoreCase("OVERLAP")||x==0) {
                if(issue.getBaselineEdge().getNode1().getDomNode().getData().getxPath().contains("bottom_navigation")){
                    System.out.println("Skipped!!!!");
                    continue;
                }
                System.out.println("Baseline Edge: " + issue.getBaselineEdge());
                NeighborEdge base = issue.getBaselineEdge();
                System.out.println("Large Edge: " + issue.getPageUnderTestEdge());
                NeighborEdge under = issue.getPageUnderTestEdge();

                System.out.println("Large Edge: " + issue.getIssueAmount());
                System.out.println("\t\t");
            }

        }





    }



}
