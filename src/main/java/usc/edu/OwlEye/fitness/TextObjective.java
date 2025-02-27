package usc.edu.OwlEye.fitness;

import gatech.xpert.dom.DomNode;
import org.tinylog.Logger;
import usc.edu.OwlEye.OwlEye;
import usc.edu.OwlEye.fitness.fitnessViolations.TextViolation;
import gatech.xpert.dom.Node;
import usc.edu.OwlEye.util.Utils;
import usc.edu.SALEM.util.LoadConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TextObjective extends FitnessObjective{


//    private double textScore;
//
//    ArrayList<TextViolation> violations;
//    private double formulaScore;
//    private double rawScore;
//    private int numberOfTextCutOff;

    public TextObjective() {
//
//        this.textScore = Double.MIN_VALUE;
//        violations = new ArrayList<>();
    }


    public double calculateObjectiveScore(String apkId,Node<DomNode> newLayoutRoot, String repairUIXML,String repairUIPNG) throws IOException {
        // Run the python script to get a score for the text cut off

//        String apkId="openfoodm";
        String args= apkId+" "+repairUIXML+" "+repairUIPNG+" "+OwlEye.getOriginalDefaultUI().getXmlFilePath() ;
        //March21 changing to dynamic vh
        //String args= apkId+" "+repairUIXML+" "+repairUIPNG+" "+OwlEye.REFINED_DYNAMIC_DD_LAYOUT_FILE_PATH ;

        String textScoreText = "";
        String scriptfile= LoadConfig.getConfig_data().get("text_cut_off_script");
        String scriptFileName = new File(scriptfile).getName();
        String ScriptFolder=scriptfile.replace(scriptFileName,"");
        Logger.debug("Text command: "+args);
        textScoreText= Utils.runPythonScript(scriptfile,args,ScriptFolder,"text_cutoff");
        String cmd = LoadConfig.getConfig_data().get("python_interpreter")+" "+ LoadConfig.getConfig_data().get("text_cut_off_script");
//        try {
//            results=runCommand(cmd,
//                    null,"text_cutoff");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        double amountOfTextCutOff= 0;
        if (textScoreText !="" && textScoreText != null){
            // [0] is the amount of text cut off and [1] is the number of elements with text cut off
            amountOfTextCutOff=Double.parseDouble(textScoreText.split("-")[0]);
            numberOfViolations=Integer.parseInt(textScoreText.split("-")[1]);
        }
        double adjustedAmount=2;

            if (OwlEye.getOriginalCollisionIssues().size()>0){
                adjustedAmount=0;
            }
            double numberScore=Double.valueOf(numberOfViolations);
            if (numberOfViolations==0) {
                adjustedAmount = 0;
            }
            rawScore=adjustedAmount+amountOfTextCutOff*100;
           // double amountScore=adjustedAmount+amountOfTextCutOff*100;
//             formulaScore =  (2 + amountOfTextCutOff)*100 + (Double.valueOf(numberOfTextCutOff)/10); // +2 to avoid small numbers (2 + or *2)

            objectiveScore = rawScore + (numberScore/10); // +2 to avoid small numbers (2 + or *2)

        if(objectiveScore>100){
            objectiveScore=100;
        }
        else if(objectiveScore<0){
            objectiveScore=0;
        }


        this.rawScore=amountOfTextCutOff;
        this.amountOfViolation=amountOfTextCutOff;

        return objectiveScore;
    }

//    public double calculateTextScore(String apkId,Node<DomNode> newLayoutRoot, String repairUIXML,String repairUIPNG) throws IOException {
//        // Run the python script to get a score for the text cut off
//
////        String apkId="openfoodm";
//      String args= apkId+" "+repairUIXML+" "+repairUIPNG+" "+OwlEye.getOriginalDefaultUI().getXmlFilePath() ;
//        String textScoreText = "";
//        String scriptfile= LoadConfig.getConfig_data().get("text_cut_off_script");
//        String scriptFileName = new File(scriptfile).getName();
//        String ScriptFolder=scriptfile.replace(scriptFileName,"");
//        Logger.debug("Text command: "+args);
//       textScoreText= Utils.runPythonScript(scriptfile,args,ScriptFolder,"text_cutoff");
//        String cmd = LoadConfig.getConfig_data().get("python_interpreter")+" "+ LoadConfig.getConfig_data().get("text_cut_off_script");
////        try {
////            results=runCommand(cmd,
////                    null,"text_cutoff");
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//        double amountOfTextCutOff= 0;
//        if (textScoreText !="" && textScoreText != null){
//            // [0] is the amount of text cut off and [1] is the number of elements with text cut off
//            amountOfTextCutOff=Double.parseDouble(textScoreText.split("-")[0]);
//            this.numberOfTextCutOff=Integer.parseInt(textScoreText.split("-")[1]);
//        }
//
//        boolean classicLevenshtein=false;
//        double formulaScore=0;
//        if(classicLevenshtein) {
//            double maxScore = 800;
//            // formula to calculate the score formulaScore=score/maxScore
//             formulaScore = amountOfTextCutOff / maxScore * 100;
//        }
//        else{
//            double numberScore=Double.valueOf(numberOfTextCutOff);
//            double adjustedAmount=2;
//            if (numberOfTextCutOff==0) {
//                adjustedAmount = 0;
//            }
//            double amountScore=adjustedAmount+amountOfTextCutOff*100;
////             formulaScore =  (2 + amountOfTextCutOff)*100 + (Double.valueOf(numberOfTextCutOff)/10); // +2 to avoid small numbers (2 + or *2)
//
//            formulaScore = amountScore + (numberScore/10); // +2 to avoid small numbers (2 + or *2)
//        }
//        if(formulaScore>100){
//            formulaScore=100;
//        }
//        else if(formulaScore<0){
//            formulaScore=0;
//        }
//        this.formulaScore=formulaScore;
//
//        this.rawScore=amountOfTextCutOff;
//        this.textScore=rawScore;
//
//        return this.formulaScore;
//
//
//
//
//    }
//    public double getFormulaScore() {
//        return formulaScore;
//    }
//    public void setFormulaScore(double formulaScore) {
//        this.formulaScore = formulaScore;
//    }
//
//    public double getRawScore() {
//        return rawScore;
//    }
//    public void setRawScore(double rawScore) {
//        this.rawScore = rawScore;
//    }
//
//
//public void setNumberOfTextCutOff(int numberOfTextCutOff) {
//        this.numberOfTextCutOff = numberOfTextCutOff;
//    }
//
//    public int getNumberOfTextCutOff() {
//        return numberOfTextCutOff;
//    }
//
//    public double getTextScore() {
//        return textScore;
//    }
//
//    public void setTextScore(double textScore) {
//        this.textScore = textScore;
//    }
//
//    public ArrayList<TextViolation> getViolations() {
//        return violations;
//    }
//
//    public void setViolations(ArrayList<TextViolation> violations) {
//        this.violations = violations;
//    }

}
