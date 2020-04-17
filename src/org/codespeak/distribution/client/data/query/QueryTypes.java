package org.codespeak.distribution.client.data.query;

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
    
}
