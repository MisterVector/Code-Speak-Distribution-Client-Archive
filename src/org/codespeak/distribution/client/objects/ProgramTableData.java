package org.codespeak.distribution.client.objects;

import javafx.beans.property.SimpleStringProperty;

/**
 * A class representing program information in a table
 *
 * @author Vector
 */
public class ProgramTableData {
   
    private int id;
    private SimpleStringProperty name;
    private SimpleStringProperty version;
    private SimpleStringProperty releaseTime;
    
    public ProgramTableData(int id, String name, String version, String releaseTime) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.version = new SimpleStringProperty(version);
        this.releaseTime = new SimpleStringProperty(releaseTime);
    }
    
    /**
     * Gets the ID of this program
     * @return ID of this program
     */
    public int getId() {
        return id;
    }
    
    /**
     * Gets the name of this program
     * @return name of this program
     */
    public String getName() {
        return name.get();
    }
    
    /**
     * Sets the name of this program
     * @param name name of this program
     */
    public void setName(String name) {
        this.name = new SimpleStringProperty(name);
    }
    
    /**
     * Gets the version of this program
     * @return version of this program
     */
    public String getVersion() {
        return version.get();
    }
    
    /**
     * Sets the version of this program
     * @param version version of this program
     */
    public void setVersion(String version) {
        this.version = new SimpleStringProperty(version);
    }
    
    /**
     * Gets the release time of this program
     * @return release time of this program
     */
    public String getReleaseTime() {
        return releaseTime.get();
    }
    
    /**
     * Sets the release time of this program
     * @param releaseTime release time of this program
     */
    public void setReleaseTime(String releaseTime) {
        this.releaseTime = new SimpleStringProperty(releaseTime);
    }
    
}
