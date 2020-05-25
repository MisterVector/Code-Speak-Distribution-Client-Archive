package org.codespeak.distribution.client.objects;

import java.util.List;
import org.codespeak.distribution.client.data.ChangelogEntry;

/**
 * An abstract class representing data and methods for an updater
 *
 * @author Vector
 */
public abstract class Updater {
    
    private List<ChangelogEntry> entries;
    
    public Updater(List<ChangelogEntry> entries) {
        this.entries = entries;
    }
    
    /**
     * Gets a list of changelog entries
     * @return list of changelog entries
     */
    public List<ChangelogEntry> getEntries() {
        return entries;
    }
    
    /**
     * Gets the name of the item being updated
     * @return name of the item being updated
     */
    public abstract String getName();
    
    /**
     * Gets the previous version represented by this updater
     * @return previous version represented by this updater
     */
    public abstract String getPreviousVersion();

    /**
     * Gets the current version represented by this updater
     * @return current version represented by this updater
     */
    public abstract String getCurrentVersion();
    
    /**
     * Calls the update routine
     * @throws java.lang.Exception IF an error occurs during the update
     */
    public abstract void update() throws Exception;
}
