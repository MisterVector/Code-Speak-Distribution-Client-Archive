package org.codespeak.distribution.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.codespeak.distribution.client.handler.DataHandler;
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
        stage = SceneUtil.getScene(SceneTypes.MAIN, Configuration.PROGRAM_TITLE).getStage();
        stage.show();
    }

    @Override
    public void stop() throws FileNotFoundException {
        JSONObject json = DataHandler.exportDataToJSON();
        File dataFile = new File(Configuration.DATA_FILE);
        
        if (dataFile.exists()) {
            dataFile.delete();
        }
        
        PrintWriter writer = new PrintWriter(dataFile);
        writer.write(json.toString(4));
        writer.close();
        
        Configuration.writeSettingsToFile();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        File programsFolder = new File(Configuration.PROGRAMS_FOLDER);
        File dataFile = new File(Configuration.DATA_FILE);
        
        if (!programsFolder.exists()) {
            programsFolder.mkdir();
        }
        
        if (dataFile.exists()) {
            byte[] bytes = Files.readAllBytes(dataFile.toPath());
            String jsonString = new String(bytes);
            JSONObject json = new JSONObject(jsonString);
            
            DataHandler.importDataFromJSON(json);
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
