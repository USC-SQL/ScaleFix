package usc.edu.SALEM.util;


import usc.edu.SALEM.fitness.TIssue;
import usc.edu.SALEM.Constants;
import usc.edu.SALEM.SALEM;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

import java.io.*;
import java.util.*;

/*
Read List of issues generated from detection Tool
 */
public class ReadIssues {
    public String filePath;
    public String listOfIssuesPath;


    public ReadIssues(String issues_file) {
        this.filePath = issues_file;

    }

    public static String parseIssuesFromLogCat(String logCatPath, String filteredIssueFilePath) throws IOException {
        ArrayList<String> listOfIssues = readIssuesLocCat(logCatPath); // read the logcat file and add TT issues to arrayList
        ArrayList<String[]> listOfOriginalTTIssues = new ArrayList<>();

        for (String issue : listOfIssues
        ) {

            String[] issueData = issue.split(",,,");
            String act = SALEM.getCurrentActivityName();
            String pack = SALEM.getCurrentApkPackage();
            String issuetype = Constants.DETECTION_TOOL_TO_ISSUES_MAPPING.get(issueData[3].trim());
            boolean checkAct;
            if (SALEM.DEBUG_GET_ISSUES) { // running to get all issues in all activities
                checkAct = true;
            } else {
                checkAct = issueData[1].trim().equalsIgnoreCase(act);
            }
            if (issueData[0].trim().equalsIgnoreCase(pack) && checkAct && issuetype != null) {// same app and same activity and the issuy type is part of TT issues
                String activity = issueData[1].trim();
                String mainIssue = issueData[2].trim();
                String issueType = issueData[3].trim();  //Parse the detection tool output for issue type
                String widgetID = issueData[6].trim();
                String widgetClass = issueData[4].trim();
                String widgetBounds = "";
                String widgedtText = "";
                String issueImagePath = issueData[7].trim();
                String[] currentIssue = {pack, activity, mainIssue, issueType, widgetClass, widgetID, widgetBounds, widgedtText, issueImagePath};
                listOfOriginalTTIssues.add(currentIssue);
                listOfOriginalTTIssues.add(currentIssue);
            }
        }

        writeFilteredTTIssues(listOfOriginalTTIssues, filteredIssueFilePath);

        return filteredIssueFilePath;
    }

    public HashMap<String, Table> parseFile() {
        HashMap<String, Table> listOfIssues = new HashMap<>();
        try {
            Table issues = Table.read().csv(filePath);
            System.out.println();
            Column<?> listActivity = issues.stringColumn("activity");
            System.out.println(issues.columnNames());
//            System.out.println("Ali " + listActivity.countUnique());
            listActivity.unique().asStringColumn();

            List uniqueAct = listActivity.unique().asList();
            for (ListIterator it = uniqueAct.listIterator(); it.hasNext(); ) {
                String activityName = it.next().toString().trim();
//                System.out.println("UniqueAc: " + activityName);
                StringColumn act = issues.stringColumn("activity");
                Table activityIssues = issues.where(act.isEqualTo(activityName));
//                System.out.println("list of issues: " + activityIssues);
                listOfIssues.put(activityName, activityIssues);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return listOfIssues;
    }


    public static String parseOriginalIssueFile(String originalFilePath, String newAPkIssue, String filteredFileDestination) throws IOException {
        ArrayList<String[]> listOfOriginalTTIssues = new ArrayList<>();
//    0    com.adzenze.FashionDesignFlatSketch
//    1 com.adzenze.FashionDesignFlatSketch.ReadBookActivity
//    2    checks.SpeakableTextPresentCheck
//     3   RESULT_ID_MISSING_SPEAKABLE_TEXT
//      4  AccessibilityCheckResult ERROR class com.google.android.apps.common.testing.accessibility.framework.checks.SpeakableTextPresentCheck "null"
//    5 8589934599
//      6  android.widget.LinearLayout
//    7 com.adzenze.FashionDesignFlatSketch:id/btn_home
//    8 Rect(0: 95 - 168: 252)
//        9 Text
//      10  /data/user/0/com.aziz.accessibilityEval/files/screenX-36833073.PNG

        BufferedReader br = null;

        String line = "";
        String cvsSplitBy = ",";
        try {

            br = new BufferedReader(new FileReader(originalFilePath));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] issue = line.split(cvsSplitBy);
                String activity = issue[1];
                String mainIssue = issue[2];
                String issueType = issue[3];  //Parse the detection tool output for issue type
                if (Constants.DETECTION_TOOL_TO_ISSUES_MAPPING.get(issueType.trim()) == null || SALEM.getCurrentActivityName().equalsIgnoreCase(activity)) {
                    // if it not TT issue then go to the next
                    continue;

                }
                String widgetID = issue[7];
                String widgetClass = issue[6];
                String widgetBounds = issue[8];
                String widgedtText = issue[9];
                String issuePath = issue[10];
                String[] currentIssue = {activity, mainIssue, issueType, widgetClass, widgetID, widgetBounds, widgedtText, issuePath};

                listOfOriginalTTIssues.add(currentIssue);
//RESULT_ID_SMALL_TOUCH_TARGET_HEIGHT	android.widget.LinearLayout	com.adzenze.FashionDesignFlatSketch:id/btn_home_slidingmenu_left
//            Rect(0: 90 - 168: 252)	 	/data/user/0/com.aziz.accessibilityEval/files/screenX-297852126.PNG

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

            String fileName = newAPkIssue + Constants.TOUCH_TARGET_ISSUES_OUTPUT_SUFFIX;

            String filteredIssueFilePath = filteredFileDestination + "/" + fileName;

            writeFilteredTTIssues(listOfOriginalTTIssues, filteredIssueFilePath);

            return filteredIssueFilePath;


        }
//    private void getIssueType(String s) {
//    }

    }

    public static void writeFilteredTTIssues(ArrayList<String[]> listOfOriginalTTIssues, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new File(filePath))) {
            for (String[] issue : listOfOriginalTTIssues
            ) {


                StringBuilder sb = new StringBuilder();
                sb.append(issue[0]);
                sb.append(',');
                sb.append(issue[1]);
                sb.append(',');
                sb.append(issue[2]);
                sb.append(',');
                sb.append(issue[3]);
                sb.append(',');
                sb.append(issue[4]);
                sb.append(',');
                sb.append(issue[5]);
                sb.append(',');
                sb.append(issue[6]);
                sb.append(',');
                sb.append(issue[7]);
                sb.append('\n');


                writer.write(sb.toString());
            }
            System.out.println("done!");


        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }

    }

    public HashMap<String, Set<TIssue>> parseIssue(String chromosomeID) {
        HashMap<String, Set<TIssue>> listofIssues = new HashMap<>();
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        try {

            br = new BufferedReader(new FileReader(this.filePath));
            while ((line = br.readLine()) != null) {
                if (!chromosomeID.equalsIgnoreCase("initial")) {
                }
                // use comma as separator
                String[] issue = line.split(cvsSplitBy);
                String activity = issue[1];
                if (listofIssues.get(activity) == null) {  // First time seeing the activity
                    Set<TIssue> actIssues = new HashSet<TIssue>();
                    listofIssues.put(activity, actIssues);
                }
                // issueType,chromosome, isInitial, activity, widgetId, widgetXpath,widgetclass,rectangle

// 0 com.adzenze.FashionDesignFlatSketch	 1 com.adzenze.FashionDesignFlatSketch.ReadBookActivity	2 checks.TouchTargetSizeCheck
// 3 RESULT_ID_SMALL_TOUCH_TARGET_HEIGHT
// 4 android.widget.LinearLayout	5 com.adzenze.FashionDesignFlatSketch:id/btn_home	6 Rect(0: 95 - 168: 252)
// 7/data/user/0/com.aziz.accessibilityEval/files/screenX-297852127.PNG

                boolean isInitial = false;
                String widgetBounds = null;
                if (chromosomeID.equalsIgnoreCase("initial")) {
                    isInitial = true;
                    SALEM.setCurrentApkPackage(issue[0]);
                    if (issue.length > 6)
                        widgetBounds = issue[6];
                }

                String issueType = issue[3];  //Parse the detection tool output for issue type
                String widgetID = "";
                if (issue.length > 5) {
                    widgetID = issue[5];
                }
                String widgetClass = issue[4];
                String widgetXpath = null;

                TIssue ti = new TIssue(issueType, chromosomeID, isInitial, activity, widgetID, widgetXpath, widgetClass, widgetBounds);
                listofIssues.get(activity).add(ti); // Add the issue to the list of issues

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

        return listofIssues;
    }
    //                             0   12-10 18:10:41.876 19687 24964 E DB_PACKAGE: : com.adzenze.FashionDesignFlatSketch
//                             1   12-10 18:10:41.876 19687 24964 E DB_ACTIVITY: : com.adzenze.FashionDesignFlatSketch.ReadBookActivity
//                            2    12-10 18:10:41.876 19687 24964 E DB_CHECK_CLASS: : checks.TouchTargetSizeCheck
//                            3    12-10 18:10:41.876 19687 24964 E DB_ERROR: : RESULT_ID_SMALL_TOUCH_TARGET_HEIGHT
//                            4    12-10 18:10:41.876 19687 24964 E DB_ELEMENT: : android.widget.LinearLayout
//                            5    12-10 18:10:41.876 19687 24964 E DB_ELEMENT_ID: : 8589934595
//                           6     12-10 18:10:41.876 19687 24964 E DB_RES_ID: : com.adzenze.FashionDesignFlatSketch:id/btn_about
//                            7    12-10 18:10:41.876 19687 24964 E SCREENSHOT FILE PATH: /data/user/0/com.aziz.accessibilityEval/files/screenX532437479.PNG
//                           8    12-10 18:10:41.876 19687 24964 E getCondensedUniqueId: 8589934595
//                            9    12-10 18:10:41.876 19687 24964 E is clickable: true
//                              10  12-10 18:10:41.876 19687 24964 E is long clickable: false
//                              11  12-10 18:10:41.876 19687 24964 E getCondensedUniqueId: false

    public static ArrayList<String> readIssuesLocCat(String logCatPath) {
        int issueLineCount = 0;
        String issueInfo = "";
        ArrayList<String> currentIssues = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(logCatPath))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                if (line.toLowerCase().contains("======= : ERROR =========".toLowerCase())) { // that means it is accessibility issue
                    issueLineCount = 0;
                    issueInfo = "";
                    //      continue;
                } else if (issueLineCount < 8) {
                    String info = "";
                    String[] infoArr = new String[0];
                    boolean correctLine = false;
                    if (line.contains("E DB_")) {
                        info = line.split("E DB_")[1];
                        infoArr = info.split(": :");
                        correctLine = true;
                    } else if (line.contains("E SCREENSHOT")) {
                        info = line.split("E SCREENSHOT")[1];
                        infoArr = info.split(": ");
                        correctLine = true;
                    }

                    if (correctLine) {
                        String infoType = infoArr[0];
                        String infoData = infoArr[1];
//                    issueInfo+=infoType+"::"+infoData+",,,";
                        if (issueLineCount == 7) {
                            issueInfo += infoData;
                        } else {
                            issueInfo += infoData + ",,,";
                        }

                        issueLineCount++;

                    }

                } else if (issueLineCount == 8) {
                    currentIssues.add(issueInfo);
                    issueLineCount = 0;
                }
                line = br.readLine();
            }
            return currentIssues;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return currentIssues;
    }
}







