package usc.edu.SALEM.GA;


import java.io.*;
import java.util.*;

//import TTFix.TestTTFIX_ACTIVITIES;
//import TTFix.TestTTFIX_SingleActivity;
import gatech.xpert.dom.DomNode;
import usc.edu.SALEM.util.ReadIssues;
import usc.edu.SALEM.fitness.TIssue;
import usc.edu.SALEM.RunSALEM;
//import usc.edu.SALEM.VHTree.Node;
import usc.edu.SALEM.VHTree.SegmentRelationGraph;
//import mfix.merge.Merger;
import usc.edu.SALEM.VHTree.XMLUtils;
import usc.edu.SALEM.util.Util;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;

import usc.edu.SALEM.Constants;

import usc.edu.SALEM.SALEM;
import usc.edu.SALEM.VHTree.DependencyGraph;
import usc.edu.SALEM.fitness.FitnessFunction;
import usc.edu.SALEM.fitness.AccessibilityScannerResults;
import usc.edu.SALEM.segmentation.Segment;
import usc.edu.SALEM.segmentation.SegmentModel;
import org.apache.commons.io.FileUtils;

public class GARunner {

    private GAChromosome chromosome;



    public void runIteratorForSegmentRelationGraph(String apkCrawledFiles, String apkIssuesListFiles
    ) throws IOException, InterruptedException {
//        public void runIterator(String url, String filePath) {


//        init(url, filePath);
        long startInitTime = System.nanoTime();
        SALEM.runtime_map.put("init", startInitTime);
        init(apkCrawledFiles, apkIssuesListFiles);



        // Now after teh init we have merged the activities based on the initial dynamic layout and already have the list of issues
//        List<String> ignoredActivities = Arrays.asList(
//                "com.colorsnap.LoginActivity_",
//                "com.colorsnap.WebViewActivity_",
//                "com.js.rssreader.DetailActivity",
//                "uk.co.bitethebullet.android.token.PinChange",
//                "com.activehours.ui.activities.ActLanding",
//                "com.activehours.ui.activities.ActLanding",
//                "com.colorsnap.WebViewActivity_",
//                "com.colorsnap.LoginActivity_",
////               "com.contorra.golfpad.MainMenu"
//                "com.contorra.golfpad.statistics.StatisticsMainMenu"
////               "com.contorra.golfpad.Upgrade"
//        );
//       if(ignoredActivities.contains(act))
        if (SALEM.RUNNING_MODE.equalsIgnoreCase("activity")) {

            String activityName = SALEM.getCurrentActivityName();
            SALEM.setCurrentActivityMergedFilePath(SALEM.getMergePath() + File.separator + activityName + ".xml");

            //(2) Build the layout of activity based on the merged layout of that activity
            XMLUtils.getInstance(SALEM.getCurrentActivityMergedFilePath());
            List<Node<DomNode>> touchTargetsList = Util.getListOfTouchTargets(XMLUtils.getRoot());
            if (touchTargetsList != null) {
                SALEM.setCurrentActivityOriginalNoOfTT(touchTargetsList.size()); //Store the originalNumberOfTouchTargets
            }
            // From now on these steps will be done for each chromosome
            long startSegment = System.nanoTime();
            SegmentModel segModel = new SegmentModel();
            segModel.buildSegmentModel();
            SALEM.setOriginalUISegmentModel(segModel);
            long endSegment = System.nanoTime();
            SALEM.runtime_map.put("SegmentTotal", endSegment - startSegment);


            /* Stage 1: initial mobile friendly problems detection */
            System.out.println("++++++++++++++++ Stage 1: get the initial touch target issues ++++++++++++++++");

            /* Already captured the initial list of size issues in the init method */
            AccessibilityScannerResults initialIssues = SALEM.getDetectionToolResults().get("initial");
            FitnessFunction ft = new FitnessFunction();
            double accessibilityScore = ft.calculateA11yHeuristicsScore("initial", XMLUtils.getRoot());
//            double accessibilityScore = initialIssues.calculateAccessibilityScore(XMLUtils.getRoot());
            SALEM.setBeforeAccessibilityScore(accessibilityScore);
            SALEM.setAfterAccessibilityScore(accessibilityScore);

            System.out.println("Initial Accessibility score  = \n" + accessibilityScore);

            /* Stage 2: extract root causes */
            System.out.println("\n++++++++++++++++ Stage 2: extract genes ++++++++++++++++");
            GAWrapper gaw = new GAWrapper(initialIssues);
            chromosome = gaw.extractGenesBasedOnIssueTypeSegmentRelationGraph();
            //Ali: Now we get the first chromosome which containts the propery with the recommended size from Google : TODO: DO we use this or my heuristic which says the increase by the smallest element
            System.out.println("Done initial chromosome: " + chromosome.toString());
            //  System.exit(0);
            System.out.println("\n **************** SEGMENT DG MAP ************");
            System.out.println(SALEM.getSegmentToSG());
            long endInitTime = System.nanoTime();
            SALEM.runtime_map.put("initTotal", endInitTime - startInitTime);
            System.out.println("Init time = " + Util.convertNanosecondsToSeconds((endInitTime - startInitTime)) + " sec");


            // Stage 3: run search
            System.out.println("\n++++++++++++++++ Stage 3: run search ++++++++++++++++");
            // Ali: We initilize the search with the initial chromosome that include the default value for issues and then go to generate the other chromosome for the population
            GASearch search = new GASearch(chromosome);

            GAChromosome chromosomeFitnessFunction = search.runGASegmentRelation();
            System.out.println("\n\nSolution chromosome based on fitness function = " + chromosomeFitnessFunction);

            long endTime = System.nanoTime();
            long startTime = SALEM.runtime_map.get("startTime");
//               mFix.runtime_map.remove("startTime");
            long totalTime = endTime - startTime;
            SALEM.runtime_map.put("totalTime", totalTime);

//            chromosome = search.getSolutionByHeuristic();
//            System.out.println("\n\nSolution chromosome based on heuristic = " + chromosome);
//            if (chromosome.getFitnessFunctionObj() != null) {
//                mFix.setAfterUsabilityScore(chromosome.getFitnessFunctionObj().getUsabilityScore());
//                if (mFix.getAfterUsabilityScore() >= Constants.USABILITY_SCORE_THRESHOLD) {
//                    mFix.setMobileFriendly(true);
//                }
//            }

            postProcessing(chromosomeFitnessFunction);


        }
        else {
            Set<String> x = SALEM.getProblematicActivities();
            for (String activityName : SALEM.getProblematicActivities()
            ) {
                //For testing list activity only
//                if (ignoredActivities.contains(activityName)) {
//                    continue;
//                }
//            if (activityName.equalsIgnoreCase("com.js.rssreader.DetailActivity") ||
//                    activityName.equalsIgnoreCase("uk.co.bitethebullet.android.token.PinChange")
//                    || activityName.equalsIgnoreCase("com.activehours.ui.activities.ActLanding") ||
//                    activityName.equalsIgnoreCase("com.activehours.ui.activities.ActLanding") ||
//            //        activityName.equalsIgnoreCase("com.colorsnap.WebViewActivity_") ||
//            activityName.equalsIgnoreCase("com.colorsnap.LoginActivity_")
//            )
//        {
//                continue;
//            }
                /* build View Tree for the activity       */
                //(1) Set the path variables for the activity
                SALEM.setCurrentActivityName(activityName);
                String xx = SALEM.getMergePath() + File.separator + activityName + ".xml";
                SALEM.setCurrentActivityMergedFilePath(SALEM.getMergePath() + File.separator + activityName + ".xml");

                //(2) Build the layout of activity based on the merged layout of that activity
                XMLUtils.getInstance(SALEM.getCurrentActivityMergedFilePath());
                List<Node<DomNode>> touchTargetsList = Util.getListOfTouchTargets(XMLUtils.getRoot());
                if (touchTargetsList != null) {
                    SALEM.setCurrentActivityOriginalNoOfTT(touchTargetsList.size()); //Store the originalNumberOfTouchTargets
                }
                // From now on these steps will be done for each chromosome
                SegmentModel segModel = new SegmentModel();
                segModel.buildSegmentModel();

                if (Constants.TESTING_SEGMENTS) { // To output the segments for me to test
                    try {
                        System.setOut(new PrintStream(new FileOutputStream(SALEM.segmentsLogoutPath, true)));

                    } catch (Exception e) {
                    }
                    System.out.println("Segments for " + SALEM.getCurrentActivityName());
                    System.out.println("\nSegment terminate threshold value = " + segModel.getSegmentationObject().getSegmentTerminateThreshold());
                    System.out.println("Segments (size = " + segModel.getSegmentationObject().getSegments().size() + "): ");


                    for (Segment seg : segModel.getSegmentationObject().getSegments()) {
                        System.out.println(seg);
                        System.out.println("\nIntra-segment edges for segment S" + seg.getId());
                        System.out.println(seg.getEdges());
                    }
                    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
                    continue;


//                System.out.println("\nSegment Model: " + segModel.getSegmentationObject().getSegments().get());
//                System.out.println("\nSegment Model: " + segModel);


                }


                SALEM.setOriginalUISegmentModel(segModel);


                /* Stage 1: initial mobile friendly problems detection */
                System.out.println("++++++++++++++++ Stage 1: initial mobile friendly problems detection ++++++++++++++++");

                /* Already captured the initial list of size issues in the init method */
                AccessibilityScannerResults initialIssues = SALEM.getDetectionToolResults().get("initial");
//                double accessibilityScore = initialIssues.calculateAccessibilityScore(XMLUtils.getRoot());
                FitnessFunction ft = new FitnessFunction();
                double accessibilityScore = ft.calculateA11yHeuristicsScore("initial", XMLUtils.getRoot());

                SALEM.setBeforeAccessibilityScore(accessibilityScore);
                SALEM.setAfterAccessibilityScore(accessibilityScore);

                System.out.println("Initial Accessibility score  = \n" + accessibilityScore);

                /* Stage 2: extract root causes */
                System.out.println("\n++++++++++++++++ Stage 2: extract genes ++++++++++++++++");
                GAWrapper gaw = new GAWrapper(initialIssues);
                chromosome = gaw.extractGenesBasedOnIssueTypeSegmentRelationGraph();
                //Ali: Now we get the first chromosome which containts the propery with the recommended size from Google : TODO: DO we use this or my heuristic which says the increase by the smallest element
                System.out.println("Done initial chromosome: " + chromosome.toString());
                //  System.exit(0);
                System.out.println("\n **************** SEGMENT DG MAP ************");
                System.out.println(SALEM.getSegmentToSG());
                long endInitTime = System.nanoTime();
                System.out.println("Init time = " + Util.convertNanosecondsToSeconds((endInitTime - startInitTime)) + " sec");


                // Stage 3: run search
                System.out.println("\n++++++++++++++++ Stage 3: run search ++++++++++++++++");
                // Ali: We initilize the search with the initial chromosome that include the default value for issues and then go to generate the other chromosome for the population
                GASearch search = new GASearch(chromosome);

                GAChromosome chromosomeFitnessFunction = search.runGASegmentRelation();
                System.out.println("\n\nSolution chromosome based on fitness function = " + chromosomeFitnessFunction);


//                chromosome = search.getSolutionByHeuristic();
//                System.out.println("\n\nSolution chromosome based on heuristic = " + chromosome);
//                if (chromosome.getFitnessFunctionObj() != null) {
//                    mFix.setAfterUsabilityScore(chromosome.getFitnessFunctionObj().getUsabilityScore());
//                    if (mFix.getAfterUsabilityScore() >= Constants.USABILITY_SCORE_THRESHOLD) {
//                        mFix.setMobileFriendly(true);
//                    }
//                }

                postProcessing(chromosomeFitnessFunction);

            }
        }


    }

    private void init(String apkCrawledFiles
                      , String apkIssuesListFolder
    ) {

        // Ali: initialize variables, set the apk names and input, output folders



        /*
        Load Initial list of issues and initial activities
         */
        Set<String> problematicActivities = null;

        if (!Constants.TESTING_SEGMENTS) { // To output the segments for me to test
            // (1) First the developer provide list of issues in a csv file that contain the activities that these issues belong to as well as other important info
            String filePath = SALEM.INITIAL_ISSUES_FOLD_PATH + "/" + SALEM.getOriginalApkName() + Constants.TOUCH_TARGET_ISSUES_OUTPUT_SUFFIX; //First time the original file of apk is used no chromosome suffix
            ReadIssues readIssue = new ReadIssues(filePath);
            HashMap<String, Set<TIssue>> TTSZResult = readIssue.parseIssue("initial");//Since this is the initial issue we add "initial" as parameter instead of chromosome ID
            //(2) Initialize the result class to store the list of issues as well as the scores
            AccessibilityScannerResults result = new AccessibilityScannerResults();

            //(3) Store the list of TT size issues in the result class
            result.setListOfIssues(TTSZResult, true); // true= this is the initial list of issues
            SALEM.getDetectionToolResults().put("initial", result);

//        mFix.setDetectionToolResults();detectionToolResults.put("initial",result);
            // (4) We assume that the original list of problematic activities are also provided for the app initially
            problematicActivities = result.getInitialProblematicActivities();
            SALEM.setProblematicActivities(problematicActivities);

//            HashMap<String, String> activitiesToIssuesType=result.getTypeOfSizeIssues();
//
//            mFix.setActivitiesToIssuesTypes(activitiesToIssuesType);

        } else {
            problematicActivities = new HashSet<>();
            for (String act : RunSALEM.appActivities) {
                problematicActivities.add(act);
            }
            SALEM.setProblematicActivities(problematicActivities);

        }

        //Takes (1) the initial dynamic layouts, (2) the result of guiAnalyzer, (3) original decompiled apk (for dummy matching),

        // (4)list of problematic activities taken from the initial list of issues(to limit the search for those activities)
        // Output now is the merged files
//        Merger merger = new Merger();
//        String x = mFix.getMergePath();
//        File destDir = new File(mFix.getMergePath());
//        if (!destDir.exists()) {
//            destDir.mkdirs();
//            merger.prepare_app_layout(mFix.getOriginalApkName(), problematicActivities, mFix.getOriginalDecompiled(), apkCrawledFiles, guianalzyerPath);
//
//        } else if (Util.isDirEmpty(mFix.getMergePath()) || mFix.generateNewMerged) {
//
//
//            merger.prepare_app_layout(mFix.getOriginalApkName(), problematicActivities, mFix.getOriginalDecompiled(), apkCrawledFiles, guianalzyerPath);
//        } //mFix.setFilepath(mFix.getMergePath());
        // (5) Now that we have merged the files  the next step is to initilize hte fitness functions and other important attributes
        FitnessFunction.setFitnessCalls(0);
        FitnessFunction.setFitnessTimeInSec(0);
        XMLUtils.resetInstance();
        SALEM.setMobileFriendly(false);
        SALEM.setSegmentToDG(new HashMap<String, DependencyGraph>());
        SALEM.setSegmentToSG(new HashMap<String, SegmentRelationGraph>());
        SALEM.setOriginalUISegmentModel(new SegmentModel());
        SALEM.setOriginalUISegments(new ArrayList<Segment>());


//        HashMap<String, Table> initial_issues = get_initial_issues_list(mFix.getApkName(), apkIssuesListFolder);
//        Set<String> activitiesWithIssues = get_list_of_activites_with_issues(initial_issues); // We should only focus on activities that have issues then merge and crawl only these
//        mFix.setProblematicActivities(activitiesWithIssues);
//        mFix.setInitial_issues(initial_issues);
        Util.setElementPropValueCache(new HashMap<String, String>());
        String logFilePath = new File(SALEM.getOriginalDecompiled()).getParent();
        if (Constants.RUN_IN_DEBUG_MODE) {
            try {
                System.setOut(new PrintStream(new FileOutputStream(logFilePath + File.separatorChar + "log_" + Util.getFormattedTimestamp() + ".txt")));
            } catch (Exception e) {
            }
        }

        // (6) Read the layouts of the activities and compute the score

        /* Copy the original apk and make a new decompile file to be used
         */


    }

    private void writeSolutionFile(GAChromosome chromosomeFitnessFunction) throws IOException {

        ArrayList<String> solutionData = new ArrayList<>();
        solutionData.add(SALEM.getCopiedApkFileName());
        solutionData.add(SALEM.getCurrentActivityName());

        chromosomeFitnessFunction.getChromosomeIdentifier();
        solutionData.add(chromosomeFitnessFunction.getChromosomeIdentifier());
        for (GAGene gene : chromosomeFitnessFunction.getGenes()) {
            String issue = gene.getIssueType();
            String value = gene.getValue();
            String property = gene.getCssProperty();
            String g = "<" + issue + ":" + property + ":" + value + ">";
            solutionData.add(g);
        }
        double fitnessScore = chromosomeFitnessFunction.getFitnessFunctionObj().getFitnessScore();
        double accScore = chromosomeFitnessFunction.getFitnessFunctionObj().getA11yHeuristicsScore();
        double asethScore = chromosomeFitnessFunction.getFitnessFunctionObj().getAestheticScore();
        double spacingScore = chromosomeFitnessFunction.getFitnessFunctionObj().getSpacingScore();
        //String score="Fitness: "+fitnessScore+","+"ACC: "+accScore+","+"AS: "+asethScore+","+"SP: "+spacingScore;
        solutionData.add("Fitness: " + fitnessScore);
        solutionData.add("ACC: " + accScore);
        solutionData.add("AS: " + asethScore);
        solutionData.add("SP: " + spacingScore);
//        solutionData.add(score+",");
        Util.writeSolutionToCSV(solutionData, SALEM.SOLUTION_CSV_OUTPUT);


    }

    private void postProcessing(GAChromosome chromosomeFitnessFunction) throws IOException {
        File destDir = new File(SALEM.final_fix_output);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        String chromosomeAPKFile = chromosomeFitnessFunction.getAPKPath();
        String chromosomeDecompiled = chromosomeFitnessFunction.getDecompiledPath();
        String chromosomeCrawled = chromosomeFitnessFunction.getCrawledPath();
        File decompiledFile = new File(chromosomeDecompiled);
        String fileName = decompiledFile.getName();
        String currentSolutionDir = fileName.split("_chromosome_")[0];
        currentSolutionDir = SALEM.final_fix_output + File.separator + currentSolutionDir;
        destDir = new File(currentSolutionDir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        /*** Write the solution ***/
        String apkNewFile = destDir.getAbsolutePath();
        // (1) Print the solution details to a text file
        PrintWriter writer = new PrintWriter(destDir.getAbsoluteFile() + File.separator + "SolutionDetails.txt", "UTF-8");

        writer.println("Activity Name:");
        writer.println(SALEM.getCurrentActivityName());
        writer.println("Chromosome ID:");
        writer.println(chromosomeFitnessFunction.getChromosomeIdentifier());
        writer.println("IssuesScore=" + chromosomeFitnessFunction.getFitnessFunctionObj().getNoIssuesPercentage());
        writer.println("------------------------------------------");
        writer.println("Genes:");
        writer.println(chromosomeFitnessFunction.getGenes().toString());
        writer.println("------------------------------------------");
        writer.println("fitness score:");
        writer.println(chromosomeFitnessFunction);
        writer.close();

        if (SALEM.RUNNING_MODE.equalsIgnoreCase("activity")) {
            SALEM.finalChromosome.add(chromosomeFitnessFunction.getChromosomeIdentifier());

            SALEM.finalChromosome.add(chromosomeFitnessFunction.getGenes().toString());
            SALEM.finalChromosome.add(chromosomeFitnessFunction.getFitnessFunctionObj().toString());

        }
        //(2) Move the apk and decompiled apk to the fix solution folder
        String destAPK = destDir.getAbsolutePath() + File.separator + SALEM.getCurrentApkPackage() + ".apk";
        String destDecompiled = destDir.getAbsolutePath() + File.separator + SALEM.getCurrentApkPackage();
        String destCrawled = destDir.getAbsolutePath() + File.separator + "CrawledData";
        //Copy APK
        FileUtils.copyFile(new File(chromosomeAPKFile), new File(destAPK));
        //Copy decompiled
        Util.copyAppFolder(chromosomeDecompiled, destDecompiled, null);

        //Copy crawled
        Util.copyAppFolder(chromosomeCrawled, destCrawled, null);
        writeSolutionFile(chromosomeFitnessFunction);

        // (3) write the run time

        PrintWriter runtimeWriter = new PrintWriter(destDir.getAbsoluteFile() + File.separator + "runtime.txt", "UTF-8");
        for (String runTimeType : SALEM.runtime_map.keySet()
        ) {
            runtimeWriter.println(runTimeType + ":" + SALEM.runtime_map.get(runTimeType));
        }
        runtimeWriter.println("totalChromosomeRun" + ":" + SALEM.chromosomeCalCount);
        runtimeWriter.println("totalFitnessRun" + ":" + SALEM.populationCalCount);
        runtimeWriter.close();

//        FileUtils.copyFile(new File(apkDecompiled), new File(destDecompiled));


        //  Now I am using my applychanges method
//        TTFIXUtil.applyNewValues(mFix.getFilepath(), chromosome);
////       Util.applyNewValues(chromosome);
//		/*System.out.println("\nFinal fitness function call:");
//		FitnessFunction ff = new FitnessFunction();
//		ff.calculateFitnessScore(chromosome);*/
//
//        // take screenshot
////        WebDriverSingleton.takeScreenshot(mFix.getOutputFolderPath() + File.separatorChar + "index-after.png");
//
//        // create fixed test page
//        //Nice Idea to create folder with the usability score and
//        String originalFile = mFix.getFilepath();
//        new File(mFix.getFilepath())
//                .renameTo(new File(new File(mFix.getFilepath()).getParent() + File.separatorChar + "index-" + Math.round(chromosome.getFitnessFunctionObj().getUsabilityScore()) + "-" + new File(mFix.getOutputFolderPath()).getName() + ".html"));
//        new File(mFix.getOriginalDecompiled()).renameTo(new File(originalFile));

        // outputFixedTestPage();

//        WebDriverSingleton.closeBrowser();
    }



}
