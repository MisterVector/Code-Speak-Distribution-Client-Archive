package org.codespeak.distribution.client.data;

import org.json.JSONObject;

/**
 * A class representing a category
 *
 * @author Vector
 */
public class Category {
    
    private final int id;
    private final String slug;
    private final String name;
    private final String description;
    
    private Category(int id, String slug, String name, String description) {
        this.id = id;
        this.slug = slug;
        this.name = name;
        this.description = description;
    }
    
    /**
     * Gets the ID of this category
     * @return ID of this category
     */
    public int getId() {
        return id;
    }
    
    /**
     * Gets the slug of this category
     * @return slug of this category
     */
    public String getSlug() {
        return slug;
    }
    
    /**
     * Gets the name of this category
     * @return name of this category
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the description of this category
     * @return description of this category
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Converts this category object to JSON
     * @return JSON representation of this Category object
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        
        json.put("id", id);
        json.put("slug", slug);
        json.put("name", name);
        json.put("description", description);
        
        return json;
    }

    @Override
    public int hashCode() {
        return id;
    }
    
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Category)) {
            return false;
        }
        
        Category category = (Category) other;
        
        return this.getId() == category.getId();
    }
    
    /**
     * Creates a category object from JSON
     * @param json JSON object containing data for a Category object
     * @return Category object from JSON data
     */
    public static Category fromJSON(JSONObject json) {
        int id = 0;
        String slug = "";
        String name = "";
        String description = "";
        
        if (json.has("id")) {
            id = json.getInt("id");
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
        
        return new Category(id, slug, name, description);
    }
    
}
