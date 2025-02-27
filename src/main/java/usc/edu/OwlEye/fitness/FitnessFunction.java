package usc.edu.OwlEye.fitness;

import gatech.xpert.dom.DomNode;
import org.tinylog.Logger;
import usc.edu.OwlEye.GA.GAChromosome;
import usc.edu.OwlEye.OwlConstants;
import usc.edu.OwlEye.OwlEye;
import usc.edu.OwlEye.VHTree.XMLUtils;
import usc.edu.SALEM.Constants;
//import usc.edu.SALEM.GA.GAChromosome;
import gatech.xpert.dom.Node;
import usc.edu.SALEM.util.Util;

import java.io.IOException;
import java.util.HashMap;
//import usc.edu.SALEM.VHTree.XMLUtils;

public class FitnessFunction {

    private transient static int fitnessCalls;
    private static double fitnessTimeInSec = Double.MIN_VALUE;
    private double fitnessScore = Double.MAX_VALUE;

//    private String fitnessScoreBreakdown;
    private String fitnessScoreSummary;

    // Objectives scores

    private double missingElementsScore = Double.MAX_VALUE;
//    private double fontSizeScore=Double.MAX_VALUE;
    private double minimumChangesScore=Double.MAX_VALUE;
    private double textScore =Double.MAX_VALUE;;
    private double positioningAndAlignmentsScore=Double.MAX_VALUE;
    private double collisionScore=Double.MAX_VALUE;
    private CollisionObjective collisionObjective;
    private FontSizeObjective fontSizeObjective;
    private  TextObjective textObjective;
    private PositioningAndAlignmentsObjective positioningAndAlignmentsObjective;
    private MissingElementsObjective missingElementsObjective ;
    private MinimumChangesObjective minimumChangesObjective;
    private HashMap<String, Integer> NumberOfUIssues =new HashMap<>();
    private String NumerOfIssusString ="";

    public FitnessFunction() {
        fitnessCalls = 0;
        collisionObjective = new CollisionObjective();
        fontSizeObjective = new FontSizeObjective();
        textObjective = new TextObjective();
        minimumChangesObjective = new MinimumChangesObjective();
        positioningAndAlignmentsObjective = new PositioningAndAlignmentsObjective();
        missingElementsObjective = new MissingElementsObjective();
        this.NumberOfUIssues =new HashMap<>();

    }














    public void calculateFitnessScore(GAChromosome chromosome, String newLayoutFolder, boolean DebugFitness) throws IOException {
        String chromosomeIdentifier;
        if (DebugFitness){
            chromosomeIdentifier=newLayoutFolder.split("/")[newLayoutFolder.split("/").length-1];
            newLayoutFolder = newLayoutFolder ;//+"/refined/";
          //  chromosomeIdentifier = "org.y20k.transistor_chromosome_initialization_0_1.apk";
            Logger.info("Debug and test fitness function");

    }else

    {
         chromosomeIdentifier = chromosome.getChromosomeIdentifier();
    }
        boolean printFitnessDetails = true;
        if (newLayoutFolder == null) {  // We could not read the layout so no point of calculating UI. we set as worst
            //Could not get the correct activity

//            fontSizeScore = Double.MAX_VALUE;
            collisionScore = Double.MAX_VALUE;
            textScore = Double.MAX_VALUE;
            positioningAndAlignmentsScore = Double.MAX_VALUE;
            missingElementsScore = Double.MAX_VALUE;
            minimumChangesScore = Double.MAX_VALUE;
            printFitnessDetails = false;

        } else {

            String newLayout = newLayoutFolder + OwlEye.getOriginalActivityName() + ".xml";
//            UI tempDynamic = new UI(newLayout, OwlEye.getOriginalActivityName());
//            XMLUtils defaultOrgVH= new XMLUtils(newLayout);
            Node<DomNode> newLayoutRoot = XMLUtils.readCrawledXML_T(newLayout);  //Just reading it to get a new dynamic VH

            long startTimeUsabilityScore = System.nanoTime();
            boolean debugTextObjective = false;
            if (!debugTextObjective) {
                // (1) Calculate Positioning and Alignments Score
//                 positioningAndAlignmentsObjective = new PositioningAndAlignmentsObjective();
                positioningAndAlignmentsScore = positioningAndAlignmentsObjective.calculatePositioningAndAlignmentsScore(newLayoutRoot);
                Logger.debug("sigmoid positioning & alignments:" + Util.sigmoid(positioningAndAlignmentsScore));
                Logger.trace("Positioning And Alignments  Score for chromosome ID: " + chromosomeIdentifier + " is: " + positioningAndAlignmentsScore);

                // (2) Calculate Collision Score
//                collisionObjective = new CollisionObjective();
                // We pass violations from positioning and alignments objective to collision objective because we have already found the collisions so now we just pass them and calculate a score
                collisionScore = collisionObjective.calculateObjectiveScore(newLayoutRoot, OwlEye.getCurrentIntersectionLayoutIssues());
                Logger.trace("collision Score for chromosome ID: " + chromosomeIdentifier + " is: " + collisionScore);

                // (3) Calculate Font Size Score

                fontSizeObjective = new FontSizeObjective();
                // fontSizeScore = fontSizeObjective.calculateFontSizeScore(newLayoutRoot);

                // (5) Calculate Missing Elements Score

//                MissingElementsObjective missingElementsObjective = new MissingElementsObjective();
                missingElementsScore = missingElementsObjective.calculateMissingElementsScore(newLayoutRoot);
                Logger.trace("Missing Elements Score for chromosome ID: " + chromosomeIdentifier + " is: " + missingElementsScore);
                long endTime = System.nanoTime();
            }
            // (4) Calculate Text Score
//            textObjective = new TextObjective();
            // We need screenshot of the new layout to calculate text score
            textScore = textObjective.calculateObjectiveScore(OwlEye.getOriginalAppID(), newLayoutRoot, newLayout, newLayout.replace(".xml", ".png"));
            minimumChangesObjective = new MinimumChangesObjective();
            minimumChangesScore = minimumChangesObjective.calculateObjectiveScore(newLayoutRoot);
            Logger.debug("Text issues Score for chromosome ID: " + chromosomeIdentifier + " is: " + textScore);
        }
            double distanceCollision = collisionScore - OwlConstants.EXPECTED_COLLISION_SCORE;
            double distanceTextScore = textScore - OwlConstants.EXPECTED_TEXT_SCORE;
            double distanceMissingElements = missingElementsScore - OwlConstants.EXPECTED_MISSING_ELEMENTS_SCORE;

            double distanceChangeScore =Util.sigmoid( minimumChangesScore) ;// sigmoid(minimumChangesScore)
        double distancePositioningAlignments = Util.sigmoid(positioningAndAlignmentsScore);

            double textWeight=OwlConstants.WEIGHT2;
            double missingElementsWeight=OwlConstants.WEIGHT2;
        double collisionWeight=OwlConstants.WEIGHT2;
        double positioningWeight=OwlConstants.WEIGHT1 ;
        double minimumChangesWeight=OwlConstants.WEIGHT1 ;
        double collisionPower=OwlConstants.EXP2;
        double textPower=OwlConstants.EXP2;
        double missingElementsPower=OwlConstants.EXP25;
        double positioningPower=OwlConstants.EXP1;
        double minimumChangesPower=OwlConstants.EXP1;

            if (OwlEye.getOriginalCollisionIssues().size()>0)
            {
                collisionWeight = 3;
               // collisionPower=OwlConstants.EXP25;
                collisionPower=OwlConstants.EXP2;
            }
            fitnessScore = (textWeight * Math.pow(distanceTextScore, textPower))+
                    (missingElementsWeight* Math.pow(distanceMissingElements, missingElementsPower)) +
                    (collisionWeight * Math.pow(distanceCollision, collisionPower)) +  +
                    (positioningWeight * Math.pow(distancePositioningAlignments, positioningPower)) +
                    + (minimumChangesWeight*Math.pow(distanceChangeScore, minimumChangesPower));
        if (Double.isNaN(fitnessScore)) {
            fitnessScore = Double.MAX_VALUE;
        }


        calculateNumberOfIssues();
//        fitnessScoreBreakdown = "positioningAndAlignmentsScore = " + positioningAndAlignmentsScore + ", collisionScore = " + collisionScore +
//                ", textScore = " + textScore + ", distanceMissingElements = " + distanceMissingElements + " =>=> " +
//                positioningWeight+ "* (" + distancePositioningAlignments + "^" + Constants.EXP2 + ") + "
//                +collisionWeight + "* (" + distanceCollision + "^" + OwlConstants.EXP1 + ") + "
//                + textWeight+ "* (" + distanceTextScore + "^" + OwlConstants.EXP1 + ") + "
//                +missingElementsWeight + "* (" + distanceMissingElements + "^" + OwlConstants.EXP1 + ") + "
//                + minimumChangesWeight+ "* (" + distanceChangeScore + "^" + OwlConstants.EXP2 + ")";


            fitnessScoreSummary = "total score="+fitnessScore+"\nFormula: "
                    + textWeight + "* (" + distanceTextScore + "^" + OwlConstants.EXP2 + ") + "
                    + missingElementsWeight+ "* (" + distanceMissingElements + "^" + OwlConstants.EXP2 + ")"
                    + collisionWeight+ "* (" + distanceCollision + "^" + OwlConstants.EXP2 + ") + "
                    + positioningWeight + "* (" + distancePositioningAlignments + "^" + OwlConstants.EXP1 + ") + "
                    + minimumChangesWeight+ "* (" + distanceChangeScore + "^" + OwlConstants.EXP1 + ")";

            if(printFitnessDetails) {
                fitnessScoreSummary +=
                        "\nDetails:" +
                                "\n1-textScore = " + textScore + " | ( " + textObjective.getNumberOfViolations()+ ","+textObjective.getAmountOfViolation()+")"
                                + "\n2-MissingElements = " + missingElementsScore + " (rawScore=" + missingElementsObjective.getRawScore()
                                + "\n3-collisionScore = " + collisionScore + " | " + collisionObjective.getNumberOfViolations() + ", " + collisionObjective.getAmountOfViolation()+ ", "+collisionObjective.rawScore
                                + "\n4-positioningAndAlignmentsScore = " + positioningAndAlignmentsScore
                                + "(directional amount & no: " + positioningAndAlignmentsObjective.getDirectionalScore() + ", "
                                + positioningAndAlignmentsObjective.getNoOfDirectionalIssues() +
                                " | alignment: " + positioningAndAlignmentsObjective.getAlignmentScore() + ", " + positioningAndAlignmentsObjective.getNoOfAlignmentIssues() + ")"
                                +  "\n5-minimumChangesScore = " + minimumChangesScore + " (rawScore=" + minimumChangesObjective.getObjectiveScore()+
                                " <"+minimumChangesObjective.sizeRawScore+", "+minimumChangesObjective.locationRawScore+">)";

            }







            Logger.debug(fitnessScoreSummary);
            if(!DebugFitness) {
                chromosome.setFitnessFunctionObj(this);

            }

            fitnessCalls++;

        }

    private void calculateNumberOfIssues() {
        // number of collision issues
        int noOfCollisionIssues = collisionObjective.getNumberOfViolations();
        // number of missing elements issues
        int noOfMissingElementsIssues = missingElementsObjective.getNumberOfMissingElements();
        // number of text issues
        int noOfTextIssues = textObjective.getNumberOfViolations();
        // number of positioning and alignment issues
    NumerOfIssusString = "noOfCollisionIssues = " + noOfCollisionIssues + ", noOfMissingElementsIssues = " + noOfMissingElementsIssues + ", noOfTextIssues = " + noOfTextIssues;
//        NumberOfUIssues.put("noOfCollisionIssues", noOfCollisionIssues);
//        NumberOfUIssues.put("noOfMissingElementsIssues", noOfMissingElementsIssues);
//        NumberOfUIssues.put("noOfTextIssues", noOfTextIssues);
    }






// pareto front calculation


    /* Setters and Getters */

    public static int getFitnessCalls() {
        return fitnessCalls;
    }

    public static void setFitnessCalls(int fitnessCalls) {
        FitnessFunction.fitnessCalls = fitnessCalls;
    }

    public static double getFitnessTimeInSec() {
        return fitnessTimeInSec;
    }

    public static void setFitnessTimeInSec(double fitnessTimeInSec) {
        FitnessFunction.fitnessTimeInSec = fitnessTimeInSec;
    }



    public double getTextScore() {
        return textScore;
    }

    public void setTextScore(double textScore) {
        this.textScore = textScore;
    }

    public double getFitnessScore() {
        return fitnessScore;
    }

    public void setFitnessScore(double fitnessScore) {
        this.fitnessScore = fitnessScore;
    }
//    public String getFitnessScoreBreakdown() {
//        return fitnessScoreBreakdown;
//    }

    public String getFitnessScoreSummary() {
        return fitnessScoreSummary;
    }
//    public void setFitnessScoreBreakdown(String fitnessScoreBreakdown) {
//        this.fitnessScoreBreakdown = fitnessScoreBreakdown;
//    }
//    @Override
//    public String toString() {
//        String ret = fitnessScore + " => " + fitnessScoreBreakdown;
//        return ret;
//    }


    @Override
    public String toString() {
        String ret = fitnessScore + " => " + fitnessScoreSummary;
        return ret;
    }
    public HashMap<String, Integer> getNumberOfUIssues() {
        return NumberOfUIssues;
    }
    public void setNumberOfUIssues(HashMap<String, Integer> numberOfUIssues) {
        this.NumberOfUIssues = numberOfUIssues;
    }

    public String printNumberOfIssuesAfter() {
    return NumerOfIssusString;
    }

    public double getMissingElementsScore() {
        return missingElementsScore;
    }
    public double getCollisionScore() {
        return collisionScore;
    }
    public double getPositioningAndAlignmentsScore() {
        return positioningAndAlignmentsScore;
    }
    public double getMinimumChangesScore() {
        return minimumChangesScore;
    }
    public double getMissingElementScore() {
        return missingElementsScore;
    }
}
