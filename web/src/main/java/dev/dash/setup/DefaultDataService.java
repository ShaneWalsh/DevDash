package dev.dash.setup;

import java.util.Arrays;
import java.util.HashSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.dash.dao.ConnectionConfigRepository;
import dev.dash.dao.DashboardConfigRepository;
import dev.dash.dao.PanelConfigRepository;
import dev.dash.dao.QueryConfigRepository;
import dev.dash.dao.SchemaConfigRepository;
import dev.dash.dao.SecurityRoleRepository;
import dev.dash.dao.SecurityUserRepository;
import dev.dash.dao.TabConfigRepository;
import dev.dash.enums.AdminDefaultRolesEnum;
import dev.dash.enums.ConnectionSourceEnum;
import dev.dash.enums.DdlTypeEnum;
import dev.dash.enums.UserTypeEnum;
import dev.dash.model.ConnectionConfig;
import dev.dash.model.DashboardConfig;
import dev.dash.model.PanelConfig;
import dev.dash.model.QueryConfig;
import dev.dash.model.SchemaConfig;
import dev.dash.model.SecurityRole;
import dev.dash.model.SecurityUser;
import dev.dash.model.TabConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * Initialises the application db with all of the default configs for the dashboard building.
 */
@Slf4j
@Transactional
@Service
public class DefaultDataService {
    
    private static final String DD_DEV_DASH = "DD_DevDash";

    private static final String DD_CONFIGURATOR_DASHBOARD = AdminDefaultRolesEnum.DD_CONFIGURATOR_DASHBOARD.getSecurityRoleCode();

    private static final String DD_CONFIGURATOR_QUERY = AdminDefaultRolesEnum.DD_CONFIGURATOR_QUERY.getSecurityRoleCode();

    private static final String DD_CONFIGURATOR_PARENT = AdminDefaultRolesEnum.DD_CONFIGURATOR_PARENT.getSecurityRoleCode();

    @Autowired
    private SecurityRoleRepository securityRoleRepository;

    @Autowired
    private SecurityUserRepository securityUserRepository;

    @Autowired
    private DashboardConfigRepository dashboardConfigRepository;

    @Autowired
    private TabConfigRepository tabConfigRepository;

    @Autowired
    private PanelConfigRepository panelConfigRepository;

    @Autowired
    private SchemaConfigRepository schemaConfigRepository;    
    
    @Autowired
    private QueryConfigRepository queryConfigRepository; 

    @Autowired
    private ConnectionConfigRepository connectionConfigRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Value("${dd.default.admin.password}")
    private String defaultAdminPassword;

    @Value("${db.url}")
    private String databaseUrl;

    @Value("${db.username}")
    private String databaseUsername;

    @Value("${db.password}")
    private String databasePassword;

    public void setupAllData(){
        // check if the db already exists, if it does, do nothing.
        this.setupSecurityAndRoles();
        this.setupDefaultScreens();
    }

    public void setupSecurityAndRoles() {
        SecurityRole existingRole = this.securityRoleRepository.findByCode(DD_CONFIGURATOR_PARENT);
        if(existingRole == null) {
            SecurityRole securityRoleParent = new SecurityRole(DD_CONFIGURATOR_PARENT,"Parent Role to access all DD configs, typically an Admin.");
            this.securityRoleRepository.saveAndFlush(securityRoleParent);

            SecurityRole securityRoleDashboard = new SecurityRole(DD_CONFIGURATOR_DASHBOARD,"Role to crud DevDash Dashboards, typically a developer.",securityRoleParent);
            this.securityRoleRepository.saveAndFlush(securityRoleDashboard);

            SecurityRole securityRoleQuery = new SecurityRole(DD_CONFIGURATOR_QUERY,"Role to crud DevDash Queries, typically a developer.", securityRoleParent);
            this.securityRoleRepository.saveAndFlush(securityRoleQuery);

            SecurityUser securityUser = new SecurityUser( "Admin", passwordEncoder.encode(defaultAdminPassword), UserTypeEnum.Admin.name() );
            securityUser.setSecurityRolesSet( new HashSet<>( Arrays.asList( securityRoleParent ) ) );
            //securityRoleParent.setSecurityUsersSet(new HashSet(Arrays.asList(securityUser)));
            this.securityUserRepository.saveAndFlush(securityUser);
        } else {
            log.info("Skipping setupSecurityAndRoles as roles already exist");
        }
    }

    public void setupDefaultScreens(){
        // setup schema's
        SchemaConfig existingConfig = schemaConfigRepository.findByCode(DD_DEV_DASH);
        if ( existingConfig == null) {
            SchemaConfig configuratorScheme = new SchemaConfig(DD_DEV_DASH, "DevDash DB");
            configuratorScheme.setSecurityRole( this.securityRoleRepository.findByCode( DD_CONFIGURATOR_QUERY ) );
            schemaConfigRepository.saveAndFlush( configuratorScheme );
            // setup connections // todo replace with configurable variables from resources
            // local connction we have to have localhost in place of devdash-mysql because the local app does not know the containers name.
            ConnectionConfig motorConnectionConfig = new ConnectionConfig("DD_DevDash_Connection","DevDash Connection",ConnectionSourceEnum.MySQL.name(),
                databaseUrl, 
                databaseUsername, databasePassword, configuratorScheme );
            connectionConfigRepository.saveAndFlush(motorConnectionConfig);

            setupReusable(configuratorScheme);

            DashboardConfig dashboardScreens = setupDashboardScreens( configuratorScheme );
            setupTabScreens( configuratorScheme, dashboardScreens );
            setupPanelScreens( configuratorScheme, dashboardScreens );

            setupSchemaScreens( configuratorScheme, dashboardScreens );
            setupConnectionScreens(configuratorScheme, dashboardScreens);
            setupQueryScreens(configuratorScheme, dashboardScreens);
        } else {
            log.info("Skipping setupDefaultScreens as screens already exist");
        }
    }

    /**
     * Create reusable elements for reuse in other elements.
     * @param configuratorScheme
     */
    private void setupReusable(SchemaConfig configuratorScheme) {
        QueryConfig secRoleSelectListQuery = new QueryConfig("DD_DevDash_Sec_Linkable_Role", "SecurityRoles Select List",
            "Select securityRole_id as value, code as label from securityRole", configuratorScheme);
        queryConfigRepository.saveAndFlush(secRoleSelectListQuery);

        QueryConfig schemaSelectListQuery = new QueryConfig("DD_DevDash_Linkable_Schema", "Schema Select List",
            "Select schemaconfig_id as value, code as label from schemaconfig", configuratorScheme);
        queryConfigRepository.saveAndFlush(schemaSelectListQuery);

        QueryConfig tabSelectListQuery = new QueryConfig("DD_DevDash_Linkable_Tabs", "Schema Select List",
            "Select tabconfig_id as value, code as label from tabconfig", configuratorScheme);
        queryConfigRepository.saveAndFlush(tabSelectListQuery);
    }

    /**
     * Default configs for dashboards, should this not be populated by an sql file on start up? 
     */ 
    public DashboardConfig setupDashboardScreens(SchemaConfig configuratorScheme ) {
        
        // setup queries
        QueryConfig dashboardListQuery = new QueryConfig("DD_DevDash_Dashboard_List", "Dashboard List",
            "Select * from dashboardconfig", configuratorScheme);
        queryConfigRepository.saveAndFlush(dashboardListQuery);

        QueryConfig dashboardCreateQuery = new QueryConfig("DD_DevDash_Dashboard_Create", "Dashboard Create",
            "insert into dashboardconfig (code,name,securityRole_id) values('${DD_Configurator_Dashboard_Create_F_Code}','${DD_Configurator_Dashboard_Create_F_Name}','${DD_Configurator_Dashboard_Create_F_Sec_Role}')", DdlTypeEnum.Insert.name(), configuratorScheme);
        queryConfigRepository.saveAndFlush(dashboardCreateQuery);

        QueryConfig dashboardUpdateQuery = new QueryConfig("DD_DevDash_Dashboard_Update", "Dashboard Update",
            "update dashboardconfig set code='${DD_Configurator_Dashboard_Update_F_Code}', name='${DD_Configurator_Dashboard_Update_F_Name}', securityRole_id='${DD_Configurator_Dashboard_Update_F_Sec_Role}' where dashboardconfig_id = ${DD_Configurator_Dashboard_Update_F_Id} ", DdlTypeEnum.Update.name(), configuratorScheme);
        queryConfigRepository.saveAndFlush(dashboardUpdateQuery);

        DashboardConfig dashboardConfig = new DashboardConfig("DD_Configurator_Dash","Configurator Dash");
        // link the new scheme to the dash
        dashboardConfig.setSchemaConfigSet(new HashSet<SchemaConfig>(Arrays.asList(configuratorScheme)));
        // set dashboard permission
        dashboardConfig.setSecurityRole( this.securityRoleRepository.findByCode( DD_CONFIGURATOR_DASHBOARD ) );
        dashboardConfigRepository.saveAndFlush(dashboardConfig);
        TabConfig tabConfig  = new TabConfig("DD_Configurator_Dashboards","Dashboards",1,dashboardConfig);
        tabConfigRepository.saveAndFlush(tabConfig);

        PanelConfig panelConfig = null;
        panelConfig = new PanelConfig("DD_Configurator_Dashboard_List", "Dashboard List", 3,1,
        "[{\"code\":\"DD_Configurator_Dashboard_List_Table1\",\"type\":\"TABLE\",\"dataOn\":\"DD_DevDash_Dashboard_List\"},{\"code\":\"DD_Configurator_Dashboard_List_Refresh1\",\"type\":\"BUTTON\", \"label\":\"Filter\",\"exeQuery\":[\"DD_DevDash_Dashboard_List\",\"DD_DevDash_Sec_Linkable_Role\"],\"triggerOnLoad\":true}]", tabConfig);
        panelConfigRepository.saveAndFlush(panelConfig);

        panelConfig = new PanelConfig("DD_Configurator_Dashboard_Create", "Dashboard Create", 1,2,
        "["+
            "{\"code\":\"DD_Configurator_Dashboard_Create_F_Code\",\"type\":\"TEXT\",\"label\":\"Code\"}, "+
            "{\"code\":\"DD_Configurator_Dashboard_Create_F_Name\",\"type\":\"TEXT\",\"label\":\"Name\"}, "+ 
            "{\"code\":\"DD_Configurator_Dashboard_Create_F_Sec_Role\",\"type\":\"SELECT\",\"label\":\"Role Permission\", \"dataOn\":\"DD_DevDash_Sec_Linkable_Role\", \"dataOnParser\":\"SelectKeyParser\",\"dataOnParserConfig\":\"{\\\"jsonParsable\\\":true}\"}, "+
            "{\"code\":\"DD_Configurator_Dashboard_Create_BT_Save\",\"type\":\"BUTTON\", \"label\":\"Save\",\"exeQuery\":[\"DD_DevDash_Dashboard_Create\",\"DD_DevDash_Dashboard_List\"],\"triggerOnLoad\":false}]", tabConfig);
        panelConfigRepository.saveAndFlush(panelConfig);   

        panelConfig = new PanelConfig("DD_Configurator_Dashboard_Update", "Dashboard Update", 2,2,
        "["+
            "{\"code\":\"DD_Configurator_Dashboard_Update_F_Code\",\"type\":\"TEXT\",\"label\":\"Code\", \"dataOn\":\"DD_Configurator_Dashboard_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"code\\\"}\"},"+
            "{\"code\":\"DD_Configurator_Dashboard_Update_F_Name\",\"type\":\"TEXT\",\"label\":\"Name\", \"dataOn\":\"DD_Configurator_Dashboard_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"name\\\"}\"},"+
            "{\"code\":\"DD_Configurator_Dashboard_Update_F_Sec_Role\",\"type\":\"TEXT\",\"label\":\"Name\", \"dataOn\":\"DD_Configurator_Dashboard_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"securityRole_id\\\"}\"},"+
            "{\"code\":\"DD_Configurator_Dashboard_Update_F_Id\",\"type\":\"TEXT\",\"label\":\"Id\", \"dataOn\":\"DD_Configurator_Dashboard_List_Table1\",\"hidden\":true,\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"dashboardconfig_id\\\"}\" },"+
            "{\"code\":\"DD_Configurator_Dashboard_Update_BT_Update\",\"type\":\"BUTTON\", \"label\":\"Update\",\"confirmation\":true,\"exeQuery\":[\"DD_DevDash_Dashboard_Update\",\"DD_DevDash_Dashboard_List\"],\"triggerOnLoad\":false} "+
        "]", tabConfig);
        panelConfigRepository.saveAndFlush(panelConfig);

        // Dashboard has a many to many relationship with Schema, so we need an additional table here to update that relationship also.
        QueryConfig dashboardSchemaListQuery = new QueryConfig("DD_DevDash_Dashboard_Schema_List", "Dashboard Schema List",
            "Select * from dashboardconfig_to_schemaconfig where dashboardconfig_id = ${DD_Configurator_Dashboard_List_dashboardconfig_id}", configuratorScheme);
        queryConfigRepository.saveAndFlush(dashboardSchemaListQuery);
        
        QueryConfig dashboardSchemaCreateQuery = new QueryConfig("DD_DevDash_Dashboard_Schema_Create", "Dashboard Schema Create",
            "insert into dashboardconfig_to_schemaconfig (dashboardconfig_id,schemaconfig_id) values('${DD_Configurator_Dashboard_Schema_Create_F_Dashboard}','${DD_Configurator_Dashboard_Schema_Create_F_Schema}')", DdlTypeEnum.Insert.name(), configuratorScheme);
        queryConfigRepository.saveAndFlush(dashboardSchemaCreateQuery);
        
        QueryConfig dashboardSchemaDeleteQuery = new QueryConfig("DD_DevDash_Dashboard_Schema_Delete", "Dashboard Schema Delete",
            "delete from dashboardconfig_to_schemaconfig where dashboardconfig_id='${DD_Configurator_Dashboard_Schema_Update_F_Dashboard}' AND schemaconfig_id='${DD_Configurator_Dashboard_Schema_Update_F_Schema}' ", DdlTypeEnum.Delete.name(), configuratorScheme);
        queryConfigRepository.saveAndFlush(dashboardSchemaDeleteQuery);

        panelConfig = new PanelConfig("DD_Configurator_Dashboard_Schema_List", "Dashboard - Schema List", 3,3,
        "[{\"code\":\"DD_Configurator_Dashboard_Schema_List_Table1\",\"type\":\"TABLE\",\"dataOn\":\"DD_DevDash_Dashboard_Schema_List\",\"exeQuery\":[\"DD_DevDash_Dashboard_Schema_List\",\"DD_DevDash_Linkable_Schema\"],\"triggerOnEmit\":\"DD_Configurator_Dashboard_List_Table1\"}]", tabConfig);
        panelConfigRepository.saveAndFlush(panelConfig);

        panelConfig = new PanelConfig("DD_Configurator_Dashboard_Schema_Create", "Dashboard Schema Create", 1,4,
        "["+
            "{\"code\":\"DD_Configurator_Dashboard_Schema_Create_F_Dashboard\",\"type\":\"TEXT\",\"label\":\"Id\", \"dataOn\":\"DD_Configurator_Dashboard_List_Table1\",\"hidden\":false,\"readonly\":true,\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"dashboardconfig_id\\\"}\" },"+
            "{\"code\":\"DD_Configurator_Dashboard_Schema_Create_F_Schema\",\"type\":\"SELECT\",\"label\":\"Schema\", \"dataOn\":\"DD_DevDash_Linkable_Schema\", \"dataOnParser\":\"SelectKeyParser\",\"dataOnParserConfig\":\"{\\\"jsonParsable\\\":true}\"}, "+
            "{\"code\":\"DD_Configurator_Dashboard_Schema_Create_BT_Save\",\"type\":\"BUTTON\", \"label\":\"Save\",\"exeQuery\":[\"DD_DevDash_Dashboard_Schema_Create\",\"DD_DevDash_Dashboard_Schema_List\"],\"triggerOnLoad\":false}]", tabConfig);
        panelConfigRepository.saveAndFlush(panelConfig);  

        panelConfig = new PanelConfig("DD_Configurator_Dashboard_Schema_Update", "Dashboard Schema Update", 2,4,
        "["+
            "{\"code\":\"DD_Configurator_Dashboard_Schema_Update_F_Dashboard\",\"type\":\"TEXT\",\"label\":\"Dashboard\", \"readonly\":true, \"dataOn\":\"DD_Configurator_Dashboard_Schema_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"dashboardconfig_id\\\"}\"},"+
            "{\"code\":\"DD_Configurator_Dashboard_Schema_Update_F_Schema\",\"type\":\"TEXT\",\"label\":\"Schema\", \"readonly\":true, \"dataOn\":\"DD_Configurator_Dashboard_Schema_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"schemaconfig_id\\\"}\" },"+
            "{\"code\":\"DD_Configurator_Dashboard_Schema_Update_BT_Delete\",\"type\":\"BUTTON\", \"label\":\"Delete\",\"confirmation\":true,\"exeQuery\":[\"DD_DevDash_Dashboard_Schema_Delete\",\"DD_DevDash_Dashboard_Schema_List\"],\"triggerOnLoad\":false} "+
        "]", tabConfig);
        panelConfigRepository.saveAndFlush(panelConfig);

        return dashboardConfig;
    }

    /**
     * Default configs for Tabs, should this not be populated by an sql file on start up? 
     */ 
    public void setupTabScreens( SchemaConfig configuratorScheme, DashboardConfig dashboardScreens) {
        // setup queries
        QueryConfig tabListQuery = new QueryConfig("DD_DevDash_Tab_List", "Tab List",
            "Select * from tabconfig", configuratorScheme);
        queryConfigRepository.saveAndFlush(tabListQuery);        
        
        QueryConfig dashboardSelectListQuery = new QueryConfig("DD_DevDash_Tab_Linkable_Dashboard", "Tab Link Dashboard Select List",
            "Select dashboardconfig_id as value, name as label from dashboardconfig", configuratorScheme);
        queryConfigRepository.saveAndFlush(dashboardSelectListQuery);
				
        QueryConfig tabCreateQuery = new QueryConfig("DD_DevDash_Tab_Create", "Tab Create",
            "insert into tabconfig (code,name,displayOrder,dashboardconfig_id) values('${DD_Configurator_Tab_Create_F_Code}','${DD_Configurator_Tab_Create_F_Name}','${DD_Configurator_Tab_Create_F_Display}','${DD_Configurator_Tab_Create_F_dashConfigId}')", DdlTypeEnum.Insert.name(), configuratorScheme);
        queryConfigRepository.saveAndFlush(tabCreateQuery);

        QueryConfig tabUpdateQuery = new QueryConfig("DD_DevDash_Tab_Update", "Tab Update",
            "update tabconfig set code='${DD_Configurator_Tab_Update_F_Code}', name='${DD_Configurator_Tab_Update_F_Name}', displayOrder=${DD_Configurator_Tab_Update_F_DisplayOrder}, dashboardconfig_id=${DD_Configurator_Tab_Update_F_Dash_Id} where tabconfig_id = ${DD_Configurator_Tab_Update_F_Tab_Id} ", DdlTypeEnum.Update.name(), configuratorScheme);
        queryConfigRepository.saveAndFlush(tabUpdateQuery);

        TabConfig tabConfig  = new TabConfig("DD_Configurator_Tab","Tabs",2,dashboardScreens);
        tabConfigRepository.saveAndFlush(tabConfig);

        PanelConfig panelConfig = null;
        panelConfig = new PanelConfig("DD_Configurator_Tab_List", "Tab List", 3,1,
        "[{\"code\":\"DD_Configurator_Tab_List_Table1\",\"type\":\"TABLE\",\"dataOn\":\"DD_DevDash_Tab_List\"},{\"code\":\"DD_Configurator_Tab_List_Refresh1\",\"type\":\"BUTTON\", \"label\":\"Filter\",\"exeQuery\":[\"DD_DevDash_Tab_List\",\"DD_DevDash_Tab_Linkable_Dashboard\"],\"triggerOnLoad\":true}]", tabConfig);
        panelConfigRepository.saveAndFlush(panelConfig);

        panelConfig = new PanelConfig("DD_Configurator_Tab_Create", "Tab Create", 1,2,
        "["+
            "{\"code\":\"DD_Configurator_Tab_Create_F_Code\",\"type\":\"TEXT\",\"label\":\"Code\"}, "+
            "{\"code\":\"DD_Configurator_Tab_Create_F_Name\",\"type\":\"TEXT\",\"label\":\"Name\"}, "+
            "{\"code\":\"DD_Configurator_Tab_Create_F_Display\",\"type\":\"TEXT\",\"label\":\"Display Order\"}, "+
            //"{\"code\":\"DD_Configurator_Tab_Create_F_dashConfigId\",\"type\":\"TEXT\",\"label\":\"Dashboard Config to Link\"}, "+
            "{\"code\":\"DD_Configurator_Tab_Create_F_dashConfigId\",\"type\":\"SELECT\",\"label\":\"Dashboard Config to Link\", \"dataOn\":\"DD_DevDash_Tab_Linkable_Dashboard\", \"dataOnParser\":\"SelectKeyParser\",\"dataOnParserConfig\":\"{\\\"jsonParsable\\\":true}\"}, "+
            "{\"code\":\"DD_Configurator_Tab_Create_BT_Save\",\"type\":\"BUTTON\", \"label\":\"Save\",\"exeQuery\":[\"DD_DevDash_Tab_Create\",\"DD_DevDash_Tab_List\"],\"triggerOnLoad\":false} "+
        "]", tabConfig);
        panelConfigRepository.saveAndFlush(panelConfig);   

        panelConfig = new PanelConfig("DD_Configurator_Tab_Update", "Tab Update", 2,2,
        "["+
            "{\"code\":\"DD_Configurator_Tab_Update_F_Code\",\"type\":\"TEXT\",\"label\":\"Code\", \"dataOn\":\"DD_Configurator_Tab_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"code\\\"}\"},"+
            "{\"code\":\"DD_Configurator_Tab_Update_F_Name\",\"type\":\"TEXT\",\"label\":\"Name\", \"dataOn\":\"DD_Configurator_Tab_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"name\\\"}\"},"+
            "{\"code\":\"DD_Configurator_Tab_Update_F_DisplayOrder\",\"type\":\"TEXT\",\"label\":\"Display Order\", \"dataOn\":\"DD_Configurator_Tab_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"displayOrder\\\"}\"},"+
            "{\"code\":\"DD_Configurator_Tab_Update_F_Tab_Id\",\"type\":\"TEXT\",\"label\":\"Tab Id\", \"dataOn\":\"DD_Configurator_Tab_List_Table1\",\"hidden\":true, \"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"tabconfig_id\\\"}\" },"+
            "{\"code\":\"DD_Configurator_Tab_Update_F_Dash_Id\",\"type\":\"TEXT\",\"label\":\"Dash Id\", \"dataOn\":\"DD_Configurator_Tab_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"dashboardconfig_id\\\"}\" },"+
            "{\"code\":\"DD_Configurator_Tab_Update_BT_Update\",\"type\":\"BUTTON\", \"label\":\"Update\",\"exeQuery\":[\"DD_DevDash_Tab_Update\",\"DD_DevDash_Tab_List\"],\"triggerOnLoad\":false} "+
        "]", tabConfig);

        panelConfigRepository.saveAndFlush(panelConfig);
    }

    /**
     * Default configs for Panels, should this not be populated by an sql file on start up? 
     */ 
    public void setupPanelScreens( SchemaConfig configuratorScheme, DashboardConfig dashboardScreens) {
        // setup queries
        QueryConfig dashboardListQuery = new QueryConfig("DD_DevDash_Panel_List", "Panel List",
            "Select * from panelconfig", configuratorScheme);
        queryConfigRepository.saveAndFlush(dashboardListQuery);
				
        QueryConfig dashboardCreateQuery = new QueryConfig("DD_DevDash_Panel_Create", "Panel Create",
            "insert into panelconfig (code, name, gridCol, gridRow, elements,tabconfig_id) values('${DD_Configurator_Panel_Create_F_Code}','${DD_Configurator_Panel_Create_F_Name}', ${DD_Configurator_Panel_Create_F_grid_col}, ${DD_Configurator_Panel_Create_F_grid_row}, '${DD_Configurator_Panel_Create_F_elements}','${DD_Configurator_Panel_Create_F_tabconfig_id}')", DdlTypeEnum.Insert.name(), configuratorScheme);
        queryConfigRepository.saveAndFlush(dashboardCreateQuery);

        QueryConfig dashboardUpdateQuery = new QueryConfig("DD_DevDash_Panel_Update", "Panel Update",
            "update panelconfig set code='${DD_Configurator_Panel_Update_F_Code}', name='${DD_Configurator_Panel_Update_F_Name}', gridCol=${DD_Configurator_Panel_Update_F_grid_col}, gridRow=${DD_Configurator_Panel_Update_F_grid_row}, elements='${DD_Configurator_Panel_Update_F_elements}', tabconfig_id=${DD_Configurator_Panel_Update_F_tabconfig_id} where panelconfig_id = ${DD_Configurator_Panel_Update_F_panelconfig_id} ", DdlTypeEnum.Update.name(), configuratorScheme);
        queryConfigRepository.saveAndFlush(dashboardUpdateQuery);

        TabConfig tabConfig  = new TabConfig("DD_Configurator_Panel","Panels",3,dashboardScreens);
        tabConfigRepository.saveAndFlush(tabConfig);

        PanelConfig panelConfig = null;
        panelConfig = new PanelConfig("DD_Configurator_Panel_List", "Panel List", 3,1,
        "[{\"code\":\"DD_Configurator_Panel_List_Table1\",\"type\":\"TABLE\",\"dataOn\":\"DD_DevDash_Panel_List\"},{\"code\":\"DD_Configurator_Panel_List_Refresh1\",\"type\":\"BUTTON\", \"label\":\"Filter\",\"exeQuery\":[\"DD_DevDash_Panel_List\",\"DD_DevDash_Linkable_Tabs\"],\"triggerOnLoad\":true}]", tabConfig);
        panelConfigRepository.saveAndFlush(panelConfig);

        panelConfig = new PanelConfig("DD_Configurator_Panel_Create", "Panel Create", 1,2,
        "["+
            "{\"code\":\"DD_Configurator_Panel_Create_F_Code\",\"type\":\"TEXT\",\"label\":\"Code\"}, "+
            "{\"code\":\"DD_Configurator_Panel_Create_F_Name\",\"type\":\"TEXT\",\"label\":\"Name\"}, "+
            "{\"code\":\"DD_Configurator_Panel_Create_F_grid_col\",\"type\":\"TEXT\",\"label\":\"Col\"}, "+
            "{\"code\":\"DD_Configurator_Panel_Create_F_grid_row\",\"type\":\"TEXT\",\"label\":\"Row\"}, "+
            "{\"code\":\"DD_Configurator_Panel_Create_F_elements\",\"type\":\"TEXT\",\"label\":\"Elements\"}, "+
            //"{\"code\":\"DD_Configurator_Panel_Create_F_tabconfig_id\",\"type\":\"TEXT\",\"label\":\"Tab to Link\"}, "+
            "{\"code\":\"DD_Configurator_Panel_Create_F_tabconfig_id\",\"type\":\"SELECT\",\"label\":\"Tab to Link\", \"dataOn\":\"DD_DevDash_Linkable_Tabs\", \"dataOnParser\":\"SelectKeyParser\",\"dataOnParserConfig\":\"{\\\"jsonParsable\\\":true}\"}, "+
            "{\"code\":\"DD_Configurator_Panel_Create_BT_Save\",\"type\":\"BUTTON\", \"label\":\"Save\",\"exeQuery\":[\"DD_DevDash_Panel_Create\",\"DD_DevDash_Panel_List\"],\"triggerOnLoad\":false} "+
        "]", tabConfig);
        panelConfigRepository.saveAndFlush(panelConfig);   

        panelConfig = new PanelConfig("DD_Configurator_Panel_Update", "Panel Update", 2,2,
        "["+
            "{\"code\":\"DD_Configurator_Panel_Update_F_Code\",\"type\":\"TEXT\",\"label\":\"Code\", \"dataOn\":\"DD_Configurator_Panel_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"code\\\"}\"},"+
            "{\"code\":\"DD_Configurator_Panel_Update_F_Name\",\"type\":\"TEXT\",\"label\":\"Name\", \"dataOn\":\"DD_Configurator_Panel_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"name\\\"}\"},"+
            "{\"code\":\"DD_Configurator_Panel_Update_F_grid_col\",\"type\":\"TEXT\",\"label\":\"Col\", \"dataOn\":\"DD_Configurator_Panel_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"gridCol\\\"}\"},"+
            "{\"code\":\"DD_Configurator_Panel_Update_F_grid_row\",\"type\":\"TEXT\",\"label\":\"Row\", \"dataOn\":\"DD_Configurator_Panel_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"gridRow\\\"}\"},"+
            "{\"code\":\"DD_Configurator_Panel_Update_F_elements\",\"type\":\"TEXT\",\"label\":\"Elements\", \"dataOn\":\"DD_Configurator_Panel_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"elements\\\"}\"},"+
            "{\"code\":\"DD_Configurator_Panel_Update_F_panelconfig_id\",\"type\":\"TEXT\",\"label\":\"Panel Id\", \"hidden\":true, \"dataOn\":\"DD_Configurator_Panel_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"panelconfig_id\\\"}\" },"+
            "{\"code\":\"DD_Configurator_Panel_Update_F_tabconfig_id\",\"type\":\"TEXT\",\"label\":\"Tab Id\", \"dataOn\":\"DD_Configurator_Panel_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"tabconfig_id\\\"}\" },"+
            "{\"code\":\"DD_Configurator_Panel_Update_BT_Update\",\"type\":\"BUTTON\", \"label\":\"Update\",\"exeQuery\":[\"DD_DevDash_Panel_Update\",\"DD_DevDash_Panel_List\"],\"triggerOnLoad\":false} "+
        "]", tabConfig);

        panelConfigRepository.saveAndFlush(panelConfig);
    }

    /**
     * Default configs for Schema's, should this not be populated by an sql file on start up? 
     */ 
    public void setupSchemaScreens(SchemaConfig configuratorScheme, DashboardConfig dashboardScreens) {
        
        // setup queries
        QueryConfig dashboardListQuery = new QueryConfig("DD_DevDash_Schema_List", "Schema List",
            "Select * from schemaconfig", configuratorScheme);
        queryConfigRepository.saveAndFlush(dashboardListQuery);

        QueryConfig dashboardCreateQuery = new QueryConfig("DD_DevDash_Schema_Create", "Schema Create",
            "insert into schemaconfig (code,name, securityRole_id) values('${DD_Configurator_Schema_Create_F_Code}','${DD_Configurator_Schema_Create_F_Name}', ${DD_Configurator_Schema_Create_F_Sec_Role})", DdlTypeEnum.Insert.name(), configuratorScheme);
        queryConfigRepository.saveAndFlush(dashboardCreateQuery);

        QueryConfig dashboardUpdateQuery = new QueryConfig("DD_DevDash_Schema_Update", "Schema Update",
            "update schemaconfig set code='${DD_Configurator_Schema_Update_F_Code}', name='${DD_Configurator_Schema_Update_F_Name}', securityRole_id='${DD_Configurator_Schema_Update_F_Sec_Role}' where schemaconfig_id = ${DD_Configurator_Schema_Update_F_Id} ", DdlTypeEnum.Update.name(), configuratorScheme);
        queryConfigRepository.saveAndFlush(dashboardUpdateQuery);

        TabConfig tabConfig  = new TabConfig("DD_Configurator_Schemas","Schemas",4,dashboardScreens);
        tabConfigRepository.saveAndFlush(tabConfig);

        PanelConfig panelConfig = null;
        panelConfig = new PanelConfig("DD_Configurator_Schema_List", "Schema List", 3,1,
        "[{\"code\":\"DD_Configurator_Schema_List_Table1\",\"type\":\"TABLE\",\"dataOn\":\"DD_DevDash_Schema_List\"},{\"code\":\"DD_Configurator_Schema_List_Refresh1\",\"type\":\"BUTTON\", \"label\":\"Filter\",\"exeQuery\":[\"DD_DevDash_Schema_List\",\"DD_DevDash_Sec_Linkable_Role\"],\"triggerOnLoad\":true}]", tabConfig);
        panelConfigRepository.saveAndFlush(panelConfig);

        panelConfig = new PanelConfig("DD_Configurator_Schema_Create", "Schema Create", 1,2,
        "["+
            "{\"code\":\"DD_Configurator_Schema_Create_F_Code\",\"type\":\"TEXT\",\"label\":\"Code\"}, "+
            "{\"code\":\"DD_Configurator_Schema_Create_F_Name\",\"type\":\"TEXT\",\"label\":\"Name\"}, "+
            "{\"code\":\"DD_Configurator_Schema_Create_F_Sec_Role\",\"type\":\"SELECT\",\"label\":\"Role Permission\", \"dataOn\":\"DD_DevDash_Sec_Linkable_Role\", \"dataOnParser\":\"SelectKeyParser\",\"dataOnParserConfig\":\"{\\\"jsonParsable\\\":true}\"}, "+
            "{\"code\":\"DD_Configurator_Schema_Create_BT_Save\",\"type\":\"BUTTON\", \"label\":\"Save\",\"exeQuery\":[\"DD_DevDash_Schema_Create\",\"DD_DevDash_Schema_List\"],\"triggerOnLoad\":false}]", tabConfig);
        panelConfigRepository.saveAndFlush(panelConfig);   

        panelConfig = new PanelConfig("DD_Configurator_Schema_Update", "Schema Update", 2,2,
        "["+
            "{\"code\":\"DD_Configurator_Schema_Update_F_Code\",\"type\":\"TEXT\",\"label\":\"Code\", \"dataOn\":\"DD_Configurator_Schema_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"code\\\"}\"},"+
            "{\"code\":\"DD_Configurator_Schema_Update_F_Name\",\"type\":\"TEXT\",\"label\":\"Name\", \"dataOn\":\"DD_Configurator_Schema_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"name\\\"}\"},"+
            "{\"code\":\"DD_Configurator_Schema_Update_F_Sec_Role\",\"type\":\"TEXT\",\"label\":\"Role Permission\", \"dataOn\":\"DD_Configurator_Schema_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"securityRole_id\\\"}\"},"+
            "{\"code\":\"DD_Configurator_Schema_Update_F_Id\",\"type\":\"TEXT\",\"label\":\"Id\", \"hidden\":true, \"dataOn\":\"DD_Configurator_Schema_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"schemaconfig_id\\\"}\" },"+
            "{\"code\":\"DD_Configurator_Schema_Update_BT_Update\",\"type\":\"BUTTON\", \"label\":\"Update\",\"exeQuery\":[\"DD_DevDash_Schema_Update\",\"DD_DevDash_Schema_List\"],\"triggerOnLoad\":false} "+
        "]", tabConfig);

        panelConfigRepository.saveAndFlush(panelConfig);
        
    }

    /**
     * Default configs for Connection screens, should this not be populated by an sql file on start up? 
     */ 
    public void setupConnectionScreens( SchemaConfig configuratorScheme, DashboardConfig dashboardScreens) {
        // setup queries
        QueryConfig dashboardListQuery = new QueryConfig("DD_DevDash_Connection_List", "Connection List",
            "Select * from connectionconfig", configuratorScheme);
        queryConfigRepository.saveAndFlush(dashboardListQuery);
				
        QueryConfig dashboardCreateQuery = new QueryConfig("DD_DevDash_Connection_Create", "Connection Create",
            "insert into connectionconfig (code,name,source,username,password,url,schemaconfig_id) "+
             "values( '${DD_Configurator_Connection_Create_F_Code}', '${DD_Configurator_Connection_Create_F_Name}', '${DD_Configurator_Connection_Create_F_Source}', " + 
             " '${DD_Configurator_Connection_Create_F_Userame}', '${DD_Configurator_Connection_Create_F_Password}', '${DD_Configurator_Connection_Create_F_Url}', '${DD_Configurator_Connection_Create_F_Schemaconfig_Id}')", DdlTypeEnum.Insert.name(), configuratorScheme);
        queryConfigRepository.saveAndFlush(dashboardCreateQuery);

        QueryConfig dashboardUpdateQuery = new QueryConfig("DD_DevDash_Connection_Update", "Connection Update",
            "update connectionconfig set code='${DD_Configurator_Connection_Update_F_Code}', name='${DD_Configurator_Connection_Update_F_Name}', " + 
             "source='${DD_Configurator_Connection_Update_F_Source}', username='${DD_Configurator_Connection_Update_F_Userame}', password='${DD_Configurator_Connection_Update_F_Password}'," +
             " url='${DD_Configurator_Connection_Update_F_Url}', schemaconfig_id=${DD_Configurator_Connection_Update_F_Schemaconfig_Id} " + 
             " where connectionconfig_id = ${DD_Configurator_Connection_Update_F_Connection_Id} ", DdlTypeEnum.Update.name(), configuratorScheme);

        queryConfigRepository.saveAndFlush(dashboardUpdateQuery);

        TabConfig tabConfig  = new TabConfig("DD_Configurator_Connections","Connections",5,dashboardScreens);
        tabConfigRepository.saveAndFlush(tabConfig);

        PanelConfig panelConfig = null;
        panelConfig = new PanelConfig("DD_Configurator_Connection_List", "Connection List", 3,1,
        "[{\"code\":\"DD_Configurator_Connection_List_Table1\",\"type\":\"TABLE\",\"dataOn\":\"DD_DevDash_Connection_List\"},{\"code\":\"DD_Configurator_Connection_List_Refresh1\",\"type\":\"BUTTON\", \"label\":\"Filter\",\"exeQuery\":[\"DD_DevDash_Connection_List\",\"DD_DevDash_Linkable_Schema\"],\"triggerOnLoad\":true}]", tabConfig);
        panelConfigRepository.saveAndFlush(panelConfig);

        panelConfig = new PanelConfig("DD_Configurator_Connection_Create", "Connection Create", 1,2,
        "["+
            "{\"code\":\"DD_Configurator_Connection_Create_F_Code\",\"type\":\"TEXT\",\"label\":\"Code\"}, "+
            "{\"code\":\"DD_Configurator_Connection_Create_F_Name\",\"type\":\"TEXT\",\"label\":\"Name\"}, "+
            //"{\"code\":\"DD_Configurator_Connection_Create_F_Source\",\"type\":\"TEXT\",\"label\":\"Source\"}, "+
            "{\"code\":\"DD_Configurator_Connection_Create_F_Source\",\"type\":\"SELECT\",\"label\":\"Source\", \"initialData\":\"[{\\\"value\\\":\\\"REST\\\", \\\"label\\\":\\\"REST\\\" },{ \\\"value\\\":\\\"MySQL\\\", \\\"label\\\":\\\"MySQL\\\" },{ \\\"value\\\":\\\"PostgreSQL\\\", \\\"label\\\":\\\"PostgreSQL\\\" },{ \\\"value\\\":\\\"CASSANDRA\\\", \\\"label\\\":\\\"CASSANDRA\\\" }]\", \"initialDataParser\":\"SelectKeyParser\",\"initialDataParserConfig\":\"{\\\"jsonParsable\\\":true}\"}, "+
            "{\"code\":\"DD_Configurator_Connection_Create_F_Userame\",\"type\":\"TEXT\",\"label\":\"Username\"}, "+
            "{\"code\":\"DD_Configurator_Connection_Create_F_Password\",\"type\":\"TEXT\",\"label\":\"Password\"}, "+
            "{\"code\":\"DD_Configurator_Connection_Create_F_Url\",\"type\":\"TEXT\",\"label\":\"Url\"}, "+
            //"{\"code\":\"DD_Configurator_Connection_Create_F_Schemaconfig_Id\",\"type\":\"TEXT\",\"label\":\"Schema Config to Link\"}, "+
            "{\"code\":\"DD_Configurator_Connection_Create_F_Schemaconfig_Id\",\"type\":\"SELECT\",\"label\":\"Schema\", \"dataOn\":\"DD_DevDash_Linkable_Schema\", \"dataOnParser\":\"SelectKeyParser\",\"dataOnParserConfig\":\"{\\\"jsonParsable\\\":true}\"}, "+
            "{\"code\":\"DD_Configurator_Connection_Create_BT_Save\",\"type\":\"BUTTON\", \"label\":\"Save\",\"exeQuery\":[\"DD_DevDash_Connection_Create\",\"DD_DevDash_Connection_List\"],\"triggerOnLoad\":false} "+
        "]", tabConfig);
        panelConfigRepository.saveAndFlush(panelConfig);   

        panelConfig = new PanelConfig("DD_Configurator_Connection_Update", "Connection Update", 2,2,
        "["+
            "{\"code\":\"DD_Configurator_Connection_Update_F_Code\",\"type\":\"TEXT\",\"label\":\"Code\", \"dataOn\":\"DD_Configurator_Connection_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"code\\\"}\"},"+
            "{\"code\":\"DD_Configurator_Connection_Update_F_Name\",\"type\":\"TEXT\",\"label\":\"Name\", \"dataOn\":\"DD_Configurator_Connection_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"name\\\"}\"},"+
            // "{\"code\":\"DD_Configurator_Connection_Update_F_Source\",\"type\":\"TEXT\",\"label\":\"Source\", \"dataOn\":\"DD_Configurator_Connection_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"source\\\"}\"},"+
            // Working Hard coded Select example example.
            "{\"code\":\"DD_Configurator_Connection_Update_F_Source\",\"type\":\"SELECT\",\"label\":\"Source\", \"dataOn\":\"DD_Configurator_Connection_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"source\\\"}\",\"initialData\":\"[{\\\"value\\\":\\\"REST\\\", \\\"label\\\":\\\"REST\\\" },{ \\\"value\\\":\\\"MySQL\\\", \\\"label\\\":\\\"MySQL\\\" },{ \\\"value\\\":\\\"PostgreSQL\\\", \\\"label\\\":\\\"PostgreSQL\\\" },{ \\\"value\\\":\\\"CASSANDRA\\\", \\\"label\\\":\\\"CASSANDRA\\\" }]\", \"initialDataParser\":\"SelectKeyParser\",\"initialDataParserConfig\":\"{\\\"jsonParsable\\\":true}\" },"+
            "{\"code\":\"DD_Configurator_Connection_Update_F_Userame\",\"type\":\"TEXT\",\"label\":\"Username\", \"dataOn\":\"DD_Configurator_Connection_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"username\\\"}\"},"+
            "{\"code\":\"DD_Configurator_Connection_Update_F_Password\",\"type\":\"TEXT\",\"label\":\"Password\", \"dataOn\":\"DD_Configurator_Connection_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"password\\\"}\"},"+
            "{\"code\":\"DD_Configurator_Connection_Update_F_Url\",\"type\":\"TEXT\",\"label\":\"Url\", \"dataOn\":\"DD_Configurator_Connection_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"url\\\"}\"},"+
            //"{\"code\":\"DD_Configurator_Connection_Update_F_Schemaconfig_Id\",\"type\":\"TEXT\",\"label\":\"Schema Id\", \"dataOn\":\"DD_Configurator_Connection_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"schemaconfig_id\\\"}\" },"+
            "{\"code\":\"DD_Configurator_Connection_Update_F_Schemaconfig_Id\",\"type\":\"SELECT\",\"label\":\"Schema Id\", \"dataOn\":\"DD_Configurator_Connection_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"schemaconfig_id\\\"}\", \"initialDataOn\":\"DD_DevDash_Linkable_Schema\", \"initialDataParser\":\"SelectKeyParser\",\"initialDataParserConfig\":\"{\\\"jsonParsable\\\":true}\" },"+
            "{\"code\":\"DD_Configurator_Connection_Update_F_Connection_Id\",\"type\":\"TEXT\",\"label\":\"Connection Id\", \"hidden\":true, \"dataOn\":\"DD_Configurator_Connection_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"connectionconfig_id\\\"}\" },"+
            "{\"code\":\"DD_Configurator_Connection_Update_BT_Update\",\"type\":\"BUTTON\", \"label\":\"Update\",\"exeQuery\":[\"DD_DevDash_Connection_Update\",\"DD_DevDash_Connection_List\"],\"triggerOnLoad\":false} "+
        "]", tabConfig);

        panelConfigRepository.saveAndFlush(panelConfig);
    }

    /**
     * Default configs for Query screens, should this not be populated by an sql file on start up? 
     */ 
    public void setupQueryScreens( SchemaConfig configuratorScheme, DashboardConfig dashboardScreens) {
        // setup queries
        QueryConfig dashboardListQuery = new QueryConfig("DD_DevDash_Query_List", "Query List",
            "Select * from queryconfig LIMIT ${DD_Configurator_Query_List_Table1_offset}, ${DD_Configurator_Query_List_Table1_limit} ", configuratorScheme);
        queryConfigRepository.saveAndFlush(dashboardListQuery);
				
        QueryConfig dashboardCreateQuery = new QueryConfig("DD_DevDash_Query_Create", "Query Create",
            "insert into queryconfig ( code, description, ddl_type, path, queryString, schemaconfig_id ) "+
             "values( '${DD_Configurator_Query_Create_F_Code}', '${DD_Configurator_Query_Create_F_Description}', '${DD_Configurator_Query_Create_F_Ddl_Type}', " + 
             " '${DD_Configurator_Query_Create_F_Path}', '${DD_Configurator_Query_Create_F_QueryString}', '${DD_Configurator_Query_Create_F_Schemaconfig_Id}')", DdlTypeEnum.Insert.name(), configuratorScheme);
        queryConfigRepository.saveAndFlush(dashboardCreateQuery);

        QueryConfig dashboardUpdateQuery = new QueryConfig("DD_DevDash_Query_Update", "Query Update",
            "update queryconfig set code='${DD_Configurator_Query_Update_F_Code}', description='${DD_Configurator_Query_Update_F_Description}', " + 
             "ddl_type='${DD_Configurator_Query_Update_F_Ddl_Type}', path='${DD_Configurator_Query_Update_F_Path}', queryString='${DD_Configurator_Query_Update_F_QueryString}', schemaconfig_id=${DD_Configurator_Query_Update_F_Schemaconfig_Id} " + 
             " where queryconfig_id = ${DD_Configurator_Query_Update_F_Query_Id} ", DdlTypeEnum.Update.name(), configuratorScheme);

        queryConfigRepository.saveAndFlush(dashboardUpdateQuery);

        TabConfig tabConfig  = new TabConfig("DD_Configurator_Queries","Queries",6,dashboardScreens);
        tabConfigRepository.saveAndFlush(tabConfig);

        PanelConfig panelConfig = null;
        panelConfig = new PanelConfig("DD_Configurator_Query_List", "Query List", 3,1,
        "["+
            "{\"code\":\"DD_Configurator_Query_List_Table1\",\"type\":\"TABLE\",\"dataOn\":\"DD_DevDash_Query_List\"}," + 
            "{\"code\":\"DD_Configurator_Query_List_Page1\",\"type\":\"PAGINATOR\", \"exeQuery\":[\"DD_DevDash_Query_List\"], \"offsetRC\":\"DD_Configurator_Query_List_Table1_offset\", \"limitRC\":\"DD_Configurator_Query_List_Table1_limit\"}," + 
            "{\"code\":\"DD_Configurator_Query_List_Refresh1\",\"type\":\"BUTTON\", \"label\":\"Filter\",\"exeQuery\":[\"DD_DevDash_Query_List\",\"DD_DevDash_Linkable_Schema\"],\"triggerOnLoad\":true}]", tabConfig);
        panelConfigRepository.saveAndFlush(panelConfig);

        panelConfig = new PanelConfig("DD_Configurator_Query_Create", "Query Create", 1,2,
        "["+
            "{\"code\":\"DD_Configurator_Query_Create_F_Code\",\"type\":\"TEXT\",\"label\":\"Code\"}, "+
            "{\"code\":\"DD_Configurator_Query_Create_F_Description\",\"type\":\"TEXT\",\"label\":\"Name\"}, "+
            "{\"code\":\"DD_Configurator_Query_Create_F_Ddl_Type\",\"type\":\"TEXT\",\"label\":\"DDL Type\"}, "+
            "{\"code\":\"DD_Configurator_Query_Create_F_Path\",\"type\":\"TEXT\",\"label\":\"Path\"}, "+
            "{\"code\":\"DD_Configurator_Query_Create_F_QueryString\",\"type\":\"TEXT\",\"label\":\"Query String\"}, " +
            // "{\"code\":\"DD_Configurator_Query_Create_F_Schemaconfig_Id\",\"type\":\"TEXT\",\"label\":\"Schema Config to Link\"}, "+
            "{\"code\":\"DD_Configurator_Query_Create_F_Schemaconfig_Id\",\"type\":\"SELECT\",\"label\":\"Schema\", \"dataOn\":\"DD_DevDash_Linkable_Schema\", \"dataOnParser\":\"SelectKeyParser\",\"dataOnParserConfig\":\"{\\\"jsonParsable\\\":true}\"}, "+
            "{\"code\":\"DD_Configurator_Query_Create_BT_Save\",\"type\":\"BUTTON\", \"label\":\"Save\",\"exeQuery\":[\"DD_DevDash_Query_Create\",\"DD_DevDash_Query_List\"],\"triggerOnLoad\":false} "+
        "]", tabConfig);
        panelConfigRepository.saveAndFlush(panelConfig);   

        panelConfig = new PanelConfig("DD_Configurator_Query_Update", "Query Update", 2,2,
        "["+
            "{\"code\":\"DD_Configurator_Query_Update_F_Code\",\"type\":\"TEXT\",\"label\":\"Code\", \"dataOn\":\"DD_Configurator_Query_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"code\\\"}\"},"+
            "{\"code\":\"DD_Configurator_Query_Update_F_Description\",\"type\":\"TEXT\",\"label\":\"Name\", \"dataOn\":\"DD_Configurator_Query_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"description\\\"}\"},"+
            "{\"code\":\"DD_Configurator_Query_Update_F_Ddl_Type\",\"type\":\"TEXT\",\"label\":\"DDL Type\", \"dataOn\":\"DD_Configurator_Query_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"ddl_type\\\"}\"},"+
            "{\"code\":\"DD_Configurator_Query_Update_F_Path\",\"type\":\"TEXT\",\"label\":\"Path\", \"dataOn\":\"DD_Configurator_Query_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"path\\\"}\"},"+
            "{\"code\":\"DD_Configurator_Query_Update_F_QueryString\",\"type\":\"TEXT\",\"label\":\"Query String\", \"dataOn\":\"DD_Configurator_Query_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"queryString\\\"}\"},"+
            "{\"code\":\"DD_Configurator_Query_Update_F_Schemaconfig_Id\",\"type\":\"TEXT\",\"label\":\"Schema Id\", \"dataOn\":\"DD_Configurator_Query_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"schemaconfig_id\\\"}\" },"+
            "{\"code\":\"DD_Configurator_Query_Update_F_Query_Id\",\"type\":\"TEXT\",\"label\":\"Query Id\", \"hidden\":true, \"dataOn\":\"DD_Configurator_Query_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"queryConfig_id\\\"}\" },"+
            "{\"code\":\"DD_Configurator_Query_Update_BT_Update\",\"type\":\"BUTTON\", \"label\":\"Update\",\"exeQuery\":[\"DD_DevDash_Query_Update\",\"DD_DevDash_Query_List\"],\"triggerOnLoad\":false} "+
        "]", tabConfig);

        panelConfigRepository.saveAndFlush(panelConfig);
    }

}
