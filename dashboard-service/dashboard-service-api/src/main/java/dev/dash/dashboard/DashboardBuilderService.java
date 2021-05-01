package dev.dash.dashboard;

import dev.dash.model.builder.DashboardBuilderData;

public interface DashboardBuilderService {

    /**
     * Import dashboard configs, will update any existing elements. Missing references will be ignored.
     * @param dashboardBuilderData
     * @return
     */
    public boolean importConfig( DashboardBuilderData dashboardBuilderData );

    /**
     * Export the configs passed in the array
     * @param dashboardBuilderData
     * @return
     */
    public String exportConfig( String[] dashboardConfigs );

}