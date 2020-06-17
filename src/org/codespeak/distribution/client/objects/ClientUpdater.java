package org.codespeak.distribution.client.objects;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import org.codespeak.distribution.client.Configuration;
import org.codespeak.distribution.client.DistributionClient;
import org.codespeak.distribution.client.data.ChangelogEntry;

/**
 * A class representing data and methods for a client update
 *
 * @author Vector
 */
public class ClientUpdater extends Updater {

    private final String previousVersion;
    private final String currentVersion;

    public ClientUpdater(String previousVersion, String currentVersion, List<ChangelogEntry> entries) {
        super(entries);
        
        this.previousVersion = previousVersion;
        this.currentVersion = currentVersion;
    }
    
    @Override
    public String getName() {
        return Configuration.PROGRAM_NAME;
    }
    
    @Override
    public String getPreviousVersion() {
        return previousVersion;
    }
    
    @Override
    public String getCurrentVersion() {
        return currentVersion;
    }
    
    @Override
    public void update() throws Exception {
        Path updaterPath = Paths.get(".").resolve(Configuration.UPDATER_FILE).toAbsolutePath();
        List<String> commands = new ArrayList<String>();
        File applicationFile = new File(DistributionClient.class.getProtectionDomain().getCodeSource().getLocation().toURI());

        if (Configuration.UPDATER_FILE.endsWith(".jar")) {
            commands.add("java");
            commands.add("-jar");
        }

        commands.add(updaterPath.toString());
        commands.add("--version");
        commands.add(previousVersion);
        commands.add("--new-version");
        commands.add(currentVersion);
        commands.add("--launch-file");
        commands.add(applicationFile.toString());

        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.directory(new File("."));
        pb.start();

        Platform.exit();
    }

}
