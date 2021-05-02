package dev.dash.dashboard;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.dash.dao.DashboardConfigRepository;
import dev.dash.dao.PanelConfigRepository;
import dev.dash.dao.SchemaConfigRepository;
import dev.dash.dao.SecurityRoleRepository;
import dev.dash.dao.TabConfigRepository;
import dev.dash.model.DashboardConfig;
import dev.dash.model.PanelConfig;
import dev.dash.model.SchemaConfig;
import dev.dash.model.SecurityRole;
import dev.dash.model.TabConfig;
import dev.dash.model.builder.DashboardBuilderData;
import dev.dash.model.builder.DashboardDTO;
import dev.dash.model.builder.PanelDTO;
import dev.dash.model.builder.TabDTO;
import dev.dash.util.JsonUtil;
import dev.dash.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Slf4j
@Service
public class DashboardBuilderServiceImpl implements DashboardBuilderService {
 
    @Autowired
    DashboardConfigRepository dashboardConfigRepository;

    @Autowired
    TabConfigRepository tabConfigRepository;

    @Autowired
    PanelConfigRepository panelConfigRepository;

    @Autowired
    SchemaConfigRepository schemaConfigRepository;

    @Autowired
    SecurityRoleRepository securityRoleRepository;

    @Override
    public boolean importConfig( DashboardBuilderData dashboardBuilderData ) {
        //TODO _SW add auditing 
        //TODO _SW add security 
        for(DashboardDTO dashboardDTO: dashboardBuilderData.getDashboardConfigs()){
            importDashboard(dashboardDTO);
        }
        for ( TabDTO tabDTO : dashboardBuilderData.getTabConfigs() ) {
            importTab(tabDTO);
        }
        for ( PanelDTO panelDTO: dashboardBuilderData.getPanelConfigs() ) {
            importPanel(panelDTO);
        }
        return true;
    }

    @Override
    public String exportConfig( String[] dashboardConfigs ) {
        //TODO _SW add auditing 
        //TODO _SW add security
        DashboardBuilderData dashboardBuilderData = new DashboardBuilderData();
        for ( String dashboardCode : dashboardConfigs ) {
            if( StringUtil.isVaildString(dashboardCode) && dashboardConfigRepository.existsByCode(dashboardCode) ){
                DashboardConfig dashboardConfig = dashboardConfigRepository.findByCode( dashboardCode );
                dashboardBuilderData.addDashboardConfig(convert(dashboardConfig));
                for(TabConfig tabConfig : dashboardConfig.getTabConfigSet()){
                    dashboardBuilderData.addTabConfig(convert(tabConfig));
                    for(PanelConfig panelConfig : tabConfig.getPanelConfigSet()){
                        dashboardBuilderData.addPanelConfig(convert(panelConfig));
                    }
                }
            }
        }
        return JsonUtil.toJSON(dashboardBuilderData);
    }

    /**
     * does it already exist? update it
     * else create it.
     */
    private void importDashboard ( DashboardDTO dashboardDTO ) {
        DashboardConfig dashboardConfig = null;
        if( !StringUtil.isVaildString( dashboardDTO.getCode() ) ) { return; }
        if( dashboardConfigRepository.existsByCode(dashboardDTO.getCode()) ) { 
            dashboardConfig = dashboardConfigRepository.findByCode(dashboardDTO.getCode());
            dashboardConfig.setName(dashboardDTO.getName());
        } else {
            dashboardConfig = new DashboardConfig(dashboardDTO.getCode(), dashboardDTO.getName());
        }

        if ( dashboardDTO.getSchemaConfigs() != null ) { 
            for (String schemaConfigCode : dashboardDTO.getSchemaConfigs()) {
                Set<SchemaConfig> schemaConfigSet = dashboardConfig.getSchemaConfigSet();
                if( schemaConfigSet == null ) {
                    schemaConfigSet = new HashSet<>();
                }

                if ( StringUtil.isVaildString( schemaConfigCode) && schemaConfigRepository.existsByCode(schemaConfigCode) ) {
                    SchemaConfig schemaConfigInstance =  schemaConfigRepository.findByCode(schemaConfigCode);
                    schemaConfigSet.add( schemaConfigInstance );
                    dashboardConfig.setSchemaConfigSet( schemaConfigSet );
                }
            }
        }
        
        if ( StringUtil.isVaildString( dashboardDTO.getSecurityRole() ) && schemaConfigRepository.existsByCode( dashboardDTO.getSecurityRole() ) ) {
            SecurityRole securityRole = securityRoleRepository.findByCode(dashboardDTO.getSecurityRole());
            dashboardConfig.setSecurityRole(securityRole);
        } else {
            dashboardConfig.setSecurityRole(null);
        }
        dashboardConfigRepository.saveAndFlush(dashboardConfig);
    }

    /**
     * does it already exist? update it
     * else create it.
     */
    private void importTab(TabDTO tabDTO) {
        String dashboardConfigCode = tabDTO.getDashboardConfig();
        if ( StringUtil.isVaildString(dashboardConfigCode) && dashboardConfigRepository.existsByCode( dashboardConfigCode ) ) {
            DashboardConfig dashboardConfig = dashboardConfigRepository.findByCode(dashboardConfigCode);
            
            TabConfig tabConfig = null;
            if( !StringUtil.isVaildString( tabDTO.getCode() ) ) { return; }
            if ( tabConfigRepository.existsByCode(tabDTO.getCode()) ) { 
                tabConfig = tabConfigRepository.findByCode(tabDTO.getCode());
                tabConfig.setName(tabDTO.getName());
                tabConfig.setDisplayOrder(tabDTO.getDisplayOrder());
                tabConfig.setDashboardConfig(dashboardConfig);
            } else {
                tabConfig = new TabConfig (
                    tabDTO.getCode(),
                    tabDTO.getName(), 
                    tabDTO.getDisplayOrder(), 
                    dashboardConfig
                );
            }
            
            if ( StringUtil.isVaildString( tabDTO.getSecurityRole() ) && schemaConfigRepository.existsByCode( tabDTO.getSecurityRole() ) ) {
                SecurityRole securityRole = securityRoleRepository.findByCode(tabDTO.getSecurityRole());
                tabConfig.setSecurityRole(securityRole);
            } else {
                tabConfig.setSecurityRole(null);
            }

            tabConfigRepository.saveAndFlush(tabConfig);
        }
    }

    /**
     * does it already exist? update it
     * else create it.
     */
    private void importPanel( PanelDTO panelDTO ) {

        // tab needs to exist
        String tabConfigCode = panelDTO.getTabConfig();
        if ( StringUtil.isVaildString(tabConfigCode) && tabConfigRepository.existsByCode( tabConfigCode ) ) {
            TabConfig tabConfig = tabConfigRepository.findByCode(tabConfigCode);

            PanelConfig panelConfig = null;
            if ( !StringUtil.isVaildString(panelDTO.getCode()) ) { return;}
            if ( panelConfigRepository.existsByCode(panelDTO.getCode()) ) {
                panelConfig = panelConfigRepository.findByCode( panelDTO.getCode() );
                panelConfig.setName(panelDTO.getName());
                panelConfig.setGridRow(panelDTO.getGridRow());
                panelConfig.setGridCol(panelDTO.getGridCol());
                panelConfig.setElements(panelDTO.getElements());
                panelConfig.setTabConfig(tabConfig);
            } else {
                panelConfig = new PanelConfig (
                    panelDTO.getCode(),
                    panelDTO.getName(),
                    panelDTO.getGridCol(),
                    panelDTO.getGridRow(),
                    panelDTO.getElements(), 
                    tabConfig
                );
            }
            
            if ( StringUtil.isVaildString( panelDTO.getSecurityRole() ) && schemaConfigRepository.existsByCode( panelDTO.getSecurityRole() ) ) {
                SecurityRole securityRole = securityRoleRepository.findByCode(panelDTO.getSecurityRole());
                panelConfig.setSecurityRole(securityRole);
            } else {
                panelConfig.setSecurityRole(null);
            }

            panelConfigRepository.saveAndFlush( panelConfig );
            
        }
    }

    private DashboardDTO convert(DashboardConfig dashboardConfig) {
        DashboardDTO dashboardDTO = new DashboardDTO();
        dashboardDTO.setCode(dashboardConfig.getCode());
        dashboardDTO.setName(dashboardConfig.getName());
        if( dashboardConfig.getSecurityRole() != null ) dashboardDTO.setSecurityRole( dashboardConfig.getSecurityRole().getCode() );
        if( dashboardConfig.getSchemaConfigSet() != null ){
            for ( SchemaConfig schemaConfig : dashboardConfig.getSchemaConfigSet() ) {
                if(schemaConfig != null) dashboardDTO.addSchemaCode(schemaConfig.getCode());
            }
        }
        return dashboardDTO;
    }

    private TabDTO convert(TabConfig tabConfig) {
        TabDTO tabDTO = new TabDTO();
        tabDTO.setCode(tabConfig.getCode());
        tabDTO.setName(tabConfig.getName());
        tabDTO.setDisplayOrder(tabConfig.getDisplayOrder());
        if ( tabConfig.getDashboardConfig() != null ) tabDTO.setDashboardConfig(tabConfig.getDashboardConfig().getCode());
        if ( tabConfig.getSecurityRole() != null ) tabDTO.setSecurityRole(tabConfig.getSecurityRole().getCode());
        return tabDTO;
    }

    private PanelDTO convert(PanelConfig panelConfig) {
        PanelDTO panelDTO = new PanelDTO();
        panelDTO.setCode(panelConfig.getCode());
        panelDTO.setName(panelConfig.getName());
        panelDTO.setGridRow(panelConfig.getGridRow());
        panelDTO.setGridCol(panelConfig.getGridCol());
        panelDTO.setElements(panelConfig.getElements());
        if( panelConfig.getTabConfig() != null ) panelDTO.setTabConfig(panelConfig.getTabConfig().getCode());
        if( panelConfig.getSecurityRole() != null ) panelDTO.setSecurityRole(panelConfig.getSecurityRole().getCode());
        return panelDTO;
    }
}