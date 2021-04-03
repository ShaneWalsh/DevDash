package dev.dash.model.builder;

import java.util.List;

import lombok.Data;

@Data
public class DashboardDTO {
	private String code; 
    private String name;
    private List<String> schemaConfigs;
}
