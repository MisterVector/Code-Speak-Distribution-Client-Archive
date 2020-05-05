package org.codespeak.distribution.client.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import org.codespeak.distribution.client.Configuration;
import org.codespeak.distribution.client.data.Category;
import org.codespeak.distribution.client.data.ChangelogEntry;
import org.codespeak.distribution.client.data.CheckVersionResponse;
import org.codespeak.distribution.client.data.ClientCheckVersionResponse;
import org.codespeak.distribution.client.data.Dependency;
import org.codespeak.distribution.client.data.FileInfo;
import org.codespeak.distribution.client.data.Program;
import org.codespeak.distribution.client.data.query.QueryTypes;
import org.json.JSONArray;
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
    public static <T> T getDataFromQuery(QueryTypes queryType) {
        return BackendHandler.getDataFromQuery(queryType, "");
    }
    
    /**
     * Gets data from the backend using the specified query
     * @param <T> A generic object representing the data from the query
     * @param queryType the type of query to make
     * @param otherPart an additional part of the query
     * @return a generic object representing the data from the query
     */
    public static <T> T getDataFromQuery(QueryTypes queryType, String otherPart) {
        URL url = null;
        
        try {
            url = new URL(Configuration.BACKEND_URL + "?query=" + queryType.getQueryName() + otherPart);
        } catch (MalformedURLException ex) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
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

                    if (dataClass == Dependency.class) {
                        return (T) Dependency.fromJSON(jsonContents);
                    } else if (dataClass == Category.class) {
                        return (T) Category.fromJSON(jsonContents);
                    } else if (dataClass == Program.class) {
                        return (T) Program.fromJSON(jsonContents, false);
                    } else if (dataClass == CheckVersionResponse.class) {
                        return (T) CheckVersionResponse.fromJSON(jsonContents);
                    } else if (dataClass == ClientCheckVersionResponse.class) {
                        return (T) ClientCheckVersionResponse.fromJSON(jsonContents);
                    }
                }
            }
        } catch (IOException ex) {

        }
        
        return null;
    }

    /**
     * Gets a readable byte channel of a remote client file
     * @param relativeFilePath relative file path of remote client file
     * @return a readable byte channel of the remote client file given
     * @throws IOException if an error occurred while getting remote file channel
     */
    public static ReadableByteChannel getRemoteFileChannel(String relativeFilePath) throws IOException {
        return getRemoteFileChannel(-1, relativeFilePath);
    }
    
    /**
     * Gets a readable byte channel of a remote client or program file
     * @param id if greater than -1, represents program ID, else client
     * @param relativeFilePath path to the program file
     * @return readable byte channel of the specified program file
     * @throws IOException if an error occurred while getting remote file channel
     */
    public static ReadableByteChannel getRemoteFileChannel(int id, String relativeFilePath) throws IOException {
        String remotePath = Configuration.DISTRIBUTION_URL;
        
        if (id > -1) {
            remotePath += "/files/" + id;
        } else {
            remotePath += "/client";
        }
        
        remotePath += "/" + relativeFilePath;
        
        URL url = new URL(remotePath);
        
        return Channels.newChannel(url.openStream());
    }

    /**
     * Gets a file channel from a URL
     * @param remoteURL
     * @return Readable Byte Channel from the requested URL
     * @throws IOException if an error occurs
     */
    public static ReadableByteChannel getRemoteFileChannelFromURL(String remoteURL) throws IOException {
        URL url = new URL(remoteURL);
        return Channels.newChannel(url.openStream());
    }
    
}
