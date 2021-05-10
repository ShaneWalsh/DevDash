package dev.dash.execute;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.dash.dao.ConnectionConfigRepository;
import dev.dash.dao.QueryConfigRepository;
import dev.dash.dao.SchemaConfigRepository;
import dev.dash.dao.SecurityRoleRepository;
import dev.dash.enums.AuditEventTypeEnum;
import dev.dash.enums.DdlTypeEnum;
import dev.dash.model.ConnectionConfig;
import dev.dash.model.QueryConfig;
import dev.dash.model.SchemaConfig;
import dev.dash.model.SecurityRole;
import dev.dash.model.builder.ConnectionDTO;
import dev.dash.model.builder.QueryBuilderData;
import dev.dash.model.builder.QueryDTO;
import dev.dash.model.builder.SchemaDTO;
import dev.dash.security.AuditLogicService;
import dev.dash.util.JsonUtil;
import dev.dash.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Slf4j
@Service
public class QueryBuilderServiceImpl implements QueryBuilderService {

    @Autowired
    SchemaConfigRepository schemaConfigRepository;

    @Autowired
    QueryConfigRepository queryConfigRepository;

    @Autowired
    ConnectionConfigRepository connectionConfigRepository;

    @Autowired
    SecurityRoleRepository securityRoleRepository;

    @Autowired
    AuditLogicService auditLogicService;
    
    @Override
    public boolean importConfig(QueryBuilderData queryBuilderData) {
        auditLogicService.auditEntityEvent(queryBuilderData, AuditEventTypeEnum.ImportConfig);
        log.info("Import of Query Schema started: {}", queryBuilderData.getContentsList());
        List<String> successSchemaCode = new ArrayList<String>();
        List<String> successConnectionCode = new ArrayList<String>();
        List<String> successQueryCode = new ArrayList<String>();
        for(SchemaDTO schemaDTO: queryBuilderData.getSchemas()){
            logImport( importSchema(schemaDTO), "Schema", successSchemaCode, schemaDTO.getCode() );
        }
        for(ConnectionDTO connectionDTO: queryBuilderData.getConnections()){
            logImport( importConnection(connectionDTO), "Connection", successConnectionCode, connectionDTO.getCode() );
        }
        for(QueryDTO queryDTO: queryBuilderData.getQueries()){
            logImport( importQuery(queryDTO), "Query", successQueryCode, queryDTO.getCode() );
        }
        log.info("Import of Schema Success Results: {}", String.format( "Schema: %s Connection: %s Query: %s", 
            successSchemaCode.stream().collect(Collectors.joining(", ")),
            successConnectionCode.stream().collect(Collectors.joining(", ")),
            successQueryCode.stream().collect(Collectors.joining(", ")))
        );
        return true;
    }

    private void logImport(boolean importSuccess, String importEntity, List<String> successCodes, String entityCode) {
        if ( importSuccess ) {
            successCodes.add(entityCode);
        } else {
            log.warn("Failed to import {}: {}", importEntity, entityCode);
        }
    }

    @Override
    public String exportConfig(String[] schemaConfigs) {
        auditLogicService.auditEntityEvent(schemaConfigs, AuditEventTypeEnum.ExportConfig);
        log.info("Export of schemas started: {}", String.join(", ",schemaConfigs));
        QueryBuilderData queryBuilderData = new QueryBuilderData();
        for ( String schemaCode : schemaConfigs ) {
            if( StringUtil.isVaildString(schemaCode) && schemaConfigRepository.existsByCode(schemaCode) ){
                SchemaConfig schemaConfig = schemaConfigRepository.findByCode( schemaCode );
                queryBuilderData.addSchemaConfig(convert(schemaConfig));
                for(QueryConfig queryConfig : schemaConfig.getQueryConfigsSet()){
                    queryBuilderData.addQueryConfig(convert(queryConfig));
                }
                for(ConnectionConfig connectionConfig : schemaConfig.getConnectionConfigSet()){
                    queryBuilderData.addConnectionConfig(convert(connectionConfig));
                }
            }
        }
        log.debug( "Exported Schema Config: {} ", queryBuilderData.getContentsList() );
        return JsonUtil.toJSON(queryBuilderData);
    }
    
    private boolean importSchema (SchemaDTO schemaDTO) {
        if ( StringUtil.isVaildString( schemaDTO.getCode() ) ) {
            SchemaConfig schemaConfig = null;
            if ( schemaConfigRepository.existsByCode(schemaDTO.getCode()) ) { 
                schemaConfig = schemaConfigRepository.findByCode(schemaDTO.getCode());
                schemaConfig.setName(schemaDTO.getName());
            } else {
                schemaConfig = new SchemaConfig (
                    schemaDTO.getCode(),
                    schemaDTO.getName()
                );
            }
            
            if ( StringUtil.isVaildString( schemaDTO.getSecurityRole() ) && schemaConfigRepository.existsByCode( schemaDTO.getSecurityRole() ) ) {
                SecurityRole securityRole = securityRoleRepository.findByCode(schemaDTO.getSecurityRole());
                schemaConfig.setSecurityRole(securityRole);
            } else {
                schemaConfig.setSecurityRole(null);
            }

            SchemaConfig importSchema = schemaConfigRepository.saveAndFlush(schemaConfig);
            return importSchema != null;
        } else {
            log.warn("Failed to import schema. Missing mandatory schema code");
            return false;
        }
    }

    private boolean importConnection ( ConnectionDTO connectionDTO ) {
        if ( StringUtil.isVaildString( connectionDTO.getSchemaCode() ) && schemaConfigRepository.existsByCode( connectionDTO.getSchemaCode() ) ) {
            SchemaConfig schemaConfig = schemaConfigRepository.findByCode(connectionDTO.getSchemaCode());
            
            ConnectionConfig connectionConfig = null;
            if( !StringUtil.isVaildString( connectionDTO.getCode() ) ) { return false; }
            if ( connectionConfigRepository.existsByCode(connectionDTO.getCode()) ) { 
                connectionConfig = connectionConfigRepository.findByCode(connectionDTO.getCode());

                connectionConfig.setName(connectionDTO.getName());
                connectionConfig.setConnectionType(connectionDTO.getConnectionType());
                connectionConfig.setLanguage(connectionDTO.getLanguage());
                connectionConfig.setDriverType(connectionDTO.getDriverType());
                connectionConfig.setUrl(connectionDTO.getUrl());
                connectionConfig.setUsername(connectionDTO.getUsername());
                connectionConfig.setPassword(connectionDTO.getPassword());
                connectionConfig.setSchemaConfig(schemaConfig);
            } else {
                connectionConfig = new ConnectionConfig (
                    connectionDTO.getCode(), connectionDTO.getName(),
                    connectionDTO.getLanguage(), connectionDTO.getUrl(),
                    connectionDTO.getUsername(), connectionDTO.getPassword(),
                    schemaConfig
                );
            }

            if ( StringUtil.isVaildString( connectionDTO.getSecurityRole() ) && schemaConfigRepository.existsByCode( connectionDTO.getSecurityRole() ) ) {
                SecurityRole securityRole = securityRoleRepository.findByCode(connectionDTO.getSecurityRole());
                connectionConfig.setSecurityRole(securityRole);
            } else {
                connectionConfig.setSecurityRole(null);
            }

            ConnectionConfig importConnection = connectionConfigRepository.saveAndFlush(connectionConfig);
            return importConnection != null;
        } else {
            log.warn("Failed to import connection. Missing mandatory schema");
            return false;
        }
    }

    private boolean importQuery( QueryDTO queryDTO ) {
        if ( StringUtil.isVaildString( queryDTO.getSchemaCode() ) && schemaConfigRepository.existsByCode( queryDTO.getSchemaCode() ) ) {
            SchemaConfig schemaConfig = schemaConfigRepository.findByCode(queryDTO.getSchemaCode());
            DdlTypeEnum queryType = StringUtil.isVaildString( queryDTO.getDdlType() ) ? DdlTypeEnum.valueOf(queryDTO.getDdlType()) : DdlTypeEnum.Select;

            QueryConfig queryConfig = null;
            if( !StringUtil.isVaildString( queryDTO.getCode() ) ) { return false; }
            if ( queryConfigRepository.existsByCode(queryDTO.getCode()) ) { 
                queryConfig = queryConfigRepository.findByCode(queryDTO.getCode());
                queryDTO.setDescription(queryDTO.getDescription());
                queryDTO.setQueryString(queryDTO.getQueryString());
                queryDTO.setDdlType(queryType.name());
                queryConfig.setSchemaConfig(schemaConfig);
            } else {
                queryConfig = new QueryConfig (
                    queryDTO.getCode(),
                    queryDTO.getDescription(),
                    queryDTO.getQueryString(),
                    queryType.name(),
                    schemaConfig
                );
            }

            if ( StringUtil.isVaildString( queryDTO.getSecurityRole() ) && schemaConfigRepository.existsByCode( queryDTO.getSecurityRole() ) ) {
                SecurityRole securityRole = securityRoleRepository.findByCode(queryDTO.getSecurityRole());
                queryConfig.setSecurityRole(securityRole);
            } else {
                queryConfig.setSecurityRole(null);
            }

            QueryConfig importQuery = queryConfigRepository.saveAndFlush(queryConfig);
            return importQuery != null;
        } else {
            log.warn("Failed to import query. Missing mandatory schema");
            return false;
        }
    }

    private SchemaDTO convert(SchemaConfig schemaConfig) {
        SchemaDTO schemaDto= new SchemaDTO();
        schemaDto.setCode(schemaConfig.getCode());
        schemaDto.setName(schemaConfig.getName());
        if( schemaConfig.getSecurityRole() != null ) schemaDto.setSecurityRole( schemaConfig.getSecurityRole().getCode() );
        return schemaDto;
    }

    private ConnectionDTO convert(ConnectionConfig connectionConfig) {
        ConnectionDTO connectionDTO= new ConnectionDTO();
        connectionDTO.setCode( connectionConfig.getCode() );
        connectionDTO.setName( connectionConfig.getName() );
        connectionDTO.setConnectionType( connectionConfig.getConnectionType() );
        connectionDTO.setLanguage( connectionConfig.getLanguage() );
        connectionDTO.setDriverType( connectionConfig.getDriverType() );
        connectionDTO.setUrl( connectionConfig.getUrl() );
        connectionDTO.setUsername( connectionConfig.getUsername() );
        connectionDTO.setPassword( connectionConfig.getPassword() );
        if( connectionConfig.getSecurityRole() != null ) connectionDTO.setSecurityRole( connectionConfig.getSecurityRole().getCode() );
        if( connectionConfig.getSchemaConfig() != null ) connectionDTO.setSchemaCode( connectionConfig.getSchemaConfig().getCode() );
        return connectionDTO;
    }

    private QueryDTO convert(QueryConfig queryConfig) {
        QueryDTO queryDTO= new QueryDTO();
        queryDTO.setCode( queryConfig.getCode() );
        queryDTO.setDescription( queryConfig.getDescription() );
        queryDTO.setQueryString( queryConfig.getQueryString() );
        queryDTO.setDdlType( queryConfig.getDdlType() );
        if( queryConfig.getSecurityRole() != null ) queryDTO.setSecurityRole( queryConfig.getSecurityRole().getCode() );
        if( queryConfig.getSchemaConfig() != null ) queryDTO.setSchemaCode( queryConfig.getSchemaConfig().getCode() );
        return queryDTO;
    }

}
