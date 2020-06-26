package org.codespeak.distribution.client;

import org.json.JSONObject;

/**
 * A class containing the settings of the client
 *
 * @author Vector
 */
public class Settings {
    
    private static final boolean DEFAULT_REMEMBER_SELECTED_CATEGORY = false;
    private static final boolean DEFAULT_CHECK_CLIENT_UPDATE_ON_STARTUP = true;
    private static final boolean DEFAULT_BACKUP_BEFORE_REMOVING_TEXT_FILES = true;
    
    private boolean rememberSelectedCategory;
    private boolean checkClientUpdateOnStartup;
    private boolean backupBeforeRemovingTextFiles;
    
    private Settings(boolean rememberSelectedCategory, boolean checkClientUpdateOnStartup, boolean backupBeforeRemovingTextFiles) {
        this.rememberSelectedCategory = rememberSelectedCategory;
        this.checkClientUpdateOnStartup = checkClientUpdateOnStartup;
        this.backupBeforeRemovingTextFiles = backupBeforeRemovingTextFiles;
    }
    
    /**
     * Gets whether the selected category is remembered
     * @return whether the selected category is remembered
     */
    public boolean getRememberSelectedCategory() {
        return rememberSelectedCategory;
    }

    /**
     * Sets whether the current category will be remembered
     * @param rememberSelectedCategory whether the current category will
     *                                 be remembered
     */
    public void setRememberSelectedCategory(boolean rememberSelectedCategory) {
        this.rememberSelectedCategory = rememberSelectedCategory;
    }
    
    /**
     * Gets if the client is checking for an update on startup
     * @return if the client is checking for an update on startup
     */
    public boolean getCheckClientUpdateOnStartup() {
        return checkClientUpdateOnStartup;
    }
    
    /**
     * Sets if the client is checking for an update on startup
     * @param checkClientUpdateOnStartup if the client is checking for an update
     * on startup
     */
    public void setCheckClientUpdateOnStartup(boolean checkClientUpdateOnStartup) {
        this.checkClientUpdateOnStartup = checkClientUpdateOnStartup;
    }
    
    /**
     * Gets if the client is backing up text files before removing them
     * @return if the client is backing up text files before removing them
     */
    public boolean getBackupBeforeRemovingTextFiles() {
        return backupBeforeRemovingTextFiles;
    }
    
    /**
     * Sets if the client is backing up text files before removing them
     * @param backupBeforeRemovingTextFiles if the client is backing up non-empty text files on update
     */
    public void setBackupBeforeRemovingTextFiles(boolean backupBeforeRemovingTextFiles) {
        this.backupBeforeRemovingTextFiles = backupBeforeRemovingTextFiles;
    }
    
    /**
     * Converts this Settings object to JSON
     * @return JSON representation of this Settings object
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        
        json.put("remember_selected_category", rememberSelectedCategory);
        json.put("check_client_update_on_startup", checkClientUpdateOnStartup);
        json.put("backup_before_removing_text_files", backupBeforeRemovingTextFiles);
        
        return json;
    }
    
    /**
     * Creates a Settings object from JSON
     * @param json JSON representation to create a Settings object from
     * @return Settings object created from JSON
     */
    public static Settings fromJSON(JSONObject json) {
        boolean rememberSelectedCategory = DEFAULT_REMEMBER_SELECTED_CATEGORY;
        boolean checkClientUpdateOnStartup = DEFAULT_CHECK_CLIENT_UPDATE_ON_STARTUP;
        boolean backupBeforeRemovingTextFiles = DEFAULT_BACKUP_BEFORE_REMOVING_TEXT_FILES;
        
        if (json.has("remember_selected_category")) {
            rememberSelectedCategory = json.getBoolean("remember_selected_category");
        }
        
        if (json.has("check_client_update_on_startup")) {
            checkClientUpdateOnStartup = json.getBoolean("check_client_update_on_startup");
        }
        
        if (json.has("backup_before_removing_text_files")) {
            backupBeforeRemovingTextFiles = json.getBoolean("backup_before_removing_text_files");
        }
        
        return new Settings(rememberSelectedCategory, checkClientUpdateOnStartup, backupBeforeRemovingTextFiles);
    }
    
}
