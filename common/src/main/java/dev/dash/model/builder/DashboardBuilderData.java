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
    private List<DashboardDTO> dashboardConfigs = new ArrayList<>();
    private List<TabDTO> tabConfigs = new ArrayList<>();
    private List<PanelDTO> panelConfigs = new ArrayList<>();

    public DashboardBuilderData addDashboardConfig ( DashboardDTO dashboardDTO ) {
        dashboardConfigs.add(dashboardDTO);
        return this;
    }

    public DashboardBuilderData addTabConfig ( TabDTO tabDTO ) {
        tabConfigs.add(tabDTO);
        return this;
    }

    public DashboardBuilderData addPanelConfig ( PanelDTO panelDTO ) {
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