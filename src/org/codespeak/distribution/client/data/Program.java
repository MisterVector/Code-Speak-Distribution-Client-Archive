package org.codespeak.distribution.client.data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.codespeak.distribution.client.data.handler.CategoryHandler;
import org.codespeak.distribution.client.data.handler.DependencyHandler;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A class representing a program
 *
 * @author Vector
 */
public class Program {

    private final int id;
    private final Category category;
    private final String slug;
    private final String name;
    private final String description;
    private final String sourceURL;
    private final String launchFile;
    private final String helpFile;
    private final String version;
    private final Timestamp releaseTime;
    private final List<Dependency> dependencies;
    
    protected Program(int id, Category category, String slug, String name, String description,
                    String sourceURL, String launchFile, String helpFile, String version,
                    Timestamp releaseTime, List<Dependency> dependencies) {
        this.id = id;
        this.category = category;
        this.slug = slug;
        this.name = name;
        this.description = description;
        this.sourceURL = sourceURL;
        this.launchFile = launchFile;
        this.helpFile = helpFile;
        this.version = version;
        this.releaseTime = releaseTime;
        this.dependencies = dependencies;
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
     * Gets the source URL of this program
     * @return source URL of this program
     */
    public String getSourceURL() {
        return sourceURL;
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
        return dependencies;
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
        json.put("source_url", sourceURL);
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
        String sourceURL = "";
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
            category = CategoryHandler.getCategory(categoryId, installed);
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
        
         if (json.has("source_url")) {
             sourceURL = json.getString("source_url");
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
                 Dependency dependency = DependencyHandler.getDependency(dependencyId, installed);
                 dependencies.add(dependency);
             }
         }
         
         return new Program(id, category, slug, name, description, sourceURL, launchFile, helpFile, version, releaseTime, dependencies);
    }
    
}