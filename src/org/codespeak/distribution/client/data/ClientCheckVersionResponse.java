package org.codespeak.distribution.client.data;

import java.sql.Timestamp;
import org.json.JSONObject;

/**
 * A class containing the response to checking the client's version
 *
 * @author Vector
 */
public class ClientCheckVersionResponse {
    
    private final String requestVersion;
    private final Timestamp requestReleaseTime;
    private final String version;
    private final Timestamp releaseTime;
    
    private ClientCheckVersionResponse(String requestVersion, Timestamp requestReleaseTime, String version, Timestamp releaseTime) {
        this.requestVersion = requestVersion;
        this.requestReleaseTime = requestReleaseTime;
        this.version = version;
        this.releaseTime = releaseTime;
    }
    
    /**
     * Gets the requested version
     * @return requested version
     */
    public String getRequestVersion() {
        return requestVersion;
    }
    
    /**
     * Gets the requested version's release time
     * @return requested version's release time
     */
    public Timestamp getRequestReleaseTime() {
        return requestReleaseTime;
    }
    
    /**
     * Gets the version
     * @return version
     */
    public String getVersion() {
        return version;
    }
    
    /**
     * Gets the release time
     * @return release time
     */
    public Timestamp getReleaseTime() {
        return releaseTime;
    }

    /**
     * Constructs a CheckVersionResponse from JSON
     * @param json JSON to construct a CheckVersionResponse object from
     * @return CheckVersionResponse object constructed from JSON
     */
    public static ClientCheckVersionResponse fromJSON(JSONObject json) {
        String requestVersion = "";
        Timestamp requestReleaseTime = null;
        String version = "";
        Timestamp releaseTime = null;
        
        if (json.has("request_version")) {
            requestVersion = json.getString("request_version");
        }
        
        if (json.has("request_release_time")) {
            requestReleaseTime = Timestamp.valueOf(json.getString("request_release_time"));
        }
        
        if (json.has("version")) {
            version = json.getString("version");
        }
        
        if (json.has("release_time")) {
            releaseTime = Timestamp.valueOf(json.getString("release_time"));
        }
        
        return new ClientCheckVersionResponse(requestVersion, requestReleaseTime, version, releaseTime);
    }
    
}
