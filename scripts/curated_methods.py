import os
import subprocess
import xml.etree.ElementTree as ET
from pathlib import Path
import shutil

import fix_padding_margin
import uiautomator2 as u2

from update_apks import *
import PrepareNewAndroidUI
from dumpUI import *



def make_app_debbugable(apk_name, decompiled_APK, package_name):
    mainfiest = 'AndroidManifest.xml'
    # base_path='/home/ali/AppSet/accessibility/scripts/'
    # apk_name = 'com.adzenze.FashionDesignFlatSketch'
    # app = apk_name + '/res/'
    pp = Path(decompiled_APK + "/" + apk_name)
    full_resource_path = Path(pp)
    mainfiest_path = Path(full_resource_path / mainfiest)

    print(mainfiest_path)

    with open(mainfiest_path, 'r') as f:
        tree = ET.ElementTree()
        tree.parse(f)
        root = tree.getroot()
        print("root", root)
        print("root.tag", root.tag)
        print(tree)
        for elem in tree.iter():
            if "application" in elem.tag:
                print("Found tag", elem.tag)
                tag = elem.tag
                atts = elem.attrib
                if '{http://schemas.android.com/apk/res/android}debuggable' in atts:
                    print("debuggable found")

                elem.set('{http://schemas.android.com/apk/res/android}debuggable', 'true')
                print("elem", elem)

                if '{http://schemas.android.com/apk/res/android}testOnly' in atts:
                    elem.set('{http://schemas.android.com/apk/res/android}testOnly', 'false')
                    print("testOnly found")

                break
        tree.write(os.path.join(mainfiest_path))

# event == 'Decompile and add IDs':
def decompile_and_add_id():
    from os.path import exists

    file_exists = exists(str(ided_apk) + '/' + apk_name + ".apk")
    if file_exists:
        sg.Popup(
            'decompiled ID version already exist, remove it or move it before creating a new decompiled version',
            keep_on_top=True)
    else:
        # package_name = get_package_name(config_dict['original_apks'] + "/" + apk_name + ".apk")
        decompile_apk(apk_name, original_apk, decompiled_APK)
        add_missing_ids(apk_name, decompiled_APK, package_name)
        make_app_debbugable(apk_name, decompiled_APK, package_name)
        compile_apk(decompiled_APK, ided_apk, apk_name, config_dict['android_signature'])


def statedumper_running_state(event):
    if event == 'Dump StateDumper':
        d = u2.connect(device_name)
        d.uiautomator.stop()
        # dump_state_dumper
        state_dumper_path = state_dumper_dump + "/" + config_dict['device_title'] + "/" + ui_version_type
        PrepareNewAndroidUI.main_state_dumper(apk_id, apk_name, subject_activity, state_dumper_path, config_dict)

    elif event == 'Parse Dump to XML':
        state_dumper_json_path = state_dumper_dump + "/" + config_dict[
            'device_title'] + "/" + ui_version_type + "/" + subject_activity + ".json"
        state_dumper_xml_path = state_dumper_xml + "/" + config_dict[
            'device_title'] + "/" + ui_version_type + "/" + subject_activity + ".xml"
        print(state_dumper_json_path)
        print(state_dumper_xml_path)
        PrepareNewAndroidUI.parse_state_dumer_to_xml(apk_name, state_dumper_xml_path, state_dumper_json_path,
                                                     config_dict)

    elif event == 'Merge Dynamic and XMLDump':
        app_augmented_path = create_apk_dynamic_folder(augmented_VH + "/" + config_dict['device_title'],
                                                       ui_version_type)
        state_dumper_xml_path = state_dumper_xml + "/" + config_dict['device_title'] + "/" + ui_version_type
        dynamic_VH_path = dynamic_VH + "/" + config_dict['device_title'] + "/" + ui_version_type
        PrepareNewAndroidUI.crete_augmentedVH(apk_id, apk_name, subject_activity, config_dict, ui_version_type,
                                              dynamic_VH_path, state_dumper_xml_path, app_augmented_path,
                                              decompiled_APK)
    elif event == 'Add origin Attributes':
        app_augmented_path = create_apk_dynamic_folder(augmented_VH + "/" + config_dict['device_title'],
                                                       ui_version_type)
        state_dumper_path = state_dumper_xml + "/" + config_dict['device_title'] + "/" + ui_version_type
        dynamic_VH_path = dynamic_VH + "/" + config_dict['device_title'] + "/" + ui_version_type
        PrepareNewAndroidUI.crete_matchCodeVH(apk_id, apk_name, subject_activity, config_dict, ui_version_type)

    elif event == 'Handle Margins & Paddings':
        res,something_changed = fix_padding_margin.handle_margins_and_padding(apk_id, apk_name, decompiled_APK, subject_activity,
                                                            config_dict, sg)
        # compile the decompiled apk that was fixed padding and margin
        #res=False
        if res:
            make_app_debbugable(apk_name, decompiled_APK, package_name)
            # make sure we backup the apk in the ided_apk folder
            if something_changed: # we need to backup the apk, otherwise just compile
                print("something changed, we will backup the apk")
                backup = ided_apk + "/" + "original/"
                try:
                    os.makedirs(backup)
                    print("Directory ", backup, " Created ")
                except FileExistsError:
                    print("Directory ", backup, " already exists")
                # move apk_name apk file in ided_apk folder to the backup folder
                shutil.move(ided_apk+"/"+apk_name+".apk", backup)
            compile_apk(decompiled_APK, ided_apk, apk_name, config_dict['android_signature'])