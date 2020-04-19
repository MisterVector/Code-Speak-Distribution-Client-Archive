package org.codespeak.distribution.client.data;

import java.io.File;
import java.sql.Timestamp;
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
        public static FileStatus getStatusFromCode(int statusCode) {
            for (FileStatus fs : values()) {
                if (fs.getStatusCode() == statusCode) {
                    return fs;
                }
            }

            return null;
        }
    }
    
    private final String fileName;
    private final String filePath;
    private final String checksum;
    private final FileStatus status;
    private final String updateVersion;
    private final Timestamp updateTime;
    
    private FileInfo(String fileName, String filePath, String checksum, FileStatus status,
                     String updateVersion, Timestamp updateTime) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.checksum = checksum;
        this.status = status;
        this.updateVersion = updateVersion;
        this.updateTime = updateTime;
    }
    
    /**
     * Gets the name from this file information
     * @return name from this file information
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * Gets the remote file path from this file information
     * @return remote file path from this file information
     */
    public String getRemoteFilePath() {
        return filePath;
    }

    /**
     * Gets the file path from this file information
     * @return file path from this file information
     */
    public String getFilePath() {
        String tempPath = filePath;
        
        if (tempPath.indexOf("/") > -1) {
            tempPath = tempPath.replace("/", File.separator);
        }
        
        return tempPath;
    }

    /**
     * Concatenates the file path (if it exists) with the file name and returns
     * them both
     * @return concatenation of file path with file name
     */
    public String getRemoteFilePathAndName() {
        String fullPath = fileName;
        
        if (!StringUtil.isNullOrEmpty(filePath)) {
            fullPath = filePath + "/" + fullPath;
        }
        
        return fullPath;
    }

    /**
     * Concatenates the file path (if it exists) with the file name making sure to
     * use the system-dependent path separator 
     * @return concatenation of file path with file name
     */
    public String getFilePathAndName() {
        String fullPath = fileName;
        String tempFilePath = filePath;
        
        if (!StringUtil.isNullOrEmpty(tempFilePath)) {
            if (tempFilePath.indexOf("/") > -1) {
                tempFilePath = tempFilePath.replace("/", File.separator);
            }
            
            fullPath = tempFilePath + File.separator + fullPath;
        }
        
        return fullPath;
    }
    
    /**
     * Gets the checksum from this file information
     * @return checksum from this file information
     */
    public String getChecksum() {
        return checksum;
    }
    
    /**
     * Gets the file status from this file information
     * @return file status from this file information
     */
    public FileStatus getFileStatus() {
        return status;
    }
    
    /**
     * Gets the update version from this file information
     * @return update version from this file information
     */
    public String getUpdateVersion() {
        return updateVersion;
    }
    
    /**
     * Gets the update time from this file information
     * @return update time from this file information
     */
    public Timestamp getUpdateTime() {
        return updateTime;
    }
    
    /**
     * Converts this FileInfo object to JSON
     * @return JSON representation of this FileInfo object
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        
        json.put("file_name", fileName);
        json.put("file_path", filePath);
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
        Timestamp updateTime = null;
        
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
            status = FileStatus.getStatusFromCode(json.getInt("status"));
        }
        
        if (json.has("update_version")) {
            updateVersion = json.getString("update_version");
        }
        
        if (json.has("update_time")) {
            updateTime = Timestamp.valueOf(json.getString("update_time"));
        }
        
        return new FileInfo(fileName, filePath, checksum, status, updateVersion, updateTime);
    }
    
}
