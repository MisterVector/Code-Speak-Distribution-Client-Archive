package org.codespeak.distribution.client.data.query;

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
    
    public QueryException(ErrorType type, String message) {
        super(message);
        
        this.type = type;
    }
    
    /**
     * Gets the query error type from this exception
     * @return query error type from this exception
     */
    public ErrorType getErrorType() {
        return type;
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
