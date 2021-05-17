package org.codespeak.distribution.client.util;

import javafx.scene.control.Alert;
import org.codespeak.distribution.client.Configuration;

/**
 * A utility class for creating alerts
 *
 * @author Vector
 */
public class AlertUtil {
    
    /**
     * Creates an information type alert
     * @param message the message for the alert
     * @return the alert with the specified settings
     */
    public static Alert createAlert(String message) {
        return createAlert(message, null);
    }
    
    /**
     * Creates an information type alert
     * @param message the message for the alert
     * @param headerText the header text
     * @return the alert with the specified settings
     */
    public static Alert createAlert(String message, String headerText) {
        return createAlert(message, headerText, Configuration.PROGRAM_NAME);
    }

    /**
     * Creates an information type alert
     * @param message the message for the alert
     * @param headerText the header text
     * @param title the title of the alert
     * @return the alert with the specified settings
     */
    public static Alert createAlert(String message, String headerText, String title) {
        return createAlert(Alert.AlertType.INFORMATION, message, headerText, title);
    }
    
    /**
     * Creates an alert of the given type
     * @param type the type of alert
     * @param message the message for the alert
     * @return the alert with the specified settings
     */
    public static Alert createAlert(Alert.AlertType type, String message) {
        return createAlert(type, message, null);
    }
    
    /**
     * Creates an alert of the given type
     * @param type the type of alert
     * @param message the message for the alert
     * @param headerText the header of the alert
     * @return the alert with the specified settings
     */
    public static Alert createAlert(Alert.AlertType type, String message, String headerText) {
        return createAlert(type, message, headerText, Configuration.PROGRAM_NAME);
    }
    
    /**
     * Creates an alert of the given type
     * @param type the type of alert
     * @param message the message for the alert
     * @param headerText the header of the alert
     * @param title the title of the alert
     * @return the alert with the specified settings
     */
    public static Alert createAlert(Alert.AlertType type, String message, String headerText, String title) {
        Alert alert = new Alert(type);
        
        alert.setContentText(message);
        alert.setHeaderText(headerText);
        alert.setTitle(title);
        
        return alert;
    }

}
