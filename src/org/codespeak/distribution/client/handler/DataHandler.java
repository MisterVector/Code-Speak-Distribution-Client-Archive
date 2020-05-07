package org.codespeak.distribution.client.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.codespeak.distribution.client.data.Category;
import org.codespeak.distribution.client.data.Dependency;
import org.codespeak.distribution.client.data.Program;
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
     * Gets an unmodifiable list of all categories
     * @param installed whether the categories are installed
     * @return unmodifiable list of all categories
     */
    public static List<Category> getCategories(boolean installed) {
        return (installed ? Collections.unmodifiableList(installedCategories) : Collections.unmodifiableList(categories));
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
     */
    public static void installProgram(Program program) throws IOException {
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
     * all programs if the category is null. It will use the installed version
     * of the program if available
     * @param category the category to return, or null for all categories
     * @return unmodifiable list of all programs
     */
    public static List<Program> getPrograms(Category category) {
        List<Program> ret = new ArrayList<Program>();
        
        for (Program program : programs) {
            Category currentCategory = program.getCategory();
            
            if (category != null && category != currentCategory) {
                continue;
            }
            
            if (installedPrograms.contains(program)) {
                Program installedProgram = getProgram(program.getId(), true);
                ret.add(installedProgram);
            } else {
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
     * Exports all data to JSON. This includes all installed programs,
     * their categories and dependencies
     * @return JSON representation of various data
     */
    public static JSONObject exportDataToJSON() {
        JSONObject json = new JSONObject();
        JSONArray jsonCategories = new JSONArray();
        JSONArray jsonDependencies = new JSONArray();
        JSONArray jsonInstalledPrograms = new JSONArray();
        
        for (Category category : installedCategories) {
            jsonCategories.put(category.toJSON());
        }
        
        for (Dependency dependency : installedDependencies) {
            jsonDependencies.put(dependency.toJSON());
        }
        
        for (Program program : installedPrograms) {
            jsonInstalledPrograms.put(program.toJSON());
        }
        
        json.put("categories", jsonCategories);
        json.put("dependencies", jsonDependencies);
        json.put("programs", jsonInstalledPrograms);
        
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
    }
    
}
