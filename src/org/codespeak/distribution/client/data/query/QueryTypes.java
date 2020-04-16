package org.codespeak.distribution.client.data.query;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import org.codespeak.distribution.client.Configuration;
import org.json.JSONObject;

/**
 * An enum containing all query types valid for this client
 *
 * @author Vector
 */
public enum QueryTypes {
    
    GET_DEPENDENCY("get_dependency"),
    GET_DEPENDENCIES("get_dependencies", true),
    GET_CATEGORY("get_category"),
    GET_CATEGORIES("get_categories", true),
    GET_PROGRAM("get_program"),
    GET_PROGRAMS("get_programs", true),
    GET_PROGRAM_FILES("get_program_files", true),
    CHECK_PROGRAM_VERSION("check_program_version"),
    GET_PROGRAM_CHANGELOG("get_program_changelog"),
    GET_CLIENT_FILES("get_client_files", true),
    CHECK_CLIENT_VERSION("check_client_version"),
    GET_CLIENT_CHANGELOG("get_client_changelog");
    
    private final String queryName;
    private final boolean informationListQuery;
    
    private QueryTypes(String queryName) {
        this(queryName, false);
    }
    
    private QueryTypes(String queryName, boolean informationListQuery) {
        this.queryName = queryName;
        this.informationListQuery = informationListQuery;
    }
    
    /**
     * Gets the name of this query
     * @return name of this query
     */
    public String getQueryName() {
        return queryName;
    }

    /**
     * Gets whether this query is an information list query
     * @return whether this query is an information list query
     */
    public boolean isInformationListQuery() {
        return informationListQuery;
    }
    
    /**
     * Queries the backend and gets a response
     * @param <T> An object that extends QueryResponse
     * @param queryType the type of query to make
     * @return a QueryResponse object containing the response of the query
     */
    public static <T extends QueryResponse> T getQueryResponse(QueryTypes queryType) {
        return getQueryResponse(queryType, "");
    }
    
    /**
     * Queries the backend and gets a response
     * @param <T> An object that extends QueryResponse
     * @param queryType the type of query to make
     * @param otherPart an additional part of the query
     * @return a QueryResponse object containing the response of the query
     */
    public static <T extends QueryResponse> T getQueryResponse(QueryTypes queryType, String otherPart) {
        try {
            URL url = new URL(Configuration.BACKEND_URL + "?query=" + queryType.getQueryName() + otherPart);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder sb = new StringBuilder();
            String input;
            
            while ((input = reader.readLine()) != null) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                
                sb.append(input);
            }
            
            String response = sb.toString();
            JSONObject json = new JSONObject(response);

            System.out.println(json.toString(4));
            
            if (queryType.isInformationListQuery()) {
                return (T) InformationListQueryResponse.fromJSON(json);
            } else {
                return (T) InformationQueryResponse.fromJSON(json);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            
            return null;
        }
    }
    
}
