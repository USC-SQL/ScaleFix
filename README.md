# ScaleFix

Copy the `config_data` file and change the required information. Currently, it has hardcoded paths. 
Create the environment variable "CONFIG_PATH" and provide the path of the updated config path. 

### To rerun the experiments.
Remove any older version installs from the device.


1. Make the Android application debuggable
2. Compile the APK file and add ids. The `decompile_and_add_id` method for adding the required ids
3. Recompile and run the IDed APK
4. Dump the ViewHeirarcy and take screenshots of the three different variation
5. Dump the state using Android Satate Dumper
6. Parse the dumped files into XML
7. Merge the VH and State
8. Add origin atrribures 
9. Run the method for handling margin and paddings
10. `runOwlEye.java` is responsible for generating the UI fixes.
   
Python functions for steps 5, 6, 7, and 8 are provided in the `curated_methods.py` file. The name of the function is `statedumper_running_state`