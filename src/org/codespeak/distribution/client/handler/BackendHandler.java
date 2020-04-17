package org.codespeak.distribution.client.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import org.codespeak.distribution.client.Configuration;
import org.codespeak.distribution.client.data.query.InformationListQueryResponse;
import org.codespeak.distribution.client.data.query.InformationQueryResponse;
import org.codespeak.distribution.client.data.query.QueryResponse;
import org.codespeak.distribution.client.data.query.QueryTypes;
import org.json.JSONObject;

/**
 * A class that handles interactions with the backend
 *
 * @author Vector
 */
public class BackendHandler {

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

            if (queryType.isInformationListQuery()) {
                return (T) InformationListQueryResponse.fromJSON(json);
            } else {
                return (T) InformationQueryResponse.fromJSON(json);
            }
        } catch (IOException ex) {
            return null;
        }
    }
    
}
