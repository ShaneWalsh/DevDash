
package dev.dash.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import dev.dash.dao.DashboardConfigRepository;
import dev.dash.dao.TabConfigRepository;
import dev.dash.model.ConnectionConfig;
import dev.dash.model.DashboardConfig;
import dev.dash.model.PanelConfig;
import dev.dash.model.SchemaConfig;
import dev.dash.model.TabConfig;
import lombok.AllArgsConstructor;
import lombok.Data;

@RestController
@RequestMapping("dashboards")
public class DashboardConfigController {

    @Autowired
    DashboardConfigRepository dashboardConfigRepository;

    @Autowired
    TabConfigRepository tabConfigRepository;

	@GetMapping("/list")
	public List<DashboardListUI> list() {
		List<DashboardListUI> dashboards = dashboardConfigRepository.findAll()
            .stream().map(dashboard -> new DashboardListUI(dashboard.getCode(),dashboard.getName())).collect(Collectors.toList());
        return dashboards;
    }

    @RequestMapping(
        value = "/{code}", 
        produces = { "application/json" }, 
        method = RequestMethod.GET)
	public String getDashboardInstanceUsingCode( @PathVariable String code) {
        System.out.println(code);
		DashboardConfig dashboardConfig = dashboardConfigRepository.findByCode(code);

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(DashboardConfig.class, new DashboardConfigSerializer());
        mapper.registerModule(module);

        try {
            String serialized = mapper.writeValueAsString(dashboardConfig);
            return serialized;
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Data
    @AllArgsConstructor
    class DashboardConfigUI {
        String code; 
        String name;
        Set<SchemaConfig> schemeConfigs;
        Set<TabConfig> tabConfigs;
    }

    @Data
    @AllArgsConstructor
    class DashboardListUI {
        String code; 
        String name;
    }

    class DashboardConfigSerializer extends StdSerializer<DashboardConfig> {
    
        /**
         * generated serial version
         */
        private static final long serialVersionUID = -3763824169679837842L;

        public DashboardConfigSerializer() {
            this(null);
        }
      
        public DashboardConfigSerializer(Class<DashboardConfig> t) {
            super(t);
        }
    
        @Override
        public void serialize( DashboardConfig dashboardConfig, JsonGenerator jgen, SerializerProvider provider) 
          throws IOException, JsonProcessingException {
     
            jgen.writeStartObject();
            jgen.writeStringField("code", dashboardConfig.getCode());
            jgen.writeStringField("name", dashboardConfig.getName());

            jgen.writeFieldName("tabConfigs");
            jgen.writeStartArray();
            for(TabConfig tab: dashboardConfig.getTabConfigSet()) {
                jgen.writeStartObject();
                jgen.writeStringField("code", tab.getCode());
                jgen.writeStringField("name", tab.getName());
                jgen.writeNumberField("displayOrder", tab.getDisplayOrder());
                jgen.writeFieldName("panelConfigs");
                jgen.writeStartArray();
                for(PanelConfig panelConfig: tab.getPanelConfigSet()) {
                    jgen.writeStartObject();
                    jgen.writeStringField("code", panelConfig.getCode());
                    jgen.writeStringField("name", panelConfig.getName());
                    jgen.writeStringField("elements", panelConfig.getElements());
                    jgen.writeEndObject();
                }
                jgen.writeEndArray();
                jgen.writeEndObject();
            }
            jgen.writeEndArray();
            jgen.writeFieldName("schemaConfigs");
            jgen.writeStartArray();
            for(SchemaConfig schemaConfig: dashboardConfig.getSchemaConfigSet()) {
                jgen.writeStartObject();
                jgen.writeStringField("code", schemaConfig.getCode());
                jgen.writeStringField("name", schemaConfig.getName());
                jgen.writeFieldName("connectionConfigs");
                jgen.writeStartArray();
                for(ConnectionConfig connectionConfig: schemaConfig.getConnectionConfigSet()) {
                    jgen.writeStartObject();
                    jgen.writeStringField("code", connectionConfig.getCode());
                    jgen.writeStringField("name", connectionConfig.getName());
                    jgen.writeEndObject();
                }
                jgen.writeEndArray();
                jgen.writeEndObject();
            }
            jgen.writeEndArray();
            jgen.writeEndObject();
        }
    }

}