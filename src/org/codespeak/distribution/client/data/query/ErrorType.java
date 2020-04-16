package org.codespeak.distribution.client.data.query;

/**
 * An enum containing query error types
 *
 * @author Vector
 */
public enum ErrorType {

    ERROR_SEVERE(1),
    ERROR_WARNING(2),
    NONE(-1);

    private final int errorTypeCode;

    private ErrorType(int errorTypeCode) {
        this.errorTypeCode = errorTypeCode;
    }

    /**
     * Gets the code of this error
     * @return 
     */
    public int getErrorTypeCode() {
        return errorTypeCode;
    }

    /**
     * Gets an ErrorType object from code
     * @param code the code for the ErrorType
     * @return an ErrorType object
     */
    public static ErrorType fromCode(int code) {
        for (ErrorType et : values()) {
            if (et.getErrorTypeCode() == code) {
                return et;
            }
        }

        return NONE;
    }

}
