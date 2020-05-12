package org.codespeak.distribution.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.codespeak.distribution.client.data.Category;
import org.codespeak.distribution.client.data.Dependency;
import org.codespeak.distribution.client.data.Program;
import org.codespeak.distribution.client.data.query.QueryException;
import org.codespeak.distribution.client.data.query.QueryTypes;
import org.codespeak.distribution.client.handler.BackendHandler;
import org.codespeak.distribution.client.handler.DataHandler;
import org.codespeak.distribution.client.scenes.SceneTypes;
import org.codespeak.distribution.client.util.MiscUtil;
import org.codespeak.distribution.client.util.SceneUtil;
import org.json.JSONObject;

/**
 *
 * @author Vector
 */
public class DistributionClient extends Application {
    
    private static QueryException savedException = null;
    private static DistributionClient instance;
    
    public DistributionClient() {
        instance = this;
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        stage = SceneUtil.getScene(SceneTypes.MAIN, Configuration.PROGRAM_TITLE).getStage();
        stage.show();
        
        if (savedException != null) {
            Alert alert = savedException.buildAlert();
            alert.show();
        }
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
        try {
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
        } catch (QueryException ex) {
            savedException = ex;
            
            logError(ex);
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

    /**
     * Gets the logger
     * @param ex
     */
    public static void logError(QueryException ex) {
        File logsFolder = new File(Configuration.LOGS_FOLDER);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        String logFileDateFormat = MiscUtil.formatTimestamp(now, "yyyy-MM-dd");
        String errorDateFormat = MiscUtil.formatTimestamp(now);
        String errorLogFile = "error-" + logFileDateFormat + ".log";
        Path logPath = logsFolder.toPath().resolve(errorLogFile);
        
        String message = ex.getMessage();
        StackTraceElement[] stackTrace = ex.getStackTrace();
        
        try {
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(logPath.toString(), true)));
            writer.println("Exception time: " + errorDateFormat);
            writer.println(message);
            writer.println("Query used: " + ex.getQuery());
            writer.println();
            
            for (StackTraceElement elem : stackTrace) {
                writer.println(elem.toString());
            }
            
            writer.println();
            writer.close();
        } catch (IOException ioe) {
            
        }
    }
    
}
