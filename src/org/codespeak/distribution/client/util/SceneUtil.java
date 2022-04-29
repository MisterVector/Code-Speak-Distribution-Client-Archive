package org.codespeak.distribution.client.util;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.codespeak.distribution.client.Configuration;
import org.codespeak.distribution.client.Main;
import org.codespeak.distribution.client.objects.StageController;
import org.codespeak.distribution.client.scenes.SceneTypes;

/**
 * A utility class with methods for getting a particular scene
 *
 * @author Vector
 */
public class SceneUtil {

    /**
     * Gets a scene with a new stage
     * @param sceneType the scene to get
     * @return StageController both stage and controller
     * @throws IOException IOException in case of failure
     */
    public static StageController getScene(SceneTypes sceneType) throws IOException {
        return getScene(sceneType, Configuration.PROGRAM_NAME);
    }

    /**
     * Gets a scene with a new stage
     * @param sceneType the scene to get
     * @param title the title of the scene
     * @return StageController both stage and controller
     * @throws IOException IOException in case of failure
     */
    public static StageController getScene(SceneTypes sceneType, String title) throws IOException {
         return getScene(new Stage(), sceneType, title, true);
    }
    
    /**
     * Opens a scene with an existing stage
     * @param existingStage existing stage
     * @param sceneType the type of scene to open
     * @return StageController both stage and controller
     * @throws IOException IOException in case of failure
     */
    public static StageController getScene(Stage existingStage, SceneTypes sceneType) throws IOException {
        return getScene(existingStage, sceneType, Configuration.PROGRAM_NAME, false);
    }
    
    /**
     * Prepares a scene for either a new stage or an existing stage
     * @param stage the stage to be used
     * @param sceneType the type of scene to open
     * @param title the title of the scene
     * @param newStage determines if the stage is a new stage
     * @return StageController both stage and controller
     * @throws IOException IOException in case of failure
     */
    public static StageController getScene(Stage stage, SceneTypes sceneType, String title, boolean newStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource(sceneType.getPath()));
        
        Parent parent = (Parent) loader.load();
        Scene scene = new Scene(parent);
        
        if (newStage) {
            stage.initModality(Modality.APPLICATION_MODAL);
        }
        
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle(title);
        
        return new StageController(stage, loader.getController());
    }

}
