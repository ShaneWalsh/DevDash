package dev.dash.execute;

import dev.dash.model.builder.QueryBuilderData;

public interface QueryBuilderService {
    
    /**
     * Import schema configs, will update any existing elements. Missing references will be ignored.
     * Secured by user type role Configurator
     * @param QueryBuilderData
     */
    public boolean importConfig( QueryBuilderData queryBuilderData );

    /**
     * Export the configs passed in the array
     * Secured by user type role Configurator
     * @param schemaConfigs list of schemas to export.
     */
    public String exportConfig( String[] schemaConfigs );

    /**
     * Export all schemas related to the provided Dashboard.
     * Secured by user type role Configurator
     * @param dashboardCode dashboard to export all schemas for
     */
    public String exportConfigByDashboard( String dashboardCode );

}
