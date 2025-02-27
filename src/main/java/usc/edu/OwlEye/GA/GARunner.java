package usc.edu.OwlEye.GA;


import gatech.xpert.dom.DomNode;
import org.apache.commons.io.FileUtils;
import org.tinylog.Logger;
import usc.edu.OwlEye.OwlConstants;
import usc.edu.OwlEye.OwlEye;
import usc.edu.OwlEye.util.Utils;
import usc.edu.SALEM.Constants;

//import usc.edu.SALEM.GA.GASearch;
import usc.edu.SALEM.RunSALEM;
import usc.edu.SALEM.SALEM;
//import usc.edu.SALEM.VHTree.DependencyGraph;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
import usc.edu.SALEM.VHTree.SegmentRelationGraph;
import usc.edu.SALEM.VHTree.XMLUtils;
import usc.edu.SALEM.fitness.AccessibilityScannerResults;
import usc.edu.SALEM.fitness.FitnessFunction;
import usc.edu.SALEM.fitness.TIssue;
import usc.edu.SALEM.segmentation.Segment;
import usc.edu.SALEM.segmentation.SegmentModel;
import usc.edu.SALEM.util.ReadIssues;
import usc.edu.SALEM.util.Util;

import java.io.*;
import java.util.*;

public class GARunner {

    private GAChromosome chromosome;
    private ArrayList<GAChromosome>initialPopulation = new ArrayList<GAChromosome>();
    public void runIterator2() throws IOException,InterruptedException{
        GAWrapper gaw = new GAWrapper(null);
    //    chromosome = gaw.extractGenesBasedOnIssueType();

        initialPopulation= gaw.extractPopulationBasedOnIssueType();
        chromosome = initialPopulation.get(0);
//        for (GAChromosome c:initialPopulation){
//             System.tr.println(c.getGenes());
//
//        }
        Logger.trace("Done initial chromosome: " + chromosome.toString());

        GASearch search = new GASearch(initialPopulation);
        boolean justInitialPopulation = false; // if I want to test new apps with initial population
        if(justInitialPopulation) {
            search.runGA3();// keeping it simple now Sep22
        }
        else {
            GAChromosome optimalSolution=null;
            try {
                optimalSolution = search.runGA4();
            } catch (Exception e) {
                Logger.debug("Exception in runGA4");
                Logger.debug(e.getMessage());
                e.printStackTrace();
            }
            finally {
                if(optimalSolution!=null) {
                    Logger.debug("*********************************************************");
                    Logger.debug("Done ...........optimal chromosome: ");
                    Logger.debug(optimalSolution.toString());
                    Logger.debug("*********************************************************");
                    Utils.writeOptimalChromosomeToFile(optimalSolution);
                    boolean printParetoFront = true;
                    if (printParetoFront) {
                        Logger.debug("*********************************************************");
                        Logger.debug("Done ...........Pareto Front: ");

                        List<GAChromosome> pareto = search.getParetoFront();
                        if (pareto != null && pareto.size() > 0) {
                            String paretoFolder=OwlEye.getFinalRepairOutputFolder()+File.separator+"ParetoFront";
                            // create the folder if it does not exist
                            File paretoFolderFile = new File(paretoFolder);
                            if (!paretoFolderFile.exists()) {
                                paretoFolderFile.mkdir();
                            }
                            for (GAChromosome c : pareto) {
                                Utils.writeChromosomeToFilePareto(c, paretoFolder);
                            }
                        }


                    }

                }
            }
            // finally block

//             optimalSolution = search.runGA4();
//            Logger.debug("*********************************************************");
//            Logger.debug("Done ...........optimal chromosome: ");
//            Logger.debug(optimalSolution.toString());
//            Logger.debug("*********************************************************");
//            if(optimalSolution!=null) {
//                Logger.debug("*********************************************************");
//                Logger.debug("Done ...........optimal chromosome: ");
//                Logger.debug(optimalSolution.toString());
//                Logger.debug("*********************************************************");
//                Utils.writeOptimalChromosomeToFile(optimalSolution);
//            }

//            Logger.debug(optimalSolution.toString());


            ;//\
        }
//        search.runGA3();
//        usc.edu.SALEM.GA.GAChromosome chromosomeFitnessFunction = search.runGASegmentRelation();
    }
//    public void runIterator() throws IOException, InterruptedException {
//        usc.edu.OwlEye.fitness.FitnessFunction ff= new usc.edu.OwlEye.fitness.FitnessFunction();
//        String originalDynamic=OwlEye.getOriginalDynamicLayoutFilePath().toLowerCase(Locale.ROOT);
//
//        // Set original as the default_font_size
//        String baseVHPath= OwlEye.getMergePath();
//        String tempToTestLarge=OwlEye.getOriginalDynamicLayoutFilePath();
//        String default_font_size_VH= baseVHPath+File.separator+OwlConstants.DEFAULT_FONT_SIZE_FOLDER+File.separator
//                +OwlEye.getOriginalActivityName()+".xml";
//        String largest_font_size_VH= tempToTestLarge+File.separator+OwlConstants.LARGEST_FONT_SIZE_FOLDER+File.separator
//                +"refined/";
//
//
//        XMLUtils.getInstance(default_font_size_VH);
//
//        //Just for testing
//        GAChromosome chromosome= new GAChromosome();
//        chromosome.setChromosomeIdentifier("1");
//        ff.calculateFitnessScore(chromosome,largest_font_size_VH);
//    }


//    private void init(String apkCrawledFiles
//                      , String apkIssuesListFolder
//    ) {
//
//        // Ali: initialize Search
//        /*
//        Load Initial list of issues and initial activities
//         */
//
//        Set<String> problematicActivities = null;
//
//
//            problematicActivities = new HashSet<>();
//            for (String act : RunSALEM.appActivities) {
//                problematicActivities.add(act);
//            }
//            SALEM.setProblematicActivities(problematicActivities);
//
//
//
//        FitnessFunction.setFitnessCalls(0);
//        FitnessFunction.setFitnessTimeInSec(0);
//        XMLUtils.resetInstance();
//        SALEM.setMobileFriendly(false);
//        SALEM.setSegmentToDG(new HashMap<String, DependencyGraph>());
//        SALEM.setSegmentToSG(new HashMap<String, SegmentRelationGraph>());
//        SALEM.setOriginalUISegmentModel(new SegmentModel());
//        SALEM.setOriginalUISegments(new ArrayList<Segment>());
//
//
//        Util.setElementPropValueCache(new HashMap<String, String>());
//        String logFilePath = new File(SALEM.getOriginalDecompiled()).getParent();
//        if (Constants.RUN_IN_DEBUG_MODE) {
//            try {
//                System.setOut(new PrintStream(new FileOutputStream(logFilePath + File.separatorChar + "log_" + Util.getFormattedTimestamp() + ".txt")));
//            } catch (Exception e) {
//            }
//        }
//
//        // (6) Read the layouts of the activities and compute the score
//
//        /* Copy the original apk and make a new decompile file to be used
//         */
//
//
//    }

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
//        double fitnessScore = chromosomeFitnessFunction.getFitnessFunctionObj().getFitnessScore();
//        double accScore = chromosomeFitnessFunction.getFitnessFunctionObj().getA11yHeuristicsScore();
//        double asethScore = chromosomeFitnessFunction.getFitnessFunctionObj().getAestheticScore();
//        double spacingScore = chromosomeFitnessFunction.getFitnessFunctionObj().getSpacingScore();
        //String score="Fitness: "+fitnessScore+","+"ACC: "+accScore+","+"AS: "+asethScore+","+"SP: "+spacingScore;
//        solutionData.add("Fitness: " + fitnessScore);
//        solutionData.add("ACC: " + accScore);
//        solutionData.add("AS: " + asethScore);
//        solutionData.add("SP: " + spacingScore);
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
//        writer.println("IssuesScore=" + chromosomeFitnessFunction.getFitnessFunctionObj().getNoIssuesPercentage());
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
