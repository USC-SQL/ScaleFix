import usc.edu.OwlEye.BuildModels.ConstructSRG;
import usc.edu.OwlEye.OwlConstants;
import usc.edu.OwlEye.OwlEye;
import usc.edu.OwlEye.UIModels.SRG;
import usc.edu.OwlEye.VH.UI;
import usc.edu.OwlEye.VHTree.XMLUtils;
import usc.edu.OwlEye.util.Utils;
import usc.edu.SALEM.util.LoadConfig;

import java.io.File;
import java.util.HashMap;

public class testConstructSRG {











    public static void main(String[] args) {
        System.out.println("Hello World!");

        HashMap<String, String> propConfig = LoadConfig.getInstance().getConfig_data();
        String subjectID="";
        String appName = "";
        String activityName = "";
        System.out.println("ARGS:" + args.length);
        if (args.length > 0) {
            System.out.println("subjectID: " + args[0]);
            subjectID=args[0];
        } else {
            subjectID="vlc";


        }

        String[] subjectsInfo = Utils.readSubjectsCSV(propConfig.get("subjects_csv"), subjectID);
        if(subjectsInfo==null){
            System.out.println("Subject not found");
            System.exit(1);
        }
        appName=subjectsInfo[0];
        activityName=subjectsInfo[1];

        OwlEye.setOriginalActivityName(activityName);

        String VHPathPlaceHolder=propConfig.get("subjects_base_path")+ File.separator+subjectID+File.separator+"%s"+File.separator+propConfig.get("device_title");

        String VHPath=String.format(VHPathPlaceHolder,propConfig.get("complete_vh"));
        String VHPlaceHolder=VHPath+ File.separator+"%s"+File.separator
                + OwlEye.getOriginalActivityName()+".xml";


        String originalDefaultXMLPath=String.format(VHPlaceHolder, OwlConstants.DEFAULT_FONT_FOLDER);
        XMLUtils defaultOrgVH= new XMLUtils(originalDefaultXMLPath);

        UI originalDefaultUI=new UI("original_default",defaultOrgVH,originalDefaultXMLPath,"");
        ConstructSRG constructSRG= ConstructSRG.getInstance();
        SRG srg = constructSRG.buildGraph(originalDefaultUI);
        System.out.println("SRG: " + srg);


    }
}
