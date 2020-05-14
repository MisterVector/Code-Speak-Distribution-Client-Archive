package org.codespeak.distribution.client.data.query;

/**
 * An enum containing various error types
 *
 * @author Vector
 */
public enum ErrorType {

    ERROR_CRITICAL(0),
    
    // These errors come from the backend
    ERROR_SEVERE(1),
    ERROR_WARNING(2),
    
    NONE(-1);

    private final int errorTypeCode;

    private ErrorType(int errorTypeCode) {
        this.errorTypeCode = errorTypeCode;
    }

    /**
     * Gets the code of this error
     * @return code of this error
     */
    public int getCode() {
        return errorTypeCode;
    }

    /**
     * Gets an ErrorType object from code
     * @param code the code for the ErrorType
     * @return an ErrorType object
     */
    public static ErrorType fromCode(int code) {
        for (ErrorType et : values()) {
            if (et.getCode() == code) {
                return et;
            }
        }

        return NONE;
    }

}
