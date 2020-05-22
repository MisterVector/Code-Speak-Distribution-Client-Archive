package org.codespeak.distribution.client.objects;

import java.util.logging.Level;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.codespeak.distribution.client.data.query.ErrorType;
import org.codespeak.distribution.client.util.AlertUtil;

/**
 * An exception that is thrown when the client has an error
 *
 * @author Vector
 */
public class ClientException extends Exception {
    
    private final ErrorType type;
    private final String source;
    
    public ClientException(ErrorType type, String message) {
        this(type, "", message);
    }
    
    public ClientException(ErrorType type, String source, String message) {
        super(message);
        
        this.type = type;
        this.source = source;
    }
    
    /**
     * Gets the error type from this exception
     * @return error type from this exception
     */
    public ErrorType getErrorType() {
        return type;
    }

    /**
     * Gets the source associated with this exception
     * @return source associated with this exception
     */
    public String getSource() {
        return source;
    }
    
    /**
     * Gets the log level of this exception
     * @return log level of this exception
     */
    public Level getLogLevel() {
        switch (type) {
            case ERROR_CRITICAL:
            case ERROR_SEVERE:
                return Level.SEVERE;
            case ERROR_WARNING:
                return Level.WARNING;
            default:
                return Level.INFO;
        }
    }
    
    /**
     * Builds an alert from this exception
     * @return alert from this exception
     */
    public Alert buildAlert() {
        AlertType alertType = AlertType.INFORMATION;
        String message = super.getMessage();
        
        switch (type) {
            case ERROR_CRITICAL:
            case ERROR_SEVERE:
                alertType = AlertType.ERROR;
                
                break;
            case ERROR_WARNING:
                alertType = AlertType.WARNING;
                
                break;
        }

        return AlertUtil.createAlert(alertType, message, "Program Error");
    }

}
