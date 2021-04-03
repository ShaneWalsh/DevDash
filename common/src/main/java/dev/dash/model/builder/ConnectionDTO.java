package dev.dash.model.builder;

import lombok.Data;

@Data
public class ConnectionDTO {
	private Long id;
    private String code; 
    private String name;
    private String connectionType;
    private String language;
    private String driverType;
    private String url;
    private String username;
    private String password;
    private String schemaCode;
}







