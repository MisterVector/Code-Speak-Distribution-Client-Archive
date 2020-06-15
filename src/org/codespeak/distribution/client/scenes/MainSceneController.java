package org.codespeak.distribution.client.scenes;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.codespeak.distribution.client.Configuration;
import org.codespeak.distribution.client.DistributionClient;
import org.codespeak.distribution.client.Settings;
import org.codespeak.distribution.client.data.Category;
import org.codespeak.distribution.client.data.ChangelogEntry;
import org.codespeak.distribution.client.data.ClientCheckVersionResponse;
import org.codespeak.distribution.client.handler.DataHandler;
import org.codespeak.distribution.client.data.Dependency;
import org.codespeak.distribution.client.data.FileInfo;
import org.codespeak.distribution.client.data.Program;
import org.codespeak.distribution.client.objects.ClientException;
import org.codespeak.distribution.client.data.query.QueryTypes;
import org.codespeak.distribution.client.handler.BackendHandler;
import org.codespeak.distribution.client.objects.ClientUpdater;
import org.codespeak.distribution.client.objects.ProgramTableData;
import org.codespeak.distribution.client.objects.ProgramUpdater;
import org.codespeak.distribution.client.objects.StageController;
import org.codespeak.distribution.client.objects.Updater;
import org.codespeak.distribution.client.util.AlertUtil;
import org.codespeak.distribution.client.util.MiscUtil;
import org.codespeak.distribution.client.util.SceneUtil;
import org.codespeak.distribution.client.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author ComputerDoctor
 */
public class MainSceneController implements Initializable {

    private Map<String, Category> categoryNamesMap = new HashMap<String, Category>();
    private Program currentlySelectedProgram;
    private Program currentlySelectedInstalledProgram;
    private int currentlySelectedCategoryIndex;
    private int currentlySelectedProgramIndex;
    private Settings settings;
    
    @FXML private ComboBox<String> categoryChoices;
    @FXML private TableView<ProgramTableData> programsTable;
    @FXML private TableColumn<ProgramTableData, String> programsTableNameColumn;
    @FXML private TableColumn<ProgramTableData, String> programsTableVersionColumn;
    @FXML private TableColumn<ProgramTableData, String> programsTableReleaseDateColumn;
    @FXML private Label programNameLabel;
    @FXML private Label programDescriptionLabel;
    @FXML private Label programUpdateLabel;
    @FXML private Button launchProgramButton;
    @FXML private Button installButton;
    @FXML private Button updateButton;

    private void populateCategories() {
        List<Category> categories = DataHandler.getCategories();
        ObservableList categoryItems = categoryChoices.getItems();
        
        categoryNamesMap.clear();
        categoryItems.clear();
        
        categoryItems.add("All");
        categoryNamesMap.put("All", null);
        
        for (Category category : categories) {
            String categoryName = category.getName();
            
            categoryItems.add(categoryName);
            categoryNamesMap.put(categoryName, category);
        }
    }
    
    private void selectProgram(int selectedIndex) {
        if (selectedIndex > -1 && selectedIndex != currentlySelectedProgramIndex) {
            currentlySelectedProgramIndex = selectedIndex;
            
            ProgramTableData programData = programsTable.getItems().get(selectedIndex);
            Program selectedProgram = programData.getProgram();
            
            if (DistributionClient.isOnline()) {
                if (selectedProgram.isInstalled()) {
                    currentlySelectedProgram = DataHandler.getProgram(selectedProgram.getId(), false);
                    currentlySelectedInstalledProgram = selectedProgram;
                } else {
                    currentlySelectedProgram = selectedProgram;
                    currentlySelectedInstalledProgram = DataHandler.getProgram(selectedProgram.getId(), true);
                }                
            } else {
                currentlySelectedProgram = selectedProgram;
                currentlySelectedInstalledProgram = selectedProgram;
            }
            
            displayProgramControls(currentlySelectedProgram, currentlySelectedInstalledProgram);
        }
    }

    private void launchInstalledProgram() throws IOException {
            Path programDirectory = currentlySelectedInstalledProgram.getDirectory().toAbsolutePath();
            Path programDirectoryAndLaunchFile = programDirectory.resolve(currentlySelectedInstalledProgram.getLaunchFile());
            String programDirectoryAndLaunchFileRaw = programDirectoryAndLaunchFile.toString();
            
            List<String> commands = new ArrayList<String>();
            
            if (programDirectoryAndLaunchFileRaw.endsWith(".jar")) {
                commands.add("java");
                commands.add("-jar");
            }
            
            commands.add(programDirectoryAndLaunchFile.toString());
            commands.add("--csds-launch");
            
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.directory(programDirectory.toFile());
            pb.start();
    }
    
    private void displayPrograms(Category category) {
        List<Program> programs = DataHandler.getPrograms(category);
        ObservableList items = programsTable.getItems();

        items.clear();
        
        for (Program program : programs) {
            String formattedReleaseDate = MiscUtil.formatTimestamp(program.getReleaseTime());
            
            ProgramTableData programData = new ProgramTableData(program, program.getName(), program.getVersion(), formattedReleaseDate);
            items.add(programData);
        }

        Tooltip t = new Tooltip(category == null ? "All programs." : category.getDescription());
        Tooltip.install(categoryChoices, t);
    }
    
    private void resetProgramControls() {
        programNameLabel.setText("No Program Selected");
        programDescriptionLabel.setText("No description. Select a program first.");
        programUpdateLabel.setText("");
            
        launchProgramButton.setDisable(true);
        installButton.setDisable(true);
        updateButton.setDisable(true);
    }
    
    private void displayProgramControls(Program program, Program installedProgram) {
        String name = null;
        String description = null;
        
        resetProgramControls();
        
        if (installedProgram != null) {
            name = installedProgram.getName();
            description = installedProgram.getDescription();
            Timestamp releaseTime = program.getReleaseTime();
            Timestamp installedReleaseTime = installedProgram.getReleaseTime();
            
            launchProgramButton.setDisable(false);
            
            if (releaseTime.after(installedReleaseTime)) {
                updateButton.setDisable(false);
                programUpdateLabel.setText("A new version is available!");
            }
        } else {
            name = program.getName();
            description = program.getDescription();
            
            installButton.setDisable(false);
        }
        
        programNameLabel.setText(name);
        programDescriptionLabel.setText(description);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentlySelectedProgram = null;
        currentlySelectedInstalledProgram = null;
        currentlySelectedCategoryIndex = -1;
        currentlySelectedProgramIndex = -1;
        
        programsTable.setEditable(false);
        
        programsTableNameColumn.setCellValueFactory(new PropertyValueFactory<ProgramTableData, String>("name"));
        programsTableVersionColumn.setCellValueFactory(new PropertyValueFactory<ProgramTableData, String>("version"));
        programsTableReleaseDateColumn.setCellValueFactory(new PropertyValueFactory<ProgramTableData, String>("releaseDate"));

        settings = Configuration.getSettings();

        populateCategories();
        
        String selectedCategoryValue = "All";
        Category selectedCategory = null;

        if (settings.getRememberSelectedCategory()) {
            if (DataHandler.hasMappedData("selected_category")) {
                String tempSelectedCategoryValue = DataHandler.getMappedData("selected_category");

                if (!StringUtil.isNullOrEmpty(tempSelectedCategoryValue)
                        && categoryNamesMap.containsKey(tempSelectedCategoryValue)) {
                    selectedCategoryValue = tempSelectedCategoryValue;
                    selectedCategory = categoryNamesMap.get(tempSelectedCategoryValue);
                }
            }            
        } else {
            DataHandler.setMappedData("selected_category", "All");
        }

        displayPrograms(selectedCategory);
        categoryChoices.getSelectionModel().select(selectedCategoryValue);
    }    

    /**
     * Checks for client update
     * @param startup if this update is being called on program startup
     */
    public void checkClientUpdate(boolean startup) {
        if (!DistributionClient.isOnline()) {
            Alert alert = AlertUtil.createAlert("Unable to check for client update at this time.");
            alert.show();
            
            return;
        }

        if (!startup || settings.getCheckClientUpdateOnStartup()) {
            try {
                ClientCheckVersionResponse response = BackendHandler.getDataFromQuery(QueryTypes.CHECK_CLIENT_VERSION, "&current_version=" + Configuration.PROGRAM_VERSION);

                Timestamp requestReleaseTime = response.getRequestReleaseTime();
                Timestamp releaseTime = response.getReleaseTime();
                String version = response.getVersion();

                if (releaseTime.after(requestReleaseTime)) {
                    List<ChangelogEntry> entries = BackendHandler.getDataFromQuery(QueryTypes.GET_CLIENT_CHANGELOG, "&since_version=" + Configuration.PROGRAM_VERSION);
                    StageController<UpdateSceneController> stageController = SceneUtil.getScene(SceneTypes.UPDATE, "New verion for " + Configuration.PROGRAM_NAME);
                    Stage stage = stageController.getStage();
                    UpdateSceneController controller = stageController.getController();
                    Updater updater = new ClientUpdater(Configuration.PROGRAM_VERSION, version, entries);

                    stage.show();
                    controller.showUpdate(updater);
                } else {
                    if (!startup) {
                        Alert alert = AlertUtil.createAlert(AlertType.CONFIRMATION, "The client is on the latest version!");
                        alert.show();
                    }
                }                
            } catch (IOException ex) {

            } catch (ClientException ex) {
                Alert alert = ex.buildAlert();
                alert.show();

                DistributionClient.logError(ex);
            }
        }
    }
    
    /**
     * Called when a program is updated
     * @param installedProgram an installed program
     * @param program the latest information on a program
     * @throws java.io.IOException
     */
    public void onUpdateProgram(Program program, Program installedProgram) throws IOException {
        try {
            installedProgram.update(program);
        
            ObservableList<ProgramTableData> programItems = programsTable.getItems();
            ProgramTableData programData = programItems.get(currentlySelectedProgramIndex);

            programData.setVersion(installedProgram.getVersion());
            programData.setReleaseTime(installedProgram.getReleaseTime().toString());

            programItems.set(currentlySelectedProgramIndex, programData);

            displayProgramControls(program, installedProgram);
        } catch (ClientException ex) {
            Alert alert = ex.buildAlert();
            alert.show();
            
            DistributionClient.logError(ex);
        }
    }

    @FXML
    public void onProgramsTableKeyReleased(KeyEvent event) throws IOException {
        KeyCode code = event.getCode();
        
        switch (code) {
            case UP:
            case DOWN:
            case SPACE:
                TableViewSelectionModel<ProgramTableData> selectionModel = programsTable.getSelectionModel();
                int selectedIndex = selectionModel.getSelectedIndex();

                selectProgram(selectedIndex);
                
                break;
            case ENTER:
                if (currentlySelectedInstalledProgram != null) {
                    launchInstalledProgram();
                }
                
                break;
        }
    }
 
    @FXML
    public void onSettingsMenuItemClick(ActionEvent event) throws Exception {
        Stage stage = SceneUtil.getScene(SceneTypes.SETTINGS, "Settings for " + Configuration.PROGRAM_NAME).getStage();
        stage.show();
    }
    
    @FXML
    public void onQuitMenuItemClick(ActionEvent event) {
        Platform.exit();
    }
    
    @FXML
    public void onAboutMenuItemClick() throws IOException {
        Stage stage = SceneUtil.getScene(SceneTypes.ABOUT, "About " + Configuration.PROGRAM_NAME).getStage();
        stage.show();
    }

    @FXML
    public void onViewHelpMenuItemClick() throws IOException {
        if (currentlySelectedInstalledProgram != null) {
            String helpFile = currentlySelectedInstalledProgram.getHelpFile();
            
            if (StringUtil.isNullOrEmpty(helpFile)) {
                Alert alert = AlertUtil.createAlert("No help file is associated with this program.");
                alert.show();
                
                return;
            }
            
            Path helpFilePath = currentlySelectedInstalledProgram.getDirectory().resolve(helpFile);
            
            Desktop desktop = Desktop.getDesktop();
            desktop.open(helpFilePath.toFile());
        } else {
            Alert alert = AlertUtil.createAlert("Select an installed program first.");
            alert.show();
        }
    }
    
    @FXML
    public void onViewDependenciesMenuItemClick() throws Exception {
        if (currentlySelectedInstalledProgram != null) {
            String programName = currentlySelectedInstalledProgram.getName();
            List<Dependency> dependencies = currentlySelectedInstalledProgram.getDependencies(false);

            if (dependencies.size() > 0) {
                Path programLaunchFile = currentlySelectedInstalledProgram.getDirectory(true);
                StageController<ProgramDependenciesSceneController> stageController = SceneUtil.getScene(SceneTypes.PROGRAM_DEPENDENCIES, "Dependencies for " + programName);
                ProgramDependenciesSceneController controller = stageController.getController();
                Stage stage = stageController.getStage();

                stage.show();
                controller.showProgramDependencies(programName, dependencies, programLaunchFile);
            } else {
                Alert alert = AlertUtil.createAlert(programName + " does not have any dependencies.");
                alert.show();
            }
        } else {
            Alert alert = AlertUtil.createAlert("Select an installed program first.");
            alert.show();
        }
    }
    
    @FXML
    public void onOpenFolderMenuItemClick() throws IOException {
        if (currentlySelectedInstalledProgram != null) {
            Path programDirectory = currentlySelectedInstalledProgram.getDirectory();
            Desktop desktop = Desktop.getDesktop();
            
            desktop.open(programDirectory.toFile());
        } else {
            Alert alert = AlertUtil.createAlert("Select an installed program first.");
            alert.show();
        }
    }

    @FXML
    public void onProgramRepairButtonClick() throws IOException {
        if (currentlySelectedInstalledProgram != null) {
            if (!DistributionClient.isOnline()) {
                Alert alert = AlertUtil.createAlert("Unable to repair program at this time.");
                alert.show();

                return;
            }

            Timestamp installedReleaseTime = currentlySelectedInstalledProgram.getReleaseTime();
            Timestamp releaseTime = currentlySelectedProgram.getReleaseTime();
            
            if (!installedReleaseTime.equals(releaseTime)) {
                Alert alert = AlertUtil.createAlert("This program must be at the latest version before it can be repaired.");
                alert.show();
                
                return;
            }

            String programName = currentlySelectedInstalledProgram.getName();
            Alert confirmAlert = AlertUtil.createAlert(AlertType.CONFIRMATION, "Repairing this program will overwrite existing "
                        + "files. Make sure to take backups if you don't want to lose anything. Are you sure you want "
                        + "to repair this program?", "Repairing " + programName);
            
            ObservableList<ButtonType> buttons = confirmAlert.getButtonTypes();

            buttons.clear();
            buttons.addAll(ButtonType.YES, ButtonType.NO);

            ButtonType buttonType = confirmAlert.showAndWait().get();
 
            if (buttonType == ButtonType.YES) {
                try {
                    currentlySelectedInstalledProgram.repair();

                    Alert alert = AlertUtil.createAlert(programName + " has been repaired.");
                    alert.show();
                } catch (ClientException ex) {
                    Alert alert = ex.buildAlert();
                    alert.show();
                    
                    DistributionClient.logError(ex);
                }
            }
        } else {
            Alert alert = AlertUtil.createAlert("Select an installed program first.");
            alert.show();
        }
    }
    
    @FXML
    public void onProgramViewChangelogButtonClick() throws IOException {
        if (currentlySelectedProgram != null) {
            if (!DistributionClient.isOnline()) {
                Alert alert = AlertUtil.createAlert("Unable to view program changelog at this time.");
                alert.show();

                return;
            }

            int id = 0;
            String name = null;

            if (currentlySelectedInstalledProgram != null) {
                id = currentlySelectedInstalledProgram.getId();
                name = currentlySelectedInstalledProgram.getName();
            } else {
                id = currentlySelectedProgram.getId();
            }

            try {
                List<ChangelogEntry> entries = BackendHandler.getDataFromQuery(QueryTypes.GET_PROGRAM_CHANGELOG, "&id=" + id);
                StageController<ChangelogSceneController> stageController = SceneUtil.getScene(SceneTypes.CHANGELOG, "Changelog for " + name);
                ChangelogSceneController controller = stageController.getController();
                Stage stage = stageController.getStage();

                stage.show();
                controller.showChangelog(name, entries);
            } catch (ClientException ex) {
                Alert alert = ex.buildAlert();
                alert.show();
                
                DistributionClient.logError(ex);
            }
        } else {
            Alert alert = AlertUtil.createAlert("Select a program first.");
            alert.show();
        }
    }
    
    @FXML
    public void onViewChangelogButtonClick() throws IOException {
        if (!DistributionClient.isOnline()) {
            Alert alert = AlertUtil.createAlert("Unable to view changelog at this time.");
            alert.show();

            return;
        }

        try {
            List<ChangelogEntry> entries = BackendHandler.getDataFromQuery(QueryTypes.GET_CLIENT_CHANGELOG);
            StageController<ChangelogSceneController> stageController = SceneUtil.getScene(SceneTypes.CHANGELOG, Configuration.PROGRAM_NAME + " Changelog");
            ChangelogSceneController controller = stageController.getController();
            Stage stage = stageController.getStage();

            stage.show();
            controller.showChangelog(Configuration.PROGRAM_NAME, entries);
        } catch (ClientException ex) {
            Alert alert = ex.buildAlert();
            alert.show();
            
            DistributionClient.logError(ex);
        }
    }
    
    @FXML
    public void onCategorySelect(ActionEvent event) {
        SingleSelectionModel<String> selectionModel = categoryChoices.getSelectionModel();
        int selectedIndex = selectionModel.getSelectedIndex();
        
        if (selectedIndex > -1 && selectedIndex != currentlySelectedCategoryIndex) {
            currentlySelectedCategoryIndex = selectedIndex;
            
            String selectedCategoryName = categoryChoices.getItems().get(selectedIndex);
            Category category = categoryNamesMap.get(selectedCategoryName);
            
            displayPrograms(category);
            resetProgramControls();

            currentlySelectedProgramIndex = -1;
            
            DataHandler.setMappedData("selected_category", selectedCategoryName);
        }
    }
    
    @FXML
    public void onProgramSelect() {
        TableViewSelectionModel<ProgramTableData> selectionModel = programsTable.getSelectionModel();
        int selectedIndex = selectionModel.getSelectedIndex();
        
        if (selectedIndex > -1 && selectedIndex != currentlySelectedProgramIndex) {
            selectProgram(selectedIndex);
        }
    }

    @FXML
    public void onLaunchProgramButtonClick() throws IOException {
        if (currentlySelectedInstalledProgram != null) {
            launchInstalledProgram();
        }
    }
    
    @FXML
    public void onInstallButtonClick() throws IOException {
        if (currentlySelectedProgram != null) {
            String programName = currentlySelectedProgram.getName();

            try {
                DataHandler.installProgram(currentlySelectedProgram);

                displayProgramControls(currentlySelectedProgram, currentlySelectedProgram);

                currentlySelectedInstalledProgram = currentlySelectedProgram;

                List<Dependency> dependencies = currentlySelectedInstalledProgram.getDependencies(false);
                
                if (dependencies.size() > 0) {
                    Path programLaunchFile = currentlySelectedInstalledProgram.getDirectory(true);
                    StageController<ProgramDependenciesSceneController> stageController = SceneUtil.getScene(SceneTypes.PROGRAM_DEPENDENCIES, "Dependencies for " + programName);
                    ProgramDependenciesSceneController controller = stageController.getController();
                    Stage stage = stageController.getStage();

                    stage.show();
                    controller.showProgramDependencies(programName, dependencies, programLaunchFile);
                }
            } catch (ClientException ex) {
                Alert alert = ex.buildAlert();
                alert.show();
                
                DistributionClient.logError(ex);
            }
        }
    }
    
    @FXML
    public void onUpdateButtonClick() throws IOException {
        if (currentlySelectedInstalledProgram != null) {
            String programName = currentlySelectedInstalledProgram.getName();

            try {
                int id = currentlySelectedInstalledProgram.getId();
                String version = currentlySelectedInstalledProgram.getVersion();
                List<ChangelogEntry> entries = BackendHandler.getDataFromQuery(QueryTypes.GET_PROGRAM_CHANGELOG, "&id=" + id + "&since_version=" + version);

                StageController<UpdateSceneController> stageController = SceneUtil.getScene(SceneTypes.UPDATE, "New version for " + programName);
                UpdateSceneController controller = stageController.getController();
                Stage stage = stageController.getStage();
                Updater updater = new ProgramUpdater(currentlySelectedProgram, currentlySelectedInstalledProgram, this, entries);

                stage.show();
                controller.showUpdate(updater);
            } catch (ClientException ex) {
                Alert alert = ex.buildAlert();
                alert.show();
                
                DistributionClient.logError(ex);
            }
        }
    }
    
    @FXML
    public void onUninstallMenuItemClick() throws IOException {
        if (currentlySelectedInstalledProgram != null) {
            String programName = currentlySelectedInstalledProgram.getName();
            
            Alert alert = AlertUtil.createAlert(AlertType.CONFIRMATION, "Are you sure you want to uninstall " + programName + "?");

            ObservableList<ButtonType> buttons = alert.getButtonTypes();

            buttons.clear();
            buttons.addAll(ButtonType.YES, ButtonType.NO);

            ButtonType buttonType = alert.showAndWait().get();

            if (buttonType == ButtonType.YES) {
                DataHandler.uninstallProgram(currentlySelectedInstalledProgram);

                if (DistributionClient.isOnline()) {
                    currentlySelectedInstalledProgram = null;

                    displayProgramControls(currentlySelectedProgram, null);
                } else {
                    currentlySelectedProgram = null;
                    currentlySelectedInstalledProgram = null;
                    currentlySelectedProgramIndex = -1;
                    currentlySelectedCategoryIndex = 0;

                    populateCategories();
                    displayPrograms(null);
                    resetProgramControls();

                    categoryChoices.getSelectionModel().select("All");
                }
                
                Alert uninstallAlert = AlertUtil.createAlert(programName + " has been uninstalled.");
                uninstallAlert.show();
            }
        } else {
            Alert alert = AlertUtil.createAlert("Select an installed program first.");
            alert.show();
        }
    }

    @FXML
    public void onClientCheckForUpdateMenuClick() {
        checkClientUpdate(false);
    }
    
}
