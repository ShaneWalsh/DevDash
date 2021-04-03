package dev.dash.model.builder;

import java.util.List;

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
}