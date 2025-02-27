package usc.edu.SALEM.util;

import java.awt.Color;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.net.ssl.HttpsURLConnection;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
//import GA.GAChromosome;
//import GA.GAGene;
//import WebDriverSingleton.Browser;
//import domTree.HtmlDomTree;
//import fitness.UsabilityScoreAPIParser;

//import org.apache.commons.io.FileUtils;
//import TTFix.WriteToDecompiled;
import com.google.common.io.Files;
import org.tinylog.Logger;
import usc.edu.OwlEye.OwlConstants;
import usc.edu.OwlEye.OwlEye;
import usc.edu.layoutgraph.edge.NeighborEdge;
import usc.edu.layoutgraph.node.LayoutNode;
import usc.edu.SALEM.fitness.TIssue;
import usc.edu.SALEM.GA.GAChromosome;
import usc.edu.SALEM.GA.GAGene;
import usc.edu.SALEM.GA.GASearch;
import usc.edu.SALEM.VHTree.*;
import gatech.xpert.dom.DomNode;
import usc.edu.SALEM.fitness.FitnessFunction;
import usc.edu.SALEM.fitness.AccessibilityScannerResults;
import usc.edu.SALEM.SALEM;
//import mfix.merge.Merger_TT;
import usc.edu.SALEM.VHTree.XMLUtils;
import usc.edu.SALEM.Constants;
import usc.edu.SALEM.segmentation.InterSegmentEdge;
import usc.edu.SALEM.segmentation.Segment;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import tech.tablesaw.api.Table;
import uiautomator.UiNode;
import usc.edu.SALEM.VHTree.*;

import static java.lang.Math.abs;
//import org.apache.logging.log4j.Logger;


public class Util {
    private static Map<String, StaticLayoutUtil> staticLayoutFilesCache;  // path to the file and the read util
    private static Map<String, String> elementPropValueCache;    // <xpath#prop, val>
    private static ArrayList<String> currentIssues;
    private static String currentApkUnderFix;  // to check if cached layout need to be reset

    public static Map<String, String> getElementPropValueCache() {
        return elementPropValueCache;
    }

    public static void setElementPropValueCache(Map<String, String> elementPropValueCache) {
        Util.elementPropValueCache = elementPropValueCache;
    }

    public static List<Double> getNumbersFromString(String string) {
        List<Double> numbers = new ArrayList<Double>();

        // Pattern p = Pattern.compile("(\\d+(?:\\.\\d+)?)"); //Ali: mfix original
        Pattern p = Pattern.compile("(\\-?\\d+(?:\\.\\d+)?)"); // Ali: I added to include (-)
        Matcher m = p.matcher(string);
        while (m.find()) {
            numbers.add(Double.valueOf(m.group()));
        }
        return numbers;
    }

    public static String getUnitFromStringValue(String string) {
        Pattern p = Pattern.compile("[a-zA-Z%]+");
        Matcher m = p.matcher(string);
        String returnValue = "";
        while (m.find()) {
            returnValue = m.group();
        }
        return returnValue;
    }

    public static double convertNanosecondsToSeconds(long time) {
        return (double) time / 1000000000.0;
    }

    public static void addAttributeToElement(DomNode e, String cssProperty,String value){

        String xpath= e.getxPath();
//        e.setAttr(Constants.PROPERTY_TO_FULL_ANDROID_NAME.get(cssProperty),
//                value);
        e.setAttr(cssProperty,
                value);
        elementPropValueCache.put(xpath + "#" + cssProperty, value);



    }
    public static void addAttributeToElement(String xpath, String cssProperty,String value){

        Node<DomNode> e = XMLUtils.searchVHTreeByXpath(xpath, XMLUtils.root);
//        e.getData().setAttr(Constants.PROPERTY_TO_FULL_ANDROID_NAME.get(cssProperty),
//                value);
        e.getData().setAttr(cssProperty,
                value);
        elementPropValueCache.put(xpath + "#" + cssProperty, value);



    }
    public static String getValueFromElement(String xpath, String cssProperty) {
        if (elementPropValueCache.containsKey(xpath + "#" + cssProperty)) {
            return elementPropValueCache.get(xpath + "#" + cssProperty);
        }

        XMLUtils xml = XMLUtils.getInstance(SALEM.getCurrentActivityMergedFilePath());
        Node<DomNode> e = XMLUtils.searchVHTreeByXpath(xpath, XMLUtils.root);
        String property = Constants.SIZE_SPACE_ATTRIBUTES.get(cssProperty);

        String val = e.getData().getAttr(property);
        if (val == null) {
            return null;
        }
        if (val.equalsIgnoreCase("match_parent") || val.equalsIgnoreCase("fill_parent")) {
//			if(cssProperty.equalsIgnoreCase("width"))
//			{
            val = "-1";
            //	}
        } else if (val.equalsIgnoreCase("wrap_content")) {
            val = "-2";
        } else if (!val.matches(".*\\d+.*")) {
            val = 1000 + "dp";

        }


        elementPropValueCache.put(xpath + "#" + cssProperty, val);

        return val;
    }

    public static String getDynamicValueInDDP(DomNode node, String cssProperty) {

        String value = null;
        if (cssProperty.equalsIgnoreCase("height") || cssProperty.equalsIgnoreCase(Constants.MAIN_PROPERTY_TO_MIN_PROPERTY.get("height"))) {
            value = String.valueOf(node.height / Constants.PHONE_DENSITY);
        } else if (cssProperty.equalsIgnoreCase("width") || cssProperty.equalsIgnoreCase(Constants.MAIN_PROPERTY_TO_MIN_PROPERTY.get("width"))) {
            value = String.valueOf(node.width / Constants.PHONE_DENSITY);
        }
        return value;

    }

    public static String getDynamicValueFromElement(DomNode node, String cssProperty) {

        String value = null;
        if (cssProperty.equalsIgnoreCase("height")) {
            value = String.valueOf(node.height / Constants.PHONE_DENSITY);
        } else if (cssProperty.equalsIgnoreCase("width")) {
            value = String.valueOf(node.width / Constants.PHONE_DENSITY);
        }
        return value;

    }

    public static String getXpathFromDGkey(String dgKey) {
        /*** dependancy graph keys are in the format: (xpath#property) so we need to sepearate that to get the actual xpath ***/
        String[] x = dgKey.split("#");
        return x[0];

    }

    public static double convertValueToDP(double value) {
        return value / Constants.PHONE_DENSITY;

    }

    public static String getDynamicValueFromElement(String xpath, String cssProperty) {
        XMLUtils xml = XMLUtils.getInstance(SALEM.getCurrentActivityMergedFilePath());
        Node<DomNode> e = XMLUtils.searchVHTreeByXpath(xpath, XMLUtils.root);
        String value = null;
        if (cssProperty.equalsIgnoreCase("height")) {
            value = String.valueOf(e.getData().height / Constants.PHONE_DENSITY);
        } else if (cssProperty.equalsIgnoreCase("width")) {
            value = String.valueOf(e.getData().width / Constants.PHONE_DENSITY);
        }
        return value;

    }


    // if we have the node element already so we do not need to search xpath
    public static String getValueFromElement(DomNode node, String cssProperty) {
        String xpath = node.getxPath();
        if (elementPropValueCache.containsKey(xpath + "#" + cssProperty)) {
            return elementPropValueCache.get(xpath + "#" + cssProperty);
        }

        String property = Constants.SIZE_SPACE_ATTRIBUTES.get(cssProperty);
        String val = node.getAttr(property);
        if (val == null) {
            return null;
        }
        if (val.equalsIgnoreCase("match_parent") || val.equalsIgnoreCase("fill_parent")) {
//			if(cssProperty.equalsIgnoreCase("width"))
//			{
            val = "-1";
            //	}
        } else if (val.equalsIgnoreCase("wrap_content")) {
            val = "-2";
        }

        elementPropValueCache.put(xpath + "#" + cssProperty, val);
        return val;
    }

    public static double getRandomDoubleValueInRange(double min, double max) {
        Random r = new Random();
        return min + (max - min) * r.nextDouble();
    }

    public static int getRandomIntValueInRange(int min, int max) {
        if (max - min == 0)
            return 0;

        Random generator = new Random();
        return generator.nextInt(max - min) + min;
    }

    public static double getWeightedAverage(double value1, double value2, double weight) {
        return Math.round((weight * value1) + ((1 - weight) * value2));
    }

    public static String getSanitizedString(String input) {
        return StringEscapeUtils.unescapeHtml4(input);
    }

    public static double round(double value) {
        int places = 2;
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static Color hex2Rgb(String colorStr) {
        return new Color(Integer.valueOf(colorStr.substring(1, 3), 16), Integer.valueOf(colorStr.substring(3, 5), 16), Integer.valueOf(colorStr.substring(5, 7), 16));
    }

    public static Color getRandomColor(List<String> visitedColors) {
        Random rand = new Random();
        int cnt = 0;
        while (cnt < 50) {
            int r = rand.nextInt(255);
            int g = rand.nextInt(255);
            int b = rand.nextInt(255);
            Color randomColor = new Color(r, g, b);
            String color = String.format("#%02x%02x%02x", r, g, b);
            if (!visitedColors.contains(color)) {
                visitedColors.add(color);
                return randomColor;
            }
            cnt++;
        }
        return hex2Rgb(visitedColors.get(0));
    }

    public static double getEuclideanDistanceBetweenPoints(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }


    public static String getProcessOutput(String command) throws IOException, InterruptedException {
        String[] c = command.split(" ");
        ProcessBuilder builder = new ProcessBuilder(c);
        Process p = builder.start();
        p.waitFor();
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = input.readLine()) != null) {
            sb.append(line);
            sb.append(System.getProperty("line.separator"));
        }
        String result = sb.toString();
        input.close();

        return result;
    }

    public static String getAPIOutputForPostRequest(String httpsURL, String urlParameter) throws IOException {
        //String json = "{\"url\":\"" + urlParameter + "\",\"requestScreenshot:true\"}";
        String json = "{\"requestScreenshot\": true, \"url\": \"" + urlParameter + "\"}";

        URL myurl = new URL(httpsURL);
        HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setDoInput(true);

        OutputStream os = con.getOutputStream();
        os.write(json.getBytes(StandardCharsets.UTF_8));
        os.close();

        InputStream ins = con.getInputStream();
        InputStreamReader isr = new InputStreamReader(ins);
        BufferedReader in = new BufferedReader(isr);

        String inputLine;
        String result = "";
        while ((inputLine = in.readLine()) != null) {
            result = result + inputLine;
        }
        in.close();

        return result;
    }

    public static String getAPIOutputForGetRequest(String httpsURL) throws IOException {
        URL myurl = new URL(httpsURL);
        HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
        InputStream ins = con.getInputStream();
        InputStreamReader isr = new InputStreamReader(ins);
        BufferedReader in = new BufferedReader(isr);

        String inputLine;
        String result = "";
        while ((inputLine = in.readLine()) != null) {
            result = result + inputLine;
        }
        in.close();

        return result;
    }

    public static double getGaussianValue(double mean, double min, double max) {
        double stddev = (max - min) / 6d;
        Random r = new Random();
        double x1 = r.nextDouble();
        double x2 = r.nextDouble();

        if (x1 == 0)
            x1 = 1;
        if (x2 == 0)
            x2 = 1;

        double y1 = Math.sqrt(-2.0 * Math.log(x1)) * Math.cos(2.0 * Math.PI * x2);
        double val = y1 * stddev + mean;

        if (val >= max)
            return max;
        if (val <= min)
            return min;
       val= Math.round(val);
        return val;
    }

    // finding input values for guassian distribution based on issue type

    public static List<Double> generateGaussianInputs(String issue) {
        // list of inputs in this order {mean, min value, max value}
        List<Double> vals = new ArrayList<>();
        double googleVal = 0;
        String str = "";
        if (issue.equals(Constants.TOUCH_TARGET_SPACE_ISSUE)) {

            str = Constants.ACCESSIBILITY_SUGGESTED_VALUES.get(Constants.TOUCH_TARGET_SPACE_ISSUE).get("minSpace");
            googleVal = Util.getNumbersFromString(str).get(0);
            vals.add(googleVal);
            vals.add(googleVal - Constants.GAUSSUIAN_MEAN_DIFF_MARGIN);
            vals.add(googleVal + Constants.GAUSSUIAN_MEAN_DIFF_MARGIN);

        } else if (issue.equals(Constants.TOUCH_TARGET_HEIGHT_ISSUE)) {

            str = Constants.ACCESSIBILITY_SUGGESTED_VALUES.get(Constants.TOUCH_TARGET_HEIGHT_ISSUE).get("height");
            googleVal = Util.getNumbersFromString(str).get(0);
            vals.add(googleVal);
            vals.add(googleVal - Constants.GAUSSUIAN_MEAN_DIFF_MARGIN);
            vals.add(googleVal + Constants.GAUSSUIAN_MEAN_DIFF_MARGIN);

        } else if (issue.equals(Constants.TOUCH_TARGET_WIDTH_ISSUE)) {

            str = Constants.ACCESSIBILITY_SUGGESTED_VALUES.get(Constants.TOUCH_TARGET_WIDTH_ISSUE).get("width");
            googleVal = Util.getNumbersFromString(str).get(0);
            vals.add(googleVal);
            vals.add(googleVal - Constants.GAUSSUIAN_MEAN_DIFF_MARGIN);
            vals.add(googleVal + Constants.GAUSSUIAN_MEAN_DIFF_MARGIN);

        } else if (issue.equals(Constants.TOUCH_TARGET_BOTH_ISSUE)) {

            str = Constants.ACCESSIBILITY_SUGGESTED_VALUES.get(Constants.TOUCH_TARGET_BOTH_ISSUE).get("width");
            googleVal = Util.getNumbersFromString(str).get(0);
            vals.add(googleVal);
            vals.add(googleVal - Constants.GAUSSUIAN_MEAN_DIFF_MARGIN);
            vals.add(googleVal + Constants.GAUSSUIAN_MEAN_DIFF_MARGIN);

        } else if (issue.equals(Constants.TOUCH_TARGET_SIZE_ISSUE)) {
            str = Constants.ACCESSIBILITY_SUGGESTED_VALUES.get(Constants.TOUCH_TARGET_SIZE_ISSUE).get("height");
            googleVal = Util.getNumbersFromString(str).get(0);

            double mean = googleVal + 14;
            vals.add(mean);
            vals.add(mean - 15);
            vals.add(mean + 15);
        } else if (issue.equals("PARENT_INCREASE")) {
            str = Constants.ACCESSIBILITY_SUGGESTED_VALUES.get("PARENT_INCREASE").get("height");
            googleVal = Util.getNumbersFromString(str).get(0);

            double mean = googleVal + 0.2;
            vals.add(mean);
            vals.add(mean - 0.2);
            vals.add(mean + 0.2);
        }
        else if (issue.equals("TEXT_INCREASE")) {
            str = Constants.ACCESSIBILITY_SUGGESTED_VALUES.get("TEXT_INCREASE").get("textSize");
            googleVal = Util.getNumbersFromString(str).get(0);

            double mean = googleVal + 0.1;
            vals.add(mean);

            vals.add(mean - 0.1);
            vals.add(mean + 0.1);
        }
        return vals;
    }

    public static String getSegmentIssueId(int segmentId, String issue) {
        return "S" + segmentId + "_" + issue;
    }

    public static int getSegmentIdFromSegmentIssueId(String segmentIssueId) {
        String s = segmentIssueId.replace("S", "").split("_")[0];
        return Integer.parseInt(s);
    }


    public static String getFormattedTimestamp() {
        String pattern = "MM-dd-yyyy-hh-mm-ss-a";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String timestamp = format.format(new Date());
        return timestamp;
    }

    public static boolean isElementLayout(DomNode e) {
        if (e.isClickable() || e.getChildren() == null || e.getChildren().size() == 0) {
            return false;
        }
        if (e.getTagName() != null) {
            for (String key :
                    Constants.LAYOUT_VIEWGROUP_MAP.keySet()) {
                if (e.getTagName().contains(key)) {
                    return true;
                }
            }
        }
        return false;

    }

    public static boolean isElementVisible(DomNode e) {

        String visible = e.getAttr("visible");

        if (visible != null) {
            boolean isVisible = Boolean.parseBoolean(visible);
            return isVisible;
        }
        //Todo: implement whne widht or height is zero

        return true;
    }
    public static boolean isElementLongClickable(DomNode e) {
        boolean isLongClickable = false;
        String longClickable = e.getAttr("long-clickable");
        if (longClickable != null) {
            isLongClickable = Boolean.parseBoolean(longClickable);
            return isLongClickable;
        }
    return isLongClickable;
    }

    public static boolean isElementCheckable(DomNode e) {
        boolean isCheckable = false;
        String checkable = e.getAttr("checkable");
        if (checkable != null) {
            isCheckable = Boolean.parseBoolean(checkable);
            return isCheckable;
        }
        return isCheckable;
    }
    public static boolean isElementScrollable(DomNode e) {
        boolean isElementScrollable = false;
        String scrollable = e.getAttr("scrollable");
        if (scrollable != null) {
            isElementScrollable = Boolean.parseBoolean(scrollable);
            return isElementScrollable;
        }
        return isElementScrollable;
    }


    public static boolean isElementClickable(DomNode e) {
        boolean isClickable = false;
        boolean isLongClickable = false;
        String clickable = e.getAttr("clickable");
        String longClickable = e.getAttr("long-clickable");
        if (clickable != null) {
            isClickable = Boolean.parseBoolean(clickable);
            if (isClickable) {
                return true;
            }
        }

        if (longClickable != null) {
            isLongClickable = Boolean.parseBoolean(longClickable);
            return isLongClickable;
        }
        return false;
    }

    public static String[] compileApp(String apkSuffix, String originalStaticFiles, String outputPath) {
        String signature = LoadConfig.getConfig_data().get("ANDROID_SIGNATURE".toLowerCase());
        String outputApk = OwlEye.getOriginalApkName() + "_" + apkSuffix + ".apk";
//        String apktool_command = "apktool -f b " + originalStaticFiles + "/" + mFix.getApkName() + " -o " + outputPath + "/" + outputApk;

       // String apktool_command = "apktool -f b " + originalStaticFiles + " -o " + outputPath + "/" + outputApk;
        String apktool_command = "apktool -f b --use-aapt2 " + originalStaticFiles + " -o " + outputPath + "/" + outputApk;

//        String signature_command = "jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore " + signature + " -storepass android "
//                + outputPath + "/" + outputApk + " alias_name";
        String signature_command ="jarsigner -verbose -keystore "+signature+" -storepass android -keypass android "+ outputPath + "/" + outputApk + " androiddebugkey";
        String cmd = apktool_command;
        Logger.debug("Running compile");
        String apkPath = "";
        try {
            runCommand(cmd, null, null);
            Logger.debug("Running signature");
            runCommand(signature_command, null, null);
            apkPath = outputPath + "/" + outputApk;
        } catch (Exception IOException) {
            Logger.error("error in compiling or signing the app");
            System.exit(0);
        }
        String[] res = {outputApk, apkPath};
        return res;  // The full path to the apk folder
    }



//    public void givenPythonScript_whenPythonProcessInvoked_thenSuccess(String cmd,String directory) throws Exception {
//        ProcessBuilder processBuilder = new ProcessBuilder("python", cmd);
//        processBuilder.redirectErrorStream(true);
//
//        Process process = processBuilder.start();
//        InputStream results = process.getInputStream();
//
//     System.out.println(results);
//
//        int exitCode = process.waitFor();
//        System.out.println("Exit code: " + exitCode);
//    }
    public static String runCommand(String cmd, String directory, String commandType) throws IOException {
        String[] act = new String[0];
        final Process p;
        if (directory == null) {
            // No need to set a directory
//            if(!cmd.contains("adb")){
           //     if (OwlEye.debugCompile) {
              //      Logger.debug("ADB CMD: "+ cmd);
        //        }
        //}
            p = Runtime.getRuntime().exec(cmd);
//            executeProcess(p);
        } else {
//            p=Runtime.getRuntime().exec("cd " + directory + " && " + cmd);
            p = Runtime.getRuntime().exec(cmd, null, new File(directory));
            //executeProcess(p);
        }


        if (commandType != null) {
            act = executeProcess(p, commandType);
            return act[0];
        } else {
            executeProcess(p);
        }
        return null;
    }

    private static void executeProcess(Process p) {
        Runnable consumeIn = new Runnable() {
            public void run() {
                InputStream in = p.getInputStream();
                InputStreamReader isr = null;
                isr = new InputStreamReader(in, StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
                String line;
                try {
                    while ((line = br.readLine()) != null) {
                        //System.out.println("inStream: " + line);
                        Logger.trace("inStream: " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Runnable consumeErr = new Runnable() {
            public void run() {
                InputStream in = p.getErrorStream();
                InputStreamReader isr = null;
                isr = new InputStreamReader(in, StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
                String line;
                try {
                    while ((line = br.readLine()) != null) {
                        Logger.error("errStream: " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(consumeIn).start();
        new Thread(consumeErr).start();

        try {
            //System.out.println("waiting for fitness function");
            p.waitFor();
            //System.out.println("fitness function complete");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    public static String[] executeProcess(Process p, String commandType) {
        /*** When we need to return string result from the command | mainly activity Name ***/
        currentIssues = new ArrayList<>();
        if(commandType.equals("text_cutoff")){
         Logger.trace("text_cutoff");

        }
        final String[] result = {null};
        Runnable consumeIn = new Runnable() {

            public void run() {
                String returnedResults = null;
                InputStream in = p.getInputStream();
                InputStreamReader isr = null;
                isr = new InputStreamReader(in, StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
                String line;
                int issueLineCount = 0;
                String issueInfo = "";
                try {
                    while ((line = br.readLine()) != null) {
                       Logger.debug("inStream: " + line);
                        if (commandType.equalsIgnoreCase("grepActivityName")) {
                            if (line.toLowerCase().contains("mcurrentfocus")) {
                                returnedResults = line;
                                result[0] = line;
                                break;
                            }
                        }
                        else if (commandType.equalsIgnoreCase("screen_res")) {
                            Util.setScreenEdge(line);
                        }
                        else if(commandType.equalsIgnoreCase("text_cutoff")){
                            Logger.trace("text_cutoff RESULTS: "+line);
                            if (line.contains("TextScore:")) {
                                line=line.replace("TextScore: ","");
                                String[] split = line.split(" ");
                                if(split.length<=1){
                                    returnedResults =null;
                                }
                                else{
                                    returnedResults = split[0]+"-"+split[1];
                                }
//                                returnedResults = split[2];
                                result[0] = returnedResults;
                            }
                        }
                        else if(commandType.equalsIgnoreCase("navigate_to_state")){
                            if (line.toLowerCase().contains("finally block for")) {
                                 String[] split = line.split("-");
                                if(split.length<=1){
                                    returnedResults ="False";
                                }
                                else{
                                 returnedResults = split[1];
                                result[0] = returnedResults;}
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };


        Runnable consumeErr = new Runnable() {
            public void run() {
                InputStream in = p.getErrorStream();
                InputStreamReader isr = null;
                isr = new InputStreamReader(in, StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
//                String line;
//                try {
//                    while ((line = br.readLine()) != null) {
//                        System.out.println("errStream: " + line);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(consumeIn).start();
        new Thread(consumeErr).start();

        try {
            //System.out.println("waiting for fitness function");
            p.waitFor();
            //System.out.println("fitness function complete");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String copyAppFolder(String sourceFolder, String destinationFolder, String apkName, String subjectID) {

        String appDecompiledFolder = sourceFolder + File.separator + apkName;
        String appDecompiledDestination = destinationFolder  + File.separator + subjectID ;//File.separator + current_run_identifier;


      createFolder(appDecompiledDestination);

        try {
            // Copy source directory into destination directory
            // including its child directories and files. When
            // the destination directory is not exists it will
            // be created. This copy process also preserve the
            // date information of the file.
            File srcDir = new File(appDecompiledFolder);
            File destDir = new File(appDecompiledDestination);
            FileUtils.copyDirectory(srcDir, destDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return appDecompiledDestination;
    }

    public static String copyAppFolder(File sourceFolder, File destinationFolder, String suffix) {
        String sourceFileName = sourceFolder.getName();
        String newFileName = sourceFileName + "_" + suffix;
        String sourceFilePath = sourceFolder.getPath();
        String destFilePath = destinationFolder.getPath() + File.separator + newFileName;
        copyAppFolder(sourceFilePath, destFilePath, null);
        return destFilePath;
    }

    public static String copyAppFolder(String sourceFolder, String destiniationFolder, String suffix) {

        String appDecompiledFolder = sourceFolder;
        String appDecombiledDestination = destiniationFolder;
        if (suffix != null) {
            appDecombiledDestination = appDecombiledDestination + File.separator + suffix;
        }


      createFolder(appDecombiledDestination);

        try {
            // Copy source directory into destination directory
            // including its child directories and files. When
            // the destination directory is not exists it will
            // be created. This copy process also preserve the
            // date information of the file.
            File srcDir = new File(appDecompiledFolder);
            File destDir = new File(appDecombiledDestination);
            FileUtils.copyDirectory(srcDir, destDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {

        return appDecombiledDestination;}
    }

    public static void createFolder(String folderName) {
        File directory = new File(folderName);
        if (!directory.exists()) {
            directory.mkdirs();
            // If you require it to make the entire directory path including parents,
            // use directory.mkdirs(); here instead.
        }
    }
    public static void readAccessibilityIssuesFile(String filePath) {
        ReadIssues read = new ReadIssues(filePath);

        HashMap<String, Table> activityIssues = read.parseFile();

    }

    public static void moveAPKtoFolder(String compiledAPKPath, String destiniationFolder) {
        String sourceFolder = compiledAPKPath;
        destiniationFolder = destiniationFolder;


        File destDir = new File(destiniationFolder);
        if (!destDir.exists()) {
            destDir.mkdirs();
            // If you require it to make the entire directory path including parents,
            // use directory.mkdirs(); here instead.
        }

        try {
            // Copy source directory into destination directory
            // including its child directories and files. When
            // the destination directory is not exists it will
            // be created. This copy process also preserve the
            // date information of the file.
            File apkFile = new File(sourceFolder);
//            File destDir = new File(destiniationFolder);
            FileUtils.copyFileToDirectory(apkFile, destDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String WriteChromosomeChangesToFileSegmentRelationGraph(GAChromosome chromosome, String outputPath) {
        /*** Given a chronmosome, we (1) first copy the original apk to a new folder, (2) decide the elements to be changed for each gene and then (3) apply the change to the static layout files
         * (4) return true if that went well ***/

        //the folder will be the apk file + _ chromosome ID
        String suffix = chromosome.getChromosomeIdentifier();
        //Copy to the same folder (so get the parent) but with the chromosome id
        File dest = new File(SALEM.getOriginalDecompiled()).getParentFile();
        File source = new File(SALEM.getOriginalDecompiled());

        String chromosomeFolderPath = copyAppFolder(source, dest, suffix);
        List<Element> ChromosomeElementsToChange = new ArrayList<>();  // elementsToChange for all elements
        HashMap<String, ArrayList<MatchParentElement>> matchParentMap = new HashMap();    // key: dependant node from dependacny graph , arraylist: list of matchParentelements that has property  with -1

        // Now we have the folder of the chromosme so we write the changes of the chromosome
        HashMap<String, GAGene> parentsGene = new HashMap<>();
        HashMap<String, GAGene> textGenes=  new HashMap<>();
        for (GAGene gene : chromosome.getGenes()) {
            if (Constants.PARENTS_INCREASE_APPROACH.equalsIgnoreCase("ratio")) {
                if (gene.getIssueType().equalsIgnoreCase("PARENT_INCREASE")) {
                    parentsGene.put(gene.getXpaths().get(0), gene);
                    continue;
                }
            }

            if(SALEM.HandleTextField) { // text increase enabled
                if (gene.getIssueType().equalsIgnoreCase("TEXT_INCREASE")) {
                    textGenes.put("TEXT_INCREASE", gene);
                    continue;
                }
            }
            List<Element> geneElementsToChange = null;
//            System.out.println("\nApplying values for gene " + gene + " | for chromosome :" + gene.getSegmentIssueId());

            String newValue = gene.getValue(); // This is the new Value to apply

            if (gene.getIssueType().equalsIgnoreCase(Constants.TOUCH_TARGET_SPACE_ISSUE)) { // means it is a gene for spacing issue
                DependencyGraph dgSpace = SALEM.getSegmentToDG().get(gene.getSegmentIssueId());
                if (SALEM.getSegmentToDG().get(gene.getSegmentIssueId()) != null) {
                    geneElementsToChange = SALEM.getSegmentToDG().get(gene.getSegmentIssueId()).
                            getElementsToChange(gene.getCssProperty(), newValue, null, gene.getIssueType(), null); // xpath 0: smallest height , xpath 1: smallest width
                }
            }
            else { // means it is a gene for sizing issue
                if (gene.getXpaths().size() == 1) {
                    //    that means the same element has the smallest width and height
                    //ToDo: what should we do now?
                }
                SegmentRelationGraph s = SALEM.getSegmentToSG().get(gene.getSegmentIssueId());
                geneElementsToChange = SALEM.getSegmentToSG().get(gene.getSegmentIssueId()).
                        getElementsToChange(gene.getSegmentIssueId(), gene.getCssProperty(), newValue, gene.getIssueType(), gene.getXpaths(),
                                matchParentMap); // xpath 0: smallest height , xpath 1: smallest width

            }

            String patchID = chromosome.getChromosomeIdentifier();
            if (geneElementsToChange != null || geneElementsToChange.size() > 0) { // If no elements to change then just skip this

                addGeneElementsToChromosomeElements(geneElementsToChange, ChromosomeElementsToChange);

            }
        }
        String patchID = chromosome.getChromosomeIdentifier();
        if (ChromosomeElementsToChange != null || ChromosomeElementsToChange.size() > 0) { // If no elements to change then just skip this
            if (Constants.PARENTS_INCREASE_APPROACH.equalsIgnoreCase("ratio")) {
                addParentIncreaseGenesChanges(chromosome, ChromosomeElementsToChange, parentsGene, matchParentMap);
            } else {
                resolveMatchParentElements(matchParentMap, ChromosomeElementsToChange);
                addParentElementsChangesIfNeeded(ChromosomeElementsToChange);

            }
            if(SALEM.HandleTextField) {
                IncreasetextSizeTest(ChromosomeElementsToChange,textGenes);
            }
            // Once we finalize everything and get final list of chromosomeElements we now apply relatedProp for the element
            addRelatedPropertiesToElements(ChromosomeElementsToChange);
            addMatchParentElements(matchParentMap, ChromosomeElementsToChange);
           // IncreasetextSizeTest(ChromosomeElementsToChange);
            //IncreasetextSizeTest(ChromosomeElementsToChange);  // Increase FONT
            applyChangeToElements(ChromosomeElementsToChange, outputPath);

        }

        //Once all chromosome genes  are applied we then write them.
        writeMofidiedStaticFilesToFile();  // once we finalized the genes now we can write the updatet s the decompiled apl
        return chromosomeFolderPath;


    }

    private static void addParentIncreaseGenesChanges(GAChromosome chromosome, List<Element> chromosomeElementsToChange, HashMap<String, GAGene> parentsGene, HashMap<String, ArrayList<MatchParentElement>> matchParentMap) {
        DependencyGraph dgHeight = null;
        DependencyGraph dgWidth = null;
        TreeMap<String, List<DependentNode>> dgTreeMapHeight = null;
        TreeMap<String, List<DependentNode>> dgTreeMapWidth = null;
        dgHeight = SALEM.getSegmentToDG().get("S0_TouchTargetHeightIssue");
        dgWidth = SALEM.getSegmentToDG().get("S0_TouchTargetWidthIssue");

        if (dgHeight != null) {  // No dependency for height
            dgTreeMapHeight = dgHeight.getDependentNodesMap();
        }
        if (dgWidth != null) {  // No dependency for height
            dgTreeMapWidth = dgWidth.getDependentNodesMap();
        }
        HashMap<String, ArrayList<Double>> parentsChange = new HashMap<>();
        HashMap<String, ArrayList<Element>> parentElements = new HashMap<>();
        for (Element element : chromosomeElementsToChange
        ) {
            if (element.getCssProperty().equalsIgnoreCase("height") ||
                    element.getCssProperty().equalsIgnoreCase(Constants.MAIN_PROPERTY_TO_MIN_PROPERTY.get("height"))) {
                if (dgTreeMapHeight != null) {
                    List<DependentNode> dependentNodes = dgTreeMapHeight.get(element.getXpath() + "#" + "height");
                    if (dependentNodes != null)
                        for (DependentNode d : dependentNodes
                        ) {
                            if (!d.getXpath().equalsIgnoreCase(element.getXpath())) {  // Not relation to same element, dependant
                                if (parentsGene.containsKey(d.getXpath())) {
                                    if (parentsGene.get(d.getXpath()) != null) {
                                        GAGene gene = parentsGene.get(d.getXpath());
                                        if (gene.getCssProperty().equalsIgnoreCase("height")) {
                                            double initialParenIncreaseVal = getNumbersFromString(element.getValue()).get(0) / getNumbersFromString(gene.getOriginalValue()).get(0);
                                            if (!parentsChange.containsKey(gene.getXpaths().get(0))) {
                                                parentsChange.put(gene.getXpaths().get(0) + "#" + "height", new ArrayList<>());
                                            }
                                            parentsChange.get(gene.getXpaths().get(0) + "#" + "height").add(initialParenIncreaseVal);
                                          //addAllOfParentParentsUntilRoot(gene,dgTreeMapHeight,parentsGene,initialParenIncreaseVal,parentsChange);
                                        }
                                        if (!parentElements.containsKey(gene.getXpaths().get(0))) {
                                            parentElements.put(gene.getXpaths().get(0), new ArrayList<>());
                                        }
                                        parentElements.get(gene.getXpaths().get(0)).add(element);
                                    }
                                }
                            }
                        }
                }
            } else if (element.getCssProperty().equalsIgnoreCase("width") ||
                    element.getCssProperty().equalsIgnoreCase(Constants.MAIN_PROPERTY_TO_MIN_PROPERTY.get("width"))) {
                if (dgTreeMapWidth != null) {
                    List<DependentNode> depentNodes = dgTreeMapWidth.get(element.getXpath() + "#" + "width");
                    if (depentNodes != null)
                        for (DependentNode d : depentNodes
                        ) {
                            if (!d.getXpath().equalsIgnoreCase(element.getXpath())) {  // Not relation to same element, dependant

                                if (parentsGene.containsKey(d.getXpath())) {
                                    if (parentsGene.get(d.getXpath()) != null) {
                                        GAGene gene = parentsGene.get(d.getXpath());
                                        if (gene.getCssProperty().equalsIgnoreCase("width")) {
                                            double initialParenIncreaseVal = getNumbersFromString(element.getValue()).get(0) / getNumbersFromString(gene.getOriginalValue()).get(0);
                                            if (!parentsChange.containsKey(gene.getXpaths().get(0) + "#" + "width")) {
                                                parentsChange.put(gene.getXpaths().get(0) + "#" + "width", new ArrayList<>());
                                            }
                                            parentsChange.get(gene.getXpaths().get(0) + "#" + "width").add(initialParenIncreaseVal);
                                        }
                                        if (!parentElements.containsKey(gene.getXpaths().get(0))) {
                                            parentElements.put(gene.getXpaths().get(0), new ArrayList<>());
                                        }
                                        parentElements.get(gene.getXpaths().get(0)).add(element);
                                    }
                                }

                            }

                        }
                }
            }
        }

        boolean sameParent = false;
        for (String pr : parentsChange.keySet()
        ) {
            ArrayList<Double> changes = parentsChange.get(pr);
            //xpath#property
            double parentNewVal = Collections.max(changes);
            String pXpath = pr.split("#")[0];
            String parentChangeProp = pr.split("#")[1];
            Node<DomNode> node = XMLUtils.searchVHTreeByXpath(pXpath, XMLUtils.getRoot());

            if (node.getChildren().size() == 1) {
                Node<DomNode> DependentNode = node.getChildren().get(0);
                for (String e : parentElements.keySet()
                ) {
                    ArrayList<Element> c = parentElements.get(e);
                    for (Element el : c
                    ) {
                        if (DependentNode.getData().getxPath().equalsIgnoreCase(el.getXpath())) {
                            //only one node that depends on this parent so just inccrease the same
//                                    String dynPVal = getDynamicValueInDDP(node.getData(), "height");
//                                    String dynCVal = getDynamicValueInDDP(el.getNode().getData(), "height");
//                                    double dP=getNumbersFromString(dynPVal).get(0);;
//                                    double dc=getNumbersFromString(dynCVal).get(0);;
                            if (el.getCssProperty().equalsIgnoreCase("height")) {
                                double dynCh = getTotalExternalSizeForNode(el.getNode(), "height");
                                double dynPa = getTotalInternalSizeForNode(node, "height");
                                if (dynPa / dynCh > 0.94) {
                                    sameParent = true; // the parentSize is almost as same as paren
                                }
                            } else if (el.getCssProperty().equalsIgnoreCase("width")) {
                                double dynCh = getTotalExternalSizeForNode(el.getNode(), "width");
                                double dynPa = getTotalInternalSizeForNode(node, "width");
                                if (dynPa / dynCh > 0.94) {
                                    sameParent = true; // the parentSize is almost as same as paren
                                }
                            }
//                                    if(dc/dP>0.92){
//                                        sameParent=true; // the parentSize is almost as same as paren
//                                    }

                        }
                    }
                }

            }
            String dynVal = null;
            if (parentChangeProp.equalsIgnoreCase("height")) {
                dynVal = getDynamicValueInDDP(node.getData(), "height");
            } else if (parentChangeProp.equalsIgnoreCase("width")) {
                dynVal = getDynamicValueInDDP(node.getData(), "width");
            }

            double originalParentVal = getNumbersFromString(dynVal).get(0);
            double diff = parentNewVal - originalParentVal;
            if (sameParent) {
                double valueAfterCountingGene = originalParentVal + diff;
                String prop = parentsGene.get(pXpath).getCssProperty();
                String correctProp = SegmentRelationGraph.DecideTheCorrectValue(pXpath, prop);
                Element pe = new Element(pXpath, correctProp, String.valueOf(valueAfterCountingGene), true, null);
                chromosomeElementsToChange.add(pe);
            } else {
                double valueAfterCountingGene = originalParentVal + diff * getNumbersFromString(parentsGene.get(pXpath).getValue()).get(0); //Multiply diff by the gene percentage
                String prop = parentsGene.get(pXpath).getCssProperty();
                String correctProp = SegmentRelationGraph.DecideTheCorrectValue(pXpath, prop);
                Element pe = new Element(pXpath, correctProp, String.valueOf(valueAfterCountingGene), true, null);
                chromosomeElementsToChange.add(pe);
            }
        }
    }

    private static void addAllOfParentParentsUntilRoot(GAGene gene, TreeMap<String, List<DependentNode>> dgTreeMapHeight,
                                                       HashMap<String, GAGene> parentsGene,double inputInitialIncreaseVal,
                                                       HashMap<String, ArrayList<Double>> parentsChange) {
        /*** Iterate throught parent's height or widht dependent and add them to parents gene */


        String elementXpath = gene.getXpaths().get(0); // The parent we want to find its dependentParent
//        if (dgTreeMapHeight != null) {
//            List<DependentNode> dependentNodes = dgTreeMapHeight.get(elementXpath + "#" + "height");
//            if (dependentNodes != null) {
//                for (DependentNode d : dependentNodes
//                ) {
//                    if (!d.getXpath().equalsIgnoreCase(elementXpath)) {  // Not relation to same element, dependant
//                        if (parentsGene.containsKey(d.getXpath())) { // it is already part of the gene so no need to handle it here
//                            if (parentsGene.get(d.getXpath()) != null) {
//                                GAGene currGene = parentsGene.get(d.getXpath());
//                                if (currGene.getCssProperty().equalsIgnoreCase("height")) {
//                                    double newIncreaseVal = inputInitialIncreaseVal / getNumbersFromString(currGene.getOriginalValue()).get(0);
//                                    if (!parentsChange.containsKey(currGene.getXpaths().get(0))) {
//                                        parentsChange.put(currGene.getXpaths().get(0) + "#" + "height", new ArrayList<>());
//                                    }
//                                    parentsChange.get(currGene.getXpaths().get(0) + "#" + "height").add(newIncreaseVal);
//                                }
//                            }
//                        } else {
//                            // it is not in the added as gene then just add to the change with same increase reate as parent initial
//                            if (!parentsChange.containsKey(d.getXpath())) {
//                                parentsChange.put(d.getXpath() + "#" + "height", new ArrayList<>());
//                            }
//                            parentsChange.get(d.getXpath() + "#" + "height").add(inputInitialIncreaseVal);
//                        }
//
//                    }
//
//
//                }
//            }
//
//
//        } //end of if dgTreeMapHeight=null


        //               } //end for


        Queue<String> q = new LinkedList<String>();
        q.add(elementXpath);
//        if (root.getChildren() != null) {
//            for (Node<DomNode> child : root.getChildren()) {
//                q.add(child);
//            }
//        }
//
//        // process descendants of the root in a bread first fashion
        while (!q.isEmpty()) {
            String currentParentXpath = q.remove(); // start with first parent and handle all of its dependent parents

            if (dgTreeMapHeight != null) {
                List<DependentNode> dependentNodes = dgTreeMapHeight.get(currentParentXpath + "#" + "height");
                if (dependentNodes != null) {
                    for (DependentNode d : dependentNodes
                    ) {
                        if (!d.getXpath().equalsIgnoreCase(currentParentXpath)) {  // Not relation to same element, dependant
                            if (parentsGene.containsKey(d.getXpath())) { // it is already part of the gene so no need to handle it here
                                if (parentsGene.get(d.getXpath()) != null) {
                                    GAGene currGene = parentsGene.get(d.getXpath());
                                    if (currGene.getCssProperty().equalsIgnoreCase("height")) {
                                        double newIncreaseVal = inputInitialIncreaseVal / getNumbersFromString(currGene.getOriginalValue()).get(0);
                                        if (!parentsChange.containsKey(currGene.getXpaths().get(0))) {
                                            parentsChange.put(currGene.getXpaths().get(0) + "#" + "height", new ArrayList<>());
                                        }
                                        parentsChange.get(currGene.getXpaths().get(0) + "#" + "height").add(newIncreaseVal);
                                    }
                                }
                            } else {
                                // it is not in the added as gene then just add to the change with same increase reate as parent initial
                                // Actually we create a gene
                                if (!parentsGene.containsKey(d.getXpath())) {
                                    ArrayList<String> arrXpath = new ArrayList<>();
                                    arrXpath.add(d.getXpath());// only contains one element but kept it as array to match other gene implementation
                                    GAGene newGene = new GAGene();
                                    newGene.setXpaths(arrXpath);
                                    newGene.setCssProperty("height");
                                    newGene.setValue(String.valueOf(gene.getValue()));
                                    newGene.setOriginalValue(String.valueOf(gene.getValue()));
                                    newGene.setIssueType("PARENT_INCREASE");
                                    newGene.setImpactScore(1);
                                    newGene.setSegmentIssueId("0");


                                    parentsGene.put(d.getXpath(), newGene);
                                    double newIncrease = inputInitialIncreaseVal / getNumbersFromString(newGene.getOriginalValue()).get(0);
                                    if (!parentsChange.containsKey(d.getXpath())) {
                                        parentsChange.put(d.getXpath() + "#" + "height", new ArrayList<>());
                                    }
                                    parentsChange.get(d.getXpath() + "#" + "height").add(newIncrease);
                                }


                            }
                            q.add(d.getXpath()); // add the dependent so we can add its parent dependnets


                        }


                    } // for


                } //if (dependentNodes != null) {
            } //if (dgTreeMapHeight != null) {
        } //while (!q.isEmpty()) {
    }
    private static void IncreasetextSizeTest(List<Element> chromosomeElementsToChange, HashMap<String, GAGene> textGenes) {
        HashMap processedNodes= new HashMap<>();
        List<Element> textSizeIncrease = new ArrayList<>();
        for (Element element : chromosomeElementsToChange
        ) {
            if (processedNodes.containsKey(element.getXpath())){
                continue;  // we already handled that
            }
            String xpath = element.getXpath();
            Node<DomNode> node = element.getNode();
            if (node == null) {
                continue;
            }
            String textSize = node.getData().getAttr(Constants.SIZE_SPACE_ATTRIBUTES.get(Constants.TEXT_SIZE));

            if (textSize == null) { // not text
                continue;
            }

            HashMap<String, Element> propertyToChange = find_type_property_change_for_element(element.getXpath(), chromosomeElementsToChange);

            double textSizeVal = getNumbersFromString(textSize).get(0);
            processedNodes.put(xpath,propertyToChange);
            if (propertyToChange.size()>1){
                //both height and width

                //height
                Element heightElement = propertyToChange.get("height");
                Node<DomNode> heightNode = heightElement.getNode();
                if (heightNode == null) {
                    continue;
                }
                String dynamicHeight = getDynamicValueInDDP(heightNode.getData(), "height");
                double heightOrgVal = getNumbersFromString(dynamicHeight).get(0);
                double heightNewVal = getNumbersFromString(heightElement.getValue()).get(0);
                double diffInHeight = heightNewVal - heightOrgVal;
                double heightRatio=  heightNewVal /heightOrgVal;
                //width
                Element widthElement = propertyToChange.get("width");
                Node<DomNode> widthNode = widthElement.getNode();
                if (widthNode == null) {
                    continue;
                }
                String dynamicWidth = getDynamicValueInDDP(widthNode.getData(), "width");
                double widthOrgVal = getNumbersFromString(dynamicWidth).get(0);
                double widthNewVal = getNumbersFromString(widthElement.getValue()).get(0);
                double diffInWidth = widthNewVal - widthOrgVal;
                double widthRatio=  widthNewVal /widthOrgVal;

                if(heightRatio>1.1 && widthRatio>1.2) {
                    double minRatio= Double.min(heightRatio,widthRatio);
                    double ratio_above =minRatio-1;
                    ratio_above=ratio_above* getNumbersFromString(textGenes.get("TEXT_INCREASE").getValue()).get(0);
                    ratio_above=1+ratio_above;
                    double ratio =ratio_above;

                    double newTextVal = textSizeVal * (ratio);
                    Element dElement = new Element(node.getData().getxPath(), Constants.TEXT_SIZE, newTextVal + ".sp", false, node);
                    textSizeIncrease.add(dElement);
                }

            } else if(propertyToChange.containsKey("height")){
                // only height inrease

                //height
                Element heightElement = propertyToChange.get("height");
                Node<DomNode> heightNode = heightElement.getNode();
                if (heightNode == null) {
                    continue;
                }
                String dynamicHeight = getDynamicValueInDDP(heightNode.getData(), "height");
                double heightOrgVal = getNumbersFromString(dynamicHeight).get(0);
                double heightNewVal = getNumbersFromString(heightElement.getValue()).get(0);
                double diffInHeight = heightNewVal - heightOrgVal;

                if(diffInHeight>1.2) {
                    double heightRatio=  heightNewVal /heightOrgVal;
                    double ratio_above =heightRatio-1;
                    GAGene val= textGenes.get("TEXT_INCREASE");
                    ratio_above=ratio_above* getNumbersFromString(textGenes.get("TEXT_INCREASE").getValue()).get(0);
                    ratio_above=1+ratio_above;
                    double ratio =ratio_above;


                    double newTextVal = textSizeVal * (ratio);
                    Element dElement = new Element(node.getData().getxPath(), Constants.TEXT_SIZE, newTextVal + ".sp", false, node);
                    textSizeIncrease.add(dElement);
                }

            }


            }


        }
    private static void IncreasetextSizeTest(List<Element> chromosomeElementsToChange) {
        HashMap processedNodes= new HashMap<>();
        List<Element> textSizeIncrease = new ArrayList<>();
        for (Element element : chromosomeElementsToChange
        ) {
            if (processedNodes.containsKey(element.getXpath())){
                continue;  // we already handled that
            }
            String xpath = element.getXpath();
            Node<DomNode> node = element.getNode();
            if (node == null) {
                continue;
            }
            String textSize = node.getData().getAttr(Constants.SIZE_SPACE_ATTRIBUTES.get(Constants.TEXT_SIZE));

            if (textSize == null) { // not text
                continue;
            }

            HashMap<String, Element> propertyToChange = find_type_property_change_for_element(element.getXpath(), chromosomeElementsToChange);

            double textSizeVal = getNumbersFromString(textSize).get(0);
            processedNodes.put(xpath,propertyToChange);
            if (propertyToChange.size()>1){
                //both height and width

                //height
                Element heightElement = propertyToChange.get("height");
                Node<DomNode> heightNode = heightElement.getNode();
                if (heightNode == null) {
                    continue;
                }
                String dynamicHeight = getDynamicValueInDDP(heightNode.getData(), "height");
                double heightOrgVal = getNumbersFromString(dynamicHeight).get(0);
                double heightNewVal = getNumbersFromString(heightElement.getValue()).get(0);
                double diffInHeight = heightNewVal - heightOrgVal;
                double heightRatio=  heightNewVal /heightOrgVal;
                //width
                Element widthElement = propertyToChange.get("width");
                Node<DomNode> widthNode = widthElement.getNode();
                if (widthNode == null) {
                    continue;
                }
                String dynamicWidth = getDynamicValueInDDP(widthNode.getData(), "width");
                double widthOrgVal = getNumbersFromString(dynamicWidth).get(0);
                double widthNewVal = getNumbersFromString(widthElement.getValue()).get(0);
                double diffInWidth = widthNewVal - widthOrgVal;
                double widthRatio=  widthNewVal /widthOrgVal;

                if(heightRatio>1.1 && widthRatio>1.2) {
                    double minRatio= Double.min(heightRatio,widthRatio);
                    double ratio_above =minRatio-1;
                    ratio_above=ratio_above* 0.25 ;
                    ratio_above=1+ratio_above;
                    double ratio =ratio_above;
//                    double minDiff= Double.min(diffInWidth,diffInHeight);

//                    double newTextVal = textSizeVal + (minDiff*0.2);
                    if(ratio>1) {
                        double newTextVal = textSizeVal * (ratio);

                        Element dElement = new Element(node.getData().getxPath(), Constants.TEXT_SIZE, newTextVal + ".sp", false, node);
                        textSizeIncrease.add(dElement);
                    }
                }

            } else if(propertyToChange.containsKey("height")){
                // only height inrease

                //height
                Element heightElement = propertyToChange.get("height");
                Node<DomNode> heightNode = heightElement.getNode();
                if (heightNode == null) {
                    continue;
                }
                String dynamicHeight = getDynamicValueInDDP(heightNode.getData(), "height");
                double heightOrgVal = getNumbersFromString(dynamicHeight).get(0);
                double heightNewVal = getNumbersFromString(heightElement.getValue()).get(0);
                double diffInHeight = heightNewVal - heightOrgVal;

                if(diffInHeight>1.2) {
                    double heightRatio=  heightNewVal /heightOrgVal;
                    double ratio_above =heightRatio-1;
                    ratio_above=ratio_above* 0.5;
                    ratio_above=1+ratio_above;
                    double ratio =ratio_above;
                    double increaseAmount=0;
                    if(node.getData().getTagName().equalsIgnoreCase("TextView")&&
                    node.getData().width>900){
//                        increaseAmount = textSizeVal + (diffInHeight*0.2);
//                        double newTextVal = textSizeVal +increaseAmount;
//                        increaseAmount = textSizeVal *ratio;
                        if(ratio>1){
                        double newTextVal = textSizeVal *ratio;
                        Element dElement = new Element(node.getData().getxPath(), Constants.TEXT_SIZE, newTextVal + ".sp", false, node);
                        textSizeIncrease.add(dElement);
                        }
                    }
//                    else {
//                      //  increaseAmount = textSizeVal + (diffInHeight * 0.2);
//                    }

//                    double newTextVal = textSizeVal +increaseAmount;
//                    Element dElement = new Element(node.getData().getxPath(), Constants.TEXT_SIZE, newTextVal + ".sp", false, node);
//                    textSizeIncrease.add(dElement);
                }

            }


        }
        chromosomeElementsToChange.addAll(textSizeIncrease);


    }


    private static HashMap<String, Element> find_type_property_change_for_element(String xpath, List<Element> chromosomeElementsToChange) {
        HashMap<String,Element>  propertyToChange=new HashMap<>();
        for (Element element : chromosomeElementsToChange
        ) {
            if(!element.getXpath().equalsIgnoreCase(xpath)){
                continue;
            }
            if (element.getCssProperty().equalsIgnoreCase("height") ||
                    element.getCssProperty().equalsIgnoreCase(Constants.MAIN_PROPERTY_TO_MIN_PROPERTY.get("height"))) {
                propertyToChange.put("height",element);
            }
            if (element.getCssProperty().equalsIgnoreCase("width") ||
                    element.getCssProperty().equalsIgnoreCase(Constants.MAIN_PROPERTY_TO_MIN_PROPERTY.get("width"))) {
                propertyToChange.put("width",element);
            }
        }
        return propertyToChange;
    }

    private static void IncreasetextSizeTest0(List<Element> chromosomeElementsToChange) {

        List<Element> textSizeIncrease = new ArrayList<>();
        for (Element element : chromosomeElementsToChange
        ) {
            String xpath = element.getXpath();
            Node<DomNode> node = element.getNode();
            if(node==null){
                continue;
            }
            String textSize = node.getData().getAttr(Constants.SIZE_SPACE_ATTRIBUTES.get(Constants.TEXT_SIZE));

            if (textSize == null) { // not text
                continue;
            }
            if (element.getCssProperty().equalsIgnoreCase("height") ||
                    element.getCssProperty().equalsIgnoreCase(Constants.MAIN_PROPERTY_TO_MIN_PROPERTY.get("height"))) {
                String dynamicHeight = getDynamicValueInDDP(node.getData(), "height");
                double heightVal = getNumbersFromString(dynamicHeight).get(0);
                double heightNewVal = getNumbersFromString(element.getValue()).get(0);
                double diff = heightNewVal - heightVal;
                double textSizeVal = getNumbersFromString(textSize).get(0);
                double newTextVal = textSizeVal;
                if (Constants.TEXT_SIZE_APPROACH.equalsIgnoreCase("ratio")) {
//                    double ratio = textSizeVal / heightVal;
                    //newTextVal = heightNewVal * (ratio);
                    double ratio = heightNewVal / heightVal;
                    double ratio_above =ratio-1;
                    ratio_above=ratio_above/2;
                    ratio_above=1+ratio_above;
                    newTextVal = textSizeVal * (ratio_above);
                } else {
                    newTextVal = textSizeVal + (heightNewVal - heightVal)/2;
                }
                Element dElement = new Element(node.getData().getxPath(), Constants.TEXT_SIZE, newTextVal + ".sp", false, node);
                textSizeIncrease.add(dElement);
            }
            if (element.getCssProperty().equalsIgnoreCase("width") ||
                    element.getCssProperty().equalsIgnoreCase(Constants.MAIN_PROPERTY_TO_MIN_PROPERTY.get("width"))) {

            }
        }
        chromosomeElementsToChange.addAll(textSizeIncrease);

    }

    private static void addRelatedPropertiesToElements(List<Element> chromosomeElementsToChange) {
        DependencyGraph dgHeight = null;
        DependencyGraph dgWidth = null;
        TreeMap<String, List<DependentNode>> dgTreeMapHeight = null;
        TreeMap<String, List<DependentNode>> dgTreeMapWidth = null;
        dgHeight = SALEM.getSegmentToDG().get("S0_TouchTargetHeightIssue");
        dgWidth = SALEM.getSegmentToDG().get("S0_TouchTargetWidthIssue");

        if (dgHeight != null) {  // No dependency for height
            dgTreeMapHeight = dgHeight.getDependentNodesMap();
        }
        if (dgWidth != null) {  // No dependency for height
            dgTreeMapWidth = dgWidth.getDependentNodesMap();
        }

        List<Element> dependentRelatedNodes = new ArrayList<>();
        for (Element element : chromosomeElementsToChange
        ) {
            String currXpath = element.getXpath();
            if (element.getCssProperty().equalsIgnoreCase("height") ||
                    element.getCssProperty().equalsIgnoreCase(Constants.MAIN_PROPERTY_TO_MIN_PROPERTY.get("height"))) {
                //height

                addElementDependentPropertyChange(dgTreeMapHeight, dependentRelatedNodes, currXpath, element.getCssProperty(), element.getValue());
//                System.out.println("Siz of dependentRelatedNodes: " + dependentRelatedNodes.size());

            } else if (element.getCssProperty().equalsIgnoreCase("width") ||
                    element.getCssProperty().equalsIgnoreCase(Constants.MAIN_PROPERTY_TO_MIN_PROPERTY.get("width"))) {
                //height
                addElementDependentPropertyChange(dgTreeMapWidth, dependentRelatedNodes, currXpath, element.getCssProperty(), element.getValue());
//                System.out.println("Siz of dependentRelatedNodes FOR WIDTH: " + dependentRelatedNodes.size());

            }
        }

        // Now Iterate over all the newly added and add them to the chromosome genes || CHECK IF MARGIN AND IT IS ALREADY ADDED (FIX SPACING)
        //THEN DO NOT ADD IT SINCE WE PRIORITIZE SPACING FIXES

        for (Element eDep : dependentRelatedNodes
        ) {
            Element foundElement = findElementByXpathANDProperty(chromosomeElementsToChange, eDep.getXpath(), eDep.getCssProperty());
            if (foundElement == null) {
                chromosomeElementsToChange.add(eDep);
            }
        }
    }

    private static void addElementDependentPropertyChange(TreeMap<String, List<DependentNode>> dependentNodesMap,
                                                          List<Element> dependentElements, String xpath,
                                                          String property, String geneNewVal) {

        double geneNewValue = Util.getNumbersFromString(geneNewVal).get(0);
        Node<DomNode> node = XMLUtils.searchVHTreeByXpath(xpath, XMLUtils.getRoot());
        String unit = "dp";
        double nodeDynamicValue = -1000;
        String propToRetrieve = property;  // to use when get dependentNodesMap.get(xpath + "#" + property);
        if (Constants.MAIN_PROPERTY_TO_MIN_PROPERTY.containsKey(property)) { // directly, height or width
            nodeDynamicValue = Double.valueOf(Util.getDynamicValueInDDP(node.getData(), property));
        } else {
            for (String key : Constants.MAIN_PROPERTY_TO_MIN_PROPERTY.keySet()
            ) {
                if (Constants.MAIN_PROPERTY_TO_MIN_PROPERTY.get(key).equalsIgnoreCase(property)) {

                    nodeDynamicValue = Double.valueOf(Util.getDynamicValueInDDP(node.getData(), key));
                    propToRetrieve = key;
                }
            }
        }
        //not height, width or minheight or minwidth
        if (nodeDynamicValue == -1000) {
            return;
        }
        List<DependentNode> dependentNodes = dependentNodesMap.get(xpath + "#" + propToRetrieve); // get all the height dependant nodes, which basically mean the other nodes in the segment
        //  /03/17/2021
//        List<DependentNode> dependentNodes = dependentNodesMap.get(xpath + "#" + property); // get all the height dependant nodes, which basically mean the other nodes in the segment
        if (dependentNodes != null && dependentNodes.size() > 0) {
            Element dElement;
            for (DependentNode dNode : dependentNodes) {
                dElement = null;
                if (!dNode.getXpath().equalsIgnoreCase(xpath)) {// we are only interested in depenendant property for the node so ignore any dependency with different xpath
                    continue;
                }
                if (dNode.getProperty().equalsIgnoreCase("width")) { // that means it is node's height to width property, we handle this in different method so ignore that
                    continue;
                }
                double dependentPropertyNewValue;
                Node<DomNode> currNode = XMLUtils.searchVHTreeByXpath(dNode.getXpath(), XMLUtils.getRoot());
                if (dNode.getRatio() == Double.POSITIVE_INFINITY) {
                    //that means this propery was not set originally but we add it (such as pading for images)
                    // To handled these we add its value as difference between gene new value and the dynamic size of the element( i.e., same size increas)
                    /***   Since it is padding and we are taking left/right or top/bottom so we divide by two ***/

//                    if (isImageElement(currNode)){
//
//                    }
                    dependentPropertyNewValue = (geneNewValue - nodeDynamicValue) / 2.0;
                    if (dependentPropertyNewValue > 0) {
                        dElement = new Element(dNode.getXpath(), dNode.getProperty(), dependentPropertyNewValue + unit, false, currNode);
                    }
                } else {
                    // value exists so apply the ratio value
                    dependentPropertyNewValue = geneNewValue / dNode.getRatio();
                }
                dElement = new Element(dNode.getXpath(), dNode.getProperty(), dependentPropertyNewValue + unit, false, currNode);
                dependentElements.add(dElement);
            }
        }
    }

    private static void addParentElementsChangesIfNeeded(List<Element> chromosomeElementsToChange) {

        DependencyGraph dgHeight = null;
        DependencyGraph dgWidth = null;
        TreeMap<String, List<DependentNode>> dgTreeMapHeight = null;
        TreeMap<String, List<DependentNode>> dgTreeMapWidth = null;
        dgHeight = SALEM.getSegmentToDG().get("S0_TouchTargetHeightIssue");
        dgWidth = SALEM.getSegmentToDG().get("S0_TouchTargetWidthIssue");

        if (dgHeight != null) {  // No dependency for height
            dgTreeMapHeight = dgHeight.getDependentNodesMap();
        }
        if (dgWidth != null) {  // No dependency for height
            dgTreeMapWidth = dgWidth.getDependentNodesMap();
        }


//        Iterator<Element> iter = chromosomeElementsToChange.iterator();
        /*** WRONG, I SHOULD START FROM LOWEST LEVEL NOT RANDOMLY**/
//        while (iter.hasNext()) {
//            Element cElement = iter.next();
        HashMap<String, Element> newlyAddedElements = new HashMap<>();  // to store new added elements
        for (Element cElement : chromosomeElementsToChange  /*** WRONG, I SHOULD START FROM LOWEST LEVEL NOT RANDOMLY**/
        ) {
            Node<DomNode> currNode = cElement.getNode();
            String correctHeightProp = SegmentRelationGraph.DecideTheCorrectValue(currNode.getData().getxPath(), "height");
            String correctWidthProp = SegmentRelationGraph.DecideTheCorrectValue(currNode.getData().getxPath(), "width");
            if (cElement.getCssProperty().equalsIgnoreCase(correctHeightProp)) {
                double childFullHeight = getTotalChildVal(cElement.getNode(), "height");
                String childDynamicHeight = Util.getDynamicValueInDDP(cElement.getNode().getData(), "height");
                double childDynamicHeightVal = Util.getNumbersFromString(childDynamicHeight).get(0);

                // check if it is dependanat noe from dependancy graph already in chromosome
                Node<DomNode> parentNode = getDependentNode(dgTreeMapHeight, "height", cElement.getXpath());

                if (parentNode != null) {  // there is dependency node
                    String parentDynamicHeight = Util.getDynamicValueInDDP(parentNode.getData(), "height");
                    double parentDynamicHeightVal = Util.getNumbersFromString(parentDynamicHeight).get(0);
                    double parentAvalSize = getTotalParentAvialableSpaceVal(parentNode, cElement.getNode(), correctHeightProp);
                    // is parent already in chromosome
                    String correctProperty = SegmentRelationGraph.DecideTheCorrectValue(parentNode.getData().getxPath(), "height");
                    String parentXpath = parentNode.getData().getxPath();
                    Element pElement = null;
                    if (newlyAddedElements.containsKey(parentXpath)) {
                        pElement = newlyAddedElements.get(parentXpath);
                    } else {
                        pElement = findElementByXpathANDProperty(chromosomeElementsToChange, parentXpath, correctProperty);

                    }
                    if (pElement == null) {
                        //It was not added before so directly add it
                        double childIncrease = getNumbersFromString(cElement.getValue()).get(0) - childDynamicHeightVal;
                        double avaliableHeight = parentAvalSize - childIncrease;
                        if (avaliableHeight < 3) { // test with 3 no particular reason but empirically making sure there is increase
                            double parentNewVal = parentDynamicHeightVal + (childIncrease + 3);
                            Element pEl = new Element(parentXpath, correctProperty, String.valueOf(parentNewVal)
                                    , false, parentNode);
                            newlyAddedElements.put(parentXpath, pEl);
                            //chromosomeElementsToChange.add(pEl);
                        }

                    } else {
                        //TODO: Already added so check if new is larger than added
                        String peV = pElement.getValue();
                        double peVal = getNumbersFromString(peV).get(0);
                        double childIncrease = getNumbersFromString(cElement.getValue()).get(0) - childDynamicHeightVal;
                        double avaliableHeight = parentAvalSize - childIncrease;
                        if (avaliableHeight < 3) { // test with 3 no particular reason but empirically making sure there is increase
                            double parentNewVal = parentDynamicHeightVal + (childIncrease + 3);
                            if (parentNewVal > peVal) {
                                pElement.setValue(String.valueOf(parentNewVal));

                                newlyAddedElements.put(parentXpath, pElement);
                            }


                            //chromosomeElementsToChange.add(pEl);
                        }
                    }
                }
            }
            if (cElement.getCssProperty().equalsIgnoreCase(correctWidthProp)) {
                String width = getDynamicValueInDDP(currNode.getData(), "width");
                double widthDyn = getNumbersFromString(width).get(0);
                // check if it is dependanat noe from dependancy graph already in chromosome
                Node<DomNode> parentNode = getDependentNode(dgTreeMapWidth, "width", cElement.getXpath());
                if (parentNode != null) {  // there is dependency node
                    String parentV = Util.getDynamicValueInDDP(parentNode.getData(), "width"); // get the dynamic value for the parent to see if it can fit the increase
                    double parentVal = Util.getNumbersFromString(parentV).get(0);
                }
            }


        }


        // Now we need just to add the elements in newlyAddedElements to chromosomeElements
        for (String xpath : newlyAddedElements.keySet()
        ) {
            Element newEl = newlyAddedElements.get(xpath);
            Element e = findElementByXpathANDProperty(chromosomeElementsToChange, xpath, newEl.getCssProperty());
            if (e == null) {
                // first time it is added so add the new element to the list
                chromosomeElementsToChange.add(newEl);
            } else {
                e.setValue(newEl.getValue());
            }
        }
    }

    private static double getTotalParentAvialableSpaceVal(Node<DomNode> parentNode, Node<DomNode> childInReference, String property) {
        /*** childInReference --> The child we need to increase and we need to check its parent ***/

        // right now I am simple considereing the child
        double totalchildVal = getTotalChildVal(childInReference, property);
        String parentV = Util.getDynamicValueInDDP(parentNode.getData(), property);
        double parentVal = Util.getNumbersFromString(parentV).get(0);

        return parentVal - totalchildVal;
    }


    private static double getTotalInternalSizeForNode(Node<DomNode> node, String property) {
        //get property: height or witdh then calculate dynamic height - padding
        String dynamicP = Util.getDynamicValueInDDP(node.getData(), property);
        double dynamicPVal = Util.getNumbersFromString(dynamicP).get(0);
        dynamicPVal = substractPaddingsFromDynamic(node, dynamicPVal, property);
        return dynamicPVal;
    }

    private static double substractPaddingsFromDynamic(Node<DomNode> node, double dynamicPVal, String property) {
        // based on proeprty height or width. It goes through
        if (property.equalsIgnoreCase("height")) {
            //top and bottom padding
            double topPaddingVal = 0.0;
            double bottomPaddingVal = 0.0;
            String topPadding = node.getData().getAttr(Constants.SIZE_SPACE_ATTRIBUTES.get(Constants.PADDING_TOP));
            if (topPadding != null) {
                topPaddingVal = getNumbersFromString(topPadding).get(0);
            }
            String bottomPadding = node.getData().getAttr(Constants.SIZE_SPACE_ATTRIBUTES.get(Constants.PADDING_BOTTOM));
            if (bottomPadding != null) {
                bottomPaddingVal = getNumbersFromString(bottomPadding).get(0);
            }

            dynamicPVal = dynamicPVal - (topPaddingVal + bottomPaddingVal);

        } else if (property.equalsIgnoreCase("width")) {
            //left and bottom padding
            double leftPaddingVal = 0.0;
            double rightPaddingVal = 0.0;
            String leftPadding = node.getData().getAttr(Constants.SIZE_SPACE_ATTRIBUTES.get(Constants.PADDING_LEFT));
            if (leftPadding != null) {
                leftPaddingVal = getNumbersFromString(leftPadding).get(0);
            }
            String rightPadding = node.getData().getAttr(Constants.SIZE_SPACE_ATTRIBUTES.get(Constants.PADDING_RIGHT));
            if (rightPadding != null) {
                rightPaddingVal = getNumbersFromString(rightPadding).get(0);
            }

            dynamicPVal = dynamicPVal - (leftPaddingVal + rightPaddingVal);

        }
        return dynamicPVal;
    }

    private static double getTotalExternalSizeForNode(Node<DomNode> node, String property) {
        //get property: height or witdh then calcualte the dnamic value for that + margins
        String dynamicP = Util.getDynamicValueInDDP(node.getData(), property);
        double dynamicPVal = Util.getNumbersFromString(dynamicP).get(0);
        dynamicPVal = addMargingsToDynamic(node, dynamicPVal, property);
        return dynamicPVal;
    }

    private static double addMargingsToDynamic(Node<DomNode> node, double dynamicPVal, String property) {
        // based on proeprty height or width. It goes through
        if (property.equalsIgnoreCase("height")) {
            //top and bottom margin
            double topMargingVal = 0.0;
            double bottomMargingVal = 0.0;
            String topMargin = node.getData().getAttr(Constants.SIZE_SPACE_ATTRIBUTES.get(Constants.MARGIN_TOP));
            if (topMargin != null) {
                topMargingVal = getNumbersFromString(topMargin).get(0);
            }
            String bottomMargin = node.getData().getAttr(Constants.SIZE_SPACE_ATTRIBUTES.get(Constants.MARGIN_BOTTOM));
            if (bottomMargin != null) {
                bottomMargingVal = getNumbersFromString(bottomMargin).get(0);
            }

            dynamicPVal = dynamicPVal - (topMargingVal + bottomMargingVal);

        } else if (property.equalsIgnoreCase("width")) {
            //left and bottom margin
            double leftMargingVal = 0.0;
            double rightMarginVal = 0.0;
            String leftMargin = node.getData().getAttr(Constants.SIZE_SPACE_ATTRIBUTES.get(Constants.MARGIN_LEFT));
            if (leftMargin != null) {
                leftMargingVal = getNumbersFromString(leftMargin).get(0);
            }
            String rightMargin = node.getData().getAttr(Constants.SIZE_SPACE_ATTRIBUTES.get(Constants.MARGIN_RIGHT));
            if (rightMargin != null) {
                rightMarginVal = getNumbersFromString(rightMargin).get(0);
            }

            dynamicPVal = dynamicPVal - (leftMargingVal + rightMarginVal);

        }
        return dynamicPVal;
    }

    private static double getTotalChildVal(Node<DomNode> childNode, String property) {
        //get property: height or witdh then calcualte the dnamic value for that + margins
        String childV = Util.getDynamicValueInDDP(childNode.getData(), property);
        double childVal = Util.getNumbersFromString(childV).get(0);
        return childVal;
    }

    private static Node<DomNode> getDependentNode(TreeMap<String, List<DependentNode>> dgTreeMapHeight, String property, String childXpath) {
        Node<DomNode> dependentParentNode = null;
        for (String dgKey : dgTreeMapHeight.keySet()) {
            if (dgKey.contains(childXpath + "#" + property)) {
                List<DependentNode> dependentNodes = dgTreeMapHeight.get(childXpath + "#" + property);
                for (DependentNode dNode : dependentNodes
                ) {
                    if (dNode.getXpath().equalsIgnoreCase(childXpath)) { // Ignore dependency of the element to itself (that include relation to min-heigth, padding and so on)
                        continue;
                    } else {
                        String parentXpath = dNode.getXpath(); //ToDo: check if the parent is really in the denominator
                        Node<DomNode> parentNode = XMLUtils.searchVHTreeByXpath(parentXpath, XMLUtils.getRoot());
                        dependentParentNode = parentNode;

                    }

                }
            }
        }
        return dependentParentNode;
    }

    private static void addMatchParentElements(HashMap<String, ArrayList<MatchParentElement>> matchParentMap,
                                               List<Element> chromosomeElementsToChange) {
        for (String parentOfMatchParentXpath : matchParentMap.keySet()
        ) {

            ArrayList<MatchParentElement> listOfElements = matchParentMap.get(parentOfMatchParentXpath);
            Node<DomNode> parentN = matchParentMap.get(parentOfMatchParentXpath).get(0).getNode();

//            double totalHeightIncrease = -100.0;
            double maxNewHeight = -100.0;
            String correctHeightProperty = SegmentRelationGraph.DecideTheCorrectValue(parentOfMatchParentXpath, "height");
            double maxNewWidth = -100.0;
            String correctWidthProperty = SegmentRelationGraph.DecideTheCorrectValue(parentOfMatchParentXpath, "width");
            String heightProp = "";
            String widthProp = "";
            String heightD = getDynamicValueInDDP(parentN.getData(), "height");
            String widthD = getDynamicValueInDDP(parentN.getData(), "width");
            double heightDyn = getNumbersFromString(heightD).get(0);
            double widthDyn = getNumbersFromString(widthD).get(0);

            for (MatchParentElement e : listOfElements
            ) {
                String prop = e.getCssProperty();
                if (e.getCssProperty().equalsIgnoreCase(correctHeightProperty)) {  // it is height
                    String val = e.getValue();
                    double currPVal = Util.getNumbersFromString(val).get(0);
                    maxNewHeight = Double.max(currPVal, maxNewHeight);
                    heightProp = prop;
//                    if ((currPVal - heightDyn) > 0) {
//                        totalHeightIncrease += (currPVal - heightDyn);
//                    }
                    //       largestHeight=Double.max(largestHeightVal,currPVal);
                } else if (e.getCssProperty().equalsIgnoreCase(correctWidthProperty)) {  // it is width
                    String val = e.getValue();
                    double currPVal = Util.getNumbersFromString(val).get(0);
                    maxNewWidth = Double.max(currPVal, maxNewWidth);
                    widthProp = prop;
//                    if ((currPVal - heightDyn) > 0) {
//                        totalWidthIncrease += (currPVal - widthDyn);
//                    }
                    //      largestWidth=Double.max(largestWidthVal,currPVal);
                }

            }


            if (maxNewHeight > 0.0) {
                // add dynamic size to total increase to get the total increase
//                double parentCalculatedVal = heightDyn + totalHeightIncrease;
                Element cElement = findElementByXpathANDProperty(chromosomeElementsToChange, parentOfMatchParentXpath, correctHeightProperty); // Assuming there is only one element in chromosome
                // check if this value is greater than chromosome value. If so then update chromosome val
                if (cElement != null) {
                    String stringVal = cElement.getValue();
                    double chromosomeVal = getNumbersFromString(stringVal).get(0);
                    if (maxNewHeight > chromosomeVal) {
                        cElement.setValue(String.valueOf(maxNewHeight + 2));
                    }
                } else {  // it is not already part of choromosome so directly add new value
//                   String xpath, String cssProperty, String value,boolean isParentIncrease,Node<DomNode> domNode
                    Element pEl = new Element(parentOfMatchParentXpath, correctHeightProperty, String.valueOf(maxNewHeight + 2)
                            , true, parentN);
                    chromosomeElementsToChange.add(pEl);
                }
            }
            if (maxNewWidth > 0.0) {
                // add dynamic size to total increase to get the total increase
//                double parentCalculatedVal = widthDyn + totalWidthIncrease;
                Element cElement = findElementByXpathANDProperty(chromosomeElementsToChange, parentOfMatchParentXpath, correctWidthProperty); // Assuming there is only one element in chromosome
                // check if this value is greater than chromosome value. If so then update chromosome val
                if (cElement != null) {
                    String stringVal = cElement.getValue();
                    double chromosomeVal = getNumbersFromString(stringVal).get(0);
                    if (maxNewWidth > chromosomeVal) {
                        cElement.setValue(String.valueOf(maxNewWidth + 2));
                    }
                } else {
                    Element pEl = new Element(parentOfMatchParentXpath, correctWidthProperty, String.valueOf(maxNewWidth + 2)
                            , true, parentN);
                    chromosomeElementsToChange.add(pEl);
                }
            }
        }

    }

    private static void resolveMatchParentElements(HashMap<String, ArrayList<MatchParentElement>> matchParentMap,
                                                   List<Element> chromosomeElementsToChange) {
        for (String parentOfMatchParentXpath : matchParentMap.keySet()
        ) {

            ArrayList<MatchParentElement> listOfElements = matchParentMap.get(parentOfMatchParentXpath);
            Node<DomNode> parentN = matchParentMap.get(parentOfMatchParentXpath).get(0).getNode();

            double totalHeightIncrease = 0.0;
            String correctHeightProperty = SegmentRelationGraph.DecideTheCorrectValue(parentOfMatchParentXpath, "height");
            double totalWidthIncrease = 0.0;
            String correctWidthProperty = SegmentRelationGraph.DecideTheCorrectValue(parentOfMatchParentXpath, "width");

            String heightD = getDynamicValueInDDP(parentN.getData(), "height");
            String widthD = getDynamicValueInDDP(parentN.getData(), "width");
            double heightDyn = getNumbersFromString(heightD).get(0);
            double widthDyn = getNumbersFromString(widthD).get(0);

            for (MatchParentElement e : listOfElements
            ) {
                if (e.getCssProperty().equalsIgnoreCase(correctHeightProperty)) {  // it is height
                    String val = e.getValue();
                    double currPVal = Util.getNumbersFromString(val).get(0);
                    if ((currPVal - heightDyn) > 0) {
                        totalHeightIncrease += (currPVal - heightDyn);
                    }
                    //       largestHeight=Double.max(largestHeightVal,currPVal);
                } else if (e.getCssProperty().equalsIgnoreCase(correctWidthProperty)) {  // it is width
                    String val = e.getValue();
                    double currPVal = Util.getNumbersFromString(val).get(0);
                    if ((currPVal - heightDyn) > 0) {
                        totalWidthIncrease += (currPVal - widthDyn);
                    }
                    //      largestWidth=Double.max(largestWidthVal,currPVal);
                }

            }
            if (totalHeightIncrease > 0.0) { // meaning that it is already included with -1
                // add dynamic size to total increase to get the total increase
                double parentCalculatedVal = heightDyn + totalHeightIncrease;
                Element cElement = findElementByXpathANDProperty(chromosomeElementsToChange, parentOfMatchParentXpath, correctHeightProperty); // Assuming there is only one element in chromosome
                // check if this value is greater than chromosome value. If so then update chromosome val
                if (cElement != null) {
                    String stringVal = cElement.getValue();
                    double chromosomeVal = getNumbersFromString(stringVal).get(0);
                    if (parentCalculatedVal > chromosomeVal) {
                        cElement.setValue(String.valueOf(parentCalculatedVal));
                    }
                } else {  // it is not already part of choromosome so directly add new value
//                   String xpath, String cssProperty, String value,boolean isParentIncrease,Node<DomNode> domNode
                    Element pEl = new Element(parentOfMatchParentXpath, correctHeightProperty, String.valueOf(parentCalculatedVal)
                            , false, parentN);
                    chromosomeElementsToChange.add(pEl);
                }
            }
            if (totalWidthIncrease > 0.0) { // meaning that it is already included with -1
                // add dynamic size to total increase to get the total increase
                double parentCalculatedVal = widthDyn + totalWidthIncrease;
                Element cElement = findElementByXpathANDProperty(chromosomeElementsToChange, parentOfMatchParentXpath, correctWidthProperty); // Assuming there is only one element in chromosome
                // check if this value is greater than chromosome value. If so then update chromosome val
                if (cElement != null) {
                    String stringVal = cElement.getValue();
                    double chromosomeVal = getNumbersFromString(stringVal).get(0);
                    if (parentCalculatedVal > chromosomeVal) {
                        cElement.setValue(parentCalculatedVal + "dp");
                    }
                } else {
                    Element pEl = new Element(parentOfMatchParentXpath, correctWidthProperty, String.valueOf(parentCalculatedVal)
                            , false, parentN);
                    chromosomeElementsToChange.add(pEl);
                }
            }
        }

    }

    private static void addGeneElementsToChromosomeElements(List<Element> geneElementsToChange, List<Element> chromosomeElements) {

        /*** Compare the gene elements to the final chromosomeElements. To do that we compare the gene elements with the already added elements***/
        for (Element geneElement : geneElementsToChange
        ) {
            updateChromosomeElements(geneElement, chromosomeElements);

        }
    }

    private static void updateChromosomeElements(Element geneElement, List<Element> chromosomeElements) {
        String gXpath = geneElement.getXpath();
        Node<DomNode> gNode = XMLUtils.searchVHTreeByXpath(gXpath, XMLUtils.getRoot());
        String gProperty = geneElement.getCssProperty();
        String gValue = geneElement.getValue();
        boolean gIsParentIncrease = geneElement.isParentIncrease();
        boolean found = false;
        double newChromosomeVal;
        for (Element chromosomeElement : chromosomeElements
        ) {
            if (!chromosomeElement.getXpath().equalsIgnoreCase(gXpath) || !chromosomeElement.getCssProperty().equalsIgnoreCase(gProperty)) { //Not the same element change
                continue;
            }
            String cXpath = chromosomeElement.getXpath();
            String cProperty = chromosomeElement.getCssProperty();
            String cValue = chromosomeElement.getValue();
            boolean cIsParentIncrease = chromosomeElement.isParentIncrease();
            double doubleGeneVal = Util.getNumbersFromString(gValue).get(0);
            double doubleChromosomeVal = Util.getNumbersFromString(cValue).get(0);
            if (!gIsParentIncrease && !cIsParentIncrease) {
                // both directIncrease
                newChromosomeVal = Double.max(doubleGeneVal, doubleChromosomeVal);
                chromosomeElement.setParentIncrease(false);
                chromosomeElement.setValue(String.valueOf(newChromosomeVal));
                found = true;
                break;

            }
        }

        if (!found) {  // means that it is not found so add the geneValueto Chromosome
            Element newElement = new Element(gXpath, gProperty, gValue, gIsParentIncrease, gNode);
            chromosomeElements.add(newElement);
        }
    }

//    public static String WriteChromosomeChangesToFileSegmentRelationGraphV1(GAChromosome chromosome, String outputPath) {
//        /*** Given a chronmosome, we (1) first copy the original apk to a new folder, (2) decide the elements to be changed for each gene and then (3) apply the change to the static layout files
//         * (4) return true if that went well ***/
//
//        //the folder will be the apk file + _ chromosome ID
//        String suffix = chromosome.getChromosomeIdentifier();
//        //Copy to the same folder (so get the parent) but with the chromosome id
//        File dest = new File(mFix.getOriginalDecompiled()).getParentFile();
//        File source = new File(mFix.getOriginalDecompiled());
//
//        String chromosomeFolderPath = copyAppFolder(source, dest, suffix);
//
//        // Now we have the folder of the chromosme so we write the changes of the chromosome
//
//        /* (1) Iterate over the genes */
//        HashMap<String, ArrayList<MatchParentElement>> matchParentMap = new HashMap();    // key: dependant node from dependacny graph , arraylist: list of matchParentelements that has property  with -1
//
//        for (GAGene gene : chromosome.getGenes()) {
//            System.out.println("\nApplying values for gene " + gene + " | for chromosome :" + gene.getSegmentIssueId());
//            String javascriptCode = "";
//            int i = 0;
//
//            /* (2) get the elements that need to be changed based on the dependancy graph... Basically: Touch targets with problems then elements that depend on it */
//            String newValue = gene.getValue(); // This is the new Value to apply
//            String smallestValue = null; // ToDo: impelement this using getMaxIncreaseNeeded(segDepGraph) in the XXFIT class and replace it with null
//            String geneXpath = null; // TODO: I used to send gene.getXpaths().get(0)
////            List<Element> elementsToChange = mFix.getSegmentToDG().get(gene.getSegmentIssueId()).
////                    getElementsToChange(gene.getCssProperty(), newValue, null, gene.getIssueType(), gene.getXpaths().get(0));
//
//
//            List<Element> elementsToChange = null;
//            if (gene.getIssueType().equalsIgnoreCase(Constants.TOUCH_TARGET_SPACE_ISSUE)) { // means it is a gene for spacing issue
//                DependencyGraph dgSpace = mFix.getSegmentToDG().get(gene.getSegmentIssueId());
//                if (mFix.getSegmentToDG().get(gene.getSegmentIssueId()) != null) {
//                    elementsToChange = mFix.getSegmentToDG().get(gene.getSegmentIssueId()).
//                            getElementsToChange(gene.getCssProperty(), newValue, null, gene.getIssueType(), null); // xpath 0: smallest height , xpath 1: smallest width
//                }
//            } else { // means it is a gene for sizing issue
//                if (gene.getXpaths().size() == 1) {
//                    //    that means the same element has the smallest width and height
//                    //ToDo: what should we do now?
//                }
//                SegmentRelationGraph s = mFix.getSegmentToSG().get(gene.getSegmentIssueId());
//                elementsToChange = mFix.getSegmentToSG().get(gene.getSegmentIssueId()).
//                        getElementsToChange(gene.getSegmentIssueId(), gene.getCssProperty(), newValue, gene.getIssueType(), gene.getXpaths()
//                                , matchParentMap); // xpath 0: smallest height , xpath 1: smallest width
//
////
//            }
//
//            //ToDo: I can store all the elements to change then write them at once OR write each gene one at a time | Right now I am writing each gene by itself
//            String patchID = chromosome.getChromosomeIdentifier();
//            if (elementsToChange != null || elementsToChange.size() > 0) { // If no elements to change then just skip this
//
//                applyChangeToElements(elementsToChange, outputPath);
//            }
//        }
//        //Once all chromosome genes  are applied we then write them.
//        writeMofidiedStaticFilesToFile();  // once we finalized the genes now we can write the updatet s the decompiled apl
//        return chromosomeFolderPath;
//    }

    private static void writeMofidiedStaticFilesToFile() {
        Map<String, StaticLayoutUtil> cachedLayouts = getStaticLayoutFilesCache();
        for (String staticLayoutFilePath :
                cachedLayouts.keySet()) {
            StaticLayoutUtil staticLayoutObj = cachedLayouts.get(staticLayoutFilePath);
            staticLayoutObj.writeStaticXML();
        }
    }

    public static boolean doesContainImage(Node<DomNode> elementNode) {

        boolean result = false;

        if (elementNode.getData().getTagName().contains("ImageView")) {
            return true;
        }

        String containsbackground = elementNode.getData().getAttr("android:background");
        return elementNode.getData().getTagName().contains("Button") && containsbackground != null;
    }

    public static String WriteChromosomeChangesToFile(GAChromosome chromosome, String outputPath) {
        /*** Given a chronmosome, we (1) first copy the original apk to a new folder, (2) decide the elements to be changed for each gene and then (3) apply the change to the static layout files
         * (4) return true if that went well ***/

        //the folder will be the apk file + _ chromosome ID
        String suffix = chromosome.getChromosomeIdentifier();
        //Copy to the same folder (so get the parent) but with the chromosome id
        File dest = new File(SALEM.getOriginalDecompiled()).getParentFile();
        File source = new File(SALEM.getOriginalDecompiled());

        String chromosomeFolderPath = copyAppFolder(source, dest, suffix);

        // Now we have the folder of the chromosme so we write the changes of the chromosome

        /* (1) Iterate over the genes */
        for (GAGene gene : chromosome.getGenes()) {
            Logger.trace("\nApplying values for gene " + gene + " | for chromosome :" + gene.getSegmentIssueId());
            String javascriptCode = "";
            int i = 0;

            /* (2) get the elements that need to be changed based on the dependancy graph... Basically: Touch targets with problems then elements that depend on it */
            String newValue = gene.getValue(); // This is the new Value to apply
            String smallestValue = null; // ToDo: impelent this using getMaxIncreaseNeeded(segDepGraph) in the XXFIT class and replace it with null
            String geneXpath = null; // TODO: I used to send gene.getXpaths().get(0)
//            List<Element> elementsToChange = mFix.getSegmentToDG().get(gene.getSegmentIssueId()).
//                    getElementsToChange(gene.getCssProperty(), newValue, null, gene.getIssueType(), gene.getXpaths().get(0));

            List<Element> elementsToChange = SALEM.getSegmentToDG().get(gene.getSegmentIssueId()).
                    getElementsToChange(gene.getCssProperty(), newValue, null, gene.getIssueType(), geneXpath);
            if (gene.getCssProperty().equalsIgnoreCase("height")) {
                List<Element> min_heightElementsToChange = SALEM.getSegmentToDG().get(gene.getSegmentIssueId()).
                        getElementsToChange("min-height", newValue, null, gene.getIssueType(), geneXpath);
                elementsToChange = Stream.concat(elementsToChange.stream(), min_heightElementsToChange.stream())
                        .collect(Collectors.toList());

            }

            //ToDo: I can store all the elements to change then write them at once OR write each gene one at a time | Right now I am writing each gene by itself
            String patchID = chromosome.getChromosomeIdentifier();
            applyChangeToElements(elementsToChange, outputPath);
        }
        return chromosomeFolderPath;
    }

    private static void applyChangeToElements(List<Element> elementsToChange, String outputPath) {
//        Map<String, StaticLayoutUtil> staticLayout = getStaticLayoutFilesCache();
        Node<DomNode> root = XMLUtils.getRoot();
        Node<DomNode> domNodeFound;
        String unit = "";
        for (Element element : elementsToChange) {
            String valueString = element.getValue();
            domNodeFound = XMLUtils.searchVHTreeByXpath(element.getXpath(), root);
            if (domNodeFound != null) {
//                System.out.println("Patch: Applying " + element.getCssProperty() + ": " + valueString + " to " + element.getXpath());
                //(1) update the node value in the XMLUtil ( so we do not overwrite the value when we write the second gene like whathappendd with lower bar of aidownload
//                updateElementInCurrentInstance(domNodeFound, Constants.SIZE_SPACE_ATTRIBUTES.get(element.getCssProperty()), valueString + unit);

                String originFIle = domNodeFound.getData().getAttributes().get("origin");
                if (originFIle != null && !originFIle.equalsIgnoreCase("NOTFOUND")) {
                    /*** If element does not contain origin then we can not write it so SKIP ***/
                    String[] layout = originFIle.split("/res/");
//                    System.out.println(" Res before crash: " + originFIle);
                    String layoutPath = "/res/" + layout[1];
                    String modify_origin = outputPath + File.separator + layoutPath;
                    modify_origin = modify_origin.replaceAll("//", "/"); // In case
//                    modify_origin=path[0]+File.separator+TTFIX.apk_name+File.separator+path[1]+File.separator+modify_origin;
                    StaticLayoutUtil originLayout = getStaticAppLayout(modify_origin);
                    String effective_id = domNodeFound.getData().getAttr("effective-id");
                    String id = domNodeFound.getData().getId();
                    if (effective_id != null) {
                        id = effective_id;
                    }
//                    System.out.println("ID: " + id);
                    if (id != null) {
                        org.w3c.dom.Element e = originLayout.searchForNode(originLayout.getRoot(), id);
                        String prop = Constants.SIZE_SPACE_ATTRIBUTES.get(element.getCssProperty());
                        if (OwlEye.debugCompile) {
                        Logger.trace("ID: " + id + " val: " + valueString + "prop " + prop);}
                        double doubleVal = getNumbersFromString(valueString).get(0);
                        String finalStringVal = doubleVal + "dp";
                       if(e==null){
                           continue;
                       }
                        e.setAttribute(Constants.SIZE_SPACE_ATTRIBUTES.get(element.getCssProperty()), finalStringVal);
//                        nodex.addAtrribute(Constants.SIZE_SPACE_ATTRIBUTES.get(element.getCssProperty()), valueString + unit); //TODO: Check if attribute already added
                    }
                }
            }
        }

    }



    private static void removeStyleAttr(UiNode nodex) {
        if (nodex.getAttributes().containsValue("style")) {
//            nodex.r
        }
    }

    private static void updateElementInCurrentInstance(Node<DomNode> domNodeFound, String property, String newValue) {
        //Update the value of the attribute
        domNodeFound.getData().setAttr(property, newValue);
    }

    //Parse issue filtered from logcat
    public static void parseIssuesFile_LogCat(String issueFolder, String chromsomeAPKIssueFile, String chromosomeID) {
        String issuePath = issueFolder;
        if (chromosomeID.equalsIgnoreCase("initial")) {
            issuePath = Constants.ORIGINAL_ISSUES_PATH;
        }

        // (1) First the developer provide list of issues in a csv file that contain the activities that these issues belong to as well as other important info

        // Issue name = the chromosome apk but repalce apk with the TT suffix and .csv
//        String issueFileName = chromsomeAPKFileName.replace(".apk", Constants.TOUCH_TARGET_ISSUES_OUTPUT_SUFFIX);
        //String issueFileName = chromsomeAPKIssueFile + "/" + Constants.TOUCH_TARGET_ISSUES_OUTPUT_SUFFIX;
        //  String filePath = issuePath + "/" + issueFileName;//First time the original file of apk is used no chromosome suffix
        String filePath = issueFolder;
        //mFix.getCopiedApkFileName():  apk file with date
        ReadIssues readIssue = new ReadIssues(filePath);
        HashMap<String, Set<TIssue>> TTSZResult = readIssue.parseIssue(chromosomeID);//Since this is the initial issue we add "initial" as parameter instead of chromosome ID
        //(2) Initialize the result class to store the list of issues as well as the scores
        AccessibilityScannerResults result = new AccessibilityScannerResults();

        //(3) Store the list of TT size issues in the result class
        result.setListOfIssues(TTSZResult, true); // true= this is the initial list of issues
        SALEM.getDetectionToolResults().put(chromosomeID, result);
//        mFix.setDetectionToolResults();detectionToolResults.put("initial",result);
        // (4) We assume that the original list of problematic activities are also provided for the app initially
        if (chromosomeID.equalsIgnoreCase("initial")) {
            Set<String> problematicActivities = result.getInitialProblematicActivities();
            SALEM.setProblematicActivities(problematicActivities);  // We only set this if it is the the initial chromosome
        }

    }

    //This method to read issues pulled from csv
    public static void parseIssuesFile(String issueFolder, String chromsomeAPKFileName, String chromosomeID) {
        String issuePath = issueFolder;
        if (chromosomeID.equalsIgnoreCase("initial")) {
            issuePath = Constants.ORIGINAL_ISSUES_PATH;
        }

        // (1) First the developer provide list of issues in a csv file that contain the activities that these issues belong to as well as other important info

        // Issue name = the chromosome apk but repalce apk with the TT suffix and .csv
        String issueFileName = chromsomeAPKFileName.replace(".apk", Constants.TOUCH_TARGET_ISSUES_OUTPUT_SUFFIX);
        String filePath = issuePath + "/" + issueFileName;//First time the original file of apk is used no chromosome suffix
        //mFix.getCopiedApkFileName():  apk file with date
        ReadIssues readIssue = new ReadIssues(filePath);
        HashMap<String, Set<TIssue>> TTSZResult = readIssue.parseIssue(chromosomeID);//Since this is the initial issue we add "initial" as parameter instead of chromosome ID
        //(2) Initialize the result class to store the list of issues as well as the scores
        AccessibilityScannerResults result = new AccessibilityScannerResults();

        //(3) Store the list of TT size issues in the result class
        result.setListOfIssues(TTSZResult, true); // true= this is the initial list of issues
        SALEM.getDetectionToolResults().put(chromosomeID, result);
//        mFix.setDetectionToolResults();detectionToolResults.put("initial",result);
        // (4) We assume that the original list of problematic activities are also provided for the app initially
        if (chromosomeID.equalsIgnoreCase("initial")) {
            Set<String> problematicActivities = result.getInitialProblematicActivities();
            SALEM.setProblematicActivities(problematicActivities);  // We only set this if it is the the initial chromosome
        }

    }

//    public static void prepareAppToGetToCorrectStateLOGCAT_backup(String currentActivityName, String apkPath, String chromosomeAPKName) throws IOException, InterruptedException {
//        startAnApp(Constants.Accessibility_SERVICE_PACKAGE); // make sure accessibility works
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        installAPK(apkPath, chromosomeAPKName);
//        startAnApp(mFix.getCurrentApkPackage());  // check if the command is correct and accurate
//
//        /*** Run the cralwing script to reach the correct state of the activity then capture the UI with UI automator***/
//        String scriptPath = Constants.CRAWLING_SCRIPTS_PATH + "/" + currentActivityName + Constants.CRAWLING_SCRIPTS_SUFFIX;
//        String crawlingScriptFile = scriptPath;
//        runCommand(Constants.CLEAN_LOGCAT_CMD, null, null); // Clean logCat
//        runCrawlingscript(crawlingScriptFile);
//        //     runCommand(Constants.CLICK_HOME_BUTTON,null,null);
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
////        runCrawlingscript(crawlingScriptFile);
////        try {
////            Thread.sleep(2000);
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
////        startAnApp(mFix.getCurrentApkPackage());
//
//
//    }

    public static String prepareAppToGetToCorrectStateLOGCAT(String currentActivityName, String apkPath, String chromosomeAPKName) throws IOException, InterruptedException {
        if (Constants.Accessibility_SCORE_APPROACH.equalsIgnoreCase("FROM_ISSUES")) {

            startAnApp(Constants.Accessibility_SERVICE_PACKAGE); // make sure accessibility works
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        installAPK(apkPath, chromosomeAPKName,SALEM.getCurrentApkPackage());
        if (Constants.Accessibility_SCORE_APPROACH.equalsIgnoreCase("FROM_ISSUES")) {

            startAnApp(SALEM.getCurrentApkPackage());  // check if the command is correct and accurate
        }
        /*** Run the cralwing script to reach the correct state of the activity then capture the UI with UI automator***/
        ArrayList<String> actScriptInfo = SALEM.getActivitiesToCrawlingScripts().get(SALEM.getCurrentActivityName().trim());
        String activityToRunWithScript = actScriptInfo.get(1);
        String scriptFileName = actScriptInfo.get(2);
        String args = SALEM.getDeviceName();
        String packageName = SALEM.getCurrentApkPackage();
        String actName = SALEM.getCurrentActivityName();

        args = args + " " + packageName + " " + activityToRunWithScript;
        String type = actScriptInfo.get(3);
        if (type == null) {
            type = "";
            //   args = "";
            //type	content	extra_content
            //     activityToRunWithScript scriptFileName
        } else if (type != null) {
            args = args + " " + type;
            String content = actScriptInfo.get(4);
            if (content != null) {
                content = content.replace(" ", "000");
                args = args + " " + content;
                String extra_content = actScriptInfo.get(5);
                if (extra_content != null) {
                    args = args + " " + extra_content;
                }
            }
        }

        String scriptPath = Constants.CRAWLING_SCRIPTS_PATH +
                "/" + scriptFileName;
//        String scriptPath = Constants.CRAWLING_SCRIPTS_PATH + "/" + currentActivityName + Constants.CRAWLING_SCRIPTS_SUFFIX;

        String crawlingScriptCMD = scriptPath + " " + args;
        Logger.debug("Running crawling script");
        if (Constants.Accessibility_SCORE_APPROACH.equalsIgnoreCase("FROM_ISSUES")) {

            runCommand(Constants.CLEAN_LOGCAT_CMD, null, null); // Clean logCat
        }
        //startAnApp(mFix.getCurrentApkPackage());
        runCrawlingscript(crawlingScriptCMD);
        //     runCommand(Constants.CLICK_HOME_BUTTON,null,null);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String activityName = getDeviceCurrentRunningActivityName();


        //(2) dump the layout using ui automator to the phone
        if (activityName.equalsIgnoreCase(SALEM.getCurrentActivityName())) {  // if it is NOT the correct activity
            return activityName;
        } else {
            return null;  // Not Correct
        }
//        runCrawlingscript(crawlingScriptFile);
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        startAnApp(mFix.getCurrentApkPackage());


    }

    public static boolean isDirEmpty(String filePath) {
        File directory = new File(filePath);
        File[] contents = directory.listFiles();
// the directory file is not really a directory..
        // Folder contains files
        if (contents == null) {
            return true;
        }
// Folder is empty
        else return contents.length == 0;
    }



    public static String captureUpdatedTTIssues(String chromosomeAPKName) throws IOException {

        String destination = Constants.REPAIR_ISSUES_PATH + chromosomeAPKName + Constants.DETECTION_TOOL_OUTPUT_SUFFIX;
        pullFileFromEmulator("issueFile", destination);
        extractTTIssuesFromOriginalFIle(destination);
        return destination;
    }

    private static void extractTTIssuesFromOriginalFIle(String originalFileIssue) {
        /***    Takes the original issues file pulled from Android and generate TT specific issue***/

    }

    public static String captureUpdatedDynamicUI(String currentActivityName, String apkPath, String chromosomeAPKName) throws IOException {
//        /*** Run the cralwing script to reach the correct state of the activity then capture the UI with UI automator***/
//        String scriptPath = Constants.CRAWLING_SCRIPTS_PATH + "/" + currentActivityName + Constants.CRAWLING_SCRIPTS_SUFFIX;
//        //(1) we already compiled the folder when we ran the detection so we need to directly install the app and then run the app
//        installAPK(apkPath, chromosomeAPKName);
//        startAnApp(mFix.getCurrentApkPackage());  // check if the command is correct and accurate
//        //(2) Once the app has started, we run the crawler script to get to the correct the activity state
//        // scripts file naming: currentActivityName+"/"+constant.CRAWLING_SCRIPTS_SUFFIX
//        String crawlingScriptFile = scriptPath;
//        // By now I should have gotten the issue recorded so I should pull the file
//
//        runCrawlingscript(crawlingScriptFile);

        /*** the commendted code above was already moved to prepareAppToGetUpdateIssueandLayout method above  so this method expect that wea are already at the correct state
         * and we are ready to dump the layout***/
        // (3) Now it is time to use UIAutomator ot dump the UI
//        uninstallApp("com.github.uiautomator");
        String dumpedFileLayout = Constants.Crawled_UI_Dynamic_Layout + chromosomeAPKName;
        String[] dumpResult = dumpActivityLayout(dumpedFileLayout);
        String xmlLayout = dumpResult[0];
        String pngLayout = dumpResult[1];
        // (4) Preprocess the dynamic layout by extacting the content of the layout
//        Merger_TT merger = new Merger_TT();

        preprocessDynamicFolder(dumpedFileLayout, dumpedFileLayout + "/refined/");
        return dumpedFileLayout + "/refined/";
    }

    public static String[] dumpActivityLayout(String dumpedFileLayout) throws IOException {
        /*** Dump the ui using UI automotor and add temp suffix | then extract the content tag using Mian's method  ***/

        // (1) if chromosome folder does not exist, create it
        File directory = new File(dumpedFileLayout);
        if (!directory.exists()) {
            directory.mkdir();
        }

        String activityName = getDeviceCurrentRunningActivityName();


        //(2) dump the layout using ui automator to the phone
        if (!activityName.equalsIgnoreCase(SALEM.getCurrentActivityName())) {  // if it is NOT the correct activity
            return null;
        }
        String cmd;
        cmd = Constants.UIAUTOMATOR_DUMP;
        runCommand(cmd, null, null);

        //(3) pull the xml layout to the pc
        String xmlFileName = SALEM.getCurrentActivityName() + ".xml";
//        cmd = Constants.PULL + " " + "/sdcard/window_dump.xml " + dumpedFileLayout + "/" + xmlFileName;
        cmd = Constants.PULL;
        cmd = String.format(cmd, "/sdcard/window_dump.xml", dumpedFileLayout + "/" + xmlFileName);
        runCommand(cmd, null, null);
        //(4) capture the screenshot
        cmd = Constants.SCREEN_CAP + " " + "/sdcard/screen.png";
        runCommand(cmd, null, null);
        //(5) pull the screenshot to the pc
        String pngFileName = dumpedFileLayout + "/" + SALEM.getCurrentActivityName() + ".png";
//        cmd = Constants.PULL + " " + "/sdcard/screen.png " + dumpedFileLayout + "/" + mFix.getCurrentActivityName() + ".png";
        cmd = Constants.PULL;
//                + " " + "/sdcard/screen.png " + dumpedFileLayout + "/" + mFix.getCurrentActivityName() + ".png";
        cmd = String.format(cmd, "/sdcard/screen.png", dumpedFileLayout + "/" + SALEM.getCurrentActivityName() + ".png");

        runCommand(cmd, null, null);

        String[] result = {xmlFileName, pngFileName};
        return result;


    }

    private static String getDeviceCurrentRunningActivityName() throws IOException {
        String cmd = Constants.GREP_ACTIVITY_NAME;
        String activityresult = runCommand(cmd, null, "grepActivityName");
        if (activityresult == null) {
            Logger.error("Error Could capture the UI");
            System.exit(1); //"Error
        }
        String activityName = null;
        if (activityresult.contains("/")) {
            String[] arr = activityresult.split("/");
            String temp = arr[1];
//                    arr= temp.split("/");
            if (temp.contains("}")) {
                activityName = temp.replace("}", "");
            }
        }
        return activityName;
    }

    public static void runCrawlingscript(String crawlingScript) throws IOException {
//        String cmd = Constants.CRAWLING_SCRIPTS_PATH + "/" + crawlingScript;
        String cmd = crawlingScript;
        cmd = "/usr/bin/python3 " + cmd;
        runCommand(cmd, Constants.CRAWLING_SCRIPTS_PATH, null);
    }

    public static void startAnApp(String packageName) throws IOException {
//        String cmd =Constants.AAPT +  "dump badging " + apkName+ " "+" | grep launchable-activity | awk '{print $2}' | sed s/name=//g | sed s/\'//g";
        String cmd = Constants.startAppCommand;
        cmd = String.format(cmd, packageName);
        runCommand(cmd, null, null);

    }

    public static void stopAnApp(String packageName) throws IOException {
//        String cmd =Constants.AAPT +  "dump badging " + apkName+ " "+" | grep launchable-activity | awk '{print $2}' | sed s/name=//g | sed s/\'//g";
        String cmd = Constants.FORCE_STOP_APP;
        cmd = String.format(cmd, packageName);
        runCommand(cmd, null, null);

    }

    public static void installAPK(String apkPath, String chromosomeAPKName,String packageName) throws IOException {
        String cmd;
        //(1) First uninstall the apk if needed for the app

        if (OwlEye.getAppNeedUninstall()) {


             cmd = OwlConstants.ADB + " uninstall " + packageName; // the current package name was set in the readIssue method
            Logger.debug("uninstalling the apk");
            runCommand(cmd, null, null);

        }


        //(2) install  the chromosome
        cmd = OwlConstants.ADB + " install " + chromosomeAPKName;
        runCommand(cmd, apkPath, null);


    }

    public static void updateLayoutWithNewValues(Node<DomNode> newLayoutRoot) {
//        //(1) Get the paths
//        String mergedLayout = mFix.getCurrentActivityMergedFilePath();
//        String newLayout = newLayoutFolder + mFix.getCurrentActivityName() + ".xml";
//
//        //(2)  Update the sizes attributes by copying them from the crawled layout to the merged layout
//        Node<DomNode> root = XMLUtils.getRoot();
        updateSizeAttributes(newLayoutRoot); // ToDo: fix ID random
        //(3) Check if there is difference in layout hirearchiy or new elements appearing or disspearing  || ToDo: Implement it
        checkifHierarchyChanged();

    }


    private static void updateSizeAttributes(Node<DomNode> crawledRoot) {
        // (1) Create the UI tree of the merged
//        Node<DomNode> crawledRoot = XMLUtils.readCrawledXML_T(newLayout);  //Just reading it to get a ne
        // (2) Update the bounds of the original instance
        iterateAndUpdateBounds(XMLUtils.getRoot(), crawledRoot);// Iterate over current instance and match it wid crawled tree and update the bounds

    }

    public static double sigmoid(double x) {
        // APril 8th
        double scalingFactor = 0.1;

        return (Math.tanh(scalingFactor * x) + 1) / 2;
        //return ((Math.atan(x) + Math.PI / 2.0) / Math.PI);
    }

    public static void iterateAndUpdateBounds(Node<DomNode> merged, Node<DomNode> crawled) {


        Queue<Node<DomNode>> q = new LinkedList<Node<DomNode>>();
        q.add(merged);
//        if (root.getChildren() != null) {
//            for (Node<DomNode> child : root.getChildren()) {
//                q.add(child);
//            }
//        }
//
//        // process descendants of the root in a bread first fashion
        while (!q.isEmpty()) {
            Node<DomNode> currMergedNode = q.remove();
//            DomNode currMergedNode = node.getData();

            String currentNodeXpath = currMergedNode.getData().getxPath();
            Node<DomNode> foundCrawledNode = XMLUtils.searchVHTreeByXpath(currentNodeXpath, crawled);// Find the node in the crawled Version
            if (foundCrawledNode != null) {
                boolean success = updateBounds(currMergedNode, foundCrawledNode);
                if (!success) {
                    Logger.error("Could not update the bounds from the crawled app");
                    System.exit(1);
                }
            }


            if (currMergedNode.getChildren() != null) {
                for (Node<DomNode> child : currMergedNode.getChildren()) {
                    q.add(child);
                }
            }
        }

    }

    public static void iterateAndUpdateBounds0(Node<DomNode> merged, Node<DomNode> crawled) {

        List<Node<DomNode>> nodes = merged.getChildren();
        if (nodes == null) {
            return;
        }
        for (int index = 0; index < nodes.size(); index++) {
//            System.out.println("processing" + nodes.get(index));

            Node<DomNode> currentNode = nodes.get(index);
            String currentNodeXpath = currentNode.getData().getxPath();
            Node<DomNode> foundCrawledNode = XMLUtils.searchVHTreeByXpath(currentNodeXpath, crawled);// Find the node in the crawled Version
            if (foundCrawledNode != null) {
                boolean success = updateBounds(currentNode, foundCrawledNode);
                if (!success) {
                    System.exit(1);
                    Logger.error("Could not update the bounds from the crawled app");
                }
            }

            iterateAndUpdateBounds0(currentNode, crawled); //We only need the root of the crawled to use it when searching for nodes


//        	for(String att: nodes[index].getAttributesArray()){
//        	    if(att.equalsIgnoreCase("resource-id")){
//
//                }
//            }
        }
    }

    private static boolean updateBounds(Node<DomNode> copyTo, Node<DomNode> copyFrom) {
        /*** get the bound from the copy from and update the bounds of copyTo ***/
        String copyId = copyFrom.getData().getId();
        String toId = copyTo.getData().getId();

        String newBounds = copyFrom.getData().getAttributes().get("bounds");
        String originalBounds = copyTo.getData().getAttributes().get("bounds");
        copyTo.getData().updateBounds(newBounds);
        return true;
    }

    private static boolean checkifHierarchyChanged() {
        return false;
    }

    public static boolean IssueExistInInitialIssues(String issueType) {
        boolean result = false;
        String currentAct = SALEM.getCurrentActivityName();
        AccessibilityScannerResults issues = SALEM.getDetectionToolResults().get("initial");// Get the list of initial issues
//        int heightIssue=issues.getHeightIssueCount();
//        int widthIssue=issues.getWidthIssueCount();

        Set<TIssue> activityIssues = issues.getListOfIssues().get(currentAct); // Get the list of issues for the current activity
        String currentType = "";
        int issueCount = 0;
        for (TIssue issue : activityIssues) {
            currentType = issue.getIssueType();
            if (currentType == null) {
                continue;
            }
            //Calculate the total number of issues
            switch (currentType) {
                case Constants.TOUCH_TARGET_HEIGHT_ISSUE:
                    issueCount = issues.getHeightIssueCount();

                    break;

                case Constants.TOUCH_TARGET_WIDTH_ISSUE:
                    issueCount = issues.getWidthIssueCount();
                    break;
            }
        }

        if (issueCount >= 0) {
            result = true;
        }
        return result;


    }

    public static void deleteAccessbFiles() throws IOException {
        //Delete all the accessibilty scanner local files in android before I scan
        String AndroidFilePath = "/data/data/com.aziz.accessibilityEval/files/*";
        String cmd = Constants.DELETE_ANDROID_FILE_CMD + AndroidFilePath;
        runCommand(cmd, null, null);
        AndroidFilePath = "/data/data/com.aziz.accessibilityEval/databases/*";
        cmd = Constants.DELETE_ANDROID_FILE_CMD + AndroidFilePath;
        runCommand(cmd, null, null);
    }

    public static void deleteIssueFileInEmulator() throws IOException {
        String cmd = String.format(Constants.DELETE_ANDROID_FILE_CMD, "/data/data/com.aziz.accessibilityEval/files/*");
        runCommand(cmd, null, null);
        cmd = String.format(Constants.DELETE_ANDROID_FILE_CMD, "/data/data/com.aziz.accessibilityEval/databases/*");
        runCommand(cmd, null, null);
    }



    public static void copyIssuesFilesFromAndroid() {

    }

    public static void pullFileFromEmulator(String fileType, String destination) throws IOException {
        String cmd = "";
        switch (fileType) {
            case "issueFile":
                cmd = String.format(Constants.PULL, SALEM.getAndroidFileIssuePath());
                cmd = cmd + " " + destination;
                break;
            case "dynamicLayout":
                cmd = String.format(Constants.PULL, SALEM.getAndroidFileIssuePath());
                cmd = cmd + " " + destination;
                break;
            case "screenShot":
                cmd = String.format(Constants.PULL, SALEM.getAndroidFileIssuePath());
                cmd = cmd + " " + destination;
                break;
        }


        runCommand(cmd, null, null);
    }


    public static void uninstallApp(String apkName) throws IOException {
        String cmd;
        if (apkName == null) {
            cmd = String.format(Constants.UNINSTALL_APK, SALEM.getCurrentApkPackage());
        } else {
            cmd = String.format(Constants.UNINSTALL_APK, apkName);
        }

        runCommand(cmd, null, null);
    }

    public static void dumpLogCat(String outPutIssueFile) throws IOException, InterruptedException {
//        ProcessBuilder pb = new ProcessBuilder(OwlConstants.ADB,"logcat","-d", "|", "grep",Constants.Accessibility_SERVICE_PACKAGE, "-B","100", "-A", "100");
//    ProcessBuilder pb = new ProcessBuilder(OwlConstants.ADB,"shell","'logcat","-d", "--pid=", "$(pidof","-s",Constants.Accessibility_SERVICE_PACKAGE,")'");
//    shell 'logcat -d --pid=$(pidof -s "+Accessibility_SERVICE_PACKAGE+")'
        ProcessBuilder pb = new ProcessBuilder(Constants.LOGCAT_DUMP_FILE_SCRIPT);

        pb.redirectOutput(new File(outPutIssueFile));
        pb.redirectErrorStream(true); // redirect the error stream to stdout
        Process p = pb.start(); // start the process
        // start a new thread to handle the stream input
        new Thread(new LogCatRunnable(p)).start();
        p.waitFor();  // wait if needed
    }

    public static int elementToSegmenets(String xpath, Set<Segment> segments) {
        for (Segment seg : segments
        ) {
            List<String> members = seg.getMembers();
            for (String memberXpath : members) {
                if (memberXpath.equalsIgnoreCase(xpath)) {
                    return seg.getId();
                }
            }

        }
        return -1; // not found
    }

    public static Element findElementByXpathANDProperty(List<Element> dependentElements, String xpath, String property) {

        /*** This method is used to search list of elements from the list of elements to change when generating changes to apply to an apk
         *
         */
        for (Element element : dependentElements
        ) {
            if (element.getXpath().equalsIgnoreCase(xpath) && element.getCssProperty().equalsIgnoreCase(property)) {
                return element;
            }
        }
        return null;
    }

    public static Element findElementbyXpath(List<Element> dependentElements, String heightXpath) {

        /*** This method is used to search list of elements from the list of elements to change when generating changes to apply to an apk
         *
         */
        for (Element element : dependentElements
        ) {
            if (element.getXpath().equalsIgnoreCase(heightXpath)) {
                return element;
            }
        }
        return null;
    }

    public static boolean canParentAccomidateChange(String childXpath, String parentXpath, String property, String childNewValue) {
        /*** in this method we should decide if a parent can accommodate the new value by comparing
         *   available size for parent compared to its children either in height or width
         *
         */

        // Right now I am just doing a simple check if parent is less than the new value then update the parent with the new value
        Node<DomNode> parentNode = XMLUtils.searchVHTreeByXpath(parentXpath, XMLUtils.getRoot());
        String parentDynamicValue = Util.getDynamicValueInDDP(parentNode.getData(), property); // get the dynamic value for the parent to see if it can fit the increase
        double parentVal = Util.getNumbersFromString(parentDynamicValue).get(0);
        double childVal = Util.getNumbersFromString(childNewValue).get(0);
        // randomly dding this 4   | in reality we should consider space carefully
        boolean res = parentVal > childVal + 4;
        return false;

    }

    public static boolean isIssueInsegemnt(TIssue issue, Segment seg) {
        // check if a TT issue from the detection is part of the segment
        boolean isPartofSegment = false;
        String isseuId = issue.getWidgetID();
        Node<DomNode> node = XMLUtils.searchByID_T(isseuId, issue.getClassName());
        if (node != null) {
            String nodeXpath = node.getData().getxPath();
            if (seg.isNodeInSegment(nodeXpath)) {
                isPartofSegment = true;
            }

        }
        return isPartofSegment;
    }

    public static void setScreenEdge(String screenRes) {
        //Physical size: 1440x2560
        String dim = screenRes.split(":")[1].trim();

        int x2 = Integer.valueOf(dim.split("x")[0]);
        int y2 = Integer.valueOf(dim.split("x")[1]);

        SALEM.CURRENT_SCREEN_COORDINATE = new int[]{0, 0, x2, y2};
    }

    public static boolean isNearScreenEdge(int[] coords) {

        if (coords[0] == SALEM.CURRENT_SCREEN_COORDINATE[0]) {
            return true;
        }
        if (coords[1] == SALEM.CURRENT_SCREEN_COORDINATE[1]) {
            return true;
        }
        if (coords[2] == SALEM.CURRENT_SCREEN_COORDINATE[2]) {
            return true;
        }
        return coords[3] == SALEM.CURRENT_SCREEN_COORDINATE[3];

    }
//        /home/ali/Android/Sdk/platform-tools/adb logcat -d > /home/ali/AppSet/accessibility/scripts/apks_folder/testIssueList/logcat/com.adzenze.FashionDesignFlatSketchXX.txt

    // mimics stream gobbler, but allows user to process the result
    public static class LogCatRunnable implements Runnable {
        Process p;
        BufferedReader br;

        LogCatRunnable(Process p) {
            this.p = p;
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(p.getInputStream());

                br = new BufferedReader(isr);

                String line = null;
                while ((line = br.readLine()) != null) {
                    Logger.trace(line);
                    // do something with the output here...
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    public static List<GAChromosome> calculateFitnessScoreForPopulationThreads(List<GAChromosome> population, String searchStep) throws IOException, InterruptedException {

        /*** Here we should run the population on Android ***/
        long startFitnessTime = System.nanoTime();
        int chromosomeCount = 0;
        List<GAChromosome> updatedPopulation = new ArrayList<>();

        //Ali Get each chromosomem, write each to a  seperate apk then get the score
        for (GAChromosome chromosome : population) {
            long startChromosomeRun = System.nanoTime();
            XMLUtils.resetInstance();
            XMLUtils.getInstance(SALEM.getCurrentActivityMergedFilePath());
            //(1) Write the chromosome gens to an new decompiled folder ( option (a) run all of them at once or (b)one at a time then run the detection and go back and run the next, FOR NOW I go with "a")
            // the folder for the apk should be with the suffix _chromosomeID
            String chromosomeIdentifier = "chromosome_" + searchStep + "_" + GASearch.getGenerationCount() + "_" + chromosomeCount;
            chromosome.setChromosomeIdentifier(chromosomeIdentifier);
            // Ali write the changes of the chromosome locally
            String outPutFile = SALEM.getOriginalDecompiled() + "_" + chromosomeIdentifier;
            /*** This will write the whole chromosome to the folder with the suffix= chromosomeIdentifier ***/
//            String chromosomeOutPutFolder = Util.WriteChromosomeChangesToFile(chromosome, outPutFile);


            String chromosomeOutPutFolder = Util.WriteChromosomeChangesToFileSegmentRelationGraph(chromosome, outPutFile);
            /*** (2) Run the detection tool and output the detection tool with chromosomeID suffix
             // Once the folder is written with the chromsome changes, we will start the run detection process (a) compile the app, (b) sign it,
             (c) move the apk to detectio tool folder, (d) run the detection tool, (e) parse the output, (f) store it in the google api object, (g) add it to issue map with key as chromosome id
             ***/


            //(a,b) Compile the modified apk

            String[] apkRes = Util.compileApp(chromosomeIdentifier, chromosomeOutPutFolder, Constants.Compiled_output_path);

            String newApkName = apkRes[0];  // the new apk name
            String compiledAPKPath = apkRes[1];  // The full path for apk

            chromosome.setAPKPath(compiledAPKPath);
            chromosome.setDecompiledPath(chromosomeOutPutFolder);
            //(c) Move the apk to the detection tool folder
            Util.moveAPKtoFolder(compiledAPKPath, SALEM.Detection_Tool_APK_PATH);
            //(d) Run detection Tool
            //
            String detectionOutPutPath = ""; //Todo: Where?
//            detectionOutPutPath = Constants.Detection_Tool_APK_PATH + "/" + newApkName + Constants.TOUCH_TARGET_ISSUES_OUTPUT_SUFFIX;
//            detectionOutPutPath = Constants.Detection_Tool_APK_PATH + "/" + newApkName;
            detectionOutPutPath = SALEM.Detection_Tool_APK_PATH;
//
            //   AccessibilityDetectionToolAPI.runDetectionTool(newApkName, detectionOutPutPath);
//
//
//            // (3) Parse the file,then: Store the object GoogleAPIResutls.java and add it to mfix.list of issues
//
//            Util.parseIssuesFile(detectionOutPutPath, newApkName, chromosomeIdentifier); // Once this is done we have stored the issues in the map and we have already stored

            // (4) calculate the accessibility score

            //(5) run the app and get the UI of the activity and the screenshot, preprocess the layout in the refined folder
//            String newLayoutFolder = Util.captureUpdatedDynamicUI(mFix.getCurrentActivityName(), detectionOutPutPath, newApkName);
            /*** I changed the logic now the detection is captured during the running of the app and crawling script so no need to separately running the detection***/

            // (5-a) run the app and the crawling script to get it ready to the correct state so we can then go ahead to get updated issue list and then crawler
            String newApkNameIssue = newApkName.replace(".apk", "");

//            Util.prepareAppToGetToCorrectState(mFix.getCurrentActivityName(), detectionOutPutPath,newApkName); // here the apk name for the chromosome

            Util.prepareAppToGetToCorrectStateLOGCAT(SALEM.getCurrentActivityName(), detectionOutPutPath, newApkName); // here the apk name for the chromosome
            if (Constants.Accessibility_SCORE_APPROACH.equalsIgnoreCase("FROM_ISSUES")) {

                String destination = Constants.LOGCAT_FILE_DUMP_PATH; // Where to dump the logcat

                String output = Constants.LOGCAT_FILE_DUMP_PATH + "/" + newApkNameIssue + Constants.DETECTION_TOOL_LOGCAT_SUFFIX;
                //output=Constants.LOGCAT_FILE_DUMP_PATH+"/88.txt";
                String filteredIssueoutput = Constants.LOGCAT_FILE_DUMP_PATH + "/" + newApkNameIssue + "" + Constants.TOUCH_TARGET_ISSUES_OUTPUT_SUFFIX;

                Util.dumpLogCat(output); //Dump the logcat to the the logcat_file_dump_path
                ReadIssues.parseIssuesFromLogCat(output, filteredIssueoutput); //dumpLogCat will dump the log to a file then we read the file and parse it then write it to the file
                Util.parseIssuesFile_LogCat(filteredIssueoutput, newApkNameIssue, chromosomeIdentifier); // Once this is done we have stored the issues in the map and we have already stored
            }

            //Comment the above 5 lines and comment the 3 below to capture the csv files from Android instead of logcat
//
//             String originalIssuePath= Util.captureUpdatedTTIssues(newApkNameIssue); //here we need the issue file
//            ReadIssues.parseOriginalIssueFile(originalIssuePath,newApkNameIssue,Constants.REPAIR_ISSUES_PATH); //Filter and write the TT issues to the file
            //            Util.parseIssuesFile(Constants.REPAIR_ISSUES_PATH, newApkNameIssue, chromosomeIdentifier); // Once this is done we have stored the issues in the map and we have already stored


//            System.exit(0);
//            for (String activityName : mFix.getProblematicActivities()
//            ) {
//                if (activityName.equalsIgnoreCase(mFix.getCurrentActivityName())) { // get the current activity
//                    GoogleAPIResults chromosomeIssues = mFix.getDetectionToolResults().get(chromosomeIdentifier);
//                    double accessibilityScore = chromosomeIssues.calculateAccessibilityScore();
//                    System.out.println(accessibilityScore);
//                }
//
//
//            }
            // (6) update the merged UI based the updated UI with the new size  , to do that we match merged layout with the new dynamic layout and then copy the new bounds values to merged layout
            // The app should be running and in the correct state
            String newLayoutFolder = Util.captureUpdatedDynamicUI(SALEM.getCurrentActivityName(), detectionOutPutPath, newApkName);
            String chromosomedChrawled = newLayoutFolder.split("/refined")[0];
            chromosome.setCrawledPath(chromosomedChrawled);
//            Util.updateLayoutWithNewValues(newLayoutFolder);

            // (7) Calculate the total fitness score
            FitnessFunction ff = new FitnessFunction();
            ff.calculateFitnessScore(chromosome, newLayoutFolder);
            // (8) increment chromosome counter
            chromosomeCount++;
            updatedPopulation.add(chromosome);
            long endChromosomeRun = System.nanoTime();
            if (!SALEM.runtime_map.containsKey("chromosomeCal")) {
                SALEM.runtime_map.put("chromosomeCal", 0L);
            }
            long curr = SALEM.runtime_map.get("chromosomeCal");
            long newTime = curr + (endChromosomeRun - startChromosomeRun);
            SALEM.runtime_map.put("chromosomeCal", newTime);
            SALEM.chromosomeCalCount++;
        }
        long endFitnessTime = System.nanoTime();
        if (!SALEM.runtime_map.containsKey("populationCal")) {
            SALEM.runtime_map.put("populationCal", 0L);
        }
        long curr = SALEM.runtime_map.get("populationCal");
        long newTime = curr + (endFitnessTime - startFitnessTime);
        SALEM.runtime_map.put("populationCal", newTime);
        SALEM.populationCalCount++;



        return updatedPopulation;
        //TOdo: I added that as placeholder Ali
//        return null;
    }

    public static List<GAChromosome> calculateFitnessScoreForPopulation(List<GAChromosome> population, String searchStep) throws IOException, InterruptedException {

        /*** Here we should run the population on Android ***/
        long startFitnessTime = System.nanoTime();
        int chromosomeCount = 0;
        List<GAChromosome> updatedPopulation = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(SALEM.MAX_NO_THREADS_FOR_APP_COMPILING);

        //Ali Get each chromosomem, write each to a  seperate apk then get the score
        for (GAChromosome chromosome : population) {
            XMLUtils.resetInstance();
            XMLUtils.getInstance(SALEM.getCurrentActivityMergedFilePath());
            String chromosomeIdentifier = "chromosome_" + searchStep + "_" + GASearch.getGenerationCount() + "_" + chromosomeCount;
            chromosome.setChromosomeIdentifier(chromosomeIdentifier);
            String outPutFile = SALEM.getOriginalDecompiled() + "_" + chromosomeIdentifier;
            String chromosomeOutPutFolder = Util.WriteChromosomeChangesToFileSegmentRelationGraph(chromosome, outPutFile);
            chromosome.setChromosomeOutPutFolder(chromosomeOutPutFolder);
            chromosomeCount++;
        }
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (GAChromosome chromosome : population) {
//            XMLUtils.resetInstance();
//            XMLUtils.getInstance(mFix.getCurrentActivityMergedFilePath());
//            String chromosomeIdentifier = "chromosome_" + searchStep + "_" + GASearch.getGenerationCount() + "_" + chromosomeCount;
//            chromosome.setChromosomeIdentifier(chromosomeIdentifier);
//            String outPutFile = mFix.getOriginalDecompiled() + "_" + chromosomeIdentifier;
//            String chromosomeOutPutFolder = Util.WriteChromosomeChangesToFileSegmentRelationGraph(chromosome, outPutFile);
            String chromosomeOutPutFolder = chromosome.getChromosomeOutPutFolder();
            String chromosomeIdentifier = chromosome.getChromosomeIdentifier();
            Runnable worker = new PrepareAppRunner(chromosomeIdentifier, chromosome, chromosomeOutPutFolder);
            executor.execute(worker);
        }
        executor.shutdown();
        // Wait until all threads are finish
        while (!executor.isTerminated()) {

        }
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Logger.trace("\nFinished all threads to prepare apps.");

        String detectionOutPutPath = ""; //Todo: Where?
        detectionOutPutPath = SALEM.Detection_Tool_APK_PATH;
//
        //   AccessibilityDetectionToolAPI.runDetectionTool(newApkName, detectionOutPutPath);
//
//
//            // (3) Parse the file,then: Store the object GoogleAPIResutls.java and add it to mfix.list of issues
//
//            Util.parseIssuesFile(detectionOutPutPath, newApkName, chromosomeIdentifier); // Once this is done we have stored the issues in the map and we have already stored

        // (4) calculate the accessibility score

        //(5) run the app and get the UI of the activity and the screenshot, preprocess the layout in the refined folder
//            String newLayoutFolder = Util.captureUpdatedDynamicUI(mFix.getCurrentActivityName(), detectionOutPutPath, newApkName);
        /*** I changed the logic now the detection is captured during the running of the app and crawling script so no need to separately running the detection***/

        // (5-a) run the app and the crawling script to get it ready to the correct state so we can then go ahead to get updated issue list and then crawler
        for (GAChromosome chromosome : population) {

            String newApkName = chromosome.getNewApkName();
            boolean skipLayout = false;
            String result = Util.prepareAppToGetToCorrectStateLOGCAT(SALEM.getCurrentActivityName(), detectionOutPutPath, newApkName); // here the apk name for the chromosome
            if (result == null) {
                SALEM.resetDeviceUIUsingADB();
                result = Util.prepareAppToGetToCorrectStateLOGCAT(SALEM.getCurrentActivityName(), detectionOutPutPath, newApkName);
                if (result == null) {
                    skipLayout = true;
                }
            }
            String newLayoutFolder;

            if (!skipLayout) {
                newLayoutFolder = Util.captureUpdatedDynamicUI(SALEM.getCurrentActivityName(), detectionOutPutPath, newApkName);
                String chromosomedChrawled = newLayoutFolder.split("/refined")[0];
                chromosome.setCrawledPath(chromosomedChrawled);
//            Util.updateLayoutWithNewValues(newLayoutFolder);
            } else {
                //we skip reading layout because we are not getting the correct activity
                newLayoutFolder = null;
            }
            // (7) Calculate the total fitness score
            FitnessFunction ff = new FitnessFunction();
            ff.calculateFitnessScore(chromosome, newLayoutFolder);
            // (8) increment chromosome counter
            chromosomeCount++;
            updatedPopulation.add(chromosome);

//            long endChromosomeRun = System.nanoTime();
            if (!SALEM.runtime_map.containsKey("chromosomeCal")) {
                SALEM.runtime_map.put("chromosomeCal", 0L);
            }
            long curr = SALEM.runtime_map.get("chromosomeCal");
//            long newTime = curr + (endChromosomeRun - startChromosomeRun);
//            mFix.runtime_map.put("chromosomeCal", newTime);
            SALEM.chromosomeCalCount++;
        }
//        for (GAChromosome chromosome : population) {
//            long startChromosomeRun = System.nanoTime();
//            XMLUtils.resetInstance();
//            XMLUtils.getInstance(mFix.getCurrentActivityMergedFilePath());
//            //(1) Write the chromosome gens to an new decompiled folder ( option (a) run all of them at once or (b)one at a time then run the detection and go back and run the next, FOR NOW I go with "a")
//            // the folder for the apk should be with the suffix _chromosomeID
//            String chromosomeIdentifier = "chromosome_" + searchStep + "_" + GASearch.getGenerationCount() + "_" + chromosomeCount;
//            chromosome.setChromosomeIdentifier(chromosomeIdentifier);
//            // Ali write the changes of the chromosome locally
//            String outPutFile = mFix.getOriginalDecompiled() + "_" + chromosomeIdentifier;
//            /*** This will write the whole chromosome to the folder with the suffix= chromosomeIdentifier ***/
////            String chromosomeOutPutFolder = Util.WriteChromosomeChangesToFile(chromosome, outPutFile);
//            String chromosomeOutPutFolder = Util.WriteChromosomeChangesToFileSegmentRelationGraph(chromosome, outPutFile);
//            /*** (2) Run the detection tool and output the detection tool with chromosomeID suffix
//             // Once the folder is written with the chromsome changes, we will start the run detection process (a) compile the app, (b) sign it,
//             (c) move the apk to detectio tool folder, (d) run the detection tool, (e) parse the output, (f) store it in the google api object, (g) add it to issue map with key as chromosome id
//             ***/
//
//
//            //(a,b) Compile the modified apk
//
//            String[] apkRes = Util.compileApp(chromosomeIdentifier, chromosomeOutPutFolder, Constants.Compiled_output_path);
//
//            String newApkName = apkRes[0];  // the new apk name
//            String compiledAPKPath = apkRes[1];  // The full path for apk
//
//            chromosome.setAPKPath(compiledAPKPath);
//            chromosome.setDecompiledPath(chromosomeOutPutFolder);
//            //(c) Move the apk to the detection tool folder
//            Util.moveAPKtoFolder(compiledAPKPath, mFix.Detection_Tool_APK_PATH);
//            //(d) Run detection Tool
//            //
//            String detectionOutPutPath = ""; //Todo: Where?
////            detectionOutPutPath = Constants.Detection_Tool_APK_PATH + "/" + newApkName + Constants.TOUCH_TARGET_ISSUES_OUTPUT_SUFFIX;
////            detectionOutPutPath = Constants.Detection_Tool_APK_PATH + "/" + newApkName;
//            detectionOutPutPath = mFix.Detection_Tool_APK_PATH;
////
//            //   AccessibilityDetectionToolAPI.runDetectionTool(newApkName, detectionOutPutPath);
////
////
////            // (3) Parse the file,then: Store the object GoogleAPIResutls.java and add it to mfix.list of issues
////
////            Util.parseIssuesFile(detectionOutPutPath, newApkName, chromosomeIdentifier); // Once this is done we have stored the issues in the map and we have already stored
//
//            // (4) calculate the accessibility score
//
//            //(5) run the app and get the UI of the activity and the screenshot, preprocess the layout in the refined folder
////            String newLayoutFolder = Util.captureUpdatedDynamicUI(mFix.getCurrentActivityName(), detectionOutPutPath, newApkName);
//            /*** I changed the logic now the detection is captured during the running of the app and crawling script so no need to separately running the detection***/
//
//            // (5-a) run the app and the crawling script to get it ready to the correct state so we can then go ahead to get updated issue list and then crawler
//            String newApkNameIssue = newApkName.replace(".apk", "");
//
////            Util.prepareAppToGetToCorrectState(mFix.getCurrentActivityName(), detectionOutPutPath,newApkName); // here the apk name for the chromosome
//            boolean skipLayout = false;
//            String result = Util.prepareAppToGetToCorrectStateLOGCAT(mFix.getCurrentActivityName(), detectionOutPutPath, newApkName); // here the apk name for the chromosome
//            if (result == null) {
//                mFix.resetDeviceUIUsingADB();
//                result = Util.prepareAppToGetToCorrectStateLOGCAT(mFix.getCurrentActivityName(), detectionOutPutPath, newApkName);
//                if (result == null) {
//                    skipLayout = true;
//                }
//            }
//            if (Constants.Accessibility_SCORE_APPROACH.equalsIgnoreCase("FROM_ISSUES")) {
//
//                String destination = Constants.LOGCAT_FILE_DUMP_PATH; // Where to dump the logcat
//
//                String output = Constants.LOGCAT_FILE_DUMP_PATH + "/" + newApkNameIssue + Constants.DETECTION_TOOL_LOGCAT_SUFFIX;
//                //output=Constants.LOGCAT_FILE_DUMP_PATH+"/88.txt";
//                String filteredIssueoutput = Constants.LOGCAT_FILE_DUMP_PATH + "/" + newApkNameIssue + "" + Constants.TOUCH_TARGET_ISSUES_OUTPUT_SUFFIX;
//
//                Util.dumpLogCat(output); //Dump the logcat to the the logcat_file_dump_path
//                ReadIssues.parseIssuesFromLogCat(output, filteredIssueoutput); //dumpLogCat will dump the log to a file then we read the file and parse it then write it to the file
//                Util.parseIssuesFile_LogCat(filteredIssueoutput, newApkNameIssue, chromosomeIdentifier); // Once this is done we have stored the issues in the map and we have already stored
//            }
//
//            //Comment the above 5 lines and comment the 3 below to capture the csv files from Android instead of logcat
////
////             String originalIssuePath= Util.captureUpdatedTTIssues(newApkNameIssue); //here we need the issue file
////            ReadIssues.parseOriginalIssueFile(originalIssuePath,newApkNameIssue,Constants.REPAIR_ISSUES_PATH); //Filter and write the TT issues to the file
//            //            Util.parseIssuesFile(Constants.REPAIR_ISSUES_PATH, newApkNameIssue, chromosomeIdentifier); // Once this is done we have stored the issues in the map and we have already stored
//
//
////            System.exit(0);
////            for (String activityName : mFix.getProblematicActivities()
////            ) {
////                if (activityName.equalsIgnoreCase(mFix.getCurrentActivityName())) { // get the current activity
////                    GoogleAPIResults chromosomeIssues = mFix.getDetectionToolResults().get(chromosomeIdentifier);
////                    double accessibilityScore = chromosomeIssues.calculateAccessibilityScore();
////                    System.out.println(accessibilityScore);
////                }
////
////
////            }
//            // (6) update the merged UI based the updated UI with the new size  , to do that we match merged layout with the new dynamic layout and then copy the new bounds values to merged layout
//            // The app should be running and in the correct state
//            String newLayoutFolder;
//            if (!skipLayout) {
//                newLayoutFolder = Util.captureUpdatedDynamicUI(mFix.getCurrentActivityName(), detectionOutPutPath, newApkName);
//                String chromosomedChrawled = newLayoutFolder.split("/refined")[0];
//                chromosome.setCrawledPath(chromosomedChrawled);
////            Util.updateLayoutWithNewValues(newLayoutFolder);
//            } else {
//                //we skip reading layout because we are not getting the correct activity
//                newLayoutFolder = null;
//            }
//            // (7) Calculate the total fitness score
//            FitnessFunction ff = new FitnessFunction();
//            ff.calculateFitnessScore(chromosome, newLayoutFolder);
//            // (8) increment chromosome counter
//            chromosomeCount++;
//            updatedPopulation.add(chromosome);
//            long endChromosomeRun = System.nanoTime();
//            if (!mFix.runtime_map.containsKey("chromosomeCal")) {
//                mFix.runtime_map.put("chromosomeCal", 0L);
//            }
//            long curr = mFix.runtime_map.get("chromosomeCal");
//            long newTime = curr + (endChromosomeRun - startChromosomeRun);
//            mFix.runtime_map.put("chromosomeCal", newTime);
//            mFix.chromosomeCalCount++;
//        }


        long endFitnessTime = System.nanoTime();
        if (!SALEM.runtime_map.containsKey("populationCal")) {
            SALEM.runtime_map.put("populationCal", 0L);
        }
        long curr = SALEM.runtime_map.get("populationCal");
        long newTime = curr + (endFitnessTime - startFitnessTime);
        SALEM.runtime_map.put("populationCal", newTime);
        SALEM.populationCalCount++;


//        // directory to store chromsome jsons to be sent to the cloud instances
//        String jsonOutDirPath = mFix.getOutputFolderPath() + File.separatorChar + "aws_out";
//        if (!new File(jsonOutDirPath).exists()) {
//            new File(jsonOutDirPath).mkdir();
//        }
//
//        // directory to store jsons coming from cloud instances
//        String jsonInDirPath = mFix.getOutputFolderPath() + File.separatorChar + "aws_in";
//        if (!new File(jsonInDirPath).exists()) {
//            new File(jsonInDirPath).mkdir();
//        }
//
//        GsonBuilder builder = new GsonBuilder();
//        builder.excludeFieldsWithModifiers(Modifier.TRANSIENT);
//        Gson gson = builder.create();
//
//        long startTimeOnCloud = System.nanoTime();
////        ExecutorService executor = Executors.newFixedThreadPool(mFix.getAwsInstances().size());
//        int chromosomeCount = 0;
//        //Ali Get each chromosomem, write each to a  seperate apk then get the score
//        for (GAChromosome chromosome : population) {
//            // prepare chromosome to be sent to the cloud by creating its json
//            String chromosomeIdentifier = "chromosome_" + searchStep + "_" + GASearch.getGenerationCount() + "_" + chromosomeCount;
//            chromosome.setChromosomeIdentifier(chromosomeIdentifier);
//            String chromsomeJsonFilepath = jsonOutDirPath + File.separatorChar + chromosomeIdentifier + ".json";
//            try (FileWriter writer = new FileWriter(chromsomeJsonFilepath)) {
//                gson.toJson(chromosome, writer);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            // run the gene on the cloud instance
//
//            // Ali run the gene locally
//            write_chromosome_to_apk(chromosome);
//            String instance = Util.getInstance(chromosomeCount);
//            System.out.println("Sending " + chromosomeIdentifier + " to " + instance);
//            Runnable worker = new FitnessFunctionCloudRunnable(instance, chromsomeJsonFilepath, jsonInDirPath);
//            executor.execute(worker);
//
//            chromosomeCount++;
//        }
//        executor.shutdown();
//        // Wait until all threads are finish
//        while (!executor.isTerminated()) {
//
//        }
//        long endTimeOnCloud = System.nanoTime();
//        System.out.println("Time spent on cloud for fitness function calculations = " + Util.convertNanosecondsToSeconds((endTimeOnCloud - startTimeOnCloud)) + " sec");
//        System.out.println("\nFinished all threads of runInstance for fitness function calculations");
//
//        // convert all updated chromosome json obtained from cloud to objects
//        Set<String> chromosomeFilePaths = new HashSet<String>();
//        for (File json : new File(jsonInDirPath).listFiles()) {
//            if (json.isFile() && json.getName().startsWith("chromosome_" + searchStep + "_" + GASearch.getGenerationCount()) && json.getName().endsWith("json")) {
//                chromosomeFilePaths.add(json.getAbsolutePath());
//            }
//        }
//
//        // update population with the new chromosomes
//        List<GAChromosome> updatedPopulation = new ArrayList<>();
//        for (String json : chromosomeFilePaths) {
//            try (Reader reader = new FileReader(json)) {
//                GAChromosome chromosome = gson.fromJson(reader, GAChromosome.class);
//                fitnessCalls++;
//                fitnessTimeInSec = fitnessTimeInSec + chromosome.getFitnessFunctionObj().fitnessTimeInSec;
//                updatedPopulation.add(chromosome);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        // delete aws in and out directories
//		/*try
//		{
//			FileUtils.deleteDirectory(new File(jsonOutDirPath));
//			FileUtils.deleteDirectory(new File(jsonInDirPath));
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}*/
//
        return updatedPopulation;
        //TOdo: I added that as placeholder Ali
//        return null;
    }

    public static boolean isElementCloseToAnotherWITHOUDLEFTRIGHTTOP(DomNode e, DomNode n, double distance) {
        distance = distance * Constants.PHONE_DENSITY;
        String eID = e.getId();
        String nID = n.getId();
        Rectangle eRect = e.getCoord();
        Rectangle nRect = n.getCoord();
        String approach = "centersNOT";
        if (approach.equalsIgnoreCase("centers")) {
//            double eXCenter = (eRect.x +  eRect.width) / 2.0;
//            double eYCenter = (eRect.y + eRect.height) / 2.0;
//            double nXCenter = (nRect.x +  nRect.width) / 2.0;
//            double nYCenter = (nRect.y + nRect.height) / 2.0;
            int eXCenter = (eRect.x + eRect.width) / 2;
            int eYCenter = (eRect.y + eRect.height) / 2;
            int nXCenter = (nRect.x + nRect.width) / 2;
            int nYCenter = (nRect.y + nRect.height) / 2;
            double centerSpace = Util.getEuclideanDistanceBetweenPoints(eXCenter, eYCenter, nXCenter, nYCenter);


            double width_dist;
            //   if(eRect.x>=nRect.x) {
            width_dist = abs(eXCenter - nXCenter) - (eRect.width + nRect.width) / 2;
//            }else{
//                width_dist = abs(nRect.x - eRect.x) - (nRect.width + eRect.width) / 2;
//
//            }
            width_dist = abs(width_dist);
            double height_dist = abs(eRect.y - nRect.y) - (eRect.height + nRect.height) / 2;
            height_dist = abs(height_dist);
            double min = Double.min(width_dist, height_dist);
            return true;
        } else {


            double half1 = Util.getEuclideanDistanceBetweenPoints(eRect.x + eRect.width, eRect.y / 2, nRect.x, nRect.y / 2);
            double half2 = Util.getEuclideanDistanceBetweenPoints(eRect.x + eRect.width, eRect.y / 2, (nRect.x + nRect.width), nRect.y / 2);
            double half3 = Util.getEuclideanDistanceBetweenPoints(eRect.x, eRect.y / 2, nRect.x, (nRect.y / 2));
            double half4 = Util.getEuclideanDistanceBetweenPoints(eRect.x, eRect.y / 2, (nRect.x + nRect.width), (nRect.y / 2));

            double half5 = Util.getEuclideanDistanceBetweenPoints((eRect.x + eRect.width) / 2, eRect.y, (nRect.x + nRect.width) / 2, nRect.y);
            double half6 = Util.getEuclideanDistanceBetweenPoints((eRect.x + eRect.width) / 2, eRect.y, (nRect.x + nRect.width) / 2, nRect.y + nRect.height);
            double half7 = Util.getEuclideanDistanceBetweenPoints((eRect.x + eRect.width) / 2, eRect.y + eRect.height, (nRect.x + nRect.width) / 2, nRect.y);
            double half8 = Util.getEuclideanDistanceBetweenPoints((eRect.x + eRect.width) / 2, eRect.y + eRect.height, (nRect.x + nRect.width) / 2, nRect.y + nRect.height);
            if (half1 < distance || half2 < distance || half3 < distance || half4 < distance ||
                    half5 < distance || half6 < distance || half7 < distance || half8 < distance) {
                int x = 5;
                //return true;
            }

            double eTLnTL = Util.getEuclideanDistanceBetweenPoints(eRect.x, eRect.y, nRect.x, nRect.y);
            double eTLnTR = Util.getEuclideanDistanceBetweenPoints(eRect.x, eRect.y, (nRect.x + nRect.width), nRect.y);
            double eTLnBL = Util.getEuclideanDistanceBetweenPoints(eRect.x, eRect.y, nRect.x, (nRect.y + nRect.height));
            double eTLnBR = Util.getEuclideanDistanceBetweenPoints(eRect.x, eRect.y, (nRect.x + nRect.width), (nRect.y + nRect.height));

            double eTRnTL = Util.getEuclideanDistanceBetweenPoints((eRect.x + eRect.width), eRect.y, nRect.x, nRect.y);
            double eTRnTR = Util.getEuclideanDistanceBetweenPoints((eRect.x + eRect.width), eRect.y, (nRect.x + nRect.width), nRect.y);
            double eTRnBL = Util.getEuclideanDistanceBetweenPoints((eRect.x + eRect.width), eRect.y, nRect.x, (nRect.y + nRect.height));
            double eTRnBR = Util.getEuclideanDistanceBetweenPoints((eRect.x + eRect.width), eRect.y, (nRect.x + nRect.width), (nRect.y + nRect.height));

            double eBLnTL = Util.getEuclideanDistanceBetweenPoints(eRect.x, (eRect.y + eRect.height), nRect.x, nRect.y);
            double eBLnTR = Util.getEuclideanDistanceBetweenPoints(eRect.x, (eRect.y + eRect.height), (nRect.x + nRect.width), nRect.y);
            double eBLnBL = Util.getEuclideanDistanceBetweenPoints(eRect.x, (eRect.y + eRect.height), nRect.x, (nRect.y + nRect.height));
            double eBLnBR = Util.getEuclideanDistanceBetweenPoints(eRect.x, (eRect.y + eRect.height), (nRect.x + nRect.width), (nRect.y + nRect.height));

            double eBRnTL = Util.getEuclideanDistanceBetweenPoints((eRect.x + eRect.width), (eRect.y + eRect.height), nRect.x, nRect.y);
            double eBRnTR = Util.getEuclideanDistanceBetweenPoints((eRect.x + eRect.width), (eRect.y + eRect.height), (nRect.x + nRect.width), nRect.y);
            double eBRnBL = Util.getEuclideanDistanceBetweenPoints((eRect.x + eRect.width), (eRect.y + eRect.height), nRect.x, (nRect.y + nRect.height));
            double eBRnBR = Util.getEuclideanDistanceBetweenPoints((eRect.x + eRect.width), (eRect.y + eRect.height), (nRect.x + nRect.width), (nRect.y + nRect.height));
            double[] array = {eTLnTL, eTLnTR, eTLnBL, eTLnBR,
                    eTRnTL, eTRnTR, eTRnBL, eTRnBR,
                    eBLnTL, eBLnTR, eBLnBL, eBLnBR,
                    eBRnTL, eBRnTR, eBRnBL, eBRnBR};

            return eTLnTL < distance || eTLnTR < distance || eTLnBL < distance || eTLnBR < distance ||
                    eTRnTL < distance || eTRnTR < distance || eTRnBL < distance || eTRnBR < distance ||
                    eBLnTL < distance || eBLnTR < distance || eBLnBL < distance || eBLnBR < distance ||
                    eBRnTL < distance || eBRnTR < distance || eBRnBL < distance || eBRnBR < distance;
        }
    }

    public static double[] isElementCloseToAnotherWithDistance(Node<DomNode> node1, Node<DomNode> node2, double distance) {
        distance = distance * Constants.PHONE_DENSITY;
        DomNode e = node1.getData();
        DomNode n = node2.getData();
        String eID = e.getId();
        String nID = n.getId();
        Rectangle eRect = e.getCoord();
        Rectangle nRect = n.getCoord();
        double isClose = 0.0;
        NeighborEdge neighborEdge = new NeighborEdge(new LayoutNode(node1),
                new LayoutNode(node2));
        boolean leftRight = neighborEdge.isStrictLeftRight(new LayoutNode(node1),
                new LayoutNode(node2));
        boolean topbottom = neighborEdge.isStrictTopBottom(new LayoutNode(node1),
                new LayoutNode(node2));
        double smallestDistance = Double.MAX_VALUE;
        double[] t;
        if (leftRight) {
            double e1x2 = node1.getData().x + node1.getData().width;
            double e2x1 = node2.getData().x;
            smallestDistance = abs(e1x2 - e1x2);
            if (smallestDistance < distance) {
                isClose = 1;
            }
        } else if (topbottom) {
            double e1y2 = node1.getData().y + node1.getData().height;
            double e2y1 = node2.getData().y;
            smallestDistance = abs(e1y2 - e2y1);
            if (smallestDistance < distance) {
                isClose = 1;
            }

        } else {
            double eTLnTL = Util.getEuclideanDistanceBetweenPoints(eRect.x, eRect.y, nRect.x, nRect.y);
            double eTLnTR = Util.getEuclideanDistanceBetweenPoints(eRect.x, eRect.y, (nRect.x + nRect.width), nRect.y);
            double eTLnBL = Util.getEuclideanDistanceBetweenPoints(eRect.x, eRect.y, nRect.x, (nRect.y + nRect.height));
            double eTLnBR = Util.getEuclideanDistanceBetweenPoints(eRect.x, eRect.y, (nRect.x + nRect.width), (nRect.y + nRect.height));

            double eTRnTL = Util.getEuclideanDistanceBetweenPoints((eRect.x + eRect.width), eRect.y, nRect.x, nRect.y);
            double eTRnTR = Util.getEuclideanDistanceBetweenPoints((eRect.x + eRect.width), eRect.y, (nRect.x + nRect.width), nRect.y);
            double eTRnBL = Util.getEuclideanDistanceBetweenPoints((eRect.x + eRect.width), eRect.y, nRect.x, (nRect.y + nRect.height));
            double eTRnBR = Util.getEuclideanDistanceBetweenPoints((eRect.x + eRect.width), eRect.y, (nRect.x + nRect.width), (nRect.y + nRect.height));

            double eBLnTL = Util.getEuclideanDistanceBetweenPoints(eRect.x, (eRect.y + eRect.height), nRect.x, nRect.y);
            double eBLnTR = Util.getEuclideanDistanceBetweenPoints(eRect.x, (eRect.y + eRect.height), (nRect.x + nRect.width), nRect.y);
            double eBLnBL = Util.getEuclideanDistanceBetweenPoints(eRect.x, (eRect.y + eRect.height), nRect.x, (nRect.y + nRect.height));
            double eBLnBR = Util.getEuclideanDistanceBetweenPoints(eRect.x, (eRect.y + eRect.height), (nRect.x + nRect.width), (nRect.y + nRect.height));

            double eBRnTL = Util.getEuclideanDistanceBetweenPoints((eRect.x + eRect.width), (eRect.y + eRect.height), nRect.x, nRect.y);
            double eBRnTR = Util.getEuclideanDistanceBetweenPoints((eRect.x + eRect.width), (eRect.y + eRect.height), (nRect.x + nRect.width), nRect.y);
            double eBRnBL = Util.getEuclideanDistanceBetweenPoints((eRect.x + eRect.width), (eRect.y + eRect.height), nRect.x, (nRect.y + nRect.height));
            double eBRnBR = Util.getEuclideanDistanceBetweenPoints((eRect.x + eRect.width), (eRect.y + eRect.height), (nRect.x + nRect.width), (nRect.y + nRect.height));
            double[] array = {eTLnTL, eTLnTR, eTLnBL, eTLnBR,
                    eTRnTL, eTRnTR, eTRnBL, eTRnBR,
                    eBLnTL, eBLnTR, eBLnBL, eBLnBR,
                    eBRnTL, eBRnTR, eBRnBL, eBRnBR};
            smallestDistance = calculateMinDouble(array);

            boolean result = eTLnTL < distance || eTLnTR < distance || eTLnBL < distance || eTLnBR < distance ||
                    eTRnTL < distance || eTRnTR < distance || eTRnBL < distance || eTRnBR < distance ||
                    eBLnTL < distance || eBLnTR < distance || eBLnBL < distance || eBLnBR < distance ||
                    eBRnTL < distance || eBRnTR < distance || eBRnBL < distance || eBRnBR < distance;

            if (result) {
                isClose = 1.0;
            }
        }

        return new double[]{isClose, smallestDistance};
//            return eTLnTL < distance || eTLnTR < distance || eTLnBL < distance || eTLnBR < distance ||
//                    eTRnTL < distance || eTRnTR < distance || eTRnBL < distance || eTRnBR < distance ||
//                    eBLnTL < distance || eBLnTR < distance || eBLnBL < distance || eBLnBR < distance ||
//                    eBRnTL < distance || eBRnTR < distance || eBRnBL < distance || eBRnBR < distance;

    }

    private static double calculateMinDouble(double[] array) {
        // Convert the primitive array into a Class array
        Double[] dArray = ArrayUtils.toObject(array);
        List<Double> dList = Arrays.asList(dArray);

// Find the minimum value
        Double returnvalue = Collections.min(dList);

        return returnvalue;
    }

    public static boolean isSegmentMember(String xpath, int segmentId) {
        // find segment of interest
        Segment seg = null;
        for (InterSegmentEdge e : SALEM.getOriginalUISegmentModel().getEdges()) {
            if (e.getSegment1().getId() == segmentId) {
                seg = e.getSegment1();
                break;
            }
            if (e.getSegment2().getId() == segmentId) {
                seg = e.getSegment2();
                break;
            }
        }
        if (seg != null) {
            if (seg.getLowestCommonAncestor().equalsIgnoreCase(xpath))
                return true;

            for (String member : seg.getMembers()) {
                if (member.equalsIgnoreCase(xpath)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void writeSolutionToCSV(ArrayList<String> solution, String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        File f = new File(filePath);
        if (f.exists() && !f.isDirectory()) {
            // do something
        }
        try (FileWriter writer = new FileWriter(new File(filePath), true)) {
            for (String item : solution
            ) {


//                 sb = new StringBuilder();
                sb.append(item);
                sb.append(',');


            }
            sb.append('\n');
            writer.write(sb.toString());
            Logger.trace("done!");


        } catch (FileNotFoundException e) {
            Logger.error(e.getMessage());
        }

    }


    public static Map<String, StaticLayoutUtil> getStaticLayoutFilesCache() {

        if (staticLayoutFilesCache == null || !SALEM.getCurrentApkPackage().equalsIgnoreCase(currentApkUnderFix) || SALEM.IsThreadExecuted) {
            staticLayoutFilesCache = new HashMap<>();
            currentApkUnderFix = SALEM.getCurrentApkPackage();
        }
        return staticLayoutFilesCache;
    }

    public static void setStaticLayoutFilesCache(Map<String, StaticLayoutUtil> staticLayoutFilesCache) {
        Util.staticLayoutFilesCache = staticLayoutFilesCache;
    }

    public static StaticLayoutUtil getStaticAppLayout(String file) {
        Map<String, StaticLayoutUtil> cached = getStaticLayoutFilesCache();
        if (cached.containsKey(file)) {
            return cached.get(file);
        }
        // first time reading the layout
        StaticLayoutUtil staticLayout = new StaticLayoutUtil();
        org.w3c.dom.Element root = staticLayout.readStaticXML(new File(file));
        staticLayoutFilesCache.put(file, staticLayout);
        return staticLayout;
    }


    public static void preprocessDynamicFolder(String dynamicResultPath, String refineRootPath) {
        String layoutPath = dynamicResultPath;
        //   String appName = dynamicResultPath.substring(dynamicResultPath.lastIndexOf( File.separator) + 1);
        String refinedPath = refineRootPath;

        File inputFolder = new File(layoutPath);
        File outputFolder = new File(refinedPath);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        // Extract the activity layout only into new XMLs
        for (File f : inputFolder.listFiles()) {
            String fileName = f.getName();
            if (fileName.endsWith(".xml")) {
                if (fileName.contains("com.sec.android.app.launchercom.android.launcher2.Launcher")) {
                    // The default launcher of Android
                    continue;
                }
                boolean success = XMLUtils.extractActivityLayout(f.getPath(), refinedPath);
                // also copy the corresponding png files
                if (success) {
                    // copy the corresponding png to the outputFolder
                    String pngFile = fileName.replace("xml", "png");
                    // check if a file exists
                    //i
//                    File srcPng = new File(inputFolder + File.separator + pngFile);
//                    File desPng = new File(outputFolder + File.separator + pngFile.substring(pngFile.lastIndexOf("/") + 1));
                    String src=inputFolder + File.separator + pngFile;
                    String des=outputFolder + File.separator + pngFile.substring(pngFile.lastIndexOf("/") + 1);
                    boolean result=copyPngIfExists( src, des);
                    if (!result){
                        Logger.error("main png file not found | trying to find _0.png and _1.png");
                        String src0=inputFolder + File.separator + pngFile.replace(".png","_0.png");
                        String des0=outputFolder + File.separator + pngFile.substring(pngFile.lastIndexOf("/") + 1).replace(".png","_0.png");
                        String src1=inputFolder + File.separator + pngFile.replace(".png","_1.png");
                        String des1=outputFolder + File.separator + pngFile.substring(pngFile.lastIndexOf("/") + 1).replace(".png","_1.png");
                        copyPngIfExists( src0, des0);
                        copyPngIfExists( src1, des1);
                    }
//                    try {
//                        Files.copy(srcPng, desPng);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }


    }
    public static boolean copyPngIfExists(String pngPath, String destinationPath) {
        File pngFile = new File(pngPath);
        if (pngFile.exists() && pngFile.isFile()) {
            File source = new File(pngPath);
            File destination = new File(destinationPath);
            try {
                Files.copy(source, destination);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }
    public static List<Node<DomNode>> getListOfTouchTargets(Node<DomNode> root) {
        List<Node<DomNode>> tapTargets = new ArrayList<>();

        // get all tap targets
        Queue<Node<DomNode>> q = new LinkedList<Node<DomNode>>();
        q.add(root);

        while (!q.isEmpty()) {
            Node<DomNode> node = q.remove();
            DomNode e = node.getData();
            if (e.getTagName().equalsIgnoreCase("android.webkit.WebView")) {
                continue;
            }
//            DomNode node2 = node;

            // ToDo: in mFix they specified link, button, etc. but here in Android it could be anything div,button,etc.. So I am cgecking for clickable attribute
            //
//            if (Constants.TAP_TARGET_ELEMENTS.contains(e.getTagName()))
            if (Util.isElementClickable(e)) //element has attribute clickable =true
            {
                tapTargets.add(node);
            }

            if (node.getChildren() != null) {
                for (Node<DomNode> child : node.getChildren()) {
                    q.add(child);
                }
            }
        }

        return tapTargets;
    }


    public static HashMap<String, ArrayList<String>> readingCrawlingScriptMapping(String activitiesToPythonScripts) {
        /*** reading the mapping to all the acripts***/
        HashMap<String, ArrayList<String>> activitiesMapping = new HashMap<>();
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        int i = 0;
        try {

            br = new BufferedReader(new FileReader(activitiesToPythonScripts));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                if (i == 0) {
                    i = 1;
                    continue;
                }
                String[] activityInfo = line.split(cvsSplitBy);
//                System.out.println(activityInfo);
                String activityName = activityInfo[0].trim();
                String activityToRunWithScript = activityInfo[1].trim();
                String scriptFileName = activityInfo[2].trim();
                String type = null;
                String content = null;
                String extra_content = null;

                if (activityInfo.length > 3) {
                    type = activityInfo[3];

                    if (activityInfo.length > 4) {
                        content = activityInfo[4];
                    }
                    if (activityInfo.length > 5) {
                        extra_content = activityInfo[5];
                    }
                }

                ArrayList<String> act = new ArrayList<>();
                act.add(activityName);
                act.add(activityToRunWithScript);
                act.add(scriptFileName);
                act.add(type);
                act.add(content);
                act.add(extra_content);

                activitiesMapping.put(activityName, act); // Add the issue to the list of issues

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }

        return activitiesMapping;
    }

}
