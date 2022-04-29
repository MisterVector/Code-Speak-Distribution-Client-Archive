package org.codespeak.distribution.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.codespeak.distribution.client.Settings.SettingFields;
import org.codespeak.distribution.client.data.Category;
import org.codespeak.distribution.client.data.Dependency;
import org.codespeak.distribution.client.data.Program;
import org.codespeak.distribution.client.objects.ClientException;
import org.codespeak.distribution.client.data.query.QueryTypes;
import org.codespeak.distribution.client.handler.BackendHandler;
import org.codespeak.distribution.client.handler.DataHandler;
import org.codespeak.distribution.client.objects.StageController;
import org.codespeak.distribution.client.scenes.MainSceneController;
import org.codespeak.distribution.client.scenes.SceneTypes;
import org.codespeak.distribution.client.util.DateUtil;
import org.codespeak.distribution.client.util.SceneUtil;
import org.codespeak.distribution.client.util.StringUtil;
import org.json.JSONObject;

/**
 * The main class
 *
 * @author Vector
 */
public class Main extends Application {
    
    private static ClientException savedException = null;
    private static boolean online = true;
    
    @Override
    public void start(Stage stage) throws Exception {
        String title = Configuration.PROGRAM_NAME;
        
        if (!online) {
            title += " - Currently Offline";
        }
        
        StageController<MainSceneController> stageController = SceneUtil.getScene(SceneTypes.MAIN, title);
        stage = stageController.getStage();
        MainSceneController controller = stageController.getController();
        
        stage.show();
        controller.checkClientUpdate(true);
        
        boolean notifyOfNewPrograms = Configuration.getSettings().getValue(SettingFields.NOTIFY_OF_NEW_PROGRAMS);
        
        if (notifyOfNewPrograms) {
            controller.checkForNewPrograms();
        }
        
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
        } catch (ClientException ex) {
            savedException = ex;
            online = false;
            
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
     * Logs the specified ClientException to file
     * @param ex the exception to log
     */
    public static void logError(ClientException ex) {
        File logsFolder = new File(Configuration.LOGS_FOLDER);
        Instant now = Instant.now();
        String logFileDateFormat = DateUtil.formatInstant(now, "yyyy-MM-dd");
        String errorDateFormat = DateUtil.formatInstant(now, "yyyy-MM-dd hh:mm:ss a");
        String errorLogFile = "error-" + logFileDateFormat + ".log";
        Path logPath = logsFolder.toPath().resolve(errorLogFile);

        String title = ex.getTitle();
        String source = ex.getSource();
        String message = ex.getMessage();
        StackTraceElement[] stackTrace = ex.getStackTrace();
        
        try {
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(logPath.toString(), true)));
            writer.println("Exception time: " + errorDateFormat);
            writer.println();
            writer.println(title);
            
            if (!StringUtil.isNullOrEmpty(source)) {
                writer.println("Source: " + source);                

            }
            writer.println(message);
            writer.println();
            
            for (StackTraceElement elem : stackTrace) {
                writer.println(elem.toString());
            }
            
            writer.println();
            writer.close();
        } catch (IOException ioe) {
            
        }
    }
    
    /**
     * Checks if the client is online
     * @return if the client is online
     */
    public static boolean isOnline() {
        return online;
    }
    
}
