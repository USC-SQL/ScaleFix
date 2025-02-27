package usc.edu.OwlEye.util;

import org.tinylog.Logger;
import usc.edu.OwlEye.GA.GAChromosome;
import usc.edu.OwlEye.OwlConstants;
import usc.edu.OwlEye.OwlEye;
import usc.edu.OwlEye.fitness.FitnessFunction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class AndroidEmulatorAccess {
//    private static final int NUM_EMULATORS = 1;
    private static final Semaphore semaphore = new Semaphore(OwlConstants.NUM_EMULATORS);
    private static  List<String> availableEmulators = new ArrayList<>(OwlConstants.NUM_EMULATORS);
    private final Map<Integer, GAChromosome> jobArguments;
    private final Thread[] threads;

//    static {
//       // availableEmulators.add("emulator-5554");
//       availableEmulators.add("emulator-5554");
////        availableEmulators.add("emulator3");
//    }

    public AndroidEmulatorAccess(Map<Integer, GAChromosome> jobArguments) {
        this.jobArguments = jobArguments;
        this.threads = new Thread[jobArguments.size()];
        availableEmulators = new ArrayList<>(OwlConstants.NUM_EMULATORS);
        // add all strngs in the OwlConstants.availableEmulators to the availableEmulators here
        for (String emulatorName: OwlConstants.availableEmulators) {
            availableEmulators.add(emulatorName);
        }
    }
    public void startEmulatorAccess2() {
        int i = 0;
        for (Map.Entry<Integer, GAChromosome> entry : jobArguments.entrySet()) {
            int jobId = entry.getKey();
            GAChromosome chromosome = entry.getValue();

            threads[i] = new Thread(() -> {
                String emulatorName = null;
                try {
                    semaphore.acquire();
                     emulatorName = availableEmulators.remove(0);
                    handleJob(jobId, chromosome,emulatorName);
                    // Access Android emulator here
                    // ...
                    // Use jobId and arguments for the job
                    // ...
                } catch (InterruptedException e) {
                    // Handle exception
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    synchronized (availableEmulators) {
                        availableEmulators.add(emulatorName);
                    }
                  //  availableEmulators.add(emulatorName);
                    semaphore.release();
                }
            });
            threads[i].start();
            i++;
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                // Handle exception
            }
        }
    }

//    public void startEmulatorAccess() {
//        for (Map.Entry<Integer, GAChromosome> entry : jobArguments.entrySet()) {
//            int jobId = entry.getKey();
//            GAChromosome chromosome = entry.getValue();
//
//            new Thread(() -> {
//                try {
//                    semaphore.acquire();
//                    handleJob(jobId, chromosome, emulatorName);
//                    // ...
//                } catch (InterruptedException | IOException e) {
//                    // Handle exception
//                } finally {
//                    semaphore.release();
//                }
//            }).start();
//
//        }
//    }

    private void handleJob(int jobId, GAChromosome chromosome, String emulatorName) throws IOException, InterruptedException {


//        int i = Integer.parseInt(arguments[0]);


        String newLayoutFolder = null;
        String newApkName = chromosome.getNewApkName();
        String capturedUIFolder = OwlEye.getCrawledDynamicRepairVHPath() + newApkName;
        String compiled_output_path= OwlEye.getCompileOutputPath();


            boolean skipLayout = false;
            File directory = new File(capturedUIFolder);
            if (!directory.exists()) {
                directory.mkdir();
            }
            // Sleep for 5 seconds to make sure the app is ready
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        String result = null;
            boolean isScrollView = chromosome.isContainsScrollView();
            if(isScrollView){ // for managing the scroll view
                 result = Utils.captureTheGeneratedUIScrollView(OwlEye.getCompileOutputPath(), newApkName, capturedUIFolder,emulatorName); // here the apk name for the chromosome
            }
            else{
                 result = Utils.captureTheGeneratedUI(OwlEye.getCompileOutputPath(), newApkName, capturedUIFolder,emulatorName); // here the apk name for the chromosome

            }
            if (result == null) {
                skipLayout = true;
                newLayoutFolder = null;
            } else {
                usc.edu.SALEM.util.Util.preprocessDynamicFolder(capturedUIFolder, capturedUIFolder + "/refined/");
                String dumpedFileLayout = capturedUIFolder + "/refined/";
                newLayoutFolder = dumpedFileLayout;
                //String chromosomedChrawled = newLayoutFolder.split("/refined")[0];
                chromosome.setCrawledPath(capturedUIFolder);
                Logger.debug("capturedUIFolder = " + capturedUIFolder);
            }

        FitnessFunction ff = new FitnessFunction();
        ff.calculateFitnessScore(chromosome, newLayoutFolder, false);
        Logger.debug("*** ======================================================== **********");
        Logger.debug("*** Running :" + chromosome.getChromosomeIdentifier() + "|| from: " + chromosome.getOriginTrace());
        Logger.debug("Genes: " + chromosome.getGenes());
        Logger.debug("Fitness function: " + ff.getFitnessScoreSummary());
        Logger.debug("*** ======================================================== **********");

            //System.exit(1);
        }

}
