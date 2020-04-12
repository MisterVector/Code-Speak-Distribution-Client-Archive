package org.codespeak.distribution.client.data;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A class representing an installed program
 *
 * @author Vector
 */
public class InstalledProgram extends Program {
    
    private final List<FileInfo> files;
    
    private InstalledProgram(Program program, List<FileInfo> files) {
        super(program.getId(), program.getCategory(), program.getSlug(), program.getName(),
              program.getDescription(), program.getSourceURL(), program.getLaunchFile(),
              program.getHelpFile(), program.getVersion(), program.getReleaseTime(), program.getDependencies());

        this.files = files;
    }
    
    public List<FileInfo> getFiles() {
        return files;
    }
    
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        JSONArray jsonFiles = new JSONArray();
        
        for (FileInfo fileInfo : files) {
            jsonFiles.put(fileInfo.toJSON());
        }
        
        json.put("files", jsonFiles);
        
        return json;
    }

    /**
     * Installs a program by taking a Program object and a list of FileInfo
     * objects and converting them into an InstalledProgram object
     * @param program the program to install
     * @param files a list of the program's files
     * @return an InstalledProgram object representing the program and its files
     */
    public static InstalledProgram install(Program program, List<FileInfo> files) {
        return new InstalledProgram(program, files);
    }
    
    /**
     * Creates an InstalledProgram object from JSON
     * @param json JSON to construct an InstalledProgram object from
     * @return InstalledProgram object represented from JSON
     */
    public static InstalledProgram fromJSON(JSONObject json) {
        Program program = Program.fromJSON(json, true);
        List<FileInfo> files = new ArrayList<FileInfo>();
        
        if (json.has("files")) {
            JSONArray jsonFiles = json.getJSONArray("files");
            
            for (int i = 0; i < jsonFiles.length(); i++) {
                JSONObject obj = jsonFiles.getJSONObject(i);
                files.add(FileInfo.fromJSON(obj));
            }
        }
        
        return new InstalledProgram(program, files);
    }
    
}
