package org.codespeak.distribution.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.codespeak.distribution.client.data.Category;
import org.codespeak.distribution.client.data.Dependency;
import org.codespeak.distribution.client.data.Program;
import org.codespeak.distribution.client.data.query.QueryTypes;
import org.codespeak.distribution.client.handler.BackendHandler;
import org.codespeak.distribution.client.handler.DataHandler;
import org.codespeak.distribution.client.scenes.SceneTypes;
import org.codespeak.distribution.client.util.SceneUtil;
import org.json.JSONObject;

/**
 *
 * @author Vector
 */
public class DistributionClient extends Application {
    
    private static DistributionClient instance;
    
    public DistributionClient() {
        instance = this;
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        stage = SceneUtil.getScene(SceneTypes.MAIN, Configuration.PROGRAM_TITLE).getStage();
        stage.show();
    }

    @Override
    public void stop() throws FileNotFoundException {
        JSONObject json = DataHandler.exportDataToJSON();
        File dataFile = new File(Configuration.DATA_FILE);
        
        if (dataFile.exists()) {
            dataFile.delete();
        }
        
        PrintWriter writer = new PrintWriter(dataFile);
        writer.write(json.toString(4));
        writer.close();
        
        Configuration.writeSettingsToFile();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
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
        
        File programsFolder = new File(Configuration.PROGRAMS_FOLDER);
        File dataFile = new File(Configuration.DATA_FILE);
        
        if (!programsFolder.exists()) {
            programsFolder.mkdir();
        }
        
        if (dataFile.exists()) {
            byte[] bytes = Files.readAllBytes(dataFile.toPath());
            String jsonString = new String(bytes);
            JSONObject json = new JSONObject(jsonString);
            
            DataHandler.importDataFromJSON(json);
        }
        
        launch(args);
    }
    
    /**
     * Gets an instance of this class
     * @return instance of this class
     */
    public static DistributionClient getInstance() {
        return instance;
    }
    
}
