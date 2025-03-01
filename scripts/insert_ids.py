import uuid
import xml.etree.ElementTree as ET
import glob
import os
import string
from pathlib import Path

# <item type="id" name="imageView1" />
# apk_name='souch.smp_16'
# base_path= '/home/ali/AppSet/accessibility/scripts/'
# layout_path = '/home/ali/AppSet/accessibility/scripts/souch.smp_16/res/layout-v17'
# value_path='/home/ali/AppSet/accessibility/scripts/souch.smp_16/res/values/ids.xml'
# layout_files=[]
# apk_ids=[]
# class InsterID:
#     num = 0
#
#     def __init__(self, name):
#         self.name = name
#


read_files = set()


def generate_id(package_name):
    # change the rondom ID to specific ID
    print("The random id using uuid1() is : ", end="")

    return str(uuid.uuid1()).replace("-", "")


def get_layout(base_path):
    path = base_path
    filenames = os.listdir(path)  # get all files' and folders' names in the current directory
    result = []
    for filename in filenames:  # loop through all the files and folders
        if os.path.isdir(
                os.path.join(os.path.abspath(path), filename)):  # check whether the current object is a folder or not
            result.append(filename)
    layout_files = []
    for x in result:

        if 'layout' in x:
            # layout_files.append(x)
            print(x)
            cleaned = x.split('-v')
            if len(cleaned) == 1:  # default layout so no -v
                if cleaned[0].strip() == 'layout': #skipping all non necessary layouts such as layout-land, layout-watch, etc
                    cleaned.append(0)
                else:
                    continue
            if cleaned[0].strip() == 'layout': # Sep30 adding it to fix the issues with kaba adding id to the root layout-land-v17
                layout_files.append(cleaned)
                print('cleaned: ', cleaned)

    for x in layout_files:
        print(x[1])
    #         files.put(0,'layout')
    #     else:
    #         files.put(1)
    print('ALi : ', len(layout_files))
    layout_files.sort(key=lambda x: int(x[1]))
    print('xxx', layout_files)
    return layout_files


def add_ids(full_resource_path, layout_file_name, value_path_path, public_xml_path, package_name,layout_files_list):
    '''
     Currently I am only adding ids to the most recent layout api folder then go down and add ids for only the new files in each layout folder. For example if layout-21 has main.xml file I will add ids to it
 Then layout-v17 has only main.xml then I will not add ids there but if it has about.xml then I will add ids to that file. The reason is that I am using recent emulator and it will alawys go with the most recent one
    '''
    # TODO: I should add same id for the same widgets in different layout files (for example main.xml in layout-v17, and layout-v21 and layout) so I can generate fixes and any layout runs it will show correct result
    full_path = Path(full_resource_path / layout_file_name)
    ids_file_path = Path(full_resource_path / value_path_path)
    print("ALI:::::::::::::::::::::", layout_file_name)
    for filename in glob.glob(os.path.join(full_path, '*.xml')):

        xml_name = filename.rsplit('/', 1)[-1]
        print("FLE NAME: ", xml_name)
        if not xml_name in read_files:
            read_files.add(xml_name)
            # if '-'  in layout_file_name:
            #      continue
            with open(os.path.join(full_path, filename), 'r') as f:
                tree = ET.ElementTree()
                tree.parse(f)
                # global apk_ids
                apk_ids = get_ids_file(ids_file_path)
                apk_ids, id_to_public_file = check_att(tree, apk_ids, package_name,full_path,layout_files_list,full_resource_path,value_path_path, public_xml_path)
                rewrite_ids_file(ids_file_path, apk_ids)
                rewrite_ids_to_public(public_xml_path, id_to_public_file)
                tree.write(os.path.join(full_path, filename))
        else:
            print(xml_name, " HAS ALREDY BEEN CHECKED BEFORE")
            # We have already added Ids to the same file from newer API
            # TODO: we have to add files to that too


# def parse_xml(path):
#     for filename in glob.glob(os.path.join(path, '*.xml')):
#         with open(os.path.join(path, filename), 'r') as f: # open in readonly mode
#             tree = ET.ElementTree()
#             print(f)
#             tree = ET.ElementTree()
#             tree.parse(f)
#             # tree = ET.parse(f)
#             # root = tree.getroot()
#             global apk_ids
#             get_ids_file()
#             print(apk_ids)
#             check_att(tree)
#             #print(root)
#             rewrite_ids_file()
#             tree.write(os.path.join(path, filename))


def rewrite_ids_file(ids_file_path, apk_ids):
    # global apk_ids
    apk_ids.append("</resources>")
    # newFile=set(apk_ids)
    # newFile.append("</resources>")

    #print(apk_ids)
    with open(ids_file_path, 'w', newline='') as id_value:
        for item in apk_ids:
            #id_value.write("%s\n" % item)
            id_value.write("%s" % item)
           # print(item)


def get_last_id(public_xml_path):
    last_id = ""
    with open(public_xml_path, 'r', newline='') as id_value:
        for item in id_value:
            if 'type="id"' in item:
                # print("ID in Menu FOUND ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^, ",item)
                listx = item.strip().split(" ")
                # list(x.split("=") for x in item.split(" "))
    print('listx: ', listx)
    print('listx[2]: ', listx[3])
    last_id = listx[3].split('="')
    last_id = last_id[1].strip('"')
    print('last_id: ', last_id)
    # id_value.write("%s" % item)
    return last_id


def write_to_public(public_xml_path, id_to_public_file, id_int):
    original_public_content = get_ids_file(public_xml_path)  # to get all values but remove the close tag of resources

    with open(public_xml_path, 'w', newline='') as id_value:
        for item in original_public_content:
            id_value.write("%s" % item)

        for raw_id in id_to_public_file:
            id_int = id_int + 1
            # id_value.write("%s\n" % item)
            hex_string = '0x{:02x}'.format(id_int)
            idd = '<public type="id" name="' + str(raw_id) + '" id="' + str(hex_string) + '" />\n'
            id_value.write("%s" % idd)
        id_value.write("</resources>")


def rewrite_ids_to_public(public_xml_path, id_to_public_file):
    last_id = get_last_id(public_xml_path)
    id_int = int(last_id, 0)
    print('|||||||||||||||||||||||||||||||||||||||||||||||||||** ', last_id, ":::", id_int, " + 1 = ", id_int + 1,
          " **||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||")
    write_to_public(public_xml_path, id_to_public_file, id_int)


def get_ids_file(ids_file_path):
    with open(ids_file_path, 'r') as id_value:
        lines = id_value.readlines()
        lines = lines[:-1]
    # global apk_ids
    # apk_ids=lines
    return lines


# radeon.modeset=0
def handle_include(elem, apk_ids,id_to_public_file, package_name,full_path,layout_files_list,full_resource_path,value_path_path, public_xml_path):
    elem_layout= elem.attrib['layout']
    print("INCLUDE: ", elem_layout)
    include_d=elem_layout.split('/')[-1]
    final_id= ""
    for lay_file in reversed(layout_files_list):
        if lay_file[1] != 0:
            cname = lay_file[0] + '-v' + lay_file[1]
        else:
            #     cname = lay_file[0]
            cname = 'layout'  # TODO Could be other layouts for different langauge, right now I am only considering enrglish such as layout-es
        print('cname: ', cname)

        full_path = Path(full_resource_path / cname)

        for filename in glob.glob(os.path.join(full_path, '*.xml')):
            if include_d in filename:
                print("INCLUDE FILE: ", filename)
                apk_ids,id_to_public_file = process_adding_ids_for_include(filename, full_path, full_resource_path, layout_files_list, package_name,
                                             public_xml_path, value_path_path,apk_ids,id_to_public_file)
                with open(os.path.join(full_path, filename), 'r') as f:
                    tree = ET.ElementTree()
                    tree.parse(f)
                    for elem in tree.iter():
                        print(elem)
                        name = elem.attrib.get('name')
                        tag = elem.tag
                        print("name: ", name, " tag: ", tag)
                        if tag == 'include':
                            continue
                        if '{http://schemas.android.com/apk/res/android}id'  in elem.attrib:
                            final_id= elem.attrib['{http://schemas.android.com/apk/res/android}id']
                            break

                # tree.write(os.path.join(full_path, filename))
        #pass
    #pass
    if final_id != "":
        print("FINAL ID: ", final_id)
        idd2 = '<item type="id" name="' + final_id.replace("@id/","") + '" />\n'
        # if idd2 not in apk_ids:
        #     apk_ids.append(idd2)
        #id_to_public_file.append(final_id.replace("@id/",""))
        print("FINAL ID: ", final_id)
        # if idd2.replace("\n","") not in apk_ids:
        #     apk_ids.append(idd2)
           # id_to_public_file.append(final_id.replace("@id/", ""))
        #apk_ids.append(idd2)
    return final_id, apk_ids


def process_adding_ids_for_include(filename, full_path, full_resource_path, layout_files_list, package_name, public_xml_path,
                       value_path_path,apk_ids,id_to_public_file):
    xml_name = filename.rsplit('/', 1)[-1]
    print("FLE NAME: ", xml_name)
    ids_file_path = Path(full_resource_path / value_path_path)

    if not xml_name in read_files:
        read_files.add(xml_name)
        # if '-'  in layout_file_name:
        #      continue
        with open(os.path.join(full_path, filename), 'r') as f:
            tree = ET.ElementTree()
            tree.parse(f)
            # global apk_ids
          #  apk_ids = get_ids_file(ids_file_path)
            apk_ids, id_to_public_file = check_att(tree, apk_ids, package_name, full_path,
                                                   layout_files_list, full_resource_path, value_path_path,
                                                   public_xml_path)
           # rewrite_ids_file(ids_file_path, apk_ids)
            #rewrite_ids_to_public(public_xml_path, id_to_public_file)
            #tree.write(os.path.join(full_path, filename))
    else:
        print(xml_name, " HAS ALREADY BEEN CHECKED BEFORE")
        # We have already added Ids to the same file from newer API
        # TODO: we have to add files to that too
    return apk_ids, id_to_public_file


def check_att(tree, apk_ids, package_name,full_path,layout_files_list,full_resource_path,value_path_path, public_xml_path):
    id_to_public_file = []
    for elem in tree.iter():
        print (elem)
        name=elem.attrib.get('name')
        tag=elem.tag
        print("name: ", name, " tag: ", tag)
        new_id_added= False
        if tag == 'include':
            included_id,apk_ids=handle_include(elem, apk_ids,id_to_public_file, package_name,full_path,layout_files_list,full_resource_path,value_path_path, public_xml_path)
           # new_id = generate_id(package_name)
            if "@+id" in included_id:
                change="@+id/"
            else:
                change="@id/"
            change=""
            new_incld=included_id.replace(change, '')
            elem.set("referenced_id",  included_id.replace(change, ''))
            print('INCLUDED ID: ', included_id.replace(change, ''))
          #  print('######## NEW ID: ', elem.attrib['{http://schemas.android.com/apk/res/android}id'])

            # ADDING MISSING IDS
         #   idd = '<item type="id" name="' + new_id + '" />\n'
          #  apk_ids.append(idd)
           # id_to_public_file.append(new_id)
            continue
        elif '{http://schemas.android.com/apk/res/android}id' not in elem.attrib:
            # print ("There is NO id in such elem" )
            new_id = generate_id(package_name)
            elem.set('{http://schemas.android.com/apk/res/android}id', '@id/' + new_id)
            print('######## NEW ID: ', elem.attrib['{http://schemas.android.com/apk/res/android}id'])

            # ADDING MISSING IDS
            idd = '<item type="id" name="' + new_id + '" />\n'
            if idd.replace("\n","") not in apk_ids:
                apk_ids.append(idd)
            #apk_ids.append(idd)
            id_to_public_file.append(new_id)
            # print ('######################################### ', id)
            # add_to_value(new_id,apk_ids)
        # else:
        #      print("ID: ", elem.attrib['{http://schemas.android.com/apk/res/android}id'])
    return apk_ids, id_to_public_file


# def add_to_value(id,apk_ids):
#     global apk_ids
#     apk_ids.append('<item type="id" name="'+id+'" />\n')
#     print ('######################################### ', id)
#     #  with open(value_path, 'a') as id_value:
#     #       id_value.write('<item type="id" name="'+id+'" />\n')


ET.register_namespace("android", "http://schemas.android.com/apk/res/android")


def add_missing_ids(apk_name, base_path, package_name):
    # base_path = '/home/ali/AppSet/accessibility/scripts'
    value_path_path = 'values/ids.xml'
    public_xml_path = 'values/public.xml'
    # base_path='/home/ali/AppSet/accessibility/scripts/'
    # apk_name = 'com.adzenze.FashionDesignFlatSketch'
    app = apk_name + '/res/'
    pp = Path(base_path + "/" + app)
    full_resource_path = Path(pp)
    value_path_path = Path(full_resource_path / value_path_path)
    public_xml_path = Path(full_resource_path / public_xml_path)
    layout_files_list = get_layout(full_resource_path)
    print(value_path_path)
    read_files.clear()
    for lay_file in reversed(layout_files_list):
        if lay_file[1] != 0:
            cname = lay_file[0] + '-v' + lay_file[1]
        else:
            #     cname = lay_file[0]
            cname = 'layout'  # TODO Could be other layouts for different langauge, right now I am only considering enrglish such as layout-es
        print('cname: ', cname)
        add_ids(full_resource_path, cname, value_path_path, public_xml_path, package_name,layout_files_list)
