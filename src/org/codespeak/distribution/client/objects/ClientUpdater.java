package org.codespeak.distribution.client.objects;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import org.codespeak.distribution.client.Configuration;
import org.codespeak.distribution.client.Main;
import org.codespeak.distribution.client.data.ChangelogEntry;

/**
 * A class representing data and methods for a client update
 *
 * @author Vector
 */
public class ClientUpdater extends Updater {

    public ClientUpdater(String previousVersion, String currentVersion, List<ChangelogEntry> entries) {
        super(Configuration.PROGRAM_NAME, previousVersion, currentVersion, entries);
    }
    
    @Override
    public void update() throws Exception {
        String previousVersion = super.getPreviousVersion();
        
        Path updaterPath = Paths.get(".").resolve(Configuration.UPDATER_FILE).toAbsolutePath();
        List<String> commands = new ArrayList<String>();
        File applicationFolder = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toPath().getParent().toFile();

        if (Configuration.UPDATER_FILE.endsWith(".jar")) {
            commands.add("java");
            commands.add("-jar");
        }

        commands.add(updaterPath.toString());
        commands.add("--old-version");
        commands.add(previousVersion);
        commands.add("--client-folder");
        commands.add(applicationFolder.toString());

        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.directory(new File("."));
        pb.start();

        Platform.exit();
    }

}
