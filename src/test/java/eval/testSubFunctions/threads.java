//package eval.clustering;
//
//
//import mfix.Constants;
//import mfix.mFix;
//
//import java.util.ArrayList;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import static mfix.util.Util.runCommand;
//
//public class threads {
//    private static final int MYTHREADS = 30;
//    public  static ArrayList<String[]> results= new ArrayList<>();
//    public static void main(String args[]) throws Exception {
//
//        ExecutorService executor = Executors.newFixedThreadPool(MYTHREADS);
//
//
//        String []apks = {"com.hcsc.android.providerfindertx",
//                "com.ihg.apps.android",
//                "com.playstation.mobilemessenger",
//                "com.podbean.app.podcast"};
//        String basepath= "/home/testing/AppSet/accessibility/TT_scripts/apks_folder/testThreads";
//        String outputPath= "/home/testing/AppSet/accessibility/TT_scripts/apks_folder/testThreads/compiled";
//        for (int i = 0; i < hostList.length; i++) {
//
//            String apk = hostList[i];
//            Runnable worker = new MyRunnable(basepath+"/"+apk,outputPath);
//            executor.execute(worker);
//        }
//        executor.shutdown();
//        // Wait until all threads are finish
//        while (!executor.isTerminated()) {
//
//        }
//        System.out.println("\nFinished all threads");
//    }
//
//    public static class MyRunnable implements Runnable {
//        private final String apk_path;
//        private final String outputPath;
//
//        MyRunnable(String url,String output) {
//
//            this.apk_path = url;
//            this.outputPath = output;
//        }
//
//        @Override
//        public void run() {
//
//            String result = "";
//            int code = 200;
//            try {
//
//
//
//                String signature = Constants.COMPILE_SIGNATURE_KEY;
//                String outputApk = mFix.getCopiedApkFileName() + "_" + "thread" + ".apk";
////        String apktool_command = "apktool -f b " + originalStaticFiles + "/" + mFix.getApkName() + " -o " + outputPath + "/" + outputApk;
//                String apktool_command = "apktool -f b " + this.apk_path + " -o " + this.outputPath + "/" + outputApk;
//
//                String signature_command = "jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore " + signature + " -storepass android "
//                        + this.outputPath + "/" + outputApk + " alias_name";
//                String cmd = apktool_command;
//                System.out.println("Running compile");
//                String apkPath = "";
//                try {
//                    runCommand(cmd, null, null);
//                    System.out.println("Running signature");
//                    runCommand(signature_command, null, null);
//                    apkPath = outputPath + "/" + outputApk;
//                } catch (Exception IOException) {
//                    System.out.println("error in compiling or signing the app");
//                    System.exit(0);
//                }
//                String[] res = {outputApk, apkPath};
//                threads.results.add(res);
//
//
//
//
//
//
//
//
//            } catch (Exception e) {
//
//
//            }
//            System.out.println(apk_path + "\t\tStatus: Compiled"  );
//        }
//    }
//}