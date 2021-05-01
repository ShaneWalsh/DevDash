package dev.dash.controller;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.dash.dao.DashboardConfigRepository;
import dev.dash.dao.TabConfigRepository;
import dev.dash.dao.PanelConfigRepository;
import dev.dash.dao.SchemaConfigRepository;
import dev.dash.model.DashboardConfig;
import dev.dash.model.PanelConfig;
import dev.dash.model.SchemaConfig;
import dev.dash.model.TabConfig;
import dev.dash.model.builder.DashboardBuilderData;
import dev.dash.model.builder.DashboardDTO;
import dev.dash.model.builder.PanelDTO;
import dev.dash.model.builder.TabDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("dashboardBuilder")
public class DashboardBuilderController {

    @Autowired
    DashboardConfigRepository dashboardConfigRepository;

    @Autowired
    TabConfigRepository tabConfigRepository;

    @Autowired
    PanelConfigRepository panelConfigRepository;

    @Autowired
    SchemaConfigRepository schemaConfigRepository;

    /**
     * import method for dashboard data data
     * @param jsonObject
     * @return
     */
    @PostMapping("/importdata")
	public Integer importData(@RequestBody DashboardBuilderData dashboardBuilderData) {
        for(DashboardDTO dashboardDTO: dashboardBuilderData.getDashboardConfigs()){
            DashboardConfig dashboardConfig = new DashboardConfig(dashboardDTO.getCode(), dashboardDTO.getName());
            dashboardConfigRepository.saveAndFlush(dashboardConfig);
            for (String schemaConfig : dashboardDTO.getSchemaConfigs()) {
                Set<SchemaConfig> schemaConfigSet = dashboardConfig.getSchemaConfigSet();
                if( schemaConfigSet == null ) {
                    schemaConfigSet = new HashSet<>();
                }
                schemaConfigSet.add(schemaConfigRepository.findByCode(schemaConfig));
                dashboardConfig.setSchemaConfigSet(schemaConfigSet);
            }
        }
        for ( TabDTO tabDTO : dashboardBuilderData.getTabConfigs() ) {
            TabConfig tabConfig = new TabConfig(
                tabDTO.getCode(),
                tabDTO.getName(), 
                tabDTO.getDisplayOrder(), 
                dashboardConfigRepository.findByCode(tabDTO.getDashboardConfig())
            );
            
            tabConfigRepository.saveAndFlush(tabConfig);
        }
        for(PanelDTO panelDTO: dashboardBuilderData.getPanelConfigs()){
            PanelConfig panelConfig = new PanelConfig(
                panelDTO.getCode(),
                panelDTO.getName(),
                panelDTO.getGridCol(),
                panelDTO.getGridRow(),
                panelDTO.getElements(), 
                tabConfigRepository.findByCode(panelDTO.getTabConfig())
            );
            panelConfigRepository.saveAndFlush(panelConfig);
        }
        log.info("Dashboard data imported");
        return 200;
    }
}
