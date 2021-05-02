package dev.dash.execute.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.dash.dao.ConnectionConfigRepository;
import dev.dash.dao.QueryConfigRepository;
import dev.dash.dao.SchemaConfigRepository;
import dev.dash.execute.QueryBuilderService;
import dev.dash.model.ConnectionConfig;
import dev.dash.model.QueryConfig;
import dev.dash.model.SchemaConfig;
import dev.dash.model.builder.ConnectionDTO;
import dev.dash.model.builder.QueryBuilderData;
import dev.dash.model.builder.QueryDTO;
import dev.dash.model.builder.SchemaDTO;
import dev.dash.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("queryBuilder")
public class QueryBuilderController {

    @Autowired
    SchemaConfigRepository schemaConfigRepository;

    @Autowired
    QueryConfigRepository queryConfigRepository;

    @Autowired
    ConnectionConfigRepository connectionConfigRepository;

    @Autowired
    QueryBuilderService queryBuilderService;

    /**
     * import method for query data
     */
    @PostMapping("/importdata")
	public ResponseEntity<Integer> importData(@RequestBody QueryBuilderData queryBuilderData) {
        boolean successFullImport = queryBuilderService.importConfig(queryBuilderData);
        if(successFullImport) {
            log.debug("Schema data imported successfully");
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            log.warn("Schema data failed to import successfully");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * export method for query data
     */
    @GetMapping("/exportdata")
	public ResponseEntity<String> exportData(@RequestParam(name = "schemas") String schemas) {
        if ( StringUtil.isVaildString(schemas) ) {
            String exportJson = queryBuilderService.exportConfig(schemas.split(","));
            if(StringUtil.isVaildString( exportJson )) {
                return new ResponseEntity<>(exportJson,HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("",HttpStatus.NO_CONTENT);
    }

    @PostMapping("/schema")
	public Long createSchema(SchemaDTO schemaDTO) {
        SchemaConfig schemaConfig = new SchemaConfig(schemaDTO.getCode(),
            schemaDTO.getName());

        schemaConfig = schemaConfigRepository.saveAndFlush(schemaConfig);   
		return schemaConfig.getId();
    }

    @PostMapping("/query")
	public Long createQuery(QueryDTO queryDTO) {
        QueryConfig queryConfig = new QueryConfig(queryDTO.getCode(),
            queryDTO.getDescription(),
            queryDTO.getQueryString(),
            schemaConfigRepository.findByCode(queryDTO.getSchemaCode()));

        queryConfig = queryConfigRepository.saveAndFlush(queryConfig);   
		return queryConfig.getId();
    }

    @PostMapping("/connection")
	public Long createConnection(ConnectionDTO connectionDTO) {
        ConnectionConfig connectionConfig = new ConnectionConfig(connectionDTO.getCode(),
            connectionDTO.getName(),
            connectionDTO.getDriverType(),
            connectionDTO.getUrl(),
            connectionDTO.getUsername(),
            connectionDTO.getPassword(),
            schemaConfigRepository.findByCode(connectionDTO.getSchemaCode()));

        connectionConfig = connectionConfigRepository.saveAndFlush(connectionConfig);   
        return connectionConfig.getId();
    }

	// @GetMapping("/list")
	// public QueryBuilderData list() {
    //     List<SchemaConfig> schemaConfigs = schemaConfigRepository.findAll();
    //     List<SchemaDTO> schemaDTOs = new ArrayList<>();
    //     List<QueryDTO> queryDTOs = new ArrayList<>();
    //     List<ConnectionDTO> connectionDTOs = new ArrayList<>();
    //     for(SchemaConfig schemaConfig : schemaConfigs){
    //         SchemaDTO schemaDTO = new SchemaDTO();
    //         schemaDTO.setId(schemaConfig.getId());
    //         schemaDTO.setCode(schemaConfig.getCode());
    //         schemaDTO.setName(schemaConfig.getName());
    //         List<String> list = new ArrayList<>();
    //         for(QueryConfig queryConfig: schemaConfig.getQueryConfigsSet()){
    //             list.add(queryConfig.getCode());
    //         }
    //         schemaDTO.setQueryCodeList(list);
    //         list = new ArrayList<>();
    //         for(ConnectionConfig connectionConfig :schemaConfig.getConnectionConfigSet()){
    //             list.add(connectionConfig.getCode());
    //         }
    //         schemaDTO.setConnectionCodeList(list);
    //         schemaDTOs.add(schemaDTO);
    //     }
        
    //     List<QueryConfig> qConfigs = queryConfigRepository.findAll();
    //     for(QueryConfig queryConfig: qConfigs){
    //         QueryDTO queryDTO = new QueryDTO();
    //         queryDTO.setId(queryConfig.getId());
    //         queryDTO.setCode(queryConfig.getCode());
    //         queryDTO.setDescription(queryConfig.getDescription());
    //         queryDTO.setQueryString(queryConfig.getQueryString());
    //         if(queryConfig.getSchemaConfig() != null) {
    //             queryDTO.setSchemaCode(queryConfig.getSchemaConfig().getCode());
    //         }
    //         queryDTOs.add(queryDTO);
    //     }
    //     List<ConnectionConfig> connectionConfigs = connectionConfigRepository.findAll();
    //     for(ConnectionConfig connectionConfig: connectionConfigs){
    //         ConnectionDTO connectionDTO = new ConnectionDTO();
    //         connectionDTO.setId(connectionConfig.getId());
    //         connectionDTO.setCode(connectionConfig.getCode());
    //         connectionDTO.setName(connectionConfig.getName());
    //         connectionDTO.setConnectionType(connectionConfig.getConnectionType());
    //         connectionDTO.setLanguage(connectionConfig.getLanguage());
    //         connectionDTO.setDriverType(connectionConfig.getDriverType());
    //         connectionDTO.setUrl(connectionConfig.getUrl());
    //         connectionDTO.setUsername(connectionConfig.getUsername());
    //         connectionDTO.setPassword(connectionConfig.getPassword());
            
    //         if(connectionConfig.getSchemaConfig() != null) {
    //             connectionDTO.setSchemaCode(connectionConfig.getSchemaConfig().getCode());
    //         }
    //         connectionDTOs.add(connectionDTO);
    //     }

	// 	return new QueryBuilderData(schemaDTOs, queryDTOs, connectionDTOs);
    // }
    
}