package dev.dash.execute;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.dash.dao.ConnectionConfigRepository;
import dev.dash.dao.QueryConfigRepository;
import dev.dash.dao.SchemaConfigRepository;
import dev.dash.dao.SecurityRoleRepository;
import dev.dash.enums.DdlTypeEnum;
import dev.dash.model.ConnectionConfig;
import dev.dash.model.QueryConfig;
import dev.dash.model.SchemaConfig;
import dev.dash.model.SecurityRole;
import dev.dash.model.builder.ConnectionDTO;
import dev.dash.model.builder.QueryBuilderData;
import dev.dash.model.builder.QueryDTO;
import dev.dash.model.builder.SchemaDTO;
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
    
    @Override
    public boolean importConfig(QueryBuilderData queryBuilderData) {
        //TODO _SW add auditing 
        //TODO _SW add security
        for(SchemaDTO schemaDTO: queryBuilderData.getSchemas()){
            importSchema(schemaDTO);
        }
        for(ConnectionDTO connectionDTO: queryBuilderData.getConnections()){
            importConnection(connectionDTO);
        }
        for(QueryDTO queryDTO: queryBuilderData.getQueries()){
            importQuery(queryDTO);
        }
        log.info("Imported Schema Data Successfully");
        return true;
    }

    @Override
    public String exportConfig(String[] schemaConfigs) {
        //TODO _SW add auditing 
        //TODO _SW add security
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
        return JsonUtil.toJSON(queryBuilderData);
    }
    
    private void importSchema (SchemaDTO schemaDTO) {
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

            schemaConfigRepository.saveAndFlush(schemaConfig);
        } else {
            log.warn("Failed to import schema. Missing mandatory schema code");
        }
    }

    private void importConnection ( ConnectionDTO connectionDTO ) {
        if ( StringUtil.isVaildString( connectionDTO.getSchemaCode() ) && schemaConfigRepository.existsByCode( connectionDTO.getSchemaCode() ) ) {
            SchemaConfig schemaConfig = schemaConfigRepository.findByCode(connectionDTO.getSchemaCode());
            
            ConnectionConfig connectionConfig = null;
            if( !StringUtil.isVaildString( connectionDTO.getCode() ) ) { return; }
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

            connectionConfigRepository.saveAndFlush(connectionConfig);
        } else {
            log.warn("Failed to import connection. Missing mandatory schema");
        }
    }

    private void importQuery( QueryDTO queryDTO ) {
        if ( StringUtil.isVaildString( queryDTO.getSchemaCode() ) && schemaConfigRepository.existsByCode( queryDTO.getSchemaCode() ) ) {
            SchemaConfig schemaConfig = schemaConfigRepository.findByCode(queryDTO.getSchemaCode());
            DdlTypeEnum queryType = StringUtil.isVaildString( queryDTO.getDdlType() ) ? DdlTypeEnum.valueOf(queryDTO.getDdlType()) : DdlTypeEnum.Select;

            QueryConfig queryConfig = null;
            if( !StringUtil.isVaildString( queryDTO.getCode() ) ) { return; }
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

            queryConfigRepository.saveAndFlush(queryConfig);
        } else {
            log.warn("Failed to import query. Missing mandatory schema");
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
