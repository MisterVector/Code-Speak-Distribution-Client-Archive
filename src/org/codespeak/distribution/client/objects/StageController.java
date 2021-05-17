package org.codespeak.distribution.client.objects;

import javafx.stage.Stage;

/**
 * A class which contains both a stage and a controller object
 *
 * @author Vector
 */
public class StageController<T> {
    
    private final Stage stage;
    private final T controller;
    
    public StageController(Stage stage, T controller) {
        this.stage = stage;
        this.controller = controller;
    }
    
    /**
     * Gets the stage
     * @return stage to be returned
     */
    public Stage getStage() {
        return stage;
    }
    
    /**
     * Gets the controller
     * @return controller to be returned
     */
    public T getController() {
        return controller;
    }
    
}
