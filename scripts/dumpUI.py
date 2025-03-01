import glob
import os
import re
import string
import subprocess
from pathlib import Path
import pandas as pd
import numpy as np
from time import sleep

from config_file import ReadConfigClass

ADB = ReadConfigClass().config['adb_path']


def create_apk_dynamic_folder(dynamic_layouts_folder,ui_version_type=""):
    dirName = dynamic_layouts_folder  +"/"+ ui_version_type
    try:
        os.makedirs(dirName)
        print("Directory ", dirName, " Created ")
    except FileExistsError:
        print("Directory ", dirName, " already exists")
    return dirName


def screencap(output_path, act,device_name):
    adb=ADB +' -s  '+device_name+'  '
    SCREEN_CAP = adb + ' shell screencap -p /sdcard/screen.png'
    process = subprocess.Popen(SCREEN_CAP.split(), stdout=subprocess.PIPE)
    output, error = process.communicate()
    for line in output.decode().split('\n'):
        print('take screenshot  result: ')
        print(line)
    sleep(2)

    pngFile = act + '.png';
    pull = adb + ' ' + 'pull /sdcard/screen.png ' + output_path + '/' + pngFile;
    process = subprocess.Popen(pull.split(), stdout=subprocess.PIPE)
    output, error = process.communicate()
    for line in output.decode().split('\n'):
        print('pull screencap process result: ')
        print(line)


def get_activity(device_name):
    activity_command = ADB +' -s '+device_name+ ' shell dumpsys activity activities'
    print(activity_command)
    process = subprocess.Popen(activity_command.split(), stdout=subprocess.PIPE)
    output, error = process.communicate()
    activity = ''
    for line in output.decode().split('\n'):
        # print('line get activity')
        # print(line)
        if 'mResumedActivity' in line:
            data = line.rsplit('/', 1)
            temp = data[1].strip()
            activity = temp.split()[0]
            print('Activity name:' + activity)
            break

    return activity


def dump(output_path, act,device_name):
    # activity_command=ADB +' shell "dumpsys activity activities | grep mResumedActivity"'
    # process = subprocess.Popen(activity_command.split(), stdout=subprocess.PIPE)
    # output, error = process.communicate()
    # for line in output.decode().split('\n'):
    #     if 'mResumedActivity: ActivityRecord' in line:
    #         data = line.rsplit('/', 1)
    #         activity=data[1].strip()
    #     print('Uactivity name:' + activity)
    # sleep(3)
    adb=ADB +' -s  '+device_name+'  '
    print('dump output path: ', output_path)
    UIAUTOMATOR_DUMP = adb +'  shell uiautomator dump'
    process = subprocess.Popen(UIAUTOMATOR_DUMP.split(), stdout=subprocess.PIPE)
    output, error = process.communicate()
    # for line in output.decode().split('\n'):
    #     print('line get activity')
    #     print(line)
    sleep(2)
    xmlFileName = act + '.xml';
    pull = adb  + ' pull ' + '/sdcard/window_dump.xml ' + output_path + '/' + xmlFileName;
    #print('pull cmd: ', pull)
    process = subprocess.Popen(pull.split(), stdout=subprocess.PIPE)
    output, error = process.communicate()
    # for line in output.decode().split('\n'):
    #     # print('pull activity process result: ')
    #     # print(line)
