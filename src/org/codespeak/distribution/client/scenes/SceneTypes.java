package org.codespeak.distribution.client.scenes;

/**
 * An enum containing a list of scenes
 *
 * @author Vector
 */
public enum SceneTypes {
    
    MAIN("MainScene.fxml"),
    ABOUT("AboutScene.fxml");
    
    private final String fxmlName;
    
    private SceneTypes(String fxmlName) {
        this.fxmlName = fxmlName;
    }
    
    public String getPath() {
        return "/org/codespeak/distribution/client/scenes/" + fxmlName;
    }
    
}
