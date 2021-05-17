package org.codespeak.distribution.client.scenes;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.codespeak.distribution.client.data.ChangelogEntry;
import org.codespeak.distribution.client.util.DateUtil;

/**
 * Controller for the changelog scene
 *
 * @author Vector
 */
public class ChangelogSceneController implements Initializable {
    
    @FXML private Label changelogNameLabel;
    @FXML private TextArea changelogText;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
    
    /**
     * Called when a program update is being displayed.The information here
     * will be used to display various pieces of information
     * @param changelogName the name of the changelog
     * @param changelogEntries 
     */
    public void showChangelog(String changelogName, List<ChangelogEntry> changelogEntries) {
        changelogNameLabel.setText("Changelog for " + changelogName);
        
        StringBuilder sb = new StringBuilder();
        
        for (ChangelogEntry entry : changelogEntries) {
            String formattedReleaseTime = DateUtil.formatInstant(entry.getReleaseTime());
            
            if (sb.length() > 0) {
                sb.append("\n\n");
            }
            
            sb.append("Version ").append(entry.getVersion()).append(" - Released on ").append(formattedReleaseTime)
              .append("\n\n").append(entry.getContent());
        }
        
        changelogText.setText(sb.toString());
    }

    @FXML
    public void onCloseButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
}
