//package TTfix.fitness;
//
//import TTfix.Constants;
//import TTfix.GA.GAChromosome;
//import TTfix.TTFix;
//import TTfix.VHTree.DependentNode;
//import TTfix.VHTree.Node;
//import TTfix.VHTree.Rectangle;
//import TTfix.VHTree.XMLUtils;
//import TTfix.segmentation.*;
//import TTfix.util.Util;
//import gatech.xpert.dom.DomNode;
//import issuesfilter.*;
//import layoutgraph.LayoutGraphBuilder;
//import layoutissue.Issue;
//
//import java.util.*;
//
//public class FitnessFunction_BEFORE_ZEEZO {
//    private transient static int fitnessCalls;
//    private static double fitnessTimeInSec = Double.MIN_VALUE;
//
//    private double accessibilityScore = Double.MIN_VALUE;
//    private double aestheticScore = Double.MIN_VALUE;
//    private double fitnessScore = Double.MIN_VALUE;
//    private double spacingScore = Double.MIN_VALUE;
//    private double sizeChangeScore=Double.MIN_VALUE;
//
//    private double amountOfChangeInSize = 0;  //  I calculate it as part of hte accessibiltyscore method but  I use this number in asethtic score
//    private int current_no_TT = 0;
//    private HashMap<String, Double> baseSizeChange;
////    private double sizeChangeScore;
//    private double totalPossibleUsabilityScore;  // largest possible number of TT for an activity: issues=number of touch targets*2
//    private double sizeBaseScore; // total size of the original layout before change
//
//    private String aestheticScoreBreakdown;
//    private String fitnessScoreBreakdown;
//    private double NoIssuesPercentage;
//    private int noIssuesInChromosome;
//
//
//    public FitnessFunction_BEFORE_ZEEZO() {
//        aestheticScoreBreakdown = "";
//        fitnessScoreBreakdown = "";
//        baseSizeChange = new HashMap<>();
//    }
//
//    public int getNoIssuesInChromosome() {
//        return noIssuesInChromosome;
//    }
//
//    public void setNoIssuesInChromosome(int noIssuesInChromosome) {
//        this.noIssuesInChromosome = noIssuesInChromosome;
//    }
//
//    public int getCurrent_no_TT() {
//        return current_no_TT;
//    }
//
//    public void setCurrent_no_TT(int current_no_TT) {
//        this.current_no_TT = current_no_TT;
//    }
//
//    public double getNoIssuesPercentage() {
//        return NoIssuesPercentage;
//    }
//
//    public double getAmountOfChangeInSize() {
//        return amountOfChangeInSize;
//    }
//
//    public void setAmountOfChangeInSize(double amountOfChangeInSize) {
//        this.amountOfChangeInSize = amountOfChangeInSize;
//    }
//
//    public void setNoIssuesPercentage(double noIssuesPercentage) {
//        NoIssuesPercentage = noIssuesPercentage;
//    }
//
//    public void setBaseSizeChange(HashMap<String, Double> changeMap) {
//        if (this.baseSizeChange == null || this.baseSizeChange.size() == 0 || changeMap == null) {
//            calculateSizeChanging();
//        } else {
//            baseSizeChange = changeMap;
//        }
//    }
//
//    public static void setFitnessCalls(int fitnessCalls) {
//        FitnessFunction_BEFORE_ZEEZO.fitnessCalls = fitnessCalls;
//    }
//
//    public static void setFitnessTimeInSec(double fitnessTimeInSec) {
//        FitnessFunction_BEFORE_ZEEZO.fitnessTimeInSec = fitnessTimeInSec;
//    }
//
//    public double getSpacingScore() {
//        return spacingScore;
//    }
//
//    public void setSpacingScore(double spacingScore) {
//        this.spacingScore = spacingScore;
//    }
//
//    public static int getFitnessCalls() {
//        return fitnessCalls;
//    }
//
//    public static double getFitnessTimeInSec() {
//        return fitnessTimeInSec;
//    }
//
//    public double getFitnessScore() {
//        return fitnessScore;
//    }
//
//    public void setFitnessScore(double fitnessScore) {
//        this.fitnessScore = fitnessScore;
//    }
//
//    public void setAccessibilityScore(double accessibilityScore) {
//        this.accessibilityScore = accessibilityScore;
//    }
//
//    public void setAestheticScore(double aestheticScore) {
//        this.aestheticScore = aestheticScore;
//    }
//
//    public double getAccessibilityScore() {
//        return accessibilityScore;
//    }
//
//    public double getAestheticScore() {
//        return aestheticScore;
//    }
//
//    public String getAestheticScoreBreakdown() {
//        return aestheticScoreBreakdown;
//    }
//
//    public void setAestheticScoreBreakdown(String aestheticScoreBreakdown) {
//        this.aestheticScoreBreakdown = aestheticScoreBreakdown;
//    }
//
//    public double calculateSizeChanging() {
//        Node<DomNode> root = XMLUtils.getRoot();
//        Queue<Node<DomNode>> q = new LinkedList<Node<DomNode>>();
//        q.add(root);
////        if (root.getChildren() != null) {
////            for (Node<DomNode> child : root.getChildren()) {
////                q.add(child);
////            }
////        }
////
////        // process descendants of the root in a bread first fashion
//        HashMap<String, Double> sizeChange = new HashMap<String, Double>();
//        double THeight = 0.0;
//        double TWidth = 0.0;
//        double NHeight = 0.0;
//        double NWidth = 0.0;
//
//
//        while (!q.isEmpty()) {
//            Node<DomNode> currMergedNode = q.remove();
////            DomNode currMergedNode = node.getData();
//            double height = Util.convertValueToDP(currMergedNode.getData().height);
//            double width = Util.convertValueToDP(currMergedNode.getData().width);
//
//            if (Util.isElementClickable(currMergedNode.getData())) { // touch target
//                THeight += height;
//                TWidth += width;
//            } else {
//                NHeight += height;
//                NWidth += width;
//            }
//            if (currMergedNode.getChildren() != null) {
//                for (Node<DomNode> child : currMergedNode.getChildren()) {
//                    q.add(child);
//                }
//            }
//        }
//
//        if (baseSizeChange.size() == 0) {  //first time calling it so it is the base
//            baseSizeChange.put("THeight", THeight);
//            baseSizeChange.put("TWidth", TWidth);
//            baseSizeChange.put("NHeight", NHeight);
//            baseSizeChange.put("NWidth", NWidth);
//        }
//        sizeChange.put("THeight", THeight);
//        sizeChange.put("TWidth", TWidth);
//        sizeChange.put("NHeight", NHeight);
//        sizeChange.put("NWidth", NWidth);
//
//
//        double TTHeight = (THeight - baseSizeChange.get("THeight")) / baseSizeChange.get("THeight");
//        double TTWidth = (TWidth - baseSizeChange.get("TWidth")) / baseSizeChange.get("TWidth");
//        double NNHeight = (NHeight - baseSizeChange.get("NHeight")) / baseSizeChange.get("NHeight");
//        double NNWidth = (NWidth - baseSizeChange.get("NWidth")) / baseSizeChange.get("NWidth");
//
//        return 3 * (TTHeight + TTWidth) - 0.3 * (NNHeight + NNWidth);
//
//
//    }
//
//    public double getTotalPossibleUsabilityScore() {
//        return totalPossibleUsabilityScore;
//    }
//
//    public String getFitnessScoreBreakdown() {
//        return fitnessScoreBreakdown;
//    }
//
//    public void setFitnessScoreBreakdown(String fitnessScoreBreakdown) {
//        this.fitnessScoreBreakdown = fitnessScoreBreakdown;
//    }
//
//    public void setTotalPossibleUsabilityScore(double totalPossibleUsabilityScore) {
//        this.totalPossibleUsabilityScore = totalPossibleUsabilityScore;
//    }
//
//    public double getSizeChangeScore() {
//        return sizeChangeScore;
//    }
//
//    public void setSizeChangeScore(double sizeChangeScore) {
//        this.sizeChangeScore = sizeChangeScore;
//    }
//
//    public double getSizeBaseScore() {
//        return sizeBaseScore;
//    }
//
//    public void setSizeBaseScore(double sizeBaseScore) {
//        this.sizeBaseScore = sizeBaseScore;
//    }
//
//
//
//    public double calculateUsabilityScore(String chromosomeIdentifier, Node<DomNode> newLayoutRoot) {
//        AccessibilityScannerResults chromosomeIssues = TTFix.getDetectionToolResults().get(chromosomeIdentifier);
////        double usabilityScore = chromosomeIssues.calculateAccessibilityScore(newLayoutRoot);
//        double[] usabilityScore;
//        if (Constants.Accessibility_SCORE_APPROACH.equalsIgnoreCase("FROM_ISSUES")) { //FROM_LAYOUT
//
//            usabilityScore = chromosomeIssues.calculateAccessibilityScore(newLayoutRoot);
//        } else {
//            usabilityScore = calculateAccessibilityScore(newLayoutRoot);
//
//        }
//        double major = usabilityScore[0];
//        double minor = usabilityScore[1];
//        double accessibility_score = 110;
//
//        accessibility_score = accessibility_score - major - minor;
//
////        double score = 100 - (usabilityScore[0] * 0.7 + usabilityScore[1] * 0.3);
//
//        this.setAmountOfChangeInSize(usabilityScore[2]);// I use it in the UI change.
//        this.setNoIssuesPercentage(usabilityScore[3]);
//        this.setNoIssuesInChromosome((int) usabilityScore[4]);
//        this.setCurrent_no_TT((int) usabilityScore[5]);
////        return score;
//        accessibility_score = (accessibility_score / 110.0) * 100;
//        return accessibility_score;
////        return usabilityScore;
//    }
//
//    public double[] calculateAccessibilityScore(Node<DomNode> newLayoutRoot) {
//
//        //Read the list of size of Issues for the current activity then claculate the score
//        /* we are calling this method from the object so we can directly use the parameter */
//
//        String curr = TTFix.getCurrentActivityName();
//        double total = 0.0;
//        double totalMissingSize = 0.0;
//        double totalcurrentArea = 0.0;
//        double currentOverallArea = 0.0;// used to calculate overall change in area
//        double numberOfIssues = 0;
//        double sizeChangeInPixel = 0.0;
//        double currentWidth = 0.0;
//        double currentHeight = 0.0;
//        double originalWidth = 0.0;
//        double originalHeight = 0.0;
//        double totalRecommendedArea = 0.0;
//        List<Node<DomNode>> crawledlayoutTouchTargets = Util.getListOfTouchTargets(newLayoutRoot);
//        double no_TT = 0;
//        if (crawledlayoutTouchTargets != null) {
//            no_TT = crawledlayoutTouchTargets.size();
//        }
//        //New approach getting totalMissingSize from all touch targets
//        boolean calculateInitialArea = false;
//        if (TTFix.totalOriginalArea == Double.MIN_VALUE) {
//            calculateInitialArea = true;
//            TTFix.totalOriginalArea = 0;
//        }
//
////        else   if (Constants.Accessibility_SCORE_APPROACH.equalsIgnoreCase("FROM_LAYOUT")) { //FROM_LAYOUT
//        int no_Near_edge = 0;
//        for (Node<DomNode> currTouchTargets : crawledlayoutTouchTargets) {
//            String heightString = Util.getDynamicValueInDDP(currTouchTargets.getData(), "height");
//            String widthString = Util.getDynamicValueInDDP(currTouchTargets.getData(), "width");
//                double normal_recommended_height_width=48.0;
//                double edge_recommended_height_width=32.0;
//            if (heightString != null && widthString != null) {
//                double height = Util.getNumbersFromString(heightString).get(0);
//                double width = Util.getNumbersFromString(widthString).get(0);
//                double currentArea = height * width;
//
//                currentHeight += height;
//                currentWidth += width;
//                currentOverallArea += currentArea;
//                double recommendedArea = Constants.RECOMMENDED_AREA_FOR_TOUCH_TARGET;
//                double recommended_height_width=normal_recommended_height_width;
//                boolean isNearEdge = Util.isNearScreenEdge(currTouchTargets.getData().getBounds());
//
//                if (isNearEdge) {
//                    if(!currTouchTargets.getData().getTagName().equalsIgnoreCase("Button")) {
//                        recommendedArea = Constants.RECOMMENDED_AREA_FOR_EDGES_TOUCH_TARGET;
//                        recommended_height_width = edge_recommended_height_width;
//                        no_Near_edge++;
//                    }
//                }
//                totalRecommendedArea += recommendedArea;
//
//                if (currentArea >= recommendedArea) {
//                    totalcurrentArea += recommendedArea;  // To avoid adding large targets wich will affect calculating the score below
//                } else {
////                    numberOfIssues++;
//                    totalcurrentArea += currentArea;
//                }
//                if(height<recommended_height_width || width<recommended_height_width){
//                    numberOfIssues++;
//                }
//
//            }
//        }
//
//
//        /*** (1) Calculate no issues scores (ie Accessibility score) ***/
//        double noTT = no_TT;
////        double noIssuesScore = numberOfIssues / noTT;
//        // I changed it to divide by the origianl number of TT in the original layout
//        double originalTTNO = TTFix.getCurrentActivityOriginalNoOfTT();
////        double noIssuesScore = numberOfIssues / originalTTNO;
//        if (originalTTNO > no_TT) {
//            double missing_TT = originalTTNO - no_TT;
//            numberOfIssues += missing_TT;   // I am considering all the missing TT as issues > for Now
//        }
//        double noIssuesScore = numberOfIssues / originalTTNO;
//
//        double noIssuesScorePercentage = noIssuesScore * 100;
////        double no_issues=numberOfIssues;
//
//        if (Constants.Accessibility_SCORE_APPROACH.equalsIgnoreCase("FROM_ISSUES")) {
//            double remainingTT = no_TT - numberOfIssues;
//            totalcurrentArea = totalcurrentArea + ((remainingTT *
//                    Constants.RECOMMENDED_AREA_FOR_TOUCH_TARGET));  // Because we only cacluated areas fot TT with issues
//
//
//        }
//        if (calculateInitialArea) {
//            //first time calculating it so it is for the initial UI
//            TTFix.totalOriginalArea = currentOverallArea;
//            TTFix.totalOriginalHeight = currentHeight;
//            TTFix.totalOriginalWidth = currentWidth;
//            TTFix.totalRecommendedArea = totalRecommendedArea;
//        }
//
//        /*** (2) Calculate missing size score ***/
////        double recommendedArea = (mFix.getCurrentActivityOriginalNoOfTT() *
////                Constants.RECOMMENDED_AREA_FOR_TOUCH_TARGET);
//        double missingSizeScore = totalcurrentArea / TTFix.totalRecommendedArea;
////        double missingSizeScorePercentage = 100 - (missingSizeScore * 100);
//        double missingSizeScorePercentage = 10 - (missingSizeScore * 10);
//        double missingSizeScoreNumber = 1 - missingSizeScore;
//        /*** (3) Calculate overall change in area  ***/
//
//
//        double initialArea = TTFix.totalOriginalArea;
//        double changeInAreaPercentage = currentOverallArea / initialArea;
//        // if I want to use change in area
//        changeInAreaPercentage = changeInAreaPercentage * 100;
//        double changeInArea = changeInAreaPercentage - 100;  // positive is size increased , below is size decrease
//        //if I want to change in height and width
//        double changeInSize = (currentHeight - TTFix.totalOriginalHeight) + (currentWidth - TTFix.totalOriginalWidth);
//
//        /***  Calculates all the scores  ***/
//        double minor = missingSizeScorePercentage;
//        double major = noIssuesScorePercentage;
////        double score=noIssuesScorePercentage+missingSizeScoreNumber;
//        /*** (4) Return all the scores  ***/
//        return new double[]{noIssuesScorePercentage, missingSizeScorePercentage, changeInSize, 100 - noIssuesScorePercentage, numberOfIssues, no_TT};
//
//    }
//
//
//    public double[] calculateAccessibilityScore_beforeASE(Node<DomNode> newLayoutRoot) {
//
//        //Read the list of size of Issues for the current activity then claculate the score
//        /* we are calling this method from the object so we can directly use the parameter */
//
//        String curr = TTFix.getCurrentActivityName();
//        double total = 0.0;
//        double totalMissingSize = 0.0;
//        double totalcurrentArea = 0.0;
//        double currentOverallArea = 0.0;// used to calculate overall change in area
//        double numberOfIssues = 0;
//        double sizeChangeInPixel = 0.0;
//        double currentWidth = 0.0;
//        double currentHeight = 0.0;
//        double originalWidth = 0.0;
//        double originalHeight = 0.0;
//        double totalRecommendedArea = 0.0;
//        List<Node<DomNode>> crawledlayoutTouchTargets = Util.getListOfTouchTargets(newLayoutRoot);
//        double no_TT = 0;
//        if (crawledlayoutTouchTargets != null) {
//            no_TT = crawledlayoutTouchTargets.size();
//        }
//        //New approach getting totalMissingSize from all touch targets
//        boolean calculateInitialArea = false;
//        if (TTFix.totalOriginalArea == Double.MIN_VALUE) {
//            calculateInitialArea = true;
//            TTFix.totalOriginalArea = 0;
//        }
//
////        else   if (Constants.Accessibility_SCORE_APPROACH.equalsIgnoreCase("FROM_LAYOUT")) { //FROM_LAYOUT
//        int no_Near_edge = 0;
//        for (Node<DomNode> currTouchTargets : crawledlayoutTouchTargets) {
//            String heightString = Util.getDynamicValueInDDP(currTouchTargets.getData(), "height");
//            String widthString = Util.getDynamicValueInDDP(currTouchTargets.getData(), "width");
//
//            if (heightString != null && widthString != null) {
//                double height = Util.getNumbersFromString(heightString).get(0);
//                double width = Util.getNumbersFromString(widthString).get(0);
//                double currentArea = height * width;
//
//                currentHeight += height;
//                currentWidth += width;
//                currentOverallArea += currentArea;
//                double recommendedArea = Constants.RECOMMENDED_AREA_FOR_TOUCH_TARGET;
//                boolean isNearEdge = Util.isNearScreenEdge(currTouchTargets.getData().getBounds());
//
//                if (isNearEdge) {
//                    recommendedArea = Constants.RECOMMENDED_AREA_FOR_EDGES_TOUCH_TARGET;
//                    no_Near_edge++;
//                }
//                totalRecommendedArea += recommendedArea;
//
//                if (currentArea >= recommendedArea) {
//                    totalcurrentArea += recommendedArea;  // To avoid adding large targets wich will affect calculating the score below
//                } else {
//                    numberOfIssues++;
//                    totalcurrentArea += currentArea;
//                }
//            }
//        }
//
//
//        /*** (1) Calculate no issues scores ***/
//        double noTT = no_TT;
////        double noIssuesScore = numberOfIssues / noTT;
//        // I changed it to divide by the origianl number of TT in the original layout
//        double originalTTNO = TTFix.getCurrentActivityOriginalNoOfTT();
////        double noIssuesScore = numberOfIssues / originalTTNO;
//        if (originalTTNO > no_TT) {
//            double missing_TT = originalTTNO - no_TT;
//            numberOfIssues += missing_TT;   // I am considering all the missing TT as issues > for Now
//        }
//        double noIssuesScore = numberOfIssues / originalTTNO;
//
//        double noIssuesScorePercentage = noIssuesScore * 100;
////        double no_issues=numberOfIssues;
//
//        if (Constants.Accessibility_SCORE_APPROACH.equalsIgnoreCase("FROM_ISSUES")) {
//            double remainingTT = no_TT - numberOfIssues;
//            totalcurrentArea = totalcurrentArea + ((remainingTT *
//                    Constants.RECOMMENDED_AREA_FOR_TOUCH_TARGET));  // Because we only cacluated areas fot TT with issues
//
//
//        }
//        if (calculateInitialArea) {
//            //first time calculating it so it is for the initial UI
//            TTFix.totalOriginalArea = currentOverallArea;
//            TTFix.totalOriginalHeight = currentHeight;
//            TTFix.totalOriginalWidth = currentWidth;
//            TTFix.totalRecommendedArea = totalRecommendedArea;
//        }
//
//        /*** (2) Calculate missing size score ***/
////        double recommendedArea = (mFix.getCurrentActivityOriginalNoOfTT() *
////                Constants.RECOMMENDED_AREA_FOR_TOUCH_TARGET);
//        double missingSizeScore = totalcurrentArea / TTFix.totalRecommendedArea;
////        double missingSizeScorePercentage = 100 - (missingSizeScore * 100);
//        double missingSizeScorePercentage = 10 - (missingSizeScore * 10);
//        double missingSizeScoreNumber = 1 - missingSizeScore;
//        /*** (3) Calculate overall change in area  ***/
//
//
//        double initialArea = TTFix.totalOriginalArea;
//        double changeInAreaPercentage = currentOverallArea / initialArea;
//        // if I want to use change in area
//        changeInAreaPercentage = changeInAreaPercentage * 100;
//        double changeInArea = changeInAreaPercentage - 100;  // positive is size increased , below is size decrease
//        //if I want to change in height and width
//        double changeInSize = (currentHeight - TTFix.totalOriginalHeight) + (currentWidth - TTFix.totalOriginalWidth);
//
//        /***  Calculates all the scores  ***/
//        double minor = missingSizeScorePercentage;
//        double major = noIssuesScorePercentage;
////        double score=noIssuesScorePercentage+missingSizeScoreNumber;
//        /*** (4) Return all the scores  ***/
//        return new double[]{noIssuesScorePercentage, missingSizeScorePercentage, changeInSize, 100 - noIssuesScorePercentage, numberOfIssues, no_TT};
//
//    }
//
//
//    private double[] calculateIntraSegmentAestheticScore(SegmentModel updatedSegmentModel) {
//        double[] intraSegmentViolation = new double[]{0, 0};
//        double intraSegmentViolatedScore = 0;
//        //double totalPossibleScore = 0;
//
//        for (Segment originalIntrasegmentModel : TTFix.getOriginalUISegments()) {
//            //totalPossibleScore = totalPossibleScore + originalIntrasegmentModel.getEdges().size();
//
//            Segment updatedIntrasegmentModel = originalIntrasegmentModel.getUpdatedIntraSegmentEdges();
//            System.out.println("\nProcessing for segment S" + originalIntrasegmentModel.getId());
//            //System.out.println("Original segment intra edges = " + originalIntrasegmentModel.getEdges());
//            //System.out.println("Updated segment intra edges = " + updatedIntrasegmentModel.getEdges());
//            List<IntraSegmentEdge> violatedEdges = originalIntrasegmentModel.compareToSegment(updatedIntrasegmentModel);
//            for (IntraSegmentEdge e : violatedEdges) {
//                for (EdgeLabel label : e.getLabels()) {
//                    System.out.println("Intra-segment violation for segment S" + originalIntrasegmentModel.getId() + ": " + e);
//                    intraSegmentViolatedScore = intraSegmentViolatedScore + e.getLabels().size();
//                    intraSegmentViolatedScore = intraSegmentViolatedScore + Constants.EDGE_LABEL_WEIGHTS.get(label);
//                }
//            }
//        }
//        intraSegmentViolation[0] = intraSegmentViolatedScore;
//		/*intraSegmentViolation[1] = totalPossibleScore * (Constants.EDGE_LABEL_WEIGHTS.get(EdgeLabel.INTERSECTION) +
//									Constants.EDGE_LABEL_WEIGHTS.get(EdgeLabel.CONTAINED_BY) + Constants.EDGE_LABEL_WEIGHTS.get(EdgeLabel.CONTAINS));*/
//        System.out.println("Intra-segment violation score = " + intraSegmentViolation[0]);
//        //System.out.println("Total possible intra-segment violation score = " + intraSegmentViolation[1]);
//        return intraSegmentViolation;
//    }
//
//    public void calculateFitnessScore(GAChromosome chromosome, String newLayoutFolder) {
//        String chromosomeIdentifier = chromosome.getChromosomeIdentifier();
//        if (newLayoutFolder == null) {  // We could not read the layout so no point of calculating UI. we set as worst
//            //Could not get the correct activity
//            accessibilityScore = Double.MIN_VALUE;
//            spacingScore = Double.MIN_VALUE;
//            aestheticScore = 2;
//        } else {
//
//            // (1) Calculate Accessibility Score
//            String newLayout = newLayoutFolder + TTFix.getCurrentActivityName() + ".xml";
//
//            Node<DomNode> newLayoutRoot = XMLUtils.readCrawledXML_T(newLayout);  //Just reading it to get a ne
//
//            long startTimeUsabilityScore = System.nanoTime();
//            accessibilityScore = calculateUsabilityScore(chromosomeIdentifier, newLayoutRoot);
//            System.out.println("Accessibility Score for chromosome ID: " + chromosomeIdentifier + " is: " + accessibilityScore);
//
//            //after ase
////            if(accessibilityScore>100){
////                accessibilityScore=100;
////            }
//            long endTimeUsabilityScore = System.nanoTime();
//
//            // (2) Calculate the Asethitic score
//            long startTimeAestheticScore = System.nanoTime();
//
//            aestheticScore = calculateAestheticScore(newLayoutRoot);
//            System.out.println("aestheticScore Score for chromosome ID: " + chromosomeIdentifier + " is: " + aestheticScore);
//
//          // OLD!  sizeChangeScore = calculateSizeChanging();
//
//            if (Constants.SPACE_CALCULATION_APPROACH.equalsIgnoreCase("ALL")) {
//                spacingScore = calculateGeneralSpacingScore(chromosomeIdentifier);
//            } else {
//                //calcualte space between segments only
//                spacingScore = calculateSpacingScore(chromosomeIdentifier);
//            }
//            long endTimeAestheticScore = System.nanoTime();
//        }
//        //(3) Calculate the overall fitness Score
//        double distanceUsabilityScore = Constants.EXPECTED_USABILITY_SCORE - accessibilityScore;
//        distanceUsabilityScore=Math.abs(distanceUsabilityScore);
////        double distanceUsabilityScore = usabilityScore;
//
////        double distanceUsabilityScore = usabilityScore-Constants.EXPECTED_USABILITY_SCORE  ; // Ali I changed this
//        System.out.println("Distortion with sigmoid: " + Util.sigmoid(aestheticScore));
//      //  double distanceAestheticScore = Util.sigmoid(aestheticScore) *100 - Constants.EXPECTED_AESTHETIC_SCORE;
//        double distanceAestheticScore = aestheticScore - Constants.EXPECTED_AESTHETIC_SCORE;
//
//
//        // after ase test
////        double distanceSizeChangeScore = Util.sigmoid(sizeChangeScore) *100 - Constants.EXPECTED_AESTHETIC_SCORE;
////        double distanceSizeChangeScore = Util.sigmoid(sizeChangeScore);
////        double distanceSizeChangeScore = Constants.EXPECTED_USABILITY_SCORE - sizeChangeScore;
//        double distanceSizeChangeScore = sizeChangeScore  - Constants.EXPECTED_AESTHETIC_SCORE;
//
//        double distanceSpacingScore = Constants.EXPECTED_SPACING_SCORE - spacingScore;
//
////        fitnessScore = (Constants.WEIGHT1 * Math.pow(distanceUsabilityScore, Constants.EXP1)) +
////                (Constants.WEIGHT2 * Math.pow(distanceAestheticScore, Constants.EXP2)) +
////                (Constants.WEIGHT3 * Math.pow(distanceSpacingScore, Constants.EXP2));
////
//
//        //after ase
////        fitnessScore = (Constants.WEIGHT1 * Math.pow(distanceUsabilityScore, Constants.EXP1)) +
////                (Constants.WEIGHT2 * Math.pow(distanceAestheticScore, Constants.EXP2)) +(Constants.WEIGHT5 * Math.pow(distanceSizeChangeScore, Constants.EXP2))+
////                (Constants.WEIGHT4 * Math.pow(distanceSpacingScore, Constants.EXP2));
//        fitnessScore = (Constants.WEIGHT1 * Math.pow(distanceUsabilityScore, Constants.EXP1)) +
//                (Constants.WEIGHT2 * Math.pow(distanceAestheticScore, Constants.EXP2)) +(Constants.WEIGHT5 * Math.pow(distanceSizeChangeScore, Constants.EXP2))+
//                (Constants.WEIGHT4 * Math.pow(distanceSpacingScore, Constants.EXP2));
//
//        fitnessScoreBreakdown = "Accessibility = " + accessibilityScore + ", Asethtic = " + aestheticScore +
//                ", Spacing = " + spacingScore + ", sizeChange = " + sizeChangeScore + " =>=> " +
//                Constants.WEIGHT1 + "* (" + distanceUsabilityScore + "^" + Constants.EXP1 + ") + "
//                + Constants.WEIGHT2 + "* (" + distanceAestheticScore + "^" + Constants.EXP2 + ") + "
//                + Constants.WEIGHT5 + "* (" + distanceSizeChangeScore + "^" + Constants.EXP2 + ") + "
//                + Constants.WEIGHT4 + "* (" + distanceSpacingScore + "^" + Constants.EXP2 + ")"
//                + "\nIssues Percentage: " + this.getNoIssuesPercentage() + " ||| No Issues: " + noIssuesInChromosome + "||| existingTT/originalTT: " + this.getCurrent_no_TT() + "/" + TTFix.getCurrentActivityOriginalNoOfTT();
//        System.out.println(chromosomeIdentifier);
//        System.out.println(fitnessScoreBreakdown);
////        fitnessScore = (Constants.WEIGHT1 * Math.pow(distanceUsabilityScore, Constants.EXP1)) +
////                (Constants.WEIGHT2 * Math.pow(distanceAestheticScore, Constants.EXP2)) +
////                (Constants.WEIGHT3 * Math.pow(distanceSpacingScore, Constants.EXP2));
//////                (Constants.WEIGHT3 * Math.pow(distanceSizeChangeScore, Constants.EXP2));
////        fitnessScoreBreakdown = "US = " + usabilityScore + ", AS = " + aestheticScore +
////                ", SP = " + spacingScore + " => " + ", CH = " + sizeChangeScore + " => " +
////                Constants.WEIGHT1 + "* (" + distanceUsabilityScore + "^" + Constants.EXP1 + ") + "
////                + Constants.WEIGHT2 + "* (" + distanceAestheticScore + "^" + Constants.EXP2 + ")"
////                + Constants.WEIGHT3 + "* (" + distanceSpacingScore + "^" + Constants.EXP2 + ")"
////                + Constants.WEIGHT4 + "* (" + distanceSizeChangeScore + "^" + Constants.EXP2 + ")";
//
//
//        chromosome.setFitnessFunctionObj(this);  // I don not know why this is used
//
//
////
////        String chromosomeFilePath = TTFIXUtil.applyNewValues(mFix.getFilepath(), chromosome);
////
////        AccessibilityDetectionToolAPI access = new AccessibilityDetectionToolAPI();
////        //access.runDetectionTool(chromosomeFilePath);
////        //Now we have a decompiled apk with chromosome identifier as the folder then we need to run it with  detection tool and also crawl and get the layout
//////        Util.applyNewValues(chromosome);
////        computeFitnessScore();
//        chromosome.setFitnessFunctionObj(this);
//
//        long endTime = System.nanoTime();
//
//        fitnessCalls++;
////        fitnessTimeInSec = fitnessTimeInSec
////                + Util.convertNanosecondsToSeconds((endTime - startTime));
//    }
//
//    private double calculateSpacingForSegment(TreeMap<String, List<DependentNode>> spacingNodesMap) {
//        /*** Calculate for the whole activity ***/
//        List<Node<DomNode>> tapTargets = new ArrayList<>();
//        Queue<Node<DomNode>> q = new LinkedList<Node<DomNode>>();
//        Node<DomNode> root = XMLUtils.getRoot();
//        if (root != null) {
//            // get all tap targets
//
//            Queue<Node<DomNode>> queue = new LinkedList<Node<DomNode>>();
//            queue.add(root);
//            while (!queue.isEmpty()) {
//                Node<DomNode> currNode = queue.remove();
//                if (currNode != null) {
//                    DomNode e = currNode.getData();
//                    if (Util.isElementClickable(e)) //element has attribute clickable =true
//                    {
//                        tapTargets.add(currNode);
//                    }
//                    if (currNode.getChildren() != null) {
//                        for (Node<DomNode> child : currNode.getChildren()) {
//                            queue.add(child);
//                        }
//                    }
//                }
//            }
//
//
//        }  // end of  if(lowestCommonRoot!=null)
//
//
//        // check for dependencies among the tap target elements
//
//        double noOfTouchTargets = tapTargets.size();
//        double noEdges = noOfTouchTargets * (noOfTouchTargets - 1) / 2;
//        double totalRecommendedSpacing = noEdges * Constants.RECOMMENDED_TOUCH_TARGET_SPACING;
//        for (int i = 0; i < tapTargets.size(); i++) {
//            Node<DomNode> i_touch_target = tapTargets.get(i);
//            DomNode e = i_touch_target.getData();
//            List<DependentNode> dependentNodes = new ArrayList<>();
//
//            // check if any other tap target is within the defined radius
//
//            for (int j = 0; j < tapTargets.size(); j++) {
//                Node<DomNode> j_touch_target = tapTargets.get(j);
//                DomNode n = j_touch_target.getData();
//
//                double[] result = Util.isElementCloseToAnotherWithDistance(i_touch_target, j_touch_target, Constants.TAP_TARGETS_RADIUS);
//                double minDistance = result[1];  // I do not use it now but maybe later
//                boolean isClose = result[0] == 1;
//                if (!e.getxPath().equalsIgnoreCase(n.getxPath()) && isClose) {
//
////                if (!e.getxPath().equalsIgnoreCase(n.getxPath()) && Util.isElementCloseToAnother(e, n, Constants.TAP_TARGETS_RADIUS)) {
//                    System.out.println("These two views are close: \n" + e.getxPath() + "\n" + n.getxPath() + "" +
//                            "\n ****************************************");
//                    // check the position of the neighboring tap target and apply margin in that direction
//                    Rectangle eRect = e.getCoord();
//                    Rectangle nRect = n.getCoord();
//                    // n is above e: e.y1 > n.y2
////                    double shortestDistance= Double.MAX_VALUE;
//                    if (eRect.y >= (nRect.y + nRect.height)) {
//                        double distance = eRect.y - (nRect.y + nRect.height);
//                        System.out.println("margin-bottom Dist: " + distance);
//                        dependentNodes.add(new DependentNode(n.getxPath(), Constants.MARGIN_BOTTOM, distance, 1.0, 1.0));
//                    }
//                    // n is below e: e.y1 < n.y2
//                    if ((eRect.y + eRect.height) <= nRect.y) {
//                        double distance = (eRect.y + eRect.height) - nRect.y;
//                        System.out.println("margin-top Dist: " + distance);
//                        dependentNodes.add(new DependentNode(n.getxPath(), Constants.MARGIN_TOP, distance, 1.0, 1.0));
//                    }
//                    // n is to the left of e: e.x1 > n.x2
//                    if (eRect.x >= (nRect.x + nRect.width)) {
//                        double distance = eRect.x - (nRect.x + nRect.width);
//                        System.out.println("margin-right Dist: " + distance);
//                        dependentNodes.add(new DependentNode(n.getxPath(), Constants.MARGIN_RIGHT, distance, 1.0, 1.0));
//                    }
//                    // n is to the right of e: e.x2 < n.x1
//                    if ((eRect.x + eRect.width) <= nRect.x) {
//                        double distance = (eRect.x + eRect.width) - nRect.x;
//                        System.out.println("margin-left Dist: " + distance);
//                        dependentNodes.add(new DependentNode(n.getxPath(), Constants.MARGIN_LEFT, distance, 1.0, 1.0));
//                    }
//
//                }
//                if (dependentNodes.size() > 0) {
//                    spacingNodesMap.put(e.getxPath(), dependentNodes);
//                }
//            }
//        }
//        return totalRecommendedSpacing;
//    }
//
//    private double calculateSpacingForSegment(Segment seg, TreeMap<String, List<DependentNode>> spacingNodesMap) {
//        List<Node<DomNode>> tapTargets = new ArrayList<>();
//        Queue<Node<DomNode>> q = new LinkedList<Node<DomNode>>();
//        Node<DomNode> lowestCommonRoot = XMLUtils.searchVHTreeByXpath(seg.getLowestCommonAncestor(), XMLUtils.getRoot());
//        if (lowestCommonRoot != null) {
//            // get all tap targets
//
//            for (String memberXpath : seg.getMembers()
//            ) {   //Instead of mFix since different segmenations can be sed so not necessary all directly under same parent
//                Node<DomNode> currNode = XMLUtils.searchVHTreeByXpath(memberXpath, lowestCommonRoot);
//                if (currNode != null) {
//                    DomNode e = currNode.getData();
//                    if (Util.isElementClickable(e)) //element has attribute clickable =true
//                    {
//                        tapTargets.add(currNode);
//                    }
//                }
//            }
//        }  // end of  if(lowestCommonRoot!=null)
//
//
//        // check for dependencies among the tap target elements
//        double noOfTouchTargets = tapTargets.size();
//        double noEdges = noOfTouchTargets * (noOfTouchTargets - 1) / 2;
//        double totalRecommendedSpacing = noEdges * Constants.RECOMMENDED_TOUCH_TARGET_SPACING;
//        for (int i = 0; i < tapTargets.size(); i++) {
//            Node<DomNode> i_touch_target = tapTargets.get(i);
//            DomNode e = i_touch_target.getData();
//            List<DependentNode> dependentNodes = new ArrayList<>();
//
//            // check if any other tap target is within the defined radius
//
//            for (int j = 0; j < tapTargets.size(); j++) {
//                Node<DomNode> j_touch_target = tapTargets.get(j);
//                DomNode n = j_touch_target.getData();
//                boolean isClose = false;
//                double[] result = Util.isElementCloseToAnotherWithDistance(i_touch_target, j_touch_target, Constants.TAP_TARGETS_RADIUS);
//                double minDistance = result[1];
//                if (result[0] == 1) {
//                    isClose = true;
//                }
//                if (!e.getxPath().equalsIgnoreCase(n.getxPath()) && isClose) {
//                    System.out.println("These two views are close: \n" + e.getxPath() + "\n" + n.getxPath() + "" +
//                            "\n ****************************************");
//                    // check the position of the neighboring tap target and apply margin in that direction
//                    Rectangle eRect = e.getCoord();
//                    Rectangle nRect = n.getCoord();
//                    // n is above e: e.y1 > n.y2
////                    double shortestDistance= Double.MAX_VALUE;
//                    if (eRect.y >= (nRect.y + nRect.height)) {
//                        double distance = eRect.y - (nRect.y + nRect.height);
//                        System.out.println("margin-bottom Dist: " + distance);
//                        dependentNodes.add(new DependentNode(n.getxPath(), Constants.MARGIN_BOTTOM, distance, 1.0, 1.0));
//                    }
//                    // n is below e: e.y1 < n.y2
//                    if ((eRect.y + eRect.height) <= nRect.y) {
//                        double distance = (eRect.y + eRect.height) - nRect.y;
//                        System.out.println("margin-top Dist: " + distance);
//                        dependentNodes.add(new DependentNode(n.getxPath(), Constants.MARGIN_TOP, distance, 1.0, 1.0));
//                    }
//                    // n is to the left of e: e.x1 > n.x2
//                    if (eRect.x >= (nRect.x + nRect.width)) {
//                        double distance = eRect.x - (nRect.x + nRect.width);
//                        System.out.println("margin-right Dist: " + distance);
//                        dependentNodes.add(new DependentNode(n.getxPath(), Constants.MARGIN_RIGHT, distance, 1.0, 1.0));
//                    }
//                    // n is to the right of e: e.x2 < n.x1
//                    if ((eRect.x + eRect.width) <= nRect.x) {
//                        double distance = (eRect.x + eRect.width) - nRect.x;
//                        System.out.println("margin-left Dist: " + distance);
//                        dependentNodes.add(new DependentNode(n.getxPath(), Constants.MARGIN_LEFT, distance, 1.0, 1.0));
//                    }
//
//                }
//                if (dependentNodes.size() > 0) {
//                    spacingNodesMap.put(e.getxPath(), dependentNodes);
//                }
//            }
//        }
//        return totalRecommendedSpacing;
//    }
//
//    private double calculateGeneralSpacingScore(String chromosomeIdentifier) {
//        /*** Calculate the score across all touch targets, regardless of Segment */
//        TreeMap<String, List<DependentNode>> spacingNodesMap = new TreeMap<String, List<DependentNode>>();
//        TreeMap<String, Double> spacingDistanceMap = new TreeMap<String, Double>();
//        /*** (1) get all elements that are close to each other ***/
//        double totalRecommendedSpacing = 0;
//        totalRecommendedSpacing += calculateSpacingForSegment(spacingNodesMap);
//
//
//        /*** (2) calculate the number of unique edges as the score ***/
//        double totalDynamicSpacing = 0.0;
//        double totalSpacing = 0.0;
//        List<String> uniqueEdges = new ArrayList<>();  // regardless of order
//        for (String xpath : spacingNodesMap.keySet()
//        ) {
//            List<DependentNode> dep = spacingNodesMap.get(xpath);
//            for (DependentNode depNode : dep) {
//                String depXpath = depNode.getXpath();
//                // check if the edge exists regardless of order
//                if (!spacingDistanceMap.containsKey(xpath + "|" + depXpath) && !spacingDistanceMap.containsKey(depXpath + "|" + xpath)) {
//                    double currDistance = depNode.getRatio();
//                    if (currDistance < 0) {  // if it is <0 then we will skip and wait for the positive distance on the reverse side edge
//                        continue;
//                    }
//
//                    spacingDistanceMap.put(xpath + "|" + depXpath, depNode.getRatio());
//                    // I am currently considering using ratio fiedl as current spacing
//                    totalDynamicSpacing += currDistance;
//                }
//            }
//
//        }
//        double totalCurrentSpacing = totalDynamicSpacing / Constants.PHONE_DENSITY; // we divide the phone density because we calculated the spacing in dynamic
//        int numberOfViolatedEdges = spacingDistanceMap.size();
//        if (numberOfViolatedEdges == 0) {
//            spacingScore = 100;
//        } else {
////            double totalRecommendedSpacing = numberOfViolatedEdges * Constants.RECOMMENDED_TOUCH_TARGET_SPACING; // number of touch targets edges * recommended size for each edge(e 9)
//            double missingSpacing = totalRecommendedSpacing - totalCurrentSpacing; // supposed spacing for the edges - the current spacing
//            if (totalCurrentSpacing > totalRecommendedSpacing) {  // jsut to make sure  perecentage does not go beyond 100 Todo:double check
//
//                totalCurrentSpacing = totalRecommendedSpacing;
//            }
//            double score = 0;
//            if (totalRecommendedSpacing > 0) {
//                score = (totalCurrentSpacing / totalRecommendedSpacing);
//            }
////            spacingScore = 100 - (score * 100);
//            spacingScore = 100 - (score * 100); // after running two emulators
//        }
//        /** I am calculating both score as number of violated edges and missing spaces but now  I am going to use missing spacing ***/
//        return spacingScore;
//
//
//    }
//
//    private double calculateSpacingScore(String chromosomeIdentifier) {
//        TreeMap<String, List<DependentNode>> spacingNodesMap = new TreeMap<String, List<DependentNode>>();
//        TreeMap<String, Double> spacingDistanceMap = new TreeMap<String, Double>();
//        /*** (1) get all elements that are close to each other ***/
//        double totalRecommendedSpacing = 0;
//        for (Segment seg : TTFix.getOriginalUISegments()
//        ) {
//            int segmentId = seg.getId();
//            totalRecommendedSpacing += calculateSpacingForSegment(seg, spacingNodesMap);
//        }
//
//        /*** (2) calculate the number of unique edges as the score ***/
//        double totalDynamicSpacing = 0.0;
//        double totalSpacing = 0.0;
//        List<String> uniqueEdges = new ArrayList<>();  // regardless of order
//        for (String xpath : spacingNodesMap.keySet()
//        ) {
//            List<DependentNode> dep = spacingNodesMap.get(xpath);
//            for (DependentNode depNode : dep) {
//                String depXpath = depNode.getXpath();
//                // check if the edge exists regardless of order
//                if (!spacingDistanceMap.containsKey(xpath + "|" + depXpath) && !spacingDistanceMap.containsKey(depXpath + "|" + xpath)) {
//                    double currDistance = depNode.getRatio();
//                    if (currDistance < 0) {  // if it is <0 then we will skip and wait for the positive distance on the reverse side edge
//                        continue;
//                    }
//
//                    spacingDistanceMap.put(xpath + "|" + depXpath, depNode.getRatio());
//                    // I am currently considering using ratio fiedl as current spacing
//                    totalDynamicSpacing += currDistance;
//                }
//            }
//
//        }
//        double totalCurrentSpacing = totalDynamicSpacing / Constants.PHONE_DENSITY; // we divide the phone density because we calculated the spacing in dynamic
//        int numberOfViolatedEdges = spacingDistanceMap.size();
//        if (numberOfViolatedEdges == 0) {
//            spacingScore = 100;
//        } else {
////            double totalRecommendedSpacing = numberOfViolatedEdges * Constants.RECOMMENDED_TOUCH_TARGET_SPACING; // number of touch targets edges * recommended size for each edge(e 9)
//            double missingSpacing = totalRecommendedSpacing - totalCurrentSpacing; // supposed spacing for the edges - the current spacing
//            if (totalCurrentSpacing > totalRecommendedSpacing) {  // jsut to make sure  perecentage does not go beyond 100 Todo:double check
//
//                totalCurrentSpacing = totalRecommendedSpacing;
//            }
//            double score = 0;
//            if (totalRecommendedSpacing > 0) {
//                score = (totalCurrentSpacing / totalRecommendedSpacing);
//            }
////            spacingScore = 100 - (score * 100);
//            spacingScore = 100 - (score * 100); // after running two emulators
//        }
//        /** I am calculating both score as number of violated edges and missing spaces but now  I am going to use missing spacing ***/
//        return spacingScore;
//    }
//
//    public double calculateAestheticScore(Node<DomNode> newLayoutRoot) {
//        double layountInconsistencyScore = 0;
//        double changeAmount = 0;
//        if (Constants.DISTORTION_APPROACH.equalsIgnoreCase("TTfix")) {
//            // get updated segment model and violated edges
//            Util.updateLayoutWithNewValues(newLayoutRoot); // copied ti from Util class
//            Node<DomNode> root = XMLUtils.getRoot();
//            SegmentModel updatedSegmentModel = TTFix.getOriginalUISegmentModel().getUpdatedSegmentModel();
//            SegmentModel original = TTFix.getOriginalUISegmentModel();
//            // get intra segment violations
//            double[] intraSegmentViolation = calculateIntraSegmentAestheticScore(updatedSegmentModel);
//
//            // calculate inter segment violations
//            List<InterSegmentEdge> interSegmentViolatedEdges = TTFix.getOriginalUISegmentModel().compareToSegmentModel(updatedSegmentModel);
//            double interSegmentViolatedScore = 0.0;
//            String interSegmentViolatedScoreBreakdown = "(";
//            for (InterSegmentEdge e : interSegmentViolatedEdges) {
//                System.out.println("Inter-segment violation: " + e);
//                for (EdgeLabel label : e.getLabels()) {
//                    interSegmentViolatedScore = interSegmentViolatedScore + Constants.EDGE_LABEL_WEIGHTS.get(label);
//                    interSegmentViolatedScoreBreakdown = interSegmentViolatedScoreBreakdown + Constants.EDGE_LABEL_WEIGHTS.get(label) + " + ";
//                }
//            }
//            if (interSegmentViolatedScoreBreakdown.length() > 3) {
//                interSegmentViolatedScoreBreakdown = interSegmentViolatedScoreBreakdown.substring(0, interSegmentViolatedScoreBreakdown.length() - 3) + ")";
//            }
//
//            // calculate the possible total violated score = no. of edges * (sum of label weights)
//            double sumOfLabelWeights = 0.0;
//            for (double w : Constants.EDGE_LABEL_WEIGHTS.values()) {
//                sumOfLabelWeights = sumOfLabelWeights + w;
//            }
//            double possibleTotalViolatedScore = TTFix.getOriginalUISegmentModel().getEdges().size() * sumOfLabelWeights;
//
//            // calculate normalized aesthetic score in the range [0, 100]
//            //aestheticScore = ((interSegmentViolatedScore + intraSegmentViolation[0]) / (possibleTotalViolatedScore + intraSegmentViolation[1])) * 100.0;
//            layountInconsistencyScore = interSegmentViolatedScore + intraSegmentViolation[0];
//
//            aestheticScoreBreakdown = interSegmentViolatedEdges.toString();
//            aestheticScoreBreakdown = aestheticScoreBreakdown + " => " + interSegmentViolatedScoreBreakdown + " / "
//                    + "(" + TTFix.getOriginalUISegmentModel().getEdges().size() + "*" + sumOfLabelWeights + ")";
//        }
//        else if (Constants.DISTORTION_APPROACH.equalsIgnoreCase("ifix")) {
//
//            LayoutGraphBuilder lgb;
//            Node<DomNode> originalRoot = XMLUtils.getRoot();
//            Node<DomNode> repairedRoot = newLayoutRoot;
//
//            lgb = new LayoutGraphBuilder(originalRoot, repairedRoot);
//            ArrayList<Issue> potentialLayoutIssues = lgb.compareLayoutGraphs();
//            int potentialscore = computeAmountOfInconsistancy(potentialLayoutIssues);
//            LayoutIssuesFilterProcessor filters = new LayoutIssuesFilterProcessor();
//
//            filters.addFilter(new ContainmentIssueFilter());
//            filters.addFilter(new DirectionIssueFilter());
////        filters.addFilter(new OptionElementFilter());
//            filters.addFilter(new OnePixelIssueFilter());
//            filters.addFilter(new CenteredIssueFilter());
//            filters.addFilter(new AlignmentIssueFilter());
////        LayoutIssuesFilterProcessor filter = new LayoutIssuesFilterProcessor();
//            ArrayList<Issue> filteredLayoutIssues = filters.filterissues(potentialLayoutIssues);
//            //filter issues
//
//            /*** (1) Calculate the inconsistency score ***/
//            int filteredscore = computeAmountOfInconsistancy(filteredLayoutIssues);
//
//            System.out.println("PotentialSize: " + potentialLayoutIssues.size() + " || " +
//                    "FilteredDize: " + filteredLayoutIssues.size());
//            System.out.println("PotentialScore: " + potentialscore + " || " +
//                    "FilteredScore: " + filteredscore);
//            layountInconsistencyScore = filteredscore;
//
//            /*** (2) Calculate the size change score ***/
//
////            changeAmount = amountOfChangeInSize * Constants.PHONE_DENSITY; // calculated that as part of accessibility method because it is faster to ue it there
//            changeAmount = amountOfChangeInSize; // calculated that as part of accessibility method because it is faster to ue it there
//
//            //if it is positive that means the total change is positive( size of touch targets has increased from  the original layout size)
//            // we penalize the negative ( decrease in size ) more than the increase
//            if (changeAmount < 0) {
//                // total size of touch targets decreased
//                changeAmount = changeAmount * -1.5;  // make it positive and multiply by 1.5
//
//            }
//            sizeChangeScore= changeAmount;
//
//
//        } else {
//            System.out.println("Not Supported asethtic score approach ");
//            System.exit(1);
//        }
//
//        /*** (3) calculate ***/
//   //     aestheticScore = layountInconsistencyScore * 0.7 + changeAmount * 0.3;
//        //after ase test
//        aestheticScore=layountInconsistencyScore;
//        return aestheticScore;
////        return new double[]{layountInconsistencyScore, changeAmount};
//    }
//
//    public static int computeAmountOfInconsistancy(ArrayList<Issue> filteredLayoutIssues) {
//
//        int amount = 0;
//
//        for (Issue issue : filteredLayoutIssues) {
//            amount += issue.getIssueAmount();
//        }
//
//        return amount;
//    }
//
//    public double calculateAestheticScore_mfix() {
//        double aestheticScore = 0;
//
//        // get updated segment model and violated edges
//        Node<DomNode> root = XMLUtils.getRoot();
//        SegmentModel updatedSegmentModel = TTFix.getOriginalUISegmentModel().getUpdatedSegmentModel();
//        SegmentModel original = TTFix.getOriginalUISegmentModel();
//        // get intra segment violations
//        double[] intraSegmentViolation = calculateIntraSegmentAestheticScore(updatedSegmentModel);
//
//        // calculate inter segment violations
//        List<InterSegmentEdge> interSegmentViolatedEdges = TTFix.getOriginalUISegmentModel().compareToSegmentModel(updatedSegmentModel);
//        double interSegmentViolatedScore = 0.0;
//        String interSegmentViolatedScoreBreakdown = "(";
//        for (InterSegmentEdge e : interSegmentViolatedEdges) {
//            System.out.println("Inter-segment violation: " + e);
//            for (EdgeLabel label : e.getLabels()) {
//                interSegmentViolatedScore = interSegmentViolatedScore + Constants.EDGE_LABEL_WEIGHTS.get(label);
//                interSegmentViolatedScoreBreakdown = interSegmentViolatedScoreBreakdown + Constants.EDGE_LABEL_WEIGHTS.get(label) + " + ";
//            }
//        }
//        if (interSegmentViolatedScoreBreakdown.length() > 3) {
//            interSegmentViolatedScoreBreakdown = interSegmentViolatedScoreBreakdown.substring(0, interSegmentViolatedScoreBreakdown.length() - 3) + ")";
//        }
//
//        // calculate the possible total violated score = no. of edges * (sum of label weights)
//        double sumOfLabelWeights = 0.0;
//        for (double w : Constants.EDGE_LABEL_WEIGHTS.values()) {
//            sumOfLabelWeights = sumOfLabelWeights + w;
//        }
//        double possibleTotalViolatedScore = TTFix.getOriginalUISegmentModel().getEdges().size() * sumOfLabelWeights;
//
//        // calculate normalized aesthetic score in the range [0, 100]
//        //aestheticScore = ((interSegmentViolatedScore + intraSegmentViolation[0]) / (possibleTotalViolatedScore + intraSegmentViolation[1])) * 100.0;
//        aestheticScore = interSegmentViolatedScore + intraSegmentViolation[0];
//
//        aestheticScoreBreakdown = interSegmentViolatedEdges.toString();
//        aestheticScoreBreakdown = aestheticScoreBreakdown + " => " + interSegmentViolatedScoreBreakdown + " / "
//                + "(" + TTFix.getOriginalUISegmentModel().getEdges().size() + "*" + sumOfLabelWeights + ")";
//
//        return aestheticScore;
//    }
////
//// Commented when implementing search based
////    private void computeFitnessScore() {
////
////        long startTimeUsabilityScore = System.nanoTime();
////        usabilityScore = calculateUsabilityScore();
////        long endTimeUsabilityScore = System.nanoTime();
////
////        long startTimeAestheticScore = System.nanoTime();
////        aestheticScore = calculateAestheticScore();
////        long endTimeAestheticScore = System.nanoTime();
////
////        double distanceUsabilityScore = Constants.EXPECTED_USABILITY_SCORE - usabilityScore;
////        double distanceAestheticScore = aestheticScore - Constants.EXPECTED_AESTHETIC_SCORE;
////
////        fitnessScore = (Constants.WEIGHT1 * Math.pow(distanceUsabilityScore, Constants.EXP1)) + (Constants.WEIGHT2 * Math.pow(distanceAestheticScore, Constants.EXP2));
////        fitnessScoreBreakdown = "US = " + usabilityScore + ", AS = " + aestheticScore + " => "
////                + Constants.WEIGHT1 + "* (" + distanceUsabilityScore + "^" + Constants.EXP1 + ") + "
////                + Constants.WEIGHT2 + "* (" + distanceAestheticScore + "^" + Constants.EXP2 + ")";
////
////		/*double usabilityScore = this.usabilityScore;
////		if(usabilityScore >= Constants.USABILITY_SCORE_THRESHOLD)
////		{
////			usabilityScore = Constants.USABILITY_SCORE_THRESHOLD;
////		}
////
////		// calculate fitness score as a weighted sum of the two objectives
////		// invert the aesthetic score to minimize the violations but achieve a maximizing objective for the fitness score
////		if(usabilityScore < Constants.USABILITY_SCORE_THRESHOLD)
////		{
////			fitnessScore = (Constants.WEIGHT1 * usabilityScore) + (Constants.WEIGHT2 * (1 / aestheticScore));
////			fitnessScoreBreakdown =  "(" + Constants.WEIGHT1 + "*" + usabilityScore + ")" + " + (" + Constants.WEIGHT2 + "*" + "1 /" + aestheticScore + ")";
////		}
////		else
////		{
////			fitnessScore = (Constants.WEIGHT1 * usabilityScore) + (Constants.WEIGHT1 * (1 / aestheticScore));
////			fitnessScoreBreakdown =  "(" + Constants.WEIGHT1 + "*" + usabilityScore + ")" + " + (" + Constants.WEIGHT1 + "*" + "1 /" + aestheticScore + ")";
////		}*/
////
////        System.out.println("Fitness score = " + this);
////        System.out.println("Usability score time = " + Util.convertNanosecondsToSeconds(endTimeUsabilityScore - startTimeUsabilityScore) + " sec");
////        System.out.println("Aesthetic score time = " + Util.convertNanosecondsToSeconds(endTimeAestheticScore - startTimeAestheticScore) + " sec");
////    }
//
//    @Override
//    public String toString() {
//        String ret = fitnessScore + " => " + fitnessScoreBreakdown;
//        return ret;
//    }
//}
