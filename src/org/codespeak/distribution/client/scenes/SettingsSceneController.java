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
import org.codespeak.distribution.client.Settings.SettingFields;

/**
 * Controller for the settings scene
 *
 * @author Vector
 */
public class SettingsSceneController implements Initializable {

    private Settings settings;
    
    @FXML private Label settingsTitleLabel;
    @FXML private CheckBox rememberCurrentlySelectedCategoryCheck;
    @FXML private CheckBox notifyOfNewPrograms;
    @FXML private CheckBox checkClientUpdateOnStartupCheck;
    @FXML private CheckBox backupBeforeRemovingTextFilesCheck;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        settings = Configuration.getSettings();
        
        settingsTitleLabel.setText("Settings for " + Configuration.PROGRAM_NAME);
        rememberCurrentlySelectedCategoryCheck.setSelected(settings.getValue(SettingFields.REMEMBER_SELECTED_CATEGORY));
        notifyOfNewPrograms.setSelected(settings.getValue(SettingFields.NOTIFY_OF_NEW_PROGRAMS));
        checkClientUpdateOnStartupCheck.setSelected(settings.getValue(SettingFields.CHECK_CLIENT_UPDATE_ON_STARTUP));
        backupBeforeRemovingTextFilesCheck.setSelected(settings.getValue(SettingFields.BACKUP_BEFORE_REMOVING_TEXT_FILES));
    }
    
    @FXML
    public void onOkButtonClick(ActionEvent event) {
        settings.setValue(SettingFields.REMEMBER_SELECTED_CATEGORY, rememberCurrentlySelectedCategoryCheck.isSelected());
        settings.setValue(SettingFields.NOTIFY_OF_NEW_PROGRAMS, notifyOfNewPrograms.isSelected());
        settings.setValue(SettingFields.CHECK_CLIENT_UPDATE_ON_STARTUP, checkClientUpdateOnStartupCheck.isSelected());
        settings.setValue(SettingFields.BACKUP_BEFORE_REMOVING_TEXT_FILES, backupBeforeRemovingTextFilesCheck.isSelected());        
        
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    @FXML
    public void onCloseButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

}
