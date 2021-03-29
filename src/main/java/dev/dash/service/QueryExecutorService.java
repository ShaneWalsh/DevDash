package dev.dash.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.dash.dao.ConnectionConfigRepository;
import dev.dash.dao.QueryConfigRepository;
import dev.dash.enums.DdlTypeEnum;
import dev.dash.model.ConnectionConfig;
import dev.dash.model.QueryConfig;
import dev.dash.model.body.ExecutionData;
import dev.dash.model.body.QueryExecution;
import dev.dash.model.body.SchemaConnection;
import dev.dash.service.util.QueryStringParser;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONArray;
import org.json.JSONObject;

@Slf4j
@Service
public class QueryExecutorService {
    
    @Autowired
    private QueryConfigRepository queryConfigRepository;

    @Autowired
    ConnectionConfigRepository connectionConfigRepository;
 
	public JSONArray processQuery(QueryExecution queryExecution) throws SQLException {
        // todo workout the connection code!
        QueryConfig queryConfig = queryConfigRepository.findByCode(queryExecution.getQueryCode());
        String schemaCode = queryConfig.getSchemaConfig().getCode();
        SchemaConnection schemaConnection = queryExecution.getSchemaToConnectionsArray().stream().filter(s -> s.getSchemaCode().equalsIgnoreCase(schemaCode)).findFirst().orElse(null);
        if(schemaConnection == null) return null; //todo logging
        ConnectionConfig connectionConfig = connectionConfigRepository.findByCode(schemaConnection.getConnectionCode());
        if(connectionConfig == null) return null; //todo logging

        return this.processQuery(queryConfig,connectionConfig,queryExecution.getExeData());
	}

    public JSONArray processQuery(String queryCode, String connectionCode,ExecutionData executionData) throws SQLException {
        QueryConfig queryConfig = queryConfigRepository.findByCode(queryCode);
        if(queryConfig == null) return null; // todo logging
        ConnectionConfig connectionConfig = connectionConfigRepository.findByCode(connectionCode);
        if(connectionConfig == null) return null; //todo logging
        return processQuery(queryConfig, connectionConfig, executionData);
    }

    /**
     * Execute a query and return the result in json format
     * 
     * @param executionData
     * 
     * @param queryCode     TODO add in a map here for the different panels and
     *                      their values, which can be used in the query.
     * @throws SQLException
     */
    public JSONArray processQuery(QueryConfig queryConfig, ConnectionConfig connectionConfig, ExecutionData executionData) throws SQLException {
        // create the connection
        Connection connection = createConnection(connectionConfig);
        // replace variables in the queryStr
        // todo add this logic
        // execute the query
        try {
            String query = QueryStringParser.parseAndReplaceQueryString(queryConfig,executionData);
            if(DdlTypeEnum.Select.equals( DdlTypeEnum.findType( queryConfig.getDdlType() ) ) ) {
                ResultSet rs = executeQuery(query, connection, executionData);
                JSONArray jsonArray = jsonify(rs);
                return jsonArray;
            } else {
                int res = executeUpdate(query, connection, executionData);
                log.info("executeUpdate "+queryConfig.getCode()+" : affected rows:" + res);
                return new JSONArray();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally { // close the connection
            connection.close();
        }
        return null;
    }

    private ResultSet executeQuery(String query, Connection connection, ExecutionData executionData) throws SQLException {
        Statement stmt = null;
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        return rs;
    }

    private int executeUpdate(String query, Connection connection, ExecutionData executionData) throws SQLException {
        Statement stmt = null;
        stmt = connection.createStatement();
        return stmt.executeUpdate(query);
    }

    private Connection createConnection(ConnectionConfig connectionConfig) throws SQLException {
        Connection connection = null;
        connection = DriverManager.getConnection(connectionConfig.getUrl(),connectionConfig.getUsername(),connectionConfig.getPassword());
        return connection;
    }

    // https://stackoverflow.com/questions/6514876/most-efficient-conversion-of-resultset-to-json
    public static JSONArray jsonify(ResultSet rs) throws SQLException {
        JSONArray json = new JSONArray();
        ResultSetMetaData rsmd = rs.getMetaData();
        while(rs.next()) {
            int numColumns = rsmd.getColumnCount();
            JSONObject obj = new JSONObject();
            for (int i=1; i<=numColumns; i++) {
                String column_name = rsmd.getColumnName(i);
                obj.put(column_name, rs.getObject(column_name));
            }
            json.put(obj);
        }
        return json;
    }

}