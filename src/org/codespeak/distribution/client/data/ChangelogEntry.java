package org.codespeak.distribution.client.data;

import java.time.Instant;
import org.codespeak.distribution.client.util.DateUtil;
import org.json.JSONObject;

/**
 *
 * @author Vector
 */
public class ChangelogEntry {

    private final String version;
    private final String content;
    private final Instant releaseTime;
    
    private ChangelogEntry(String version, String content, Instant releaseTime) {
        this.version = version;
        this.content = content;
        this.releaseTime = releaseTime;
    }
    
    /**
     * Gets the version of this changelog entry
     * @return version of this changelog entry
     */
    public String getVersion() {
        return version;
    }
    
    /**
     * Gets the content of this changelog entry
     * @return content of this changelog entry
     */
    public String getContent() {
        return content;
    }
    
    /**
     * Gets the release time of this changelog entry
     * @return release time of this changelog entry
     */
    public Instant getReleaseTime() {
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
        Instant releaseTime = null;
        
        if (json.has("version")) {
            version = json.getString("version");
        }
        
        if (json.has("content")) {
            content = json.getString("content");
        }
        
        if (json.has("release_time")) {
            releaseTime = DateUtil.getInstant(json.getString("release_time"));
        }
        
        return new ChangelogEntry(version, content, releaseTime);
    }
    
}
