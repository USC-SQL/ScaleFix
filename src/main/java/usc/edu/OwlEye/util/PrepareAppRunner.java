package usc.edu.OwlEye.util;

import usc.edu.OwlEye.GA.GAChromosome;
import usc.edu.OwlEye.OwlConstants;
import usc.edu.OwlEye.OwlEye;
import usc.edu.SALEM.Constants;
//import usc.edu.SALEM.GA.GAChromosome;
import usc.edu.SALEM.SALEM;
import usc.edu.SALEM.util.Util;

import java.io.File;

public class PrepareAppRunner implements Runnable {

    //        private final String apkName;
//        private final String apk_path;
    GAChromosome chromosome;
    String chromosomeIdentifier;
    private final String chromosomeOutPutFolder;
    String basePath = "/home/testing/AppSet/accessibility/TT_scripts";
    String COMPILE_SIGNATURE_KEY = basePath + File.separator + "apks_folder" + "/" + "app_with_ids_key.keystore";

    //    PrepareAppRunner(String apkName,String url,String output) {
//
//            this.outputPath = output;
//        }
    public PrepareAppRunner(String chromosomeIdentifier, GAChromosome chromosome, String chromosomeOutPutFolder) {

        this.chromosome = chromosome;
        this.chromosomeOutPutFolder = chromosomeOutPutFolder;
        this.chromosomeIdentifier = chromosomeIdentifier;
    }



    @Override
    public void run() {

        String result = "";
        int code = 200;
        try {

//                String chromosomeOutPutFolder = Util.WriteChromosomeChangesToFileSegmentRelationGraph(this.chromosome, this.outputPath);
            String[] apkRes = Utils.compileApp(chromosomeIdentifier, chromosomeOutPutFolder, OwlEye.getCompileOutputPath());
            String newApkName = apkRes[0];  // the new apk name
            String compiledAPKPath = apkRes[1];  // The full path for apk
            chromosome.setNewApkName(newApkName);
            chromosome.setAPKPath(compiledAPKPath);
            chromosome.setDecompiledPath(chromosomeOutPutFolder);
            //(c) Move the apk to the detection tool folder
           // Util.moveAPKtoFolder(compiledAPKPath, SALEM.Detection_Tool_APK_PATH);


        } catch (Exception e) {
            System.err.println("Error in Runner inside executer");
            e.printStackTrace();

        }

    }


}
