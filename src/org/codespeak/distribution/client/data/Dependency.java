package org.codespeak.distribution.client.data;

import org.json.JSONObject;

/**
 * A class representing a dependency
 *
 * @author Vector
 */
public class Dependency {
    
    private final int id;
    private final String name;
    private final String description;
    private final String url;
    private final boolean displayOnClient;
    
    private Dependency(int id, String name, String description, String url, boolean displayOnClient) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
        this.displayOnClient = displayOnClient;
    }
    
    /**
     * Gets the ID of this dependency
     * @return ID of this dependency
     */
    public int getId() {
        return id;
    }
    
    /**
     * Gets the name of this dependency
     * @return name of this dependency
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the description of this dependency
     * @return description of this dependency
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Gets the URL of this dependency
     * @return URL of this dependency
     */
    public String getURL() {
        return url;
    }
    
    /**
     * Gets if this dependency is displayed on the client
     * @return if this dependency is displayed on the client
     */
    public boolean isDisplayOnClient() {
        return displayOnClient;
    }
    
    /**
     * Converts this dependency object to JSON
     * @return JSON representation of this Dependency object
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        
        json.put("id", id);
        json.put("name", name);
        json.put("description", description);
        json.put("url", url);
        json.put("display_on_client", displayOnClient);
        
        return json;
    }
    
    @Override
    public int hashCode() {
        return id;
    }
    
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Dependency)) {
            return false;
        }
        
        Dependency dependency = (Dependency) other;
        
        return this.getId() == dependency.getId();
    }
    
    /**
     * Creates a dependency object from a JSON object
     * @param json JSON object comprising a dependency
     * @return a dependency object
     */
    public static Dependency fromJSON(JSONObject json) {
        int id = 0;
        String name = "";
        String description = "";
        String url = "";
        boolean displayOnClient = false;

        if (json.has("id")) {
            id = json.getInt("id");
        }
        
        if (json.has("name")) {
            name = json.getString("name");
        }
        
        if (json.has("description")) {
            description = json.getString("description");
        }
        
        if (json.has("url")) {
            url = json.getString("url");
        }
        
        if (json.has("display_on_client")) {
            displayOnClient = json.getBoolean("display_on_client");
        }

        return new Dependency(id, name, description, url, displayOnClient);
    }
    
}
