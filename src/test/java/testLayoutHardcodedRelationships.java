import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import org.xml.sax.SAXException;
import usc.edu.OwlEye.BuildModels.ConstructSPLRG;
import usc.edu.OwlEye.BuildModels.ConstructSRG;
import usc.edu.OwlEye.OwlEye;
import usc.edu.OwlEye.UIModels.SPLRG;
import usc.edu.OwlEye.UIModels.SPLRGNode;
import usc.edu.OwlEye.UIModels.SRG;
import usc.edu.OwlEye.VH.UI;
import usc.edu.OwlEye.VHTree.XMLUtils;
import usc.edu.OwlEye.fitness.MissingElementsObjective;
import usc.edu.OwlEye.util.Utils;
import usc.edu.SALEM.util.LoadConfig;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class testLayoutHardcodedRelationships {





    public static void main(String[] args) throws SAXException, IOException, InterruptedException {
        String default_ui_version_type = "default_font";
        String largest_ui_version_type = "largest_font";
        String subjectID = "8vim";
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





        UI originalDefaultUI=new UI("original_default",defaultOrgVH,originalFile,originalPNGFile);
        ConstructSPLRG constructSPLRG= ConstructSPLRG.getInstance();
        OwlEye.setOriginalDefaultUI(originalDefaultUI);
        SPLRG splrg = constructSPLRG.buildGraph(originalDefaultUI);
        for (Map.Entry<String, TreeMap<String, List<SPLRGNode>>> entry : splrg.getDependentNodesMap().entrySet()) {
            System.out.println("Node: "+entry.getKey() +" | Size of dependent nodes: "+entry.getValue().size());
//            for (SPLRGNode node : entry.getValue()) {
//                System.out.println(node+"\n**");
//            }
            System.out.println("\n-------------------------------------------------");

        }


    }
    }
