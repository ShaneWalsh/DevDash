package dev.dash.execute;


import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.dash.dao.ConnectionConfigRepository;
import dev.dash.dao.QueryConfigRepository;
import dev.dash.enums.AuditEventTypeEnum;
import dev.dash.enums.DatabaseLanguageEnum;
import dev.dash.enums.DdlTypeEnum;
import dev.dash.execute.processor.CassandraProcessor;
import dev.dash.execute.processor.DBProcessor;
import dev.dash.model.ConnectionConfig;
import dev.dash.model.QueryConfig;
import dev.dash.model.body.ExecutionData;
import dev.dash.model.body.QueryExecution;
import dev.dash.model.body.SchemaConnection;
import dev.dash.security.AuditLogicService;
import dev.dash.security.SecurityLogicService;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONArray;

@Slf4j
@Service
public class QueryExecutorServiceImpl implements QueryExecutorService {
    
    @Autowired
    private QueryConfigRepository queryConfigRepository;

    @Autowired
    private ConnectionConfigRepository connectionConfigRepository;

    @Autowired
    private SecurityLogicService securityLogicService;

    @Autowired
    private AuditLogicService auditLogicService;

    @Autowired
    private DBProcessor mySqlProcessor;

    @Autowired
    private DBProcessor cassandraProcessor;
 
	public JSONArray processQuery(QueryExecution queryExecution){
        QueryConfig queryConfig = queryConfigRepository.findByCode(queryExecution.getQueryCode());
        if(queryConfig == null) {
            log.warn("ProcessQuery failure. No query with the code: {}", queryExecution.getQueryCode());
            return null;  
        } 
        String schemaCode = queryConfig.getSchemaConfig().getCode();

        SchemaConnection schemaConnection = queryExecution.getSchemaToConnectionsArray().stream().filter(s -> s.getSchemaCode().equalsIgnoreCase(schemaCode)).findFirst().orElse(null);
        if(schemaConnection == null) {
            log.warn("ProcessQuery failure. No schema with the code: {}", schemaCode);
            return null;  
        } 

        ConnectionConfig connectionConfig = connectionConfigRepository.findByCode(schemaConnection.getConnectionCode());
        if(connectionConfig == null) {
            log.warn("ProcessQuery failure. No connection with the code: {}",schemaConnection.getConnectionCode());
            return null;  
        } 

        return this.processQuery(queryConfig,connectionConfig,queryExecution.getExeData());
	}

    public JSONArray processQuery(String queryCode, String connectionCode,ExecutionData executionData){
        QueryConfig queryConfig = queryConfigRepository.findByCode(queryCode);
        if(queryConfig == null) {
            log.warn("ProcessQuery failure. No query with the code: {}", queryCode);
            return null;  
        } 

        ConnectionConfig connectionConfig = connectionConfigRepository.findByCode(connectionCode);
        if(connectionConfig == null) {
            log.warn("ProcessQuery failure. No connection with the code: {}",connectionCode);
            return null;  
        } 

        return processQuery(queryConfig, connectionConfig, executionData);
    }

    /**
     * Execute a query on the selected connection and return the result in json format
     * 
     * @param queryConfig
     * @param connectionConfig
     * @param executionData 
     * @throws SQLException
     */
    public JSONArray processQuery(QueryConfig queryConfig, ConnectionConfig connectionConfig, ExecutionData executionData){

        if ( !checkUserHasPermission(queryConfig,connectionConfig) ) {
            auditLogicService.auditEntityEvent(queryConfig, AuditEventTypeEnum.ExecuteQueryUserLackingRole, executionData);
            return new JSONArray();
        } else {
            auditLogicService.auditEntityEvent(queryConfig, AuditEventTypeEnum.ExecuteQuery, executionData);
            JSONArray processQuery = null;
            switch (DatabaseLanguageEnum.findType(connectionConfig.getLanguage())) {
                case MySQL : {
                    processQuery = mySqlProcessor.processQuery(queryConfig,connectionConfig,executionData);
                    break;
                } case CASSANDRA : {
                    processQuery = cassandraProcessor.processQuery(queryConfig,connectionConfig,executionData);
                    break;
                }
            }
            
            return processQuery;
        }
    }

    private boolean checkUserHasPermission ( QueryConfig queryConfig, ConnectionConfig connectionConfig ) {
        if( !securityLogicService.checkUserHasRole(queryConfig.getSecurityRole() ) ) return false;
        if( !securityLogicService.checkUserHasRole(connectionConfig.getSecurityRole() ) ) return false;
        if( !securityLogicService.checkUserHasRole(queryConfig.getSchemaConfig().getSecurityRole() ) ) return false;
        if( !securityLogicService.checkUserHasRole(connectionConfig.getSchemaConfig().getSecurityRole() ) ) return false;
        return true;
    }
}