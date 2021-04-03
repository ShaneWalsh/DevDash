package dev.dash.model.builder;

import java.util.List;

import lombok.Data;

@Data
public class SchemaDTO {
	private Long id;
	private String code; 
    private String name;
    private List<String> connectionCodeList;
    private List<String> queryCodeList;
}





