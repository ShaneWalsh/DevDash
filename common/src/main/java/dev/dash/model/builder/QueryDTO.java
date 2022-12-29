package dev.dash.model.builder;

import lombok.Data;

@Data
public class QueryDTO {
	private String code;
    private String description;
    private String ddlType;
    private String path;
    private String queryString;
    private String schemaCode;
    private String securityRole;
}