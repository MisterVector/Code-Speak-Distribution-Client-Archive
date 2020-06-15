package org.codespeak.distribution.client.scenes;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.codespeak.distribution.client.Configuration;
import org.codespeak.distribution.client.Settings;

/**
 * Controller for the settings scene
 *
 * @author Vector
 */
public class SettingsSceneController implements Initializable {

    private Settings settings;
    
    @FXML private Label settingsTitleLabel;
    @FXML private CheckBox rememberCurrentlySelectedCategoryCheck;
    @FXML private CheckBox checkClientUpdateOnStartupCheck;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        settings = Configuration.getSettings();
        
        settingsTitleLabel.setText("Settings for " + Configuration.PROGRAM_NAME);
        rememberCurrentlySelectedCategoryCheck.setSelected(settings.getRememberSelectedCategory());
        checkClientUpdateOnStartupCheck.setSelected(settings.getCheckClientUpdateOnStartup());
    }
    
    @FXML
    public void onOkButtonClick(ActionEvent event) {
        settings.setRememberSelectedCategory(rememberCurrentlySelectedCategoryCheck.isSelected());
        settings.setCheckClientUpdateOnStartup(checkClientUpdateOnStartupCheck.isSelected());
        
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    @FXML
    public void onCloseButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

}
