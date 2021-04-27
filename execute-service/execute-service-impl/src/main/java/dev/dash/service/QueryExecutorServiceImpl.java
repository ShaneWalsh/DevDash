package dev.dash.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.dash.dao.ConnectionConfigRepository;
import dev.dash.dao.QueryConfigRepository;
import dev.dash.enums.AuditEventTypeEnum;
import dev.dash.enums.DdlTypeEnum;
import dev.dash.model.ConnectionConfig;
import dev.dash.model.QueryConfig;
import dev.dash.model.body.ExecutionData;
import dev.dash.model.body.QueryExecution;
import dev.dash.model.body.SchemaConnection;
import dev.dash.security.AuditLogicService;
import dev.dash.security.SecurityLogicService;
import dev.dash.service.util.QueryStringParser;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONArray;

@Slf4j
@Service
public class QueryExecutorServiceImpl implements QueryExecutorService {
    
    @Autowired
    private QueryConfigRepository queryConfigRepository;

    @Autowired
    ConnectionConfigRepository connectionConfigRepository;

    @Autowired
    SecurityLogicService securityLogicService;

    @Autowired
    AuditLogicService auditLogicService;
 
	public JSONArray processQuery(QueryExecution queryExecution) throws SQLException {
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

    public JSONArray processQuery(String queryCode, String connectionCode,ExecutionData executionData) throws SQLException {
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
    public JSONArray processQuery(QueryConfig queryConfig, ConnectionConfig connectionConfig, ExecutionData executionData) throws SQLException {

        if ( !checkUserHasPermission(queryConfig,connectionConfig) ) {
            auditLogicService.auditEntityEvent(queryConfig, AuditEventTypeEnum.ExecuteQueryUserLackingRole, executionData);
            return new JSONArray();
        } else {
            auditLogicService.auditEntityEvent(queryConfig, AuditEventTypeEnum.ExecuteQuery, executionData);
            // create the connection
            Connection connection = createConnection(connectionConfig);

            try { // execute the query
                String query = QueryStringParser.parseAndReplaceQueryString( queryConfig, executionData );
                if ( DdlTypeEnum.Select.equals( DdlTypeEnum.findType( queryConfig.getDdlType() ) ) ) {
                    ResultSet rs = executeQuery(query, connection, executionData);
                    JSONArray jsonArray = jsonify(rs);
                    return jsonArray;
                } else {
                    int res = executeUpdate(query, connection, executionData);
                    log.info("Execute Update "+queryConfig.getCode()+" : affected rows:" + res);
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(new String[]{"Affected Rows : "+res});
                    return jsonArray;
                }
            } catch (SQLException e) {
                auditLogicService.auditEntityEvent(queryConfig, AuditEventTypeEnum.ExecuteQueryFailed, executionData);
                e.printStackTrace();
            } finally { // close the connection
                connection.close();
            }
        }
        return new JSONArray();
    }

    private ResultSet executeQuery(String query, Connection connection, ExecutionData executionData) throws SQLException {
        Statement stmt = null;
        stmt = connection.createStatement();
        if(log.isDebugEnabled()){log.debug(query);}
        ResultSet rs = stmt.executeQuery(query);
        return rs;
    }

    private int executeUpdate(String query, Connection connection, ExecutionData executionData) throws SQLException {
        Statement stmt = null;
        stmt = connection.createStatement();
        if(log.isDebugEnabled()){log.debug(query);}
        return stmt.executeUpdate(query);
    }

    private Connection createConnection(ConnectionConfig connectionConfig) throws SQLException {
        Connection connection = null;
        connection = DriverManager.getConnection(connectionConfig.getUrl(), connectionConfig.getUsername(), connectionConfig.getPassword());
        return connection;
    }

    private boolean checkUserHasPermission ( QueryConfig queryConfig, ConnectionConfig connectionConfig ) {
        if( !securityLogicService.checkUserHasRole(queryConfig.getSecurityRole() ) ) return false;
        if( !securityLogicService.checkUserHasRole(connectionConfig.getSecurityRole() ) ) return false;
        if( !securityLogicService.checkUserHasRole(queryConfig.getSchemaConfig().getSecurityRole() ) ) return false;
        if( !securityLogicService.checkUserHasRole(connectionConfig.getSchemaConfig().getSecurityRole() ) ) return false;
        return true;
    }
}