package dev.dash.model.builder;

import lombok.Data;

@Data
public class QueryDTO {
	private Long id;
	private String code;
    private String description;
    private String queryString;
    private String ddlType;
    private String schemaCode;
    private String securityRole;
}