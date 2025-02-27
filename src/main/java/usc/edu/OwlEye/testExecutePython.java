package usc.edu.OwlEye;

import usc.edu.SALEM.Constants;
import usc.edu.SALEM.util.LoadConfig;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static usc.edu.SALEM.util.Util.executeProcess;

public class testExecutePython {


    public static void main(String args[]) throws IOException {
        HashMap<String, String> propConfig = LoadConfig.getInstance().getConfig_data();
        String cmd = propConfig.get("python")+" "+ propConfig.get("text_cut_off_script");
        runCommand(cmd,
            null,"null");
    }


    public static void runCommand(String cmd, String directory, String commandType) throws IOException {
        String[] act = new String[0];
        final Process p;

            // No need to set a directory
            System.out.println("CMD: "+ cmd);
            p = Runtime.getRuntime().exec(cmd);
//            executeProcess(p);


            act =  usc.edu.SALEM.util.Util.executeProcess(p, "text_cutoff");
            System.out.println("SCORE: "+ act[0]);


    }

}
