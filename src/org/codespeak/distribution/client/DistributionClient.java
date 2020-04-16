package org.codespeak.distribution.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.codespeak.distribution.client.data.DataManager;
import org.codespeak.distribution.client.scenes.SceneTypes;
import org.codespeak.distribution.client.util.SceneUtil;
import org.json.JSONObject;

/**
 *
 * @author Vector
 */
public class DistributionClient extends Application {
    
    private static DistributionClient instance;
    
    public DistributionClient() {
        instance = this;
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        stage = SceneUtil.getScene(SceneTypes.MAIN, Configuration.PROGRAM_NAME).getStage();
        stage.show();
    }

    @Override
    public void stop() throws FileNotFoundException {
        JSONObject json = DataManager.exportInstalledProgramsToJSON();
        File storedProgramsFile = new File(Configuration.STORED_PROGRAMS_FILE);
        
        if (storedProgramsFile.exists()) {
            storedProgramsFile.delete();
        }
        
        PrintWriter writer = new PrintWriter(storedProgramsFile);
        writer.write(json.toString(4));
        writer.close();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File programsFolder = new File(Configuration.PROGRAMS_FOLDER);
        
        if (!programsFolder.exists()) {
            programsFolder.mkdir();
        }
        
        launch(args);
    }
    
    /**
     * Gets an instance of this class
     * @return instance of this class
     */
    public static DistributionClient getInstance() {
        return instance;
    }
    
}
