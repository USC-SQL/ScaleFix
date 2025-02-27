package usc.edu.SALEM.util;

import usc.edu.OwlEye.OwlConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.Scanner;

public class LoadConfig {
    private static LoadConfig instance = null;



    private static HashMap<String, String> config_data=null;
    private LoadConfig() {
        readIniConfig(OwlConstants.configFile);
    }

    public static LoadConfig getInstance() {
        if (instance == null) {
            instance = new LoadConfig();
        }
        return instance;
    }
    public static HashMap<String, String> getConfig_data() {
        return config_data;
    }

    //Load config file and return a hashmap of key-value pairs
    private Properties readConfigProperty(String fileName){
        File file = new File(fileName);
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(fileName)) {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

    //Load config file and return a hashmap of key-value pairs
    private HashMap<String, String> readIniConfig(String fileName)  {
        String home_dir = System.getProperty("user.home");

        HashMap<String, String> config = new HashMap<String, String>();
        File file = new File(fileName);
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            if(line.startsWith("#")){
                continue;
            }
            String[] tokens = line.split("=");
            if(tokens.length != 2){
                continue;
            }
            String val= tokens[1].trim();
            if (val.contains("/home/ali")) {
                val = val.replace("/home/ali", home_dir);

            }

            config.put(tokens[0].toLowerCase(Locale.ROOT), val);
        }
        scanner.close();
        config_data = config;
        return config;
    }

}
