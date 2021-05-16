package org.codespeak.distribution.client.data;

import java.time.Instant;
import org.codespeak.distribution.client.util.DateUtil;
import org.json.JSONObject;

/**
 * A class containing the response to checking the client's version
 *
 * @author Vector
 */
public class ClientCheckVersionResponse {
    
    private final String requestVersion;
    private final Instant requestReleaseTime;
    private final String version;
    private final Instant releaseTime;
    
    private ClientCheckVersionResponse(String requestVersion, Instant requestReleaseTime, String version, Instant releaseTime) {
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
    public Instant getRequestReleaseTime() {
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
    public Instant getReleaseTime() {
        return releaseTime;
    }

    /**
     * Constructs a CheckVersionResponse from JSON
     * @param json JSON to construct a CheckVersionResponse object from
     * @return CheckVersionResponse object constructed from JSON
     */
    public static ClientCheckVersionResponse fromJSON(JSONObject json) {
        String requestVersion = "";
        Instant requestReleaseTime = null;
        String version = "";
        Instant releaseTime = null;
        
        if (json.has("request_version")) {
            requestVersion = json.getString("request_version");
        }
        
        if (json.has("request_release_time")) {
            requestReleaseTime = DateUtil.getInstant(json.getString("request_release_time"));
        }
        
        if (json.has("version")) {
            version = json.getString("version");
        }
        
        if (json.has("release_time")) {
            releaseTime = DateUtil.getInstant(json.getString("release_time"));
        }
        
        return new ClientCheckVersionResponse(requestVersion, requestReleaseTime, version, releaseTime);
    }
    
}
