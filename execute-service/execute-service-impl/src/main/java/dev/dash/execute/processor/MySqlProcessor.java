package dev.dash.execute.processor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.dash.enums.AuditEventTypeEnum;
import dev.dash.enums.DdlTypeEnum;
import dev.dash.execute.connector.MySQLConnector;
import dev.dash.execute.util.QueryStringParser;
import dev.dash.model.ConnectionConfig;
import dev.dash.model.QueryConfig;
import dev.dash.model.body.ExecutionData;
import dev.dash.security.AuditLogicService;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONArray;
import org.json.JSONObject;

@Slf4j
@Service
public class MySqlProcessor implements DBProcessor {

    @Autowired
    AuditLogicService auditLogicService;

    @Override
    public JSONArray processQuery(QueryConfig queryConfig, ConnectionConfig connectionConfig, ExecutionData executionData) {
        MySQLConnector connector = new MySQLConnector(connectionConfig);
        Connection connection = connector.connect();
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
            connector.close();
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
    
    // https://stackoverflow.com/questions/6514876/most-efficient-conversion-of-resultset-to-json
    public JSONArray jsonify(ResultSet rs) {
        JSONArray json = new JSONArray();
        ResultSetMetaData rsmd;
        try {
            rsmd = rs.getMetaData();
        
            while(rs.next()) {
                int numColumns = rsmd.getColumnCount();
                JSONObject obj = new JSONObject();
                for (int i=1; i<=numColumns; i++) {
                    String column_name = rsmd.getColumnLabel(i);
                    obj.put(column_name, rs.getObject(column_name));
                }
                json.put(obj);
            }
            return json;
        } catch (SQLException exception) {
            log.error("jsonify", exception);
            //todo handle it
        }
        return null;
    }
    
}
