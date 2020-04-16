package org.codespeak.distribution.client.data.query;

import org.json.JSONObject;

/**
 * A class representing a query response
 *
 * @author Vector
 */
public class QueryResponse {

    private final int statusCode;
    private final ErrorType errorType;
    private final String errorMessage;
    
    protected QueryResponse(int statusCode, ErrorType errorType, String errorMessage) {
        this.statusCode = statusCode;
        this.errorType = errorType;
        this.errorMessage = errorMessage;
    }
    
    /**
     * Gets the status code of this query response
     * @return status code of this query response
     */
    public int getStatusCode() {
        return statusCode;
    }
    
    /**
     * Checks whether this query response is an error
     * @return whether this query response is an error
     */
    public boolean isError() {
        return statusCode == 0;
    }
    
    /**
     * Gets the error type of this query
     * @return error type of this query
     */
    public ErrorType getErrorType() {
        return errorType;
    }
    
    /**
     * Gets the error message of this query
     * @return error message of this query
     */
    public String getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * Creates a QueryResponse object from JSON
     * @param json JSON object representing a QueryResponse object
     * @return QueryResponse object from JSON
     */
    public static QueryResponse fromJSON(JSONObject json) {
        int statusCode = 0;
        ErrorType errorType = ErrorType.NONE;
        String errorMessage = "";
        
        if (json.has("status")) {
            statusCode = json.getInt("status");
        }
        
        if (json.has("error_code")) {
            errorType = ErrorType.fromCode(json.getInt("error_code"));
        }
        
        if (json.has("error_message")) {
            errorMessage = json.getString("error_message");
        }
        
        return new QueryResponse(statusCode, errorType, errorMessage);
    }
    
}
