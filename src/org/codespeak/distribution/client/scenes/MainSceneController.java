package org.codespeak.distribution.client.scenes;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.codespeak.distribution.client.Configuration;
import org.codespeak.distribution.client.Main;
import org.codespeak.distribution.client.Settings;
import org.codespeak.distribution.client.Settings.SettingFields;
import org.codespeak.distribution.client.data.Category;
import org.codespeak.distribution.client.data.ChangelogEntry;
import org.codespeak.distribution.client.data.ClientCheckVersionResponse;
import org.codespeak.distribution.client.handler.DataHandler;
import org.codespeak.distribution.client.data.Dependency;
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
import org.codespeak.distribution.client.util.DateUtil;
import org.codespeak.distribution.client.util.MiscUtil;
import org.codespeak.distribution.client.util.SceneUtil;
import org.codespeak.distribution.client.util.StringUtil;

/**
 * Controller for the main scene
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
    @FXML private Label categoryDescriptionLabel;
    @FXML private TableView<ProgramTableData> programsTable;
    @FXML private TableColumn<ProgramTableData, String> programsTableNameColumn;
    @FXML private TableColumn<ProgramTableData, String> programsTableVersionColumn;
    @FXML private TableColumn<ProgramTableData, String> programsTableReleaseDateColumn;
    @FXML private Label programNameLabel;
    @FXML private Label programDescriptionLabel;
    @FXML private Label programDetailsLabel;
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
            
            if (Main.isOnline()) {
                if (selectedProgram.isInstalled()) {
                    if (selectedProgram.isDetached()) {
                        currentlySelectedProgram = selectedProgram;
                    } else {
                        currentlySelectedProgram = DataHandler.getProgram(selectedProgram.getId(), false);
                    }
                    
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
        ProcessBuilder pb = MiscUtil.createProcessBuilder(currentlySelectedInstalledProgram.getDirectory(true));
        pb.start();
    }
    
    private void displayPrograms(Category category) {
        List<Program> programs = DataHandler.getPrograms(category);
        ObservableList items = programsTable.getItems();

        items.clear();
        
        for (Program program : programs) {
            String formattedReleaseDate = DateUtil.formatInstant(program.getReleaseTime());
            
            ProgramTableData programData = new ProgramTableData(program, program.getName(), program.getVersion(), formattedReleaseDate);
            items.add(programData);
        }

        categoryDescriptionLabel.setText(category == null ? "All programs." : category.getDescription());
    }
    
    private void resetProgramControls() {
        programNameLabel.setText("No Program Selected");
        programDescriptionLabel.setText("No description. Select a program first.");
        programDetailsLabel.setText("");
            
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
            Instant releaseTime = program.getReleaseTime();
            Instant installedReleaseTime = installedProgram.getReleaseTime();
            
            launchProgramButton.setDisable(false);
            
            if (installedProgram.isDetached()) {
                programDetailsLabel.setText("This program is no longer maintained.");
            } else {
                if (releaseTime.isAfter(installedReleaseTime)) {
                    updateButton.setDisable(false);
                    programDetailsLabel.setText("A new version is available!");
                }                
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

        if (Main.isOnline()) {
            DataHandler.markDetachedPrograms();
        }
        
        populateCategories();
        
        String selectedCategoryValue = "All";
        Category selectedCategory = null;
        boolean rememberSelectedCategory = settings.getValue(SettingFields.REMEMBER_SELECTED_CATEGORY);

        if (rememberSelectedCategory) {
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
     * Checks for newly added programs to the distribution system
     */
    public void checkForNewPrograms() {
        if (Main.isOnline()) {
            List<Program> newPrograms = DataHandler.getNewPrograms();
            
            if (!newPrograms.isEmpty()) {
                String newProgramList = "";
                
                for (Program program : newPrograms) {
                    if (!newProgramList.isEmpty()) {
                        newProgramList += "\n";
                    }
                    
                    newProgramList += program.getName();
                }
                
                Alert alert = AlertUtil.createAlert("The following programs have been added since this program was started:\n\n" + newProgramList);
                alert.show();
            }
        }
    }

    /**
     * Checks for client update
     * @param startup if this update is being called on program startup
     */
    public void checkClientUpdate(boolean startup) {
        if (!Main.isOnline()) {
            if (!startup) {
                Alert alert = AlertUtil.createAlert("Unable to check for client update at this time.");
                alert.show();
            }
            
            return;
        }

        boolean checkClientUpdateOnStartup = settings.getValue(SettingFields.CHECK_CLIENT_UPDATE_ON_STARTUP);
        
        if (!startup || checkClientUpdateOnStartup) {
            try {
                ClientCheckVersionResponse response = BackendHandler.getDataFromQuery(QueryTypes.CHECK_CLIENT_VERSION, "&current_version=" + Configuration.PROGRAM_VERSION);

                Instant requestReleaseTime = response.getRequestReleaseTime();
                Instant releaseTime = response.getReleaseTime();
                String version = response.getVersion();

                if (releaseTime.isAfter(requestReleaseTime)) {
                    List<ChangelogEntry> entries = BackendHandler.getDataFromQuery(QueryTypes.GET_CLIENT_CHANGELOG, "&since_version=" + Configuration.PROGRAM_VERSION);
                    StageController<UpdateSceneController> stageController = SceneUtil.getScene(SceneTypes.UPDATE, "New verion for " + Configuration.PROGRAM_NAME);
                    Stage stage = stageController.getStage();
                    UpdateSceneController controller = stageController.getController();
                    Updater updater = new ClientUpdater(Configuration.PROGRAM_VERSION, version, entries);

                    stage.show();
                    controller.showUpdate(updater);
                } else {
                    if (!startup) {
                        Alert alert = AlertUtil.createAlert(AlertType.INFORMATION, "The client is on the latest version!");
                        alert.show();
                    }
                }                
            } catch (ClientException | IOException e) {
                ClientException ex = ClientException.fromException(e);
                
                Alert alert = ex.buildAlert();
                alert.show();

                Main.logError(ex);
            }
        }
    }
    
    /**
     * Called when a program is updated
     * @param installedProgram an installed program
     * @param program the latest information on a program
     */
    public void onUpdateProgram(Program program, Program installedProgram) {
        try {
            boolean newDependencies = installedProgram.hasNewDependencies(program.getDependencies(), true);
            
            installedProgram.update(program);
        
            ObservableList<ProgramTableData> programItems = programsTable.getItems();
            ProgramTableData programData = programItems.get(currentlySelectedProgramIndex);

            programData.setVersion(installedProgram.getVersion());
            programData.setReleaseTime(installedProgram.getReleaseTime());

            programItems.set(currentlySelectedProgramIndex, programData);

            displayProgramControls(program, installedProgram);
            
            if (newDependencies) {
                Alert alert = AlertUtil.createAlert("This program has new dependencies. The dependencies window will now be shown.");
                alert.showAndWait();
                
                String programName = installedProgram.getName();
                StageController<ProgramDependenciesSceneController> stageController = SceneUtil.getScene(SceneTypes.PROGRAM_DEPENDENCIES, "Dependencies for " + programName);
                Stage stage = stageController.getStage();
                ProgramDependenciesSceneController controller = stageController.getController();
                
                stage.show();
                controller.showProgramDependencies(programName, installedProgram.getDependencies(true), installedProgram.getDirectory(true));
            }
        } catch (ClientException | IOException e) {
            ClientException ex = ClientException.fromException(e);
            
            Alert alert = ex.buildAlert();
            alert.show();
            
            Main.logError(ex);
        }
    }

    @FXML
    public void onProgramsTableKeyReleased(KeyEvent event) {
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
                    try {
                        launchInstalledProgram();
                    } catch (IOException e) {
                        ClientException ex = ClientException.fromException(e);

                        Alert alert = ex.buildAlert();
                        alert.show();

                        Main.logError(ex);
                    }
                }
                
                break;
        }
    }

    @FXML
    public void onOpenBackupFolderMenuItemClick(ActionEvent event) {
        Path backupFolder = Paths.get(Configuration.BACKUPS_FOLDER);
        
        if (!backupFolder.toFile().exists()) {
            Alert alert = AlertUtil.createAlert("There are no program backups.");
            alert.show();
            
            return;
        }

        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(backupFolder.toFile());
        } catch (IOException e) {
            ClientException ex = ClientException.fromException(e);

            Alert alert = ex.buildAlert();
            alert.show();
            
            Main.logError(ex);
        }
    }
    
    @FXML
    public void onSettingsMenuItemClick(ActionEvent event) {
        try {
            Stage stage = SceneUtil.getScene(SceneTypes.SETTINGS, "Settings for " + Configuration.PROGRAM_NAME).getStage();
            stage.show();
        } catch (IOException e) {
            ClientException ex = ClientException.fromException(e);

            Alert alert = ex.buildAlert();
            alert.show();
            
            Main.logError(ex);
        }
    }
    
    @FXML
    public void onQuitMenuItemClick(ActionEvent event) {
        Platform.exit();
    }
    
    @FXML
    public void onAboutMenuItemClick() {
        try {
            Stage stage = SceneUtil.getScene(SceneTypes.ABOUT, "About " + Configuration.PROGRAM_NAME).getStage();
            stage.show();
        } catch (IOException e) {
            ClientException ex = ClientException.fromException(e);

            Alert alert = ex.buildAlert();
            alert.show();
            
            Main.logError(ex);
        }
    }

    @FXML
    public void onProgramViewHelpMenuItemClick() {
        if (currentlySelectedInstalledProgram != null) {
            String helpFile = currentlySelectedInstalledProgram.getHelpFile();
            
            if (StringUtil.isNullOrEmpty(helpFile)) {
                Alert alert = AlertUtil.createAlert("No help file is associated with this program.");
                alert.show();
                
                return;
            }
            
            Path helpFilePath = currentlySelectedInstalledProgram.getDirectory().resolve(helpFile);
            
            if (!helpFilePath.toFile().exists()) {
                Alert alert = AlertUtil.createAlert("The help file was not found.");
                alert.show();
                
                return;
            }
            
            try {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(helpFilePath.toFile());
            } catch (IOException e) {
                ClientException ex = ClientException.fromException(e);

                Alert alert = ex.buildAlert();
                alert.show();

                Main.logError(ex);
            }
        } else {
            Alert alert = AlertUtil.createAlert("Select an installed program first.");
            alert.show();
        }
    }
    
    @FXML
    public void onViewDependenciesMenuItemClick() {
        if (currentlySelectedInstalledProgram != null) {
            String programName = currentlySelectedInstalledProgram.getName();
            Map<Dependency, Long> dependencies = currentlySelectedInstalledProgram.getDependencies(true);

            if (dependencies.size() > 0) {
                try {
                    Path programLaunchFile = currentlySelectedInstalledProgram.getDirectory(true);
                    StageController<ProgramDependenciesSceneController> stageController = SceneUtil.getScene(SceneTypes.PROGRAM_DEPENDENCIES, "Dependencies for " + programName);
                    ProgramDependenciesSceneController controller = stageController.getController();
                    Stage stage = stageController.getStage();

                    stage.show();
                    controller.showProgramDependencies(programName, dependencies, programLaunchFile);
                } catch (IOException e) {
                    ClientException ex = ClientException.fromException(e);

                    Alert alert = ex.buildAlert();
                    alert.show();

                    Main.logError(ex);
                }
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
    public void onOpenFolderMenuItemClick() {
        if (currentlySelectedInstalledProgram != null) {
            Path programDirectory = currentlySelectedInstalledProgram.getDirectory();

            try {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(programDirectory.toFile());
            } catch (IOException e) {
                ClientException ex = ClientException.fromException(e);

                Alert alert = ex.buildAlert();
                alert.show();

                Main.logError(ex);
            }
        } else {
            Alert alert = AlertUtil.createAlert("Select an installed program first.");
            alert.show();
        }
    }

    @FXML
    public void onProgramRepairButtonClick() {
        if (currentlySelectedInstalledProgram != null) {
            if (!Main.isOnline()) {
                Alert alert = AlertUtil.createAlert("Unable to repair program at this time.");
                alert.show();

                return;
            }

            if (currentlySelectedInstalledProgram.isDetached()) {
                Alert alert = AlertUtil.createAlert("Unable to repair. This program is no longer available.");
                alert.show();
                
                return;
            }
            
            Instant installedReleaseTime = currentlySelectedInstalledProgram.getReleaseTime();
            Instant releaseTime = currentlySelectedProgram.getReleaseTime();
            
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

            buttons.setAll(ButtonType.YES, ButtonType.NO);

            ButtonType buttonType = confirmAlert.showAndWait().get();
 
            if (buttonType == ButtonType.YES) {
                try {
                    currentlySelectedInstalledProgram.repair();

                    Alert alert = AlertUtil.createAlert(programName + " has been repaired.");
                    alert.show();
                } catch (ClientException | IOException e) {
                    ClientException ex = ClientException.fromException(e);

                    Alert alert = ex.buildAlert();
                    alert.show();
                    
                    Main.logError(ex);
                }
            }
        } else {
            Alert alert = AlertUtil.createAlert("Select an installed program first.");
            alert.show();
        }
    }
    
    @FXML
    public void onProgramViewChangelogButtonClick() {
        if (currentlySelectedProgram != null) {
            if (!Main.isOnline()) {
                Alert alert = AlertUtil.createAlert("Unable to view program changelog at this time.");
                alert.show();

                return;
            }

            if (currentlySelectedProgram.isDetached()) {
                Alert alert = AlertUtil.createAlert("Unable to view changelog. This program is no longer available.");
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
                name = currentlySelectedProgram.getName();
            }

            try {
                List<ChangelogEntry> entries = BackendHandler.getDataFromQuery(QueryTypes.GET_PROGRAM_CHANGELOG, "&id=" + id);
                StageController<ChangelogSceneController> stageController = SceneUtil.getScene(SceneTypes.CHANGELOG, "Changelog for " + name);
                ChangelogSceneController controller = stageController.getController();
                Stage stage = stageController.getStage();

                stage.show();
                controller.showChangelog(name, entries);
            } catch (ClientException | IOException e) {
                ClientException ex = ClientException.fromException(e);

                Alert alert = ex.buildAlert();
                alert.show();
                
                Main.logError(ex);
            }
        } else {
            Alert alert = AlertUtil.createAlert("Select a program first.");
            alert.show();
        }
    }
    
    @FXML
    public void onViewChangelogButtonClick() {
        if (!Main.isOnline()) {
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
        } catch (ClientException | IOException e) {
            ClientException ex = ClientException.fromException(e);

            Alert alert = ex.buildAlert();
            alert.show();
            
            Main.logError(ex);
        }
    }
    
    @FXML
    public void onViewHelpMenuItemClick(ActionEvent event) {
        File file = new File(Configuration.README_FILE);
        
        if (!file.exists()) {
            Alert alert = AlertUtil.createAlert(("The help file was not found."));
            alert.show();
            
            return;
        }
        
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(file);
        } catch (IOException e) {
            ClientException ex = ClientException.fromException(e);

            Alert alert = ex.buildAlert();
            alert.show();
            
            Main.logError(ex);
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
    public void onLaunchProgramButtonClick() {
        if (currentlySelectedInstalledProgram != null) {
            try {
                launchInstalledProgram();
            } catch (IOException e) {
                ClientException ex = ClientException.fromException(e);

                Alert alert = ex.buildAlert();
                alert.show();

                Main.logError(ex);
            }
        }
    }
    
    @FXML
    public void onInstallButtonClick() {
        if (currentlySelectedProgram != null) {
            String programName = currentlySelectedProgram.getName();
            
            try {
                DataHandler.installProgram(currentlySelectedProgram);

                displayProgramControls(currentlySelectedProgram, currentlySelectedProgram);

                currentlySelectedInstalledProgram = currentlySelectedProgram;

                Map<Dependency, Long> dependencies = currentlySelectedInstalledProgram.getDependencies(true);
                
                if (dependencies.size() > 0) {
                    Path programLaunchFile = currentlySelectedInstalledProgram.getDirectory(true);
                    StageController<ProgramDependenciesSceneController> stageController = SceneUtil.getScene(SceneTypes.PROGRAM_DEPENDENCIES, "Dependencies for " + programName);
                    ProgramDependenciesSceneController controller = stageController.getController();
                    Stage stage = stageController.getStage();

                    stage.show();
                    controller.showProgramDependencies(programName, dependencies, programLaunchFile);
                }
            } catch (ClientException | IOException e) {
                ClientException ex = ClientException.fromException(e);

                Alert alert = ex.buildAlert();
                alert.show();
                
                Main.logError(ex);
            }
        }
    }
    
    @FXML
    public void onUpdateButtonClick() {
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
            } catch (ClientException | IOException e) {
                ClientException ex = ClientException.fromException(e);
                
                Alert alert = ex.buildAlert();
                alert.show();
                
                Main.logError(ex);
            }
        }
    }
    
    @FXML
    public void onUninstallMenuItemClick() {
        if (currentlySelectedInstalledProgram != null) {
            String programName = currentlySelectedInstalledProgram.getName();
            
            Alert alert = AlertUtil.createAlert(AlertType.CONFIRMATION, "Are you sure you want to uninstall " + programName + "?");

            ObservableList<ButtonType> buttons = alert.getButtonTypes();

            buttons.setAll(ButtonType.YES, ButtonType.NO);

            ButtonType buttonType = alert.showAndWait().get();

            if (buttonType == ButtonType.YES) {
                try {
                    DataHandler.uninstallProgram(currentlySelectedInstalledProgram);

                    if (Main.isOnline() && !currentlySelectedInstalledProgram.isDetached()) {
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
                } catch (IOException e) {
                    ClientException ex = ClientException.fromException(e);

                    alert = ex.buildAlert();
                    alert.show();

                    Main.logError(ex);
                }
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
