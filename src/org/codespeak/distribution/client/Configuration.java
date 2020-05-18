package org.codespeak.distribution.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import org.json.JSONObject;

/**
 * A configuration class
 *
 * @author Vector
 */
public class Configuration {

    public static final String PROGRAM_VERSION = "0.0.0";
    public static final String PROGRAM_NAME = "Code Speak Distribution Client";
    public static final String PROGRAM_TITLE = PROGRAM_NAME + " v" + PROGRAM_VERSION;
    public static final String WEBSITE_URL = "http://www.codespeak.org";
    public static final String CONTACT_ME_PAGE = WEBSITE_URL + "/contact";
    public static final String DISTRIBUTION_URL = "http://distribution.codespeak.org";
    public static final String BACKEND_URL = DISTRIBUTION_URL + "/data_handler.php";
    public static final String PROGRAMS_FOLDER = "programs";
    public static final String LOGS_FOLDER = "logs";
    public static final String DATA_FILE = "data.json";
    public static final String SETTINGS_FILE = "settings.json";
    public static final String UPDATER_FILE = "Code_Speak_Distribution_Updater.jar";
    
    private static Settings settings = null;

    private static Settings loadSettings() {
        File settingsFile = new File(SETTINGS_FILE);
        
        if (!settingsFile.exists()) {
            return Settings.fromJSON(new JSONObject());
        }
        
        try {
            byte[] bytes = Files.readAllBytes(settingsFile.toPath());
            String jsonString = new String(bytes);
            JSONObject json = new JSONObject(jsonString);
            JSONObject jsonSettings = json.getJSONObject("settings");
            
            return Settings.fromJSON(jsonSettings);
        } catch (IOException ex) {
            return Settings.fromJSON(new JSONObject());
        }
    }
    
    /**
     * Gets the settings. The settings will be loaded if not initialized
     * @return loaded settings
     */
    public static Settings getSettings() {
        if (settings == null) {
            settings = loadSettings();
        }
        
        return settings;
    }

    /**
     * Writes the settings to file
     */
    public static void writeSettingsToFile() {
        if (settings == null) {
            return;
        }
        
        File settingsFile = new File(SETTINGS_FILE);
        JSONObject json = new JSONObject();
        
        json.put("settings", settings.toJSON());

        if (settingsFile.exists()) {
            settingsFile.delete();
        }
        
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(settingsFile))) {
            writer.write(json.toString(4));
        } catch (IOException ex) {
            
        }
    }
    
}
