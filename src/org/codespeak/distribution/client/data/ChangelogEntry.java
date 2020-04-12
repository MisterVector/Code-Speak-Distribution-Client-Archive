package org.codespeak.distribution.client.data;

import java.sql.Timestamp;
import org.json.JSONObject;

/**
 *
 * @author Vector
 */
public class ChangelogEntry {

    private final String version;
    private final String content;
    private final Timestamp releaseTime;
    
    private ChangelogEntry(String version, String content, Timestamp releaseTime) {
        this.version = version;
        this.content = content;
        this.releaseTime = releaseTime;
    }
    
    public String getVersion() {
        return version;
    }
    
    public String getContent() {
        return content;
    }
    
    public Timestamp getReleaseTime() {
        return releaseTime;
    }
    
    /**
     * Converts this ChangelogEntry object to JSON
     * @return JSON representation of this ChangelogEntry object
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        
        json.put("version", version);
        json.put("content", content);
        json.put("release_time", releaseTime);
        
        return json;
    }
 
    /**
     * Creates a changelog entry object from JSON
     * @param json JSON object containing data for a ChangelogEntry object
     * @return ChangelogEntry object from JSON data
     */
    public static ChangelogEntry fromJSON(JSONObject json) {
        String version = "";
        String content = "";
        Timestamp releaseTime = null;
        
        if (json.has("version")) {
            version = json.getString("version");
        }
        
        if (json.has("content")) {
            content = json.getString("content");
        }
        
        if (json.has("release_time")) {
            releaseTime = Timestamp.valueOf(json.getString("release_time"));
        }
        
        return new ChangelogEntry(version, content, releaseTime);
    }
    
}
