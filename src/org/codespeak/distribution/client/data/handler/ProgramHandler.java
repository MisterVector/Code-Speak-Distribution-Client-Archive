package org.codespeak.distribution.client.data.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.codespeak.distribution.client.data.Program;

/**
 * A class representing a list of programs
 *
 * @author Vector
 */
public class ProgramHandler {
    
    private static List<Program> programs = new ArrayList<Program>();
    
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
    
}
