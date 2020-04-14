package org.codespeak.distribution.client.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A class that manages all categories, dependencies and programs
 *
 * @author Vector
 */
public class DataManager {
    
    private static List<Category> categories = new ArrayList<Category>();
    private static List<Dependency> dependencies = new ArrayList<Dependency>();
    private static List<Program> programs = new ArrayList<Program>();
    
    private static List<Category> installedCategories = new ArrayList<Category>();
    private static List<Dependency> installedDependencies = new ArrayList<Dependency>();
    private static List<InstalledProgram> installedPrograms = new ArrayList<InstalledProgram>();
    
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
     */
    public static void addProgram(Program program) {
        programs.add(program);
    }
    
    /**
     * Gets a program by its ID
     * @param id ID of program
     * @return 
     */
    public static Program getProgram(int id) {
        for (Program program : programs) {
            if (program.getId() == id) {
                return program;
            }
        }
        
        return null;
    }
    
    /**
     * Deletes a program by its ID
     * @param id ID of program
     * @return Program object from deleted program
     */
    public static Program deleteProgram(int id) {
        for (Iterator<Program> it = programs.iterator(); it.hasNext();) {
            Program program = it.next();
            
            if (program.getId() == id) {
                it.remove();
                
                return program;
            }
        }
        
        return null;
    }

    /**
     * Adds an installed program to the list of programs
     * @param program installed program to add
     */
    public static void addInstalledProgram(InstalledProgram program) {
        installedPrograms.add(program);
    }
    
    /**
     * Gets an installed program by its ID
     * @param id ID of installed program
     * @return installed program
     */
    public static InstalledProgram getInstalledProgram(int id) {
        for (InstalledProgram program : installedPrograms) {
            if (program.getId() == id) {
                return program;
            }
        }
        
        return null;
    }
    
    /**
     * Deletes an installed program by its ID
     * @param id ID of program
     * @return Program object from deleted program
     */
    public static InstalledProgram deleteInstalledProgram(int id) {
        for (Iterator<InstalledProgram> it = installedPrograms.iterator(); it.hasNext();) {
            InstalledProgram program = it.next();
            
            if (program.getId() == id) {
                it.remove();
                
                return program;
            }
        }
        
        return null;
    }

    /**
     * Exports all installed programs to JSON
     * @return JSON representation of all programs
     */
    public static JSONObject exportInstalledProgramsToJSON() {
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
     * Installs all programs from the specified JSON
     * @param json JSON representing all installed programs
     */
    public static void importInstalledProgramsFromJSON(JSONObject json) {
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
                InstalledProgram program = InstalledProgram.fromJSON(json);
                installedPrograms.add(program);
            }
        }
    }
    
}
