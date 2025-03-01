'''
 if I want to clean VHs files from subject folder
(ie remove Vhs and screenshots from dynamic, augment, and VH but
 keep the device name and other folder structure) I can use this script.
'''

import os
import shutil
from pathlib import Path
import xml.etree.ElementTree as ET

from config_file import ReadConfigClass

# def layout_files(tree):
#     element_id_att_map = {}
#     for elem in tree.iter():
#         atts=elem.attrib
#         if '{http://schemas.android.com/apk/res/android}id' not in atts:
#             continue
#         id=atts['{http://schemas.android.com/apk/res/android}id']
#         #print("id: ", id)
#

def replace_att_value(elem, att_name):
    prefix = "{http://schemas.android.com/apk/res/android}"
    full_att_name =prefix + att_name
    currentVal = elem.attrib[full_att_name]
    elem.set(full_att_name+"Top", currentVal)
    elem.set(full_att_name+"Bottom", currentVal)
    elem.set(full_att_name+"Left", currentVal)
    elem.set(full_att_name+"Right", currentVal)
    elem.attrib.pop(full_att_name)

def replace_start_end_att_value(elem, att_name):
    prefix = "{http://schemas.android.com/apk/res/android}"
    full_att_name =prefix + att_name
    currentVal = elem.attrib[full_att_name]
    if "Start" in att_name:
        new_att_name = full_att_name.replace("Start", "Left")
    elif "End" in att_name:
        new_att_name = full_att_name.replace("End", "Right")
    elem.set(new_att_name, currentVal)

    elem.attrib.pop(full_att_name)
def check_if_start_end_exists(att_name, static_atts, dynamic_atts, id,android_prefix):
    full_att_name = android_prefix+ att_name
    if full_att_name in static_atts:
        # print attributes in dynamic_atts that contains "padding"

        print("id:", id, "|",att_name,"\n\tstatic value:", static_atts[full_att_name])
        print("\tdynamic value: ")
        for att in dynamic_atts:
            if att_name in att:
                print("\t", att.replace(android_prefix, "android:"), dynamic_atts[att])
        return True
    return False
def check_if_padding_or_margin_exists(att_name, static_atts, dynamic_atts, id,android_prefix):
    full_att_name = android_prefix+ att_name
    if full_att_name in static_atts:
        # print attributes in dynamic_atts that contains "padding"

        print("id:", id, "|",att_name,"\n\tstatic value:", static_atts[full_att_name])
        print("\tdynamic value: ")
        for att in dynamic_atts:
            if att_name in att:
                print("\t", att.replace(android_prefix, "android:"), dynamic_atts[att])
        return True
    return False
def modify_layout_file_for_margins_and_padding(layout_file_path, id, dynamic_atts, something_changed):
    # read the layout file and check if it contains "android:padding" attribute or "android:margin" attribute.
    # If it does, print the id and the value of the attribute
    static_tree,static_layout_id_atts_map=parse_vh(layout_file_path, "static_layout")
    android_prefix="{http://schemas.android.com/apk/res/android}"
    id_att_name = android_prefix+"id"
    change_happened=False
    for elem in static_tree.iter():

        if id_att_name not in elem.attrib:
            continue
        static_id=elem.attrib[id_att_name]
        #print("id: ", id)
        static_id=static_id.split("/")[-1]
        if id == static_id:
            # atts = static_layout_id_atts_map[id]
            # print("\tatts: ", atts)
            att_name = "padding"
            found = check_if_padding_or_margin_exists(att_name, elem.attrib, dynamic_atts, id,android_prefix)
            if found:
                replace_att_value(elem, att_name)
                change_happened=True

            att_name = "layout_margin"
            found = check_if_padding_or_margin_exists(att_name, elem.attrib, dynamic_atts, id,android_prefix)
            if found:
                replace_att_value(elem, att_name)
                change_happened=True

            # check if start/end exists and replace with left/right
            att_name="layout_marginStart"
            found = check_if_start_end_exists(att_name, elem.attrib, dynamic_atts, id,android_prefix)
            if found:
                replace_start_end_att_value(elem, att_name)
                change_happened=True

            att_name="layout_marginEnd"
            found = check_if_start_end_exists(att_name, elem.attrib, dynamic_atts, id,android_prefix)
            if found:
                replace_start_end_att_value(elem, att_name)
                change_happened=True

            att_name="paddingStart"
            found = check_if_start_end_exists(att_name, elem.attrib, dynamic_atts, id,android_prefix)
            if found:
                replace_start_end_att_value(elem, att_name)
                change_happened=True

            att_name="paddingEnd"
            found = check_if_start_end_exists(att_name, elem.attrib, dynamic_atts, id,android_prefix)
            if found:
                replace_start_end_att_value(elem, att_name)
                change_happened=True

    if change_happened:
        something_changed = True
        static_tree.write(layout_file_path)
        print("changed file with margins Alic: ", layout_file_path)

    return something_changed


def write_attributes_to_vh_file(id, att_to_add):
    id_att="resource-id"
    with open(default_vh, 'r') as f:
        tree = ET.ElementTree()
        tree.parse(f)
    for elem in tree.iter():
        if id in elem.attrib[id_att]:
            for att in att_to_add:
                elem.set(att, att_to_add[att])
            tree.write(default_vh)
            break

    with open(largest_vh, 'r') as f:
        tree = ET.ElementTree()
        tree.parse(f)
    for elem in tree.iter():
        if id in elem.attrib[id_att]:
            for att in att_to_add:
                elem.set(att, att_to_add[att])
            tree.write(largest_vh)
            break




def include_attributes_needed(layout_file_path, id, something_changed):
    static_tree, static_layout_id_atts_map = parse_vh(layout_file_path, "static_layout")
    android_prefix = "{http://schemas.android.com/apk/res/android}"
    android_auto_prefix = "{http://schemas.android.com/apk/res-auto}"
    id_att_name = android_prefix + "id"
    needed_atts = ["scrollbars",'layout_constraintVertical_bias','layout_constraintHorizontal_bias','layout_alignParentLeft','layout_alignParentRight',
                   'layout_alignParentTop','layout_alignParentBottom','layout_below','layout_above','layout_toLeftOf','layout_toRightOf',
                   'layout_constraintVertical_weight','layout_constraintHorizontal_weight','layout_constraintVertical_chainStyle','layout_alignTop'
                   ,'layout_alignBottom','layout_alignLeft','layout_alignRight','layout_alignParentStart','layout_alignParentEnd',
                   'layout_alignStart','layout_alignEnd','layout_alignBaseline','layout_alignWithParentIfMissing','layout_toLeftOf',]


    att_to_add = {}
    for elem in static_tree.iter():

        if id_att_name not in elem.attrib:
            continue
        static_id = elem.attrib[id_att_name]
        # print("id: ", id)
        static_id = static_id.split("/")[-1]
        if id == static_id:
            for needed_att in needed_atts:
                if android_prefix+needed_att in elem.attrib:
                    att_to_add[android_prefix+needed_att] = elem.attrib[android_prefix+needed_att]
                if android_auto_prefix+needed_att in elem.attrib:
                    att_to_add[android_auto_prefix+needed_att] = elem.attrib[android_auto_prefix+needed_att]
            if len(att_to_add) > 0:
                something_changed = True
                write_attributes_to_vh_file(id,att_to_add)
    return something_changed


def iterate_through_origins(element_id_att_map,path_to_decompiled_apk_to_edit,apk_name):
    # iterate through key value pairs of element_id_att_map and check if atts contains origin attribute
    something_changed=False
    for id, atts_list in element_id_att_map.items():
        if 'origin' in atts_list:
            origin=atts_list['origin']
            if origin=='NOTFOUND':
                continue
            # now we have a path to a specific layout file in the subject folder
            layout_file_path=origin
            sub_path=layout_file_path.split(apk_name)[1]
            layout_file_path=path_to_decompiled_apk_to_edit+"/"+sub_path
            something_changed=modify_layout_file_for_margins_and_padding(layout_file_path, id, atts_list, something_changed)

            something_changed=include_attributes_needed(layout_file_path, id, something_changed) # like the scrollable text view attrbitue. We need to copy those from static to dynamic

            #print(id, origin)
    return something_changed


def get_ids_att_map(tree,id_att_name):
    element_id_att_map = {}
    for elem in tree.iter():
        atts=elem.attrib
        if id_att_name not in atts:
            continue
        id=atts[id_att_name]
        #print("id: ", id)
        id=id.split("/")[-1]
        element_id_att_map[id]=atts
    return element_id_att_map

# def get_ids_att_map_for_static_layout(tree):
#     element_id_att_map = {}
#     for elem in tree.iter():
#         atts=elem.attrib
#         if 'resource-id' not in atts:
#             continue
#         id=atts['resource-id']
#         #print("id: ", id)
#         element_id_att_map[id]=atts
#     return element_id_att_map







def parse_vh(xml_file, type_of_file):
    with open(xml_file, 'r') as f:
        tree = ET.ElementTree()
        tree.parse(f)
        # global apk_ids
        if(type_of_file=="vh"):
            id_att_name = "resource-id"
        elif(type_of_file=="static_layout"):
            id_att_name="{http://schemas.android.com/apk/res/android}id"
        element_id_att_map =get_ids_att_map(tree,id_att_name)


        return [tree, element_id_att_map]


def handle_margins_and_padding(apk_id,apk_name,decompiled_folder_path,subject_activity,config_dict,sg):
    device_title = config_dict['device_title']
    subjects_base_path = config_dict['subjects_base_path']
    vh_folders = config_dict['complete_vh']
    default_font = config_dict['default_font_ui']
    largest_font = config_dict['largest_display_largest_font_ui']
    print("decompiled_folder_path", decompiled_folder_path)
    global default_vh
    default_vh= subjects_base_path + apk_id + '/' + vh_folders + '/' + device_title + '/' + default_font + '/'+subject_activity+".xml"
    global largest_vh
    largest_vh= subjects_base_path + apk_id + '/' + vh_folders + '/' + device_title + '/' + largest_font + '/'+subject_activity+".xml"

    folders_in_decompiled_folder=os.walk(decompiled_folder_path).__next__()[1]
    no_folders_in_decompiled_folder=len(folders_in_decompiled_folder)
    print("no_folders_in_decompiled_folder: ", no_folders_in_decompiled_folder)
    if no_folders_in_decompiled_folder<2 or  "original" not in folders_in_decompiled_folder:
        sg.Popup('ONLY ONE FOLDER IN DECOMPILED FOLDER!!! Please copy the original folder as backup first', keep_on_top=True)
        print("ONLY ONE FOLDER IN DECOMPILED FOLDER!!! Please copy the original folder as backup first")
        print('Create "original" folder and copy the original decompiled apk there')
        return False,False
        #exit()
    else:
        print("------------------")
        tree,element_id_att_map = parse_vh(default_vh,"vh") #vh : dynamic complete VH
        # copy_apk_folder_to_backup()
        path_to_decompiled_apk_to_edit=decompiled_folder_path+apk_name
        something_changed=iterate_through_origins(element_id_att_map,path_to_decompiled_apk_to_edit,apk_name)
        return True,something_changed


if __name__ == '__main__':
    # configClass = ReadConfigClass().config
    from cleaned_gui_functions import read_subjects_csv

    config_dict = ReadConfigClass().config

    device_name = config_dict['device_name']
    device_title = config_dict['device_title']
    subjects_base_path = config_dict['subjects_base_path']
    vh_folders = config_dict['complete_vh']
    decompiled_folder=config_dict['decompiled_apk']
    default_font = config_dict['default_font_ui']
    largest_font = config_dict['largest_font_ui']





    apk_id = 'train'
    subject_csv_file = config_dict["subjects_csv"]
    activity_to_run, apk_name, subject_activity, uninstall_first = read_subjects_csv(apk_id, subject_csv_file)
    decompiled_folder_path=subjects_base_path + apk_id + '/' + decompiled_folder
    default_vh= subjects_base_path + apk_id + '/' + vh_folders + '/' + device_title + '/' + default_font + '/'+subject_activity+".xml"
    largest_vh= subjects_base_path + apk_id + '/' + vh_folders + '/' + device_title + '/' + largest_font + '/'+subject_activity+".xml"

    path_to_decompiled_apk_to_edit=decompiled_folder_path+"/"+apk_name
    print(default_vh)




    folders_in_decompiled_folder=os.walk(decompiled_folder_path).__next__()[1]
    no_folders_in_decompiled_folder=len(folders_in_decompiled_folder)
    print("no_folders_in_decompiled_folder: ", no_folders_in_decompiled_folder)
    if no_folders_in_decompiled_folder<2 or  "original" not in folders_in_decompiled_folder:
        print("ONLY ONE FOLDER IN DECOMPILED FOLDER!!! Please copy the original folder as backup first")
        print('Create "original" folder and copy the original decompiled apk there')
        exit()
    print("------------------")
    tree,element_id_att_map = parse_vh(default_vh,"vh") #vh : dynamic complete VH
    # copy_apk_folder_to_backup()

    iterate_through_origins(element_id_att_map,path_to_decompiled_apk_to_edit,apk_name)

