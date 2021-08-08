package org.codespeak.distribution.client.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import org.codespeak.distribution.client.Configuration;
import org.codespeak.distribution.client.data.Category;
import org.codespeak.distribution.client.data.ChangelogEntry;
import org.codespeak.distribution.client.data.ClientCheckVersionResponse;
import org.codespeak.distribution.client.data.Dependency;
import org.codespeak.distribution.client.data.FileInfo;
import org.codespeak.distribution.client.data.Program;
import org.codespeak.distribution.client.data.query.ErrorType;
import org.codespeak.distribution.client.objects.ClientException;
import org.codespeak.distribution.client.data.query.QueryTypes;
import org.codespeak.distribution.client.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONException;
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
     * @throws org.codespeak.distribution.client.objects.ClientException if
     * there is an error during the query
     */
    public static <T> T getDataFromQuery(QueryTypes queryType) throws ClientException {
        return BackendHandler.getDataFromQuery(queryType, "");
    }
    
    /**
     * Gets data from the backend using the specified query
     * @param <T> A generic object representing the data from the query
     * @param queryType the type of query to make
     * @param otherPart an additional part of the query
     * @return a generic object representing the data from the query
     * @throws org.codespeak.distribution.client.objects.ClientException if
     * there is an error during the query
     */
    public static <T> T getDataFromQuery(QueryTypes queryType, String otherPart) throws ClientException {
        String fullQuery = Configuration.BACKEND_URL + "?query=" + queryType.getName() + otherPart;
        URL url = null;
        HttpsURLConnection connection = null;
        
        String title =  "An error occurred while performing query: " + queryType.getTitle() + ".";
        ErrorType type = ErrorType.ERROR_SEVERE;

        try {
            url = new URL(fullQuery);
            connection = (HttpsURLConnection) url.openConnection();  
            connection.setRequestProperty("User-Agent", "CodeSpeakDistributionClient/" + Configuration.PROGRAM_VERSION);
        } catch (IOException ex) {
            throw new ClientException(type, title, fullQuery, ex);
        }
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String input;
            
            while ((input = reader.readLine()) != null) {
                sb.append(input);
            }
            
            String response = sb.toString();
            JSONObject json = new JSONObject(response);
            int statusCode = json.getInt("status");
            
            if (statusCode == 1) {
                Class dataClass = queryType.getDataClass();
                
                if (queryType.isListQuery()) {
                    JSONArray jsonContents = json.getJSONArray("contents");
                    List<T> listData = new ArrayList<T>();
                    
                    for (int i = 0; i < jsonContents.length(); i++) {
                        JSONObject obj = jsonContents.getJSONObject(i);
                        
                        if (dataClass == Dependency.class) {
                            listData.add((T) Dependency.fromJSON(obj));
                        } else if (dataClass == Category.class) {
                            listData.add((T) Category.fromJSON(obj));
                        } else if (dataClass == Program.class) {
                            listData.add((T) Program.fromJSON(obj, false));
                        } else if (dataClass == FileInfo.class) {
                            listData.add((T) FileInfo.fromJSON(obj));
                        } else if (dataClass == ChangelogEntry.class) {
                            listData.add((T) ChangelogEntry.fromJSON(obj));
                        }
                    }
                    
                    return (T) listData;
                } else {
                    JSONObject jsonContents = json.getJSONObject("contents");
                    Object obj = null;

                    if (dataClass == Dependency.class) {
                        obj = Dependency.fromJSON(jsonContents);
                    } else if (dataClass == Category.class) {
                        obj = Category.fromJSON(jsonContents);
                    } else if (dataClass == Program.class) {
                        obj = Program.fromJSON(jsonContents, false);
                    } else if (dataClass == ClientCheckVersionResponse.class) {
                        obj = ClientCheckVersionResponse.fromJSON(jsonContents);
                    }
                    
                    return (T) obj;
                }
            } else {
                type = ErrorType.fromCode(json.getInt("error_code"));
                String errorMessage = json.getString("error_message");
                throw new ClientException(type, title, fullQuery, new Exception(errorMessage));
            }
        } catch (IOException | JSONException ex) {
            throw new ClientException(type, title, fullQuery, ex);
        }
    }

    /**
     * Gets a readable byte channel of a remote client file
     * @param relativeFilePath relative file path of remote client file
     * @return a readable byte channel of the remote client file given
     * @throws org.codespeak.distribution.client.objects.ClientException if an
     * error occurred while getting remote file channel
     */
    public static ReadableByteChannel getRemoteFileChannel(String relativeFilePath) throws ClientException {
        return getRemoteFileChannel("", relativeFilePath);
    }
    
    /**
     * Gets a readable byte channel of a remote client or program file
     * @param slug the slug of a program
     * @param relativeFilePath path to the program file
     * @return readable byte channel of the specified program file
     * @throws org.codespeak.distribution.client.objects.ClientException if an
     * error occurred while getting remote file channel
     */
    public static ReadableByteChannel getRemoteFileChannel(String slug, String relativeFilePath) throws ClientException {
        String remotePath = Configuration.DISTRIBUTION_URL;
        
        if (!StringUtil.isNullOrEmpty(slug)) {
            remotePath += "/files/" + slug;
        } else {
            remotePath += "/client";
        }
        
        remotePath += "/" + relativeFilePath;
        
        return getRemoteFileChannelFromURL(remotePath);
    }

    /**
     * Gets a file channel from a URL
     * @param remoteURL URL of the file
     * @return Readable Byte Channel from the requested URL
     * @throws org.codespeak.distribution.client.objects.ClientException if an
     * error occurred while getting remote file channel
     */
    public static ReadableByteChannel getRemoteFileChannelFromURL(String remoteURL) throws ClientException {
        String title =  "An exception occurred while fetching a remote file.";
        ErrorType type = ErrorType.ERROR_SEVERE;
        remoteURL = remoteURL.replace(" ", "%20");

        try {
            URL url = new URL(remoteURL);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();  
            connection.setRequestProperty("User-Agent", "CodeSpeakDistributionClient/" + Configuration.PROGRAM_VERSION);
            
            return Channels.newChannel(connection.getInputStream());
        } catch (IOException ex) {
            throw new ClientException(type, title, remoteURL, ex);
        }
    }
    
}
