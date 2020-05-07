package org.codespeak.distribution.client;

import org.json.JSONObject;

/**
 * A class containing the settings of the client
 *
 * @author Vector
 */
public class Settings {
    
    private Settings() {

    }
    
    /**
     * Converts this Settings object to JSON
     * @return JSON representation of this Settings object
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        
        return json;
    }
    
    /**
     * Creates a Settings object from JSON
     * @param json JSON representation to create a Settings object from
     * @return Settings object created from JSON
     */
    public static Settings fromJSON(JSONObject json) {
        return new Settings();
    }
    
}
