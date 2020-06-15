package org.codespeak.distribution.client.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.codespeak.distribution.client.data.Category;
import org.codespeak.distribution.client.data.Dependency;
import org.codespeak.distribution.client.data.Program;
import org.codespeak.distribution.client.objects.ClientException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A class that manages all categories, dependencies and programs
 *
 * @author Vector
 */
public class DataHandler {
    
    private static List<Category> categories = new ArrayList<Category>();
    private static List<Dependency> dependencies = new ArrayList<Dependency>();
    private static List<Program> programs = new ArrayList<Program>();
    
    private static List<Category> installedCategories = new ArrayList<Category>();
    private static List<Dependency> installedDependencies = new ArrayList<Dependency>();
    private static List<Program> installedPrograms = new ArrayList<Program>();

    private static Map<String, String> mappedData = new HashMap<String, String>();
    
    /**
     * Adds a category to the list
     * @param category category to add
     * @param installed whether the category is installed
     */
    public static void addCategory(Category category, boolean installed) {
        if (installed) {
            installedCategories.add(category);
        } else {
            categories.add(category);
        }
    }
    
    /**
     * Gets a category by its ID
     * @param id ID of category
     * @param installed whether the category is installed
     * @return Category object represented by the ID
     */
    public static Category getCategory(int id, boolean installed) {
        List<Category> cats = (installed ? installedCategories : categories);
        
        for (Category category : cats) {
            if (category.getId() == id) {
                return category;
            }
        }
        
        return null;
    }
    
    /**
     * Removes a category by its ID
     * @param id ID of category
     * @param installed whether the category is installed
     * @return Category object from deleted category
     */
    public static Category removeCategory(int id, boolean installed) {
        List<Category> cats = (installed ? installedCategories : categories);
        
        for (Iterator<Category> it = cats.iterator(); it.hasNext();) {
            Category category = it.next();
            
            if (category.getId() == id) {
                it.remove();
                
                return category;
            }
        }
        
        return null;
    }
    
    /**
     * Gets an unmodifiable list of all categories. The installed categories
     * will be used if the current list of categories is empty
     * @return unmodifiable list of all categories
     */
    public static List<Category> getCategories() {
        return (categories.size() > 0 ? Collections.unmodifiableList(categories) : Collections.unmodifiableList(installedCategories));
    }

    /**
     * Adds a dependency to the list
     * @param dependency the dependency to add
     * @param installed whether the dependency is installed
     */
    public static void addDependency(Dependency dependency, boolean installed) {
        if (installed) {
            installedDependencies.add(dependency);
        } else {
            dependencies.add(dependency);            
        }
    }
    
    /**
     * Gets a dependency by its ID
     * @param id ID of dependency
     * @param installed whether the dependency is installed
     * @return dependency object representing the ID
     */
    public static Dependency getDependency(int id, boolean installed) {
        List<Dependency> deps = (installed ? installedDependencies : dependencies);
        
        for (Dependency dependency : deps) {
            if (dependency.getId() == id) {
                return dependency;
            }
        }
        
        return null;
    }

    /**
     * Removes a dependency by its ID
     * @param id ID of dependency
     * @param installed whether the dependency is installed
     * @return Dependency object from deleted dependency
     */
    public static Dependency removeDependency(int id, boolean installed) {
        List<Dependency> deps = (installed ? installedDependencies : dependencies);
        
        for (Iterator<Dependency> it = deps.iterator(); it.hasNext();) {
            Dependency dependency = it.next();
            
            if (dependency.getId() == id) {
                it.remove();
                
                return dependency;
            }
        }
        
        return null;
    }
    
    /**
     * Gets an unmodifiable list of all dependencies
     * @param installed whether the dependencies are installed
     * @return unmodifiable list of all dependencies
     */
    public static List<Dependency> getDependencies(boolean installed) {
        return (installed ? Collections.unmodifiableList(installedDependencies) : Collections.unmodifiableList(dependencies));
    }
    
    /**
     * Adds a program to the list of programs
     * @param program program to add
     * @param installed whether the program is installed
     */
    public static void addProgram(Program program, boolean installed) {
        if (installed) {
            installedPrograms.add(program);
        } else {
            programs.add(program);            
        }
    }
    
    /**
     * Gets a program by its ID
     * @param id ID of program
     * @param installed whether the program is installed
     * @return 
     */
    public static Program getProgram(int id, boolean installed) {
        List<Program> progs = (installed ? installedPrograms : programs);
        
        for (Program program : progs) {
            if (program.getId() == id) {
                return program;
            }
        }
        
        return null;
    }
    
    /**
     * Deletes a program by its ID
     * @param id ID of program
     * @param installed whether the program is installed
     * @return Program object from deleted program
     */
    public static Program deleteProgram(int id, boolean installed) {
        List<Program> progs = (installed ? installedPrograms : programs);
        
        for (Iterator<Program> it = progs.iterator(); it.hasNext();) {
            Program program = it.next();
            
            if (program.getId() == id) {
                it.remove();
                
                return program;
            }
        }
        
        return null;
    }

    /**
     * Installs a new program
     * @param program the program to install
     * @throws java.io.IOException error if unable to install program
     * @throws org.codespeak.distribution.client.objects.ClientException if
     * there is an error doing a query
     */
    public static void installProgram(Program program) throws IOException, ClientException {
        program.install();
        
        Category category = program.getCategory();
        
        if (!installedCategories.contains(category)) {
            installedCategories.add(category);
        }
        
        for (Dependency dependency : program.getDependencies()) {
            if (!installedDependencies.contains(dependency)) {
                installedDependencies.add(dependency);
            }
        }
        
        installedPrograms.add(program);
    }

    /**
     * Uninstalls the specified program
     * @param program program to uninstall
     * @throws java.io.IOException
     */
    public static void uninstallProgram(Program program) throws IOException {
        program.uninstall();

        installedPrograms.remove(program);
        
        Category category = program.getCategory();
        List<Dependency> deps = program.getDependencies();
        
        for (Program checkProgram : installedPrograms) {
            Category checkCategory = checkProgram.getCategory();
            List<Dependency> checkDependencies = checkProgram.getDependencies();
            
            if (checkCategory.equals(category)) {
                category = null;
            }
            
            for (Dependency checkDependency : checkDependencies) {
                if (deps.contains(checkDependency)) {
                    deps.remove(checkDependency);
                }
            }
        }
        
        if (category != null) {
            installedCategories.remove(category);
        }
        
        for (Dependency dependency : deps) {
            installedDependencies.remove(dependency);
        }
    }
    
    /**
     * Gets an unmodifiable list of all programs according to the category, or
     * all programs if the category is null. It starts by building a list of all
     * installed programs, and then includes the latest programs if not yet
     * added to the list
     * @param category the category to return, or null for all categories
     * @return unmodifiable list of all programs
     */
    public static List<Program> getPrograms(Category category) {
        List<Program> ret = new ArrayList<Program>();
        
        for (Program program : installedPrograms) {
            Category currentCategory = program.getCategory();

            if (category != null && !category.equals(currentCategory)) {
                continue;
            }
            
            ret.add(program);
        }

        for (Program program : programs) {
            Category currentCategory = program.getCategory();

            if (category != null && !category.equals(currentCategory)) {
                continue;
            }

            if (!ret.contains(program)) {
                ret.add(program);
            }
        }

        return Collections.unmodifiableList(ret);
    }

    /**
     * Gets an unmodifiable list of all programs
     * @param installed whether the programs are installed
     * @return unmodifiable list of all programs
     */
    public static List<Program> getPrograms(boolean installed) {
        return (installed ? Collections.unmodifiableList(installedPrograms) : Collections.unmodifiableList(programs));
    }
    
    /**
     * Gets a list of programs by a specific category
     * @param category the category to search for, or null for all programs
     * @param installed whether the programs are installed
     * @return a list of programs by category
     */
    public static List<Program> getProgramsByCategory(Category category, boolean installed) {
        List<Program> progs = (installed ? installedPrograms : programs);
        List<Program> ret = new ArrayList<Program>();
        
        for (Program program : progs) {
            if (category == null || category == program.getCategory()) {
                ret.add(program);
            }
        }
        
        return ret;
    }
    
    /**
     * Assigns a new mapped data key with its value
     * @param key the key to the mapped data
     * @param value value to the mapped data
     */
    public static void setMappedData(String key, String value) {
        mappedData.put(key, value);
    }
    
    /**
     * Gets a mapped data value by its key
     * @param key key to the mapped data
     * @return mapped data value from a key
     */
    public static String getMappedData(String key) {
        return mappedData.get(key);
    }
    
    /**
     * Checks if there is mapped data by the specified key
     * @param key
     * @return 
     */
    public static boolean hasMappedData(String key) {
        return mappedData.containsKey(key);
    }
    
    /**
     * Exports all data to JSON. This includes all installed programs,
     * their categories and dependencies
     * @return JSON representation of various data
     */
    public static JSONObject exportDataToJSON() {
        JSONObject json = new JSONObject();
        JSONArray jsonCategories = new JSONArray();
        JSONArray jsonDependencies = new JSONArray();
        JSONArray jsonInstalledPrograms = new JSONArray();
        JSONObject jsonMappedData = new JSONObject();
        
        for (Category category : installedCategories) {
            jsonCategories.put(category.toJSON());
        }
        
        for (Dependency dependency : installedDependencies) {
            jsonDependencies.put(dependency.toJSON());
        }
        
        for (Program program : installedPrograms) {
            jsonInstalledPrograms.put(program.toJSON());
        }
        
        for (String key : mappedData.keySet()) {
            String value = mappedData.get(key);
            
            jsonMappedData.put(key, value);
        }
        
        json.put("categories", jsonCategories);
        json.put("dependencies", jsonDependencies);
        json.put("programs", jsonInstalledPrograms);
        json.put("mapped_data", jsonMappedData);
        
        return json;
    }
    
    /**
     * Imports data from JSON. This includes all installed programs, their
     * categories and dependencies
     * @param json JSON representing data to import
     */
    public static void importDataFromJSON(JSONObject json) {
        if (json.has("categories")) {
            JSONArray jsonCategories = json.getJSONArray("categories");
            
            for (int i = 0; i < jsonCategories.length(); i++) {
                JSONObject obj = jsonCategories.getJSONObject(i);
                Category category = Category.fromJSON(obj);
                installedCategories.add(category);
            }
        }
        
        if (json.has("dependencies")) {
            JSONArray jsonDependencies = json.getJSONArray("dependencies");
            
            for (int i = 0; i < jsonDependencies.length(); i++) {
                JSONObject obj = jsonDependencies.getJSONObject(i);
                Dependency dependency = Dependency.fromJSON(obj);
                installedDependencies.add(dependency);
            }
        }
        
        if (json.has("programs")) {
            JSONArray jsonPrograms = json.getJSONArray("programs");
            
            for (int i = 0; i < jsonPrograms.length(); i++) {
                JSONObject obj = jsonPrograms.getJSONObject(i);
                Program program = Program.fromJSON(obj, true);
                installedPrograms.add(program);
            }
        }
        
        if (json.has("mapped_data")) {
            JSONObject jsonMappedData = json.getJSONObject("mapped_data");
            
            for (String key : jsonMappedData.keySet()) {
                String value = jsonMappedData.getString(key);
                
                mappedData.put(key, value);
            }
        }
    }
}
    
