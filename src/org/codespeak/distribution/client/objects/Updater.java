package org.codespeak.distribution.client.objects;

import java.util.List;
import org.codespeak.distribution.client.data.ChangelogEntry;

/**
 * An abstract class representing data and methods for an updater
 *
 * @author Vector
 */
public abstract class Updater {
    
    private final String name;
    private final String previousVersion;
    private final String currentVersion;
    private final List<ChangelogEntry> entries;
    
    public Updater(String name, String previousVersion, String currentVersion, List<ChangelogEntry> entries) {
        this.name = name;
        this.previousVersion = previousVersion;
        this.currentVersion = currentVersion;
        this.entries = entries;
    }
    
    /**
     * Gets the name represented by this updater
     * @return name represented by this updater
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the previous version represented by this updater
     * @return previous version represented by this updater
     */
    public String getPreviousVersion() {
        return previousVersion;
    }
    

    /**
     * Gets the current version represented by this updater
     * @return current version represented by this updater
     */
    public String getCurrentVersion() {
        return currentVersion;
    }
    
    /**
     * Gets a list of changelog entries
     * @return list of changelog entries
     */
    public List<ChangelogEntry> getEntries() {
        return entries;
    }

    /**
     * Calls the update routine
     * @throws java.lang.Exception IF an error occurs during the update
     */
    public abstract void update() throws Exception;
}
