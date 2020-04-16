package org.codespeak.distribution.client.scenes;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.codespeak.distribution.client.data.Category;
import org.codespeak.distribution.client.data.DataManager;
import org.codespeak.distribution.client.data.Dependency;
import org.codespeak.distribution.client.data.Program;
import org.codespeak.distribution.client.data.query.InformationListQueryResponse;
import org.codespeak.distribution.client.data.query.QueryResponse;
import org.codespeak.distribution.client.data.query.QueryTypes;
import org.codespeak.distribution.client.objects.ProgramTableData;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author ComputerDoctor
 */
public class MainWindowController implements Initializable {

    @FXML private TableView<ProgramTableData> programsTable;
    @FXML private TableColumn<ProgramTableData, String> programsTableNameColumn;
    @FXML private TableColumn<ProgramTableData, String> programsTableVersionColumn;
    @FXML private TableColumn<ProgramTableData, String> programsTableReleaseTimeColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        programsTable.setEditable(false);
        
        programsTableNameColumn.setCellValueFactory(new PropertyValueFactory<ProgramTableData, String>("name"));
        programsTableVersionColumn.setCellValueFactory(new PropertyValueFactory<ProgramTableData, String>("version"));
        programsTableReleaseTimeColumn.setCellValueFactory(new PropertyValueFactory<ProgramTableData, String>("releaseTime"));

        InformationListQueryResponse response = QueryTypes.getQueryResponse(QueryTypes.GET_CATEGORIES);
        JSONArray jsonContents = response.getContents();

        for (int i = 0; i < jsonContents.length(); i++) {
            JSONObject obj = jsonContents.getJSONObject(i);
            Category category = Category.fromJSON(obj);
            
            DataManager.addCategory(category, false);
        }
        
        response = QueryTypes.getQueryResponse(QueryTypes.GET_DEPENDENCIES);
        jsonContents = response.getContents();
        
        for (int i = 0; i < jsonContents.length(); i++) {
            JSONObject obj = jsonContents.getJSONObject(i);
            Dependency dependency = Dependency.fromJSON(obj);
            
            DataManager.addDependency(dependency, false);
        }

        response = QueryTypes.getQueryResponse(QueryTypes.GET_PROGRAMS);
        jsonContents = response.getContents();

        for (int i = 0; i < jsonContents.length(); i++) {
            JSONObject obj = jsonContents.getJSONObject(i);
            Program program = Program.fromJSON(obj, false);
            
            DataManager.addProgram(program, false);
        }
    }    
    
}
