package dev.dash.model.builder;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class DashboardDTO {
	private String code; 
    private String name;
    private List<String> schemaConfigs;
    private String securityRole;

    public DashboardDTO addSchemaCode(String code){
        if(schemaConfigs == null) schemaConfigs = new ArrayList<>();
        schemaConfigs.add(code);
        return this;
    }
}
