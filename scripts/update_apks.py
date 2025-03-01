from insert_ids import *
import glob
import os
import string
import subprocess
from pathlib import Path


def read_apk_list(apks_path):
    list_apk = set()
    for filename in glob.glob(os.path.join(apks_path, '*.apk')):
        filename = os.path.splitext(filename)[0]
        apk_name = filename.rsplit('/', 1)[-1]
        # list_apk.append()
        # apk_name=apk_name.split(".")[0]
        list_apk.add(apk_name)
        # with open(ids_file_path, 'r') as apks:
        #     lines = id_value.readlines()
        #     lines = [x.strip() for x in lines]
    return list_apk


def decompile_apk(apk, apkPath, output_folder):
    apktool_command = 'apktool -f -s d ' + str(apkPath) + '/' + apk + '.apk' + ' -o ' + str(output_folder) + '/' + apk # s decompile without code so faster to recompile
    print(apktool_command)
    subprocess.run(apktool_command.split(" "))


def compile_apk(decompiled_path, output_path, apk, signature_key, fix_id=None):
    apk_name = apk + ".apk"
    # apk_name = apk + "_ided.apk"

    signature = signature_key
    decompiled_path = Path(decompiled_path)
    # apk_folder= decompiled_path.parent
    # apktool_command = 'apktool -f b ' + base_path + '/' + output_folder + '/' + decompled_folder + '/' + apk + ' -o ' + base_path + '/' + output_folder + '/' + apk_name
    #apktool_command = 'apktool -f b ' + str(decompiled_path) + "/" + apk + ' -o ' + str(output_path) + '/' + apk_name
    # use of this --use-aapt2 to compile osmfocus as mentioned in https://github.com/iBotPeaches/Apktool/issues/1978
    apktool_command = 'apktool -f b --use-aapt2 ' + str(decompiled_path) + "/" + apk + ' -o ' + str(output_path) + '/' + apk_name

    # signature_command = "jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore " + signature + " -storepass android " + str(
    #     output_path) + "/" + apk_name + " alias_name"

    signature_command = "jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore " + signature + " -storepass android " + str(
        output_path) + "/" + apk_name + " androiddebugkey"
    print(signature_command)
    subprocess.run(apktool_command.split(" "))
    # os.chdir(base_path+'/'+output_folder)
    # subprocess.run('pwd')
    subprocess.run(signature_command.split(" "))
    # "+signature+"


if __name__ == '__main__':

    base_path = '/home/ali/AppSet/accessibility/scripts/apks_folder'
    output_folder = 'apks_with_ids'
    apks_list = read_apk_list(base_path)
    print(apks_list)
    signature_key = base_path + '/' + output_folder + '/my.keystore'
    decompiled_folder = 'decompiled'
    decompiledPath = Path(base_path + '/' + output_folder + '/' + decompiled_folder)

    # First: Decompile all apks
    for apk in apks_list:
        decompile_apk(apk, decompiledPath.parent, decompiledPath)

    # Second add missign IDs to each decompiled apk
    print(decompiledPath)

    for apk in apks_list:
        add_missing_ids(apk, decompiledPath)
    #     print ('############################################################ COUNT OF NEW IDs:  ############################################################')

    # signature_key='my-release-key.keystore'

    for apk in apks_list:
        compile_apk(decompiledPath, decompiledPath.parent, apk, signature_key)
        # compile_apk(base_path, output_folder, decompiled_folder, apk, signature_key)
