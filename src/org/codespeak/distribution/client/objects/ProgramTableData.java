package org.codespeak.distribution.client.objects;

import java.time.Instant;
import javafx.beans.property.SimpleStringProperty;
import org.codespeak.distribution.client.data.Program;
import org.codespeak.distribution.client.util.DateUtil;

/**
 * A class representing program information in a table
 *
 * @author Vector
 */
public class ProgramTableData {
   
    private Program program;
    private SimpleStringProperty name;
    private SimpleStringProperty version;
    private SimpleStringProperty releaseDate;
    
    public ProgramTableData(Program program, String name, String version, String releaseDate) {
        this.program = program;
        this.name = new SimpleStringProperty(name);
        this.version = new SimpleStringProperty(version);
        this.releaseDate = new SimpleStringProperty(releaseDate);
    }
    
    /**
     * Gets the program represented by this table item
     * @return program represented by this table item
     */
    public Program getProgram() {
        return program;
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
        this.name.set(name);
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
        this.version.set(version);
    }
    
    /**
     * Gets the release date of this program
     * @return release date of this program
     */
    public String getReleaseDate() {
        return releaseDate.get();
    }
    
    /**
     * Sets the release time of this program
     * @param releaseTime release time of this program
     */
    public void setReleaseTime(Instant releaseTime) {
        String formattedReleaseDate = DateUtil.formatInstant(releaseTime);
            
        this.releaseDate.set(formattedReleaseDate);
    }
    
}
