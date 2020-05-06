package org.codespeak.distribution.client.scenes;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
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
import org.codespeak.distribution.client.data.Category;
import org.codespeak.distribution.client.data.ChangelogEntry;
import org.codespeak.distribution.client.handler.DataHandler;
import org.codespeak.distribution.client.data.Dependency;
import org.codespeak.distribution.client.data.FileInfo;
import org.codespeak.distribution.client.data.Program;
import org.codespeak.distribution.client.data.query.QueryTypes;
import org.codespeak.distribution.client.handler.BackendHandler;
import org.codespeak.distribution.client.objects.ProgramTableData;
import org.codespeak.distribution.client.objects.StageController;
import org.codespeak.distribution.client.util.AlertUtil;
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
    
    @FXML private ComboBox<String> categoryChoices;
    @FXML private TableView<ProgramTableData> programsTable;
    @FXML private TableColumn<ProgramTableData, String> programsTableNameColumn;
    @FXML private TableColumn<ProgramTableData, String> programsTableVersionColumn;
    @FXML private TableColumn<ProgramTableData, String> programsTableReleaseTimeColumn;
    @FXML private Label programNameLabel;
    @FXML private Label programDescriptionLabel;
    @FXML private Label programUpdateLabel;
    @FXML private Button launchProgramButton;
    @FXML private Button installButton;
    @FXML private Button updateButton;

    private void selectProgram(int selectedIndex) {
        if (selectedIndex > -1 && selectedIndex != currentlySelectedProgramIndex) {
            currentlySelectedProgramIndex = selectedIndex;
            
            ProgramTableData programData = programsTable.getItems().get(selectedIndex);
            Program selectedProgram = programData.getProgram();
            
            if (selectedProgram.isInstalled()) {
                currentlySelectedProgram = DataHandler.getProgram(selectedProgram.getId(), false);
                currentlySelectedInstalledProgram = selectedProgram;
            } else {
                currentlySelectedProgram = selectedProgram;
                currentlySelectedInstalledProgram = DataHandler.getProgram(selectedProgram.getId(), true);
            }
            
            displayProgramControls(currentlySelectedProgram, currentlySelectedInstalledProgram);
        }
    }

    private void launchInstalledProgram() throws IOException {
            String slug = currentlySelectedInstalledProgram.getSlug();
            String launchFile = currentlySelectedInstalledProgram.getLaunchFile();
            String programLaunchFileLocation = Configuration.PROGRAMS_FOLDER 
                 + File.separator + slug + File.separator + launchFile;
            String command = programLaunchFileLocation;
            
            if (launchFile.endsWith(".jar")) {
                command = "java -jar \"" + command + "\"";
            }
            
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);

    }
    
    private void displayPrograms(Category category) {
        List<Program> programs = DataHandler.getPrograms(category);
        ObservableList items = programsTable.getItems();

        items.clear();
        
        for (Program program : programs) {
            ProgramTableData programData = new ProgramTableData(program, program.getName(), program.getVersion(), program.getReleaseTime().toString());
            items.add(programData);
        }
    }
    
    private void resetProgramControls() {
        programNameLabel.setText("Select A Program");
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
                programUpdateLabel.setText("An update is available!");
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
        programsTableReleaseTimeColumn.setCellValueFactory(new PropertyValueFactory<ProgramTableData, String>("releaseTime"));

        List<Category> categories = BackendHandler.getDataFromQuery(QueryTypes.GET_CATEGORIES);

        for (Category category : categories) {
            DataHandler.addCategory(category, false);
        }
        
        List<Dependency> dependencies = BackendHandler.getDataFromQuery(QueryTypes.GET_DEPENDENCIES);
        
        for (Dependency dependency : dependencies) {
            DataHandler.addDependency(dependency, false);
        }

        List<Program> programs = BackendHandler.getDataFromQuery(QueryTypes.GET_PROGRAMS);

        for (Program program : programs) {
            DataHandler.addProgram(program, false);
        }
        
        ObservableList categoryItems = categoryChoices.getItems();
        
        categoryItems.add("All");
        categoryNamesMap.put("All", null);
        
        for (Category category : categories) {
            String categoryName = category.getName();
            
            categoryItems.add(categoryName);
            categoryNamesMap.put(categoryName, category);
        }
        
        displayPrograms(null);
        
        categoryChoices.getSelectionModel().select("All");
    }    

    /**
     * Called when a program is updated
     * @param installedProgram an installed program
     * @param program the latest information on a program
     * @throws java.io.IOException
     */
    public void onUpdateProgram(Program program, Program installedProgram) throws IOException {
        installedProgram.update(program);
        
        ObservableList<ProgramTableData> programItems = programsTable.getItems();
        ProgramTableData programData = programItems.get(currentlySelectedProgramIndex);
        
        programData.setVersion(installedProgram.getVersion());
        programData.setReleaseTime(installedProgram.getReleaseTime().toString());

        programItems.set(currentlySelectedProgramIndex, programData);
        
        displayProgramControls(program, installedProgram);
    }

    @FXML
    public void onProgramsTableKeyReleased(KeyEvent event) throws IOException {
        KeyCode code = event.getCode();
        
        if (code == KeyCode.UP || code == KeyCode.DOWN) {
            TableViewSelectionModel<ProgramTableData> selectionModel = programsTable.getSelectionModel();
            int selectedIndex = selectionModel.getSelectedIndex();
            
            selectProgram(selectedIndex);
        } else if (code == KeyCode.ENTER) {
            if (currentlySelectedInstalledProgram != null) {
                launchInstalledProgram();
            }
        }
    }
    
    @FXML
    public void onAboutMenuItemClick() throws IOException {
        Stage stage = SceneUtil.getScene(SceneTypes.ABOUT, "About").getStage();
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
            
            helpFile = Configuration.PROGRAMS_FOLDER + File.separator + currentlySelectedInstalledProgram.getSlug()
                              + File.separator + helpFile;
            
            Desktop desktop = Desktop.getDesktop();
            desktop.open(new File(helpFile));
        } else {
            Alert alert = AlertUtil.createAlert("Select an installed program first.");
            alert.show();
        }
    }
    
    @FXML
    public void onViewSourceMenuItemClick() throws Exception {
        if (currentlySelectedInstalledProgram != null) {
            String sourceURL = currentlySelectedInstalledProgram.getSourceURL();
            
            if (StringUtil.isNullOrEmpty(sourceURL)) {
                Alert alert = AlertUtil.createAlert("This program does not have a source repository.");
                alert.show();
                
                return;
            }
            
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI(sourceURL));
        } else {
            Alert alert = AlertUtil.createAlert("Select an installed program first.");
            alert.show();
        }
    }
    
    @FXML
    public void onViewDependenciesMenuItemClick() throws Exception {
        if (currentlySelectedInstalledProgram != null) {
            String programName = currentlySelectedInstalledProgram.getName();
            StageController<ProgramDependenciesSceneController> stageController = SceneUtil.getScene(SceneTypes.PROGRAM_DEPENDENCIES, "Dependencies for " + programName);
            ProgramDependenciesSceneController controller = stageController.getController();
            Stage stage = stageController.getStage();
            
            stage.show();
            controller.showProgramDependencies(currentlySelectedInstalledProgram);
        } else {
            Alert alert = AlertUtil.createAlert("Select an installed program first.");
            alert.show();
        }
    }
    
    @FXML
    public void onOpenFolderMenuItemClick() throws IOException {
        if (currentlySelectedInstalledProgram != null) {
            String programFolder = Configuration.PROGRAMS_FOLDER + File.separator + currentlySelectedInstalledProgram.getSlug();
            Desktop desktop = Desktop.getDesktop();
            
            desktop.open(new File(programFolder));
        } else {
            Alert alert = AlertUtil.createAlert("Select an installed program first.");
            alert.show();
        }
    }

    @FXML
    public void onProgramRepairButtonClick() throws IOException {
        if (currentlySelectedInstalledProgram != null) {
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
                currentlySelectedInstalledProgram.repair();

                Alert alert = AlertUtil.createAlert(programName + " has been repaired.");
                alert.show();
            }
        } else {
            Alert alert = AlertUtil.createAlert("Select an installed program first.");
            alert.show();
        }
    }
    
    @FXML
    public void onProgramViewChangelogButtonClick() throws IOException {
        if (currentlySelectedProgram != null) {
            int id = 0;
            String name = null;
            String version = null;
            boolean installed = false;

            if (currentlySelectedInstalledProgram != null) {
                id = currentlySelectedInstalledProgram.getId();
                name = currentlySelectedInstalledProgram.getName();
                version = currentlySelectedInstalledProgram.getVersion();
                installed = true;
            } else {
                id = currentlySelectedProgram.getId();
                name = currentlySelectedProgram.getName();
                version = currentlySelectedProgram.getVersion();
            }

            String otherPart = "&id=" + id;

            if (installed) {
                otherPart += "&up_to=" + version;
            }
            
            List<ChangelogEntry> entries = BackendHandler.getDataFromQuery(QueryTypes.GET_PROGRAM_CHANGELOG, otherPart);
            StageController<ChangelogSceneController> stageController = SceneUtil.getScene(SceneTypes.CHANGELOG, name + " Changelog");
            ChangelogSceneController controller = stageController.getController();
            Stage stage = stageController.getStage();

            stage.show();
            controller.showChangelog(name, entries);
        } else {
            Alert alert = AlertUtil.createAlert("Select a program first.");
            alert.show();
        }
    }
    
    @FXML
    public void onViewChangelogButtonClick() throws IOException {
        List<ChangelogEntry> entries = BackendHandler.getDataFromQuery(QueryTypes.GET_CLIENT_CHANGELOG, "&up_to=" + Configuration.PROGRAM_VERSION);
        StageController<ChangelogSceneController> stageController = SceneUtil.getScene(SceneTypes.CHANGELOG, Configuration.PROGRAM_NAME + " Changelog");
        ChangelogSceneController controller = stageController.getController();
        Stage stage = stageController.getStage();
        
        stage.show();
        controller.showChangelog(Configuration.PROGRAM_NAME, entries);
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
            DataHandler.installProgram(currentlySelectedProgram);
            
            displayProgramControls(currentlySelectedProgram, currentlySelectedProgram);
            
            currentlySelectedInstalledProgram = currentlySelectedProgram;
            
            String programName = currentlySelectedProgram.getName();
            StageController<ProgramDependenciesSceneController> stageController = SceneUtil.getScene(SceneTypes.PROGRAM_DEPENDENCIES, "Dependencies for " + programName);
            ProgramDependenciesSceneController controller = stageController.getController();
            Stage stage = stageController.getStage();

            stage.show();
            controller.showProgramDependencies(currentlySelectedProgram);
            
        }
    }
    
    @FXML
    public void onUpdateButtonClick() throws IOException {
        if (currentlySelectedInstalledProgram != null) {
            int id = currentlySelectedInstalledProgram.getId();
            String programName = currentlySelectedInstalledProgram.getName();
            String version = currentlySelectedInstalledProgram.getVersion();
            List<ChangelogEntry> entries = BackendHandler.getDataFromQuery(QueryTypes.GET_PROGRAM_CHANGELOG, "&id=" + id + "&since_version=" + version);
            
            StageController<ProgramUpdateSceneController> stageController = SceneUtil.getScene(SceneTypes.PROGRAM_UPDATE, "New version for " + programName);
            ProgramUpdateSceneController controller = stageController.getController();
            Stage stage = stageController.getStage();
            
            stage.show();
            controller.showUpdate(this, currentlySelectedProgram, currentlySelectedInstalledProgram, entries);
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

                displayProgramControls(currentlySelectedProgram, null);
                
                currentlySelectedInstalledProgram = null;
            }
        } else {
            Alert alert = AlertUtil.createAlert("Select an installed program first.");
            alert.show();
        }
    }
    
}
