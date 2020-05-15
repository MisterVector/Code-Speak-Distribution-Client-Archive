package org.codespeak.distribution.client.data.query;

import org.codespeak.distribution.client.data.Category;
import org.codespeak.distribution.client.data.ChangelogEntry;
import org.codespeak.distribution.client.data.ClientCheckVersionResponse;
import org.codespeak.distribution.client.data.Dependency;
import org.codespeak.distribution.client.data.FileInfo;
import org.codespeak.distribution.client.data.Program;

/**
 * An enum containing all query types used by this client
 *
 * @author Vector
 */
public enum QueryTypes {
    
    GET_DEPENDENCIES("get_dependencies", "Getting Dependencies", Dependency.class, true),
    GET_CATEGORIES("get_categories", "Getting Categories", Category.class, true),
    GET_PROGRAMS("get_programs", "Getting Programs", Program.class, true),
    GET_PROGRAM_FILES("get_program_files", "Getting Program Files", FileInfo.class, true),
    GET_PROGRAM_CHANGELOG("get_program_changelog", "Getting Program Changelog", ChangelogEntry.class, true),
    CHECK_CLIENT_VERSION("check_client_version", "Checking Client Version", ClientCheckVersionResponse.class),
    GET_CLIENT_CHANGELOG("get_client_changelog", "Getting Client Changelog", ChangelogEntry.class, true);
    
    private final String name;
    private final String title;
    private final Class dataClazz;
    private final boolean listQuery;
    
    private QueryTypes(String name, String title, Class dataClazz) {
        this(name, title, dataClazz, false);
    }
    
    private QueryTypes(String name, String title, Class dataClazz, boolean informationListQuery) {
        this.name = name;
        this.title = title;
        this.dataClazz = dataClazz;
        this.listQuery = informationListQuery;
    }
    
    /**
     * Gets the name of this query
     * @return name of this query
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the title of this query
     * @return title of this query
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Gets the class representing the kind of data returned with this query
     * @return class representing the kind of data in query response
     */
    public Class getDataClass() {
        return dataClazz;
    }
    
    /**
     * Gets whether the data of this query is a list of items
     * @return whether the data of this query is a list of items
     */
    public boolean isListQuery() {
        return listQuery;
    }
    
}
