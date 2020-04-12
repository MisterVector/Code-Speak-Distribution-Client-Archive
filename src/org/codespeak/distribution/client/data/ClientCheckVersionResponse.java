package org.codespeak.distribution.client.data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Vector
 */
public class ClientCheckVersionResponse extends CheckVersionResponse {
    
    private final List<FileInfo> files;
    
    private ClientCheckVersionResponse(CheckVersionResponse response, List<FileInfo> files) {
        super(response.getRequestVersion(), response.getRequestReleaseTime(), response.getVersion(), response.getReleaseTime());
        
        this.files = files;
    }
    
    /**
     * Gets the files associated with this version response
     * @return 
     */
    public List<FileInfo> getFiles() {
        return files;
    }

    /**
     * Constructs a ClientCheckVersionResponse from JSON
     * @param json JSON to construct a ClientCheckVersionResponse object from
     * @return ClientCheckVersionResponse object constructed from JSON
     */
    public static ClientCheckVersionResponse fromJSON(JSONObject json) {
        CheckVersionResponse response = CheckVersionResponse.fromJSON(json);
        List<FileInfo> files = new ArrayList<FileInfo>();
        
        if (json.has("changed_files")) {
            JSONArray jsonChangedFiles = json.getJSONArray("changed_files");
            
            for (int i = 0; i < jsonChangedFiles.length(); i++) {
                JSONObject obj = jsonChangedFiles.getJSONObject(i);
                files.add(FileInfo.fromJSON(obj));
            }
        }
        
        return new ClientCheckVersionResponse(response, files);
    }
    
}
