package org.codespeak.distribution.client.scenes;

/**
 * An enum containing a list of scenes
 *
 * @author Vector
 */
public enum SceneTypes {
    
    MAIN("MainWindow.fxml");
    
    private final String path;
    
    private SceneTypes(String path) {
        this.path = path;
    }
    
    public String getPath() {
        return path;
    }
    
}
