package org.codespeak.distribution.client.scenes;

import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.codespeak.distribution.client.Configuration;

/**
 * Controller for the about scene
 *
 * @author Vector
 */
public class AboutSceneController implements Initializable {
    
    private Desktop desktop;

    @FXML private Label headerLabel;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        desktop = Desktop.getDesktop();
        
        headerLabel.setText(headerLabel.getText().replace("%v", Configuration.PROGRAM_VERSION));
    }    

    @FXML
    public void onCodeSpeakLinkClick() throws Exception {
        desktop.browse(new URI(Configuration.WEBSITE_URL));
    }
    
    @FXML
    public void onCloseButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
}
