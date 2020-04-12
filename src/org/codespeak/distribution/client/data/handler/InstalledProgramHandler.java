package org.codespeak.distribution.client.data.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.codespeak.distribution.client.data.InstalledProgram;

/**
 * A class representing a list of programs
 *
 * @author Vector
 */
public class InstalledProgramHandler {
    
    private static List<InstalledProgram> installedPrograms = new ArrayList<InstalledProgram>();
    
    /**
     * Adds a program to the list of programs
     * @param program program to add
     */
    public static void addProgram(InstalledProgram program) {
        installedPrograms.add(program);
    }
    
    /**
     * Gets a program by its ID
     * @param id ID of program
     * @return 
     */
    public static InstalledProgram getProgram(int id) {
        for (InstalledProgram program : installedPrograms) {
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
    public static InstalledProgram deleteProgram(int id) {
        for (Iterator<InstalledProgram> it = installedPrograms.iterator(); it.hasNext();) {
            InstalledProgram program = it.next();
            
            if (program.getId() == id) {
                it.remove();
                
                return program;
            }
        }
        
        return null;
    }
    
}
