package org.codespeak.distribution.client.data.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.codespeak.distribution.client.data.Dependency;

/**
 * A class that holds dependencies, whether installed or not
 *
 * @author Vector
 */
public class DependencyHandler {

    private static List<Dependency> dependencies = new ArrayList<Dependency>();
    private static List<Dependency> installedDependencies = new ArrayList<Dependency>();
    
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
    
}
