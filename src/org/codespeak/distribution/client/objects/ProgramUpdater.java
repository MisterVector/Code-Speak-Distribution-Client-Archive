package org.codespeak.distribution.client.objects;

import java.util.List;
import org.codespeak.distribution.client.data.ChangelogEntry;
import org.codespeak.distribution.client.data.Program;
import org.codespeak.distribution.client.scenes.MainSceneController;

/**
 * A class representing data and methods for a program update
 *
 * @author Vector
 */
public class ProgramUpdater extends Updater {
   
    private final Program program;
    private final Program installedProgram;
    private final MainSceneController controller;
    
    public ProgramUpdater(Program program, Program installedProgram, MainSceneController controller, List<ChangelogEntry> entries) {
        super(entries);
        
        this.program = program;
        this.installedProgram = installedProgram;
        this.controller = controller;
    }
    
    @Override
    public String getName() {
        return program.getName();
    }
    
    @Override
    public String getCurrentVersion() {
        return program.getVersion();
    }
    
    @Override
    public String getPreviousVersion() {
        return installedProgram.getVersion();
    }
    
    @Override
    public void update() throws Exception {
        controller.onUpdateProgram(program, installedProgram);
    }
    
}
