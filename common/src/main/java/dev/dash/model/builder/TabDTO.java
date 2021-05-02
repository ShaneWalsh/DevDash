package dev.dash.model.builder;

import lombok.Data;

@Data
public class TabDTO {
    private String code;
    private String name;
	private Integer displayOrder;
    private String dashboardConfig;
    private String securityRole;
}
