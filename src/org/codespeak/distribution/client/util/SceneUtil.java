package org.codespeak.distribution.client.util;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.codespeak.distribution.client.Configuration;
import org.codespeak.distribution.client.DistributionClient;
import org.codespeak.distribution.client.objects.StageController;
import org.codespeak.distribution.client.scenes.SceneTypes;

/**
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
         return getScene(new Stage(), sceneType, title);
    }
    
    /**
     * Opens a scene with an existing stage
     * @param existingStage existing stage
     * @param sceneType the type of scene to open
     * @return StageController both stage and controller
     * @throws IOException IOException in case of failure
     */
    public static StageController getScene(Stage existingStage, SceneTypes sceneType) throws IOException {
        return getScene(existingStage, sceneType, Configuration.PROGRAM_NAME);
    }
    
    /**
     * Opens a scene with an existing stage
     * @param existingStage existing stage
     * @param sceneType the type of scene to open
     * @param title the title of the scene
     * @return StageController both stage and controller
     * @throws IOException IOException in case of failure
     */
    public static StageController getScene(Stage existingStage, SceneTypes sceneType, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(DistributionClient.getInstance().getClass().getResource(sceneType.getPath()));
        
        Parent parent = (Parent) loader.load();
        Scene scene = new Scene(parent);
        
        existingStage.setResizable(false);
        existingStage.setScene(scene);
        existingStage.setTitle(title);
        
        return new StageController(existingStage, loader.getController());
    }

}
