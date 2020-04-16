package org.codespeak.distribution.client.data.query;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A class representing an information list query response
 *
 * @author Vector
 */
public class InformationListQueryResponse extends QueryResponse {
    
    private JSONArray jsonContents;
    
    private InformationListQueryResponse(QueryResponse response, JSONArray jsonContents) {
        super(response.getStatusCode(), response.getErrorType(), response.getErrorMessage());
        this.jsonContents = jsonContents;
    }
    
    /**
     * Gets the JSON contents of this query
     * @return JSON contents of this query
     */
    public JSONArray getContents() {
        return jsonContents;
    }
    
    /**
     * Creates a QueryResponse object from JSON
     * @param json JSON object representing a QueryResponse object
     * @return QueryResponse object from JSON
     */
    public static InformationListQueryResponse fromJSON(JSONObject json) {
        QueryResponse response = QueryResponse.fromJSON(json);
        JSONArray jsonContents = null;
        
        if (json.has("contents")) {
            jsonContents = json.getJSONArray("contents");
        }
        
        return new InformationListQueryResponse(response, jsonContents);
    }
    
}
