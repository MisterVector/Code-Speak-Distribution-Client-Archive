package org.codespeak.distribution.client.data;

import java.io.File;
import java.time.Instant;
import org.codespeak.distribution.client.util.DateUtil;
import org.codespeak.distribution.client.util.StringUtil;
import org.json.JSONObject;

/**
 * A class representing information for a file
 *
 * @author Vector
 */
public class FileInfo {
    
    public enum FileStatus {
        NEW(1),
        MODIFIED(2),
        REMOVED(3);

        private final int statusCode;

        private FileStatus(final int statusCode) {
            this.statusCode = statusCode;
        }

        /**
         * Gets the code of this file status
         * @return code of this file status
         */
        public int getStatusCode() {
            return statusCode;
        }
                
        /**
         * Gets a file status from its code
         * @param statusCode code to match file status
         * @return a file status based on the code
         */
        public static FileStatus fromCode(int statusCode) {
            for (FileStatus fs : values()) {
                if (fs.getStatusCode() == statusCode) {
                    return fs;
                }
            }

            return null;
        }
    }
    
    private final String name;
    private final String path;
    private final String checksum;
    private final FileStatus status;
    private final String updateVersion;
    private final Instant updateTime;
    
    private FileInfo(String name, String path, String checksum, FileStatus status,
                     String updateVersion, Instant updateTime) {
        this.name = name;
        this.path = path;
        this.checksum = checksum;
        this.status = status;
        this.updateVersion = updateVersion;
        this.updateTime = updateTime;
    }
    
    /**
     * Gets the name from this file
     * @return name from this file
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the remote path from this file
     * @return remote path from this file
     */
    public String getRemotePath() {
        return path;
    }

    /**
     * Gets the path from this file
     * @return path from this file
     */
    public String getPath() {
        String tempPath = path;
        
        if (tempPath.indexOf("/") > -1) {
            tempPath = tempPath.replace("/", File.separator);
        }
        
        return tempPath;
    }

    /**
     * Concatenates the remote path (if it exists) with the remote name and
     * returns them both
     * @return concatenation of remote path and name
     */
    public String getRemotePathAndName() {
        String fullPath = name;
        
        if (!StringUtil.isNullOrEmpty(path)) {
            fullPath = path + "/" + fullPath;
        }
        
        return fullPath;
    }

    /**
     * Concatenates the path (if it exists) with the name making sure to
     * use the system-dependent path separator 
     * @return concatenation of path and name
     */
    public String getPathAndName() {
        String fullPath = name;
        String tempPath = path;
        
        if (!StringUtil.isNullOrEmpty(tempPath)) {
            if (tempPath.indexOf("/") > -1) {
                tempPath = tempPath.replace("/", File.separator);
            }
            
            fullPath = tempPath + File.separator + fullPath;
        }
        
        return fullPath;
    }
    
    /**
     * Gets the checksum of this file
     * @return checksum of this file
     */
    public String getChecksum() {
        return checksum;
    }
    
    /**
     * Gets the status of this file
     * @return status of this file
     */
    public FileStatus getFileStatus() {
        return status;
    }
    
    /**
     * Gets the update version of this file
     * @return update version of this file
     */
    public String getUpdateVersion() {
        return updateVersion;
    }
    
    /**
     * Gets the update time of this file
     * @return update time of this file
     */
    public Instant getUpdateTime() {
        return updateTime;
    }
    
    /**
     * Converts this FileInfo object to JSON
     * @return JSON representation of this FileInfo object
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        
        json.put("file_name", name);
        json.put("file_path", path);
        json.put("checksum", checksum);
        json.put("status", status.getStatusCode());
        json.put("update_version", updateVersion);
        json.put("update_time", updateTime.toString());
        
        return json;
    }

    /**
     * Constructs a FileInfo object from JSON
     * @param json JSON to construct a FileInfo object from
     * @return FileInfo object constructed from JSON
     */
    public static FileInfo fromJSON(JSONObject json) {
        String fileName = "";
        String filePath = "";
        String checksum = "";
        FileStatus status = FileStatus.NEW;
        String updateVersion = "";
        Instant updateTime = null;
        
        if (json.has("file_name")) {
            fileName = json.getString("file_name");
        }
        
        if (json.has("file_path")) {
            filePath = json.getString("file_path");
        }
        
        if (json.has("checksum")) {
            checksum = json.getString("checksum");
        }
        
        if (json.has("status")) {
            status = FileStatus.fromCode(json.getInt("status"));
        }
        
        if (json.has("update_version")) {
            updateVersion = json.getString("update_version");
        }
        
        if (json.has("update_time")) {
            updateTime = DateUtil.getInstant(json.getString("update_time"));
        }
        
        return new FileInfo(fileName, filePath, checksum, status, updateVersion, updateTime);
    }
    
}
