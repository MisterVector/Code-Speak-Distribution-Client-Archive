package org.codespeak.distribution.client.data.query;

import org.codespeak.distribution.client.data.Category;
import org.codespeak.distribution.client.data.ChangelogEntry;
import org.codespeak.distribution.client.data.CheckVersionResponse;
import org.codespeak.distribution.client.data.ClientCheckVersionResponse;
import org.codespeak.distribution.client.data.Dependency;
import org.codespeak.distribution.client.data.FileInfo;
import org.codespeak.distribution.client.data.Program;

/**
 * An enum containing all query types valid for this client
 *
 * @author Vector
 */
public enum QueryTypes {
    
    GET_DEPENDENCY("get_dependency", Dependency.class),
    GET_DEPENDENCIES("get_dependencies", Dependency.class, true),
    GET_CATEGORY("get_category", Category.class),
    GET_CATEGORIES("get_categories", Category.class, true),
    GET_PROGRAM("get_program", Program.class),
    GET_PROGRAMS("get_programs", Program.class, true),
    GET_PROGRAM_FILES("get_program_files", FileInfo.class, true),
    CHECK_PROGRAM_VERSION("check_program_version", CheckVersionResponse.class),
    GET_PROGRAM_CHANGELOG("get_program_changelog", ChangelogEntry.class, true),
    GET_CLIENT_FILES("get_client_files", FileInfo.class, true),
    CHECK_CLIENT_VERSION("check_client_version", ClientCheckVersionResponse.class),
    GET_CLIENT_CHANGELOG("get_client_changelog", ChangelogEntry.class, true);
    
    private final String queryName;
    private final Class dataClazz;
    private final boolean listQuery;
    
    private QueryTypes(String queryName, Class dataClazz) {
        this(queryName, dataClazz, false);
    }
    
    private QueryTypes(String queryName, Class dataClazz, boolean informationListQuery) {
        this.queryName = queryName;
        this.dataClazz = dataClazz;
        this.listQuery = informationListQuery;
    }
    
    /**
     * Gets the name of this query
     * @return name of this query
     */
    public String getQueryName() {
        return queryName;
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
