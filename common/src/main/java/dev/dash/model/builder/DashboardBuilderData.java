package dev.dash.model.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DashboardBuilderData {
    private List<DashboardDTO> dashboardConfigs;
    private List<TabDTO> tabConfigs;
    private List<PanelDTO> panelConfigs;

    public DashboardBuilderData addDashboardConfig ( DashboardDTO dashboardDTO ) {
        if(dashboardConfigs == null) dashboardConfigs = new ArrayList<>();
        dashboardConfigs.add(dashboardDTO);
        return this;
    }

    public DashboardBuilderData addTabConfig ( TabDTO tabDTO ) {
        if(tabConfigs == null) tabConfigs = new ArrayList<>();
        tabConfigs.add(tabDTO);
        return this;
    }

    public DashboardBuilderData addPanelConfig ( PanelDTO panelDTO ) {
        if(panelConfigs == null) panelConfigs = new ArrayList<>();
        panelConfigs.add(panelDTO);
        return this;
    }

    public String getContentsList() {
        return String.format( "Dashboards: %s Tabs: %s Panels: %s", 
            dashboardConfigs.stream().map(DashboardDTO::getCode).collect(Collectors.joining(", ")),
            tabConfigs.stream().map(TabDTO::getCode).collect(Collectors.joining(", ")),
            panelConfigs.stream().map(PanelDTO::getCode).collect(Collectors.joining(", "))
        );
    }
}