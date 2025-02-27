package usc.edu.SALEM.fitness;

import java.util.*;

import gatech.xpert.dom.DomNode;
import usc.edu.SALEM.Constants;
//import usc.edu.SALEM.VHTree.Node;
import usc.edu.SALEM.SALEM;
import usc.edu.SALEM.VHTree.XMLUtils;
import usc.edu.SALEM.segmentation.Segment;
import usc.edu.SALEM.util.Util;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
//
public class AccessibilityScannerResults {

//    private UsabilityScoreAPIParser accessibilityScannerObj;
    //
//    private static double usabilityScoreAPITotalTimeInSec;
    //    private static int mobileFriendlyAPICalls;
//    private static int usabilityScoreAPICalls;
    //
    private final double heightIssueScore = 0.0;
    private int heightIssueCount = 0;
    private final double widthIssueScore = 0.0;
    private int widthIssueCount = 0;
    private final double bothIssueScore = 0.0;
    private int bothIssueCount = 0;
    private Set<String> initialProblematicActivities;
    private HashMap<String, Set<TIssue>> ListOfIssues; //List of Issues Parsed from the detection tool output
    private final HashMap<String, HashMap<String, Integer>> issuesForActivity; // Map acitivty with total count of each issue so we use it when we count issue for each issue

    public AccessibilityScannerResults() {
        this.issuesForActivity = new HashMap<>();
    }

    public HashMap<String, Set<TIssue>> getListOfIssues() {
        return ListOfIssues;
    }


    public void setListOfIssues(HashMap<String, Set<TIssue>> listOfIssues, boolean isInitial) {
        this.ListOfIssues = listOfIssues;

        if (isInitial) {  // Only set problematic activities if this is the initialization phase because this is not going to change
            Set<String> initialProblematicActivities = new HashSet<>();
            for (String activity : listOfIssues.keySet()
            ) {
                initialProblematicActivities.add(activity.trim());

            }
            setInitialProblematicActivities(initialProblematicActivities);


        }

    }

    public Set<String> getInitialProblematicActivities() {
        return initialProblematicActivities;
    }

    private void setInitialProblematicActivities(Set<String> initialProblematicActivities) {
        this.initialProblematicActivities = initialProblematicActivities;
    }

    public int getHeightIssueCount() {
        return heightIssueCount;
    }

    public void setHeightIssueCount(int heightIssueCount) {
        this.heightIssueCount = heightIssueCount;
    }

    public int getWidthIssueCount() {
        return widthIssueCount;
    }

    public void setWidthIssueCount(int widthIssueCount) {
        this.widthIssueCount = widthIssueCount;
    }

    public double[] calculateAccessibilityScore(Node<DomNode> newLayoutRoot) {

        //Read the list of size of Issues for the current activity then claculate the score
        /* we are calling this method from the object so we can directly use the parameter */

        String curr = SALEM.getCurrentActivityName();
        double total = 0.0;
        double totalMissingSize = 0.0;
        double totalcurrentArea = 0.0;
        double currentOverallArea = 0.0;// used to calculate overall change in area
        int numberOfIssues = 0;
        double sizeChangeInPixel = 0.0;
        double currentWidth = 0.0;
        double currentHeight = 0.0;
        double originalWidth = 0.0;
        double originalHeight = 0.0;
        List<Node<DomNode>> crawledlayoutTouchTargets = Util.getListOfTouchTargets(newLayoutRoot);
        int no_TT = 0;
        if (crawledlayoutTouchTargets != null) {
            no_TT = crawledlayoutTouchTargets.size();
        }
        //New approach getting totalMissingSize from all touch targets
        boolean calculateInitialArea = false;
        if (SALEM.totalOriginalArea == Double.MIN_VALUE) {
            calculateInitialArea = true;
            SALEM.totalOriginalArea = 0;
        }

//        else   if (Constants.Accessibility_SCORE_APPROACH.equalsIgnoreCase("FROM_LAYOUT")) { //FROM_LAYOUT
        for (Node<DomNode> currTouchTargets : crawledlayoutTouchTargets) {
            String heightString = Util.getDynamicValueInDDP(currTouchTargets.getData(), "height");
            String widthString = Util.getDynamicValueInDDP(currTouchTargets.getData(), "width");
            if (heightString != null && widthString != null) {
                double height = Util.getNumbersFromString(heightString).get(0);
                double width = Util.getNumbersFromString(widthString).get(0);
                double currentArea = height * width;

                currentHeight += height;
                currentWidth += width;
                currentOverallArea += currentArea;
                if (currentArea >= Constants.RECOMMENDED_AREA_FOR_TOUCH_TARGET) {
                    totalcurrentArea += Constants.RECOMMENDED_AREA_FOR_TOUCH_TARGET;  // To avoid adding large targets wich will affect calculating the score below
                } else {
                    numberOfIssues++;
                    totalcurrentArea += currentArea;
                }
            }
        }


        if (Constants.Accessibility_SCORE_APPROACH.equalsIgnoreCase("FROM_ISSUES")) { //FROM_LAYOUT
            Set<TIssue> issues = getListOfIssues().get(SALEM.getCurrentActivityName()); // Get the list of issues for the current activity
            String type;
            double score;
            numberOfIssues = 0;
            totalcurrentArea = 0.0;
            if (issues != null) {
                for (TIssue issue : issues
                ) {

                    // Height
                    // Width
                    // Both = 2 because it is actually 2 issues : height & width
                    String xpath = issue.getWidgetXpath();
                    Node<DomNode> currNode = XMLUtils.searchByID_T(issue.getWidgetID(), issue.getClassName());
                    if (currNode != null) {
                        String heightString = Util.getDynamicValueInDDP(currNode.getData(), "height");
                        String widthString = Util.getDynamicValueInDDP(currNode.getData(), "width");
                        if (heightString != null && widthString != null) {
                            double height = Util.getNumbersFromString(heightString).get(0);
                            double width = Util.getNumbersFromString(widthString).get(0);
                            double currentArea = height * width;
                            if (currentArea >= Constants.RECOMMENDED_AREA_FOR_TOUCH_TARGET) {
                                totalcurrentArea += Constants.RECOMMENDED_AREA_FOR_TOUCH_TARGET;  // To avoid adding large targets wich will affect calculating the score below
                            } else {
                                totalcurrentArea += currentArea;
                            }

                        }

                        numberOfIssues++;

                    }

                }
            }


        }

        /*** (1) Calculate no issues scores ***/
        double noTT = no_TT;
        double noIssuesScore = numberOfIssues / noTT;
        noIssuesScore = noIssuesScore * 100;


        if (Constants.Accessibility_SCORE_APPROACH.equalsIgnoreCase("FROM_ISSUES")) {
            int remainingTT = no_TT - numberOfIssues;
            totalcurrentArea = totalcurrentArea + ((remainingTT *
                    Constants.RECOMMENDED_AREA_FOR_TOUCH_TARGET));  // Because we only cacluated areas fot TT with issues


        }
        /*** (2) Calculate missing size score ***/
        double recommendedArea = (SALEM.getCurrentActivityOriginalNoOfTT() *
                Constants.RECOMMENDED_AREA_FOR_TOUCH_TARGET);
        double missingSizeScore = totalcurrentArea / recommendedArea;
        missingSizeScore = 100 - (missingSizeScore * 100);

        /*** (3) Calculate overall change in area  ***/
        if (calculateInitialArea) {
            //first time calculating it so it is for the initial UI
            SALEM.totalOriginalArea = currentOverallArea;
            SALEM.totalOriginalHeight = currentHeight;
            SALEM.totalOriginalWidth = currentWidth;
        }

        double initialArea = SALEM.totalOriginalArea;
        double changeInAreaPercentage = currentOverallArea / initialArea;
        // if I want to use change in area
        changeInAreaPercentage = changeInAreaPercentage * 100;
        double changeInArea = changeInAreaPercentage - 100;  // positive is size increased , below is size decrease
        //if I want to change in height and width
        double changeInSize = (currentHeight - SALEM.totalOriginalHeight) + (currentWidth - SALEM.totalOriginalWidth);

        /*** (4) Return all the scores  ***/
        return new double[]{noIssuesScore, missingSizeScore, changeInSize};

    }

    public double calculateAccessibilityScore0(Node<DomNode> newLayoutRoot) {

        //Read the list of size of Issues for the current activity then claculate the score
        /* we are calling this method from the object so we can directly use the parameter */
        String curr = SALEM.getCurrentActivityName();
        double total = 0.0;
        double totalMissingSize = 0.0;
        double totalcurrentArea = 0.0;
        int no_TT = 0;
        try {
            Set<TIssue> issues = getListOfIssues().get(SALEM.getCurrentActivityName()); // Get the list of issues for the current activity
            String type;
            double score;
            if (Constants.Accessibility_SCORE_APPROACH.equalsIgnoreCase("numberOfIssues")) {
                for (TIssue issue : issues
                ) {

                    // Height
                    // Width
                    // Both = 2 because it is actually 2 issues : height & width
                    type = issue.getIssueType();
                    score = Constants.PROBLEM_IMPACT.get(type);
                    total += score;
                    //Calculate the total number of issues
                    switch (type) {
                        case Constants.TOUCH_TARGET_HEIGHT_ISSUE:
                            this.heightIssueCount++;
                            break;

                        case Constants.TOUCH_TARGET_WIDTH_ISSUE:
                            this.widthIssueCount++;
                            break;


                        case Constants.TOUCH_TARGET_BOTH_ISSUE:
                            this.bothIssueCount++;
                            break;

                    }


                }
            } else if (Constants.Accessibility_SCORE_APPROACH.equalsIgnoreCase("missingSize")) {
                List<Node<DomNode>> crawledlayoutTouchTargets = Util.getListOfTouchTargets(newLayoutRoot);
                if (crawledlayoutTouchTargets != null) {
                    no_TT = crawledlayoutTouchTargets.size();
                }
                //New approach getting totalMissingSize from all touch targets
                for (Node<DomNode> currTouchTargets : crawledlayoutTouchTargets) {
                    String heightString = Util.getDynamicValueInDDP(currTouchTargets.getData(), "height");
                    String widthString = Util.getDynamicValueInDDP(currTouchTargets.getData(), "width");
                    if (heightString != null && widthString != null) {
                        double height = Util.getNumbersFromString(heightString).get(0);
                        double width = Util.getNumbersFromString(widthString).get(0);
                        double currentArea = height * width;
                        if (currentArea > Constants.RECOMMENDED_AREA_FOR_TOUCH_TARGET) {
                            totalcurrentArea += Constants.RECOMMENDED_AREA_FOR_TOUCH_TARGET;  // To avoid adding large targets wich will affect calculating the score below
                        } else {
                            totalcurrentArea += currentArea;
                        }
                    }
                }


// getTouch target from issues
                //                for (TIssue issue : issues
//                ) {
//                    String id = issue.getWidgetID();
//                    String issueClass = issue.getClassName();
//                    Node<DomNode> dd = XMLUtils.searchByID_T(id, issueClass);
//                    if (dd != null) {
//                        String heightString = Util.getDynamicValueInDDP(dd.getData(), "height");
//                        String widthString = Util.getDynamicValueInDDP(dd.getData(), "width");
//                        if(heightString!=null && widthString!=null) {
//                            double height= Util.getNumbersFromString(heightString).get(0);
//                            double width=  Util.getNumbersFromString(widthString).get(0);
//                            double currentArea =height * width;
//
//                            totalMissingSize+=(totalRecommendedArea-currentArea);
//                        }
//                    }
//                }
            }

        } catch (Exception e) {
            System.out.println("Exception while cacluating accessibily issue score |" + e.getMessage());

        } finally {
            // number of TT issues /total number of clickable elements *2
            if (Constants.Accessibility_SCORE_APPROACH.equalsIgnoreCase("numberOfIssues")) {
                double noTT = no_TT;
                double accSizeScore = total / no_TT;
                return accSizeScore * 100;
            } else if (Constants.Accessibility_SCORE_APPROACH.equalsIgnoreCase("missingSize")) {
                double recommendedArea = (SALEM.getCurrentActivityOriginalNoOfTT() * Constants.RECOMMENDED_AREA_FOR_TOUCH_TARGET);
                double accSizeScore = totalcurrentArea / recommendedArea;
                return accSizeScore * 100;
            }
            return 0;

        }
    }

    public double getRuleImpactScore(String problemType) {
        double impact = Constants.PROBLEM_IMPACT.get(problemType); //The impact of each issue
        return impact;
    }
//
//    public boolean runDetectionTool(String toolOutputFileName) {
//        boolean ranSuccess = false;
//        //Run the tool and get the output file
//
//
//        //Parse the output file and get the issues of the app
//
//        //Update the list of issues so you can calculate the score
//
//        return ranSuccess;
//    }

    //    public static double getMobileFriendlyAPITotalTimeInSec()
//    {
//        return mobileFriendlyAPITotalTimeInSec;
//    }
//    public static void setMobileFriendlyAPITotalTimeInSec(double mobileFriendlyAPITotalTimeInSec)
//    {
//        GoogleAPIResults.mobileFriendlyAPITotalTimeInSec = mobileFriendlyAPITotalTimeInSec;
//    }
//    public static double getUsabilityScoreAPITotalTimeInSec() {
//        return usabilityScoreAPITotalTimeInSec;
//    }
//
//    public static void setUsabilityScoreAPITotalTimeInSec(double usabilityScoreAPITotalTimeInSec) {
//        AccessibilityScannerResults.usabilityScoreAPITotalTimeInSec = usabilityScoreAPITotalTimeInSec;
//    }

    //    public static int getMobileFriendlyAPICalls()
//    {
//        return mobileFriendlyAPICalls;
//    }
//    public static void setMobileFriendlyAPICalls(int mobileFriendlyAPICalls)
//    {
//        GoogleAPIResults.mobileFriendlyAPICalls = mobileFriendlyAPICalls;
//    }
//    public static int getUsabilityScoreAPICalls() {
//        return usabilityScoreAPICalls;
//    }

    //    public static void setUsabilityScoreAPICalls(int usabilityScoreAPICalls)
//    {
//        GoogleAPIResults.usabilityScoreAPICalls = usabilityScoreAPICalls;
//    }
//    public MobileFriendlyAPIParser getMobileFriendlyAPIObj()
//    {
//        return mobileFriendlyAPIObj;
//    }
//    public UsabilityScoreAPIParser getAccessibilityScannerObj() {
//        return accessibilityScannerObj;
//    }

    //
//    public void processUsabilityScoreAPIResult() {
//        System.out.println("Usability Score API call");
//        long startTime = System.nanoTime();
//        accessibilityScannerObj = new UsabilityScoreAPIParser();
//        accessibilityScannerObj.processAPIResults();
//        System.out.println("Usability Score = " + accessibilityScannerObj.getUsabilityScore());
//        long endTime = System.nanoTime();
//        usabilityScoreAPICalls++;
//        usabilityScoreAPITotalTimeInSec = usabilityScoreAPITotalTimeInSec + Util.convertNanosecondsToSeconds((endTime - startTime));
//    }

    public HashMap<String, String> getTypeOfSizeIssues() {
        /*** What type of TT size issues each activity has? height, width or both? This is based on the initiali list of issues and we use this info to decide whether we need to increase height adn/or width .. ***/
        HashMap<String, String> activitiesToIssuesTypes = new HashMap<>();

        for (String actName : this.getListOfIssues().keySet()
        ) {
            Set<TIssue> issues = this.getListOfIssues().get(SALEM.getCurrentActivityName()); // Get the list of issues for the current activity
            String type;
            int height = 0;
            int width = 0;
            int both = 0;
            for (TIssue issue : issues) {
                type = issue.getIssueType();
                switch (type) {
                    case Constants.TOUCH_TARGET_HEIGHT_ISSUE:
                        height++;
                        break;

                    case Constants.TOUCH_TARGET_WIDTH_ISSUE:
                        width++;
                        break;

                    case Constants.TOUCH_TARGET_BOTH_ISSUE:
                        both++;
                        break;

                }
            }
            if (both > 0) {
                activitiesToIssuesTypes.put(actName, "both");
            }
            if (height > 0) {
                activitiesToIssuesTypes.put(actName, "height");
            }
            if (width > 0) {
                activitiesToIssuesTypes.put(actName, "width");
            }


        }
        return activitiesToIssuesTypes;
    }

    public void setTypeOfSizeIssuesForSegments(Segment seg) {
        /*** What type of TT size issues each activity has? height, width or both? This is based on the initiali list of issues and we use this info to decide whether we need to increase height adn/or width .. ***/
        HashMap<String, String> activitiesToIssuesTypes = new HashMap<>();

        Set<TIssue> issues = this.getListOfIssues().get(SALEM.getCurrentActivityName()); // Get the list of issues for the current activity

        String type;
        int height = 0;
        int width = 0;
        int both = 0;

        for (TIssue issue : issues) {
            if (Util.isIssueInsegemnt(issue, seg)) { // only count an issue if it effects a node in the segment
                type = issue.getIssueType();
                switch (type) {
                    case Constants.TOUCH_TARGET_HEIGHT_ISSUE:
                        height++;
                        break;

                    case Constants.TOUCH_TARGET_WIDTH_ISSUE:
                        width++;
                        break;

                    case Constants.TOUCH_TARGET_BOTH_ISSUE:
                        both++;
                        break;

                }
            }
        }
        if (both > 0 || (height > 0 && width > 0)) {
            seg.setSizeIssueType(Constants.TOUCH_TARGET_BOTH_ISSUE);
        } else if (height > 0) {
            seg.setSizeIssueType(Constants.TOUCH_TARGET_HEIGHT_ISSUE);
        } else if (width > 0) {
            seg.setSizeIssueType(Constants.TOUCH_TARGET_WIDTH_ISSUE);
        }


    }


//
//    public void processMobileFriendlyAPIResult()
//    {
//        int count = 0;
//        do
//        {
//            System.out.println("Mobile friendly API call " + (count + 1));
//            long startTime = System.nanoTime();
//            mobileFriendlyAPIObj = new MobileFriendlyAPIParser();
//            mobileFriendlyAPIObj.processAPIResults();
//            long endTime = System.nanoTime();
//            mobileFriendlyAPICalls++;
//            mobileFriendlyAPITotalTimeInSec = mobileFriendlyAPITotalTimeInSec + Util.convertNanosecondsToSeconds((endTime - startTime));
//            count++;
//
//            if(!mobileFriendlyAPIObj.isStatusComplete())
//            {
//                try
//                {
//                    Thread.sleep(100000);	// quota: 1 request per 100 seconds for one user
//                }
//                catch (InterruptedException e)
//                {
//                    e.printStackTrace();
//                }
//            }
//
//        } while(!mobileFriendlyAPIObj.isStatusComplete() && count < 3);
//        System.out.println("Mobile friendly API json = \n" + mobileFriendlyAPIObj.getJsonString());
//    }
//
//    public static void main(String[] args)
//    {
//        mFix.setUrl("http://sonal.usc.edu:8080/mfix/irs/");
//        //WebDriverSingleton.loadPage(mFix.getUrl());
//        long startTime = System.nanoTime();
//        GoogleAPIResults gar = new GoogleAPIResults();
//        //System.out.println("isMobileFriendly = " + gar.getMobileFriendlyAPIObj().isMobileFriendly());
//        //System.out.println("mobileFriendlyIssues = " + gar.getMobileFriendlyAPIObj().getMobileFriendlyIssues());
//        //gar.processUsabilityScoreAPIResult();
//        gar.processMobileFriendlyAPIResult();
//        //System.out.println("usabilityScore = " + gar.getUsabilityScoreAPIObj().getUsabilityScore());
//        System.out.println(gar.getMobileFriendlyAPIObj().isMobileFriendly());
//        long endTime = System.nanoTime();
//        System.out.println("Time = " + Util.convertNanosecondsToSeconds((endTime - startTime)) + "s");
//    }
}
