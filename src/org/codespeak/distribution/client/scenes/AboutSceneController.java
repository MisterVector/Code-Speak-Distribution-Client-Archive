/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codespeak.distribution.client.scenes;

import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.codespeak.distribution.client.Configuration;

/**
 * FXML Controller class
 *
 * @author Vector
 */
public class AboutSceneController implements Initializable {
    
    private Desktop desktop;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        desktop = Desktop.getDesktop();
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
