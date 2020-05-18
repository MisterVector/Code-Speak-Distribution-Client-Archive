package org.codespeak.distribution.client;

import org.json.JSONObject;

/**
 * A class containing the settings of the client
 *
 * @author Vector
 */
public class Settings {
    
    private static final boolean DEFAULT_REMEMBER_SELECTED_CATEGORY = false;
    private static final boolean DEFAULT_SHOW_CLIENT_UPDATE_ON_STARTUP = true;
    
    private boolean rememberSelectedCategory;
    private boolean showClientUpdateOnStartup;
    
    private Settings(boolean rememberSelectedCategory, boolean showClientUpdateOnStartup) {
        this.rememberSelectedCategory = rememberSelectedCategory;
        this.showClientUpdateOnStartup = showClientUpdateOnStartup;
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
     * Gets if the client is showing update notifications on startup
     * @return if the client is showing update notifications on startup
     */
    public boolean getShowClientUpdateOnStartup() {
        return showClientUpdateOnStartup;
    }
    
    /**
     * Sets if the client is showing update notifications on startup
     * @param showClientUpdateOnStartup if the client is showing update
     * notifications on startup
     */
    public void setShowClientUpdateOnStartup(boolean showClientUpdateOnStartup) {
        this.showClientUpdateOnStartup = showClientUpdateOnStartup;
    }
    
    /**
     * Converts this Settings object to JSON
     * @return JSON representation of this Settings object
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        
        json.put("remember_selected_category", rememberSelectedCategory);
        json.put("show_client_update_on_startup", showClientUpdateOnStartup);
        
        return json;
    }
    
    /**
     * Creates a Settings object from JSON
     * @param json JSON representation to create a Settings object from
     * @return Settings object created from JSON
     */
    public static Settings fromJSON(JSONObject json) {
        boolean rememberSelectedCategory = DEFAULT_REMEMBER_SELECTED_CATEGORY;
        boolean showClientUpdateOnStartup = DEFAULT_SHOW_CLIENT_UPDATE_ON_STARTUP;
        
        if (json.has("remember_selected_category")) {
            rememberSelectedCategory = json.getBoolean("remember_selected_category");
        }
        
        if (json.has("show_client_update_on_startup")) {
            showClientUpdateOnStartup = json.getBoolean("show_client_update_on_startup");
        }
        
        return new Settings(rememberSelectedCategory, showClientUpdateOnStartup);
    }
    
}
