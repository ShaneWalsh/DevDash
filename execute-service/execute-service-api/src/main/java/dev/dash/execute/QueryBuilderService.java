package dev.dash.execute;

import dev.dash.model.builder.QueryBuilderData;

public interface QueryBuilderService {
    
    /**
     * Import schema configs, will update any existing elements. Missing references will be ignored.
     * Secured by user type role Configurator
     * @param QueryBuilderData
     * @return
     */
    public boolean importConfig( QueryBuilderData queryBuilderData );

    /**
     * Export the configs passed in the array
     * Secured by user type role Configurator
     * @param QueryBuilderData
     * @return
     */
    public String exportConfig( String[] schemaConfigs );

}
