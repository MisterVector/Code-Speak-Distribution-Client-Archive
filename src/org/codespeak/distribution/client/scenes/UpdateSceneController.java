package org.codespeak.distribution.client.scenes;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.codespeak.distribution.client.data.ChangelogEntry;
import org.codespeak.distribution.client.objects.Updater;
import org.codespeak.distribution.client.util.DateUtil;

/**
 * Controller for the program update scene
 *
 * @author Vector
 */
public class UpdateSceneController implements Initializable {

    private Updater updater = null;
    
    @FXML private Label versionNotificationLabel;
    @FXML private Label changesSinceLabel;
    @FXML private TextArea changesSinceText;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    /**
     * Called when a program update is being displayed. The information here
     * will be used to display various pieces of information
     * @param updater updater representing the client/program being updated
     */
    public void showUpdate(Updater updater) {
        this.updater = updater;
        
        versionNotificationLabel.setText(updater.getName() + " version " + updater.getCurrentVersion() + " is available!");
        changesSinceLabel.setText("Showing changes since version " + updater.getPreviousVersion());
        
        StringBuilder sb = new StringBuilder();
        
        for (ChangelogEntry entry : updater.getEntries()) {
            String formattedReleaseTime = DateUtil.formatInstant(entry.getReleaseTime());
            
            if (sb.length() > 0) {
                sb.append("\n\n");
            }
            
            sb.append("Version ").append(entry.getVersion()).append(" - Released on ").append(formattedReleaseTime)
              .append("\n\n").append(entry.getContent());
        }
        
        changesSinceText.setText(sb.toString());
    }
    
    @FXML
    public void onUpdateButtonClick(ActionEvent event) throws Exception {
        updater.update();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    @FXML
    public void onCancelButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
}
