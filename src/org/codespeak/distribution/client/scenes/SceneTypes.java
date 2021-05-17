package org.codespeak.distribution.client.scenes;

/**
 * An enum containing a list of scenes
 *
 * @author Vector
 */
public enum SceneTypes {
    
    MAIN("MainScene.fxml"),
    ABOUT("AboutScene.fxml"),
    CHANGELOG("ChangelogScene.fxml"),
    UPDATE("UpdateScene.fxml"),
    PROGRAM_DEPENDENCIES("ProgramDependenciesScene.fxml"),
    SETTINGS("SettingsScene.fxml");
    
    private final String fxmlName;
    
    private SceneTypes(String fxmlName) {
        this.fxmlName = fxmlName;
    }
    
    /**
     * Gets the path of this scene
     * @return path of this scene
     */
    public String getPath() {
        return "/org/codespeak/distribution/client/scenes/" + fxmlName;
    }
    
}
