package org.codespeak.distribution.client.scenes;

import java.io.IOException;
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
import org.codespeak.distribution.client.data.Program;
import org.codespeak.distribution.client.util.MiscUtil;

/**
 * Controller for the program update scene
 *
 * @author Vector
 */
public class ProgramUpdateSceneController implements Initializable {

    private MainSceneController controller;
    private Program program;
    private Program installedProgram;
    
    @FXML private Label programUpdateLabel;
    @FXML private Label programChangesSinceLabel;
    @FXML private TextArea programChangesText;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        program = null;
        installedProgram = null;
    }
    
    /**
     * Called when a program update is being displayed. The information here
     * will be used to display various pieces of information
     * @param controller
     * @param program
     * @param installedProgram
     * @param changelogEntries 
     */
    public void showUpdate(MainSceneController controller, Program program, Program installedProgram, List<ChangelogEntry> changelogEntries) {
        this.controller = controller;
        this.program = program;
        this.installedProgram = installedProgram;
        
        programUpdateLabel.setText(program.getName() + " version " + program.getVersion() + " is available!");
        programChangesSinceLabel.setText("Changes since " + installedProgram.getVersion());
        
        StringBuilder sb = new StringBuilder();
        
        for (ChangelogEntry entry : changelogEntries) {
            String formattedReleaseTime = MiscUtil.formatTimestamp(entry.getReleaseTime());
            
            if (sb.length() > 0) {
                sb.append("\n\n\n");
            }
            
            sb.append(entry.getVersion()).append(" (").append(formattedReleaseTime).append(")")
              .append("\n\n\n").append(entry.getContent());
        }
        
        programChangesText.setText(sb.toString());
    }
    
    @FXML
    public void onUpdateButtonClick(ActionEvent event) throws IOException {
        controller.onUpdateProgram(program, installedProgram);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    @FXML
    public void onCancelButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
}
