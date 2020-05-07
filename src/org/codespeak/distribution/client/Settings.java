package org.codespeak.distribution.client;

import org.json.JSONObject;

/**
 * A class containing the settings of the client
 *
 * @author Vector
 */
public class Settings {
    
    private static final boolean DEFAULT_REMEMBER_SELECTED_CATEGORY = false;
    
    private boolean rememberSelectedCategory;
    
    private Settings(boolean rememberSelectedCategory) {
        this.rememberSelectedCategory = rememberSelectedCategory;
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
     * Converts this Settings object to JSON
     * @return JSON representation of this Settings object
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        
        json.put("remember_selected_category", rememberSelectedCategory);
        
        return json;
    }
    
    /**
     * Creates a Settings object from JSON
     * @param json JSON representation to create a Settings object from
     * @return Settings object created from JSON
     */
    public static Settings fromJSON(JSONObject json) {
        boolean rememberSelectedCategory = DEFAULT_REMEMBER_SELECTED_CATEGORY;
        
        if (json.has("remember_selected_category")) {
            rememberSelectedCategory = json.getBoolean("remember_selected_category");
        }
        
        return new Settings(rememberSelectedCategory);
    }
    
}
