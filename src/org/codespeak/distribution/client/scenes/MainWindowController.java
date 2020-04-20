package org.codespeak.distribution.client.scenes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import org.codespeak.distribution.client.Configuration;
import org.codespeak.distribution.client.data.Category;
import org.codespeak.distribution.client.handler.DataHandler;
import org.codespeak.distribution.client.data.Dependency;
import org.codespeak.distribution.client.data.Program;
import org.codespeak.distribution.client.data.query.InformationListQueryResponse;
import org.codespeak.distribution.client.data.query.QueryResponse;
import org.codespeak.distribution.client.data.query.QueryTypes;
import org.codespeak.distribution.client.handler.BackendHandler;
import org.codespeak.distribution.client.objects.ProgramTableData;
import org.codespeak.distribution.client.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author ComputerDoctor
 */
public class MainWindowController implements Initializable {

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
    @FXML private Button launchProgramButton;
    @FXML private Button viewHelpButton;
    @FXML private Button viewSourceButton;
    @FXML private Button installButton;
    @FXML private Button updateButton;

    private void disableButtons() {
        launchProgramButton.setDisable(true);
        viewHelpButton.setDisable(true);
        viewSourceButton.setDisable(true);
        installButton.setDisable(true);
        updateButton.setDisable(true);
    }
    
    private void displayProgramControls(Program program, Program installedProgram) {
        String name = null;
        String description = null;
        
        disableButtons();
        
        if (installedProgram != null) {
            name = installedProgram.getName();
            description = installedProgram.getDescription();
            Timestamp releaseTime = program.getReleaseTime();
            Timestamp installedReleaseTime = installedProgram.getReleaseTime();
            
            launchProgramButton.setDisable(false);
            
            if (!StringUtil.isNullOrEmpty(installedProgram.getHelpFile())) {
                viewHelpButton.setDisable(false);
            }
            
            if (installedReleaseTime.after(releaseTime)) {
                updateButton.setDisable(false);
            }
        } else {
            name = program.getName();
            description = program.getDescription();
            
            installButton.setDisable(false);
        }
        
        if (!StringUtil.isNullOrEmpty(program.getSourceURL())) {
            viewSourceButton.setDisable(false);
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

        InformationListQueryResponse response = BackendHandler.getQueryResponse(QueryTypes.GET_CATEGORIES);
        JSONArray jsonContents = response.getContents();

        for (int i = 0; i < jsonContents.length(); i++) {
            JSONObject obj = jsonContents.getJSONObject(i);
            Category category = Category.fromJSON(obj);
            
            DataHandler.addCategory(category, false);
        }
        
        response = BackendHandler.getQueryResponse(QueryTypes.GET_DEPENDENCIES);
        jsonContents = response.getContents();
        
        for (int i = 0; i < jsonContents.length(); i++) {
            JSONObject obj = jsonContents.getJSONObject(i);
            Dependency dependency = Dependency.fromJSON(obj);
            
            DataHandler.addDependency(dependency, false);
        }

        response = BackendHandler.getQueryResponse(QueryTypes.GET_PROGRAMS);
        jsonContents = response.getContents();

        for (int i = 0; i < jsonContents.length(); i++) {
            JSONObject obj = jsonContents.getJSONObject(i);
            Program program = Program.fromJSON(obj, false);
            
            DataHandler.addProgram(program, false);
        }
        
        List<Category> categories = DataHandler.getCategories(false);
        ObservableList categoryItems = categoryChoices.getItems();
        
        categoryItems.add("All");
        categoryNamesMap.put("All", null);
        
        for (Category category : categories) {
            String categoryName = category.getName();
            
            categoryItems.add(categoryName);
            categoryNamesMap.put(categoryName, category);
            
        }
        
        List<Program> programs = DataHandler.getPrograms(false);
        ObservableList items = programsTable.getItems();
        
        for (Program program : programs) {
            ProgramTableData programData = new ProgramTableData(program.getId(), program.getName(), program.getVersion(), program.getReleaseTime().toString());
            items.add(programData);
        }
        
        categoryChoices.getSelectionModel().select("All");
    }    
    
    @FXML
    public void onCategorySelect(ActionEvent event) {
        SingleSelectionModel<String> selectionModel = categoryChoices.getSelectionModel();
        int selectedIndex = selectionModel.getSelectedIndex();
        
        if (selectedIndex > -1 && selectedIndex != currentlySelectedCategoryIndex) {
            currentlySelectedCategoryIndex = selectedIndex;
            
            String selectedCategoryName = categoryChoices.getItems().get(selectedIndex);
            Category category = categoryNamesMap.get(selectedCategoryName);
            List<Program> programs = DataHandler.getProgramsByCategory(category, false);
            ObservableList<ProgramTableData> programsList = programsTable.getItems();
            
            programsList.clear();
            
            for (Program program : programs) {
                ProgramTableData programData = new ProgramTableData(program.getId(), program.getName(), program.getVersion(), program.getReleaseTime().toString());
                programsList.add(programData);
            }
            
            currentlySelectedProgramIndex = -1;
            
            programNameLabel.setText("Select A Program");
            programDescriptionLabel.setText("No description. Select a program first.");
            
            disableButtons();
        }
    }
    
    @FXML
    public void onProgramSelect() {
        TableViewSelectionModel<ProgramTableData> selectionModel = programsTable.getSelectionModel();
        int selectedIndex = selectionModel.getSelectedIndex();
        
        if (selectedIndex > -1 && selectedIndex != currentlySelectedProgramIndex) {
            currentlySelectedProgramIndex = selectedIndex;
            
            ProgramTableData programData = programsTable.getItems().get(selectedIndex);
            
            Program program = DataHandler.getProgram(programData.getId(), false);
            Program installedProgram = DataHandler.getProgram(programData.getId(), true);
            
            displayProgramControls(program, installedProgram);
            
            currentlySelectedProgram = program;
            currentlySelectedInstalledProgram = installedProgram;
        }
    }

    @FXML
    public void onLaunchProgramButtonClick() throws IOException {
        if (currentlySelectedInstalledProgram != null) {
            String slug = currentlySelectedInstalledProgram.getSlug();
            String launchFile = currentlySelectedInstalledProgram.getLaunchFile();
            String programLaunchFileLocation = Configuration.PROGRAMS_FOLDER 
                 + File.separator + slug + File.separator + launchFile;

            Runtime runtime = Runtime.getRuntime();
            runtime.exec(programLaunchFileLocation);
        }
    }
    
    @FXML
    public void onInstallButtonClick() throws IOException {
        if (currentlySelectedProgram != null) {
            currentlySelectedProgram.install();
            DataHandler.installProgram(currentlySelectedProgram);
            
            displayProgramControls(currentlySelectedProgram, currentlySelectedProgram);
            
            currentlySelectedInstalledProgram = currentlySelectedProgram;
        }
    }
    
}
