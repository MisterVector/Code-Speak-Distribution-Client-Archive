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
    private final String title;
    private final String source;
    
    public ClientException(ErrorType type, String title, Exception ex) {
        this(type, title, "", ex);
    }
    
    public ClientException(ErrorType type, String title, String source, Exception ex) {
        super(ex);
        
        this.type = type;
        this.title = title;
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
     * Gets the title of this exception
     * @return title of this exception
     */
    public String getTitle() {
        return title;
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
        String message = title + "\n\n" + super.getMessage();
        
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

    /**
     * Converts an exception to a ClientException
     * @param ex the exception to convert
     * @return an ClientException converted from an exception
     */
    public static ClientException fromException(Exception ex) {
        if (ex instanceof ClientException) {
            return (ClientException) ex;
        }
        
        return new ClientException(ErrorType.ERROR_SEVERE, "An exception has occurred.", ex);
    }

}
