package org.codespeak.distribution.client;

import java.io.File;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.codespeak.distribution.client.scenes.SceneTypes;
import org.codespeak.distribution.client.util.SceneUtil;

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
