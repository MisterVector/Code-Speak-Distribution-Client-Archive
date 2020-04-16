package org.codespeak.distribution.client.data.query;

import org.json.JSONObject;

/**
 * A class representing an information query response
 *
 * @author Vector
 */
public class InformationQueryResponse extends QueryResponse {

    private final JSONObject jsonContents;
    
    private InformationQueryResponse(QueryResponse response, JSONObject jsonContents) {
        super(response.getStatusCode(), response.getErrorType(), response.getErrorMessage());

        this.jsonContents = jsonContents;
    }
    
    /**
     * Gets the JSON contents of this query
     * @return JSON contents of this query
     */
    public JSONObject getContents() {
        return jsonContents;
    }
    
    /**
     * Creates a QueryResponse object from JSON
     * @param json JSON object representing a QueryResponse object
     * @return QueryResponse object from JSON
     */
    public static InformationQueryResponse fromJSON(JSONObject json) {
        QueryResponse response = QueryResponse.fromJSON(json);
        JSONObject jsonContents = null;
        
        if (json.has("contents")) {
            jsonContents = json.getJSONObject("contents");
        }
        
        return new InformationQueryResponse(response, jsonContents);
    }
    
}
