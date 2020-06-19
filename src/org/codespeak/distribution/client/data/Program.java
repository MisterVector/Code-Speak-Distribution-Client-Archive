package org.codespeak.distribution.client.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.codespeak.distribution.client.handler.DataHandler;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.codespeak.distribution.client.Configuration;
import org.codespeak.distribution.client.objects.ClientException;
import org.codespeak.distribution.client.data.query.QueryTypes;
import org.codespeak.distribution.client.handler.BackendHandler;
import org.codespeak.distribution.client.util.MiscUtil;
import org.codespeak.distribution.client.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A class representing a program
 *
 * @author Vector
 */
public class Program {

    private final int id;
    private Category category;
    private String slug;
    private String name;
    private String description;
    private String launchFile;
    private String helpFile;
    private String version;
    private Timestamp releaseTime;
    private List<Dependency> dependencies;
    private boolean installed;
    private boolean detached = false;
    
    protected Program(int id, Category category, String slug, String name, String description,
                    String launchFile, String helpFile, String version, Timestamp releaseTime,
                    List<Dependency> dependencies, boolean installed) {
        this.id = id;
        this.category = category;
        this.slug = slug;
        this.name = name;
        this.description = description;
        this.launchFile = launchFile;
        this.helpFile = helpFile;
        this.version = version;
        this.releaseTime = releaseTime;
        this.dependencies = dependencies;
        this.installed = installed;
    }
    
    /**
     * Gets the ID of this program
     * @return ID of this program
     */
    public int getId() {
        return id;
    }
    
    /**
     * Gets the category of this program
     * @return 
     */
    public Category getCategory() {
        return category;
    }
    
    /**
     * Gets the slug of this program
     * @return slug of this program
     */
    public String getSlug() {
        return slug;
    }
    
    /**
     * Gets the name of this program
     * @return name of this program
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the description of this program
     * @return description of this program
     */
    public String getDescription() {
        return description;
    }
    
   /**
     * Gets the launch file of this program
     * @return launch file of this program
     */
    public String getLaunchFile() {
        return launchFile;
    }
    
    
    public String getHelpFile() {
        return helpFile;
    }
    
    /**
     * Gets the version of this program
     * @return version of this program
     */
    public String getVersion() {
        return version;
    }
    
    /**
     * Gets the release time of this program
     * @return release time of this program
     */
    public Timestamp getReleaseTime() {
        return releaseTime;
    }

    /**
     * Gets a list of dependencies of this program
     * @return list of dependencies of this program
     */
    public List<Dependency> getDependencies() {
        return getDependencies(true);
    }

    /**
     * Gets a list of dependencies of this program
     * @param allowHidden whether hidden dependencies are returned
     * @return list of dependencies of this program
     */
    public List<Dependency> getDependencies(boolean allowHidden) {
        List<Dependency> ret = new ArrayList<Dependency>();
        
        for (Dependency dependency : dependencies) {
            if (allowHidden || !dependency.isHideOnClient()) {
                ret.add(dependency);
            }
        }
        
        return ret;
    }

    /**
     * Gets if this program object represents an installed program
     * @return whether this program is installed
     */
    public boolean isInstalled() {
        return installed;
    }

    /**
     * Checks if this program is detached from the distribution system
     * @return if this program is detached from the distribution system
     */
    public boolean isDetached() {
        return detached;
    }
    
    /**
     * Sets if this program is detached from the distribution system
     * @param detached if this program is detached from the distribution system
     */
    public void setDetached(boolean detached) {
        this.detached = detached;
    }
    
    /**
     * Gets the directory of this program
     * @return directory of this program
     */
    public Path getDirectory() {
        return getDirectory(false);
    }
    
    /**
     * Gets the directory of this program
     * @param includeLaunchFile whether the launch file is included
     * @return directory of this program
     */
    public Path getDirectory(boolean includeLaunchFile) {
        String programPath = Configuration.PROGRAMS_FOLDER + File.separator + slug;
        
        if (includeLaunchFile) {
            programPath += File.separator + launchFile;
        }
        
        return Paths.get(programPath);
    }
    
    /**
     * Uninstalls this program
     * @throws IOException thrown if an error occurs while installing
     */
    public void uninstall() throws IOException {
        File programFolder = getDirectory().toFile();

        Files.walk(programFolder.toPath())
             .sorted(Comparator.reverseOrder())
             .map(Path::toFile)
             .forEach(File::delete);
        
        installed = false;
    }
    
    /**
     * Installs this program
     * @throws org.codespeak.distribution.client.objects.ClientException if an
     * error occurs while performing a query
     * @throws IOException thrown if an error occurs while installing
     */
    public void install() throws IOException, ClientException {
        List<FileInfo> files = BackendHandler.getDataFromQuery(QueryTypes.GET_PROGRAM_FILES, "&id=" + id);
        Path programDirectory = getDirectory();
        File programDirectoryFile = programDirectory.toFile();
        
        programDirectoryFile.mkdir();
        
        for (FileInfo file : files) {
            String currentFilePath = file.getPath();
            String currentFilePathAndName = file.getPathAndName();
            String currentRemotePathAndName = file.getRemotePathAndName();

            if (!StringUtil.isNullOrEmpty(currentFilePath)) {
                Path fullFilePath = programDirectory.resolve(currentFilePath);

                fullFilePath.toFile().mkdirs();
            }
            
            Path localFilePathAndName = programDirectory.resolve(currentFilePathAndName);
            ReadableByteChannel readableByteChannel = BackendHandler.getRemoteFileChannel(slug, currentRemotePathAndName);
            FileChannel outChannel = new FileOutputStream(localFilePathAndName.toFile()).getChannel();
            
            outChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            readableByteChannel.close();
            outChannel.close();
        }
        
        installed = true;
    }
    
    /**
     * Updates this program with the latest files and information using details
     * from the latest Program object
     * @param program program to get updated information from
     * @throws org.codespeak.distribution.client.objects.ClientException if an
     * error occurs while performing a query
     * @throws java.io.IOException error thrown if an error occurs during update
     */
    public void update(Program program) throws IOException, ClientException {
        List<FileInfo> files = BackendHandler.getDataFromQuery(QueryTypes.GET_PROGRAM_FILES, "&id=" + id + "&since_version=" + version);
        Path programDirectory = getDirectory();
        
        for (FileInfo file : files) {
            Path updateFilePath = programDirectory.resolve(file.getPathAndName());
            File updateFile = updateFilePath.toFile();
            
            if (updateFile.exists()) {
                updateFile.delete();
            }
            
            switch (file.getFileStatus()) {
                case NEW:
                case MODIFIED:
                    ReadableByteChannel readableByteChannel = BackendHandler.getRemoteFileChannel(slug, file.getRemotePathAndName());
                    FileChannel outChannel = new FileOutputStream(updateFile).getChannel();
                    
                    outChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                    
                    readableByteChannel.close();
                    outChannel.close();
                    
                    break;
            }
        }

        this.category = program.getCategory();
        this.slug = program.getSlug();
        this.name = program.getName();
        this.description = program.getDescription();
        this.launchFile = program.getLaunchFile();
        this.helpFile = program.getHelpFile();
        this.version = program.getVersion();
        this.releaseTime = program.getReleaseTime();
        this.dependencies = program.getDependencies();
    }

    /**
     * Repairs this program
     * @throws org.codespeak.distribution.client.objects.ClientException if an
     * error occurs while performing a query
     * @throws IOException thrown if an error occurs
     */
    public void repair() throws IOException, ClientException {
        List<FileInfo> files = BackendHandler.getDataFromQuery(QueryTypes.GET_PROGRAM_FILES, "&id=" + id);
        Path programDirectory = getDirectory();
        
        for (FileInfo file : files) {
            Path currentFilePath = programDirectory.resolve(file.getPathAndName());
            File currentFilePathFile = currentFilePath.toFile();
            File currentFolderPath = currentFilePath.getParent().toFile();
            
            boolean canCreateFile = false;
            
            if (currentFilePath.toFile().exists()) {
                String currentChecksum = file.getChecksum();
                String oldChecksum = MiscUtil.getFileChecksum(currentFilePath);
                canCreateFile = !currentChecksum.equals(oldChecksum);
                
                if (canCreateFile) {
                    currentFilePathFile.delete();
                }
            } else {
                canCreateFile = true;
            }
            
            if (canCreateFile) {
                currentFolderPath.mkdirs();
                
                ReadableByteChannel readableByteChannel = BackendHandler.getRemoteFileChannel(slug, file.getRemotePathAndName());
                FileChannel outChannel = new FileOutputStream(currentFilePathFile).getChannel();
                
                outChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                
                readableByteChannel.close();
                outChannel.close();
            }
        }
    }
    
    /**
     * Converts this Program object to JSON
     * @return JSON representation of this Program object
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        
        json.put("id", id);
        json.put("category_id", category.getId());
        json.put("slug", slug);
        json.put("name", name);
        json.put("description", description);
        json.put("launch_file", launchFile);
        json.put("help_file", helpFile);
        json.put("version", version);
        json.put("release_time", releaseTime.toString());
        
        JSONArray jsonDependencies = new JSONArray();
        
        for (Dependency dependency : dependencies) {
            jsonDependencies.put(dependency.getId());
        }
        
        json.put("dependencies", jsonDependencies);
        
        return json;
    }

    @Override
    public int hashCode() {
        return id;
    }
    
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Program)) {
            return false;
        }
        
        Program program = (Program) other;
        
        return this.getId() == program.getId();
    }
    
    /**
     * Creates a Program object from JSON
     * @param json JSON to construct a Program object from
     * @param installed whether the program is installed
     * @return Program object represented from JSON
     */
    public static Program fromJSON(JSONObject json, boolean installed) {
        int id = 0;
        Category category = null;
        String slug = "";
        String name = "";
        String description = "";
        String launchFile = "";
        String helpFile = "";
        String version = "";
        Timestamp releaseTime = null;
        List<Dependency> dependencies = new ArrayList<Dependency>();
        
        if (json.has("id")) {
            id = json.getInt("id");
        }
        
        if (json.has("category_id")) {
            int categoryId = json.getInt("category_id");
            category = DataHandler.getCategory(categoryId, installed);
        }
        
        if (json.has("slug")) {
            slug = json.getString("slug");
        }
        
        if (json.has("name")) {
            name = json.getString("name");
        }
        
        if (json.has("description")) {
            description = json.getString("description");
        }
        
         if (json.has("launch_file")) {
             launchFile = json.getString("launch_file");
         }
         
         if (json.has("help_file")) {
             helpFile = json.getString("help_file");
         }
         
         if (json.has("version")) {
             version = json.getString("version");
         }
         
         if (json.has("release_time")) {
             releaseTime = Timestamp.valueOf(json.getString("release_time"));
         }
         
         if (json.has("dependencies")) {
             JSONArray jsonDependencies = json.getJSONArray("dependencies");
             
             for (int i = 0; i < jsonDependencies.length(); i++) {
                 int dependencyId = jsonDependencies.getInt(i);
                 Dependency dependency = DataHandler.getDependency(dependencyId, installed);
                 dependencies.add(dependency);
             }
         }
         
         return new Program(id, category, slug, name, description, launchFile, helpFile, version, releaseTime, dependencies, installed);
    }
    
}