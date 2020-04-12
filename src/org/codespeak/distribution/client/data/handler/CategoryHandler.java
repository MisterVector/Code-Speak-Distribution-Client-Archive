package org.codespeak.distribution.client.data.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.codespeak.distribution.client.data.Category;
import org.codespeak.distribution.client.data.Dependency;

/**
 * A class that holds categories, whether installed or not
 *
 * @author Vector
 */
public class CategoryHandler {
    
    private static List<Category> categories = new ArrayList<Category>();
    private static List<Category> installedCategories = new ArrayList<Category>();
    
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
    
}
