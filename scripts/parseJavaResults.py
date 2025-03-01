import sys

import pandas as pd
import csv

import os
from pathlib import Path

import run_detection
import subprocess

from datetime import datetime


# activity	activity to run	script	type	content

def get_activity_script(apk_name, activity_to_run):
    activity_map_path = "/home/testing/AppSet/accessibility/TT_scripts/apks_folder/crawling_scripts/activities_to_scritpt_mapping.csv"
    with open(activity_map_path, mode='r') as infile:
        result = []
        reader = csv.reader(infile)
        for row in reader:
            act = row[0]
            if activity_to_run != act:
                continue
            result.append(act)
            activity_to_run = row[1]
            result.append(activity_to_run)
            script = row[2]
            result.append(script)

            if (len(row) > 2):
                type = row[3]
                result.append(type)

                if (len(row) > 3):
                    content = row[4]
                    result.append(content)
                    if (len(row) > 4):
                        extra_content = row[5]
                        result.append(extra_content)
                        if (len(row) > 5):
                            extra_content2 = row[6]
                            result.append(extra_content2)
            return result


def run_process(crawlingScriptCMD):
    process = subprocess.Popen(crawlingScriptCMD.split(), stdout=subprocess.PIPE)
    output, error = process.communicate()
    for line in output.decode().split('\n'):
        print('Run script Result:' + line)


def run_activity(apk_name, run_activity_list):
    print("run_activity")
    CRAWLING_SCRIPTS_PATH = "/home/testing/AppSet/accessibility/TT_scripts/apks_folder/crawling_scripts"
    device_name = "emulator-5554";
    activity_to_run = run_activity_list[1]
    # if '-' in activity_to_run:
    #     run_detection.start_app(apk_name,path)
    # else:
    args = device_name + " " + apk_name + " " + activity_to_run
    script_file = run_activity_list[2]
    if (len(run_activity_list) > 2):
        type = run_activity_list[3]
        args = args + " " + type
    if (len(run_activity_list) > 3):
        content = run_activity_list[4]
        content = content.replace(" ", "000");
        args = args + " " + content
        if (len(run_activity_list) > 4):
            extra_content = run_activity_list[5]
            args = args + " " + extra_content
            if (len(run_activity_list) > 5):
                extra_content2 = run_activity_list[6]
                args = args + " " + extra_content2

    scriptPath = CRAWLING_SCRIPTS_PATH + "/" + script_file
    crawlingScriptCMD = "/usr/bin/python " + scriptPath + " " + args
    # ensure root privilage:
    print("SCRIPT:::: ", crawlingScriptCMD)
    run_process(crawlingScriptCMD)


def run_final_solution(apk_name, activity_to_run, index):
    base_path = "/home/testing/TT-Results/final_chromosome_fixes/"
    bases_path = base_path + "/" + activity_to_run + "/" + apk_name
    files = sorted(Path(bases_path).iterdir(), key=os.path.getmtime, reverse=True)
    print("Ali files")
    print(str(files[index]))
    full_path = str(files[index])
    ADB = "/home/testing/Android/Sdk/platform-tools/adb -s emulator-5554 "

    run_process(ADB + " root")
    run_process(ADB + " uninstall " + apk_name)
    run_detection.run_app(apk_name, full_path)
    run_activity_list = get_activity_script(apk_name, activity_to_run)
    run_activity(apk_name, run_activity_list)


if __name__ == '__main__':
    # run_final_solution("com.daily.calling", "com.js.rssreader.ListActivity", 0)
    # run_final_solution("com.contorra.golfpad","com.contorra.golfpad.Upgrade",0)
    df = pd.DataFrame(columns=['apk', 'activity',
                               'exception',
                               'RunTime',
                               'in Sec',
                               'fitness score', 'acc score',
                               'UI score', 'spacing score'
                               ])

    #  , 'change score'
    # '/home/testing/AppSet/accessibility/TT_scripts/apks_folder/log_ActivitiesRun03-17-2021-11-28-53-PM.txt'
    # '/home/testing/AppSet/accessibility/TT_scripts/apks_folder/run_results_logs/Activities_Results/ActivitiesRun_Results_03-18-2021-04-15-18-AM.txt'
    apps_data = {}
    file_name = 'ActivitiesRun_Results_03-20-2021-03-29-45-PM.txt'
    date = file_name.replace("ActivitiesRun_Results", "")
    date = date.replace(".txt", "")
    result_file = "/home/testing/AppSet/accessibility/TT_scripts/apks_folder/run_results_logs/Parsed_CSV_Results/R_" + date + ".csv"

    x = pd.read_csv("/home/testing/AppSet/accessibility/TT_scripts/apks_folder/activitiesToRun_WITH_NEW_APPS.csv")
    activities_to_package = {}
    for index, row in x.iterrows():
        apk = row['APK']
        activity = row['activity']
        # print("ACTIVITY: ",row['activity'])
        # print("APK: ",row['APK'])
        activities_to_package[activity] = apk

    with open(
            "/home/testing/AppSet/accessibility/TT_scripts/apks_folder/run_results_logs/Activities_Results/" + file_name) as f:
        content = f.readlines()
    act = []
    for line in content:
        if line.startswith("SEVERE"):
            if len(act) > 0:
                activity_name = act.pop(0)

                apps_data[activity_name] = act
            act = []
            continue
        if line.startswith("INFO: java"):
            error = line.split(":")[1].strip()
            act.append(error)
            continue
        if line.startswith("INFO: Running activity"):
            l = line.split("|")
            act_name = l[1].strip()
            act.append(act_name)
            continue
        if line.startswith("INFO: Total Run Time:"):
            l = line.split("   ")
            r = l[1].strip()
            time = r.split("sec:")
            nano = time[0].strip()
            sec = time[1].strip()
            act.append(nano)
            act.append(sec)
            continue

        if line.startswith("INFO: chromosome"):
            chormosome = line.split(":")[1].strip()
            act.append(chormosome)
            continue

        if line.startswith("INFO: [ <"):
            genes = line.split(":")[1].strip()
            act.append(genes)
            continue

        if "=> Accessibility =" in line:  # results of fitness
            fitness_array = line.split("=>")
            # [0] total score, [1] individual scores
            total_score = fitness_array[0]
            sc = fitness_array[1].split(",")
            acc = sc[0].split("=")[1]
            dist = sc[1].split("=")[1]
            spacing = sc[2].split("=")[1]
            #   change = fitness_array[2].split("=")[1].strip()

            act.append(acc)
            act.append(dist)
            act.append(spacing)
            #    act.append(change)
            continue

    for key in apps_data.keys():
        app = activities_to_package[key]
        list = apps_data[key]
        if len(list) < 2:
            df.loc[len(df)] = [app,
                               key,
                               list[0],
                               "",
                               "",
                               "", "",
                               "", ""
                               ]
        else:
            df.loc[len(df)] = [app,
                               key,
                               "",
                               list[0],
                               list[1], list[2],
                               list[3], list[4],
                               list[5]
                               ]

    df.to_csv(result_file)
    print(apps_data)
