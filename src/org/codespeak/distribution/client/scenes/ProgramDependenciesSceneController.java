package org.codespeak.distribution.client.scenes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.codespeak.distribution.client.data.Dependency;
import org.codespeak.distribution.client.objects.ClientException;
import org.codespeak.distribution.client.handler.BackendHandler;
import org.codespeak.distribution.client.util.AlertUtil;
import org.codespeak.distribution.client.util.MiscUtil;

/**
 * Controller for the program dependencies scene
 *
 * @author Vector
 */
public class ProgramDependenciesSceneController implements Initializable {

    private Map<String, Dependency> dependencyNameMap = new HashMap<String, Dependency>();
    private Path programDirectoryAndLaunchFile;
    private Dependency currentlySelectedDependency;
    private Runtime runtime;
    
    @FXML private Label programNameLabel;
    @FXML private Label dependencyNameLabel;
    @FXML private ListView<String> dependencyList;
    @FXML private Label dependencyDescriptionLabel;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentlySelectedDependency = null;
        runtime = Runtime.getRuntime();
    }    
    
    /**
     * Called when displaying dependency information from a program
     * @param programName name of the program
     * @param dependencies dependencies of the program
     * @param programLaunchFile launch file of the program
     */
    public void showProgramDependencies(String programName, Map<Dependency, Long> dependencies, Path programLaunchFile) {
        this.programDirectoryAndLaunchFile = programLaunchFile;
        
        programNameLabel.setText(programName);
        
        ObservableList<String> dependencyItems = dependencyList.getItems();
        
        for (Dependency dependency : dependencies.keySet()) {
            String name = dependency.getName();
            
            dependencyItems.add(name);
            dependencyNameMap.put(name, dependency);
        }
    }
    
    @FXML
    public void onDependencySelect() {
        int selectedIndex = dependencyList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex > -1) {
            ObservableList<String> dependencyItems = dependencyList.getItems();            
            String dependencyName = dependencyItems.get(selectedIndex);
            Dependency dependency = dependencyNameMap.get(dependencyName);
            
            dependencyNameLabel.setText(dependency.getName());
            dependencyDescriptionLabel.setText(dependency.getDescription());
            
            currentlySelectedDependency = dependency;
        }
    }
    
    @FXML
    public void onInstallDependencyButtonClick(ActionEvent event) throws IOException {
        if (currentlySelectedDependency != null) {
            String URL = currentlySelectedDependency.getURL();
            String ext = URL.substring(URL.lastIndexOf("."));
            
            try {
                ReadableByteChannel readableByteChannel = BackendHandler.getRemoteFileChannelFromURL(URL);
                File tempFile = File.createTempFile("csds_client", ext);
                FileChannel outChannel = new FileOutputStream(tempFile).getChannel();

                outChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

                readableByteChannel.close();
                outChannel.close();

                String command = tempFile.toString();
                String OS = System.getProperty("os.name");
                
                if (OS.startsWith("Windows")) {
                    command = "cmd /c " + command;
                }
                
                runtime.exec(command);
            } catch (ClientException ex) {
                Alert alert = ex.buildAlert();
                alert.show();
            }
        } else {
            Alert alert = AlertUtil.createAlert("Select a dependency first.");
            alert.show();
        }
    }

    @FXML
    public void onTestProgramButtonClick(ActionEvent event) throws IOException {
        ProcessBuilder pb = MiscUtil.createProcessBuilder(programDirectoryAndLaunchFile);
        pb.start();
    }
    
    @FXML
    public void onCloseButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
}
