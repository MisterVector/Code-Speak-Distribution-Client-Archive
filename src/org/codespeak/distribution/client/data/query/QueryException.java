package org.codespeak.distribution.client.data.query;

import java.util.logging.Level;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.codespeak.distribution.client.util.AlertUtil;

/**
 * An exception that is thrown when a query has an error
 *
 * @author Vector
 */
public class QueryException extends Exception {
    
    private final ErrorType type;
    private final String query;
    
    public QueryException(ErrorType type, String query, String message) {
        super(message);
        
        this.type = type;
        this.query = query;
    }
    
    /**
     * Gets the query error type from this exception
     * @return query error type from this exception
     */
    public ErrorType getErrorType() {
        return type;
    }

    /**
     * Gets the query associated with this exception
     * @return query associated with this exception
     */
    public String getQuery() {
        return query;
    }
    
    /**
     * Gets the log level of this query exception
     * @return log level of this query exception
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

        return AlertUtil.createAlert(alertType, message, "Query Error");
    }
}
